/**
 * Complaint.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Complaint.Service = {
    initialize : function() {
    }

    ,API_LIST_COMPLAINT         : "/api/latest/plugin/search/COMPLAINT"
    ,API_RETRIEVE_DETAIL        : "/api/latest/plugin/complaint/byId/"
    ,API_SAVE_COMPLAINT         : "/api/latest/plugin/complaint"
    ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"
    ,API_UPLOAD_COMPLAINT_FILE  : "/api/latest/plugin/complaint/file"
    ,API_RETRIEVE_TASKS         : "/api/latest/plugin/search/children?parentType=COMPLAINT&childType=TASK&parentId="


    ,listComplaint : function(treeInfo) {
        var complaintId = treeInfo.complaintId;
        var initKey = treeInfo.initKey;
        var start = treeInfo.start;
        var n = treeInfo.n;
        var s = treeInfo.s;
        var q = treeInfo.q;

        Acm.Ajax.asyncGet(App.getContextPath() + this.API_LIST_COMPLAINT
            ,Complaint.Callback.EVENT_LIST_RETRIEVED
        );
    }
    ,retrieveDetail : function(complaintId) {
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_DETAIL + complaintId
            ,Complaint.Callback.EVENT_DETAIL_RETRIEVED
        );
    }
    ,saveComplaint : function(data) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_SAVE_COMPLAINT
            ,JSON.stringify(data)
            ,Complaint.Callback.EVENT_COMPLAIN_SAVED
        );
    }

//    ,retrieveTasks : function(complaintId) {
//        //Acm.Ajax.asyncGet(App.getContextPath() + "/api/v1/plugin/search/quickSearch?q=object_type_s:Task&start=0&n=800&s="
//        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_TASKS + complaintId
//            ,Complaint.Callback.EVENT_TASKS_RETRIEVED
//        );
//    }


};

