var Objects=require('../json/Objects.json');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var SelectWrapper = require('../util/select-wrapper.js');
var waitHelper = require('../util/waitHelper.js');
var chooseReportListBox = new SelectWrapper(by.model(Objects.adminPage.locators.chooseReportListBox));
var notAuthorizedListBox = new SelectWrapper(by.model(Objects.adminPage.locators.notAuthorizedListBox));
var authorizedListBox = element(by.model(Objects.adminPage.locators.authorizedListBox));

var AdminPage = function() {

    this.clickSubLink = function(link) {
               var xPathStr = "//span[contains(text(),'";
                var completexPath;
                completexPath = xPathStr + link + "')]";
                browser.wait(EC.visibilityOf(element(by.xpath(completexPath))), 30000).then(function() {
                    var el = element(by.xpath(completexPath));
                    el.click();
                });
                return this;

    };
    this.ChooseReport = function (report) {
        chooseReportListBox.selectByText(report);
        return this;
    };
    this.returnAuthorized = function () {
        return values = authorizedListBox.all(by.tagName('option')).getAttribute('label');
    }

};
AdminPage.prototype = basePage;
module.exports = new AdminPage();