package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isNullOrBlank
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import java.util.*

class PpudClientCreateTest : PpudClientTest() {

  @Test
  fun `Given offender details when creating an offender then offender with ID is returned`() {
    val newOffender = PpudClient.NewOffender(
      croNumber = "CRO-${UUID.randomUUID()}",
      nomsId = "NOMS-${UUID.randomUUID()}",
      firstNames = "John",
      familyName = "Teal-${UUID.randomUUID().toString().uppercase()}",
      dateOfBirth = "12/12/1985",
      indexOffence = "OTHER DRUGS OFFENCES",
      mappaLevel = "Level 3 – MAPPP", // This is a specific type of hyphen, not "minus"
      prisonNumber = "PRISON-NUMBER-${UUID.randomUUID()}",
      ethnicity = "White – Other", // This is a specific type of hyphen, not "minus"
      gender = "M",
      dateOfSentence = "01/01/2020",
      sentencingCourt = "Sheffield",
      sentencedUnder = "CJA 1991",
    )
    val result = runBlocking {
      ppudClient.createOffender(newOffender)
    }
    assertThat(result.id, isNullOrBlank.not())
    assertThat(result.id.length, equalTo(42))
    assertThat(result.familyName, equalTo(newOffender.familyName))
  }

  @Test
  fun `Given invalid offender details when creating an offender then exception is thrown with validation details`() {
    val newOffender = PpudClient.NewOffender(
      croNumber = "",
      nomsId = "",
      firstNames = "",
      familyName = "",
      dateOfBirth = "",
      indexOffence = "",
      mappaLevel = "",
      prisonNumber = "",
      ethnicity = "",
      gender = "",
      dateOfSentence = "",
    )
    assertThatThrownBy {
      runBlocking {
        ppudClient.createOffender(newOffender)
      }
    }.isInstanceOf(Exception::class.java)
      .hasMessageContaining("Validation Failed.")
      .hasMessageContaining("You must enter a first name.")
  }
}
