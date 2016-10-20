var Objects=require('../json/Objects.json');
var newBtn = element(by.linkText(Objects.taskpage.locators.newButton));
var taskBtn = element(by.linkText(Objects.taskpage.locators.taskButton));
var BasePage = function(){

	  this.navigateToURL = function(url){

	    browser.get(url);

	  };

	   this.getPageTitle = function(){

	       return browser.getTitle();

	   }
	   this.clickNewButton = function() {
			newBtn.click();
			return this;
		};
		this.clickTaskButton = function() {
			taskBtn.click();
			return this;
		};

	   

	};
	
module.exports = new BasePage();