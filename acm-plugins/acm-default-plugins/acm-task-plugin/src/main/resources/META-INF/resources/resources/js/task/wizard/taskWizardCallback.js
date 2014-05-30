/**
 * TaskWizard.Callback
 *
 * Callback handlers for server responses
 *
 * @author jwu
 */
TaskWizard.Callback = {
    initialize : function() {
        Acm.Dispatcher.addEventListener(this.EVENT_APPROVERS_RETRIEVED, this.onApproversRetrieved);
        Acm.Dispatcher.addEventListener(this.EVENT_CREATE_RETURNED, this.onCreateReturned);
    }

    ,EVENT_APPROVERS_RETRIEVED  : "task-wizard-get-approvers"
    ,EVENT_CREATE_RETURNED		: "task-wizard-create-returned"


    ,onApproversRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            TaskWizard.Object.initApprovers(response);
            success = true;
        }

        if (!success) {
            Acm.Dialog.error("Failed to retrieve approvers");
        }
    }
    ,onCreateReturned : function(Callback, response) {
        var success = false;
        if (response) {
            if (Acm.isNotEmpty(response.taskId)) {
                TaskWizard.Object.setTaskData(response);
                success = true;
            }
        }

        if (!success) {
            Acm.Dialog.error("Failed to create new task");
        }
    }
};
