package com.armedia.arkcase.uitests.dashboard;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;
import org.testng.asserts.SoftAssert;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;

public class DashboardPage extends ArkCaseTestBase {

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[1]/div/span/a/i")
	WebElement editButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[1]/div/span/a/i")
	WebElement addWidgetButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h4")
	WebElement addWidgettitle;
	// cases by status
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/dl/dt[1]/a")
	WebElement casesByStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[1]/div[1]/div[1]/div/h3")
	WebElement widgetTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[1]/div[1]/div[1]/div/h3/span/a[1]/i")
	WebElement widgetReload;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[1]/div[1]/div[1]/div/h3/span/a[2]/i")
	WebElement changeWidgetLocation;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[1]/div[1]/div[1]/div/h3/span/a[5]/i")
	WebElement editWidgetButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[1]/div[1]/div[1]/div/h3/span/a[7]/i")
	WebElement removeWidgetButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[1]/div/span/a[2]/i")
	WebElement editDashBoardButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[1]/div/span/a[3]/i")
	WebElement saveChangesButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[1]/div/span/a[4]/i")
	WebElement undoButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h4")
	WebElement casesByStatusTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/input")
	WebElement casesByStatusTitleInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement casesByStatusApply;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[1]/div[1]/div[2]/adf-widget-content/div/div/div/div[1]/p")
	WebElement widgetInsideTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/select")
	WebElement selectTimePeriod;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/select/option[2]")
	WebElement selectCasesLastWeek;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/select/option[3]")
	WebElement selectCasesLastMonth;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/select/option[4]")
	WebElement selectCasesLastYear;
	// my cases
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/dl/dt[2]/a")
	WebElement myCases;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h4")
	WebElement myCasesTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/input")
	WebElement myCasesTitleInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement myCasesApply;
	// my complaints
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/dl/dt[3]/a")
	WebElement myComplaints;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h4")
	WebElement myComplaintsTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/input")
	WebElement myComplaintsInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement myComplaintsApply;
	// my tasks
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/dl/dt[4]/a")
	WebElement myTasks;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h4")
	WebElement myTasksTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/input")
	WebElement myTaskInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement myTasksApply;
	// new complaints
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/dl/dt[5]/a")
	WebElement newComplaints;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h4")
	WebElement newComplaintsTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/input")
	WebElement newComplaintsInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement newComplaintsApply;
	// news
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/dl/dt[8]/a")
	WebElement news;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h4")
	WebElement newsTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/input")
	WebElement newsInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/input")
	WebElement feedUrlInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement newsApply;
	// weather
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/dl/dt[7]/a")
	WebElement weather;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h4")
	WebElement weatherTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/input")
	WebElement weatherTitleInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/input")
	WebElement weatherLocationInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement weatherApply;
	// Team Workload
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/dl/dt[6]/a")
	WebElement teamWorkload;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h4")
	WebElement teamWorkloadTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/input")
	WebElement teamWorkloadInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement teamWorkloadApply;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/select/option[2]")
	WebElement teamWorkloadPastDue;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/select/option[3]")
	WebElement teamWorkloadDueTommorow;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/select/option[4]")
	WebElement teamWorkloadDueInSevenDays;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/adf-widget-content/form/div/select/option[5]")
	WebElement teamWorkloadDueInMonth;
	// edit dashboard
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/input")
	WebElement dashboardTitleInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[2]/div[3]/label/input")
	WebElement radioButtonTripleFour;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button")
	WebElement closeEditDashboard;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[3]")
	WebElement checkThirdColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[2]/div[1]/label/input")
	WebElement radioButtonTwelve;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div")
	WebElement layoutTwelve;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[2]/div[2]/label/input")
	WebElement radioButtonSixSix;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[2]")
	WebElement layoutSixSix;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[2]")
	WebElement secondLayout;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[3]")
	WebElement thirdLayout;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[1]")
	WebElement layoutFourEight;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[2]/div[4]/label/input")
	WebElement radioButtonFourEight;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[2]/div[5]/label/input")
	WebElement radioButtonEightFour;

	public void editButtonClick() {

		editButton.click();
	}

	public void addNewWidgetButtonClik() {

		addWidgetButton.click();

	}

	public void casesByStatusClick() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(casesByStatus.getText(), "Cases By Status", "Cases by status name is wrong");
		softAssert.assertAll();
		casesByStatus.click();

	}

	public void verifyNewWidgetTitle(String title) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(widgetTitle.getText(), title, "Widget title is wrong");
		softAssert.assertAll();

	}

	public void verifyWidgetReloadButton() {

		Assert.assertTrue("Widget reload button is not displayed", widgetReload.isDisplayed());
		Assert.assertTrue("Widget reload button is not enabled", widgetReload.isEnabled());

	}

	public void widgetReloadButtonClick() {
		widgetReload.click();
	}

	public void verifyWidgetChangeLocationButton() {

		Assert.assertTrue("Widget change lcoation button is not displayed", changeWidgetLocation.isDisplayed());
		Assert.assertTrue("Widget change location button is not enabled", changeWidgetLocation.isEnabled());

	}

	public void widgetChangeLocationButtonClick() {
		changeWidgetLocation.click();
	}

	public void verifyWidgetEditButton() {

		Assert.assertTrue("Widget edit button is not displayed", editWidgetButton.isDisplayed());
		Assert.assertTrue("Widget edit button is not enabled", editWidgetButton.isEnabled());

	}

	public void widgetEditButtonClick() {
		editWidgetButton.click();
	}

	public void verifyWidgetRemoveButton() {

		Assert.assertTrue("Widget remove button is not displayed", removeWidgetButton.isDisplayed());
		Assert.assertTrue("Widget remove button is not enabled", removeWidgetButton.isEnabled());

	}

	public void widgetRemoveButtonClick() {
		removeWidgetButton.click();
	}

	public void editDashboardButtonClick() {

		editDashBoardButton.click();
	}

	public void saveChangesButtonClick() {

		saveChangesButton.click();
	}

	public void undoButtonClick() {

		undoButton.click();
	}

	public void verifyCasesByStatusTitle() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(casesByStatusTitle.getText(), "Cases By Status", "Cases by status title is wrong");
		softAssert.assertAll();
	}

	public void casesByStatusTitleInput(String newTitle) {

		casesByStatusTitleInput.click();
		casesByStatusTitleInput.clear();
		casesByStatusTitleInput.sendKeys(newTitle);

	}

	public void casesByStatusApplyClick() {

		casesByStatusApply.click();
	}

	public void verifyWidgetInsideTitle(String insideTitle) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(widgetInsideTitle.getText(), insideTitle, "Inside widget title is wrong");
		softAssert.assertAll();

	}

	public void selectCasesTimePeriodClick() {

		selectTimePeriod.click();

	}

	public void selectCasesLastWeek() throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(selectCasesLastWeek.getText(), "Last Week", "Last Week label name is wrong");
		softAssert.assertAll();
		new Select(driver.findElement(By.xpath("//form/div/select"))).selectByVisibleText("Last Week");
		

	}

	public void selectCasesLastMonth() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(selectCasesLastMonth.getText(), "Last Month", "Last Month label name is wrong");
		softAssert.assertAll();
		new Select(driver.findElement(By.xpath("//form/div/select"))).selectByVisibleText("Last Month");
		

	}

	public void selectCasesLastYear() {
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(selectCasesLastYear.getText(), "Last Year", "Last Year label name is wrong");
		softAssert.assertAll();
		new Select(driver.findElement(By.xpath("//form/div/select"))).selectByVisibleText("Last Year");
		

	}

	public void myCasesClick() {

		myCases.click();
	}

	public void verifyMyCasesTitle() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(myCasesTitle.getText(), "My Cases", "My cases title name is wrong");
		softAssert.assertAll();
	}

	public void myCasesTitleInput(String title) {

		myCasesTitle.click();
		myCasesTitleInput.clear();
		myCasesTitleInput.sendKeys(title);

	}

	public void myCasesApplyButtonClick() {

		myCasesApply.click();
	}

	public void myComplaintsClick() {

		myComplaints.click();

	}

	public void verifyMyComplaintsTitle() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(myComplaintsTitle.getText(), "My Complaints", "My complaints label name is wrong");
		softAssert.assertAll();

	}

	public void myComplaintsTitleInput(String title) {

		myComplaintsInput.click();
		myComplaintsInput.clear();
		myComplaintsInput.sendKeys(title);

	}

	public void myComplaintsApplyButtonClick() {

		myComplaintsApply.click();

	}

	public void myTaskClick() {

		myTasks.click();

	}

	public void verifyMyTasksTitle() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(myTasksTitle.getText(), "My Tasks", "My Tasks title name is wrong");
		softAssert.assertAll();

	}

	public void myTasksTitleInput(String title) {

		myTaskInput.click();
		myTaskInput.clear();
		myTaskInput.sendKeys(title);

	}

	public void myTasksApplyButtonClick() {

		myTasksApply.click();

	}

	public void newComplaintsClick() {

		newComplaints.click();

	}

	public void verifyNewComplaintsTitle() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(newComplaintsTitle.getText(), "New Complaints", "New Complaints title is wrong");
		softAssert.assertAll();

	}

	public void newComplaintsTitleInput(String title) {

		newComplaintsInput.click();
		newComplaintsInput.clear();
		newComplaintsInput.sendKeys(title);

	}

	public void newComplaintsApplyButtonClick() {

		newComplaintsApply.click();

	}

	public void newsClick() {

		news.click();

	}

	public void verifyNewsTitle() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(newsTitle.getText(), "News", "News title name is wrong");
		softAssert.assertAll();

	}

	public void newsInput(String title) {

		newsInput.click();
		newsInput.clear();
		newsInput.sendKeys(title);

	}

	public void feedUrlInput(String title) {

		feedUrlInput.click();
		feedUrlInput.clear();
		feedUrlInput.sendKeys(title);
	}

	public void newsApplyButtonClick() {

		newsApply.click();

	}

	public void weatherClick() {

		weather.click();

	}

	public void verifyWeatherTitle() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(weatherTitle.getText(), "Weather", "Weather title name is wrong");
		softAssert.assertAll();

	}

	public void weatherTitleInput(String title) {

		weatherTitleInput.click();
		weatherTitleInput.clear();
		weatherTitleInput.sendKeys(title);

	}

	public void weatherLocationInput(String title) {

		weatherLocationInput.click();
		weatherLocationInput.clear();
		weatherLocationInput.sendKeys(title);
	}

	public void weatherApplyButtonClick() {

		weatherApply.click();

	}

	public void teamWorkloadClick() {

		Assert.assertEquals("Team Workload link text is wrong", "Team Workload", teamWorkload.getText());

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(teamWorkload.getText(), "Team Workload", "Team Workload link text  is wrong");
		softAssert.assertAll();

		teamWorkload.click();

	}

	public void verifyTeamWorkloadTitle() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(teamWorkloadTitle.getText(), "Team Workload", "Team Workload title popup is wrong");
		softAssert.assertAll();

	}

	public void teamWorkloadInput(String title) {

		teamWorkloadInput.click();
		teamWorkloadInput.clear();
		teamWorkloadInput.sendKeys(title);
	}

	public void teamWorkLoadApplyClick() {

		teamWorkloadApply.click();

	}

	public void teamWorkloadSelectPastDue() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(teamWorkloadPastDue.getText(), "Past Due", "Past due label name is wrong");
		softAssert.assertAll();
		new Select(driver.findElement(By.xpath("//form/div/select"))).selectByVisibleText("Past Due");
		teamWorkloadPastDue.click();

	}

	public void teamWorkloadSelectDueTomorrow() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(teamWorkloadDueTommorow.getText(), "Due Tomorrow", "Due Tommorw label name is wrong");
		softAssert.assertAll();
		new Select(driver.findElement(By.xpath("//form/div/select"))).selectByVisibleText("Due Tomorrow");
		teamWorkloadDueTommorow.click();

	}

	public void teamWorkloadSelectDueInSevenDays() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(teamWorkloadDueInSevenDays.getText(), "Due in 7 Days",
				"Due in 7 days label name is wrong");
		softAssert.assertAll();
		new Select(driver.findElement(By.xpath("//form/div/select"))).selectByVisibleText("Due in 7 Days");
		teamWorkloadDueInSevenDays.click();

	}

	public void teamWorkloadSelectDueInMonth() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(teamWorkloadDueInMonth.getText(), "Due in Month", "Due in Month label name is wrong");
		softAssert.assertAll();

		new Select(driver.findElement(By.xpath("//form/div/select"))).selectByVisibleText("Due in Month");
		teamWorkloadDueInMonth.click();

	}

	public void editDashboadrCloseButtonClick() {

		closeEditDashboard.click();

	}

	public void checkThirdColumnIsDisplayed() {

		Assert.assertTrue("Third column in the dashboard is not displayed", checkThirdColumn.isDisplayed());

	}

	public void checkTripleFourStructureIsSaved() {

		int i = driver
				.findElements(
						By.xpath("/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[2]/div/div[3]"))
				.size();
		if (i == 0) {
			assertTrue(true);
		} else {
			assertTrue(false);
		}

	}

	public void verifyLayoutTwelveIsDisplayed() {

		Assert.assertTrue("Layout twelve is not displayed", layoutTwelve.isDisplayed());

	}

	public void verifyLayoutSixSixIsDisplayed() {

		Assert.assertTrue("Lay put six is not displayed", layoutSixSix.isDisplayed());
	}

	public void verifySecondLayout() {

		Assert.assertTrue("Second dashboard layout is not displayed", secondLayout.isDisplayed());

	}

	public void verifyThirdLayout() {

		Assert.assertTrue("Third dsahboard layout is not displayed", thirdLayout.isDisplayed());

	}

	public void verifyFourEightLayout() {

		Assert.assertTrue("Layout 4 of 4-8 is not displayed", layoutFourEight.isDisplayed());
		Assert.assertTrue("Layout 8 of 4-8 is not displayed", secondLayout.isDisplayed());

	}

	public void verifyEightFour() {

		Assert.assertTrue("Layout 8 of 4-8 is not displayed", secondLayout.isDisplayed());

		Assert.assertTrue("Layout 4 of 4-8 is not displayed", layoutFourEight.isDisplayed());

	}
}
