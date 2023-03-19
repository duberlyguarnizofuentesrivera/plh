//------------Helper functions------------
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

//------------End Helper Functions-------

const params = new URLSearchParams();
const fields = document.querySelectorAll('.filter-form');
const sortingFields = document.querySelectorAll('.dataTable-sorter');
const table = document.getElementById("table-body");
const loadingIndicator = document.getElementById("loading-indicator");
const pagination = document.getElementById("pagination");
const paginationInfo = document.getElementById("pagination-info");
let currentPage = 1;
let currentSortOrder = "asc";
let currentSort = "firstName";
//add change listener to each field
fields.forEach(field => {
    field.addEventListener('change', doFilter);
});

function fetchUsersWithFilters() {
    clearPagination();
    console.log('Fetching users with filters')
    //disable fields until fetch is complete
    fields.forEach(field => {
        field.disabled = true;
    });
    loadingIndicator.style.visibility = "visible";
    console.log(loadingIndicator.style.visibility);
    console.log("/api/v1/users?" + params.toString() + "&page=" + currentPage + "&sort=" + currentSort + "&order=" + currentSortOrder);
    fetch("/api/v1/users?" + params.toString() + "&page=" + currentPage + "&sort=" + currentSort + "&order=" + currentSortOrder)
        .then((response) => {

                if (response.status === 200) {
                    response.json()
                        .then((data) => {
                                console.log("Received data")
                                console.log(data['_embedded']['userBasicDtoList']);
                                console.log(data['page']);

                                table.innerHTML = "";
                                data['_embedded']['userBasicDtoList'].forEach((user) => {
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
                                        + user._links.self.href + "><i class='bi bi-person-fill pe-1'></i> "
                                        + user.username + "</a></td>" +
                                        "<td>" + user.firstName + " " + user.lastName + "</td>" +
                                        "<td><a href="
                                        + user._links.search.href + "><i class='bi bi-ticket-detailed pe-1'></i> "
                                        + "Tickets</a></td>" +
                                        "<td>" + user.role.label + "</td>" +
                                        "<td>" + statusLabel + "</td>";
                                    table.appendChild(tr);

                                })

                                //pagination
                                const numberOfPages = data['page']['totalPages'];
                                const activePage = data['page']['number'] + 1;
                                const pageSize = data['page']['size'];
                                const totalElements = data['page']['totalElements'];
                                const paginationFooter = "Mostrando página " + activePage + " de " + numberOfPages + ". Total: " + totalElements + " registros.";

                                //First page link creation (starting from 1)

                                if (activePage > 1) {
                                    const firstPageListItem = document.createElement('li');
                                    const firstPageLink = document.createElement('a');
                                    firstPageLink.href = "#";
                                    firstPageLink.innerHTML = "Anterior";
                                    firstPageLink.addEventListener("click", () => {
                                        currentPage = activePage - 1;
                                        fetchUsersWithFilters();
                                    });
                                    firstPageListItem.appendChild(firstPageLink);
                                    firstPageListItem.className = 'page-item'
                                    pagination.appendChild(firstPageListItem)
                                }

                                // Numbered page links creation
                                for (let i = 0; i < numberOfPages; i++) {
                                    const pageListItem = document.createElement('li');
                                    const pageLink = document.createElement('a');
                                    pageLink.addEventListener("click", () => {
                                        currentPage = i + 1;
                                        fetchUsersWithFilters();
                                    });
                                    pageLink.innerHTML = (i + 1).toString();
                                    if (i === activePage - 1) {
                                        pageLink.className = 'page-link active';
                                    }
                                    pageListItem.appendChild(pageLink);
                                    pageListItem.className = 'page-item';
                                    pagination.appendChild(pageListItem)
                                    paginationInfo.innerHTML = paginationFooter;
                                }

                                // Next page link creation
                                if (currentPage < numberOfPages) {
                                    const nextPageListItem = document.createElement('li');
                                    const nextPageLink = document.createElement('a');
                                    nextPageLink.href = "#";
                                    nextPageLink.innerHTML = "Siguiente";
                                    nextPageLink.addEventListener("click", () => {
                                        currentPage = activePage + 1;
                                        fetchUsersWithFilters();
                                    });
                                    nextPageListItem.appendChild(nextPageLink);
                                    nextPageListItem.className = 'page-item';
                                    pagination.appendChild(nextPageListItem)
                                }

                                //re-enable fields
                                fields.forEach(field => {
                                    field.disabled = false;
                                });
                                loadingIndicator.style.visibility = "hidden";
                                console.log(loadingIndicator.style.visibility);
                            }
                        )
                } else if (response.status === 204) {
                    console.log("No content found matching the filters")
                    sleep(1000).then(r =>
                        fields.forEach(field => {
                            field.disabled = false;
                            loadingIndicator.style.visibility = "hidden";
                            table.innerHTML = "";
                        }));

                    console.log(loadingIndicator.style.visibility);
                } else if (response.status === 400) {
                    console.log("Invalid parameters...")
                    sleep(1000).then(r =>
                        fields.forEach(field => {
                            field.disabled = false;
                            loadingIndicator.style.visibility = "hidden";
                            table.innerHTML = "";
                        }));
                    console.log(loadingIndicator.style.visibility);
                }
            }
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

function clearPagination() {
    pagination.innerHTML = "";
}


function initializeSort() {
    sortingFields.forEach(field => {
        field.addEventListener("click", (event) => {
            console.log("entering setup!")
            try {
                currentSort = event.target.getAttribute("data-sorter");
                console.log("currentSort: " + currentSort)
                if (currentSortOrder === "asc") {
                    currentSortOrder = "desc";
                } else {
                    currentSortOrder = "asc";
                }
                clearPagination();
                doFilter();
            } catch (e) {
                //nothing here...
                console.log("exception caught")
                console.log(e)
            }
        });
    })
}

function doFilter() {
    currentPage = 1;
    console.log("doing filter")
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
    initializeSort();
}