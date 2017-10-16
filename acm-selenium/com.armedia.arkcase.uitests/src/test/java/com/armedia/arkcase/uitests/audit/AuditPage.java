package com.armedia.arkcase.uitests.audit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.WaitHelper;
import com.thoughtworks.selenium.webdriven.commands.WaitForPageToLoad;

public class AuditPage extends ArkCaseTestBase {

	@FindBy(how = How.XPATH, using = ".//*[@title='Audit']")
	WebElement ReportsLink;
	@FindBy(how = How.ID, using = "reportNameAudit")
	WebElement reportName;
	@FindBy(how = How.ID, using = "selectIDAudit")
	WebElement id;
	@FindBy(how = How.ID, using = "dateFromAudit")
	WebElement auditDateFrom;
	// cases by status
	@FindBy(how = How.ID, using = "dateToAudit")
	WebElement auditDateTo;
	@FindBy(how = How.XPATH, using = "//*[contains(text(),'Generate Audit Report')]")
	WebElement generateAuditReportButton;
	@FindBy(how = How.ID, using = "dijit_form_NumberTextBox_0")
	WebElement pageNumber;
	@FindBy(how = How.XPATH, using = ".//*[contains(@id, 'panel-')]/div[2]/select")
	WebElement outputType;
	@FindBy(how = How.ID, using = "reportContent")
	WebElement reportContent;
	@FindBy(how = How.XPATH, using = "//table")
	WebElement reportTable;
	@FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[2]")
    WebElement dateColumnHeader;
	@FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[3]")
    WebElement userColumnHeader;
	@FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[4]")
    WebElement nameColumnHeader;
	@FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[5]")
    WebElement resultColumnHeader;
	@FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[6]")
    WebElement ipaddressColumnHeader;
	@FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[7]")
    WebElement objectIdColumnHeader;
	@FindBy(how = How.XPATH, using ="//table/tbody/tr[2]/td[8]")
    WebElement objectTypeColumnHeader;	
	public AuditPage ReportsMenuClick() {
		WaitHelper.getWhenElementIsVisible(ReportsLink, 60, driver);
		ReportsLink.click();
		return this;
	}

	public AuditPage selectReportName(String reportname) {
		new Select(driver.findElement(By.id("reportNameAudit"))).selectByVisibleText(reportname);
		return this;
	}

	public AuditPage insertId(String idvalue) {
		id.click();
		id.clear();
		id.sendKeys(idvalue);
		return this;
	}

	public AuditPage insertDateFrom(String date) {

		auditDateFrom.click();
		auditDateFrom.clear();
		auditDateFrom.sendKeys(date);
		return this;

	}

	public AuditPage insertDateTo(String date) {

		auditDateTo.click();
		auditDateTo.clear();
		auditDateTo.sendKeys(date);
		return this;

	}

	public AuditPage generateAuditReportButtonClick() {

		generateAuditReportButton.click();
		return this;
	}

	public AuditPage generateAuditReport(String report, String idvalue, String datefrom, String dateto) {		
		selectReportName(report);
		insertId(idvalue);
		insertDateFrom(datefrom);
		insertDateTo(dateto);
		generateAuditReportButtonClick();		
		return this;
	}
	
	public AuditPage switchToAuditFrame(){
		driver.switchTo().frame("audit-iframe");
		return this;
	}
	public AuditPage switchToDefaultContent(){
		driver.switchTo().defaultContent();
		return this;
	}
	
	public AuditPage switchToReportContentFrame(){
		driver.switchTo().frame("reportContent");
		return this;
	}
	
	public String readDateColumnHeader()
	{
		WaitHelper.waitForElement(dateColumnHeader, driver);
		return dateColumnHeader.getText();
	}
	
	public String readUserColumnHeader()
	{
		return userColumnHeader.getText();
	}
	public String readNameColumnHeader()
	{
		return nameColumnHeader.getText();
	}
	
	public String readResultColumnHeader()
	{
		return resultColumnHeader.getText();
	}
	
	public String readIpAddressColumnHeader()
	{
		return ipaddressColumnHeader.getText();
	}
	public String readObjectIdColumnHeader()
	{
	   return objectIdColumnHeader.getText();
	}
	
	public String readObjectTypeColumnHeader()
	{
		return objectTypeColumnHeader.getText();
	}
	
}