package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud

import io.github.bonigarcia.wdm.WebDriverManager
import kotlinx.coroutines.delay
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.support.ui.Select

var WebDriverSleepDurationInMilliseconds: Long = 0

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

suspend fun sleepIfRequired() {
  delay(WebDriverSleepDurationInMilliseconds)
}

fun enterInputTextIfNotBlank(input: WebElement?, text: String?) {
  if (text?.isNotBlank() == true && input != null) {
    input.sendKeys(text)
  }
}

fun selectCheckboxValue(checkbox: WebElement?, value: Boolean) {
  if ((checkbox?.isSelected == true && value.not())
    || (checkbox?.isSelected == false && value)
  ) {
    checkbox.click()
  }
}

fun selectDropdownOptionIfNotBlank(dropdown: WebElement?, option: String?) {
  if (option?.isNotBlank() == true && dropdown != null) {
    Select(dropdown).selectByVisibleText(option)
  }
}

