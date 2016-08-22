package com.armedia.arkcase.uitests.cases;

import java.awt.AWTException;
import java.io.IOException;

import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import com.armedia.arkcase.uitests.audit.AuditPage;
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
	@FindBy(how = How.ID, using = "edtPoSearch")
	public WebElement searchForUsers;
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
	@FindBy(how = How.XPATH, using = ".//*[@title='Case Files']")
	public WebElement caseFilesMenu;
	@FindBy(how = How.XPATH, using = ".//*[@class='row']/div[2]/h4")
	public WebElement caseId;

	public CasePage newCase() throws InterruptedException {

		Assert.assertTrue("The edit button in dashboard is not displayed", editButton.isDisplayed());
		Assert.assertTrue("The new button is not displayed", newButton.isDisplayed());
		newButton.click();
		Thread.sleep(2000);
		Assert.assertEquals("Case name is wrong", "Case", newCaseButton.getText());
		newCaseButton.click();
		return this;
	}

	public CasePage vrifyGeneralInformationTabName() {

		Assert.assertTrue(generalInformationTab.isDisplayed());
		Assert.assertEquals("General Information tab title is wrong", "General Information",
				generalInformationTab.getText());
		return this;

	}

	public CasePage veifyCaseTitle() {

		Assert.assertTrue(caseTitle.isDisplayed());
		Assert.assertEquals("Case title is wrong", "Case Title", caseTitle.getText());
		return this;

	}

	public CasePage caseTitleInput(String caseName) {

		Assert.assertTrue(caseTitleInput.isDisplayed());
		caseTitleInput.click();
		caseTitleInput.sendKeys(caseName);
		return this;
	}

	public CasePage verifyCaseTypeTitle() {

		Assert.assertTrue(caseTypeTitle.isDisplayed());
		Assert.assertEquals("Case type title is wrong", "Case Type", caseTypeTitle.getText());
		return this;

	}

	public CasePage caseTypeInputClick() {

		Assert.assertTrue(caseTypeInput.isDisplayed());
		caseTypeInput.click();
		return this;

	}

	public CasePage caseTypeEmbezzalmendClick() {
		Assert.assertEquals("Case type Embezzlement name is wrong", "Embezzlement", caseTypeEmbezzalmend.getText());
		caseTypeEmbezzalmend.click();
		return this;
	}

	public CasePage caseTypeAgricultural() {
		Assert.assertTrue(caseTypeAgricultural.getText().equals("Agricultural"));
		caseTypeAgricultural.click();
		return this;
	}

	public CasePage caseTypeArson() {

		Assert.assertTrue(caseTypeArson.getText().equals("Arson"));
		caseTypeArson.click();
		return this;
	}

	public CasePage caseTypeBackgroundInvestigation() {

		Assert.assertTrue(caseTypeBackgroundInvestigation.getText().equals("Background Investigation"));
		caseTypeBackgroundInvestigation.click();
		return this;

	}

	public CasePage caseTypeBetterBuisnessDispute() {

		Assert.assertTrue(caseTypeBetterBuisnessDispute.getText().equals("Better Business Dispute"));
		caseTypeBetterBuisnessDispute.click();
		return this;
	}

	public CasePage caseTypeClinicalInvestigatorFraud() {

		Assert.assertTrue(caseTypeClinicalInvestigatorFraud.getText().equals("Clinical Investigator Fraud"));
		caseTypeClinicalInvestigatorFraud.click();
		return this;
	}

	public CasePage caseTypeCongressionalResponse() {

		Assert.assertTrue(caseTypeCongressionalResponse.getText().equals("Congressional Response"));
		caseTypeCongressionalResponse.click();
		return this;
	}

	public CasePage caseTypeDisabilityWaiverRequest() {

		Assert.assertTrue(caseTypeDisabilityWaiverRequest.getText().equals("Disability Waiver Request"));
		caseTypeDisabilityWaiverRequest.click();
		return this;
	}

	public CasePage caseTypeDomesticDispute() {

		Assert.assertTrue(caseTypeDomesticDispute.getText().equals("Domestic Dispute"));
		caseTypeDomesticDispute.click();
		return this;
	}

	public CasePage caseTypeDrugTrafficking() {

		Assert.assertTrue(caseTypeDrugTrafficking.getText().equals("Drug Trafficking"));
		caseTypeDrugTrafficking.click();
		return this;
	}

	public CasePage caseTypeEducationWaiverRequest() {

		Assert.assertTrue(caseTypeEducationWaiverRequest.getText().equals("Education Waiver Request"));
		caseTypeEducationWaiverRequest.click();
		return this;
	}

	public CasePage caseTypeEEOHarassment() {

		Assert.assertTrue(caseTypeEEOHarassment.getText().equals("EEO/Harassment"));
		caseTypeEEOHarassment.click();
		return this;
	}

	public CasePage caseTypeExtortion() {

		Assert.assertTrue(caseTypeExtortion.getText().equals("Extortion"));
		caseTypeExtortion.click();
		return this;
	}

	public CasePage caseTypeFraud() {

		Assert.assertTrue(caseTypeFraud.getText().equals("Fraud"));
		caseTypeFraud.click();
		return this;
	}

	public CasePage caseTypeGovernment() {

		Assert.assertTrue(caseTypeGovernment.getText().equals("Government"));
		caseTypeGovernment.click();
		return this;
	}

	public CasePage caseTypeInvestorComplaint() {

		Assert.assertTrue(caseTypeInvestorComplaint.getText().equals("Investor Complaint"));
		caseTypeInvestorComplaint.click();
		return this;

	}

	public CasePage caseTypeBenefitsAppeal() {

		Assert.assertTrue(caseTypeBenefitsAppeal.getText().equals("Benefits Appeal"));
		caseTypeBenefitsAppeal.click();
		return this;

	}

	public CasePage caseTypeLaborRacketeering() {

		Assert.assertTrue(caseTypeLaborRacketeering.getText().equals("Labor Racketeering"));
		caseTypeLaborRacketeering.click();
		return this;

	}

	public CasePage caseTypeLocal() {

		Assert.assertTrue(caseTypeLocal.getText().equals("Local"));
		caseTypeLocal.click();
		return this;
	}

	public CasePage caseTypeMudred() {

		Assert.assertTrue(caseTypeMurder.getText().equals("Murder"));
		caseTypeMurder.click();
		return this;
	}

	public CasePage caseTypeNewDrugApplicationFraud() {

		Assert.assertTrue(casetypeNewDrugApplicationFraud.getText().equals("New Drug Application Fraud"));
		casetypeNewDrugApplicationFraud.click();
		return this;
	}

	public CasePage caseTypePayoff() {

		Assert.assertTrue(casseTypePayoff.getText().equals("Payoff"));
		casseTypePayoff.click();
		return this;
	}

	public CasePage caseTypePensionWaiverRequest() {

		Assert.assertTrue(caseTypePensionWaiverRequest.getText().equals("Pension Waiver Request"));
		caseTypePensionWaiverRequest.click();
		return this;
	}

	public CasePage caseTypePollution() {

		Assert.assertTrue(caseTypePollution.getText().equals("Pollution"));
		caseTypePollution.click();
		return this;
	}

	public CasePage caseTypeProductTampering() {

		Assert.assertTrue(caseTypeProductTampering.getText().equals("Product Tampering"));
		caseTypeProductTampering.click();
		return this;
	}

	public CasePage caseTypeTheftTheftormisuseofasset() {
		caseTypeTheftormisuseofasset.click();
		return this;
	}

	public CasePage descriptionInput(String description) {

		descriptionInput.click();
		descriptionInput.sendKeys(description);
		return this;
	}

	public CasePage nextButtonClick() {
		nextButton.click();
		return this;
	}

	public CasePage clickInitiatorMr() {

		Assert.assertTrue(initiatorMr.getText().equals("Mr"));
		initiatorMr.click();
		return this;

	}

	public CasePage clickInitiatorMrs() {
		Assert.assertTrue(initiatorMrs.getText().equals("Mrs"));
		initiatorMrs.click();
		return this;
	}

	public CasePage clickInitiatorMs() {

		Assert.assertTrue(initiatorMs.getText().equals("Ms"));
		initiatorMs.click();
		return this;

	}

	public CasePage ClickInitiatorMiss() {

		Assert.assertTrue(initiatorMiss.getText().equals("Miss"));
		initiatorMiss.click();
		return this;

	}

	public CasePage initiatorFirstName(String firstName) {

		initiatorFirstName.click();
		initiatorFirstName.sendKeys(firstName);
		return this;

	}

	public CasePage initiatorLastName(String lastName) {

		initiatorLastName.click();
		initiatorLastName.sendKeys(lastName);
		return this;
	}

	public CasePage ClickHomePhoneCd() {

		Assert.assertTrue(homePhoneCD.getText().equals("Home phone"));
		homePhoneCD.click();
		return this;
	}

	public CasePage ClickWorkPhoneCd() {

		Assert.assertTrue(WorkPhoneCD.getText().equals("Work phone"));
		WorkPhoneCD.click();
		return this;
	}

	public CasePage ClickMobileCd() {

		Assert.assertTrue(mobileCD.getText().equals("Mobile"));
		mobileCD.click();
		return this;
	}

	public CasePage ClickEmailCd() {

		Assert.assertTrue(emailCD.getText().equals("Email"));
		emailCD.click();
		return this;

	}

	public CasePage ClickFacebookCd() {
		Assert.assertTrue(facebookCD.getText().equals("Facebook"));
		facebookCD.click();
		return this;
	}

	public CasePage insertValueCd(String value) {

		valueCD.click();
		valueCD.sendKeys(value);
		return this;
	}

	public CasePage insertDateCD(String date) {

		dateCD.click();
		dateCD.clear();
		dateCD.sendKeys(date);
		return this;
	}

	public CasePage verifyAddedByCd(String user) {

		Assert.assertTrue(addedByCD.getText().equals(user));
		return this;
	}

	public CasePage ClickNonProfitOi() {

		Assert.assertTrue(nonProfitOI.getText().equals("Non-profit"));
		nonProfitOI.click();
		return this;
	}

	public CasePage ClickGovernmentOi() {

		Assert.assertTrue(governmentOI.getText().equals("Government"));
		governmentOI.click();
		return this;
	}

	public CasePage ClickCorporationOi() {

		Assert.assertTrue(corporationOI.getText().equals("Corporation"));
		corporationOI.click();
		return this;
	}

	public CasePage insertNameOi(String name) {

		nameOI.click();
		nameOI.sendKeys(name);
		return this;
	}

	public CasePage insertDateOi(String date) {

		dateOI.click();
		dateOI.clear();
		dateOI.sendKeys(date);
		return this;
	}

	public CasePage verifyAddedByOi(String user) {

		Assert.assertTrue(addedbyOI.getText().equals(user));
		return this;
	}

	public CasePage insertAddressLi(String address) {

		addressLI.click();
		addressLI.sendKeys(address);
		return this;

	}

	public CasePage ClickbuisnessLi() {

		Assert.assertTrue(buisnessLI.getText().equals("Business"));
		buisnessLI.click();
		return this;
	}

	public CasePage CLickHomeLi() {

		Assert.assertTrue(homeLI.getText().equals("Home"));
		homeLI.click();
		return this;
	}

	public CasePage insertCityLi(String city) {

		cityLI.click();
		cityLI.sendKeys(city);
		return this;
	}

	public CasePage insertStateLi(String state) {

		stateLI.click();
		stateLI.sendKeys(state);
		return this;
	}

	public CasePage insertZipCodeLi(String zip) {

		zipCodeLI.click();
		zipCodeLI.sendKeys(zip);
		return this;
	}

	public CasePage insertStartDateLi(String date) throws InterruptedException {

		dateLI.click();
		Thread.sleep(2000);
		dateLI.clear();
		dateLI.sendKeys(date);
		return this;

	}

	public CasePage verifyAddedByLi(String user) {

		Assert.assertTrue(addedByLI.getText().equals(user));
		return this;
	}

	public CasePage clickTitlePeopleMr() {

		Assert.assertTrue(titlePeopleMr.getText().equals("Mr"));
		titlePeopleMr.click();
		return this;

	}

	public CasePage clickTitlePeopleMrs() {

		Assert.assertTrue(titlePeopleMrs.getText().equals("Mrs"));
		titlePeopleMrs.click();
		return this;

	}

	public CasePage clickTitlePeopleMs() {

		Assert.assertTrue(titlePeopleMs.getText().equals("Ms"));
		titlePeopleMs.click();
		return this;

	}

	public CasePage clickTitlePeopleMiss() {

		Assert.assertTrue(titlePeopleMiss.getText().equals("Miss"));
		titlePeopleMiss.click();
		return this;

	}

	public CasePage insertFirstNamePeople(String firstName) {

		firstNamePeople.click();
		firstNamePeople.sendKeys(firstName);
		return this;

	}

	public CasePage insertLastNamePeople(String lastName) {

		lastNamePeople.click();
		lastNamePeople.sendKeys(lastName);
		return this;
	}

	public CasePage typePeopleComplaintant() {

		Assert.assertTrue(typePComplaintant.getText().equals("Complaintant"));
		typePComplaintant.click();
		return this;

	}

	public CasePage typePeopleWitness() {

		Assert.assertTrue(typePWitness.getText().equals("Witness"));
		typePWitness.click();
		return this;

	}

	public CasePage typePeopleComHomePhone() {

		Assert.assertTrue(peopleComunicationHome.getText().equals("Home phone"));
		peopleComunicationHome.click();
		return this;

	}

	public CasePage typePeopleComWorkPhone() {

		Assert.assertTrue(peopleComunicationWork.getText().equals("Work phone"));
		peopleComunicationWork.click();
		return this;

	}

	public CasePage typePeopleComMobilePhone() {

		Assert.assertTrue(peopleComunicationMobile.getText().equals("Mobile"));
		peopleComunicationMobile.click();
		return this;

	}

	public CasePage typePeopleComEmail() {

		Assert.assertTrue(peopleComunicationEmail.getText().equals("Email"));
		peopleComunicationEmail.click();
		return this;

	}

	public CasePage typePeopleComFacebook() {

		Assert.assertTrue(peopleCominicationFacebook.getText().equals("Facebook"));
		peopleCominicationFacebook.click();
		return this;

	}

	public CasePage insertPeoplComValue(String value) {

		valuePcomDevice.click();
		valuePcomDevice.sendKeys(value);
		return this;
	}

	public CasePage insertPeopleComDate(String date) throws InterruptedException {

		datePcomDevice.click();
		Thread.sleep(2000);
		datePcomDevice.clear();
		datePcomDevice.sendKeys(date);
		return this;

	}

	public CasePage typePeopleOrganizationNonProfit() {

		Assert.assertTrue(peopleOrganizationNonProfit.getText().equals("Non-profit"));
		peopleOrganizationNonProfit.click();
		return this;
	}

	public CasePage typePeopleOrganizationGovernment() {

		Assert.assertTrue(peopleOrganizationGovernment.getText().equals("Goverment"));
		peopleOrganizationGovernment.click();
		return this;
	}

	public CasePage typePeopleOrganizationCorpoation() {

		Assert.assertTrue(peopleOrganizationNonProfit.getText().equals("Corporation"));
		peopleOrganizationCorporation.click();
		return this;
	}

	public CasePage insertPeopleNameOrganizationInformation() {

		nameOrganization.click();
		nameOrganization.sendKeys("Organization Information");
		return this;

	}

	public CasePage insertPeopleORganizationDate(String date) throws InterruptedException {

		datePOrganization.click();
		Thread.sleep(2000);
		datePOrganization.clear();
		datePOrganization.sendKeys(date);
		return this;

	}

	public CasePage typePeopleLocationBusiness() {

		Assert.assertTrue(peopleLocationBusiness.getText().equals("Business"));
		peopleLocationBusiness.click();
		return this;
	}

	public CasePage typePeopleLocationHome() {

		Assert.assertTrue(peopleLocationHome.getText().equals("Home"));
		peopleLocationHome.click();
		return this;
	}

	public CasePage insertPeopleLocationInfoAddress(String address) {

		adderessPeopleLocation.click();
		adderessPeopleLocation.sendKeys(address);
		return this;
	}

	public CasePage insertPeopleLocationInfoCity(String city) {

		cityPeopleLocationInfo.click();
		cityPeopleLocationInfo.sendKeys(city);
		return this;

	}

	public CasePage insertPeopleLocationInfoState(String state) {

		statePeopleLocatonInfo.click();
		statePeopleLocatonInfo.sendKeys(state);
		return this;

	}

	public CasePage inssertPeopleLocationInfoZip(String zip) {

		zipPeopleLocationInfo.click();
		zipPeopleLocationInfo.sendKeys(zip);
		return this;

	}

	public CasePage insertPeopleLocationInfoDate(String date) throws InterruptedException {

		datePeopleLocationInfo.click();
		Thread.sleep(2000);
		datePeopleLocationInfo.clear();
		datePeopleLocationInfo.sendKeys(date);
		return this;

	}

	public CasePage attachmentsAddFilesClickButton() {
		addFiles.click();
		return this;
	}

	public CasePage browseButtonClick() {

		BrowseButton.click();
		return this;
	}

	public CasePage addFile() throws IOException, AWTException {

		ArkCaseTestUtils.uploadPdf();
		return this;

	}

	public CasePage uploadButtonClick() {
		uploadButton.click();
		return this;
	}

	public CasePage selectParticipantTypeClick() {
		selectParticipantType.click();
		return this;
	}

	public CasePage selectparticipantOwner() throws InterruptedException {

		Assert.assertEquals("Owner name is wrong", "Owner", selectParticipantOwner.getText());
		selectParticipantOwner.click();
		Thread.sleep(4000);
		return this;

	}

	public CasePage selectParticipantClick() {
		selectParticipant.click();
		return this;
	}

	public CasePage searchForUsers() throws InterruptedException {

		searchForUsers.click();
		Thread.sleep(2000);
		searchForUsers.sendKeys("Samuel Supervisor");
		searchUserButton.click();
		return this;

	}

	public CasePage searchedName() {
		Assert.assertEquals("User name is wrong", "Samuel Supervisor", searchedName.getText().toString());
		searchedName.click();
		return this;

	}

	public CasePage addSearchedNameClick() {
		addSearchedName.click();
		return this;
	}

	public CasePage verifyInitiatorTab() {
		Assert.assertEquals("Initiator tab name is wrong", "Initiator", initiatorTab.getText());
		return this;
	}

	public CasePage verifyPeopleTab() {
		Assert.assertTrue(peopleTab.getText().equals("People"));
		return this;
	}

	public CasePage verifyAttachmentTab() {
		Assert.assertTrue(attachmentTab.getText().equals("Attachments"));
		return this;
	}

	public CasePage verifyParticipantTab() {
		Assert.assertTrue(participantnsTab.getText().equals("Participants"));
		return this;
	}

	public CasePage clickParticipantTypePlusBtn() {
		participantTypePlusBtn.click();
		return this;
	}

	public CasePage selectSecondTypeParticipant() {
		secondRowSelectParticipantType.click();
		return this;
	}

	public CasePage selectParticipantTypeFolower() {
		Assert.assertEquals("Folower label name is wrong", "Follower", selectParticipantFolower.getText());
		selectParticipantFolower.click();
		return this;
	}

	public CasePage clickSecondParticipant() {
		selectSecondParticipant.click();
		return this;

	}

	public CasePage selectParticipantFollower() {
		selectParticipantFollower.click();
		return this;
	}

	public CasePage CaseFilesMenuClick() {
		caseFilesMenu.click();
		return this;
	}

	public String parseCaseId() {
		String subcaseId = caseId.getText().substring(9, 12);
		return subcaseId;
	}

}
