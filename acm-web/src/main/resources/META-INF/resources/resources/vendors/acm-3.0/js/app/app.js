/**
 * App serves as namespace for Application
 *
 * @author jwu
 */
var App = App || {
    initialize : function() {
        App.Object.initialize();
        App.Event.initialize();
        App.Service.initialize();
        App.Callback.initialize();

        Acm.deferred(App.Event.onPostInit);
    }

    ,Object : {}
    ,Event : {}
    ,Service : {}
    ,Callback : {}


    ,OBJTYPE_CASE:        "Case"
    ,OBJTYPE_COMPLAINT:   "Complaint"
    ,OBJTYPE_TASK:        "Task"
    ,OBJTYPE_DOCUMENT:    "Document"
    ,OBJTYPE_PEOPLE:    "People"


    ,getContextPath: function() {
        return App.Object.getContextPath();
    }
    ,getUserName: function() {
        return App.Object.getUserName();
    }

    ,gotoPage: function(url) {
        window.location.href = App.getContextPath() + url;
    }

};


