var HtmlScreenshotReporter = require(process.env['USERPROFILE'] + '/node_modules/protractor-jasmine2-screenshot-reporter');  
var utils = require('../util/utils.js');
var reporter = new HtmlScreenshotReporter({
  dest: 'target/screenshots_'+ utils.returnToday("_"),
  filename: 'AutoTestRun-report.html'
});
exports.config = {
    //seleniumAddress: 'http://localhost:4444/wd/hub',
    directConnect: true,
    defaultTimeoutInterval: 20000,

    cababilities: {
        'browserName': 'chrome'
    },

    framework: 'jasmine2',

    directConnect: true,
    //next 3 lines are for run on selenium GRID, the path should be changed to take drivers from ACM configuraion project
    //not working for safari browser, should be investigated why? 
    //seleniumAddress: 'http://localhost:4444/wd/hub',        
    //seleniumArgs: '-Dwebdriver.ie.driver='+process.env['USERPROFILE']+'/AppData/Roaming/npm/node_modules/protractor/selenium/IEDriverServer_x64_2.52.0.exe',
    //seleniumArgs: '-Dwebdriver.safari.driver='+process.env['USERPROFILE']+'/AppData/Roaming/npm/node_modules/protractor/selenium/SafariDriver.safariextz',
    // Capabilities to be passed to the webdriver instance.
    multiCapabilities: [{
        'browserName': 'chrome',
        'maxInstances': 5
    }],
    //if you want to run in paralel comment previous line and uncomment all above
    //        }, {
    //              'browserName': 'internet explorer',  
    //              'maxInstances': 5,      
    //              'version': '11' 
    //        }, {
    //              'browserName': 'firefox',  
    //              'maxInstances': 5
    //        
    //        }, {
    //          'browserName': 'safari' ,
    //          'maxInstances': 5
    //        }],


    specs: [            
          '../test_spec/dashboard_test.spec.js',
           '../test_spec/user_test.spec.js',
           '../test_spec/task_test.spec.js',
           '../test_spec/case_test.spec.js'
    ],
    

    jasmineNodeOpts: {
        showColors: true,
        defaultTimeoutInterval: 90000

    },
    beforeLaunch: function() {
        return new Promise(function(resolve){
          reporter.beforeLaunch(resolve);
        });
      },
    onPrepare: function () {
    	jasmine.getEnv().addReporter(reporter);
        browser.driver.manage().window().maximize();    
        browser.driver.get('https://cloud.arkcase.com/arkcase/login'); 
        browser.manage().timeouts().setScriptTimeout(60000);
    },
    afterLaunch: function(exitCode) {
        return new Promise(function(resolve){
          reporter.afterLaunch(resolve.bind(this, exitCode));
        });
      }
};
