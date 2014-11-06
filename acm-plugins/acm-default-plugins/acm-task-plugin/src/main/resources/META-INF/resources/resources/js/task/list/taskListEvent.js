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
        alert("onClickBtnReject");
        var taskId = Task.getTaskId();
    }

    ,onPostInit: function() {

        var treeInfo = TaskList.Object.getTreeInfo();
        TaskList.setTaskId(treeInfo.taskId);

        if (0 < treeInfo.taskId) { //single task
            TaskList.setTaskId(treeInfo.taskId);
            TaskList.Service.retrieveDetail(treeInfo.taskId);
        } else {
            TaskList.Service.listTask(App.getUserName());
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
    
};
