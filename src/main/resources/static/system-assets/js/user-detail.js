const deleteBtn = document.getElementById("btn-delete-user");
const deleteCheck = document.getElementById("securityNotifyDeleteUser");
const deactivateBtn = document.getElementById("btn-deactivate-user");
const deactivateCheck = document.getElementById("securityNotifyDeactivateUser");
const curPassword = document.getElementById("currentPassword");
const newPassword = document.getElementById("newPassword");
const reNewPassword = document.getElementById("renewPassword");
const btnChangePassword = document.getElementById("btnChangePassword");
const passwordChangedOkInfo = document.getElementById("passwordChangedOkInfo");
const passwordChangedErrorInfo = document.getElementById("passwordChangedErrorInfo");
const passwordChangedBadPwdInfo = document.getElementById("passwordChangedBadPwdInfo");

deleteCheck.addEventListener("change", () => {
    deleteBtn.toggleAttribute("disabled");
});
deactivateCheck.addEventListener("change", () => {
    deactivateBtn.toggleAttribute("disabled");
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
        //Path format is: /system/user/crud/by-username/<username>
        const selectedUser = window.location.pathname.split('/')[5];
        console.log(selectedUser);
        //API call for admin user
        console.log("admin changing password")
        pwdData = {
            newPassword: newPassword.value,
            username: selectedUser
        };
        //this API call is made in the ADMIN controller.
        fetch("/api/v1/user/change-password", {
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
                        passwordChangedOkInfo.style.visibility = "visible";
                    }
                    btnChangePassword.removeAttribute("disabled");
                })
            }
        ).catch(err => {
            console.log("error");
            console.log(err);
            passwordChangedErrorInfo.style.visibility = "visible";
        });
    } else {
        //API call for regular users. Must be same user as profile name
        console.log("regular user changing password")
        pwdData = {
            currentPassword: curPassword.value,
            newPassword: newPassword.value
        };
        //this API call is made in the general controller.
        fetch("/api/v1/user/change-password", {
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
                            passwordChangedBadPwdInfo.style.visibility = "visible";
                            break;
                        case "PASSWORD_CHANGED":
                            passwordChangedOkInfo.style.visibility = "visible";
                            break;
                    }
                    btnChangePassword.removeAttribute("disabled");
                })
            }
        ).catch(err => {
            console.log("error");
            console.log(err);
            passwordChangedErrorInfo.style.visibility = "visible";
            btnChangePassword.removeAttribute("disabled");
        });
    }
})

window.onload = () => {
    passwordChangedOkInfo.style.visibility = "hidden";
    passwordChangedErrorInfo.style.visibility = "hidden";
    passwordChangedBadPwdInfo.style.visibility = "hidden";
};