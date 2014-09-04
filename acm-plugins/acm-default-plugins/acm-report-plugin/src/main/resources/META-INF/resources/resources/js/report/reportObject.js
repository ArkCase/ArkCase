/**
 * Report.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Report.Object = {
    COMPLAINT_REPORT : "Complaint Report",
    BILLING_REPORT : "Billing Report",
    
    initialize : function() {
        var items = $(document).items();
        this.$selReport = $("#selectReport");
        this.$caseNumber = $("#caseNumber");
        
        this.$edtStartDate = $("#startDate");
        this.setValueStartDate(Acm.getCurrentDay());

        this.$edtEndDate = $("#endDate");
        this.setValueEndDate(Acm.getCurrentDay());
        //$('#dateSelector').datepicker('disable');

        this.$btnGenerateReport = $("#generateReport");
        this.$btnGenerateReport.click(function(e) {Report.Event.onClickBtnGenerateReport(e);});

        //this.$btnTest = $("#test");
        //this.$btnTest.click(function(e) {Report.Event.onClickBtnTest(e);});
        
        this.$mainContentSel = $("#mainContent");

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
    
    ,setValueStartDate: function(val) {
        Acm.Object.setValueDatePicker(this.$edtStartDate, val);
    }
    ,setValueEndDate: function(val) {
        Acm.Object.setValueDatePicker(this.$edtEndDate, val);
    }
};




