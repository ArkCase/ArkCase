var Objects=require('../Objects.json');
var DashboardPage = function() {

    this.editBtn = element(by.xpath(Objects.dashboardpage.locators.editBtn));
    this.addNewWidgetBtn = element(by.xpath(Objects.dashboardpage.locators.addNewWidgetBtn));
    this.editDashboardBtn = element(by.xpath(Objects.dashboardpage.locators.editDashboardBtn));
    this.saveChangesBtn = element(by.xpath(Objects.dashboardpage.locators.saveChangesBtn));
    this.undoChangesBtn = element(by.xpath(Objects.dashboardpage.locators.undoChangesBtn));
    this.addNewWidgetTitle = element(by.css(Objects.dashboardpage.locators.addNewWidgetTitle));
    this.casesByStatus = element(by.xpath(Objects.dashboardpage.locators.casesByStatus));
    this.myCases = element(by.xpath(Objects.dashboardpage.locators.myCases));
    this.myComplaints = element(by.xpath(Objects.dashboardpage.locators.myComplaints));
    this.newComplaints = element(by.xpath(Objects.dashboardpage.locators.newComplaints));
    this.teamWorkload = element(by.xpath(Objects.dashboardpage.locators.teamWorkload));
    this.weather = element(by.xpath(Objects.dashboardpage.locators.weather));
    this.news = element(by.xpath(Objects.dashboardpage.locators.news));
    this.closeBtn = element(by.buttonText(Objects.dashboardpage.locators.closeBtn));
    this.widgetTitle = element.all(by.xpath(Objects.dashboardpage.locators.widgetTitle)).get(0);
    this.reloadWidgetContentBtn = element(by.xpath(Objects.dashboardpage.locators.reloadWidgetContentBtn));
    this.chnageWidgetLocationBtn = element(by.xpath(Objects.dashboardpage.locators.chnageWidgetLocationBtn));
    this.editWidgetConfigurationBtn = element(by.xpath(Objects.dashboardpage.locators.editWidgetConfigurationBtn));
    this.removeWidgetBtn = element.all(by.xpath(Objects.dashboardpage.locators.removeWidgetBtn)).get(0);

};

module.exports = new DashboardPage();
