/**
 * TaskList.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskList.Object = {
    initialize : function() {
        var items = $(document).items();
        var taskId = items.properties("taskId").itemValue();
        if (Acm.isNotEmpty(taskId)) {
            Task.setTaskId(taskId);
            this.showAsideTasks(false);
            TaskList.setSingleObject(true);
        } else {
            TaskList.setSingleObject(false);
        }

        this.$ulTasks           = $("#ulTasks");
        this.$asideTasks        = this.$ulTasks.closest("aside");

        this.$btnComplete       = $("button[data-title='Complete']");
        this.$btnSignConfirm    = $("#signatureConfirmBtn");
        this.$btnReject         = $("button[data-title='Reject']");
        this.$btnComplete.click(function(e) {TaskList.Event.onClickBtnComplete(e);});
        this.$btnSignConfirm.click(function(e) {TaskList.Event.onClickBtnSignConfirm(e);});
        this.$btnReject.click(function(e) {TaskList.Event.onClickBtnReject(e);});

        this.$lnkTitle          = $("#caseTitle");
        this.$h4TitleHeader     = $("#caseTitle").parent();

        this.$perCompleted		= $("#percentageCompleted");
        this.$lnkStartDate      = $("#startDate");
        this.$lnkDueDate        = $("#dueDate");
        this.$lnkPriority       = $("#priority");
        this.$lnkOwner          = $("#taskOwner");
        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");
        this.$lnkStatus         = $("#status");
        this.$linkDetails		= $("#details");

        this.$lnkTitle.editable({placement: 'right'});
        this.$lnkDueDate.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,format: 'mm/dd/yyyy'
            ,viewformat: 'mm/dd/yyyy'
            ,datepicker: {
                weekStart: 1
            }
        });
        
        this.$listSignature     = $("#signatureList");
        
        // forms
        this.$formSignature     = $("#signatureConfirmForm");
        
        // modals
        this.$modalSignConfirm  = $("#signatureModal");

    }


    ,showAsideTasks: function(show) {
        Acm.Object.show(this.$asideTasks, show);
    }

    ,setSignatureList: function(val) {
        this.$listSignature.empty();
        this.$listSignature.append(val);
    }
    ,getSignatureForm: function() {
        return this.$formSignature;
    }
    ,getSignatureModal: function() {
        return this.$modalSignConfirm;
    }
    ,getHtmlUlTasks: function() {
        return Acm.Object.getHtml(this.$ulTasks);
    }
    ,setHtmlUlTasks: function(val) {
        return Acm.Object.setHtml(this.$ulTasks, val);
    }
    ,registerClickListItemEvents: function() {
        this.$ulTasks.find("a.text-ellipsis").click(function(e) {TaskList.Event.onClickLnkListItem(this);});
        this.$ulTasks.find("a.thumb-sm").click(function(e) {TaskList.Event.onClickLnkListItemImage(this);});
    }
    ,getHiddenTaskId: function(e) {
        var $hidden = $(e).siblings("input[type='hidden']");
        return $hidden.val();
    }
    ,setValueLnkTitle: function(txt) {
        this.$lnkTitle.editable("setValue", txt);
    }
    ,setTextTitleHeader: function(txt) {
        Acm.Object.setTextNodeText(this.$h4TitleHeader, txt, 1);
    }
    ,setValueLnkPerCompleted : function(txt) {
    	if ( txt ) {
        	this.$perCompleted.editable("setValue", txt + "%")    		
    	}
    	else {
        	this.$perCompleted.editable("setValue", "0%")    		    		
    	}
    }
    ,setValueLnkStartDate : function(date) {
    	if ( date ) {
            this.$lnkStartDate.editable("setValue", date, true);    	    		
    	}
    	else {
            this.$lnkStartDate.editable("setValue", "Unknown", true);    	    		    		
    	}
    }
    ,setValueLnkDueDate: function(date) {
    	if ( date ) {
            this.$lnkDueDate.editable("setValue", date, true);
    	}
    	else {
            this.$lnkDueDate.editable("setValue", "Unknown", true);
    	}
    }
    ,setValueLnkPriority: function(txt) {
        var priorityValue;
        if(txt == "Low"){
            priorityValue = 25;
        }
        else if(txt == "Medium"){
            priorityValue = 50;
        }
        else if (txt == "High"){
            priorityValue = 75;
        }
        else {
            priorityValue = 90;
        }
        this.$lnkPriority.editable("setValue", priorityValue);
    }
    ,setValueLnkAssigned: function(txt) {
        this.$lnkAssigned.editable("setValue", txt);
    }
    ,setValueLnkComplaintType: function(txt) {
        this.$lnkComplaintType.editable("setValue", txt);
    }
    ,initPriority: function(data){
        var choices = []; //[{value: "", text: "Choose Priority"}];
        $.each(data,function(idx,val){
            var opt= {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        this.$lnkPriority.editable({placement: 'bottom', value:"",emptytext: "Unknown",

        source: choices
        })
    }
    ,initAssignee: function(data) {
        var choices = []; //[{value: "", text: "Choose Assignee"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val.userId;
            opt.text = val.fullName;
            choices.push(opt);
        });

        this.$lnkAssigned.editable({placement: 'bottom', value: "",emptytext: "Unknown",
            source: choices
        });
    }
    ,initComplaintType: function(data) {
        var choices = []; //[{value: "", text: "Choose Type"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        this.$lnkComplaintType.editable({placement: 'bottom', value: "",emptytext: "Unknown",
            source: choices
        });
    }
    ,hiliteSelectedItem: function() {
        var cur = Task.getTaskId();
        this.$ulTasks.find("li").each(function(index) {
            var tid = $(this).find("input[type='hidden']").val();
            if (tid == cur) {
                $(this).addClass("active");
            } else {
                $(this).removeClass("active");
            }
        });
    }

    ,setValueAssignedStatus : function(status) {
    	if ( status ) {
            this.$lnkStatus.editable("setValue", "Assigned");    	    		
    	}
    	else {
            this.$lnkStatus.editable("setValue", "Unassigned");    	    		    		
    	}
    	
    }
    
    ,setValueTaskOwner : function(owner) {
    	if ( owner ) {
            this.$lnkOwner.editable("setValue", owner, false);    	    		
    	}
    	else {
            this.$lnkOwner.editable("setValue", "Unknown", false);    	    		    		
    	}
    	
    }
    ,setValueDetails : function(details) {
    	if ( details ) {
            this.$linkDetails.editable("setValue", details, false);    		
    	}
    	else {
    		Acm.Object.setHtml(this.$linkDetails, "");   		
    	}
    }
    ,updateDetail: function(t) {
        this.setValueLnkTitle(t.title);
        this.setValueLnkPerCompleted(t.percentComplete);
        this.setValueLnkStartDate(Acm.getDateFromDatetime(t.taskStartDate));
        this.setValueLnkDueDate(Acm.getDateFromDatetime(t.dueDate));
        this.setValueLnkPriority(t.priority);
        this.setValueLnkAssigned(t.assignee);
        this.setValueTaskOwner(t.owner);
        this.setValueAssignedStatus(t.assignee);
        this.setValueDetails(t.details);
    }

//============= Old Stuff ==========================
    ,setValueEdtTitle: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtTitle, val);
    }
    ,setValueEdtPriority: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtPriority, val);
    }
    ,setValueEdtDueDate: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtDueDate, val);
    }
    ,setValueEdtAssignee: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtAssignee, val);
    }
    ,setValueEdtTaskId: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtTaskId, val);
    }
    ,setValueEdtBusinessProcessName: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtBusinessProcessName, val);
    }
//    ,setValueEdtAttachedToObjectType: function(val) {
//        return Acm.Object.setPlaceHolderInput(this.$edtAttachedToObjectType, val);
//    }
//    ,setValueEdtAttachedToObjectId: function(val) {
//        return Acm.Object.setPlaceHolderInput(this.$edtAttachedToObjectId, val);
//    }
    ,setTextNodeScanAttachedToObjectType: function(val) {
        Acm.Object.setTextNodeText(this.scanAttachedToObjectType, val);
    }
    ,setTextNodeScanAttachedToObjectId: function(val) {
        Acm.Object.setTextNodeText(this.scanAttachedToObjectId, val);
    }
    ,setHrefLnkAttachedToObject: function(val) {
        this.lnkAttachedToObject.attr("href", val);
    }
//    ,setCheckedChkAdhocTask: function(val) {
//        Acm.Object.setChecked(this.$chkAdhocTask, val);
//    }
    ,setValueEdtAdhocTask: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtAdhocTask, val);
    }
    ,showDivExtra: function(show) {
        Acm.Object.show(this.$divExtra, show);
    }
//=======================================
};




