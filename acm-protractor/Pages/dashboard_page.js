var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var SelectWrapper = require('../util/select-wrapper.js');
var EC = protractor.ExpectedConditions;
var editBtn = element(by.xpath(Objects.dashboardpage.locators.editBtn));
var addNewWidgetBtn = element(by.xpath(Objects.dashboardpage.locators.addNewWidgetBtn));
var editDashboardBtn = element(by.xpath(Objects.dashboardpage.locators.editDashboardBtn));
var saveChangesBtn = element(by.xpath(Objects.dashboardpage.locators.saveChangesBtn));
var undoChangesBtn = element(by.xpath(Objects.dashboardpage.locators.undoChangesBtn));
var addNewWidgetTitle = element(by.css(Objects.dashboardpage.locators.addNewWidgetTitle));
var casesByStatus = element(by.linkText(Objects.dashboardpage.locators.casesByStatus));
var myCases = element(by.linkText(Objects.dashboardpage.locators.myCases));
var myComplaints = element(by.linkText(Objects.dashboardpage.locators.myComplaints));
var newComplaints = element(by.linkText(Objects.dashboardpage.locators.newComplaints));
var teamWorkload = element(by.linkText(Objects.dashboardpage.locators.teamWorkload));
var weather = element(by.linkText(Objects.dashboardpage.locators.weather));
var news = element(by.linkText(Objects.dashboardpage.locators.news));
var closeBtn = element(by.buttonText(Objects.dashboardpage.locators.closeBtn));
var widgetTitle = element.all(by.xpath(Objects.dashboardpage.locators.widgetTitle)).get(0);
var reloadWidgetContentBtn = element(by.xpath(Objects.dashboardpage.locators.reloadWidgetContentBtn));
var chnageWidgetLocationBtn = element(by.xpath(Objects.dashboardpage.locators.chnageWidgetLocationBtn));
var editWidgetConfigurationBtn = element(by.xpath(Objects.dashboardpage.locators.editWidgetConfigurationBtn));
var removeWidgetBtn = element.all(by.xpath(Objects.dashboardpage.locators.removeWidgetBtn)).get(0);
var myTasks = element(by.linkText(Objects.dashboardpage.locators.myTasks));
var dashboardTitleInput = element(by.id(Objects.dashboardpage.locators.dashboardTitleInput));
var dashboardTitle = element(by.xpath(Objects.dashboardpage.locators.dashboardTitle));
var pageSize = new SelectWrapper(by.model(Objects.dashboardpage.locators.pageSize));
var itemesPerPage = element(by.model(Objects.dashboardpage.locators.pageSize));





var DashboardPage = function() {
    this.clickEditButton = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.dashboardpage.locators.editBtn))), 30000, "Edit button in the dashboard page is not displayed").then(function() {
            editBtn.click();
        });
        return this;
    };
    this.clickAddWidgetButton = function() {
        addNewWidgetBtn.click();
        return this;
    }
    this.returnWidgetTitle = function() {
        return widgetTitle.getText();
    }
    this.removeWidgetButton = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.dashboardpage.locators.removeWidgetBtn))), 10000, "remove widget button in the dashboard page is not displayed").then(function() {
            removeWidgetBtn.click();
        });
        return this;
    }
    this.clickSaveChangesButton = function() {
        browser.executeScript('arguments[0].click()', saveChangesBtn);
        return this;
    }
    this.addWidget = function(type) {
        switch (type) {
            case "CasesByStatus":
                browser.wait(EC.visibilityOf(element(by.linkText(Objects.dashboardpage.locators.casesByStatus))), 30000, "Cases by status link is not visible").then(function() {
                    casesByStatus.click();
                });
                return this;
                break;
            case "MyCases":
                browser.wait(EC.visibilityOf(element(by.linkText(Objects.dashboardpage.locators.myCases))), 30000, "My cases link is not visible").then(function() {
                    myCases.click();
                });
                return this;
                break;
            case "MyComplaints":
                browser.wait(EC.visibilityOf(element(by.linkText(Objects.dashboardpage.locators.myComplaints))), 30000, "My complaints link is not visible").then(function() {
                    myComplaints.click();
                });
                return this;
                break;
            case "NewComplaints":
                browser.wait(EC.visibilityOf(element(by.linkText(Objects.dashboardpage.locators.newComplaints))), 30000, "New complaints link is not visible").then(function() {
                    newComplaints.click();
                });
                return this;
                break;
            case "TeamWorkload":
                browser.wait(EC.visibilityOf(element(by.linkText(Objects.dashboardpage.locators.teamWorkload))), 30000, "Team workload link is not visible").then(function() {
                    teamWorkload.click();
                });
                return this;
                break;
            case "Weather":
                browser.wait(EC.visibilityOf(element(by.linkText(Objects.dashboardpage.locators.weather))), 30000, "Weather link is not visible").then(function() {
                    weather.click();
                });
                return this;
                break;
            case "News":
                browser.wait(EC.visibilityOf(element(by.linkText(Objects.dashboardpage.locators.news))), 30000, "News link is not visible").then(function() {
                    news.click();
                });
                return this;
                break;
            case "MyTasks":
                browser.wait(EC.visibilityOf(element(by.linkText(Objects.dashboardpage.locators.myTasks))), 30000, "My tasks link is not visible").then(function() {
                    myTasks.click();
                });
                return this;

            default:
                casesByStatus.click();
                return this;
                break;
        }
    }

    this.editDashboardTitle = function(title) {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.dashboardpage.locators.editBtn))), 30000, "Edit button in the dashboard page is not displayed").then(function() {
            editBtn.click().then(function() {
                editDashboardBtn.click().then(function() {
                    dashboardTitleInput.clear().then(function() {
                        dashboardTitleInput.sendKeys(title).then(function() {
                            closeBtn.click().then(function() {
                                browser.executeScript('arguments[0].click()', saveChangesBtn);
                            });
                        });
                    });
                });
            });
        });
        return this;
    };

    this.returnDashboardTitle = function() {
        return dashboardTitle.getText();
    }

    this.selectPageSizeOnWidget = function(size) {

        pageSize.selectByText(size);

    }

    this.returnItemsPerPage = function() {

        browser.wait(EC.visibilityOf(element(by.model(Objects.dashboardpage.locators.pageSize))), 30000, "Items per page in widget is not displayed");
        return itemesPerPage.getText();
    }

};

DashboardPage.prototype = basePage;
module.exports = new DashboardPage();
