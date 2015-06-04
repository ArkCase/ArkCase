/**
 * Report.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Report.Event = {
    create : function() {

    }
    ,onPostInit: function() {
    }
    ,onClickBtnTest: function(e) {
        alert("test clicked");
    }
    
    /**
     * on Click gets the selected option text which is the report name
     */
    ,onClickBtnGenerateReport: function(e) {
        var reportName = Report.Object.getSelectedTextSelReport();
        this.openReport(reportName);
    }
    
    /**
     * Based on the report name, do validation if needed, and formulate the url,
     * then make the report rendering request.
     * 
     */
    ,openReport : function(reportName) {
    	var pageUrl;
    	pageUrl = Report.Object.getSelectedValueSelReport();
    	
        if (reportName === Report.Object.BILLING_REPORT) {
        	var validCaseNumber = Report.Rule.validateCaseNumber(Report.Object.$caseNumber.val());
        	if ( validCaseNumber ) {
            	pageUrl = pageUrl +"&caseNumber=" + Report.Object.$caseNumber.val(); 
        	}
        	else {
                Acm.Dialog.error($.t("report:msg.case-number-invalid"));
                return false;
        	}
        }
        else {
        	var status = Report.Rule.validateCaseStatus(Report.Object.getSelectedValueSelStatus())
            var startDate = Acm.Object.getValue(Report.Object.$edtStartDate);
            var endDate = Acm.Object.getValue(Report.Object.$edtEndDate);
            var dateFormat = $.t("common:date.pentaho");
            
            startDate = startDate.replace(/\//g, "-");
            endDate = endDate.replace(/\//g, "-");

            if (pageUrl.indexOf("?") <= -1) {
                pageUrl = pageUrl +"?startDate=" + startDate + "&endDate=" + endDate + "&caseStatus=" + status + "&dateFormat=" + encodeURIComponent(dateFormat); 
            } else {
                pageUrl = pageUrl +"&startDate=" + startDate + "&endDate=" + endDate + "&caseStatus=" + status + "&dateFormat=" + encodeURIComponent(dateFormat);
            }
            
            // Incident Category, Priority, Owner not added since DB clarification is needed.
            
        }

        var mainContent = Report.Object.$mainContentSel;
    	mainContent.hide();
    	window.open(pageUrl, 'report_iframe');
    }
    
};
