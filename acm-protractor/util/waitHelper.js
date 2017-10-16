
    var EC = protractor.ExpectedConditions;


    var waitElementToBeVisible = function (elm) {
        browser.wait(EC.visibilityOf(elm), 2000);
    };

    var waitElementToBeClickable = function (elm) {

        browser.wait(EC.elementToBeClickable(elm), 2000);
    };

    var waitUntilReady = function (elm) {
        return browser.wait(function () {
            return elm.isPresent();
        }, 2000).then(function () {
            return browser.wait(function () {
                return elm.isDisplayed();
            }, 2000);
        });
    };

    var waitElementToDisappear = function (locator) {
        browser.wait(function () {
            return browser.isElementPresent(locator) //if element is already present, this line will evaluate to true
                .then(function (presenceOfElement) {
                    return !presenceOfElement
                }); // this modifies the previous line so that it evaluates to false until the element is no longer present.
        }, 2000);
    }

    var waitForCheckboxToBeChecked = function (elem) {
        browser.wait(function () {
            return (elem.getAttribute('checked')).then(function (isElementChecked) {
                return (isElementChecked);
            });
        }, 2000);
    }


exports.waitElementToBeVisible = waitElementToBeVisible;
exports.waitElementToBeClickable = waitElementToBeClickable;
exports.waitElementToDisappear = waitElementToDisappear;
exports.waitUntilReady = waitUntilReady;
exports.waitForCheckboxToBeChecked = waitForCheckboxToBeChecked;