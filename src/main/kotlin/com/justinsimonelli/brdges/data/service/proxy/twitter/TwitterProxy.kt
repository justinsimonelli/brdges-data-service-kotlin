package com.justinsimonelli.brdges.data.service.proxy.twitter

import com.justinsimonelli.brdges.data.service.models.twitter.TwitterResponse
import com.justinsimonelli.brdges.data.service.secrets.AwsSecretsRetriever
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private const val HEADER_AUTHORIZATION = "Authorization"
private const val TWITTER_AUTH_TOKEN_KEY = "twitter_auth_token"

@Component
class TwitterProxy(
    private val httpClient: HttpClient,
    private val secretsRetriever: AwsSecretsRetriever,
    @Value("\${twitter.query.handle}") private val queryHandle: String,
    @Value("\${twitter.query.url}") private val queryUrl: String) {

    private var sinceId: String? = null

    suspend fun searchLatestTweets(): TwitterResponse {
        val twitterResponse = httpClient
            .get<TwitterResponse>(buildTwitterQuery()) {
                header(HEADER_AUTHORIZATION, "Bearer ${secretsRetriever.getSecret(TWITTER_AUTH_TOKEN_KEY)}")
            }

        sinceId = twitterResponse.meta?.newestId

        return twitterResponse
    }

    private fun buildTwitterQuery(): String {
        val endpoint = "${queryUrl}from:${queryHandle}&tweet.fields=created_at"

        return if (sinceId?.isNotBlank() == true) {
            endpoint.plus("&since_id=${sinceId}")
        } else endpoint
    }
}