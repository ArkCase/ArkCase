var Objects = require('../../json/Objects.json');
var loginPage = require('../../Pages/login_page.js');
var subscriptionPage = require('../../Pages/subscriptions_page.js')
var utils = require('../../util/utils.js');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');




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

    using([{ parentType: "Task", expected: "TASK" }, { parentType: "Complaint", expected: "COMPLAINT" },
        { parentType: "Case File", expected: "CASE_FILE" }
    ], function(data) {

        it('should select Task as parent type and verift it the result table ', function() {

            subscriptionPage.clickSubcriptionsModule();
            subscriptionPage.selectSubscription(data.parentType);
            subscriptionPage.clickParentTypeSort();
            expect(subscriptionPage.returnResultParentType()).toEqual(data.expected);

        });

    });


    it('should search for case and  verify it in the result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.searchForSubscription("case")
        expect(subscriptionPage.returnResultParentType()).toEqual("CASE_FILE");
    });

    it('should select previous week and verify the modified date in result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Previous Week")
        subscriptionPage.clikmodifiedBySort();
        subscriptionPage.returnModifiedByWeek();
    });

    it('should select previous month and verify the modified date in result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Previous Month")
        subscriptionPage.clikmodifiedBySort();
        subscriptionPage.returnModifiedByMonth();

    });

    it('should select previous Year and verify the modified date in result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Previous Year")
        subscriptionPage.clikmodifiedBySort();
        subscriptionPage.returnModifiedByYear();

    });
});
