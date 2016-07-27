package com.armedia.arkcase.uitests.audit;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;
import org.testng.asserts.SoftAssert;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.reports.ReportsPage;

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
	
	public AuditPage ReportsMenuClick() {

		ReportsLink.click();
		return this;
	}

	public AuditPage selectReportName(String reportname){
	    new Select(driver.findElement(By.id("reportNameAudit"))).selectByVisibleText(reportname);
	    return this;
	}
	
	public AuditPage insertId(String idvalue){
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
	
	public AuditPage generateAuditReport(String report, String idvalue, String datefrom, String dateto){
		selectReportName(report);
		insertId(idvalue);
		insertDateFrom(datefrom);
		insertDateTo(dateto);
		generateAuditReportButtonClick();
		return this;
	}
}