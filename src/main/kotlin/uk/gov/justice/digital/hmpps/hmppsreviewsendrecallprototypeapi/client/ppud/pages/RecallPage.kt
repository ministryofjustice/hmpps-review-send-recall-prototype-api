package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages

import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.MandatoryDocument
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.enterInputTextIfNotBlank
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.selectCheckboxValue
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.selectDropdownOptionIfNotBlank
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.sleepIfRequired
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecallPage(private val driver: WebDriver) {

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

  @FindBy(id = "cntDetails_aceiREVOCATION_ISSUED_BY_AutoCompleteTextBox")
  private val revocationIssuedByOwnerInput: WebElement? = null

  @FindBy(id = "cntDetails_aceiREVOCATION_ISSUED_BY_AutoSelect")
  private val revocationIssuedByOwnerDropdown: WebElement? = null

  @FindBy(id = "cntDetails_ddliPOLICE_FORCE")
  private val policeForceDropdown: WebElement? = null

  @FindBy(id = "cntDetails_ddliRTC_NOTIF_METHOD")
  private val returnToCustodyNotificationMethodDropdown: WebElement? = null

  @FindBy(id = "cntDetails_ddliMAND_DOCS_RECEIVED")
  private val mandatoryDocumentsReceivedDropdown: WebElement? = null

  @FindBy(id = "cntDetails_chkMAND_DOC_PART_A")
  private val missingPartACheckbox: WebElement? = null

  @FindBy(id = "cntDetails_chkMAND_DOC_OASYS")
  private val missingOaSysCheckbox: WebElement? = null

  @FindBy(id = "cntDetails_chkMAND_DOC_PRE_SENTENCE_REP")
  private val missingPreSentenceReportCheckbox: WebElement? = null

  @FindBy(id = "cntDetails_chkMAND_DOC_PREV_CONV")
  private val missingPreviousConvictionsCheckbox: WebElement? = null

  @FindBy(id = "cntDetails_chkMAND_DOC_LICENCE")
  private val missingLicenceCheckbox: WebElement? = null

  @FindBy(id = "cntDetails_chkMAND_DOC_CHARGE_SHEET")
  private val missingChargeSheetCheckbox: WebElement? = null

  private val addMinuteButton: WebElement?
    get() = driver.findElements(By.id("cntDetails_PageFooter1_Minutes1_btnReplyTop")).firstOrNull()

  private val minuteEditor: WebElement
    get() = driver.findElement(By.id("cntDetails_PageFooter1_Minutes1_MinutesTextRich_tw"))

  private val saveMinuteButton: WebElement
    get() = driver.findElement(By.id("cntDetails_PageFooter1_Minutes1_btnSave"))

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
    val recommendedToOwnerSearchable = newRecall.recommendedToOwner.takeWhile { (it == '(').not() }
    recommendedToOwnerInput?.click()
    enterInputTextIfNotBlank(recommendedToOwnerInput, recommendedToOwnerSearchable)
    val revocationIssuedByOwner = "EO Recall(Recall Team)"
    val revocationIssuedByOwnerSearchable = revocationIssuedByOwner.takeWhile { (it == '(').not() }
    revocationIssuedByOwnerInput?.click()
    enterInputTextIfNotBlank(revocationIssuedByOwnerInput, revocationIssuedByOwnerSearchable)

    // Complete standalone fields
    selectDropdownOptionIfNotBlank(recallTypeDropdown, "To be determined")
    selectDropdownOptionIfNotBlank(
      probationAreaDropdown,
      newRecall.probationArea,
    ) // Probation area supposed to be populated automatically
    selectCheckboxValue(ualCheckbox, newRecall.isInCustody)
    selectDropdownOptionIfNotBlank(
      mappaLevelDropdown,
      newRecall.mappaLevel,
    ) // Mappa level supposed to be populated automatically
    if (newRecall.isInCustody.not()) {
      val nextUalCheckDate = LocalDateTime.now().plusMonths(6).format(dateFormatter)
      enterInputTextIfNotBlank(nextUalCheckInput, nextUalCheckDate)
    }
    enterInputTextIfNotBlank(decisionFollowingBreachDateInput, newRecall.decisionDateTime)
    enterInputTextIfNotBlank(reportReceivedDateInput, newRecall.receivedDateTime)
    val recommendedToDate = LocalDateTime.now().format(dateTimeFormatter)
    enterInputTextIfNotBlank(recommendedToDateInput, recommendedToDate)
    selectDropdownOptionIfNotBlank(policeForceDropdown, newRecall.policeForce)
    if (newRecall.isInCustody) {
      selectDropdownOptionIfNotBlank(returnToCustodyNotificationMethodDropdown, "Already in custody")
    }
    selectMandatoryDocumentsReceivedDropdown(newRecall.missingDocuments)
    checkMissingMandatoryDocuments(newRecall.missingDocuments)

    // Complete fields that have been updated/refreshed.
    // This is a hacky solution but will do for now
    delay(2000)
    selectDropdownOptionIfNotBlank(recommendedToOwnerDropdown, newRecall.recommendedToOwner)
    selectDropdownOptionIfNotBlank(revocationIssuedByOwnerDropdown, revocationIssuedByOwner)

    saveButton?.click()
  }

  suspend fun addMinute(newRecall: PpudClient.NewRecall) {
    delay(1000) // Wait for save to be processed. HACK: Can we use selenium waits?
    addMinuteButton?.click()
    minuteEditor.click()
    minuteEditor.sendKeys(generateMinuteText(newRecall))
    sleepIfRequired()
    saveMinuteButton.click()
  }

  private fun checkMissingMandatoryDocuments(missingDocuments: Collection<MandatoryDocument>) {
    selectCheckboxValue(missingPartACheckbox, missingDocuments.contains(MandatoryDocument.PartA))
    selectCheckboxValue(missingOaSysCheckbox, missingDocuments.contains(MandatoryDocument.OaSys))
    selectCheckboxValue(
      missingPreSentenceReportCheckbox,
      missingDocuments.contains(MandatoryDocument.PreSentenceReport),
    )
    selectCheckboxValue(
      missingPreviousConvictionsCheckbox,
      missingDocuments.contains(MandatoryDocument.PreviousConvictions),
    )
    selectCheckboxValue(missingLicenceCheckbox, missingDocuments.contains(MandatoryDocument.Licence))
    selectCheckboxValue(missingChargeSheetCheckbox, missingDocuments.contains(MandatoryDocument.ChargeSheet))
  }

  private fun selectMandatoryDocumentsReceivedDropdown(missingDocuments: Collection<MandatoryDocument>) {
    val option = if (missingDocuments.any()) {
      "No"
    } else {
      "Yes"
    }
    selectDropdownOptionIfNotBlank(mandatoryDocumentsReceivedDropdown, option)
  }

  fun throwIfInvalid() {
    if (validationSummary?.text?.isNotBlank() == true) {
      throw Exception("Validation Failed.${System.lineSeparator()}${validationSummary?.text}")
    }
  }

  private fun generateMinuteText(newRecall: PpudClient.NewRecall): String {
    val extended = if (newRecall.isExtendedSentence) {
      "YES"
    } else {
      "NO"
    }
    val custody = if (newRecall.isInCustody) {
      "YES at HMP"
    } else {
      "NO"
    }
    return "BACKGROUND INFO ${System.lineSeparator()}" +
      "Extended sentence: $extended${System.lineSeparator()}" +
      "Risk of Serious Harm Level: ${newRecall.riskOfSeriousHarmLevel.name.uppercase()}${System.lineSeparator()}" +
      "In custody: $custody"
  }

  fun extractRecallDetails(): PpudClient.Recall {
    // This should be performed when the Recall screen is in "existing recall" mode.
    // The add minute button is shown then, but not for a new recall
    if (addMinuteButton?.isDisplayed == true) {
      val idMatch = Regex(".+?data=(.+)").find(driver.currentUrl)!!
      val (id) = idMatch.destructured

      return PpudClient.Recall(
        id = id,
      )
    } else {
      throw Exception("Recall screen not refreshed")
    }
  }
}
