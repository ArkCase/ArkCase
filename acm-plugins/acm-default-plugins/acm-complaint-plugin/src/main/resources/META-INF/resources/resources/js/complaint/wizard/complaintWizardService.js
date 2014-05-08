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

    ,API_CREATE_COMPLAINT      : "/api/latest/plugin/complaint"
    ,API_UPLOAD_COMPLAINT_FILE : "/api/latest/plugin/complaint/file"

    ,createComplaint : function(data) {
        Acm.Ajax.asyncPost(Acm.getContextPath() + this.API_CREATE_COMPLAINT
            ,JSON.stringify(data)
            ,ComplaintWizard.Callback.EVENT_CREATE_RETURNED
        );
    }
};

