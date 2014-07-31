/**
 * Report is namespace component for Report plugin
 *
 * @author jwu
 */
var Report = Report || {
    initialize: function() {
        Report.Object.initialize();
        Report.Event.initialize();
        Report.Page.initialize();
        Report.Rule.initialize();
        Report.Service.initialize();
        Report.Callback.initialize();

        Acm.deferred(Report.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

