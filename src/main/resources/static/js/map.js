// Thymeleaf 변수
const address = /*[[${address}]]*/ '';
const radius = /*[[${radius}]]*/ '';

console.log("서버에서 받은 주소:", address);
console.log("서버에서 받은 반경:", radius);

// 주유소 데이터 가져오기
fetch(`/nearby?address=${encodeURIComponent(address)}&radius=${radius}`)
    .then(r => {
        if (!r.ok) {
            throw new Error(`HTTP error! status: ${r.status}`);
        }
        return r.json();
    })
    .then(data => {
        console.log("받은 데이터:", data);
        if (!data || !data.length) {
            console.log("데이터가 없습니다.");
            return;
        }

        const map = L.map('map').setView([data[0].wgs84Y, data[0].wgs84X], 13);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(map);

        const minPrice = Math.min(...data.map(s => s.price));
        const bounds = L.latLngBounds();
        const stationList = document.getElementById('stations');
        const markerMap = new Map();

        const defaultIcon = new L.Icon({
            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-blue.png',
            shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowSize: [41, 41]
        });

        const redIcon = new L.Icon({
            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
            shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowSize: [41, 41]
        });

        data.sort((a, b) => a.price - b.price).forEach(station => {
            const isLowest = station.price === minPrice;
            const icon = isLowest ? redIcon : defaultIcon;

            const marker = L.marker([station.wgs84Y, station.wgs84X], { icon }).addTo(map);
            const tooltip = marker.bindTooltip(
                `<strong style="color:${isLowest ? 'red' : 'black'}">${station.stationName}</strong><br/>${station.price}원`,
                { 
                    direction: 'top', 
                    offset: [0, -42],
                    permanent: false,
                    interactive: true,
                    opacity: 0.9,
                    sticky: true,
                    delay: 200,
                    className: 'custom-tooltip'
                }
            );

            // 마우스 이벤트 직접 처리
            marker.on('mouseover', function(e) {
                this.openTooltip();
            });

            marker.on('mouseout', function(e) {
                this.closeTooltip();
            });

            markerMap.set(station.stationName, marker);

            bounds.extend([station.wgs84Y, station.wgs84X]);

            const div = document.createElement('div');
            div.className = 'station-item';
            div.innerHTML = `<span class="${isLowest ? 'lowest' : ''}">${station.stationName}</span><br/><span>${station.price.toLocaleString()}원</span>`;

            div.addEventListener('click', () => {
                const target = markerMap.get(station.stationName);
                if (target) {
                    // 기존에 열린 모든 툴팁 닫기
                    map.eachLayer((layer) => {
                        if (layer instanceof L.Marker) {
                            layer.closeTooltip();
                        }
                    });
                    
                    const latLng = target.getLatLng();
                    map.setView(latLng, 15);
                    target.openTooltip();
                }
            });

            stationList.appendChild(div);
        });

        map.fitBounds(bounds, { paddingTopLeft: [40, 40], maxZoom: 15 });
    })
    .catch(error => {
        console.error('주유소 데이터 가져오기 실패:', error);
        alert('주유소 정보를 가져오는데 실패했습니다.');
    }); 