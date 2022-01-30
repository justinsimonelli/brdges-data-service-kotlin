package com.justinsimonelli.brdges.data.service.mapper

import com.justinsimonelli.brdges.data.service.models.BridgeStatus
import com.justinsimonelli.brdges.data.service.models.gov.BridgeData
import org.springframework.stereotype.Component

private const val UP = "up"
private const val DOWN = "down"

@Component
class SDOTResponseMapper {

    fun map(latestBridgeData: List<BridgeData>): MutableMap<String, BridgeStatus> =
        latestBridgeData.associateTo(mutableMapOf()) {
            it.cleanName() to mapBridgeStatus(it)
        }

    private fun mapBridgeStatus(bridgeData: BridgeData): BridgeStatus {
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