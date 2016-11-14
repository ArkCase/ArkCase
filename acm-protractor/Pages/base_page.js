var EC = protractor.ExpectedConditions;
var Objects=require('../json/Objects.json');
var util = require('../util/utils.js');
var wait = require('../util/waitHelper');
var newBtn = element(by.linkText(Objects.basepage.locators.newButton));
var root = element(by.xpath(Objects.basepage.locators.root));
var newCorrespondence = element(by.xpath(Objects.basepage.locators.newCorrespondence));
var docTitle = element(by.xpath(Objects.basepage.locators.docTitle));
var docExtension = element(by.xpath(Objects.basepage.locators.docExtension));
var docType = element(by.xpath(Objects.basepage.locators.docType));
var docCreated = element(by.xpath(Objects.basepage.locators.docCreated));
var docModified = element(by.xpath(Objects.basepage.locators.docModified));
var docAuthor = element(by.xpath(Objects.basepage.locators.docAuthor));
var docVersion = element(by.xpath(Objects.basepage.locators.docVersion));
var docStatus = element(by.xpath(Objects.basepage.locators.docStatus));
var deleteDoc = element(by.xpath(Objects.basepage.locators.deleteDoc));
//var docRow = elements(by.xpath(Objects.basepage.locators.docRow));
var downloadDoc = element(by.xpath(Objects.basepage.locators.downloadDoc));
var docRow = element(by.xpath(Objects.basepage.locators.docRow));
var fancyTreeExpandTop = element(by.xpath(Objects.casepage.locators.fancyTreeExpandTop));
var root = element(by.xpath(Objects.taskspage.locators.root));
var documentsLink = element(by.xpath(Objects.casepage.locators.documentsLink));

var BasePage = function(){

	  this.navigateToURL = function(url){

	    browser.get(url);

	  };

	   this.getPageTitle = function(){

	       return browser.getTitle();

	   };
	   this.clickNewButton = function() {
		   newBtn.click();
		   return this;
		};

	   this.clickNewCorrespondence = function () {
		   browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.newCorrespondence))), 30000).then(function () {
			   newCorrespondence.click();
			   return this;
		   });
	   };
	   this.selectCorrespondence = function(type, correspondence){
		   var xPathStr;
		   if (type == "case")
		   {
			   xPathStr = ".//li[@data-command='template/";
		   }
		   else
		   {
			   xPathStr = ".//li[@data-command='template/Complaint";
		   }
		   var completexPath;
		   switch (correspondence) {
				   case "General Release":
					   completexPath = xPathStr + "GeneralRelease.docx']";
					   break;
				   case "Medical Release":
					   completexPath = xPathStr + "MedicalRelease.docx']";
					   break;
				   case "Clearance Granted":
					   completexPath = xPathStr + "ClearanceGranted.docx']";
					   break;
				   case "Clearance Denied":
					   completexPath = xPathStr + "ClearanceDenied.docx']";
					   break;
				   case "Notice of Investigation":
					   completexPath = xPathStr + "NoticeofInvestigation.docx']";
					   break;
				   case "Witness Interview Request":
					   completexPath = xPathStr + "InterviewRequest.docx']";
					   break;
				   default:
					   completexPath = xPathStr + "GeneralRelease.docx']";
					   break;
			   }

		   var el = element(by.xpath(completexPath));
		   el.click();
		   return this;
	   };
	   this.addCorrespondence = function(correspondence){
		   this.clickNewCorrespondence();
		   this.selectCorrespondence(correspondence);
	   };
	   this.returnDocTitleGrid = function(){

		   return docTitle.getText();
	   };
	   this.returnDocExtensionGrid = function () {
		   return docExtension.getText();
	   };
	   this.returnDocTypeGrid = function () {
		   return docType.getText();
	   };
	   this.returnDocCreatedGrid = function () {
		   return docCreated.getText();
	   };
	   this.returnDocModifiedGrid = function () {
		   return docModified.getText();
	   };
	   this.returnDocAuthorGrid = function () {
		   return docAuthor.getText();
	   };
	   this.returnDocVersionGrid = function () {
		   return docVersion.getText();
	   };
	   this.returnDocStatusGrid = function(){
		   return docStatus.getText();
	   };
	   this.clickDeleteDoc = function () {
		   browser.waitForAngular().then(function () {
			   docTitle.click().then(function () {
				   browser.actions().click(protractor.Button.RIGHT).perform().then(function () {
					   deleteDoc.click();
					   return this;
				   });
			   });
		   });

	   };
	   this.clickDownloadDoc = function () {
		   downloadDoc.click();
		   return this;
	   };
	   this.returnDocRowAdded = function() {
            if (docRow.isPresent())
			{
				return true;
			}
			else
			{
				return false;
			}
	   };
	   this.validateDocGridData = function(added, doctitle, docextension, doctype, createddate, modifieddate, author, version, status){
		   browser.waitForAngular();
		   browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.docAuthor))), 30000).then(function () {
			   expect(this.returnDocRowAdded()).toBe(added);
			   expect(this.returnDocTitleGrid()).toEqual(doctitle);
			   expect(this.returnDocExtensionGrid()).toEqual(docextension);
			   expect(this.returnDocTypeGrid()).toEqual(doctype);
			   expect(this.returnDocCreatedGrid()).toEqual(createddate);
			   expect(this.returnDocModifiedGrid()).toEqual(modifieddate);
			   expect(this.returnDocAuthorGrid()).toEqual(author);
			   expect(this.returnDocVersionGrid()).toEqual(version);
			   expect(this.returnDocStatusGrid()).toEqual(status);
		   });
	   }
	   this.switchToIframes = function() {
		   browser.ignoreSynchronization = true;
		   browser.wait(EC.visibilityOf(element(by.className("new-iframe ng-scope"))), 30000);
		   browser.switchTo().frame(browser.driver.findElement(by.className("new-iframe ng-scope"))).then(function() {
			   browser.switchTo().frame(browser.driver.findElement(By.className("frevvo-form")));
		});
		return this;
	   };
	this.switchToDefaultContent = function() {

		browser.driver.switchTo().defaultContent();
		return this;

	};
	this.rightClickRootFolder = function(){
		browser.wait(EC.visibilityOf(element(by.xpath(Objects.taskspage.locators.root))), 30000).then(function () {
			root.click();
			browser.actions().click(protractor.Button.RIGHT).perform();
		});
		return this;
	}
	this.clickExpandFancyTreeTopElement = function () {
		browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.fancyTreeExpandTop))), 30000).then(function () {
			fancyTreeExpandTop.click().then(function () {
				browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.documentsLink))), 30000).then(function () {
					documentsLink.click();
					return this;
				});
			});
		});

	};

	};
	
module.exports = new BasePage();