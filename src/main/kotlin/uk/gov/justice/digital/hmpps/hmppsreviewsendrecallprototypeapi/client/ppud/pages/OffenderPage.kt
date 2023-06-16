package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.client.ppud.pages

import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.OutputType
import org.openqa.selenium.Point
import org.openqa.selenium.Rectangle
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

private fun WebElement?.getValue(): String {
  return this?.getAttribute("value")?.trim() ?: ""
}

class TreeView(private val element: WebElement) : WebElement {

  fun expandNodeWithText(text: String): TreeViewNode {
    return TreeViewNode(this).expandNodeWithText(text)
  }

  override fun findElements(by: By?): MutableList<WebElement> {
    return element.findElements(by)
  }

  override fun findElement(by: By?): WebElement {
    return element.findElement(by)
  }

  override fun <X : Any?> getScreenshotAs(target: OutputType<X>?): X {
    return element.getScreenshotAs(target)
  }

  override fun click() {
    element.click()
  }

  override fun submit() {
    element.submit()
  }

  override fun sendKeys(vararg keysToSend: CharSequence?) {
    element.sendKeys()
  }

  override fun clear() {
    element.clear()
  }

  override fun getTagName(): String {
    return element.tagName
  }

  override fun getAttribute(name: String?): String {
    return element.getAttribute(name)
  }

  override fun isSelected(): Boolean {
    return element.isSelected
  }

  override fun isEnabled(): Boolean {
    return element.isEnabled
  }

  override fun getText(): String {
    return element.text
  }

  override fun isDisplayed(): Boolean {
    return element.isDisplayed
  }

  override fun getLocation(): Point {
    return element.location
  }

  override fun getSize(): Dimension {
    return element.size
  }

  override fun getRect(): Rectangle {
    return element.rect
  }

  override fun getCssValue(propertyName: String?): String {
    return element.getCssValue(propertyName)
  }

  class TreeViewNode(val element: WebElement) {

    fun expandNodeWithText(text: String): TreeViewNode {
      return TreeViewNode((expandNode(findNodeWithText(text))))
    }

    fun expandNodeWithTextContaining(text: String): TreeViewNode {
      return TreeViewNode(expandNode(findNodeWithTextContaining(text)))
    }

    private fun findNodeWithText(text: String): TreeViewNode {
      return TreeViewNode(element.findElement(By.xpath(".//*[text()='$text']")))
    }

    fun findNodeWithTextContaining(text: String): TreeViewNode {
      return TreeViewNode(element.findElement(By.xpath(".//*[contains(text(), '$text')]")))
    }

    private fun expandNode(textNode: TreeViewNode): WebElement {
      val expansionElement = textNode.findElement(By.xpath("../following-sibling::div"))
      if (expansionElement.isDisplayed.not()) {
        val expanderImage = textNode.findElement(By.xpath("../img[@imgtype='exp']"))
        expanderImage.click()
      }
      return expansionElement
    }

    fun click() {
      element.click()
    }

    private fun findElement(by: By?): WebElement {
      return element.findElement(by)
    }
  }
}
