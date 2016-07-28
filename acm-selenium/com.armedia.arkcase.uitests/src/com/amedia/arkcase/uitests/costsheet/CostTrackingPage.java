package com.amedia.arkcase.uitests.costsheet;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.asserts.SoftAssert;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.HttpResponseCode;

public class CostTrackingPage extends ArkCaseTestBase {

	HttpResponseCode http = new HttpResponseCode();

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[1]/h3/span")
	WebElement costTrackingTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[1]/div/input")
	WebElement searchCostsheetInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[1]/div/span/button")
	WebElement searchCostsheetBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[2]/button")
	WebElement refreshCostListBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div/div/div/a[1]")
	WebElement newCostsheetBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div/div/div/a[2]")
	WebElement editCostsheetBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div/div/div/button")
	WebElement refreshPageBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[2]/a")
	WebElement detailsLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[3]/a")
	WebElement personLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[4]")
	WebElement expensesLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[5]/a")
	WebElement tagsLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[1]/a")
	WebElement overviewLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/span/span[1]")
	WebElement firstCostsheetExpander;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/ul/li[1]/span/span[3]")
	WebElement listDetail;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/ul/li[2]/span/span[3]")
	WebElement listPerson;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/ul/li[3]/span/span[3]")
	WebElement expensesList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/ul/li[4]/span/span[3]")
	WebElement tagsList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[3]/button")
	WebElement sortButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[3]/ul/li[2]/a")
	WebElement sortDateDesc;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/span")
	WebElement firstCostsheet;
	// information ribbon
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[6]/small/span")
	WebElement stateLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[6]/div")
	WebElement stateValue;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[3]/small/span")
	WebElement priorityLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[3]/div")
	WebElement priorityValue;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[5]/small/span")
	WebElement subjectTypeLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[5]/div")
	WebElement subjectTypeValue;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[2]/small/span")
	WebElement incidentDateLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[2]/div")
	WebElement incidentDateValue;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[4]/small/span")
	WebElement assidnedToLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[4]/div")
	WebElement assignedToValue;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[1]/a/small")
	WebElement typeNumberValue;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div/div[1]/a/div")
	WebElement typeTitleValue;
	// Details section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div[4]/div[4]")
	WebElement detailsTextArea;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/div/button")
	WebElement detailsSaveBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]")
	WebElement detailsTableTitle;

	// Person section

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/span")
	WebElement personTabTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement fullNameColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement userNameColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement fullName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement userName;

	// Cost summary section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/span")
	WebElement costSummaryTabTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement parentIDcolumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement parentTypeColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement totalCostColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement titleColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[5]/div[2]/div[1]/span[1]")
	WebElement descriptionColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]")
	WebElement parentID;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement parentType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement totalCost;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement title;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/div")
	WebElement description;
	// tags section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/span")
	WebElement tagsTabTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/div/button")
	WebElement addTagBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement tagColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement createdColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement createdByColumnName;

	public void verifyCostTrackingTitle() {

		Assert.assertEquals("Cost tracing title is wrong", "Cost Tracking", costTrackingTitle.getText());
	}

	public void verifyInformationRibbon(String typeTitle, String typeID, String state, String priority, String assignTo,
			String subjectType) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(stateLabel.getText(), "State", "State label name is wrong");
		softAssert.assertEquals(stateValue.getText(), state, "State value is wrong");
		softAssert.assertEquals(priorityLabel.getText(), "Priority", "Priority label name is wrong");
		softAssert.assertEquals(priorityValue.getText(), priority, "Priority value is wrong");
		softAssert.assertEquals(incidentDateLabel.getText(), "Incident Date", "Incident date label name is wrong");
		softAssert.assertEquals(incidentDateValue.getText(), createdDate, "Incident date is wrong");
		softAssert.assertEquals(assignedToValue.getText(), assignTo, "Assighn To username is wrong");
		softAssert.assertEquals(subjectTypeValue.getText(), subjectType, "Subject type is wrong");
		softAssert.assertEquals(typeNumberValue.getText(), typeID, "Type id number is wrong");
		softAssert.assertEquals(typeTitleValue.getText(), typeTitle, "Type title is wrong");
		softAssert.assertAll();
	}

	public void verifyButtons() {
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(newCostsheetBtn.isDisplayed(), "New costsheet button is not displayed");
		softAssert.assertTrue(editCostsheetBtn.isDisplayed(), "Edit costsheet button is not displayed");
		softAssert.assertTrue(refreshPageBtn.isDisplayed(), "Refresh page button is not displayed");
		softAssert.assertTrue(refreshCostListBtn.isDisplayed(), "Refresh cost list button is not displayed");
		softAssert.assertAll();

	}

	public void clickFirsCostsheetInList() {
		firstCostsheet.click();
	}

	public void clickDetailsLink() {
		detailsLink.click();
	}

	public void verifyDetailsSection() {
		Assert.assertEquals("Details table title is wrong", "Details", detailsTableTitle.getText());
	}

	public void verifyDetailsTextArea(String text) {
		Assert.assertEquals("Details text is not shown", text, detailsTextArea.getText());
	}

	public void verifyPersonTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(personTabTitle.getText(), "Person", "Person table title is wrong");
		softAssert.assertEquals(fullNameColumnName.getText(), "Full Name", "Full name column name is wrong");
		softAssert.assertEquals(userNameColumnName.getText(), "Username", "Username column name is wrong");
		softAssert.assertAll();

	}

	public void verifyIfPersonIsShown() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();
		Assert.assertTrue("Person added is not shown in the person table", i != 0);

	}

	public void verifyAddedPerson(String fullName, String username) {
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(this.fullName.getText(), fullName, "Person full name is wrong");
		softAssert.assertEquals(userName.getText(), username, "Person username is wrong");
		softAssert.assertAll();
	}

	public void clickPersonLink() {
		personLink.click();
	}

	public void clickCostSummaryLink() {
		expensesLink.click();
	}

	public void verifyCostSummaryTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(costSummaryTabTitle.getText(), "Cost Summary", "Cost summary table title is wrong");
		softAssert.assertEquals(parentIDcolumnName.getText(), "Parent ID", "Parent ID column name is wrong");
		softAssert.assertEquals(parentTypeColumnName.getText(), "Parent Type", "Parent Type column name is wrong");
		softAssert.assertEquals(totalCostColumnName.getText(), "Total Cost", "Total Cost column name is wrong");
		softAssert.assertEquals(titleColumnName.getText(), "Title", "Title column name is wrong");
		softAssert.assertEquals(descriptionColumnName.getText(), "Description", "Description column name is wrong ");
		softAssert.assertAll();

	}

	public void verifyCostsheetValuesInCostSummaryTable(String parentType, String totalCost, String title,
			String description) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(this.parentType.getText(), parentType, "Parent type value is wrong");
		softAssert.assertEquals(this.totalCost.getText(), totalCost, "Total cost value is wrong");
		softAssert.assertEquals(this.title.getText(), title, "Title value is wrong");
		softAssert.assertEquals(this.description.getText(), description, "Description value is wrong");
		softAssert.assertAll();

	}

	public void detailsInput(String text) throws InterruptedException {

		detailsTextArea.clear();
		Thread.sleep(2000);
		detailsTextArea.sendKeys(text);

	}

	public void clickDetailsSaveBtn() {
		detailsSaveBtn.click();
	}

	public void verifyUpdatedDetailsTextArea(String text) {
		Assert.assertEquals("After refresh,Details text area is not updated ", text, detailsTextArea.getText());
	}

	public void clickSortButton() {
		sortButton.click();
	}

	public void clickSortDateDesc() {
		Assert.assertEquals("Sort date desc name is wrong", "Sort Date Decending", sortDateDesc.getText());
		sortDateDesc.click();
	}

	public void clickFirstCostsheet() {
		firstCostsheet.click();
	}

}
