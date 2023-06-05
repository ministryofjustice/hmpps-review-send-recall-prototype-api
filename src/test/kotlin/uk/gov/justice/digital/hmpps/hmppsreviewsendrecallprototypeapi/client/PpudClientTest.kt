package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client

import com.natpryce.hamkrest.allElements
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.caseInsensitive
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.greaterThan
import com.natpryce.hamkrest.isEmpty
import com.natpryce.hamkrest.isNullOrBlank
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.initialiseDriver
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PpudClientTest {

  private val ppudUrl = "https://uat.ppud.justice.gov.uk/"

  private lateinit var ppudClient: PpudClient

  @BeforeEach
  fun beforeEach() {
    ppudClient = PpudClient()
    ppudClient.driver = initialiseDriver()
    ppudClient.ppudUrl = ppudUrl
  }

  @AfterEach
  fun afterEach() {
    ppudClient.driver.close()
  }

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
  fun `Given search criteria for existing offender when searching for an offender then returned offender includes unique identifier`() {
    val nomsId = "A8273DJ"
    val expectedId = "4F6666656E64657269643D313230323533G709H667"
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

  @Test
  fun `Given offender details when creating an offender then offender with ID is returned`() {
    val newOffender = PpudClient.NewOffender(
      croNumber = "CRO--${UUID.randomUUID()}",
      nomsId = "NOMS-${UUID.randomUUID()}",
      firstNames = "John",
      familyName = "Teal-${UUID.randomUUID().toString().uppercase()}",
      dateOfBirth = "12/12/1985",
      indexOffence = "OTHER DRUGS OFFENCES",
      mappaLevel = "Level 2 – Multi-Agency Management", // This is a specific type of hyphen, not "minus"
      prisonNumber = "PRISON-NUMBER-${UUID.randomUUID()}",
      ethnicity = "White – Other", // This is a specific type of hyphen, not "minus"
      gender = "M",
      dateOfSentence = "01/01/2020",
      sentencingCourt = "Sheffield",
      sentencedUnder = "CJA 1991",
    )
    val result = ppudClient.createOffender(newOffender)

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
      ppudClient.createOffender(newOffender)
    }.isInstanceOf(Exception::class.java)
      .hasMessageContaining("Validation Failed.")
      .hasMessageContaining("You must enter a first name.")
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
