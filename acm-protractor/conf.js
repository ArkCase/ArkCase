exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
   // directConnect: true,
    defaultTimeoutInterval: 20000,
    
    cababilities: {
        'browserName': 'chrome'
    },

    framework: 'jasmine2',
    specs: ['./tasks_page.spec.js'],

    jasmineNodeOpts: {
        showColors: true,
    },


    onPrepare: function() {
        browser.driver.get('http://cloud.arkcase.com/arkcase/login');
        browser.driver.manage().window().maximize();
        browser.ignoresynchronization = true;

    },
};
