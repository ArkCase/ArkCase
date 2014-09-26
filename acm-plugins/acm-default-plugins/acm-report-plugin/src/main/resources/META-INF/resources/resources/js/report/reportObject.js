/**
 * Report.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Report.Object = {
    CASE_SUMMARY_REPORT : "Case Summary Report",
    OPEN_CASE_REPORT : "Open Case Report",
    CLOSE_CASE_REPORT : "Close Case Report",
    COMPLAINT_REPORT : "Complaint Report",
    BILLING_REPORT : "Billing Report",
    CHOOSE_REPORT : "Choose Report",
    
    initialize : function() {
        var items = $(document).items();
        this.$selReport = $("#selectReport");
        
        this.$caseNumberSection = $("#caseNumberSection");
        this.$caseNumber = $("#caseNumber");
        this.$caseNumberLbl = $("#caseNumberlbl");
        
        this.$caseStatusSection = $("#caseStatusSection");
        this.$selCaseStatus = $("#selectCaseStatus");
        
        this.$datepickerSection = $("#datepickerSection");
        this.$edtStartDate = $("#startDate");
        this.setValueStartDate(Acm.getCurrentDay());

        this.$edtEndDate = $("#endDate");
        this.setValueEndDate(Acm.getCurrentDay());
        //$('#dateSelector').datepicker('disable');

        this.$reportSubmitSection = $("#reportSubmitSection");
        this.$btnGenerateReport = $("#generateReport");
        this.$btnGenerateReport.click(function(e) {Report.Event.onClickBtnGenerateReport(e);});

        //this.$btnTest = $("#test");
        //this.$btnTest.click(function(e) {Report.Event.onClickBtnTest(e);});
        
        this.$mainContentSel = $("#ReportMeassge");
    	this.registerChangeSelNewReportEvents();
    	Report.Rule.updateSearchPanel();

    }
    ,getValueStartDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtStartDate);
    }
    ,getValueEndDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtEndDate);
    }
    
    /**
     * get the report selected option value
     */
    ,getSelectedValueSelReport: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selReport);
    }
    
    /**
     * get the report selected option text/label
     */
    ,getSelectedTextSelReport: function() {
        return Acm.Object.getSelectTextIgnoreFirst(this.$selReport);
    }

    /**
     * get the case status selected option value
     */
    ,getSelectedValueSelStatus: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selCaseStatus);
    }

    /**
     * get the case status selected option text/label
     */
    ,getSelectedTextSelStatus: function() {
        return Acm.Object.getSelectTextIgnoreFirst(this.$selCaseStatus);
    }
    
    ,setValueStartDate: function(val) {
        Acm.Object.setValueDatePicker(this.$edtStartDate, val);
    }
    ,setValueEndDate: function(val) {
        Acm.Object.setValueDatePicker(this.$edtEndDate, val);
    }
    
    /**
     * Register the new report selector changed event
     */
	,registerChangeSelNewReportEvents: function() {
		Report.Page.toggleBillingReportCriteria(false);

    	this.$selReport.change(function(e) {
    		var reportName = $(this).find('option:selected').text();
    		Report.Rule.updateSearchPanel(reportName);
    	});
	}
    
};




