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
        var t = TaskList.findTask(taskId);
        if (null != t) {
            TaskList.Object.updateDetail(t);
            Task.setTaskId(taskId);
            TaskList.Object.hiliteSelectedItem(taskId);
        }
    }

    ,onClickBtnComplete : function(e) {
        var taskId = Task.getTaskId();
        TaskList.Service.completeTask(taskId);
    }

    ,onPostInit: function() {
        var user = Acm.Object.getUserName()
        TaskList.Service.listTask(user);
    }
};
