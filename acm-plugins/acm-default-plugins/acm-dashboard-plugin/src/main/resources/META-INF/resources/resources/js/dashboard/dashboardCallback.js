/**
 * Dashboard.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
Dashboard.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_MY_TASKS_RETRIEVED, this.onMyTasksRetrieved);
    }

    ,EVENT_MY_TASKS_RETRIEVED		: "dashboard-my-tasks-retrieved"

    ,onMyTasksRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            Dashboard.Page.fillMyTasks(response);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve my tasks");
        }
    }
};


