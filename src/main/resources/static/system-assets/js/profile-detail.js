//-------Inputs---------------------
const curPassword = document.getElementById("currentPassword");
const newPassword = document.getElementById("newPassword");
const reNewPassword = document.getElementById("renewPassword");
//-----Buttons-----------------
const btnChangePassword = document.getElementById("btnChangePassword");
//------Alerts--------------------------------
const passwordChangedOkInfo = document.getElementById("passwordChangedOkInfo");
const passwordChangedErrorInfo = document.getElementById("passwordChangedErrorInfo");
const passwordChangedBadPwdInfo = document.getElementById("passwordChangedBadPwdInfo");

//-----Images--------------------------------
const profilePictureImage = document.getElementById("profilePictureImage");

//--------Event Handlers--------------------------------
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

function loadUserDetails() {
    fetch("/api/v1/users/current").then(currentResponse => {
            if (currentResponse.status === 200) {
                currentResponse.json().then(currentData => {
                    fetch("/api/v1/users/" + currentData["username"]).then(response => {
                            //entity found
                            if (response.status === 200) {
                                response.json().then(data => {
                                    console.log("Entity found...")
                                    console.log(data)
                                    profilePictureImage.setAttribute("src", data["_links"]["profilePictureUrl"]["href"])
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

                                    //auditing card: fetch usernames for createdBy and lastModifiedBy

                                    //details tab
                                    const fieldUserIdNumber = document.getElementById("fieldUserIdNumber");
                                    const fieldUserUserName = document.getElementById("fieldUserUserName");
                                    const fieldUserPhone = document.getElementById("fieldUserPhone");
                                    const fieldUserPhone2 = document.getElementById("fieldUserPhone2");
                                    const fieldUserEmail = document.getElementById("fieldUserEmail");
                                    fieldUserIdNumber.innerHTML = data["idNumber"];
                                    fieldUserUserName.innerHTML = data["username"];
                                    fieldUserPhone.innerHTML = data["phone"];
                                    fieldUserPhone2.innerHTML = data["phone2"];
                                    fieldUserEmail.innerHTML = data["email"];

                                });
                            } else {
                                //FORBIDDEN OR NOT_FOUND... do nothing (for now)
                            }
                        }
                    );
                });
            } else {
                //FORBIDDEN
            }
        }
    )
}

window.onload = () => {
    passwordChangedOkInfo.style.display = "none";
    passwordChangedErrorInfo.style.display = "none";
    passwordChangedBadPwdInfo.style.display = "none";
    loadUserDetails();
};