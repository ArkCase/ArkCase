/**
 * TaskList.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
TaskList.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_RETRIEVED, this.onListRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_COMPLETED, this.onTaskCompleted);
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_SIGNED, this.onTaskSigned);
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_BYTYPEBYID_RETRIEVED, this.onFindByTypeByIdRetrieved);
    }

    ,EVENT_LIST_RETRIEVED		: "task-list-retrieved"
    ,EVENT_DETAIL_RETRIEVED		: "task-list-detail-retrieved"
    ,EVENT_TASK_COMPLETED		: "task-list-task-completed"
    ,EVENT_TASK_SIGNED			: "task-list-task-signed"
    ,EVENT_LIST_BYTYPEBYID_RETRIEVED : "task-list-signature-byTypeById-retrieved"
    		
    ,onListRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve task list:"  +response.errorMsg);
        } else {
            TaskList.setTaskList(response);
            TaskList.Page.buildTaskList(response);
        }
    }
    ,onDetailRetrieved : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to retrieve task detail:"  +response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.taskId)) {
                var curId = Task.getTaskId();
                if (curId != response.taskId) {
                    return;         //user clicks another task before callback, do nothing
                }

                var task = response;
                Task.setTask(task);
                TaskList.Object.updateDetail(task);
                
                // TODO:  check for signatures for task, this function is not called right now so won't implement here
            }
        }
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
            Acm.Dialog.error("Failed to electronically sign task:"  +response.errorMsg);
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
            Acm.Dialog.error("Failed to retrieve signature list by type and by id:" + response.errorMsg);
        } else {
        	TaskList.Page.buildSignatureList(response);
        }
    }
};
