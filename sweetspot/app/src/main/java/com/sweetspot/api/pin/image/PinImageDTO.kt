package com.sweetspot.api.pin.image

data class PinImageDTO (
    val imageId: Long,
    val pinId: Long,
    val imageUrl: String,
    val fileName: String,
)