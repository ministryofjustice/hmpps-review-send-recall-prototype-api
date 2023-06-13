package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient

class NewRecallPage(private val driver: WebDriver) {

  @FindBy(id = "cntDetails_PageFooter1_cmdSave")
  private val saveButton: WebElement? = null

  private val validationSummary: WebElement?
    get() = driver.findElements(By.id("cntDetails_ValidationSummary1")).firstOrNull()

  init {
    PageFactory.initElements(driver, this)
  }

  fun createRecall(newRecall: PpudClient.NewRecall) {
    saveButton?.click()
  }

  fun throwIfInvalid() {
    if (validationSummary?.text?.isNotBlank() == true) {
      throw Exception("Validation Failed.${System.lineSeparator()}${validationSummary?.text}")
    }
  }
}
