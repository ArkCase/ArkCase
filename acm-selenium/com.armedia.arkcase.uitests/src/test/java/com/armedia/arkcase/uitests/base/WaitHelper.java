package com.armedia.arkcase.uitests.base;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

public class WaitHelper {
	public static WebElement getWhenElementIsVisible(WebElement element, int timeout, WebDriver driver) {
	    WebDriverWait wait = new WebDriverWait(driver, timeout);
	    element = wait.until(ExpectedConditions.visibilityOf(element));
	    return element;
	}

	public static void clickWhenElelementIsClickable(WebElement element, int timeout, WebDriver driver) {
	    WebDriverWait wait = new WebDriverWait(driver, timeout);
	    WebElement el= wait.until(ExpectedConditions.elementToBeClickable(element));
	    el.click();
	}
	
	public static void continueWhenElementIsNotVisible(int timeout, WebDriver driver) {
		//loadign form from frevvo
		WebDriverWait waitLoadForm1 = new WebDriverWait(driver, timeout);
		waitLoadForm1.until(ExpectedConditions.invisibilityOfElementLocated(By.id("overlay")));
	}
	
	public static WebElement getWhenElementIsPresent(WebElement element, int timeout, WebDriver driver) {
	    WebDriverWait wait = new WebDriverWait(driver, timeout);
	    element = wait.until(ExpectedConditions.visibilityOf(element));
	    return element;
	}
	
	public static void waitUntilElementisPresent(WebElement element, int timeout, WebDriver driver) {
	    WebDriverWait wait = new WebDriverWait(driver, timeout);
	    wait.until(ExpectedConditions.visibilityOf(element));	    
	}
	
	public static void waitPageToLoad(int timeout, WebDriver driver)
	{			
		WebDriverWait wait = new WebDriverWait(driver, 30);

	    wait.until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver wdriver) {
	            return ((JavascriptExecutor) driver).executeScript(
	                "return document.readyState"
	            ).equals("complete");
	        }
	    });
	}
	public static void ClickElementAtPoint(WebElement element, WebDriver driver)
	{		
	    Actions actions = new Actions(driver);
	    actions.moveToElement(element).click().perform();
	}	
	
	public static void waitUntilUpdateAlertDisapear(int timeout, WebDriver driver) {
	    WebDriverWait wait = new WebDriverWait(driver, timeout);
	    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//div[@ng-show='!$messageTemplate']")));
	        
	}
	
	public static void waitForFrameAndSwitchToIt(int timeout, WebDriver driver, String frameName)
	{
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
	}
	public static void waitForElement(WebElement element, WebDriver driver) 
	{		 
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		wait.pollingEvery(250,  TimeUnit.MILLISECONDS);
		wait.withTimeout(3, TimeUnit.MINUTES);
		wait.ignoring(NoSuchElementException.class); //make sure that this exception is ignored
		Function<WebDriver, WebElement> function = new Function<WebDriver, WebElement>()
				{
					public WebElement apply(WebDriver arg0) {
						System.out.println("Checking for the element!!");						
						if(element != null)
						{
							System.out.println("Target element found");
						}
						return element;
					}
				};
 
		wait.until(function);
	}
	
	public static void waitForElementFindInCache(WebElement element, WebDriver driver) 
	{		 
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		wait.pollingEvery(250,  TimeUnit.MILLISECONDS);
		wait.withTimeout(3, TimeUnit.MINUTES);
		wait.ignoring(StaleElementReferenceException.class); //make sure that this exception is ignored
		Function<WebDriver, WebElement> function = new Function<WebDriver, WebElement>()
				{
					public WebElement apply(WebDriver arg0) {
						System.out.println("Checking for the element in cache!!");						
						if(element != null)
						{
							System.out.println("Target element found in cache");
						}
						return element;
					}
				};
 
		wait.until(function);
	}
	
	
	public static void waitUntilElementIsVisible(WebElement element, WebDriver driver) 
	{		 
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		wait.pollingEvery(250,  TimeUnit.MILLISECONDS);
		wait.withTimeout(2, TimeUnit.MINUTES);
		wait.ignoring(ElementNotVisibleException.class); //make sure that this exception is ignored
		Function<WebDriver, WebElement> function = new Function<WebDriver, WebElement>()
				{
					public WebElement apply(WebDriver driver) {
						System.out.println("Checking for the element!!");						
						if(element.isDisplayed() != true)
						{
							System.out.println("Target element is not visible");
						}
						return element;
					}
				};
 
		wait.until(function);
	}
	public static void waitUntilTextIsChanged(WebElement element, WebDriver driver) 
	{		 
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		wait.pollingEvery(250,  TimeUnit.MILLISECONDS);
		wait.withTimeout(2, TimeUnit.MINUTES);
		wait.ignoring(ElementNotVisibleException.class); //make sure that this exception is ignored
		Function<WebDriver, WebElement> function = new Function<WebDriver, WebElement>()
				{
					public WebElement apply(WebDriver arg0) {
						System.out.println("Checking is element changed into closed");						
						if(element.getText()== "CLOSED")
						{
							System.out.println("Target element text is changed into closed");
						}
						return element;
					}
				};
 
		wait.until(function);
	}
 
}