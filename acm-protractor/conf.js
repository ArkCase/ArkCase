exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',

    cababilities: {
        'browserName': 'chrome'
    },

    specs: ['./user_profile.spec.js'],

    jasmineNodeOpts: {
        showColors: true
    },

    onPrepare: function() {
        browser.driver.manage().window().maximize();
        browser.driver.get('http://cloud.arkcase.com/arkcase/login');
    },
};
