/**
 * ComplaintList.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
ComplaintList.Object = {
    initialize : function() {
        this.$ulComplaints = $("#ulComplaints");
        this.$lnkTitle = $("#caseTitle");
        this.$h4TitleDate = $("#caseTitle").parent();
        this.$divDetails = $(".complaintDetails");

    }

    ,get$ulComplaints : function() {
        return this.$ulComplaints;
    }

    ,getHtmlUlComplaints: function() {
        return Acm.Object.getHtml(this.$ulComplaints);
    }
    ,setHtmlUlComplaints: function(val) {
        return Acm.Object.setHtml(this.$ulComplaints, val);
    }
    ,registerClickListItemEvents: function() {
        this.$ulComplaints.find("a.thumb-sm").click(function(e) {ComplaintList.Event.onClickLnkListItemImage(this);});
        this.$ulComplaints.find("a.text-ellipsis").click(function(e) {ComplaintList.Event.onClickLnkListItem(this);});
    }
    ,getHiddenComplaintId: function(e) {
        var $hidden = $(e).siblings("input[type='hidden']");
        return $hidden.val();
    }
    ,updateDetail: function(c) {
        this.setTextTitle(c.complaintTitle);
        this.setTextTitleDate(" (" + ComplaintList.getDateFromDatetime(c.created) + ")");
        this.setHtmlDetails(c.details);


//todo: jasmine test
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
        var cur = Complaint.getComplaintId();
        this.$ulComplaints.find("li").each(function(index) {
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
//        var checked = ComplaintList.Object.isCheckedChkQa();
//        ComplaintList.Object.showLnkNextItemQa(checked);
//        checked = ComplaintList.Object.isCheckedChkMailback();
//        ComplaintList.Object.showLnkNextItemMb(checked);
    }
    ,showLnkNextItemQa: function(show) {
        Acm.Object.showParent(this.$lnkNextItemQa, show);
    }
    ,showLnkNextItemMb: function(show) {
        Acm.Object.showParent(this.$lnkNextItemMb, show);
    }
	,putUnassignedDoc: function(unassignedTaskId, unassignedDoc) {
        var selected = false;
        var prevUnassignedDoc = ComplaintList.Object.getUnassignedDoc(unassignedTaskId);
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
        var unassignedDocs = ComplaintList.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = ComplaintList.Object.getUnassignedDoc(unassignedTaskId);
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
        var unassignedDocs = ComplaintList.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = ComplaintList.Object.getUnassignedDoc(unassignedTaskId);
            if (unassignedDoc) {
                if (unassignedDoc.selected) {
                    taskIds.push(unassignedTaskId);
                }
            }
        }
        return taskIds;
    }
	,setCheckChkSelectOne : function(checked) {
		jQuery("input[type=checkbox]" + ComplaintList.Object.clsChkSelectOne).each(function(i, item) {
			item.checked = checked;
            ComplaintList.Object.selectUnassignedDoc(item);
		});
	}
	,regClickChkSelectOne: function() {
		jQuery("input[type=checkbox]" + ComplaintList.Object.clsChkSelectOne).click(function() {
            ComplaintList.Callbacks.onClickChkSelectOne(this);
        });
	}
	,getTaskIdClicked: function(checkBox) {
		return jQuery(checkBox).next().val();
	}
    ,selectUnassignedDoc : function(checkBox) {
        var unassignedTaskId = ComplaintList.Object.getTaskIdClicked(checkBox);
        var unassignedDoc  = ComplaintList.Object.getUnassignedDoc(unassignedTaskId);
        if (unassignedDoc) {
            unassignedDoc.selected = checkBox.checked;
        }
    }
	,getContextPath: function() {
        return ComplaintList.contextPath;
	}
    ,getTicket: function() {
        return ComplaintList.ticket;
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
        ComplaintList.Object.setValueTxtAssignComment("");
        ComplaintList.Object.showInvalidAssign(false);
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
        jQuery(ComplaintList.Object.idBtnWidDeleteCancel).click();
    }
    ,clearFrmWidDelete: function() {
        ComplaintList.Object.setValueTxtDeleteComment("");
        ComplaintList.Object.showInvalidDelete(false);
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
        ComplaintList.Object.setValueTxtFingerPrintName("");
        ComplaintList.Object.showInvalidFingerPrint(false);
    }

    ,showResultList: function() {
    	ComplaintList.Object._showDivResultList(true);
    	ComplaintList.Object._showDivResultGrid(false);
    	ComplaintList.Object._showLnkViewList(false);
    	ComplaintList.Object._showLnkViewGrid(true);
    }
    ,showResultGrid: function() {
    	ComplaintList.Object._showDivResultList(false);
    	ComplaintList.Object._showDivResultGrid(true);
    	ComplaintList.Object._showLnkViewList(true);
    	ComplaintList.Object._showLnkViewGrid(false);
    }
	,getSearchTerm : function() {
		var term = {};

		term.docType = ComplaintList.Object._getValueSelDocType();
		term.subjectLastName = ComplaintList.Object._getValueEdtLastName();
		term.subjectSSN = ComplaintList.Object._getValueEdtSsn();
		term.eqipRequestNumber = ComplaintList.Object._getValueEdtEQipRequest();
		term.soi = ComplaintList.Object._getValueEdtSoi();
		term.son = ComplaintList.Object._getValueEdtSon();
		term.assignee = ComplaintList.Object._getValueSelAssignee();

		term.supervisorReviewFlag = ComplaintList.Object._isCheckedChkSupervisorReview();
		term.contractOversightReviewFlag = ComplaintList.Object._isCheckedChkContractOversight();

		term.queues = [{},{},{}];
		term.queues[0].name = Unassigned.queueProcessing.name;
		term.queues[0].checked = ComplaintList.Object.isCheckedChkProcessing();
		term.queues[1].name = Unassigned.queueQa.name;
		term.queues[1].checked = ComplaintList.Object.isCheckedChkQa();
		term.queues[2].name = Unassigned.queueMailback.name;
		term.queues[2].checked = ComplaintList.Object.isCheckedChkMailback();

		return term;
	}
    ,clearSearchTerm : function() {
        ComplaintList.Object._setValueSelDocType("placeholder");
        ComplaintList.Object._setValueEdtLastName("");
        ComplaintList.Object._setValueEdtSsn("");
        ComplaintList.Object._setValueEdtEQipRequest("");
        ComplaintList.Object._setValueEdtSoi("");
        ComplaintList.Object._setValueEdtSon("");
        ComplaintList.Object._setValueSelAssignee("placeholder");

        ComplaintList.Object._setCheckedChkSupervisorReview(false);
        ComplaintList.Object._setCheckedChkContractOversight(false);

        ComplaintList.Object._setCheckedChkProcessing(false);
        ComplaintList.Object._setCheckedChkQa(true);
        ComplaintList.Object._setCheckedChkMailback(false);
    }

    ,getSelectedQueueNames : function () {
    	var queueNames = "";
    	if (ComplaintList.Object.isCheckedChkProcessing()) {
    		queueNames += "," + Unassigned.queueProcessing.name;
    	}
    	if (ComplaintList.Object.isCheckedChkQa()) {
    		queueNames += "," + Unassigned.queueQa.name;
    	}
    	if (ComplaintList.Object.isCheckedChkMailback()) {
    		queueNames += "," + Unassigned.queueMailback.name;
    	}

    	if (Acm.Common.isNotEmpty(queueNames)) {
    		queueNames = queueNames.substring(1, queueNames.length); //discard extra leading ','
    	}
    	return queueNames;
    }
//    ,getSelectedQueue : function () {
//        if (ComplaintList.Object.isCheckedChkProcessing()) {
//            return Unassigned.queueProcessing;
//        } else if (ComplaintList.Object.isCheckedChkQa()) {
//            return Unassigned.queueQa;
//        } else if (ComplaintList.Object.isCheckedChkMailback()) {
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




