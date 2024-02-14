package no.nav.pensjon.opptjening.gcp.maskinporten.client

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.Date

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TokenCacheTest {
    private val privateKey: RSAKey = RSAKeyGenerator(2048).keyID("123").generate()

    @Test
    fun `assert expired if expiration time on token is under 20 seconds from now`() {
        val cache = TokenCache().apply {
            put(createMaskinportenToken(scope = "scope", resource = null, expiresIn = 19))
        }

        assertNull(cache.get(scope = "scope", resource = null))
    }

    @Test
    fun `should not be expired if expiration time is over 20 seconds from now`() {
        val cache = TokenCache().apply {
            put(createMaskinportenToken(scope = "scope", resource = null, expiresIn = 30))
        }
        assertNotNull(cache.get(scope = "scope", resource = null))
    }

    @Test
    fun `cache tokens with equal scope`() {
        val cache = TokenCache()

        assertNull(cache.get(scope = "scope", resource = null))
        cache.put(createMaskinportenToken(scope = "scope", resource = null, expiresIn = 120))
        assertNotNull(cache.get(scope = "scope", resource = null))
    }

    @Test
    fun `caches tokens with equal scope and resource`() {
        val cache = TokenCache()

        assertNull(cache.get(scope = "a", resource = null))
        val a = createMaskinportenToken(scope = "a", resource = null, expiresIn = 120)
        cache.put(a)
        val aa = createMaskinportenToken(scope = "a", resource = "b", expiresIn = 120)
        cache.put(aa)

        assertEquals(a, cache.get("a", null))
        assertEquals(aa, cache.get("a", "b"))
        assertNotEquals(a, aa)
    }

    private fun createMaskinportenToken(scope: String, resource: String?, expiresIn: Int): SignedJWT {
        val claimsSet = JWTClaimsSet.Builder().apply {
            claim("scope", scope)
            resource?.also { claim("resource", it) }
            expirationTime(Date(Date().time + expiresIn * 1000))
        }.build()

        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(privateKey.keyID).build(),
            claimsSet
        )
        signedJWT.sign(RSASSASigner(privateKey))
        return signedJWT
    }
}