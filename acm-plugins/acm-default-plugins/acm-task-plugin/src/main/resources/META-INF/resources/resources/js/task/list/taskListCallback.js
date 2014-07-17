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
    }

    ,EVENT_LIST_RETRIEVED		: "task-list-retrieved"
    ,EVENT_DETAIL_RETRIEVED		: "task-list-detail-retrieved"
    ,EVENT_TASK_COMPLETED		: "task-list-task-completed"

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
            }
        }
    }
    ,onTaskCompleted : function(Callback, response) {
        if (response.hasError) {
            Acm.Dialog.error("Failed to complete task:"  +response.errorMsg);
        } else {
            if (Acm.isNotEmpty(response.taskId)) {
                if (TaskList.isSingleObject()) {
                    Acm.gotoPage(TaskList.Page.URL_DASHBOARD);
                } else {
                    //todo: remove item from local copy list, no need to call service to retrieve list
                    TaskList.Service.listTask(Acm.getUserName());
                }
            }
        }
    }
};
