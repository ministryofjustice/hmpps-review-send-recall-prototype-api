package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class PpudClientTest(val sleepDurationInMilliseconds: Long = 0) {

  protected val ppudUrl = "https://uat.ppud.justice.gov.uk/"

  protected lateinit var ppudClient: PpudClient

  @BeforeEach
  fun before() {
    ppudClient = PpudClient(ppudUrl, sleepDurationInMilliseconds)
  }

  @AfterEach
  fun after() {
    ppudClient.quit()
  }
}
