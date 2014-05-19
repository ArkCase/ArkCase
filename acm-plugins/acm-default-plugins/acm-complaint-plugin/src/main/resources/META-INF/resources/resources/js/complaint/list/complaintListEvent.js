/**
 * ComplaintList.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
ComplaintList.Event = {
    initialize : function() {
    }

    ,onClickLnkListItemImage : function(e) {
        var complaintId = ComplaintList.Object.getHiddenComplaintId(e);
        if (Complaint.getComplaintId() == complaintId) {
            return;
        }
        alert("onClickLnkListItemImage, complaintId=" + complaintId);
        Complaint.setComplaintId(complaintId);
    }
    ,onClickLnkListItem : function(e) {
        var complaintId = ComplaintList.Object.getHiddenComplaintId(e);
        if (Complaint.getComplaintId() == complaintId) {
            return;
        }

        var c = ComplaintList.findComplaint(complaintId);
        ComplaintList.Object.updateDetail(c);

        Complaint.setComplaintId(complaintId);
    }

    ,onPostInit: function() {
        ComplaintList.Service.listComplaint();
    }
};
