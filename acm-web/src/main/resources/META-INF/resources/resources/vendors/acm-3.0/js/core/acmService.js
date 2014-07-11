/**
 * Acm.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Acm.Service = {
    initialize : function() {
    }

    ,API_GET_APPROVERS             : "/api/latest/users/withPrivilege/acm-complaint-approve"
    ,API_GET_COMPLAINT_TYPES       : "/api/latest/plugin/complaint/types"
    ,API_GET_PRIORITIES            : "/api/latest/plugin/complaint/priorities"


    ,getApprovers : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_APPROVERS
            ,Acm.Callback.EVENT_APPROVERS_RETRIEVED
        );
    }
    ,getComplaintTypes : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_COMPLAINT_TYPES
            ,Acm.Callback.EVENT_COMPLAINT_TYPES_RETRIEVED
        );
    }
    ,getPriorities : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_PRIORITIES
            ,Acm.Callback.EVENT_PRIORIES_RETRIEVED
        );
    }
};

