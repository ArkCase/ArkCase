var DashboardPage = function() {

    this.editBtn = element(by.xpath('.//*[@class="well-sm clearfix ng-scope"]/span/a/i'));
    this.addNewWidgetBtn = element(by.xpath('.//*[@class="well-sm clearfix ng-scope"]/span/a[1]/i'));
    this.editDashboardBtn = element(by.xpath('.//*[@class="well-sm clearfix ng-scope"]/span/a[2]/i'));
    this.saveChangesBtn = element(by.xpath('.//*[@class="well-sm clearfix ng-scope"]/span/a[3]/i'));
    this.undoChangesBtn = element(by.xpath('.//*[@class="well-sm clearfix ng-scope"]/span/a[4]/i'));
    this.addNewWidgetTitle = element(by.css('.modal-header ng-scope'));
    this.casesByStatus = element(by.xpath('.//*[@class="dl-horizontal"]/dt[1]/a'));
    this.myCases = element(by.xpath('.//*[@class="dl-horizontal"]/dt[2]/a'));
    this.myComplaints = element(by.xpath('.//*[@class="dl-horizontal"]/dt[3]/a'));
    this.newComplaints = element(by.xpath('.//*[@class="dl-horizontal"]/dt[4]/a'));
    this.teamWorkload = element(by.xpath('.//*[@class="dl-horizontal"]/dt[5]/a'));
    this.weather = element(by.xpath('.//*[@class="dl-horizontal"]/dt[6]/a'));
    this.news = element(by.xpath('.//*[@class="dl-horizontal"]/dt[7]/a'));
    this.closeBtn = element(by.buttonText('Close'));
    this.widgetTitle = element.all(by.xpath('.//*[@class="panel-heading clearfix"]/div/h3')).get(0);
    this.reloadWidgetContentBtn = element(by.xpath('.//*[@class="panel-title ng-binding ng-scope"]/span/a[1]/i'));
    this.chnageWidgetLocationBtn = element(by.xpath('.//*[@class="panel-title ng-binding ng-scope"]/span/a[2]/i'));
    this.editWidgetConfigurationBtn = element(by.xpath('.//*[@class="panel-title ng-binding ng-scope"]/span/a[5]/i'));
    this.removeWidgetBtn = element.all(by.xpath('.//*[@class="panel-title ng-binding ng-scope"]/span/a[7]')).get(0);

};

module.exports = new DashboardPage();
