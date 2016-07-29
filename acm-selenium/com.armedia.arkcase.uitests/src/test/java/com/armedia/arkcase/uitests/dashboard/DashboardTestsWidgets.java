package com.armedia.arkcase.uitests.dashboard;

import java.awt.AWTException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.support.PageFactory;
import org.testng.asserts.SoftAssert;
import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.group.SmokeTests;

public class DashboardTestsWidgets extends ArkCaseTestBase {

	DashboardPage dash = PageFactory.initElements(driver, DashboardPage.class);

	@Test
	public void addEditRemoveNewWidgetCassesByStatusAll() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(4000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		dash.casesByStatusClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Cases By Status");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Cases By Status");
		dash.verifyWidgetInsideTitle("All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyCasesByStatusTitle();
		dash.casesByStatusTitleInput("Cases by status All");
		dash.casesByStatusApplyClick();
		Thread.sleep(4000);
		dash.verifyNewWidgetTitle("Cases by status All");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Cases by status All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("Cases by status All"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void addEditRemoveNewWidgetCasesByStatusLastWeek() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.casesByStatusClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Cases By Status");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Cases By Status");
		dash.verifyWidgetInsideTitle("All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyCasesByStatusTitle();
		dash.casesByStatusTitleInput("Cases by status Last Week");
		dash.selectCasesTimePeriodClick();
		Thread.sleep(2000);
		dash.selectCasesLastWeek();
		Thread.sleep(4000);
		dash.casesByStatusApplyClick();
		Thread.sleep(5000);
		dash.verifyNewWidgetTitle("Cases by status Last Week");
		dash.verifyWidgetInsideTitle("Last Week");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Cases by status Last Week");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("Cases by status Last Week"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetCassesByStatusLastMonth() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.casesByStatusClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Cases By Status");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Cases By Status");
		dash.verifyWidgetInsideTitle("All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyCasesByStatusTitle();
		dash.casesByStatusTitleInput("Cases by status Last Month");
		dash.selectCasesTimePeriodClick();
		Thread.sleep(2000);
		dash.selectCasesLastMonth();
		Thread.sleep(4000);
		dash.casesByStatusApplyClick();
		Thread.sleep(5000);
		dash.verifyNewWidgetTitle("Cases by status Last Month");
		dash.verifyWidgetInsideTitle("Last Month");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Cases by status Last Month");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed",
				dash.widgetTitle.getText().equals("Cases by status Last Month"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void addEditRemoveNewWidgetCassesByStatusLastYear() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.casesByStatusClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Cases By Status");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Cases By Status");
		dash.verifyWidgetInsideTitle("All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyCasesByStatusTitle();
		dash.casesByStatusTitleInput("Cases by status Last Year");
		dash.selectCasesTimePeriodClick();
		Thread.sleep(2000);
		dash.selectCasesLastYear();
		Thread.sleep(4000);
		dash.casesByStatusApplyClick();
		Thread.sleep(5000);
		dash.verifyNewWidgetTitle("Cases by status Last Year");
		dash.verifyWidgetInsideTitle("Last Year");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Cases by status Last Year");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("Cases by status Last Year"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void addEditRemoveNewWidgetMyCases() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.myCasesClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("My Cases");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("My Cases");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyMyCasesTitle();
		dash.myCasesTitleInput("My My cases");
		dash.myCasesApplyButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("My My cases");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("My My cases");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("My My cases"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetMyComplaints() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.myComplaintsClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("My Complaints");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("My Complaints");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyMyComplaintsTitle();
		dash.myComplaintsTitleInput("my complaints");
		dash.myComplaintsApplyButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("my complaints");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("my complaints");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("my complaints"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetMyTasks() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.myTaskClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("My Tasks");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("My Tasks");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyMyTasksTitle();
		dash.myTasksTitleInput("my tasks");
		dash.myTasksApplyButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("my tasks");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("my tasks");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("my tasks"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetNewComplaints() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.newComplaintsClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("New Complaints");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("New Complaints");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyNewComplaintsTitle();
		dash.newComplaintsTitleInput("new complaints");
		dash.newComplaintsApplyButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("new complaints");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("new complaints");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("new complaints"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetTeamWorkLoadAll() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.teamWorkloadClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetInsideTitle("All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyTeamWorkloadTitle();
		dash.teamWorkloadInput("team workload");
		dash.teamWorkLoadApplyClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("team workload");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("team workload");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("team workload"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetTeamWorkLoadPastDue() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.teamWorkloadClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetInsideTitle("All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyTeamWorkloadTitle();
		dash.teamWorkloadInput("Past Due");
		Thread.sleep(2000);
		dash.teamWorkloadSelectPastDue();
		dash.teamWorkLoadApplyClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Past Due");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Past Due");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("Past Due"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetTeamWorkLoadDueTomorrow() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.teamWorkloadClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetInsideTitle("All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyTeamWorkloadTitle();
		dash.teamWorkloadInput("Due Tomorrow");
		Thread.sleep(2000);
		dash.teamWorkloadSelectDueTomorrow();
		dash.teamWorkLoadApplyClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Due Tomorrow");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Due Tomorrow");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("Due Tomorrow"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetTeamWorkLoadDueIn7Days() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.teamWorkloadClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetInsideTitle("All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyTeamWorkloadTitle();
		dash.teamWorkloadInput("Due in 7 days");
		dash.teamWorkloadSelectDueInSevenDays();
		dash.teamWorkLoadApplyClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Due in 7 days");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Due in 7 days");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("Due in 7 days"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetTeamWorkLoadDueInMonth() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.teamWorkloadClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Team Workload");
		dash.verifyWidgetInsideTitle("All");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyTeamWorkloadTitle();
		dash.teamWorkloadInput("Due in Month");
		dash.teamWorkloadSelectDueInMonth();
		dash.teamWorkLoadApplyClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Due in Month");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Due in Month");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("Due in Month"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void addEditRemoveNewWidgetWeather() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.weatherClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("Weather");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("Weather");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyWeatherTitle();
		dash.weatherTitleInput("weather weather");
		dash.weatherLocationInput("skopje");
		dash.newsApplyButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("weather weather");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("weather weather");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("weather weather"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void addEditRemoveNewWidgetNews() throws InterruptedException, IOException, AWTException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.addNewWidgetButtonClik();
		Thread.sleep(2000);
		softAssert.assertEquals(dash.addWidgettitle.getText(), "Add new widget", "Add new widget title is wrong");
		softAssert.assertAll();
		dash.newsClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("News");
		dash.verifyWidgetReloadButton();
		dash.verifyWidgetEditButton();
		dash.verifyWidgetChangeLocationButton();
		dash.verifyWidgetRemoveButton();
		dash.saveChangesButtonClick();
		Thread.sleep(3000);
		dash.verifyNewWidgetTitle("News");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetEditButtonClick();
		Thread.sleep(2000);
		dash.verifyNewsTitle();
		dash.newsInput("BBC");
		dash.feedUrlInput("http://feeds.bbci.co.uk/news/world/rss.xml?edition=uk");
		dash.newsApplyButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("BBC");
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyNewWidgetTitle("BBC");
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.widgetRemoveButtonClick();
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		Assert.assertFalse("The widget is not removed", dash.widgetTitle.getText().equals("BBC"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	@Category({ SmokeTests.class })
	public void changeDashboardLayoutTwelve() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonTwelve.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		dash.verifyLayoutTwelveIsDisplayed();
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.editDashboardButtonClick();
		dash.radioButtonSixSix.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyLayoutSixSixIsDisplayed();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	@Category({ SmokeTests.class })
	public void changeDashboardLayoutSixSix() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonSixSix.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		dash.verifyLayoutSixSixIsDisplayed();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	@Category({ SmokeTests.class })
	public void changeDashboardLayoutFourFourFour() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonTripleFour.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		dash.verifySecondLayout();
		dash.verifyThirdLayout();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	@Category({ SmokeTests.class })
	public void changeDashboardLayoutFourEight() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonFourEight.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyFourEightLayout();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	@Category({ SmokeTests.class })
	public void changeDashboardLayoutEightFour() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonEightFour.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyEightFour();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void changeDashboardUndoButton() throws InterruptedException, IOException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonTripleFour.click();
		Thread.sleep(2000);
		dash.editDashboadrCloseButtonClick();
		Thread.sleep(2000);
		dash.checkThirdColumnIsDisplayed();
		Thread.sleep(2000);
		dash.undoButtonClick();
		Thread.sleep(2000);
		dash.checkTripleFourStructureIsSaved();
		ArkCaseAuthentication.logOut(driver);
	}
}
