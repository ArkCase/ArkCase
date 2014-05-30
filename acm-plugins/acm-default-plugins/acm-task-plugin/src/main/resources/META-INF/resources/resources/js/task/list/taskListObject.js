/**
 * TaskList.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskList.Object = {
    initialize : function() {
        this.$ulTasks = $("#ulTasks");
        this.$lnkTitle = $("#caseTitle");
        this.$h4TitleDate = $("#caseTitle").parent();
        this.$divDetails = $(".taskDetails");

    }

    ,get$ulTasks : function() {
        return this.$ulTasks;
    }

    ,getHtmlUlTasks: function() {
        return Acm.Object.getHtml(this.$ulTasks);
    }
    ,setHtmlUlTasks: function(val) {
        return Acm.Object.setHtml(this.$ulTasks, val);
    }
    ,registerClickListItemEvents: function() {
        this.$ulTasks.find("a.thumb-sm").click(function(e) {TaskList.Event.onClickLnkListItemImage(this);});
        this.$ulTasks.find("a.text-ellipsis").click(function(e) {TaskList.Event.onClickLnkListItem(this);});
    }
    ,getHiddenTaskId: function(e) {
        var $hidden = $(e).siblings("input[type='hidden']");
        return $hidden.val();
    }
    ,updateDetail: function(c) {
        this.setTextTitle(c.complaintTitle);
        this.setTextTitleDate(" (" + TaskList.getDateFromDatetime(c.created) + ")");
        this.setHtmlDetails(c.details);


//        var $c = $("<h4>beg<a>mid</a>end</h4>");
//        var c1 = Acm.Object.getTextNodeText($c);
//        var c2 = Acm.Object.getTextNodeText($c, 0);
//        var c3 = Acm.Object.getTextNodeText($c, 1);
//        Acm.Object.setTextNodeText($c, "last", -1);
//        var c4 = Acm.Object.getTextNodeText($c);
    }
    ,setTextTitle: function(txt) {
        Acm.Object.setText(this.$lnkTitle, txt);
    }
    ,setTextTitleDate: function(txt) {
        Acm.Object.setTextNodeText(this.$h4TitleDate, txt, 1);
    }
    ,setHtmlDetails: function(html) {
        Acm.Object.setHtml(this.$divDetails, html);
    }
    ,hiliteSelectedItem: function() {
        var cur = Task.getTaskId();
        this.$ulTasks.find("li").each(function(index) {
            var cid = $(this).find("input[type='hidden']").val();
            if (cid == cur) {
                $(this).addClass("active");
            } else {
                $(this).removeClass("active");
            }
        });
    }







    ,setEnableBtnPrint: function(enable) {
        Acm.Object.setEnable(this.$btnPrint, enable);
    }
    ,setEnableBtnAssign: function(enable) {
        Acm.Object.setEnable(this.$btnAssign, enable);
    }
    ,setEnableBtnDelete: function(enable) {
        Acm.Object.setEnable(this.$btnDelete, enable);
    }
    ,showBtnPrint: function(show) {
        Acm.Object.show(this.$btnPrint, show);
    }
    ,setEnableBtnFingerPrint: function(enable) {
        Acm.Object.setEnable(this.$btnFingerPrint, enable);
    }
    ,showBtnFingerPrint: function(show) {
        Acm.Object.show(this.$btnFingerPrint, show);
    }
    ,showBtnAssign: function(show) {
        Acm.Object.show(this.$btnAssign, show);
    }
    ,showBtnDelete: function(show) {
        Acm.Object.show(this.$btnDelete, show);
    }
    ,showLnkNextItems: function() {
        alert("showLnkNextItem depleted");
//        var checked = TaskList.Object.isCheckedChkQa();
//        TaskList.Object.showLnkNextItemQa(checked);
//        checked = TaskList.Object.isCheckedChkMailback();
//        TaskList.Object.showLnkNextItemMb(checked);
    }
    ,showLnkNextItemQa: function(show) {
        Acm.Object.showParent(this.$lnkNextItemQa, show);
    }
    ,showLnkNextItemMb: function(show) {
        Acm.Object.showParent(this.$lnkNextItemMb, show);
    }
	,putUnassignedDoc: function(unassignedTaskId, unassignedDoc) {
        var selected = false;
        var prevUnassignedDoc = TaskList.Object.getUnassignedDoc(unassignedTaskId);
        if (prevUnassignedDoc) {
            selected = prevUnassignedDoc.selected;
        }
        unassignedDoc.selected = selected;
		window.unassignedDocs[unassignedTaskId] = unassignedDoc;
	}
	,getUnassignedDoc: function(unassignedTaskId) {
		return window.unassignedDocs[unassignedTaskId];
	}
	,getUnassignedDocs: function() {
		return window.unassignedDocs;
	}
    ,resetUnassignedDocs: function() {
        window.unassignedDocs = {};
    }
    ,getSelectedDocs : function(e) {
        var selectedDocs = {};
        var unassignedDocs = TaskList.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = TaskList.Object.getUnassignedDoc(unassignedTaskId);
            if (unassignedDoc) {
                if (unassignedDoc.selected) {
                    selectedDocs[unassignedTaskId] = unassignedDoc;
                }
            }
        }
        return selectedDocs;
    }
    ,getSelectedTaskIds : function(e) {
        var taskIds = [];
        var unassignedDocs = TaskList.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = TaskList.Object.getUnassignedDoc(unassignedTaskId);
            if (unassignedDoc) {
                if (unassignedDoc.selected) {
                    taskIds.push(unassignedTaskId);
                }
            }
        }
        return taskIds;
    }
	,setCheckChkSelectOne : function(checked) {
		jQuery("input[type=checkbox]" + TaskList.Object.clsChkSelectOne).each(function(i, item) {
			item.checked = checked;
            TaskList.Object.selectUnassignedDoc(item);
		});
	}
	,regClickChkSelectOne: function() {
		jQuery("input[type=checkbox]" + TaskList.Object.clsChkSelectOne).click(function() {
            TaskList.Callbacks.onClickChkSelectOne(this);
        });
	}
	,getTaskIdClicked: function(checkBox) {
		return jQuery(checkBox).next().val();
	}
    ,selectUnassignedDoc : function(checkBox) {
        var unassignedTaskId = TaskList.Object.getTaskIdClicked(checkBox);
        var unassignedDoc  = TaskList.Object.getUnassignedDoc(unassignedTaskId);
        if (unassignedDoc) {
            unassignedDoc.selected = checkBox.checked;
        }
    }
	,getContextPath: function() {
        return TaskList.contextPath;
	}
    ,getTicket: function() {
        return TaskList.ticket;
    }
    ,showLabAssigneeProgress: function(show) {
    	Acm.Object.show(this.$labAssigneeProgress, show);
	}
    ,setTextLabTotalCount: function(value) {
    	Acm.Object.setText(this.$labTotalCount, value);
    }
    ,setHtmlDivResultList : function(html) {
    	Acm.Object.setHtml(this.$divResultList, html);
    }
    ,setHtmlDivResultGrid: function(html) {
    	Acm.Object.setHtml(this.$divResultGrid, html);
    }
    ,emptySelAssignee: function() {
    	Acm.Object.empty(this.$selAssignee);
    }
    ,setHtmlSelAssignee : function(html) {
    	Acm.Object.setHtml(this.$selAssignee, html);
    }
    ,emptySelWidAssignee: function() {
        Acm.Object.empty(this.$selWidAssignee);
    }
    ,setHtmlSelWidAssignee : function(html) {
        Acm.Object.setHtml(this.$selWidAssignee, html);
    }
    ,getValueSelWidAssignee: function() {
        return Acm.Object.getSelectValue(this.$selWidAssignee);
    }
    ,showSelWidAssignee: function(show) {
        return Acm.Object.show(this.$selWidAssignee, show);
    }
    ,getTextDivAssigneeSelf: function() {
        return Acm.Object.getTextNodeText(this.$divAssigneeSelf);
    }
    ,setTextDivAssigneeSelf: function() {
        Acm.Object.setTextNodeText(this.$divAssigneeSelf);
    }
    ,showDivAssigneeSelf: function(show) {
        Acm.Object.show(this.$divAssigneeSelf, show);
    }
    ,isVisibleDivAssigneeSelf: function() {
        return Acm.Object.isVisible(this.$divAssigneeSelf);
    }
    ,getWidAssign: function() {
	    return this.$widAssign;
	}
    ,getValueTxtAssignComment: function() {
        return Acm.Object.getValue(this.$txtAssignComment);
    }
    ,setValueTxtAssignComment: function(value) {
        Acm.Object.setValue(this.$txtAssignComment, value);
    }
    ,showInvalidAssign: function(show) {
        Acm.Object.showParent(this.$spanInvalidAssign, show);
    }
    ,closeFrmWidAssign: function() {
        this.$btnWidAssignCancel.click();
    }
    ,clearFrmWidAssign: function() {
        TaskList.Object.setValueTxtAssignComment("");
        TaskList.Object.showInvalidAssign(false);
    }
    ,getWidDelete: function() {
    	return jQuery(this.idWidDelete);
	}
    ,getValueTxtDeleteComment: function() {
        return Acm.Object.getValue(jQuery(this.idTxtDeleteComment));
    }
    ,setValueTxtDeleteComment: function(value) {
        Acm.Object.setValue(this.$txtDeleteComment, value);
    }
    ,showInvalidDelete: function(show) {
        Acm.Object.showParent(this.$spanInvalidDelete, show);
    }
    ,closeFrmWidDelete: function() {
        jQuery(TaskList.Object.idBtnWidDeleteCancel).click();
    }
    ,clearFrmWidDelete: function() {
        TaskList.Object.setValueTxtDeleteComment("");
        TaskList.Object.showInvalidDelete(false);
    }
	,getWidFingerPrint: function() {
	    return this.$widFingerPrint;
	}
    ,getValueTxtFingerPrintName: function() {
        return Acm.Object.getValue(this.$txtFingerPrintName);
    }
    ,setValueTxtFingerPrintName: function(value) {
        Acm.Object.setValue(this.$txtFingerPrintName, value);
    }
    ,showInvalidFingerPrint: function(show) {
        Acm.Object.showParent(this.$spanInvalidFingerPrint, show);
    }
    ,closeFrmWidFingerPrint: function() {
        this.$btnWidFingerPrintCancel.click();
    }
    ,clearFrmWidFingerPrint: function() {
        TaskList.Object.setValueTxtFingerPrintName("");
        TaskList.Object.showInvalidFingerPrint(false);
    }

    ,showResultList: function() {
    	TaskList.Object._showDivResultList(true);
    	TaskList.Object._showDivResultGrid(false);
    	TaskList.Object._showLnkViewList(false);
    	TaskList.Object._showLnkViewGrid(true);
    }
    ,showResultGrid: function() {
    	TaskList.Object._showDivResultList(false);
    	TaskList.Object._showDivResultGrid(true);
    	TaskList.Object._showLnkViewList(true);
    	TaskList.Object._showLnkViewGrid(false);
    }
	,getSearchTerm : function() {
		var term = {};

		term.docType = TaskList.Object._getValueSelDocType();
		term.subjectLastName = TaskList.Object._getValueEdtLastName();
		term.subjectSSN = TaskList.Object._getValueEdtSsn();
		term.eqipRequestNumber = TaskList.Object._getValueEdtEQipRequest();
		term.soi = TaskList.Object._getValueEdtSoi();
		term.son = TaskList.Object._getValueEdtSon();
		term.assignee = TaskList.Object._getValueSelAssignee();

		term.supervisorReviewFlag = TaskList.Object._isCheckedChkSupervisorReview();
		term.contractOversightReviewFlag = TaskList.Object._isCheckedChkContractOversight();

		term.queues = [{},{},{}];
		term.queues[0].name = Unassigned.queueProcessing.name;
		term.queues[0].checked = TaskList.Object.isCheckedChkProcessing();
		term.queues[1].name = Unassigned.queueQa.name;
		term.queues[1].checked = TaskList.Object.isCheckedChkQa();
		term.queues[2].name = Unassigned.queueMailback.name;
		term.queues[2].checked = TaskList.Object.isCheckedChkMailback();

		return term;
	}
    ,clearSearchTerm : function() {
        TaskList.Object._setValueSelDocType("placeholder");
        TaskList.Object._setValueEdtLastName("");
        TaskList.Object._setValueEdtSsn("");
        TaskList.Object._setValueEdtEQipRequest("");
        TaskList.Object._setValueEdtSoi("");
        TaskList.Object._setValueEdtSon("");
        TaskList.Object._setValueSelAssignee("placeholder");

        TaskList.Object._setCheckedChkSupervisorReview(false);
        TaskList.Object._setCheckedChkContractOversight(false);

        TaskList.Object._setCheckedChkProcessing(false);
        TaskList.Object._setCheckedChkQa(true);
        TaskList.Object._setCheckedChkMailback(false);
    }

    ,getSelectedQueueNames : function () {
    	var queueNames = "";
    	if (TaskList.Object.isCheckedChkProcessing()) {
    		queueNames += "," + Unassigned.queueProcessing.name;
    	}
    	if (TaskList.Object.isCheckedChkQa()) {
    		queueNames += "," + Unassigned.queueQa.name;
    	}
    	if (TaskList.Object.isCheckedChkMailback()) {
    		queueNames += "," + Unassigned.queueMailback.name;
    	}

    	if (Acm.Common.isNotEmpty(queueNames)) {
    		queueNames = queueNames.substring(1, queueNames.length); //discard extra leading ','
    	}
    	return queueNames;
    }
//    ,getSelectedQueue : function () {
//        if (TaskList.Object.isCheckedChkProcessing()) {
//            return Unassigned.queueProcessing;
//        } else if (TaskList.Object.isCheckedChkQa()) {
//            return Unassigned.queueQa;
//        } else if (TaskList.Object.isCheckedChkMailback()) {
//            return Unassigned.queueMailback;
//        } else {
//            return null;
//        }
//    }

    ,_showLnkViewList: function(show) {
        Acm.Object.show(this.$lnkViewList).closest('li', show);
    }
    ,_showLnkViewGrid: function(show) {
        Acm.Object.show(this.$lnkViewGrid).closest('li', show);
    }
    ,_showDivResultList: function(show) {
        Acm.Object.show(this.$divResultList, show);
    }
    ,_showDivResultGrid: function(show) {
        Acm.Object.show(this.$divResultGrid, show);
    }
    ,_getValueSelDocType : function() {
        return Acm.Object.getSelectValue(this.$selDocType);
    }
    ,_setValueSelDocType : function(val) {
        Acm.Object.setSelectValue(this.$selDocType, val);
    }
    ,_getValueEdtLastName: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtLastName);
    }
    ,_setValueEdtLastName: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtLastName, val);
    }
    ,_getValueEdtSsn: function() {
    	var display = Acm.Object.getPlaceHolderInput(this.$edtSsn);
        return Acm.Object.getSsnValue(display);
    }
    ,_setValueEdtSsn: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtSsn, val);
    }
    ,_getValueEdtEQipRequest: function() {
    	return Acm.Object.getPlaceHolderInput(this.$edtEQipRequest);
    }
    ,_setValueEdtEQipRequest: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtEQipRequest, val);
    }
    ,_getValueEdtSoi: function() {
    	return Acm.Object.getPlaceHolderInput(this.$edtSoi);
    }
    ,_setValueEdtSoi: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtSoi, val);
    }
    ,_getValueEdtSon: function() {
    	return Acm.Object.getPlaceHolderInput(this.$edtSon);
    }
    ,_setValueEdtSon: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtSon, val);
    }
    ,_getValueSelAssignee: function() {
    	return Acm.Object.getSelectValue(this.$selAssignee);
    }
    ,_setValueSelAssignee : function(val) {
        Acm.Object.setSelectValue(this.$selAssignee, val);
    }
    ,_isCheckedChkSupervisorReview: function() {
    	return Acm.Object.isChecked(this.$chkSupervisorReview);
    }
    ,_setCheckedChkSupervisorReview: function(val) {
        Acm.Object.setChecked(this.$chkSupervisorReview, val);
    }
    ,_isCheckedChkContractOversight: function() {
    	return Acm.Object.isChecked(this.$chkContractOversight);
    }
    ,_setCheckedChkContractOversight: function(val) {
        Acm.Object.setChecked(this.$chkContractOversight, val);
    }
    ,isCheckedChkProcessing: function() {
    	return Acm.Object.isChecked(this.$chkProcessing);
    }
    ,_setCheckedChkProcessing: function(val) {
        Acm.Object.setChecked(this.$chkProcessing, val);
    }
    ,isCheckedChkQa: function() {
    	return Acm.Object.isChecked(this.$chkQa);
    }
    ,_setCheckedChkQa: function(val) {
        Acm.Object.setChecked(this.$chkQa, val);
    }
    ,isCheckedChkMailback: function() {
    	return Acm.Object.isChecked(this.$chkMailback);
    }
    ,_setCheckedChkMailback: function(val) {
        Acm.Object.setChecked(this.$chkMailback, val);
    }

};




