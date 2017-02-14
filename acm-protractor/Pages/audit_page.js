var logger = require('../log');
var waitHelper = require('../util/waitHelper.js');
var util = require('../util/utils.js');
var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var SelectWrapper = require('../util/select-wrapper.js');
var EC = protractor.ExpectedConditions;
var reportNameDropDownList = new SelectWrapper(by.id(Objects.auditPage.locators.reportName));
var fileId = element(by.id(Objects.auditPage.locators.id));
var dateFrom = element(by.id(Objects.auditPage.locators.dateFromAudit));
var dateTo = element(by.id(Objects.auditPage.locators.dateToAudit));
var generateAuditReportButton = element(by.buttonText(Objects.auditPage.locators.generateAuditReportButton));
var auditReportDateColumnTitle = element(by.xpath(Objects.auditPage.locators.auditReportColumn1Title));
var auditReportUserColumnTitle = element(by.xpath(Objects.auditPage.locators.auditReportColumn2Title));
var auditReportNameColumnTitle = element(by.xpath(Objects.auditPage.locators.auditReportColumn3Title));
var auditReportResultColumnTitle = element(by.xpath(Objects.auditPage.locators.auditReportColumn4Title));
var auditReportIpAddressColumnTitle = element(by.xpath(Objects.auditPage.locators.auditReportColumn5Title));
var auditReportObjectIdTitle = element(by.xpath(Objects.auditPage.locators.auditReportColumn6Title));
var auditReportObjectTypeTitle = element(by.xpath(Objects.auditPage.locators.auditReportColumn7Title));
var dateValue = element(by.xpath(Objects.auditPage.locators.dateValue));
var userValue = element(by.xpath(Objects.auditPage.locators.userValue));
var nameValue = element(by.xpath(Objects.auditPage.locators.nameValue));
var resultValue = element(by.xpath(Objects.auditPage.locators.resultValue));
var ipAddressValue = element(by.xpath(Objects.auditPage.locators.ipAddressValue));
var objectIdValue = element(by.xpath(Objects.auditPage.locators.objectIdValue));
var objectTypeValue = element(by.xpath(Objects.auditPage.locators.objectTypeValue));
var objectIdValueSecondRow = element(by.xpath(Objects.auditPage.locators.objectIdValueSecondRow));

var AuditPage = function() {

    this.selectReportName = function(report) {
        browser.wait(EC.presenceOf(element(by.id(Objects.auditPage.locators.reportName))), 30000, "Select report drop down is not present in the DOM").then(function () {
            browser.wait(EC.visibilityOf(element(by.id(Objects.auditPage.locators.reportName))), 30000, "Select report drop down is not visible").then(function () {
                browser.wait((EC.textToBePresentInElement(element(by.id(Objects.auditPage.locators.reportName)), report)), 30000, report + " is not present in select report drop down list").then(function () {
                    reportNameDropDownList.selectByText(report);
                });
            });
        });
        return this;
    };
    this.insertId = function (id) {
        browser.wait(EC.visibilityOf(element(by.id(Objects.auditPage.locators.id))), 30000, "File id field is not visible").then(function () {
            fileId.clear();
            fileId.sendKeys(id);
        });
        return this;
    };
    this.insertDateFrom = function (datefrom) {
        browser.wait(EC.visibilityOf(element(by.id(Objects.auditPage.locators.dateFromAudit))), 30000, "Date from element is not visible").then(function () {
            dateFrom.clear();
            dateFrom.sendKeys(datefrom);
        });
        return this;
    };
    this.insertDateTo = function (dateto) {
        browser.wait(EC.visibilityOf(element(by.id(Objects.auditPage.locators.dateToAudit))), 30000, "Date to element is not visible").then(function () {
            dateTo.clear();
            dateTo.sendKeys(dateto);
        });
        return this;
    };
    this.clickGenerateAuditReport = function () {
        generateAuditReportButton.click();
        return this;
    };
    this.runReport = function (report, id, datefrom, dateto) {
        this.insertDateFrom(datefrom);
        this.insertDateTo(dateto);
        this.insertId(id);
        this.selectReportName(report);
        this.clickGenerateAuditReport();
    };
    this.returnAuditReportDateColumnTitle = function () {
        return auditReportDateColumnTitle.getText();
    };
    this.returnAuditReportUserColumnTitle = function () {
        return auditReportUserColumnTitle.getText();
    };
    this.returnAuditReportNameColumnTitle = function () {
        return auditReportNameColumnTitle.getText();
    };
    this.returnAuditReportResultColumnTitle = function () {
        return auditReportResultColumnTitle.getText();
    };
    this.returnAuditReportIpAddressColumnTitle = function () {
        return auditReportIpAddressColumnTitle.getText();
    };
    this.returnAuditReportObjectIdTitle = function () {
        return auditReportObjectIdTitle.getText();
    };
    this.returnAuditReportObjectTypeTitle = function () {
        return auditReportObjectTypeTitle.getText();
    };
    this.returnDateValue = function () {
        dateValue.getText().then(function (text) {
            return text.substring(0,9);
        })
    };
    this.returnUserValue = function () {
        return userValue.getText();
    };
    this.returnNameValue = function () {
        return nameValue.getText();
    };
    this.returnResultValue = function () {
        return resultValue.getText();
    };
    this.returnIpAddressValue = function () {
        return ipAddressValue.getText();
    };
    this.returnObjectIdValue = function () {
        return objectIdValue.getText();
    };
    this.returnObjectTypeValue = function () {
        return objectTypeValue.getText();
    };
    this.returnSecondRowObjectIdValue = function () {
        return objectIdValueSecondRow.getText();
    };
    this.switchToAuditframes = function() {
        browser.ignoresynchronization = true;
         browser.switchTo().frame(browser.driver.findElement(by.name("audit-iframe"))).then(function () {
            browser.switchTo().frame(browser.driver.findElement(by.id("reportContent")));
        });
        return this;
    };
    this.validateAuditReportTitles = function(dateTitle, userTitle, nameTitle, resultTitle, ipAddressTitle, objectIdTitle, objectTypeTitle) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.auditPage.locators.auditReportColumn1Title))), 30000, "Report column 1 title is not present in the DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.auditPage.locators.auditReportColumn1Title))), 30000, "Report column 1 title is not visible").then(function () {
                expect(auditReportDateColumnTitle.getText()).toBe(dateTitle, "Date title column in audit report is not correct");
                expect(auditReportUserColumnTitle.getText()).toEqual(userTitle, "User title column in audit report is not correct");
                expect(auditReportNameColumnTitle.getText()).toEqual(nameTitle, "Name title column in audit report is not correct");
                expect(auditReportResultColumnTitle.getText()).toEqual(resultTitle, "Result title column in audit report is not correct");
                expect(auditReportIpAddressColumnTitle.getText()).toEqual(ipAddressTitle, "IP address title column in audit report is not correct");
                expect(auditReportObjectIdTitle.getText()).toEqual(objectIdTitle, "Object Id title in audit report is not correct");
                expect(auditReportObjectTypeTitle.getText()).toEqual(objectTypeTitle, "Object type title in audit report is not correct");
            });
        });
    };

    this.validateAuditReportValues = function (date, user, name, result, objectId, objectType) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.auditPage.locators.dateValue))), 30000, "The audit report is empty for chosed filter, not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.auditPage.locators.dateValue))), 30000, "The audit report is empty for chosed filter, values not visible").then(function() {
                expect(dateValue.getText()).toContain(date, "Date value in audit report is not correct");
                expect(userValue.getText()).toEqual(user, "User value in audit report is not correct");
                expect(nameValue.getText()).toEqual(name, "Name value in audit report is not correct");
                expect(resultValue.getText()).toEqual(result, "Result value in audit report is not correct");
                expect(objectId).toContain(objectIdValue.getText(), "The audit report does not display the last viewed case/complaint");
                expect(objectTypeValue.getText()).toEqual(objectType, "Object type value in audit report is not correct");
            });
        })
    };

};


AuditPage.prototype = basePage;
module.exports = new AuditPage();
