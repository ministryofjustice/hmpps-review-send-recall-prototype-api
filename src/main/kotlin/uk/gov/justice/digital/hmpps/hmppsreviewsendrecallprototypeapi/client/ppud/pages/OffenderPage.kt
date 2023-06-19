package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.selenium.TreeView
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.selenium.getValue

class OffenderPage(private val driver: WebDriver) {

  @FindBy(id = "cntDetails_txtCRO_PNC")
  private val croNumberInput: WebElement? = null

  @FindBy(id = "cntDetails_txtNOMS_ID")
  private val nomsIdInput: WebElement? = null

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
    val idMatch = Regex(".+?data=(.+)").find(driver.currentUrl)!!
    val (id) = idMatch.destructured

    return PpudClient.Offender(
      id = id,
      croNumber = croNumberInput.getValue(),
      nomsId = nomsIdInput.getValue(),
      firstNames = firstNamesInput.getValue(),
      familyName = familyNameInput.getValue(),
      dateOfBirth = dateOfBirthInput.getValue(),
    )
  }

  fun navigateTo(ppudUrl: String, offenderId: String) {
    driver.get("$ppudUrl/Offender/PersonalDetails.aspx?data=$offenderId")
  }

  fun navigateToNewRecallFor(sentenceDate: String, releaseDate: String) {
    val navigationElement = driver.findElement(By.id("navigation"))
    val navigationTreeView = TreeView(navigationElement.findElement(By.id("T_ctl00treetvOffender")))

    navigationTreeView
      .expandNodeWithText("Sentences")
      .expandNodeWithTextContaining(sentenceDate)
      .expandNodeWithText("Releases")
      .expandNodeWithTextContaining(releaseDate)
      .expandNodeWithTextContaining("Recalls")
      .findNodeWithTextContaining("New")
      .click()
  }
}
