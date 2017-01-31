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
var caseFormName = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(0);
var caseApplicationName = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(1);
var caseDescription = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(2);
var caseTargetFile = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(3);
var complaintFormName = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(4);
var complaintApplicationName = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(5);
var complaintDescription = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(6);
var complaintTargetFile = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(7);
var complaintBussinesProcessName = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(0);
var complaintBPDescription = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(1);
var complaintBPModified = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(2);
var complaintBPAuthor = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(3);
var docApprovalBussinesProcessName = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(5);
var docApprovalBPDescription = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(6);
var docApprovalBPModified = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(7);
var docApprovalBPAuthor = element.all(by.xpath(Objects.adminPage.locators.tableRow)).get(8);

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
    };
    this.returnCaseFormName = function () {
        return caseFormName.getText();
    };
    this.returnCaseApplicationName = function () {
        return caseApplicationName.getText();
    };
    this.returnCaseDescription = function () {
        return caseDescription.getText();
    };
    this.returnCaseTargetFile = function () {
        return caseTargetFile.getText();
    };
    this.returnComplaintFormName = function () {
        return complaintFormName.getText();
    };
    this.returnComplaintApplicationName = function () {
        return complaintApplicationName.getText();
    };
    this.returnComplaintDescription = function () {
        return complaintDescription.getText();
    };
    this.returnComplaintTargetFile = function () {
        return complaintTargetFile.getText();
    };
    this.returnComplaintBussinessProcessName = function () {
        return complaintBussinesProcessName.getText();
    };
    this.returnComplaintBPDescription = function () {
        return complaintBPDescription.getText();
    };
    this.returnComplaintBPModified = function () {
        return complaintBPModified.getText();
    };
    this.returnComplaintBPAuthor = function () {
        return complaintBPAuthor.getText();
    };
    this.returnDocApprovalBussinesProcessName = function () {
        return docApprovalBussinesProcessName.getText();
    };
    this.returnDocApprovalBPDescription = function () {
        return docApprovalBPDescription.getText();
    };
    this.returnDocApprovalBPModified = function () {
        return docApprovalBPModified.getText();
    };
    this.returnDocApprovalBPAuthor = function () {
        return docApprovalBPAuthor.getText();
    }
};
AdminPage.prototype = basePage;
module.exports = new AdminPage();