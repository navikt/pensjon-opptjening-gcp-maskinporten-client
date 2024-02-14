package no.nav.pensjon.opptjening.gcp.maskinporten.client.api

import com.nimbusds.jose.JOSEObjectType
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.springframework.stereotype.Service

@Service
internal class TestTokenIssuer(
    private val oauth2Server: MockOAuth2Server
) {
    companion object {
        const val ACCEPTED_AUDIENCE = "pensjon-opptjening-afp-api"
    }

    fun token(
        issuerId: String,
        audience: String,
        scopes: List<String>,
        roles: List<String>,
    ): String {
        return oauth2Server.issueToken(
            issuerId = issuerId,
            clientId = "theclientid",
            tokenCallback = DefaultOAuth2TokenCallback(
                issuerId = issuerId,
                subject = "subject",
                typeHeader = JOSEObjectType.JWT.type,
                audience = listOf(audience),
                claims = mapOf(
                    "scope" to scopes,
                    "roles" to roles,
                ),
                expiry = 3600
            )
        ).serialize()
    }

    fun bearerToken(
        issuerId: String,
        audience: String = ACCEPTED_AUDIENCE,
        scopes: List<String> = emptyList(),
        roles: List<String> = emptyList()
    ): String {
        return """Bearer ${token(issuerId, audience, scopes, roles)}"""
    }
}