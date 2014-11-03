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
        Task.setTaskId(taskId);
        if (0 >= taskId) {
            //show blank TaskList in page
            return;
        }

        var task = Task.getTask();
        if (task) {
            TaskList.Service.retrieveDetail(taskId);
        }
        TaskList.Object.showTab(node.key);
    }
    ,onClickLnkListItemImage : function(e) {
        var taskId = TaskList.Object.getHiddenTaskId(e);
        if (Task.getTaskId() == taskId) {
            return;
        } else {
            Task.setTaskId(taskId);
        }

        this.doClickLnkListItem();
    }
    ,onClickLnkListItem : function(e) {
        var taskId = TaskList.Object.getHiddenTaskId(e);
        if (Task.getTaskId() == taskId) {
            return;
        } else {
            Task.setTaskId(taskId);
        }

        this.doClickLnkListItem();
    }
    ,doClickLnkListItem: function() {
        var taskId = Task.getTaskId();
        var t = TaskList.findTask(taskId);
        if (null != t) {
        	// get task details
        	TaskList.Service.retrieveDetail(taskId);
        }
    }
    ,onClickBtnComplete : function(e) {
        var taskId = Task.getTaskId();
        TaskList.Service.completeTask(taskId);
    }
    ,onClickBtnSignConfirm : function(e) {
        var taskId = Task.getTaskId();

        TaskList.Object.hideSignatureModal();
        TaskList.Service.signTask(taskId);
    }
    ,onClickBtnReject : function(e) {
        alert("onClickBtnReject");
        var taskId = Task.getTaskId();
    }

    ,onPostInit: function() {

        var treeInfo = TaskList.Object.getTreeInfo();
        if (0 < treeInfo.taskId) { //single complaint
            TaskList.setTaskId(treeInfo.taskId);
            TaskList.Service.retrieveDetail(treeInfo.taskId);
        } else {
            TaskList.Service.listTask(App.getUserName());
        }

        /*if (TaskList.isSingleObject()) {
            var taskId = Task.getTaskId();
            TaskList.Service.retrieveDetail(taskId);
        } else {
            TaskList.Service.listTask(App.getUserName());
        }*/

        Acm.keepTrying(TaskList.Event._tryInitAssignee, 8, 200);
        //Acm.keepTrying(TaskList.Event._tryInitTaskListType, 8, 200);
        Acm.keepTrying(TaskList.Event._tryInitPriority, 8, 200);

    }


    ,_tryInitAssignee: function() {
        var data = App.Object.getApprovers();
        if (Acm.isNotEmpty(data)) {
            TaskList.Object.initAssignee(data);
            return true;
        } else {
            return false;
        }
    }
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
        var taskId = Task.getTaskId();
        var t = TaskList.findTask(taskId);
        if (null != t) {
            // get task details
            TaskList.Service.retrieveDetail(taskId);
        }
        var task = Task.getTask();
        task.title = value;
        var data = this.getTaskData(task);
    	TaskList.Service.listTaskSaveDetail(data.taskId, data);
        TaskList.Object.refreshTaskTreeNode(task);
    }
    
    /**
     * Save owner value changed
     */
    ,onSaveOwner : function(value) {
        var t = this.getSelectedTask();
        t.owner = value;
        this.executeSaveTask(t);
    }
    
    /**
     * Save percentage completed value changed
     */
    ,onSavePerComplete : function(value) {
        var t = this.getSelectedTask();
        t.percentComplete = value;
        this.executeSaveTask(t);
    }

    /**
     * Save priority value changed
     */
    ,onSavePriority : function(value) {
        var t = this.getSelectedTask();
        t.priority = value;
        this.executeSaveTask(t);
    }

    /**
     * Save start date value changed
     */
    ,onSaveStartDate : function(value) {
        var t = this.getSelectedTask();
        t.taskStartDate = Acm.xDateToDatetime(value);
        this.executeSaveTask(t);
    }
    
    /**
     * Save start date value changed
     */
    ,onSaveDueDate : function(value) {
        var t = this.getSelectedTask();
        t.dueDate = Acm.xDateToDatetime(value);
        this.executeSaveTask(t);
    }

    /**
     * Save start date value changed
     */
    ,onSaveIncidentDate : function(value) {
        var t = this.getSelectedTask();
        t.dueDate = Acm.xDateToDatetime(value);
        this.executeSaveTask(t);
    }
    
    /**
     * Save start date value changed
     */
    ,onSaveStatus : function(value) {
        var t = this.getSelectedTask();
        t.status = value;
        this.executeSaveTask(t);
    }
    
    /**
     * Open the detail section editor
     */
    ,onClickBtnEditDetails: function(e) {
        TaskList.Object.editDivDetails();
    }

    /**
     * Cancel and close the detail section editor
     */
    ,onClickBtnCancelDetails: function(e) {
        TaskList.Object.cancelEditDivDetails();
    }

    /**
     * Save the detail section update to backend
     */
    ,onClickBtnSaveDetails : function(e) {
        var t = this.getSelectedTask();
        var value = TaskList.Object.saveDivDetails();
        t.details = value;
        this.executeSaveTask(t);    	
    }
    /////////////////////////////////////////////////////////////////////////////////
    // This section should move to the request object file
    /////////////////////////////////////////////////////////////////////////////////
    /**
     * Get the current selected taskid and find the task object
     */
    ,getSelectedTask : function () {
        var taskId = Task.getTaskId();
        return TaskList.findTask(taskId);
    }
    
    /**
     * Execute the save task object
     */
    ,executeSaveTask : function(task) {
        var data = this.getTaskData(task);
    	TaskList.Service.listTaskSaveDetail(data.taskId, data);
    }
    
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
    
};
