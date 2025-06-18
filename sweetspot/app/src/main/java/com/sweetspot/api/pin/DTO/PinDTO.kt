package com.sweetspot.api.pin.DTO

data class PinDTO(
    val pinId: Long,
    val userId: Long,
    val categoryId: Long ?,
    val latitude: Double,
    val longitude: Double,
    var title: String,
    var description: String,
)