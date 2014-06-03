/**
 * ComplaintDetail.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
ComplaintDetail.Event = {
    initialize : function() {
    }

    ,onPostInit: function() {
        var complaintId = Complaint.getComplaintId();
        ComplaintDetail.Service.retrieveDetail(complaintId);
    }
};
