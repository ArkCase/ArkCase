# Protractor tests

### Introduction
Protractor is an end-to-end test framework for AngularJS applications. Protractor runs tests against your application running in a real browser, interacting with it as a user would.
### Prerequisites
Use npm to install Protractor globally with:
```
> npm install -g protractor
```
Install ```RobotJS``` for Node.js Desktop Automation. Control the mouse, keyboard, and read the screen using npm:
```
> npm install robotjs
```
The ```webdriver-manager``` is a helper tool to easily get an instance of a Selenium Server running. Use it to download the necessary binaries with:
```
> webdriver-manager update
```
Install ```system-sleep``` to delay script execution
````
> npm install system-sleep
````
### Configuration
Start a server with:
```
> webdriver-manager start
```

Configuration of the Protractor tests is done in the ```config.js``` file. 

### Running the tests
```
> protractor conf.js
```

### Running suites - suite names are defined in conf.js file under suites
```
> protractor conf.js --suite=[SuiteName]
```

### Using Object.json file for data and objects
At the beggining of test add this row:
```
> var Objects = require('./Objects.json'); 

```
In Objects.json insert all locators and data in this format: 

```
> { 
  "page": {
    "locators": {
      "username": "j_username",
      "password": "j_password",
      "loginbutton": "submit"
    },
    "data": {
      "supervisoruser": {
        "username": "samuel-acm",
        "password": "Armedia#1"
      }
    }
  }
}

```
then use them in the test, next row will return j_username

> Object.page.locators.username 

### Using Logging in tests
At the beggining of test add this row:
```
> var logger = require('../log');

```
then use logging in the test, next row will log Info message with text "Username inserted": 
```
> logger.log('info', 'Username inserted');

```
Install ```jasmine-data-provider``` simple data provider for jasmine
````
> npm install jasmine-data-provider

````
