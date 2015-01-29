/**
 * TaskList.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
TaskList.Callback = {
    create : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_RETRIEVED, this.onListRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_SAVED, this.onDetailSaved);
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_COMPLETED, this.onTaskCompleted);
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_COMPLETED_WITH_OUTCOME, this.onTaskCompletedWithOutcome);
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_DELETED, this.onTaskDeleted);
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_SIGNED, this.onTaskSigned);
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_BYTYPEBYID_RETRIEVED, this.onFindByTypeByIdRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAINT_DETAIL_RETRIEVED, this.onComplaintDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_CASE_DETAIL_RETRIEVED, this.onCaseDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_NOTE_SAVED, this.onNoteSaved);
        Acm.Dispatcher.addEventListener(this.EVENT_NOTE_DELETED, this.onNoteDeleted);
        Acm.Dispatcher.addEventListener(this.EVENT_NOTE_LIST_RETRIEVED, this.onNotesListRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_WORKFLOW_HISTORY_RETRIEVED, this.onWorkflowHistoryRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_EVENTS_RETRIEVED, this.onTaskEventsRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_USERS_RETRIEVED, this.onUsersRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_REJECT_COMMENTS_LIST_RETRIEVED, this.onRejectCommentsListRetrieved);
    }

    ,EVENT_LIST_RETRIEVED			 : "task-list-retrieved"
    ,EVENT_LIST_SAVED				 : "task-list-saved"
    ,EVENT_DETAIL_RETRIEVED			 : "task-list-detail-retrieved"
    ,EVENT_TASK_COMPLETED			 : "task-list-task-completed"
    ,EVENT_TASK_COMPLETED_WITH_OUTCOME : "task-completed-with-outcome"
    ,EVENT_TASK_DELETED                 : "task-list-task-deleted"
    ,EVENT_TASK_SIGNED				 : "task-list-task-signed"
    ,EVENT_LIST_BYTYPEBYID_RETRIEVED : "task-list-signature-byTypeById-retrieved"
    ,EVENT_DETAIL_SAVED               : "event-detail-saved"
    ,EVENT_COMPLAINT_DETAIL_RETRIEVED : "complaint-detail-retrieved"
    ,EVENT_CASE_DETAIL_RETRIEVED : "case-detail-retrieved"
    ,EVENT_NOTE_SAVED           : "object-note-saved"
    ,EVENT_NOTE_DELETED         : "object-note-deleted"
    ,EVENT_NOTE_LIST_RETRIEVED  : "object-note-listed"
    ,EVENT_WORKFLOW_HISTORY_RETRIEVED: "workflow-history-retrieved"
    ,EVENT_TASK_EVENTS_RETRIEVED: "task-events-retrieved"
    ,EVENT_USERS_RETRIEVED: "task-users-retrieved"
    ,EVENT_REJECT_COMMENTS_LIST_RETRIEVED  : "object-reject-comments-retrieved"

    ,onDetailSaved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to save task detail:"  +response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.taskId)) {
            	TaskList.Callback.processTaskDetailUpdate(response);
            }
        }
    	
    }
    ,onListRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve task list:"  +response.errorMsg);
        } else {
        	var responseData = response.response;
        	var taskList = responseData.docs;

            var treeInfo = TaskList.Object.getTreeInfo();
            treeInfo.total = taskList.length;
            var start = treeInfo.start;
            TaskList.cachePage.put(start,taskList);

            TaskList.setTaskList(taskList);

            var key = treeInfo.initKey;
            if (null == key) {
                if (0 < taskList.length) {
                    var taskId = parseInt(taskList[0].object_id_s);
                    if (0 < taskId) {
                        if(taskList[0].adhocTask_b == true){
                            key = start + "." + "adHoc"+ taskId;
                        }
                        else{
                            key = start + "." + taskId;
                        }
                    }
                }
            } else {
                treeInfo.initKey = null;
            }
            TaskList.Object.refreshTree(key);
        }
    }
    ,onDetailRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve task detail:"  +response.errorMsg);
        } else {
        	var taskId = response.taskId
            if (Acm.isNotEmpty(taskId)) {
                var taskId = TaskList.getTaskId();
                if (taskId != response.taskId) {
                    return;         //user clicks another task before callback, do nothing
                }
                var task = response;
                //handle single task situation
                var treeInfo = TaskList.Object.getTreeInfo();
                if (0 < treeInfo.taskId) {
                    treeInfo.total = 1;

                    var pageId = treeInfo.start;
                    var taskSolr = {};
                    taskSolr.due_tdt = task.dueDate;
                    taskSolr.title_parseable = task.title;
                    taskSolr.priority_s = task.priority;
                    taskSolr.object_id_s = task.taskId;
                    taskSolr.parent_object_id_i = task.attachedToObjectId;
                    taskSolr.parent_object_type_s = task.attachedToObjectType;
                    taskSolr.adhocTask_b = task.adhocTask;
                    TaskList.cachePage.put(pageId, [taskSolr]);
                    var key;
                    if(task.adhocTask == true){
                        key = pageId + "." + "adHoc" + treeInfo.taskId.toString();
                    }
                    else{
                        key = pageId + "." + treeInfo.taskId.toString();
                    }
                    TaskList.Object.refreshTree(key);
                }

                TaskList.cacheTask.put(taskId, task);

                if(response.attachedToObjectId != null){
                    var parentObjId = response.attachedToObjectId;
                    TaskList.setParentObjId(parentObjId);
                    if(response.attachedToObjectType.toLowerCase() == "complaint") {
                    	TaskList.Service.retrieveComplaintDetail(parentObjId);	
                    }else if(response.attachedToObjectType.toLowerCase() == "case_file") {
                    	TaskList.Service.retrieveCaseDetail(parentObjId);
                    }
                }
                else{
                    var parentObj = {};
                    TaskList.Object.updateParentObjDetail(parentObj);
                }
                // check for signatures
                TaskList.Service.findSignatureByTypeById(taskId);


                //check for notes
                var parentId;
                var parentType;
                if(task.businessProcessId != null){
                    parentId = task.businessProcessId;
                    parentType = App.OBJTYPE_BUSINESS_PROCESS;
                }
                else{
                    parentId = task.taskId;
                    parentType = App.OBJTYPE_TASK;
                }
                var notes = TaskList.cacheNoteList.get(parentId);
                if (notes) {
                    TaskList.Object.refreshJTableNotes();
                    //TaskList.Object.updateDetail(task);
                } else {
                    TaskList.Service.retrieveNotes(parentId,parentType);
                }
                
                // Workflow History
                if (task && task.businessProcessId){
                	TaskList.Service.retrieveWorkflowHistory(task.businessProcessId, "false");
                }else if (task && task.taskId){
                	TaskList.Service.retrieveWorkflowHistory(task.taskId, "true");
                }


                //attachments list
                if(task && task.childObjects){
                    var attachmentsList = [];
                    for(var i = 0; i < task.childObjects.length; i++){
                        var childObject = task.childObjects[i];
                        var attachment = {};
                        attachment.id = childObject.targetId;
                        attachment.name= childObject.targetName;
                        attachment.created = childObject.created;
                        attachment.creator=childObject.creator;
                        attachment.status=childObject.status;
                        attachmentsList.push(attachment);
                    }
                    TaskList.cacheAttachments.put(task.taskId, attachmentsList);
                }
                
                // Reject Comments
                if(task && task.adhocTask == true){
	                var rejectComments = TaskList.cacheRejectComments.get(parentId);
	                if (rejectComments) {
	                    TaskList.Object.refreshJTableRejectComments();
	                } else {
	                    TaskList.Service.retrieveRejectComments(TaskList.REJECT_COMMENT,parentId,parentType);
	                }
                }

                //load all the details
                TaskList.Object.updateDetail(task);

            }
        }
    }

    ,onComplaintDetailRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve complaint detail:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.complaintId)) {
                var complaint = response;
                var complaintId = TaskList.getParentObjId();
                if (complaintId != complaint.complaintId) {
                    return;         //user clicks another complaint before callback, do nothing
                }
                TaskList.cacheParentObject.put(complaintId+"_complaint", complaint);

                //pack data into parent object
                var parentObj = {}
                parentObj.title = complaint.complaintTitle;
                parentObj.incidentDate = complaint.created;
                parentObj.priority =  complaint.priority;
                parentObj.assignee = complaint.creator;
                parentObj.status = complaint.status;
                parentObj.subjectType = complaint.complaintType;
                parentObj.number = complaint.complaintNumber;
                TaskList.Object.updateParentObjDetail(parentObj);
            }
        }
    }
    
    ,onCaseDetailRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve case detail:" + response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.id)) {
                var c = response;
                var id = TaskList.getParentObjId();
                if (id != c.id) {
                    return;         //user clicks another case before callback, do nothing
                }
                TaskList.cacheParentObject.put(id+"_case_file", c);

                //pack data into parent object
                var parentObj = {}
                parentObj.title = c.title;
                parentObj.incidentDate = c.created;
                parentObj.priority =  c.priority;
                parentObj.assignee = c.creator;
                parentObj.status = c.status;
                parentObj.subjectType = c.caseType;
                parentObj.number = c.caseNumber;
                TaskList.Object.updateParentObjDetail(parentObj);
            }
        }
    }
    
    /**
     * Based on an AJAX call resposne, update the task details section
     */
    ,processTaskDetailUpdate : function(response) {
        var taskId = TaskList.getTaskId();
        if (taskId != response.taskId) {
            return;         //user clicks another task before callback, do nothing
        }
        var task = response;
        TaskList.cacheTask.put(taskId,task)
        TaskList.Object.updateDetail(task);    	
    }
    
    ,onTaskCompleted : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to complete task:"  +response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.taskId)) {
                TaskList.Object.hideAllWorkflowButtons();
                var taskId = TaskList.getTaskId();
                TaskList.cacheTask.put(taskId,response);
            }
        }
    }
    ,onTaskCompletedWithOutcome : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to complete task with outcome:"  +response.errorMsg);
        } else {
            TaskList.Object.hideAllWorkflowButtons();
            var taskId = TaskList.getTaskId();
            TaskList.cacheTask.put(taskId,response);
            TaskList.Object.hideDynamicWorkflowButtons();
        }
    }
    ,onTaskDeleted : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to delete task:"  +response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.taskId)) {
                TaskList.Object.hideAllWorkflowButtons();
                var taskId = TaskList.getTaskId();
                TaskList.cacheTask.put(taskId,response);
            }
        }
    }
    ,onTaskSigned : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to electronically sign task. Incorrect parameters.");
        } else {
        	var taskId = response.objectId;
            if (Acm.isNotEmpty(taskId)) {
                if (TaskList.isSingleObject()) {
                    App.gotoPage(TaskList.Page.URL_DASHBOARD);
                } else {
                	// no need to call service to retrieve list on signing, also keeps user on the current task in the list
                
                    // refresh signature
                    TaskList.Service.findSignatureByTypeById(taskId);
                }
            }
        }
    }
    ,onFindByTypeByIdRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve signature list");
        } else {
        	TaskList.Page.buildSignatureList(response);
        }
    }

    ,onNoteSaved: function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to create or save note:" + response.errorMsg);
        } else {
            //update the note list cache manually instead of calling service
            //next refresh will update the cache anyway
            var task = TaskList.getTask();
            var id;
            if(task != null){
                if(task.businessProcessId != null){
                    id = task.businessProcessId;
                }
                else{
                    id = task.taskId;
                }
            }
            var oldNotesList = TaskList.cacheNoteList.get(id);
            var updatedNotesList = {};
            var isNew = true;
            if(oldNotesList){
                for(var i = 0; i < oldNotesList.length; i++){
                    if(response.id == oldNotesList[i].id){
                        oldNotesList[i] = response;
                        updatedNotesList = oldNotesList;
                        isNew = false;
                    }
                }
                if(isNew == true){
                    updatedNotesList = oldNotesList;
                    updatedNotesList.push(response);
                }
                TaskList.cacheNoteList.put(id, updatedNotesList);
                TaskList.Object.refreshJTableNotes();

            }
        }
    }
    ,onNoteDeleted : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to delete note:" + response.errorMsg);
        } else {
            //update the note list cache manually instead of calling service
            //next refresh will update the cache anyway
            var task = TaskList.getTask();
            var id;
            if(task != null){
                if(task.businessProcessId != null){
                    id = task.businessProcessId;
                }
                else{
                    id = task.taskId;
                }
            }
            var oldNotesList = TaskList.cacheNoteList.get(id);
            if(oldNotesList){
                var updatedNotesList = {};
                var deletedNoteId = response.deletedNoteId;
                for(var i = 0; i < oldNotesList.length; i++)
                {
                    if(oldNotesList[i].id == deletedNoteId){
                        oldNotesList.splice(i, 1);
                        updatedNotesList = oldNotesList;
                        TaskList.cacheNoteList.put(id, updatedNotesList);
                        TaskList.Object.refreshJTableNotes();
                    }
                }
            }
        }
    }
    ,onNotesListRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to list notes:" + response.errorMsg);
        } else {
            var task = TaskList.getTask();
            var id;
            if(task != null){
                if(task.businessProcessId != null){
                    id = task.businessProcessId;
                }
                else{
                    id = task.taskId;
                }
            }
            TaskList.cacheNoteList.put(id,response)
            TaskList.Object.refreshJTableNotes();
        }
    }
    
    ,onWorkflowHistoryRetrieved : function(Callback, response) {
        if (response.hasError) {
            //ignore all warnings for now

            //Acm.Dialog.error("Failed to retrieve workflow history.");

        } else {
        	/*var task = TaskList.getTask();
        	task.workflowHistory = response;
        	
            TaskList.cacheTask.put(task.taskId, task);
             */
            var taskId = TaskList.getTaskId();
            TaskList.cacheWorkflowHistory.put(taskId, response);
            TaskList.Object.refreshJTableWorkflowOverview();
        }
    }
    
    ,onUsersRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve users." + response.errorMsg);
        } else {
        	TaskList.Object.refreshDlgRejectTaskUsers(response);
        }
    }
    
    ,onRejectCommentsListRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to list reject comments:" + response.errorMsg);
        } else {
            var taskId = TaskList.getTaskId();
            TaskList.cacheRejectComments.put(taskId,response)
            TaskList.Object.refreshJTableRejectComments();
        }
    }
};
