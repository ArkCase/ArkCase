var helpers = function helpers() {
    var robot = require(process.env['USERPROFILE'] + '/node_modules/robotjs');
    var sleep = require(process.env['USERPROFILE'] + '/node_modules/system-sleep');
    var home = process.env['USERPROFILE'];
    var uplaodPathPng = home + '\\.arkcase\\seleniumTests\\filesForUpload\\imageprofile.png';
    var uplaodPathDocx = home + '\\.arkcase\\seleniumTests\\filesForUpload\\ArkCaseTesting.docx';
    var uploadPathPdf = home + '\\.arkcase\\seleniumTests\\filesForUpload\\caseSummary.pdf';
    var uplaodPathXlsx = home + '\\.arkcase\\seleniumTests\\filesForUpload\\caseSummary.xlsx';

    this.uploadPng = function() {

            sleep(2000);
            robot.typeStringDelayed(uplaodPathPng, 14000);
            robot.keyTap("enter");
        },

        this.uploadPdf = function() {

            sleep(2000);
            robot.typeStringDelayed(uploadPathPdf, 14000);
            robot.keyTap("enter");
        },

        this.uploadXlsx = function() {

            sleep(2000);
            robot.typeStringDelayed(uplaodPathXlsx, 14000);
            robot.keyTap("enter");
        },

        this.uploadDocx = function() {

            sleep(2000);
            robot.typeStringDelayed(uplaodPathDocx, 14000);
            robot.keyTap("enter");

        },
        this.mouseMoveToRoot = function() {

            robot.moveMouse(853, 508);
            robot.mouseClick("right");
        },

        this.mouseMoveToFirstDocument = function() {

            robot.moveMouse(883, 538);
            robot.mouseClick("right");
        }
        this.returnToday = function(sign){
 		   var now = new Date();
 		   var day = ("0" + now.getDate()).slice(-2);
 		   var month = ("0" + (now.getMonth() + 1)).slice(-2);
 		   return today = (month) + sign + (day) + sign + now.getFullYear();
 	   }
 	   this.rightClick = function () {
           browser.actions().click(protractor.Button.RIGHT).perform();
       }
       this.returnDate = function (sign, NoDays) {
           var now = new Date();
           var dueDate = new Date();
           dueDate.setDate(now.getDate()+NoDays);
           var day = ("0" + dueDate.getDate()).slice(-2);
           var month = ("0" + (dueDate.getMonth() + 1)).slice(-2);
           return dueDateOut = (month) + sign + (day) + sign + dueDate.getFullYear();
       }



};

module.exports = new helpers();
