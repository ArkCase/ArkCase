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
            Complaint.Object.updateDetail(complaint);
        } else {
            Complaint.Service.retrieveDetail(complaintId);
        }

        //Complaint.Object.activeTreeNode(node.key);

        Complaint.Object.showTab(node.key);

        //alert("onActivateTreeNode:(" + node.key + "," + node.title + "," + node.data.acmIcon + ")");

//        if ("prevPage" == node.key) {
//            alert("Call service to get previous page");
//            return;
//        } else if ("nextPage" == node.key) {
//            alert("Call service to get next page");
//            return;
//        }
//
//        var complaintId = Complaint.Object.getComplaintIdByKey(node.key);
//        if (0 < complaintId) {
//            Complaint.Service.retrieveDetail(complaintId);
//            Complaint.Object.refreshJTableTasks();
//        }
//
//        var c = Complaint.findComplaint(complaintId);
//        if (c) {
//            Complaint.Object.updateDetail(c);
//            Complaint.setComplaintId(complaintId);
//            Complaint.Object.hiliteSelectedItem(complaintId);
//        }
//
//
//        var initTab = Complaint.Object.getInitTab();
//        if (Acm.isNotEmpty(initTab)) {
//            Complaint.Object.clickTab(initTab);
//            Complaint.Object.setInitTab("");
//        }
    }



    ,onClickLnkListItemImage : function(e) {
        var complaintId = Complaint.Object.getHiddenComplaintId(e);
        if (Complaint.getComplaintId() == complaintId) {
            return;
        } else {
            Complaint.setComplaintId(complaintId);
        }

        this.doClickLnkListItem();
    }
    ,onClickLnkListItem : function(e) {
        var complaintId = Complaint.Object.getHiddenComplaintId(e);
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
            Complaint.Service.retrieveDetail(complaintId);
//        Complaint.Service.retrieveTasks(complaintId);
            Complaint.Object.refreshJTableTasks();
        }

        var c = Complaint.findComplaint(complaintId);
        if (c) {
            Complaint.Object.updateDetail(c);
            Complaint.setComplaintId(complaintId);
            Complaint.Object.hiliteSelectedItem(complaintId);
            //todo: bring item in list to view
        }


        var initTab = Complaint.Object.getInitTab();
        if (Acm.isNotEmpty(initTab)) {
            Complaint.Object.clickTab(initTab);
            Complaint.Object.setInitTab("");
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
        var url = Complaint.Page.URL_NEW_TASK + complaintId;
        App.gotoPage(url);
    }
    ,onPostInit0: function() {
        if (Complaint.isSingleObject()) {
            var complaintId = Complaint.getComplaintId();
            Complaint.Service.retrieveDetail(complaintId);
            //Complaint.Service.retrieveTasks(complaintId);
        } else {
            Complaint.Service.listComplaint();
        }

        Acm.keepTrying(Complaint.Event._tryInitAssignee, 8, 200);
        Acm.keepTrying(Complaint.Event._tryInitPriority, 8, 200);
        Acm.keepTrying(Complaint.Event._tryInitComplaintType, 8, 200);
    }

//    ,onClickLnkNewTasks : function(e) {
//        var complaintId = Complaint.getComplaintId();
//        var url = Complaint.Page.URL_NEW_TASK + complaintId;
//        App.gotoPage(url);
//    }
//    ,onChangeSelTasks : function(e) {
//        alert("onChangeSelTasks:" + e.value);
//    }

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
