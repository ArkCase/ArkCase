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
        this.$btnReject         = $("button[data-title='Reject']");
        this.$btnComplete.click(function(e) {TaskList.Event.onClickBtnComplete(e);});
        this.$btnReject.click(function(e) {TaskList.Event.onClickBtnReject(e);});

        this.$lnkTitle          = $("#caseTitle");
        this.$h4TitleHeader     = $("#caseTitle").parent();

        this.$lnkDueDate        = $("#incident");
        this.$lnkPriority       = $("#priority");
        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");
        this.$lnkStatus         = $("#status");


        this.$lnkTitle.editable({placement: 'right'});
        this.$lnkDueDate.editable({placement: 'bottom'
            ,format: 'yyyy-mm-dd'
            ,viewformat: 'yyyy/mm/dd'
            ,datepicker: {
                weekStart: 1
            }
        });


        /*this.$lnkPriority.editable({placement: 'bottom'
            ,value: 50
        });*/



//old stuff
//        this.$divDetails                = $(".taskDetails");
//
//        this.$edtTaskId                 = $("#taskId");
//        this.$edtTitle                  = $("#title");
//        this.$edtPriority               = $("#priority");
//        this.$edtDueDate                = $("#dueDate");
//        this.$edtAssignee               = $("#assignee");
//        //this.$chkAdhocTask              = $("#adhocTask");
//        this.$edtAdhocTask              = $("#adhocTask");
//        this.$edtBusinessProcessName    = $("#businessProcessName");
////        this.$edtAttachedToObjectType   = $("#attachedToObjectType");
////        this.$edtAttachedToObjectId     = $("#attachedToObjectId");
//        this.$divExtra                  = $("#divExtra");
//
//        this.lnkAttachedToObject        = this.$divExtra.find("a");
//        this.scanAttachedToObjectType   = this.$divExtra.find("a > scan:first");
//        this.scanAttachedToObjectId     = this.$divExtra.find("a > scan:last");
    }


    ,showAsideTasks: function(show) {
        Acm.Object.show(this.$asideTasks, show);
    }
    ,updateDetail: function(t) {
        this.setValueLnkTitle(t.title);
        this.setTextTitleHeader(" (" + Acm.getDateFromDatetime(t.dueDate) + ")");

        this.setValueLnkDueDate(Acm.getDateFromDatetime(t.dueDate));
        this.setValueLnkPriority(t.priority);
        this.setValueLnkAssigned(t.assignee);
        //this.setValueLnkComplaintType(c.complaintType);
        //this.setTextLnkStatus(c.status);

//old stuff
//        this.setValueEdtTitle(t.title);
//        this.setValueEdtPriority(t.priority);
//        this.setValueEdtDueDate(Acm.getDateFromDatetime(t.dueDate));
//        this.setValueEdtAssignee(t.assignee);
//        this.setValueEdtTaskId(t.taskId);
//
//        //this.setCheckedChkAdhocTask(t.adhocTask);
//        if (t.adhocTask) {
//            this.setValueEdtAdhocTask("Yes");
//            this.showDivExtra(false);
//        } else {
//            this.setValueEdtAdhocTask("No");
//            this.showDivExtra(true);
//            this.setValueEdtBusinessProcessName(t.businessProcessName);
////            this.setValueEdtAttachedToObjectType(t.attachedToObjectType);
////            this.setValueEdtAttachedToObjectId(t.attachedToObjectId);
//            this.setTextNodeScanAttachedToObjectType(t.attachedToObjectType);
//            this.setTextNodeScanAttachedToObjectId(t.attachedToObjectId);
//            this.setHrefLnkAttachedToObject(App.getContextPath() + "/plugin/complaint/" + t.attachedToObjectId);
//        }

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
    ,setValueLnkDueDate: function(txt) {
        Acm.Object.setText(this.$lnkDueDate, txt);
        //this.$lnkDueDate.editable("setValue", txt);
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
    ,setTextLnkStatus: function(txt) {
        Acm.Object.setText(this.$lnkStatus, txt);
    }


    ,initPriority: function(data){
        var choices = []; //[{value: "", text: "Choose Priority"}];
        $.each(data,function(idx,val){
            var opt= {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        this.$lnkPriority.editable({placement: 'bottom', value:"",
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

        this.$lnkAssigned.editable({placement: 'bottom', value: "",
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

        this.$lnkComplaintType.editable({placement: 'bottom', value: "",
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




