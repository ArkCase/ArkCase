package com.armedia.arkcase.uitests.base;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ArkCaseAuthentication {

	public static void logIn(String username, String password, WebDriver driver, String baseUrl) {

		LocalStorage localStorage = new LocalStorage(driver);
		SessionStorage sessionStorage = new SessionStorage(driver);
		driver.get(baseUrl + "login");
		localStorage.clearLocalStorage();
		sessionStorage.clearSessionStorage();
		WebElement username1 = driver.findElement(By.xpath("/html/body/div/form/div/div[1]/input"));
		username1.clear();
		username1.sendKeys(username);
		WebElement password1 = driver.findElement(By.xpath("/html/body/div/form/div/div[2]/input"));
		password1.clear();
		password1.sendKeys(password);
		WebElement logo = driver.findElement(By.xpath("/html/body/div/div/img"));
		Assert.assertTrue(logo.isDisplayed());
		WebElement logInText = driver.findElement(By.xpath("/html/body/div/header/strong"));
		Assert.assertTrue(logInText.getText().equals("Enter your username and password."));
		Assert.assertTrue(logInText.isDisplayed());
		WebElement loginbutton = driver.findElement(By.id("submit"));
		loginbutton.click();
	}

	public static void logOut(WebDriver driver) throws InterruptedException {
		WebElement linklogout = driver
				.findElement(By.xpath("/html/body/div[1]/div/div[1]/nav/div[1]/div/div/div[2]/a/strong/span[2]"));
		linklogout.click();
		WebElement logout = driver
				.findElement(By.xpath("/html/body/div[1]/div/div[1]/nav/div[1]/div/div/div[2]/ul/li[4]/a"));
		logout.click();
		Thread.sleep(3000);
		WebElement logOutsuccesfull = driver.findElement(By.xpath("/html/body/div/div[2]"));
		Assert.assertTrue(logOutsuccesfull.getText().equals("You have been logged out successfully."));
	}
	
	

}
