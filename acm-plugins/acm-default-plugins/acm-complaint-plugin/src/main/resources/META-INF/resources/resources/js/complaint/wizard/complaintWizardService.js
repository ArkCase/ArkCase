/**
 * ComplaintWizard.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
ComplaintWizard.Service = {
    initialize : function() {
    }

    ,API_GET_APPROVERS             : "/api/latest/users/withPrivilege/acm-complaint-approve"
    ,API_GET_COMPLAINT_TYPES       : "/api/latest/plugin/complaint/types"
    ,API_GET_PRIORITIES            : "/api/latest/plugin/complaint/priorities"
    ,API_SAVE_COMPLAINT            : "/api/latest/plugin/complaint"
    ,API_UPLOAD_COMPLAINT_FILE     : "/api/latest/plugin/complaint/file"
    ,API_SUBMIT_FOR_APPROVAL       : "/api/latest/plugin/complaint/workflow"


    ,getApprovers : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_APPROVERS
            ,ComplaintWizard.Callback.EVENT_APPROVERS_RETRIEVED
        );
    }
    ,getComplaintTypes : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_COMPLAINT_TYPES
            ,ComplaintWizard.Callback.EVENT_COMPLAINT_TYPES_RETRIEVED
        );
    }
    ,getPriorities : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_PRIORITIES
            ,ComplaintWizard.Callback.EVENT_PRIORIES_RETRIEVED
        );
    }
    ,saveComplaint : function(data) {
        Acm.Ajax.asyncPost(Acm.getContextPath() + this.API_SAVE_COMPLAINT
            ,JSON.stringify(data)
            ,ComplaintWizard.Callback.EVENT_COMPLAIN_SAVED
        );
    }
    ,submitForApproval : function(data) {
        Acm.Ajax.asyncPost(Acm.getContextPath() + this.API_SUBMIT_FOR_APPROVAL
            ,JSON.stringify(data)
            ,ComplaintWizard.Callback.EVENT_COMPLAIN_SUBMITTED
        );
    }
};

