package com.armedia.arkcase.uitests.reports;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.WaitHelper;

public class ReportsPage extends ArkCaseTestBase {

	@FindBy(how = How.XPATH, using = ".//*[@title='Reports']")
	WebElement ReportsLink;
	@FindBy(how = How.ID, using = "selectionReports")
	WebElement selectReport;
	@FindBy(how = How.ID, using = "selectStateReports")
	WebElement selectState;
	@FindBy(how = How.ID, using = "dateFromReports")
	WebElement reportDateFrom;
	// cases by status
	@FindBy(how = How.ID, using = "dateToReports")
	WebElement reportDateTo;
	@FindBy(how = How.XPATH, using = "//*[contains(text(),'Generate report')]")
	WebElement generateReportButton;
	@FindBy(how = How.ID, using = "dijit_form_NumberTextBox_0")
	WebElement pageNumber;
	@FindBy(how = How.XPATH, using = ".//*[contains(@id, 'panel-')]/div[2]/select")
	WebElement outputType;
	@FindBy(how = How.ID, using = "reportContent")
	WebElement reportContent;
	@FindBy(how = How.XPATH, using = "//table")
	WebElement reportTable;
	@FindBy(how = How.XPATH, using = "//table/tbody/tr[2]/td[3]")
	WebElement caseNumberColumnHeader;
	@FindBy(how = How.XPATH, using = "//table/tbody/tr[2]/td[4]")
	WebElement statusColumnHeader;
	@FindBy(how = How.XPATH, using = "//table/tbody/tr[2]/td[5]")
	WebElement titleColumnHeader;
	@FindBy(how = How.XPATH, using = "//table/tbody/tr[2]/td[6]")
	WebElement incidentDateColumnHeader;
	@FindBy(how = How.XPATH, using = "//table/tbody/tr[2]/td[7]")
	WebElement priorityColumnHeader;
	@FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[8]")
	WebElement dueDateColumnHeader;
	@FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[9]")
	WebElement typeColumnHeader;	
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[1]/td[2]")
    WebElement dispositionTitle;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[1]/td[3]")
    WebElement countTitle;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[3]/td[2]")
    WebElement addToExistingCaseLabel;
    @FindBy(how = How.XPATH, using = "//table/tbody/tr[5]/td[2]")
    WebElement noFurtherActionLabel;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[7]/td[2]")
    WebElement openInvestigationLabel;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[9]/td[2]")
    WebElement referExternalLabel;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[4]/td[3]")
    WebElement addToExistingCaseValue;
    @FindBy(how = How.XPATH, using = "//table/tbody/tr[6]/td[3]")
    WebElement noFurtherActionValue;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[8]/td[3]")
    WebElement openInvestigationValue;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[10]/td[3]")
    WebElement referExternalValue;
    @FindBy(how = How.XPATH, using = "//table/tbody/tr[11]/td/img")
    WebElement complaintDispositionGraph;
    @FindBy(how = How.XPATH, using = "//table/tbody/tr[1]/td[1]")
    WebElement complaintReportTitle;
    @FindBy(how = How.XPATH, using = "//table/tbody/tr[2]/td[3]")
    WebElement complaintComColumnHeader;
    @FindBy(how = How.XPATH, using = "//table/tbody/tr[2]/td[4]")
    WebElement statusComColumnHeader;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[5]")
    WebElement typeComColumnHeader;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[6]")
    WebElement priorityComColumnHeader;
    @FindBy(how = How.XPATH, using = "//table/tbody/tr[2]/td[7]")
    WebElement createDateComColumnHeader;
    @FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[8]")
    WebElement incidentDateComColumnHeader;
    
	public ReportsPage ReportsMenuClick() {
       WaitHelper.clickWhenElelementIsClickable(ReportsLink, 60, driver);	   
	return this;
	}

	public ReportsPage selectReport(String report) {
		new Select(driver.findElement(By.id("selectionReports"))).selectByVisibleText(report);		
		return this;
	}

	public ReportsPage selectState(String state) {
		new Select(driver.findElement(By.id("selectStateReports"))).selectByVisibleText(state);
		return this;
	}

	public ReportsPage insertDateFrom(String date) {

		reportDateFrom.click();
		reportDateFrom.clear();
		reportDateFrom.sendKeys(date);
		return this;

	}

	public ReportsPage insertDateTo(String date) {

		reportDateTo.click();
		reportDateTo.clear();
		reportDateTo.sendKeys(date);
		return this;

	}

	public ReportsPage generateReportButtonClick() {

		generateReportButton.click();
		return this;
	}

	public ReportsPage generateReport(String report, String state, String datefrom, String dateto) {
		selectReport(report);
		selectState(state);
		insertDateFrom(datefrom);
		insertDateTo(dateto);
		generateReportButtonClick();
		return this;
	}
	

	public ReportsPage generateCDCReport(String report, String datefrom, String dateto) {
		selectReport(report);		
		insertDateFrom(datefrom);
		insertDateTo(dateto);
		generateReportButtonClick();
		return this;
	}
	
	
	public ReportsPage switchToReportFrame(){
		driver.switchTo().frame("reports-iframe");
		return this;
	}
	public ReportsPage switchToDefaultContent(){
		driver.switchTo().defaultContent();
		return this;
	}
	public ReportsPage switchToReportContentFrame(){
		driver.switchTo().frame("reportContent");
		return this;
	}
	public String readCaseNumberColumnHeader()
	{
		WaitHelper.waitForElement(caseNumberColumnHeader, driver);
		return caseNumberColumnHeader.getText();
	}
	public String readStatusColumnHeader()
	{
		return statusColumnHeader.getText();
		
	}
	public String readTitleColumnHeader()
	{
		return titleColumnHeader.getText();
	}
	public String readIncidentDateColumnHeader()
	{
		return incidentDateColumnHeader.getText();
	}
	public String readPriorityColumnHeader()
	{
		return priorityColumnHeader.getText();
	}
	public String readDueDateColumnHeader()
	{
		return dueDateColumnHeader.getText();
	}
	public String readTypecolumnHeader()
	{
		return typeColumnHeader.getText();
	}	
	
	public String readDispositionTitle()
	{
		return dispositionTitle.getText();
	}
	
	public String readCountTitle()
	{
		return countTitle.getText();
	}
	
	public String readAddToExistingCaseLabel()
	{
		return addToExistingCaseLabel.getText();
	}
	
	public String readNoFurtherActionLabel()
	{
		return noFurtherActionLabel.getText();
	}
	
	public String readOpenInvestigationLabel()
	{
		return openInvestigationLabel.getText();
	}
	
	public String readReferExternalLabel()
	{
		return referExternalLabel.getText();
	}
	
	public String readAddToExistingCaseValue()
	{
		return addToExistingCaseValue.getText();
	}
	
	public String readNoFurtherActionValue()
	{
		return noFurtherActionValue.getText();
	}
	
	public String readOpenInvestigationValue()
	{
		return openInvestigationValue.getText();
	}
	
	public String readReferExternalValue()
	{
		return referExternalValue.getText();
	}
	
	public Boolean isAddToExistingCaseValueDisplayed()
	{
		return addToExistingCaseValue.isDisplayed();
	}
	
	public Boolean isNoFurtherActionValueDisplayed()
	{
		return noFurtherActionValue.isDisplayed();
	}
	
	public Boolean isOpenInvestigationValueDisplayed()
	{
		return openInvestigationValue.isDisplayed();
	}
	
	public Boolean isReferExternalValueDisplayed()
	{
		return referExternalValue.isDisplayed();
	}
	
	public Boolean isComplaintDispositionGraphDisplayed()
	{
		return complaintDispositionGraph.isDisplayed();
	}
	
	
	
	

}
