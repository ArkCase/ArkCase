/**
 * TaskList.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
TaskList.Event = {
    create : function() {
    }
    ,onActivateTreeNode: function(node) {
        if ("prevPage" == node.key) {
            var treeInfo = TaskList.Object.getTreeInfo();
            if (0 < treeInfo.start) {
                treeInfo.start -= treeInfo.n;
                if (0 > treeInfo.start) {
                    treeInfo.start = 0;
                }
            }
            TaskList.Service.listTaskAll(treeInfo);
            return;
        }
        if ("nextPage" == node.key) {
            var treeInfo = TaskList.Object.getTreeInfo();
            if (0 > treeInfo.total) {       //should never get to this condition
                treeInfo.start = 0;
            } else if ((treeInfo.total - treeInfo.n) > treeInfo.start) {
                treeInfo.start += treeInfo.n;
            }
            TaskList.Service.listTaskAll(treeInfo);
            return;
        }

        var taskId = TaskList.Object.getTaskIdByKey(node.key);
        TaskList.setTaskId(taskId);
        if (0 >= taskId) {
            //show blank TaskList in page
            return;
        }

        var task = TaskList.cacheTask.get(taskId);
        if (task) {
            TaskList.Object.updateDetail(task);
        } else {
            TaskList.Service.retrieveDetail(taskId);
        }
        TaskList.Object.showTab(node.key);
    }
    ,onClickBtnSignConfirm : function(e) {
        var taskId = Task.getTaskId();

        TaskList.Object.hideSignatureModal();
        TaskList.Service.signTask(taskId);
    }
    ,onClickBtnReject : function(e) {
        //alert("onClickBtnReject");
        var taskId = TaskList.getTaskId();
    }
    ,onClickBtnAdHocTaskComplete : function(e) {
        var taskId = TaskList.getTaskId();
        TaskList.Service.completeTask(taskId);
    }
    ,onClickBtnAdHocTaskDelete : function(e) {
        var taskId = TaskList.getTaskId();
        TaskList.Service.deleteTask(taskId);

    }
    
    // Reject Task Events
    ,_onRetrieveUsers: function() {
    	var task = TaskList.getTask();
    	var start = TaskList.Object.getDlgRejectTaskStart();
    	var n = TaskList.Object.getDlgRejectTaskN();
    	var sortDirection = TaskList.Object.getDlgRejectTaskSortDirection();
    	var searchKeyword = TaskList.Object.getDlgRejectTaskSearchKeyword();
    	var exclude = task.owner;
    	
    	TaskList.Service.retrieveUsers(start, n, sortDirection, searchKeyword, exclude);
    }
    ,onClickBtnRejectTask: function(e) {
    	TaskList.Object.initDlgRejectTask();
    	this._onRetrieveUsers();
    	TaskList.Object.showDlgRejectTask(function(event, ctrl) {
    		var returnTo = TaskList.Object.getDlgRejectTaskSelected();
    		if (returnTo != null) {
    			TaskList.Event.onSaveAssignee(returnTo);
    		}
    	});
    }
    ,onClickDlgRejectTaskSortableColumn: function(e) {
    	var sortDirection = TaskList.Object.getDlgRejectTaskSortDirection();
    	
    	if (sortDirection && sortDirection == 'ASC') {
    		TaskList.Object.setDlgRejectTaskSortDirection('DESC');
    	} else {
    		TaskList.Object.setDlgRejectTaskSortDirection('ASC');
    	}
    	
    	this._onRetrieveUsers();
    }
    ,onClickDlgRejectTaskLeftBtn: function(e) {
    	var start = TaskList.Object.getDlgRejectTaskStart();
    	var page = TaskList.Object.getDlgRejectTaskPage();
    	
    	if (page > 0) {
    		TaskList.Object.setDlgRejectTaskStart(start - TaskList.Object.getDlgRejectTaskN());
    		TaskList.Object.setDlgRejectTaskPage(page - 1);
    	}
    	
    	this._onRetrieveUsers();
    }
    ,onClickDlgRejectTaskRightBtn: function(e) {
    	var start = TaskList.Object.getDlgRejectTaskStart();
    	var page = TaskList.Object.getDlgRejectTaskPage();
    	var pages = TaskList.Object.getDlgRejectTaskPages();
    	
    	if (page < pages) {
    		TaskList.Object.setDlgRejectTaskStart(start + TaskList.Object.getDlgRejectTaskN());
    		TaskList.Object.setDlgRejectTaskPage(page + 1);
    	}
    	
    	this._onRetrieveUsers();
    }
    ,onClickDlgRejectTaskPageBtn: function(e) {
    	var $page = $(e.target);
    	var page = $page.html();
    	
    	if (page) {
    		TaskList.Object.setDlgRejectTaskStart((page - 1) * TaskList.Object.getDlgRejectTaskN());
    		TaskList.Object.setDlgRejectTaskPage(page);
    	}
    	
    	this._onRetrieveUsers();
    }
    ,onClickSearchRejectTask: function(e) {
    	var keyword = TaskList.Object.$inputSearchRejectTask.val();
    	TaskList.Object.setDlgRejectTaskSearchKeyword(keyword);
    	
    	TaskList.Object.setDlgRejectTaskStart(TaskList.DLG_RETURN_TASK_START);
    	TaskList.Object.setDlgRejectTaskN(TaskList.DLG_REJECT_TASK_N);
    	TaskList.Object.setDlgRejectTaskSortDirection(TaskList.DLG_REJECT_TASK_SORT_DIRECTION);
    	TaskList.Object.setDlgRejectTaskPage(0);
    	TaskList.Object.setDlgRejectTaskPages(0);
    	
    	this._onRetrieveUsers();
    }
    ,onKeyUpSearchRejectTask: function(e) {
    	if (e.keyCode == 13) {
    		var keyword = TaskList.Object.$inputSearchRejectTask.val();
        	TaskList.Object.setDlgRejectTaskSearchKeyword(keyword);
        	
        	TaskList.Object.setDlgRejectTaskStart(TaskList.DLG_REJECT_TASK_START);
        	TaskList.Object.setDlgRejectTaskN(TaskList.DLG_REJECT_TASK_N);
        	TaskList.Object.setDlgRejectTaskSortDirection(TaskList.DLG_REJECT_TASK_SORT_DIRECTION);
        	TaskList.Object.setDlgRejectTaskPage(0);
        	TaskList.Object.setDlgRejectTaskPages(0);
        	
    		this._onRetrieveUsers();
    	}
    }
    ,onChangeDlgRejectTaskSelected: function(e) {
    	TaskList.Object.setDlgRejectTaskSelected($(e.target).val());
    }

    ,onClickBtnTaskOutcomeApprove : function(e) {
        var task = TaskList.getTask();
        for(var i = 0; i < task.availableOutcomes.length; i++){
            var availableOutcome = task.availableOutcomes[i];
            if(availableOutcome.name == "APPROVE"){
                task.taskOutcome = availableOutcome;
            }
        }
        TaskList.Service.completeTaskWithOutcome(task);
    }
    ,onClickBtnTaskOutcomeRework : function(e) {
        var task = TaskList.getTask();
        var reworkInstructions = AcmEx.Object.SummerNote.get(TaskList.Object.$divReworkInstructions);
        if(reworkInstructions == null || reworkInstructions == ""){
            Acm.Dialog.error("Invalid rework instructions")
        }
        else{
            //reworkInstructions = AcmEx.Object.SummerNote.get(TaskList.Object.$divReworkInstructions);
            task.reworkInstructions = reworkInstructions;

            var requiredField = {};
            for(var i = 0; i < task.availableOutcomes.length; i++){
                var availableOutcome = task.availableOutcomes[i];
                if(availableOutcome.name == "SEND_FOR_REWORK"){
                    task.taskOutcome = availableOutcome;
                }
            }
            TaskList.Service.completeTaskWithOutcome(task);
        }
    }
    ,onClickBtnTaskOutcomeResubmit : function(e) {
        var task = TaskList.getTask();
        for(var i = 0; i < task.availableOutcomes.length; i++){
            var availableOutcome = task.availableOutcomes[i];
            if(availableOutcome.name == "RESUBMIT"){
                task.taskOutcome = availableOutcome;
            }
        }
        TaskList.Service.completeTaskWithOutcome(task);
    }
    ,onClickBtnTaskOutcomeCancelRequest : function(e) {
        var task = TaskList.getTask();
        for(var i = 0; i < task.availableOutcomes.length; i++){
            var availableOutcome = task.availableOutcomes[i];
            if(availableOutcome.name == "CANCEL_DOCUMENT"){
                task.taskOutcome = availableOutcome;
            }
        }
        TaskList.Service.completeTaskWithOutcome(task);
    }
    ,onPostInit: function() {

        var treeInfo = TaskList.Object.getTreeInfo();
        TaskList.setTaskId(treeInfo.taskId);

        if (0 < treeInfo.taskId) { //single task
            TaskList.setTaskId(treeInfo.taskId);
            TaskList.Service.retrieveDetail(treeInfo.taskId);
        } else {
//            TaskList.Service.listTask(App.getUserName());
            TaskList.Service.listTaskAll(treeInfo,App.getUserName());

        }
        var data = App.Object.getPriorities();
        if (Acm.isEmpty(data)) {
            App.Service.getPriorities();
        }

        //Acm.keepTrying(TaskList.Event._tryInitAssignee, 8, 200);
        //Acm.keepTrying(TaskList.Event._tryInitTaskListType, 8, 200);
        Acm.keepTrying(TaskList.Event._tryInitPriority, 8, 200);

    }


    /*,_tryInitAssignee: function() {
        var data = App.Object.getApprovers();
        if (Acm.isNotEmpty(data)) {
            TaskList.Object.initAssignee(data);
            return true;
        } else {
            return false;
        }
    }*/
    ,_tryInitComplaintType: function() {
        var data = App.Object.getComplaintTypes();
        if (Acm.isNotEmpty(data)) {
            TaskList.Object.initComplaintType(data);
            return true;
        } else {
            return false;
        }
    }
    ,_tryInitPriority: function() {
        var data = App.Object.getPriorities();
        if (Acm.isNotEmpty(data)) {
            TaskList.Object.initPriority(data);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Save title value changed
     */
    ,onSaveTitle : function(value) {
        var task = TaskList.getTask();
        task.title = value;
        TaskList.Service.listTaskSaveDetail(task.taskId, task);
        TaskList.Object.refreshTaskTreeNode(task);
    }
    
    /**
     * Save owner value changed
     */
    ,onSaveOwner : function(value) {
        var task = TaskList.getTask();
        task.owner = value;
        TaskList.Service.listTaskSaveDetail(task.taskId, task);
    }
    
    /**
     * Save percentage completed value changed
     */
    ,onSavePerComplete : function(value) {
        var task = TaskList.getTask();
        task.percentComplete = value;
        TaskList.Service.listTaskSaveDetail(task.taskId, task);
    }

    /**
     * Save priority value changed
     */
    ,onSavePriority : function(value) {
        var task = TaskList.getTask();
        task.priority = value;
        TaskList.Service.listTaskSaveDetail(task.taskId, task);
        TaskList.Object.refreshTaskTreeNode(task);
    }

    /**
     * Save start date value changed
     */
    ,onSaveStartDate : function(value) {
        var task = TaskList.getTask();
        task.taskStartDate = Acm.xDateToDatetime(value);
        TaskList.Service.listTaskSaveDetail(task.taskId, task);
    }
    
    /**
     * Save start date value changed
     */
    ,onSaveDueDate : function(value) {
        var task = TaskList.getTask();
        task.dueDate = Acm.xDateToDatetime(value);
        TaskList.Service.listTaskSaveDetail(task.taskId, task);
    }
    
    /**
     * Save assignee value changed
     */
    ,onSaveAssignee : function(value) {
        var task = TaskList.getTask();
        task.assignee = value;
        TaskList.Service.listTaskSaveDetail(task.taskId, task);
    }

    /**
     * Open the detail section editor
     */
    ,onClickBtnEditDetails: function(e) {
        TaskList.Object.editDivDetails();
    }

    /**
     * Save the detail section update to backend
     */
    ,onClickBtnSaveDetails : function(e) {
        var task = TaskList.getTask();
        var value = TaskList.Object.saveDivDetails();
        task.details = value;
        TaskList.Service.listTaskSaveDetail(task.taskId, task);
    }

    /**
     * Open the rework instructions section editor
     */
    ,onClickBtnEditReworkInstructions: function(e) {
        TaskList.Object.editDivReworkInstructions();
    }

    /**
     * Save the rework instructions section update to backend
     */
    ,onClickBtnSaveReworkInstructions : function(e) {
        var task = TaskList.getTask();
        var value = TaskList.Object.saveDivReworkInstructions();
        task.reworkInstructions = value;
        TaskList.Service.listTaskSaveDetail(task.taskId, task);
    }


    /////////////////////////////////////////////////////////////////////////////////
    // This section should move to the request object file
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Setup the task object to be saved. We have to send
     * all the attributes of the task object to the backend.
     */
    ,getTaskData : function(t) {
        var data = {};
        data.assignee = t.assignee;
        data.owner = t.owner;
        data.attachedToObjectType = "COMPLAINT";
        data.attachedToObjectId = t.attachedToObjectId;
        data.title = t.title;
        data.taskStartDate = t.taskStartDate;
        data.status = t.status;
        //data.priority = this.getValueEdtPriority();
        data.dueDate = t.dueDate;
        data.priority = t.priority;
        data.percentComplete = t.percentComplete;
        data.details = t.details;
        data.adhocTask = true;

        data.taskId = t.taskId;
        data.businessProcessName = t.businessProcessName;
        data.completed = t.completed;
        data.taskFinishedDate = t.taskFinishedDate;
        data.taskDurationInMillis = t.taskDurationInMillis;
        return data;
    }

    ,onAddNewAttachment: function(event, ctrl) {
        event.preventDefault();
        /*this.$formAttachment = TaskList.Object.$formAttachment;
         this.$btnNewAttachment = TaskList.Object.$btnNewAttachment;*/
        var count = TaskList.Object.$btnNewAttachment[0].files.length;
        var fd = new FormData();
        fd.append("taskId", TaskList.getTaskId());
        for(var i = 0; i < count; i++ ){
            fd.append("files[]", TaskList.Object.$btnNewAttachment[0].files[i]);
        }
        TaskList.Service.uploadFile(fd);
    }
    ,onChangeFileInput: function(event, ctrl) {
        TaskList.Object.$formAttachment.submit();
    }

    //frevvo edit close complaint
    ,onEditCloseComplaint: function(e) {
        /*var doc = {
            "fileId" : 4056,
            "status" : "ACTIVE",
            "created" : "2014-11-06T20:15:27.541+0000",
            "creator" : "ecmillar",
            "modified" : "2014-11-06T20:15:27.541+0000",
            "modifier" : "ecmillar",
            "ecmFileId" : "workspace://SpacesStore/88fa9bbd-f1ae-4b94-985a-ace90d3da228",
            "fileName" : "Close_Complaint_06112014151525629.pdf",
            "fileMimeType" : "application/pdf;frevvo-snapshot=true; charset=utf-8",
            "fileType" : "close_complaint",
            "parentObjects" : [{
                "associationId" : 4057,
                "status" : "ACTIVE",
                "parentName" : "20140806_198",
                "parentType" : "COMPLAINT",
                "parentId" : 409,
                "targetName" : "Close_Complaint_06112014151525629.pdf",
                "targetType" : "FILE",
                "targetId" : 4056,
                "created" : "2014-11-06T20:15:27.541+0000",
                "creator" : "ecmillar",
                "modified" : "2014-11-06T20:15:27.541+0000",
                "modifier" : "ecmillar"
            }
            ]
        };*/
        var task = TaskList.getTask();
        var documentUnderReview = null;
        var parentName = null;
        var parentId = null;
        var reviewDocumentPdfRenditionId = null;
        var reviewDocumentFormXmlId = null;
        var workflowRequestId = null;

        //task.documentUnderReview = doc;
        if(task.documentUnderReview != null){
            var documentUnderReview = task.documentUnderReview;
            var parentName = documentUnderReview.parentObjects[0].parentName;
            var parentId = documentUnderReview.parentObjects[0].parentId;
            var reviewDocumentPdfRenditionId = task.reviewDocumentPdfRenditionId;
            var reviewDocumentFormXmlId = task.reviewDocumentFormXmlId;
            var workflowRequestId = task.workflowRequestId;

            var url = TaskList.Object.getFormUrls() != null ? TaskList.Object.getFormUrls()['edit_close_complaint'] : '';
            if (url != null && url != '') {
                url = url.replace("_data=(", "_data=(complaintId:'" +  parentId + "',complaintNumber:'" + parentName +
                    "',mode:'edit',xmlId:" + "'" + reviewDocumentFormXmlId + "'" + ",pdfId:" + "'" + reviewDocumentPdfRenditionId + "'" + ",requestId:" + "'" + workflowRequestId + "'" + ",");
                //url = url.replace("_data=(", "_data=(complaintId:'" + "409" + "',complaintNumber:'" + "20140806_198" + "',mode:'edit',xmlId:'783',pdfId:'785',requestId:'780',");

                Acm.Dialog.openWindow(url, "", 860, 700, this.onDone);
            }
        }
        else{
            Acm.Dialog.info("Edit cannot be performed without documents under review")
        }
    }
    
    ,onChangeCaseStatus: function(e) {
        var task = TaskList.getTask();
        var documentUnderReview = null;
        var parentName = null;
        var parentId = null;
        var reviewDocumentPdfRenditionId = null;
        var reviewDocumentFormXmlId = null;
        var workflowRequestId = null;

        //task.documentUnderReview = doc;
        if(task.documentUnderReview != null){
            var documentUnderReview = task.documentUnderReview;
            var parentName = documentUnderReview.parentObjects[0].parentName;
            var parentId = documentUnderReview.parentObjects[0].parentId;
            var reviewDocumentPdfRenditionId = task.reviewDocumentPdfRenditionId;
            var reviewDocumentFormXmlId = task.reviewDocumentFormXmlId;
            var workflowRequestId = task.workflowRequestId;

            var url = TaskList.Object.getFormUrls() != null ? TaskList.Object.getFormUrls()['change_case_status'] : '';
            if (url != null && url != '') {
                url = url.replace("_data=(", "_data=(caseId:'" +  parentId + "',caseNumber:'" + parentName +
                    "',mode:'edit',xmlId:" + "'" + reviewDocumentFormXmlId + "'" + ",pdfId:" + "'" + reviewDocumentPdfRenditionId + "'" + ",requestId:" + "'" + workflowRequestId + "'" + ",");

                Acm.Dialog.openWindow(url, "", 860, 700, this.onDone);
            }
        }
        else{
            Acm.Dialog.info("Edit cannot be performed without documents under review")
        }
    }

    ,onDone: function() {
    	// TODO: Open module after closing the popup window
    }
};
