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
var chooseHeaderLogoBtn = element(by.name(Objects.adminPage.locators.chooseHeaderLogoBtn));
var selectModuleLabelConfig = new SelectWrapper(by.model(Objects.adminPage.locators.selectModuleLabelConfig));
var filterLabelId = element(by.model(Objects.adminPage.locators.filterLabelId));
var firstRowValue = element.all(by.xpath(Objects.adminPage.locators.adminLabelsTableRow)).get(0);
var updateField = element(by.model(Objects.adminPage.locators.updateField));
var confirmButton = element(by.css(Objects.userpage.locators.confirmBtn));
var resetModuleButtonText = element(by.xpath(Objects.adminPage.locators.resetModuleButtonText));
var addToAuthorizedButton = element(by.xpath(Objects.adminPage.locators.addToAuthorizedButton));
var removeFromAuthorizedButton = element(by.xpath(Objects.adminPage.locators.removeFromAuthorizedButton));
var headerLogoFileName = element(by.binding(Objects.adminPage.locators.headerLogoFileName));
var saveFile = element(by.xpath(Objects.adminPage.locators.saveFile));

var AdminPage = function() {

    this.clickSubLink = function(link) {
               var xPathStr = "//span[contains(text(),'";
                var completexPath;
                completexPath = xPathStr + link + "')]";
                browser.wait(EC.visibilityOf(element(by.xpath(completexPath))), 30000, "Link " +link+ " is not visible").then(function() {
                    var el = element(by.xpath(completexPath));
                    el.click();
                });
                return this;

    };
    this.ChooseListBoxValue = function (value) {
        browser.wait(EC.visibilityOf(element(by.model(Objects.adminPage.locators.ListBox))), 30000, "Drop down list for selecting is not visible").then(function() {
            ListBox.selectByPartialText(value);
        });
    };
    this.returnAuthorized = function () {
        browser.wait(EC.visibilityOf(element(by.model(Objects.adminPage.locators.authorizedListBox))), 30000, "Authorized list box is not visible").then(function () {
            return values = authorizedListBox.all(by.tagName('option')).getAttribute('label');
        })
    };
    this.validateAuthorized = function (groups) {
        browser.wait(EC.visibilityOf(element(by.model(Objects.adminPage.locators.authorizedListBox))), 30000, "Authorized list box is not visible").then(function () {
            var values = authorizedListBox.all(by.tagName('option')).getAttribute('label');
            expect(values).toEqual(jasmine.arrayContaining(groups), "Admin user is not authorized to view appropriate widget");
        })

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
    };
    this.uploadLogo = function () {
        browser.wait(EC.visibilityOf(element(by.name(Objects.adminPage.locators.chooseHeaderLogoBtn))), 30000, "Button for uploading header logo is not visible").then(function() {
            chooseHeaderLogoBtn.click().then(function () {
                util.uploadLogo();
            })
        })
        return this;
    };
    this.selectModuleInLabelConfig = function(module, labelid){
        browser.wait(EC.visibilityOf(element(by.model(Objects.adminPage.locators.selectModuleLabelConfig))), 30000, "Drop down list for selecting modules is not visible").then(function() {
            selectModuleLabelConfig.selectByText(module);
        });
        return this;
    };
    this.filterLabelId = function (labelid) {
        browser.wait(EC.visibilityOf(element(by.model(Objects.adminPage.locators.filterLabelId))), 30000, "Drop down list for filtering modules is not visible").then(function() {
            filterLabelId.click().then(function () {
                filterLabelId.sendKeys(labelid);
            })
        });
        return this;
    };
    this.firstRowValueClick = function () {
        browser.wait(EC.visibilityOf(element.all(by.xpath(Objects.adminPage.locators.adminLabelsTableRow)).get(0)), 30000, "Table is empty, there is no records for searched id").then(function() {
            firstRowValue.click();
        });
        return this;
    };
    this.updateLabel = function (label) {
        this.firstRowValueClick();
        this.editLabel(label);
        this.confirmUpdate();
    };

    this.editLabel = function (label) {
        browser.wait(EC.visibilityOf(element(by.model(Objects.adminPage.locators.updateField))), 30000, "Table is empty, there is no records for searched id").then(function() {
            updateField.click().then(function () {
                updateField.clear().then(function () {
                    updateField.sendKeys(label);
                })
            })
        })
        return this;
    };
    this.confirmUpdate = function () {
        confirmButton.click();
        return this;
    };
    this.validateResetModuleButtonText = function (label) {
        browser.wait(EC.presenceOf(element(by.model(Objects.adminPage.locators.resetModuleButtonText))), 30000, "Button is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.adminPage.locators.resetModuleButtonText))), 30000, "Button is not visible").then(function () {
                expect(resetModuleButtonText.getText()).toEqual(label, "Label of button is not updated successfully");
            })
        })
    };
    this.selectNotAuthorized = function (value) {
        browser.wait(EC.visibilityOf(element(by.model(Objects.adminPage.locators.notAuthorizedListBox))), 30000, "Not authorized drop down list for selecting is not visible").then(function() {
            notAuthorizedListBox.selectByText(value);
        });
        return this;
    };
    this.clickAddToAuthorizedButton = function () {
        addToAuthorizedButton.click();
        return this;
    };
    this.addAuthorization = function (value) {
        this.selectNotAuthorized(value);
        this.clickAddToAuthorizedButton();
        return this;
    };
    this.selectAuthorized = function (value) {
        browser.wait(EC.presenceOf(element(by.model(Objects.adminPage.locators.authorizedListBox))), 30000, "Authorized drop down list for selecting is not visible").then(function() {
        browser.wait(EC.visibilityOf(element(by.model(Objects.adminPage.locators.authorizedListBox))), 30000, "Authorized drop down list for selecting is not visible").then(function() {
            authorizedListBox.selectByText(value);
        });
        });
        return this;
    };
    this.clickRemoveFromAuthorizedButton = function () {
        removeFromAuthorizedButton.click();
        return this;
    }
    this.removeAuthorization = function (value) {
        this.selectAuthorized(value);
        this.clickRemoveFromAuthorizedButton();
        return this;
    };
    this.HeaderUploaded = function () {
        return headerLogoFileName.getText();
    };
    this.clickSaveButton = function () {
        saveFile.click();
        return this;
    }

};
AdminPage.prototype = basePage;
module.exports = new AdminPage();