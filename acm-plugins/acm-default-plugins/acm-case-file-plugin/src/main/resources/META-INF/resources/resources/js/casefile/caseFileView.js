/**
 * CaseFile.View
 *
 * @author jwu
 */
CaseFile.View = CaseFile.View || {
    create : function() {
        if (CaseFile.View.MicroData.create)       {CaseFile.View.MicroData.create();}
        if (CaseFile.View.Navigator.create)       {CaseFile.View.Navigator.create();}
        if (CaseFile.View.Content.create)         {CaseFile.View.Content.create();}
        if (CaseFile.View.Action.create)          {CaseFile.View.Action.create();}
        if (CaseFile.View.Detail.create)          {CaseFile.View.Detail.create();}
        if (CaseFile.View.People.create)    	  {CaseFile.View.People.create();}
        if (CaseFile.View.Documents.create)       {CaseFile.View.Documents.create();}
        if (CaseFile.View.Participants.create)    {CaseFile.View.Participants.create();}
        if (CaseFile.View.Notes.create)           {CaseFile.View.Notes.create();}
        if (CaseFile.View.Tasks.create)           {CaseFile.View.Tasks.create();}
        if (CaseFile.View.References.create)      {CaseFile.View.References.create();}
        if (CaseFile.View.History.create)         {CaseFile.View.History.create();}
        if (CaseFile.View.Correspondence.create)  {CaseFile.View.Correspondence.create();}
        if (CaseFile.View.OutlookCalendar.create) {CaseFile.View.OutlookCalendar.create();}
        if (CaseFile.View.Time.create)            {CaseFile.View.Time.create();}
        if (CaseFile.View.Cost.create)            {CaseFile.View.Cost.create();}

        // uncomment to override default jtable
        // popups and use ArkCase messageboard:
        // App.View.MessageBoard.useAcmMessageBoard();
    }
    ,onInitialized: function() {
        if (CaseFile.View.MicroData.onInitialized)       {CaseFile.View.MicroData.onInitialized();}
        if (CaseFile.View.Navigator.onInitialized)       {CaseFile.View.Navigator.onInitialized();}
        if (CaseFile.View.Content.onInitialized)         {CaseFile.View.Content.onInitialized();}
        if (CaseFile.View.Action.onInitialized)          {CaseFile.View.Action.onInitialized();}
        if (CaseFile.View.Detail.onInitialized)          {CaseFile.View.Detail.onInitialized();}
        if (CaseFile.View.People.onInitialized)          {CaseFile.View.People.onInitialized();}
        if (CaseFile.View.Documents.onInitialized)       {CaseFile.View.Documents.onInitialized();}
        if (CaseFile.View.Participants.onInitialized)    {CaseFile.View.Participants.onInitialized();}
        if (CaseFile.View.Notes.onInitialized)           {CaseFile.View.Notes.onInitialized();}
        if (CaseFile.View.Tasks.onInitialized)           {CaseFile.View.Tasks.onInitialized();}
        if (CaseFile.View.References.onInitialized)      {CaseFile.View.References.onInitialized();}
        if (CaseFile.View.History.onInitialized)         {CaseFile.View.History.onInitialized();}
        if (CaseFile.View.Correspondence.onInitialized)  {CaseFile.View.Correspondence.onInitialized();}
        if (CaseFile.View.OutlookCalendar.onInitialized) {CaseFile.View.OutlookCalendar.onInitialized();}
        if (CaseFile.View.Time.onInitialized)            {CaseFile.View.Time.onInitialized();}
        if (CaseFile.View.Cost.onInitialized)            {CaseFile.View.Cost.onInitialized();}
    }

    ,getActiveCaseFileId: function() {
        return ObjNav.View.Navigator.getActiveObjId();
    }
    ,getActiveCaseFile: function() {
        var objId = ObjNav.View.Navigator.getActiveObjId();
        var caseFile = null;
        if (Acm.isNotEmpty(objId)) {
            caseFile = ObjNav.Model.Detail.getCacheObject(CaseFile.Model.DOC_TYPE_CASE_FILE, objId);
        }
        return caseFile;
    }

    ,MicroData: {
        create : function() {
            this.treeFilter = Acm.Object.MicroData.getJson("treeFilter");
            this.treeSort   = Acm.Object.MicroData.getJson("treeSort");
            this.token      = Acm.Object.MicroData.get("token");
            this.arkcaseUrl      = Acm.Object.MicroData.get("arkcaseUrl");

            this.formUrls = {};
            this.formUrls.urlEditCaseFileForm            = Acm.Object.MicroData.get("urlEditCaseFileForm");
            this.formUrls.urlReinvestigateCaseFileForm   = Acm.Object.MicroData.get("urlReinvestigateCaseFileForm");
            this.formUrls.enableFrevvoFormEngine         = Acm.Object.MicroData.get("enableFrevvoFormEngine");
            this.formUrls.urlChangeCaseStatusForm        = Acm.Object.MicroData.get("urlChangeCaseStatusForm");
            this.formUrls.urlEditChangeCaseStatusForm    = Acm.Object.MicroData.get("urlEditChangeCaseStatusForm");
            this.formUrls.roiFormUrl                     = Acm.Object.MicroData.get("roiFormUrl");
            this.formUrls.electronicCommunicationFormUrl = Acm.Object.MicroData.get("electronicCommunicationFormUrl");

            var formDocuments = Acm.Object.MicroData.getJson("formDocuments");
            var mapDocForms = {};
            if (Acm.isArray(formDocuments)) {
                for (var i = 0; i < formDocuments.length; i++) {
                    var form = Acm.goodValue(formDocuments[i].value);
                    if (Acm.isNotEmpty(form)) {
                        mapDocForms[form] = formDocuments[i];
                    }
                }
            }
            this.fileTypes = Acm.Object.MicroData.getJson("fileTypes");
            if (Acm.isArray(this.fileTypes)) {
                for (var i = 0; i < this.fileTypes.length; i++) {
                    var form = this.fileTypes[i].form;
                    if (Acm.isNotEmpty(form)) {
                        this.fileTypes[i].url = Acm.goodValue(this.formUrls[form]);
                        var formDocument = mapDocForms[form];
                        if (formDocument) {
                            this.fileTypes[i].label = Acm.goodValue(formDocument.label);
                        }
                    }
                }
            }
        }
        ,onInitialized: function() {
        }

        ,getToken: function() {
            return this.token;
        }

        ,findFileTypeByType: function(type) {
            var ft = null;
            if (Acm.isArray(this.fileTypes)) {
                for (var i = 0; i < this.fileTypes.length; i++) {
                    if (type == this.fileTypes[i].type) {
                        ft = this.fileTypes[i];
                        break;
                    }
                }
            }
            return ft;
        }
    }

    ,Navigator: {
        create: function() {
            this.$ulFilter = $("#ulFilter");
            this.$ulSort   = $("#ulSort");
            this.$tree     = $("#tree");

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CASE_TITLE       , this.onViewChangedCaseTitle);
//            if ("undefined" != typeof Topbar) {
//                Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_SET_ASN_DATA       , this.onTopbarViewSetAsnData, Acm.Dispatcher.PRIORITY_HIGH);
//            }
        }
        ,onInitialized: function() {
        }

        ,onViewChangedCaseTitle: function(caseFileId, title) {
            var caseFileSolr = ObjNav.Model.List.getSolrObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId);
            if (ObjNav.Model.List.validateObjSolr(caseFileSolr)) {
                caseFileSolr.title_parseable = Acm.goodValue(title);
                ObjNav.View.Navigator.updateObjNode(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId);
            }
        }
//        ,onTopbarViewSetAsnData: function(asnData) {
//            if (AcmEx.Model.Tree.Config.validateTreeInfo(asnData)) {
//                if (0 == asnData.name.indexOf("/plugin/casefile")) {
//                    var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
//                    if (AcmEx.Model.Tree.Config.sameResultSet(asnData)) {
//                        if (asnData.key) {
//                            var key = CaseFile.Model.Tree.Key.getKeyBySubWithPage(asnData.start, asnData.key);
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
                    CaseFile.View.Navigator.lazyLoad(event, data);
                }
                ,getContextMenu: function(node) {
                    CaseFile.View.Navigator.getContextMenu(node);
                }
            };
        }
        ,lazyLoad: function(event, data) {
            var key = data.node.key;
            var nodeType = ObjNav.Model.Tree.Key.getNodeTypeByKey(key);
            switch (nodeType) {
                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, CaseFile.Model.DOC_TYPE_CASE_FILE]):
                    data.result = AcmEx.FancyTreeBuilder
                        .reset()
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DETAILS
                            ,title: $.t("casefile:navigation.leaf-title.details")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_PEOPLE
                            ,title: $.t("casefile:navigation.leaf-title.people")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS
                            ,title: $.t("casefile:navigation.leaf-title.documents")
//                            ,folder: true
//                            ,lazy: true
//                            ,cache: false
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_PARTICIPANTS
                            ,title: $.t("casefile:navigation.leaf-title.participants")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_NOTES
                            ,title: $.t("casefile:navigation.leaf-title.notes")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TASKS
                            ,title: $.t("casefile:navigation.leaf-title.tasks")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_REFERENCES
                            ,title: $.t("casefile:navigation.leaf-title.references")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_HISTORY
                            ,title: $.t("casefile:navigation.leaf-title.history")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TEMPLATES
                            ,title: $.t("casefile:navigation.leaf-title.correspondence")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_CALENDAR
                            ,title: "Calendar"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TIME
                            ,title: $.t("casefile:navigation.leaf-title.time")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_COST
                            ,title: $.t("casefile:navigation.leaf-title.cost")
                        })
                        .getTree();

                    break;

                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, CaseFile.Model.DOC_TYPE_CASE_FILE, CaseFile.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS]):
                    var caseFileId = ObjNav.Model.Tree.Key.getObjIdByKey(key);
                    var c = ObjNav.Model.Detail.getCacheObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId);
                    if (c) {
                        data.result = [
                            {key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.1", title: "Document1" + "[Status]"}
                            ,{key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.2", title: "Doc2" + "[Status]"}
                        ];
                    } else {
                        data.result = ObjNav.Service.Detail.retrieveObjectDeferred(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId
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
                {title: $.t("casefile:context-menu.menu-title.menu") + key, cmd: "cut", uiIcon: "ui-icon-scissors"},
                {title: $.t("casefile:context-menu.menu-title.copy"), cmd: "copy", uiIcon: "ui-icon-copy"},
                {title: $.t("casefile:context-menu.menu-title.paste"), cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: false },
                {title: "----"},
                {title: $.t("casefile:context-menu.menu-title.edit"), cmd: "edit", uiIcon: "ui-icon-pencil", disabled: true },
                {title: $.t("casefile:context-menu.menu-title.delete"), cmd: "delete", uiIcon: "ui-icon-trash", disabled: true },
                {title: $.t("casefile:context-menu.menu-title.more"), children: [
                    {title: $.t("casefile:context-menu.menu-title.sub1"), cmd: "sub1"},
                    {title: $.t("casefile:context-menu.menu-title.sub2"), cmd: "sub1"}
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
            Acm.Dialog.error(Acm.goodValue(error.errMsg, $.t("casefile:msg.error-retrieving-casefile-data")));
        }
    }

    ,Action: {
        create: function() {
            this.$olMilestoneTrack          = $(".track-progress");
            this.$dlgChangeCaseStatus      = $("#changeCaseStatus");
            this.$dlgConsolidateCase       = $("#consolidateCase");
            this.$edtConsolidateCase       = $("#edtConsolidateCase");
            this.$btnEditCaseFile    	   = $("#btnEditCaseFile");
            this.$btnChangeCaseStatus      = $("#btnChangeCaseStatus");
            this.$btnConsolidateCase       = $("#btnConsolidateCase");
            this.$btnReinvestigateCaseFile = $("#btnReinvestigate");
            this.$btnEditCaseFile   	  .on("click", function(e) {CaseFile.View.Action.onClickBtnEditCaseFile(e, this);});
            this.$btnChangeCaseStatus     .on("click", function(e) {CaseFile.View.Action.onClickBtnChangeCaseStatus(e, this);});
            this.$btnConsolidateCase      .on("click", function(e) {CaseFile.View.Action.onClickBtnConsolidateCase(e, this);});
            this.$btnReinvestigateCaseFile.on("click", function(e) {CaseFile.View.Action.onClickBtnReinvestigateCaseFile(e, this);});

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT         ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onClickBtnEditCaseFile: function(event, ctrl) {
        	var urlEditCaseFileForm = CaseFile.View.MicroData.formUrls.urlEditCaseFileForm;
        	var caseFileId = CaseFile.View.getActiveCaseFileId();
            var c = CaseFile.View.getActiveCaseFile();
            if (Acm.isNotEmpty(urlEditCaseFileForm) && Acm.isNotEmpty(c)) {
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
            	
            	urlEditCaseFileForm = urlEditCaseFileForm.replace("/embed?", "/popupform?");
            	urlEditCaseFileForm = urlEditCaseFileForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',mode:'edit',xmlId:'" + xmlId + "',pdfId:'" + pdfId + "',");
            	Acm.Dialog.openWindow(urlEditCaseFileForm, "", 1060, 700
                    ,function() {
                        CaseFile.Controller.viewChangedCaseFile(caseFileId);
                    }
                );
            }
        }
        
        ,onClickBtnChangeCaseStatus: function() {
            CaseFile.View.Action.showDlgChangeCaseStatus(function(event, ctrl){
                var urlChangeCaseStatusForm = CaseFile.View.MicroData.formUrls.urlChangeCaseStatusForm;
                var caseFileId = CaseFile.View.getActiveCaseFileId();
                //var objType = ObjNav.View.Navigator.getActiveObjType();
                //var c = ObjNav.Model.Detail.getCacheObject(objType, caseFileId);
                var c = CaseFile.View.getActiveCaseFile();
                if (Acm.isNotEmpty(urlChangeCaseStatusForm) && Acm.isNotEmpty(c)) {
                    if (Acm.isNotEmpty(c.caseNumber)) {
                        urlChangeCaseStatusForm = urlChangeCaseStatusForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',");
                        Acm.Dialog.openWindow(urlChangeCaseStatusForm, "", 1060, 700
                            ,function() {
                                CaseFile.Controller.viewClosedCaseFile(caseFileId);
                            }
                        );
                    }
                }
            });
        }

        //---- demo how to use object picker ----
        ,onPickObjectDemo: function() {
            SearchBase.Dialog.create({name: "demoDialog"
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
            }).show();
        }
        //---------------------------------------

        ,onClickBtnConsolidateCase: function() {
//borrow it to test object picker dialog
//            this.onPickObjectDemo();
//            return;

            CaseFile.View.Action.setValueEdtConsolidateCase("");
            CaseFile.View.Action.showDlgConsolidateCase(function(event, ctrl) {
                var caseNumber = CaseFile.View.Action.getValueEdtConsolidateCase();
                alert("Consolidate case:" + caseNumber);
            });
        }
        ,onClickBtnReinvestigateCaseFile: function() {
        	var urlReinvestigateCaseFileForm = CaseFile.View.MicroData.formUrls.urlReinvestigateCaseFileForm;
        	var caseFileId = CaseFile.View.getActiveCaseFileId();
            var c = CaseFile.View.getActiveCaseFile();
            if (Acm.isNotEmpty(urlReinvestigateCaseFileForm) && Acm.isNotEmpty(c)) {
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
            	urlReinvestigateCaseFileForm = urlReinvestigateCaseFileForm.replace("/embed?", "/popupform?");
            	urlReinvestigateCaseFileForm = urlReinvestigateCaseFileForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',mode:'reinvestigate',xmlId:'" + xmlId + "',");
            	Acm.Dialog.openWindow(urlReinvestigateCaseFileForm, "", 1060, 700
                    ,function() {
            			// TODO: When James will find solution, we should change this
            			window.location.href = App.getContextPath() + '/plugin/casefile';
                    }
                );
            }
        }

        ,onModelRetrievedObject: function(objData) {
                CaseFile.View.Action.populate(objData);
        }
        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            CaseFile.View.Action.populate(objData);
            SubscriptionOp.Model.checkSubscription(App.getUserName(), objType, objId);
        }

        ,populate: function(caseFile) {
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                CaseFile.View.Action.showBtnChangeCaseStatus(Acm.goodValue(caseFile.changeCaseStatus, true));
                //Comment out temporarily
                //CaseFile.View.Action.showMilestone(Acm.goodValue(caseFile.milestones));
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
            this.$btnEditDetail.on("click", function(e) {CaseFile.View.Detail.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {CaseFile.View.Detail.onClickBtnSaveDetail(e, this);});

            this.$labCaseNumber   = $("#caseNumber");
            this.$lnkCaseTitle    = $("#caseTitle");
            this.$lnkIncidentDate = $("#incident");
            this.$lnkPriority     = $("#priority");
            this.$lnkAssignee     = $("#assigned");
            this.$lnkGroup 		  = $("#group");
            this.$lnkSubjectType  = $("#type");
            this.$lnkDueDate      = $("#dueDate");
            this.$lnkStatus       = $("#status");

            this.$chkRestrict     = $("#restrict");
            this.$chkRestrict.on("click", function(e) {CaseFile.View.Detail.onClickRestrictCheckbox(e, this);});


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


            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_CASE_FILE          ,this.onModelSavedCaseFile);
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
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_DETAIL             ,this.onModelSavedDetail);

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

            AcmEx.Object.XEditable.useEditable(CaseFile.View.Detail.$lnkAssignee, {
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
            CaseFile.View.Detail.populateRestriction(CaseFile.View.getActiveCaseFile());
        }
        ,onModelRetrievedGroups: function(groups) {
            var choices = [];
            $.each(groups, function(idx, val) {
                var opt = {};
                opt.value = val.object_id_s;
                opt.text = val.name;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(CaseFile.View.Detail.$lnkGroup, {
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
            CaseFile.View.Detail.populateRestriction(CaseFile.View.getActiveCaseFile());
        }
        ,onModelFoundSubjectTypes: function(subjectTypes) {
            var choices = [];
            $.each(subjectTypes, function(idx, val) {
                var opt = {};
                opt.value = val;
                opt.text = val;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(CaseFile.View.Detail.$lnkSubjectType, {
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

            AcmEx.Object.XEditable.useEditable(CaseFile.View.Detail.$lnkPriority, {
                source: choices
                ,success: function(response, newValue) {
                    CaseFile.Controller.viewChangedPriority(CaseFile.View.getActiveCaseFileId(), newValue);
                }
            });
        }


        ,onModelRetrievedObject: function(objData) {
            CaseFile.View.Detail.populateCaseFile(objData);
        }
        ,onModelSavedCaseTitle: function(caseFileId, title) {
            if (title.hasError) {
                CaseFile.View.Detail.setTextLnkCaseTitle($.t("casefile:detail.error-value"));
            }
        }
        ,onModelSavedIncidentDate: function(caseFileId, incidentDate) {
            if (incidentDate.hasError) {
                CaseFile.View.Detail.setTextLnkIncidentDate($.t("casefile:detail.error-value"));
            }
        }
        ,onModelSavedAssignee: function(caseFileId, assginee) {
            if (assginee.hasError) {
                CaseFile.View.Detail.setTextLnkAssignee($.t("casefile:detail.error-value"));
            }
        }
        ,onModelSavedGroup: function(caseFileId, group) {
            if (group.hasError) {
                CaseFile.View.Detail.setTextLnkGroup($.t("casefile:detail.error-value"));
            }
        }
        ,onModelSavedSubjectType: function(caseFileId, subjectType) {
            if (subjectType.hasError) {
                CaseFile.View.Detail.setTextLnkSubjectType($.t("casefile:detail.error-value"));
            }
        }
        ,onModelSavedPriority: function(caseFileId, priority) {
            if (priority.hasError) {
                CaseFile.View.Detail.setTextLnkPriority($.t("casefile:detail.error-value"));
            }
        }
        ,onModelSavedDueDate: function(caseFileId, created) {
            if (created.hasError) {
                CaseFile.View.Detail.setTextLnkDueDate($.t("casefile:detail.error-value"));
            }
        }
        ,onModelSavedDetail: function(caseFileId, details) {
            if (details.hasError) {
                CaseFile.View.Detail.setHtmlDivDetail($.t("casefile:detail.error-value"));
            }
        }

        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            CaseFile.View.Detail.populateCaseFile(objData);
        }

        ,DIRTY_EDITING_DETAIL: "Editing case detail"
        ,onClickBtnEditDetail: function(event, ctrl) {
            App.Object.Dirty.declare(CaseFile.View.Detail.DIRTY_EDITING_DETAIL);
            CaseFile.View.Detail.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = CaseFile.View.Detail.saveDivDetail();
            CaseFile.Controller.viewChangedDetail(CaseFile.View.getActiveCaseFileId(), htmlDetail);
            App.Object.Dirty.clear(CaseFile.View.Detail.DIRTY_EDITING_DETAIL);
        }
        ,onClickRestrictCheckbox: function(event,ctrl){
            var restriction = ($(ctrl).prop('checked')) ? true : false;
            CaseFile.Controller.viewClickedRestrictCheckbox(CaseFile.View.getActiveCaseFileId(),restriction);
        }

        ,populateCaseFile: function(c) {
            if (CaseFile.Model.Detail.validateCaseFile(c)) {
                this.setTextLabCaseNumber(Acm.goodValue(c.caseNumber));
                this.setTextLnkCaseTitle(Acm.goodValue(c.title));
                this.setTextLnkIncidentDate(Acm.getDateFromDatetime(c.created));//c.incidentDate
                this.setTextLnkSubjectType(Acm.goodValue(c.caseType));
                this.setTextLnkPriority(Acm.goodValue(c.priority));
                this.setTextLnkDueDate(Acm.getDateFromDatetime(c.dueDate));
                this.setTextLnkStatus("  (" + Acm.goodValue(c.status) +")");
                this.setPropertyRestricted(Acm.goodValue(c.restricted));
                this.setHtmlDivDetail(Acm.goodValue(c.details));

                var assignee = CaseFile.Model.Detail.getAssignee(c);
                this.setTextLnkAssignee(Acm.goodValue(assignee));
                
                var group = CaseFile.Model.Detail.getGroup(c);
                this.setTextLnkGroup(Acm.goodValue(group));
                
                CaseFile.View.Detail.populateRestriction(c);
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
//        	if (CaseFile.View.Action.$btnChangeCaseStatus) {
//        		CaseFile.View.Action.$btnChangeCaseStatus.show();
//        	}
//        }
//        ,hideChangeCaseStatusButton: function() {
//        	if (CaseFile.View.Action.$btnChangeCaseStatus) {
//        		CaseFile.View.Action.$btnChangeCaseStatus.hide();
//        	}
//        }
        
        ,populateRestriction: function(c) {
        	if (CaseFile.Model.Detail.validateCaseFile(c)) {
	        	var assignee = CaseFile.Model.Detail.getAssignee(c);
	        	var group = CaseFile.Model.Detail.getGroup(c);
	        	var assignees = CaseFile.Model.Lookup.getAssignees(c.id);
	        	var groups = CaseFile.Model.Lookup.getGroups(c.id);
	            
	            var restrict = Acm.checkRestriction(assignee, group, assignees, groups);
	            CaseFile.View.Detail.$chkRestrict.prop('disabled', restrict);
        	}
        }
    }
    
    ,People: {
        create: function() {
            this.$divPeople = $("#divPeople");
            this.createJTable(this.$divPeople);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT              ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT                ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_PERSON_ASSOCIATION    ,this.onModelAddedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_PERSON_ASSOCIATION  ,this.onModelUpdatedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_PERSON_ASSOCIATION  ,this.onModelDeletedPersonAssociation);

            if (this.ContactMethods.create)     {this.ContactMethods.create();}
            if (this.SecurityTags.create)       {this.SecurityTags.create();}
            if (this.Organizations.create)      {this.Organizations.create();}
            if (this.Addresses.create)          {this.Addresses.create();}
            if (this.Aliases.create)            {this.Aliases.create();}
        }
        ,onInitialized: function() {
            if (CaseFile.View.People.ContactMethods.onInitialized)     {CaseFile.View.People.ContactMethods.onInitialized();}
            if (CaseFile.View.People.SecurityTags.onInitialized)       {CaseFile.View.People.SecurityTags.onInitialized();}
            if (CaseFile.View.People.Organizations.onInitialized)      {CaseFile.View.People.Organizations.onInitialized();}
            if (CaseFile.View.People.Addresses.onInitialized)          {CaseFile.View.People.Addresses.onInitialized();}
            if (CaseFile.View.People.Aliases.onInitialized)            {CaseFile.View.People.Aliases.onInitialized();}
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
        }
        ,onModelAddedPersonAssociation: function(personAssociation) {
            if (personAssociation.hasError) {
                Acm.Dialog.info(personAssociation.errorMsg);
            }
            else{
                AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
            }
        }
        ,onModelUpdatedPersonAssociation: function(personAssociation) {
            if (personAssociation.hasError) {
                Acm.Dialog.info(personAssociation.errorMsg);
            }
            else{
                AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
            }
        }
        ,onModelDeletedPersonAssociation: function(personAssociationId) {
            if (personAssociationId.hasError) {
                Acm.Dialog.info(personAssociationId.errorMsg);
            }
            else{
                AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
            }
        }

        ,createJTable: function($s) {
            AcmEx.Object.JTable.useChildTable($s
                ,[
                    CaseFile.View.People.ContactMethods.createLink
                    ,CaseFile.View.People.Organizations.createLink
                    ,CaseFile.View.People.Addresses.createLink
                    ,CaseFile.View.People.Aliases.createLink
                ]
                ,{
                    title: $.t("casefile:people.table.title")
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:people.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            //var caseFileId = CaseFile.View.getActiveCaseFileId();
                            var c = CaseFile.View.getActiveCaseFile();
                            if (CaseFile.Model.Detail.validateCaseFile(c)) {
                                var personAssociations = c.personAssociations;
                                for (var i = 0; i < personAssociations.length; i++) {
                                    if (CaseFile.Model.People.validatePersonAssociation(personAssociations[i])) {
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
                            title: $.t("casefile:people.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }
                        ,title: {
                            title: $.t("casefile:people.table.field.title")
                            ,width: '10%'
                            ,options: CaseFile.Model.Lookup.getPersonTitles()
                        }
                        ,givenName: {
                            title: $.t("casefile:people.table.field.first-name")
                            ,width: '15%'
                        }
                        ,familyName: {
                            title: $.t("casefile:people.table.field.last-name")
                            ,width: '15%'
                        }
                        ,personType: {
                            title: $.t("casefile:people.table.field.type")
                            ,options: CaseFile.Model.Lookup.getPersonTypes()
                        }
                    }
                    ,recordAdded: function(event, data){
                        var record = data.record;
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId) {
                            var pa = {};
                            pa.personType = record.personType;
                            //pa.personDescription = record.personDescription;
                            pa.person = {};
                            pa.person.title = record.title;
                            pa.person.givenName = record.givenName;
                            pa.person.familyName = record.familyName;
                            CaseFile.Controller.viewAddedPersonAssociation(caseFileId, pa);
                        }
                     }

                    ,recordUpdated: function(event, data){
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var assocId = record.assocId;
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        var c = CaseFile.View.getActiveCaseFile();
                        if (CaseFile.Model.Detail.validateCaseFile(c)) {
                            if (c.personAssociations.length > whichRow) {
                                var pa = c.personAssociations[whichRow];
                                if (CaseFile.Model.People.validatePersonAssociation(pa)) {
                                    pa.person.title = record.title;
                                    pa.person.givenName = record.givenName;
                                    pa.person.familyName = record.familyName;
                                    pa.personType = record.personType;
                                    CaseFile.Controller.viewUpdatedPersonAssociation(caseFileId, pa);
                                }
                            }
                        }
                    }
                    ,recordDeleted: function(event,data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var personAssociationId = record.assocId;
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < personAssociationId) {
                            CaseFile.Controller.viewDeletedPersonAssociation(caseFileId, personAssociationId);
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
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_CONTACT_METHOD        ,this.onModelAddedContactMethod);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_CONTACT_METHOD      ,this.onModelUpdatedContactMethod);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_CONTACT_METHOD      ,this.onModelDeletedContactMethod);
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
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='" + $.t("casefile:people.table.contact-methods.table.title") + "'><i class='fa fa-phone'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.ContactMethods.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_CONTACT_METHODS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_CONTACT_METHODS
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:people.table.contact-methods.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                //var caseFileId = CaseFile.View.getActiveCaseFileId();
                                var c = CaseFile.View.getActiveCaseFile();
                                if (CaseFile.Model.Detail.validateCaseFile(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
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
                            title: $.t("casefile:people.table.contact-methods.table.field.type")
                            ,width: '15%'
                            ,options: CaseFile.Model.Lookup.getContactMethodTypes()
                        }
                        ,value: {
                            title: $.t("casefile:people.table.contact-methods.table.field.value")
                            ,width: '30%'
                        }
                        ,created: {
                            title: $.t("casefile:people.table.contact-methods.table.field.date-added")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: $.t("casefile:people.table.communication.table.field.added-by")
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedContactMethod(caseFileId, assocId, contactMethod);
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < contactMethod.id) {
                            CaseFile.Controller.viewUpdatedContactMethod(caseFileId, assocId, contactMethod);
                        }
                    }
                    ,recordDeleted: function (event, data) {
                        //var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var assocId = record.assocId;
                        var contactMethodId = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < contactMethodId) {
                            CaseFile.Controller.viewDeletedContactMethod(caseFileId, assocId, contactMethodId);
                        }
                    }
                });
            }
        }

        ,SecurityTags: {
            create: function() {
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_SECURITY_TAG        ,this.onModelAddedSecurityTag);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_SECURITY_TAG      ,this.onModelUpdatedSecurityTag);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_SECURITY_TAG      ,this.onModelDeletedSecurityTag);
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
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.ContactMethods.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_SECURITY_TAGS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_SECURITY_TAGS
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

                                //var caseFileId = CaseFile.View.getActiveCaseFileId();
                                var c = CaseFile.View.getActiveCaseFile();
                                if (CaseFile.Model.Detail.validateCaseFile(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
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
                            title: $.t("casefile:people.table.security-tags.table.field.type")
                            ,width: '15%'
                            ,options: CaseFile.Model.Lookup.getSecurityTagTypes()
                        }
                        ,value: {
                            title: $.t("casefile:people.table.security-tags.table.field.value")
                            ,width: '30%'
                        }
                        ,created: {
                            title: $.t("casefile:people.table.security-tags.table.field.date-added")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: $.t("casefile:people.table.security-tags.table.field.added-by")
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedSecurityTag(caseFileId, assocId, securityTag);
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < securityTag.id) {
                            CaseFile.Controller.viewUpdatedSecurityTag(caseFileId, assocId, securityTag);
                        }
                    }
                    ,recordDeleted: function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var securityTagId = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < securityTagId) {
                            CaseFile.Controller.viewDeletedSecurityTag(caseFileId, assocId, securityTagId);
                        }
                    }
                });
            }
        }

        ,Organizations: {
            create: function() {
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_ORGANIZATION        ,this.onModelAddedOrganization);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_ORGANIZATION      ,this.onModelUpdatedOrganization);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_ORGANIZATION      ,this.onModelDeletedOrganization);
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
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='"+ $.t("casefile:people.table.security-tags.organizations.table.title") +"'><i class='fa fa-book'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.Organizations.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:people.table.security-tags.organizations.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                //var caseFileId = CaseFile.View.getActiveCaseFileId();
                                var c = CaseFile.View.getActiveCaseFile();
                                if (CaseFile.Model.Detail.validateCaseFile(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
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
                            title: $.t("casefile:people.table.security-tags.organizations.table.field.type")
                            ,width: '15%'
                            ,options: CaseFile.Model.Lookup.getOrganizationTypes()
                        }
                        , value: {
                            title: $.t("casefile:people.table.security-tags.organizations.table.field.value")
                            ,width: '30%'
                        }
                        , created: {
                            title: $.t("casefile:people.table.security-tags.organizations.table.field.date-added")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                        }
                        , creator: {
                            title: $.t("casefile:people.table.security-tags.organizations.table.field.added-by")
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedOrganization(caseFileId, assocId, organization);
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < organization.organizationId) {
                            CaseFile.Controller.viewUpdatedOrganization(caseFileId, assocId, organization);
                        }
                    }
                    , recordDeleted: function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var organizationId = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < organizationId) {
                            CaseFile.Controller.viewDeletedOrganization(caseFileId, assocId, organizationId);
                        }
                    }
                });
            }
        }

        ,Addresses: {
            create: function() {
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_ADDRESS        ,this.onModelAddedAddress);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_ADDRESS      ,this.onModelUpdatedAddress);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_ADDRESS      ,this.onModelDeletedAddress);
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
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='" + $.t("casefile:people.table.security-tags.addresses.table.title") + "'><i class='fa fa-map-marker'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.Addresses.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ADDRESSES);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ADDRESSES
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:people.table.security-tags.addresses.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;

                                //var caseFileId = CaseFile.View.getActiveCaseFileId();
                                var c = CaseFile.View.getActiveCaseFile();
                                if (CaseFile.Model.Detail.validateCaseFile(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                            title: $.t("casefile:people.table.security-tags.addresses.table.field.type")
                            ,width: '8%'
                            ,options: CaseFile.Model.Lookup.getAddressTypes()
                        }
                        ,streetAddress: {
                            title: $.t("casefile:people.table.security-tags.addresses.table.field.address")
                            ,width: '20%'
                        }
                        ,city: {
                            title: $.t("casefile:people.table.security-tags.addresses.table.field.city")
                            ,width: '10%'
                        }
                        ,state: {
                            title: $.t("casefile:people.table.security-tags.addresses.table.field.state")
                            ,width: '8%'
                        }
                        ,zip: {
                            title: $.t("casefile:people.table.security-tags.addresses.table.field.zip")
                            ,width: '8%'
                        }
                        ,country: {
                            title: $.t("casefile:people.table.security-tags.addresses.table.field.country")
                            ,width: '8%'
                        }
                        ,created: {
                            title: $.t("casefile:people.table.security-tags.addresses.table.field.date-added")
                            ,width: '15%'
                            ,create: false
                            ,edit: false
                        }
                        ,creator: {
                            title: $.t("casefile:people.table.security-tags.addresses.table.field.added-by")
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedAddress(caseFileId, assocId, address);
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < address.id) {
                            CaseFile.Controller.viewUpdatedAddress(caseFileId, assocId, address);
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var addressId  = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < addressId) {
                            CaseFile.Controller.viewDeletedAddress(caseFileId, assocId, addressId);
                        }
                    }
                });
            }
        }

        ,Aliases: {
            create: function() {
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_PERSON_ALIAS        ,this.onModelAddedPersonAlias);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_PERSON_ALIAS      ,this.onModelUpdatedPersonAlias);
                Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_PERSON_ALIAS      ,this.onModelDeletedPersonAlias);
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
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='" + $.t("casefile:people.table.security-tags.aliases.table.title") + "'><i class='fa fa-users'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.Aliases.onOpen, CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES
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

                                //var caseFileId = CaseFile.View.getActiveCaseFileId();
                                var c = CaseFile.View.getActiveCaseFile();
                                if (CaseFile.Model.Detail.validateCaseFile(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = CaseFile.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($row, postData);
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
                            title: $.t("casefile:people.table.security-tags.aliases.table.field.type")
                            ,width: '15%'
                            ,options: CaseFile.Model.Lookup.getAliasTypes()
                        }
                        ,value: {
                            title: $.t("casefile:people.table.security-tags.aliases.table.field.value")
                            ,width: '30%'
                        }
                        ,created: {
                            title: $.t("casefile:people.table.security-tags.aliases.table.field.date-added")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: $.t("casefile:people.table.security-tags.aliases.table.field.added-by")
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedPersonAlias(caseFileId, assocId, personAlias);
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
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < personAlias.id) {
                            CaseFile.Controller.viewUpdatedPersonAlias(caseFileId, assocId, personAlias);
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var personAliasId = Acm.goodValue(record.id, 0);
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId && 0 < personAliasId) {
                            CaseFile.Controller.viewDeletedPersonAlias(caseFileId, assocId, personAliasId);
                        }
                    }
                });
            } //onOpen
        }
    }

    ,Documents: {
        create: function() {
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_TREE_NODE        ,this.onViewSelectedTreeNode);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedTreeNode: function(key) {
            var lastKeyPart = ObjNav.Model.Tree.Key.getLastKeyPart(key);
            if (CaseFile.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS == lastKeyPart) {
                DocTree.View.expandTopNode();
            }
        }
        ,onViewSelectedObject: function(nodeType, nodeId) {
            DocTree.Controller.viewChangedParent(nodeType, nodeId);
        }

        ,uploadForm: function(type, folderId, onCloseForm) {
            //var token = CaseFile.View.MicroData.token;
            var caseFileId = CaseFile.View.getActiveCaseFileId();
            var caseFile = CaseFile.View.getActiveCaseFile();
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                //var url = Acm.goodValue(CaseFile.View.MicroData.formUrls[report]);
                var url = null;
                var fileType = CaseFile.View.MicroData.findFileTypeByType(type);
                if (fileType) {
                    url = Acm.goodValue(fileType.url);
                }
                if (Acm.isNotEmpty(url)) {
                    // an apostrophe in case title will make Frevvo throw up.  Need to encode it here, then rules in
                    // the Frevvo form will decode it.
                    var caseTitle = Acm.goodValue(caseFile.title);
                    caseTitle = caseTitle.replace("'", "_0027_"); // 0027 is the Unicode string for apostrophe

                    url = url.replace("_data=(", "_data=(type:'case', caseId:'" + caseFileId
                        + "',caseNumber:'" + Acm.goodValue(caseFile.caseNumber)
                        + "',caseTitle:'" + caseTitle
                        + "',casePriority:'" + Acm.goodValue(caseFile.priority)
                        + "',folderId:'" + folderId
                        + "',"
                    );
                    Acm.Dialog.openWindow(url, "", 1060, $(window).height() - 30, onCloseForm);
                }
            }
        }
    }

    ,Documents_JTable_To_Retire: {
        create: function() {
            //for cases frevvo form is disabled in the properties file
            this.$formAddDocument = $("#formAddDocument");
            this.$btnAddDocument = $("#addDocument")
            this.$btnAddDocument.on("change", function(e) {CaseFile.View.Documents.onChangeFileInput(e, this);});
            this.$formAddDocument.submit(function(e) {CaseFile.View.Documents.onSubmitAddDocument(e, this);});

            this.$divDocuments    = $("#divDocs");
            this.createJTableDocuments(this.$divDocuments);
            AcmEx.Object.JTable.clickAddRecordHandler(this.$divDocuments, CaseFile.View.Documents.onClickSpanAddDocument);
            this.$spanAddDocument = this.$divDocuments.find(".jtable-toolbar-item-add-record");
            CaseFile.View.Documents.fillReportSelection();



            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_CASE_FILE     ,this.onModelRetrievedCaseFile);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT          ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_DOCUMENT          ,this.onModelAddedDocument);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_CREATED_CORRESPONDENCE  ,this.onModelCreatedCorrespondence);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE       ,this.onViewSelectedCaseFile);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT            ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLOSED_CASE_FILE         ,this.onViewClosedCaseFile);
        }
        ,onInitialized: function() {
        }

        ,onChangeFileInput: function(event, ctrl) {
            CaseFile.View.Documents.$formAddDocument.submit();
        }
        ,onSubmitAddDocument: function(event, ctrl) {
            event.preventDefault();
            var count = CaseFile.View.Documents.$btnAddDocument[0].files.length;
            var report = CaseFile.View.Documents.getSelectReportText();

            var fd = new FormData();
            fd.append("fileType", report);
            fd.append("parentObjectId", CaseFile.Model.getCaseFileId());
            fd.append("parentObjectType", CaseFile.Model.DOC_TYPE_CASE_FILE);
            for(var i = 0; i < count; i++ ){
                fd.append("files[]", CaseFile.View.Documents.$btnAddDocument[0].files[i]);
            }
            CaseFile.Service.Documents.uploadDocument(fd);
            this.$formAddDocument[0].reset();
        }
        ,onModelAddedDocument: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                CaseFile.Controller.viewClosedAddDocumentWindow(CaseFile.View.getActiveCaseFileId());
            }
        }
        ,onModelCreatedCorrespondence: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
            }
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }
        ,onViewClosedCaseFile: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }
        ,onViewAddedDocument: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divDocuments);
        }


        ,onClickSpanAddDocument: function(event, ctrl) {
            var enableFrevvoFormEngine = CaseFile.View.MicroData.formUrls.enableFrevvoFormEngine;
            var report = CaseFile.View.Documents.getSelectReport();
            var reportext = CaseFile.View.Documents.getSelectReportText();

            if(report == "roiFormUrl" || report == "electronicCommunicationFormUrl"){
                var token = CaseFile.View.MicroData.getToken();

                var caseFileId = CaseFile.View.getActiveCaseFileId();
                var caseFile = CaseFile.View.getActiveCaseFile();

                if (caseFile) {
                    var url = CaseFile.View.MicroData.getFormUrls()[report];
                    if (Acm.isNotEmpty(url)) {
                        // an apostrophe in case title will make Frevvo throw up.  Need to encode it here, then rules in
                        // the Frevvo form will decode it.
                        var caseTitle = Acm.goodValue(caseFile.title);
                        caseTitle = caseTitle.replace("'", "_0027_"); // 0027 is the Unicode string for apostrophe

                        url = url.replace("_data=(", "_data=(type:'case', caseId:'" + caseFileId
                            + "',caseNumber:'" + Acm.goodValue(caseFile.caseNumber)
                            + "',caseTitle:'" + caseTitle
                            + "',casePriority:'" + Acm.goodValue(caseFile.priority)
                            + "',");

                        Acm.Dialog.openWindow(url, "", 1060, $(window).height() - 30
                            ,function() {
                        		CaseFile.Controller.viewClosedAddDocumentWindow(CaseFile.View.getActiveCaseFileId());
                            }
                        );
                    }
                }
            }
            else if(report && report != ""){
                CaseFile.View.Documents.$btnAddDocument.click();
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
                + "<option value=''>"+ $.t("casefile:documents.form-document.document-type") +"</option>";

            if (formDocuments != null && formDocuments.length > 0) {
            	for (var i = 0; i < formDocuments.length; i ++) {
            		html += "<option value='" + formDocuments[i]["value"] + "'>" + formDocuments[i]["label"] + "</option>"
            	}
            }
                
            html += "<option value='mr'>"+ $.t("casefile:documents.form-document.medical-release") +"</option>"
                + "<option value='gr'>"+ $.t("casefile:documents.form-document.general-release") +"</option>"
                + "<option value='ev'>"+ $.t("casefile:documents.form-document.e-delivery") +"</option>"
                + "<option value='sig'>" +$.t("casefile:documents.form-document.sf86-signature") + "</option>"
                + "<option value='noi'>" + $.t("casefile:documents.form-document.notice-of-investigation") + "</option>"
                + "<option value='wir'>"+ $.t("casefile:documents.form-document.within-interview-request") +"</option>"
                + "<option value='ot'>" + $.t("casefile:documents.form-document.other") + "</option>"
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
        ,_makeJtData: function(documents, totalDocuments) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Acm.isNotEmpty(documents)) {
                for (var i = 0; i < documents.length; i++) {
                    if(CaseFile.Model.Documents.validateDocument(documents[i])){
                        var Record = {};
                        Record.id = Acm.goodValue(documents[i].objectId)
                        Record.title = Acm.goodValue(documents[i].name);
                        Record.created = Acm.getDateFromDatetime(documents[i].created);
                        Record.creator = Acm.__FixMe__getUserFullName(documents[i].creator);
                        jtData.Records.push(Record);
                    }
                }
                jtData.TotalRecordCount = Acm.goodValue(totalDocuments, 0);
            }
            return jtData;
        }
        , reloadDocs: function()
        {
            var divDocuments = $("#divDocs");
            CaseFile.View.Documents.createJTableDocuments(divDocuments);
        }
        , createJTableDocuments: function ($s) {
            AcmEx.Object.JTable.usePaging($s, {
                title: $.t("casefire:documents.table.title")
                ,paging: true
                ,sorting: true
                ,pageSize: 10 //Set page size (default: 10)
                , messages: {
                    addNewRecord: $.t("casefire:documents.msg.add-new-record")
                }
                , actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        var caseId = CaseFile.View.getActiveCaseFileId();
                        if ( ! caseId || 0 >= caseId )
                        {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        //var documentsCache = CaseFile.Model.Documents.cacheDocuments.get(caseId + "." + jtParams.jtStartIndex);
                        //if (CaseFile.Model.Documents.validateDocuments(documentsCache)) {
                        //    var documents = documentsCache.children;
                        //    var totalDocuments = documentsCache.totalChildren;
                        //    return CaseFile.View.Documents._makeJtData(documents, totalDocuments);
                        //} else {
                            return CaseFile.Service.Documents.retrieveDocumentsDeferred(caseId
                                ,postData
                                ,jtParams
                                ,sortMap
                                ,function(data) {
                                    if(CaseFile.Model.Documents.validateDocuments(data)){
                                        var documents = data.children;
                                        var totalDocuments = data.totalChildren;
                                        return CaseFile.View.Documents._makeJtData(documents, totalDocuments);
                                    }
                                    return AcmEx.Object.JTable.getEmptyRecords();
                                }
                                ,function(error) {
                                }
                            );
                        //}  //end else
                    }
                    ,createAction: function(postData, jtParams) {
                        //placeholder. this action should never be called
                        var rc = {"Result": "OK", "Record": {id:0, title:"", created:"", creator:""}};
                        return rc;
                    }
                }
                , fields: {
                    id: {
                        title: $.t("casefile:documents.table.field.id")
                        , key: true
                        , list: false
                        , create: false
                        , edit: false
                        , defaultvalue: 0
                    }
                    , title: {
                        title: $.t("casefile:documents.table.field.title")
                        , width: '50%'
                        , edit: false
                        , create: false
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + CaseFile.Service.Documents.API_DOWNLOAD_DOCUMENT_
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    , created: {
                        title: $.t("casefile:documents.table.field.date-added")
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                    , creator: {
                        title: $.t("casefile:documents.table.field.added-by")
                        , width: '15%'
                        , edit: false
                        , create: false
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
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_ASSIGNEE    ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_GROUP	    ,this.onModelSavedGroup);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
        }
        ,onModelSavedAssignee: function(caseFileId, assginee) {
            if (!assginee.hasError) {
                AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
            }
        }
        ,onModelSavedGroup: function(caseFileId, group) {
            if (!group.hasError) {
                AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
                CaseFile.Service.Lookup.retrieveAssignees();
            }
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
        }

        ,createJTableParticipants: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: $.t("casefile:participants.table.title")
                ,paging: true //fix me
                ,sorting: true //fix me
                ,pageSize: 10 //Set page size (default: 10)
                ,messages: {
                    addNewRecord: $.t("casefile:participants.msg.add-new-record")
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        //var caseFileId = CaseFile.View.getActiveCaseFileId();
                        var c = CaseFile.View.getActiveCaseFile();
                        if (CaseFile.Model.Detail.validateCaseFile(c)) {
                            for (var i = 0; i < c.participants.length; i++) {
                                var participant = c.participants[i];
                                var record = {};
                                record.id = Acm.goodValue(participant.id, 0);
                                // Here I am not taking user full name. It will be automatically shown because now 
                                // I am sending key-value object with key=username and value=fullname
                                record.title = Acm.goodValue(participant.participantLdapId);
                                //record.title = Acm.__FixMe__getUserFullName(Acm.goodValue(participant.participantLdapId));
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
                        //var caseFileId = CaseFile.View.getActiveCaseFileId();
                        var caseFile = CaseFile.View.getActiveCaseFile();
                        if (caseFile) {
                            rc.Record.title = record.title;
                            rc.Record.type = record.type;
                        }
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        //var caseFileId = CaseFile.View.getActiveCaseFileId();
                        var caseFile = CaseFile.View.getActiveCaseFile();
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
                        title: $.t("casefile:participants.table.field.id")
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
	                ,type: {
	                    title: $.t("casefile:participants.table.field.type")
	                    ,width: '30%'
	                    ,options: CaseFile.Model.Lookup.getParticipantTypes()
	                    ,display: function (data) {
                            if (data.record.type == '*') {
                            	// Default user. This is needed to show default user in the table.
                        		// I am setting it here, because i don't want to show it in the popup while
                        		// creating new participant. If we set it in the popup, it should be removed from here.
                        		// This is used only to recognize the * type.
                            	return '*';
                            } else {
                            	var options = CaseFile.Model.Lookup.getParticipantTypes();
                            	return options[data.record.type];
                            }
                        }
	                }
                    ,title: {
                        title: $.t("casefile:participants.table.field.name")
                        ,width: '70%'
                        ,dependsOn: 'type'
                        ,options: function (data) {
                        	if (data.dependedValues.type == '*') {
                        		// Default user. This is needed to show default user in the table.
                        		// I am setting it here, because i don't want to show it in the popup while
                        		// creating new participant. If we set it in the popup, it should be removed from here.
                        		// This is used only to recognize the * type.
                        		return {"*": "*"}
                        	}else if (data.dependedValues.type == 'owning group') {
                        		var caseFileId = CaseFile.View.getActiveCaseFileId();
                        		return Acm.createKeyValueObject(CaseFile.Model.Lookup.getGroups(caseFileId));
                    		} else {
                    			return Acm.createKeyValueObject(CaseFile.Model.Lookup.getUsers());
                    		}
                        }
                    }
                }
                ,recordAdded : function (event, data) {
                    var record = data.record;
                    var caseFileId = CaseFile.View.getActiveCaseFileId();
                    if (0 < caseFileId) {
                        var participant = {};
                        participant.participantLdapId = record.title;
                        participant.participantType = record.type;
                        CaseFile.Controller.viewAddedParticipant(caseFileId, participant);
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = CaseFile.View.getActiveCaseFileId();
                    var c = CaseFile.View.getActiveCaseFile();
                    if (c && Acm.isArray(c.participants)) {
                        if (0 < c.participants.length && whichRow < c.participants.length) {
                            var participant = c.participants[whichRow];
                            participant.participantLdapId = record.title;
                            participant.participantType = record.type;
                            CaseFile.Controller.viewUpdatedParticipant(caseFileId, participant);
                        }
                    }
                }
                ,recordDeleted : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var caseFileId = CaseFile.View.getActiveCaseFileId();
                    var c = CaseFile.View.getActiveCaseFile();
                    if (c && Acm.isArray(c.participants)) {
                        if (0 < c.participants.length && whichRow < c.participants.length) {
                            var participant = c.participants[whichRow];
                            CaseFile.Controller.viewDeletedParticipant(caseFileId, participant.id);
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
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_NOTE        ,this.onModelAdddNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_NOTE      ,this.onModelUpdatedNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DELETED_NOTE      ,this.onModelDeletedNote);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

//
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
        }
        ,onModelAdddNote: function(caseFile) {
            if (caseFile.hasError) {
                //show error
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
            }
        }
        ,onModelUpdatedNote: function(caseFile) {
            if (caseFile.hasError) {
                //show error
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
            }
        }
        ,onModelDeletedNote: function(noteId) {
            if (noteId.hasError) {
                //show error
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
            }
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
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
                    title: $.t("casefile:notes.table.title")
                    ,paging: true
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,messages: {
                        addNewRecord: $.t("casefile:notes.msg.add-new-record")
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var caseFileId = CaseFile.View.getActiveCaseFileId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var noteList = CaseFile.Model.Notes.cacheNoteList.get(caseFileId);
                            if (noteList) {
                                return CaseFile.View.Notes._makeJtData(noteList);

                            } else {
                                return CaseFile.Service.Notes.retrieveNoteListDeferred(caseFileId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        var noteList = data;
                                        return CaseFile.View.Notes._makeJtData(noteList);
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            var caseFileId = CaseFile.View.getActiveCaseFileId();
                            var caseFile = CaseFile.View.getActiveCaseFile();
                            if (caseFile) {
                                rc.Record.parentId = Acm.goodValue(caseFileId, 0);
                                rc.Record.parentType = CaseFile.Model.DOC_TYPE_CASE_FILE;
                                rc.Record.note = record.note;
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var caseFileId = CaseFile.View.getActiveCaseFileId();
                            var caseFile = CaseFile.View.getActiveCaseFile();
                            if (caseFile) {
                                rc.Record.parentId = Acm.goodValue(caseFileId, 0);
                                rc.Record.parentType = CaseFile.Model.DOC_TYPE_CASE_FILE;
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
                            title: $.t("casefile:notes.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                            ,defaultvalue : 0
                        }
                        ,note: {
                            title: $.t("casefile:notes.table.field.note")
                            ,type: 'textarea'
                            ,width: '50%'
                            ,edit: true
                        }
                        ,created: {
                            title: $.t("casefile:notes.table.field.created")
                            ,width: '15%'
                            ,edit: false
                            ,create: false
                        }
                        ,creator: {
                            title: $.t("casefile:notes.table.field.creator")
                            ,width: '15%'
                            ,edit: false
                            ,create: false
                        }
                    } //end field
                    ,formCreated: function (event, data) {
                        var $noteForm = $(".jtable-create-form");
                        //other constraints can be added
                        //as needed as shown below
                        var opt = {
                            resizable: false
                            //,autoOpen: false,
                            //height:200,
                            //width:200,
                            //modal: true,
                            //etc..
                        };
                        $noteForm.parent().dialog(opt);
                    }
                    ,recordAdded : function (event, data) {
                        var record = data.record;
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId) {
                            var noteToSave = {};
                            //noteToSave.id = record.id;
                            noteToSave.id = 0;
                            noteToSave.note = record.note;
                            noteToSave.created = Acm.getCurrentDayInternal(); //record.created;
                            noteToSave.creator = record.creator;   //record.creator;
                            noteToSave.parentId = caseFileId;
                            noteToSave.parentType = CaseFile.Model.DOC_TYPE_CASE_FILE;
                            //CaseFile.Service.Notes.saveNote(noteToSave);
                            CaseFile.Controller.viewAddedNote(noteToSave);
                        }
                    }
                    ,recordUpdated: function(event,data){
                        var whichRow = data.row.prevAll("tr").length;
                        var record = data.record;
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId) {
                            var notes = CaseFile.Model.Notes.cacheNoteList.get(caseFileId);
                            if (notes) {
                                if(notes[whichRow]){
                                    notes[whichRow].note = record.note;
                                    //CaseFile.Service.Notes.saveNote(notes[whichRow]);
                                    CaseFile.Controller.viewUpdatedNote(notes[whichRow]);
                                }
                            }
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId) {
                            var notes = CaseFile.Model.Notes.cacheNoteList.get(caseFileId);
                            if (notes) {
                                if(notes[whichRow]){
                                    //CaseFile.Service.Notes.deleteNote(notes[whichRow].id);
                                    CaseFile.Controller.viewDeletedNote(notes[whichRow].id);
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
            AcmEx.Object.JTable.clickAddRecordHandler(this.$divTasks, CaseFile.View.Tasks.onClickSpanAddTask);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_TASKS    ,this.onModelRetrievedTasks);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_COMPLETED_TASK      ,this.onModelCompletedTask);

        }
        ,onInitialized: function() {
        }

        ,URL_TASK_DETAIL:  "/plugin/task/"
        ,URL_NEW_TASK_:    "/plugin/task/wizard?parentType=CASE_FILE&reference="


        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
        }
        ,onModelRetrievedTasks: function(tasks) {
            if (tasks.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
            }
        }
        ,onModelCompletedTask: function(task) {
            if (task.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
            }
        }
        ,onClickSpanAddTask: function(event, ctrl) {
            //var caseFileId = CaseFile.View.getActiveCaseFileId();
            var caseFile = CaseFile.View.getActiveCaseFile();
            if (caseFile) {
                var caseNumber = Acm.goodValue(caseFile.caseNumber);
                var url = CaseFile.View.Tasks.URL_NEW_TASK_  + caseNumber;
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

            CaseFile.Service.Tasks.completeTask(taskId);
        }
        ,onClickBtnTaskWithOutcome : function(outcome,taskId) {
            var tasks = CaseFile.Model.Tasks.cacheTasks.get(0);
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
                CaseFile.Service.Tasks.completeTaskWithOutcome(task);
            }

            //alert("task with outcome");

        }
        ,retrieveTaskOutcome : function(taskId){
            var tasks = CaseFile.Model.Tasks.cacheTasks.get(0);
            var $a = $("");
            if(tasks){
                for(var i = 0; i < tasks.length; i++){
                    if(tasks[i].taskId == taskId){
                        var task = tasks[i];
                        if(task.adhocTask == true && task.completed == false){
                            $a = $("<div class='btn-group-task'><button class='btn btn-default btn-sm adhoc' title='" + $.t("casefile:tasks.buttons.complete-task") + "'>"+ $.t("casefile:tasks.buttons.complete") +"</button></div>");
                        }
                        else if(task.adhocTask == false && task.completed == false && task.availableOutcomes != null){
                             var availableOutcomes = task.availableOutcomes;
                             for(var j = 0; j < availableOutcomes.length; j++ ){
                                 if(availableOutcomes[j].name == 'COMPLETE'){
                                 $a = $("<div class='btn-group-task'><button class='btn btn-default btn-sm businessProcess' id='COMPLETE' title='" + $.t("casefile:tasks.buttons.complete-task-outcome") + "'>" + $.t("casefile:tasks.buttons.complete") + "</button></div>");
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
                    title: $.t("casefile:tasks.table.title")
                    ,multiselect: false
                    ,selecting: false
                    ,selectingCheckboxes: false
                    ,paging: true
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:tasks.msg.add-new-record")
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var caseFileId = CaseFile.View.getActiveCaseFileId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var taskList = CaseFile.Model.Tasks.cacheTaskSolr.get(caseFileId);
                            if (taskList) {
                                return CaseFile.View.Tasks._makeJtData(taskList);

                            } else {
                                return CaseFile.Service.Tasks.retrieveTaskListDeferred(caseFileId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        var taskList = data;
                                        return CaseFile.View.Tasks._makeJtData(taskList);
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
                            title: $.t("casefile:tasks.table.field.id")
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
                            title: $.t("casefile:tasks.table.field.title")
                            ,width: '30%'
                            ,display: function (commData) {
                                var a = "<a href='" + App.getContextPath() + '/plugin/task/' +
                                    + ((0 >= commData.record.id)? "#" : commData.record.id)
                                    + "'>" + commData.record.title + "</a>";
                                return $(a);
                            }
                        }
                        ,assignee: {
                            title: $.t("casefile:tasks.table.field.assignee")
                            ,sorting: false
                            ,width: '25'
                        }
                        ,created: {
                            title: $.t("casefile:tasks.table.field.created")
                            ,width: '15%'
                        }
                        ,priority: {
                            title: $.t("casefile:tasks.table.field.priority")
                            ,width: '10%'
                        }
                        ,dueDate: {
                            title: $.t("casefile:tasks.table.field.due-date")
                            ,width: '15%'
                        }
                        ,status: {
                            title: $.t("casefile:tasks.table.field.status")
                            ,width: '10%'
                        }
                        ,description: {
                            title: $.t("casefile:tasks.table.field.action")
                            ,width: '10%'
                            ,edit: false
                            ,create: false
                            ,display: function (commData) {
                                var $a = CaseFile.View.Tasks.retrieveTaskOutcome(commData.record.id);
                                $a.on("click", ".businessProcess", function(e) {CaseFile.View.Tasks.onClickBtnTaskWithOutcome(e.target.id,commData.record.id);$a.hide();});
                                $a.on("click", ".adhoc", function(e) {CaseFile.View.Tasks.onClickBtnCompleteTask(commData.record.id);$a.hide();});
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
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_DOCUMENT         ,this.onModelAddedDocument);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_CREATED_CORRESPONDENCE ,this.onModelCreatedCorrespondence);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
        }
        ,onModelAddedDocument: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
            }
        }
        ,onModelCreatedCorrespondence: function(caseFileId) {
            if (caseFileId.hasError) {
                ;
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
            }
        }
        ,onViewSelectedObject: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
        }

        ,createJTableReferences: function($jt) {
            //var sortMap = {};
            //sortMap["modified"] = "modified";

            AcmEx.Object.JTable.useBasic($jt, {
                    title: $.t("casefile:references.table.title")
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:references.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function(postData, jtParams) {
                            var caseFileId = CaseFile.View.getActiveCaseFileId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            var c = CaseFile.View.getActiveCaseFile();
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
                            title: $.t("casefile:references.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                            ,defaultvalue : 0
                        }
                        ,title: {
                            title: $.t("casefile:references.table.field.title")
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
                            title: $.t("casefile:references.table.field.modified")
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                        ,type: {
                            title: $.t("casefile:references.table.field.reference-type")
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                        ,status: {
                            title: $.t("casefile:references.table.field.status")
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
    ,History: {
        create: function() {
            this.$divHistory          = $("#divHistory");
            this.createJTableHistory(this.$divHistory);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(CaseFile.View.History.$divHistory);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(CaseFile.View.History.$divHistory);
        }

        ,_makeJtData: function(history) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Acm.isNotEmpty(history.events)) {
                var events = history.events;
                for (var i = 0; i < events.length; i++) {
                    if(CaseFile.Model.History.validateEvent(events[i])){
                        var Record = {};
                        Record.eventType = Acm.goodValue(events[i].eventType);
                        Record.eventDate = Acm.getDateFromDatetime(events[i].eventDate);
                        Record.userId = Acm.__FixMe__getUserFullName(events[i].userId);
                        jtData.Records.push(Record);
                    }
                }
                jtData.TotalRecordCount = history.totalEvents;
            }
            return jtData;
        }
        ,createJTableHistory: function($jt) {
            var sortMap = {};
            sortMap["created"] = "created";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: $.t("casefile:history.table.title")
                    ,paging: true
                    ,sorting: true
                    ,pageSize: 10 //Set page size (default: 10)
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var caseFileId = CaseFile.View.getActiveCaseFileId();
                            if (0 >= caseFileId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var historyCache = CaseFile.Model.History.cacheHistory.get(caseFileId + "." + jtParams.jtStartIndex);
                            if (CaseFile.Model.History.validateHistory(historyCache)) {
                                var history = {};
                                history.events = historyCache.resultPage;
                                history.totalEvents = historyCache.totalCount;
                                return CaseFile.View.History._makeJtData(history);
                            } else {
                                return CaseFile.Service.History.retrieveHistoryDeferred(caseFileId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        if(CaseFile.Model.History.validateHistory(data)){
                                            var history = {};
                                            history.events = data.resultPage;
                                            history.totalEvents = data.totalCount;
                                            return CaseFile.View.History._makeJtData(history);
                                        }
                                        return AcmEx.Object.JTable.getEmptyRecords();
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }
                    }
                    , fields: {
                        id: {
                            title: $.t("casefile:history.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }, eventType: {
                            title: $.t("casefile:history.table.field.event-name")
                            ,width: '50%'
                        }, eventDate: {
                            title: $.t("casefile:history.table.field.date")
                            ,width: '25%'
                        }, userId: {
                            title: $.t("casefile:history.table.field.user")
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
            this.$divCorrespondence = $("#divCorrespondence");
            this.createJTableCorrespondence(this.$divCorrespondence);

            AcmEx.Object.JTable.clickAddRecordHandler(this.$divCorrespondence, CaseFile.View.Correspondence.onClickSpanAddDocument);
            this.$spanAddTemplate = this.$divCorrespondence.find(".jtable-toolbar-item-add-record");
            CaseFile.View.Correspondence.fillReportSelection();

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT          ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_CREATED_CORRESPONDENCE  ,this.onModelCreatedCorrespondence);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT            ,this.onViewSelectedObject);
        }
        , onInitialized: function () {
        }

        ,API_DOWNLOAD_DOCUMENT_      : "/api/latest/plugin/ecm/download/byId/"

        , onModelRetrievedObject: function (objData) {
            AcmEx.Object.JTable.load(CaseFile.View.Correspondence.$divCorrespondence);
        }
        , onViewSelectedObject: function (objType, objId) {
            AcmEx.Object.JTable.load(CaseFile.View.Correspondence.$divCorrespondence);
        }
        ,onModelCreatedCorrespondence: function(correspondence) {
            if (correspondence.hasError) {
                Acm.Dialog.info(correspondence.errorMsg);
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Correspondence.$divCorrespondence);
            }
        }

        ,getSelectTemplate: function() {
            return Acm.Object.getSelectValue(this.$spanAddTemplate.prev().find("select"));
        }
        ,onClickSpanAddDocument: function(event, ctrl) {
            var caseFileId = CaseFile.View.getActiveCaseFileId();
            var templateName = CaseFile.View.Correspondence.getSelectTemplate();
            CaseFile.Controller.viewClickedAddCorrespondence(caseFileId, templateName);
        }

        ,_makeJtData: function(correspondences, totalCorrespondences) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Acm.isNotEmpty(correspondences)) {
                for (var i = 0; i < correspondences.length; i++) {
                    if(CaseFile.Model.Correspondence.validateCorrespondence(correspondences[i])){
                        var Record = {};
                        Record.id = Acm.goodValue(correspondences[i].objectId)
                        Record.title = Acm.goodValue(correspondences[i].name);
                        Record.created = Acm.getDateFromDatetime(correspondences[i].created);
                        Record.creator = Acm.__FixMe__getUserFullName(correspondences[i].creator);
                        jtData.Records.push(Record);
                    }
                }
                jtData.TotalRecordCount = Acm.goodValue(totalCorrespondences);
            }
            return jtData;
        }

        ,fillReportSelection: function() {
            var html = "<span>"
                + "<select class='input-sm form-control input-s-sm inline v-middle' id='docDropDownValue'>"
                + "<option value='GeneralRelease.docx'>" + $.t("casefile:correspondence.form-document.general-release") + "</option>"
                + "<option value='MedicalRelease.docx'>" + $.t("casefile:correspondence.form-document.medical-release") + "</option>"
                + "<option value='ClearanceGranted.docx'>" + $.t("casefile:correspondence.form-document.clearance-granted") + "</option>"
                + "<option value='ClearanceDenied.docx'>" + $.t("casefile:correspondence.form-document.clearance-denied") + "</option>"
                + "<option value='NoticeofInvestigation.docx'>" + $.t("casefile:correspondence.form-document.notice-of-investigation") + "</option>"
                + "<option value='InterviewRequest.docx'>" + $.t("casefile:correspondence.form-document.witness-interview-request") + "</option>"
                + "</select>"
                + "</span>"
                ;


            this.$spanAddTemplate.before(html);
        }

        , createJTableCorrespondence: function ($s) {
            AcmEx.Object.JTable.usePaging($s, {
                title: $.t("casefile:correspondence.table.title")
                ,paging: true
                ,sorting: true
                ,pageSize: 10 //Set page size (default: 10)
                , messages: {
                    addNewRecord: $.t("casefile:correspondence.msg.add-new-record")
                }
                , actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        var correspondencesCache = CaseFile.Model.Correspondence.cacheCorrespondences.get(caseFileId + "." + jtParams.jtStartIndex);
                        if (CaseFile.Model.Correspondence.validateCorrespondences(correspondencesCache)) {
                            var correspondences = correspondencesCache.children;
                            var totalCorrespondences = correspondencesCache.totalChildren;
                            return CaseFile.View.Correspondence._makeJtData(correspondences, totalCorrespondences);
                        } else {
                            return CaseFile.Service.Correspondence.retrieveCorrespondenceDeferred(caseFileId
                                ,postData
                                ,jtParams
                                ,sortMap
                                ,function(data) {
                                    if(CaseFile.Model.Correspondence.validateCorrespondences(data)){
                                        var correspondences = data.children;
                                        var totalCorrespondences = data.totalChildren;
                                        return CaseFile.View.Correspondence._makeJtData(correspondences, totalCorrespondences);
                                    }
                                    return AcmEx.Object.JTable.getEmptyRecords();
                                }
                                ,function(error) {
                                }
                            );
                        }  //end else
                    }
                    ,createAction: function(postData, jtParams) {
                        //placeholder. this action should never be called
                        var rc = {"Result": "OK", "Record": {id:0, title:"", created:"", creator:""}};
                        return rc;
                    }
                }
                , fields: {
                    id: {
                        title: $.t("casefile:correspondence.table.field.id")
                        , key: true
                        , list: false
                        , create: false
                        , edit: false
                        , defaultvalue: 0
                    }
                    , title: {
                        title: $.t("casefile:correspondence.table.field.title")
                        , width: '50%'
                        , edit: false
                        , create: false
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + CaseFile.View.Correspondence.API_DOWNLOAD_DOCUMENT_
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    , created: {
                        title: $.t("casefile:correspondence.table.field.created")
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                    , creator: {
                        title: $.t("casefile:correspondence.table.field.creator")
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                }
            });
        }
    }

    ,OutlookCalendar: {
        create: function() {
            this.$outlookCalendar          = $(".calendar");
            this.$weekView                 = $("#weekview");
            this.$monthView                = $("#monthview");
            this.$dayView                  = $("#dayview");
            this.$btnRefreshCalendar       = $("#refreshCalendar");

            this.$btnRefreshCalendar.on("click", function(e) {CaseFile.View.OutlookCalendar.onClickbtnRefreshCalendar(e, this);});

            this.createOutlookCalendarWidget(this.$outlookCalendar);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_OUTLOOK_CALENDAR_ITEMS     ,this.onModelRetrievedOutlookCalendarItem);
        }
        ,onInitialized: function() {
        }
        ,onViewSelectedObject: function(nodeType, nodeId) {
            CaseFile.View.OutlookCalendar.$outlookCalendar.html("");
            CaseFile.View.OutlookCalendar.createOutlookCalendarWidget(CaseFile.View.OutlookCalendar.$outlookCalendar);
        }
        ,onModelRetrievedOutlookCalendarItem: function(outlookCalendarItems){
            if(outlookCalendarItems.hasError){
                App.View.MessageBoard.show($.t("casefile:outlook-calendar.msg.error-occurred"), outlookCalendarItems.errorMsg);
            }
            else{
                CaseFile.View.OutlookCalendar.$outlookCalendar.html("");
                CaseFile.View.OutlookCalendar.createOutlookCalendarWidget(CaseFile.View.OutlookCalendar.$outlookCalendar);
            }
        }
        ,onClickbtnRefreshCalendar: function(){
                CaseFile.Controller.viewRefreshedOutlookCalendar(CaseFile.View.getActiveCaseFileId());
        }
        ,createCalendarSource:function(){
            var calendarSource = [];
            var outlookCalendarItems = CaseFile.Model.OutlookCalendar.cacheOutlookCalendarItems.get(CaseFile.View.getActiveCaseFileId());
            if(CaseFile.Model.OutlookCalendar.validateOutlookCalendarItems(outlookCalendarItems)){
                for(var i = 0; i<outlookCalendarItems.items.length; i++){
                    if(CaseFile.Model.OutlookCalendar.validateOutlookCalendarItem(outlookCalendarItems.items[i])) {
                        var outlookCalendarItem = {};
                        outlookCalendarItem.id = Acm.goodValue(outlookCalendarItems.items[i].id);
                        outlookCalendarItem.title = Acm.goodValue(outlookCalendarItems.items[i].subject);
                        outlookCalendarItem.start = Acm.goodValue(outlookCalendarItems.items[i].startDate);
                        outlookCalendarItem.end = Acm.goodValue(outlookCalendarItems.items[i].endDate);
                        outlookCalendarItem.detail = CaseFile.View.OutlookCalendar.makeDetail(outlookCalendarItems.items[i]);
                        outlookCalendarItem.className = Acm.goodValue("b-l b-2x b-info");
                        outlookCalendarItem.allDay = Acm.goodValue(outlookCalendarItems.items[i].allDayEvent);
                        calendarSource.push(outlookCalendarItem);
                    }
                }
            }
            return calendarSource;
        }

        ,makeDetail: function(calendarItem){
            if(CaseFile.Model.OutlookCalendar.validateOutlookCalendarItem(calendarItem)) {
                var body = Acm.goodValue(calendarItem.body) + "</br>";
                var startDateTime = Acm.getDateTimeFromDatetime(calendarItem.startDate);
                var startDateTimeWithoutSecond = $.t("casefile:outlook-calendar.label.start") + " " + startDateTime.substring(0,startDateTime.lastIndexOf(":"))+ "</br>";
                var endDateTime = Acm.getDateTimeFromDatetime(calendarItem.endDate);
                var endDateTimeWithoutSecond = $.t("casefile:outlook-calendar.label.end") + " " + endDateTime.substring(0,endDateTime.lastIndexOf(":"))+ "</br>";
                var detail = body + startDateTimeWithoutSecond + endDateTimeWithoutSecond
                return detail;
            }
        }

        ,createOutlookCalendarWidget: function($s){
            var calendarSource = this.createCalendarSource();
            var addDragEvent = function($this){
                // create an Event Object (http://arshaw.com/fullcalendar/docs/event_data/Event_Object/)
                // it doesn't need to have a start or end
                var eventObject = {
                    title: $.trim($this.text()), // use the element's text as the event title
                    className: $this.attr('class').replace('label','')
                };

                // store the Event Object in the DOM element so we can get to it later
                $this.data('eventObject', eventObject);

                // make the event draggable using jQuery UI
                $this.draggable({
                    zIndex: 999,
                    revert: true,      // will cause the event to go back to its
                    revertDuration: 0  //  original position after the drag
                });
            };

            $s.fullCalendar({
                header: {
                    left: 'prev',
                    center: 'title',
                    right: 'next'
                },
                timeFormat: 'h(:mm)t {-h(:mm)t}',
                displayEventEnd : true,
                editable: true,
                //disable fullcalendar droppable as it creates conflict with the doctree's.
                //looks like fullcalendar uses the generic jquery draggable
                //we might need to add our own external draggable event handlers
                //tailored for fullcalendar
                droppable: false, // this allows things to be dropped onto the calendar !!!
                drop: function(date, allDay) { // this function is called when something is dropped

                    // retrieve the dropped element's stored Event Object
                    var originalEventObject = $(this).data('eventObject');

                    // we need to copy it, so that multiple events don't have a reference to the same object
                    var copiedEventObject = $.extend({}, originalEventObject);

                    // assign it the date that was reported
                    copiedEventObject.start = date;
                    copiedEventObject.allDay = allDay;

                    // render the event on the calendar
                    // the last `true` argument determines if the event "sticks" (http://arshaw.com/fullcalendar/docs/event_rendering/renderEvent/)
                    this.$outlookCalendar.fullCalendar('renderEvent', copiedEventObject, true);

                    // is the "remove after drop" checkbox checked?
                    if ($('#drop-remove').is(':checked')) {
                        // if so, remove the element from the "Draggable Events" list
                        $(this).remove();
                    }

                }
                ,events: calendarSource
                ,eventRender: function (event, element) {
                    element.qtip({
                        content: {
                            text: Acm.goodValue(event.detail),
                            title: {
                                text: Acm.goodValue(event.title)
                            }
                        }
                        ,position: {
                            my: 'right center',
                            at: 'left center',
                            target: 'mouse',
                            viewport: $s,
                            adjust: {
                                mouse: false,
                                scroll: false
                            }
                        }
                        ,style: {
                            classes: "qtip-rounded qtip-shadow"
                        }
                        ,show: { solo: true} //, ready: true, when: false
                        ,hide: { when: 'mouseout', fixed: true}
                    });
                }
            });
            $('#myEvents').on('change', function(e, item){
                addDragEvent($(item));
            });

            $('#myEvents li > div').each(function() {
                addDragEvent($(this));
            });

            this.$dayView.on('click', function() {
                $('.calendar').fullCalendar('changeView', 'agendaDay')
            });

            this.$weekView.on('click', function() {
                $('.calendar').fullCalendar('changeView', 'agendaWeek')
            });

            this.$monthView.on('click', function() {
                $('.calendar').fullCalendar('changeView', 'month')
            });

        }
    }

    ,Time: {
        create: function() {
            this.$divTime          = $("#divTime");
            this.createJTableTime(this.$divTime);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_TIMESHEETS     ,this.onModelRetrievedTimesheets);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(CaseFile.View.Time.$divTime);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(CaseFile.View.Time.$divTime);
        }
        ,onModelRetrievedTimesheets: function(timesheet){
            AcmEx.Object.JTable.load(CaseFile.View.Time.$divTime);
        }

        ,findTotalHours: function(timeRecords){
            var totalHours = 0;
            if(Acm.isArray(timeRecords) && Acm.isNotEmpty(timeRecords)) {
                for (var i = 0; i < timeRecords.length; i++) {
                    if (CaseFile.Model.Time.validateTimeRecord(timeRecords[i])) {
                        var timeRecord = timeRecords[i];
                        if (Acm.isNotEmpty(timeRecord.objectId) && Acm.compare(Acm.goodValue(timeRecord.objectId), CaseFile.View.getActiveCaseFileId())) {
                            totalHours += Acm.goodValue(timeRecord.value);
                        }
                    }
                }
            }
            return totalHours;
        }
        ,_makeJtData: function(timesheets) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            for(var j = 0; j < timesheets.length; j++){
                if(CaseFile.Model.Time.validateTimesheet(timesheets[j])){
                    var timesheet = timesheets[j];
                    var Record = {};
                    Record.id = Acm.goodValue(timesheet.id);
                    Record.name = $.t("casefile:time.table.label.timesheet") + " " + Acm.getDateFromDatetime(timesheet.startDate) + " - " + Acm.getDateFromDatetime(timesheet.endDate);
                    Record.type = CaseFile.Model.DOC_TYPE_TIMESHEET;
                    Record.status = Acm.goodValue(timesheet.status);
                    Record.username = Acm.goodValue(timesheet.creator);
                    Record.hours = Acm.goodValue(CaseFile.View.Time.findTotalHours(timesheet.times));
                    Record.modified = Acm.getDateFromDatetime(timesheet.modified);
                    jtData.Records.push(Record);
                }
            }
            return jtData;
        }
        ,createJTableTime: function($jt) {
            AcmEx.Object.JTable.useBasic($jt
                , {
                    title: $.t("casefile:time.table.title")
                    , sorting: true
                    , actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var timesheets = CaseFile.Model.Time.cacheTimesheets.get(CaseFile.View.getActiveCaseFileId());
                            if (CaseFile.Model.Time.validateTimesheets(timesheets)) {
                                rc = CaseFile.View.Time._makeJtData(timesheets);
                            }
                            return rc;
                        }
                    }
                    , fields: {
                        id: {
                            title: $.t("casefile:time.table.field.id")
                            , key: true
                            , list: false
                            , create: false
                            , edit: false
                        }, name: {
                            title: $.t("casefile:time.table.field.form-name")
                            , width: '20%'
                            ,display: function(data) {
                                var url = App.buildObjectUrl(Acm.goodValue(data.record.type), Acm.goodValue(data.record.id), "#");
                                var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.name) + "</a>");
                                return $lnk;
                            }
                        }, username: {
                            title: $.t("casefile:time.table.field.username")
                            , width: '10%'
                        }, hours: {
                            title: $.t("casefile:time.table.field.total-hours")
                            , width: '10%'
                        }, modified: {
                            title: $.t("casefile:time.table.field.modified-date")
                            , width: '10%'
                        }, status: {
                            title: $.t("casefile:time.table.field.status")
                            , width: '10%'
                        }
                    } //end field
                } //end args
            );
            $jt.jtable('load');
        }
    }


    ,Cost: {
        create: function() {
            this.$divCost          = $("#divCost");
            this.createJTableCost(this.$divCost);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_COSTSHEETS     ,this.onModelRetrievedCostsheets);

        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(CaseFile.View.Cost.$divCost);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(CaseFile.View.Cost.$divCost);
        }

        ,onModelRetrievedCostsheets: function(costsheet){
            AcmEx.Object.JTable.load(CaseFile.View.Cost.$divCost);
        }

        ,findTotalCost: function(costRecords){
            var totalCost = 0;
            if(Acm.isArray(costRecords) && Acm.isNotEmpty(costRecords)){
                for(var i = 0; i < costRecords.length; i++){
                    if(CaseFile.Model.Cost.validateCostRecord(costRecords[i])){
                        var costRecord = costRecords[i];
                        if(Acm.isNotEmpty(costRecord.value)){
                            totalCost += Acm.goodValue(costRecord.value);
                        }
                    }
                }
            }
            return totalCost;
        }
        ,_makeJtData: function(costsheets) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            for(var j = 0; j < costsheets.length; j++){
                if(CaseFile.Model.Cost.validateCostsheet(costsheets[j])){
                    var costsheet = costsheets[j];
                    var Record = {};
                    Record.id = Acm.goodValue(costsheet.id);
                    Record.name = $.t("casefile:cost.table.label.costsheet") + " " + Acm.goodValue(costsheet.parentNumber);
                    Record.type = CaseFile.Model.DOC_TYPE_COSTSHEET;
                    Record.status = Acm.goodValue(costsheet.status);
                    Record.username = Acm.goodValue(costsheet.creator);
                    Record.cost = Acm.goodValue(CaseFile.View.Cost.findTotalCost(costsheet.costs));
                    Record.modified = Acm.getDateFromDatetime(costsheet.modified);
                    jtData.Records.push(Record);
                }
            }
            return jtData;
        }
        ,createJTableCost: function($jt) {
            AcmEx.Object.JTable.useBasic($jt
                ,{
                    title: $.t("casefile:cost.table.title")
                    ,sorting: true
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var costsheets = CaseFile.Model.Cost.cacheCostsheets.get(CaseFile.View.getActiveCaseFileId());
                            if (CaseFile.Model.Cost.validateCostsheets(costsheets)) {
                                rc = CaseFile.View.Cost._makeJtData(costsheets);
                            }
                            return rc;
                        }
                    }

                    ,fields: {
                        id: {
                            title: $.t("casefile:cost.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }, name: {
                            title: $.t("casefile:cost.table.field.form-name")
                            ,width: '20%'
                            ,display: function(data) {
                                var url = App.buildObjectUrl(Acm.goodValue(data.record.type), Acm.goodValue(data.record.id), "#");
                                var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.name) + "</a>");
                                return $lnk;
                            }
                        }, username: {
                            title: $.t("casefile:cost.table.field.username")
                            ,width: '10%'
                        }, cost: {
                            title: $.t("casefile:cost.table.field.total-cost")
                            ,width: '10%'
                        }, modified: {
                            title: $.t("casefile:cost.table.field.modified-date")
                            ,width: '10%'
                        }, status: {
                            title: $.t("casefile:cost.table.field.status")
                            ,width: '10%'
                        }
                    } //end field
                } //end arg
            );
        }
    }


};

