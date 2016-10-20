var Objects=require('../json/Objects.json');
var BasePage = function(){

	  this.navigateToURL = function(url){

	    browser.get(url);

	  };

	   this.getPageTitle = function(){

	       return browser.getTitle();

	   }
	   

	};
	
module.exports = new BasePage();