const state = {
    places: [],
    selectedId: null
};

const teamFilter = document.querySelector("#teamFilter");
const districtFilter = document.querySelector("#districtFilter");
const keywordFilter = document.querySelector("#keywordFilter");
const districtChips = document.querySelector("#districtChips");
const placeList = document.querySelector("#placeList");
const placeCount = document.querySelector("#placeCount");
const markerLayer = document.querySelector("#markerLayer");
const placeDetail = document.querySelector("#placeDetail");

const markerPositions = new Map([
    [1, [18, 28]],
    [2, [69, 32]],
    [3, [42, 56]],
    [4, [16, 58]],
    [5, [53, 43]]
]);

async function init() {
    await loadFilters();
    await loadPlaces();

    teamFilter.addEventListener("change", async () => {
        await loadFilters();
        await loadPlaces();
    });
    districtFilter.addEventListener("change", loadPlaces);
    keywordFilter.addEventListener("input", debounce(loadPlaces, 140));
}

async function loadFilters() {
    const params = new URLSearchParams();
    if (teamFilter.value) {
        params.set("team", teamFilter.value);
    }

    const response = await fetch(`/api/place-filters?${params}`);
    const filters = await response.json();

    renderOptions(teamFilter, filters.teams, "전체 구단", teamFilter.value);
    renderOptions(districtFilter, filters.districts, "전체 지역", districtFilter.value);
    renderDistrictChips(filters.districts);
}

async function loadPlaces() {
    const params = new URLSearchParams();
    if (teamFilter.value) {
        params.set("team", teamFilter.value);
    }
    if (districtFilter.value) {
        params.set("district", districtFilter.value);
    }
    if (keywordFilter.value.trim()) {
        params.set("keyword", keywordFilter.value.trim());
    }

    const response = await fetch(`/api/places?${params}`);
    state.places = await response.json();
    state.selectedId = state.places.some(place => place.id === state.selectedId)
            ? state.selectedId
            : state.places[0]?.id ?? null;

    renderPlaces();
    renderMarkers();
    renderDetail();
    syncActiveChip();
}

function renderOptions(select, values, placeholder, selectedValue) {
    select.replaceChildren(new Option(placeholder, ""));
    values.forEach(value => select.add(new Option(value, value)));
    select.value = values.includes(selectedValue) ? selectedValue : "";
}

function renderDistrictChips(values) {
    districtChips.replaceChildren();
    const chips = ["전체", ...values];

    chips.forEach(value => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "chip";
        button.textContent = value;
        button.dataset.value = value === "전체" ? "" : value;
        button.addEventListener("click", () => {
            districtFilter.value = button.dataset.value;
            loadPlaces();
        });
        districtChips.append(button);
    });

    syncActiveChip();
}

function syncActiveChip() {
    districtChips.querySelectorAll(".chip").forEach(chip => {
        chip.classList.toggle("active", chip.dataset.value === districtFilter.value);
    });
}

function renderPlaces() {
    placeCount.textContent = state.places.length;
    placeList.replaceChildren();

    if (state.places.length === 0) {
        const empty = document.createElement("p");
        empty.className = "empty";
        empty.textContent = "조건에 맞는 장소가 없습니다.";
        placeList.append(empty);
        return;
    }

    state.places.forEach(place => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = `place-card${place.id === state.selectedId ? " active" : ""}`;
        button.addEventListener("click", () => selectPlace(place.id));
        button.innerHTML = `
            <div class="place-thumb"></div>
            <div class="place-info">
                <h2>${place.name}</h2>
                <div class="meta-row">
                    <span>${place.district}</span>
                    <span>|</span>
                    <span>${place.distanceMeters}m</span>
                </div>
                <p class="address">${place.note}</p>
                <div class="meta-row">
                    <span class="star">★ ${place.rating.toFixed(1)}</span>
                    <span>(${place.reviewCount})</span>
                </div>
            </div>
        `;
        placeList.append(button);
    });
}

function renderMarkers() {
    markerLayer.replaceChildren();

    state.places.forEach(place => {
        const [left, top] = markerPositions.get(place.id) ?? [50, 50];
        const button = document.createElement("button");
        button.type = "button";
        button.className = `marker${place.id === state.selectedId ? " active" : ""}`;
        button.style.left = `${left}%`;
        button.style.top = `${top}%`;
        button.addEventListener("click", () => selectPlace(place.id));
        button.innerHTML = `
            <span class="pin"><span>T</span></span>
            <span class="marker-bubble">
                <strong>${place.name}</strong>
                <span>${place.district} | ${place.distanceMeters}m</span>
                <span class="star">★ ${place.rating.toFixed(1)}</span>
            </span>
        `;
        markerLayer.append(button);
    });
}

function renderDetail() {
    const place = state.places.find(item => item.id === state.selectedId);
    if (!place) {
        placeDetail.innerHTML = `<p class="empty">지도나 목록에서 장소를 선택하세요.</p>`;
        return;
    }

    placeDetail.innerHTML = `
        <div class="detail-thumb"></div>
        <div>
            <span class="badge">추천</span>
            <h2>${place.name}</h2>
            <div class="meta-row">
                <span>${place.district}역</span>
                <span>|</span>
                <span>${place.distanceMeters}m</span>
            </div>
            <p class="note">${place.note}</p>
            <div class="tag-row">
                ${place.tags.slice(0, 3).map(tag => `<span class="tag">${tag}</span>`).join("")}
            </div>
            <div class="meta-row">
                <span class="star">★ ${place.rating.toFixed(1)}</span>
                <span>(${place.reviewCount})</span>
                <span>|</span>
                <span>리뷰 보기</span>
            </div>
        </div>
        <div class="detail-actions">
            <a href="${place.naverMapUrl}" target="_blank" rel="noreferrer">길찾기</a>
        </div>
    `;
}

function selectPlace(id) {
    state.selectedId = id;
    renderPlaces();
    renderMarkers();
    renderDetail();
}

function debounce(callback, delay) {
    let timerId;
    return (...args) => {
        window.clearTimeout(timerId);
        timerId = window.setTimeout(() => callback(...args), delay);
    };
}

init();
