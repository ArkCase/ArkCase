/**
 * Report.Rule
 *
 * manages rules govern interaction between elements in page
 *
 * @author jwu
 */
Report.Rule = {
    create : function() {
    }

	,validateCaseNumber : function(caseNumber) {

		if ( undefined === caseNumber || caseNumber === "" || caseNumber === "Case Number" ) {
			return false;
		}
		else {
			return true;
		}
	}
	,validateCaseStatus : function(status) {
		if ( undefined === status || caseNumber === "" || caseNumber === "Choose Case Status" ) {
			return "Draft";
		}
		else {
			return status;
		}
	}
	
	,updateSearchPanel : function(reportName) {
		
		if ( !reportName || reportName === Report.Object.CHOOSE_REPORT ) {
			Report.Rule.resetCriteria();
			return;
		}
		
		if ( reportName === Report.Object.BILLING_REPORT) {
			Report.Rule.updateBillingReportSection();
		}
		else {
			Report.Page.toggleBillingReportCriteria(false);    			    			
			Report.Page.toggleCaseStatusCriteria(true);			
			Report.Page.toggleDatepickertCriteria(true);
			Report.Page.toggleReportButton(true);
		}
		
	}
	
	,updateBillingReportSection : function() {
		Report.Page.toggleBillingReportCriteria(true);			
		Report.Page.toggleCaseStatusCriteria(false);			
		Report.Page.toggleDatepickertCriteria(false);
		Report.Page.toggleReportButton(true);
		
	}
	
	,resetCriteria : function() {
		Report.Page.toggleCaseStatusCriteria(false);			
		Report.Page.toggleBillingReportCriteria(false);
		Report.Page.toggleDatepickertCriteria(false);
		Report.Page.toggleReportButton(false);
	}
};

