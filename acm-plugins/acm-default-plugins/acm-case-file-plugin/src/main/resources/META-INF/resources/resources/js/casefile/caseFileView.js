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
        if (CaseFile.View.Ribbon.create)          {CaseFile.View.Ribbon.create();}
        if (CaseFile.View.Action.create)          {CaseFile.View.Action.create();}
        if (CaseFile.View.DetailNote.create)      {CaseFile.View.DetailNote.create();}
        if (CaseFile.View.People.create)    	  {CaseFile.View.People.create();}
        if (CaseFile.View.Documents.create)       {CaseFile.View.Documents.create();}
        if (CaseFile.View.Participants.create)    {CaseFile.View.Participants.create();}
        if (CaseFile.View.Notes.create)           {CaseFile.View.Notes.create();}
        if (CaseFile.View.Tasks.create)           {CaseFile.View.Tasks.create();}
        if (CaseFile.View.References.create)      {CaseFile.View.References.create();}
        if (CaseFile.View.History.create)         {CaseFile.View.History.create();}
        if (CaseFile.View.Correspondence.create)  {CaseFile.View.Correspondence.create();}
        if (CaseFile.View.Time.create)            {CaseFile.View.Time.create();}
        if (CaseFile.View.Cost.create)            {CaseFile.View.Cost.create();}
        if (CaseFile.View.Calendar.create)        {CaseFile.View.Calendar.create();}

        // uncomment to override default jtable
        // popups and use ArkCase messageboard:
        // App.View.MessageBoard.useAcmMessageBoard();
    }
    ,onInitialized: function() {
        if (CaseFile.View.MicroData.onInitialized)       {CaseFile.View.MicroData.onInitialized();}
        if (CaseFile.View.Navigator.onInitialized)       {CaseFile.View.Navigator.onInitialized();}
        if (CaseFile.View.Content.onInitialized)         {CaseFile.View.Content.onInitialized();}
        if (CaseFile.View.Ribbon.onInitialized)          {CaseFile.View.Ribbon.onInitialized();}
        if (CaseFile.View.Action.onInitialized)          {CaseFile.View.Action.onInitialized();}
        if (CaseFile.View.DetailNote.onInitialized)      {CaseFile.View.DetailNote.onInitialized();}
        if (CaseFile.View.People.onInitialized)          {CaseFile.View.People.onInitialized();}
        if (CaseFile.View.Documents.onInitialized)       {CaseFile.View.Documents.onInitialized();}
        if (CaseFile.View.Participants.onInitialized)    {CaseFile.View.Participants.onInitialized();}
        if (CaseFile.View.Notes.onInitialized)           {CaseFile.View.Notes.onInitialized();}
        if (CaseFile.View.Tasks.onInitialized)           {CaseFile.View.Tasks.onInitialized();}
        if (CaseFile.View.References.onInitialized)      {CaseFile.View.References.onInitialized();}
        if (CaseFile.View.History.onInitialized)         {CaseFile.View.History.onInitialized();}
        if (CaseFile.View.Correspondence.onInitialized)  {CaseFile.View.Correspondence.onInitialized();}
        if (CaseFile.View.Time.onInitialized)            {CaseFile.View.Time.onInitialized();}
        if (CaseFile.View.Cost.onInitialized)            {CaseFile.View.Cost.onInitialized();}
        if (CaseFile.View.Calendar.onInitialized)        {CaseFile.View.Calendar.onInitialized();}

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
            this.arkcasePort      = Acm.Object.MicroData.get("arkcasePort");

            this.formUrls = {};
            this.formUrls.urlEditCaseFileForm            = Acm.Object.MicroData.get("urlEditCaseFileForm");
            this.formUrls.urlReinvestigateCaseFileForm   = Acm.Object.MicroData.get("urlReinvestigateCaseFileForm");
            this.formUrls.enableFrevvoFormEngine         = Acm.Object.MicroData.get("enableFrevvoFormEngine");
            this.formUrls.urlChangeCaseStatusForm        = Acm.Object.MicroData.get("urlChangeCaseStatusForm");
            this.formUrls.urlEditChangeCaseStatusForm    = Acm.Object.MicroData.get("urlEditChangeCaseStatusForm");

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
        }
        ,onInitialized: function() {
        }

        ,getToken: function() {
            return this.token;
        }
    }


    ,interfaceNavObj: {
        nodeTitle: function(objSolr) {
            return Acm.goodValue(objSolr.title_parseable) + ' (' + Acm.goodValue(objSolr.name) + ')';

//        	var defaultExpression = "Acm.goodValue(objSolr.title_parseable) + ' (' + Acm.goodValue(objSolr.name) + ')'";
//        	var caseFileTreeRootNameExpression = Acm.Object.MicroData.get("caseFileTreeRootNameExpression");
//            if (Acm.isEmpty(caseFileTreeRootNameExpression)) {
//                caseFileTreeRootNameExpression = defaultExpression;
//            }
//            return eval(caseFileTreeRootNameExpression);
        }
        ,nodeToolTip: function(objSolr) {
            return Acm.goodValue(objSolr.title_parseable);
        }
        ,nodeTypeMap: function() {
            return CaseFile.View.Navigator.nodeTypeMap;
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

        ,nodeTypeMap: [
            {nodeType: "prevPage"      ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
            ,{nodeType: "nextPage"     ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
            ,{nodeType: "p"            ,icon: ""                 ,tabIds: ["tabBlank"]}
            ,{nodeType: "p/CASE_FILE"  ,icon: "i i-folder"       ,tabIds: ["tabTitle"
                ,"tabDetail"
                ,"tabPeople"
                ,"tabDocs"
                ,"tabParticipants"
                ,"tabNotes"
                ,"tabTasks"
                ,"tabRefs"
                ,"tabHistory"
                ,"tabCorrespondence"
                ,"tabOutlookCalendar"
                ,"tabTime"
                ,"tabCost"
            ]}
            ,{nodeType: "p/CASE_FILE/det"       ,icon: "" ,res: "casefile:navigation.leaf-title.details"        ,tabIds: ["tabDetail"]}
            ,{nodeType: "p/CASE_FILE/ppl"       ,icon: "" ,res: "casefile:navigation.leaf-title.people"         ,tabIds: ["tabPeople"]}
            ,{nodeType: "p/CASE_FILE/doc"       ,icon: "" ,res: "casefile:navigation.leaf-title.documents"      ,tabIds: ["tabDocs"]}
            //,{nodeType: "p/CASE_FILE/doc/c"     ,icon: "",tabIds: ["tabDoc"]}
            ,{nodeType: "p/CASE_FILE/par"       ,icon: "" ,res: "casefile:navigation.leaf-title.participants"   ,tabIds: ["tabParticipants"]}
            ,{nodeType: "p/CASE_FILE/note"      ,icon: "" ,res: "casefile:navigation.leaf-title.notes"          ,tabIds: ["tabNotes"]}
            ,{nodeType: "p/CASE_FILE/task"      ,icon: "" ,res: "casefile:navigation.leaf-title.tasks"          ,tabIds: ["tabTasks"]}
            ,{nodeType: "p/CASE_FILE/ref"       ,icon: "" ,res: "casefile:navigation.leaf-title.references"     ,tabIds: ["tabRefs"]}
            ,{nodeType: "p/CASE_FILE/his"       ,icon: "" ,res: "casefile:navigation.leaf-title.history"        ,tabIds: ["tabHistory"]}
            ,{nodeType: "p/CASE_FILE/tpl"       ,icon: "" ,res: "casefile:navigation.leaf-title.correspondence" ,tabIds: ["tabCorrespondence"]}
            ,{nodeType: "p/CASE_FILE/calendar"  ,icon: "" ,res: "casefile:navigation.leaf-title.calendar"       ,tabIds: ["tabOutlookCalendar"]}
            ,{nodeType: "p/CASE_FILE/time"      ,icon: "" ,res: "casefile:navigation.leaf-title.time"           ,tabIds: ["tabTime"]}
            ,{nodeType: "p/CASE_FILE/cost"      ,icon: "" ,res: "casefile:navigation.leaf-title.cost"           ,tabIds: ["tabCost"]}

        ]

        ,getTreeArgs: function() {
            return {
//                lazyLoad: function(event, data) {
//                    CaseFile.View.Navigator.lazyLoad(event, data);
//                }
//                ,
                getContextMenu: function(node) {
                    CaseFile.View.Navigator.getContextMenu(node);
                }
            };
        }
//retired
//        ,lazyLoad: function(event, data) {
//            var key = data.node.key;
//            var nodeType = ObjNav.Model.Tree.Key.getNodeTypeByKey(key);
//            var builder = AcmEx.FancyTreeBuilder.reset();
//            if (ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, CaseFile.Model.DOC_TYPE_CASE_FILE]) == nodeType) {
//                var nodeTypeMap = CaseFile.View.Navigator.nodeTypeMap;
//                for (var i = 0; i < nodeTypeMap.length; i++) {
//                    if (0 == nodeTypeMap[i].nodeType.indexOf(nodeType)) {
//                        var lastSep = nodeTypeMap[i].nodeType.lastIndexOf(ObjNav.Model.Tree.Key.KEY_SEPARATOR);
//                        if (nodeType.length == lastSep) {
//                            var subPart = nodeTypeMap[i].nodeType.substring(lastSep);
//                            builder.addLeaf({key: key + subPart
//                                ,title: $.t(nodeTypeMap[i].res)
//                            });
//                        }
//                    }
//                }
//            }
//            data.result = builder.getTree();
//            return;
//
////            switch (nodeType) {
////                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, CaseFile.Model.DOC_TYPE_CASE_FILE]):
////                    data.result = AcmEx.FancyTreeBuilder
////                        .reset()
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DETAILS
////                            ,title: $.t("casefile:navigation.leaf-title.details")
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_PEOPLE
////                            ,title: $.t("casefile:navigation.leaf-title.people")
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS
////                            ,title: $.t("casefile:navigation.leaf-title.documents")
//////                            ,folder: true
//////                            ,lazy: true
//////                            ,cache: false
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_PARTICIPANTS
////                            ,title: $.t("casefile:navigation.leaf-title.participants")
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_NOTES
////                            ,title: $.t("casefile:navigation.leaf-title.notes")
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TASKS
////                            ,title: $.t("casefile:navigation.leaf-title.tasks")
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_REFERENCES
////                            ,title: $.t("casefile:navigation.leaf-title.references")
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_HISTORY
////                            ,title: $.t("casefile:navigation.leaf-title.history")
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TEMPLATES
////                            ,title: $.t("casefile:navigation.leaf-title.correspondence")
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_CALENDAR
////                            ,title: "Calendar"
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_TIME
////                            ,title: $.t("casefile:navigation.leaf-title.time")
////                        })
////                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + CaseFile.Model.Tree.Key.NODE_TYPE_PART_COST
////                            ,title: $.t("casefile:navigation.leaf-title.cost")
////                        })
////                        .getTree();
////
////                    break;
////
////                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, CaseFile.Model.DOC_TYPE_CASE_FILE, CaseFile.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS]):
////                    var caseFileId = ObjNav.Model.Tree.Key.getObjIdByKey(key);
////                    var c = ObjNav.Model.Detail.getCacheObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId);
////                    if (c) {
////                        data.result = [
////                            {key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.1", title: "Document1" + "[Status]"}
////                            ,{key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.2", title: "Doc2" + "[Status]"}
////                        ];
////                    } else {
////                        data.result = ObjNav.Service.Detail.retrieveObjectDeferred(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId
////                            ,function(response) {
////                                var z = 1;
////
////                                var resultFake = [
////                                    {key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.3", title: "Document3" + "[Status]"}
////                                    ,{key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + "c.4", title: "Doc4" + "[Status]"}
////                                ];
////                                return resultFake;
////                            }
////                        );
////
////                    }
////
////                    break;
////
////                default:
////                    data.result = [];
////                    break;
////            }
//        }

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

    ,Ribbon: {
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
                    CaseFile.Service.Detail.saveCaseFileItem(CaseFile.View.getActiveCaseFileId(), "title", newValue)
                        .done(function(response){
                            CaseFile.Controller.viewChangedCaseTitle(CaseFile.View.getActiveCaseFileId(), newValue);
                        })
                        .fail(function(response){
                            CaseFile.View.Ribbon.setTextLnkCaseTitle($.t("casefile:detail.error-value"));
                        })
                    ;
                }
            });
            //            AcmEx.Object.XEditable.useEditableDate(this.$lnkIncidentDate, {
            //                success: function(response, newValue) {
            //                    CaseFile.Controller.viewChangedIncidentDate(CaseFile.View.getActiveCaseFileId(), newValue);
            //                }
            //            });
            AcmEx.Object.XEditable.useEditableDate(this.$lnkDueDate, {
                success: function(response, newValue) {
                    newValue = AcmEx.Object.XEditable.xDateToDatetime(newValue);
                    CaseFile.Service.Detail.saveCaseFileItem(CaseFile.View.getActiveCaseFileId(), "incidentDate", newValue)
                        .fail(function(response){
                            CaseFile.View.Ribbon.setTextLnkDueDate($.t("casefile:detail.error-value"));
                        })
                    ;
                }
            });

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_ASSIGNEES          ,this.onModelFoundAssignees);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_GROUPS         ,this.onModelRetrievedGroups);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_SUBJECT_TYPES      ,this.onModelFoundSubjectTypes);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_PRIORITIES         ,this.onModelFoundPriorities);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_CASE_TITLE         ,this.onModelSavedCaseTitle);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_INCIDENT_DATE      ,this.onModelSavedIncidentDate);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_ASSIGNEE           ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_GROUP	           ,this.onModelSavedGroup);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_SUBJECT_TYPE       ,this.onModelSavedSubjectType);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_PRIORITY           ,this.onModelSavedPriority);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_DUE_DATE           ,this.onModelSavedDueDate);

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
            CaseFile.View.Action.populateRestriction(CaseFile.View.getActiveCaseFile());
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
            CaseFile.View.Action.populateRestriction(CaseFile.View.getActiveCaseFile());
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
//        ,onModelSavedCaseTitle: function(caseFileId, title) {
//            if (title.hasError) {
//                CaseFile.View.Ribbon.setTextLnkCaseTitle($.t("casefile:detail.error-value"));
//            }
//        }
//        ,onModelSavedIncidentDate: function(caseFileId, incidentDate) {
//            if (incidentDate.hasError) {
//                CaseFile.View.Ribbon.setTextLnkIncidentDate($.t("casefile:detail.error-value"));
//            }
//        }
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
//        ,onModelSavedDueDate: function(caseFileId, created) {
//            if (created.hasError) {
//                CaseFile.View.Ribbon.setTextLnkDueDate($.t("casefile:detail.error-value"));
//            }
//        }

        ,populateCaseFile: function(c) {
            if (CaseFile.Model.Detail.validateCaseFile(c)) {
                this.setTextLabCaseNumber(Acm.goodValue(c.caseNumber));
                this.setTextLnkCaseTitle(Acm.goodValue(c.title));
                //this.setTextLnkIncidentDate(Acm.getDateFromDatetime(c.created));//c.incidentDate
                this.setTextLnkIncidentDate(Acm.getDateFromDatetime(c.created,$.t("common:date.short")));
                this.setTextLnkSubjectType(Acm.goodValue(c.caseType));
                this.setTextLnkPriority(Acm.goodValue(c.priority));
                this.setTextLnkDueDate(Acm.getDateFromDatetime(c.dueDate,$.t("common:date.short")));
                this.setTextLnkStatus("  (" + Acm.goodValue(c.status) +")");

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

    }

    ,Action: {
        create: function() {
            this.$olMilestoneTrack         = $(".track-progress");
            this.$dlgChangeCaseStatus      = $("#changeCaseStatus");
            //this.$dlgConsolidateCase       = $("#consolidateCase");
            //this.$edtConsolidateCase       = $("#edtConsolidateCase");
            //this.$btnConsolidateCase       = $("#btnConsolidateCase")   .on("click", function(e) {CaseFile.View.Action.onClickBtnConsolidateCase(e, this);});
            this.$btnEditCaseFile    	   = $("#btnEditCaseFile")      .on("click", function(e) {CaseFile.View.Action.onClickBtnEditCaseFile(e, this);});
            this.$btnChangeCaseStatus      = $("#btnChangeCaseStatus")  .on("click", function(e) {CaseFile.View.Action.onClickBtnChangeCaseStatus(e, this);});
            this.$btnSplitCase             = $("#btnSplitCase")         .on("click", function(e) {CaseFile.View.Action.onClickBtnSplitCase(e, this);});
            this.$btnMergeCase             = $("#btnMergeCase")         .on("click", function(e) {CaseFile.View.Action.onClickBtnMergeCase(e, this);});;
            this.$btnReinvestigateCaseFile = $("#btnReinvestigate")     .on("click", function(e) {CaseFile.View.Action.onClickBtnReinvestigateCaseFile(e, this);});
            this.$chkRestrict              = $("#restrict")             .on("click", function(e) {CaseFile.View.Action.onClickRestrictCheckbox(e, this);});

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT         ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_MERGED_CASE_FILES      ,this.onModelMergedCaseFiles);

        }
        ,onInitialized: function() {
        }

        ,onClickBtnEditCaseFile: function(event, ctrl) {
            var urlEditCaseFileForm = CaseFile.View.MicroData.formUrls.urlEditCaseFileForm;
            var caseFileId = CaseFile.View.getActiveCaseFileId();
            var c = CaseFile.View.getActiveCaseFile();
            if (Acm.isNotEmpty(urlEditCaseFileForm) && Acm.isNotEmpty(c)) {
                var containerId = c.container.id;
                var folderId = c.container.attachmentFolder.id;

                urlEditCaseFileForm = urlEditCaseFileForm.replace("/embed?", "/popupform?");
                urlEditCaseFileForm = urlEditCaseFileForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',mode:'edit',containerId:'" + containerId + "',folderId:'" + folderId + "',");
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
        ,onClickBtnSplitCase: function(event,ctrl){
            var url = App.getContextPath() + "/plugin/casefile/split/" + CaseFile.View.getActiveCaseFileId();
//            window.open(url);
            Acm.Dialog.openWindow2({url: url}).done(function() {
                var splitInfo = new Acm.Model.LocalData("AcmSplitTmp");
                var splitId = splitInfo.get();
                if (Acm.isNotEmpty(splitId)) {
                    var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
                    var key = CaseFile.Model.DOC_TYPE_CASE_FILE + ObjNav.Model.Tree.Key.TYPE_ID_SEPARATOR + splitId;
                    treeInfo.key = key;
                    splitInfo.set(null);

                    var pageId = ObjNav.Model.Tree.Config.getPageId();
                    ObjNav.Model.List.cachePage.remove(pageId);

                    ObjNav.Model.retrieveData(treeInfo);
                }
            });
        }

        ,onClickBtnMergeCase: function() {
            SearchBase.Dialog.create({name: $.t("casefile:case-picker.name")
                ,title: $.t("casefile:case-picker.title")
                ,prompt: $.t("casefile:case-picker.prompt")
                ,btnGoText: $.t("casefile:case-picker.btn-search")
                ,btnOkText: $.t("casefile:case-picker.btn-ok")
                ,btnCancelText: $.t("casefile:case-picker.btn-cancel")
                ,filters: [{key: "Object Type", values: ["CASE_FILE"]}]
                ,onClickBtnPrimary : function(event, ctrl) {
                    var selectedRows = SearchBase.Dialog.getSelectedRows();
                    if(selectedRows.length > 1){
                        Acm.Dialog.info("casefile:case-picker.selection-error")
                    }else{
                        selectedRows.each(function () {
                            var record = $(this).data('record');
                            var targetCaseFileId = record.id;
                            var sourceCaseFileId = CaseFile.View.getActiveCaseFileId();
                            if(Acm.isEmpty(sourceCaseFileId) && Acm.isEmpty(targetCaseFileId)){
                                Acm.Dialog.info("Please check your selection and try again.");
                            }
                            else {
                                CaseFile.Controller.viewMergedCaseFiles(sourceCaseFileId, targetCaseFileId);
                            }
                        });
                    }
                }
            }).show();
        }

//        ,onClickBtnConsolidateCase: function() {
//            CaseFile.View.Action.setValueEdtConsolidateCase("");
//            CaseFile.View.Action.showDlgConsolidateCase(function(event, ctrl) {
//                var caseNumber = CaseFile.View.Action.getValueEdtConsolidateCase();
//                alert("Consolidate case:" + caseNumber);
//            });
//        }
        ,onClickBtnReinvestigateCaseFile: function() {
            var urlReinvestigateCaseFileForm = CaseFile.View.MicroData.formUrls.urlReinvestigateCaseFileForm;
            var caseFileId = CaseFile.View.getActiveCaseFileId();
            var c = CaseFile.View.getActiveCaseFile();
            if (Acm.isNotEmpty(urlReinvestigateCaseFileForm) && Acm.isNotEmpty(c)) {
                var containerId = c.container.id;
                var folderId = c.container.attachmentFolder.id;

                urlReinvestigateCaseFileForm = urlReinvestigateCaseFileForm.replace("/embed?", "/popupform?");
                urlReinvestigateCaseFileForm = urlReinvestigateCaseFileForm.replace("_data=(", "_data=(caseId:'" + caseFileId + "',caseNumber:'" + c.caseNumber + "',mode:'reinvestigate',containerId:'" + containerId + "',folderId:'" + folderId + "',");
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
        ,onModelMergedCaseFiles: function(targetCaseFile){
            if(targetCaseFile.hasError) {
                App.View.MessageBoard.show("Merge failed" , targetCaseFile.errorMsg);
            }
            else{
                if(CaseFile.Model.Detail.validateCaseFile(targetCaseFile)){
                    var url = "/plugin/casefile/" + Acm.goodValue(targetCaseFile.id);
                    App.View.gotoPage(url);
                }
            }
        }
        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            CaseFile.View.Action.populate(objData);
            SubscriptionOp.Model.checkSubscription(App.getUserName(), objType, objId);
        }
        ,onClickRestrictCheckbox: function(event,ctrl){
            var restriction = ($(ctrl).prop('checked')) ? true : false;
            CaseFile.Controller.viewClickedRestrictCheckbox(CaseFile.View.getActiveCaseFileId(),restriction);
        }

        ,populate: function(c) {
            if (CaseFile.Model.Detail.validateCaseFile(c)) {
                CaseFile.View.Action.showBtnChangeCaseStatus(Acm.goodValue(c.changeCaseStatus, true));

                this.setPropertyRestricted(Acm.goodValue(c.restricted));
                CaseFile.View.Action.populateRestriction(c);

                //Comment out temporarily
                //CaseFile.View.Action.showMilestone(Acm.goodValue(caseFile.milestones));
            }
        }
        ,populateRestriction: function(c) {
            if (CaseFile.Model.Detail.validateCaseFile(c)) {
                var assignee = CaseFile.Model.Detail.getAssignee(c);
                var group = CaseFile.Model.Detail.getGroup(c);
                var assignees = CaseFile.Model.Lookup.getAssignees(c.id);
                var groups = CaseFile.Model.Lookup.getGroups(c.id);

                var restrict = Acm.checkRestriction(assignee, group, assignees, groups);
                CaseFile.View.Action.$chkRestrict.prop('disabled', restrict);
            }
        }
        ,setPropertyRestricted: function(restriction){
            this.$chkRestrict.prop('checked', restriction);
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
//        ,showDlgConsolidateCase: function(onClickBtnPrimary) {
//            Acm.Dialog.modal(this.$dlgConsolidateCase, onClickBtnPrimary);
//        }
//        ,getValueEdtConsolidateCase: function() {
//            return Acm.Object.getValue(this.$edtConsolidateCase);
//        }
//        ,setValueEdtConsolidateCase: function(val) {
//            Acm.Object.setValue(this.$edtConsolidateCase, val);
//        }
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

    ,DetailNote: {
        create: function() {
            this.$divDetail       = $(".divDetail");
            this.$btnEditDetail   = $("#tabDetail button:eq(0)");
            this.$btnSaveDetail   = $("#tabDetail button:eq(1)");
            this.$btnEditDetail.on("click", function(e) {CaseFile.View.DetailNote.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {CaseFile.View.DetailNote.onClickBtnSaveDetail(e, this);});

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_DETAIL             ,this.onModelSavedDetail);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_CASE_FILE          ,this.onModelSavedCaseFile);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            CaseFile.View.DetailNote.populateCaseFile(objData);
        }
        ,onModelRetrievedObject: function(objData) {
            CaseFile.View.DetailNote.populateCaseFile(objData);
        }
        ,onModelSavedDetail: function(caseFileId, details) {
            if (details.hasError) {
                CaseFile.View.DetailNote.setHtmlDivDetail($.t("casefile:detail.error-value"));
            }
        }


        ,DIRTY_EDITING_DETAIL: "Editing case detail"
        ,onClickBtnEditDetail: function(event, ctrl) {
            App.View.Dirty.declare(CaseFile.View.DetailNote.DIRTY_EDITING_DETAIL);
            CaseFile.View.DetailNote.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = CaseFile.View.DetailNote.saveDivDetail();
            CaseFile.Controller.viewChangedDetail(CaseFile.View.getActiveCaseFileId(), htmlDetail);
            App.View.Dirty.clear(CaseFile.View.DetailNote.DIRTY_EDITING_DETAIL);
        }

        ,populateCaseFile: function(c) {
            if (CaseFile.Model.Detail.validateCaseFile(c)) {
                this.setHtmlDivDetail(Acm.goodValue(c.details));
            }
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
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_PERSON_ASSOCIATION_TYPES  ,this.onModelFoundPersonAssociationTypes);

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
        ,onModelFoundPersonAssociationTypes: function(personAssociation) {
            if (personAssociation && personAssociation.hasError) {
                Acm.Dialog.info(personAssociation.errorMsg);
            }
            else{
                AcmEx.Object.JTable.load(CaseFile.View.People.$divPeople);
            }
        }

        ,createJTable: function($s) {
            AcmEx.Object.JTable.useChildTable_new({$jt: $s
                ,childLinks: [
                    CaseFile.View.People.ContactMethods.createLink
                    ,CaseFile.View.People.Organizations.createLink
                    ,CaseFile.View.People.Addresses.createLink
                    ,CaseFile.View.People.Aliases.createLink
                ]
                ,sortMap: function(personAssociation1, personAssociation2, sortBy, sortDir) {
                    var value1 = "";
                    var value2 = "";
                    if ("personType" == sortBy) {
                        value1 = Acm.goodValue(personAssociation1.personType);
                        value2 = Acm.goodValue(personAssociation2.personType);
                    } else if ("familyName" == sortBy) {
                        value1 = Acm.goodValue(personAssociation1.person.familyName);
                        value2 = Acm.goodValue(personAssociation2.person.familyName);
                    } else if ("givenName" == sortBy) {
                        value1 = Acm.goodValue(personAssociation1.person.givenName);
                        value2 = Acm.goodValue(personAssociation2.person.givenName);
                    }
                    var rc = ((value1 < value2) ? -1 : ((value1 > value2) ? 1 : 0));
                    return ("DESC" == sortDir)? -rc : rc;
                }

                ,title: $.t("casefile:people.table.title")
                ,messages: {
                    addNewRecord: $.t("casefile:people.msg.add-new-record")
                }
                ,actions: {
                    pagingListAction: function(postData, jtParams, comparator) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = CaseFile.View.getActiveCaseFile();
                        if (CaseFile.Model.Detail.validateCaseFile(c)) {
                            if (CaseFile.Model.People.validatePersonAssociations(c.personAssociations)) {
                                var pagingItems = AcmEx.Object.JTable.getPagingItems(jtParams, c.personAssociations, comparator);
                                for (var i = 0; i < pagingItems.length; i++) {
                                    var personAssociation = AcmEx.Object.JTable.getPagingItemData(pagingItems[i]);
                                    var record = AcmEx.Object.JTable.getPagingRecord(pagingItems[i]);
                                    record.assocId = Acm.goodValue(personAssociation.id, 0);
                                    record.personType = Acm.goodValue(personAssociation.personType);
                                    record.familyName = Acm.goodValue(personAssociation.person.familyName);
                                    record.givenName = Acm.goodValue(personAssociation.person.givenName);

                                    rc.Records.push(record);
                                }
                                rc.TotalRecordCount = rc.Records.length;
                            }
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        //rc.Record.title = record.title;
                        rc.Record.givenName = record.givenName;
                        rc.Record.familyName = record.familyName;
                        rc.Record.personType = record.personType;
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        //rc.Record.title = record.title;
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
//                        ,title: {
//                            title: $.t("casefile:people.table.field.title")
//                            ,width: '10%'
//                            ,options: CaseFile.Model.Lookup.getPersonTitles()
//                        }
                    ,personType: {
                        title: $.t("casefile:people.table.field.type")
                        ,options: CaseFile.Model.Lookup.getPersonTypes
                    }
                    ,familyName: {
                        title: $.t("casefile:people.table.field.last-name")
                        ,width: '15%'
                    }
                    ,givenName: {
                        title: $.t("casefile:people.table.field.first-name")
                        ,width: '15%'
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
                        //pa.person.title = record.title;
                        pa.person.givenName = record.givenName;
                        pa.person.familyName = record.familyName;
                        CaseFile.Controller.viewAddedPersonAssociation(caseFileId, pa);
                    }
                }

                ,recordUpdated: function(event, data){
                    var whichRow = AcmEx.Object.JTable.getPagingRow(data);
                    var record = data.record;
                    var assocId = record.assocId;
                    var caseFileId = CaseFile.View.getActiveCaseFileId();
                    var c = CaseFile.View.getActiveCaseFile();
                    if (CaseFile.Model.Detail.validateCaseFile(c)) {
                        if (c.personAssociations.length > whichRow) {
                            var pa = c.personAssociations[whichRow];
                            if (CaseFile.Model.People.validatePersonAssociation(pa)) {
                                //pa.person.title = record.title;
                                pa.person.givenName = record.givenName;
                                pa.person.familyName = record.familyName;
                                pa.personType = record.personType;
                                CaseFile.Controller.viewUpdatedPersonAssociation(caseFileId, pa);
                            }
                        }
                    }
                }
                ,recordDeleted: function(event,data) {
                    var whichRow = AcmEx.Object.JTable.getPagingRow(data);
                    var record = data.record;
                    var personAssociationId = record.assocId;
                    var caseFileId = CaseFile.View.getActiveCaseFileId();
                    if (0 < caseFileId && 0 < personAssociationId) {
                        CaseFile.Controller.viewDeletedPersonAssociation(caseFileId, personAssociationId);
                    }
                }
            });
        }


        ,_commonTypeValueRecord: function ($link, postData) {
            var rc = AcmEx.Object.jTableGetEmptyRecord();
            var recordParent = $link.closest('tr').data('record');
            if (recordParent && recordParent.assocId) {
                var assocId = recordParent.assocId;
                var record = Acm.urlToJson(postData);
                rc.Record.assocId = assocId;
                rc.Record.type = Acm.goodValue(record.type);
                rc.Record.value = Acm.goodValue(record.value);
                rc.Record.created = Acm.getCurrentDay(); //record.created;
                rc.Record.creator = App.Model.Users.getUserFullName(App.getUserName());   //record.creator;
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
                //var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='" + $.t("casefile:people.table.contact-methods.table.title") + "'><i class='fa fa-phone'></i></a>");
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' title='" + $.t("casefile:people.table.contact-methods.table.title") + "'><i class='fa fa-phone'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.ContactMethods.onOpen, $.t("casefile:people.table.contact-methods.table.title"));
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $link) {
                AcmEx.Object.JTable.useAsChild_new({$jt: $jt, $link: $link
                    ,title: $.t("casefile:people.table.contact-methods.table.title") //CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_CONTACT_METHODS
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:people.table.contact-methods.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $link.closest('tr').data('record');
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
                                                ,created : Acm.getDateFromDatetime(contactMethods[i].created,$.t("common:date.short"))
                                                ,creator : App.Model.Users.getUserFullName(Acm.goodValue(contactMethods[i].creator))
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($link, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($link, postData);
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
                            title: $.t("casefile:people.table.contact-methods.table.field.added-by")
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    ,recordAdded: function (event, data) {
                        //var recordParent = $link.closest('tr').data('record');
                        //if (recordParent && recordParent.assocId && 0 < caseFileId) {
                        //    var assocId = recordParent.assocId;
                        var record = data.record;
                        var contactMethod = {};
                        var assocId = record.assocId;
                        contactMethod.type  = Acm.goodValue(record.type);
                        contactMethod.value = Acm.goodValue(record.value);
                        contactMethod.created = Acm.getCurrentDayInternal();
                        contactMethod.creator = App.getUserName();
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 < caseFileId && 0 < assocId) {
                            CaseFile.Controller.viewAddedContactMethod(caseFileId, assocId, contactMethod);
                        }
                    }
                    ,recordUpdated: function (event, data) {
                        //var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        //var recordParent = $link.closest('tr').data('record');
                        //if (recordParent && recordParent.assocId && 0 < caseFileId) {
                        //    var assocId = recordParent.assocId;
                        var record = data.record;
                        var contactMethod = {};
                        var assocId = record.assocId;
                        contactMethod.id    = Acm.goodValue(record.id, 0);
                        contactMethod.type  = Acm.goodValue(record.type);
                        contactMethod.value = Acm.goodValue(record.value);
                        contactMethod.created = Acm.getCurrentDayInternal();
                        contactMethod.creator = App.getUserName();
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
                //var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-phone'></i></a>");
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs'><i class='fa fa-phone'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.ContactMethods.onOpen, $.t("casefile:people.table.security-tags.table-title"));
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $link) {
                AcmEx.Object.JTable.useAsChild_new({$jt: $jt, $link: $link
                    ,title: $.t("casefile:people.table.security-tags.table-title") //CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_SECURITY_TAGS
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:people.table.security-tags.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $link.closest('tr').data('record');
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
                                                ,created : Acm.getDateFromDatetime(securityTags[i].created,$.t("common:date.short"))
                                                ,creator : App.Model.Users.getUserFullName(Acm.goodValue(securityTags[i].creator))
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($link, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($link, postData);
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
                        securityTag.creator = App.getUserName();
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
                        securityTag.creator = App.getUserName();
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
                //var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='"+ $.t("casefile:people.table.security-tags.organizations.table.title") +"'><i class='fa fa-book'></i></a>");
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' title='"+ $.t("casefile:people.table.security-tags.organizations.table.title") +"'><i class='fa fa-book'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.Organizations.onOpen, $.t("casefile:people.table.security-tags.organizations.table.title"));
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $link) {
                AcmEx.Object.JTable.useAsChild_new({$jt: $jt, $link: $link
                    ,title: $.t("casefile:people.table.security-tags.organizations.table.title") //CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:people.table.security-tags.organizations.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $link.closest('tr').data('record');
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
                                                ,created : Acm.getDateFromDatetime(organizations[i].created,$.t("common:date.short"))
                                                ,creator : App.Model.Users.getUserFullName(Acm.goodValue(organizations[i].creator))
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($link, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($link, postData);
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
                        organization.creator = App.getUserName();
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
                        organization.creator = App.getUserName();
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
                //var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='" + $.t("casefile:people.table.security-tags.addresses.table.title") + "'><i class='fa fa-map-marker'></i></a>");
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' title='" + $.t("casefile:people.table.security-tags.addresses.table.title") + "'><i class='fa fa-map-marker'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.Addresses.onOpen, $.t("casefile:people.table.security-tags.addresses.table.title"));
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $link) {
                AcmEx.Object.JTable.useAsChild_new({$jt: $jt, $link: $link
                    ,title: $.t("casefile:people.table.security-tags.addresses.table.title") //CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ADDRESSES
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:people.table.security-tags.addresses.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $link.closest('tr').data('record');
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
                                                ,created       : Acm.getDateFromDatetime(addresses[i].created,$.t("common:date.short"))
                                                ,creator       : App.Model.Users.getUserFullName(Acm.goodValue(addresses[i].creator))
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var recordParent = $link.closest('tr').data('record');
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
                                rc.Record.creator       = App.Model.Users.getUserFullName(App.getUserName());   //record.creator;
                            }
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var recordParent = $link.closest('tr').data('record');
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
                                rc.Record.creator       = App.Model.Users.getUserFullName(App.getUserName());   //record.creator;
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
                        address.creator = App.getUserName();
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
                        address.creator = App.getUserName();
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
                //var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show' title='" + $.t("casefile:people.table.security-tags.aliases.table.title") + "'><i class='fa fa-users'></i></a>");
                var $link = $("<a href='#' class='inline animated btn btn-default btn-xs' title='" + $.t("casefile:people.table.security-tags.aliases.table.title") + "'><i class='fa fa-users'></i></a>");
                $link.click(function (e) {
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, CaseFile.View.People.Aliases.onOpen, $.t("casefile:people.table.security-tags.aliases.table.title"));
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $link) {
                AcmEx.Object.JTable.useAsChild_new({$jt: $jt, $link: $link
                    ,title: $.t("casefile:people.table.security-tags.aliases.table.title") //CaseFile.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("casefile:people.table.security-tags.aliases.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $link.closest('tr').data('record');
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
                                                ,created : Acm.getDateFromDatetime(personAliases[i].created,$.t("common:date.short"))
                                                ,creator : App.Model.Users.getUserFullName(Acm.goodValue(personAliases[i].creator))
                                            });
                                        }
                                    }
                                }
                            }
                            return rc;
                        }
                        ,createAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($link, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return CaseFile.View.People._commonTypeValueRecord($link, postData);
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
                        personAlias.creator = App.getUserName();
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
                        personAlias.creator = App.getUserName();
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
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_DOCUMENTS_RETRIEVED_PLAIN_FORMS, this.onModelDocumentsRetrievedPlainForms);

            this.$btnRefreshDocs = $("#btnRefreshDocs").on("click", function(e) {CaseFile.View.Documents.onClickBtnRefreshDocs(e, this);});
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
        ,onModelDocumentsRetrievedPlainForms: function() {
        	DocTree.View.fileTypes = CaseFile.View.Documents.getFileTypes();
        	DocTree.View.refreshDocTree();
        }
        ,onClickBtnRefreshDocs: function(event,ctrl){
            DocTree.View.refreshTree();
        }

        ,uploadForm: function(type, folderId, onCloseForm) {
            //var token = CaseFile.View.MicroData.token;
            var caseFileId = CaseFile.View.getActiveCaseFileId();
            var caseFile = CaseFile.View.getActiveCaseFile();
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                //var url = Acm.goodValue(CaseFile.View.MicroData.formUrls[report]);
                var url = null;
                var fileType = CaseFile.View.Documents.getFileTypeByType(type);
                if (fileType) {
                    url = Acm.goodValue(fileType.url);
                }
                if (Acm.isNotEmpty(url)) {
                    var data = "_data=(";
                    if (fileType && fileType.urlParameters && fileType.urlParameters.length > 0) {
                    	var urlParameters = fileType.urlParameters;
                    	var parametersAsString = '';
                    	for (var i = 0; i < urlParameters.length; i++) {
                    		var key = urlParameters[i].name;
                    		var value = '';
                    		if (Acm.isNotEmpty(urlParameters[i].defaultValue)) {
                    			value = Acm.silentReplace(urlParameters[i].defaultValue, "'", "_0027_");
                    		} else if (Acm.isNotEmpty(urlParameters[i].keyValue)) {
                    			if (Acm.isNotEmpty(caseFile[urlParameters[i].keyValue])) {
                    				value = Acm.silentReplace(caseFile[urlParameters[i].keyValue], "'", "_0027_");
                    			}
                    		}
                    		value = encodeURIComponent(value);
                    		parametersAsString += key + ":'" + Acm.goodValue(value) + "',";
                    	}
                    	parametersAsString +="folderId:'" + folderId + "',";
                    	data += parametersAsString;
                    }
                    url = url.replace("_data=(", data);
                    Acm.Dialog.openWindow(url, "", 1060, $(window).height() - 30, onCloseForm);
                }
            }
        }

        ,getFileTypes: function() {
        	var fileTypes = CaseFile.View.MicroData.fileTypes;
        	var plainForms = CaseFile.Model.Documents.getPlainForms();
        	var plainFormsAsFileTypes = [];

        	if (CaseFile.Model.Documents.validatePlainForms(plainForms)) {
        		for (var i = 0; i < plainForms.length; i++) {
        			if (Acm.isNotEmpty(plainForms[i].key)) {
        				var fileType = {};
            			fileType.type = plainForms[i].key;
            			fileType.label = Acm.goodValue(plainForms[i].name);
            			fileType.url = Acm.goodValue(plainForms[i].url);
            			fileType.form = true;
            			fileType.urlParameters = plainForms[i].urlParameters;

            			plainFormsAsFileTypes.push(fileType);
        			}
        		}
        	}

        	if (Acm.isArray(fileTypes)) {
                fileTypes = plainFormsAsFileTypes.concat(fileTypes);
            }else {
            	fileTypes = plainFormsAsFileTypes;
            }

        	return fileTypes;
        }

        ,getFileTypeByType: function(type) {
            var ft = null;
            var _fileTypes = CaseFile.View.Documents.getFileTypes();
            if (Acm.isArray(_fileTypes)) {
                for (var i = 0; i < _fileTypes.length; i++) {
                    if (type == _fileTypes[i].type) {
                        ft = _fileTypes[i];
                        break;
                    }
                }
            }
            return ft;
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

        ,createJTableParticipants: function($jt) {
            AcmEx.Object.JTable.usePaging_new({$jt: $jt
                ,sortMap: {
                    "type": "participantType"
                    ,"title": "participantLdapId"
                }

                ,title: $.t("casefile:participants.table.title")
                ,messages: {
                    addNewRecord: $.t("casefile:participants.msg.add-new-record")
                }
                ,actions: {
                    pagingListAction: function(postData, jtParams, sortMap) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = CaseFile.View.getActiveCaseFile();
                        if (CaseFile.Model.Detail.validateCaseFile(c)) {
                            var pagingItems = AcmEx.Object.JTable.getPagingItems(jtParams, c.participants, sortMap);
                            for (var i = 0; i < pagingItems.length; i++) {
                                var participant = AcmEx.Object.JTable.getPagingItemData(pagingItems[i]);
                                var record = AcmEx.Object.JTable.getPagingRecord(pagingItems[i]);
                                record.id = Acm.goodValue(participant.id, 0);
                                // Here I am not taking user full name. It will be automatically shown because now
                                // I am sending key-value object with key=username and value=fullname
                                record.title = Acm.goodValue(participant.participantLdapId);
                                record.type = Acm.goodValue(participant.participantType);
                                rc.Records.push(record);
                            }
                            rc.TotalRecordCount = c.participants.length;
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        rc.Record.title = record.title;
                        rc.Record.type = record.type;
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        rc.Record.title = record.title;
                        rc.Record.type = record.type;
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
                    var whichRow = AcmEx.Object.JTable.getPagingRow(data);
                    var record = data.record;
                    var caseFileId = CaseFile.View.getActiveCaseFileId();
                    var c = CaseFile.View.getActiveCaseFile();
                    if (CaseFile.Model.Detail.validateCaseFile(c)) {
                        if (0 < c.participants.length && whichRow < c.participants.length) {
                            var participant = c.participants[whichRow];
                            participant.participantLdapId = record.title;
                            participant.participantType = record.type;
                            CaseFile.Controller.viewUpdatedParticipant(caseFileId, participant);
                        }

                    }
                }
                ,recordDeleted : function (event, data) {
                    var whichRow = AcmEx.Object.JTable.getPagingRow(data);
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
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(objType, objId) {
            //AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);

            CaseFile.View.Notes.currentKey = objId;
            CaseFile.Model.Notes.getNoteList(objId, CaseFile.View.Notes.currentKey)
                .done(function(noteListData){
                    AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
                })
            ;
        }

        ,createJTableNotes: function($jt) {
            AcmEx.Object.JTable.usePaging_new({$jt: $jt
                ,sortMap: {
                    note      : "note"
                    ,created  : "created"
                    ,creator  : "creator"
                }
//keep this for future service paging support
//                ,dataMaker: function(noteListData) {
//                    var total = noteListData.total;
//                    var noteList = noteListData.list;
//                    var jtData = AcmEx.Object.JTable.getEmptyRecords();
//                    if (CaseFile.Model.Notes.validateNotes(noteList)) {
//                        for (var i = 0; i < noteList.length; i++) {
//                            var Record = {};
//                            Record.id         = Acm.goodValue(noteList[i].id, 0);
//                            Record.note       = Acm.goodValue(noteList[i].note);
//                            Record.created    = Acm.getDateFromDatetime(noteList[i].created,$.t("common:date.short"));
//                            Record.creator = App.Model.Users.getUserFullName(Acm.goodValue(noteList[i].creator));
//                            //Record.parentId   = Acm.goodValue(noteList[i].parentId);
//                            //Record.parentType = Acm.goodValue(noteList[i].parentType);
//                            jtData.Records.push(Record);
//                        }
//                        jtData.TotalRecordCount = total;
//                    }
//                    return jtData;
//                }

                ,title: $.t("casefile:notes.table.title")
                ,selecting: true
                ,multiselect: false
                ,selectingCheckboxes: false
                ,messages: {
                    addNewRecord: $.t("casefile:notes.msg.add-new-record")
                }
                ,actions: {
//keep this for future service paging support
//                    serviceListAction: function (postData, jtParams, sortMap, dataMaker, keyGetter) {
//                        var caseFileId = CaseFile.View.getActiveCaseFileId();
//                        if (0 >= caseFileId) {
//                            return AcmEx.Object.JTable.getEmptyRecords();
//                        }
//
//                        var cacheKey = keyGetter(caseFileId, jtParams);
//                        CaseFile.View.Notes.currentKey = cacheKey;
//                        var noteListData = CaseFile.Model.Notes.cacheNoteList.get(cacheKey);
//                        if (noteListData) {
//                            return dataMaker(noteListData);
//
//                        } else {
//                            return CaseFile.Model.Notes.noteListAction(caseFileId
//                                ,postData
//                                ,jtParams
//                                ,sortMap
//                                ,dataMaker
//                                ,cacheKey
//                            ).fail(function(response){
//                                Acm.Log("Note list error");
//                            });
//                        }  //end else
//                    }
                    pagingListAction: function(postData, jtParams, sortMap) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var cacheKey = CaseFile.View.Notes.currentKey;
                        var noteListData = CaseFile.Model.Notes.cacheNoteList.get(cacheKey);
                        if (noteListData) {
                            var noteList = noteListData.list;
                            if (CaseFile.Model.Notes.validateNotes(noteList)) {
                                var pagingItems = AcmEx.Object.JTable.getPagingItems(jtParams, noteList, sortMap);
                                for (var i = 0; i < pagingItems.length; i++) {
                                    var note = AcmEx.Object.JTable.getPagingItemData(pagingItems[i]);
                                    var record = AcmEx.Object.JTable.getPagingRecord(pagingItems[i]);

                                    record.id         = Acm.goodValue(note.id, 0);
                                    record.note       = Acm.goodValue(note.note);
                                    record.created    = Acm.getDateFromDatetime(note.created,$.t("common:date.short"));
                                    record.creator    = App.Model.Users.getUserFullName(Acm.goodValue(note.creator));
                                    //record.parentId   = Acm.goodValue(note.parentId);
                                    //record.parentType = Acm.goodValue(note.parentType);
                                    rc.Records.push(record);
                                }
                                rc.TotalRecordCount = noteList.length;
                            }
                        }
                        return rc;
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
                        noteToSave.id = 0;
                        noteToSave.note = record.note;
                        noteToSave.created = Acm.getCurrentDayInternal(); //record.created;
                        noteToSave.creator = record.creator;   //record.creator;
                        noteToSave.parentId = caseFileId;
                        noteToSave.parentType = CaseFile.Model.DOC_TYPE_CASE_FILE;

                        var cacheKey = CaseFile.View.Notes.currentKey;
                        CaseFile.Model.Notes.saveNote(noteToSave, cacheKey).done(function(note){
                            AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
                        });
                    }
                }
                ,recordUpdated: function(event,data){
                    var whichRow = AcmEx.Object.JTable.getTableRow(data);
                    var record = data.record;
                    var caseFileId = CaseFile.View.getActiveCaseFileId();
                    if (0 < caseFileId) {
                        var cacheKey = CaseFile.View.Notes.currentKey;
                        var noteListData = CaseFile.Model.Notes.cacheNoteList.get(cacheKey);
                        var noteList = noteListData.list;
                        if (noteList) {
                            if(noteList[whichRow]){
                                noteList[whichRow].note = record.note;
                                CaseFile.Model.Notes.saveNote(noteList[whichRow], cacheKey).done(function(note){
                                    AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
                                });
                            }
                        }
                    }
                }
                ,recordDeleted : function (event, data) {
                    var whichRow = AcmEx.Object.JTable.getTableRow(data);
                    var caseFileId = CaseFile.View.getActiveCaseFileId();
                    if (0 < caseFileId) {
                        var cacheKey = CaseFile.View.Notes.currentKey;
                        var noteListData = CaseFile.Model.Notes.cacheNoteList.get(cacheKey);
                        var noteList = noteListData.list;
                        if (noteList) {
                            if(noteList[whichRow]){
                                CaseFile.Model.Notes.deleteNote(noteList[whichRow].id, cacheKey).done(function(noteId){
                                    AcmEx.Object.JTable.load(CaseFile.View.Notes.$divNotes);
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    ,Tasks: {
        create: function() {
            this.$divTasks          = $("#divTasks");
            this.createJTableTasks(this.$divTasks);
            AcmEx.Object.JTable.clickAddRecordHandler(this.$divTasks, CaseFile.View.Tasks.onClickSpanAddTask);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_MY_TASKS    ,this.onModelRetrievedMyTasks);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_COMPLETED_TASK      ,this.onModelCompletedTask);

        }
        ,onInitialized: function() {
        }

        ,URL_TASK_DETAIL:  "/plugin/task/"
        ,URL_NEW_TASK:     "/plugin/task/wizard?parentType=CASE_FILE&reference="


        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
        }
        ,onModelRetrievedMyTasks: function(tasks) {
            if (tasks.hasError) {
                //empty table?
            } else {
                AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
            }
        }
//        ,onModelRetrievedObject: function(objData) {
//            AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
//        }
//        ,onModelCompletedTask: function(task) {
//            if (task.hasError) {
//                //empty table?
//            } else {
//                AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
//            }
//        }
        ,onClickSpanAddTask: function(event, ctrl) {
            var caseFile = CaseFile.View.getActiveCaseFile();
            if (caseFile) {
                var caseNumber = Acm.goodValue(caseFile.caseNumber);
                var url = CaseFile.View.Tasks.URL_NEW_TASK  + caseNumber;
                App.gotoPage(url);
            }
        }
        ,onClickBtnCompleteTask : function(taskId) {
            CaseFile.Model.Tasks.completeTask(taskId, CaseFile.View.Tasks.currentKey).done(function(task){
                AcmEx.Object.JTable.load(CaseFile.View.Tasks.$divTasks);
            });
        }
        ,onClickBtnTaskWithOutcome : function(outcome,taskId) {
            var tasks = CaseFile.Model.Tasks.cacheMyTasks.get(App.getUserName());
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
                CaseFile.Model.Tasks.completeTaskWithOutcome(task, CaseFile.View.Tasks.currentKey);
            }

            //alert("task with outcome");

        }
        ,retrieveTaskOutcome : function(taskId){
            var myTasks = CaseFile.Model.Tasks.cacheMyTasks.get(App.getUserName());
            var $a = $("");
            if(myTasks){
                for(var i = 0; i < myTasks.length; i++){
                    if(myTasks[i].taskId == taskId){
                        var task = myTasks[i];
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

        ,createJTableTasks: function($jt) {
            AcmEx.Object.JTable.usePaging_new({$jt: $jt
                ,sortMap: {id  : "object_id_s"
                    ,title     : "name"
                    ,assignee  : "assignee_s"
                    ,created   : "create_tdt"
                    ,priority  : "priority_s"
                    ,dueDate   : "due_tdt"
                    ,status    : "status_s"
                }
                ,dataMaker: function(taskListData) {
                    var total = taskListData.total;
                    var taskList = taskListData.list;
                    var jtData = AcmEx.Object.JTable.getEmptyRecords();
                    if (!Acm.isArrayEmpty(taskList)) {
                        for (var i = 0; i < taskList.length; i++) {
                            var Record = {};
                            Record.id       = taskList[i].id;
                            Record.title    = taskList[i].title;
                            Record.created  = taskList[i].created;
                            Record.priority = taskList[i].priority;
                            Record.dueDate  = taskList[i].dueDate;
                            Record.status   = taskList[i].status;
                            Record.assignee = App.Model.Users.getUserFullName(Acm.goodValue(taskList[i].assignee));

                            jtData.Records.push(Record);
                        }
                        jtData.TotalRecordCount = total;
                    }
                    return jtData;
                }

                ,title: $.t("casefile:tasks.table.title")
                ,multiselect: false
                ,selecting: false
                ,selectingCheckboxes: false
                ,messages: {
                    addNewRecord: $.t("casefile:tasks.msg.add-new-record")
                }
                ,actions: {
                    serviceListAction: function (postData, jtParams, sortMap, dataMaker, keyGetter) {
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        CaseFile.View.Tasks.currentKey = keyGetter(caseFileId, jtParams);
                        return CaseFile.Model.Tasks.taskListAction(caseFileId
                            ,postData
                            ,jtParams
                            ,sortMap
                            ,dataMaker
                            ,CaseFile.View.Tasks.currentKey
                        ).fail(function(response) {
                            Acm.log("Task list error");
                        });
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
                        ,sorting: false
                        ,display: function (commData) {
                            var $a = CaseFile.View.Tasks.retrieveTaskOutcome(commData.record.id);
                            $a.on("click", ".businessProcess", function(e) {CaseFile.View.Tasks.onClickBtnTaskWithOutcome(e.target.id,commData.record.id);$a.hide();});
                            $a.on("click", ".adhoc", function(e) {CaseFile.View.Tasks.onClickBtnCompleteTask(commData.record.id);$a.hide();});
                            return $a;
                        }
                    }
                } //end field
            });
        }
    }

    ,References: {
        create: function() {
            this.$divReferences          = $("#divRefs");
            this.createJTableReferences(this.$divReferences);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_DOCUMENT         ,this.onModelAddedDocument);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_CREATED_CORRESPONDENCE ,this.onModelCreatedCorrespondence);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(caseFileId) {
            AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
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
//        ,onModelCreatedCorrespondence: function(caseFileId) {
//            if (caseFileId.hasError) {
//                ;
//            } else {
//                AcmEx.Object.JTable.load(CaseFile.View.References.$divReferences);
//            }
//        }

        ,createJTableReferences: function($jt) {
            var sortMap = {};
            sortMap["title"]    = "targetName";
            sortMap["modified"] = "participantLdapId";
            sortMap["type"]     = "targetType";
            sortMap["status"]   = "status";

            AcmEx.Object.JTable.usePaging_new({$jt: $jt
                ,sortMap: {
                    title      : "targetName"
                    ,modified  : "participantLdapId"
                    ,type      : "targetType"
                    ,status    : "status"
                }

                ,title: $.t("casefile:references.table.title")
                ,messages: {
                    addNewRecord: $.t("casefile:references.msg.add-new-record")
                }
                ,actions: {
                    pagingListAction: function(postData, jtParams, sortMap) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = CaseFile.View.getActiveCaseFile();
                        if (CaseFile.Model.Detail.validateCaseFile(c)) {
                            var pagingItems = AcmEx.Object.JTable.getPagingItems(jtParams, c.references, sortMap);
                            for (var i = 0; i < pagingItems.length; i++) {
                                var reference = AcmEx.Object.JTable.getPagingItemData(pagingItems[i]);
                                var record = AcmEx.Object.JTable.getPagingRecord(pagingItems[i]);
                                record.id = Acm.goodValue(reference.targetId, 0);
                                record.title = Acm.goodValue(reference.targetName);
                                record.modified    = Acm.getDateFromDatetime(reference.modified,$.t("common:date.short"));
                                record.type = Acm.goodValue(reference.targetType);
                                record.status = Acm.goodValue(reference.status);
                                rc.Records.push(record);
                            }
                            rc.TotalRecordCount = c.references.length;
                        }
                        return rc;
                    }
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
//                ,recordAdded : function (event, data) {
//                }
//                ,recordUpdated: function(event,data){
//                }
//                ,recordDeleted : function (event, data) {
//                }
            });
        }
    }
    ,History: {
        create: function() {
            this.$divHistory          = $("#divHistory");
            this.createJTableHistory(this.$divHistory);

//            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(CaseFile.View.History.$divHistory);
        }
//        ,onModelRetrievedObject: function(objData) {
//            AcmEx.Object.JTable.load(CaseFile.View.History.$divHistory);
//        }

        ,createJTableHistory: function($jt) {
            AcmEx.Object.JTable.usePaging_new({$jt: $jt
                ,sortMap: {
                    eventType    : "eventType"
                    ,eventDate   : "eventDate"
                    ,user        : "userId"
                }
                ,dataMaker: function(history) {
                    var jtData = AcmEx.Object.JTable.getEmptyRecords();
                    if(CaseFile.Model.History.validateHistory(history)){
                        var events = history.resultPage;
                        for (var i = 0; i < events.length; i++) {
                            var Record = {};
                            Record.eventType = Acm.goodValue(events[i].eventType);
                            Record.eventDate = Acm.getDateFromDatetime(events[i].eventDate,$.t("common:date.short"));
                            Record.user = App.Model.Users.getUserFullName(Acm.goodValue(events[i].userId));
                            jtData.Records.push(Record);
                        }
                        jtData.TotalRecordCount = history.totalCount;
                    }
                    return jtData;
                }

                ,title: $.t("casefile:history.table.title")
                ,actions: {
                    serviceListAction: function (postData, jtParams, sortMap, dataMaker, keyGetter) {
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var cacheKey = keyGetter(caseFileId, jtParams);
                        return CaseFile.Model.History.historyListAction(caseFileId
                                ,postData
                                ,jtParams
                                ,sortMap
                                ,dataMaker
                                ,cacheKey
                            ).fail(function(response) {
                                Acm.log("History event list error");
                            });
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
                    }, user: {
                        title: $.t("casefile:history.table.field.user")
                        ,width: '25%'
                    }
                } //end field
            });
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
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT            ,this.onViewSelectedObject);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_CREATED_CORRESPONDENCE  ,this.onModelCreatedCorrespondence);
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
//        ,onModelCreatedCorrespondence: function(correspondence) {
//            if (correspondence.hasError) {
//                Acm.Dialog.info(correspondence.errorMsg);
//            } else {
//                AcmEx.Object.JTable.load(CaseFile.View.Correspondence.$divCorrespondence);
//            }
//        }

        ,getSelectTemplate: function() {
            return Acm.Object.getSelectValue(this.$spanAddTemplate.prev().find("select"));
        }
        ,onClickSpanAddDocument: function(event, ctrl) {
            var caseFileId = CaseFile.View.getActiveCaseFileId();
            var templateName = CaseFile.View.Correspondence.getSelectTemplate();
            //CaseFile.Controller.viewClickedAddCorrespondence(caseFileId, templateName);
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                CaseFile.Model.Correspondence.createCorrespondence(caseFile, templateName, CaseFile.View.Correspondence.currentKey)
                    .done(function(newCorrespondence){
                        AcmEx.Object.JTable.load(CaseFile.View.Correspondence.$divCorrespondence);
                    })
                    .fail(function(response){
                        //App.MessageBoard.show("Failed to creaet correspondence", response.errorMsg);
                    })
                ;
            }
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
            AcmEx.Object.JTable.usePaging_new({$jt: $s
                ,sortMap: function(baseUrl, pageStart, pageSize, sortBy, sortDir) {
                    return AcmEx.Model.JTable.hashMapUrlDecoratorDir(baseUrl, pageStart, pageSize, sortBy, sortDir
                        ,{title        : "name"
                            ,created   : "created"
                            ,creator   : "creator"
                        }
                    )
                }
                ,dataMaker: function(correspondenceData) {
                    var totalCorrespondences = correspondenceData.totalChildren;
                    var correspondences = correspondenceData.children;
                    var jtData = AcmEx.Object.JTable.getEmptyRecords();
                    if (Acm.isNotEmpty(correspondences)) {
                        for (var i = 0; i < correspondences.length; i++) {
                            if(CaseFile.Model.Correspondence.validateCorrespondence(correspondences[i])){
                                var Record = {};
                                Record.id = Acm.goodValue(correspondences[i].objectId)
                                Record.title = Acm.goodValue(correspondences[i].name);
                                Record.created = Acm.getDateFromDatetime(correspondences[i].created,$.t("common:date.short"));
                                Record.creator = App.Model.Users.getUserFullName(Acm.goodValue(correspondences[i].creator));
                                jtData.Records.push(Record);
                            }
                        }
                        jtData.TotalRecordCount = Acm.goodValue(totalCorrespondences, 0);
                    }
                    return jtData;
                }

                ,title: $.t("casefile:correspondence.table.title")
                , messages: {
                    addNewRecord: $.t("casefile:correspondence.msg.add-new-record")
                }
                , actions: {
                    serviceListAction: function (postData, jtParams, sortMap, dataMaker, keyGetter) {
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        if (0 >= caseFileId) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var cacheKey = keyGetter(caseFileId, jtParams);
                        CaseFile.View.Correspondence.currentKey = cacheKey;
                        return CaseFile.Model.Correspondence.correspondenceListAction(caseFileId
                                ,postData
                                ,jtParams
                                ,sortMap
                                ,dataMaker
                                ,cacheKey
                            ).fail(function(response) {
                                Acm.log("Correspondence list error");
                            });
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

    ,Time: {
        create: function() {
            this.$divTime          = $("#divTime");
            this.createJTableTime(this.$divTime);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_TIMESHEETS     ,this.onModelRetrievedTimesheets);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            CaseFile.Model.Time.getTimesheets(nodeId).done(function(timesheets){
                AcmEx.Object.JTable.load(CaseFile.View.Time.$divTime);
            });
        }
//        ,onModelRetrievedObject: function(objData) {
//            AcmEx.Object.JTable.load(CaseFile.View.Time.$divTime);
//        }
//        ,onModelRetrievedTimesheets: function(timesheet){
//            AcmEx.Object.JTable.load(CaseFile.View.Time.$divTime);
//        }

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
//        ,_makeJtData: function(timesheets) {
//            var jtData = AcmEx.Object.JTable.getEmptyRecords();
//            for(var j = 0; j < timesheets.length; j++){
//                if(CaseFile.Model.Time.validateTimesheet(timesheets[j])){
//                    var timesheet = timesheets[j];
//                    var Record = {};
//                    Record.id = Acm.goodValue(timesheet.id);
//                    Record.name = $.t("casefile:time.table.label.timesheet") + " " + Acm.getDateFromDatetime(timesheet.startDate,$.t("common:date.short")) + " - " +  Acm.getDateFromDatetime(timesheet.endDate,$.t("common:date.short"));
//                    Record.type = CaseFile.Model.DOC_TYPE_TIMESHEET;
//                    Record.status = Acm.goodValue(timesheet.status);
//                    Record.username = Acm.goodValue(timesheet.creator);
//                    Record.hours = Acm.goodValue(CaseFile.View.Time.findTotalHours(timesheet.times));
//                    Record.modified = Acm.getDateFromDatetime(timesheet.modified,$.t("common:date.short"));
//                    jtData.Records.push(Record);
//                }
//            }
//            return jtData;
//        }
        ,createJTableTime: function($jt) {
            AcmEx.Object.JTable.usePaging_new({$jt: $jt
                ,sortMap: function(timesheet1, timesheet2, sortBy, sortDir) {
                    if ("hours" == sortBy) {
                        var value1 = "";
                        var value2 = "";
                        if (timesheet1) {
                            value1 = Acm.goodValue(CaseFile.View.Time.findTotalHours(timesheet1.times));
                        }
                        if (timesheet2) {
                            value2 = Acm.goodValue(CaseFile.View.Time.findTotalHours(timesheet2.times));
                        }
                        var rc = ((value1 < value2) ? -1 : ((value1 > value2) ? 1 : 0));
                        return ("DESC" == sortDir)? -rc : rc;

                    } else {
                        return AcmEx.Object.JTable.hashMapComparator(timesheet1, timesheet2, sortBy, sortDir
                            ,{
                                name       : "startDate"
                                ,username  : "creator"
                                ,modified  : "modified"
                                ,status    : "status"
                            }
                        );
                    }
                }

                ,title: $.t("casefile:time.table.title")
                , actions: {
//                    listAction: function (postData, jtParams) {
//                        var rc = AcmEx.Object.jTableGetEmptyRecords();
//                        var timesheets = CaseFile.Model.Time.cacheTimesheets.get(CaseFile.View.getActiveCaseFileId());
//                        if (CaseFile.Model.Time.validateTimesheets(timesheets)) {
//                            rc = CaseFile.View.Time._makeJtData(timesheets);
//                        }
//                        return rc;
//                    }
                    pagingListAction: function(postData, jtParams, comparator) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        var timesheets = CaseFile.Model.Time.cacheTimesheets.put(caseFileId);
                        if (CaseFile.Model.Time.validateTimesheets(timesheets)) {
                            var pagingItems = AcmEx.Object.JTable.getPagingItems(jtParams, timesheets, comparator);
                            for (var i = 0; i < pagingItems.length; i++) {
                                var timesheet = AcmEx.Object.JTable.getPagingItemData(pagingItems[i]);
                                var record = AcmEx.Object.JTable.getPagingRecord(pagingItems[i]);
                                record.id = Acm.goodValue(timesheet.id);
                                record.name = $.t("casefile:time.table.label.timesheet") + " " + Acm.getDateFromDatetime(timesheet.startDate,$.t("common:date.short")) + " - " +  Acm.getDateFromDatetime(timesheet.endDate,$.t("common:date.short"));
                                record.type = CaseFile.Model.DOC_TYPE_TIMESHEET;
                                record.status = Acm.goodValue(timesheet.status);
                                record.username = Acm.goodValue(timesheet.creator);
                                record.hours = Acm.goodValue(CaseFile.View.Time.findTotalHours(timesheet.times));
                                record.modified = Acm.getDateFromDatetime(timesheet.modified,$.t("common:date.short"));
                                rc.Records.push(record);
                            }
                            rc.TotalRecordCount = timesheets.length;
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
            });
        }
    }


    ,Cost: {
        create: function() {
            this.$divCost          = $("#divCost");
            this.createJTableCost(this.$divCost);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_RETRIEVED_COSTSHEETS     ,this.onModelRetrievedCostsheets);

        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            CaseFile.Model.Time.getTimesheets(nodeId).done(function(costsheets){
                AcmEx.Object.JTable.load(CaseFile.View.Cost.$divCost);
            });
        }
//        ,onModelRetrievedObject: function(objData) {
//            AcmEx.Object.JTable.load(CaseFile.View.Cost.$divCost);
//        }
//
//        ,onModelRetrievedCostsheets: function(costsheet){
//            AcmEx.Object.JTable.load(CaseFile.View.Cost.$divCost);
//        }
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
//        ,_makeJtData: function(costsheets) {
//            var jtData = AcmEx.Object.JTable.getEmptyRecords();
//            for(var j = 0; j < costsheets.length; j++){
//                if(CaseFile.Model.Cost.validateCostsheet(costsheets[j])){
//                    var costsheet = costsheets[j];
//                    var Record = {};
//                    Record.id = Acm.goodValue(costsheet.id);
//                    Record.name = $.t("casefile:cost.table.label.costsheet") + " " + Acm.goodValue(costsheet.parentNumber);
//                    Record.type = CaseFile.Model.DOC_TYPE_COSTSHEET;
//                    Record.status = Acm.goodValue(costsheet.status);
//                    Record.username = Acm.goodValue(costsheet.creator);
//                    Record.cost = Acm.goodValue(CaseFile.View.Cost.findTotalCost(costsheet.costs));
//                    Record.modified = Acm.getDateFromDatetime(costsheet.modified,$.t("common:date.short"));
//                    jtData.Records.push(Record);
//                }
//            }
//            return jtData;
//        }
        ,createJTableCost: function($jt) {
            AcmEx.Object.JTable.usePaging_new({$jt: $jt
                ,sortMap: function(costsheet1, costsheet2, sortBy, sortDir) {
                    if ("cost" == sortBy) {
                        var value1 = "";
                        var value2 = "";
                        if (costsheet1) {
                            value1 = Acm.goodValue(CaseFile.View.Cost.findTotalCost(costsheet1.costs));
                        }
                        if (costsheet2) {
                            value2 = Acm.goodValue(CaseFile.View.Cost.findTotalCost(costsheet2.costs));
                        }
                        var rc = ((value1 < value2) ? -1 : ((value1 > value2) ? 1 : 0));
                        return ("DESC" == sortDir)? -rc : rc;

                    } else {
                        return AcmEx.Object.JTable.hashMapComparator(costsheet1, costsheet2, sortBy, sortDir
                            ,{
                                name       : "parentNumber"
                                ,username  : "creator"
                                ,modified  : "modified"
                                ,status    : "status"
                            }
                        );
                    }
                }

                ,title: $.t("casefile:cost.table.title")
                ,actions: {
//                    listAction: function (postData, jtParams) {
//                        var rc = AcmEx.Object.jTableGetEmptyRecords();
//                        var costsheets = CaseFile.Model.Cost.cacheCostsheets.get(CaseFile.View.getActiveCaseFileId());
//                        if (CaseFile.Model.Cost.validateCostsheets(costsheets)) {
//                            rc = CaseFile.View.Cost._makeJtData(costsheets);
//                        }
//                        return rc;
//                    }
                    pagingListAction: function(postData, jtParams, comparator) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var caseFileId = CaseFile.View.getActiveCaseFileId();
                        var costsheets = CaseFile.Model.Time.cacheTimesheets.put(caseFileId);
                        if (CaseFile.Model.Time.validateTimesheets(costsheets)) {
                            var pagingItems = AcmEx.Object.JTable.getPagingItems(jtParams, costsheets, comparator);
                            for (var i = 0; i < pagingItems.length; i++) {
                                var costsheet = AcmEx.Object.JTable.getPagingItemData(pagingItems[i]);
                                var record = AcmEx.Object.JTable.getPagingRecord(pagingItems[i]);
                                record.id = Acm.goodValue(costsheet.id);
                                record.name = $.t("casefile:cost.table.label.costsheet") + " " + Acm.goodValue(costsheet.parentNumber);
                                record.type = CaseFile.Model.DOC_TYPE_COSTSHEET;
                                record.status = Acm.goodValue(costsheet.status);
                                record.username = Acm.goodValue(costsheet.creator);
                                record.cost = Acm.goodValue(CaseFile.View.Cost.findTotalCost(costsheet.costs));
                                record.modified = Acm.getDateFromDatetime(costsheet.modified,$.t("common:date.short"));
                                rc.Records.push(record);
                            }
                            rc.TotalRecordCount = costsheets.length;
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
            });
        }
    }

    ,Calendar: {
        create: function () {
        }
        , onInitialized: function () {
        }
        ,displayError: function() {
            Calendar.View.OutlookCalendar.$calendarTabTitle.text($.t("casefile:outlook-calendar.msg.error-occurred"));

            //App.View.MessageBoard.show($.t("casefile:outlook-calendar.msg.error-occurred"));
        }
    }


};

