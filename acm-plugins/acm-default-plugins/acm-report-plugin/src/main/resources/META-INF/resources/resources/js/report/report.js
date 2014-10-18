/**
 * Report is namespace component for Report plugin
 *
 * @author jwu
 */
var Report = Report || {
    create: function() {
        Report.Object.create();
        Report.Event.create();
        Report.Page.create();
        Report.Rule.create();
        Report.Service.create();
        Report.Callback.create();

        Acm.deferred(Report.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

