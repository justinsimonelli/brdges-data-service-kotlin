package com.justinsimonelli.brdges.data.service.models

data class BridgeStatusResponse(
    val statuses: MutableMap<String, BridgeStatus>,
    var lastUpdated: String?
)