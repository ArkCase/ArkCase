var logger = require('../log');
var waitHelper = require('../util/waitHelper.js');
var util = require('../util/utils.js');
var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var description = element.all(by.repeater(Objects.notificationPage.locators.notificationCol)).get(0);
var modifiedBy = element.all(by.repeater(Objects.notificationPage.locators.notificationCol)).get(3);


var NotificationPage = function() {

    this.returnDescription = function () {
        return description.getText();
    };

    this.returnModifiedBy = function () {
        return modifiedBy.getText();
    }
};


NotificationPage.prototype = basePage;
module.exports = new NotificationPage();
