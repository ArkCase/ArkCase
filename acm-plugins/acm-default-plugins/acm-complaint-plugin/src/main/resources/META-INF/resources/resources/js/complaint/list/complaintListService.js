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


    ,listComplaint : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_LIST_COMPLAINT
            ,ComplaintList.Callback.EVENT_LIST_RETURNED
        );
    }
};

