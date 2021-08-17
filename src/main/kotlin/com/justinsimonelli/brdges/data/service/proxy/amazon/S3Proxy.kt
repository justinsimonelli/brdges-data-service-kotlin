package com.justinsimonelli.brdges.data.service.proxy.amazon

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.justinsimonelli.brdges.data.service.models.AvailableBridge
import org.springframework.stereotype.Component

private const val BRDGES_BUCKET_NAME = "brdges"
private const val BRDGES_AVAILABLE_BRIDGE_LIST_LOCATION_DATA_FILE = "available_bridges_location_data.json"


@Component
class S3Proxy(
    private val amazonS3: AmazonS3,
    private val gson: Gson
) {
    fun getAvailableBridges(): List<AvailableBridge> {
        val objectContentString = String(getS3ObjectContent().readAllBytes())
        return gson.fromJson(objectContentString, object : TypeToken<List<AvailableBridge>>() {}.type)
    }

    private fun getS3ObjectContent(): S3ObjectInputStream =
        amazonS3.getObject(GetObjectRequest(BRDGES_BUCKET_NAME, BRDGES_AVAILABLE_BRIDGE_LIST_LOCATION_DATA_FILE)).objectContent
}