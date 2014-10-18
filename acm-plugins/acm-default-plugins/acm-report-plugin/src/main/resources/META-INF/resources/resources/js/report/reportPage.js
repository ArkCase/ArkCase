/**
 * Report.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
Report.Page = {
    create : function() {
    }

	/**
	 * Toggle show or hide the case number fields.
	 */
	,toggleBillingReportCriteria : function(show) {
	    Acm.Object.show(Report.Object.$caseNumberSection, show);
	}
	
	/**
	 * Toggle show or hide the case status fields.
	 */
	,toggleCaseStatusCriteria : function(show) {
	    Acm.Object.show(Report.Object.$caseStatusSection, show);
	}
	
	,toggleDatepickertCriteria : function(show) {
		Acm.Object.show(Report.Object.$datepickerSection, show);
	}
	
	,toggleReportButton : function(show) {
		Acm.Object.show(Report.Object.$reportSubmitSection, show);
	}

};

