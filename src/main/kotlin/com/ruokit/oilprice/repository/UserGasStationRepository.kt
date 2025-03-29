package com.ruokit.oilprice.repository

import com.ruokit.oilprice.domain.UserGasStation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserGasStationRepository : JpaRepository<UserGasStation, Long>