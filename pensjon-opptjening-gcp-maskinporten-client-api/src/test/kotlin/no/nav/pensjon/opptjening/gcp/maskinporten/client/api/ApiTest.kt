package no.nav.pensjon.opptjening.gcp.maskinporten.client.api

import no.nav.pensjon.opptjening.gcp.maskinporten.client.MaskinportenClient
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenParseJwkException
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@EnableMockOAuth2Server
class ApiTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var tokenIssuer: TestTokenIssuer

    @MockBean
    private lateinit var maskinportenClient: MaskinportenClient

    @Test
    fun `returnerer token fra maskinporten hvis alt går bra`() {
        whenever(maskinportenClient.tokenString).thenReturn("token")

        mvc.perform(
            get("/api/token")
                .header(HttpHeaders.AUTHORIZATION, tokenIssuer.bearerToken("azure"))
        ).andExpect(status().isOk)
            .andExpect(content().string("token"))
    }

    @Test
    fun `svarer med 401 dersom ukjent issuer`() {
        whenever(maskinportenClient.tokenString).thenReturn("token")

        mvc.perform(
            get("/api/token")
                .header(HttpHeaders.AUTHORIZATION, tokenIssuer.bearerToken("okta"))
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `svarer med 500 dersom ukjent feil`() {
        whenever(maskinportenClient.tokenString).thenThrow(MaskinportenParseJwkException())

        mvc.perform(
            get("/api/token")
                .header(HttpHeaders.AUTHORIZATION, tokenIssuer.bearerToken("azure"))
        ).andExpect(status().isInternalServerError)
    }
}