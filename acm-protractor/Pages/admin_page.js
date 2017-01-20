var Objects=require('../json/Objects.json');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var SelectWrapper = require('../util/select-wrapper.js');
var waitHelper = require('../util/waitHelper.js');
var util = require('../util/utils.js');
var ListBox = new SelectWrapper(by.model(Objects.adminPage.locators.ListBox));
var notAuthorizedListBox = new SelectWrapper(by.model(Objects.adminPage.locators.notAuthorizedListBox));
var authorizedListBox = element(by.model(Objects.adminPage.locators.authorizedListBox));
var directoryName =  element.all(by.repeater(Objects.notificationPage.locators.notificationCol)).get(0);
var LDAPUrl =  element.all(by.repeater(Objects.notificationPage.locators.notificationCol)).get(1);

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
    this.ChooseListBoxValue = function (value) {
        ListBox.selectByText(value);
        return this;
    };
    this.returnAuthorized = function () {
        return values = authorizedListBox.all(by.tagName('option')).getAttribute('label');
    };
    this.returnArmediaDirectoryName = function () {

        return directoryName.getText();
    };
    this.returnArmediaLDAPUrl = function () {
        return LDAPUrl.getText();
    }
};
AdminPage.prototype = basePage;
module.exports = new AdminPage();