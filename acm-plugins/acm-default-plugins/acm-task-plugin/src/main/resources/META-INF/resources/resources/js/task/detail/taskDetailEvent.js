/**
 * TaskDetail.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
TaskDetail.Event = {
    initialize : function() {
    }

    ,onClickBtnComplete : function(e) {
        var taskId = Task.getTaskId();
        TaskDetail.Service.completeTask(taskId);
    }

    ,onPostInit: function() {
        var taskId = Task.getTaskId();
        TaskDetail.Service.retrieveDetail(taskId);
    }
};