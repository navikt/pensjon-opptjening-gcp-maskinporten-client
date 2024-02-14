package no.nav.pensjon.opptjening.gcp.maskinporten.client.api

import no.nav.pensjon.opptjening.gcp.maskinporten.client.MaskinportenClient
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@EnableJwtTokenValidation
@Protected
class Api(
    private val maskinportenClient: MaskinportenClient
) {

    @GetMapping("/token", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun token(
        @RequestParam scope: String,
        @RequestParam resource: String?
    ): ResponseEntity<String> {
        return try {
            ResponseEntity.ok(maskinportenClient.token(scope, resource))
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().body(ex.message)
        }
    }
}