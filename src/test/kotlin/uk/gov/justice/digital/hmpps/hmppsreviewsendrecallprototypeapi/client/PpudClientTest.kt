package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isEmpty
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient

class PpudClientTest {

  private val ppudUrl = "https://uat.ppud.justice.gov.uk/"

  private val ppudClient: PpudClient = PpudClient(ppudUrl)

  @Test
  fun `Given an existing NOMS ID when searching for an offender then details are returned`() {
    val nomsId = "A8273DJ"
    val expectedFirstNames = "Daisy"
    val expectedFamilyName = "Owens"
    val results = searchForOffender(nomsId = nomsId)
    assertThat(results.size, equalTo(1))
    val actualOffender = results.first()
    assertThat(actualOffender.firstNames, equalTo(expectedFirstNames))
    assertThat(actualOffender.familyName, equalTo(expectedFamilyName))
  }

  @Test
  fun `Given an existing CRO Number when searching for an offender then details are returned`() {
    val croNumber = "86/107775E/0"
    val expectedFirstNames = "Steve"
    val expectedFamilyName = "Amber"
    val results = searchForOffender(croNumber = croNumber)
    assertThat(results.size, equalTo(1))
    val actualOffender = results.first()
    assertThat(actualOffender.firstNames, equalTo(expectedFirstNames))
    assertThat(actualOffender.familyName, equalTo(expectedFamilyName))
  }

  @Test
  fun `Given an existing family name and date of birth when searching for an offender then details are returned`() {
    val expectedDateOfBirth = "30/11/1984"
    val expectedFirstNames = "Charlotte"
    val expectedFamilyName = "Lilac"
    val results = searchForOffender(familyName = expectedFamilyName, dateOfBirth = expectedDateOfBirth)
    assertThat(results.size, equalTo(1))
    val actualOffender = results.first()
    assertThat(actualOffender.firstNames, equalTo(expectedFirstNames))
    assertThat(actualOffender.familyName, equalTo(expectedFamilyName))
    assertThat(actualOffender.dateOfBirth, equalTo(expectedDateOfBirth))
  }

  @Test
  fun `Given non existent details when searching for an offender then empty list is returned`() {
    val results = searchForOffender(
      "MissingCRO",
      "MissingNOMS",
      "MissingFamilyName",
      "MissingDateOfBirth",
    )
    assertThat(results, isEmpty)
  }

  private fun searchForOffender(
    croNumber: String = "",
    nomsId: String = "",
    familyName: String = "",
    dateOfBirth: String = "",
  ): List<PpudClient.Offender> {
    return ppudClient.searchForOffender(croNumber, nomsId, familyName, dateOfBirth)
  }
}
