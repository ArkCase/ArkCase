package com.armedia.arkcase.uitests.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

}
