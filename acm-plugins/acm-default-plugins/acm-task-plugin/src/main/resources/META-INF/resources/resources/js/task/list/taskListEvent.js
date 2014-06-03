/**
 * TaskList.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
TaskList.Event = {
    initialize : function() {
    }

    ,onClickLnkListItemImage : function(e) {
        var taskId = TaskList.Object.getHiddenTaskId(e);
        if (Task.getTaskId() == taskId) {
            return;
        } else {
            Task.setTaskId(taskId);
        }

        this.doClickLnkListItem();
    }
    ,onClickLnkListItem : function(e) {
        var taskId = TaskList.Object.getHiddenTaskId(e);
        if (Task.getTaskId() == taskId) {
            return;
        } else {
            Task.setTaskId(taskId);
        }

        this.doClickLnkListItem();
    }
    ,doClickLnkListItem: function() {
        var taskId = Task.getTaskId();
        var c = TaskList.findTask(taskId);
        if (null != c) {
            TaskList.Object.updateDetail(c);
            Task.setTaskId(taskId);
            TaskList.Object.hiliteSelectedItem(taskId);
        }
    }

    ,onPostInit: function() {
        TaskList.Service.listTask();
    }
};
