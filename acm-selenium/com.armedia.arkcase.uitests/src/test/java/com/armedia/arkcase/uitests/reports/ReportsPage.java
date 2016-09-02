package com.armedia.arkcase.uitests.reports;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.armedia.arkcase.uitests.audit.AuditPage;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.WaitHelper;
import com.thoughtworks.selenium.webdriven.commands.WaitForCondition;

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

	public ReportsPage ReportsMenuClick() {
       WaitHelper.clickWhenElelementIsClickable(ReportsLink, 30, driver);	   
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
		WaitHelper.clickWhenElelementIsClickable(selectReport, 30, driver);
		selectReport(report);
		selectState(state);
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

}
