package com.sweetspot.api.pin.DTO

import com.sweetspot.api.pin.image.PinImageInfoDTO
import java.time.LocalDateTime

data class PinResponseDTO(
    val pinId: Long,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: LocalDateTime?,
    val imageInfo: PinImageInfoDTO?
)