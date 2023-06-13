package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages

import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.enterInputTextIfNotBlank
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.selectCheckboxValue
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.selectDropdownOptionIfNotBlank
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NewRecallPage(private val driver: WebDriver) {

  @FindBy(id = "cntDetails_PageFooter1_cmdSave")
  private val saveButton: WebElement? = null

  @FindBy(id = "cntDetails_ddliRECALL_TYPE")
  private val recallTypeDropdown: WebElement? = null

  @FindBy(id = "cntDetails_ddliPROBATION_AREA")
  private val probationAreaDropdown: WebElement? = null

  @FindBy(id = "cntDetails_chkUAL_FLAG")
  private val ualCheckbox: WebElement? = null

  @FindBy(id = "cntDetails_ddliMAPPA_LEVEL")
  private val mappaLevelDropdown: WebElement? = null

  @FindBy(id = "igtxtcntDetails_dteUAL_CHECK")
  private val nextUalCheckInput: WebElement? = null

  @FindBy(id = "igtxtcntDetails_dtePB_DECISION_AFTER_BREACH_ACTUAL")
  private val decisionFollowingBreachDateInput: WebElement? = null

  @FindBy(id = "igtxtcntDetails_dteREPORT_RECD_BY_UNIT_ACTUAL")
  private val reportReceivedDateInput: WebElement? = null

  @FindBy(id = "igtxtcntDetails_dteRECOMMEND_TO_EO_ACTUAL")
  private val recommendedToDateInput: WebElement? = null

  @FindBy(id = "cntDetails_aceiRECOMMEND_TO_EO_CWORKER_AutoCompleteTextBox")
  private val recommendedToOwnerInput: WebElement? = null

  @FindBy(id = "cntDetails_aceiRECOMMEND_TO_EO_CWORKER_AutoSelect")
  private val recommendedToOwnerDropdown: WebElement? = null

  private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

  private val validationSummary: WebElement?
    get() = driver.findElements(By.id("cntDetails_ValidationSummary1")).firstOrNull()

  init {
    PageFactory.initElements(driver, this)
  }

  suspend fun createRecall(newRecall: PpudClient.NewRecall) {
    // Complete these first as they trigger additional processing
    // Autocomplete box doesn't work with brackets
    val owner = newRecall.recommendedToOwner.takeWhile { (it == '(').not() }
    recommendedToOwnerInput?.click()
    enterInputTextIfNotBlank(recommendedToOwnerInput, owner)

    // Complete standalone fields
    selectDropdownOptionIfNotBlank(recallTypeDropdown, "To be determined")
    selectDropdownOptionIfNotBlank(
      probationAreaDropdown,
      newRecall.probationArea,
    ) // Supposed to be populated automatically
    selectCheckboxValue(ualCheckbox, newRecall.isInCustody)
    selectDropdownOptionIfNotBlank(mappaLevelDropdown, newRecall.mappaLevel) // Supposed to be populated automatically
    if (newRecall.isInCustody.not()) {
      val nextUalCheckDate = LocalDateTime.now().plusMonths(6).format(dateFormatter)
      enterInputTextIfNotBlank(nextUalCheckInput, nextUalCheckDate)
    }
    enterInputTextIfNotBlank(decisionFollowingBreachDateInput, newRecall.decisionDateTime)
    enterInputTextIfNotBlank(reportReceivedDateInput, newRecall.receivedDateTime)
    val recommendedToDate = LocalDateTime.now().format(dateTimeFormatter)
    enterInputTextIfNotBlank(recommendedToDateInput, recommendedToDate)

    // Complete fields that have been updated/refreshed.
    // This is a hacky solution but will do for now
    delay(2000)
    selectDropdownOptionIfNotBlank(recommendedToOwnerDropdown, newRecall.recommendedToOwner)

    saveButton?.click()
  }

  fun throwIfInvalid() {
    if (validationSummary?.text?.isNotBlank() == true) {
      throw Exception("Validation Failed.${System.lineSeparator()}${validationSummary?.text}")
    }
  }
}
