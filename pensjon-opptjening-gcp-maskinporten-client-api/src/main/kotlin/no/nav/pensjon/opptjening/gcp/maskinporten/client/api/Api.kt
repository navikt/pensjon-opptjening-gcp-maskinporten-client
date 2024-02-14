package no.nav.pensjon.opptjening.gcp.maskinporten.client.api

import no.nav.pensjon.opptjening.gcp.maskinporten.client.MaskinportenClient
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@EnableJwtTokenValidation
@Protected
class Api(
    private val maskinportenClient: MaskinportenClient
) {

    @GetMapping("/token")
    fun token(): ResponseEntity<String> {
        return try {
            ResponseEntity.ok(maskinportenClient.tokenString)
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().body(ex.message)
        }
    }
}