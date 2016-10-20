var Objects=require('../json/Objects.json');
var editBtn = element(by.xpath(Objects.dashboardpage.locators.editBtn));
var addNewWidgetBtn = element(by.xpath(Objects.dashboardpage.locators.addNewWidgetBtn));
var editDashboardBtn = element(by.xpath(Objects.dashboardpage.locators.editDashboardBtn));
var saveChangesBtn = element(by.xpath(Objects.dashboardpage.locators.saveChangesBtn));
var undoChangesBtn = element(by.xpath(Objects.dashboardpage.locators.undoChangesBtn));
var addNewWidgetTitle = element(by.css(Objects.dashboardpage.locators.addNewWidgetTitle));
var casesByStatus = element(by.linkText(Objects.dashboardpage.locators.casesByStatus));
var myCases = element(by.linkText(Objects.dashboardpage.locators.myCases));
var myComplaints = element(by.linkText(Objects.dashboardpage.locators.myComplaints));
var newComplaints = element(by.linkText(Objects.dashboardpage.locators.newComplaints));
var teamWorkload = element(by.linkText(Objects.dashboardpage.locators.teamWorkload));
var weather = element(by.linkText(Objects.dashboardpage.locators.weather));
var news = element(by.linkText(Objects.dashboardpage.locators.news));
var closeBtn = element(by.buttonText(Objects.dashboardpage.locators.closeBtn));
var widgetTitle = element.all(by.xpath(Objects.dashboardpage.locators.widgetTitle)).get(0);
var reloadWidgetContentBtn = element(by.xpath(Objects.dashboardpage.locators.reloadWidgetContentBtn));
var chnageWidgetLocationBtn = element(by.xpath(Objects.dashboardpage.locators.chnageWidgetLocationBtn));
var editWidgetConfigurationBtn = element(by.xpath(Objects.dashboardpage.locators.editWidgetConfigurationBtn));
var removeWidgetBtn = element.all(by.xpath(Objects.dashboardpage.locators.removeWidgetBtn)).get(0);
var DashboardPage = function() { 
	this.clickEditButton = function() {
		editBtn.click();
		return this;
	};
	this.clickAddWidgetButton = function(){
		addNewWidgetBtn.click();
		return this;
	}	
    this.returnWidgetTitle = function(){
    	return widgetTitle.getText();
    }
    this.removeWidgetButton = function(){
    	removeWidgetBtn.click();
    	return this;
    }
    this.clickSaveChangesButton = function(){
    	saveChangesBtn.click();
    	return this;
    }
    this.addWidget = function(type){
    	switch (type) {
		case "CasesByStatus":
			casesByStatus.click();
			return this;
			break;
		case "MyCases":
			myCases.click();
	    	return this;
            break;
		case "MyComplaints":
			myComplaints.click();
	    	return this;
	    	break;
		case "NewComplaints":
			newComplaints.click();
	    	return this;
	    	break;
		case "TeamWorkload":
			teamWorkload.click();
	    	return this;
	    	break;
		case "Weather":
			weather.click();
	    	return this;
	    	break;
		case "News":
			news.click();
	    	return this;
	    	break;
		default:
			casesByStatus.click();
		    return this;
			break;
		}		
	}
   

};

module.exports = new DashboardPage();
