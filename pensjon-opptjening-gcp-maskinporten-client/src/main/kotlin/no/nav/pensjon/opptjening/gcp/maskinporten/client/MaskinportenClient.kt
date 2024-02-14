package no.nav.pensjon.opptjening.gcp.maskinporten.client

interface MaskinportenClient {
    fun token(scope: String): String {
        return token(scope, null)
    }

    fun token(scope: String, resource: String?): String {
        return token(scope, resource, null)
    }

    fun token(scope: String, resource: String?, jti: String?): String

    companion object {
        fun builder(): MaskinportenClientBuilder = MaskinportenClientBuilder()
    }
}