package com.justinsimonelli.brdges.data.service.models

data class BridgeStatus(
    val id: String,
    val name: String,
    var status: String?,
    var closedAt: String? = null,
    val lat: Double,
    val lng: Double
)
