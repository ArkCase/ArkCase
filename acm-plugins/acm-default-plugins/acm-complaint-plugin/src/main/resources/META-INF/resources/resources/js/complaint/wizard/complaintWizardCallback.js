/**
 * ComplaintWizard.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
ComplaintWizard.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_APPROVERS_RETRIEVED, this.onApproversRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_CREATE_RETURNED, this.onCreateReturned);
    }

    ,EVENT_APPROVERS_RETRIEVED  : "complaint-wizard-get-approvers"
    ,EVENT_CREATE_RETURNED		: "complaint-wizard-create-returned"


    ,onApproversRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            ComplaintWizard.Object.initApprovers(response);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve approvers");
        }
    }
    ,onCreateReturned : function(Callback, response) {
        var success = false;
        if (response) {
            if (Acm.isNotEmpty(response.complaintId)) {
                ComplaintWizard.Object.setComplaintData(response);
                success = true;
            }
        }

        if (!success) {
            Acm.Dialog.error("Failed to create new complaint");
        }
    }
};
