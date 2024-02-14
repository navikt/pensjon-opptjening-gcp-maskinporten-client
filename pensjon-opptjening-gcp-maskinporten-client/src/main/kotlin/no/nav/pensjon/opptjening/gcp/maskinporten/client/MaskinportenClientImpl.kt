package no.nav.pensjon.opptjening.gcp.maskinporten.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenConfig
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenClientException
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenObjectMapperException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpResponse.BodyHandlers.ofString
import java.util.Date


internal class MaskinportenClientImpl(
    private val config: MaskinportenConfig
) : MaskinportenClient {
    private var tokenCache: TokenCache = TokenCache()

    private val httpClient: HttpClient = HttpClient.newBuilder().proxy(config.proxy).build()
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    override fun token(scope: String, resource: String?, jti: String?): String {
        val cached = tokenCache.get(scope, resource)
        return when (cached != null) {
            true -> {
                cached.parsedString
            }

            false -> {
                val claims = JWTClaimsSet.Builder().apply {
                    audience(config.issuer)
                    issuer(config.clientId)
                    claim("scope", scope)
                    jti?.also { claim("jti", it) }
                    resource?.also { claim("resource", it) }
                    issueTime(Date())
                    expirationTime(Date() addSeconds 120)
                }.build()

                fetchToken(sign(claims)).let {
                    tokenCache.put(it)
                    it.parsedString
                }
            }
        }
    }

    private fun sign(claims: JWTClaimsSet): String {
        return SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(config.privateKey.keyID).build(),
            claims
        ).apply {
            sign(RSASSASigner(config.privateKey))
        }.serialize()
    }

    private fun fetchToken(assertion: String): SignedJWT {
        return httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create(config.baseUrl + MASKINPORTEN_TOKEN_PATH))
                .header("Content-Type", CONTENT_TYPE)
                .POST(ofString("grant_type=$GRANT_TYPE&assertion=${assertion}"))
                .build(),
            ofString()
        ).run {
            if (statusCode() != 200) throw MaskinportenClientException(this)
            SignedJWT.parse(mapToMaskinportenResponseBody(body()).access_token)
        }
    }

    private fun mapToMaskinportenResponseBody(responseBody: String): MaskinportenResponseBody = try {
        objectMapper.readValue(responseBody)
    } catch (e: Exception) {
        throw MaskinportenObjectMapperException(e.toString())
    }

    companion object {
        internal const val MASKINPORTEN_TOKEN_PATH = "/token"
        internal const val GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer"
        internal const val CONTENT_TYPE = "application/x-www-form-urlencoded"
    }

    internal data class MaskinportenResponseBody(
        val access_token: String,
        val token_type: String?,
        val expires_in: Int?,
        val scope: String?
    )
}
