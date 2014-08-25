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
        if (0 < complaintId) {
            ComplaintList.Service.retrieveDetail(complaintId);
//        ComplaintList.Service.retrieveTasks(complaintId);
            ComplaintList.Object.refreshJTableTasks();
        }

        var c = ComplaintList.findComplaint(complaintId);
        if (c) {
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
    ,onActivateTreeNode: function(node) {
        //alert("onActivateTreeNode:(" + node.key + "," + node.title + "," + node.data.acmIcon + ")");
        if ("prevPage" == node.key) {
            alert("Call service to get previous page");
            return;
        } else if ("nextPage" == node.key) {
            alert("Call service to get next page");
            return;
        }

        var complaintId = ComplaintList.Object.getComplaintIdByKey(node.key);
        if (0 < complaintId) {
            ComplaintList.Service.retrieveDetail(complaintId);
            ComplaintList.Object.refreshJTableTasks();
        }

        var c = ComplaintList.findComplaint(complaintId);
        if (c) {
            ComplaintList.Object.updateDetail(c);
            Complaint.setComplaintId(complaintId);
            ComplaintList.Object.hiliteSelectedItem(complaintId);
        }


        var initTab = ComplaintList.Object.getInitTab();
        if (Acm.isNotEmpty(initTab)) {
            ComplaintList.Object.clickTab(initTab);
            ComplaintList.Object.setInitTab("");
        }
    }
    ,onClickBtnTaskAssign : function(e) {
        alert("onClickBtnTaskAssign");
    }
    ,onClickBtnTaskUnassign : function(e) {
        alert("onClickBtnTaskUnassign");
    }
    ,onClickSpanAddTask: function(e) {
        var complaintId = Complaint.getComplaintId();
        var url = ComplaintList.Page.URL_NEW_TASK + complaintId;
        App.gotoPage(url);
    }
    ,onPostInit: function() {
        if (ComplaintList.isSingleObject()) {
            var complaintId = Complaint.getComplaintId();
            ComplaintList.Service.retrieveDetail(complaintId);
            //ComplaintList.Service.retrieveTasks(complaintId);
        } else {
            ComplaintList.Service.listComplaint();
        }

        Acm.keepTrying(ComplaintList.Event._tryInitAssignee, 8, 200);
        Acm.keepTrying(ComplaintList.Event._tryInitPriority, 8, 200);
        Acm.keepTrying(ComplaintList.Event._tryInitComplaintType, 8, 200);
    }

//    ,onClickLnkNewTasks : function(e) {
//        var complaintId = Complaint.getComplaintId();
//        var url = ComplaintList.Page.URL_NEW_TASK + complaintId;
//        App.gotoPage(url);
//    }
//    ,onChangeSelTasks : function(e) {
//        alert("onChangeSelTasks:" + e.value);
//    }

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
    
    /**
     * To do: complaint_id and other variable values should be
     * populated with real complaint data based on the id. In addition,
     * these values should be passed to orbeon form via the request body.
     */
    ,onChangeSelForm : function(event) {
    	var complaint_id = "52";
    	var complaint_number= "20140430_52";
    	var complaint_title="testTitle";
    	var complaint_priority="Expedite";

    	var pageUrl = ComplaintList.Object.$roiFormUrl + "?acm_ticket=" + ComplaintList.Object.$token 
    		+ "&complaint_id=" + complaint_id + "&complaint_number="+ complaint_number 
    		+ "&complaint_title=" + complaint_title + "&complaint_priority=" + complaint_priority;
    	window.open(pageUrl, "_self");
    }};
