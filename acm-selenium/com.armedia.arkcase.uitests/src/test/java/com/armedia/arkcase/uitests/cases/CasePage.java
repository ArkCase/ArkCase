package com.armedia.arkcase.uitests.cases;

import java.awt.AWTException;
import java.io.IOException;

import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;


import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;

public class CasePage {

	// General Information
	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/div/div[2]/div/a")
	public WebElement newCaseButton;
	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/a")
	public WebElement newButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[1]/div/span/a/i")
	public WebElement editButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[1]/span[4]")
	public WebElement generalInformationTab;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[2]/span[3]/label")
	public WebElement caseTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[2]/div[1]/input")
	public WebElement caseTitleInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/span[3]/label")
	public WebElement caseTypeTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/input[1]")
	public WebElement caseTypeInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[14]/a")
	public WebElement caseTypeEmbezzalmend;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[26]/a")
	public WebElement caseTypeTheftormisuseofasset;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[4]/div[2]/div[2]/div[1]/div/div[6]")
	public WebElement descriptionInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[4]/div[1]/input")
	public WebElement nextButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[4]")
	public WebElement nextButtonArea;
	// Initiator
	// Initiaotr Information
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/input[1]")
	public WebElement initiatorTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/ul/li[2]/a")
	public WebElement initiatorMr;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/ul/li[3]/a")
	public WebElement initiatorMrs;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/ul/li[4]/a")
	public WebElement initiatorMs;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/ul/li[5]/a")
	public WebElement initiatorMiss;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[3]/div[1]/input")
	public WebElement initiatorFirstName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[4]/div[1]/input")
	public WebElement initiatorLastName;
	// Initiator Comunication Device
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/input[1]")
	public WebElement typeCD;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[2]/a")
	public WebElement homePhoneCD;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[3]/a")
	public WebElement WorkPhoneCD;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[4]/a")
	public WebElement mobileCD;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[5]/a")
	public WebElement emailCD;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[6]/a")
	public WebElement facebookCD;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[3]/div[1]/input")
	public WebElement valueCD;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[4]/div[1]/input")
	public WebElement dateCD;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[5]/div[1]/input")
	public WebElement addedByCD;
	// Organization Information
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[2]/div[1]/input[1]")
	public WebElement typeOI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[2]/div[1]/ul/li[2]/a")
	public WebElement nonProfitOI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[2]/div[1]/ul/li[3]/a")
	public WebElement governmentOI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[2]/div[1]/ul/li[4]/a")
	public WebElement corporationOI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[3]/div[1]/input")
	public WebElement nameOI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[4]/div[1]/input")
	public WebElement dateOI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[5]/div[1]/input")
	public WebElement addedbyOI;
	// Location Information
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[2]/div[1]/input[1]")
	public WebElement typeLI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[2]/div[1]/ul/li[2]/a")
	public WebElement buisnessLI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[2]/div[1]/ul/li[3]/a")
	public WebElement homeLI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[3]/div[1]/input")
	public WebElement addressLI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[4]/div[1]/input")
	public WebElement cityLI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[5]/div[1]/input")
	public WebElement stateLI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[6]/div[1]/input")
	public WebElement zipCodeLI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[7]/div[1]/input")
	public WebElement dateLI;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[8]/div[1]/input")
	public WebElement addedByLI;
	// People
	// People Information
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[2]/div[1]/input[1]")
	public WebElement titlePeople;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[2]/div[1]/ul/li[2]/a")
	public WebElement titlePeopleMr;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[2]/div[1]/ul/li[3]/a")
	public WebElement titlePeopleMrs;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[2]/div[1]/ul/li[4]/a")
	public WebElement titlePeopleMs;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[2]/div[1]/ul/li[5]/a")
	public WebElement titlePeopleMiss;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[3]/div[1]/input")
	public WebElement firstNamePeople;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[4]/div[1]/input")
	public WebElement lastNamePeople;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[5]/div[1]/input[1]")
	public WebElement typePeopleInfo;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[5]/div[1]/ul/li[2]/a")
	public WebElement typePComplaintant;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[5]/div[1]/ul/li[3]/a")
	public WebElement typePSubject;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[5]/div[1]/ul/li[4]/a")
	public WebElement typePWitness;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[5]/div[1]/ul/li[5]/a")
	public WebElement typePWrongdoer;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[5]/div[1]/ul/li[6]/a")
	public WebElement typePOther;

	// People Comunication Device
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/input[1]")
	public WebElement typePcomDevice;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[2]/a")
	public WebElement peopleComunicationHome;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[3]/a")
	public WebElement peopleComunicationWork;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[4]/a")
	public WebElement peopleComunicationMobile;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[5]/a")
	public WebElement peopleComunicationEmail;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[2]/div[1]/ul/li[6]/a")
	public WebElement peopleCominicationFacebook;;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[3]/div[1]/input")
	public WebElement valuePcomDevice;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[6]/div[2]/div/div[2]/div[4]/div[1]/input")
	public WebElement datePcomDevice;
	// Organization Information
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[2]/div[1]/input[1]")
	public WebElement typePorganization;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[2]/div[1]/ul/li[2]/a")
	public WebElement peopleOrganizationNonProfit;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[2]/div[1]/ul/li[3]/a")
	public WebElement peopleOrganizationGovernment;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[2]/div[1]/ul/li[4]/a")
	public WebElement peopleOrganizationCorporation;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[3]/div[1]/input")
	public WebElement nameOrganization;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[7]/div[2]/div/div[2]/div[4]/div[1]/input")
	public WebElement datePOrganization;
	// Location Information
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[2]/div[1]/input[1]")
	public WebElement typePLocation;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[2]/div[1]/ul/li[2]/a")
	public WebElement peopleLocationBusiness;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[2]/div[1]/ul/li[3]/a")
	public WebElement peopleLocationHome;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[3]/div[1]/input")
	public WebElement adderessPeopleLocation;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[4]/div[1]/input")
	public WebElement cityPeopleLocationInfo;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[5]/div[1]/input")
	public WebElement statePeopleLocatonInfo;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[6]/div[1]/input")
	public WebElement zipPeopleLocationInfo;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[8]/div[2]/div/div[2]/div[7]/div[1]/input")
	public WebElement datePeopleLocationInfo;
	// Attachments
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[4]/div/a")
	public WebElement addFiles;
	@FindBy(how = How.XPATH, using = "/html/body/div[4]/div[2]/div/div[1]/div/table/tbody/tr/td/form/input")
	public WebElement BrowseButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[4]/div[2]/div/div[2]/div/div/a")
	public WebElement uploadButton;
	// Participants
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[3]/div[2]/table/tbody/tr/td[5]/div/div[1]/input[1]")
	public WebElement selectParticipantType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[3]/div[2]/table/tbody/tr/td[5]/div/div[1]/ul/li[2]/a")
	public WebElement selectParticipantOwner;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[3]/div[2]/table/tbody/tr/td[5]/div/div[1]/ul/li[3]/a")
	public WebElement selectParticipantFollower;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[3]/div[2]/table/tbody/tr/td[8]/div")
	public WebElement selectParticipant;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[3]/div[2]/table/tbody/tr/td[2]")
	public WebElement participantTypePlusBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[3]/div[2]/table/tbody/tr[2]/td[5]/div/div[1]/input[1]")
	public WebElement secondRowSelectParticipantType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[3]/div[2]/table/tbody/tr[2]/td[5]/div/div[1]/ul/li[2]/a")
	public WebElement selectParticipantFolower;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[3]/div[2]/table/tbody/tr[2]/td[8]")
	public WebElement selectSecondParticipant;

	// add user
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/input")
	public WebElement serachForUsers;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/span/button")
	public WebElement searchUserButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[2]/a")
	public WebElement searchedName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[3]/button[2]")
	public WebElement addSearchedName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[7]/div/input")
	public WebElement submit;
	// Case Types
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[2]/a")
	public WebElement caseTypeAgricultural;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[3]/a")
	public WebElement caseTypeArson;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[4]/a")
	public WebElement caseTypeBackgroundInvestigation;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[5]/a")
	public WebElement caseTypeBenefitsAppeal;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[6]/a")
	public WebElement caseTypeBetterBuisnessDispute;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[7]/a")
	public WebElement caseTypeClinicalInvestigatorFraud;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[8]/a")
	public WebElement caseTypeCongressionalResponse;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[9]/a")
	public WebElement caseTypeDisabilityWaiverRequest;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[10]/a")
	public WebElement caseTypeDomesticDispute;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[11]/a")
	public WebElement caseTypeDrugTrafficking;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[12]/a")
	public WebElement caseTypeEducationWaiverRequest;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[13]/a")
	public WebElement caseTypeEEOHarassment;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[15]/a")
	public WebElement caseTypeExtortion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[16]/a")
	public WebElement caseTypeFraud;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[17]/a")
	public WebElement caseTypeGovernment;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[18]/a")
	public WebElement caseTypeInvestorComplaint;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[19]/a")
	public WebElement caseTypeLaborRacketeering;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[20]/a")
	public WebElement caseTypeLocal;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[21]/a")
	public WebElement caseTypeMurder;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[22]/a")
	public WebElement casetypeNewDrugApplicationFraud;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[23]/a")
	public WebElement casseTypePayoff;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[24]/a")
	public WebElement caseTypePensionWaiverRequest;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[25]/a")
	public WebElement caseTypePollution;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[1]/ul/li[26]/a")
	public WebElement caseTypeProductTampering;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[2]/span[4]/label")
	public WebElement initiatorTab;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[3]/span[4]/label")
	public WebElement peopleTab;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[4]/span[4]/label")
	public WebElement attachmentTab;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[5]/span[4]/label")
	public WebElement participantnsTab;
    @FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/div/div[1]/div")
	public WebElement caseButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[1]/nav/ul/li[2]/a")
	public WebElement caseModule;

	public void newCase() throws InterruptedException {

		Assert.assertTrue("The edit button in dashboard is not displayed", editButton.isDisplayed());
		Assert.assertTrue("The new button is not displayed", newButton.isDisplayed());
		newButton.click();
		Thread.sleep(2000);
		Assert.assertEquals("Case name is wrong", "Case", newCaseButton.getText());
		newCaseButton.click();
		//caseButton.click();
	}

	public void vrifyGeneralInformationTabName() {

		Assert.assertTrue(generalInformationTab.isDisplayed());
		Assert.assertEquals("General Information tab title is wrong", "General Information",
				generalInformationTab.getText());

	}

	public void veifyCaseTitle() {

		Assert.assertTrue(caseTitle.isDisplayed());
		Assert.assertEquals("Case title is wrong", "Case Title", caseTitle.getText());

	}

	public void caseTitleInput(String caseName) {

		Assert.assertTrue(caseTitleInput.isDisplayed());
		caseTitleInput.click();
		caseTitleInput.sendKeys(caseName);
	}

	public void verifyCaseTypeTitle() {

		Assert.assertTrue(caseTypeTitle.isDisplayed());
		Assert.assertEquals("Case type title is wrong", "Case Type", caseTypeTitle.getText());

	}

	public void caseTypeInputClick() {

		Assert.assertTrue(caseTypeInput.isDisplayed());
		caseTypeInput.click();

	}

	public void caseTypeEmbezzalmendClick() {
		Assert.assertEquals("Case type Embezzlement name is wrong", "Embezzlement", caseTypeEmbezzalmend.getText());
		caseTypeEmbezzalmend.click();
	}

	public void caseTypeAgricultural() {
		Assert.assertTrue(caseTypeAgricultural.getText().equals("Agricultural"));

		caseTypeAgricultural.click();
	}

	public void caseTypeArson() {

		Assert.assertTrue(caseTypeArson.getText().equals("Arson"));
		caseTypeArson.click();
	}

	public void caseTypeBackgroundInvestigation() {

		Assert.assertTrue(caseTypeBackgroundInvestigation.getText().equals("Background Investigation"));
		caseTypeBackgroundInvestigation.click();

	}

	public void caseTypeBetterBuisnessDispute() {

		Assert.assertTrue(caseTypeBetterBuisnessDispute.getText().equals("Better Business Dispute"));
		caseTypeBetterBuisnessDispute.click();
	}

	public void caseTypeClinicalInvestigatorFraud() {

		Assert.assertTrue(caseTypeClinicalInvestigatorFraud.getText().equals("Clinical Investigator Fraud"));
		caseTypeClinicalInvestigatorFraud.click();
	}

	public void caseTypeCongressionalResponse() {

		Assert.assertTrue(caseTypeCongressionalResponse.getText().equals("Congressional Response"));
		caseTypeCongressionalResponse.click();
	}

	public void caseTypeDisabilityWaiverRequest() {

		Assert.assertTrue(caseTypeDisabilityWaiverRequest.getText().equals("Disability Waiver Request"));
		caseTypeDisabilityWaiverRequest.click();
	}

	public void caseTypeDomesticDispute() {

		Assert.assertTrue(caseTypeDomesticDispute.getText().equals("Domestic Dispute"));
		caseTypeDomesticDispute.click();
	}

	public void caseTypeDrugTrafficking() {

		Assert.assertTrue(caseTypeDrugTrafficking.getText().equals("Drug Trafficking"));
		caseTypeDrugTrafficking.click();
	}

	public void caseTypeEducationWaiverRequest() {

		Assert.assertTrue(caseTypeEducationWaiverRequest.getText().equals("Education Waiver Request"));
		caseTypeEducationWaiverRequest.click();
	}

	public void caseTypeEEOHarassment() {

		Assert.assertTrue(caseTypeEEOHarassment.getText().equals("EEO/Harassment"));
		caseTypeEEOHarassment.click();
	}

	public void caseTypeExtortion() {

		Assert.assertTrue(caseTypeExtortion.getText().equals("Extortion"));
		caseTypeExtortion.click();
	}

	public void caseTypeFraud() {

		Assert.assertTrue(caseTypeFraud.getText().equals("Fraud"));
		caseTypeFraud.click();
	}

	public void caseTypeGovernment() {

		Assert.assertTrue(caseTypeGovernment.getText().equals("Government"));
		caseTypeGovernment.click();
	}

	public void caseTypeInvestorComplaint() {

		Assert.assertTrue(caseTypeInvestorComplaint.getText().equals("Investor Complaint"));
		caseTypeInvestorComplaint.click();

	}

	public void caseTypeBenefitsAppeal() {

		Assert.assertTrue(caseTypeBenefitsAppeal.getText().equals("Benefits Appeal"));
		caseTypeBenefitsAppeal.click();

	}

	public void caseTypeLaborRacketeering() {

		Assert.assertTrue(caseTypeLaborRacketeering.getText().equals("Labor Racketeering"));
		caseTypeLaborRacketeering.click();

	}

	public void caseTypeLocal() {

		Assert.assertTrue(caseTypeLocal.getText().equals("Local"));
		caseTypeLocal.click();
	}

	public void caseTypeMudred() {

		Assert.assertTrue(caseTypeMurder.getText().equals("Murder"));
		caseTypeMurder.click();
	}

	public void caseTypeNewDrugApplicationFraud() {

		Assert.assertTrue(casetypeNewDrugApplicationFraud.getText().equals("New Drug Application Fraud"));
		casetypeNewDrugApplicationFraud.click();
	}

	public void caseTypePayoff() {

		Assert.assertTrue(casseTypePayoff.getText().equals("Payoff"));
		casseTypePayoff.click();
	}

	public void caseTypePensionWaiverRequest() {

		Assert.assertTrue(caseTypePensionWaiverRequest.getText().equals("Pension Waiver Request"));
		caseTypePensionWaiverRequest.click();
	}

	public void caseTypePollution() {

		Assert.assertTrue(caseTypePollution.getText().equals("Pollution"));
		caseTypePollution.click();
	}

	public void caseTypeProductTampering() {

		Assert.assertTrue(caseTypeProductTampering.getText().equals("Product Tampering"));
		caseTypeProductTampering.click();
	}

	public void caseTypeTheftTheftormisuseofasset() {
		caseTypeTheftormisuseofasset.click();
	}

	public void descriptionInput(String description) {

		descriptionInput.click();
		descriptionInput.sendKeys(description);
	}

	public void nextButtonClick() {
		nextButton.click();
	}

	public void clickInitiatorMr() {

		Assert.assertTrue(initiatorMr.getText().equals("Mr"));
		initiatorMr.click();

	}

	public void clickInitiatorMrs() {
		Assert.assertTrue(initiatorMrs.getText().equals("Mrs"));
		initiatorMrs.click();
	}

	public void clickInitiatorMs() {

		Assert.assertTrue(initiatorMs.getText().equals("Ms"));
		initiatorMs.click();

	}

	public void ClickInitiatorMiss() {

		Assert.assertTrue(initiatorMiss.getText().equals("Miss"));
		initiatorMiss.click();

	}

	public void initiatorFirstName(String firstName) {

		initiatorFirstName.click();
		initiatorFirstName.sendKeys(firstName);

	}

	public void initiatorLastName(String lastName) {

		initiatorLastName.click();
		initiatorLastName.sendKeys(lastName);
	}

	public void ClickHomePhoneCd() {

		Assert.assertTrue(homePhoneCD.getText().equals("Home phone"));
		homePhoneCD.click();
	}

	public void ClickWorkPhoneCd() {

		Assert.assertTrue(WorkPhoneCD.getText().equals("Work phone"));
		WorkPhoneCD.click();
	}

	public void ClickMobileCd() {

		Assert.assertTrue(mobileCD.getText().equals("Mobile"));
		mobileCD.click();
	}

	public void ClickEmailCd() {

		Assert.assertTrue(emailCD.getText().equals("Email"));
		emailCD.click();

	}

	public void ClickFacebookCd() {
		Assert.assertTrue(facebookCD.getText().equals("Facebook"));
		facebookCD.click();
	}

	public void insertValueCd(String value) {

		valueCD.click();
		valueCD.sendKeys(value);
	}

	public void insertDateCD(String date) {

		dateCD.click();
		dateCD.clear();
		dateCD.sendKeys(date);
	}

	public void verifyAddedByCd(String user) {

		Assert.assertTrue(addedByCD.getText().equals(user));
	}

	public void ClickNonProfitOi() {

		Assert.assertTrue(nonProfitOI.getText().equals("Non-profit"));
		nonProfitOI.click();
	}

	public void ClickGovernmentOi() {

		Assert.assertTrue(governmentOI.getText().equals("Government"));
		governmentOI.click();
	}

	public void ClickCorporationOi() {

		Assert.assertTrue(corporationOI.getText().equals("Corporation"));
		corporationOI.click();
	}

	public void insertNameOi(String name) {

		nameOI.click();
		nameOI.sendKeys(name);
	}

	public void insertDateOi(String date) {

		dateOI.click();
		dateOI.clear();
		dateOI.sendKeys(date);
	}

	public void verifyAddedByOi(String user) {

		Assert.assertTrue(addedbyOI.getText().equals(user));
	}

	public void insertAddressLi(String address) {

		addressLI.click();
		addressLI.sendKeys(address);

	}

	public void ClickbuisnessLi() {

		Assert.assertTrue(buisnessLI.getText().equals("Business"));
		buisnessLI.click();
	}

	public void CLickHomeLi() {

		Assert.assertTrue(homeLI.getText().equals("Home"));
		homeLI.click();
	}

	public void insertCityLi(String city) {

		cityLI.click();
		cityLI.sendKeys(city);
	}

	public void insertStateLi(String state) {

		stateLI.click();
		stateLI.sendKeys(state);
	}

	public void insertZipCodeLi(String zip) {

		zipCodeLI.click();
		zipCodeLI.sendKeys(zip);
	}

	public void insertStartDateLi(String date) throws InterruptedException {

		dateLI.click();
		Thread.sleep(2000);
		dateLI.clear();
		dateLI.sendKeys(date);

	}

	public void verifyAddedByLi(String user) {

		Assert.assertTrue(addedByLI.getText().equals(user));
	}

	public void clickTitlePeopleMr() {

		Assert.assertTrue(titlePeopleMr.getText().equals("Mr"));
		titlePeopleMr.click();

	}

	public void clickTitlePeopleMrs() {

		Assert.assertTrue(titlePeopleMrs.getText().equals("Mrs"));
		titlePeopleMrs.click();

	}

	public void clickTitlePeopleMs() {

		Assert.assertTrue(titlePeopleMs.getText().equals("Ms"));
		titlePeopleMs.click();

	}

	public void clickTitlePeopleMiss() {

		Assert.assertTrue(titlePeopleMiss.getText().equals("Miss"));
		titlePeopleMiss.click();

	}

	public void insertFirstNamePeople(String firstName) {

		firstNamePeople.click();
		firstNamePeople.sendKeys(firstName);

	}

	public void insertLastNamePeople(String lastName) {

		lastNamePeople.click();
		lastNamePeople.sendKeys(lastName);
	}

	public void typePeopleComplaintant() {

		Assert.assertTrue(typePComplaintant.getText().equals("Complaintant"));
		typePComplaintant.click();

	}

	public void typePeopleWitness() {

		Assert.assertTrue(typePWitness.getText().equals("Witness"));
		typePWitness.click();

	}

	public void typePeopleComHomePhone() {

		Assert.assertTrue(peopleComunicationHome.getText().equals("Home phone"));
		peopleComunicationHome.click();

	}

	public void typePeopleComWorkPhone() {

		Assert.assertTrue(peopleComunicationWork.getText().equals("Work phone"));
		peopleComunicationWork.click();

	}

	public void typePeopleComMobilePhone() {

		Assert.assertTrue(peopleComunicationMobile.getText().equals("Mobile"));
		peopleComunicationMobile.click();

	}

	public void typePeopleComEmail() {

		Assert.assertTrue(peopleComunicationEmail.getText().equals("Email"));
		peopleComunicationEmail.click();

	}

	public void typePeopleComFacebook() {

		Assert.assertTrue(peopleCominicationFacebook.getText().equals("Facebook"));
		peopleCominicationFacebook.click();

	}

	public void insertPeoplComValue(String value) {

		valuePcomDevice.click();
		valuePcomDevice.sendKeys(value);
	}

	public void insertPeopleComDate(String date) throws InterruptedException {

		datePcomDevice.click();
		Thread.sleep(2000);
		datePcomDevice.clear();
		datePcomDevice.sendKeys(date);

	}

	public void typePeopleOrganizationNonProfit() {

		Assert.assertTrue(peopleOrganizationNonProfit.getText().equals("Non-profit"));
		peopleOrganizationNonProfit.click();
	}

	public void typePeopleOrganizationGovernment() {

		Assert.assertTrue(peopleOrganizationGovernment.getText().equals("Goverment"));
		peopleOrganizationGovernment.click();
	}

	public void typePeopleOrganizationCorpoation() {

		Assert.assertTrue(peopleOrganizationNonProfit.getText().equals("Corporation"));
		peopleOrganizationCorporation.click();
	}

	public void insertPeopleNameOrganizationInformation() {

		nameOrganization.click();
		nameOrganization.sendKeys("Organization Information");

	}

	public void insertPeopleORganizationDate(String date) throws InterruptedException {

		datePOrganization.click();
		Thread.sleep(2000);
		datePOrganization.clear();
		datePOrganization.sendKeys(date);

	}

	public void typePeopleLocationBusiness() {

		Assert.assertTrue(peopleLocationBusiness.getText().equals("Business"));
		peopleLocationBusiness.click();
	}

	public void typePeopleLocationHome() {

		Assert.assertTrue(peopleLocationHome.getText().equals("Home"));
		peopleLocationHome.click();
	}

	public void insertPeopleLocationInfoAddress(String address) {

		adderessPeopleLocation.click();
		adderessPeopleLocation.sendKeys(address);
	}

	public void insertPeopleLocationInfoCity(String city) {

		cityPeopleLocationInfo.click();
		cityPeopleLocationInfo.sendKeys(city);

	}

	public void insertPeopleLocationInfoState(String state) {

		statePeopleLocatonInfo.click();
		statePeopleLocatonInfo.sendKeys(state);

	}

	public void inssertPeopleLocationInfoZip(String zip) {

		zipPeopleLocationInfo.click();
		zipPeopleLocationInfo.sendKeys(zip);

	}

	public void insertPeopleLocationInfoDate(String date) throws InterruptedException {

		datePeopleLocationInfo.click();
		Thread.sleep(2000);
		datePeopleLocationInfo.clear();
		datePeopleLocationInfo.sendKeys(date);

	}

	public void attachmentsAddFilesClickButton() {
		addFiles.click();
	}

	public void browseButtonClick() {

		BrowseButton.click();
	}

	public void addFile() throws IOException, AWTException {

		ArkCaseTestUtils.uploadPdf();

	}

	public void uploadButtonClick() {
		uploadButton.click();
	}

	public void selectParticipantTypeClick() {
		selectParticipantType.click();
	}

	public void selectparticipantOwner() throws InterruptedException {

		Assert.assertEquals("Owner name is wrong", "Owner", selectParticipantOwner.getText());
		selectParticipantOwner.click();
		Thread.sleep(4000);

	}

	public void selectParticipantClick() {
		selectParticipant.click();
	}

	public void searchForUsers() throws InterruptedException {

		serachForUsers.click();
		Thread.sleep(2000);
		serachForUsers.sendKeys("Samuel Supervisor");
		searchUserButton.click();

	}

	public void searchedName() {

		Assert.assertEquals("User name is wrong", "Samuel Supervisor", searchedName.getText().toString());

		searchedName.click();

	}

	public void addSearchedNameClick() {
		addSearchedName.click();
	}

	public void verifyInitiatorTab() {

		Assert.assertEquals("Initiator tab name is wrong", "Initiator", initiatorTab.getText());
	}

	public void verifyPeopleTab() {

		Assert.assertTrue(peopleTab.getText().equals("People"));
	}

	public void verifyAttachmentTab() {

		Assert.assertTrue(attachmentTab.getText().equals("Attachments"));
	}

	public void verifyParticipantTab() {

		Assert.assertTrue(participantnsTab.getText().equals("Participants"));
	}

	public void clickParticipantTypePlusBtn() {
		participantTypePlusBtn.click();
	}

	public void selectSecondTypeParticipant() {

		secondRowSelectParticipantType.click();
	}

	public void selectParticipantTypeFolower() {

		Assert.assertEquals("Folower label name is wrong", "Follower", selectParticipantFolower.getText());
		selectParticipantFolower.click();
	}

	public void clickSecondParticipant() {
		selectSecondParticipant.click();

	}

	public void selectParticipantFollower() {
		selectParticipantFollower.click();
	}

}
