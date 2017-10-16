var helpers = function helpers() {
    var robot = require(process.env['USERPROFILE'] + '/node_modules/robotjs');
    var sleep = require(process.env['USERPROFILE'] + '/node_modules/system-sleep');
    var home = process.env['USERPROFILE'];
    var uplaodPathPng = home + '\\.arkcase\\seleniumTests\\filesForUpload\\imageprofile.png';
    var uplaodPathDocx = home + '\\.arkcase\\seleniumTests\\filesForUpload\\ArkCaseTesting.docx';
    var uploadPathPdf = home + '\\.arkcase\\seleniumTests\\filesForUpload\\caseSummary.pdf';
    var uplaodPathXlsx = home + '\\.arkcase\\seleniumTests\\filesForUpload\\caseSummary.xlsx';
    var uploadPathLogo = home + '\\.arkcase\\seleniumTests\\filesForUpload\\ArkCaseLogo.png';
    var Users = require('../json/Users.json');

    this.uploadPng = function() {

            sleep(2000);
            robot.typeStringDelayed(uplaodPathPng, 14000);
            robot.keyTap("enter");
            browser.sleep(5000);
        },

        this.uploadPdf = function() {

            sleep(2000);
            robot.typeStringDelayed(uploadPathPdf, 14000);
            robot.keyTap("enter");
            browser.sleep(5000);
        },

        this.uploadXlsx = function() {

            sleep(2000);
            robot.typeStringDelayed(uplaodPathXlsx, 14000);
            robot.keyTap("enter");
            browser.sleep(5000);
        },

        this.uploadDocx = function() {

            sleep(2000);
            robot.typeStringDelayed(uplaodPathDocx, 14000);
            robot.keyTap("enter");
            browser.sleep(5000);

        },

        this.uploadLogo = function() {

            sleep(2000);
            robot.typeStringDelayed(uploadPathLogo, 14000);
            robot.keyTap("enter");
            browser.sleep(5000);

        },

        this.mouseMoveToRoot = function() {

            robot.moveMouse(853, 508);
            robot.mouseClick("right");
        },

        this.mouseMoveToFirstDocument = function() {

            robot.moveMouse(883, 538);
            robot.mouseClick("right");
        }
    this.returnToday = function(sign) {

        var now = new Date();
        var day = ("0" + now.getDate()).slice(-2);
        var month = ("0" + (now.getMonth() + 1)).slice(-2);
        return today = (month) + sign + (day) + sign + now.getFullYear();
    }

    this.rightClick = function() {
        browser.actions().click(protractor.Button.RIGHT).perform();
    }

    this.returnDate = function(sign, NoDays) {

        var now = new Date();
        var dueDate = new Date();
        dueDate.setDate(now.getDate() + NoDays);
        var day = ("0" + dueDate.getDate()).slice(-2);
        var month = ("0" + (dueDate.getMonth() + 1)).slice(-2);
        return dueDateOut = (month) + sign + (day) + sign + dueDate.getFullYear();
    }

    this.returnTimeTrackingWeek = function() {

        var now = new Date();
        var monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ];
        var currentMonth = monthNames[now.getMonth()];
        var first = new Date(now.setDate(now.getDate() - now.getDay()));
        var last = new Date(now.setDate(now.getDate() - now.getDay() + 6));
        var firstday = first.getDate();
        var lastday = last.getDate();
        return week = (currentMonth) + " " + (firstday) + " - " + (currentMonth) + " " + (lastday) + " " + now.getFullYear();
    }

    this.returnCurrentMonth = function() {
        var now = new Date();
        var month = ("0" + (now.getMonth() + 1)).slice(-2);
        return currentMont = (month);

    }

    this.returnPreviousMonth = function() {
        var now = new Date();
        var month = ("0" + (now.getMonth() + 1)).slice(-2) - 1;
        var previousMonth;
        if (month == 0) {
            previousMonth = 12;
            return previousMonth;
        } else {
            return month;
        }
    }

    this.returnCurrentYear = function() {
        now = new Date();
        var currentYear = ("0" + now.getFullYear()).slice(-4);
        return currentYear;
    }

    this.returnpreviousYear = function() {
        now = new Date();
        var previousYear = ("0" + now.getFullYear()).slice(-4) - 1;
        return previousYear;

    }

    this.previousWeek = function() {

        var now = new Date();
        var day1 = ("0" + now.getDate()).slice(-2);
        var day2 = ("0" + now.getDate()).slice(-2) - 1;
        var day3 = ("0" + now.getDate()).slice(-2) - 2;
        var day4 = ("0" + now.getDate()).slice(-2) - 3;
        var day5 = ("0" + now.getDate()).slice(-2) - 4;
        var day6 = ("0" + now.getDate()).slice(-2) - 5;
        var day7 = ("0" + now.getDate()).slice(-2) - 6;
        var week = "" + [day1, day2, day3, day4, day5, day6, day7] + "";
        return week;

    }
    this.readGroupsFromJson = function(user) {
        var dictionarydoc = Users.response.docs;
        for (var i in dictionarydoc) {
            var userName = dictionarydoc[i].object_id_s;
            if (userName == user) {
                return dictionarydoc[i].groups_id_ss;
            }
        }
    }
    this.returnNumberOfGroupsFromJson = function(user) {

        var dictionarydoc = Users.response.docs;
        for (var i in dictionarydoc) {
            var userName = dictionarydoc[i].object_id_s;
            if (userName == user) {
                return dictionarydoc[i].groups_id_ss.length;
            }
        }
    }


};

module.exports = new helpers();
