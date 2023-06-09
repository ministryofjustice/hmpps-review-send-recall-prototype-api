package uk.gov.justice.digital.hmpps.hmppsreviewsendrecallprototypeapi.selenium

import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.OutputType
import org.openqa.selenium.Point
import org.openqa.selenium.Rectangle
import org.openqa.selenium.WebElement

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

  class TreeViewNode(private val element: WebElement) {

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
