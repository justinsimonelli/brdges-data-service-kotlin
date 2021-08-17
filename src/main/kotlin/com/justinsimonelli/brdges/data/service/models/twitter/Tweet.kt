package com.justinsimonelli.brdges.data.service.models.twitter

import com.fasterxml.jackson.annotation.JsonAlias

data class Tweet(
    val id: String,
    @JsonAlias("created_at") val createdAt: String,
    val text: String?)
