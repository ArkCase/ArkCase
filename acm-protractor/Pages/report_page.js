var logger = require('../log');
var waitHelper = require('../util/waitHelper.js');
var util = require('../util/utils.js');
var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var SelectWrapper = require('../util/select-wrapper.js');
var EC = protractor.ExpectedConditions;
var reportDropDownList = new SelectWrapper(by.id(Objects.reportPage.locators.reportDropDownList));
var stateDropDownList = new SelectWrapper(by.id(Objects.reportPage.locators.stateDropDownList));
var dateFrom = element(by.id(Objects.reportPage.locators.dateFrom));
var dateTo = element(by.id(Objects.reportPage.locators.dateTo));
var generateButton = element(by.buttonText(Objects.reportPage.locators.generateButton));
var summaryReportTitle = element(by.xpath(Objects.reportPage.locators.summaryReportTitle));
var caseNumberColumnTitle = element(by.xpath(Objects.reportPage.locators.caseNumberColumnTitle));
var caseStatusColumnTitle = element(by.xpath(Objects.reportPage.locators.caseStatusColumnTitle));
var caseTitleColumnTitle = element(by.xpath(Objects.reportPage.locators.caseTitleColumnTitle));
var caseIncidentDateColumnTitle = element(by.xpath(Objects.reportPage.locators.caseIncidentDateColumnTitle));
var casePriorityColumnTitle = element(by.xpath(Objects.reportPage.locators.casePriorityColumnTitle));
var caseDuedateColumnTitle = element(by.xpath(Objects.reportPage.locators.caseDuedateColumnTitle));
var caseTypeColumnTitle = element(by.xpath(Objects.reportPage.locators.caseTypeColumnTitle));
var caseNumberValue = element(by.xpath(Objects.reportPage.locators.caseNumberValue));
var caseStatusValue = element(by.xpath(Objects.reportPage.locators.caseStatusValue));
var caseTitleValue = element(by.xpath(Objects.reportPage.locators.caseTitleValue));
var caseIncidentDateValue = element(by.xpath(Objects.reportPage.locators.caseIncidentDateValue));
var casePriorityValue = element(by.xpath(Objects.reportPage.locators.casePriorityValue));
var caseDuedateValue = element(by.xpath(Objects.reportPage.locators.caseDuedateValue));
var caseTypeValue = element(by.xpath(Objects.reportPage.locators.caseTypeValue));
var complaintTitleColumnTitle = element(by.xpath(Objects.reportPage.locators.complaintTitleColumnTitle));
var complaintStatusColumnTitle = element(by.xpath(Objects.reportPage.locators.complaintStatusColumnTitle));
var complaintTypeColumnTitle = element(by.xpath(Objects.reportPage.locators.complaintTypeColumnTitle));
var complaintPriorityColumnTitle = element(by.xpath(Objects.reportPage.locators.complaintPriorityColumnTitle));
var complaintCreateDateColumnTitle = element(by.xpath(Objects.reportPage.locators.complaintCreateDateColumnTitle));
var complaintIncidentDateColumnTitle = element(by.xpath(Objects.reportPage.locators.complaintIncidentDateColumnTitle));
var complaintTitleValue = element(by.xpath(Objects.reportPage.locators.complaintTitleValue));
var complaintStatusValue = element(by.xpath(Objects.reportPage.locators.complaintStatusValue));
var complaintTypeValue = element(by.xpath(Objects.reportPage.locators.complaintTypeValue));
var complaintPriorityValue = element(by.xpath(Objects.reportPage.locators.complaintPriorityValue));
var complaintCreatedDateValue = element(by.xpath(Objects.reportPage.locators.complaintCreatedDateValue));
var complaintIncidentDateValue = element(by.xpath(Objects.reportPage.locators.complaintIncidentDateValue));
var CDCDispositionTitle = element(by.xpath(Objects.reportPage.locators.CDCDispositionTitle));
var CDCCountTitle = element(by.xpath(Objects.reportPage.locators.CDCCountTitle));
var CDCAddToExistingCaseTitle = element(by.xpath(Objects.reportPage.locators.CDCAddToExistingCaseTitle));
var CDCNoFurtherActionTitle = element(by.xpath(Objects.reportPage.locators.CDCNoFurtherActionTitle));
var CDCOpenInvestigationTitle = element(by.xpath(Objects.reportPage.locators.CDCOpenInvestigationTitle));
var CDCReferExternalTitle = element(by.xpath(Objects.reportPage.locators.CDCReferExternalTitle));
var CDCAddToExistingCaseValue = element(by.xpath(Objects.reportPage.locators.CDCAddToExistingCaseValue));
var CDCNoFurtherActionValue = element(by.xpath(Objects.reportPage.locators.CDCNoFurtherActionValue));
var CDCOpenInvestigationValue = element(by.xpath(Objects.reportPage.locators.CDCOpenInvestigationValue));
var CDCReferExternalValue = element(by.xpath(Objects.reportPage.locators.CDCReferExternalValue));
var CDCImage = element(by.xpath(Objects.reportPage.locators.CDCImage));
var reportsIFrame = element(by.name(Objects.reportPage.locators.reportsIFrame));
var reportAreaIFrame = element(by.id(Objects.reportPage.locators.reportAreaIFrame));

var ReportPage = function() {

    this.selectReport = function(report) {
        browser.wait(EC.presenceOf(element(by.id(Objects.reportPage.locators.reportDropDownList))), 30000, "Report drop down list is not present in DOM").then(function () {
            browser.wait(EC.visibilityOf(element(by.id(Objects.reportPage.locators.reportDropDownList))), 30000, "Report drop down list is not visible").then(function () {
                browser.wait((EC.textToBePresentInElement(element(by.id(Objects.reportPage.locators.reportDropDownList)), report)), 30000, report + " is not present in report drop down list").then(function () {
                    reportDropDownList.selectByText(report);
                });
            });
        });
        return this;
    };
    this.selectState = function (state) {
        browser.wait(EC.visibilityOf(element(by.id(Objects.reportPage.locators.stateDropDownList))), 30000, "State drop down list is not visible").then(function () {
            stateDropDownList.selectByText(state);
        });
        return this;
    };
    this.insertDateFrom = function (datefrom) {
        browser.wait(EC.visibilityOf(element(by.id(Objects.reportPage.locators.dateFrom))), 30000, "Date from field is not visible").then(function () {
            dateFrom.clear();
            dateFrom.sendKeys(datefrom);
        });
        return this;
    };
    this.insertDateTo = function (dateto) {
        browser.wait(EC.visibilityOf(element(by.id(Objects.reportPage.locators.dateTo))), 30000, "Date to field is not visible").then(function () {
            dateTo.clear();
            dateTo.sendKeys(dateto);
        });
        return this;
    };
    this.clickGenerateReport = function () {
        browser.executeScript('arguments[0].click()', generateButton);
        return this;
    };
    this.runReport = function (report, state, datefrom, dateto) {
        this.insertDateFrom(datefrom);
        this.insertDateTo(dateto);
        this.selectReport(report);
        if (report != "COMPLAINT DISPOSITION COUNT") {
            this.selectState(state);
        }
        this.clickGenerateReport();
        return this;
    };

    this.returnCDCDispositionTitle = function () {
        return CDCDispositionTitle.getText();
    };
    this.returnCDCCountTitle = function () {
        return CDCCountTitle.getText();
    };
    this.returnCDCAddToExistingCaseTitle = function () {
        return CDCAddToExistingCaseTitle.getText();
    };
    this.returnCDCNoFurtherActionTitle = function () {
        return CDCNoFurtherActionTitle.getText();
    };
    this.returnCDCOpenInvestigationTitle = function () {
        return CDCOpenInvestigationTitle.getText();
    };
    this.returnCDCReferExternalTitle = function () {
        return CDCReferExternalTitle.getText();
    };
    this.returnCDCAddToExistingCaseValue = function () {
        return CDCAddToExistingCaseValue.getText();
    };
    this.returnCDCNoFurtherActionValue = function () {
        // browser.wait(EC.presenceOf(element(by.xpath(Objects.reportPage.locators.CDCNoFurtherActionValue))), 30000, "CDC No further action value is not present in DOM").then(function() {
        //     browser.wait(EC.visibilityOf(element(by.xpath(Objects.reportPage.locators.CDCNoFurtherActionValue))), 30000, "CDC No further action value is not visible").then(function () {
                return CDCNoFurtherActionValue.getText();
        //     });
        // });
    };
    this.returnCDCOpenInvestigationValue = function () {
        return CDCOpenInvestigationValue.getText();
    };
    this.returnCDCReferExternalValue = function () {
        return CDCReferExternalValue.getText();
    };
    this.returnCDCImage = function () {
        return CDCImage.isDisplayed();
    };
    this.switchToReportframes = function() {
        browser.ignoreSynchronization = true;
        browser.wait(EC.visibilityOf(element(by.name(Objects.reportPage.locators.reportsIFrame))), 120000, "Reports iframe is not visible");
            browser.switchTo().frame(browser.driver.findElement(by.name("reports-iframe"))).then(function () {
                browser.wait(EC.visibilityOf(element(by.id("reportContent"))), 120000, "Reports content is not visible").then(function () {
                    browser.switchTo().frame(browser.driver.findElement(by.id("reportContent")));
                })
            });
        return this;
    };
    this.validateCaseReportTitles = function(reportTitle, casenumberTitle, statusTitle, caseTitle, incidentDateTitle, priorityTitle, dueDateTitle, typeTitle) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.reportPage.locators.summaryReportTitle))), 30000, "Summary report title is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.reportPage.locators.summaryReportTitle))), 30000, "Summary report title is not visible").then(function () {
                expect(summaryReportTitle.getText()).toBe(reportTitle);
                expect(caseNumberColumnTitle.getText()).toEqual(casenumberTitle);
                expect(caseStatusColumnTitle.getText()).toEqual(statusTitle);
                expect(caseTitleColumnTitle.getText()).toEqual(caseTitle);
                expect(caseIncidentDateColumnTitle.getText()).toEqual(incidentDateTitle);
                expect(casePriorityColumnTitle.getText()).toEqual(priorityTitle);
                expect(caseDuedateColumnTitle.getText()).toEqual(dueDateTitle);
                expect(caseTypeColumnTitle.getText()).toEqual(typeTitle);
            });
        });
    };

    this.validateComplaintReportTitles = function(reportTitle, complaintTitle, statusTitle, typeTitle, priorityTitle, createDateTitle, IncidenDateTitle) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.reportPage.locators.summaryReportTitle))), 30000, "Summary report title is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.reportPage.locators.summaryReportTitle))), 30000, "Summary report title is not visible").then(function() {
                expect(summaryReportTitle.getText()).toBe(reportTitle);
                expect(complaintTitleColumnTitle.getText()).toBe(complaintTitle)
                expect(complaintStatusColumnTitle.getText()).toEqual(statusTitle);
                expect(complaintTypeColumnTitle.getText()).toEqual(typeTitle);
                expect(complaintPriorityColumnTitle.getText()).toEqual(priorityTitle);
                expect(complaintCreateDateColumnTitle.getText()).toEqual(createDateTitle);
                expect(complaintIncidentDateColumnTitle.getText()).toEqual(IncidenDateTitle);
            });
        })

    };
    this.validateCDCReportTitles = function (dispositionTitle, countTitle, ATECTitle, NFATitle, OITitle, RETitle) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.reportPage.locators.CDCDispositionTitle))), 30000, "CDC report title is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.reportPage.locators.CDCDispositionTitle))), 30000, "CDC report title is not visible").then(function() {
                expect(CDCDispositionTitle.getText()).toBe(dispositionTitle);
                expect(CDCCountTitle.getText()).toBe(countTitle)
                expect(CDCAddToExistingCaseTitle.getText()).toEqual(ATECTitle);
                expect(CDCNoFurtherActionTitle.getText()).toEqual(NFATitle);
                expect(CDCOpenInvestigationTitle.getText()).toEqual(OITitle);
                expect(CDCReferExternalTitle.getText()).toEqual(RETitle);
            });
        })
    };
    this.validateComplaintReportValues = function (title, status, type, priority, createDate, IncidentDate) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.reportPage.locators.caseTitleValue))), 30000, "Case title value is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.reportPage.locators.caseTitleValue))), 30000, "Case title value is not visible").then(function() {
                expect(complaintTitleValue.getText()).toBe(title, "Title of complaint is not correct");
                expect(complaintStatusValue.getText()).toEqual(status, "Status of complaint is not correct");
                expect(complaintTypeValue.getText()).toEqual(type, "Complaint type is not correct");
                expect(complaintPriorityValue.getText()).toEqual(priority, "Complaint priority is not correct");
                expect(complaintCreatedDateValue.getText()).toEqual(createDate, "Complaint created date is not correct");
                expect(complaintIncidentDateValue.getText()).toEqual(IncidentDate, "Complaint incident date is not correct");
            });
        })
    };
    this.validateComplaintReportisNotEmpty = function () {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.reportPage.locators.caseTitleValue))), 30000, "Case title value is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.reportPage.locators.caseTitleValue))), 30000, "Case title value is not visible").then(function () {
                expect(complaintTitleValue.getText()).not.toBe("", "Complaint report is empty");
            });
        });
    };
    this.validateCaseReportValues = function(caseNumber, status, title, incidentDate, priority, dueDate, type) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.reportPage.locators.caseStatusValue))), 30000, "Case Status value is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.reportPage.locators.caseStatusValue))), 30000, "Case status value is not visible").then(function() {
                    expect(caseNumberValue.getText()).toEqual(caseNumber, "Case id is not correct");
                    expect(caseStatusValue.getText()).toEqual(status, "Case status is not correct");
                    expect(caseTitleValue.getText()).toEqual(title, "Case title is not correct");
                    expect(caseIncidentDateValue.getText()).toEqual(incidentDate, "Case incident date is not correct");
                    expect(casePriorityValue.getText()).toEqual(priority, "Case priority is not correct");
                    expect(caseDuedateValue.getText()).toEqual(dueDate, "Case due date is not correct");
                    expect(caseTypeValue.getText()).toEqual(type, "Case type is not correct");

            });
        })

    };
    this.validateCaseReportisNotEmpty = function () {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.reportPage.locators.caseStatusValue))), 30000, "Case Status value is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.reportPage.locators.caseStatusValue))), 30000, "Case status value is not visible").then(function () {
                expect(caseNumberValue.getText()).not.toBe("", "Case report is empty");
            });
        });
    };
    this.validateCDCReportValues = function (ATECNumber, NFANumber, OINumber, RENumber) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.reportPage.locators.CDCAddToExistingCaseValue))), 30000, "CDC Add to  exisiting case value is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.reportPage.locators.CDCAddToExistingCaseValue))), 30000, "CDC Add to existing case value is not visible").then(function() {
                expect(CDCAddToExistingCaseValue.getText()).toEqual(ATECNumber, "Add to existing case number is not correct");
                var text = CDCAddToExistingCaseValue.getText();
                CDCAddToExistingCaseValue.getText().then(function (text) {
                    console.log('text', text);
                })
                expect(CDCNoFurtherActionValue.getText()).toEqual(NFANumber, "No further action number is not correct");
                expect(CDCOpenInvestigationValue.getText()).toEqual(OINumber, "Open investigation number is not correct");
                expect(CDCReferExternalValue.getText()).toEqual(RENumber, "Refer external number is not correct ");
            });
        })
    };
};


ReportPage.prototype = basePage;
module.exports = new ReportPage();
