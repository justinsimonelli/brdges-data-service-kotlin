package com.justinsimonelli.brdges.data.service.models.twitter

import com.fasterxml.jackson.annotation.JsonAlias

data class Meta(
    @JsonAlias("newest_id") val newestId: String?,
    @JsonAlias("oldest_id") val oldestId: String?,
    @JsonAlias("result_count") val resultCount: Int?,
    @JsonAlias("next_token") val nextToken: String?
)
