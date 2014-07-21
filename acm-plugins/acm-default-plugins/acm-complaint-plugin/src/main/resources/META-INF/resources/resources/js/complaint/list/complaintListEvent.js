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
        ComplaintList.Service.retrieveTasks(complaintId);

        var c = ComplaintList.findComplaint(complaintId);
        if (null != c) {
            ComplaintList.Object.updateDetail(c);
            Complaint.setComplaintId(complaintId);
            ComplaintList.Object.hiliteSelectedItem(complaintId);
            //todo: bring item in list to view
        }


        var initTab = ComplaintList.Object.getInitTab();
        if (Acm.isNotEmpty(initTab)) {
            ComplaintList.Object.clickTab(initTab);
            ComplaintList.Object.setInitTab("");
        }
    }

    ,onPostInit: function() {
        if (ComplaintList.isSingleObject()) {
            var complaintId = Complaint.getComplaintId();
            ComplaintList.Service.retrieveDetail(complaintId);
            ComplaintList.Service.retrieveTasks(complaintId);
        } else {
            ComplaintList.Service.listComplaint();
        }

        Acm.keepTrying(ComplaintList.Event._tryInitAssignee, 8, 200);
        Acm.keepTrying(ComplaintList.Event._tryInitPriority, 8, 200);
        Acm.keepTrying(ComplaintList.Event._tryInitComplaintType, 8, 200);
    }

    ,onClickLnkNewTasks : function(e) {
        var complaintId = Complaint.getComplaintId();
        var url = ComplaintList.Page.URL_NEW_TASK + complaintId;
        App.gotoPage(url);
    }
    ,onChangeSelTasks : function(e) {
        alert("onChangeSelTasks:" + e.value);
    }

    ,_tryInitAssignee: function() {
        var data = App.Object.getApprovers();
        if (Acm.isNotEmpty(data)) {
            ComplaintList.Object.initAssignee(data);
            return true;
        } else {
            return false;
        }
    }
    ,_tryInitPriority: function() {
        var data = App.Object.getPriorities();
        if (Acm.isNotEmpty(data)) {
            ComplaintList.Object.initPriority(data);
            return true;
        } else {
            return false;
        }
    }
    ,_tryInitComplaintType: function() {
        var data = App.Object.getComplaintTypes();
        if (Acm.isNotEmpty(data)) {
            ComplaintList.Object.initComplaintType(data);
            return true;
        } else {
            return false;
        }
    }

};
