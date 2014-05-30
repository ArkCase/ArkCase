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
    ,API_CREATE_COMPLAINT          : "/api/latest/plugin/complaint"
    ,API_UPLOAD_COMPLAINT_FILE     : "/api/latest/plugin/complaint/file"


    ,getApprovers : function() {
        Acm.Ajax.asyncGet(Acm.getContextPath() + this.API_GET_APPROVERS
            ,ComplaintWizard.Callback.EVENT_APPROVERS_RETRIEVED
        );
    }
    ,createComplaint : function(data) {
        Acm.Ajax.asyncPost(Acm.getContextPath() + this.API_CREATE_COMPLAINT
            ,JSON.stringify(data)
            ,ComplaintWizard.Callback.EVENT_CREATE_RETURNED
        );
    }
};

