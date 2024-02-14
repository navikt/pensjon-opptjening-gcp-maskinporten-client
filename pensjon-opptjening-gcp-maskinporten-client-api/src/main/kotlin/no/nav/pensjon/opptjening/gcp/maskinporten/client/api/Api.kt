package no.nav.pensjon.opptjening.gcp.maskinporten.client.api

import no.nav.pensjon.opptjening.gcp.maskinporten.client.MaskinportenClient
import no.nav.pensjon.opptjening.gcp.maskinporten.client.config.MaskinportenEnvVariableConfigCreator
import no.nav.security.token.support.core.api.Protected
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api")
@Protected
class Api(
    @Value("\${MASKINPORTEN_WELL_KNOWN_URL}") var wellknownUrl: String
) {

    private val client = MaskinportenClient(
        MaskinportenEnvVariableConfigCreator.createMaskinportenConfig(
            wellKnownUrl = wellknownUrl,
            clientId = wellknownUrl,
            privateKey = wellknownUrl,
            scopes = wellknownUrl,
            validInSeconds = wellknownUrl,
        )
    )

    @GetMapping("/token")
    fun token(): String {
        return client.tokenString
    }
}