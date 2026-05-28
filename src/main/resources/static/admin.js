const adminState = {
    dashboard: null,
    places: [],
    reports: [],
    crawlCandidates: [],
    editingPlaceId: null
};

const dashboardStats = document.querySelector("#dashboardStats");
const placesTable = document.querySelector("#placesTable");
const placeSummary = document.querySelector("#placeSummary");
const reportsList = document.querySelector("#reportsList");
const crawlList = document.querySelector("#crawlList");
const toast = document.querySelector("#toast");
const reportStatusFilter = document.querySelector("#reportStatusFilter");
const crawlStatusFilter = document.querySelector("#crawlStatusFilter");
const adminPages = [...document.querySelectorAll(".admin-page")];
const adminNavLinks = [...document.querySelectorAll(".admin-nav a[href^='#']")];
const adminPageKicker = document.querySelector("#adminPageKicker");
const adminPageTitle = document.querySelector("#adminPageTitle");
const placeForm = document.querySelector("#placeForm");
const placeFormTitle = document.querySelector("#placeFormTitle");
const placeFormMode = document.querySelector("#placeFormMode");
const placeSubmitButton = document.querySelector("#placeSubmitButton");
const placeCancelButton = document.querySelector("#placeCancelButton");

document.querySelector("#refreshButton").addEventListener("click", loadAdmin);
placeForm.addEventListener("submit", savePlace);
placeCancelButton.addEventListener("click", resetPlaceForm);
document.querySelector("#crawlForm").addEventListener("submit", createCrawlCandidate);
reportStatusFilter.addEventListener("change", loadReports);
crawlStatusFilter.addEventListener("change", loadCrawlCandidates);
window.addEventListener("hashchange", renderAdminPage);

async function loadAdmin() {
    await Promise.all([
        loadDashboard(),
        loadPlaces(),
        loadReports(),
        loadCrawlCandidates()
    ]);
}

async function loadDashboard() {
    adminState.dashboard = await fetchJson("/api/admin/dashboard");
    renderDashboard();
}

async function loadPlaces() {
    adminState.places = await fetchJson("/api/admin/places");
    renderPlacesTable();
}

async function loadReports() {
    const params = new URLSearchParams();
    if (reportStatusFilter.value) {
        params.set("status", reportStatusFilter.value);
    }
    adminState.reports = await fetchJson(`/api/admin/reports?${params}`);
    renderReports();
}

async function loadCrawlCandidates() {
    const params = new URLSearchParams();
    if (crawlStatusFilter.value) {
        params.set("status", crawlStatusFilter.value);
    }
    adminState.crawlCandidates = await fetchJson(`/api/admin/crawl-candidates?${params}`);
    renderCrawlCandidates();
}

function renderDashboard() {
    const dashboard = adminState.dashboard;
    const stats = [
        ["활성 장소", dashboard.placeCount],
        ["전체 제보", dashboard.reportCount],
        ["검수 대기", dashboard.pendingReportCount],
        ["활성 리뷰", dashboard.reviewCount]
    ];

    dashboardStats.replaceChildren(...stats.map(([label, value]) => {
        const item = document.createElement("article");
        item.className = "admin-stat";
        item.innerHTML = `<span>${label}</span><strong>${value}</strong>`;
        return item;
    }));
}

function renderAdminPage() {
    const targetId = window.location.hash.slice(1) || "dashboard";
    const activePage = adminPages.find(page => page.id === targetId) ?? adminPages[0];

    adminPages.forEach(page => {
        page.hidden = page !== activePage;
    });

    adminNavLinks.forEach(link => {
        const isActive = link.getAttribute("href") === `#${activePage.id}`;
        link.classList.toggle("active", isActive);
        if (isActive) {
            link.setAttribute("aria-current", "page");
        } else {
            link.removeAttribute("aria-current");
        }
    });

    adminPageKicker.textContent = activePage.dataset.pageKicker || "Admin Console";
    adminPageTitle.textContent = activePage.dataset.pageTitle || "야구틀어주는술집 운영 관리";
}

function renderPlacesTable() {
    placeSummary.textContent = `${adminState.places.length}개`;
    placesTable.replaceChildren();

    adminState.places.forEach(place => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>
                <strong>${escapeHtml(place.name)}</strong>
                <span>${escapeHtml(place.address)}</span>
            </td>
            <td>${escapeHtml(place.district)}</td>
            <td><span class="status ${statusClass(place.status)}">${escapeHtml(place.status)}</span></td>
            <td>${Number(place.rating).toFixed(1)} / ${place.reviewCount}</td>
            <td>
                <div class="admin-row-actions">
                    <button class="admin-secondary" type="button" data-action="edit">수정</button>
                    <button class="admin-danger" type="button" data-action="delete" ${place.status === "INACTIVE" ? "disabled" : ""}>
                        비활성
                    </button>
                </div>
            </td>
        `;
        row.querySelector("[data-action='edit']").addEventListener("click", () => startEditPlace(place));
        row.querySelector("[data-action='delete']").addEventListener("click", () => deletePlace(place.id));
        placesTable.append(row);
    });
}

function renderReports() {
    reportsList.replaceChildren();

    if (adminState.reports.length === 0) {
        reportsList.append(emptyText("제보가 없습니다."));
        return;
    }

    adminState.reports.forEach(report => {
        const item = document.createElement("article");
        item.className = "admin-list-item";
        item.innerHTML = `
            <div>
                <div class="admin-item-title">
                    <strong>${escapeHtml(report.placeName)}</strong>
                    <span class="status ${statusClass(report.status)}">${escapeHtml(report.status)}</span>
                </div>
                <p>${escapeHtml(report.address)}</p>
                <p>${escapeHtml(report.content)}</p>
                <small>${escapeHtml(report.team)} · ${formatDate(report.createdAt)}</small>
                ${report.rejectReason ? `<p class="admin-reason">반려 사유: ${escapeHtml(report.rejectReason)}</p>` : ""}
            </div>
            <div class="admin-actions"></div>
        `;

        const actions = item.querySelector(".admin-actions");
        if (report.status === "PENDING") {
            const approve = document.createElement("button");
            approve.className = "admin-primary";
            approve.type = "button";
            approve.textContent = "승인";
            approve.addEventListener("click", () => approveReport(report));

            const reject = document.createElement("button");
            reject.className = "admin-secondary";
            reject.type = "button";
            reject.textContent = "반려";
            reject.addEventListener("click", () => rejectReport(report.id));
            actions.append(approve, reject);
        }

        reportsList.append(item);
    });
}

function renderCrawlCandidates() {
    crawlList.replaceChildren();

    if (adminState.crawlCandidates.length === 0) {
        crawlList.append(emptyText("크롤링 후보가 없습니다."));
        return;
    }

    adminState.crawlCandidates.forEach(candidate => {
        const item = document.createElement("article");
        item.className = "admin-list-item";
        item.innerHTML = `
            <div>
                <div class="admin-item-title">
                    <strong>${escapeHtml(candidate.name)}</strong>
                    <span class="status ${statusClass(candidate.status)}">${escapeHtml(candidate.status)}</span>
                </div>
                <p>${escapeHtml(candidate.address)}</p>
                <small>${escapeHtml(candidate.keyword)} · ${formatDate(candidate.collectedAt)}</small>
            </div>
            <a class="admin-link" href="${escapeAttribute(candidate.mapLink)}" target="_blank" rel="noreferrer">지도</a>
        `;
        crawlList.append(item);
    });
}

async function savePlace(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const body = placeBody(new FormData(form));

    if (adminState.editingPlaceId) {
        await sendJson(`/api/places/${adminState.editingPlaceId}`, "PATCH", body);
        showToast("장소 정보를 수정했습니다.");
    } else {
        await sendJson("/api/places", "POST", body);
        showToast("장소를 등록했습니다.");
    }

    resetPlaceForm();
    await Promise.all([loadDashboard(), loadPlaces()]);
}

function startEditPlace(place) {
    adminState.editingPlaceId = place.id;
    placeFormTitle.textContent = "장소 수정";
    placeFormMode.textContent = `#${place.id} 기존 장소`;
    placeSubmitButton.textContent = "수정 저장";
    placeCancelButton.hidden = false;

    placeForm.elements.name.value = place.name ?? "";
    placeForm.elements.address.value = place.address ?? "";
    placeForm.elements.latitude.value = place.latitude ?? 0;
    placeForm.elements.longitude.value = place.longitude ?? 0;
    placeForm.elements.teamId.value = place.teamId ?? 0;
    placeForm.elements.team.value = place.team ?? "";
    placeForm.elements.category.value = place.category ?? "";
    placeForm.elements.phone.value = place.phone ?? "";
    placeForm.elements.note.value = place.note ?? "";
    placeForm.elements.tags.value = (place.tags ?? []).join(", ");
    placeForm.scrollIntoView({behavior: "smooth", block: "start"});
}

function resetPlaceForm() {
    adminState.editingPlaceId = null;
    placeForm.reset();
    placeForm.elements.latitude.value = "37.5122";
    placeForm.elements.longitude.value = "127.0719";
    placeForm.elements.teamId.value = "1";
    placeForm.elements.team.value = "LG 트윈스";
    placeForm.elements.category.value = "술집";
    placeFormTitle.textContent = "장소 등록";
    placeFormMode.textContent = "신규 응원 장소";
    placeSubmitButton.textContent = "등록";
    placeCancelButton.hidden = true;
}

async function deletePlace(placeId) {
    await fetch(`/api/places/${placeId}`, {method: "DELETE"});
    showToast("장소를 비활성화했습니다.");
    await Promise.all([loadDashboard(), loadPlaces()]);
}

async function approveReport(report) {
    await sendJson(`/api/admin/reports/${report.id}/approve`, "POST", {
        name: report.placeName,
        address: report.address,
        latitude: 37.5122,
        longitude: 127.0719,
        teamId: report.teamId,
        team: report.team
    });
    showToast("제보를 승인하고 장소로 등록했습니다.");
    await Promise.all([loadDashboard(), loadPlaces(), loadReports()]);
}

async function rejectReport(reportId) {
    const reason = window.prompt("반려 사유를 입력하세요.", "운영 기준에 맞지 않습니다.");
    if (reason === null) {
        return;
    }

    await sendJson(`/api/admin/reports/${reportId}/reject`, "POST", {reason});
    showToast("제보를 반려했습니다.");
    await Promise.all([loadDashboard(), loadReports()]);
}

async function createCrawlCandidate(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const data = new FormData(form);

    await sendJson("/api/admin/crawl-candidates", "POST", {
        keyword: data.get("keyword"),
        name: data.get("name"),
        address: data.get("address"),
        phone: data.get("phone"),
        mapLink: data.get("mapLink")
    });
    form.reset();
    form.elements.mapLink.value = "https://map.naver.com/";
    showToast("크롤링 후보를 등록했습니다.");
    await loadCrawlCandidates();
}

function placeBody(data) {
    return {
        name: data.get("name"),
        address: data.get("address"),
        latitude: Number(data.get("latitude")),
        longitude: Number(data.get("longitude")),
        teamId: Number(data.get("teamId")),
        team: data.get("team"),
        category: data.get("category"),
        phone: data.get("phone"),
        note: data.get("note"),
        tags: splitList(data.get("tags")),
        status: "ACTIVE"
    };
}

async function fetchJson(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`${url} ${response.status}`);
    }
    return response.json();
}

async function sendJson(url, method, body) {
    const response = await fetch(url, {
        method,
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(body)
    });
    if (!response.ok) {
        throw new Error(`${url} ${response.status}`);
    }
    return response.status === 204 ? null : response.json();
}

function splitList(value) {
    return String(value ?? "")
            .split(",")
            .map(item => item.trim())
            .filter(Boolean);
}

function emptyText(text) {
    const element = document.createElement("p");
    element.className = "empty";
    element.textContent = text;
    return element;
}

function statusClass(status) {
    return String(status).toLowerCase();
}

function formatDate(value) {
    return String(value).slice(0, 16).replace("T", " ");
}

function showToast(message) {
    toast.textContent = message;
    toast.classList.add("show");
    window.setTimeout(() => toast.classList.remove("show"), 1800);
}

function escapeHtml(value) {
    return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
}

function escapeAttribute(value) {
    return escapeHtml(value).replaceAll("`", "&#096;");
}

renderAdminPage();

loadAdmin().catch(error => {
    console.error(error);
    showToast("관리자 데이터를 불러오지 못했습니다.");
});
