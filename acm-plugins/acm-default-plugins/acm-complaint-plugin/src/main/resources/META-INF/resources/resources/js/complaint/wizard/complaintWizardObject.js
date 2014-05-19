/**
 * ComplaintWizard.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
ComplaintWizard.Object = {
    initialize : function() {
        this.$lnkSave                = $("#lnkSave");
        this.$lnkSubmit              = $("#lnkSubmit");
        this.$edtFname               = $("#fname");
        this.$edtLname               = $("#lname");
        this.$edtCompany             = $("#company");
        this.$selPersonTitle         = $("#personTitle");
        this.$divComplaintDetails    = $('.complaintDetails');
        this.$selIntiatorFlags       = $(".choose-intitiatorFlags");   //"#intiatorFlags"
        this.$selComplaintFlags      = $(".choose-complaintFlags");    //"#intiatorFlags"
        this.$selApprovers           = $(".choose-approvers");         //"#approvers"
        this.$selCollab              = $(".choose-collab");            //""
        this.$selNotifications       = $(".choose-notifications");     //"#notifications"
        this.$edtIncidentDate        = $("#incidentDate");
        this.$selComplaintType       = $("#complaintType");

        this.$uppload                = $('#upload');
        this.$upploadDrop            = $('#drop');
        this.$upploadList            = $('#upload ul');
        this.$upploadClick           = $('#drop a');

        this.$lnkSave.click(function(e) {ComplaintWizard.Event.onClickLnkSave(e);});
        this.$lnkSubmit.click(function(e) {ComplaintWizard.Event.onClickLnkSubmit(e);});

        this.$divComplaintDetails.summernote({
            height: 300
        });

        this.$selIntiatorFlags.chosen();
        this.$selComplaintFlags.chosen();
        this.$selApprovers.chosen();
        this.$selCollab.chosen();
        this.$selNotifications.chosen();


        this._useFileUpload(this.$uppload, this.$upploadDrop, this.$upploadList, this.$upploadClick);
    }


    ,setEnableBtnSave: function(enable) {
        Acm.Object.setEnable(this.$btnSave, enable);
    }
    ,getValueEdtFname: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtFname);
    }
//    ,setValueEdtFname: function(val) {
//        return Acm.Object.setPlaceHolderInput(this.$edtFname, val);
//    }
    ,getValueEdtLname: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtLname);
    }
    ,getValueEdtCompany: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtCompany);
    }
    ,getValueSelPersonTitle: function() {
        return Acm.Object.getSelectValue(this.$selPersonTitle);
    }
    ,getHtmlDivComplaintDetails: function() {
        return Acm.Object.getSummernote(this.$divComplaintDetails);
    }
    ,getValueEdtIncidentDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtIncidentDate);
    }
    ,getSelectValuesSelIntiatorFlags: function() {
        return Acm.Object.getSelectValues(this.$selIntiatorFlags);
    }
    ,getSelectValuesSelComplaintFlags: function() {
        return Acm.Object.getSelectValues(this.$selComplaintFlags);
    }
    ,getSelectValuesSelApprovers: function() {
        return Acm.Object.getSelectValues(this.$selApprovers);
    }
    ,getSelectValuesSelCollab: function() {
        return Acm.Object.getSelectValues(this.$selCollab);
    }
    ,getSelectValuesSelNotifications: function() {
        return Acm.Object.getSelectValues(this.$selNotifications);
    }

    ,getComplaintData : function() {
        var data = {};

        data.originator = {};
        if (Acm.isNotEmpty(Complaint.getComplaintId())) {
            data.complaintId = Complaint.getComplaintId();
        }
        data.originator.givenName = this.getValueEdtFname();
        data.originator.familyName = this.getValueEdtLname();
        data.originator.company = this.getValueEdtCompany();

        //$selComplaintType
        data.originator.title = this.getValueSelPersonTitle();

        //data.details
        var a1 = this.getHtmlDivComplaintDetails();

        //incidentDate
        var d1 = this.getValueEdtIncidentDate();

        //"securityTags": ["Anonymous", "Confidential", "Top Secret"]
        var b6 = this.getSelectValuesSelApprovers();



        var z = 1;

//        term.docType = ComplaintWizard.Object._getValueSelDocType();
//        term.subjectLastName = ComplaintWizard.Object._getValueEdtLastName();
//        term.subjectSSN = ComplaintWizard.Object._getValueEdtSsn();
//        term.eqipRequestNumber = ComplaintWizard.Object._getValueEdtEQipRequest();
//        term.soi = ComplaintWizard.Object._getValueEdtSoi();
//        term.son = ComplaintWizard.Object._getValueEdtSon();
//        term.assignee = ComplaintWizard.Object._getValueSelAssignee();
//
//        term.supervisorReviewFlag = ComplaintWizard.Object._isCheckedChkSupervisorReview();
//        term.contractOversightReviewFlag = ComplaintWizard.Object._isCheckedChkContractOversight();
//
//        term.queues = [{},{},{}];
//        term.queues[0].name = Unassigned.queueProcessing.name;
//        term.queues[0].checked = ComplaintWizard.Object.isCheckedChkProcessing();
//        term.queues[1].name = Unassigned.queueQa.name;
//        term.queues[1].checked = ComplaintWizard.Object.isCheckedChkQa();
//        term.queues[2].name = Unassigned.queueMailback.name;
//        term.queues[2].checked = ComplaintWizard.Object.isCheckedChkMailback();

        return data;
    }


    ,_jqXHR : undefined
    ,_useFileUpload: function($upload, $drop, $ul, $click) {
        $(function(){

            //var ul = $ul;

            $click.click(function(){
                // Simulate a click on the file input button
                // to show the file browser dialog
                $(this).parent().find('input').click();
            });

            // Initialize the jQuery File Upload plugin
            //jwu $('#upload').fileupload({
            _jqXHR = $upload.fileupload({
                //To Explore:
                //redirect : to complaintList
                //redirectParamName:
                //
//check if complaintId not created, create it first
//                submit: function (e, data) {
//                    var input = $('#input');
//                    data.formData = {example: input.val()};
//                    if (!data.formData.example) {
//                        data.context.find('button').prop('disabled', false);
//                        input.focus();
//                        return false;
//                    }
//                },
                done: function (e, data) {
                    var a1 = data.result
                    var a2 = data.textStatus;
                    var a3 = data.jqXHR;
                    var z = 1;
                    //alert("done");
                },
//                always: function (e, data) {
//                    // data.result
//                    // data.textStatus;
//                    // data.jqXHR;
//                },
                //autoUpload: false
                //sequentialUploads: true



                url: Acm.getContextPath() + ComplaintWizard.Service.API_UPLOAD_COMPLAINT_FILE,

                formData: function(form) {
                    var fd = [{}];
                    fd[0].name = "complaintId";
                    fd[0].value = Complaint.getComplaintId();
                    return fd;
                },

                // This element will accept file drag/drop uploading
                dropZone: $drop,

                // This function is called when a file is added to the queue;
                // either via the browse button, or via drag/drop:
                add: function (e, data) {

                    var tpl = $('<li class="working"><input type="text" value="0" data-width="48" data-height="48"'+
                        ' data-fgColor="#0788a5" data-readOnly="1" data-bgColor="#3e4043" /><p></p><span></span></li>');

                    // Append the file name and file size
                    tpl.find('p').text(data.files[0].name)
                        .append('<i>' + formatFileSize(data.files[0].size) + '</i>');

                    // Add the HTML to the UL element
                    data.context = tpl.appendTo($ul);

                    // Initialize the knob plugin
                    tpl.find('input').knob();

                    // Listen for clicks on the cancel icon
                    tpl.find('span').click(function(){

                        if(tpl.hasClass('working')){
                            //jwu jqXHR.abort();
                            _jqXHR.abort();
                        }

                        tpl.fadeOut(function(){
                            tpl.remove();
                        });

                    });

                    // Automatically upload the file once it is added to the queue
                    //var jqXHR = data.submit();
                    _jqXHR = data.submit();
                },

                progress: function(e, data){

                    // Calculate the completion percentage of the upload
                    var progress = parseInt(data.loaded / data.total * 100, 10);

                    // Update the hidden input field and trigger a change
                    // so that the jQuery knob plugin knows to update the dial
                    data.context.find('input').val(progress).change();

                    if(progress == 100){
                        data.context.removeClass('working');
                    }
                },

                fail:function(e, data){
                    // Something has gone wrong!
                    data.context.addClass('error');
                }

            });


            // Prevent the default action when a file is dropped on the window
            $(document).on('drop dragover', function (e) {
                e.preventDefault();
            });

            // Helper function that formats the file sizes
            function formatFileSize(bytes) {
                if (typeof bytes !== 'number') {
                    return '';
                }

                if (bytes >= 1000000000) {
                    return (bytes / 1000000000).toFixed(2) + ' GB';
                }

                if (bytes >= 1000000) {
                    return (bytes / 1000000).toFixed(2) + ' MB';
                }

                return (bytes / 1000).toFixed(2) + ' KB';
            }

        });
    }

    ,usePagination: function(totalItems, itemsPerPage, callback) {
        Acm.Object.usePagination(this.$jPaginate, totalItems, itemsPerPage, callback)
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
//        var checked = ComplaintWizard.Object.isCheckedChkQa();
//        ComplaintWizard.Object.showLnkNextItemQa(checked);
//        checked = ComplaintWizard.Object.isCheckedChkMailback();
//        ComplaintWizard.Object.showLnkNextItemMb(checked);
    }
    ,showLnkNextItemQa: function(show) {
        Acm.Object.showParent(this.$lnkNextItemQa, show);
    }
    ,showLnkNextItemMb: function(show) {
        Acm.Object.showParent(this.$lnkNextItemMb, show);
    }
	,putUnassignedDoc: function(unassignedTaskId, unassignedDoc) {
        var selected = false;
        var prevUnassignedDoc = ComplaintWizard.Object.getUnassignedDoc(unassignedTaskId);
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
        var unassignedDocs = ComplaintWizard.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = ComplaintWizard.Object.getUnassignedDoc(unassignedTaskId);
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
        var unassignedDocs = ComplaintWizard.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = ComplaintWizard.Object.getUnassignedDoc(unassignedTaskId);
            if (unassignedDoc) {
                if (unassignedDoc.selected) {
                    taskIds.push(unassignedTaskId);
                }
            }
        }
        return taskIds;
    }
	,setCheckChkSelectOne : function(checked) {
		jQuery("input[type=checkbox]" + ComplaintWizard.Object.clsChkSelectOne).each(function(i, item) {
			item.checked = checked;
            ComplaintWizard.Object.selectUnassignedDoc(item);
		});
	}
	,regClickChkSelectOne: function() {
		jQuery("input[type=checkbox]" + ComplaintWizard.Object.clsChkSelectOne).click(function() {
            ComplaintWizard.Callbacks.onClickChkSelectOne(this);
        });
	}
	,getTaskIdClicked: function(checkBox) {
		return jQuery(checkBox).next().val();
	}
    ,selectUnassignedDoc : function(checkBox) {
        var unassignedTaskId = ComplaintWizard.Object.getTaskIdClicked(checkBox);
        var unassignedDoc  = ComplaintWizard.Object.getUnassignedDoc(unassignedTaskId);
        if (unassignedDoc) {
            unassignedDoc.selected = checkBox.checked;
        }
    }
	,getContextPath: function() {
        return ComplaintWizard.contextPath;
	}
    ,getTicket: function() {
        return ComplaintWizard.ticket;
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
        ComplaintWizard.Object.setValueTxtAssignComment("");
        ComplaintWizard.Object.showInvalidAssign(false);
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
        jQuery(ComplaintWizard.Object.idBtnWidDeleteCancel).click();
    }
    ,clearFrmWidDelete: function() {
        ComplaintWizard.Object.setValueTxtDeleteComment("");
        ComplaintWizard.Object.showInvalidDelete(false);
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
        ComplaintWizard.Object.setValueTxtFingerPrintName("");
        ComplaintWizard.Object.showInvalidFingerPrint(false);
    }

    ,showResultList: function() {
    	ComplaintWizard.Object._showDivResultList(true);
    	ComplaintWizard.Object._showDivResultGrid(false);
    	ComplaintWizard.Object._showLnkViewList(false);
    	ComplaintWizard.Object._showLnkViewGrid(true);
    }
    ,showResultGrid: function() {
    	ComplaintWizard.Object._showDivResultList(false);
    	ComplaintWizard.Object._showDivResultGrid(true);
    	ComplaintWizard.Object._showLnkViewList(true);
    	ComplaintWizard.Object._showLnkViewGrid(false);
    }
	,getSearchTerm : function() {
		var term = {};

		term.docType = ComplaintWizard.Object._getValueSelDocType();
		term.subjectLastName = ComplaintWizard.Object._getValueEdtLastName();
		term.subjectSSN = ComplaintWizard.Object._getValueEdtSsn();
		term.eqipRequestNumber = ComplaintWizard.Object._getValueEdtEQipRequest();
		term.soi = ComplaintWizard.Object._getValueEdtSoi();
		term.son = ComplaintWizard.Object._getValueEdtSon();
		term.assignee = ComplaintWizard.Object._getValueSelAssignee();

		term.supervisorReviewFlag = ComplaintWizard.Object._isCheckedChkSupervisorReview();
		term.contractOversightReviewFlag = ComplaintWizard.Object._isCheckedChkContractOversight();

		term.queues = [{},{},{}];
		term.queues[0].name = Unassigned.queueProcessing.name;
		term.queues[0].checked = ComplaintWizard.Object.isCheckedChkProcessing();
		term.queues[1].name = Unassigned.queueQa.name;
		term.queues[1].checked = ComplaintWizard.Object.isCheckedChkQa();
		term.queues[2].name = Unassigned.queueMailback.name;
		term.queues[2].checked = ComplaintWizard.Object.isCheckedChkMailback();

		return term;
	}
    ,clearSearchTerm : function() {
        ComplaintWizard.Object._setValueSelDocType("placeholder");
        ComplaintWizard.Object._setValueEdtLastName("");
        ComplaintWizard.Object._setValueEdtSsn("");
        ComplaintWizard.Object._setValueEdtEQipRequest("");
        ComplaintWizard.Object._setValueEdtSoi("");
        ComplaintWizard.Object._setValueEdtSon("");
        ComplaintWizard.Object._setValueSelAssignee("placeholder");

        ComplaintWizard.Object._setCheckedChkSupervisorReview(false);
        ComplaintWizard.Object._setCheckedChkContractOversight(false);

        ComplaintWizard.Object._setCheckedChkProcessing(false);
        ComplaintWizard.Object._setCheckedChkQa(true);
        ComplaintWizard.Object._setCheckedChkMailback(false);
    }

    ,getSelectedQueueNames : function () {
    	var queueNames = "";
    	if (ComplaintWizard.Object.isCheckedChkProcessing()) {
    		queueNames += "," + Unassigned.queueProcessing.name;
    	}
    	if (ComplaintWizard.Object.isCheckedChkQa()) {
    		queueNames += "," + Unassigned.queueQa.name;
    	}
    	if (ComplaintWizard.Object.isCheckedChkMailback()) {
    		queueNames += "," + Unassigned.queueMailback.name;
    	}

    	if (Acm.Common.isNotEmpty(queueNames)) {
    		queueNames = queueNames.substring(1, queueNames.length); //discard extra leading ','
    	}
    	return queueNames;
    }
//    ,getSelectedQueue : function () {
//        if (ComplaintWizard.Object.isCheckedChkProcessing()) {
//            return Unassigned.queueProcessing;
//        } else if (ComplaintWizard.Object.isCheckedChkQa()) {
//            return Unassigned.queueQa;
//        } else if (ComplaintWizard.Object.isCheckedChkMailback()) {
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




