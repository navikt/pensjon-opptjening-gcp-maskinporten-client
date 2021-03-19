package no.nav.pensjon.opptjening.gcp.maskinporten.client.config

import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_CLIENT_ID_KEY
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_CLIENT_JWK_KEY
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_SCOPES_KEY
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_WELL_KNOWN_URL_KEY
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.createMaskinportenConfig
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenParseJwkException
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MissingEnvironmentVariablesException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MaskinportenEnvVariableConfigCreatorTest {

    @Test
    fun `Throws MaskinportenParseJwkException when token can not be parsed`() {
        assertThrows<MaskinportenParseJwkException> {
            createMaskinportenConfig(envVariables(privateKey = "Faulty private jwk", wellKnownUrl = "https://dummyValue.com"))
        }
    }

    @Test
    fun `Throws MissingEnvironmentVariables when environment variables are missing`() {
        val envVariables = emptyMap<String, String>()
        val exception = assertThrows<MissingEnvironmentVariablesException> { createMaskinportenConfig(envVariables) }

        assertTrue(exception.message!! containWord MASKINPORTEN_WELL_KNOWN_URL_KEY)
        assertTrue(exception.message!! containWord MASKINPORTEN_CLIENT_ID_KEY)
        assertTrue(exception.message!! containWord MASKINPORTEN_CLIENT_JWK_KEY)
        assertTrue(exception.message!! containWord MASKINPORTEN_SCOPES_KEY)
        assertTrue(exception.message!! containWord MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY)

    }

    @Test
    fun `Maps values correctly`() {
        val envVariables = envVariables(privateKey = createPrivateKey(), wellKnownUrl = "https://ver2.maskinporten.no/.well-known/oauth-authorization-server")
        val config: MaskinportenConfig = createMaskinportenConfig(envVariables)

        assertEquals("https://ver2.maskinporten.no", config.baseUrl)
        assertEquals(envVariables[MASKINPORTEN_CLIENT_ID_KEY], config.clientId)
        assertEquals(envVariables[MASKINPORTEN_CLIENT_JWK_KEY], config.privateKey.toJSONString())
        assertEquals(envVariables[MASKINPORTEN_SCOPES_KEY], config.scope)
        assertEquals(envVariables[MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY]!!.toInt(), config.validInSeconds)
    }

    @Test
    fun `add port if well known url contains port`() {
        val envVariables = envVariables(privateKey = createPrivateKey(), wellKnownUrl = "https://dummyValue.com:4444/test")
        val config: MaskinportenConfig = createMaskinportenConfig(envVariables)

        assertEquals("https://dummyValue.com:4444", config.baseUrl)
        assertEquals(envVariables[MASKINPORTEN_CLIENT_ID_KEY], config.clientId)
        assertEquals(envVariables[MASKINPORTEN_CLIENT_JWK_KEY], config.privateKey.toJSONString())
        assertEquals(envVariables[MASKINPORTEN_SCOPES_KEY], config.scope)
        assertEquals(envVariables[MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY]!!.toInt(), config.validInSeconds)
    }

    private fun envVariables(privateKey: String, wellKnownUrl: String) = mapOf(
        MASKINPORTEN_WELL_KNOWN_URL_KEY to wellKnownUrl,
        MASKINPORTEN_CLIENT_ID_KEY to "testClient",
        MASKINPORTEN_CLIENT_JWK_KEY to privateKey,
        MASKINPORTEN_SCOPES_KEY to "testScope",
        MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY to "120",
    )

}
// https://ver2.maskinporten.no/.well-known/oauth-authorization-server


private infix fun String.containWord(word: String) = this.contains(word)

private fun createPrivateKey() = RSAKeyGenerator(2048).keyID("123").generate().toJSONString()