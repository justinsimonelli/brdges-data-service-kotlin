package com.justinsimonelli.brdges.data.service.models

data class AvailableBridge(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val status: String? = null,
    val closedAt: String? = null
){
    fun cleanName(): String = name.replace("\\s+".toRegex(), "_").toLowerCase()
}
