/**
 * TaskList.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
TaskList.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_LIST_RETURNED, this.onListReturned);
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_COMPLETED, this.onTaskCompleted);
    }

    ,EVENT_LIST_RETURNED		: "task-list-returned"
    ,EVENT_DETAIL_RETRIEVED		: "task-list-detail-retrieved"
    ,EVENT_TASK_COMPLETED		: "task-list-task-completed"

    ,onListReturned : function(Callback, response) {
        var success = false;
        if (response) {
            TaskList.setTaskList(response);
            TaskList.Page.buildTaskList(response);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve task list");
        }
    }
    ,onDetailRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            if (Acm.isNotEmpty(response.taskId)) {
                var curId = Task.getTaskId();
                if (curId != response.taskId) {
                    return;         //user clicks another complaint before callback, do nothing
                }

                var task = response;
                Task.setTask(task);
                TaskList.Object.updateDetail(task);

                success = true;
            }
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve task detail");
        }
    }
    ,onTaskCompleted : function(Callback, response) {
        var success = false;
        if (response) {
            if (Acm.isNotEmpty(response.taskId)) {
                if (TaskList.isSingleObject()) {
                    Acm.goHome();
                } else {
                    //todo: remove from local copy, no need to call service
                    TaskList.Service.listTask(Acm.getUserName());
                }
                success = true;
            }
        }

        if (!success) {
            Acm.Dialog.error("Failed to complete task");
        }
    }
};
