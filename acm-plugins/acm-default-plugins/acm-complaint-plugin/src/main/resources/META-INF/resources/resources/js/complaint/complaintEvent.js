/**
 * Complaint.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Complaint.Event = {
    create : function() {
    }
    ,onPostInit: function() {
        var treeInfo = Complaint.Object.getTreeInfo();
        if (0 < treeInfo.complaintId) { //single complaint
            Complaint.setComplaintId(treeInfo.complaintId);
            Complaint.Service.retrieveDetail(treeInfo.complaintId);
        } else {
            Complaint.Service.listComplaint(treeInfo);
        }

        var data = App.Object.getApprovers();
        if (Acm.isEmpty(data)) {
            App.Service.getApprovers();
        }
        data = App.Object.getComplaintTypes();
        if (Acm.isEmpty(data)) {
            App.Service.getComplaintTypes();
        }
        data = App.Object.getPriorities();
        if (Acm.isEmpty(data)) {
            App.Service.getPriorities();
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
        Complaint.Object.showTab(node.key);
    }
    ,onSaveTitle: function(value) {
        var c = Complaint.getComplaint();
        c.complaintTitle = value;
        Complaint.Service.saveComplaint(c);

        Complaint.Object.refreshComplaintTreeNode(c);
    }
    ,onSaveIncidentDate: function(value) {
        var c = Complaint.getComplaint();
        c.incidentDate = Acm.xDateToDatetime(value);
        Complaint.Service.saveComplaint(c);
    }
    ,onSavePriority: function(value) {
        var c = Complaint.getComplaint();
        c.priority = value;
        Complaint.Service.saveComplaint(c);
    }
    ,onSaveAssigned: function(value) {
        var c = Complaint.getComplaint();

        var foundAssignee = false;
        var participantsDefined = typeof c.participants != "undefined";
        if ( participantsDefined )
        {
            for (var partNum = 0; partNum < c.participants.length; partNum++)
            {
                if (c.participants[partNum]['participantType'] == "assignee")
                {
                    c.participants[partNum]['participantLdapId'] = value;
                    foundAssignee = true;
                    break;
                }
            }
        }
        if ( ! foundAssignee )
        {
            var participants = participantsDefined ? c.participants : [];
            c['participants'] = participants;
            var assignee =
            {
                "participantLdapId": value,
                "participantType": "assignee"
            };
            participants.push(assignee);


        }

        Complaint.Service.saveComplaint(c);
    }
    ,onSaveComplaintType: function(value) {
        var c = Complaint.getComplaint();
        //c.complaintType = value;            //fix meeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
        //Complaint.Service.saveComplaint(c);
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

        var url = Complaint.Object.getFormUrls() != null ? Complaint.Object.getFormUrls()[report] : '';
        if (url != '') {
        	url = url.replace("_data=(", "_data=(type:'complaint', complaintId:'" + c.complaintId + "',complaintNumber:'" + c.complaintNumber + "',complaintTitle:'" + c.complaintTitle + "',complaintPriority:'" + c.priority + "',");
        	this._showPopup(url, "", 810, $(window).height() - 30);
        }
    }

    ,onClickBtnTaskAssign : function(e) {
        alert("onClickBtnTaskAssign");
    }
    ,onClickBtnTaskUnassign : function(e) {
        alert("onClickBtnTaskUnassign");
    }
    ,onClickSpanAddTask: function(e) {
        var complaint = Complaint.getComplaint();
        var complaintNumber = complaint.complaintNumber;
        var url = Complaint.Page.URL_NEW_TASK  + complaintNumber;
        App.gotoPage(url);
    }
    ,onCloseComplaint: function(e) {
    	var c = Complaint.getComplaint();
    	
    	var url = Complaint.Object.getFormUrls() != null ? Complaint.Object.getFormUrls()['close_complaint'] : '';
        if (url != null && url != '') {
        	url = url.replace("_data=(", "_data=(complaintId:'" + c.complaintId + "',complaintNumber:'" + c.complaintNumber + "',");
        	this._showPopup(url, "", 860, 700);        	
        }
    }
    ,onEditCloseComplaint: function(e) {
        var c = Complaint.getComplaint();

        var url = Complaint.Object.getFormUrls() != null ? Complaint.Object.getFormUrls()['close_complaint'] : '';
        if (url != null && url != '') {
            url = url.replace("_data=(", "_data=(complaintId:'" + c.complaintId + "',complaintNumber:'" + c.complaintNumber + "',mode:'edit',xmlId:'816',pdfId:'818',requestId:'813',");
            //this._showPopup(url, "", 860, 700);
            Acm.Dialog.openWindow(url, "", 860, 700
                , function () {
                    Complaint.Object.refreshJTableDocuments();
                }
            );
        }
    }
    ,_showPopup: function(url, title, w, h) {

        var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
        var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

        width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
        height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

        var left = ((width / 2) - (w / 2)) + dualScreenLeft;
        var top = ((height / 2) - (h / 2)) + dualScreenTop;
        var newWindow = window.open(url, title, 'scrollbars=yes, resizable=1, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);


        if (window.focus) {
            newWindow.focus();
        }

        this._checkClosePopup(newWindow);
    }

    ,_checkClosePopup: function(newWindow){
        var timer = setInterval(function() {
            if(newWindow.closed) {
                clearInterval(timer);
                Complaint.Object.refreshJTableDocuments();
            }
        }, 1000);
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
            Complaint.Object.$lnkPriority(data);
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
