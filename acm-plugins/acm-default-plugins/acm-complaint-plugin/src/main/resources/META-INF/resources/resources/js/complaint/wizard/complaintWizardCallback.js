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
                //Acm.Dialog.showError("onCreateReturned:" + response.complaintId);
                ComplaintWizard.setComplaintId(response.complaintId);
                success = true;
            }
        }

        if (!success) {
            Acm.Dialog.showError("Failed to create new complaint");
        }
    }
};
