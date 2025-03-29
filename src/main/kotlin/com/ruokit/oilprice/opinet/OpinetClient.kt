package com.ruokit.oilprice.opinet

interface OpinetClient {
    /**
     * 오피넷에서 전체 주유소 정보를 수집하는 메서드
     * @return 주유소 응답 리스트
     */
    fun fetchAllStations(): List<OilStationResponse>
    /**
     * KATEC 좌표 기준 반경 내 주유소 목록 조회
     * @param x KATEC X 좌표 (중심)
     * @param y KATEC Y 좌표 (중심)
     * @param radius 반경 (단위: 미터)
     * @return 해당 좌표 반경 내 주유소 정보 리스트
     */
    fun fetchStationsAround(x: Int, y: Int, radius: Int): List<OilStationResponse>
}
