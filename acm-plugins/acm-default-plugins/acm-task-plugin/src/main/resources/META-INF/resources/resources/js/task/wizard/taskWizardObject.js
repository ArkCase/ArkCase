/**
 * TaskWizard.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskWizard.Object = {
    create : function() {
        //access data from jsp page
    	var items = $(document).items();
        var parentType = items.properties("parentType").itemValue();
        var reference = items.properties("reference").itemValue();
        if (Acm.isNotEmpty(parentType) && Acm.isNotEmpty(reference) ) {
            var t = TaskOld.getTask();
            t.attachedToObjectType = parentType;
            t.attachedToObjectId = reference;
        }
        
        //jsp object definitions
        this.$btnSave          = $("#saveBtn");
        this.$btnSave.click(function(e) {TaskWizard.Event.onClickBtnSave(e);});

        this.$selOwners        = $("#assignee");
        this.$edtComplaint     = $("#complaintId");
        this.setValueEdtComplaint(Acm.goodValue(reference));
        this.useTypeAhead(this.$edtComplaint);

        this.$edtSubject       = $("#subject");

        this.$edtStartDate     = $("#startDate");;
        this.setValueEdtStartDate(Acm.getCurrentDay());

        this.$selStatus        = $("#statusSel");

        this.$edtDueDate       = $("#dueDate");
        this.setValueEdtDueDate(Acm.getCurrentDay());

        this.$prioritySel      = $("#prioritySel");
        this.$completedStatus  = $("#completedStatus");
        this.$divDetail        = $("#taskDetail");
        
        this.$edtCase          = $("#case");
        this.$selTaskFlags     = $("#taskFlags");
        this.$selTaskFlags.chosen();


//        this.$divDetail.summernote({
//            height: 300
//        });

    }

	,initOwners: function(data) {
	    $.each(data, function(idx, val) {
            Acm.Object.appendSelect(TaskWizard.Object.$selOwners, val.object_id_s, val.name);
	    });
	}
    ,sortAssignees: function (previous,next) {
        if (previous.name < next.name)
            return -1;
        if (previous.name > next.name)
            return 1;
        return 0;
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
        //return Acm.Object.getPlaceHolderInput(this.$edtComplaint);
        return Acm.Object.getValue(this.$edtComplaint);

    }

    /**
     * set the complaint id or case id field value
     */
    ,setValueEdtComplaint: function(val) {
        return Acm.Object.setValue(this.$edtComplaint, val);
    }

	/**
	 * Get the subject/title field value
	 */
    ,getValueEdtSubject: function() {
        //return Acm.Object.getPlaceHolderInput(this.$edtSubject);
        return Acm.Object.getValue(this.$edtSubject);

    }

	/**
	 * Get the start date field value
	 */
    ,getValueEdtStartDate: function() {
        //this is in mm/dd/yyyy format
        var startDate = Acm.Object.getPlaceHolderInput(this.$edtStartDate);
        //convert to yyyy-mm-dd format
        var startDateArr = startDate.split("/");
        var month = startDateArr[0];
        var day = startDateArr[1];
        var year = startDateArr[2];
        var newStartDate = year + "-" + month + "-" + day;
        //var newStartDate = startDate.split("/").reverse().join("-");
        return newStartDate;
        //return Acm.Object.getPlaceHolderInput(this.$edtStartDate);
    }

    /**
     * set the start date field value
     */
    ,setValueEdtStartDate: function(val) {
        return Acm.Object.setValueDatePicker(this.$edtStartDate, val);
    }

    /**
     * Get the due date field value
     */
    ,getValueEdtDueDate: function() {
        //this is in mm/dd/yyyy format
        var dueDate = Acm.Object.getPlaceHolderInput(this.$edtDueDate);
        //convert to yyyy-mm-dd format
        var dueDateArr = dueDate.split("/");
        var month = dueDateArr[0];
        var day = dueDateArr[1];
        var year = dueDateArr[2];
        var newDueDate = year + "-" + month + "-" + day;
        //var newDueDate = dueDate.split("/").reverse().join("-");
        return newDueDate;
        //return Acm.Object.getPlaceHolderInput(this.$edtDueDate);
    }

    /**
     * set the start date field value
     */
    ,setValueEdtDueDate: function(val) {
        return Acm.Object.setValueDatePicker(this.$edtDueDate, val);
    }
	/**
	 * Get the status select option field value
	 */
    ,getSelectedTextSelStatus: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selStatus);
    }
    
    /**
     * get the priority selected option text/label
     */
    ,getSelectedTextSelPriority: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$prioritySel);
    }
    
	/**
	 * Get the completed percentage field value
	 */
    ,getValueEdtCompletedStatus: function() {
        return Acm.Object.getPlaceHolderInput(this.$completedStatus);
    }



///////////////////////////////////////
    ,_ctrObjs: {}
    ,_ctrTitles: []

    ,_onSuccessSuggestion: function(query, process, data) {
        TaskWizard.Object._ctrObjs = {};
        TaskWizard.Object._ctrTitles = [];

        _.each( data, function(item, ix, list){
            TaskWizard.Object._ctrTitles.push( item.name );
            TaskWizard.Object._ctrObjs[ item.name ] = item;
        });

        process( TaskWizard.Object._ctrTitles );
    }
    ,_getTypeAheadUrl: function(query) {
        var url = App.getContextPath() + TaskWizard.Service.API_TYPEAHEAD_SUGGESTION_BEGIN_
            + query
            + TaskWizard.Service.API_TYPEAHEAD_SUGGESTION_END;
        return url;
    }
    ,_validateSuggestionData: function(data) {
        if (Acm.isEmpty(data.responseHeader) || Acm.isEmpty(data.response)) {
            return false;
        }
        if (Acm.isEmpty(data.response.numFound) || Acm.isEmpty(data.response.start) || Acm.isEmpty(data.response.docs)) {
            return false;
        }
        return true;
    }
    ,_throttledRequest: function(query, process){
        $.ajax({
            url: TaskWizard.Object._getTypeAheadUrl(query)
            ,cache: false
            ,success: function(data){
                if (TaskWizard.Object._validateSuggestionData(data)) {
                    var docs = data.response.docs;
                    TaskWizard.Object._onSuccessSuggestion(query, process, docs);
                }
            }
        });
    }

    ,useTypeAhead: function($s) {
        $s.typeahead({
            source: function ( query, process ) {
                _.debounce(TaskWizard.Object._throttledRequest( query, process ), 300);
            }
            ,highlighter: function( item ){
                html = '<div class="ctr">';
                var ctr = TaskWizard.Object._ctrObjs[ item ];
                if (ctr) {
                    var icon = "";
                    var type = Acm.goodValue(ctr.object_type_s, "UNKNOWN");
                    if (type == "COMPLAINT") {
                        icon = '<i class="i i-notice i-2x"></i>';
                    } else if (type == "CASE") {
                        icon = '<i class="i i-folder i-2x"></i>';
                    } else if (type == "TASK") {
                        icon = '<i class="i i-checkmark i-2x"></i>';
                    } else if (type == "DOCUMENT") {
                        icon = '<i class="i i-file i-2x"></i>';
                    } else {
                        icon = '<i class="i i-circle i-2x"></i>';
                    }

                    html += '<div class="icontype">' + icon + '</div>'
                        + '<div class="title">' + Acm.goodValue(ctr.title_parseable) + '</div>'
                        + '<div class="identifier">' + Acm.goodValue(ctr.name) + ' ('+ Acm.goodValue(ctr.object_type_s) + ')' + '</div>'
                        + '<div class="author">By ' + ctr.author_s  + ' on '+ Acm.getDateTimeFromDatetime(ctr.last_modified_tdt) + '</div>'
                    html += '</div>';
                }
                return html;
            }
            , updater: function ( selectedName ) {
                $( "#refCtrId" ).val( TaskWizard.Object._ctrObjs[ selectedName ].object_id_s );
                return selectedName;
            }
            ,hint: true
            ,highlight: true
            ,minLength: 1

        });

    }

    
/////////// older setter ang getter ///////////////////////////////////////////////
    
    ,getAttachedToObjectType: function() {
        var t = TaskOld.getTask();
        return Acm.goodValue(t.attachedToObjectType);
    }
    
    ,getAttachedToObjectId: function() {
        var t = TaskOld.getTask();
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
        var t = TaskOld.getTask();
        t.title = data.title;
        t.priority = data.priority;
        t.dueDate = data.dueDate;
        t.assignee = data.assignee;
        t.taskId = data.taskId;
        t.attachedToObjectType = data.attachedToObjectType;
        t.attachedToObjectId = data.attachedToObjectId;
        t.attachedToObjectName = data.attachedToObjectName;
        t.businessProcessName = data.businessProcessName;
        t.adhocTask = data.adhocTask;
        t.completed = data.completed;
        t.taskStartDate = data.taskStartDate;
        t.taskFinishedDate = data.taskFinishedDate;
        t.taskDurationInMillis = data.taskDurationInMillis;
    }
    
    ,getTaskData : function() {
        var data = {};
        var t = TaskOld.getTask();
        data.assignee = this.getSelectValueSelOwners();
        if(data.assignee == "null"){data.assignee = App.getUserName();}
        data.attachedToObjectType = t.attachedToObjectType;
        data.attachedToObjectName = this.getValueEdtComplaint();
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
//        var t = TaskOld.getTask();
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
//        var t = TaskOld.getTask();
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




