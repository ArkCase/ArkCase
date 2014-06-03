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
    }

    ,EVENT_DETAIL_RETRIEVED		: "task-detail-retrieved"

    ,onDetailRetrieved : function(Callback, response) {
        var success = false;
        if (response) {
            var complaint = response[0];
            Task.setTask(complaint);
            TaskDetail.Object.updateDetail(complaint);

            success = true;
        }

        if (!success) {
            Acm.Dialog.showError("Failed to retrieve task detail");
        }
    }
};