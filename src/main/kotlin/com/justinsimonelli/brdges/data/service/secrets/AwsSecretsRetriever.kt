package com.justinsimonelli.brdges.data.service.secrets

import com.amazonaws.services.secretsmanager.AWSSecretsManager
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException
import com.amazonaws.services.secretsmanager.model.InvalidParameterException
import com.amazonaws.services.secretsmanager.model.InvalidRequestException
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64

private const val DECRYPTION_FAILURE = "Unable to decrypt data"
private const val INTERNAL_SERVICE_ERROR = "Internal AWS error occurred"
private const val INVALID_PARAMETERS = "Invalid parameter supplied"
private const val INVALID_REQUEST = "Invalid request"
private const val RESOURCE_NOT_FOUND = "Resource was not found"
private const val JSON_PROCESSING_ERROR = "Unable to process Json"
private const val UNKNOWN_ERROR = "Unknown exception occurred"

@Component
class AwsSecretsRetriever(
    private val secretsManager: AWSSecretsManager,
    @Value("\${aws.secrets.bucket}") private val secretBucketName: String
) {
    private val objectMapper: ObjectMapper = ObjectMapper()
    private var cachedSecretsJson: JsonNode? = null
    private var cachedSecretsString: String? = null

    init {
        refreshCachedSecrets()
    }

    fun getSecret(secretKey: String): String? = cachedSecretsJson?.get(secretKey)?.textValue()

    private fun getAllSecrets(): String? = cachedSecretsString

    @Synchronized
    fun refreshCachedSecrets() {
        try {
            val getSecretValueResult = secretsManager.getSecretValue(
                GetSecretValueRequest().withSecretId(secretBucketName)
            )

            cachedSecretsString = getSecretValueResult.secretString.ifEmpty {
                String(Base64.getDecoder().decode(getSecretValueResult.secretBinary).array())
            }

            cachedSecretsJson = objectMapper.readTree(getAllSecrets())
        } catch (e: Exception) {
            val exceptionMessage = when(e) {
                is DecryptionFailureException -> DECRYPTION_FAILURE
                is InternalServiceErrorException -> INTERNAL_SERVICE_ERROR
                is InvalidParameterException -> INVALID_PARAMETERS
                is InvalidRequestException -> INVALID_REQUEST
                is ResourceNotFoundException -> RESOURCE_NOT_FOUND
                is JsonProcessingException -> JSON_PROCESSING_ERROR
                else -> UNKNOWN_ERROR
            }

            throw SecretsRetrieverException(
                message = exceptionMessage,
                throwable = e
            )
        }
    }
}