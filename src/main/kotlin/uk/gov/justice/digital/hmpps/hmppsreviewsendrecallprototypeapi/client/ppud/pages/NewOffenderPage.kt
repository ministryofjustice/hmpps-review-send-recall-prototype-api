package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages

import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.NoAlertPresentException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.setDropdownOptionIfNotBlank
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.setInputTextIfNotBlank
import java.time.Duration

class NewOffenderPage(private val driver: WebDriver) {

  private val title = "New Offender"

  @FindBy(id = "content_cmdSave1")
  private val saveButton: WebElement? = null

  private val validationSummary: WebElement?
    get() = driver.findElements(By.id("content_valSummary")).firstOrNull()

  @FindBy(id = "content_txtNOMS_ID")
  private val nomsIdInput: WebElement? = null

  @FindBy(id = "content_txtFamilyName")
  private val familyNameInput: WebElement? = null

  @FindBy(id = "content_txtFirstName")
  private val firstNamesInput: WebElement? = null

  @FindBy(id = "igtxtcontent_dteDOB")
  private val dateOfBirthInput: WebElement? = null

  @FindBy(id = "content_ddlPrisonerCategory")
  private val prisonerCategoryDropdown: WebElement? = null

  @FindBy(id = "content_aceINDEX_OFFENCE_AutoCompleteTextBox")
  private val indexOffenceInput: WebElement? = null

  @FindBy(id = "content_aceINDEX_OFFENCE_AutoSelect")
  private val indexOffenceDropdown: WebElement? = null

  @FindBy(id = "content_ddlCustodyType")
  private val custodyTypeDropdown: WebElement? = null

  @FindBy(id = "content_ddlMappaLevel")
  private val mappaLevelDropdown: WebElement? = null

  @FindBy(id = "content_txtPrisonNumber")
  private val prisonNumberInput: WebElement? = null

  @FindBy(id = "content_ddlEthnicity")
  private val ethnicityDropdown: WebElement? = null

  @FindBy(id = "content_ddlIMMIGRATION_STATUS")
  private val immigrationStatusDropdown: WebElement? = null

  @FindBy(id = "content_ddlGender")
  private val genderDropdown: WebElement? = null

  @FindBy(id = "content_ddlStatus")
  private val statusDropdown: WebElement? = null

  @FindBy(id = "igtxtcontent_dtpDateOfSentence")
  private val dateOfSentenceInput: WebElement? = null

  @FindBy(id = "content_txtSENTENCING_COURT")
  private val sentencingCourtInput: WebElement? = null

  @FindBy(id = "content_ddliSENTENCED_UNDER")
  private val sentencedUnderDropdown: WebElement? = null

  init {
    PageFactory.initElements(driver, this)
  }

  fun verifyOn(): NewOffenderPage {
    WebDriverWait(driver, Duration.ofSeconds(2))
      .until(ExpectedConditions.titleIs(title))
    return this
  }

  suspend fun createOffender(newOffender: PpudClient.NewOffender) {
    // Complete these first as they trigger additional processing
    indexOffenceInput?.click()
    indexOffenceInput?.sendKeys(newOffender.indexOffence)
    Select(custodyTypeDropdown).selectByVisibleText("Determinate")

    // Complete standalone fields
    nomsIdInput?.sendKeys(newOffender.nomsId)
    familyNameInput?.sendKeys(newOffender.familyName)
    dismissCheckCapitalisationAlert()
    firstNamesInput?.sendKeys(newOffender.firstNames)
    dismissCheckCapitalisationAlert()
    dateOfBirthInput?.click()
    dateOfBirthInput?.sendKeys(newOffender.dateOfBirth)
    setDropdownOptionIfNotBlank(prisonerCategoryDropdown, "Not Applicable")
    prisonNumberInput?.sendKeys(newOffender.prisonNumber)
    setDropdownOptionIfNotBlank(ethnicityDropdown, newOffender.ethnicity)
    setDropdownOptionIfNotBlank(immigrationStatusDropdown, "Not Applicable")
    setDropdownOptionIfNotBlank(genderDropdown, newOffender.gender)
    setDropdownOptionIfNotBlank(statusDropdown, "Recalled [*]")
    dateOfSentenceInput?.click()
    setInputTextIfNotBlank(dateOfSentenceInput, newOffender.dateOfSentence)
    setInputTextIfNotBlank(sentencingCourtInput, newOffender.sentencingCourt)
    setDropdownOptionIfNotBlank(sentencedUnderDropdown, newOffender.sentencedUnder)

    // Complete fields that have been updated/refreshed.
    // This is a hacky solution but will do for now
    delay(2000)
    setDropdownOptionIfNotBlank(indexOffenceDropdown, newOffender.indexOffence)
    setDropdownOptionIfNotBlank(mappaLevelDropdown, newOffender.mappaLevel)

    saveButton?.click()
  }

  fun throwIfInvalid() {
    if (validationSummary?.text?.isNotBlank() == true) {
      throw Exception("Validation Failed.${System.lineSeparator()}${validationSummary?.text}")
    }
  }

  private fun dismissCheckCapitalisationAlert() {
    try {
      nomsIdInput?.click()
      val alert = driver.switchTo().alert()
      if (alert.text.contains("check that the capitalisation is correct")) {
        alert.accept()
      } else {
        throw Exception("Alert shown with the text '${alert.text}")
      }
    } catch (ex: NoAlertPresentException) {
      // No alert so we can proceed
    }
  }
}
