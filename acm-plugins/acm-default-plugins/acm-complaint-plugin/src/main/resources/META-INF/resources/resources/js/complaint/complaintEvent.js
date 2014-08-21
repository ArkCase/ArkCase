/**
 * Complaint.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Complaint.Event = {
    initialize : function() {
    }
    ,onPostInit: function() {
        var treeInfo = Complaint.Object.getTreeInfo();
        if (0 < treeInfo.complaintId) { //single complaint
            Complaint.setComplaintId(treeInfo.complaintId);
            Complaint.Service.retrieveDetail(treeInfo.complaintId);
        } else {
            Complaint.Service.listComplaint(treeInfo);
        }

        Acm.keepTrying(Complaint.Event._tryInitAssignee, 8, 200);
        Acm.keepTrying(Complaint.Event._tryInitPriority, 8, 200);
        Acm.keepTrying(Complaint.Event._tryInitComplaintType, 8, 200);
    }
    ,onActivateTreeNode: function(node) {
        if ("prevPage" == node.key) {
            var treeInfo = Complaint.Object.getTreeInfo();
            if (0 < treeInfo.start) {
                treeInfo.start -= treeInfo.n;
                if (0 > treeInfo.start) {
                    treeInfo.start = 0;
                }
            }
            Complaint.Service.listComplaint(treeInfo);
            return;
        }
        if ("nextPage" == node.key) {
            var treeInfo = Complaint.Object.getTreeInfo();
            if (0 > treeInfo.total) {       //should never get to this condition
                treeInfo.start = 0;
            } else if ((treeInfo.total - treeInfo.n) > treeInfo.start) {
                treeInfo.start += treeInfo.n;
            }
            Complaint.Service.listComplaint(treeInfo);
            return;
        }

        var complaintId = Complaint.Object.getComplaintIdByKey(node.key);
        Complaint.setComplaintId(complaintId);
        if (0 >= complaintId) {
            //show blank complaint in page
            return;
        }

        var complaint = Complaint.cacheComplaint.get(complaintId);
        if (complaint) {
            Complaint.Object.populateComplaint(complaint);
        } else {
            Complaint.Service.retrieveDetail(complaintId);
        }

        //Complaint.Object.activeTreeNode(node.key);

        Complaint.Object.showTab(node.key);
    }
    ,onSaveTitle: function(value) {
        var c = Complaint.getComplaint();
        c.complaintTitle = value;
        Complaint.Service.saveComplaint(c);
    }
    ,onSaveIncidentDate: function(value) {
        var c = Complaint.getComplaint();
        var fullyear = value.getFullYear();
        var month = value.getMonth();
        var day = value.getDay();
        var year = value.getYear();
        var date0 = value.getDate();
        var s = Acm.dateToString(value);

        c.created = Acm.dateToString(value);
        Complaint.Service.saveComplaint(c);
    } //Acm.getDateFromDatetime(c.created)
    ,onSavePriority: function(value) {
        var c = Complaint.getComplaint();
        c.priority = value;
        Complaint.Service.saveComplaint(c);
    }
    ,onSaveAssigned: function(value) {
        var c = Complaint.getComplaint();
        c.assignee = value;
        Complaint.Service.saveComplaint(c);
    }
    ,onSaveComplaintType: function(value) {
        var c = Complaint.getComplaint();
        c.complaintTitle = value;
        Complaint.Service.saveComplaint(c);
    }
    ,onClickBtnEditDetails: function(e) {
        Complaint.Object.editDivDetails();
    }
    ,onClickBtnSaveDetails: function(e) {
        var c = Complaint.getComplaint();
        var html = Complaint.Object.saveDivDetails();
        c.details = html;
        Complaint.Service.saveComplaint(c);
    }
    ,onClickSpanAddDocument: function(e) {
        var report = Complaint.Object.getSelectReport();
        var token = Complaint.Object.setToken();
        var c = Complaint.getComplaint();

        var url = "http://10.21.4.149/orbeon/fr/acm/roi-form/new"
            + "?acm_ticket=" + token
            + "&complaint_id=" + c.complaintId
            + "&complaint_number=" + c.complaintNumber
            + "&complaint_title=" + c.complaintTitle
            + "&complaint_priority=" + c.priority;

        window.location.href = url;
    }

    ,onClickBtnTaskAssign : function(e) {
        alert("onClickBtnTaskAssign");
    }
    ,onClickBtnTaskUnassign : function(e) {
        alert("onClickBtnTaskUnassign");
    }
    ,onClickSpanAddTask: function(e) {
        var complaintId = Complaint.getComplaintId();
        var url = Complaint.Page.URL_NEW_TASK + complaintId;
        App.gotoPage(url);
    }

    ,_tryInitAssignee: function() {
        var data = App.Object.getApprovers();
        if (Acm.isNotEmpty(data)) {
            Complaint.Object.initAssignee(data);
            return true;
        } else {
            return false;
        }
    }
    ,_tryInitPriority: function() {
        var data = App.Object.getPriorities();
        if (Acm.isNotEmpty(data)) {
            Complaint.Object.initPriority(data);
            return true;
        } else {
            return false;
        }
    }
    ,_tryInitComplaintType: function() {
        var data = App.Object.getComplaintTypes();
        if (Acm.isNotEmpty(data)) {
            Complaint.Object.initComplaintType(data);
            return true;
        } else {
            return false;
        }
    }

};
