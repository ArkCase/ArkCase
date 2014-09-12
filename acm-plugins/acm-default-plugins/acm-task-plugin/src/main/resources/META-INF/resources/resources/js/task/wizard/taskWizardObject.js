/**
 * TaskWizard.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskWizard.Object = {
    initialize : function() {
        //access data from jsp page
    	var items = $(document).items();
        var parentType = items.properties("parentType").itemValue();
        var parentId = items.properties("parentId").itemValue();
        
        if (Acm.isNotEmpty(parentType) && Acm.isNotEmpty(parentId)) {
            var t = Task.getTask();
            t.attachedToObjectType = parentType;
            t.attachedToObjectId = parseInt(parentId);

        }
        
        //jsp object definitions
        this.$btnSave          = $("#saveBtn");
        this.$btnSave.click(function(e) {TaskWizard.Event.onClickBtnSave(e);});

        this.$selOwners        = $("#assignee");
        this.$edtComplaint     = $("#complaintId");
        this.$edtSubject       = $("#subject");
        this.$edtStartDate     = $("#startDate");
        this.$selStatus        = $("#statusSel");
        this.$edtDueDate       = $("#dueDate");
        this.$prioritySel      = $("#prioritySel");
        this.$completedStatus  = $("#completedStatus");
        this.$divDetail        = $("#taskDetail");
        
        this.$edtCase          = $("#case");
        this.$selTaskFlags     = $("#taskFlags");
        this.$selTaskFlags.chosen();

        this.$divDetail.summernote({
            height: 300
        });

    }

	,initOwners: function(data) {
	    $.each(data, function(idx, val) {
	        Acm.Object.appendSelect(TaskWizard.Object.$selOwners, val.userId, val.fullName);
	    });
	}

	/**
	 * Get the assignee field value
	 */
    ,getSelectValueSelOwners: function() {
        return Acm.Object.getSelectValue(this.$selOwners);
    }
    
	/**
	 * Get the complaint id or case id field value
	 */
    ,getValueEdtComplaint: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtComplaint);
    }

	/**
	 * Get the subject/title field value
	 */
    ,getValueEdtSubject: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtSubject);
    }

	/**
	 * Get the start date field value
	 */
    ,getValueEdtStartDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtStartDate);
    }
    
	/**
	 * Get the status select option field value
	 */
    ,getSelectedTextSelStatus: function() {
        return Acm.Object.getSelectTextIgnoreFirst(this.$selStatus);
    }

	/**
	 * Get the due date field value
	 */
    ,getValueEdtDueDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtDueDate);
    }
    
    /**
     * get the priority selected option text/label
     */
    ,getSelectedTextSelPriority: function() {
        return Acm.Object.getSelectTextIgnoreFirst(this.$prioritySel);
    }
    
	/**
	 * Get the completed percentage field value
	 */
    ,getValueEdtCompletedStatus: function() {
        return Acm.Object.getPlaceHolderInput(this.$completedStatus);
    }

    /**
     * get the priority selected option value
     */
    ,getSelectedValueSelPriority: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$prioritySel);
    }
    
/////////// older setter ang getter ///////////////////////////////////////////////
    
    ,getAttachedToObjectType: function() {
        var t = Task.getTask();
        return Acm.goodValue(t.attachedToObjectType);
    }
    
    ,getAttachedToObjectId: function() {
        var t = Task.getTask();
        return Acm.goodValue(t.attachedToObjectId);
    }
    
    ,getValueEdtCase: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtCase);
    }
    ,getSelectValueSelStatus: function() {
        return Acm.Object.getSelectValue(this.$selStatus);
    }
    ,getSelectValueSelTaskFlags: function() {
        return Acm.Object.getSelectValue(this.$selTaskFlags);
    }
    ,getHtmlDivDetail: function() {
        return AcmEx.Object.getSummerNote(this.$divDetail);
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
        data.assignee = this.getSelectValueSelOwners();
        data.attachedToObjectType = "COMPLAINT";
        data.attachedToObjectId = this.getValueEdtComplaint();
        data.title = this.getValueEdtSubject();
        data.taskStartDate = this.getValueEdtStartDate();
        data.status = this.getSelectedTextSelStatus();
        //data.priority = this.getValueEdtPriority();
        data.dueDate = this.getValueEdtDueDate();
        data.priority = this.getSelectedTextSelPriority();
        data.percentComplete = this.getValueEdtCompletedStatus();
        data.details = this.getHtmlDivDetail();
        data.adhocTask = true;

        data.taskId = t.taskId;
        data.businessProcessName = t.businessProcessName;
        data.completed = t.completed;
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




