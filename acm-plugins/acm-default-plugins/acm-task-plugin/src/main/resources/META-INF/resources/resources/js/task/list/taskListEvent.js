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
    ,onClickBtnReject : function(e) {
        alert("onClickBtnReject");
        var taskId = Task.getTaskId();
    }

    ,onPostInit: function() {
        if (TaskList.isSingleObject()) {
            var taskId = Task.getTaskId();
            TaskList.Service.retrieveDetail(taskId);
        } else {
            TaskList.Service.listTask(Acm.getUserName());
        }
    }
};
