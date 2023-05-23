package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient

class OffenderPage(private val driver: WebDriver) {

  @FindBy(id = "cntDetails_txtCRO_PNC")
  private val croNumberInput: WebElement? = null

  @FindBy(id = "cntDetails_txtFIRST_NAMES")
  private val firstNamesInput: WebElement? = null

  @FindBy(id = "cntDetails_txtFAMILY_NAME")
  private val familyNameInput: WebElement? = null

  @FindBy(id = "igtxtcntDetails_dteDOB")
  private val dateOfBirthInput: WebElement? = null

  init {
    PageFactory.initElements(driver, this)
  }

  fun extractOffenderDetails(): PpudClient.Offender {
    return PpudClient.Offender(
      croNumber = croNumberInput?.getAttribute("value")?.trim() ?: "",
      nomsId = "",
      firstNames = firstNamesInput?.getAttribute("value")?.trim() ?: "",
      familyName = familyNameInput?.getAttribute("value")?.trim() ?: "",
      dateOfBirth = dateOfBirthInput?.getAttribute("value")?.trim() ?: "",
    )
  }
}
