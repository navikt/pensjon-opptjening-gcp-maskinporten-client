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

1. Deploy applikasjon til testmiljø med manuell workflow (hvis nødvendig)
2. Naviger til token-endepunkt https://pensjon-opptjening-gcp-maskinporten-client-api.intern.dev.nav.no/api/token i
   nettleser
3. Logg inn med dine trygdeetaten-credentials
4. Kopier feilende request (i chrome "copy as fetch")
    1. Lim inn i developer console
    2. Modifiser url ved å legge til scope og resource (hvis appen man skal kalle krever audience restricted token) som
       url params
    3. Legg til content-type application/x-www-form-urlencoded
    4. Legg til `.then((d) => console.log(d.text()));` på slutten av fetch-request
    5. Du står igjen med noe som ligner på:

```javascript
fetch("https://pensjon-opptjening-gcp-maskinporten-client-api.intern.dev.nav.no/api/token?scope=nav:pensjonopptjening:ekstern.afp.beholdningsgrunnlag.simuler.read&resource=https://pensjonopptjening-gw.ekstern.dev.nav.no/opptjening/afp/beholdningsgrunnlag", {
  "headers": {
    ... mye annet stuff
    "content-type":"application/x-www-form-urlencoded"  
  },
  ... mye annet stuff
}).then((d) => console.log(d.text()));
```

5. Kjør modifisert request og les ut verdi av token fra returnert promise.
