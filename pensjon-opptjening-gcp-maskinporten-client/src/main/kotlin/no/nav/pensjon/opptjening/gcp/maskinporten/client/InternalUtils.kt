package no.nav.pensjon.opptjening.gcp.maskinporten.client

import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MissingEnvironmentVariablesException
import java.util.*

internal fun String.suffix(s: String) = if (endsWith(s)) this else plus(s)
internal infix fun Date.addSeconds(seconds: Int): Date = Date(time + seconds * ONE_SECOND_IN_MILLISECONDS)
internal const val ONE_SECOND_IN_MILLISECONDS = 1000
internal fun Map<String, String>.verifyEnvironmentVariables(keys: List<String>) {
    val missingKeys = keys.filter { this[it] == null }
    if (missingKeys.isNotEmpty()) throw MissingEnvironmentVariablesException(missingKeys.joinToString(", ", "Missing env keys: "))
}