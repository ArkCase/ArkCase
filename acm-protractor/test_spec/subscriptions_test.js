var Objects = require('../json/Objects.json');
var loginPage = require('../Pages/login_page.js');
var subscriptionPage = require('../Pages/subscriptions_page.js')
var utils = require('../util/utils.js');




function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 30000);

}

describe('Subscriptions page tests', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });

    it('should select Case File parent type and verift it the result table ', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Case File");
        expect(subscriptionPage.returnResultEvent()).toContain("Subscription on");
        expect(subscriptionPage.returnResultParentType()).toEqual("CASE_FILE");
        expect(subscriptionPage.returnResultParentName()).not.toEqual(0);
        expect(subscriptionPage.returnResultModified()).not.toEqual(0);
    });


    it('should select Complaint as  parent type and verift it the result table ', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Complaint");
        expect(subscriptionPage.returnResultEvent()).toContain("Subscription on");
        expect(subscriptionPage.returnResultParentType()).toEqual("COMPLAINT");
        expect(subscriptionPage.returnResultParentName()).not.toEqual(0);
        expect(subscriptionPage.returnResultModified()).not.toEqual(0);
    });

    it('should select Task as parent type and verift it the result table ', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Task");
        expect(subscriptionPage.returnResultEvent()).toContain("Subscription on");
        expect(subscriptionPage.returnResultParentType()).toEqual("TASK");
        expect(subscriptionPage.returnResultParentName()).not.toEqual(0);
        expect(subscriptionPage.returnResultModified()).not.toEqual(0);
    });

    it('should search for case and  verify it in the result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.searchForSubscription("case")
        expect(subscriptionPage.returnResultParentType()).toEqual("CASE_FILE");
    });

    it('should select previous week and verify the mdified date in result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Previous Week")
        expect(subscriptionPage.returnResultModified()).toContain(utils.returnToday("/"))

    });

    it('should select previous month and verify the mdified date in result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Previous Month")
        expect(subscriptionPage.returnResultModified()).toContain(utils.returnToday("/"))

    });

    it('should select previous Year and verify the mdified date in result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Previous Year")
        expect(subscriptionPage.returnResultModified()).toContain(utils.returnToday("/"))

    });


});
