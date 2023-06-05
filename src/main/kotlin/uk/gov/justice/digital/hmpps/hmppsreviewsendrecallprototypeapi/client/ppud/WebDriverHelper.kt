package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxOptions

fun initialiseDriver(): WebDriver {
  // Needed to address failure to establish WebSocket in Chrome 111+
  // https://stackoverflow.com/questions/75718422/org-openqa-selenium-remote-http-connectionfailedexception-unable-to-establish-w
  //    val options = ChromeOptions()
  //    options.addArguments("--remote-allow-origins=*")
  //    driver = WebDriverManager.chromedriver().capabilities(options).create()

  val options = FirefoxOptions()
  val binary = System.getenv("HMPPS_RSR_FIREFOX_BINARY")
  if (!binary.isNullOrBlank()) {
    options.setBinary(binary)
  }

  val headless = System.getenv("HMPPS_RSR_HEADLESS").toBoolean()
  if (headless) {
    options.setHeadless(true)
  }

  return WebDriverManager.firefoxdriver().capabilities(options).create()
}
