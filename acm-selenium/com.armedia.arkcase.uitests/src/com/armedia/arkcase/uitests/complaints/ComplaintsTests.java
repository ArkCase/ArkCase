package com.armedia.arkcase.uitests.complaints;

import org.junit.Test;
import org.openqa.selenium.support.PageFactory;
import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;

public class ComplaintsTests extends ArkCaseTestBase {

ComplaintPage complaint=PageFactory.initElements(driver, ComplaintPage.class);	
	
@Test
public void createNewComplaint() throws InterruptedException{
	
	super.logIn();
	Thread.sleep(10000);
	complaint.clickNewButton();
	Thread.sleep(5000);
	complaint.clickNewComplain();
	Thread.sleep(20000);
	driver.switchTo().frame(complaint.firstIfarme);
	Thread.sleep(3000);
	driver.switchTo().frame(complaint.secondIframe);
	Thread.sleep(3000);
	complaint.verifyNewComplaintPage();
	complaint.clickInitiatorFirstName();
	Thread.sleep(2000);
	complaint.setInitiatorFirstName("Milan");
	Thread.sleep(3000);
	complaint.clickInitiatorLastName();
	Thread.sleep(3000);
	complaint.setInitiatorLastName("Jovanovski");
	Thread.sleep(3000);
	complaint.clickIncidentTab();
	complaint.clickIncidentTab();
	Thread.sleep(3000);
	complaint.clickIncidentCategory();
	Thread.sleep(3000);
	complaint.selectAgricultural();
	Thread.sleep(3000);
	complaint.clickComplaintTitle();
	complaint.setComplaintTitle("Milan's Test");
	Thread.sleep(3000);
	complaint.clickPeopleTab();
	complaint.clickPeopleTab();
	Thread.sleep(4000);
	complaint.clickSelectparticipantType();
	Thread.sleep(3000);
	complaint.selectOwner();
	Thread.sleep(3000);
	complaint.clickSelectParticipant();
	Thread.sleep(3000);
	complaint.verifyAddpersonPopUp();
	complaint.setUserSearch("samuel");
	Thread.sleep(3000);
	complaint.clickGoButton();
	Thread.sleep(5000);
	complaint.verifyError();
	complaint.verifySearchedUser("Samuel Supervisor");
	Thread.sleep(2000);
	complaint.clickSearchedUser();
	Thread.sleep(3000);
	complaint.clickAddButton();
	Thread.sleep(4000);
	complaint.clickSubmitButton();
	Thread.sleep(20000);
	driver.switchTo().defaultContent();
	ArkCaseAuthentication.logOut(driver);
		
}
	
	
	
}
