package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.LoginPage
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.OffenderPage
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages.SearchPage

class PpudClient(
  private val ppudUrl: String,
) {

  private val driver: WebDriver

  init {

    // Needed to address failure to establish WebSocket in Chrome 111+
    // https://stackoverflow.com/questions/75718422/org-openqa-selenium-remote-http-connectionfailedexception-unable-to-establish-w
    val options = ChromeOptions()
    options.addArguments("--remote-allow-origins=*")
//    options.setHeadless(true)
    driver = WebDriverManager.chromedriver().capabilities(options).create()
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  fun searchForOffender(croNumber: String, nomsId: String, familyName: String, dateOfBirth: String): List<Offender> {
    try {
      driver.get(ppudUrl)

      log.info("Searching for CRO Number: '$croNumber' NomsId: '$nomsId' Family Name: '$familyName' Date of Birth: '$dateOfBirth'")

      logIn()

      val resultLinks = searchUntilFound(croNumber, nomsId, familyName, dateOfBirth)
      // Thread.sleep(3000)

      return resultLinks.map { extractOffenderDetail(it) }
    } finally {
      driver.close()
    }
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
    if (searchPage.searchResultsCount() == 0) {
      searchByNomsIdIfPresent(searchPage, nomsId)

      if (searchPage.searchResultsCount() == 0) {
        searchByPersonalDetailsIfPresent(searchPage, familyName, dateOfBirth)
      }
    }

    return searchPage.searchResultsLinks()
  }

  private fun extractOffenderDetail(url: String): Offender {
    driver.get(url)
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

  data class Offender(
    val id: String,
    val croNumber: String,
    val nomsId: String,
    val firstNames: String,
    val familyName: String,
    val dateOfBirth: String,
  )
}
