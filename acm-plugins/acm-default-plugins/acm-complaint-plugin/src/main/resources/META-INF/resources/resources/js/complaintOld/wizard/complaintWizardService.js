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

    ,API_SAVE_COMPLAINT            : "/api/latest/plugin/complaint"
    ,API_UPLOAD_COMPLAINT_FILE     : "/api/latest/plugin/complaint/file"
    ,API_SUBMIT_FOR_APPROVAL       : "/api/latest/plugin/complaint/workflow"


    ,saveComplaint : function(data) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_SAVE_COMPLAINT
            ,JSON.stringify(data)
            ,ComplaintWizard.Callback.EVENT_COMPLAIN_SAVED
        );
    }
    ,submitForApproval : function(data) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_SUBMIT_FOR_APPROVAL
            ,JSON.stringify(data)
            ,ComplaintWizard.Callback.EVENT_COMPLAIN_SUBMITTED
        );
    }
};

