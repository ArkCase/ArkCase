/**
 * Report.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Report.Object = {
    initialize : function() {
        this.$selReport = $("#selectReport");

        this.$edtStartDate = $("#startDate");
        //this.$edtStartDate(Acm.getCurrentDay());
        this.setValueStartDate(Acm.getCurrentDay());

        this.$edtEndDate = $("#endDate");
        this.setValueEndDate(Acm.getCurrentDay());


        this.$btnGenerateReport = $("#generateReport");
        this.$btnGenerateReport.click(function(e) {Report.Event.onClickBtnGenerateReport(e);});

        //this.$btnTest = $("#test");
        //this.$btnTest.click(function(e) {Report.Event.onClickBtnTest(e);});
    }
    ,getValueStartDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtStartDate);
    }
    ,getValueEndDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtEndDate);
    }
    ,getSelectedValueSelReport: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selReport);
    }
    ,setValueStartDate: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtStartDate, val);
    }
    ,setValueEndDate: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtEndDate, val);
    }
};




