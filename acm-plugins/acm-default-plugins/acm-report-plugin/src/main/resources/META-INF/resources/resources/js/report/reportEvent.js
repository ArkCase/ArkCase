/**
 * Report.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Report.Event = {
    initialize : function() {

    }
    ,onPostInit: function() {
    }
    ,onClickBtnTest: function(e) {
        alert("test clicked");
    }
    ,onClickBtnGenerateReport: function(e) {
        var reportName = Report.Object.getSelectedValueSelReport()
        var startDate = Report.Object.getValueStartDate();
        var endDate = Report.Object.getValueEndDate();
        Acm.Dialog.info(reportName + "<br>" +  startDate + "</br>" + endDate);
    }
};
