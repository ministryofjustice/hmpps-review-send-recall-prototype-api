package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud

import kotlinx.coroutines.delay
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.LoginPage
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.NewOffenderPage
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.OffenderPage
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.SearchPage

class PpudClient(private val ppudUrl: String, private val sleepDurationInMilliseconds: Long) {

  private val driver: WebDriver = initialiseDriver()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  suspend fun searchForOffender(croNumber: String, nomsId: String, familyName: String, dateOfBirth: String): List<Offender> {
    log.info("Searching for CRO Number: '$croNumber' NomsId: '$nomsId' Family Name: '$familyName' Date of Birth: '$dateOfBirth'")

    try {
      driver.get(ppudUrl)
      sleepIfRequired()

      logIn()

      val resultLinks = searchUntilFound(croNumber, nomsId, familyName, dateOfBirth)
      sleepIfRequired()

      return resultLinks.map { extractOffenderDetail(it) }
    } catch (e: Exception) {
      log.error("Exception searching for offender.", e)
      throw e
    }
  }

  suspend fun createOffender(newOffender: NewOffender): Offender {
    log.info("Creating new offender $newOffender")

    try {
      driver.get(ppudUrl)
      sleepIfRequired()

      logIn()

      val searchPage = SearchPage(driver).verifyOn()
      searchPage.gotoNewOffenderPage()
      sleepIfRequired()
      val newOffenderPage = NewOffenderPage(driver).verifyOn()
      newOffenderPage.createOffender(newOffender)

      newOffenderPage.throwIfInvalid()

      val offenderPage = OffenderPage(driver)
      sleepIfRequired()
      return offenderPage.extractOffenderDetails()
    } catch (e: Exception) {
      log.error("Exception creating new offender.", e)
      throw e
    }
  }

  fun quit() {
    driver.quit()
  }

  private fun logIn() {
    val loginPage = LoginPage(driver).verifyOn()
    val userName = System.getenv("HMPPS_PPUD_UAT_USERNAME")
      ?: throw Exception("Username environment variable not set")
    val password = System.getenv("HMPPS_PPUD_UAT_PASSWORD")
      ?: throw Exception("Password environment variable not set")

    loginPage.login(userName, password)
  }

  private suspend fun searchUntilFound(
    croNumber: String,
    nomsId: String,
    familyName: String,
    dateOfBirth: String,
  ): List<String> {
    val searchPage = SearchPage(driver).verifyOn()

    searchByCroNumberIfPresent(searchPage, croNumber)
    sleepIfRequired()
    if (searchPage.searchResultsCount() == 0) {
      searchByNomsIdIfPresent(searchPage, nomsId)
      sleepIfRequired()

      if (searchPage.searchResultsCount() == 0) {
        searchByPersonalDetailsIfPresent(searchPage, familyName, dateOfBirth)
        sleepIfRequired()
      }
    }

    return searchPage.searchResultsLinks()
  }

  private suspend fun extractOffenderDetail(url: String): Offender {
    driver.get(url)
    sleepIfRequired()
    val offenderPage = OffenderPage(driver)
    return offenderPage.extractOffenderDetails()
  }

  private fun searchByCroNumberIfPresent(searchPage: SearchPage, croNumber: String) {
    if (croNumber.isNotBlank()) {
      searchPage.clearFields()
      searchPage.searchByCroNumber(croNumber)
    }
  }

  private fun searchByNomsIdIfPresent(searchPage: SearchPage, nomsId: String) {
    if (nomsId.isNotBlank()) {
      searchPage.clearFields()
      searchPage.searchByNomsId(nomsId)
    }
  }

  private fun searchByPersonalDetailsIfPresent(searchPage: SearchPage, familyName: String, dateOfBirth: String) {
    if (familyName.isNotBlank() || dateOfBirth.isNotBlank()) {
      searchPage.clearFields()
      searchPage.searchByPersonalDetails(familyName, dateOfBirth)
    }
  }

  private suspend fun sleepIfRequired() {
    delay(sleepDurationInMilliseconds)
  }

  data class Offender(
    val id: String,
    val croNumber: String,
    val nomsId: String,
    val firstNames: String,
    val familyName: String,
    val dateOfBirth: String,
  )

  data class NewOffender(
    val croNumber: String,
    val nomsId: String?,
    val firstNames: String,
    val familyName: String,
    val dateOfBirth: String,
    val indexOffence: String,
    val mappaLevel: String,
    val prisonNumber: String,
    val ethnicity: String,
    val gender: String,
    val dateOfSentence: String,
    val sentencingCourt: String? = null,
    val sentencedUnder: String? = null,
  )
}
