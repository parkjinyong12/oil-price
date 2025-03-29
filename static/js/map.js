// 전역 변수
let map;
let markers = [];
let markerClusterer;
let currentData = [];
let currentFilters = {
    type: 'all',
    sort: 'price',
    region: 'all'
};

// 지도 초기화
function initMap() {
    const container = document.getElementById('map');
    const options = {
        center: new kakao.maps.LatLng(37.5665, 126.9780),
        level: 8
    };
    map = new kakao.maps.Map(container, options);
    
    // 마커 클러스터러 초기화
    markerClusterer = new kakao.maps.MarkerClusterer({
        map: map,
        markers: [],
        gridSize: 60,
        averageCenter: true,
        minLevel: 8,
        disableClickZoom: true,
        styles: [{
            width: '40px',
            height: '40px',
            background: 'rgba(0,123,255,0.8)',
            borderRadius: '50%',
            color: '#fff',
            textAlign: 'center',
            fontWeight: 'bold',
            lineHeight: '40px'
        }]
    });
}

// 유가 데이터 가져오기
async function getOilPriceData() {
    try {
        const response = await fetch('/api/oil-prices');
        const data = await response.json();
        currentData = data;
        updateMarkers();
        updateSidebar();
    } catch (error) {
        console.error('데이터 로딩 실패:', error);
    }
}

// 마커 업데이트
function updateMarkers() {
    // 기존 마커 제거
    markers.forEach(marker => marker.setMap(null));
    markers = [];
    
    // 필터링된 데이터로 마커 생성
    const filteredData = filterData(currentData);
    
    filteredData.forEach(item => {
        const marker = new kakao.maps.Marker({
            position: new kakao.maps.LatLng(item.lat, item.lng),
            map: map
        });
        
        // 마커 클릭 이벤트
        kakao.maps.event.addListener(marker, 'click', () => {
            showDetailModal(item);
        });
        
        markers.push(marker);
    });
    
    // 클러스터러 업데이트
    markerClusterer.clear();
    markerClusterer.addMarkers(markers);
}

// 사이드바 업데이트
function updateSidebar() {
    const sidebar = document.getElementById('sidebar');
    const filteredData = filterData(currentData);
    
    sidebar.innerHTML = `
        <div class="filter-section">
            <h5>필터</h5>
            <div class="mb-3">
                <button class="btn btn-sm filter-btn ${currentFilters.type === 'all' ? 'active' : ''}" 
                        onclick="setFilter('type', 'all')">전체</button>
                <button class="btn btn-sm filter-btn ${currentFilters.type === 'gasoline' ? 'active' : ''}" 
                        onclick="setFilter('type', 'gasoline')">휘발유</button>
                <button class="btn btn-sm filter-btn ${currentFilters.type === 'diesel' ? 'active' : ''}" 
                        onclick="setFilter('type', 'diesel')">경유</button>
                <button class="btn btn-sm filter-btn ${currentFilters.type === 'lpg' ? 'active' : ''}" 
                        onclick="setFilter('type', 'lpg')">LPG</button>
            </div>
            <div class="mb-3">
                <select class="form-select" onchange="setFilter('sort', this.value)">
                    <option value="price" ${currentFilters.sort === 'price' ? 'selected' : ''}>가격순</option>
                    <option value="distance" ${currentFilters.sort === 'distance' ? 'selected' : ''}>거리순</option>
                </select>
            </div>
        </div>
    `;
    
    // 가격 카드 추가
    filteredData.forEach(item => {
        const card = createPriceCard(item);
        sidebar.appendChild(card);
    });
}

// 필터 설정
function setFilter(type, value) {
    currentFilters[type] = value;
    updateMarkers();
    updateSidebar();
}

// 데이터 필터링
function filterData(data) {
    let filtered = [...data];
    
    // 주유소 종류 필터링
    if (currentFilters.type !== 'all') {
        filtered = filtered.filter(item => item.type === currentFilters.type);
    }
    
    // 정렬
    if (currentFilters.sort === 'price') {
        filtered.sort((a, b) => a.price - b.price);
    } else if (currentFilters.sort === 'distance') {
        // 거리순 정렬 로직 구현
    }
    
    return filtered;
}

// 가격 카드 생성
function createPriceCard(item) {
    const card = document.createElement('div');
    card.className = 'price-card';
    card.innerHTML = `
        <div class="price-info">
            <h6>${item.name}</h6>
            <span class="price">${item.price}원</span>
        </div>
        <div class="address-info">${item.address}</div>
        <button class="btn btn-detail" onclick="showDetailModal(${JSON.stringify(item)})">
            상세정보
        </button>
    `;
    return card;
}

// 상세 정보 모달 표시
function showDetailModal(item) {
    const modal = new bootstrap.Modal(document.getElementById('detailModal'));
    const modalBody = document.querySelector('#detailModal .modal-body');
    
    modalBody.innerHTML = `
        <h5>${item.name}</h5>
        <p>주소: ${item.address}</p>
        <p>휘발유: ${item.gasoline}원</p>
        <p>경유: ${item.diesel}원</p>
        <p>LPG: ${item.lpg}원</p>
        <p>업데이트: ${new Date(item.updated_at).toLocaleString()}</p>
    `;
    
    modal.show();
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    initMap();
    getOilPriceData();
    
    // 5분마다 데이터 갱신
    setInterval(getOilPriceData, 300000);
}); 