package no.nav.pensjon.opptjening.gcp.maskinporten.client

import com.nimbusds.jose.jwk.RSAKey
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenConfig
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenClientConfigurationException
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenParseJwkException
import java.net.ProxySelector
import java.net.URI
import java.net.URL

class MaskinportenClientBuilder {
    lateinit var propWellKnown: String
    lateinit var propClientId: String
    lateinit var propPrivateKey: String

    fun wellKnownUrl(value: String): MaskinportenClientBuilder {
        propWellKnown = value
        return this
    }

    fun clientId(value: String): MaskinportenClientBuilder {
        propClientId = value
        return this
    }

    fun privateKey(value: String): MaskinportenClientBuilder {
        propPrivateKey = value
        return this
    }

    fun build(): MaskinportenClient {
        if (!this::propWellKnown.isInitialized) throw MaskinportenClientConfigurationException("Required property wellknownUrl is missing")
        if (!this::propClientId.isInitialized) throw MaskinportenClientConfigurationException("Required property clientId is missing")
        if (!this::propPrivateKey.isInitialized) throw MaskinportenClientConfigurationException("Required property privateKey is missing")

        return MaskinportenClientImpl(
            config = MaskinportenConfig(
                baseUrl = createBaseUrl(propWellKnown),
                clientId = propClientId,
                privateKey = parseJwk(propPrivateKey),
                proxy = ProxySelector.getDefault()
            )
        )
    }

    private fun parseJwk(jwk: String) = try {
        RSAKey.parse(jwk)
    } catch (e: Exception) {
        throw MaskinportenParseJwkException()
    }

    private fun createBaseUrl(url: String): String {
        val wellKnownUrl = URI.create(url).toURL()
        return "${wellKnownUrl.protocol}://${wellKnownUrl.host}${createPortString(wellKnownUrl)}"
    }

    private fun createPortString(url: URL) = if (url.port <= 0) "" else ":${url.port}"
}