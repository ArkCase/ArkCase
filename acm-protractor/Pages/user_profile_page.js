var Objects = require('../json/Objects.json');
var utils = require('../util/utils.js');
var basePage = require('./base_page.js');
var userName = element(by.xpath(Objects.userpage.locators.userName));
var userNameInput = element(by.xpath(Objects.userpage.locators.userNameInput));
var userNameConfirmBtn = element(by.xpath(Objects.userpage.locators.userNameConfirmBtn));
var userNavigation = element(by.css(Objects.userpage.locators.userNavigation));
var userNavigationProfile = element(by.linkText(Objects.userpage.locators.userNavigationProfile));
var userPageHeader = element(by.xpath(Objects.userpage.locators.userPageHeader));

var userLocation = element.all(by.xpath(Objects.userpage.locators.userLocation)).get(0);
var userLocationInput = element(by.xpath(Objects.userpage.locators.userLocationInput));
var userLocationConfirmBtn = element(by.xpath(Objects.userpage.locators.userLocationConfirmBtn));

var officePhone = element.all(by.xpath(Objects.userpage.locators.officePhone)).get(0);
var officePhoneInput = element(by.xpath(Objects.userpage.locators.officePhoneInput));
var officePhoneConfirmBtn = element(by.xpath(Objects.userpage.locators.officePhoneConfirmBtn));

var imAccount = element.all(by.xpath(Objects.userpage.locators.imAccount)).get(0);
var imAccountInput = element(by.xpath(Objects.userpage.locators.imAccountInput));
var imAccountConfirmBtn = element(by.xpath(Objects.userpage.locators.imAccountConfirmBtn));

var shortImaccount = element.all(by.xpath(Objects.userpage.locators.shortImAccount)).get(0);
var shortImaccountInput = element(by.xpath(Objects.userpage.locators.shortImAccountInput));
var shortImaccountConfirmBtn = element(by.xpath(Objects.userpage.locators.shortImAccountConfirmBtn));

var mobilephone = element.all(by.xpath(Objects.userpage.locators.mobilephone)).get(0);
var mobilephoneInput = element(by.xpath(Objects.userpage.locators.mobilephoneInput));
var mobilephoneConfirmBtn = element(by.xpath(Objects.userpage.locators.mobilephoneConfirmBtn));

var companyName = element.all(by.xpath(Objects.userpage.locators.companyName)).get(1);
var companyNameInput = element(by.xpath(Objects.userpage.locators.companyNameInput));
var companyNameConfirmBtn = element(by.xpath(Objects.userpage.locators.companyNameConfirmBtn));

var addressOne = element.all(by.xpath(Objects.userpage.locators.addressOne)).get(2);
var addressOneInput = element(by.xpath(Objects.userpage.locators.addressOneInput));
var addressOneConfirmBtn = element(by.xpath(Objects.userpage.locators.addressOneConfirmBtn));

var addressTwo = element.all(by.xpath(Objects.userpage.locators.addressTwo)).get(0);
var addressTwoInput = element(by.xpath(Objects.userpage.locators.addressTwoInput));
var addressTwoConfirmBtn = element(by.xpath(Objects.userpage.locators.addressTwoConfirmBtn));

var city = element(by.xpath(Objects.userpage.locators.city));
var cityInput = element(by.xpath(Objects.userpage.locators.cityInput));
var cityConfirmBtn = element(by.xpath(Objects.userpage.locators.cityInputConfirmBtn));

var state = element(by.xpath(Objects.userpage.locators.state));
var stateInput = element(by.xpath(Objects.userpage.locators.stateInput));
var stateConfirmBtn = element(by.xpath(Objects.userpage.locators.stateInputConfirmBtn));

var zip = element(by.xpath(Objects.userpage.locators.zip));
var zipInput = element(by.xpath(Objects.userpage.locators.zipInput));
var zipConfirmBtn = element(by.xpath(Objects.userpage.locators.zipInputConfirmBtn))

var mainOfficePhone = element.all(by.xpath(Objects.userpage.locators.mainOfficePhone)).get(1);
var mainOfficePhoneInput = element(by.xpath(Objects.userpage.locators.mainOfficePhoneInput));
var mainOfficePhoneConfirmBtn = element(by.css(Objects.userpage.locators.mainOfficePhoneConfirmBtn));

var fax = element.all(by.xpath(Objects.userpage.locators.fax)).get(1);
var faxInput = element(by.xpath(Objects.userpage.locators.faxInput));
var faxConfirmBtn = element(by.xpath(Objects.userpage.locators.faxConfirmBtn));

var website = element(by.xpath(Objects.userpage.locators.website));
var websiteInput = element(by.xpath(Objects.userpage.locators.websiteInput));
var websiteConfirmBtn = element(by.xpath(Objects.userpage.locators.websiteConfirmBtn));

var changeProfilePic = element(by.xpath(Objects.userpage.locators.changeProfilePic));

var UserPage = function(){ 
	this.clickUserNavigation = function(){
		userNavigation.click();
		return this;
	}
	this.returnUserNavigationProfile = function(){
		return userNavigationProfile.getText();
	}
	this.clickUserNavigationProfile = function(){
		userNavigationProfile.click();
		return this;
	}
	this.returnUserPageHeader = function(){
		return userPageHeader.getText();
	}
	this.clickUserName = function(){
		userName.click();
		return this;
	}
	this.insertUserName = function(username){
		userNameInput.clear();
		userNameInput.sendKeys(username);
		return this;
	}
	this.clickUsernameConfirm = function(){
		userNameConfirmBtn.click();
		return this;
	}
	this.editUsername = function(username){
		this.clickUserName();
		this.insertUserName(username);
		this.clickUsernameConfirm();		
	}
	this.returnUsername = function(){
		return userName.getText();
	}
	this.clickUserLocation = function(){
		userLocation.click();
		return this;
	}
	this.insertUserLocation = function(location){
		userLocationInput.clear();
		userLocationInput.sendKeys(location)
		return this;
	}
	this.clickUserLocationConfirm = function(){
		userLocationConfirmBtn.click();
		return this;
	}
	this.editLocation = function(location){
		this.clickUserLocation();
		this.insertUserLocation(location);
		this.clickUserLocationConfirm();		
	}
	this.returnUserLocation = function(){
		return userLocation.getText();
	}
	this.clickOfficePhone = function(){
		officePhone.click();
		return this;
	}
	this.insertOfficePhone = function(officePhone){
		officePhoneInput.clear();
		officePhoneInput.sendKeys(officePhone);
		return this;
	}
	this.clickOfficePhoneConfirm = function(){
		officePhoneConfirmBtn.click();
		return this;
	}
	this.editOfficePhone = function(officePhone){
		this.clickOfficePhone();
		this.insertOfficePhone(officePhone);
		this.clickOfficePhoneConfirm();
	}
	this.returnOfficePhone = function(){
		return officePhone.getText();
	}
	this.clickImAccount = function(){
		imAccount.click();
		return this;
	}
	this.insertImAccount = function(imAccount){
		imAccountInput.clear();
		imAccountInput.sendKeys(imAccount);
		return this;
	}
	this.clickImAccountConfirm = function(){
		imAccountConfirmBtn.click();
		return this;
	}
	this.editImAccount = function(imAccount){
		this.clickImAccount();
		this.insertImAccount(imAccount);
		this.clickImAccountConfirm();
	}
	this.returnImAccount = function(){
		return imAccount.getText();
	}
	this.clickShortImAccount = function(){
		shortImaccount.click();
		return this;
	}
	this.insertShortImAccount = function(shortImAccount){
		shortImaccountInput.clear();
		shortImaccountInput.sendKeys(shortImAccount);
		return this;
	}
	this.clickShortImAccountConfirm = function(){
		shortImaccountConfirmBtn.click();
		return this;
	}
	this.editShortImAccount = function(shortImAccount){
		this.clickShortImAccount();
		this.insertShortImAccount(shortImAccount);
		this.clickShortImAccountConfirm();
	}
	this.returnShortImAccount = function(){
		return shortImaccount.getText();
	}
	this.clickMobilePhone = function(){
		mobilephone.click();
		return this;
	}
	this.insertMobilePhone = function(mobilephone){
	   mobilephoneInput.clear();
	   mobilephoneInput.sendKeys(mobilephone);
	   return this;
	}
	this.clickMobilePhoneConfirm = function(){
		mobilephoneConfirmBtn.click();
		return this;
	}
	this.editMobilePhone = function(mobilephone){
		this.clickMobilePhone();
		this.insertMobilePhone(mobilephone);
		this.clickMobilePhoneConfirm();
	}
	this.returnMobilePhone = function(){
		return mobilephone.getText();
	}
	this.clickCompanyName = function(){
		companyName.click();
		return this;
	}
	this.insertCompanyName = function(company){
		companyNameInput.clear();
		companyNameInput.sendKeys(company);
		return this;
	}
	this.clickCompanyNameConfirm = function(){
		companyNameConfirmBtn.click();
		return this;
	}
	this.editCompanyName = function(company){
		this.clickCompanyName();
		this.insertCompanyName(company);
		this.clickCompanyNameConfirm();
	}
	this.returnCompanyName = function(){
		return companyName.getText();
	}
	this.clickAddressOne = function(){
		addressOne.click();
		return this;
	}
	this.insertAddressOne = function(address){
		addressOneInput.clear();
		addressOneInput.sendKeys(address);
		return this;
	}
	this.clickAddressOneConfirm = function(){
		addressOneConfirmBtn.click();
		return this;
	}
	this.editAddressOne = function(address){
		this.clickAddressOne();
		this.insertAddressOne(address);
		this.clickAddressOneConfirm();
	}
	this.returnAddressOne = function(){
		return addressOne.getText();
	}
	this.clickAddressTwo = function(){
		addressTwo.click();
		return this;
	}
	this.insertAddressTwo = function(address){
		addressTwoInput.clear();
		addressTwoInput.sendKeys(address);
		return this;
	}
	this.clickAddressTwoConfirm = function(){
		addressTwoConfirmBtn.click();
		return this;
	}
	this.editAddressTwo = function(address){
		this.clickAddressTwo();
		this.insertAddressTwo(address);
		this.clickAddressTwoConfirm();
	}
	this.returnAddressTwo = function(){
		return addressTwo.getText();
	}
	this.clickCity = function(){
		city.click();
		return this;
	}
	this.insertCity = function(city){
		cityInput.clear();
		cityInput.sendKeys(city);
		return this;
	}
	this.clickCityConfirm = function(){
		cityConfirmBtn.click();
		return this;
	}
	this.editCity = function(city){
		this.clickCity();
		this.insertCity(city);
		this.clickCityConfirm();
	}
	this.returnCity = function(){
		return city.getText();
	}
	this.clickState = function(){
		state.click();
		return this;
	}
	this.insertState = function(state){
		stateInput.clear();
		stateInput.sendKeys(state);
		return this;
	}
	this.clickStateConfirm = function(){
		stateConfirmBtn.click();
		return this;
	}
	this.editState = function(state){
		this.clickState();
		this.insertState(state);
		this.clickStateConfirm();
	}
	this.returnState = function(){
		return state.getText();
	}
	this.clickZip = function(){
		zip.click();
		return this;
	}
	this.insertZip = function(zip){
		zipInput.clear();
		zipInput.sendKeys(zip);
		return this;
	}
	this.clickZipConfirm = function(){
		zipConfirmBtn.click();
		return this;
	}
	this.editZip = function(zip){
		this.clickZip();
		this.insertZip(zip);
		this.clickZipConfirm();
	}
	this.returnZip = function(){
		return zip.getText();
	}
	this.clickMainOfficePhone = function(){
		mainOfficePhone.click();
		return this;
	}
	this.insertMainOfficePhone = function(phone){
		mainOfficePhoneInput.clear();
		mainOfficePhoneInput.sendKeys(phone);
		return this;
	}
	this.clickMainOfficePhoneConfirm = function(){
		mainOfficePhoneConfirmBtn.click();
		return this;
	}
	this.editMainOfficePhone = function(phone){
		this.clickMainOfficePhone();
		this.insertMainOfficePhone(phone);
		this.clickMainOfficePhoneConfirm();
	}
	this.returnMainOfficePhone = function(){
		return mainOfficePhone.getText();		
	}
	this.clickFax = function(){
		fax.click();
		return this;
	}
	this.insertFax = function(fax){
		faxInput.clear();
		faxInput.sendKeys(fax);
		return this;
	}
	this.clickFaxConfirm = function(){
		faxConfirmBtn.click();
		return this;
	}
	this.editFax = function(fax){
		this.clickFax();
		this.insertFax(fax);
		this.clickFaxConfirm();
	}
	this.returnFax = function(){
		return fax.getText();
	}
	this.clickWebsite = function(){
		website.click();
		return this;
	}
	this.insertWebsite = function(website){
		websiteInput.clear();
		websiteInput.sendKeys(website);
		return this;
	}
	this.clickWebsiteConfirm = function(){
		websiteConfirmBtn.click();
		return this;
	}
	this.editWebSite = function(website){
		this.clickWebsite();
		this.insertWebsite(website);
		this.clickWebsiteConfirm();
	}
	this.returnWebSite = function(){
		return website.getText();
	}
	this.changePicture = function(){
		changeProfilePic.click();
		utils.uploadPng();
		return this;
	}
	
	
	

};

UserPage.prototype = basePage;
module.exports = new UserPage();
