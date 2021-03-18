package no.nav.pensjon.opptjening.gcp.maskinporten.client.config

import com.nimbusds.jose.jwk.RSAKey
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenParseJwkException
import no.nav.pensjon.opptjening.gcp.maskinporten.client.verifyEnvironmentVariables


class MaskinportenEnvVariableConfig {

    companion object {
        const val MASKINPORTEN_HOST_KEY = "MASKINPORTEN_HOST"
        const val MASKINPORTEN_CLIENT_ID_KEY = "MASKINPORTEN_CLIENT_ID"
        const val MASKINPORTEN_JWK_PRIVATE_KEY = "MASKINPORTEN_JWK_PRIVATE_KEY"
        const val MASKINPORTEN_SCOPE_KEY = "MASKINPORTEN_SCOPE"
        const val MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY = "MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS"
    }

    private val requiredEnvKey = listOf(
        MASKINPORTEN_HOST_KEY,
        MASKINPORTEN_CLIENT_ID_KEY,
        MASKINPORTEN_JWK_PRIVATE_KEY,
        MASKINPORTEN_SCOPE_KEY,
        MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY
    )

    fun create(env: Map<String, String>): MaskinportenConfig {
        env.verifyEnvironmentVariables(requiredEnvKey)
        return MaskinportenConfig(
            baseUrl = env[MASKINPORTEN_HOST_KEY]!!,
            clientId = env[MASKINPORTEN_CLIENT_ID_KEY]!!,
            privateKey = parseJwk(env[MASKINPORTEN_JWK_PRIVATE_KEY]!!),
            scope = env[MASKINPORTEN_SCOPE_KEY]!!,
            validInSeconds = env[MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY]!!.toInt()
        )
    }

    private fun parseJwk(jwk: String) = try {
        RSAKey.parse(jwk)
    } catch (e: Exception) {
        throw MaskinportenParseJwkException()
    }
}