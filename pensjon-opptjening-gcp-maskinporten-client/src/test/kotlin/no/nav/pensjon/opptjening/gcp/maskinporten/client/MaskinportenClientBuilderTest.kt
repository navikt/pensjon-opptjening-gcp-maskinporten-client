package no.nav.pensjon.opptjening.gcp.maskinporten.client

import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenClientConfigurationException
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenParseJwkException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.MalformedURLException

internal class MaskinportenClientBuilderTest {

    @Test
    fun `Throws MaskinportenParseJwkException when token can not be parsed`() {
        assertThrows<MaskinportenParseJwkException> {
            MaskinportenClient.builder()
                .wellKnownUrl("https://dummyValue.com")
                .clientId("clientId")
                .privateKey("config.privateKey.toString()")
                .build()
        }
    }

    @Test
    fun `Throws MaskinportenClientBuilderMissingPropertyException when required properties are not initialized`() {
        assertThrows<MaskinportenClientConfigurationException> {
            MaskinportenClient.builder()
                .clientId("clientId")
                .privateKey("config.privateKey.toString()")
                .build()
        }.also {
            assertTrue(it.message!!.contains("Required property wellknownUrl is missing"))
        }
    }

    @Test
    fun `Assigns properties correctly`() {
        val privateKey = createPrivateKey()
        val builder = MaskinportenClient.builder()
            .wellKnownUrl("http://baseurl.com")
            .clientId("clientId")
            .privateKey(privateKey)

        assertEquals("http://baseurl.com", builder.propWellKnown)
        assertEquals("clientId", builder.propClientId)
        assertEquals(privateKey, builder.propPrivateKey)
    }

    @Test
    fun `throws MalformedUrlException if wellknownurl is invalid`() {
        assertThrows<MalformedURLException> {
            MaskinportenClient.builder()
                .wellKnownUrl("baseurl.com:1234")
                .clientId("clientId")
                .privateKey(createPrivateKey())
                .build()
        }
    }

}

private fun createPrivateKey() = RSAKeyGenerator(2048).keyID("123").generate().toJSONString()