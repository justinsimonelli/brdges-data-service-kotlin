package com.justinsimonelli.brdges.data.service.mapper

import com.justinsimonelli.brdges.data.service.models.AvailableBridge
import com.justinsimonelli.brdges.data.service.models.BridgeStatus
import com.justinsimonelli.brdges.data.service.models.twitter.TwitterResponse
import org.springframework.stereotype.Component
import java.util.regex.Pattern

private const val up = "up"
private const val down = "down"

@Component
class TwitterResponseMapper {
    private val bridgeNamePattern =
        Pattern.compile("(The\\s+)([1-9a-zA-Z\\s+]+)(\\s+Bridge)", Pattern.CASE_INSENSITIVE)
    private val whiteSpacePattern = "\\s+".toRegex()
    private val whiteSpaceReplacement = "_"

    fun map(twitterResponse: TwitterResponse?,
            availableBridges: Collection<AvailableBridge>): Map<String, BridgeStatus> {
        if (twitterResponse == null || twitterResponse.data?.isEmpty() == true) {
            return emptyMap()
        }

        val statusMap = availableBridges
            .associateBy(
                { it.cleanName() },
                { BridgeStatus(id = it.id, name = it.name, status = down, lat = it.lat, lng = it.lng) }
            ).toMutableMap()

        twitterResponse
            .data
            ?.reversed()
            ?.forEach { tweet ->
                var bridgeName = getBridgeNameFromTweet(tweet.text)
                if (bridgeName.isNullOrEmpty()) {
                    return@forEach // write to S3 or email or something?
                }

                bridgeName = bridgeName.replace(whiteSpacePattern, whiteSpaceReplacement).toLowerCase()

                val isClosed = tweet.text?.toLowerCase()?.contains("closed") ?: false

                statusMap[bridgeName]?.apply {
                    this.status = if (isClosed) up else down
                    this.closedAt = if (isClosed) tweet.createdAt else null
                }
            }

        return statusMap
    }

    private fun getBridgeNameFromTweet(text: String?): String? {
        val tweetText = text ?: ""
        val matcher = bridgeNamePattern.matcher(tweetText)
        return if (matcher.lookingAt()) {
            matcher.group(2)
        } else null
    }
}