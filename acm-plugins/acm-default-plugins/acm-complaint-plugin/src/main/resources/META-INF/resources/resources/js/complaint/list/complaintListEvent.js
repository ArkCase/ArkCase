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
        } else {
            Complaint.setComplaintId(complaintId);
        }

        this.doClickLnkListItem();
    }
    ,onClickLnkListItem : function(e) {
        var complaintId = ComplaintList.Object.getHiddenComplaintId(e);
        if (Complaint.getComplaintId() == complaintId) {
            return;
        } else {
            Complaint.setComplaintId(complaintId);
        }

        this.doClickLnkListItem();
    }
    ,doClickLnkListItem: function() {
        var complaintId = Complaint.getComplaintId();
        ComplaintList.Service.retrieveDetail(complaintId);

        var c = ComplaintList.findComplaint(complaintId);
        if (null != c) {
            ComplaintList.Object.updateDetail(c);
            Complaint.setComplaintId(complaintId);
            ComplaintList.Object.hiliteSelectedItem(complaintId);
        }
    }

    ,onPostInit: function() {
        if (ComplaintList.isSingleObject()) {
            var complaintId = Complaint.getComplaintId();
            ComplaintList.Service.retrieveDetail(complaintId);
        } else {
            ComplaintList.Service.listComplaint();
        }
    }
};
