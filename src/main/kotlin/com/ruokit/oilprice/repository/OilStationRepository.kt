package com.ruokit.oilprice.repository

import com.ruokit.oilprice.domain.OilStation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OilStationRepository : JpaRepository<OilStation, Long> {
    @Query(
        """
    SELECT * FROM tb_gas_station_price o
    WHERE (o.station_id, o.collected_dt) IN (
        SELECT station_id, MAX(collected_dt)
        FROM tb_gas_station_price
        GROUP BY station_id
    )
    """, nativeQuery = true
    )
    fun findLatestStations(): List<OilStation>
}
