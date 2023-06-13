package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.integration.ppud

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.generateValidNewRecall
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.integration.IntegrationTestBase
import java.time.Duration

private const val timeoutInSeconds = 30L

class PpudIntegrationTest : IntegrationTestBase() {

  @Test
  fun `Search returns result`() {
    runBlocking {
      testSearchRequest(0)
    }
  }

  @Test
  fun `Multiple concurrent searches return result`() {
    // This test doesn't seem to result in concurrent requests actually
    // getting into the controller
    runBlocking {
      for (index: Int in 1..5) {
        launch { // launch a new coroutine and continue
          println("** Test call $index initiating")
          testSearchRequest(index)
        }
      }
    }
  }

  @Test
  fun `Create recall returns success`() {
    webTestClient
      .mutate().responseTimeout(Duration.ofSeconds(timeoutInSeconds)).build()
      .post().uri("/recall")
      .body(BodyInserters.fromValue(generateValidNewRecall()))
      .exchange()
      .expectStatus().is2xxSuccessful
  }

  private suspend fun testSearchRequest(index: Int) {
    coroutineScope {
      launch {
        println("** Test call $index calling")
        webTestClient
          .mutate().responseTimeout(Duration.ofSeconds(timeoutInSeconds)).build()
          .get().uri("/search?croNumber=08/5159AB&sleepDuration=100")
          .exchange()
          .expectStatus().isOk
          .expectBody().jsonPath("[0].croNumber").isEqualTo("08/5159AB")
      }
    }
    println("** Test call $index done")
  }
}
