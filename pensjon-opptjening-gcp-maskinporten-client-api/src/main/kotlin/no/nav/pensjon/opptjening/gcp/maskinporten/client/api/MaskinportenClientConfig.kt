package no.nav.pensjon.opptjening.gcp.maskinporten.client.api

import no.nav.pensjon.opptjening.gcp.maskinporten.client.MaskinportenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod-gcp","dev-gcp")
class MaskinportenClientConfig {

    @Bean
    fun maskinportenClient(
        @Value("\${MASKINPORTEN_WELL_KNOWN_URL}") wellKnownUrl: String,
        @Value("\${MASKINPORTEN_CLIENT_ID}") clientId: String,
        @Value("\${MASKINPORTEN_CLIENT_JWK}") privateKey: String,
    ): MaskinportenClient {
        return MaskinportenClient.builder()
            .wellKnownUrl(wellKnownUrl)
            .clientId(clientId)
            .privateKey(privateKey)
            .build()
    }
}