package no.nav.pensjon.opptjening.gcp.maskinporten.client.config

import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_CLIENT_ID_KEY
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_HOST_KEY
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_JWK_PRIVATE_KEY
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator.Companion.MASKINPORTEN_SCOPE_KEY
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
            createMaskinportenConfig(envVariables(privateKey = "Faulty private jwk"))
        }
    }

    @Test
    fun `Throws MissingEnvironmentVariables when environment variables are missing`() {
        val exception = assertThrows<MissingEnvironmentVariablesException> { createMaskinportenConfig(emptyMap()) }

        assertTrue(exception.message!! containWord MASKINPORTEN_HOST_KEY)
        assertTrue(exception.message!! containWord MASKINPORTEN_CLIENT_ID_KEY)
        assertTrue(exception.message!! containWord MASKINPORTEN_JWK_PRIVATE_KEY)
        assertTrue(exception.message!! containWord MASKINPORTEN_SCOPE_KEY)
        assertTrue(exception.message!! containWord MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY)

    }

    @Test
    fun `Maps values correctly`() {
        val envVariables = envVariables(createPrivateKey())
        val config: MaskinportenConfig = createMaskinportenConfig(envVariables)

        assertEquals(envVariables[MASKINPORTEN_HOST_KEY],config.baseUrl)
        assertEquals(envVariables[MASKINPORTEN_CLIENT_ID_KEY],config.clientId)
        assertEquals(envVariables[MASKINPORTEN_JWK_PRIVATE_KEY],config.privateKey.toJSONString())
        assertEquals(envVariables[MASKINPORTEN_SCOPE_KEY],config.scope)
        assertEquals(envVariables[MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY]!!.toInt(),config.validInSeconds)
    }

    private fun envVariables(privateKey: String) = mapOf(
        MASKINPORTEN_HOST_KEY to "https://dummyValue.com",
        MASKINPORTEN_CLIENT_ID_KEY to "testClient",
        MASKINPORTEN_JWK_PRIVATE_KEY to privateKey,
        MASKINPORTEN_SCOPE_KEY to "testScope",
        MASKINPORTEN_JWT_EXPIRATION_TIME_IN_SECONDS_KEY to "120",
    )

}

private infix fun String.containWord(word: String) = this.contains(word)

private fun createPrivateKey() = RSAKeyGenerator(2048).keyID("123").generate().toJSONString()