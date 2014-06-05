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
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_COMPLETED, this.onTaskCompleted);
    }

    ,EVENT_LIST_RETURNED		: "task-list-returned"
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
    ,onTaskCompleted : function(Callback, response) {
        var success = false;
        if (response) {
            if (Acm.isNotEmpty(response.taskId)) {

                //todo: remove from local copy, no need to call service
                TaskList.Service.listTask(Acm.Object.getUserName());

                success = true;
            }
        }

        if (!success) {
            Acm.Dialog.error("Failed to complete task");
        }
    }
};
