package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.PpudClient

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
    val navigationTreeView = navigationElement.findElement(By.id("T_ctl00treetvOffender"))

    val sentences = navigationTreeView.findElementWithText("Sentences")
    val sentencesChildren = expandNode(sentences)
    val sentence = sentencesChildren.findElement(By.xpath("//*[contains(text(), '$sentenceDate')]"))
    val sentenceChildren = expandNode(sentence)

    val releases = sentenceChildren.findElementWithText("Releases")
    val releasesChildren = expandNode(releases)
    val release = releasesChildren.findElementWithTextContaining(releaseDate)
    val releaseChildren = expandNode(release)

    val recalls = releaseChildren.findElementWithTextContaining("Recalls")
    val recallsChildren = expandNode(recalls)
    val newRecallElement = recallsChildren.findElementWithTextContaining("New")
    newRecallElement.click()

    // We perhaps want to write this like this...
    //    findTreeView("tree_tvOffender")
    //      .expandNodeWithText("Sentences")
    //      .expandNodeWithTextContaining(sentenceDate)
    //      .expandNodeWithText("Releases")
    //      .expandNodeWithTextContaining(releaseDate)
    //      .expandNodeWithText("Recalls")
    //      .findNodeWithWithTextContaining("New")
    //      .click()
  }

  private fun expandNode(textNode: WebElement): WebElement {
    val expansionElement = textNode.findElement(By.xpath("../following-sibling::div"))
    if (expansionElement.isDisplayed.not()) {
      val expanderImage = textNode.findElement(By.xpath("../img[@imgtype='exp']"))
      expanderImage.click()
    }
    return expansionElement
  }
}

private fun WebElement?.getValue(): String {
  return this?.getAttribute("value")?.trim() ?: ""
}

private fun WebElement.findElementWithText(text: String): WebElement {
  return this.findElement(By.xpath(".//*[text()='$text']"))
}

private fun WebElement.findElementWithTextContaining(text: String): WebElement {
  return this.findElement(By.xpath(".//*[contains(text(), '$text')]"))
}
