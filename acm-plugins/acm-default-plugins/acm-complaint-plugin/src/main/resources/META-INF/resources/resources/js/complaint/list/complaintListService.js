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

    ,API_LIST_COMPLAINT       : "/api/latest/plugin/complaint/list"
    ,API_RETRIEVE_DETAIL       : "/api/latest/plugin/complaint/byId/"


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

