package no.nav.pensjon.opptjening.gcp.maskinporten.client.api

import no.nav.pensjon.opptjening.gcp.maskinporten.client.MaskinportenClient
import no.nav.pensjon.opptjening.gcp.maskinporten.client.exceptions.MaskinportenParseJwkException
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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
        whenever(maskinportenClient.token(any(), anyOrNull())).thenReturn("token")

        mvc.perform(
            get("/api/token")
                .param("scope", "scope")
                .param("resource", "resource")
                .header(HttpHeaders.AUTHORIZATION, tokenIssuer.bearerToken("azure"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(status().isOk)
            .andExpect(content().string("token"))

        verify(maskinportenClient).token(eq("scope"), eq("resource"))
    }

    @Test
    fun `returnerer token fra maskinporten hvis alt går bra, optional parameter`() {
        whenever(maskinportenClient.token(any(), anyOrNull())).thenReturn("token")

        mvc.perform(
            get("/api/token")
                .param("scope", "scope")
                .header(HttpHeaders.AUTHORIZATION, tokenIssuer.bearerToken("azure"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(status().isOk)
            .andExpect(content().string("token"))

        verify(maskinportenClient).token(eq("scope"), eq(null))
    }

    @Test
    fun `returnerer 400 dersom manglende parametere`() {
        whenever(maskinportenClient.token(any(), anyOrNull())).thenReturn("token")

        mvc.perform(
            get("/api/token")
                .header(HttpHeaders.AUTHORIZATION, tokenIssuer.bearerToken("azure"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `svarer med 401 dersom ukjent issuer`() {
        whenever(maskinportenClient.token(any(), anyOrNull())).thenReturn("token")

        mvc.perform(
            get("/api/token")
                .param("scope", "scope")
                .header(HttpHeaders.AUTHORIZATION, tokenIssuer.bearerToken("okta"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `svarer med 500 dersom ukjent feil`() {
        whenever(maskinportenClient.token(any(), anyOrNull())).thenThrow(MaskinportenParseJwkException())

        mvc.perform(
            get("/api/token")
                .param("scope", "scope")
                .header(HttpHeaders.AUTHORIZATION, tokenIssuer.bearerToken("azure"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(status().isInternalServerError)
    }

    @Test
    fun `actuator kan kalles uten token`() {
        mvc.perform(
            get("/actuator/health")
        ).andExpect(status().isOk)
    }
}