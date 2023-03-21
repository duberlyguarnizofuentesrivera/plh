const entityForm = document.getElementById("entityForm");
const fields = entityForm.getElementsByClassName("form-control");
const submitButton = document.getElementById("submitButton");
const creationSuccessAlert = document.getElementById("creationSuccessAlert");
const creationErrorAlert = document.getElementById("creationErrorAlert");
const creationStatusSpinner = document.getElementById("creationStatusSpinner");
const jsonBody = {};

submitButton.addEventListener("click", () => {
    let validForm = true;
    console.log(fields);
    Array.from(fields).forEach(field => {
        if (field.getAttribute("required") != null) {
            //required field
            if (field.value === "") {
                entityForm.classList.add("was-validated")
                validForm = false;
            } else {
                jsonBody[field.name] = field.value;
                console.log("field added:" + field.name + " value is: " + field.value);
            }
        } else {
            //optional field
            if (field.value !== "") {
                //we only add it if not empty
                jsonBody[field.name] = field.value;
                console.log("field added:" + field.name + " value is: " + field.value);
            }
        }
    });
    if (validForm) {
        creationStatusSpinner.style.visibility = "visible";
        fetch("/api/v1/users", {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(jsonBody)
        }).then(response => {
            if (response.status === 201) {
                creationSuccessAlert.style.display = "block";
                creationErrorAlert.style.display = "none";
            } else if (response.status === 500) {
                creationErrorAlert.style.display = "block";
                creationSuccessAlert.style.display = "none";
            }
            creationStatusSpinner.style.visibility = "hidden";
        }).catch(() => {
            creationErrorAlert.style.display = "block";
            creationSuccessAlert.style.display = "none";
            creationStatusSpinner.style.visibility = "hidden";
        })
    }
    console.log(JSON.stringify(jsonBody));
})

window.onload = () => {
    creationStatusSpinner.style.visibility = "hidden";
    creationSuccessAlert.style.display = "none";
    creationErrorAlert.style.display = "none";
    entityForm.addEventListener("submit", e => {
        e.preventDefault();
    })
}
