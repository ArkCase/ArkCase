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

    ,onPostInit: function() {
        var complaintId = Task.getTaskId();
        TaskDetail.Service.retrieveDetail(complaintId);
    }
};