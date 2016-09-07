package com.armedia.arkcase.uitests.base;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.thoughtworks.selenium.webdriven.commands.WaitForCondition;

public class ArkCaseAuthentication {

	public static void logIn(String username, String password, WebDriver driver, String baseUrl) {

		LocalStorage localStorage = new LocalStorage(driver);
		SessionStorage sessionStorage = new SessionStorage(driver);
		driver.get(baseUrl + "login");
		WaitHelper.waitPageToLoad(60, driver);
		localStorage.clearLocalStorage();
		sessionStorage.clearSessionStorage();
		WebElement username1 = driver.findElement(By.id("j_username"));
		username1.clear();
		username1.sendKeys(username);
		WebElement password1 = driver.findElement(By.id("j_password"));
		password1.clear();
		password1.sendKeys(password);
		WebElement logo = driver.findElement(By.xpath(".//div[@class='logo']/img"));
		Assert.assertTrue(logo.isDisplayed());
		WebElement logInText = driver.findElement(By.xpath(".//header[@class='text-center']/strong"));
		Assert.assertTrue(logInText.getText().equals("Enter your username and password."));
		Assert.assertTrue(logInText.isDisplayed());
		WebElement loginbutton = driver.findElement(By.id("submit"));
		loginbutton.click();
		WaitHelper.waitPageToLoad(60, driver);
	}

	public static void logOut(WebDriver driver) throws InterruptedException {
		WebElement linklogout = driver
				.findElement(By.xpath(".//span[@class='glyphicon glyphicon-triangle-bottom']"));
		WaitHelper.waitUntilUpdateAlertDisapear(60, driver);
		WebElement el = WaitHelper.getWhenElementIsVisible(linklogout, 60, driver);
		WaitHelper.clickWhenElelementIsClickable(el, 60, driver);
		WaitHelper.waitPageToLoad(60, driver);
		WebElement logout = driver
				.findElement(By.xpath(".//a[@ng-click='onClickLogout()']"));
		WebElement el1 = WaitHelper.getWhenElementIsVisible(logout, 60, driver);
		el1.click();
		WaitHelper.waitPageToLoad(60, driver);
		WaitHelper.waitUntilElementisPresent(driver.findElement(By.xpath(".//div[@class='alert alert-success']")), 60, driver);
		WebElement logOutsuccesfull = driver.findElement(By.xpath(".//div[@class='alert alert-success']"));
		Assert.assertTrue(logOutsuccesfull.getText().equals("You have been logged out successfully."));
	}
	
	

}
