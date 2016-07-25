package com.armedia.arkcase.uitests.complaints;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
public class ComplaintPage extends ArkCaseTestBase{
	
@FindBy(how=How.XPATH,using="/html/body/header/div/nav/ul/li/div/div[1]/div/a/span")
WebElement newComplaintBtn;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div")
WebElement complaintPage;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[1]/span[4]/label")
WebElement initiatorTab;
@FindBy(how=How.XPATH,using="/html/body/iframe")
WebElement secondIframe;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/iframe")
WebElement firstIfarme;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[2]/div[4]/div[2]/div[1]/div[2]/div[3]/div[1]/input")
WebElement firstNameInput;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[2]/div[4]/div[2]/div[1]/div[2]/div[4]/div[1]/input")
WebElement lastNameInput;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[2]/span[4]/label")
WebElement incidendTab;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[4]/div[1]/input")
WebElement nextButton;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div[4]/div[1]/input[1]")
WebElement incidentCategoryDropDown;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div[4]/div[1]/ul/li[2]/a")
WebElement agricultural;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div[6]/div[1]/input")
WebElement complaintTitleInput;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[5]/span[4]")
WebElement peopleTab;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[5]/div[2]/table/tbody/tr/td[4]/div/div[1]/input[1]")
WebElement selectParticipantTypeDropDown;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[5]/div[2]/table/tbody/tr/td[4]/div/div[1]/ul/li[2]/a")
WebElement participantOwner;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[5]/div[2]/table/tbody/tr/td[7]/div")
WebElement selectParticipant;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/div/div/div/div")
WebElement addUserPopUp;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/input")
WebElement searchForUserInput;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/span/button")
WebElement goBtn;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[2]")
WebElement searchedName;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[3]")
WebElement searchedUserType;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[5]")
WebElement searchedUsername;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/div/div/div/div/div[3]/button[2]")
WebElement addBtn;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div/input")
WebElement submitBtn;
@FindBy(how=How.XPATH,using="/html/body/header/div/nav/ul/li/a")
WebElement newButton;
@FindBy(how=How.XPATH,using="/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td")
WebElement noDataAviable;

public void clickNewButton(){

	newButton.click();
}

public void clickNewComplain(){
	Assert.assertEquals("Complaint new button name is wrong", "Complaint", newComplaintBtn.getText());	
	newComplaintBtn.click();
	
}

public void verifyNewComplaintPage(){
	int i=driver.findElements(By.xpath("/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div")).size();
	Assert.assertTrue("New complaint page is not displayed", i!=0);
	Assert.assertFalse("New complaint form is not displayed", complaintPage.getText().isEmpty());

}

public void clickInitiatorFirstName(){
	firstNameInput.click();
}
public void setInitiatorFirstName(String name){
	firstNameInput.sendKeys(name);
}

public void clickInitiatorLastName(){
	lastNameInput.click();
}

public void setInitiatorLastName(String lastName){
	lastNameInput.sendKeys(lastName);
}

public void clickNextButton(){
nextButton.click();
}

public void clickIncidentCategory(){
	incidentCategoryDropDown.click();
}

public void selectAgricultural(){
	agricultural.click();
}

public void clickIncidentTab(){
	incidendTab.click();
}

public void clickComplaintTitle(){
	complaintTitleInput.click();
}

public void setComplaintTitle(String title){
	complaintTitleInput.sendKeys(title);
}

public void clickPeopleTab(){
	peopleTab.click();
}

public void clickSelectparticipantType(){
	selectParticipantTypeDropDown.click();
}

public void selectOwner(){
	participantOwner.click();
}

public void clickSelectParticipant(){
	selectParticipant.click();
}

public void verifyAddpersonPopUp(){
	int i=driver.findElements(By.xpath("/html/body/div[1]/div[2]/div/div/div/div")).size();
	Assert.assertTrue("Add person popup is not displayed", i!=0);
}


public void setUserSearch(String name){
	searchForUserInput.sendKeys(name);
}


public void clickGoButton(){
	goBtn.click();
}

public void verifySearchedUser(String name){
Assert.assertEquals("Searched username is wrong",name , searchedName.getText());
}

public void clickSearchedUser(){
	searchedName.click();
}

public void clickAddButton(){
	addBtn.click();
}

public void clickSubmitButton(){
	submitBtn.click();
}



public void verifyNoDataAviliable(){
	Assert.assertTrue("Searched user is not shown", noDataAviable.getText().equals("No data available!"));
}


public void verifyError()
{
	int i=driver.findElements(By.xpath("/html/body/div[6]")).size();
	Assert.assertTrue("When go button is clicked for searching user Error message comunicating with server is shown", i==0);
}
















































	

}
