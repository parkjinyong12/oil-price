package com.ruokit.oilprice.kakao

data class KakaoRequest(
    val userRequest: UserRequest
)

data class UserRequest(
    val utterance: String
)

typealias KakaoResponse = Map<String, Any>
