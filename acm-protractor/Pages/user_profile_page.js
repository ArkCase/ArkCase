var UserPage = function() {

    this.userName = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[1]/section/div/div/div[2]/h4[2]/a'));
    this.userNameInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[1]/section/div/div/div[2]/h4[2]/form/div/input'));
    this.userNameConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[1]/section/div/div/div[2]/h4[2]/form/div/span/button[1]'));
    this.userNavigation = element(by.css('.fullname'));
    this.userNavigationProfile = element(by.xpath('html/body/div[1]/div/div[1]/nav/div[1]/div/div/div[2]/ul/li[1]/a'));
    this.userPageHeader = element(by.xpath('.//*[@class="module-header"]/h3'));

    this.userLocation = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[1]/a'));
    this.userLocationInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[1]/form/div/input'));
    this.userLocationConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[1]/form/div/span/button[1]'));

    this.officePhone = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[1]/a'));
    this.officePhoneInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[1]/form/div/input'));
    this.officePhoneConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[1]/form/div/span/button[1]'));

    this.imAccount = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/a[1]'));
    this.imAccountInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/form/div/input'));
    this.imAccountConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/form/div/span/button[1]'));

    this.shortImaccount = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/a[2]'));
    this.shortImaccountInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/form/div/input'));
    this.shortImaccountConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/form/div/span/button[1]'));

    this.mobilephone = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[2]/a'));
    this.mobilephoneInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[2]/form/div/input'));
    this.mobilephoneConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[2]/form/div/span/button[1]'));

    this.companyName = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[1]/a'));
    this.companyNameInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[1]/form/div/input'));
    this.companyNameConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[1]/form/div/span/button[1]'));

    this.addressOne = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[2]/a'));
    this.addressOneInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[2]/form/div/input'));
    this.addressOneConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[2]/form/div/span/button[1]'));

    this.addressTwo = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[3]/a'));
    this.addressTwoInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[3]/form/div/input'));
    this.addressTwoConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[3]/form/div/span/button[1]'));

    this.city = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[4]/a'));
    this.cityInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[4]/form/div/input'));
    this.cityConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[4]/form/div/span/button[1]'));

    this.state = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[5]/a'));
    this.stateInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[5]/form/div/input'));
    this.stateConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[5]/form/div/span/button[1]'));

    this.zip = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[6]/a'));
    this.zipInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[6]/form/div/input'));
    this.zipConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[6]/form/div/span/button[1]'))

    this.mainOfficePhone = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[1]/a'));
    this.mainOfficePhoneInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[1]/form/div/input'));
    this.mainOfficePhoneConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[1]/form/div/span/button[1]'));

    this.fax = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[2]/a'));
    this.faxInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[2]/form/div/input'));
    this.faxConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[2]/form/div/span/button[1]'));

    this.website = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[3]/a'));
    this.websiteInput = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[3]/form/div/input'));
    this.websiteConfirmBtn = element(by.xpath('html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[3]/form/div/span/button[1]'));

};

module.exports = new UserPage();
