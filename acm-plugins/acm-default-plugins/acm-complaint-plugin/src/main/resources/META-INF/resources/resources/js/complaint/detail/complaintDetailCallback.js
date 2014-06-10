/**
 * ComplaintDetail.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
ComplaintDetail.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
    }

    ,EVENT_DETAIL_RETRIEVED		: "complaint-detail-retrieved"

    ,onDetailRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            if (Acm.isNotEmpty(response.complaintId)) {
                var complaint = response;
                Complaint.setComplaint(complaint);
                ComplaintDetail.Object.updateDetail(complaint);
                success = true;
            }
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve complaint detail");
        }
    }
};
