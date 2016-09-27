var Objects = require('../json/Objects.json');
var UserPage = function() {

    this.userName = element(by.xpath(Objects.userpage.locators.userName));
    this.userNameInput = element(by.xpath(Objects.userpage.locators.userNameInput));
    this.userNameConfirmBtn = element(by.xpath(Objects.userpage.locators.userNameConfirmBtn));
    this.userNavigation = element(by.css(Objects.userpage.locators.userNavigation));
    this.userNavigationProfile = element(by.linkText(Objects.userpage.locators.userNavigationProfile));
    this.userPageHeader = element(by.xpath(Objects.userpage.locators.userPageHeader));

    this.userLocation = element.all(by.xpath(Objects.userpage.locators.userLocation)).get(0);
    this.userLocationInput = element(by.xpath(Objects.userpage.locators.userLocationInput));
    this.userLocationConfirmBtn = element(by.xpath(Objects.userpage.locators.userLocationConfirmBtn));

    this.officePhone = element.all(by.xpath(Objects.userpage.locators.officePhone)).get(0);
    this.officePhoneInput = element(by.xpath(Objects.userpage.locators.officePhoneInput));
    this.officePhoneConfirmBtn = element(by.xpath(Objects.userpage.locators.officePhoneConfirmBtn));

    this.imAccount = element.all(by.xpath(Objects.userpage.locators.imAccount)).get(0);
    this.imAccountInput = element(by.xpath(Objects.userpage.locators.imAccountInput));
    this.imAccountConfirmBtn = element(by.xpath(Objects.userpage.locators.imAccountConfirmBtn));

    this.shortImaccount = element.all(by.xpath(Objects.userpage.locators.shortImAccount)).get(0);
    this.shortImaccountInput = element(by.xpath(Objects.userpage.locators.shortImAccountInput));
    this.shortImaccountConfirmBtn = element(by.xpath(Objects.userpage.locators.shortImAccountConfirmBtn));

    this.mobilephone = element.all(by.xpath(Objects.userpage.locators.mobilephone)).get(0);
    this.mobilephoneInput = element(by.xpath(Objects.userpage.locators.mobilephoneInput));
    this.mobilephoneConfirmBtn = element(by.xpath(Objects.userpage.locators.mobilephoneConfirmBtn));

    this.companyName = element.all(by.xpath(Objects.userpage.locators.companyName)).get(1);
    this.companyNameInput = element(by.xpath(Objects.userpage.locators.companyNameInput));
    this.companyNameConfirmBtn = element(by.xpath(Objects.userpage.locators.companyNameConfirmBtn));

    this.addressOne = element.all(by.xpath(Objects.userpage.locators.addressOne)).get(2);
    this.addressOneInput = element(by.xpath(Objects.userpage.locators.addressOneInput));
    this.addressOneConfirmBtn = element(by.xpath(Objects.userpage.locators.addressOneConfirmBtn));

    this.addressTwo = element.all(by.xpath(Objects.userpage.locators.addressTwo)).get(0);
    this.addressTwoInput = element(by.xpath(Objects.userpage.locators.addressTwoInput));
    this.addressTwoConfirmBtn = element(by.xpath(Objects.userpage.locators.addressTwoConfirmBtn));

    this.city = element(by.xpath(Objects.userpage.locators.city));
    this.cityInput = element(by.xpath(Objects.userpage.locators.cityInput));
    this.cityConfirmBtn = element(by.xpath(Objects.userpage.locators.cityInputConfirmBtn));

    this.state = element(by.xpath(Objects.userpage.locators.state));
    this.stateInput = element(by.xpath(Objects.userpage.locators.stateInput));
    this.stateConfirmBtn = element(by.xpath(Objects.userpage.locators.stateInputConfirmBtn));

    this.zip = element(by.xpath(Objects.userpage.locators.zip));
    this.zipInput = element(by.xpath(Objects.userpage.locators.zipInput));
    this.zipConfirmBtn = element(by.xpath(Objects.userpage.locators.zipInputConfirmBtn))

    this.mainOfficePhone = element.all(by.xpath(Objects.userpage.locators.mainOfficePhone)).get(1);
    this.mainOfficePhoneInput = element(by.xpath(Objects.userpage.locators.mainOfficePhoneInput));
    this.mainOfficePhoneConfirmBtn = element(by.xpath(Objects.userpage.locators.mainOfficePhoneConfirmBtn));

    this.fax = element.all(by.xpath(Objects.userpage.locators.fax)).get(1);
    this.faxInput = element(by.xpath(Objects.userpage.locators.faxInput));
    this.faxConfirmBtn = element(by.xpath(Objects.userpage.locators.faxConfirmBtn));

    this.website = element(by.xpath(Objects.userpage.locators.website));
    this.websiteInput = element(by.xpath(Objects.userpage.locators.websiteInput));
    this.websiteConfirmBtn = element(by.xpath(Objects.userpage.locators.websiteConfirmBtn));

    this.changeProfilePic = element(by.xpath(Objects.userpage.locators.changeProfilePic));

};

module.exports = new UserPage();
