package com.ruokit.oilprice.domain

import jakarta.persistence.*

@Entity
@Table(name = "tb_users")
data class User(
    @Id
    val id: String, // 카카오 채널 사용자 ID 등

    val name: String? = null,
    val address: String,
    val lat: Double,
    val lon: Double
)
