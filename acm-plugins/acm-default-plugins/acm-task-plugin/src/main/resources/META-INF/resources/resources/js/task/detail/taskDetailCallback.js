/**
 * TaskDetail.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
TaskDetail.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_DETAIL_RETRIEVED, this.onDetailRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_TASK_COMPLETED, this.onTaskCompleted);
    }

    ,EVENT_DETAIL_RETRIEVED		: "task-detail-retrieved"
    ,EVENT_TASK_COMPLETED		: "task-detail-task-completed"

    ,onDetailRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            if (Acm.isNotEmpty(response.taskId)) {
                var task = response;
                Task.setTask(task);
                TaskDetail.Object.updateDetail(task);

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
                window.location.href = Acm.getContextPath() + "/plugin/dashboard";
                success = true;
            }
        }

        if (!success) {
            Acm.Dialog.error("Failed to complete task");
        }
    }
};