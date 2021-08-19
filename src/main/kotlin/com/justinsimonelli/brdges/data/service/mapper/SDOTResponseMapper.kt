package com.justinsimonelli.brdges.data.service.mapper

import com.justinsimonelli.brdges.data.service.models.BridgeStatus
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import org.springframework.stereotype.Component

private const val UP = "up"
private const val DOWN = "down"

@Component
class SDOTResponseMapper {

    fun map(bridgeData: List<BridgeData>): Map<String, BridgeStatus> =
        bridgeData.associate { it.cleanName() to buildBridgeStatus(it) }

    private fun buildBridgeStatus(bridgeData: BridgeData): BridgeStatus {
        return BridgeStatus(
            id = bridgeData.id.toString(),
            name = bridgeData.name,
            status = if (bridgeData.isOpenToTraffic()) DOWN else UP,
            closedToTrafficAt = bridgeData.closedToTrafficAt,
            reopenedToTrafficAt = bridgeData.reopenedToTrafficAt,
            lat = bridgeData.lat,
            lng = bridgeData.lng
        )
    }
}