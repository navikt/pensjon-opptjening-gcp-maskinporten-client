### pensjon-opptjening-gcp-maskinporten-client
Klient for uthenting av token fra maskinporten.

Se https://docs.nais.io/security/auth/maskinporten/client/ for flere detaljer.

Nye versjoner releases manuelt fra github.

### pensjon-opptjening-gcp-maskinporten-client-api
Api som tilbyr uthenting av maskinporten-token for test.

Se https://docs.nais.io/security/auth/maskinporten/

Deploy til testmiljø gjøres manuelt fra github ved behov.

#### Hvordan bruke?

Følg oppskriften under, eventuelt lag et eget GUI dersom du orker det.

1. Deploy applikasjon til testmiljø med manuell workflow
2. Naviger til token-endepunkt i nettleser
3. Logg inn med dine trygdeetaten-credentials
4. Kopier feilende request (i chrome "copy as fetch")
   5. Lim inn i console
   6. Legg til ønsket scope og resource som url params
   7. Legg til content-type application/x-www-form-urlencoded
   8. Legg til `.then((d) => console.log(d.text()));` på slutten av fetch-request
9. Kjør modifisert request og les ut verdi av token