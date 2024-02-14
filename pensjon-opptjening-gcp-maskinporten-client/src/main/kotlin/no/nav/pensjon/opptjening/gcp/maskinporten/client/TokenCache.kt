package no.nav.pensjon.opptjening.gcp.maskinporten.client

import com.nimbusds.jwt.SignedJWT
import java.util.Date


internal class TokenCache {

    private val cache: MutableMap<String, SignedJWT> = mutableMapOf()

    fun get(scope: String, resource: String?): SignedJWT? {
        val key = "$scope$resource"
        val token = cache[key]
        return if (token != null && !token.isExpired) {
            token
        } else {
            cache.remove(key)
            null
        }
    }

    fun put(token: SignedJWT) {
        val key = token.jwtClaimsSet.let {
            val scope = it.getStringClaim("scope")
            val resource = it.getStringClaim("resource")
            "$scope$resource"
        }
        cache[key] = token
    }

    private val SignedJWT.isExpired: Boolean
        get() = jwtClaimsSet?.expirationTime?.is20SecondsPrior?.not() ?: false

    private val Date.is20SecondsPrior: Boolean
        get() = epochSeconds - (now.epochSeconds + TWENTY_SECONDS) >= 0

    private val Date.epochSeconds: Long
        get() = time / 1000

    private val now: Date
        get() = Date()

    companion object {
        private const val TWENTY_SECONDS = 20
    }
}
