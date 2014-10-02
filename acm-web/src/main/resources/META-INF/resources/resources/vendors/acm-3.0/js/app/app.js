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

    ,OBJTYPE_CASE:        "Case"
    ,OBJTYPE_COMPLAINT:   "COMPLAINT"
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


    //fix me: make it plugin independent
    ,getComplaintTreeInfo: function() {
        var data = sessionStorage.getItem("AcmComplaintTreeInfo");
        if (Acm.isEmpty(data)) {
            return null;
        }
        return JSON.parse(data);
    }
    ,setComplaintTreeInfo: function(treeInfo) {
        var data = (Acm.isEmpty(treeInfo))? null : JSON.stringify(treeInfo);
        sessionStorage.setItem("AcmComplaintTreeInfo", data);
    }


};


