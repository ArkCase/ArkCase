/**
 * CaseFile.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
CaseFile.Event = {
    create : function() {
    }
    ,onPostInit: function() {
        var treeInfo = CaseFile.Object.getTreeInfo();
        if (0 < treeInfo.caseFileId) { //single caseFile
            CaseFile.setCaseFileId(treeInfo.caseFileId);
            CaseFile.Service.retrieveDetail(treeInfo.caseFileId);
        } else {
            CaseFile.Service.listCaseFile(treeInfo);
        }

//        var data = CaseFile.Service.getCaseTypes();
//        if (Acm.isEmpty(data)) {
//            //todo: call service, CaseFile.Service.getCaseTypes();
//        }
        //todo: call service, CaseFile.Service.getCaseTypes();
        //todo: call service, CaseFile.Service.getCaseTypes();
        //todo: call service, CaseFile.Service.getCaseTypes();

        //Acm.keepTrying(CaseFile.Event._tryInitCaseType,         8, 200);
        //Acm.keepTrying(CaseFile.Event._tryInitCloseDisposition, 8, 200);
//        Acm.keepTrying(CaseFile.Event._tryInitItemStatus,        8, 200);
        //Acm.keepTrying(CaseFile.Event._tryInitItemAssignee,     8, 200);
    }
    ,onActivateTreeNode: function(node) {
        if ("prevPage" == node.key) {
            var treeInfo = CaseFile.Object.getTreeInfo();
            if (0 < treeInfo.start) {
                treeInfo.start -= treeInfo.n;
                if (0 > treeInfo.start) {
                    treeInfo.start = 0;
                }
            }
            CaseFile.Service.listCaseFile(treeInfo);
            return;
        }
        if ("nextPage" == node.key) {
            var treeInfo = CaseFile.Object.getTreeInfo();
            if (0 > treeInfo.total) {       //should never get to this condition
                treeInfo.start = 0;
            } else if ((treeInfo.total - treeInfo.n) > treeInfo.start) {
                treeInfo.start += treeInfo.n;
            }
            CaseFile.Service.listCaseFile(treeInfo);
            return;
        }

        var caseFileId = CaseFile.Object.getCaseFileIdByKey(node.key);
        CaseFile.setCaseFileId(caseFileId);
        if (0 >= caseFileId) {
            CaseFile.Object.showTop(false);
            return;
        } else {
            CaseFile.Object.showTop(true);
        }

        var caseFile = CaseFile.cacheCaseFile.get(caseFileId);
        if (caseFile) {
            CaseFile.Object.populateCaseFile(caseFile);
        } else {
            CaseFile.Service.retrieveDetail(caseFileId);
        }

        CaseFile.Object.showTab(node.key);
    }
    ,onSaveStartDate: function(value) {
        var c = CaseFile.getCaseFile();
        c.created = Acm.xDateToDatetime(value);
        CaseFile.Service.saveCaseFile(c);
    }
    ,onSaveCaseType: function(value) {
        var c = CaseFile.getCaseFile();
        c.caseType = value;
        CaseFile.Service.saveCaseFile(c);
    }
    ,onSaveCloseDisposition: function(value) {
        var c = CaseFile.getCaseFile();
        c.closeDisposition = value;
        CaseFile.Service.saveCaseFile(c);
    }
    ,onSaveCaseTitle: function(value) {
        var c = CaseFile.getCaseFile();
        c.title = value;
        CaseFile.Service.saveCaseFile(c);
    }
    ,onSaveCasePriority: function(value){
        var c = CaseFile.getCaseFile();
        c.priority = value;
        CaseFile.Service.saveCaseFile(c);
    }
    ,onSaveCaseAssignee: function(value){
        var c = CaseFile.getCaseFile();
        c.assignee = value;
        CaseFile.Service.saveCaseFile(c);
    }
    ,onSaveCaseIncidentDate: function(value) {
        var c = CaseFile.getCaseFile();
        c.incidentDate = Acm.xDateToDatetime(value);
        CaseFile.Service.saveCaseFile(c);
    }
    ,onSaveCaseSubjectType: function(value) {
        var c = CaseFile.getCaseFile();
        c.subjectType = value;
        CaseFile.Service.saveCaseFile(c);
    }
    ,onSaveCaseStatus: function(value) {
        var c = CaseFile.getCaseFile();
        c.status = value;
        CaseFile.Service.saveCaseFile(c);
    }


    ,onClickSpanAddRoi: function(e) {
        var report = CaseFile.Object.getSelectReport();
        var c = CaseFile.getCaseFile();
console.log(c);
        var url = CaseFile.Object.getFormUrls() != null ? CaseFile.Object.getFormUrls()[report] : '';
        if (url != null && url != '') {
        	url = url.replace("_data=(", "_data=(type:'case', caseId:'" + c.id + "',caseNumber:'" + c.caseNumber + "',caseTitle:'" + c.title + "',caseFolderId:'" + c.ecmFolderId + "',");
        	this._showPopup(url, "", 860, $(window).height() - 30);        	
        }
    }

    ,onCloseCase: function(e) {
        Acm.Dialog.confirm("Are you sure you want to close this case?", function(result) {
            if(result){
                var c = CaseFile.getCaseFile();
                CaseFile.Service.closeCaseFile(c);
            }
        });

    }

//    ,_tryInitCaseType: function() {
//        var data = CaseFile.Service.getCaseTypes();
//        if (Acm.isNotEmpty(data)) {
//            CaseFile.Object.initCaseType(data);
//            return true;
//        } else {
//            return false;
//        }
//    }
//    ,_tryInitCloseDisposition: function() {
//        var data = CaseFile.Service.getCloseDispositions();
//        if (Acm.isNotEmpty(data)) {
//            CaseFile.Object.initCloseDisposition(data);
//            return true;
//        } else {
//            return false;
//        }
//    }

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
    }
};
