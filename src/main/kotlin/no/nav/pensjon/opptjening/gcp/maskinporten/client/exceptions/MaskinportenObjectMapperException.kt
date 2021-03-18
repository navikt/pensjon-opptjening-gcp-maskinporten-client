package no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions

class MaskinportenObjectMapperException(message: String) : Exception("Feil ved deserialisering av response fra maskinporten: $message")