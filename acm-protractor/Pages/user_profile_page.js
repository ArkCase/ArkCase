var UserPage = function() {

    this.userName = element(by.xpath('.//*[@class="col-xs-8"]/h4[2]/a'));
    this.userNameInput = element(by.xpath('.//*[@class="col-xs-8"]/h4[2]/form/div/input'));
    this.userNameConfirmBtn = element(by.xpath('.//*[@class="col-xs-8"]/h4[2]/form/div/span/button[1]'));
    this.userNavigation = element(by.css('.fullname'));
    this.userNavigationProfile = element(by.linkText('Profile'));
    this.userPageHeader = element(by.xpath('.//*[@class="module-header"]/h3'));

    this.userLocation = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[1]/a')).get(0);
    this.userLocationInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[1]/form/div/input'));
    this.userLocationConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[1]//form/div/span/button[1]'));

    this.officePhone = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[1]/a')).get(0);
    this.officePhoneInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[1]/form/div/input'));
    this.officePhoneConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[1]/form/div/span/button[1]'));

    this.imAccount = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[2]/a[1]')).get(0);
    this.imAccountInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[2]/form/div/input'));
    this.imAccountConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[2]/form/div/span/button[1]'));

    this.shortImaccount = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[2]/a[2]')).get(0);
    this.shortImaccountInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[2]/form/div/input'));
    this.shortImaccountConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[2]/form/div/span/button[1]'));

    this.mobilephone = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[2]/a')).get(0);
    this.mobilephoneInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[2]/form/div/input'));
    this.mobilephoneConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[2]/form/div/span/button[1]'));

    this.companyName = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[1]/a')).get(1);
    this.companyNameInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[1]/form/div/input'));
    this.companyNameConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[1]/form/div/span/button[1]'));

    this.addressOne = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[2]/a')).get(2);
    this.addressOneInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[2]/form/div/input'));
    this.addressOneConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[2]/form/div/span/button[1]'));

    this.addressTwo = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[3]/a')).get(0);
    this.addressTwoInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[3]/form/div/input'));
    this.addressTwoConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[3]/form/div/span/button[1]'));

    this.city = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[4]/a')).get(0);
    this.cityInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[4]/form/div/input'));
    this.cityConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[4]/form/div/span/button[1]'));

    this.state = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[5]/a')).get(0);
    this.stateInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[5]/form/div/input'));
    this.stateConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[5]/form/div/span/button[1]'));

    this.zip = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[6]/a')).get(0);
    this.zipInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[6]/form/div/input'));
    this.zipConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[1]/h4[6]/form/div/span/button[1]'))

    this.mainOfficePhone = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[1]/a')).get(1);
    this.mainOfficePhoneInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[1]/form/div/input'));
    this.mainOfficePhoneConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[1]/form/div/span/button[1]'));

    this.fax = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[2]/a')).get(1);
    this.faxInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[2]/form/div/input'));
    this.faxConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[2]/form/div/span/button[1]'));

    this.website = element.all(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[3]/a')).get(0);
    this.websiteInput = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[3]/form/div/input'));
    this.websiteConfirmBtn = element(by.xpath('.//*[@class="scroll-body ng-scope"]/section/ul/li/div/div[2]/h4[3]/form/div/span/button[1]'));

    this.changeProfilePic = element(by.xpath('//*[@id="lnkChangePicture"]/u/span'));



};

module.exports = new UserPage();
