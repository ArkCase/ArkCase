/**
 * TaskWizard.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskWizard.Object = {
    initialize : function() {
        var items = $(document).items();
        var parentType = items.properties("parentType").itemValue();
        var parentId = items.properties("parentId").itemValue();
        if (Acm.isNotEmpty(parentType) && Acm.isNotEmpty(parentId)) {
            var t = Task.getTask();
            t.attachedToObjectType = parentType;
            t.attachedToObjectId = parseInt(parentId);

        }
        this.$btnSave          = $("button[data-title='Save']");
        this.$btnSave.click(function(e) {TaskWizard.Event.onClickBtnSave(e);});

        this.$selOwners        = $("select[name='owner']");
        this.$edtSubject       = $("#subject");
        this.$edtCase          = $("#case");
        this.$edtComplaint     = $("#complaint");
        this.$selStatus        = $("select[name='status']");

        this.$edtPriority      = $("#priority");
        this.setValueEdtPriority(50);

        this.$edtDueDate       = $("#dueDate");
        Acm.Object.setValueDatePicker(this.$edtDueDate, Acm.getCurrentDay());

        this.$edtStartDate     = $("#startDate");
        Acm.Object.setValueDatePicker(this.$edtStartDate, Acm.getCurrentDay());

        this.$selTaskFlags     = $("#taskFlags");
        this.$selTaskFlags.chosen();

        this.$divDetail        = $(".detail");
        this.$divDetail.summernote({
            height: 300
        });

//old stuff
//        this.$edtTitle         = $("#title");
//        this.$edtPriority      = $("#priority");
//        this.$edtDueDate       = $("#dueDate");
//        this.$selAssignees     = $("#assignees");
    }

    ,getAttachedToObjectType: function() {
        var t = Task.getTask();
        return Acm.goodValue(t.attachedToObjectType);
    }
    ,getAttachedToObjectId: function() {
        var t = Task.getTask();
        return Acm.goodValue(t.attachedToObjectId);
    }
    ,initOwners: function(data) {
        $.each(data, function(idx, val) {
            Acm.Object.appendSelect(TaskWizard.Object.$selOwners, val.userId, val.fullName);
        });
    }
    ,getSelectValueSelOwners: function() {
        return Acm.Object.getSelectValue(this.$selOwners);
    }
    ,getValueEdtDueDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtDueDate);
    }
    ,getValueEdtSubject: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtSubject);
    }
    ,getValueEdtPriority: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtPriority);
    }
    ,setValueEdtPriority: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtPriority, val);
    }
    ,getValueEdtStartDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtStartDate);
    }
    ,getValueEdtCase: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtCase);
    }
    ,getValueEdtComplaint: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtComplaint);
    }
    ,getSelectValueSelStatus: function() {
        return Acm.Object.getSelectValue(this.$selStatus);
    }
    ,getSelectValueSelTaskFlags: function() {
        return Acm.Object.getSelectValue(this.$selTaskFlags);
    }
    ,getHtmlDivDetail: function() {
        return Acm.Object.getSummernote(this.$divDetail);
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
        data.title = this.getValueEdtSubject();
        data.priority = this.getValueEdtPriority();
        data.dueDate = this.getValueEdtDueDate();
        data.assignee = this.getSelectValueSelOwners();
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


//old stuff
//    ,initAssignees: function(data) {
//        $.each(data, function(idx, val) {
//            TaskWizard.Object.appendAssignees(val.userId, val.fullName);
//        });
//        this.$selAssignees.chosen();
//    }
//    ,appendAssignees: function(key, val) {
//        this.$selAssignees.append($("<option></option>")
//            .attr("value",key)
//            .text(val));
//    }
//    ,setEnableBtnSave: function(enable) {
//        Acm.Object.setEnable(this.$btnSave, enable);
//    }
//    ,getValueEdtTitle: function() {
//        return Acm.Object.getPlaceHolderInput(this.$edtTitle);
//    }
//    ,getSelectValueSelAssignee: function() {
//        return Acm.Object.getSelectValue(this.$selAssignees);
//    }
//
//
//    ,setTaskData : function(data) {
//        var t = Task.getTask();
//        t.title = data.title;
//        t.priority = data.priority;
//        t.dueDate = data.dueDate;
//        t.assignee = data.assignee;
//
//        t.taskId = data.taskId;
//        t.attachedToObjectType = data.attachedToObjectType;
//        t.attachedToObjectId = data.attachedToObjectId;
//        t.businessProcessName = data.businessProcessName;
//        t.adhocTask = data.adhocTask;
//        t.completed = data.completed;
//        t.taskStartDate = data.taskStartDate;
//        t.taskFinishedDate = data.taskFinishedDate;
//        t.taskDurationInMillis = data.taskDurationInMillis;
//    }
//    ,getTaskData : function() {
//        var data = {};
//        var t = Task.getTask();
//        data.title = this.getValueEdtTitle();
//        data.priority = this.getValueEdtPriority();
//        data.dueDate = this.getValueEdtDueDate();
//        data.assignee = this.getSelectValueSelAssignee();
//        data.adhocTask = true;
//
//        data.taskId = t.taskId;
//        data.attachedToObjectType = t.attachedToObjectType;
//        data.attachedToObjectId = t.attachedToObjectId;
//        data.businessProcessName = t.businessProcessName;
//        data.completed = t.completed;
//        data.taskStartDate = t.taskStartDate;
//        data.taskFinishedDate = t.taskFinishedDate;
//        data.taskDurationInMillis = t.taskDurationInMillis;
//        return data;
//    }
};




