package qx.aieduserver

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.restdocs.payload.PayloadDocumentation.*


@SpringBootTest(properties = ["spring.main.web-application-type=reactive"])
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
class AieduserverApplicationTests {

    @Test
    fun exampleTest(@Autowired webClient: WebTestClient) {
        webClient.get().uri("/api/exercises/{page}/{size}/{sorts}/{filters}", 0, 10, "none", "none").exchange().expectStatus().isOk
                .expectBody()
                .consumeWith(document("exercises",


                        *(CommonDocs.commonPagingDocs().toTypedArray())
                ))
    }
}
