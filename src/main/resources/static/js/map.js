// Thymeleaf 변수
const address = /*[[${address}]]*/ '';
const radius = /*[[${radius}]]*/ '';

console.log("서버에서 받은 주소:", address);
console.log("서버에서 받은 반경:", radius);

// 위치 권한 요청 함수
function requestLocationPermission() {
    if ("permissions" in navigator) {
        navigator.permissions.query({ name: 'geolocation' })
            .then(function(permissionStatus) {
                console.log('위치 권한 상태:', permissionStatus.state);
                if (permissionStatus.state === 'denied') {
                    alert('위치 정보 접근이 거부되었습니다. 브라우저 설정에서 위치 정보 접근을 허용해주세요.');
                }
            })
            .catch(function(error) {
                console.error('위치 권한 확인 실패:', error);
            });
    }
}

// 현재 위치 확인 및 주유소 데이터 가져오기
navigator.geolocation.getCurrentPosition(
    position => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;
        console.log("현재 위치:", lat, lon);
        
        // 대한민국 영토 체크
        if((36.87226 < lat < 38.300603) && (126.262021 < lon < 127.830532)) {
            console.log("대한민국 영토입니다.");
            
            // HTML 폼에서 주소와 반경 값을 가져옴
            const formAddress = document.querySelector('input[name="address"]').value;
            const formRadius = document.querySelector('select[name="radius"]').value;
            
            // 주유소 데이터 가져오기
            const searchAddress = formAddress || address || `${lat},${lon}`;
            const searchRadius = formRadius || radius;
            
            console.log("검색 주소:", searchAddress);
            console.log("검색 반경:", searchRadius);
            
            fetch(`/nearby?address=${encodeURIComponent(searchAddress)}&radius=${searchRadius}`)
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
        }
    },
    error => {
        let errorMessage = "위치 정보를 가져올 수 없습니다.";
        switch(error.code) {
            case error.PERMISSION_DENIED:
                errorMessage = "위치 정보 접근 권한이 거부되었습니다. 브라우저 설정에서 위치 정보 접근을 허용해주세요.";
                requestLocationPermission(); // 위치 권한 상태 확인
                break;
            case error.POSITION_UNAVAILABLE:
                errorMessage = "위치 정보를 사용할 수 없습니다. GPS가 켜져있는지 확인해주세요.";
                break;
            case error.TIMEOUT:
                errorMessage = "위치 정보 요청 시간이 초과되었습니다. 다시 시도해주세요.";
                break;
            default:
                errorMessage = "알 수 없는 오류가 발생했습니다.";
                break;
        }
        alert(errorMessage);
        console.error("위치 정보 오류:", error);
    },
    {
        enableHighAccuracy: true,
        timeout: 5000,
        maximumAge: 0
    }
); 