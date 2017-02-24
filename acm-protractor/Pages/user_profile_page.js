var Objects = require('../json/Objects.json');
var utils = require('../util/utils.js');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var userName = element(by.binding(Objects.userpage.locators.userName));

var userNavigation = element(by.css(Objects.userpage.locators.userNavigation));
var userNavigationProfile = element(by.linkText(Objects.userpage.locators.userNavigationProfile));
var userPageHeader = element(by.xpath(Objects.userpage.locators.userPageHeader));

var userLocation = element(by.binding(Objects.userpage.locators.userLocation));
var inputField = element(by.model(Objects.userpage.locators.inputField));
var confirmButton = element(by.css(Objects.userpage.locators.confirmBtn));

var officePhone = element(by.binding(Objects.userpage.locators.officePhone));
var imAccount = element(by.binding(Objects.userpage.locators.imAccount));
var shortImaccount = element(by.binding(Objects.userpage.locators.shortImAccount));
var mobilephone = element(by.binding(Objects.userpage.locators.mobilephone));
var companyName = element(by.binding(Objects.userpage.locators.companyName));
var addressOne = element(by.binding(Objects.userpage.locators.addressOne));
var addressTwo = element(by.binding(Objects.userpage.locators.addressTwo));
var city = element(by.binding(Objects.userpage.locators.city));
var state = element(by.binding(Objects.userpage.locators.state));
var zip = element(by.binding(Objects.userpage.locators.zip));
var mainOfficePhone = element(by.binding(Objects.userpage.locators.mainOfficePhone));
var fax = element(by.binding(Objects.userpage.locators.fax));
var website = element(by.binding(Objects.userpage.locators.website));

var changeProfilePic = element(by.xpath(Objects.userpage.locators.changeProfilePic));


var UserPage = function(){
	this.clickUserNavigation = function(){
            browser.wait(EC.visibilityOf(element(by.css(Objects.userpage.locators.userNavigation))), 30000, "User navigation field is not visible").then(function () {
                userNavigation.click();
            });

		return this;
	}
	this.validateUserNavigationProfile = function(text, error){
        browser.wait(EC.visibilityOf(element(by.linkText(Objects.userpage.locators.userNavigationProfile))), 30000, "User navigation profile field is not visible").then(function() {
            expect(userNavigationProfile.getText()).toEqual(text, error);
        });
	}
	this.clickUserNavigationProfile = function(){
        browser.wait(EC.visibilityOf(element(by.linkText(Objects.userpage.locators.userNavigationProfile))), 30000, "User navigation profile field is not visible").then(function() {
            userNavigationProfile.click();
        });
		return this;
	}
	this.validateUserPageHeader = function(text, error){
        browser.wait(EC.visibilityOf(element(by.id(Objects.userpage.locators.picture))), 30000, "Picture element is not visible on user profile page"). then(function () {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.userpage.locators.userPageHeader))), 30000, "User page header is not visible").then(function() {
                expect(userPageHeader.getText()).toEqual(text, error);
            });
        })
	}
	this.clickUserName = function(){
        browser.wait(EC.visibilityOf(element(by.binding(Objects.userpage.locators.userName))), 30000, "User name field is not visible").then(function() {
            userName.click();
        });
		return this;
	}
	this.insertData = function(data){
		inputField.clear();
		inputField.sendKeys(data);
		return this;
	}

	this.editUsername = function(username){
		this.clickUserName();
		this.insertData(username);
		this.clickConfirm();
	}
	this.returnUsername = function(){
		return userName.getText();
	}
	this.clickUserLocation = function(){
		userLocation.click();
		return this;
	}

	this.clickConfirm = function(){
		confirmButton.click();
		return this;
	}
	this.editLocation = function(location){
		this.clickUserLocation();
		this.insertData(location);
		this.clickConfirm();
	}
	this.returnUserLocation = function(){
		return userLocation.getText();
	}
	this.clickOfficePhone = function(){
		officePhone.click();
		return this;
	}

	this.editOfficePhone = function(officePhone){
		this.clickOfficePhone();
		this.insertData(officePhone);
		this.clickConfirm();
	}
	this.returnOfficePhone = function(){
		return officePhone.getText();
	}
	this.clickImAccount = function(){
		imAccount.click();
		return this;
	}

	this.editImAccount = function(imAccount){
		this.clickImAccount();
		this.insertData(imAccount);
		this.clickConfirm();
	}
	this.returnImAccount = function(){
		return imAccount.getText();
	}
	this.clickShortImAccount = function(){
		shortImaccount.click();
		return this;
	}

	this.editShortImAccount = function(shortImAccount){
		this.clickShortImAccount();
		this.insertData(shortImAccount);
		this.clickConfirm();
	}
	this.returnShortImAccount = function(){
		return shortImaccount.getText();
	}
	this.clickMobilePhone = function(){
		mobilephone.click();
		return this;
	}

	this.editMobilePhone = function(mobilephone){
		this.clickMobilePhone();
		this.insertData(mobilephone);
		this.clickConfirm();
	}
	this.returnMobilePhone = function(){
		return mobilephone.getText();
	}
	this.clickCompanyName = function(){
		companyName.click();
		return this;
	}

	this.editCompanyName = function(company){
		this.clickCompanyName();
		this.insertData(company);
		this.clickConfirm();
	}
	this.returnCompanyName = function(){
		return companyName.getText();
	}
	this.clickAddressOne = function(){
		addressOne.click();
		return this;
	}

	this.editAddressOne = function(address){
		this.clickAddressOne();
		this.insertData(address);
		this.clickConfirm();
	}
	this.returnAddressOne = function(){
		return addressOne.getText();
	}
	this.clickAddressTwo = function(){
		addressTwo.click();
		return this;
	}

	this.editAddressTwo = function(address){
		this.clickAddressTwo();
		this.insertData(address);
		this.clickConfirm();
	}
	this.returnAddressTwo = function(){
		return addressTwo.getText();
	}
	this.clickCity = function(){
		city.click();
		return this;
	}

	this.editCity = function(city){
		this.clickCity();
		this.insertData(city);
		this.clickConfirm();
	}
	this.returnCity = function(){
		return city.getText();
	}
	this.clickState = function(){
		state.click();
		return this;
	}

	this.editState = function(state){
		this.clickState();
		this.insertData(state);
		this.clickConfirm();
	}
	this.returnState = function(){
		return state.getText();
	}
	this.clickZip = function(){
       zip.click();
       return this;
	}

	this.editZip = function(zip){
		this.clickZip();
		this.insertData(zip);
		this.clickConfirm();
	}
	this.returnZip = function(){
		return zip.getText();
	}
	this.clickMainOfficePhone = function(){
		mainOfficePhone.click();
		return this;
	}

	this.editMainOfficePhone = function(phone){
		this.clickMainOfficePhone();
		this.insertData(phone);
		this.clickConfirm();
	}
	this.returnMainOfficePhone = function(){
		return mainOfficePhone.getText();		
	}
	this.clickFax = function(){
		fax.click();
		return this;
	}

	this.editFax = function(fax){
		this.clickFax();
		this.insertData(fax);
		this.clickConfirm();
	}
	this.returnFax = function(){
		return fax.getText();
	}
	this.clickWebsite = function(){
		website.click();
		return this;
	}

	this.editWebSite = function(website){
		this.clickWebsite();
		this.insertData(website);
		this.clickConfirm();
	}
	this.returnWebSite = function(){
		return website.getText();
	}
	this.changePicture = function() {
		changeProfilePic.click().then(function () {
			utils.uploadPng();
		});
	}
	
	

};

UserPage.prototype = basePage;
module.exports = new UserPage();
