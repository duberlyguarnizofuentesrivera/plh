//------------Helper functions------------
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

//------------End Helper Functions-------

const params = new URLSearchParams();
const fields = document.querySelectorAll('.filter-form');
const table = document.getElementById("table-body");
const loadingIndicator = document.getElementById("loading-indicator");
//add change listener to each field
fields.forEach(field => {
    field.addEventListener('change', doFilter);
});

function fetchUsersWithFilters() {
    console.log('Fetching users with filters')
    //disable fields until fetch is complete
    fields.forEach(field => {
        field.disabled = true;
    });
    loadingIndicator.style.visibility = "visible";
    console.log(loadingIndicator.style.visibility);
    console.log("/api/v1/users?" + params.toString());
    fetch("/api/v1/users?" + params.toString())
        .then((response) => response.json()
            .then((data) => {
                console.log("Received data")
                console.log(data);
                table.innerHTML = "";
                data.forEach((user) => {
                    const tr = document.createElement("tr");
                    let statusLabel;
                    if (user.status.label === "Activo") {
                        statusLabel = "<span class='badge rounded-pill text-bg-success'>" +
                                "<i class='bi bi-check-circle-fill pe-1'></i> " +
                                user.status.label +
                                "</span>";
                        } else {
                            statusLabel = "<span class='badge rounded-pill text-bg-danger'>" +
                                "<i class='bi bi bi-x-circle-fill pe-1'></i> " +
                                user.status.label +
                                "</span>";
                        }
                        tr.innerHTML =
                            "<td>" + user.idNumber + "</td>" +
                            "<td><a href="
                            + user.links[0].href + "><i class='bi bi-person-fill pe-1'></i> "
                            + user.username + "</a></td>" +
                            "<td>" + user.firstName + " " + user.lastName + "</td>" +
                            "<td><a href="
                            + user.links[1].href + "><i class='bi bi-ticket-detailed pe-1'></i> "
                            + "Tickets</a></td>" +
                            "<td>" + user.role.label + "</td>" +
                            "<td>" + statusLabel + "</td>";
                    table.appendChild(tr);

                })
                    //enable fields and hide spinner, but wait half a second to give a "loading" effect
                    fields.forEach(field => {
                        field.disabled = false;
                    });
                    loadingIndicator.style.visibility = "hidden";
                    console.log(loadingIndicator.style.visibility);

                }
            )
        )
}

function fetchClientsWithFilters() {
    console.log('Fetching users with filters')
    //disable fields until fetch is complete
    fields.forEach(field => {
        field.disabled = true;
    });
    loadingIndicator.style.visibility = "visible";
    console.log(loadingIndicator.style.visibility);
    console.log("/api/v1/clients?" + params.toString());
    fetch("/api/v1/clients?" + params.toString())
        .then((response) => response.json()
            .then((data) => {
                console.log("Received data")
                console.log(data);
                table.innerHTML = "";
                data.forEach((user) => {
                    const tr = document.createElement("tr");
                    let statusLabel;
                    if (user.status.label === "Activo") {
                        statusLabel = "<span class='badge rounded-pill text-bg-success'>" +
                                "<i class='bi bi-check-circle-fill pe-1'></i> " +
                                user.status.label +
                                "</span>";
                        } else {
                            statusLabel = "<span class='badge rounded-pill text-bg-danger'>" +
                                "<i class='bi bi bi-x-circle-fill pe-1'></i> " +
                                user.status.label +
                                "</span>";
                        }
                        tr.innerHTML =
                            "<td>" + user.idNumber + "</td>" +
                            "<td><a href='/system/users/crud/by-username/"
                            + user.username + "'><i class='bi bi-person-fill pe-1'></i> "
                            + user.username + "</a></td>" +
                            "<td>" + user.firstName + " " + user.lastName + "</td>" +
                            "<td>" + user.role.label + "</td>" +
                            "<td>" + statusLabel + "</td>";
                        table.appendChild(tr);

                    })
                    //enable fields and hide spinner, but wait half a second to give a "loading" effect
                    fields.forEach(field => {
                        field.disabled = false;
                    });
                    loadingIndicator.style.visibility = "hidden";
                    console.log(loadingIndicator.style.visibility);

                }
            )
        )
}

function doFilter() {
    fields.forEach(field => {
        params.set(field.id, field.value)
    });
    //call the filter function
    //now we filter by entity to do the fetch operation
    const currentEntityUrl = window.location.pathname;
    //format: /system/<entity>/...
    const entity = currentEntityUrl.split("/")[2];
    console.log(entity);
    switch (entity) {
        case "users":
            fetchUsersWithFilters();
            break;
        case "clients":
            fetchClientsWithFilters()
            break;
    }
}

window.onload = () => {
    doFilter();
}