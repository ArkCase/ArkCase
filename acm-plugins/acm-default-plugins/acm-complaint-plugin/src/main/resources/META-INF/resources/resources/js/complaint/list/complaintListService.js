/**
 * ComplaintList.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
ComplaintList.Service = {
    initialize : function() {
    }

    ,API_LIST_COMPLAINT        : "/api/latest/plugin/complaint/list"
    ,API_RETRIEVE_DETAIL       : "/api/latest/plugin/complaint/byId/"
    ,API_DOWNLOAD_DOCUMENT     : "/api/v1/plugin/ecm/download/byId/"
    ,API_UPLOAD_COMPLAINT_FILE     : "/api/latest/plugin/complaint/file"


    ,listComplaint : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_LIST_COMPLAINT
            ,ComplaintList.Callback.EVENT_LIST_RETURNED
        );
    }

    ,retrieveDetail : function(complaintId) {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_RETRIEVE_DETAIL + complaintId
            ,ComplaintList.Callback.EVENT_DETAIL_RETRIEVED
        );
    }
};

