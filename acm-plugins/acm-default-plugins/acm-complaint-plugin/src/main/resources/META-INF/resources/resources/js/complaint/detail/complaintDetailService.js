/**
 * ComplaintDetail.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
ComplaintDetail.Service = {
    initialize : function() {
    }

    ,API_RETRIEVE_DETAIL       : "/api/latest/plugin/complaint/list"


    ,retrieveDetail : function(complaintId) {
        var a = complaintId;

        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_RETRIEVE_DETAIL
            ,ComplaintDetail.Callback.EVENT_DETAIL_RETRIEVED
        );
    }
};

