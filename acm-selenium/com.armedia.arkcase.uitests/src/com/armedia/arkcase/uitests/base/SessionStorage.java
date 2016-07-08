package com.armedia.arkcase.uitests.base;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class SessionStorage {
	private JavascriptExecutor js;

	public SessionStorage(WebDriver driver) {
		this.js = (JavascriptExecutor) driver;
	}

	public void removeItemFromSessionStorage(String item) {
		js.executeScript(String.format("window.sessionStorage.removeItem('%s');", item));
	}

	public boolean isItemPresentInSessionStorage(String item) {
		if (js.executeScript(String.format("return window.sessionStorage.getItem('%s');", item)) == null) {
			return false;
		} else {
			return true;
		}
	}

	public String getItemFromSessionStorage(String key) {
		return (String) js.executeScript(String.format("return window.sessionStorage.getItem('%s');", key));
	}

	public String getKeyFromSessionStorage(int key) {
		return (String) js.executeScript(String.format("return window.sessionStorage.key('%s');", key));
	}

	public Long getSessionStorageLength() {
		return (Long) js.executeScript("return window.sessionStorage.length;");
	}

	public void setItemInSessionStorage(String item, String value) {
		js.executeScript(String.format("window.sessionStorage.setItem('%s','%s');", item, value));
	}

	public void clearSessionStorage() {
		js.executeScript(String.format("window.sessionStorage.clear();"));
	}
}