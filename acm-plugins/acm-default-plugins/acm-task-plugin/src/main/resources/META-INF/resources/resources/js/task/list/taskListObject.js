/**
 * TaskList.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskList.Object = {
    initialize : function() {
        this.$ulTasks     = $("#ulTasks");

        this.$btnComplete               = $("button[data-title='Complete']");
        this.$btnComplete.click(function(e) {TaskList.Event.onClickBtnComplete(e);});

        this.$lnkTitle    = $("#caseTitle");
        this.$h4TitleDate = $("#caseTitle").parent();
        this.$divDetails  = $(".taskDetails");

        this.$edtTaskId                 = $("#taskId");
        this.$edtTitle                  = $("#title");
        this.$edtPriority               = $("#priority");
        this.$edtDueDate                = $("#dueDate");
        this.$edtAssignee               = $("#assignee");
        //this.$chkAdhocTask              = $("#adhocTask");
        this.$edtAdhocTask              = $("#adhocTask");
        this.$edtBusinessProcessName    = $("#businessProcessName");
//        this.$edtAttachedToObjectType   = $("#attachedToObjectType");
//        this.$edtAttachedToObjectId     = $("#attachedToObjectId");
        this.$divExtra                  = $("#divExtra");

        this.lnkAttachedToObject        = this.$divExtra.find("a");
        this.scanAttachedToObjectType   = this.$divExtra.find("a > scan:first");
        this.scanAttachedToObjectId     = this.$divExtra.find("a > scan:last");
    }


//=======================================
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
    ,updateDetail: function(t) {
        this.setTextTitle(t.title);
        this.setTextTitleDate(" (" + Acm.getDateFromDatetime(t.dueDate) + ")");

        this.setValueEdtTitle(t.title);
        this.setValueEdtPriority(t.priority);
        this.setValueEdtDueDate(Acm.getDateFromDatetime(t.dueDate));
        this.setValueEdtAssignee(t.assignee);
        this.setValueEdtTaskId(t.taskId);

        //this.setCheckedChkAdhocTask(t.adhocTask);
        if (t.adhocTask) {
            this.setValueEdtAdhocTask("Yes");
            this.showDivExtra(false);
        } else {
            this.setValueEdtAdhocTask("No");
            this.showDivExtra(true);
            this.setValueEdtBusinessProcessName(t.businessProcessName);
//            this.setValueEdtAttachedToObjectType(t.attachedToObjectType);
//            this.setValueEdtAttachedToObjectId(t.attachedToObjectId);
            this.setTextNodeScanAttachedToObjectType(t.attachedToObjectType);
            this.setTextNodeScanAttachedToObjectId(t.attachedToObjectId);
            this.setHrefLnkAttachedToObject(Acm.getContextPath() + "/plugin/complaint/" + t.attachedToObjectId);
        }

    }
    ,getHtmlUlTasks: function() {
        return Acm.Object.getHtml(this.$ulTasks);
    }
    ,setHtmlUlTasks: function(val) {
        return Acm.Object.setHtml(this.$ulTasks, val);
    }
    ,registerClickListItemEvents: function() {
        this.$ulTasks.find("a.text-ellipsis").click(function(e) {TaskList.Event.onClickLnkListItem(this);});
    }
    ,getHiddenTaskId: function(e) {
        var $hidden = $(e).siblings("input[type='hidden']");
        return $hidden.val();
    }
    ,setTextTitle: function(txt) {
        Acm.Object.setText(this.$lnkTitle, txt);
    }
    ,setTextTitleDate: function(txt) {
        Acm.Object.setTextNodeText(this.$h4TitleDate, txt, 1);
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

};




