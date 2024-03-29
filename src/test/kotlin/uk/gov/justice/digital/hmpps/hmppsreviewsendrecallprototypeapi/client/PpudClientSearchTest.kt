package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient

class PpudClientSearchTest : PpudClientTest() {

  @Test
  fun `Given an existing NOMS ID when searching for an offender then details are returned`() {
    ppudClient = PpudClient(ppudUrl, sleepDurationInMilliseconds = 0)
    val nomsId = "A1234AB"
    val expectedFirstNames = "James"
    val expectedFamilyName = "Bond"
    val results = searchForOffender(nomsId = nomsId)
    assertThat(results.size, equalTo(1))
    val actualOffender = results.first()
    assertThat(actualOffender.firstNames, equalTo(expectedFirstNames))
    assertThat(actualOffender.familyName, equalTo(expectedFamilyName))
  }

  @Test
  fun `Given an existing CRO Number when searching for an offender then details are returned`() {
    val croNumber = "08/5159AB"
    val expectedFirstNames = "Percival"
    val expectedFamilyName = "Peabody"
    val results = searchForOffender(croNumber = croNumber)
    assertThat(results.size, equalTo(1))
    val actualOffender = results.first()
    assertThat(actualOffender.firstNames, equalTo(expectedFirstNames))
    assertThat(actualOffender.familyName, equalTo(expectedFamilyName))
  }

  @Test
  fun `Given an existing family name and date of birth when searching for an offender then details are returned`() {
    val expectedDateOfBirth = "14/08/1952"
    val expectedFirstNames = "Graham"
    val expectedFamilyName = "Green"
    val results = searchForOffender(familyName = expectedFamilyName, dateOfBirth = expectedDateOfBirth)
    assertThat(results.size, equalTo(1))
    val actualOffender = results.first()
    assertThat(actualOffender.firstNames, equalTo(expectedFirstNames))
    assertThat(actualOffender.familyName, equalTo(expectedFamilyName))
    assertThat(actualOffender.dateOfBirth, equalTo(expectedDateOfBirth))
  }

  @Test
  fun `Given search criteria for existing offender when searching for an offender then returned offender includes unique identifier`() {
    val nomsId = "A1234AB"
    val expectedId = "4F6666656E64657269643D313633333636G717H671"
    val results = searchForOffender(nomsId = nomsId)
    assertThat(results.size, equalTo(1))
    val actualOffender = results.first()
    assertThat(actualOffender.id, equalTo(expectedId))
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

  @Test
  fun `Given non unique details when searching for an offender then all matches are returned`() {
    val nomsId = "AB"
    val results = searchForOffender(nomsId = nomsId)
    assertThat(results.size, greaterThan(1))
    val nomsIds = results.map { it.nomsId }
    assertThat(nomsIds, allElements(containsSubstring(nomsId).caseInsensitive()))
  }

  private fun searchForOffender(
    croNumber: String = "",
    nomsId: String = "",
    familyName: String = "",
    dateOfBirth: String = "",
  ): List<PpudClient.Offender> {
    return runBlocking {
      ppudClient.searchForOffender(croNumber, nomsId, familyName, dateOfBirth)
    }
  }
}
