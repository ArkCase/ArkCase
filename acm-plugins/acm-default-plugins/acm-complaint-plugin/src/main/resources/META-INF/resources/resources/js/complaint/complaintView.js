/**
 * Complaint.View
 *
 * @author jwu
 */
Complaint.View = Complaint.View || {
    create : function() {
        if (Complaint.View.MicroData.create)          {Complaint.View.MicroData.create();}
        if (Complaint.View.Navigator.create)          {Complaint.View.Navigator.create();}
        if (Complaint.View.Content.create)            {Complaint.View.Content.create();}
        if (Complaint.View.Action.create)             {Complaint.View.Action.create();}
        if (Complaint.View.Detail.create)             {Complaint.View.Detail.create();}
        if (Complaint.View.People.create)             {Complaint.View.People.create();}
        if (Complaint.View.Documents.create)          {Complaint.View.Documents.create();}
        if (Complaint.View.Notes.create)              {Complaint.View.Notes.create();}
        if (Complaint.View.References.create)         {Complaint.View.References.create();}
        if (Complaint.View.Tasks.create)              {Complaint.View.Tasks.create();}
        if (Complaint.View.Participants.create)       {Complaint.View.Participants.create();}
        if (Complaint.View.Location.create)           {Complaint.View.Location.create();}
        if (Complaint.View.History.create)            {Complaint.View.History.create();}
        if (Complaint.View.OutlookCalendar.create)    {Complaint.View.OutlookCalendar.create();}
        if (Complaint.View.Time.create)               {Complaint.View.Time.create();}
        if (Complaint.View.Cost.create)               {Complaint.View.Cost.create();}
    }
    ,onInitialized: function() {
        if (Complaint.View.MicroData.onInitialized)      {Complaint.View.MicroData.onInitialized();}
        if (Complaint.View.Navigator.onInitialized)      {Complaint.View.Navigator.onInitialized();}
        if (Complaint.View.Content.onInitialized)        {Complaint.View.Content.onInitialized();}
        if (Complaint.View.Action.onInitialized)         {Complaint.View.Action.onInitialized();}
        if (Complaint.View.Detail.onInitialized)         {Complaint.View.Detail.onInitialized();}
        if (Complaint.View.People.onInitialized)         {Complaint.View.People.onInitialized();}
        if (Complaint.View.Documents.onInitialized)      {Complaint.View.Documents.onInitialized();}
        if (Complaint.View.Notes.onInitialized)          {Complaint.View.Notes.onInitialized();}
        if (Complaint.View.References.onInitialized)     {Complaint.View.References.onInitialized();}
        if (Complaint.View.Tasks.onInitialized)          {Complaint.View.Tasks.onInitialized();}
        if (Complaint.View.Participants.onInitialized)   {Complaint.View.Participants.onInitialized();}
        if (Complaint.View.Location.onInitialized)       {Complaint.View.Location.onInitialized();}
        if (Complaint.View.History.onInitialized)        {Complaint.View.History.onInitialized();}
        if (Complaint.View.OutlookCalendar.onInitialized) {Complaint.View.OutlookCalendar.onInitialized();}
        if (Complaint.View.Time.onInitialized)           {Complaint.View.Time.onInitialized();}
        if (Complaint.View.Cost.onInitialized)           {Complaint.View.Cost.onInitialized();}
    }

    ,getActiveComplaintId: function() {
        return ObjNav.View.Navigator.getActiveObjId();
    }
    ,getActiveComplaint: function() {
        var objId = ObjNav.View.Navigator.getActiveObjId();
        var complaint = null;
        if (Acm.isNotEmpty(objId)) {
            complaint = ObjNav.Model.Detail.getCacheObject(Complaint.Model.DOC_TYPE_COMPLAINT, objId);
        }
        return complaint;
    }

    ,MicroData: {
        create : function() {
            this.treeFilter = Acm.Object.MicroData.getJson("treeFilter");
            this.treeSort   = Acm.Object.MicroData.getJson("treeSort");
            this.token      = Acm.Object.MicroData.get("token");

            this.formUrls = {};
            this.formUrls.closeComplaintFormUrl          = Acm.Object.MicroData.get("closeComplaintFormUrl");
            this.formUrls.editCloseComplaintFormUrl      = Acm.Object.MicroData.get("editCloseComplaintFormUrl");
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

            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_CHANGED_COMPLAINT_TITLE       , this.onViewChangedComplaintTitle);
        }
        ,onInitialized: function() {
        }

        ,onViewChangedComplaintTitle: function(complaintId, title) {
            var complaintSolr = ObjNav.Model.List.getSolrObject(Complaint.Model.DOC_TYPE_COMPLAINT, complaintId);
            if (ObjNav.Model.List.validateObjSolr(complaintSolr)) {
                complaintSolr.title_parseable = Acm.goodValue(title);
                ObjNav.View.Navigator.updateObjNode(Complaint.Model.DOC_TYPE_COMPLAINT, complaintId);
            }
        }

        ,getTreeArgs: function() {
            return {
                lazyLoad: function(event, data) {
                    Complaint.View.Navigator.lazyLoad(event, data);
                }
                ,getContextMenu: function(node) {
                    Complaint.View.Navigator.getContextMenu(node);
                }
            };
        }
        ,lazyLoad: function(event, data) {
            var key = data.node.key;
            var nodeType = ObjNav.Model.Tree.Key.getNodeTypeByKey(key);
            switch (nodeType) {
                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, Complaint.Model.DOC_TYPE_COMPLAINT]):
                    data.result = AcmEx.FancyTreeBuilder
                        .reset()
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_DETAILS
                            ,title: $.t("complaint.navigation.leaf-title.details")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_LOCATION
                            ,title: $.t("complaint.navigation.leaf-title.location")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_PEOPLE
                            ,title: $.t("complaint.navigation.leaf-title.people")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS
                            ,title: $.t("complaint.navigation.leaf-title.documents")
//                            ,folder: true
//                            ,lazy: true
//                            ,cache: false
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_TASKS
                            ,title: $.t("complaint.navigation.leaf-title.tasks")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_NOTES
                            ,title: $.t("complaint.navigation.leaf-title.notes")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_PARTICIPANTS
                            ,title: $.t("complaint.navigation.leaf-title.participants")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_REFERENCES
                            ,title: $.t("complaint.navigation.leaf-title.references")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_HISTORY
                            ,title: $.t("complaint.navigation.leaf-title.history")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_CALENDAR
                            ,title: $.t("complaint.navigation.leaf-title.calendar")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_TIME
                            ,title: $.t("complaint.navigation.leaf-title.time")
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Complaint.Model.Tree.Key.NODE_TYPE_PART_COST
                            ,title: $.t("complaint.navigation.leaf-title.cost")
                        })
                        .getTree();

                    break;

                default:
                    data.result = [];
                    break;
            }
        }

        ,getContextMenu: function(node) {
            var key = node.key;
            var menu = [
                {title: $.t("complaint.context-menu.menu-title.menu") + key, cmd: "cut", uiIcon: "ui-icon-scissors"},
                {title: $.t("complaint.context-menu.menu-title.copy"), cmd: "copy", uiIcon: "ui-icon-copy"},
                {title: $.t("complaint.context-menu.menu-title.paste"), cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: false },
                {title: "----"},
                {title: $.t("complaint.context-menu.menu-title.edit"), cmd: "edit", uiIcon: "ui-icon-pencil", disabled: true },
                {title: $.t("complaint.context-menu.menu-title.delete"), cmd: "delete", uiIcon: "ui-icon-trash", disabled: true },
                {title: $.t("complaint.context-menu.menu-title.more"), children: [
                    {title: $.t("complaint.context-menu.menu-title.sub1"), cmd: "sub1"},
                    {title: $.t("complaint.context-menu.menu-title.sub2"), cmd: "sub1"}
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
            Acm.Dialog.error(Acm.goodValue(error.errMsg, $.t("complaint.msg.error-retrieving-complaint-data")));
        }
    }


    ,Action: {
        create: function() {
            this.$chkRestrict     = $("#restrict");
            this.$chkRestrict.on("click", function(e) {Complaint.View.Action.onClickChkRestrict(e, this);});

            this.$btnComplaintClose = $("#closeComplaint");
            this.$btnComplaintClose.click(function(e){Complaint.View.Action.onClickBtnComplaintClose(e, this)});

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT         ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        //---- demo how to use document tree picker ----
        ,onPickDocumentDemo: function() {
            DocTree.showDocumentDialog({name: "demoDialog"
                ,title: $.t("complaint.document-dialog.title")
                ,btnOkText: $.t("complaint.document-dialog.btn-ok")
                ,btnCancelText: $.t("complaint.document-dialog.btn-cancel")
                ,folderOnly: false
                ,onClickBtnPrimary : function(event, ctrl) {
                    var a1 = DocTree.View.getSelectedNodes();

//                    SearchBase.View.Results.getSelectedRows().each(function () {
//                        var record = $(this).data('record');
//
//                        var z = 1;
//                        alert("ok");
//                    });
                    alert("ok");
                }
                ,onClickBtnDefault : function(event, ctrl) {
                    alert("cancel");
                }
            });
        }
        //---------------------------------------

        ,onClickBtnComplaintClose: function(event, ctrl) {
//borrow it to test document picker dialog
//            this.onPickDocumentDemo();
//            return;

            var url = Complaint.View.MicroData.formUrls.closeComplaintFormUrl;
            var c = Complaint.View.getActiveComplaint();
            if (Complaint.Model.Detail.validateComplaint(c) && Acm.isNotEmpty(url)) {
                url = url.replace("_data=(", "_data=(complaintId:'" + c.complaintId
                    + "',complaintNumber:'" + Acm.goodValue(c.complaintNumber)
                    + "',mode:'create',");
                    //+ "',mode:'edit',xmlId:'816',pdfId:'818',requestId:'813',");

                Acm.Dialog.openWindow(url, "", 1060, 700, function() {});
            }
        }

        ,onModelRetrievedObject: function(objData) {
            Complaint.View.Action.populate(objData);
        }
        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            Complaint.View.Action.populate(objData);
            SubscriptionOp.Model.checkSubscription(App.getUserName(), objType, objId);
        }

        ,onClickChkRestrict: function(event,ctrl){
            var restriction = ($(ctrl).prop('checked')) ? true : false;
            Complaint.Controller.viewChangedRestriction(Complaint.View.getActiveComplaintId(), restriction);
        }

        ,populate: function(complaint) {
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                this.setCheckedChkRestrict(Acm.goodValue(complaint.restricted, false));
            }
        }

        ,setCheckedChkRestrict: function(value){
            Acm.Object.setChecked(this.$chkRestrict, value);
        }

    }

    ,Detail: {
        create: function() {
            this.$labComplaintNumber   = $("#complaintNum");
            this.$lnkComplaintTitle    = $("#complaintTitle");
            AcmEx.Object.XEditable.useEditable(this.$lnkComplaintTitle, {
                success: function(response, newValue) {
                    Complaint.Controller.viewChangedComplaintTitle(Complaint.View.getActiveComplaintId(), newValue);
                }
            });
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_SAVED_COMPLAINT_TITLE   ,this.onModelSavedComplaintTitle);


            this.$lnkIncidentDate      = $("#incident");
            AcmEx.Object.XEditable.useEditableDate(this.$lnkIncidentDate, {
                success: function(response, newValue) {
                    Complaint.Controller.viewChangedIncidentDate(Complaint.View.getActiveComplaintId(), newValue);
                }
            });
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_SAVED_INCIDENT_DATE     ,this.onModelSavedIncidentDate);


            this.$lnkPriority     = $("#priority");
            this.$lnkAssignee     = $("#assigned");
            this.$lnkGroup 		  = $("#group");
            this.$lnkComplaintType  = $("#type");
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_FOUND_ASSIGNEES         ,this.onModelFoundAssignees);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_SAVED_ASSIGNEE          ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_RETRIEVED_GROUPS        ,this.onModelRetrievedGroups);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_SAVED_GROUP	           ,this.onModelSavedGroup);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_FOUND_COMPLAINT_TYPES   ,this.onModelFoundComplaintTypes);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_SAVED_COMPLAINT_TYPE    ,this.onModelSavedComplaintType);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_FOUND_PRIORITIES        ,this.onModelFoundPriorities);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_SAVED_PRIORITY          ,this.onModelSavedPriority);

            this.$lnkStatus       = $("#status");


            this.$divDetail       = $(".divDetail");
            this.$btnEditDetail   = $("#tabDetail button:eq(0)");
            this.$btnSaveDetail   = $("#tabDetail button:eq(1)");
            this.$btnEditDetail.on("click", function(e) {Complaint.View.Detail.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {Complaint.View.Detail.onClickBtnSaveDetail(e, this);});
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_SAVED_DETAIL            ,this.onModelSavedDetail);


            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }


        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            Complaint.View.Detail.populateComplaint(objData);
        }
        ,onModelRetrievedObject: function(objData) {
            Complaint.View.Detail.populateComplaint(objData);
        }

        ,onModelFoundAssignees: function(assignees) {
            var choices = [];
            $.each(assignees, function(idx, val) {
                var opt = {};
                opt.value = val.userId;
                opt.text = val.fullName;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(Complaint.View.Detail.$lnkAssignee, {
                source: choices
                ,success: function(response, newValue) {
                    Complaint.Controller.viewChangedAssignee(Complaint.View.getActiveComplaintId(), newValue);
                }
            	,currentValue: Complaint.Model.Detail.getAssignee(Complaint.View.getActiveComplaint())
            });
        }
        ,onModelRetrievedGroups: function(groups) {
            var choices = [];
            $.each(groups, function(idx, val) {
                var opt = {};
                opt.value = val.object_id_s;
                opt.text = val.name;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(Complaint.View.Detail.$lnkGroup, {
                source: choices
                ,success: function(response, newValue) {
                    Complaint.Controller.viewChangedGroup(Complaint.View.getActiveComplaintId(), newValue);
                }
            	,currentValue: Complaint.Model.Detail.getGroup(Complaint.View.getActiveComplaint())
            });
        }
        ,onModelFoundComplaintTypes: function(complaintTypes) {
            var choices = [];
            $.each(complaintTypes, function(idx, val) {
                var opt = {};
                opt.value = val;
                opt.text = val;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(Complaint.View.Detail.$lnkComplaintType, {
                source: choices
                ,success: function(response, newValue) {
                    Complaint.Controller.viewChangedComplaintType(Complaint.View.getActiveComplaintId(), newValue);
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

            AcmEx.Object.XEditable.useEditable(Complaint.View.Detail.$lnkPriority, {
                source: choices
                ,success: function(response, newValue) {
                    Complaint.Controller.viewChangedPriority(Complaint.View.getActiveComplaintId(), newValue);
                }
            });
        }

        ,onModelSavedComplaintTitle: function(complaintId, title) {
            if (title.hasError) {
                Complaint.View.Detail.setTextLnkComplaintTitle("(Error)");
            }
        }
        ,onModelSavedIncidentDate: function(complaintId, created) {
            if (created.hasError) {
                Complaint.View.Detail.setTextLnkIncidentDate("(Error)");
            }
        }
        ,onModelSavedAssignee: function(complaintId, assginee) {
            if (assginee.hasError) {
                Complaint.View.Detail.setTextLnkAssignee("(Error)");
            }
        }
        ,onModelSavedGroup: function(complaintId, group) {
            if (group.hasError) {
                Complaint.View.Detail.setTextLnkGroup("(Error)");
            }
        }
        ,onModelSavedComplaintType: function(complaintId, subjectType) {
            if (subjectType.hasError) {
                Complaint.View.Detail.setTextLnkComplaintType("(Error)");
            }
        }
        ,onModelSavedPriority: function(complaintId, priority) {
            if (priority.hasError) {
                Complaint.View.Detail.setTextLnkPriority("(Error)");
            }
        }
        ,onModelSavedDetail: function(complaintId, details) {
            if (details.hasError) {
                Complaint.View.Detail.setHtmlDivDetail("(Error)");
            }
        }

        // TODO: Resolve string resources loading order
        //,DIRTY_EDITING_DETAIL:  $.t("complaint.detail.dirty-editing-detail")
        ,DIRTY_EDITING_DETAIL:  "Editing complaint detail"
        ,onClickBtnEditDetail: function(event, ctrl) {
            App.Object.Dirty.declare(Complaint.View.Detail.DIRTY_EDITING_DETAIL);
            Complaint.View.Detail.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = Complaint.View.Detail.saveDivDetail();
            Complaint.Controller.viewChangedDetail(Complaint.View.getActiveComplaintId(), htmlDetail);
            App.Object.Dirty.clear(Complaint.View.Detail.DIRTY_EDITING_DETAIL);
        }


        ,populateComplaint: function(c) {
            if (Complaint.Model.Detail.validateComplaint(c)) {
                var assignee = Complaint.Model.Detail.getAssignee(c);
                this.setTextLnkAssignee(Acm.goodValue(assignee));

                var group = Complaint.Model.Detail.getGroup(c);
                this.setTextLnkGroup(Acm.goodValue(group));

                this.setTextLnkComplaintTitle(Acm.goodValue(c.complaintTitle));
                this.setTextLabComplaintNumber(Acm.goodValue(c.complaintNumber));
                this.setTextLnkIncidentDate(Acm.getDateFromDatetime(c.incidentDate));
                this.setTextLnkComplaintType(Acm.goodValue(c.complaintType));
                this.setTextLnkPriority(Acm.goodValue(c.priority));
                this.setTextLnkStatus("  (" + Acm.goodValue(c.status) +")");
                this.setHtmlDivDetail(Acm.goodValue(c.details));
            }
        }

        ,setTextLabComplaintNumber: function(txt) {
            Acm.Object.setText(this.$labComplaintNumber, txt);
        }
        ,setTextLnkComplaintTitle: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkComplaintTitle, txt);
        }
        ,setTextLnkIncidentDate: function(txt) {
            AcmEx.Object.XEditable.setDate(this.$lnkIncidentDate, txt);
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
        ,setTextLnkAssignee: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkAssignee, txt);
        }
        ,setTextLnkGroup: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkGroup, txt);
        }
        ,setTextLnkComplaintType: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkComplaintType, txt);
        }
        ,setTextLnkPriority: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkPriority, txt);
        }
        ,setTextLnkStatus: function(txt) {
            Acm.Object.setText(this.$lnkStatus, txt);
        }

    }


    ,People: {
        create: function() {
            this.$divPeople = $("#divPeople");
            this.createJTable(this.$divPeople);

            if (this.ContactMethods.create)     {this.ContactMethods.create();}
            if (this.SecurityTags.create)       {this.SecurityTags.create();}
            if (this.Organizations.create)      {this.Organizations.create();}
            if (this.Addresses.create)          {this.Addresses.create();}
            if (this.Aliases.create)            {this.Aliases.create();}

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT         ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);

            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_PERSON_ASSOCIATION    ,this.onModelAddedPersonAssociation);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPDATED_PERSON_ASSOCIATION  ,this.onModelUpdatedPersonAssociation);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_DELETED_PERSON_ASSOCIATION  ,this.onModelDeletedPersonAssociation);
        }
        ,onInitialized: function() {
            if (Complaint.View.People.ContactMethods.onInitialized)     {Complaint.View.People.ContactMethods.onInitialized();}
            if (Complaint.View.People.SecurityTags.onInitialized)       {Complaint.View.People.SecurityTags.onInitialized();}
            if (Complaint.View.People.Organizations.onInitialized)      {Complaint.View.People.Organizations.onInitialized();}
            if (Complaint.View.People.Addresses.onInitialized)          {Complaint.View.People.Addresses.onInitialized();}
            if (Complaint.View.People.Aliases.onInitialized)            {Complaint.View.People.Aliases.onInitialized();}
        }

        ,onModelRetrievedObject: function(complaint) {
            AcmEx.Object.JTable.load(Complaint.View.People.$divPeople);
        }
        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Complaint.View.People.$divPeople);
        }
        ,onModelAddedPersonAssociation: function(personAssociation) {
            if (personAssociation.hasError) {
                Acm.Dialog.info(personAssociation.errorMsg);
            }
            else{
                AcmEx.Object.JTable.load(Complaint.View.People.$divPeople);
            }
        }
        ,onModelUpdatedPersonAssociation: function(personAssociation) {
            if (personAssociation.hasError) {
                Acm.Dialog.info(personAssociation.errorMsg);
            }
            else{
                AcmEx.Object.JTable.load(Complaint.View.People.$divPeople);
            }
        }
        ,onModelDeletedPersonAssociation: function(personAssociationId) {
            if (personAssociationId.hasError) {
                Acm.Dialog.info(personAssociationId.errorMsg);
            }
            else{
                AcmEx.Object.JTable.load(Complaint.View.People.$divPeople);
            }
        }

        ,createJTable: function($s) {
            AcmEx.Object.JTable.useChildTable($s
                ,[
                    Complaint.View.People.ContactMethods.createLink
                    ,Complaint.View.People.Organizations.createLink
                    ,Complaint.View.People.Addresses.createLink
                    ,Complaint.View.People.Aliases.createLink
                ]
                ,{
                    title: $.t("complaint.people.table.title")
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("complaint.people.table.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function(postData, jtParams) {
                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            var complaint = Complaint.View.getActiveComplaint();
                            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                                var personAssociations = complaint.personAssociations;
                                for (var i = 0; i < personAssociations.length; i++) {
                                    if (Complaint.Model.People.validatePersonAssociation(personAssociations[i])) {
                                        //
                                        //make initiator on top of table and disable delete button
                                        //
                                        //if(Acm.isNotEmpty(complaint.originator)){
                                        if (Acm.isNotEmpty(complaint.originator) && (personAssociations[i].id == Acm.goodValue(complaint.originator.id))) {
                                                rc.Records.unshift({
                                                    assocId: personAssociations[i].id
                                                    , title: personAssociations[i].person.title
                                                    , givenName: personAssociations[i].person.givenName
                                                    , familyName: personAssociations[i].person.familyName
                                                    , personType: personAssociations[i].personType
                                                });
                                            }
                                        //}
                                        else{
                                            rc.Records.push({
                                                assocId: personAssociations[i].id
                                                , title: personAssociations[i].person.title
                                                , givenName: personAssociations[i].person.givenName
                                                , familyName: personAssociations[i].person.familyName
                                                , personType: personAssociations[i].personType
                                            });
                                        }
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
                            title: $.t("complaint.people.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }
                        ,title: {
                            title: $.t("complaint.people.table.field.title")
                            ,width: '10%'
                            ,options: Complaint.Model.Lookup.getPersonTitles()
                        }
                        ,givenName: {
                            title: $.t("complaint.people.table.field.first-name")
                            ,width: '15%'
                        }
                        ,familyName: {
                            title: $.t("complaint.people.table.field.last-name")
                            ,width: '15%'
                        }
                        ,personType: {
                            title: $.t("complaint.people.table.field.type")
                            ,options: Complaint.Model.Lookup.getPersonTypes()
                        }
                    }
                    ,rowInserted: function (event, data) {
                        var complaint = Complaint.View.getActiveComplaint();
                        //remove the edit/delete buttons if needed - make this check for initiator
                        //initiator record cannot be deleted, so disable delete button
                        if (Complaint.Model.Detail.validateComplaint(complaint)) {
                            if(Acm.isNotEmpty(complaint.originator) && Acm.isNotEmpty(complaint.originator.id)){
                                if (data.record.assocId == complaint.originator.id){
                                    //data.row.find('.jtable-edit-command-button').hide();
                                    data.row.find('.jtable-delete-command-button').hide();
                                }
                            }
                        }
                    }
                    ,recordAdded: function(event, data){
                        var record = data.record;
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId) {
                            var pa = {};
                            pa.personType = record.personType;
                            //pa.personDescription = record.personDescription;
                            pa.person = {};
                            pa.person.title = record.title;
                            pa.person.givenName = record.givenName;
                            pa.person.familyName = record.familyName;
                            Complaint.Controller.viewAddedPersonAssociation(complaintId, pa);
                        }
                    }

                    ,recordUpdated: function(event, data){
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var assocId = record.assocId;
                        var complaintId = Complaint.View.getActiveComplaintId();
                        var c = Complaint.View.getActiveComplaint();
                        if (Complaint.Model.Detail.validateComplaint(c)) {
                            if (c.personAssociations.length > whichRow) {
                                var pa = c.personAssociations[whichRow];
                                if (Complaint.Model.People.validatePersonAssociation(pa)) {
                                    pa.person.title = record.title;
                                    pa.person.givenName = record.givenName;
                                    pa.person.familyName = record.familyName;
                                    pa.personType = record.personType;
                                    Complaint.Controller.viewUpdatedPersonAssociation(complaintId, pa);
                                }
                            }
                        }
                    }
                    ,recordDeleted: function(event,data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var personAssociationId = record.assocId;
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < personAssociationId) {
                            Complaint.Controller.viewDeletedPersonAssociation(complaintId, personAssociationId);
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
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_CONTACT_METHOD        ,this.onModelAddedContactMethod);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPDATED_CONTACT_METHOD      ,this.onModelUpdatedContactMethod);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_DELETED_CONTACT_METHOD      ,this.onModelDeletedContactMethod);
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
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Complaint.View.People.ContactMethods.onOpen, Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_CONTACT_METHODS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_CONTACT_METHODS
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
                                var c = Complaint.View.getActiveComplaint();
                                if (Complaint.Model.Detail.validateComplaint(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Complaint.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                            return Complaint.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return Complaint.View.People._commonTypeValueRecord($row, postData);
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
                            title: $.t("complaint.contact-method.table.field.type")
                            ,width: '15%'
                            ,options: Complaint.Model.Lookup.getDeviceTypes()
                        }
                        ,value: {
                            title: $.t("complaint.contact-method.table.field.value")
                            ,width: '30%'
                        }
                        ,created: {
                            title: $.t("complaint.contact-method.table.field.date-added")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: $.t("complaint.contact-method.table.field.added-by")
                            ,width: '30%'
                            ,create: false
                            ,edit: false
                        }
                    }
                    ,recordAdded: function (event, data) {
                        //var recordParent = $row.closest('tr').data('record');
                        //if (recordParent && recordParent.assocId && 0 < complaintId) {
                        //    var assocId = recordParent.assocId;
                        var record = data.record;
                        var contactMethod = {};
                        var assocId = record.assocId;
                        contactMethod.type  = Acm.goodValue(record.type);
                        contactMethod.value = Acm.goodValue(record.value);
                        contactMethod.created = Acm.getCurrentDayInternal();
                        contactMethod.creator = Acm.goodValue(record.creator);
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId) {
                            Complaint.Controller.viewAddedContactMethod(complaintId, assocId, contactMethod);
                        }
                    }
                    ,recordUpdated: function (event, data) {
                        //var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        //var recordParent = $row.closest('tr').data('record');
                        //if (recordParent && recordParent.assocId && 0 < complaintId) {
                        //    var assocId = recordParent.assocId;
                        var record = data.record;
                        var contactMethod = {};
                        var assocId = record.assocId;
                        contactMethod.id    = Acm.goodValue(record.id, 0);
                        contactMethod.type  = Acm.goodValue(record.type);
                        contactMethod.value = Acm.goodValue(record.value);
                        contactMethod.created = Acm.getCurrentDayInternal();
                        contactMethod.creator = Acm.goodValue(record.creator);
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < contactMethod.id) {
                            Complaint.Controller.viewUpdatedContactMethod(complaintId, assocId, contactMethod);
                        }
                    }
                    ,recordDeleted: function (event, data) {
                        //var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var assocId = record.assocId;
                        var contactMethodId = Acm.goodValue(record.id, 0);
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < contactMethodId) {
                            Complaint.Controller.viewDeletedContactMethod(complaintId, assocId, contactMethodId);
                        }
                    }
                });
            }
        }

        ,SecurityTags: {
            create: function() {
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_SECURITY_TAG        ,this.onModelAddedSecurityTag);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPDATED_SECURITY_TAG      ,this.onModelUpdatedSecurityTag);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_DELETED_SECURITY_TAG      ,this.onModelDeletedSecurityTag);
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
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Complaint.View.People.ContactMethods.onOpen, Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_SECURITY_TAGS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_SECURITY_TAGS
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
                                var c = Complaint.View.getActiveComplaint();
                                if (Complaint.Model.Detail.validateComplaint(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Complaint.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                            return Complaint.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return Complaint.View.People._commonTypeValueRecord($row, postData);
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
                            title: $.t("complaint.security-tags.table.field.type")
                            ,width: '15%'
                            ,options: Complaint.Model.Lookup.getSecurityTagTypes()
                        }
                        ,value: {
                            title: $.t("complaint.security-tags.table.field.value")
                            ,width: '30%'
                        }
                        ,created: {
                            title: $.t("complaint.security-tags.table.field.date-added")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: $.t("complaint.security-tags.table.field.added-by")
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
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId) {
                            Complaint.Controller.viewAddedSecurityTag(complaintId, assocId, securityTag);
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
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < securityTag.id) {
                            Complaint.Controller.viewUpdatedSecurityTag(complaintId, assocId, securityTag);
                        }
                    }
                    ,recordDeleted: function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var securityTagId = Acm.goodValue(record.id, 0);
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < securityTagId) {
                            Complaint.Controller.viewDeletedSecurityTag(complaintId, assocId, securityTagId);
                        }
                    }
                });
            }
        }

        ,Organizations: {
            create: function() {
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_ORGANIZATION        ,this.onModelAddedOrganization);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPDATED_ORGANIZATION      ,this.onModelUpdatedOrganization);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_DELETED_ORGANIZATION      ,this.onModelDeletedOrganization);
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
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Complaint.View.People.Organizations.onOpen, Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("complaint.organizations.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;
                                var c = Complaint.View.getActiveComplaint();
                                if (Complaint.Model.Detail.validateComplaint(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Complaint.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                            return Complaint.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return Complaint.View.People._commonTypeValueRecord($row, postData);
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
                            title: $.t("complaint.organizations.table.field.type")
                            ,width: '15%'
                            ,options: Complaint.Model.Lookup.getOrganizationTypes()
                        }
                        , value: {
                            title: $.t("complaint.organizations.table.field.value")
                            ,width: '30%'
                        }
                        , created: {
                            title: $.t("complaint.organizations.table.field.date-added")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                        }
                        , creator: {
                            title: $.t("complaint.organizations.table.field.added-by")
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
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId) {
                            Complaint.Controller.viewAddedOrganization(complaintId, assocId, organization);
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
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < organization.organizationId) {
                            Complaint.Controller.viewUpdatedOrganization(complaintId, assocId, organization);
                        }
                    }
                    , recordDeleted: function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var organizationId = Acm.goodValue(record.id, 0);
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < organizationId) {
                            Complaint.Controller.viewDeletedOrganization(complaintId, assocId, organizationId);
                        }
                    }
                });
            }
        }

        ,Addresses: {
            create: function() {
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_ADDRESS        ,this.onModelAddedAddress);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPDATED_ADDRESS      ,this.onModelUpdatedAddress);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_DELETED_ADDRESS      ,this.onModelDeletedAddress);
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
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Complaint.View.People.Addresses.onOpen, Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_ADDRESSES);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_ADDRESSES
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("complaint.organizations.addresses.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;
                                var c = Complaint.View.getActiveComplaint();
                                if (Complaint.Model.Detail.validateComplaint(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Complaint.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                            title: $.t("complaint.addresses.table.field.type")
                            ,width: '8%'
                            ,options: Complaint.Model.Lookup.getLocationTypes()
                        }
                        ,streetAddress: {
                            title: $.t("complaint.addresses.table.field.address")
                            ,width: '20%'
                        }
                        ,city: {
                            title: $.t("complaint.addresses.table.field.city")
                            ,width: '10%'
                        }
                        ,state: {
                            title: $.t("complaint.addresses.table.field.state")
                            ,width: '8%'
                        }
                        ,zip: {
                            title: $.t("complaint.addresses.table.field.zip")
                            ,width: '8%'
                        }
                        ,country: {
                            title: $.t("complaint.addresses.table.field.country")
                            ,width: '8%'
                        }
                        ,created: {
                            title: $.t("complaint.addresses.table.field.date-added")
                            ,width: '15%'
                            ,create: false
                            ,edit: false
                        }
                        ,creator: {
                            title: $.t("complaint.addresses.table.field.added-by")
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
                        address.creator       = Acm.goodValue(record.creator);
                        address.created       = Acm.getCurrentDayInternal();
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId) {
                            Complaint.Controller.viewAddedAddress(complaintId, assocId, address);
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
                        address.creator       = Acm.goodValue(record.creator);
                        address.created       = Acm.getCurrentDayInternal();
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < address.id) {
                            Complaint.Controller.viewUpdatedAddress(complaintId, assocId, address);
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var addressId  = Acm.goodValue(record.id, 0);
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < addressId) {
                            Complaint.Controller.viewDeletedAddress(complaintId, assocId, addressId);
                        }
                    }
                });
            }
        }

        ,Aliases: {
            create: function() {
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_PERSON_ALIAS        ,this.onModelAddedPersonAlias);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPDATED_PERSON_ALIAS      ,this.onModelUpdatedPersonAlias);
                Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_DELETED_PERSON_ALIAS      ,this.onModelDeletedPersonAlias);
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
                    AcmEx.Object.JTable.toggleChildTable($jt, $link, Complaint.View.People.Aliases.onOpen, Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES);
                    e.preventDefault();
                });
                return $link;
            }
            ,onOpen: function($jt, $row) {
                AcmEx.Object.JTable.useAsChild($jt, $row, {
                    title: Complaint.Model.Lookup.PERSON_SUBTABLE_TITLE_ALIASES
                    ,paging: true //fix me
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("complaint.aliases.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var recordParent = $row.closest('tr').data('record');
                            if (recordParent && recordParent.assocId) {
                                var assocId = recordParent.assocId;
                                var c = Complaint.View.getActiveComplaint();
                                if (Complaint.Model.Detail.validateComplaint(c)) {
                                    var personAssociations = c.personAssociations;
                                    var personAssociation = Complaint.Model.People.findPersonAssociation(assocId, personAssociations);
                                    if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                            return Complaint.View.People._commonTypeValueRecord($row, postData);
                        }
                        ,updateAction: function (postData, jtParams) {
                            return Complaint.View.People._commonTypeValueRecord($row, postData);
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
                            title: $.t("complaint.aliases.table.field.type")
                            ,width: '15%'
                            ,options: Complaint.Model.Lookup.getAliasTypes()
                        }
                        ,value: {
                            title: $.t("complaint.aliases.table.field.value")
                            ,width: '30%'
                        }
                        ,created: {
                            title: $.t("complaint.aliases.table.field.date-added")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                            //,type: 'date'
                            //,displayFormat: 'yy-mm-dd'
                        }
                        ,creator: {
                            title: $.t("complaint.aliases.table.field.added-by")
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
                        personAlias.creator  = Acm.goodValue(record.creator);
                        personAlias.created = Acm.getCurrentDayInternal();
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId) {
                            Complaint.Controller.viewAddedPersonAlias(complaintId, assocId, personAlias);
                        }
                    }
                    ,recordUpdated : function (event, data) {
                        var record = data.record;
                        var personAlias = {};
                        var assocId = record.assocId;
                        personAlias.id         = Acm.goodValue(record.id, 0);
                        personAlias.aliasType  = Acm.goodValue(record.type);
                        personAlias.aliasValue = Acm.goodValue(record.value);
                        personAlias.creator  = Acm.goodValue(record.creator);
                        personAlias.created = Acm.getCurrentDayInternal();
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < personAlias.id) {
                            Complaint.Controller.viewUpdatedPersonAlias(complaintId, assocId, personAlias);
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var record = data.record;
                        var assocId = record.assocId;
                        var personAliasId = Acm.goodValue(record.id, 0);
                        var complaintId = Complaint.View.getActiveComplaintId();

                        if (0 < complaintId && 0 < assocId && 0 < personAliasId) {
                            Complaint.Controller.viewDeletedPersonAlias(complaintId, assocId, personAliasId);
                        }
                    }
                });
            } //onOpen
        }
    }

    ,Documents:{
        create : function() {
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_TREE_NODE        ,this.onViewSelectedTreeNode);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedTreeNode: function(key) {
            var lastKeyPart = ObjNav.Model.Tree.Key.getLastKeyPart(key);
            if (Complaint.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS == lastKeyPart) {
                DocTree.View.expandTopNode();
            }
        }
        ,onViewSelectedObject: function(nodeType, nodeId) {
            DocTree.Controller.viewChangedParent(nodeType, nodeId);
        }

        ,uploadForm: function(type, onCloseForm) {
            var complaintId = Complaint.View.getActiveComplaintId();
            var complaint = Complaint.View.getActiveComplaint();
            if (Complaint.Model.Detail.validateComplaint(complaint) )
            {
                //var url = Acm.goodValue(Complaint.View.MicroData.formUrls[report]);
                var url = null;
                var fileType = Complaint.View.MicroData.findFileTypeByType(type);
                if (fileType) {
                    url = Acm.goodValue(fileType.url);
                }
                if (Acm.isNotEmpty(url)) {
                    // an apostrophe in complaint title will make Frevvo throw up.  Need to encode it here, then rules in
                    // the Frevvo form will decode it.
                    var complaintTitle = Acm.goodValue(complaint.complaintTitle);
                    complaintTitle = complaintTitle.replace("'", "_0027_"); // 0027 is the Unicode string for apostrophe
                    url = url.replace("_data=(", "_data=(type:'complaint', complaintId:'" + complaint.complaintId + "',complaintNumber:'" + Acm.goodValue(complaint.complaintNumber) + "',complaintTitle:'" + complaintTitle + "',complaintPriority:'" + Acm.goodValue(complaint.priority) + "',");
                    Acm.Dialog.openWindow(url, "", 1060, $(window).height() - 30, onCloseForm);
                }
            }
        }
    }

    ,Documents_JTable_To_Retire:{
        create : function() {
//            this.$tree = $("#treeDoc");
//
//            this.$divDocuments = $("#divDocuments");
//            this.createJTableDocuments(this.$divDocuments);
//
//            this.$btnAddNewDocument = $("#addDocument");
//            this.$btnAddNewDocument.on("change", function(e) {Complaint.View.Documents.onChangeFileInput(e, this);});
//
//            this.$formNewDocuments = $("#formAddDocument");
//            this.$formNewDocuments.submit(function(e) {Complaint.View.Documents.onSubmitAddNewDocuments(e, this);});
//
//            AcmEx.Object.JTable.clickAddRecordHandler(this.$divDocuments,this.onClickSpanAddDocument);
//            this.$spanAddDocument   = this.$divDocuments.find(".jtable-toolbar-item-add-record");
//            Complaint.View.Documents.fillReportSelection();
//            //AcmEx.Object.JTable.clickAddRecordHandler(this.$divDocuments,this.onClickSpanAddNewDocuments);



            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_TREE_NODE        ,this.onViewSelectedTreeNode);

//            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT         ,this.onModelRetrievedObject);
//            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPLOADED_DOCUMENTS    ,this.onModelUploadedDocuments);
//            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_DOCUMENT          ,this.onModelAddedDocument);

        }
        ,onInitialized: function() {
        }

        ,onViewSelectedTreeNode: function(key) {
            var lastKeyPart = ObjNav.Model.Tree.Key.getLastKeyPart(key);
            if (Complaint.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS == lastKeyPart) {
                DocTree.View.expandTopNode();
            }
        }
        ,onViewSelectedObject: function(nodeType, nodeId) {
            DocTree.Controller.viewChangedParent(nodeType, nodeId);
        }


        //----------------------------------------------------------------


//        ,onViewSelectedObject: function(nodeType, nodeId) {
//            AcmEx.Object.JTable.load(Complaint.View.Documents.$divDocuments);
//        }

        ,onModelRetrievedObject: function(complaint) {
            AcmEx.Object.JTable.load(Complaint.View.Documents.$divDocuments);
        }

        ,onModelUploadedDocuments: function(documents) {
            if (documents.hasError) {
                Acm.Dialog.info(documents.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Complaint.View.Documents.$divDocuments);
            }
        }
        /*,onClickSpanAddNewDocuments: function(event, ctrl) {
            Complaint.View.Documents.$btnAddNewDocument.click();
        }*/
        ,onChangeFileInput: function(event, ctrl) {
            Complaint.View.Documents.$formNewDocuments.submit();
        }
        ,beforeSpanAddDocument: function(html) {
            Complaint.View.Documents.$spanAddDocument.before(html);
        }
        ,fillReportSelection: function() {

//            try {
//                var formDocuments = JSON.parse(Complaint.View.MicroData.formUrls.formDocuments);
//            }catch(e) {
//
//            }
            var formDocuments = Complaint.View.MicroData.formDocuments;
            var html = "<span>"
                + "<select class='input-sm form-control input-s-sm inline v-middle'>"
                + "<option value=''>" + $.t("complaint.documents-to-retire.form-document.document-type") + "</option>";

            if (!Acm.isArrayEmpty(formDocuments)) {
                for (var i = 0; i < formDocuments.length; i ++) {
                    html += "<option value='" + formDocuments[i]["value"] + "'>" + formDocuments[i]["label"] + "</option>"
                }

                html += "<option value='mr'>" + $.t("complaint.documents-to-retire.form-document.medical-release") + "</option>"
                + "<option value='gr'>" + $.t("complaint.documents-to-retire.form-document.general-release") + "</option>"
                + "<option value='ev'>" + $.t("complaint.documents-to-retire.form-document.e-delivery") + "</option>"
                + "<option value='sig'>" + $.t("complaint.documents-to-retire.form-document.sf86-signature") + "</option>"
                + "<option value='noi'>" + $.t("complaint.documents-to-retire.form-document.notice-of-investigation") + "</option>"
                + "<option value='wir'>" + $.t("complaint.documents-to-retire.form-document.witness-interview-request") + "</option>"
                + "<option value='ot'>" + $.t("complaint.documents-to-retire.form-document.other") + "</option>";
            }

            html += "</select>"
            + "</span>";
            Complaint.View.Documents.beforeSpanAddDocument(html);
        }
        ,getSelectReport: function() {
            return Acm.Object.getSelectValue(this.$spanAddDocument.prev().find("select"));
        }
        ,getSelectReportText: function() {
            return Acm.Object.getSelectedText(this.$spanAddDocument.prev().find("select"));
        }
        ,uploadForm: function(report, onCloseForm) {
            var complaintId = Complaint.View.getActiveComplaintId();
            var complaint = Complaint.View.getActiveComplaint();
            if (Complaint.Model.Detail.validateComplaint(complaint) )
            {
                var url = Acm.goodValue(Complaint.View.MicroData.formUrls[report]);
                if (Acm.isNotEmpty(url)) {
                    // an apostrophe in complaint title will make Frevvo throw up.  Need to encode it here, then rules in
                    // the Frevvo form will decode it.
                    var complaintTitle = Acm.goodValue(complaint.complaintTitle);
                    complaintTitle = complaintTitle.replace("'", "_0027_"); // 0027 is the Unicode string for apostrophe
                    url = url.replace("_data=(", "_data=(type:'complaint', complaintId:'" + complaint.complaintId + "',complaintNumber:'" + Acm.goodValue(complaint.complaintNumber) + "',complaintTitle:'" + complaintTitle + "',complaintPriority:'" + Acm.goodValue(complaint.priority) + "',");
                    Acm.Dialog.openWindow(url, "", 1060, $(window).height() - 30, onCloseForm);
                }
            }
        }
        ,onClickSpanAddDocument: function(e) {
            var report = Complaint.View.Documents.getSelectReport();

            if(report == "roiFormUrl" || report == "electronicCommunicationFormUrl"){
                 this.uploadForm(report, function() {
                     Complaint.Controller.viewClosedAddDocumentWindow(Complaint.View.getActiveComplaintId());
                 });
            }
            else if(report && report != ""){
                Complaint.View.Documents.$btnAddNewDocument.click();
            }
        }
        ,onSubmitAddNewDocuments: function(event, ctrl) {
             event.preventDefault();
             var count = Complaint.View.Documents.$btnAddNewDocument[0].files.length;
             var report = Complaint.View.Documents.getSelectReportText();

             var fd = new FormData();
             fd.append("fileType", report);
             fd.append("parentObjectId", Complaint.View.getActiveComplaintId());
             fd.append("parentObjectType", Complaint.Model.DOC_TYPE_COMPLAINT);
             for(var i = 0; i < count; i++ ) {
                 fd.append("files[]", Complaint.View.Documents.$btnAddNewDocument[0].files[i]);
             }
             Complaint.Service.Documents.uploadDocuments(fd);
             Complaint.View.Documents.$formNewDocuments[0].reset();
        }
        ,onModelAddedDocument: function(complaintId) {
            if (complaintId.hasError) {
                ;
            } else {
                Complaint.Controller.viewClosedAddDocumentWindow(Complaint.View.getActiveComplaintId());
            }
        }
        ,_makeJtData: function(documents, totalDocuments) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Acm.isNotEmpty(documents)) {
                for (var i = 0; i < documents.length; i++) {
                    if(Complaint.Model.Documents.validateDocument(documents[i])){
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
            var divDocuments = $("#divDocuments");
            Complaint.View.Documents.createJTableDocuments(divDocuments);
        }
        , createJTableDocuments: function ($s) {
            AcmEx.Object.JTable.usePaging($s, {
                title: $.t("complaint.documents-to-retire.title")
                ,paging: true
                ,sorting: true
                ,pageSize: 10 //Set page size (default: 10)
                , messages: {
                    addNewRecord: $.t("complaint.documents-to-retire.msg.add-new-record")
                }
                , actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        var complaintId = Complaint.View.getActiveComplaintId();
                        if ( ! complaintId || 0 >= complaintId )
                        {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                        //var documentsCache = Complaint.Model.Documents.cacheDocuments.get(complaintId + "." + jtParams.jtStartIndex);
                        //if (Complaint.Model.Documents.validateDocuments(documentsCache)) {
                        //    var documents = documentsCache.children;
                        //    var totalDocuments = documentsCache.totalChildren;
                        //    return Complaint.View.Documents._makeJtData(documents, totalDocuments);
                        //} else {
                            return Complaint.Service.Documents.retrieveDocumentsDeferred(complaintId
                                ,postData
                                ,jtParams
                                ,sortMap
                                ,function(data) {
                                    if(Complaint.Model.Documents.validateDocuments(data)){
                                        var documents = data.children;
                                        var totalDocuments = data.totalChildren;
                                        return Complaint.View.Documents._makeJtData(documents, totalDocuments);
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
                        title: $.t("complaint.documents-to-retire.table.field.id")
                        , key: true
                        , list: false
                        , create: false
                        , edit: false
                        , defaultvalue: 0
                    }
                    , title: {
                        title: $.t("complaint.documents-to-retire.table.field.title")
                        , width: '50%'
                        , edit: false
                        , create: false
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + Complaint.Service.Documents.API_DOWNLOAD_DOCUMENT
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    , created: {
                        title: $.t("complaint.documents-to-retire.table.field.created")
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                    , creator: {
                        title: $.t("complaint.documents-to-retire.table.field.creator")
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                }
            });
        }

    }

    ,Notes: {
        create: function() {
            this.$divNotes = $("#divNotes");
            this.createJTableNotes(this.$divNotes);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_NOTE        ,this.onModelAddedNote);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPDATED_NOTE      ,this.onModelUpdatedNote);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_DELETED_NOTE      ,this.onModelDeletedNote);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(complaint) {
            AcmEx.Object.JTable.load(Complaint.View.Notes.$divNotes);
        }
        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Complaint.View.Notes.$divNotes);
        }

        ,onModelAddedNote: function(note) {
            if (note.hasError) {
                Acm.Dialog.info(note.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Complaint.View.Notes.$divNotes);
            }
        }
        ,onModelUpdatedNote: function(note) {
            if (note.hasError) {
                Acm.Dialog.info(note.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Complaint.View.Notes.$divNotes);
            }
        }
        ,onModelDeletedNote: function(deletedNote) {
            if (deletedNote.hasError) {
                Acm.Dialog.info(deletedNote.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Complaint.View.Notes.$divNotes);
            }
        }
        ,_makeJtData: function(notes) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Acm.isNotEmpty(notes)) {
                for (var i = 0; i < notes.length; i++) {
                    if(Complaint.Model.Notes.validateNote(notes[i])){
                        var Record = {};
                        Record.id         = Acm.goodValue(notes[i].id, 0);
                        Record.note       = Acm.goodValue(notes[i].note);
                        Record.created    = Acm.getDateFromDatetime(notes[i].created);
                        Record.creator    = Acm.__FixMe__getUserFullName(Acm.goodValue(notes[i].creator));
                        //Record.parentId   = Acm.goodValue(noteList[i].parentId);
                        //Record.parentType = Acm.goodValue(noteList[i].parentType);
                        jtData.Records.push(Record);
                    }
                }
                jtData.TotalRecordCount = notes.length;
            }
            return jtData;
        }
        ,createJTableNotes: function($jt) {
            var sortMap = {};
            sortMap["created"] = "created";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: $.t("complaint.notes.title")
                    ,paging: true
                    ,sorting: true
                    ,pageSize: 10 //Set page size (default: 10)
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,messages: {
                        addNewRecord: $.t("complaint.notes.msg.add-new-record")
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var complaintId = Complaint.View.getActiveComplaintId();
                            if (0 >= complaintId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var notes = Complaint.Model.Notes.cacheNoteList.get(complaintId);
                            if (Complaint.Model.Notes.validateNotes(notes)) {
                                return Complaint.View.Notes._makeJtData(notes);

                            } else {
                                return Complaint.Service.Notes.retrieveNotesDeferred(complaintId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(notes) {
                                        if(Complaint.Model.Notes.validateNotes(notes)){
                                            return Complaint.View.Notes._makeJtData(notes);
                                        }
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            if (Acm.isNotEmpty(record.note)) {
                                rc.Record.note = record.note;
                            }
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            if (Acm.isNotEmpty(record.note)) {
                                rc.Record.note = record.note;
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
                            title: $.t("complaint.notes.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                            ,defaultvalue : 0
                        }
                        ,note: {
                            title: $.t("complaint.notes.table.field.note")
                            ,type: 'textarea'
                            ,width: '50%'
                            ,edit: true
                        }
                        ,created: {
                            title: $.t("complaint.notes.table.field.created")
                            ,width: '15%'
                            ,edit: false
                            ,create: false
                        }
                        ,creator: {
                            title: $.t("complaint.notes.table.field.author")
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
                        var complaintId = Complaint.View.getActiveComplaintId();
                        if (0 < complaintId) {
                            var noteToSave = {};
                            //noteToSave.id = record.id;
                            //noteToSave.id = 0;
                            noteToSave.note = record.note;
                            noteToSave.created = Acm.getCurrentDayInternal();
                            noteToSave.creator = App.getUserName();
                            noteToSave.parentId = Complaint.View.getActiveComplaintId();
                            noteToSave.parentType = Complaint.Model.DOC_TYPE_COMPLAINT;
                            Complaint.Controller.viewAddedNote(noteToSave);
                        }
                    }
                    ,recordUpdated: function(event,data){
                        var whichRow = data.row.prevAll("tr").length;
                        var record = data.record;
                        var complaintId = Complaint.View.getActiveComplaintId();
                        if (0 < complaintId) {
                            var notes = Complaint.Model.Notes.cacheNoteList.get(complaintId);
                            if (Complaint.Model.Notes.validateNotes(notes)) {
                                if(Acm.isNotEmpty(notes[whichRow])){
                                    notes[whichRow].note = record.note;
                                    Complaint.Controller.viewUpdatedNote(notes[whichRow]);
                                }
                            }
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var complaintId = Complaint.View.getActiveComplaintId();
                        if (0 < complaintId) {
                            var notes = Complaint.Model.Notes.cacheNoteList.get(complaintId);
                            if (Complaint.Model.Notes.validateNotes(notes)) {
                                if(notes[whichRow]){
                                    Complaint.Controller.viewDeletedNote(notes[whichRow].id);
                                }
                            }
                        }
                    }
                } //end arg
                ,sortMap
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

        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Complaint.View.History.$divHistory);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Complaint.View.History.$divHistory);
        }

        ,_makeJtData: function(history) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Acm.isNotEmpty(history.events)) {
                var events = history.events;
                for (var i = 0; i < events.length; i++) {
                    if(Complaint.Model.History.validateEvent(events[i])){
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
                    title: $.t("complaint.history.title")
                    ,paging: true
                    ,sorting: true
                    ,pageSize: 10 //Set page size (default: 10)
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var complaintId = Complaint.View.getActiveComplaintId();
                            if (0 >= complaintId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var historyCache = Complaint.Model.History.cacheHistory.get(complaintId + "." + jtParams.jtStartIndex);
                            if (Complaint.Model.History.validateHistory(historyCache)) {
                                var history = {};
                                history.events = historyCache.resultPage;
                                history.totalEvents = historyCache.totalCount;
                                return Complaint.View.History._makeJtData(history);
                            } else {
                                return Complaint.Service.History.retrieveHistoryDeferred(complaintId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        if(Complaint.Model.History.validateHistory(data)){
                                            var history = {};
                                            history.events = data.resultPage;
                                            history.totalEvents = data.totalCount;
                                            return Complaint.View.History._makeJtData(history);
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
                            title: $.t("complaint.history.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }, eventType: {
                            title: $.t("complaint.history.table.field.event-name")
                            ,width: '50%'
                        }, eventDate: {
                            title: $.t("complaint.history.table.field.date")
                            ,width: '25%'
                        }, userId: {
                            title: $.t("complaint.history.table.field.user")
                            ,width: '25%'
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }

    ,References: {
        create: function() {
            this.$divReferences          = $("#divReferences");
            this.createJTableReferences(this.$divReferences);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_DOCUMENT         ,this.onModelAddedDocument);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Complaint.View.References.$divReferences);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Complaint.View.References.$divReferences);
        }
        ,onModelAddedDocument: function(complaintId) {
            if (complaintId.hasError) {
                Acm.Dialog.info(complaint.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Complaint.View.References.$divReferences);
            }
        }

        ,_makeJtData: function(documents){
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if(Acm.isNotEmpty(documents)){
                for (var i = 0; i < documents.length; i++) {
                    if(Complaint.Model.References.validateReferenceRecord(documents[i])){
                        var record = {};
                        record.id = Acm.goodValue(documents[i].targetId, 0);
                        record.title = Acm.goodValue(documents[i].targetName);
                        record.modified = Acm.getDateFromDatetime(documents[i].modified);
                        record.type = Acm.goodValue(documents[i].targetType);
                        record.status = Acm.goodValue(documents[i].status);
                        jtData.Records.push(record);
                    }
                }
                jtData.TotalRecordCount = jtData.Records.length;
            }
            return jtData;
        }
        ,createJTableReferences: function($jt) {
            AcmEx.Object.JTable.useBasic($jt, {
                    title: $.t("complaint.references.title")
                    ,paging: true
                    ,sorting: true
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("complaint.references.msg.add-new-record")
                    }
                    ,actions: {
                        listAction: function(postData, jtParams) {
                            var complaint = Complaint.View.getActiveComplaint();
                            if(Complaint.Model.References.validateExistingDocuments(complaint)){
                                var documents = complaint.childObjects;
                                return Complaint.View.References._makeJtData(documents);
                            }
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                    }
                    ,fields: {
                        id: {
                            title: $.t("complaint.references.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                            ,defaultvalue : 0
                        }
                        ,title: {
                            title: $.t("complaint.references.table.field.title")
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
                            title: $.t("complaint.references.table.field.modified")
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                        ,type: {
                            title: $.t("complaint.references.table.field.reference-type")
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                        ,status: {
                            title: $.t("complaint.references.table.field.status")
                            ,width: '14%'
                            ,edit: false
                            ,create: false
                        }
                    } //end field

                } //end arg
            );
        }
    }

    ,Tasks: {
        create: function() {
            this.$divTasks          = $("#divTasks");
            this.createJTableTasks(this.$divTasks);
            AcmEx.Object.JTable.clickAddRecordHandler(this.$divTasks, Complaint.View.Tasks.onClickSpanAddTask);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,URL_NEW_TASK_:    "/plugin/task/wizard?parentType=COMPLAINT&reference="


        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Complaint.View.Tasks.$divTasks);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Complaint.View.Tasks.$divTasks);
        }
        ,onClickSpanAddTask: function(event, ctrl) {
            var complaint = Complaint.View.getActiveComplaint();
            if (complaint) {
                var complaintNumber = Acm.goodValue(complaint.complaintNumber);
                var url = Complaint.View.Tasks.URL_NEW_TASK_  + complaintNumber;
                App.gotoPage(url);
            }
        }

        ,_makeJtData: function(tasks) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Complaint.Model.Tasks.validateTask(tasks)) {
                for (var i = 0; i < tasks.length; i++) {
                    var Record = {};
                    Record.id       = tasks[i].id;
                    Record.title    = tasks[i].title;
                    Record.created  = tasks[i].created;
                    Record.priority = tasks[i].priority;
                    Record.dueDate  = tasks[i].dueDate;
                    Record.status   = tasks[i].status;
                    Record.assignee = Acm.__FixMe__getUserFullName(tasks[i].assignee);
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = tasks.length;
            }
            return jtData;
        }
        ,createJTableTasks: function($jt) {
            var sortMap = {};
            sortMap["title"] = "title_parseable";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: $.t("complaint.tasks.title")
                    ,multiselect: false
                    ,selecting: false
                    ,selectingCheckboxes: false
                    ,paging: true
                    ,sorting: true //fix me
                    ,pageSize: 10 //Set page size (default: 10)
                    ,messages: {
                        addNewRecord: $.t("complaint.tasks.msg.add-new-record")
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var complaintId = Complaint.View.getActiveComplaintId();
                            if (0 >= complaintId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }

                            var tasks = Complaint.Model.Tasks.cacheTaskSolr.get(complaintId);
                            if (tasks) {
                                return Complaint.View.Tasks._makeJtData(tasks);

                            } else {
                                return Complaint.Service.Tasks.retrieveTaskListDeferred(complaintId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(tasks) {
                                        return Complaint.View.Tasks._makeJtData(tasks);
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
                            title: $.t("complaint.tasks.table.field.id")
                            ,key: true
                            ,list: true
                            ,create: false
                            ,edit: false
                            ,sorting: true //fix me
                            ,width: '5%'
                            ,display: function (commData) {
                                var a = "<a href='" + App.getContextPath() + '/plugin/task/' +
                                    + ((0 >= commData.record.id)? "#" : commData.record.id)
                                    + "'>" + commData.record.id + "</a>";
                                return $(a);
                            }
                        }
                        ,title: {
                            title: $.t("complaint.tasks.table.field.title")
                            ,width: '30%'
                            ,sorting: true //fix me
                            ,display: function (commData) {
                                var a = "<a href='" + App.getContextPath() + '/plugin/task/' +
                                    + ((0 >= commData.record.id)? "#" : commData.record.id)
                                    + "'>" + commData.record.title + "</a>";
                                return $(a);
                            }
                        }
                        ,created: {
                            title: $.t("complaint.tasks.table.field.created")
                            ,width: '10%'
                            ,sorting: true //fix me
                        }
                        ,priority: {
                            title: $.t("complaint.tasks.table.field.priority")
                            ,width: '10%'
                            ,sorting: true //fix me
                        }
                        ,dueDate: {
                            title: $.t("complaint.tasks.table.field.due")
                            ,width: '10%'
                            ,sorting: true //fix me
                        }
                        ,assignee: {
                            title: $.t("complaint.tasks.table.field.assignee")
                            ,width: '10%'
                            ,sorting: true //fix me
                        }
                        ,status: {
                            title: $.t("complaint.tasks.table.field.status")
                            ,width: '10%'
                            ,sorting: true //fix me
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }


    ,Participants: {
        create: function() {
            this.$divParticipants    = $("#divParticipants");
            this.createJTableParticipants(this.$divParticipants);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_SAVED_ASSIGNEE         ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_SAVED_GROUP	    	  ,this.onModelSavedGroup);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_PARTICIPANT      ,this.onModelModifiedParticipants);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPDATED_PARTICIPANT    ,this.onModelModifiedParticipants);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_DELETED_PARTICIPANT    ,this.onModelModifiedParticipants);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Complaint.View.Participants.$divParticipants);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Complaint.View.Participants.$divParticipants);
        }
        ,onModelSavedAssignee: function(complaintId, assginee) {
            if (!assginee.hasError) {
                AcmEx.Object.JTable.load(Complaint.View.Participants.$divParticipants);
            }
        }
        ,onModelSavedGroup: function(complaintId, group) {
            if (!group.hasError) {
                AcmEx.Object.JTable.load(Complaint.View.Participants.$divParticipants);
                Complaint.Service.Lookup.retrieveAssignees();
            }
        }
        ,onModelModifiedParticipants: function(complaint) {
            if (complaint.hasError) {
                Acm.Dialog.info(complaint.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Complaint.View.Participants.$divParticipants);
            }
        }
        ,_makeJtData: function(participants) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Complaint.Model.Participants.validateParticipants(participants)) {
                for (var i = 0; i < participants.length; i++) {
                    if(Complaint.Model.Participants.validateParticipantRecord(participants[i])){
                        var record = {};
                        record.id = Acm.goodValue(participants[i].id, 0);
                        // Here I am not taking user full name. It will be automatically shown because now 
                        // I am sending key-value object with key=username and value=fullname
                        record.title = Acm.goodValue(participants[i].participantLdapId);
                        //record.title = Acm.__FixMe__getUserFullName(Acm.goodValue(participants[i].participantLdapId));
                        record.type = Acm.goodValue(participants[i].participantType);
                        jtData.Records.push(record);
                    }
                }
                jtData.TotalRecordCount = participants.length;
            }
            return jtData;
        }

        ,createJTableParticipants: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: $.t("complaint.participants.title")
                ,paging: true
                ,sorting: true
                ,pageSize: 10 //Set page size (default: 10)
                ,messages: {
                    addNewRecord: $.t("complaint.participants.msg.add-new-record")
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var c = Complaint.View.getActiveComplaint();
                        if (Complaint.Model.Detail.validateComplaint(c)) {
                            return Complaint.View.Participants._makeJtData(c.participants);
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        var complaint = Complaint.View.getActiveComplaint();
                        if (complaint) {
                            rc.Record.title = record.title;
                            rc.Record.type = record.type;
                        }
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.JTable.getEmptyRecord();
                        var complaint = Complaint.View.getActiveComplaint();
                        if (complaint) {
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
                        title: $.t("complaint.participants.table.field.id")
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
	                ,type: {
	                    title: $.t("complaint.participants.table.field.type")
	                    ,width: '30%'
	                    ,options: Complaint.Model.Lookup.getParticipantTypes()
	                    ,display: function (data) {
	                        if (data.record.type == '*') {
	                        	// Default user. This is needed to show default user in the table.
	                    		// I am setting it here, because i don't want to show it in the popup while
	                    		// creating new participant. If we set it in the popup, it should be removed from here.
	                    		// This is used only to recognize the * type.
	                        	return '*';
	                        } else {
	                        	var options = Complaint.Model.Lookup.getParticipantTypes();
	                        	return options[data.record.type];
	                        }
	                    }
	                }
	                ,title: {
	                    title: $.t("complaint.participants.table.field.name")
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
	                    		var complaintId = Complaint.View.getActiveComplaintId();
	                    		return Acm.createKeyValueObject(Complaint.Model.Lookup.getGroups(complaintId));
	                		} else {
	                			return Acm.createKeyValueObject(Complaint.Model.Lookup.getUsers());
	                		}
	                    }
	                }
                }
                ,recordAdded : function (event, data) {
                    var record = data.record;
                    var complaintId = Complaint.View.getActiveComplaintId();

                    if (0 < complaintId) {
                        var participant = {};
                        participant.participantLdapId = record.title;
                        participant.participantType = record.type;
                        Complaint.Controller.viewAddedParticipant(complaintId, participant);
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var complaintId = Complaint.View.getActiveComplaintId();
                    var c = Complaint.View.getActiveComplaint();
                    if (Complaint.Model.Detail.validateComplaint(c)) {
                        if (0 < c.participants.length && whichRow < c.participants.length) {
                            var participant = c.participants[whichRow];
                            participant.participantLdapId = record.title;
                            participant.participantType = record.type;
                            Complaint.Controller.viewUpdatedParticipant(complaintId, participant);
                        }
                    }
                }
                ,recordDeleted : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var complaintId = Complaint.View.getActiveComplaintId();
                    var c = Complaint.View.getActiveComplaint();
                    if (Complaint.Model.Detail.validateComplaint(c)) {
                        if (0 < c.participants.length && whichRow < c.participants.length) {
                            var participant = c.participants[whichRow];
                            Complaint.Controller.viewDeletedParticipant(complaintId, participant.id);
                        }
                    }
                }
            });
        }
    }

    ,Location: {
        create: function() {
            this.$divLocation    = $("#divLocation");
            this.createJTableLocation(this.$divLocation);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_ADDED_LOCATION         ,this.onModelModifiedLocation);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_UPDATED_LOCATION       ,this.onModelModifiedLocation);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_DELETED_LOCATION       ,this.onModelModifiedLocation);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Complaint.View.Location.$divLocation);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Complaint.View.Location.$divLocation);
        }
        ,onModelModifiedLocation: function(complaint) {
            if (complaint.hasError) {
                Acm.Dialog.info(complaint.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Complaint.View.Location.$divLocation);
            }
        }
        ,_makeJtData: function(location) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Complaint.Model.Location.validateLocation(location)) {
                var record = {};
                record.id = Acm.goodValue(location.id, 0);
                record.address = Acm.goodValue(location.streetAddress);
                record.city = Acm.goodValue(location.city);
                record.type = Acm.goodValue(location.type);
                record.state = Acm.goodValue(location.state);
                record.zip = Acm.goodValue(location.zip);
                jtData.Records.push(record);
            }
            return jtData;
        }

        ,createJTableLocation: function($s) {
            $s.jtable({
                title: $.t("complaint.location.title")
                ,paging: false
                ,sorting: true
                ,pageSize: 10 //Set page size (default: 10)
                ,messages: {
                    addNewRecord: $.t("complaint.location.msg.add-new-record")
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var c = Complaint.View.getActiveComplaint();
                        if (Complaint.Model.Detail.validateComplaint(c) && Acm.isNotEmpty(c.location)){
                            return Complaint.View.Location._makeJtData(c.location);
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var record = Acm.urlToJson(postData);
                        rc.Record.address = record.address;
                        rc.Record.type = record.type;
                        rc.Record.city = record.city;
                        rc.Record.state = record.state;
                        rc.Record.zip = record.zip;
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        rc.Record.address = record.address;
                        rc.Record.type = record.type;
                        rc.Record.city = record.city;
                        rc.Record.state = record.state;
                        rc.Record.zip = record.zip;
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
                        title: $.t("complaint.location.table.field.id")
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
                    ,address: {
                        title: $.t("complaint.location.table.field.address")
                        ,width: '20%'
                    }
                    ,type: {
                        title: $.t("complaint.location.table.field.type")
                        ,width: '8%'
                        ,options: Complaint.Model.Lookup.getLocationTypes()
                    }
                    ,city: {
                        title: $.t("complaint.location.table.field.city")
                        ,width: '20%'
                    }
                    ,state: {
                        title: $.t("complaint.location.table.field.state")
                        ,width: '20%'
                    }
                    ,zip: {
                        title: $.t("complaint.location.table.field.zip")
                        ,width: '10%'
                    }
                }
                ,recordAdded : function (event, data) {
                    var record = data.record;
                    var complaint = Complaint.View.getActiveComplaint();
                    if (complaint) {
                        complaint.location = {};
                        complaint.location.streetAddress = record.address;
                        complaint.location.city = record.city;
                        complaint.location.type = record.type;
                        complaint.location.state = record.state;
                        complaint.location.zip = record.zip;
                        Complaint.Controller.viewAddedLocation(complaint);
                    }
                }
                ,recordUpdated : function (event, data) {
                    var record = data.record;
                    var complaint = Complaint.View.getActiveComplaint();
                    if (complaint && complaint.location) {
                        complaint.location.streetAddress = record.address;
                        complaint.location.city = record.city;
                        complaint.location.type = record.type;
                        complaint.location.state = record.state;
                        complaint.location.zip = record.zip;
                        Complaint.Controller.viewUpdatedLocation(complaint);
                    }
                }
                ,recordDeleted : function (event, data) {
                    var complaint = Complaint.View.getActiveComplaint();
                    if (complaint && complaint.location) {
                        complaint.location = null;
                        Complaint.Controller.viewDeletedLocation(complaint);
                    }
                }
            });
            $s.jtable('load');
        }
    }

    ,OutlookCalendar: {
        create: function() {
            this.$outlookCalendar          = $(".calendar");
            this.$weekView                 = $("#weekview");
            this.$monthView                = $("#monthview");
            this.$dayView                  = $("#dayview");

            this.createOutlookCalendarWidget(this.$outlookCalendar);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_RETRIEVED_OUTLOOK_CALENDAR_ITEMS     ,this.onModelRetrievedOutlookCalendarItem);
        }
        ,onInitialized: function() {
        }
        ,onViewSelectedObject: function(nodeType, nodeId) {
            Complaint.View.OutlookCalendar.$outlookCalendar.html("");
            Complaint.View.OutlookCalendar.createOutlookCalendarWidget(Complaint.View.OutlookCalendar.$outlookCalendar);
        }
        ,onModelRetrievedOutlookCalendarItem: function(outlookCalendarItems){
            if(outlookCalendarItems.hasError){
                App.View.MessageBoard.show("Error occurred while retrieving calendar items.", outlookCalendarItems.errorMsg);
            }
            else{
                Complaint.View.OutlookCalendar.$outlookCalendar.html("");
                Complaint.View.OutlookCalendar.createOutlookCalendarWidget(Complaint.View.OutlookCalendar.$outlookCalendar);
            }
        }

        ,createCalendarSource:function(){
            var calendarSource = [];
            var outlookCalendarItems = Complaint.Model.OutlookCalendar.cacheOutlookCalendarItems.get(Complaint.View.getActiveComplaintId());
            if(Complaint.Model.OutlookCalendar.validateOutlookCalendarItems(outlookCalendarItems)){
                for(var i = 0; i<outlookCalendarItems.items.length; i++){
                    if(Complaint.Model.OutlookCalendar.validateOutlookCalendarItem(outlookCalendarItems.items[i])) {
                        var outlookCalendarItem = {};
                        outlookCalendarItem.id = Acm.goodValue(outlookCalendarItems.items[i].id);
                        outlookCalendarItem.title = Acm.goodValue(outlookCalendarItems.items[i].subject);
                        outlookCalendarItem.start = Acm.goodValue(outlookCalendarItems.items[i].startDate);
                        outlookCalendarItem.end = Acm.goodValue(outlookCalendarItems.items[i].endDate);
                        outlookCalendarItem.detail = Complaint.View.OutlookCalendar.makeDetail(outlookCalendarItems.items[i]);
                        outlookCalendarItem.className = Acm.goodValue("b-l b-2x b-info");
                        outlookCalendarItem.allDay = Acm.goodValue(outlookCalendarItems.items[i].allDayEvent);
                        calendarSource.push(outlookCalendarItem);
                    }
                }
            }
            return calendarSource;
        }

        ,makeDetail: function(calendarItem){
            if(Complaint.Model.OutlookCalendar.validateOutlookCalendarItem(calendarItem)) {
                var body = Acm.goodValue(calendarItem.body) + "</br>";
                var startDateTime = Acm.getDateTimeFromDatetime(calendarItem.startDate);
                var startDateTimeWithoutSecond = "Start: " + startDateTime.substring(0,startDateTime.lastIndexOf(":"))+ "</br>";
                var endDateTime = Acm.getDateTimeFromDatetime(calendarItem.endDate);
                var endDateTimeWithoutSecond = "End: " + endDateTime.substring(0,endDateTime.lastIndexOf(":"))+ "</br>";
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
                droppable: true, // this allows things to be dropped onto the calendar !!!
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
                            //target: 'mouse',
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
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_RETRIEVED_TIMESHEETS     ,this.onModelRetrievedTimesheets);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Complaint.View.Time.$divTime);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Complaint.View.Time.$divTime);
        }
        ,onModelRetrievedTimesheets: function(timesheet){
            AcmEx.Object.JTable.load(Complaint.View.Time.$divTime);
        }

        ,findTotalHours: function(timeRecords){
            var totalHours = 0;
            if(Acm.isArray(timeRecords) && Acm.isNotEmpty(timeRecords)) {
                for (var i = 0; i < timeRecords.length; i++) {
                    if (Complaint.Model.Time.validateTimeRecord(timeRecords[i])) {
                        var timeRecord = timeRecords[i];
                        if (Acm.isNotEmpty(timeRecord.objectId) && Acm.compare(Acm.goodValue(timeRecord.objectId), Complaint.View.getActiveComplaintId())) {
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
                if(Complaint.Model.Time.validateTimesheet(timesheets[j])){
                    var timesheet = timesheets[j];
                    var Record = {};
                    Record.id = Acm.goodValue(timesheet.id);
                    Record.name = "Timesheet " + Acm.getDateFromDatetime(timesheet.startDate) + " - " + Acm.getDateFromDatetime(timesheet.endDate);
                    Record.type = Complaint.Model.DOC_TYPE_TIMESHEET;
                    Record.status = Acm.goodValue(timesheet.status);
                    Record.username = Acm.goodValue(timesheet.creator);
                    Record.hours = Acm.goodValue(Complaint.View.Time.findTotalHours(timesheet.times));
                    Record.modified = Acm.getDateFromDatetime(timesheet.modified);
                    jtData.Records.push(Record);
                }
            }
            return jtData;
        }
        ,createJTableTime: function($jt) {
            AcmEx.Object.JTable.useBasic($jt
                , {
                    title: $.t("complaint.time.title")
                    , sorting: true
                    , actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var timesheets = Complaint.Model.Time.cacheTimesheets.get(Complaint.View.getActiveComplaintId());
                            if (Complaint.Model.Time.validateTimesheets(timesheets)) {
                                rc = Complaint.View.Time._makeJtData(timesheets);
                            }
                            return rc;
                        }
                    }
                    , fields: {
                        id: {
                            title: $.t("complaint.time.table.field.id")
                            , key: true
                            , list: false
                            , create: false
                            , edit: false
                        }, name: {
                            title: $.t("complaint.time.table.field.form-name")
                            , width: '20%'
                            ,display: function(data) {
                                var url = App.buildObjectUrl(Acm.goodValue(data.record.type), Acm.goodValue(data.record.id), "#");
                                var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.name) + "</a>");
                                return $lnk;
                            }
                        }, username: {
                            title: $.t("complaint.time.table.field.username")
                            , width: '10%'
                        }, hours: {
                            title: $.t("complaint.time.table.field.total-hours")
                            , width: '10%'
                        }, modified: {
                            title: $.t("complaint.time.table.field.modified-date")
                            , width: '10%'
                        }, status: {
                            title: $.t("complaint.time.table.field.status")
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
            Acm.Dispatcher.addEventListener(Complaint.Controller.MODEL_RETRIEVED_COSTSHEETS     ,this.onModelRetrievedCostsheets);

        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Complaint.View.Cost.$divCost);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Complaint.View.Cost.$divCost);
        }

        ,onModelRetrievedCostsheets: function(costsheet){
            AcmEx.Object.JTable.load(Complaint.View.Cost.$divCost);
        }

        ,findTotalCost: function(costRecords){
            var totalCost = 0;
            if(Acm.isArray(costRecords) && Acm.isNotEmpty(costRecords)){
                for(var i = 0; i < costRecords.length; i++){
                    if(Complaint.Model.Cost.validateCostRecord(costRecords[i])){
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
                if(Complaint.Model.Cost.validateCostsheet(costsheets[j])){
                    var costsheet = costsheets[j];
                    var Record = {};
                    Record.id = Acm.goodValue(costsheet.id);
                    Record.name = "Costsheet " + Acm.goodValue(costsheet.parentNumber);
                    Record.type = Complaint.Model.DOC_TYPE_COSTSHEET;
                    Record.status = Acm.goodValue(costsheet.status);
                    Record.username = Acm.goodValue(costsheet.creator);
                    Record.cost = Acm.goodValue(Complaint.View.Cost.findTotalCost(costsheet.costs));
                    Record.modified = Acm.getDateFromDatetime(costsheet.modified);
                    jtData.Records.push(Record);
                }
            }
            return jtData;
        }
        ,createJTableCost: function($jt) {
            AcmEx.Object.JTable.useBasic($jt
                ,{
                    title: $.t("complaint.cost.title")
                    ,sorting: true
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var costsheets = Complaint.Model.Cost.cacheCostsheets.get(Complaint.View.getActiveComplaintId());
                            if (Complaint.Model.Cost.validateCostsheets(costsheets)) {
                                rc = Complaint.View.Cost._makeJtData(costsheets);
                            }
                            return rc;
                        }
                    }

                    ,fields: {
                        id: {
                            title: $.t("complaint.cost.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }, name: {
                            title: $.t("complaint.cost.table.field.form-name")
                            ,width: '20%'
                            ,display: function(data) {
                                var url = App.buildObjectUrl(Acm.goodValue(data.record.type), Acm.goodValue(data.record.id), "#");
                                var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.name) + "</a>");
                                return $lnk;
                            }
                        }, username: {
                            title: $.t("complaint.cost.table.field.username")
                            ,width: '10%'
                        }, cost: {
                            title: $.t("complaint.cost.table.field.total-cost")
                            ,width: '10%'
                        }, modified: {
                            title: $.t("complaint.cost.table.field.modified-date")
                            ,width: '10%'
                        }, status: {
                            title: $.t("complaint.cost.table.field.status")
                            ,width: '10%'
                        }
                    } //end field
                } //end arg
            );
        }
    }
};

