package com.justinsimonelli.brdges.data.service.models

data class BridgeStatus(
    val id: String,
    val name: String,
    var status: String?,
    var closedToTrafficAt: String? = null,
    var reopenedToTrafficAt: String? = null,
    val lat: Double,
    val lng: Double
)
