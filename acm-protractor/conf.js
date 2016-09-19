exports.config = {
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
//		  }, {
//			    'browserName': 'internet explorer',  
//		        'maxInstances': 5,		
//		        'version': '11'	
//		  }, {
//			    'browserName': 'firefox',  
//		        'maxInstances': 5
//		  
//		  }, {
//		    'browserName': 'safari'	,
//			'maxInstances': 5
//		  }],

    specs: ['./login_test.spec.js'],

    jasmineNodeOpts: {
        showColors: true
    },

    onPrepare: function() {
        browser.driver.manage().window().maximize();
        browser.driver.get('http://cloud.arkcase.com/arkcase/login');
    },
};
