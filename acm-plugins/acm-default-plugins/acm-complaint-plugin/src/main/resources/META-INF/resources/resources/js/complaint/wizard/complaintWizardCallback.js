/**
 * ComplaintWizard.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
ComplaintWizard.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_CREATE_RETURNED, this.onCreateReturned);
    }

    ,EVENT_CREATE_RETURNED		: "complaint-wizard-create-returned"

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
