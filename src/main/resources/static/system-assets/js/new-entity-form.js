const entityForm = document.getElementById("entityForm");
const fields = entityForm.getElementsByClassName("form-control");
const submitButton = document.getElementById("submitButton");
const creationSuccessAlert = document.getElementById("creationSuccessAlert");
const creationErrorAlert = document.getElementById("creationErrorAlert");
const creationStatusSpinner = document.getElementById("creationStatusSpinner");

const regionSelector = document.getElementById("region");
const provinceSelector = document.getElementById("province");
const districtSelector = document.getElementById("district");
const btnSaveAddress = document.getElementById("btnSaveAddress");
const addressLineInput = document.getElementById("addressLineInput");
const addressObservationsInput = document.getElementById("addressObservationsInput");

const ubigeo = window.ubigeo;
const jsonBody = {};
const jsonAddress = [];

submitButton.addEventListener("click", () => {
    let formIsValid = true;
    console.log(fields);
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
            jsonBody["pickUpAddress"] = jsonAddress;
        }
        let entity = verifyEntityName(); //in plural
        console.log(JSON.stringify(jsonBody));

        fetch("/api/v1/" + entity, {
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
})

/**
 * Extracts the entity name from the current URL and returns it.
 * @return {string} The entity name extracted from the URL.
 */
function verifyEntityName() {
    const url = window.location.pathname;
    let entityName = url.split("/")[2]
    console.log(entityName);
    return entityName;
}

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
    jsonAddress.push(result);
    console.log(jsonAddress);
    let listElement = document.createElement("li");
    listElement.innerHTML = result.addressLine + " ( " + result.region + ", " + result.province + ", " + result.district + ").";
    addressAddedList.appendChild(listElement);
    btnSaveAddress.innerHTML = "Guardar Otra";
});

window.onload = () => {
    populateLocationSelector();
    creationStatusSpinner.style.visibility = "hidden";
    creationSuccessAlert.style.display = "none";
    creationErrorAlert.style.display = "none";
    entityForm.addEventListener("submit", e => {
        e.preventDefault();
    })
}

