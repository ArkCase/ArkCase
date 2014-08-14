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
            TaskList.Service.listTask(App.getUserName());
        }

        Acm.keepTrying(TaskList.Event._tryInitAssignee, 8, 200);
        Acm.keepTrying(TaskList.Event._tryInitComplaintType, 8, 200);
        Acm.keepTrying(TaskList.Event._tryInitPriority, 8, 200);

    }


    ,_tryInitAssignee: function() {
        var data = App.Object.getApprovers();
        if (Acm.isNotEmpty(data)) {
            TaskList.Object.initAssignee(data);
            return true;
        } else {
            return false;
        }
    }
    ,_tryInitComplaintType: function() {
        var data = App.Object.getComplaintTypes();
        if (Acm.isNotEmpty(data)) {
            TaskList.Object.initComplaintType(data);
            return true;
        } else {
            return false;
        }
    }
    ,_tryInitPriority: function() {
        var data = App.Object.getPriorities();
        if (Acm.isNotEmpty(data)) {
            TaskList.Object.initPriority(data);
            return true;
        } else {
            return false;
        }
    }
};
