/**
 * App serves as namespace for Application
 *
 * @author jwu
 */
var App = App || {
    create : function() {
        if (App.Controller.create)         App.Controller.create();
        if (App.Model.create)              App.Model.create();
        if (App.View.create)               App.View.create();

        this.create_old();
    }
    ,onInitialized: function() {
        if (App.Controller.onInitialized)  App.Controller.onInitialized();
        if (App.Model.onInitialized)       App.Model.onInitialized();
        if (App.View.onInitialized)        App.View.onInitialized();
    }

    ,getContextPath: function() {
        return App.View.MicroData.contextPath;
    }
    ,getUserName: function() {
        return App.View.MicroData.userName;
    }

    ,buildObjectUrl : function(objectType, objectId, defaultUrl) {
        var url = null;
        var ot = App.View.MicroData.findObjectType(objectType);
        if (ot && Acm.isNotEmpty(ot.url)) {
            url = App.getContextPath() + ot.url + objectId + Acm.goodValue(ot.urlEnd);
        }
        if (null == url && undefined != defaultUrl) {
            url = defaultUrl;
        }
        return url;
    }

    ,gotoPage: function(url) {
        window.location.href = App.getContextPath() + url;
    }


    ,create_old : function() {
        App.Object.create();
        App.Event.create();
        App.Service.create();
        App.Callback.create();

        Acm.deferred(App.Event.onPostInit);
    }

    ,OBJTYPE_CASE:        "CASE_FILE"
    ,OBJTYPE_COMPLAINT:   "COMPLAINT"
    ,OBJTYPE_TASK:        "TASK"
    ,OBJTYPE_DOCUMENT:    "DOCUMENT"
    ,OBJTYPE_PEOPLE:    "PEOPLE"
    ,OBJTYPE_PERSON:    "PERSON"
    ,OBJTYPE_BUSINESS_PROCESS: "BUSINESS_PROCESS"
    ,OBJTYPE_TIMESHEET:        "TIMESHEET"
    ,OBJTYPE_COSTSHEET:        "COSTSHEET"


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


    ,getContextPath_old: function() {
        return App.Object.getContextPath();
    }
    ,getUserName_old: function() {
        return App.Object.getUserName();
    }
    ,buildObjectUrl_old : function(objectType, objectId)
    {
        var url = App.getContextPath();
        if (App.OBJTYPE_CASE == objectType) {
            url += "/plugin/casefile/" + objectId;
        } else if (App.OBJTYPE_COMPLAINT == objectType) {
            url += "/plugin/complaint/" + objectId;
        } else if (App.OBJTYPE_TASK == objectType) {
            url += "/plugin/task/" + objectId;
        } else if (App.OBJTYPE_DOCUMENT == objectType) {
            url += "/plugin/document/" + objectId;
        } else if (App.OBJTYPE_PEOPLE == objectType) {
            url += "/plugin/people/" + objectId;
        }else if (App.OBJTYPE_PERSON == objectType) {
            url += "/plugin/person/" + objectId;
        }

        return url;
    }


};


