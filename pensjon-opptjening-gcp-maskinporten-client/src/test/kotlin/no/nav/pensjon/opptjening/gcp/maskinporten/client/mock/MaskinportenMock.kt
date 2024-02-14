package no.nav.pensjon.opptjening.gcp.maskinporten.client.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.pensjon.opptjening.gcp.maskinporten.client.MaskinportenClientImpl.Companion.CONTENT_TYPE
import no.nav.pensjon.opptjening.gcp.maskinporten.client.MaskinportenClientImpl.Companion.GRANT_TYPE
import java.util.Date

internal class MaskinportenMock {
    private var wireMockServer = WireMockServer(PORT)
    private val privateKey: RSAKey = RSAKeyGenerator(2048).keyID("123").generate()

    init {
        wireMockServer.start()
    }

    internal fun reset() {
        wireMockServer.resetAll()
    }

    internal fun stop() {
        wireMockServer.stop()
    }

    internal fun `mock  maskinporten token enpoint`() {
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo(TOKEN_PATH))
                .withHeader("Content-Type", WireMock.equalTo(CONTENT_TYPE))
                .withRequestBody(WireMock.matchingJsonPath("$.grant_type", WireMock.matching(GRANT_TYPE)))
                .withRequestBody(WireMock.matchingJsonPath("$.assertion"))
                .willReturn(
                    WireMock.ok(
                        """{
                      "access_token" : "${createMaskinportenToken()}",
                      "token_type" : "Bearer",
                      "expires_in" : 599,
                      "scope" : "difitest:test1"
                    }
                """
                    )
                )
        )
    }

    internal fun `mock valid response for only one call`() {
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo(TOKEN_PATH))
                .withHeader("Content-Type", WireMock.equalTo(CONTENT_TYPE))
                .inScenario("First time")
                .whenScenarioStateIs(Scenario.STARTED)
                .withRequestBody(WireMock.matching("grant_type=$GRANT_TYPE&assertion=.*"))
                .willReturn(
                    WireMock.ok(
                        """{
                      "access_token" : "${createMaskinportenToken()}",
                      "token_type" : "Bearer",
                      "expires_in" : 599,
                      "scope" : "difitest:test1"
                    }
                """
                    )
                )
                .willSetStateTo("Ended")
        )
    }

    internal fun `mock invalid JSON response`() {
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo(TOKEN_PATH))
                .withHeader("Content-Type", WireMock.equalTo(CONTENT_TYPE))
                .withRequestBody(WireMock.matching("grant_type=$GRANT_TYPE&assertion=.*"))
                .willReturn(WireMock.ok("""w"""))
        )
    }

    internal fun `mock 500 server error`() {
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlPathEqualTo(TOKEN_PATH))
                .withHeader("Content-Type", WireMock.equalTo(CONTENT_TYPE))
                .withRequestBody(WireMock.matching("grant_type=$GRANT_TYPE&assertion=.*"))
                .willReturn(WireMock.serverError().withBody("test body"))
        )
    }

    private fun createMaskinportenToken(): String {
        val claimsSet = JWTClaimsSet.Builder()
            .subject("alice")
            .issuer("https://c2id.com")
            .expirationTime(Date(Date().time + (60 * 1000)))
            .claim("scope", DEFAULT_SCOPE)
            .build()
        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(privateKey.keyID).build(),
            claimsSet
        )
        val signer: JWSSigner = RSASSASigner(privateKey)
        signedJWT.sign(signer)
        return signedJWT.serialize()
    }

    fun grant(): String {
        return wireMockServer.allServeEvents.single().request.formParameters["grant_type"]!!.values.single()
    }

    fun assertion(): String {
        return wireMockServer.allServeEvents.single().request.formParameters["assertion"]!!.values().single()
    }

    fun baseUrl(): String {
        return MASKINPORTEN_MOCK_HOST
    }

    companion object {

        private const val PORT = 8096
        private const val TOKEN_PATH = "/token"
        private const val MASKINPORTEN_MOCK_HOST = "http://localhost:$PORT"
        const val DEFAULT_SCOPE = "scope"
    }
}