package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isNullOrBlank
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.MandatoryDocument
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.RiskOfSeriousHarmLevel
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.generateValidNewRecall
import java.io.File

class PpudClientRecallTest : PpudClientTest() {

  @Test
  fun `Given recall details when creating a recall then recall with ID is returned`() {
    val documents = listOf(
      PpudClient.DocumentForUpload(
        MandatoryDocument.PartA,
        File("src/test/test-data/Part A Document.docx").absolutePath,
      ),
      PpudClient.DocumentForUpload(
        MandatoryDocument.OaSys,
        File("src/test/test-data/OASys Document.pdf").absolutePath,
      ),
    )
    val newRecall = generateValidNewRecall(documents)

    val result = runBlocking {
      ppudClient.createRecall(newRecall)
    }
    assertThat(result.id, isNullOrBlank.not())
    assertThat(result.id.length, equalTo(76))
  }

  @Test
  fun `Given invalid recall details when creating a recall then exception is thrown with validation details`() {
    val newRecall = PpudClient.NewRecall(
      offenderId = "4F6666656E64657269643D313937363935G722H678",
      sentenceDate = "10/07/2014",
      releaseDate = "27/10/2014",
      probationArea = "",
      isInCustody = true,
      mappaLevel = "",
      decisionDateTime = "",
      receivedDateTime = "",
      recommendedToOwner = "",
      policeForce = "",
      isExtendedSentence = false,
      riskOfSeriousHarmLevel = RiskOfSeriousHarmLevel.High,
      documents = emptyList(),
    )
    assertThatThrownBy {
      runBlocking {
        ppudClient.createRecall(newRecall)
      }
    }.isInstanceOf(Exception::class.java)
      .hasMessageContaining("Validation Failed.")
      .hasMessageContaining("You must select a Probation Area.")
  }
}
