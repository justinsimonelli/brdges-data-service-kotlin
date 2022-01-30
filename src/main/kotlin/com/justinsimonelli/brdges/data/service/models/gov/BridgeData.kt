package com.justinsimonelli.brdges.data.service.models.gov

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonAutoDetect

private const val WHITESPACE_REPLACEMENT_UNDERSCORE = "_"
private val WHITESPACE_PATTERN = "\\s+".toRegex()
private const val STATUS_CLOSED = "closed"

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class BridgeData(
    @JsonAlias("BridgeID") val id: Int,
    @JsonAlias("DisplayName") val name: String,
    @JsonAlias("Latitude") val lat: Double,
    @JsonAlias("Longitude") val lng: Double,
    @JsonAlias("Status") val status: String,
    var closedToTrafficAt: String? = null,
    var reopenedToTrafficAt: String? = null,
) {
    fun cleanName() = name.replace(WHITESPACE_PATTERN, WHITESPACE_REPLACEMENT_UNDERSCORE).toLowerCase()
    fun isOpenToTraffic() = STATUS_CLOSED.equals(status, ignoreCase = true)
}