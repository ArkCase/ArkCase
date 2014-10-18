/**
 * Complaint.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Complaint.Service = {
    create : function() {
    }

    ,API_LIST_COMPLAINT         : "/api/latest/plugin/search/COMPLAINT"
    ,API_RETRIEVE_DETAIL        : "/api/latest/plugin/complaint/byId/"
    ,API_SAVE_COMPLAINT         : "/api/latest/plugin/complaint"
    ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"
    ,API_UPLOAD_COMPLAINT_FILE  : "/api/latest/plugin/complaint/file"
    ,API_RETRIEVE_TASKS         : "/api/latest/plugin/search/children?parentType=COMPLAINT&childType=TASK&parentId="
    ,API_RETRIEVE_PERSON_LIST_COMPLAINT   : "/api/latest/plugin/person/list/complaint/"
    //,API_SAVE_PERSON             : "/api/latest/plugin/person"
    ,API_SAVE_PERSON_ASSOCIATION : "/api/latest/plugin/personAssociation"
    ,API_DELETE_PERSON           : "/api/latest/plugin/person/delete/"



    ,listComplaint : function(treeInfo) {
        var complaintId = treeInfo.complaintId;
        var initKey = treeInfo.initKey;
        var start = treeInfo.start;
        var n = treeInfo.n;
        var s = treeInfo.s;
        var q = treeInfo.q;

        var url = App.getContextPath() + this.API_LIST_COMPLAINT;
        url += "?start=" + treeInfo.start;
        url += "&n=" + treeInfo.n;
        Acm.Ajax.asyncGet(url
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
  /*  ,savePerson: function(data){
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_SAVE_PERSON
            ,JSON.stringify(data)
            ,Complaint.Callback.EVENT_PERSON_SAVED
        );
    }*/

    ,savePersonAssociation: function(data){
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_SAVE_PERSON_ASSOCIATION
            ,JSON.stringify(data)
            ,Complaint.Callback.EVENT_PERSON_ASSOCIATION_SAVED
        );
    }
    ,deletePersonById: function(personId){
        var url = (App.getContextPath() + this.API_DELETE_PERSON + personId);
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_DELETE_PERSON + personId
            ,Complaint.Callback.EVENT_PERSON_DELETED
        );
    }
//    ,retrievePersonListComplaint : function(parentId) {
//        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_PERSON_LIST_COMPLAINT + parentId
//            ,Complaint.Callback.EVENT_COMPLAINT_PERSON_LIST_RETRIEVED
//        );
//    }

//    ,retrieveTasks : function(complaintId) {
//        //Acm.Ajax.asyncGet(App.getContextPath() + "/api/v1/plugin/search/quickSearch?q=object_type_s:Task&start=0&n=800&s="
//        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_TASKS + complaintId
//            ,Complaint.Callback.EVENT_TASKS_RETRIEVED
//        );
//    }


};

