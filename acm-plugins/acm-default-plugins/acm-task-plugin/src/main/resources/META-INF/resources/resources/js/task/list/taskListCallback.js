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
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_SIGNED, this.onTaskSigned);
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_BYTYPEBYID_RETRIEVED, this.onFindByTypeByIdRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_COMPLAINT_DETAIL_RETRIEVED, this.onComplaintDetailRetrieved);
    }

    ,EVENT_LIST_RETRIEVED			 : "task-list-retrieved"
    ,EVENT_LIST_SAVED				 : "task-list-saved"
    ,EVENT_DETAIL_RETRIEVED			 : "task-list-detail-retrieved"
    ,EVENT_TASK_COMPLETED			 : "task-list-task-completed"
    ,EVENT_TASK_SIGNED				 : "task-list-task-signed"
    ,EVENT_LIST_BYTYPEBYID_RETRIEVED : "task-list-signature-byTypeById-retrieved"
    ,EVENT_DETAIL_SAVED               : "event-detail-saved"
    ,EVENT_COMPLAINT_DETAIL_RETRIEVED : "complaint-detail-retrieved"

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

            var start = treeInfo.start;
            TaskList.cachePage.put(start,taskList);

            TaskList.setTaskList(taskList);

            var key = treeInfo.initKey;
            if (null == key) {
                if (0 < taskList.length) {
                    var taskId = parseInt(taskList[0].object_id_s);
                    if (0 < taskId) {
                        key = start + "." + taskId;
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
                TaskList.cacheTask.put(taskId, task);
                TaskList.Object.updateDetail(task);
                if(response.attachedToObjectId != null){
                    var parentObjId = response.attachedToObjectId;
                    TaskList.setParentObjId(parentObjId);
                    //if(response.attachedToObjectType.ignoreCase == "complaint")
                    TaskList.Service.retrieveComplaintDetail(parentObjId);
                }
                else{
                    var parentObj = {};
                    TaskList.Object.updateParentObjDetail(parentObj);
                }

                // check for signatures
                TaskList.Service.findSignatureByTypeById(taskId);
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
                if (TaskList.isSingleObject()) {
                    App.gotoPage(TaskList.Page.URL_DASHBOARD);
                } else {
                    //todo: remove item from local copy list, no need to call service to retrieve list
                    TaskList.Service.listTask(App.getUserName());
                }
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
};
