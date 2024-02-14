package no.nav.pensjon.opptjening.gcp.maskinporten.client.config

import com.nimbusds.jose.jwk.RSAKey
import no.nav.pensjon.opptjening.gcp.maskinporten.client.suffix
import java.net.ProxySelector

internal data class MaskinportenConfig(
     internal val baseUrl: String,
     internal val clientId: String,
     internal val privateKey: RSAKey,
     internal val proxy: ProxySelector = ProxySelector.getDefault()
) {
     internal val issuer = baseUrl.suffix("/")
}