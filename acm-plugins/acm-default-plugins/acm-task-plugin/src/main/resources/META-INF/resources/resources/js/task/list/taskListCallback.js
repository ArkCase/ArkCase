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
    }

    ,EVENT_LIST_RETURNED		: "task-list-list-returned"

    ,onListReturned : function(Callback, response) {
        var success = false;
        if (response) {
            //if (Acm.isNotEmpty(response.taskId)) {
            //TaskWizard.setTaskId(response.taskId);

            TaskList.setTaskList(response);

            TaskList.Page.buildTaskList(response);
            success = true;
            //}
        }

        if (!success) {
            Acm.Dialog.showError("Failed to retrieve task list");
        }
    }
};
