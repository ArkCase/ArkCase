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


    //Complaint treeInfo is in jason format, includes
    //{
    //     complaintId : 0
    //     ,initKey: null
    //     ,start: 0
    //    ,n: 10
    //    ,term: null
    //    sort fields
    //    search terms
    //    filters
    //}
    ,getComplaintTreeInfo: function() {
        var data = sessionStorage.getItem("AcmComplaintTreeInfo");
        var treeInfo = ("null" !== data)? JSON.parse(data) : {
            complaintId: 0
            ,initKey: null
            ,start: 0
            ,n: Acm.TREE_DEFAULT_PAGE_SIZE
            ,term: null
        };
        return treeInfo;
    }
    ,setComplaintTreeInfo: function(treeInfo) {
        var data = (Acm.isEmpty(treeInfo))? null : JSON.stringify(treeInfo);
        sessionStorage.setItem("AcmComplaintTreeInfo", data);
    }
    ,TREE_DEFAULT_PAGE_SIZE:10

};


