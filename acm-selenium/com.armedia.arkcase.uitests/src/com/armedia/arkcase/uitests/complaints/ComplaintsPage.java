package com.armedia.arkcase.uitests.complaints;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ComplaintsPage {
	
@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/div/div[1]/h3/span")
WebElement complaintsPageTitle;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/div/div[1]/h3/span")
WebElement complaintsTitle;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[5]")
WebElement documentsLink;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/a[1]")
WebElement newComplaintBtn;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/a[2]")
WebElement closeComplaintBtn;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[2]")
WebElement subscribeBtn;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[3]")
WebElement refreshPageBtn;



	
	

}
