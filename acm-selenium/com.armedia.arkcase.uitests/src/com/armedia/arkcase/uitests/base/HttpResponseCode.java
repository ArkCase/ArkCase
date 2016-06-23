package com.armedia.arkcase.uitests.base;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HttpResponseCode extends ArkCaseTestBase {

	public static int getResponseCode(String urlString) throws MalformedURLException, IOException {
		URL url = new URL(urlString);
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		return huc.getResponseCode();
	}

	private static int statusCode;

	public void checkHttpResponse(String path) throws IOException {

		List<WebElement> links = driver.findElements(By.xpath(path));
		for (int i = 0; i < links.size(); i++) {
			if (!(links.get(i).getAttribute("href") == null) && !(links.get(i).getAttribute("href").equals(""))) {
				if (links.get(i).getAttribute("href").contains("http")) {
					statusCode = getResponseCode(links.get(i).getAttribute("href").trim());
					if (statusCode == 403) {

						Assert.assertFalse("HTTP 403 Forbidden # " + i + " " + links.get(i).getAttribute("href"),
								statusCode == 403);

					}
					if (statusCode == 500) {

						Assert.assertFalse("HTTP 500 Forbidden # " + i + " " + links.get(i).getAttribute("href"),
								statusCode == 500);
					}
					if(statusCode==404){
						Assert.assertFalse("HTTP 404 Not found # " + i + " " + links.get(i).getAttribute("href"),
								statusCode == 404);
					}

				}
			}
		}
	}

}