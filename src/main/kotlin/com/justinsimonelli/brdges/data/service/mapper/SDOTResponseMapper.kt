package com.justinsimonelli.brdges.data.service.mapper

import com.justinsimonelli.brdges.data.service.models.BridgeStatus
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val CLOSED = "closed"
private const val UP = "up"
private const val DOWN = "down"

const val SDOT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

@Component
class SDOTResponseMapper(private val dateFormatter: DateTimeFormatter) {

    fun map(bridgeData: List<BridgeData>?): Map<String, BridgeStatus>? =
        bridgeData?.associate { it.cleanName() to buildBridgeStatus(it) }

    private fun buildBridgeStatus(bridgeData: BridgeData): BridgeStatus {
        val isBridgeDown = bridgeData.status.toLowerCase() == CLOSED
        return BridgeStatus(
            id = bridgeData.id.toString(),
            name = bridgeData.name,
            status = if (isBridgeDown) DOWN else UP,
            closedAt = if (isBridgeDown) null else dateFormatter.format(ZonedDateTime.now()),
            lat = bridgeData.lat,
            lng = bridgeData.lng
        )
    }
}