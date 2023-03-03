//------Checkboxes----------------------
const checkDeleteUser = document.getElementById("securityNotifyDeleteUser");
const checkDeactivateUser = document.getElementById("securityNotifyDeactivateUser");
//-------Inputs---------------------
const curPassword = document.getElementById("currentPassword");
const newPassword = document.getElementById("newPassword");
const reNewPassword = document.getElementById("renewPassword");
const profilePictureSelector = document.getElementById("profilePictureSelector");
//-----Buttons-----------------
const btnDeleteUser = document.getElementById("btn-delete-user");
const btnToggleStatusUser = document.getElementById("btn-toggle-status-user");
const btnChangePassword = document.getElementById("btnChangePassword");
const btnUploadProfilePicture = document.getElementById("btnUploadProfilePicture");
//------Alerts--------------------------------
const passwordChangedOkInfo = document.getElementById("passwordChangedOkInfo");
const passwordChangedErrorInfo = document.getElementById("passwordChangedErrorInfo");
const passwordChangedBadPwdInfo = document.getElementById("passwordChangedBadPwdInfo");
const toggleStatusUserOkInfo = document.getElementById("toggleStatusUserOkInfo");
const toggleStatusUserSameUserInfo = document.getElementById("toggleStatusUserSameUserInfo");
const toggleStatusUserErrorInfo = document.getElementById("toggleStatusUserErrorInfo");
const deleteUserOkInfo = document.getElementById("deleteUserOkInfo");
const deleteUserSameUserInfo = document.getElementById("deleteUserSameUserInfo");
const deleteUserErrorInfo = document.getElementById("deleteUserErrorInfo");


//--------Event Handlers--------------------------------
checkDeleteUser.addEventListener("change", () => {
    btnDeleteUser.toggleAttribute("disabled");
});
checkDeactivateUser.addEventListener("change", () => {
    btnToggleStatusUser.toggleAttribute("disabled");
});
/*--------------------------------------------------
Change Password
*------------------------------------------------- */
//First we have utility functions.
//Second new password field is checked for special characters. It will also be checked in backend, for added security.
//Third new password field is checked for validity (equals first field) and takes care of visual validation.
// Finally, Password button checks whether the current password field exists (created by backend) and creates an API call

function validateFields() {
    if ((newPassword.value === reNewPassword.value) && (newPassword.value.length > 4)) {

        newPassword.classList.remove("is-invalid");
        reNewPassword.classList.remove("is-invalid");
        newPassword.classList.add("is-valid");
        reNewPassword.classList.add("is-valid");
        btnChangePassword.removeAttribute('disabled');
    } else {
        // passwords don't match
        newPassword.classList.add("is-invalid");
        reNewPassword.classList.add("is-invalid");
        btnChangePassword.setAttribute('disabled', '');
    }
}

newPassword.addEventListener("keyup", () => {
    let c = newPassword.selectionStart,
        r = /[^A-Za-z0-9]/gi,
        v = newPassword.value;
    if (r.test(v)) {
        newPassword.value = v.replace(r, '');
        c--;
    }
    newPassword.setSelectionRange(c, c);
})


reNewPassword.addEventListener("keyup", () => {
    validateFields();
});

btnChangePassword.addEventListener("click", () => {
    btnChangePassword.setAttribute("disabled", '');

    let pwdData;
    if (curPassword == null) {
        //Path may contain parameters, but original path will be the same. Because of this,
        // se select a fixed index for the name of the user currently on screen.
        //Path format is: /system/users/crud/by-username/<username>
        const selectedUser = window.location.pathname.split('/')[5];
        console.log(selectedUser);
        //API call for admin user
        console.log("admin changing password")
        pwdData = {
            newPassword: newPassword.value,
            username: selectedUser
        };
        //this API call is made in the ADMIN controller.
        fetch("/api/v1/users/change-any-password", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(pwdData)
        }).then(
            response => {
                console.log("response")
                console.log(response);
                response.json().then(data => {
                    if (data['result'] === "PASSWORD_CHANGED") {
                        passwordChangedOkInfo.style.display = "block";
                    }
                    btnChangePassword.removeAttribute("disabled");
                })
            }
        ).catch(err => {
            console.log("error");
            console.log(err);
            passwordChangedErrorInfo.style.display = "block";
        });
    } else {
        //API call for regular users. Must be same user as profile name
        console.log("regular user changing password")
        pwdData = {
            currentPassword: curPassword.value,
            newPassword: newPassword.value
        };
        //this API call is made in the general controller.
        fetch("/api/v1/users/change-password", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(pwdData)
        }).then(
            response => {
                console.log("response")
                response.json().then(data => {
                    console.log(data);
                    switch (data['result']) {
                        case "BAD_PASSWORD":
                            passwordChangedBadPwdInfo.style.display = "block";
                            break;
                        case "PASSWORD_CHANGED":
                            passwordChangedOkInfo.style.display = "block";
                            break;
                    }
                    btnChangePassword.removeAttribute("disabled");
                })
            }
        ).catch(err => {
            console.log("error");
            console.log(err);
            passwordChangedErrorInfo.style.display = "block";
            btnChangePassword.removeAttribute("disabled");
        });
    }
})

btnDeleteUser.addEventListener("click", () => {
    btnDeleteUser.setAttribute("disabled", "");
    const selectedUser = window.location.pathname.split('/')[5];
    fetch("/api/v1/users/" + selectedUser, {
        method: 'DELETE',
    })
        .then(
            response => {
                response.json().then(async data => {

                    switch (data["result"]) {
                        case 'USER_DELETED':
                            deleteUserOkInfo.style.display = "block";
                            await new Promise(() => setTimeout(() => {
                                console.log("User deleted... waited for 5 seconds...")
                            }, 5000));
                            break;
                        case 'ERROR':
                            deleteUserErrorInfo.style.display = "block";
                            btnDeleteUser.removeAttribute("disabled");
                            break;
                        case 'CANNOT_DELETE_SAME_USER':
                            deleteUserSameUserInfo.style.display = "block"
                            btnDeleteUser.removeAttribute("disabled");
                            break;
                    }
                })
            }
        ).catch(() => {
        deleteUserErrorInfo.style.display = "block";
        btnDeleteUser.removeAttribute("disabled");
    });
});


btnToggleStatusUser.addEventListener("click", () => {
    btnToggleStatusUser.setAttribute("disabled", "");
    const selectedUser = window.location.pathname.split('/')[5];
    const deleteData = {username: selectedUser}
    fetch("/api/v1/users/status", {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(deleteData)
    })
        .then(
            response => {
                response.json().then(async data => {

                    switch (data["result"]) {
                        case 'STATUS_CHANGED':
                            toggleStatusUserOkInfo.style.display = "block";
                            break;
                        case 'ERROR':
                            toggleStatusUserErrorInfo.style.display = "block";
                            break;
                        case 'CANNOT_TOGGLE_STATUS_SAME_USER':
                            toggleStatusUserSameUserInfo.style.display = "block"
                            break;
                    }
                    btnToggleStatusUser.removeAttribute("disabled");
                })
            }
        ).catch(() => {
        toggleStatusUserErrorInfo.style.display = "block";
        btnToggleStatusUser.removeAttribute("disabled");
    });
});

btnUploadProfilePicture.addEventListener("click", () => {
    const selectedUser = window.location.pathname.split('/')[5];
    profilePictureSelector.click();
    profilePictureSelector.onchange = () => {
        const file = profilePictureSelector.files[0];
        const formData = new FormData();
        formData.append('file', file);
        formData.append('username', selectedUser);
        fetch("/api/v1/users/upload-profile-picture", {
            method: 'POST',
            body: formData
        }).then(response => {
            response.json().then(data => {
                switch (data['result']) {
                    case 'PROFILE_PICTURE_UPLOADED':
                        alert("imagen subida con éxito");
                        break;
                    case 'ERROR':
                        alert("error al subir la imagen");
                        break;
                }
            })
        })
    }
})
window.onload = () => {
    passwordChangedOkInfo.style.display = "none";
    passwordChangedErrorInfo.style.display = "none";
    passwordChangedBadPwdInfo.style.display = "none";
    toggleStatusUserOkInfo.style.display = "none";
    toggleStatusUserErrorInfo.style.display = "none";
    toggleStatusUserSameUserInfo.style.display = "none";
    deleteUserOkInfo.style.display = "none";
    deleteUserSameUserInfo.style.display = "none";
    deleteUserErrorInfo.style.display = "none";
};