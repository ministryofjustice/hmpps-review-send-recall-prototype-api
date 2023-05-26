package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.LoginPage
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.NewOffenderPage
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.OffenderPage
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.SearchPage

@Component
class PpudClient {

  private lateinit var driver: WebDriver

  var ppudUrl: String = ""

  var sleepDurationInMilliseconds: Long = 0

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  fun searchForOffender(croNumber: String, nomsId: String, familyName: String, dateOfBirth: String): List<Offender> {
    log.info("Searching for CRO Number: '$croNumber' NomsId: '$nomsId' Family Name: '$familyName' Date of Birth: '$dateOfBirth'")

    try {
      initialiseDriver()

      driver.get(ppudUrl)
      sleepIfRequired()

      logIn()

      val resultLinks = searchUntilFound(croNumber, nomsId, familyName, dateOfBirth)
      sleepIfRequired()

      return resultLinks.map { extractOffenderDetail(it) }
    } finally {
      driver.close()
    }
  }

  fun createOffender(newOffender: NewOffender): Offender {
    log.info("Creating new offender $newOffender")

    try {
      initialiseDriver()

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
    } finally {
      driver.close()
    }
  }

  private fun initialiseDriver() {
    // Needed to address failure to establish WebSocket in Chrome 111+
    // https://stackoverflow.com/questions/75718422/org-openqa-selenium-remote-http-connectionfailedexception-unable-to-establish-w
    val options = ChromeOptions()
    options.addArguments("--remote-allow-origins=*")
//    options.setHeadless(true)
    driver = WebDriverManager.chromedriver().capabilities(options).create()
  }

  private fun logIn() {
    val loginPage = LoginPage(driver).verifyOn()
    val userName = System.getenv("HMPPS_PPUD_UAT_USERNAME")
      ?: throw MissingEnvironmentVariableException("Username environment variable not set")
    val password = System.getenv("HMPPS_PPUD_UAT_PASSWORD")
      ?: throw MissingEnvironmentVariableException("Password environment variable not set")

    loginPage.login(userName, password)
  }

  private fun searchUntilFound(
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

  private fun extractOffenderDetail(url: String): Offender {
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

  private fun sleepIfRequired() {
    Thread.sleep(sleepDurationInMilliseconds)
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
