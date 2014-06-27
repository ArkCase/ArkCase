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
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
    }

    ,EVENT_LIST_RETURNED		: "complaint-list-list-returned"
    ,EVENT_DETAIL_RETRIEVED		: "complaint-list-detail-retrieved"

    ,onListReturned : function(Callback, response) {
        var success = false;
        if (response) {
            ComplaintList.setComplaintList(response);
            ComplaintList.Page.buildComplaintList(response);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve complaint list");
        }
    }
    ,onDetailRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            if (Acm.isNotEmpty(response.complaintId)) {
                var curId = Complaint.getComplaintId();
                if (curId != response.complaintId) {
                    return;         //user clicks another complaint before callback, do nothing
                }

                var complaint = response;
                Complaint.setComplaint(complaint);
                ComplaintList.Object.updateDetail(complaint);
                success = true;
            }
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve complaint detail");
        }
    }
};
