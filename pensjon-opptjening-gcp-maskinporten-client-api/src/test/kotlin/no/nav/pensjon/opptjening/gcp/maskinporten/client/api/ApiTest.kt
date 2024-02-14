package no.nav.pensjon.opptjening.gcp.maskinporten.client.api

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@EnableMockOAuth2Server
class ApiTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var tokenIssuer: TestTokenIssuer

    @Test
    fun `test`() {
        mvc.perform(
            get("/api/token")
                .header(HttpHeaders.AUTHORIZATION, tokenIssuer.bearerToken("azure"))
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }
}