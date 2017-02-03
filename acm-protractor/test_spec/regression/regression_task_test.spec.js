var logger = require('../../log');
var utils = require('../../util/utils.js');
var taskPage = require('../../Pages/task_page.js');
var userPage = require('../../Pages/user_profile_page.js');
var loginPage = require('../../Pages/login_page.js');
var Objects = require('../../json/Objects.json');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');
var preferencesPage = require('../../Pages/preference_page.js');
var flag = false;

function testAsync(done) {
    // Wait two seconds, then set the flag to true
    setTimeout(function() {
        flag = true;

        // Invoke the special done callback
        done();
    }, 30000);
}


describe('Create new task ', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {

        loginPage.Logout();
    });


    it('should create new task with selcting group and verify it ', function() {

        taskPage.clickNewButton().clickTaskButton().insertGroupTaskData(Objects.taskspage.data.owningGroup, Objects.taskpage.data.Subject, utils.returnToday("/"), Objects.taskpage.data.DueDateInput, "Expedite", Objects.taskpage.data.percentCompleteInput).clickSave();
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.owningGroup, "Assigned group name is not correct");

    });

});
