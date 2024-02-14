package no.nav.pensjon.opptjening.gcp.maskinporten.client

import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.SignedJWT
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenClientException
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenObjectMapperException
import no.nav.pensjon.opptjening.gcp.maskinporten.client.mock.MaskinportenMock
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.util.Date
import kotlin.math.absoluteValue


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MaskinportenClientImplTest {
    private var maskinportenMock: MaskinportenMock = MaskinportenMock()

    private lateinit var maskinportenClient: MaskinportenClient

    private val privateKey: RSAKey = RSAKeyGenerator(2048).keyID("123").generate()
    private val publicKey: RSAKey = privateKey.toPublicJWK()


    @BeforeEach
    internal fun beforeEach() {
        maskinportenMock.reset()
        maskinportenClient = MaskinportenClient.builder()
            .wellKnownUrl(maskinportenMock.baseUrl())
            .clientId("client")
            .privateKey(privateKey.toString())
            .build()
    }

    @AfterAll
    internal fun teardown() {
        maskinportenMock.stop()
    }

    @Test
    fun `reuse token from maskinporten if not expired`() {
        maskinportenMock.`mock valid response for only one call`()

        val firstToken = maskinportenClient.token(MaskinportenMock.DEFAULT_SCOPE)
        val secondToken = maskinportenClient.token(MaskinportenMock.DEFAULT_SCOPE)
        assertEquals(firstToken, secondToken)
    }

    @Test
    fun `throws MaskinportenObjectMapperException if response from maskinporten cant be mapped`() {
        maskinportenMock.`mock invalid JSON response`()

        assertThrows<MaskinportenObjectMapperException> { maskinportenClient.token("scope") }
    }

    @Test
    fun `Throws MaskinportenClientException when status other than 200 is returned from maskinporten`() {
        maskinportenMock.`mock 500 server error`()

        assertThrows<MaskinportenClientException> { maskinportenClient.token("scope") }
    }

    @Test
    fun `Grant type on token request is correct`() {
        maskinportenMock.`mock valid response for only one call`()
        maskinportenClient.token(MaskinportenMock.DEFAULT_SCOPE)
        assertEquals("urn:ietf:params:oauth:grant-type:jwt-bearer", maskinportenMock.grant())
    }

    @Test
    fun `Assertion is signed with private key`() {
        maskinportenMock.`mock valid response for only one call`()
        maskinportenClient.token(MaskinportenMock.DEFAULT_SCOPE)

        val signedAssertion = SignedJWT.parse(maskinportenMock.assertion())
        val verifier = RSASSAVerifier(publicKey)

        assertTrue(signedAssertion.verify(verifier))
    }

    @Test
    fun `Signing algorithm is RSA256`() {
        maskinportenMock.`mock valid response for only one call`()
        maskinportenClient.token(MaskinportenMock.DEFAULT_SCOPE)

        val signedAssertion = SignedJWT.parse(maskinportenMock.assertion())

        assertEquals("RS256", signedAssertion.header.algorithm.name)
    }

    @Test
    fun `Required values are added to assertion`() {
        maskinportenMock.`mock valid response for only one call`()
        maskinportenClient.token(MaskinportenMock.DEFAULT_SCOPE)

        val signedAssertion = SignedJWT.parse(maskinportenMock.assertion())

        assertEquals("${maskinportenMock.baseUrl()}/", signedAssertion.jwtClaimsSet.audience.single())
        assertEquals("client", signedAssertion.jwtClaimsSet.issuer)
        assertEquals("scope", signedAssertion.jwtClaimsSet.getClaim("scope"))
        assertTrue(Date() equalWithinOneSecond signedAssertion.jwtClaimsSet.issueTime)
        assertTrue(Date() addSeconds 120 equalWithinOneSecond signedAssertion.jwtClaimsSet.expirationTime)
    }

    @Test
    fun `Optional claims are added to assertion`() {
        maskinportenMock.`mock valid response for only one call`()
        maskinportenClient.token(MaskinportenMock.DEFAULT_SCOPE, "resource", "jti")

        val signedAssertion = SignedJWT.parse(maskinportenMock.assertion())

        assertEquals("resource", signedAssertion.jwtClaimsSet.getClaim("resource"))
        assertEquals("jti", signedAssertion.jwtClaimsSet.getClaim("jti"))
    }
}

private infix fun Date.equalWithinOneSecond(date: Date): Boolean = (time - date.time).absoluteValue < 1000L
