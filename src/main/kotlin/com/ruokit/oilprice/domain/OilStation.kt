package com.ruokit.oilprice.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "tb_gas_station_price")
data class OilStation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "station_id")
    val stationId: String,

    @Column(name = "station_name")
    val stationName: String,

    @Column(name = "oil_type")
    val oilType: String,

    val price: Double,

    @Column(name = "katec_x")
    val katecX: Double,

    @Column(name = "katec_y")
    val katecY: Double,

    // 경도(lon)
    @Column(name = "wgs84_x")
    val wgs84X: Double,

    // 위도(lat)
    @Column(name = "wgs84_y")
    val wgs84Y: Double,

    @Column(name = "collected_dt")
    val collectedDt: LocalDateTime,

    @Column(name = "batch_id")
    var batchId: String? = null
)
