package com.justinsimonelli.brdges.data.service.models

class BridgeStatusResponse(
    val statuses: Map<String, BridgeStatus>,
    val lastUpdated: String?
)