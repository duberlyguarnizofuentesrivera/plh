const toggleStatusClientErrorInfo = document.getElementById("toggleStatusClientErrorInfo");
const toggleStatusClientOkInfo = document.getElementById("toggleStatusClientOkInfo");
const deleteClientOkInfo = document.getElementById("deleteClientOkInfo");
const deleteClientErrorInfo = document.getElementById("deleteClientErrorInfo");
const fields = document.getElementsByClassName("form-control");

const regionSelector = document.getElementById("region");
const provinceSelector = document.getElementById("province");
const districtSelector = document.getElementById("district");
const btnSaveAddress = document.getElementById("btnSaveAddress");
const addressLineInput = document.getElementById("addressLineInput");
const addressObservationsInput = document.getElementById("addressObservationsInput");
const btnEditClient = document.getElementById("btnEditClient");
const deleteAddressLink = document.getElementsByClassName("deleteAddressLink");
//Values from entity put in hidden fields
const hiddenEntityName = document.getElementById("hiddenEntityName").value;
const hiddenEntityId = document.getElementById("hiddenEntityId").value;
console.log(hiddenEntityName);
console.log(hiddenEntityId);
const ubigeo = window.ubigeo;
const jsonAddress = [];
const jsonBody = {};
let formIsValid = false;

btnEditClient.addEventListener("click", () => {
    Array.from(fields).forEach(field => {
        if (field.getAttribute("required") != null) {
            //required field
            if (field.value === "") {
                entityForm.classList.add("was-validated")
                formIsValid = false;
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
    if (formIsValid) {
        creationStatusSpinner.style.visibility = "visible";
        if (jsonAddress.length > 0) {
            jsonBody["pickUpAddressIds"] = jsonAddress;
        }
        let entity = verifyEntityName(); //in plural
        console.log(JSON.stringify(jsonBody));

        fetch("/api/v1/" + entity, {
            method: "PATCH",
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
})
btnSaveAddress.addEventListener("click", () => {
    const regionValue = ubigeo.departamentos[Number(regionSelector.selectedIndex)];
    const provinceValue = ubigeo.provincias[Number(regionSelector.value)][provinceSelector.selectedIndex];
    const districtValue = ubigeo.distritos[Number(provinceSelector.value)][districtSelector.selectedIndex];
    const addressAddedList = document.getElementById("addressAddedList");
    const addressLineText = addressLineInput.value;
    const obs = addressObservationsInput.value;
    const result = {
        "region": regionValue["nombre_ubigeo"],
        "province": provinceValue["nombre_ubigeo"],
        "district": districtValue["nombre_ubigeo"],
        "addressLine": addressLineText,
        "observations": obs
    };
    console.log(result);
    jsonAddress.push(result);
    fetch("/api/v1/" + hiddenEntityName + "/" + hiddenEntityId + "/add-address", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(result)
    }).then(response => {
        let result = response.status;
        switch (result) {
            case 201:
                //successful response (address created and added to client)
                window.location.reload();
                break;
            case 404:
                //todo
                console.log("add-address: entity not found");
                break;
            case 500:
                //error
                console.log("add-address: internal server error");
                break;
        }

    })
    let listElement = document.createElement("li");
    listElement.innerHTML = result.addressLine + " ( " + result.region + ", " + result.province + ", " + result.district + ").";
    addressAddedList.appendChild(listElement);
    btnSaveAddress.innerHTML = "Guardar Otra";
});

function fillChildren(parent, current, childrenName) {
    parent.addEventListener("change", function () {
        if (childrenName === "provinces") {
            current.innerHTML = "" //delete if old nodes exist
            const provinces = ubigeo.provincias[parent.value];
            for (let i = 0; i < provinces.length; i++) {
                const option = document.createElement('option');
                option.value = provinces[i]["id_ubigeo"];
                option.innerHTML = provinces[i]["nombre_ubigeo"]
                current.appendChild(option);
            }
        } else if (childrenName === "districts") {
            current.innerHTML = "" //delete if old nodes exist
            const distritos = ubigeo.distritos[parent.value];
            for (let i = 0; i < distritos.length; i++) {
                const option = document.createElement('option');
                option.value = distritos[i]["id_ubigeo"];
                option.innerHTML = distritos[i]["nombre_ubigeo"]
                current.appendChild(option);
            }
        }
    })
}

function populateLocationSelector() {
    const regions = ubigeo.departamentos;
    for (let i = 0; i < regions.length; i++) {
        const optionElement = document.createElement("option");
        optionElement.innerHTML = regions[i]["nombre_ubigeo"];
        optionElement.setAttribute("value", regions[i]["id_ubigeo"]);
        //default value is Lima
        if (regions[i].nombre_ubigeo === "Lima") {
            optionElement.setAttribute("selected", "")
        }
        regionSelector.appendChild(optionElement);
    }
    fillChildren(regionSelector, provinceSelector, "provinces");
    fillChildren(provinceSelector, districtSelector, "districts");
    const event = new Event("change")
    regionSelector.dispatchEvent(event);
}

function addDeleteAddressLinkListeners() {
    Array.from(deleteAddressLink).forEach(link => {
            link.addEventListener("click", e => {
                e.preventDefault();
                let url = e.target.getAttribute("href");
                fetch(url, {
                    method: "DELETE"
                }).then(response => {
                    const result = response.status;
                    switch (result) {
                        case 200:
                            console.log("Deleted address!")
                            window.location.reload();
                            break;
                        case 404:
                            console.log("Cannot delete address! Address or entity not found!")
                            break;
                        case 500:
                            console.log("Cannot delete address: Internal Server Error")
                            break;
                    }
                })
            })
        }
    )
}

window.onload = () => {
    populateLocationSelector();
    addDeleteAddressLinkListeners();
    toggleStatusClientErrorInfo.style.display = "none";
    toggleStatusClientOkInfo.style.display = "none";
    deleteClientOkInfo.style.display = "none";
    deleteClientErrorInfo.style.display = "none";
}