/**
 * TaskWizard.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskWizard.Object = {
    initialize : function() {
        this.$btnSave          = $("button[data-title='Save']");
        this.$btnSave.click(function(e) {TaskWizard.Event.onClickBtnSave(e);});

        this.$edtTitle         = $("#title");
        this.$edtPriority      = $("#priority");
        this.$edtDueDate       = $("#dueDate");
        this.$selAssignees     = $("#assignees");
    }

    ,initAssignees: function(data) {
        $.each(data, function(idx, val) {
            TaskWizard.Object.appendAssignees(val.userId, val.fullName);
        });
        this.$selAssignees.chosen();
    }
    ,appendAssignees: function(key, val) {
        this.$selAssignees.append($("<option></option>")
                .attr("value",key)
                .text(val));
    }
    ,setEnableBtnSave: function(enable) {
        Acm.Object.setEnable(this.$btnSave, enable);
    }

    ,getValueEdtTitle: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtTitle);
    }
    ,getValueEdtPriority: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtPriority);
    }
    ,getValueEdtDueDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtDueDate);
    }
    ,setValueEdtDueDate: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtDueDate, val);
    }
    ,getSelectValueSelAssignee: function() {
        return Acm.Object.getSelectValue(this.$selAssignees);
    }


    ,setTaskData : function(data) {
        var t = Task.getTask();
        t.title = data.title;
        t.priority = data.priority;
        t.dueDate = data.dueDate;
        t.assignee = data.assignee;

        t.taskId = data.taskId;
        t.attachedToObjectType = data.attachedToObjectType;
        t.attachedToObjectId = data.attachedToObjectId;
        t.businessProcessName = data.businessProcessName;
        t.adhocTask = data.adhocTask;
        t.completed = data.completed;
        t.taskStartDate = data.taskStartDate;
        t.taskFinishedDate = data.taskFinishedDate;
        t.taskDurationInMillis = data.taskDurationInMillis;

    }
    ,getTaskData : function() {
        var data = {};
        var t = Task.getTask();
        data.title = this.getValueEdtTitle();
        data.priority = this.getValueEdtPriority();
        data.dueDate = this.getValueEdtDueDate();
        data.assignee = this.getSelectValueSelAssignee();
        data.adhocTask = true;

        data.taskId = t.taskId;
        data.attachedToObjectType = t.attachedToObjectType;
        data.attachedToObjectId = t.attachedToObjectId;
        data.businessProcessName = t.businessProcessName;
        data.completed = t.completed;
        data.taskStartDate = t.taskStartDate;
        data.taskFinishedDate = t.taskFinishedDate;
        data.taskDurationInMillis = t.taskDurationInMillis;
        return data;
    }


};




