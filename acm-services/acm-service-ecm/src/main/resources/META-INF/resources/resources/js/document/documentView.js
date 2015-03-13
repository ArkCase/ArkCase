/**
 * Document.View
 *
 * @author jwu
 */
Document.View = Document.View || {
    create : function() {
        if (Document.View.MicroData.create)       {Document.View.MicroData.create();}
        if (Document.View.Navigator.create)       {Document.View.Navigator.create();}
        if (Document.View.Content.create)         {Document.View.Content.create();}
        if (Document.View.Action.create)          {Document.View.Action.create();}
        if (Document.View.Detail.create)          {Document.View.Detail.create();}
        if (Document.View.People.create)    	  {Document.View.People.create();}
        if (Document.View.Documents.create)       {Document.View.Documents.create();}
        if (Document.View.Participants.create)    {Document.View.Participants.create();}
        if (Document.View.Notes.create)           {Document.View.Notes.create();}
        if (Document.View.Tasks.create)           {Document.View.Tasks.create();}
        if (Document.View.References.create)      {Document.View.References.create();}
        if (Document.View.Events.create)          {Document.View.Events.create();}
        if (Document.View.Correspondence.create)  {Document.View.Correspondence.create();}
    }
    ,onInitialized: function() {
        if (Document.View.MicroData.onInitialized)      {Document.View.MicroData.onInitialized();}
        if (Document.View.Navigator.onInitialized)      {Document.View.Navigator.onInitialized();}
        if (Document.View.Content.onInitialized)        {Document.View.Content.onInitialized();}
        if (Document.View.Action.onInitialized)         {Document.View.Action.onInitialized();}
        if (Document.View.Detail.onInitialized)         {Document.View.Detail.onInitialized();}
        if (Document.View.People.onInitialized)         {Document.View.People.onInitialized();}
        if (Document.View.Documents.onInitialized)      {Document.View.Documents.onInitialized();}
        if (Document.View.Participants.onInitialized)   {Document.View.Participants.onInitialized();}
        if (Document.View.Notes.onInitialized)          {Document.View.Notes.onInitialized();}
        if (Document.View.Tasks.onInitialized)          {Document.View.Tasks.onInitialized();}
        if (Document.View.References.onInitialized)     {Document.View.References.onInitialized();}
        if (Document.View.Events.onInitialized)         {Document.View.Events.onInitialized();}
        if (Document.View.Correspondence.onInitialized) {Document.View.Correspondence.onInitialized();}
    }

    ,getActiveDocumentId: function() {
        return ObjNav.View.Navigator.getActiveObjId();
    }
    ,getActiveDocument: function() {
        var objId = ObjNav.View.Navigator.getActiveObjId();
        var caseFile = null;
        if (Acm.isNotEmpty(objId)) {
            caseFile = ObjNav.Model.Detail.getCacheObject(Document.Model.DOC_TYPE_CASE_FILE, objId);
        }
        return caseFile;
    }

    ,MicroData: {
        create : function() {
            //this.objId      = Acm.Object.MicroData.get("objId");
            this.treeFilter = Acm.Object.MicroData.getJson("treeFilter");
            this.treeSort   = Acm.Object.MicroData.getJson("treeSort");
            this.token      = Acm.Object.MicroData.get("token");
            
            this.formUrls = {}; //new Object();
            this.formUrls["edit_case_file"]            = Acm.Object.MicroData.get("urlEditDocumentForm");
            this.formUrls["reinvestigate_case_file"]   = Acm.Object.MicroData.get("urlReinvestigateDocumentForm");
            this.formUrls["roiFormUrl"]                = Acm.Object.MicroData.get("urlRoiForm");
            this.formUrls["electronicCommunicationFormUrl"]  = Acm.Object.MicroData.get("urlElectronicCommunicationForm");
            this.formUrls["enable_frevvo_form_engine"] = Acm.Object.MicroData.get("enableFrevvoFormEngine");
            this.formUrls["change_case_status"]        = Acm.Object.MicroData.get("urlChangeCaseStatusForm");
            this.formUrls["edit_change_case_status"]   = Acm.Object.MicroData.get("urlEditChangeCaseStatusForm");
        }
        ,onInitialized: function() {
        }

        ,getToken: function() {
            return this.token;
        }
        ,getFormUrls: function(){
        	return this.formUrls;
        }
    }

    ,Navigator: {
        create: function() {
            this.$ulFilter = $("#ulFilter");
            this.$ulSort   = $("#ulSort");
            this.$tree     = $("#tree");

            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_CASE_TITLE       , this.onViewChangedCaseTitle);
//            if ("undefined" != typeof Topbar) {
//                Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_SET_ASN_DATA       , this.onTopbarViewSetAsnData, Acm.Dispatcher.PRIORITY_HIGH);
//            }
        }
        ,onInitialized: function() {
        }

        ,onViewChangedCaseTitle: function(caseFileId, title) {
            var caseFileSolr = ObjNav.Model.List.getSolrObject(Document.Model.DOC_TYPE_CASE_FILE, caseFileId);
            if (ObjNav.Model.List.validateObjSolr(caseFileSolr)) {
                caseFileSolr.title_parseable = Acm.goodValue(title);
                ObjNav.View.Navigator.updateObjNode(Document.Model.DOC_TYPE_CASE_FILE, caseFileId);
            }
        }
//        ,onTopbarViewSetAsnData: function(asnData) {
//            if (AcmEx.Model.Tree.Config.validateTreeInfo(asnData)) {
//                if (0 == asnData.name.indexOf("/plugin/casefile")) {
//                    var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
//                    if (AcmEx.Model.Tree.Config.sameResultSet(asnData)) {
//                        if (asnData.key) {
//                            var key = Document.Model.Tree.Key.getKeyBySubWithPage(asnData.start, asnData.key);
//                            AcmEx.Object.Tree.refreshTree(key);
//                        }
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }

        ,getTreeArgs: function() {
            return {
                lazyLoad: function(event, data) {
                    Document.View.Navigator.lazyLoad(event, data);
                }
                ,getContextMenu: function(node) {
                    Document.View.Navigator.getContextMenu(node);
                }
            };
        }
        ,lazyLoad: function(event, data) {
            var key = data.node.key;
            var nodeType = ObjNav.Model.Tree.Key.getNodeTypeByKey(key);
            switch (nodeType) {
                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, Document.Model.DOC_TYPE_CASE_FILE]):
                    data.result = AcmEx.FancyTreeBuilder
                        .reset()
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Document.Model.Tree.Key.NODE_TYPE_PART_DETAILS
                            ,title: "Details"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Document.Model.Tree.Key.NODE_TYPE_PART_PEOPLE
                            ,title: "People"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Document.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS
                            ,title: "Documents"
//                            ,folder: true
//                            ,lazy: true
//                            ,cache: false
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Document.Model.Tree.Key.NODE_TYPE_PART_PARTICIPANTS
                            ,title: "Participants"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Document.Model.Tree.Key.NODE_TYPE_PART_NOTES
                            ,title: "Notes"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Document.Model.Tree.Key.NODE_TYPE_PART_TASKS
                            ,title: "Tasks"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Document.Model.Tree.Key.NODE_TYPE_PART_REFERENCES
                            ,title: "References"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Document.Model.Tree.Key.NODE_TYPE_PART_HISTORY
                            ,title: "History"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Document.Model.Tree.Key.NODE_TYPE_PART_TEMPLATES
                            ,title: "Correspondence"
                        })
                        .getTree();

                    break;

                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, Document.Model.DOC_TYPE_CASE_FILE, Document.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS]):
                    var caseFileId = ObjNav.Model.Tree.Key.getObjIdByKey(key);
                    var c = ObjNav.Model.Detail.getCacheObject(Document.Model.DOC_TYPE_CASE_FILE, caseFileId);
                    if (c) {
                        data.result = [
                            {key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.1", title: "Document1" + "[Status]"}
                            ,{key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.2", title: "Doc2" + "[Status]"}
                        ];
                    } else {
                        data.result = ObjNav.Service.Detail.retrieveObjectDeferred(Document.Model.DOC_TYPE_CASE_FILE, caseFileId
                            ,function(response) {
                                var z = 1;

                                var resultFake = [
                                    {key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.3", title: "Document3" + "[Status]"}
                                    ,{key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.4", title: "Doc4" + "[Status]"}
                                ];
                                return resultFake;
                            }
                        );

                    }

                    break;

                default:
                    data.result = [];
                    break;
            }
        }

        ,getContextMenu: function(node) {
            var key = node.key;
            var menu = [
                {title: "Menu:" + key, cmd: "cut", uiIcon: "ui-icon-scissors"},
                {title: "Copy", cmd: "copy", uiIcon: "ui-icon-copy"},
                {title: "Paste", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: false },
                {title: "----"},
                {title: "Edit", cmd: "edit", uiIcon: "ui-icon-pencil", disabled: true },
                {title: "Delete", cmd: "delete", uiIcon: "ui-icon-trash", disabled: true },
                {title: "More", children: [
                    {title: "Sub 1", cmd: "sub1"},
                    {title: "Sub 2", cmd: "sub1"}
                ]}
            ];
            return menu;
        }
    }

    ,Content: {
        create : function() {
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT_ERROR    ,this.onModelRetrievedObjectError);
        }
        ,onInitialized: function() {
        }
        ,onModelRetrievedObjectError: function(error) {
            Acm.Dialog.error(Acm.goodValue(error.errMsg, "Error occurred for retrieving casefile data"));
        }
    }

    ,Action: {
        create: function() {
            this.$olMilestoneTrack          = $(".track-progress");
            this.$dlgChangeCaseStatus      = $("#changeCaseStatus");
            this.$dlgConsolidateCase       = $("#consolidateCase");
            this.$edtConsolidateCase       = $("#edtConsolidateCase");
            this.$btnEditDocument    	   = $("#tabTitle button[data-title='Edit Case File']");
            this.$btnChangeCaseStatus      = $("#tabTitle button[data-title='Change Case Status']");
            this.$btnConsolidateCase       = $("#tabTitle button[data-title='Consolidate Case']");
            this.$btnReinvestigateDocument = $("#tabTitle button[data-title='Reinvestigate Case File']");
            this.$btnEditDocument   	  .on("click", function(e) {Document.View.Action.onClickBtnEditDocument(e, this);});
            this.$btnChangeCaseStatus     .on("click", function(e) {Document.View.Action.onClickBtnChangeCaseStatus(e, this);});
            this.$btnConsolidateCase      .on("click", function(e) {Document.View.Action.onClickBtnConsolidateCase(e, this);});
            this.$btnReinvestigateDocument.on("click", function(e) {Document.View.Action.onClickBtnReinvestigateDocument(e, this);});

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT         ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onClickBtnEditDocument: function(event, ctrl) {
        	var urlEditDocumentForm = Document.View.MicroData.getFormUrls()['edit_case_file'];
        	var caseFileId = Document.View.getActiveDocumentId();
            var c = Document.View.getActiveDocument();
            if (Acm.isNotEmpty(urlEditDocumentForm) && Acm.isNotEmpty(c)) {
            	var xmlId = '';
            	var pdfId = '';
            	if (Acm.isNotEmpty(c.childObjects) && c.childObjects.length > 0) {
            		for (var i = 0; i < c.childObjects.length; i++) {
            			var child = c.childObjects[i];
            			
            			if (child.targetType != null && child.targetType == 'FILE' && 
            			    child.targetName != null && child.targetName.indexOf('form_case_file_') == 0 &&
            			    child.targetName.substr(-4) == '.xml') 
            			{
            				xmlId = child.targetId;
            			}
            			
            			if (child.targetType != null && child.targetType == 'FILE' && 
            				child.targetName != null && child.targetName.indexOf('Case_File_') == 0&&
            			    child.targetName.substr(-4) == '.pdf') 
            			{
            				pdfId = child.targetId;
            			}
            		}
            	}
            	
            	urlEditDocumentForm = urlEditDocumentForm.replace("/embed?", "/popupform?");
            	urlEditDocumentForm = urlEditDocumentForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',mode:'edit',xmlId:'" + xmlId + "',pdfId:'" + pdfId + "',");
            	Acm.Dialog.openWindow(urlEditDocumentForm, "", 860, 700
                    ,function() {
                        Document.Controller.viewChangedDocument(caseFileId);
                    }
                );
            }
        }
        
        ,onClickBtnChangeCaseStatus: function() {
            Document.View.Action.showDlgChangeCaseStatus(function(event, ctrl){
                var urlChangeCaseStatusForm = Document.View.MicroData.getFormUrls()['change_case_status'];
                var caseFileId = Document.View.getActiveDocumentId();
                //var objType = ObjNav.View.Navigator.getActiveObjType();
                //var c = ObjNav.Model.Detail.getCacheObject(objType, caseFileId);
                var c = Document.View.getActiveDocument();
                if (Acm.isNotEmpty(urlChangeCaseStatusForm) && Acm.isNotEmpty(c)) {
                    if (Acm.isNotEmpty(c.caseNumber)) {
                        urlChangeCaseStatusForm = urlChangeCaseStatusForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',");
                        Acm.Dialog.openWindow(urlChangeCaseStatusForm, "", 860, 700
                            ,function() {
                                Document.Controller.viewClosedDocument(caseFileId);
                            }
                        );
                    }
                }
            });
        }

        //---- demo how to use object picker ----
        ,onPickObjectDemo: function() {
            SearchBase.showSearchDialog({name: "demoDialog"
                ,title: "My Dialog Title"
                ,prompt: "Enter to search Case or Task"
                ,btnGoText: "Search Now!"
                ,btnOkText: "Select"
                ,btnCancelText: "Away"
                ,filters: [{key: "Object Type", values: ["CASE_FILE", "TASK"]}]
                ,onClickBtnPrimary : function(event, ctrl) {
                    SearchBase.View.Results.getSelectedRows().each(function () {
                        var record = $(this).data('record');

                        var z = 1;
                        alert("ok");
                    });
                }
                ,onClickBtnDefault : function(event, ctrl) {
                    alert("cancel");
                }
            });
        }
        //---------------------------------------

        ,onClickBtnConsolidateCase: function() {
//borrow it to test object picker dialog
//            this.onPickObjectDemo();
//            return;

            Document.View.Action.setValueEdtConsolidateCase("");
            Document.View.Action.showDlgConsolidateCase(function(event, ctrl) {
                var caseNumber = Document.View.Action.getValueEdtConsolidateCase();
                alert("Consolidate case:" + caseNumber);
            });
        }
        ,onClickBtnReinvestigateDocument: function() {
        	var urlReinvestigateDocumentForm = Document.View.MicroData.getFormUrls()['reinvestigate_case_file'];
        	var caseFileId = Document.View.getActiveDocumentId();
            var c = Document.View.getActiveDocument();
            if (Acm.isNotEmpty(urlReinvestigateDocumentForm) && Acm.isNotEmpty(c)) {
            	var xmlId = '';
            	if (Acm.isNotEmpty(c.childObjects) && c.childObjects.length > 0) {
            		for (var i = 0; i < c.childObjects.length; i++) {
            			var child = c.childObjects[i];
            			
            			if (child.targetType != null && child.targetType == 'FILE' && 
            			    child.targetName != null && child.targetName.indexOf('form_case_file_') == 0 &&
            			    child.targetName.substr(-4) == '.xml') 
            			{
            				xmlId = child.targetId;
            			}
            		}
            	}
            	urlReinvestigateDocumentForm = urlReinvestigateDocumentForm.replace("/embed?", "/popupform?");
            	urlReinvestigateDocumentForm = urlReinvestigateDocumentForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',mode:'reinvestigate',xmlId:'" + xmlId + "',");
            	Acm.Dialog.openWindow(urlReinvestigateDocumentForm, "", 860, 700
                    ,function() {
            			// TODO: When James will find solution, we should change this
            			window.location.href = App.getContextPath() + '/plugin/casefile';
                    }
                );
            }
        }

        ,onModelRetrievedObject: function(objData) {
                Document.View.Action.populate(objData);
        }
        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            Document.View.Action.populate(objData);
            SubscriptionOp.Model.checkSubscription(App.getUserName(), objType, objId);
        }

        ,populate: function(caseFile) {
            if (Document.Model.Detail.validateDocument(caseFile)) {
                Document.View.Action.showBtnChangeCaseStatus(Acm.goodValue(caseFile.changeCaseStatus, true));
                //Comment out temporarily
                //Document.View.Action.showMilestone(Acm.goodValue(caseFile.milestones));
            }
        }
        ,showMilestone: function(milestones) {
            var achievedMilestones = [];
            for ( var m = 0; m < milestones.length; m++ )
            {
                achievedMilestones.push(milestones[m].milestoneName);
            }

            var allMilestones = ["Initiated", "Waiver", "Adjudication", "Issued", "Closed"];

            var html = "";
            for ( var i = 0; i < allMilestones.length; i++ )
            {
                html += "<li";
                for ( var j = 0; j < achievedMilestones.length; j++ )
                {
                    if ( achievedMilestones[j] === allMilestones[i] )
                    {
                        html += " class='done'";
                        break;
                    }
                }
                html += "><span>" + allMilestones[i] + "</span><i></i></li>\r";
            }
            this.setHtmlOlMilestoneTracker(html);
            this.setAttrOlMilestoneTracker("data-steps", allMilestones.length);
        }
        ,showDlgChangeCaseStatus: function(onClickBtnPrimary) {
            Acm.Dialog.modal(this.$dlgChangeCaseStatus, onClickBtnPrimary);
        }
        ,showDlgConsolidateCase: function(onClickBtnPrimary) {
            Acm.Dialog.modal(this.$dlgConsolidateCase, onClickBtnPrimary);
        }
        ,getValueEdtConsolidateCase: function() {
            return Acm.Object.getValue(this.$edtConsolidateCase);
        }
        ,setValueEdtConsolidateCase: function(val) {
            Acm.Object.setValue(this.$edtConsolidateCase, val);
        }
        ,showBtnChangeCaseStatus: function(show) {
            Acm.Object.show(this.$btnChangeCaseStatus, show);
        }
        ,setHtmlOlMilestoneTracker: function(html) {
            Acm.Object.setHtml(this.$olMilestoneTrack, html);
        }
        ,setAttrOlMilestoneTracker: function(name, value) {
            this.$olMilestoneTrack.attr(name, value);
        }
    }

    ,Detail: {
        create: function() {
            this.$divDetail       = $(".divDetail");
            this.$btnEditDetail   = $("#tabDetail button:eq(0)");
            this.$btnSaveDetail   = $("#tabDetail button:eq(1)");
            this.$btnEditDetail.on("click", function(e) {Document.View.Detail.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {Document.View.Detail.onClickBtnSaveDetail(e, this);});

            this.$labCaseNumber   = $("#caseNumber");
            this.$lnkCaseTitle    = $("#caseTitle");
            this.$lnkIncidentDate = $("#incident");
            this.$lnkPriority     = $("#priority");
            this.$lnkAssignee     = $("#assigned");
            this.$lnkSubjectType  = $("#type");
            this.$lnkDueDate      = $("#dueDate");
            this.$lnkStatus       = $("#status");

            this.$chkRestrict     = $("#restrict");
            this.$chkRestrict.on("click", function(e) {Document.View.Detail.onClickRestrictCheckbox(e, this);});


            AcmEx.Object.XEditable.useEditable(this.$lnkCaseTitle, {
                success: function(response, newValue) {
                    Document.Controller.viewChangedCaseTitle(Document.View.getActiveDocumentId(), newValue);
                }
            });
//            AcmEx.Object.XEditable.useEditableDate(this.$lnkIncidentDate, {
//                success: function(response, newValue) {
//                    Document.Controller.viewChangedIncidentDate(Document.View.getActiveDocumentId(), newValue);
//                }
//            });
            AcmEx.Object.XEditable.useEditableDate(this.$lnkDueDate, {
                success: function(response, newValue) {
                    Document.Controller.viewChangedDueDate(Document.View.getActiveDocumentId(), newValue);
                }
            });


            //Acm.Dispatcher.addEventListener(Document.Controller.MODEL_SAVED_CASE_FILE          ,this.onModelSavedDocument);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_FOUND_ASSIGNEES          ,this.onModelFoundAssignees);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_FOUND_SUBJECT_TYPES      ,this.onModelFoundSubjectTypes);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_FOUND_PRIORITIES         ,this.onModelFoundPriorities);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_SAVED_CASE_TITLE         ,this.onModelSavedCaseTitle);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_SAVED_INCIDENT_DATE      ,this.onModelSavedIncidentDate);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_SAVED_ASSIGNEE           ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_SAVED_SUBJECT_TYPE       ,this.onModelSavedSubjectType);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_SAVED_PRIORITY           ,this.onModelSavedPriority);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_SAVED_DUE_DATE           ,this.onModelSavedDueDate);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_SAVED_DETAIL             ,this.onModelSavedDetail);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT_ERROR     ,this.onModelRetrievedObjectError);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }


        ,onModelFoundAssignees: function(assignees) {
            var choices = [];
            $.each(assignees, function(idx, val) {
                var opt = {};
                opt.value = val.userId;
                opt.text = val.fullName;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(Document.View.Detail.$lnkAssignee, {
                source: choices
                ,success: function(response, newValue) {
                    Document.Controller.viewChangedAssignee(Document.View.getActiveDocumentId(), newValue);
                }
            });
        }
        ,onModelFoundSubjectTypes: function(subjectTypes) {
            var choices = [];
            $.each(subjectTypes, function(idx, val) {
                var opt = {};
                opt.value = val;
                opt.text = val;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(Document.View.Detail.$lnkSubjectType, {
                source: choices
                ,success: function(response, newValue) {
                    Document.Controller.viewChangedSubjectType(Document.View.getActiveDocumentId(), newValue);
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

            AcmEx.Object.XEditable.useEditable(Document.View.Detail.$lnkPriority, {
                source: choices
                ,success: function(response, newValue) {
                    Document.Controller.viewChangedPriority(Document.View.getActiveDocumentId(), newValue);
                }
            });
        }


        ,onModelRetrievedObject: function(objData) {
            Document.View.Detail.populateDocument(objData);
        }
        ,onModelSavedCaseTitle: function(caseFileId, title) {
            if (title.hasError) {
                Document.View.Detail.setTextLnkCaseTitle("(Error)");
            }
        }
        ,onModelSavedIncidentDate: function(caseFileId, incidentDate) {
            if (incidentDate.hasError) {
                Document.View.Detail.setTextLnkIncidentDate("(Error)");
            }
        }
        ,onModelSavedAssignee: function(caseFileId, assginee) {
            if (assginee.hasError) {
                Document.View.Detail.setTextLnkAssignee("(Error)");
            }
        }
        ,onModelSavedSubjectType: function(caseFileId, subjectType) {
            if (subjectType.hasError) {
                Document.View.Detail.setTextLnkSubjectType("(Error)");
            }
        }
        ,onModelSavedPriority: function(caseFileId, priority) {
            if (priority.hasError) {
                Document.View.Detail.setTextLnkPriority("(Error)");
            }
        }
        ,onModelSavedDueDate: function(caseFileId, created) {
            if (created.hasError) {
                Document.View.Detail.setTextLnkDueDate("(Error)");
            }
        }
        ,onModelSavedDetail: function(caseFileId, details) {
            if (details.hasError) {
                Document.View.Detail.setHtmlDivDetail("(Error)");
            }
        }

        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            Document.View.Detail.populateDocument(objData);
        }

        ,DIRTY_EDITING_DETAIL: "Editing case detail"
        ,onClickBtnEditDetail: function(event, ctrl) {
            App.Object.Dirty.declare(Document.View.Detail.DIRTY_EDITING_DETAIL);
            Document.View.Detail.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = Document.View.Detail.saveDivDetail();
            Document.Controller.viewChangedDetail(Document.View.getActiveDocumentId(), htmlDetail);
            App.Object.Dirty.clear(Document.View.Detail.DIRTY_EDITING_DETAIL);
        }
        ,onClickRestrictCheckbox: function(event,ctrl){
            var restriction = ($(ctrl).prop('checked')) ? true : false;
            Document.Controller.viewClickedRestrictCheckbox(Document.View.getActiveDocumentId(),restriction);
        }

        ,populateDocument: function(c) {
            if (Document.Model.Detail.validateDocument(c)) {
                this.setTextLabCaseNumber(Acm.goodValue(c.caseNumber));
                this.setTextLnkCaseTitle(Acm.goodValue(c.title));
                this.setTextLnkIncidentDate(Acm.getDateFromDatetime(c.created));//c.incidentDate
                this.setTextLnkSubjectType(Acm.goodValue(c.caseType));
                this.setTextLnkPriority(Acm.goodValue(c.priority));
                this.setTextLnkDueDate(Acm.getDateFromDatetime(c.dueDate));
                this.setTextLnkStatus(Acm.goodValue(c.status));
                this.setPropertyRestricted(Acm.goodValue(c.restricted));
                this.setHtmlDivDetail(Acm.goodValue(c.details));

                var assignee = Document.Model.Detail.getAssignee(c);
                this.setTextLnkAssignee(Acm.goodValue(assignee));
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
        ,setPropertyRestricted: function(restriction){
            this.$chkRestrict.prop('checked', restriction);
        }
        ,getHtmlDivDetail: function() {
            return AcmEx.Object.SummerNote.get(this.$divDetail);
        }
        ,setHtmlDivDetail: function(html) {
            AcmEx.Object.SummerNote.set(this.$divDetail, html);
        }
        ,editDivDetail: function() {
            AcmEx.Object.SummerNote.edit(this.$divDetail);
        }
        ,saveDivDetail: function() {
            return AcmEx.Object.SummerNote.save(this.$divDetail);
        }
//        ,showChangeCaseStatusButton: function() {
//        	if (Document.View.Action.$btnChangeCaseStatus) {
//        		Document.View.Action.$btnChangeCaseStatus.show();
//        	}
//        }
//        ,hideChangeCaseStatusButton: function() {
//        	if (Document.View.Action.$btnChangeCaseStatus) {
//        		Document.View.Action.$btnChangeCaseStatus.hide();
//        	}
//        }
    }
    
    ,People: {
        create: function() {
            this.$divPeople = $("#divPeople");
            this.createJTable(this.$divPeople);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT              ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT                ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_ADDED_PERSON_ASSOCIATION    ,this.onModelAddedPersonAssociation);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_UPDATED_PERSON_ASSOCIATION  ,this.onModelUpdatedPersonAssociation);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_DELETED_PERSON_ASSOCIATION  ,this.onModelDeletedPersonAssociation);

            if (this.ContactMethods.create)     {this.ContactMethods.create();}
            if (this.SecurityTags.create)       {this.SecurityTags.create();}
            if (this.Organizations.create)      {this.Organizations.create();}
            if (this.Addresses.create)          {this.Addresses.create();}
            if (this.Aliases.create)            {this.Aliases.create();}
        }
        ,onInitialized: function() {
            if (Document.View.People.ContactMethods.onInitialized)     {Document.View.People.ContactMethods.onInitialized();}
            if (Document.View.People.SecurityTags.onInitialized)       {Document.View.People.SecurityTags.onInitialized();}
            if (Document.View.People.Organizations.onInitialized)      {Document.View.People.Organizations.onInitialized();}
            if (Document.View.People.Addresses.onInitialized)          {Document.View.People.Addresses.onInitialized();}
            if (Document.View.People.Aliases.onInitialized)            {Document.View.People.Aliases.onInitialized();}
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Document.View.People.$divPeople);
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Document.View.People.$divPeople);
        }
        ,onModelAddedPersonAssociation: function(personAssociation) {
            if (personAssociation.hasError) {
                AcmEx.Object.JTable.load(Document.View.People.$divPeople);
            }
        }
        ,onModelUpdatedPersonAssociation: function(personAssociation) {
            if (personAssociation.hasError) {
                AcmEx.Object.JTable.load(Document.View.People.$divPeople);
            }
        }
        ,onModelDeletedPersonAssociation: function(personAssociationId) {
            if (personAssociationId.hasError) {
                AcmEx.Object.JTable.load(Document.View.People.$divPeople);
            }
        }

        ,createJTable: function($s) {
            AcmEx.Object.JTable.useChildTable($s
                ,[
                    Document.View.People.ContactMethods.createLink
                    ,Document.View.People.Organizations.createLink
                    ,Document.View.People.Addresses.createLink
                    ,Document.View.People.Aliases.createLink
                ]
                ,{
                    title: 'People'
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: 'Add Person'
                    }
                    ,actions: {
                        listAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            //var caseFileId = Document.View.getActiveDocumentId();
                            var c = Document.View.getActiveDocument();
                            if (Document.Model.Detail.validateDocument(c)) {
                                var personAssociations = c.personAssociations;
                                for (var i = 0; i < personAssociations.length; i++) {
                                    if (Document.Model.People.validatePersonAssociation(personAssociations[i])) {
                                        rc.Records.push({
                                            assocId:     personAssociations[i].id
                                            ,title:      personAssociations[i].person.title
                                            ,givenName:  personAssociations[i].person.givenName
                                            ,familyName: personAssociations[i].person.familyName
                                            ,personType: personAssociations[i].personType
                                        });
                                    }
                                }
                                rc.TotalRecordCount = rc.Records.length;
                            }
                            return rc;
    //                        return {
    //	                          "Result": "OK"&& c.originator
    //	                          ,"Records": [
    //	                              {"id": 11, "title": "Mr", "givenName": "Some Name 1", "familyName": "Some Second Name 1", "personType": "Initiator"}
    //	                              ,{"id": 12, "title": "Mrs", "givenName": "Some Name 2", "familyName": "Some Second Name 2", "personType": "Complaintant"}
    //	                          ]
    //	                          ,"TotalRecordCount": 2
    //	                      };
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            rc.Record.title = record.title;
                            rc.Record.givenName = record.givenName;
                            rc.Record.familyName = record.familyName;
                            rc.Record.personType = record.personType;
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            rc.Record.title = record.title;
                            rc.Record.givenName = record.givenName;
                            rc.Record.familyName = record.familyName;
                            rc.Record.personType = record.personType;
                            return rc;
                        }
                        ,deleteAction: function(postData, jtParams) {
                            return {
                               "Result": "OK"
                            };
                        }
                    }
                    ,fields: {
                        assocId: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }
                        ,title: {
                            title: 'Title'
                            ,width: '10%'
                            ,options: Document.Model.Lookup.getPersonTitles()
                        }
                        ,givenName: {
                            title: 'First Name'
                            ,width: '15%'
                        }
                        ,familyName: {
                            title: 'Last Name'
                            ,width: '15%'
                        }
                        ,personType: {
                            title: 'Type'
                            ,options: Document.Model.Lookup.getPersonTypes()
                        }
                    }
                    ,recordAdded: function(event, data){
                        var record = data.record;
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId) {
                            var pa = {};
                            pa.personType = record.personType;
                            //pa.personDescription = record.personDescription;
                            pa.person = {};
                            pa.person.title = record.title;
                            pa.person.givenName = record.givenName;
                            pa.person.familyName = record.familyName;
                            Document.Controller.viewAddedPersonAssociation(caseFileId, pa);
                        }
                     }

                    ,recordUpdated: function(event, data){
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var assocId = record.assocId;
                        var caseFileId = Document.View.getActiveDocumentId();
                        var c = Document.View.getActiveDocument();
                        if (Document.Model.Detail.validateDocument(c)) {
                            if (c.personAssociations.length > whichRow) {
                                var pa = c.personAssociations[whichRow];
                                if (Document.Model.People.validatePersonAssociation(pa)) {
                                    pa.person.title = record.title;
                                    pa.person.givenName = record.givenName;
                                    pa.person.familyName = record.familyName;
                                    pa.personType = record.personType;
                                    Document.Controller.viewUpdatedPersonAssociation(caseFileId, pa);
                                }
                            }
                        }
                    }
                    ,recordDeleted: function(event,data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var personAssociationId = record.assocId;
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < personAssociationId) {
                            Document.Controller.viewDeletedPersonAssociation(caseFileId, personAssociationId);
                        }
                    }
                }
            );
        }


        ,_commonTypeValueRecord: function ($row, postData) {
            var rc = AcmEx.Object.jTableGetEmptyRecord();
            var recordParent = $row.closest('tr').data('record');
            if (recordParent && recordParent.assocId) {
                var assocId = recordParent.assocId;
                var record = Acm.urlToJson(postData);
                rc.Record.assocId = assocId;
                rc.Record.type = Acm.goodValue(record.type);
                rc.Record.value = Acm.goodValue(record.value);
                rc.Record.created = Acm.getCurrentDay(); //record.created;
                rc.Record.creator = App.getUserName();   //record.creator;
            }
            return rc;
        }

        ,ContactMethods: {
            create: function() {
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_ADDED_CONTACT_METHOD        ,this.onModelAddedContactMethod);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_UPDATED_CONTACT_METHOD      ,this.onModelUpdatedContactMethod);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_DELETED_CONTACT_METHOD      ,this.onModelDeletedContactMethod);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedContactMethod: function(contactMethod) {
                if (contactMethod.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedContactMethod: function(contactMethod) {
                if (contactMethod.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedContactMethod: function(contactMethodId) {
                if (contactMethodId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='Communication Devices'><i class='fa fa-phone'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Document.View.People.ContactMethods.onOpen, Document.Model.Lookup.PERSON_SUBTABLE_TITLE_CONTACT_METHODS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Document.Model.Lookup.PERSON_SUBTABLE_TITLE_CONTACT_METHODS
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: 'Add Device'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                //var caseFileId = Document.View.getActiveDocumentId();
                                var c = Document.View.getActiveDocument();
                                if (Document.Model.Detail.validateDocument(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Document.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                                        var contactMethods = personAssociation.person.contactMethods;
                                        for (var i = 0; i < contactMethods.length; i++) {
                                            rc.Records.push({
                                                assocId  : assocId
                                                ,id      : Acm.goodValue(contactMethods[i].id, 0)
                                                ,type    : Acm.goodValue(contactMethods[i].type)
                                                ,value   : Acm.goodValue(contactMethods[i].value)
                                                ,created : Acm.getDateFromDatetime(contactMethods[i].created)
                                                ,creator : Acm.goodValue(contactMethods[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return Document.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return Document.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,deleteAction: function (postData, jtParams) {
                            return {"Result": "OK"};
                        }
                    }
                    ,fields: {
                        assocId: {
                            type: 'hidden'
                            ,defaultValue: 0
                        }
                        , id: {
                            key: true
                            ,create: false
                            ,edit: false
                            ,list: false
                        }
                        ,type: {
                            title: 'Type'
                            ,width: '15%'
                            ,options: Document.Model.Lookup.getContactMethodTypes()
                        }
                        ,value: {
                            title: 'Value'
                            ,width: '30%'
                        }
                        ,created: {
                            title: 'Date Added'
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: 'Added By'
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    ,recordAdded: function (event, data) {
                        //var recordParent = $row.closest('tr').data('record');
                        //if (recordParent && recordParent.assocId && 0 < caseFileId) {
                        //    var assocId = recordParent.assocId;
                        var record = data.record;
                        var contactMethod = {};
                        var assocId = record.assocId;
                        contactMethod.type  = Acm.goodValue(record.type);
                        contactMethod.value = Acm.goodValue(record.value);
                        contactMethod.created = Acm.getCurrentDayInternal();
                        contactMethod.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId) {
                            Document.Controller.viewAddedContactMethod(caseFileId, assocId, contactMethod);
                        }
                    }
                    ,recordUpdated: function (event, data) {
                        //var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        //var recordParent = $row.closest('tr').data('record');
                        //if (recordParent && recordParent.assocId && 0 < caseFileId) {
                        //    var assocId = recordParent.assocId;
                        var record = data.record;
                        var contactMethod = {};
                        var assocId = record.assocId;
                        contactMethod.id    = Acm.goodValue(record.id, 0);
                        contactMethod.type  = Acm.goodValue(record.type);
                        contactMethod.value = Acm.goodValue(record.value);
                        contactMethod.created = Acm.getCurrentDayInternal();
                        contactMethod.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < contactMethod.id) {
                            Document.Controller.viewUpdatedContactMethod(caseFileId, assocId, contactMethod);
                        }
                    }
                    ,recordDeleted: function (event, data) {
                        //var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var assocId = record.assocId;
                        var contactMethodId = Acm.goodValue(record.id, 0);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < contactMethodId) {
                            Document.Controller.viewDeletedContactMethod(caseFileId, assocId, contactMethodId);
                        }
                    }
                });
            }
        }

        ,SecurityTags: {
            create: function() {
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_ADDED_SECURITY_TAG        ,this.onModelAddedSecurityTag);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_UPDATED_SECURITY_TAG      ,this.onModelUpdatedSecurityTag);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_DELETED_SECURITY_TAG      ,this.onModelDeletedSecurityTag);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedSecurityTag: function(securityTag) {
                if (securityTag.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedSecurityTag: function(securityTag) {
                if (securityTag.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedSecurityTag: function(securityTagId) {
                if (securityTagId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-phone'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Document.View.People.ContactMethods.onOpen, Document.Model.Lookup.PERSON_SUBTABLE_TITLE_SECURITY_TAGS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Document.Model.Lookup.PERSON_SUBTABLE_TITLE_SECURITY_TAGS
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: 'Add Device'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                //var caseFileId = Document.View.getActiveDocumentId();
                                var c = Document.View.getActiveDocument();
                                if (Document.Model.Detail.validateDocument(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Document.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                                        var securityTags = personAssociation.person.securityTags;
                                        for (var i = 0; i < securityTags.length; i++) {
                                            rc.Records.push({
                                                assocId  : assocId
                                                ,id      : Acm.goodValue(securityTags[i].id, 0)
                                                ,type    : Acm.goodValue(securityTags[i].type)
                                                ,value   : Acm.goodValue(securityTags[i].value)
                                                ,created : Acm.getDateFromDatetime(securityTags[i].created)
                                                ,creator : Acm.goodValue(securityTags[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return Document.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return Document.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,deleteAction: function (postData, jtParams) {
                            return {"Result": "OK"};
                        }
                    }
                    ,fields: {
                        assocId: {
                            type: 'hidden'
                            ,defaultValue: 0
                        }
                        , id: {
                            key: true
                            ,create: false
                            ,edit: false
                            ,list: false
                        }
                        ,type: {
                            title: 'Type'
                            ,width: '15%'
                            ,options: Document.Model.Lookup.getSecurityTagTypes()
                        }
                        ,value: {
                            title: 'Value'
                            ,width: '30%'
                        }
                        ,created: {
                            title: 'Date Added'
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: 'Added By'
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    ,recordAdded: function (event, data) {
                        var record = data.record;
                        var securityTag = {};
                        var assocId = record.assocId;
                        securityTag.type  = Acm.goodValue(record.type);
                        securityTag.value = Acm.goodValue(record.value);
                        securityTag.created = Acm.getCurrentDayInternal();
                        securityTag.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId) {
                            Document.Controller.viewAddedSecurityTag(caseFileId, assocId, securityTag);
                        }
                    }
                    ,recordUpdated: function (event, data) {
                        var record = data.record;
                        var securityTag = {};
                        var assocId = record.assocId;
                        securityTag.id    = Acm.goodValue(record.id, 0);
                        securityTag.type  = Acm.goodValue(record.type);
                        securityTag.value = Acm.goodValue(record.value);
                        securityTag.created = Acm.getCurrentDayInternal();
                        securityTag.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < securityTag.id) {
                            Document.Controller.viewUpdatedSecurityTag(caseFileId, assocId, securityTag);
                        }
                    }
                    ,recordDeleted: function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var securityTagId = Acm.goodValue(record.id, 0);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < securityTagId) {
                            Document.Controller.viewDeletedSecurityTag(caseFileId, assocId, securityTagId);
                        }
                    }
                });
            }
        }

        ,Organizations: {
            create: function() {
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_ADDED_ORGANIZATION        ,this.onModelAddedOrganization);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_UPDATED_ORGANIZATION      ,this.onModelUpdatedOrganization);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_DELETED_ORGANIZATION      ,this.onModelDeletedOrganization);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedOrganization: function(organization) {
                if (organization.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedOrganization: function(organization) {
                if (organization.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedOrganization: function(organizationId) {
                if (organizationId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='Organizations'><i class='fa fa-book'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Document.View.People.Organizations.onOpen, Document.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Document.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: 'Add Organization'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                //var caseFileId = Document.View.getActiveDocumentId();
                                var c = Document.View.getActiveDocument();
                                if (Document.Model.Detail.validateDocument(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Document.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                                        var organizations = personAssociation.person.organizations;
                                        for (var i = 0; i < organizations.length; i++) {
                                            rc.Records.push({
                                                assocId  : assocId
                                                ,id      : Acm.goodValue(organizations[i].organizationId, 0)
                                                ,type    : Acm.goodValue(organizations[i].organizationType)
                                                ,value   : Acm.goodValue(organizations[i].organizationValue)
                                                ,created : Acm.getDateFromDatetime(organizations[i].created)
                                                ,creator : Acm.goodValue(organizations[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return Document.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return Document.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,deleteAction: function (postData, jtParams) {
                            return {"Result": "OK"};
                        }
                    }
                    , fields: {
                        assocId: {
                            type: 'hidden'
                            ,defaultValue: 0
                        }
                        , id: {
                            key: true
                            ,create: false
                            ,edit: false
                            ,list: false
                        }
                        , type: {
                            title: 'Type'
                            ,width: '15%'
                            ,options: Document.Model.Lookup.getOrganizationTypes()
                        }
                        , value: {
                            title: 'Value'
                            ,width: '30%'
                        }
                        , created: {
                            title: 'Date Added'
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                        }
                        , creator: {
                            title: 'Added By'
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    , recordAdded: function (event, data) {
                        var record = data.record;
                        var organization = {};
                        var assocId = record.assocId;
                        organization.organizationType  = Acm.goodValue(record.type);
                        organization.organizationValue = Acm.goodValue(record.value);
                        organization.created = Acm.getCurrentDayInternal();
                        organization.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId) {
                            Document.Controller.viewAddedOrganization(caseFileId, assocId, organization);
                        }
                    }
                    , recordUpdated: function (event, data) {
                        var record = data.record;
                        var organization = {};
                        var assocId = record.assocId;
                        organization.organizationId    = Acm.goodValue(record.id, 0);
                        organization.organizationType  = Acm.goodValue(record.type);
                        organization.organizationValue = Acm.goodValue(record.value);
                        organization.created = Acm.getCurrentDayInternal();
                        organization.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < organization.organizationId) {
                            Document.Controller.viewUpdatedOrganization(caseFileId, assocId, organization);
                        }
                    }
                    , recordDeleted: function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var organizationId = Acm.goodValue(record.id, 0);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < organizationId) {
                            Document.Controller.viewDeletedOrganization(caseFileId, assocId, organizationId);
                        }
                    }
                });
            }
        }

        ,Addresses: {
            create: function() {
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_ADDED_ADDRESS        ,this.onModelAddedAddress);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_UPDATED_ADDRESS      ,this.onModelUpdatedAddress);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_DELETED_ADDRESS      ,this.onModelDeletedAddress);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedAddress: function(address) {
                if (address.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedAddress: function(address) {
                if (address.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedAddress: function(addressId) {
                if (addressId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='Locations'><i class='fa fa-map-marker'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Document.View.People.Addresses.onOpen, Document.Model.Lookup.PERSON_SUBTABLE_TITLE_ADDRESSES);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Document.Model.Lookup.PERSON_SUBTABLE_TITLE_ADDRESSES
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: 'Add Location'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                //var caseFileId = Document.View.getActiveDocumentId();
                                var c = Document.View.getActiveDocument();
                                if (Document.Model.Detail.validateDocument(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Document.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                                        var addresses = personAssociation.person.addresses;
                                        for (var i = 0; i < addresses.length; i++) {
                                            rc.Records.push({
                                                assocId        : assocId
                                                ,id            : Acm.goodValue(addresses[i].id, 0)
                                                ,type          : Acm.goodValue(addresses[i].type)
                                                ,streetAddress : Acm.goodValue(addresses[i].streetAddress)
                                                ,city          : Acm.goodValue(addresses[i].city)
                                                ,state         : Acm.goodValue(addresses[i].state)
                                                ,zip           : Acm.goodValue(addresses[i].zip)
                                                ,country       : Acm.goodValue(addresses[i].country)
                                                ,created       : Acm.getDateFromDatetime(addresses[i].created)
                                                ,creator       : Acm.goodValue(addresses[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;
                                var record = Acm.urlToJson(postData);
                                rc.Record.assocId       = assocId;
                                rc.Record.type          = Acm.goodValue(record.type);
                                rc.Record.streetAddress = Acm.goodValue(record.streetAddress);
                                rc.Record.city          = Acm.goodValue(record.city);
                                rc.Record.state         = Acm.goodValue(record.state);
                                rc.Record.zip           = Acm.goodValue(record.zip);
                                rc.Record.country       = Acm.goodValue(record.country);
                                rc.Record.created       = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator       = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;
                                var record = Acm.urlToJson(postData);
                                rc.Record.assocId       = assocId;
                                rc.Record.type          = Acm.goodValue(record.type);
                                rc.Record.streetAddress = Acm.goodValue(record.streetAddress);
                                rc.Record.city          = Acm.goodValue(record.city);
                                rc.Record.state         = Acm.goodValue(record.state);
                                rc.Record.zip           = Acm.goodValue(record.zip);
                                rc.Record.country       = Acm.goodValue(record.country);
                                rc.Record.created       = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator       = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,deleteAction: function(postData, jtParams) {
                            return {"Result": "OK"};
                        }
                    }

                    ,fields: {
                        personId: {
                            type: 'hidden'
                            ,defaultValue: 1 //commData.record.StudentId
                        }
                        ,id: {
                            key: true
                            ,create: false
                            ,edit: false
                            ,list: false
                        }
                        ,type: {
                            title: 'Type'
                            ,width: '8%'
                            ,options: Document.Model.Lookup.getAddressTypes()
                        }
                        ,streetAddress: {
                            title: 'Address'
                            ,width: '20%'
                        }
                        ,city: {
                            title: 'City'
                            ,width: '10%'
                        }
                        ,state: {
                            title: 'State'
                            ,width: '8%'
                        }
                        ,zip: {
                            title: 'Zip'
                            ,width: '8%'
                        }
                        ,country: {
                            title: 'Country'
                            ,width: '8%'
                        }
                        ,created: {
                            title: 'Date Added'
                            ,width: '15%'
                            ,create: false
                            ,edit: false
                        }
                        ,creator: {
                            title: 'Added By'
                            ,width: '15%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    ,recordAdded : function (event, data) {
                        var record = data.record;
                        var address = {};
                        var assocId = record.assocId;
                        address.type          = Acm.goodValue(record.type);
                        address.streetAddress = Acm.goodValue(record.streetAddress);
                        address.city          = Acm.goodValue(record.city);
                        address.state         = Acm.goodValue(record.state);
                        address.zip           = Acm.goodValue(record.zip);
                        address.country       = Acm.goodValue(record.country);
                        address.created = Acm.getCurrentDayInternal();
                        address.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId) {
                            Document.Controller.viewAddedAddress(caseFileId, assocId, address);
                        }
                    }
                    ,recordUpdated : function (event, data) {
                        var record = data.record;
                        var address = {};
                        var assocId = record.assocId;
                        address.id            = Acm.goodValue(record.id, 0);
                        address.type          = Acm.goodValue(record.type);
                        address.streetAddress = Acm.goodValue(record.streetAddress);
                        address.city          = Acm.goodValue(record.city);
                        address.state         = Acm.goodValue(record.state);
                        address.zip           = Acm.goodValue(record.zip);
                        address.country       = Acm.goodValue(record.country);
                        address.created = Acm.getCurrentDayInternal();
                        address.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < address.id) {
                            Document.Controller.viewUpdatedAddress(caseFileId, assocId, address);
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var addressId  = Acm.goodValue(record.id, 0);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < addressId) {
                            Document.Controller.viewDeletedAddress(caseFileId, assocId, addressId);
                        }
                    }
                });
            }
        }

        ,Aliases: {
            create: function() {
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_ADDED_PERSON_ALIAS        ,this.onModelAddedPersonAlias);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_UPDATED_PERSON_ALIAS      ,this.onModelUpdatedPersonAlias);
                Acm.Dispatcher.addEventListener(Document.Controller.MODEL_DELETED_PERSON_ALIAS      ,this.onModelDeletedPersonAlias);
            }
            ,onInitialized: function() {
            }

            ,onModelAddedPersonAlias: function(personAlias) {
                if (personAlias.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelUpdatedPersonAlias: function(personAlias) {
                if (personAlias.hasError) {
                    //refresh child table??;
                }
            }
            ,onModelDeletedPersonAlias: function(personAliasId) {
                if (personAliasId.hasError) {
                    //refresh child table??;
                }
            }

            ,createLink: function($jt) {
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='Aliases'><i class='fa fa-users'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Document.View.People.Aliases.onOpen, Document.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Document.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: 'Add Alias'
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                //var caseFileId = Document.View.getActiveDocumentId();
                                var c = Document.View.getActiveDocument();
                                if (Document.Model.Detail.validateDocument(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Document.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                                        var personAliases = personAssociation.person.personAliases;
                                        for (var i = 0; i < personAliases.length; i++) {
                                            rc.Records.push({
                                                assocId  : assocId
                                                ,id      : Acm.goodValue(personAliases[i].id, 0)
                                                ,type    : Acm.goodValue(personAliases[i].aliasType)
                                                ,value   : Acm.goodValue(personAliases[i].aliasValue)
                                                ,created : Acm.getDateFromDatetime(personAliases[i].created)
                                                ,creator : Acm.goodValue(personAliases[i].creator)
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return Document.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return Document.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,deleteAction: function (postData, jtParams) {
                            return {"Result": "OK"};
                        }
                    }
                    ,fields: {
                        assocId: {
                            type: 'hidden'
                            ,defaultValue: 0
                        }
                        , id: {
                            key: true
                            ,create: false
                            ,edit: false
                            ,list: false
                        }
                        ,type: {
                            title: 'Type'
                            ,width: '15%'
                            ,options: Document.Model.Lookup.getAliasTypes()
                        }
                        ,value: {
                            title: 'Value'
                            ,width: '30%'
                        }
                        ,created: {
                            title: 'Date Added'
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: 'Added By'
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    ,recordAdded : function (event, data) {
                        var record = data.record;
                        var personAlias = {};
                        var assocId = record.assocId;
                        personAlias.aliasType  = Acm.goodValue(record.type);
                        personAlias.aliasValue = Acm.goodValue(record.value);
                        personAlias.created = Acm.getCurrentDayInternal();
                        personAlias.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId) {
                            Document.Controller.viewAddedPersonAlias(caseFileId, assocId, personAlias);
                        }
                    }
                    ,recordUpdated : function (event, data) {
                        var record = data.record;
                        var personAlias = {};
                        var assocId = record.assocId;
                        personAlias.id         = Acm.goodValue(record.id, 0);
                        personAlias.aliasType  = Acm.goodValue(record.type);
                        personAlias.aliasValue = Acm.goodValue(record.value);
                        personAlias.created = Acm.getCurrentDayInternal();
                        personAlias.creator = Acm.goodValue(record.creator);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < personAlias.id) {
                            Document.Controller.viewUpdatedPersonAlias(caseFileId, assocId, personAlias);
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var personAliasId = Acm.goodValue(record.id, 0);
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId && 0 < assocId && 0 < personAliasId) {
                            Document.Controller.viewDeletedPersonAlias(caseFileId, assocId, personAliasId);
                        }
                    }
                });
            } //onOpen
        }
    }

    ,Documents: {
        create: function() {
            //for cases frevvo form is disabled in the properties file
            this.$formAddDocument = $("#formAddDocument");
            this.$btnAddDocument = $("#addDocument")
            this.$btnAddDocument.on("change", function(e) {Document.View.Documents.onChangeFileInput(e, this);});
            this.$formAddDocument.submit(function(e) {Document.View.Documents.onSubmitAddDocument(e, this);});

            this.$divDocuments    = $("#divDocs");
            this.createJTableDocuments(this.$divDocuments);
            AcmEx.Object.JTable.clickAddRecordHandler(this.$divDocuments, Document.View.Documents.onClickSpanAddDocument);
            this.$spanAddDocument = this.$divDocuments.find(".jtable-toolbar-item-add-record");
            Document.View.Documents.fillReportSelection();



            //Acm.Dispatcher.addEventListener(Document.Controller.MODEL_RETRIEVED_CASE_FILE     ,this.onModelRetrievedDocument);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT          ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_ADDED_DOCUMENT          ,this.onModelAddedDocument);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_CREATED_CORRESPONDENCE  ,this.onModelCreatedCorrespondence);
            //Acm.Dispatcher.addEventListener(Document.Controller.VIEW_SELECTED_CASE_FILE       ,this.onViewSelectedDocument);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT            ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CLOSED_CASE_FILE         ,this.onViewClosedDocument);
        }
        ,onInitialized: function() {
        }

        ,onChangeFileInput: function(event, ctrl) {
            Document.View.Documents.$formAddDocument.submit();
        }
        ,onSubmitAddDocument: function(event, ctrl) {
            event.preventDefault();
            var count = Document.View.Documents.$btnAddDocument[0].files.length;
            var report = Document.View.Documents.getSelectReportText();

            var fd = new FormData();
            fd.append("uploadFileType", report);
            fd.append("caseFileId", Document.Model.getDocumentId());
            for(var i = 0; i < count; i++ ){
                fd.append("files[]", Document.View.Documents.$btnAddDocument[0].files[i]);
            }
            Document.Service.Documents.uploadDocument(fd);
            this.$formAddDocument[0].reset();
        }
        ,onModelAddedDocument: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(Document.View.Documents.$divDocuments);
            }
        }
        ,onModelCreatedCorrespondence: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(Document.View.Documents.$divDocuments);
            }
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Document.View.Documents.$divDocuments);
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Document.View.Documents.$divDocuments);
        }
        ,onViewClosedDocument: function(caseFileId) {
            AcmEx.Object.JTable.load(Document.View.Documents.$divDocuments);
        }
        ,onViewAddedDocument: function(caseFileId) {
            AcmEx.Object.JTable.load(Document.View.Documents.$divDocuments);
        }


        ,onClickSpanAddDocument: function(event, ctrl) {
            var enableFrevvoFormEngine = Document.View.MicroData.getFormUrls()['enable_frevvo_form_engine'];
            var report = Document.View.Documents.getSelectReport();
            var reportext = Document.View.Documents.getSelectReportText();

            if(report == "roiFormUrl" || report == "electronicCommunicationFormUrl"){
                var token = Document.View.MicroData.getToken();

                var caseFileId = Document.View.getActiveDocumentId();
                var caseFile = Document.View.getActiveDocument();
                if (caseFile) {
                    var url = Document.View.MicroData.getFormUrls()[report];
                    if (Acm.isNotEmpty(url)) {
                        url = url.replace("_data=(", "_data=(type:'case', caseId:'" + caseFileId
                            + "',caseNumber:'" + Acm.goodValue(caseFile.caseNumber)
                            + "',caseTitle:'" + Acm.goodValue(caseFile.title)
                            + "',casePriority:'" + Acm.goodValue(caseFile.priority)
                            + "',");

                        Acm.Dialog.openWindow(url, "", 810, $(window).height() - 30
                            ,function() {
                                Document.Controller.viewAddedDocument(caseFileId);
                            }
                        );
                    }
                }
            }
            else if(report && report != ""){
                Document.View.Documents.$btnAddDocument.click();
            }
        }
//html+= "<form id='formFiles' style='display:none;'>"
//    + "<input id='newAttachment' type='file' name='files[]' multiple/>"
//    + "</form>"

        ,fillReportSelection: function() {
        	var formDocuments = null;
        	try {
        		formDocuments = JSON.parse(Acm.Object.MicroData.get("formDocuments"));
        	} catch(e) {
        		
        	}
        	
            var html = "<span>"
                + "<select class='input-sm form-control input-s-sm inline v-middle' id='docDropDownValue'>"
                + "<option value=''>Document Type</option>";

            if (formDocuments != null && formDocuments.length > 0) {
            	for (var i = 0; i < formDocuments.length; i ++) {
            		html += "<option value='" + formDocuments[i]["value"] + "'>" + formDocuments[i]["label"] + "</option>"
            	}
            }
                
            html += "<option value='mr'>Medical Release</option>"
                + "<option value='gr'>General Release</option>"
                + "<option value='ev'>eDelivery</option>"
                + "<option value='sig'>SF86 Signature</option>"
                + "<option value='ot'>Other</option>"
                + "</select>"
                + "</span>";


            this.$spanAddDocument.before(html);
        }
        ,getSelectReport: function() {
            return Acm.Object.getSelectValue(this.$spanAddDocument.prev().find("select"));
        }
        ,getSelectReportText: function() {
            return Acm.Object.getSelectedText(this.$spanAddDocument.prev().find("select"));
        }

        ,createJTableDocuments: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: 'Documents'
                ,paging: true //fix me
                ,sorting: true //fix me
                ,pageSize: 10 //Set page size (default: 10)
                ,messages: {
                    addNewRecord: 'Add Document'
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
//                        var caseFileId = Document.View.getActiveDocumentId();
//                        if (0 >= caseFileId) {
//                            return AcmEx.Object.JTable.getEmptyRecords();
//                        }
//
//                        var rc = AcmEx.Object.JTable.getEmptyRecords();
//                        var documents = Document.Model.Documents.cacheDocuments.get(caseFileId);
//
//                        if(Acm.isArray(documents)){
//                            for (var i = 0; i < documents.length; i++) {
//                                var childObject = documents[i];
//                                if (Acm.compare(Document.Model.DOCUMENT_TARGET_TYPE_FILE, childObject.targetType)) {
//                                    var record = {};
//                                    record.id = Acm.goodValue(childObject.id, 0);
//                                    record.title = Acm.goodValue(childObject.name);
//                                    record.created = Acm.getDateFromDatetime(childObject.created);
//                                    record.creator = Acm.__FixMe__getUserFullName(Acm.goodValue(childObject.creator));
//                                    record.status = Acm.goodValue(childObject.status);
//                                    record.docType = Acm.goodValue(childObject.targetSubtype);
//                                    rc.Records.push(record);
//                                }
//                            }
//                            rc.TotalRecordCount = rc.Records.length;
//                        }
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        //var caseFileId = Document.View.getActiveDocumentId();
                        var c = Document.View.getActiveDocument();
                        if(Document.Model.Detail.validateDocument(c)){
                            for (var i = 0; i < c.childObjects.length; i++) {
                                var childObject = c.childObjects[i];
                                if (Acm.compare(Document.Model.DOC_TYPE_FILE, childObject.targetType)) {
                                    var record = {};
                                    if (!Acm.compare(Document.Model.DOC_CATEGORY_CORRESPONDENCE, childObject.category)) {
                                        record.id = Acm.goodValue(childObject.targetId, 0);
                                        record.title = Acm.goodValue(childObject.targetName);
                                        record.created = Acm.getDateFromDatetime(childObject.created);
                                        record.creator = Acm.goodValue(childObject.creator);
                                        //record.status = Acm.goodValue(childObject.status);
                                        record.docType = Acm.goodValue(childObject.targetSubtype);
                                        rc.Records.push(record);
                                    }
                                }
                            }
                            rc.TotalRecordCount = rc.Records.length;
                        }

//                        var c = Document.View.getActiveDocument();
//                        if (c && Acm.isArray(c.childObjects)) {
//                            for (var i = 0; i < c.childObjects.length; i++) {
//                                var childObject = c.childObjects[i];
//                                if (Acm.compare("FILE", childObject.targetType)) {
//                                    var record = {};
//                                    record.id = Acm.goodValue(childObject.targetId, 0);
//                                    record.title = Acm.goodValue(childObject.targetName);
//                                    record.created = Acm.getDateFromDatetime(childObject.created);
//                                    record.creator = Acm.goodValue(childObject.creator);
//                                    record.status = Acm.goodValue(childObject.status);
//                                    rc.Records.push(record);
//                                }
//                            }
//                            rc.TotalRecordCount = rc.Records.length;
//                        }
                        return rc;

//for test
//                        return {
//                            "Result": "OK"
//                            ,"Records": [
//                                {"id": 11, "title": "Nick Name", "created": "01-02-03", "creator": "123 do re mi", "status": "JJ"}
//                                ,{"id": 12, "title": "Some Name", "created": "14-05-15", "creator": "xyz abc", "status": "Ice Man"}
//                            ]
//                            ,"TotalRecordCount": 2
//                        };
                    }
                    ,createAction: function(postData, jtParams) {
                        //custom web form creation takes over; this action should never be called
                        var rc = {"Result": "OK", "Record": {id:0, title:"", created:"", creator:"", status:""}};
                        return rc;
                    }
//not tested, disabled
//                    ,updateAction: function(postData, jtParams) {
//                        var record = Acm.urlToJson(postData);
//                        var rc = AcmEx.Object.JTable.getEmptyRecord();
//                        //id,created,creator is readonly
//                        //rc.Record.id = record.id;
//                        //rc.Record.created = record.created;
//                        //rc.Record.creator = record.creator;
//                        rc.Record.title = record.title;
//                        rc.Record.status = record.status;
//                        return rc;
//                    }
                }
                ,fields: {
                    id: {
                        title: 'ID'
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
                    ,title: {
                        title: 'Title'
                        ,width: '30%'
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + Document.Service.Documents.API_DOWNLOAD_DOCUMENT_
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    ,docType: {
                        title: 'Document Type'
                        ,width: '15%'
                        ,edit: false
                    }
                    ,created: {
                        title: 'Created'
                        ,width: '15%'
                        ,edit: false
                    }
                    ,creator: {
                        title: 'Creator'
                        ,width: '15%'
                        ,edit: false
                    }
                    ,status: {
                        title: 'Status'
                        ,width: '10%'
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = Document.View.getActiveDocumentId();
                    var c = Document.View.getActiveDocument();
                    if (c && Acm.isArray(c.childObjects)) {
                        var childObject = {};
                        childObject.targetId = record.id;
                        childObject.title = record.title;
                        childObject.status = record.status;
                        //childObject.parentId = record.parentId;
                        Document.Controller.viewChangedChildObject(caseFileId, childObject);
                    }
                }
            });
        }
    }

    ,Participants: {
        create: function() {
            this.$divParticipants    = $("#divParticipants");
            this.createJTableParticipants(this.$divParticipants);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_SAVED_ASSIGNEE    ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Document.View.Participants.$divParticipants);
        }
        ,onModelSavedAssignee: function(caseFileId, assginee) {
            if (!assginee.hasError) {
                AcmEx.Object.JTable.load(Document.View.Participants.$divParticipants);
            }
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Document.View.Participants.$divParticipants);
        }

        ,createJTableParticipants: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: 'Participants'
                ,paging: true //fix me
                ,sorting: true //fix me
                ,pageSize: 10 //Set page size (default: 10)
                ,messages: {
                    addNewRecord: 'Add Participant'
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        //var caseFileId = Document.View.getActiveDocumentId();
                        var c = Document.View.getActiveDocument();
                        if (Document.Model.Detail.validateDocument(c)) {
                            for (var i = 0; i < c.participants.length; i++) {
                                var participant = c.participants[i];
                                var record = {};
                                record.id = Acm.goodValue(participant.id, 0);
                                record.title = Acm.__FixMe__getUserFullName(Acm.goodValue(participant.participantLdapId));
                                record.type = Acm.goodValue(participant.participantType);
                                rc.Records.push(record);
                            }
                            rc.TotalRecordCount = rc.Records.length;
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        //var caseFileId = Document.View.getActiveDocumentId();
                        var caseFile = Document.View.getActiveDocument();
                        if (caseFile) {
                            rc.Record.title = record.title;
                            rc.Record.type = record.type;
                        }
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        //var caseFileId = Document.View.getActiveDocumentId();
                        var caseFile = Document.View.getActiveDocument();
                        if (caseFile) {
                            rc.Record.title = record.title;
                            rc.Record.type = record.type;
                        }
                        return rc;
                    }
                    ,deleteAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }
                ,fields: {
                    id: {
                        title: 'ID'
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
                    ,title: {
                        title: 'Name'
                        ,width: '70%'
                    }
                    ,type: {
                        title: 'Type'
                        ,width: '30%'
                    }
                }
                ,recordAdded : function (event, data) {
                    var record = data.record;
                    var caseFileId = Document.View.getActiveDocumentId();
                    if (0 < caseFileId) {
                        var participant = {};
                        participant.participantLdapId = record.title;
                        participant.participantType = record.type;
                        Document.Controller.viewAddedParticipant(caseFileId, participant);
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = Document.View.getActiveDocumentId();
                    var c = Document.View.getActiveDocument();
                    if (c && Acm.isArray(c.participants)) {
                        if (0 < c.participants.length && whichRow < c.participants.length) {
                            var participant = c.participants[whichRow];
                            participant.participantLdapId = record.title;
                            participant.participantType = record.type;
                            Document.Controller.viewUpdatedParticipant(caseFileId, participant);
                        }
                    }
                }
                ,recordDeleted : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = Document.View.getActiveDocumentId();
                    var c = Document.View.getActiveDocument();
                    if (c && Acm.isArray(c.participants)) {
                        if (0 < c.participants.length && whichRow < c.participants.length) {
                            var participant = c.participants[whichRow];
                            Document.Controller.viewDeletedParticipant(caseFileId, participant.id);
                        }
                    }
                }
            });
        }
    }

    ,Notes: {
        create: function() {
            this.$divNotes          = $("#divNotes");
            this.createJTableNotes(this.$divNotes);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_ADDED_NOTE        ,this.onModelAdddNote);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_UPDATED_NOTE      ,this.onModelUpdatedNote);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_DELETED_NOTE      ,this.onModelDeletedNote);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

//
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Document.View.Notes.$divNotes);
        }
        ,onModelAdddNote: function(caseFile) {
            if (caseFile.hasError) {
                //show error
            } else {
                AcmEx.Object.JTable.load(Document.View.Notes.$divNotes);
            }
        }
        ,onModelUpdatedNote: function(caseFile) {
            if (caseFile.hasError) {
                //show error
            } else {
                AcmEx.Object.JTable.load(Document.View.Notes.$divNotes);
            }
        }
        ,onModelDeletedNote: function(noteId) {
            if (noteId.hasError) {
                //show error
            } else {
                AcmEx.Object.JTable.load(Document.View.Notes.$divNotes);
            }
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Document.View.Notes.$divNotes);
        }

        ,_makeJtData: function(noteList) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (noteList) {
                for (var i = 0; i < noteList.length; i++) {
                    var Record = {};
                    Record.id         = Acm.goodValue(noteList[i].id, 0);
                    Record.note       = Acm.goodValue(noteList[i].note);
                    Record.created    = Acm.getDateFromDatetime(noteList[i].created);
                    Record.creator    = Acm.__FixMe__getUserFullName(Acm.goodValue(noteList[i].creator));
                    //Record.parentId   = Acm.goodValue(noteList[i].parentId);
                    //Record.parentType = Acm.goodValue(noteList[i].parentType);
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = noteList.length;
            }
            return jtData;
        }
        ,createJTableNotes: function($jt) {
            var sortMap = {};
            sortMap["created"] = "created";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'Notes'
                    ,paging: true
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,messages: {
                        addNewRecord: 'Add Note'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var caseFileId = Document.View.getActiveDocumentId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var noteList = Document.Model.Notes.cacheNoteList.get(caseFileId);
                            if (noteList) {
                                return Document.View.Notes._makeJtData(noteList);

                            } else {
                                return Document.Service.Notes.retrieveNoteListDeferred(caseFileId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        var noteList = data;
                                        return Document.View.Notes._makeJtData(noteList);
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            var caseFileId = Document.View.getActiveDocumentId();
                            var caseFile = Document.View.getActiveDocument();
                            if (caseFile) {
                                rc.Record.parentId = Acm.goodValue(caseFileId, 0);
                                rc.Record.parentType = Document.Model.DOC_TYPE_CASE_FILE;
                                rc.Record.note = record.note;
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var caseFileId = Document.View.getActiveDocumentId();
                            var caseFile = Document.View.getActiveDocument();
                            if (caseFile) {
                                rc.Record.parentId = Acm.goodValue(caseFileId, 0);
                                rc.Record.parentType = Document.Model.DOC_TYPE_CASE_FILE;
                                rc.Record.note = record.note;
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,deleteAction: function(postData, jtParams) {
                            return {
                                "Result": "OK"
                            };
                        }
                    }

                    ,fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                            ,defaultvalue : 0
                        }
                        ,note: {
                            title: 'Note'
                            ,type: 'textarea'
                            ,width: '50%'
                            ,edit: true
                        }
                        ,created: {
                            title: 'Created'
                            ,width: '15%'
                            ,edit: false
                            ,create: false
                        }
                        ,creator: {
                            title: 'Author'
                            ,width: '15%'
                            ,edit: false
                            ,create: false
                        }
                    } //end field
                    ,recordAdded : function (event, data) {
                        var record = data.record;
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId) {
                            var noteToSave = {};
                            //noteToSave.id = record.id;
                            noteToSave.id = 0;
                            noteToSave.note = record.note;
                            noteToSave.created = Acm.getCurrentDayInternal(); //record.created;
                            noteToSave.creator = record.creator;   //record.creator;
                            noteToSave.parentId = caseFileId;
                            noteToSave.parentType = Document.Model.DOC_TYPE_CASE_FILE;
                            //Document.Service.Notes.saveNote(noteToSave);
                            Document.Controller.viewAddedNote(noteToSave);
                        }
                    }
                    ,recordUpdated: function(event,data){
                        var whichRow = data.row.prevAll("tr").length;
                        var record = data.record;
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId) {
                            var notes = Document.Model.Notes.cacheNoteList.get(caseFileId);
                            if (notes) {
                                if(notes[whichRow]){
                                    notes[whichRow].note = record.note;
                                    //Document.Service.Notes.saveNote(notes[whichRow]);
                                    Document.Controller.viewUpdatedNote(notes[whichRow]);
                                }
                            }
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var caseFileId = Document.View.getActiveDocumentId();
                        if (0 < caseFileId) {
                            var notes = Document.Model.Notes.cacheNoteList.get(caseFileId);
                            if (notes) {
                                if(notes[whichRow]){
                                    //Document.Service.Notes.deleteNote(notes[whichRow].id);
                                    Document.Controller.viewDeletedNote(notes[whichRow].id);
                                }
                            }
                        }
                    }
                } //end arg
                ,sortMap
            );
        }
    }

    ,Tasks: {
        create: function() {
            this.$divTasks          = $("#divTasks");
            this.createJTableTasks(this.$divTasks);
            AcmEx.Object.JTable.clickAddRecordHandler(this.$divTasks, Document.View.Tasks.onClickSpanAddTask);

            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_RETRIEVED_TASKS    ,this.onModelRetrievedTasks);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_COMPLETED_TASK      ,this.onModelCompletedTask);

        }
        ,onInitialized: function() {
        }

        ,URL_TASK_DETAIL:  "/plugin/task/"
        ,URL_NEW_TASK_:    "/plugin/task/wizard?parentType=CASE_FILE&reference="


        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Document.View.Tasks.$divTasks);
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Document.View.Tasks.$divTasks);
        }
        ,onModelRetrievedTasks: function(tasks) {
            if (tasks.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(Document.View.Tasks.$divTasks);
            }
        }
        ,onModelCompletedTask: function(task) {
            if (task.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(Document.View.Tasks.$divTasks);
            }
        }
        ,onClickSpanAddTask: function(event, ctrl) {
            //var caseFileId = Document.View.getActiveDocumentId();
            var caseFile = Document.View.getActiveDocument();
            if (caseFile) {
                var caseNumber = Acm.goodValue(caseFile.caseNumber);
                var url = Document.View.Tasks.URL_NEW_TASK_  + caseNumber;
                App.gotoPage(url);
            }
        }
        ,onClickBtnTaskAssign: function(event, ctrl) {
            alert("onClickBtnTaskAssign");
        }
        ,onClickBtnTaskUnassign: function(event, ctrl) {
            alert("onClickBtnTaskUnassign");
        }
        ,onClickBtnCompleteTask : function(taskId) {
           // alert("adhoc task");

            Document.Service.Tasks.completeTask(taskId);
        }
        ,onClickBtnTaskWithOutcome : function(outcome,taskId) {
            var tasks = Document.Model.Tasks.cacheTasks.get(0);
            var task = {};
            for(var i = 0; i < tasks.length; i++) {
                if (tasks[i].taskId == taskId) {
                    task = tasks[i];
                }
            }
            for(var i = 0; i < task.availableOutcomes.length; i++){
                var availableOutcome = task.availableOutcomes[i];
                if(availableOutcome.name == outcome) {
                    task.taskOutcome = availableOutcome;
                }
            }
            if(task.taskOutcome){
                Document.Service.Tasks.completeTaskWithOutcome(task);
            }

            //alert("task with outcome");

        }
        ,retrieveTaskOutcome : function(taskId){
            var tasks = Document.Model.Tasks.cacheTasks.get(0);
            var $a = $("");
            if(tasks){
                for(var i = 0; i < tasks.length; i++){
                    if(tasks[i].taskId == taskId){
                        var task = tasks[i];
                        if(task.adhocTask == true && task.completed == false){
                            $a = $("<div class='btn-group-task'><button class='btn btn-default btn-sm adhoc' title='Complete Task'>Complete</button></div>");
                        }
                        else if(task.adhocTask == false && task.completed == false && task.availableOutcomes != null){
                             var availableOutcomes = task.availableOutcomes;
                             for(var j = 0; j < availableOutcomes.length; j++ ){
                                 if(availableOutcomes[j].name == 'COMPLETE'){
                                 $a = $("<div class='btn-group-task'><button class='btn btn-default btn-sm businessProcess' id='COMPLETE' title='Complete Task Outcome'>Complete</button></div>");
                                 }
                             }
                        }
                    }
                }
            }
            return $a;
        }

        ,_makeJtData: function(taskList) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (taskList) {
                for (var i = 0; i < taskList.length; i++) {
                    var Record = {};
                    Record.id       = taskList[i].id;
                    Record.title    = taskList[i].title;
                    Record.created  = taskList[i].created;
                    Record.priority = taskList[i].priority;
                    Record.dueDate  = taskList[i].dueDate;
                    Record.status   = taskList[i].status;
                    Record.assignee = Acm.__FixMe__getUserFullName(taskList[i].assignee);
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = taskList.length;
            }
            return jtData;
        }
        ,createJTableTasks: function($jt) {
            var sortMap = {};
            sortMap["title"] = "title_parseable";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'Tasks'
                    ,multiselect: false
                    ,selecting: false
                    ,selectingCheckboxes: false
                    ,paging: true
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: 'Add Task'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var caseFileId = Document.View.getActiveDocumentId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var taskList = Document.Model.Tasks.cacheTaskSolr.get(caseFileId);
                            if (taskList) {
                                return Document.View.Tasks._makeJtData(taskList);

                            } else {
                                return Document.Service.Tasks.retrieveTaskListDeferred(caseFileId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        var taskList = data;
                                        return Document.View.Tasks._makeJtData(taskList);
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }

                        ,createAction: function(postData, jtParams) {
                            return AcmEx.Object.JTable.getEmptyRecord();
                        }
                    }

                    ,fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: true
                            ,create: false
                            ,edit: false
                            ,display: function (commData) {
                                var a = "<a href='" + App.getContextPath() + '/plugin/task/' +
                                    + ((0 >= commData.record.id)? "#" : commData.record.id)
                                    + "'>" + commData.record.id + "</a>";
                                return $(a);
                            }
                        }
                        ,title: {
                            title: 'Title'
                            ,width: '30%'
                            ,display: function (commData) {
                                var a = "<a href='" + App.getContextPath() + '/plugin/task/' +
                                    + ((0 >= commData.record.id)? "#" : commData.record.id)
                                    + "'>" + commData.record.title + "</a>";
                                return $(a);
                            }
                        }
                        ,assignee: {
                            title: 'Assignee'
                            ,sorting: false
                            ,width: '25'
                        }
                        ,created: {
                            title: 'Created'
                            ,width: '15%'
                        }
                        ,priority: {
                            title: 'Priority'
                            ,width: '10%'
                        }
                        ,dueDate: {
                            title: 'Due'
                            ,width: '15%'
                        }
                        ,status: {
                            title: 'Status'
                            ,width: '10%'
                        }
                        ,description: {
                            title: 'Action'
                            ,width: '10%'
                            ,edit: false
                            ,create: false
                            ,display: function (commData) {
                                var $a = Document.View.Tasks.retrieveTaskOutcome(commData.record.id);
                                $a.on("click", ".businessProcess", function(e) {Document.View.Tasks.onClickBtnTaskWithOutcome(e.target.id,commData.record.id);$a.hide();});
                                $a.on("click", ".adhoc", function(e) {Document.View.Tasks.onClickBtnCompleteTask(commData.record.id);$a.hide();});
                                return $a;
                            }
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }

    ,References: {
        create: function() {
            this.$divReferences          = $("#divRefs");
            this.createJTableReferences(this.$divReferences);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_ADDED_DOCUMENT         ,this.onModelAddedDocument);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_CREATED_CORRESPONDENCE ,this.onModelCreatedCorrespondence);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Document.View.References.$divReferences);
        }
        ,onModelAddedDocument: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(Document.View.References.$divReferences);
            }
        }
        ,onModelCreatedCorrespondence: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(Document.View.References.$divReferences);
            }
        }
        ,onViewSelectedObject: function(caseFileId) {
            AcmEx.Object.JTable.load(Document.View.References.$divReferences);
        }

        ,createJTableReferences: function($jt) {
            //var sortMap = {};
            //sortMap["modified"] = "modified";

            AcmEx.Object.JTable.useBasic($jt, {
                    title: 'References'
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: 'Add Reference'
                    }
                    ,actions: {
                        listAction: function(postData, jtParams) {
                            var caseFileId = Document.View.getActiveDocumentId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            var c = Document.View.getActiveDocument();
                            if (c && Acm.isArray(c.references)) {
                                for (var i = 0; i < c.references.length; i++) {
                                    var reference = c.references[i];
                                    var record = {};
                                    record.id = Acm.goodValue(reference.targetId, 0);
                                    record.title = Acm.goodValue(reference.targetName);
                                    record.modified = Acm.getDateFromDatetime(reference.modified);
                                    record.type = Acm.goodValue(reference.targetType);
                                    record.status = Acm.goodValue(reference.status);
                                    rc.Records.push(record);
                                }
                                rc.TotalRecordCount = rc.Records.length;
                            }
                            return rc;
                        }
//unsure the requirement, add/update/delete not implemented
//                        ,createAction: function(postData, jtParams) {
//                            var record = Acm.urlToJson(postData);
//                            var rc = AcmEx.Object.JTable.getEmptyRecord();
//
//                            return rc;
//                        }
//                        ,deleteAction: function(postData, jtParams) {
//                            return {
//                                "Result": "OK"
//                            };
//                        }
                    }

                    ,fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                            ,defaultvalue : 0
                        }
                        ,title: {
                            title: 'Title'
                            ,width: '30%'
                            ,edit: true
                            ,create: false
                            ,display: function(data) {
                                var url = App.buildObjectUrl(data.record.type, data.record.id);
                                var $lnk = $("<a href='" + url + "'>" + data.record.title + "</a>");
                                return $lnk;
                            }
                        }
                        ,modified: {
                            title: 'Modified'
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                        ,type: {
                            title: 'Reference Type'
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                        ,status: {
                            title: 'Status'
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                    } //end field
                    ,recordAdded : function (event, data) {
                        var record = data.record;
//                        var complaint = Complaint.getComplaint();
//                        if (complaint) {
//                            var noteToSave = {};
//                            //noteToSave.id = record.id;
//                            noteToSave.note = record.note;
//                            noteToSave.created = Acm.getCurrentDayInternal(); //record.created;
//                            noteToSave.creator = record.creator;   //record.creator;
//                            noteToSave.parentId = complaint.complaintId;
//                            noteToSave.parentType = App.OBJTYPE_COMPLAINT;
//                            Complaint.Service.saveNote(noteToSave);
//                        }
                    }
                    ,recordUpdated: function(event,data){
                        var whichRow = data.row.prevAll("tr").length;
                        var record = data.record;
//                        var complaint = Complaint.getComplaint();
//                        if(complaint){
//                            var notes = Complaint.cacheNoteList.get(Complaint.getComplaintId());
//                            if (notes) {
//                                if(notes[whichRow]){
//                                    var noteToSave;
//                                    noteToSave = notes[whichRow];
//                                    noteToSave.note = record.note;
//                                    Complaint.Service.saveNote(noteToSave);
//                                }
//                            }
//                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
//                        var complaint = Complaint.getComplaint();
//                        if (complaint) {
//                            var notes = Complaint.cacheNoteList.get(Complaint.getComplaintId());
//                            if (notes) {
//                                var noteToDelete = notes[whichRow];
//                                var noteId = noteToDelete.id;
//                                Complaint.Service.deleteNoteById(noteId);
//                            }
//                        }
                    }
                } //end arg
//                ,sortMap
            );
        }
    }

    ,Events: {
        create: function() {
            this.$divEvents          = $("#divEvents");
            this.createJTableEvents(this.$divEvents);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Document.View.Events.$divEvents);
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Document.View.Events.$divEvents);
        }

        ,_makeJtData: function(eventList) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
//            if (eventList) {
//                for (var i = 0; i < eventList.length; i++) {
//                    var Record = {};
//                    Record.id         = Acm.goodValue(eventList[i].id, 0);
//                    Record.note       = Acm.goodValue(eventList[i].note);
//                    Record.created    = Acm.getDateFromDatetime(eventList[i].created);
//                    Record.creator    = Acm.goodValue(eventList[i].creator);
//                    //Record.parentId   = Acm.goodValue(eventList[i].parentId);
//                    //Record.parentType = Acm.goodValue(eventList[i].parentType);
//                    jtData.Records.push(Record);
//                }
//                jtData.TotalRecordCount = eventList.length;
//            }
            return jtData;
        }
        ,createJTableEvents: function($jt) {
            var sortMap = {};
            sortMap["created"] = "created";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'History'
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,messages: {
                        addNewRecord: 'Add Event'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            return AcmEx.Object.JTable.getEmptyRecords();

//                            var caseFileId = Document.View.getActiveDocumentId();
//                            if (0 >= caseFileId) {
//                                return AcmEx.Object.JTable.getEmptyRecords();
//                            }
//
//                            var eventList = Document.Model.Events.cacheEventList.get(caseFileId);
//                            if (eventList) {
//                                return Document.View.Events._makeJtData(eventList);
//
//                            } else {
//                                return Document.Service.Events.retrieveEventListDeferred(caseFileId
//                                    ,postData
//                                    ,jtParams
//                                    ,sortMap
//                                    ,function(data) {
//                                        var eventList = data;
//                                        return Document.View.Events._makeJtData(eventList);
//                                    }
//                                    ,function(error) {
//                                    }
//                                );
//                            }  //end else
                        }
                    }
                    , fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }, eventType: {
                            title: 'Event Name'
                            ,width: '50%'
                        }, eventDate: {
                            title: 'Date'
                            ,width: '25%'
                        }, userId: {
                            title: 'User'
                            ,width: '25%'
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }

    ,Correspondence: {
        create: function () {
            this.$divTemplates = $("#divTemplates");
            this.createJTableCorrespondence(this.$divTemplates);

            AcmEx.Object.JTable.clickAddRecordHandler(this.$divTemplates, Document.View.Correspondence.onClickSpanAddDocument);
            this.$spanAddTemplate = this.$divTemplates.find(".jtable-toolbar-item-add-record");
            Document.View.Correspondence.fillReportSelection();

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT          ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(Document.Controller.MODEL_CREATED_CORRESPONDENCE  ,this.onModelCreatedCorrespondence);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT            ,this.onViewSelectedObject);
        }
        , onInitialized: function () {
        }
        , onModelRetrievedObject: function (objData) {
            AcmEx.Object.JTable.load(Document.View.Correspondence.$divTemplates);
        }
        , onViewSelectedObject: function (objType, objId) {
            AcmEx.Object.JTable.load(Document.View.Correspondence.$divTemplates);
        }
        ,onModelCreatedCorrespondence: function(caseFileId) {
            if (caseFileId.hasError) {
                Acm.Dialog.info(caseFileId.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Document.View.Correspondence.$divTemplates);
            }
        }

        ,getSelectTemplate: function() {
            return Acm.Object.getSelectValue(this.$spanAddTemplate.prev().find("select"));
        }
        ,onClickSpanAddDocument: function(event, ctrl) {
            var caseFileId = Document.View.getActiveDocumentId();
            var templateName = Document.View.Correspondence.getSelectTemplate();
            Document.Controller.viewClickedAddCorrespondence(caseFileId, templateName);
        }

        ,fillReportSelection: function() {
            var html = "<span>"
                + "<select class='input-sm form-control input-s-sm inline v-middle' id='docDropDownValue'>"
//                + "<option value='GR'>General Release</option>"
//                + "<option value='MR'>Medical Release</option>"
//                + "<option value='CG'>Clearance Granted</option>"
//                + "<option value='CD'>Clearance Denied</option>"
                + "<option value='GeneralRelease.docx'>General Release</option>"
                + "<option value='MedicalRelease.docx'>Medical Release</option>"
                + "<option value='ClearanceGranted.docx'>Clearance Granted</option>"
                + "<option value='ClearanceDenied.docx'>Clearance Denied</option>"
                + "<option value='NoticeofInvestigation.docx'>Notice of Investigation</option>"
                + "</select>"
                + "</span>"
                ;


            this.$spanAddTemplate.before(html);
        }

        , createJTableCorrespondence: function ($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: 'Correspondence'
                ,paging: true //fix me
                ,sorting: true //fix me
                ,pageSize: 10 //Set page size (default: 10)
                , messages: {
                    addNewRecord: 'Add Correspondence'
                }
                , actions: {
                    listAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        //var caseFileId = Document.View.getActiveDocumentId();
                        var c = Document.View.getActiveDocument();
                        if(Document.Model.Detail.validateDocument(c)){
                            for (var i = 0; i < c.childObjects.length; i++) {
                                var childObject = c.childObjects[i];
                                if (Acm.compare(Document.Model.DOC_TYPE_FILE, childObject.targetType)) {
                                    var record = {};
                                    if (Acm.compare(Document.Model.DOC_CATEGORY_CORRESPONDENCE, childObject.category)) {
                                        record.id = Acm.goodValue(childObject.targetId, 0);
                                        record.title = Acm.goodValue(childObject.targetName);
                                        record.created = Acm.getDateFromDatetime(childObject.created);
                                        record.creator = Acm.goodValue(childObject.creator);
                                        //record.status = Acm.goodValue(childObject.status);
                                        rc.Records.push(record);
                                    }
                                }
                            }
                            rc.TotalRecordCount = rc.Records.length;
                        }
                        return rc;

                    }
//                    listAction: function (postData, jtParams) {
//                        var caseFileId = Document.View.getActiveDocumentId();
//                        if (0 >= caseFileId) {
//                            return AcmEx.Object.JTable.getEmptyRecords();
//                        }
//
//                        var rc = AcmEx.Object.JTable.getEmptyRecords();
//                        var documents = Document.Model.Documents.cacheDocuments.get(caseFileId);
//                        if(Acm.isArray(documents)){
//                            for (var i = 0; i < documents.length; i++) {
//                                var childObject = documents[i];
//                                if (Acm.compare(Document.Model.DOCUMENT_TARGET_TYPE_FILE, childObject.targetType)) {
//                                    var record = {};
//                                    if (Acm.compare(Document.Model.DOCUMENT_CATEGORY_CORRESPONDENCE, childObject.category)) {
//                                        record.id = Acm.goodValue(childObject.id, 0);
//                                        record.title = Acm.goodValue(childObject.name);
//                                        record.created = Acm.getDateFromDatetime(childObject.created);
//                                        record.creator = Acm.goodValue(childObject.creator);
//                                        //record.status = Acm.goodValue(childObject.status);
//                                        rc.Records.push(record);
//                                    }
//                                }
//                            }
//                            rc.TotalRecordCount = rc.Records.length;
//                        }
//                        return rc;
//
//                    }
                    ,createAction: function(postData, jtParams) {
                        //placeholder. this action should never be called
                        var rc = {"Result": "OK", "Record": {id:0, title:"", created:"", creator:""}};
                        return rc;
                    }
                }
                , fields: {
                    id: {
                        title: 'ID'
                        , key: true
                        , list: false
                        , create: false
                        , edit: false
                        , defaultvalue: 0
                    }
                    , title: {
                        title: 'Title'
                        , width: '50%'
                        , edit: false
                        , create: false
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + Document.Service.Documents.API_DOWNLOAD_DOCUMENT_
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    , created: {
                        title: 'Created'
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                    , creator: {
                        title: 'Creator'
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                }
            });
        }
    }

};

