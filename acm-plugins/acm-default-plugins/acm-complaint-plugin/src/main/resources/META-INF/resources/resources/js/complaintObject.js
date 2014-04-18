/**
 * Complaint.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Complaint.Object = {
    $chkSelectAll              : undefined

    ,initialize : function() {
//        this.$chkSelectAll              = jQuery("#chkSelectAll");

//        this.$chkSelectAll.click(function() {Complaint.Event.onClickChkSelectAll(this);});
    }

    ,readPermissions: function() {
        this.$hidPermissions.each (function(i, e) {
            var val = jQuery(e).val();
            var key = jQuery(e).attr("id");
            Complaint.permissions[key] = ("true" === val);
        });

    }
    ,usePagination: function(totalItems, itemsPerPage, callback) {
        ACM.Object.usePagination(this.$jPaginate, totalItems, itemsPerPage, callback)
    }
    ,setEnableBtnPrint: function(enable) {
        ACM.Object.setEnable(this.$btnPrint, enable);
    }
    ,setEnableBtnAssign: function(enable) {
        ACM.Object.setEnable(this.$btnAssign, enable);
    }
    ,setEnableBtnDelete: function(enable) {
        ACM.Object.setEnable(this.$btnDelete, enable);
    }
    ,showBtnPrint: function(show) {
        ACM.Object.show(this.$btnPrint, show);
    }
    ,setEnableBtnFingerPrint: function(enable) {
        ACM.Object.setEnable(this.$btnFingerPrint, enable);
    }
    ,showBtnFingerPrint: function(show) {
        ACM.Object.show(this.$btnFingerPrint, show);
    }
    ,showBtnAssign: function(show) {
        ACM.Object.show(this.$btnAssign, show);
    }
    ,showBtnDelete: function(show) {
        ACM.Object.show(this.$btnDelete, show);
    }
    ,showLnkNextItems: function() {
        alert("showLnkNextItem depleted");
//        var checked = Complaint.Object.isCheckedChkQa();
//        Complaint.Object.showLnkNextItemQa(checked);
//        checked = Complaint.Object.isCheckedChkMailback();
//        Complaint.Object.showLnkNextItemMb(checked);
    }
    ,showLnkNextItemQa: function(show) {
        ACM.Object.showParent(this.$lnkNextItemQa, show);
    }
    ,showLnkNextItemMb: function(show) {
        ACM.Object.showParent(this.$lnkNextItemMb, show);
    }
	,putUnassignedDoc: function(unassignedTaskId, unassignedDoc) {
        var selected = false;
        var prevUnassignedDoc = Complaint.Object.getUnassignedDoc(unassignedTaskId);
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
        var unassignedDocs = Complaint.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = Complaint.Object.getUnassignedDoc(unassignedTaskId);
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
        var unassignedDocs = Complaint.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = Complaint.Object.getUnassignedDoc(unassignedTaskId);
            if (unassignedDoc) {
                if (unassignedDoc.selected) {
                    taskIds.push(unassignedTaskId);
                }
            }
        }
        return taskIds;
    }
	,setCheckChkSelectOne : function(checked) {
		jQuery("input[type=checkbox]" + Complaint.Object.clsChkSelectOne).each(function(i, item) {
			item.checked = checked;
            Complaint.Object.selectUnassignedDoc(item);
		});
	}
	,regClickChkSelectOne: function() {
		jQuery("input[type=checkbox]" + Complaint.Object.clsChkSelectOne).click(function() {
            Complaint.Callbacks.onClickChkSelectOne(this);
        });
	}
	,getTaskIdClicked: function(checkBox) {
		return jQuery(checkBox).next().val();
	}
    ,selectUnassignedDoc : function(checkBox) {
        var unassignedTaskId = Complaint.Object.getTaskIdClicked(checkBox);
        var unassignedDoc  = Complaint.Object.getUnassignedDoc(unassignedTaskId);
        if (unassignedDoc) {
            unassignedDoc.selected = checkBox.checked;
        }
    }
	,getContextPath: function() {
        return Complaint.contextPath;
	}
    ,getTicket: function() {
        return Complaint.ticket;
    }
    ,showLabAssigneeProgress: function(show) {
    	ACM.Object.show(this.$labAssigneeProgress, show);
	}
    ,setTextLabTotalCount: function(value) {
    	ACM.Object.setText(this.$labTotalCount, value);
    }
    ,setHtmlDivResultList : function(html) {
    	ACM.Object.setHtml(this.$divResultList, html);
    }
    ,setHtmlDivResultGrid: function(html) {
    	ACM.Object.setHtml(this.$divResultGrid, html);
    }
    ,emptySelAssignee: function() {
    	ACM.Object.empty(this.$selAssignee);
    }
    ,setHtmlSelAssignee : function(html) {
    	ACM.Object.setHtml(this.$selAssignee, html);
    }
    ,emptySelWidAssignee: function() {
        ACM.Object.empty(this.$selWidAssignee);
    }
    ,setHtmlSelWidAssignee : function(html) {
        ACM.Object.setHtml(this.$selWidAssignee, html);
    }
    ,getValueSelWidAssignee: function() {
        return ACM.Object.getSelectValue(this.$selWidAssignee);
    }
    ,showSelWidAssignee: function(show) {
        return ACM.Object.show(this.$selWidAssignee, show);
    }
    ,getTextDivAssigneeSelf: function() {
        return ACM.Object.getTextNodeText(this.$divAssigneeSelf);
    }
    ,setTextDivAssigneeSelf: function() {
        ACM.Object.setTextNodeText(this.$divAssigneeSelf);
    }
    ,showDivAssigneeSelf: function(show) {
        ACM.Object.show(this.$divAssigneeSelf, show);
    }
    ,isVisibleDivAssigneeSelf: function() {
        return ACM.Object.isVisible(this.$divAssigneeSelf);
    }
    ,getWidAssign: function() {
	    return this.$widAssign;
	}
    ,getValueTxtAssignComment: function() {
        return ACM.Object.getValue(this.$txtAssignComment);
    }
    ,setValueTxtAssignComment: function(value) {
        ACM.Object.setValue(this.$txtAssignComment, value);
    }
    ,showInvalidAssign: function(show) {
        ACM.Object.showParent(this.$spanInvalidAssign, show);
    }
    ,closeFrmWidAssign: function() {
        this.$btnWidAssignCancel.click();
    }
    ,clearFrmWidAssign: function() {
        Complaint.Object.setValueTxtAssignComment("");
        Complaint.Object.showInvalidAssign(false);
    }
    ,getWidDelete: function() {
    	return jQuery(this.idWidDelete);
	}
    ,getValueTxtDeleteComment: function() {
        return ACM.Object.getValue(jQuery(this.idTxtDeleteComment));
    }
    ,setValueTxtDeleteComment: function(value) {
        ACM.Object.setValue(this.$txtDeleteComment, value);
    }
    ,showInvalidDelete: function(show) {
        ACM.Object.showParent(this.$spanInvalidDelete, show);
    }
    ,closeFrmWidDelete: function() {
        jQuery(Complaint.Object.idBtnWidDeleteCancel).click();
    }
    ,clearFrmWidDelete: function() {
        Complaint.Object.setValueTxtDeleteComment("");
        Complaint.Object.showInvalidDelete(false);
    }
	,getWidFingerPrint: function() {
	    return this.$widFingerPrint;
	}
    ,getValueTxtFingerPrintName: function() {
        return ACM.Object.getValue(this.$txtFingerPrintName);
    }
    ,setValueTxtFingerPrintName: function(value) {
        ACM.Object.setValue(this.$txtFingerPrintName, value);
    }
    ,showInvalidFingerPrint: function(show) {
        ACM.Object.showParent(this.$spanInvalidFingerPrint, show);
    }
    ,closeFrmWidFingerPrint: function() {
        this.$btnWidFingerPrintCancel.click();
    }
    ,clearFrmWidFingerPrint: function() {
        Complaint.Object.setValueTxtFingerPrintName("");
        Complaint.Object.showInvalidFingerPrint(false);
    }

    ,showResultList: function() {
    	Complaint.Object._showDivResultList(true);
    	Complaint.Object._showDivResultGrid(false);
    	Complaint.Object._showLnkViewList(false);
    	Complaint.Object._showLnkViewGrid(true);
    }
    ,showResultGrid: function() {
    	Complaint.Object._showDivResultList(false);
    	Complaint.Object._showDivResultGrid(true);
    	Complaint.Object._showLnkViewList(true);
    	Complaint.Object._showLnkViewGrid(false);
    }
	,getSearchTerm : function() {
		var term = {};

		term.docType = Complaint.Object._getValueSelDocType();
		term.subjectLastName = Complaint.Object._getValueEdtLastName();
		term.subjectSSN = Complaint.Object._getValueEdtSsn();
		term.eqipRequestNumber = Complaint.Object._getValueEdtEQipRequest();
		term.soi = Complaint.Object._getValueEdtSoi();
		term.son = Complaint.Object._getValueEdtSon();
		term.assignee = Complaint.Object._getValueSelAssignee();

		term.supervisorReviewFlag = Complaint.Object._isCheckedChkSupervisorReview();
		term.contractOversightReviewFlag = Complaint.Object._isCheckedChkContractOversight();

		term.queues = [{},{},{}];
		term.queues[0].name = Unassigned.queueProcessing.name;
		term.queues[0].checked = Complaint.Object.isCheckedChkProcessing();
		term.queues[1].name = Unassigned.queueQa.name;
		term.queues[1].checked = Complaint.Object.isCheckedChkQa();
		term.queues[2].name = Unassigned.queueMailback.name;
		term.queues[2].checked = Complaint.Object.isCheckedChkMailback();

		return term;
	}
    ,clearSearchTerm : function() {
        Complaint.Object._setValueSelDocType("placeholder");
        Complaint.Object._setValueEdtLastName("");
        Complaint.Object._setValueEdtSsn("");
        Complaint.Object._setValueEdtEQipRequest("");
        Complaint.Object._setValueEdtSoi("");
        Complaint.Object._setValueEdtSon("");
        Complaint.Object._setValueSelAssignee("placeholder");

        Complaint.Object._setCheckedChkSupervisorReview(false);
        Complaint.Object._setCheckedChkContractOversight(false);

        Complaint.Object._setCheckedChkProcessing(false);
        Complaint.Object._setCheckedChkQa(true);
        Complaint.Object._setCheckedChkMailback(false);
    }

    ,getSelectedQueueNames : function () {
    	var queueNames = "";
    	if (Complaint.Object.isCheckedChkProcessing()) {
    		queueNames += "," + Unassigned.queueProcessing.name;
    	}
    	if (Complaint.Object.isCheckedChkQa()) {
    		queueNames += "," + Unassigned.queueQa.name;
    	}
    	if (Complaint.Object.isCheckedChkMailback()) {
    		queueNames += "," + Unassigned.queueMailback.name;
    	}

    	if (ACM.Common.isNotEmpty(queueNames)) {
    		queueNames = queueNames.substring(1, queueNames.length); //discard extra leading ','
    	}
    	return queueNames;
    }
//    ,getSelectedQueue : function () {
//        if (Complaint.Object.isCheckedChkProcessing()) {
//            return Unassigned.queueProcessing;
//        } else if (Complaint.Object.isCheckedChkQa()) {
//            return Unassigned.queueQa;
//        } else if (Complaint.Object.isCheckedChkMailback()) {
//            return Unassigned.queueMailback;
//        } else {
//            return null;
//        }
//    }

    ,_showLnkViewList: function(show) {
        ACM.Object.show(this.$lnkViewList).closest('li', show);
    }
    ,_showLnkViewGrid: function(show) {
        ACM.Object.show(this.$lnkViewGrid).closest('li', show);
    }
    ,_showDivResultList: function(show) {
        ACM.Object.show(this.$divResultList, show);
    }
    ,_showDivResultGrid: function(show) {
        ACM.Object.show(this.$divResultGrid, show);
    }
    ,_getValueSelDocType : function() {
        return ACM.Object.getSelectValue(this.$selDocType);
    }
    ,_setValueSelDocType : function(val) {
        ACM.Object.setSelectValue(this.$selDocType, val);
    }
    ,_getValueEdtLastName: function() {
        return ACM.Object.getPlaceHolderInput(this.$edtLastName);
    }
    ,_setValueEdtLastName: function(val) {
        ACM.Object.setPlaceHolderInput(this.$edtLastName, val);
    }
    ,_getValueEdtSsn: function() {
    	var display = ACM.Object.getPlaceHolderInput(this.$edtSsn);
        return ACM.Object.getSsnValue(display);
    }
    ,_setValueEdtSsn: function(val) {
        ACM.Object.setPlaceHolderInput(this.$edtSsn, val);
    }
    ,_getValueEdtEQipRequest: function() {
    	return ACM.Object.getPlaceHolderInput(this.$edtEQipRequest);
    }
    ,_setValueEdtEQipRequest: function(val) {
        ACM.Object.setPlaceHolderInput(this.$edtEQipRequest, val);
    }
    ,_getValueEdtSoi: function() {
    	return ACM.Object.getPlaceHolderInput(this.$edtSoi);
    }
    ,_setValueEdtSoi: function(val) {
        ACM.Object.setPlaceHolderInput(this.$edtSoi, val);
    }
    ,_getValueEdtSon: function() {
    	return ACM.Object.getPlaceHolderInput(this.$edtSon);
    }
    ,_setValueEdtSon: function(val) {
        ACM.Object.setPlaceHolderInput(this.$edtSon, val);
    }
    ,_getValueSelAssignee: function() {
    	return ACM.Object.getSelectValue(this.$selAssignee);
    }
    ,_setValueSelAssignee : function(val) {
        ACM.Object.setSelectValue(this.$selAssignee, val);
    }
    ,_isCheckedChkSupervisorReview: function() {
    	return ACM.Object.isChecked(this.$chkSupervisorReview);
    }
    ,_setCheckedChkSupervisorReview: function(val) {
        ACM.Object.setChecked(this.$chkSupervisorReview, val);
    }
    ,_isCheckedChkContractOversight: function() {
    	return ACM.Object.isChecked(this.$chkContractOversight);
    }
    ,_setCheckedChkContractOversight: function(val) {
        ACM.Object.setChecked(this.$chkContractOversight, val);
    }
    ,isCheckedChkProcessing: function() {
    	return ACM.Object.isChecked(this.$chkProcessing);
    }
    ,_setCheckedChkProcessing: function(val) {
        ACM.Object.setChecked(this.$chkProcessing, val);
    }
    ,isCheckedChkQa: function() {
    	return ACM.Object.isChecked(this.$chkQa);
    }
    ,_setCheckedChkQa: function(val) {
        ACM.Object.setChecked(this.$chkQa, val);
    }
    ,isCheckedChkMailback: function() {
    	return ACM.Object.isChecked(this.$chkMailback);
    }
    ,_setCheckedChkMailback: function(val) {
        ACM.Object.setChecked(this.$chkMailback, val);
    }

};




