var Objects = require('../json/Objects.json');
var loginPage = require('../Pages/login_page.js');
var EC = protractor.ExpectedConditions;
var preferencePageTitle = element(by.xpath(Objects.preferencesPage.locators.preferencePageTitle));
var disabledLabel = element(by.css(Objects.preferencesPage.locators.disabledLabel));
var enabledLabel = element(by.xpath(Objects.preferencesPage.locators.enabledLabel));
var widgetPanel = element(by.xpath(Objects.preferencesPage.locators.widgetPanel));


var preferencesPage = function() {

    this.enableWidget = function(preferencelink, widgetLink) {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.preferencesPage.locators.preferencePageTitle))), 30000, "Preference Page title is not displayed").then(function() {
            browser.sleep(10000);
            var preference = element(by.cssContainingText(Objects.preferencesPage.locators.preferenceModule, preferencelink));
            var widget = element(by.cssContainingText(Objects.preferencesPage.locators.preferenceWidgetTitle, widgetLink));
            var enableWidget = element(by.cssContainingText(Objects.preferencesPage.locators.preferenceWidgetTitle, widgetLink)).element(by.css('.pull-right'));
            preference.click().then(function() {
                browser.sleep(5000);
                browser.wait(EC.visibilityOf(element(by.linkText(widgetLink))), 10000,widgetLink + " "+ "Is not displayed").then(function() {
                    widget.click().then(function() {
                        browser.sleep(5000);
                        element(by.cssContainingText(Objects.preferencesPage.locators.preferenceWidgetTitle, widgetLink)).element(by.css(Objects.preferencesPage.locators.preferenceWidgetLabel)).getText().then(function(result) {

                            if (result == "Disabled") {

                                enableWidget.click();
                                browser.sleep(5000);
                                expect(enableWidget.getText()).toEqual("Enabled", "The widget" + " " + widgetLink + " " + "is not enabled");

                            } else {

                                console.log("Widget" + " " + widgetLink + " " + "is alredy enabled");

                            }

                        });
                    });
                });
            });
        });

        return this;
    }


    this.disabledWidget = function(preferencelink, widgetLink) {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.preferencesPage.locators.preferencePageTitle))), 30000, "Preference Page title is not displayed").then(function() {
            browser.sleep(10000);
            var preference = element(by.cssContainingText(Objects.preferencesPage.locators.preferenceModule, preferencelink));
            var widget = element(by.cssContainingText(Objects.preferencesPage.locators.preferenceWidgetTitle, widgetLink));
            var enableWidget = element(by.cssContainingText(Objects.preferencesPage.locators.preferenceWidgetTitle, widgetLink)).element(by.css(Objects.preferencesPage.locators.preferenceWidgetLabel));
            preference.click().then(function() {
                browser.sleep(5000);
                browser.wait(EC.visibilityOf(element(by.linkText(widgetLink))), 10000, widgetLink + " "+ "Is not displayed").then(function() {
                    widget.click().then(function() {
                        browser.sleep(5000);
                        element(by.cssContainingText(Objects.preferencesPage.locators.preferenceWidgetTitle, widgetLink)).element(by.css(Objects.preferencesPage.locators.preferenceWidgetLabel)).getText().then(function(result) {

                            if (result == "Enabled") {

                                enableWidget.click();
                                browser.sleep(5000);
                                expect(enableWidget.getText()).toEqual("Disabled", "The widget" + " " + widgetLink + " " + "is not disabled");

                            } else {

                                console.log("Widget" + " " + widgetLink + " " + "is alredy Disabled");

                            }

                        });
                    });
                });
            });
        });
        return this;
    }

};

module.exports = new preferencesPage();
