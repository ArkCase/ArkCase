//var HtmlScreenshotReporter = require(process.env['USERPROFILE'] + '/node_modules/protractor-jasmine2-screenshot-reporter');
var utils = require('../util/utils.js');
var Objects = require('../json/Objects.json');
var folderName = ('target/screenshots_' + utils.returnToday("_"));
var jasmineReporters = require(process.env['USERPROFILE'] + '/node_modules/jasmine-reporters');
var HTMLReport = require(process.env['USERPROFILE'] + '/node_modules/protractor-html-reporter');


exports.config = {
    //seleniumAddress: 'http://localhost:4444/wd/hub',
    directConnect: true,
    defaultTimeoutInterval: 200000,

    cababilities: {
        'browserName': 'chrome'
    },

    framework: 'jasmine2',
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
    //    }, {
    //          'browserName': 'internet explorer',
    //          'maxInstances': 5,
    //          'version': '11'
    //     }, {
    //            'browserName': 'firefox',
    //            'maxInstances': 5

    //      }, {
    //      'browserName': 'safari' ,
    //     'maxInstances': 5
    // }],

    specs: [


        // '../test_spec/functional/functional_test.spec.js'


        //any test can be run with command "protractor conf.js, just place it here"
    

    ],
    //any suite can be run with command "protractor conf.js --suite=selected"


    suites: {

        smoke: ['../test_spec/smoke/*.spec.js'],
        regression: ['../test_spec/regression/*.spec.js'],
        functional: ['../test_spec/functional/*.spec.js'],
        all: ['../test_spec/*/*.spec.js'],
        selected: ['../test_spec/functional/case_test.spec.js'],
    },

    jasmineNodeOpts: {
        showColors: true,

        defaultTimeoutInterval: 1200000

    },

    plugins: [{
        package: (process.env['USERPROFILE'] + '/node_modules/jasmine2-protractor-utils'),
        disableHTMLReport: true,
        disableScreenshot: false,
        screenshotPath: 'target/screenshots_' + utils.returnToday("_"),
        screenshotOnExpectFailure: true,
        screenshotOnSpecFailure: false,
        clearFoldersBeforeTest: true
    }],


    onPrepare: function() {


        jasmine.getEnv().addReporter(new jasmineReporters.JUnitXmlReporter({
            consolidateAll: true,
            savePath: folderName,
            filePrefix: 'xmlresults'
        }));

        browser.driver.manage().window().maximize();
        browser.driver.get(Objects.siteurl);
        browser.manage().timeouts().setScriptTimeout(90000);
        browser.manage().timeouts().pageLoadTimeout(40000);

    },

    onComplete: function() {
        var browserName, browserVersion;
        var capsPromise = browser.getCapabilities();
        capsPromise.then(function(caps) {
            browserName = caps.get('browserName');
            browserVersion = caps.get('version');

            testConfig = {
                reportTitle: 'Arkcase Test Report',
                outputPath: './target/screenshots_' + utils.returnToday("_"),
                screenshotPath: './',
                testBrowser: browserName,
                browserVersion: browserVersion,
                modifiedSuiteName: false,
                screenshotsOnlyOnFailure: true


            };
            new HTMLReport().from('target/screenshots_' + utils.returnToday("_") + '/xmlresults.xml', testConfig);
        });
    }




};
