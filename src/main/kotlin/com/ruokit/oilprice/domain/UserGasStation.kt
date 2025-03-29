package com.ruokit.oilprice.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "tb_user_gas_station")
data class UserGasStation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(name = "station_name")
    val stationName: String,

    val address: String,

    @Column(name = "oil_type")
    val oilType: String,

    val price: Double,

    val distance: Double,

    @Column(name = "wgs84_x")
    val wgs84X: Double,

    @Column(name = "wgs84_y")
    val wgs84Y: Double,

    @Column(name = "collected_at")
    val collectedAt: LocalDate
)
