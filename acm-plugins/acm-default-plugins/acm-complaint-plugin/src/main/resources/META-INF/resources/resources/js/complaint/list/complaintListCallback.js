/**
 * ComplaintList.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
ComplaintList.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_RETURNED, this.onListReturned);
    }

    ,EVENT_LIST_RETURNED		: "complaint-list-list-returned"

    ,onListReturned : function(Callback, response) {
        var success = false;
        if (response) {
            //if (Acm.isNotEmpty(response.complaintId)) {
            //ComplaintWizard.setComplaintId(response.complaintId);

            ComplaintList.setComplaintList(response);

            ComplaintList.Page.buildComplaintList(response);
            success = true;
            //}
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve complaint list");
        }
    }
};
