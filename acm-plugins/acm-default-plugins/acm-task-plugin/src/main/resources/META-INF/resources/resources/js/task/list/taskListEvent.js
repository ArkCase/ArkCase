/**
 * TaskList.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
TaskList.Event = {
    initialize : function() {
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
            TaskList.Object.updateDetail(t);
            Task.setTaskId(taskId);
            TaskList.Object.hiliteSelectedItem(taskId);
            
            // check for signatures
            TaskList.Service.findSignatureByTypeById(taskId);
        }
    }
    ,onClickBtnComplete : function(e) {
        var taskId = Task.getTaskId();
        TaskList.Service.completeTask(taskId);
    }
    ,onClickBtnSignConfirm : function(e) {
        var taskId = Task.getTaskId();

        // hide the modal window that holds the form
        var sigModal = TaskList.Object.getSignatureModal();
        $(sigModal).modal('hide');

        TaskList.Service.signTask(taskId);
    }
    ,onClickBtnReject : function(e) {
        alert("onClickBtnReject");
        var taskId = Task.getTaskId();
    }

    ,onPostInit: function() {
        if (TaskList.isSingleObject()) {
            var taskId = Task.getTaskId();
            TaskList.Service.retrieveDetail(taskId);
        } else {
            TaskList.Service.listTask(App.getUserName());
        }

        Acm.keepTrying(TaskList.Event._tryInitAssignee, 8, 200);
        Acm.keepTrying(TaskList.Event._tryInitComplaintType, 8, 200);
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
        t.title = value;
        var data = this.getTaskData(t);
    	TaskList.Service.listTaskSaveDetail(data.taskId, data);
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
    ,onSaveStatus : function(value) {
        var t = this.getSelectedTask();
        t.status = value;
        this.executeSaveTask(t);
    }

    ,getSelectedTask : function () {
        var taskId = Task.getTaskId();
        return TaskList.findTask(taskId);
    }
    
    ,executeSaveTask : function(task) {
        var data = this.getTaskData(task);
    	TaskList.Service.listTaskSaveDetail(data.taskId, data);
    }
    
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
