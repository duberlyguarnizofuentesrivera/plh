//------Checkboxes----------------------
const checkDeleteUser = document.getElementById("securityNotifyDeleteUser");
const checkDeactivateUser = document.getElementById("securityNotifyDeactivateUser");
//-------Inputs---------------------
const curPassword = document.getElementById("currentPassword");
const newPassword = document.getElementById("newPassword");
const reNewPassword = document.getElementById("renewPassword");
const profilePictureSelector = document.getElementById("profilePictureSelector");
const editUserFields = document.getElementsByClassName("editUserForm")
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

//-----Images--------------------------------
const profilePictureImage = document.getElementById("profilePictureImage");
const profilePictureEditImage = document.getElementById("profilePictureEditImage");

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
    //curPassword is null: SSR did not render tha field, which means we are admin
    if (curPassword == null) {
        //Path may contain parameters, but original path will be the same. Because of this,
        // se select a fixed index for the name of the user currently on screen.
        //Path format is: /system/users/<username>
        const selectedUser = window.location.pathname.split('/')[3];
        console.log(selectedUser);
        //API call for admin user
        console.log("admin changing password")
        fetch("/api/v1/users/" + selectedUser).then(response => {
                if (response.status === 200) {
                    response.json().then(data => {
                        const userId = data["id"];
                        const pwdData = {
                            id: userId,
                            password: newPassword.value
                        }
                        //don't verify password
                        fetch("/api/v1/users", {
                            method: "PATCH",
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(pwdData)
                        }).then(response => {
                            console.log("response")
                            console.log(response);
                            if (response.status === 200) {
                                passwordChangedOkInfo.style.display = "block";
                                btnChangePassword.removeAttribute("disabled");
                            } else {
                                console.log("error");
                                passwordChangedErrorInfo.style.display = "block";
                            }
                            response.json().then(data => {
                                console.log(data);
                            })
                        }).catch(err => {
                            console.log("error");
                            console.log(err);
                            passwordChangedErrorInfo.style.display = "block";
                        });
                    });
                } else if (response.status === 404) {
                    console.log("error");
                    passwordChangedErrorInfo.style.display = "block";
                }
            }
        );
    } else {
        //API call for regular users. Must be same user as profile name
        console.log("regular user changing password")
        fetch("/api/v1/users/current").then(response => {
            if (response.status === 200) {
                response.json().then(data => {
                    const userId = data["id"];
                    //validate password
                    fetch("/api/users/validate-password", {
                        method: "POST",
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({oldPassword: curPassword.value})
                    }).then(response => {
                        response.json().then(data => {
                            //server responds with true or false
                            if (data === true) {
                                fetch("/api/v1/users", {
                                    method: "PATCH",
                                    body: {id: userId, password: newPassword.value}
                                }).then(response => {
                                    if (response.statusCode === 200) {
                                        passwordChangedOkInfo.style.display = "block";
                                    } else {
                                        //posible errors:
                                        //INTERNAL_SERVER_ERROR
                                        //FORBIDDEN
                                        //BAD_REQUEST
                                    }
                                })
                            } else {

                            }
                        })
                    })
                })
            } else {

            }
        })
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

function loadUserDetails() {
    const url = window.location.pathname;
    const urlComponents = url.split("/");
    let username = urlComponents[urlComponents.length - 1];
    console.log("Username is: " + username);
    if (username.includes("?")) {
        username = username.substring(0, username.indexOf("?"));
    }
    fetch("/api/v1/users/" + username).then(response => {
        if (response.status === 200) {
            //entity found
            response.json().then(data => {
                console.log("Entity found...")
                console.log(data)
                profilePictureImage.setAttribute("src", data["_links"]["profilePictureUrl"]["href"])
                profilePictureEditImage.setAttribute("src", data["_links"]["profilePictureUrl"]["href"])
                const fieldUserNames = document.getElementById("fieldUserNames");
                const fieldUserRole = document.getElementById("fieldUserRole");
                const fieldUserStatus = document.getElementById("fieldUserStatus");
                const fieldSocialLinksPhone = document.getElementById("fieldSocialLinksPhone");
                const fieldSocialLinksWhatsApp = document.getElementById("fieldSocialLinksWhatsApp");
                const fieldSocialLinksEmail = document.getElementById("fieldSocialLinksEmail");
                fieldUserNames.innerHTML = data["firstName"] + " " + data["lastName"];
                fieldUserRole.innerHTML = data["role"]["label"];
                if (data["status"]["label"] === "Activo") {
                    fieldUserStatus.classList.add("text-bg-secondary");
                } else {
                    fieldUserStatus.classList.add("text-bg-danger");
                }
                fieldUserStatus.innerHTML = data["status"]["label"];
                fieldSocialLinksPhone.innerHTML = "<i class='bi bi-phone'></i>";
                fieldSocialLinksPhone.href = "tel:" + data["phone"];
                fieldSocialLinksWhatsApp.innerHTML = "<i class='bi bi-whatsapp'></i>";
                fieldSocialLinksWhatsApp.href = "https://wa.me/51" + data["phone"];
                fieldSocialLinksEmail.innerHTML = "<i class='bi bi-at'></i>";
                fieldSocialLinksEmail.href = "mailto:" + data["email"];

                //auditing card: only visible to ADMIN and SUPERVISOR
                const fieldCreationDate = document.getElementById("fieldCreationDate");
                const fieldLastModifiedDate = document.getElementById("fieldLastModifiedDate");
                const fieldCreatedBy = document.getElementById("fieldCreatedBy");
                const fieldLastModifiedBy = document.getElementById("fieldLastModifiedBy");
                //auditing card: fetch usernames for createdBy and lastModifiedBy
                if (data["createdBy"] == null) {
                    fieldCreatedBy.innerHTML = "Sin datos";
                } else {
                    fetch("/api/v1/users/" + data["createdBy"]).then(response => {
                        if (response.status === 200) {
                            response.json().then(data => {
                                fieldCreatedBy.innerHTML = data["firstName"] + " " + data["lastName"];
                            })
                        } else {
                            fieldCreatedBy.innerHTML = "Usuario eliminado";
                        }
                    })
                }
                if (data["lastModifiedBy"] == null) {
                    fieldLastModifiedBy.innerHTML = "Sin datos";
                } else {
                    fetch("/api/v1/users/" + data["lastModifiedBy"]).then(response => {
                        if (response.status === 200) {
                            response.json().then(data => {
                                fieldLastModifiedBy.innerHTML = data["firstName"] + " " + data["lastName"];
                            })
                        } else {
                            fieldLastModifiedBy.innerHTML = "Usuario eliminado";
                        }
                    })
                }
                fieldCreationDate.innerHTML = new Date(data["createdDate"]).toLocaleDateString();
                fieldLastModifiedDate.innerHTML = new Date(data["lastModifiedDate"]).toLocaleDateString();
                fieldLastModifiedBy.innerHTML = data["lastModifiedBy"];

                //details tab
                const fieldUserNotes = document.getElementById("fieldUserNotes");
                const fieldUserIdNumber = document.getElementById("fieldUserIdNumber");
                const fieldUserUserName = document.getElementById("fieldUserUserName");
                const fieldUserPhone = document.getElementById("fieldUserPhone");
                const fieldUserPhone2 = document.getElementById("fieldUserPhone2");
                const fieldUserEmail = document.getElementById("fieldUserEmail");
                fieldUserNotes.innerHTML = data["notes"];
                fieldUserIdNumber.innerHTML = data["idNumber"];
                fieldUserUserName.innerHTML = data["username"];
                fieldUserPhone.innerHTML = data["phone"];
                fieldUserPhone2.innerHTML = data["phone2"];
                fieldUserEmail.innerHTML = data["email"];

                //iterate through the Edit User tab
                Array.from(editUserFields).forEach(field => {
                    field.value = data[field.getAttribute("name")];
                })
            })
        } else if (response.status === 404) {
            //entity not found
        }
    })
}

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
    loadUserDetails();
};