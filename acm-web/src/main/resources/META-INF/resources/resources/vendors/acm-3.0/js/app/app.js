/**
 * App serves as namespace for Application
 *
 * @author jwu
 */
var App = App || {
    create : function() {
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

    ,buildObjectUrl : function(objectType, objectId)
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


