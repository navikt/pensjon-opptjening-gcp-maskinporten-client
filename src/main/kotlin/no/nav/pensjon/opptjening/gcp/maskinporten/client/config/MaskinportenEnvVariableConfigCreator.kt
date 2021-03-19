package no.nav.pensjon.opptjening.gcp.maskinporten.client.config

import com.nimbusds.jose.jwk.RSAKey
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenParseJwkException
import no.nav.pensjon.opptjening.gcp.maskinporten.client.verifyEnvironmentVariables
import java.net.URI
import java.net.URL


class MaskinportenEnvVariableConfigCreator {

    companion object {
        const val MASKINPORTEN_WELL_KNOWN_URL_KEY = "MASKINPORTEN_WELL_KNOWN_URL"
        const val MASKINPORTEN_CLIENT_ID_KEY = "MASKINPORTEN_CLIENT_ID"
        const val MASKINPORTEN_CLIENT_JWK_KEY = "MASKINPORTEN_CLIENT_JWK"
        const val MASKINPORTEN_SCOPES_KEY = "MASKINPORTEN_SCOPES"
        const val MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY = "MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS"

        private val requiredEnvKey = listOf(
            MASKINPORTEN_WELL_KNOWN_URL_KEY,
            MASKINPORTEN_CLIENT_ID_KEY,
            MASKINPORTEN_CLIENT_JWK_KEY,
            MASKINPORTEN_SCOPES_KEY
        )

        fun createMaskinportenConfig(env: Map<String, String>): MaskinportenConfig {
            env.verifyEnvironmentVariables(requiredEnvKey)

            return MaskinportenConfig(
                baseUrl = createBaseUrl(env[MASKINPORTEN_WELL_KNOWN_URL_KEY]!!),
                clientId = env[MASKINPORTEN_CLIENT_ID_KEY]!!,
                privateKey = parseJwk(env[MASKINPORTEN_CLIENT_JWK_KEY]!!),
                scope = env[MASKINPORTEN_SCOPES_KEY]!!,
                validInSeconds = env.getOrDefault(MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY, "120").toInt()
            )
        }

        private fun parseJwk(jwk: String) = try {
            RSAKey.parse(jwk)
        } catch (e: Exception) {
            throw MaskinportenParseJwkException()
        }

        //TODO make pretty
        private fun createBaseUrl(url: String): String {
            val wellKnownUrl = URI.create(url).toURL()
            return "${wellKnownUrl.protocol}://${wellKnownUrl.host}${createPortString(wellKnownUrl)}"
        }

        private fun createPortString(url: URL) = if (url.port <= 0) "" else ":${url.port}"
    }
}