/**
 * eBrief customization
 *
 * @author jwu
 */
CaseFile.View.Ribbon = CaseFile.View.Ribbon || {
    create: function() {
        this.$labCaseNumber   = $("#caseNumber");
        this.$lnkCaseTitle    = $("#caseTitle");
        this.$lnkIncidentDate = $("#incident");
        this.$lnkPriority     = $("#priority");
        this.$lnkAssignee     = $("#assigned");
        this.$lnkGroup 		  = $("#group");
        this.$lnkSubjectType  = $("#type");
        this.$lnkDueDate      = $("#dueDate");
        this.$lnkStatus       = $("#status");


        AcmEx.Object.XEditable.useEditable(this.$lnkCaseTitle, {
            success: function(response, newValue) {
                CaseFile.Controller.viewChangedCaseTitle(CaseFile.View.getActiveCaseFileId(), newValue);
            }
        });
//            AcmEx.Object.XEditable.useEditableDate(this.$lnkIncidentDate, {
//                success: function(response, newValue) {
//                    CaseFile.Controller.viewChangedIncidentDate(CaseFile.View.getActiveCaseFileId(), newValue);
//                }
//            });
        AcmEx.Object.XEditable.useEditableDate(this.$lnkDueDate, {
            success: function(response, newValue) {
                CaseFile.Controller.viewChangedDueDate(CaseFile.View.getActiveCaseFileId(), newValue);
            }
        });

        Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
        Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);

        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_ASSIGNEES          ,this.onModelFoundAssignees);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_GROUPS         ,this.onModelRetrievedGroups);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_SUBJECT_TYPES      ,this.onModelFoundSubjectTypes);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_PRIORITIES         ,this.onModelFoundPriorities);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_CASE_TITLE         ,this.onModelSavedCaseTitle);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_INCIDENT_DATE      ,this.onModelSavedIncidentDate);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_ASSIGNEE           ,this.onModelSavedAssignee);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_GROUP	           ,this.onModelSavedGroup);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_SUBJECT_TYPE       ,this.onModelSavedSubjectType);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_PRIORITY           ,this.onModelSavedPriority);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_DUE_DATE           ,this.onModelSavedDueDate);

    }
    ,onInitialized: function() {
    }

    ,onViewSelectedObject: function(objType, objId) {
        var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
        CaseFile.View.Ribbon.populateCaseFile(objData);
    }
    ,onModelRetrievedObject: function(objData) {
        CaseFile.View.Ribbon.populateCaseFile(objData);
    }

    ,onModelFoundAssignees: function(assignees) {
        var choices = [];
        $.each(assignees, function(idx, val) {
            var opt = {};
            opt.value = val.userId;
            opt.text = val.fullName;
            choices.push(opt);
        });

        AcmEx.Object.XEditable.useEditable(CaseFile.View.Ribbon.$lnkAssignee, {
            source: choices
            ,success: function(response, newValue) {
                CaseFile.Controller.viewChangedAssignee(CaseFile.View.getActiveCaseFileId(), newValue);
            }
            ,currentValue: CaseFile.Model.Detail.getAssignee(CaseFile.View.getActiveCaseFile())
        });

        // This is happen after loading the object, for that reason we should check here as well.
        // We need both, assignees and groups for checking.
        // For this to be happened, assignees and groups should be loaded. If in this stage
        // assignees or groups are not loaded, checking for assignees and groups will be skipped.
        CaseFile.View.DetailNote.populateRestriction(CaseFile.View.getActiveCaseFile());
    }
    ,onModelRetrievedGroups: function(groups) {
        var choices = [];
        $.each(groups, function(idx, val) {
            var opt = {};
            opt.value = val.object_id_s;
            opt.text = val.name;
            choices.push(opt);
        });

        AcmEx.Object.XEditable.useEditable(CaseFile.View.Ribbon.$lnkGroup, {
            source: choices
            ,success: function(response, newValue) {
                CaseFile.Controller.viewChangedGroup(CaseFile.View.getActiveCaseFileId(), newValue);
            }
            ,currentValue: CaseFile.Model.Detail.getGroup(CaseFile.View.getActiveCaseFile())
        });

        // This is happen after loading the object, for that reason we should check here as well.
        // We need both, assignees and groups for checking.
        // For this to be happened, assignees and groups should be loaded. If in this stage
        // assignees or groups are not loaded, checking for assignees and groups will be skipped.
        CaseFile.View.DetailNote.populateRestriction(CaseFile.View.getActiveCaseFile());
    }
    ,onModelFoundSubjectTypes: function(subjectTypes) {
        var choices = [];
        $.each(subjectTypes, function(idx, val) {
            var opt = {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        AcmEx.Object.XEditable.useEditable(CaseFile.View.Ribbon.$lnkSubjectType, {
            source: choices
            ,success: function(response, newValue) {
                CaseFile.Controller.viewChangedSubjectType(CaseFile.View.getActiveCaseFileId(), newValue);
            }
        });
    }
    ,onModelFoundPriorities: function(priorities) {
        var choices = []; //[{value: "", text: "Choose Priority"}];
        $.each(priorities, function(idx, val) {
            var opt = {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        AcmEx.Object.XEditable.useEditable(CaseFile.View.Ribbon.$lnkPriority, {
            source: choices
            ,success: function(response, newValue) {
                CaseFile.Controller.viewChangedPriority(CaseFile.View.getActiveCaseFileId(), newValue);
            }
        });
    }
    ,onModelSavedCaseTitle: function(caseFileId, title) {
        if (title.hasError) {
            CaseFile.View.Ribbon.setTextLnkCaseTitle($.t("casefile:detail.error-value"));
        }
    }
    ,onModelSavedIncidentDate: function(caseFileId, incidentDate) {
        if (incidentDate.hasError) {
            CaseFile.View.Ribbon.setTextLnkIncidentDate($.t("casefile:detail.error-value"));
        }
    }
    ,onModelSavedAssignee: function(caseFileId, assginee) {
        if (assginee.hasError) {
            CaseFile.View.Ribbon.setTextLnkAssignee($.t("casefile:detail.error-value"));
        }
    }
    ,onModelSavedGroup: function(caseFileId, group) {
        if (group.hasError) {
            CaseFile.View.Ribbon.setTextLnkGroup($.t("casefile:detail.error-value"));
        }
    }
    ,onModelSavedSubjectType: function(caseFileId, subjectType) {
        if (subjectType.hasError) {
            CaseFile.View.Ribbon.setTextLnkSubjectType($.t("casefile:detail.error-value"));
        }
    }
    ,onModelSavedPriority: function(caseFileId, priority) {
        if (priority.hasError) {
            CaseFile.View.Ribbon.setTextLnkPriority($.t("casefile:detail.error-value"));
        }
    }
    ,onModelSavedDueDate: function(caseFileId, created) {
        if (created.hasError) {
            CaseFile.View.Ribbon.setTextLnkDueDate($.t("casefile:detail.error-value"));
        }
    }

    ,populateCaseFile: function(c) {
        if (CaseFile.Model.Detail.validateCaseFile(c)) {
            // DGM fixes... sorry about this bad code
            var displayTitle = Acm.goodValue(c.title) + " #" + Acm.goodValue(c.caseNumber) + " (" + Acm.goodValue(c.status) +")";
            this.setTextLabCaseNumber(Acm.goodValue(displayTitle));
            //this.setTextLabCaseNumber(Acm.goodValue(c.caseNumber));
            //this.setTextLnkCaseTitle(Acm.goodValue(c.title));
            this.setTextLnkIncidentDate(Acm.getDateFromDatetime(c.created));//c.incidentDate
            this.setTextLnkSubjectType(Acm.goodValue(c.caseType));
            this.setTextLnkPriority(Acm.goodValue(c.priority));
            this.setTextLnkDueDate(Acm.getDateFromDatetime(c.dueDate));
            // this.setTextLnkStatus("  (" + Acm.goodValue(c.status) +")");

            var assignee = CaseFile.Model.Detail.getAssignee(c);
            this.setTextLnkAssignee(Acm.goodValue(assignee));

            var group = CaseFile.Model.Detail.getGroup(c);
            this.setTextLnkGroup(Acm.goodValue(group));
        }
    }

    ,setTextLabCaseNumber: function(txt) {
        Acm.Object.setText(this.$labCaseNumber, txt);
    }
    ,setTextLnkCaseTitle: function(txt) {
        AcmEx.Object.XEditable.setValue(this.$lnkCaseTitle, txt);
    }
    ,setTextLnkIncidentDate: function(txt) {
        //AcmEx.Object.XEditable.setDate(this.$lnkIncidentDate, txt);
        Acm.Object.setText(this.$lnkIncidentDate, txt);
    }
    ,setTextLnkAssignee: function(txt) {
        AcmEx.Object.XEditable.setValue(this.$lnkAssignee, txt);
    }
    ,setTextLnkGroup: function(txt) {
        AcmEx.Object.XEditable.setValue(this.$lnkGroup, txt);
    }
    ,setTextLnkSubjectType: function(txt) {
        AcmEx.Object.XEditable.setValue(this.$lnkSubjectType, txt);
    }
    ,setTextLnkPriority: function(txt) {
        AcmEx.Object.XEditable.setValue(this.$lnkPriority, txt);
    }
    ,setTextLnkDueDate: function(txt) {
        AcmEx.Object.XEditable.setDate(this.$lnkDueDate, txt);
    }
    ,setTextLnkStatus: function(txt) {
        Acm.Object.setText(this.$lnkStatus, txt);
    }

};

