/**
 * eBrief customization
 *
 * @author jwu
 */
CaseFile.prepare = function() {

    CaseFile.View.interfaceNavObj.nodeTitle = function(objSolr) {
        return Acm.goodValue(objSolr.title_parseable);
    }

    CaseFile.View.Navigator.nodeTypeMap = [
        {nodeType: "prevPage"      ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
        ,{nodeType: "nextPage"     ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
        ,{nodeType: "p"            ,icon: ""                 ,tabIds: ["tabBlank"]}
        ,{nodeType: "p/CASE_FILE"  ,icon: "i i-folder"       ,tabIds: ["tabTitle"
            ,"tabDetail"
            ,"tabTasks"
            ,"tabParticipants"
            ,"tabPeople"
            ,"tabDocs"
            ,"tabRefs"
            ,"tabHistory"
            ,"tabOutlookCalendar"
        ]}
        ,{nodeType: "p/CASE_FILE/det"       ,icon: "" ,res: "casefile:navigation.leaf-title.details"       ,tabIds: ["tabDetail"]}
        ,{nodeType: "p/CASE_FILE/task"      ,icon: "", res: "casefile:navigation.leaf-title.tasks"         ,tabIds: ["tabTasks"]}
        ,{nodeType: "p/CASE_FILE/par"       ,icon: "", res: "ebrief:navigation.leaf-title.participants"    ,tabIds: ["tabParticipants"]}
        ,{nodeType: "p/CASE_FILE/ppl"       ,icon: "", res: "casefile:navigation.leaf-title.people"        ,tabIds: ["tabPeople"]}
        ,{nodeType: "p/CASE_FILE/doc"       ,icon: "", res: "casefile:navigation.leaf-title.documents"     ,tabIds: ["tabDocs"]}
        ,{nodeType: "p/CASE_FILE/ref"       ,icon: "" ,res: "casefile:navigation.leaf-title.references"    ,tabIds: ["tabRefs"]}
        ,{nodeType: "p/CASE_FILE/his"       ,icon: "", res: "casefile:navigation.leaf-title.history"       ,tabIds: ["tabHistory"]}
        ,{nodeType: "p/CASE_FILE/calendar"  ,icon: "", res: "casefile:navigation.leaf-title.calendar"      ,tabIds: ["tabOutlookCalendar"]}
    ];

    CaseFile.View.Ribbon = {
        create: function() {
            this.$labCaseNumber   = $("#caseNumber");
            this.$lnkAssignee     = $("#assigned");
            this.$lnkOrganisation = $("#organisation");
            this.$lnkHearingDate  = $("#hearingDate");
            this.$lnkCourt        = $("#court");

            this.$lnkCaseTitle    = $("#caseTitle");
            //this.$lnkStatus       = $("#status");


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

            AcmEx.Object.XEditable.useEditableDate(this.$lnkHearingDate, {
                success: function(response, newValue) {
                    newValue = AcmEx.Object.XEditable.xDateToDatetime(newValue);
                    CaseFile.Service.Detail.saveCaseFileItem(CaseFile.View.getActiveCaseFileId(), "nextCourtDate", newValue)
                        .fail(function(response){
                            CaseFile.View.Ribbon.setTextLnkHearingDate($.t("casefile:detail.error-value"));
                        })
                    ;
                }
            });


            var choices = [];
            var myCfg = App.Model.Config.getMyConfig();
            var courtLocations = Acm.goodValue(myCfg.courtLocations, {});
            $.each(courtLocations, function(idx, val) {
                choices.push({value: val, text: val});
            });
            AcmEx.Object.XEditable.useEditable(CaseFile.View.Ribbon.$lnkCourt, {
                source: choices
                ,success: function(response, newValue) {
                    CaseFile.Service.Detail.saveCaseFileItem(CaseFile.View.getActiveCaseFileId(), "courtroomName", newValue)
                        .fail(function(response){
                            CaseFile.View.Ribbon.setTextLnkCourt($.t("casefile:detail.error-value"));
                        })
                    ;
                }
            });

            choices = [];
            var courtLocations = Acm.goodValue(myCfg.organisations, {});
            $.each(courtLocations, function(idx, val) {
                choices.push({value: val, text: val});
            });
            AcmEx.Object.XEditable.useEditable(CaseFile.View.Ribbon.$lnkOrganisation, {
                source: choices
                ,success: function(response, newValue) {
                    CaseFile.Service.Detail.saveCaseFileItem(CaseFile.View.getActiveCaseFileId(), "responsibleOrganization", newValue)
                        .fail(function(response){
                            CaseFile.View.Ribbon.setTextLnkOrganisation($.t("casefile:detail.error-value"));
                        })
                    ;
                }
            });


            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_FOUND_ASSIGNEES          ,this.onModelFoundAssignees);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_ASSIGNEE           ,this.onModelSavedAssignee);
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

        ,onModelSavedAssignee: function(caseFileId, assginee) {
            if (assginee.hasError) {
                CaseFile.View.Ribbon.setTextLnkAssignee($.t("casefile:detail.error-value"));
            }
        }

        ,populateCaseFile: function(c) {
            if (CaseFile.Model.Detail.validateCaseFile(c)) {
                // DGM fixes... sorry about this bad code
                //var displayTitle = Acm.goodValue(c.title) + " (" + Acm.goodValue(c.status) +")";
                //var displayTitle = Acm.goodValue(c.caseNumber) + ", " + Acm.goodValue(c.title);
                //this.setTextLabCaseNumber(Acm.goodValue(displayTitle));
                //this.setTextLabCaseNumber(Acm.goodValue(c.caseNumber));
                this.setTextLnkCaseTitle(Acm.goodValue(c.title));
                this.setTextLnkCourt(Acm.goodValue(c.courtroomName));
                this.setTextLnkHearingDate(Acm.getDateFromDatetime2(c.nextCourtDate, $.t("common:date.short")));
                this.setTextLnkOrganisation(Acm.goodValue(c.responsibleOrganization));

                var assignee = CaseFile.Model.Detail.getAssignee(c);
                this.setTextLnkAssignee(Acm.goodValue(assignee));
            }
        }
        ,setTextLnkAssignee: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkAssignee, txt);
        }
        ,setTextLabCaseNumber: function(txt) {
            Acm.Object.setText(this.$labCaseNumber, txt);
        }
        ,setTextLnkCaseTitle: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkCaseTitle, txt);
        }
        ,setTextLnkOrganisation: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkOrganisation, txt);
        }
        ,setTextLnkCourt: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkCourt, txt);
        }
        ,setTextLnkHearingDate: function(txt) {
            AcmEx.Object.XEditable.setDate(this.$lnkHearingDate, txt);
        }

    };

    CaseFile.Model.Participants = {
        create: function() {
            this.cacheParticipantProfile = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
        }
        ,API_RETRIEVE_PROFILE_INFO         : "/api/latest/plugin/profile/get/"
        ,validateProfile: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.userId) || Acm.isEmpty(data.email)) {
                return false;
            }
            if (!Acm.isArray(data.groups)) {
                return false;
            }
            return true;
        }
        ,retrieveProfileInfo : function(user) {
            var url = this.API_RETRIEVE_PROFILE_INFO + user;
            return Acm.Service.call({type: "GET"
                ,url: url
                ,callback: function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Model.Participants.validateProfile(response)) {
                            var profileInfo = response;
                            var profile = {};
                            profile.organisation = Acm.goodValue(profileInfo.companyName);
                            profile.email = Acm.goodValue(profileInfo.email);
                            profile.phone = Acm.goodValue(profileInfo.phone);
                            CaseFile.Model.Participants.cacheParticipantProfile.put(user, profile);
                            return profile;
                        }
                    }
                    else if (response.hasError)  {
                        var profile = {};
                        profile.organisation = "";
                        profile.email = "";
                        profile.phone = "";
                        CaseFile.Model.Participants.cacheParticipantProfile.put(user, profile);
                        return profile;
                    }//end else
                }
            })
        }
    }

    CaseFile.View.Participants = {
        create: function() {
            this.$divParticipants    = $("#divParticipants");
            this.createJTableParticipants(this.$divParticipants);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_ASSIGNEE    ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_SAVED_GROUP	    ,this.onModelSavedGroup);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_ADDED_PARTICIPANT	,this.onModelAddedParticipant);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.MODEL_UPDATED_PARTICIPANT ,this.onModelUpdatedParticipant);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }
        ,reloadParticipants: function(){
            var caseFile = CaseFile.View.getActiveCaseFile();
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var participants = caseFile.participants;
                var requests = [];
                for(var i= 0; i< participants.length; i++){
                    var participant = participants[i];
                    if(Acm.goodValue(participant.participantType) !== "*" && Acm.goodValue(participant.participantType) !== "owning group"){
                        var user = participant.participantLdapId;
                        var profile = CaseFile.Model.Participants.cacheParticipantProfile.get(Acm.goodValue(user));
                        if(Acm.isEmpty(profile)){
                            var req = CaseFile.Model.Participants.retrieveProfileInfo(user);
                            requests.push(req);
                        }
                    }
                }
                Acm.Promise.resolvePromises(requests)
                    .done(function() {
                        AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
                    })
                    .fail(function() {
                        AcmEx.Object.JTable.load(CaseFile.View.Participants.$divParticipants);
                    });
            }
        }
        ,onModelRetrievedObject: function(objData) {
            CaseFile.View.Participants.reloadParticipants();
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
        ,onModelAddedParticipant: function(caseFileId, participant) {
        	CaseFile.View.Participants.reloadParticipants();
        }
        ,onModelUpdatedParticipant: function(caseFileId, participant) {
        	CaseFile.View.Participants.reloadParticipants();
        }
        ,onViewSelectedObject: function(objType, objId) {
            CaseFile.View.Participants.reloadParticipants();
        }

        ,createJTableParticipants: function($s) {
            AcmEx.Object.JTable.usePaging($s
                ,{
                    title: $.t("ebrief:participants.table.title")
                    ,messages: {
                        addNewRecord: $.t("ebrief:participants.msg.add-new-record")
                    }
                    ,actions: {
                        pagingListAction: function(postData, jtParams, comparator) {
                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            //var caseFileId = CaseFile.View.getActiveCaseFileId();
                            var c = CaseFile.View.getActiveCaseFile();
                            if (CaseFile.Model.Detail.validateCaseFile(c)) {
                                var pagingItems = AcmEx.Object.JTable.getPagingItems(jtParams, c.participants, comparator);
                                for (var i = 0; i < pagingItems.length; i++) {
                                    var participant = AcmEx.Object.JTable.getPagingItemData(pagingItems[i]);
                                    var record = AcmEx.Object.JTable.getPagingRecord(pagingItems[i]);

                                    if(Acm.goodValue(participant.participantType) !== "*" && Acm.goodValue(participant.participantType) !== "owning group") {
                                        record.id = Acm.goodValue(participant.id, 0);
                                        record.title = Acm.goodValue(participant.participantLdapId);
                                        record.type = Acm.goodValue(participant.participantType);

                                        var profile = CaseFile.Model.Participants.cacheParticipantProfile.get(Acm.goodValue(participant.participantLdapId));
                                        if(Acm.isNotEmpty(profile)){
                                            record.organisation = Acm.goodValue(profile.organisation);
                                            record.email = Acm.goodValue(profile.email);
                                            record.phone = Acm.goodValue(profile.phone);
                                        }
                                        else{
                                            record.organisation = "";
                                            record.email = "";
                                            record.phone = "";
                                        }

                                        rc.Records.push(record);
                                    }
                                }
                                rc.TotalRecordCount = rc.Records.length;
                            }
                            return rc;
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
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
                            title: $.t("ebrief:participants.table.field.id")
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }
                        ,type: {
                            title: $.t("ebrief:participants.table.field.type")
                            ,width: '20%'
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
                            title: $.t("ebrief:participants.table.field.name")
                            ,width: '25%'
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
                        ,organisation: {
                            title: $.t("ebrief:participants.table.field.organisation")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                        }
                        ,email: {
                            title: $.t("ebrief:participants.table.field.email")
                            ,width: '20%'
                            ,create: false
                            ,edit: false
                        }
                        ,phone: {
                            title: $.t("ebrief:participants.table.field.phone")
                            ,width: '15%'
                            ,create: false
                            ,edit: false
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
                }
                ,function(participant1, participant2, sortBy, sortDir) {
                    var value1 = "";
                    var value2 = "";

                    if ("title" == sortBy) {
                        value1 = Acm.goodValue([participant1, "participantLdapId"]);
                        value2 = Acm.goodValue([participant2, "participantLdapId"]);
                    } else if ("type" == sortBy) {
                        value1 = Acm.goodValue([participant1, "participantType"]);
                        value2 = Acm.goodValue([participant2, "participantType"]);
                    } else {
                        var profile1 = CaseFile.Model.Participants.cacheParticipantProfile.get(Acm.goodValue(participant1.participantLdapId));
                        var profile2 = CaseFile.Model.Participants.cacheParticipantProfile.get(Acm.goodValue(participant2.participantLdapId));
                        if ("organisation" == sortBy) {
                            value1 = Acm.goodValue([profile1, "organisation"]);
                            value2 = Acm.goodValue([profile2, "organisation"]);
                        } else if ("email" == sortBy) {
                            value1 = Acm.goodValue([profile1, "email"]);
                            value2 = Acm.goodValue([profile2, "email"]);
                        } else if ("phone" == sortBy) {
                            value1 = Acm.goodValue([profile1, "phone"]);
                            value2 = Acm.goodValue([profile2, "phone"]);
                        }
                    }
                    var rc = ((value1 < value2) ? -1 : ((value1 > value2) ? 1 : 0));
                    return ("DESC" == sortDir)? -rc : rc;
                }
            );
        }
    };


    CaseFile.View.Documents.createOrig = Acm.copyObjectFunction(CaseFile.View.Documents, "create", "createOrig");
    $.extend(CaseFile.View.Documents, {
        create: function() {
            CaseFile.View.Documents.createOrig();

            this.$btnNewFolder  = $("#btnNewFolder") .on("click", function(e) {CaseFile.View.Documents.onClickBtnNewFolder(e, this);});
            this.$btnLodgeDocs  = $("#btnLodgeDocs") .on("click", function(e) {CaseFile.View.Documents.onClickBtnLodgeDocs(e, this);});
            this.$btnRejectDocs = $("#btnRejectDocs").on("click", function(e) {CaseFile.View.Documents.onClickBtnRejectDocs(e, this);});
            this.$btnRefreshDocs = $("#btnRefreshDocs").on("click", function(e) {CaseFile.View.Documents.onClickBtnRefreshDocs(e, this);});

            this.$dlgLodgeDocs  = $("#dlgLodgeDocs");
            //this.$edtBmailAddr  = $("#edtBmailAddr");
            this.$divLodgeDocs  = $("#divLodgeDocs");
            this.createJTableLodgeDocs(this.$divLodgeDocs);

            this.$dlgRejectDocs    = $("#dlgRejectDocs");
            //this.$edtBmailReject   = $("#edtBmailReject");
            this.$edtRejectReason  = $("#edtRejectReason");
            this.$divRejectDocs    = $("#divRejectDocs");
            this.createJTableRejectDocs(this.$divRejectDocs);

        }

        ,onClickBtnNewFolder: function(event, ctrl) {
            var nodes = DocTree.View.getEffectiveNodes();
            if (Acm.isArrayEmpty(nodes)) {
                Acm.Dialog.alert($.t("ebrief:documents.need-parent-folder"));
            } else {
                nodes[0].setActive();
                DocTree.View.Command.trigger("newFolder");
            }

            //var topNode = DocTree.View.getTopNode();
            //DocTree.View.Op.createFolder(topNode, "hahahaha");
        }
        ,onClickBtnLodgeDocs: function(event, ctrl) {
            var folderMap = {};
            var nodes = DocTree.View.getEffectiveNodes();
            if (DocTree.View.validateNodes(nodes)) {
                for (var i = 0; i < nodes.length; i++) {
                    //var folderNames = CaseFile.View.Documents.getFolderNames(nodes[i], CaseFile.View.Documents.FOLDER_COURT_BRIEF);
                    var folderNames = DocTree.View.getNodePathNames(nodes[i]);
                    folderNames.pop();
                    if (CaseFile.View.Documents.FOLDER_PROSECUTION_BRIEF != folderNames[1]) {
                        Acm.Dialog.alert($.t("ebrief:documents.lodge-wrong-folder"));
                        return;
                    }
                    folderNames[1] = CaseFile.View.Documents.FOLDER_COURT_BRIEF;
                    var entry = folderMap[folderNames];
                    if (!entry) {
                        entry = {parentNode: nodes[i].parent, docIds: []};
                    }
                    entry.docIds.push(nodes[i].data.objectId);
                    folderMap[folderNames] = entry;

                    //nodes[i].remove();
                }
            }


//            CaseFile.View.Documents.setValueEdtBmailAddr("");
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divLodgeDocs);
            Acm.Dialog.modal(CaseFile.View.Documents.$dlgLodgeDocs, function() {
//                var emailAddresses = CaseFile.View.Documents.getValueEdtBmailAddr();
//                if (Acm.isEmpty(emailAddresses)) {
//                    Acm.Dialog.alert("Email Address is required");
//                    return;
//                }

                var emailAddresses = CaseFile.View.Documents.getCurrentUserEmailAddress();
                if (Acm.isNotEmpty(emailAddresses)) {
                    var emailNotifications = DocTree.View.Email.makeEmailData(emailAddresses, nodes, "Lodged Document(s)");
                    if(Acm.isNotEmpty(emailNotifications)){
                        DocTree.Controller.viewSentEmail(emailNotifications);
                    }
                }

//                    DocTree.Controller.viewSentEmail(emailNotifications);
//                    var emailNotifications = DocTree.View.Email.makeEmailData(emailAddresses, nodes);


                $.each( folderMap, function( key, value ) {
                    var folderNames = key.split(",");
                    var entry = value;
                    DocTree.View.Op.lodgeDocuments(folderNames, entry.docIds, entry.parentNode);
                    var z = 1;
                });

            });
        }
        ,onClickBtnRejectDocs: function(event, ctrl) {
            var nodes = DocTree.View.getEffectiveNodes();
            if (DocTree.View.validateNodes(nodes)) {
                for (var i = 0; i < nodes.length; i++) {
                    //var folderNames = CaseFile.View.Documents.getFolderNames(nodes[i], CaseFile.View.Documents.FOLDER_COURT_BRIEF);
                    var folderNames = DocTree.View.getNodePathNames(nodes[i]);
                    folderNames.pop();
                    if (CaseFile.View.Documents.FOLDER_COURT_BRIEF != folderNames[1]) {
                        Acm.Dialog.alert($.t("ebrief:documents.reject-wrong-folder"));
                        return;
                    }
                }
            }

//            CaseFile.View.Documents.setValueEdtBmailReject("");
            CaseFile.View.Documents.setValueEdtRejectReason("");
            AcmEx.Object.JTable.load(CaseFile.View.Documents.$divRejectDocs);
            Acm.Dialog.modal(CaseFile.View.Documents.$dlgRejectDocs, function() {
//                var emailAddresses = CaseFile.View.Documents.getValueEdtBmailReject();
//                if (Acm.isEmpty(emailAddresses)) {
//                    Acm.Dialog.alert("Email Address is required");
//                    return;
//                }
                var reason = CaseFile.View.Documents.getValueEdtRejectReason();

                var emailAddresses = CaseFile.View.Documents.getCurrentUserEmailAddress();
                if (Acm.isNotEmpty(emailAddresses)) {
                    var emailNotifications = DocTree.View.Email.makeEmailData(emailAddresses, nodes, $.t("ebrief:documents.reject-doc-email-subject"));
                    if(Acm.isNotEmpty(emailNotifications)){
                        if(Acm.isNotEmpty(reason)){
                            if(Acm.isNotEmpty(emailNotifications[0].note)){
                                emailNotifications[0].note += $.t("ebrief:documents.reject-doc-email-text") + "\n\n";
                                emailNotifications[0].note += Acm.goodValue(reason);
                            }
                        }
                        DocTree.Controller.viewSentEmail(emailNotifications);
                    }
                }

//                    DocTree.Controller.viewSentEmail(emailNotifications);
//                    var emailNotifications = DocTree.View.Email.makeEmailData(emailAddresses, nodes, reason);

                DocTree.View.Command.trigger("remove");
            });
        }
        ,getCurrentUserEmailAddress: function(){
            var emailAddresses = [];
            var profileInfo = Sidebar.Model.Profile.getProfileInfo();
            if(Acm.isNotEmpty(profileInfo) && Acm.isNotEmpty(profileInfo.email)){
                emailAddresses.push(Acm.goodValue(profileInfo.email));
            }
            return emailAddresses;
        }
        ,onClickBtnRefreshDocs: function(event,ctrl){
            DocTree.View.refreshTree();
        }
        ,onViewSelectedTreeNode: function(key) {
            DocTree.View.expandTopNode();
        }

        ,FOLDER_COURT_BRIEF: "Court Brief"
        ,FOLDER_PROSECUTION_BRIEF: "Prosecution Brief"
        ,getFolderNames: function(node, firstLevelFolder) {
            var pathNames = DocTree.View.getNodePathNames(node);
            pathNames.pop();
            pathNames[1] = firstLevelFolder;
            return pathNames;
        }

        ,_lodgeFolderId: 0
        ,getLodgeFolderId: function() {
            if (0 >= this._lodgeFolderId) {
                var topNode = DocTree.View.getTopNode();
                if (topNode) {
                    for (var i = 0; i < topNode.children.length; i++) {
                        var child = topNode.children[i];
                        if (CaseFile.View.Documents.FOLDER_COURT_BRIEF == Acm.goodValue(child.data.name)) {
                            this._lodgeFolderId = Acm.goodValue(child.data.objectId, 0);
                            break;
                        }
                    }
                }
            }
            return this._lodgeFolderId;
        }

        ,createJTableLodgeDocs: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: $.t("ebrief:documents.dialog.lodge-table-title")
                ,paging: true
                ,sorting: true
                ,pageSize: 16
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var nodes = DocTree.View.getEffectiveNodes();
                        if (DocTree.View.validateNodes(nodes)) {
                            for (var i = 0; i < nodes.length; i++) {
                                var record = {};
                                record.id = Acm.goodValue(nodes[i].data.objectId, 0);
                                var pathNames = DocTree.View.getNodePathNames(nodes[i]);
                                pathNames.shift(); //remove top node
                                var path = "/" + pathNames.join("/");
                                record.name = path; //Acm.goodValue(nodes[i].data.name);
                                rc.Records.push(record);
                            }
                            rc.TotalRecordCount = rc.Records.length;
                        }
                        return rc;
                    }
                }
                ,fields: {
                    id: {
                        title: "ID"  //not shown, no need with label resource
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
                    ,name: {
                        title: $.t("ebrief:documents.dialog.lodge-name-column")
                        ,edit: false
                        ,create: false
                    }
                }
            });
        }

        ,createJTableRejectDocs: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: $.t("ebrief:documents.dialog.reject-table-title")
                ,paging: true
                ,sorting: true
                ,pageSize: 16
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        var nodes = DocTree.View.getEffectiveNodes();
                        if (DocTree.View.validateNodes(nodes)) {
                            for (var i = 0; i < nodes.length; i++) {
                                var record = {};
                                record.id = Acm.goodValue(nodes[i].data.objectId, 0);
                                var pathNames = DocTree.View.getNodePathNames(nodes[i]);
                                pathNames.shift(); //remove top node
                                var path = "/" + pathNames.join("/");
                                record.name = path; //Acm.goodValue(nodes[i].data.name);
                                rc.Records.push(record);
                            }
                            rc.TotalRecordCount = rc.Records.length;
                        }
                        return rc;
                    }
                }
                ,fields: {
                    id: {
                        title: "ID"  //not shown, no need with label resource
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
                    ,name: {
                        title: $.t("ebrief:documents.dialog.reject-name-column")
                        ,edit: false
                        ,create: false
                    }
                }
            });
        }

//        ,getValueEdtBmailAddr: function() {
//            return Acm.Object.getValue(this.$edtBmailAddr);
//        }
//        ,setValueEdtBmailAddr: function(txt) {
//            Acm.Object.setValue(this.$edtBmailAddr, txt);
//        }
//        ,getValueEdtBmailReject: function() {
//            return Acm.Object.getValue(this.$edtBmailReject);
//        }
//        ,setValueEdtBmailReject: function(txt) {
//            Acm.Object.setValue(this.$edtBmailReject, txt);
//        }
        ,getValueEdtRejectReason: function() {
            return Acm.Object.getValue(this.$edtRejectReason);
        }
        ,setValueEdtRejectReason: function(txt) {
            Acm.Object.setValue(this.$edtRejectReason, txt);
        }
    });


    //CaseFile.View.DetailNote = {};
    CaseFile.View.Notes = {};
    //CaseFile.View.References = {};
    CaseFile.View.Correspondence = {};
    CaseFile.View.Time = {};
    CaseFile.View.Cost = {};
    //Calendar = {};

    CaseFile.View.Calendar.displayError = function() {
        Calendar.View.OutlookCalendar.$calendarTabTitle.text($.t("ebrief:outlook-calendar.msg.error-occurred"));
    }

    DocTree.View.Menu.getBatchMenu = function(nodes) {
        var menu = [{title: $.t("doctree:menu.title-no-op"), cmd: "noop", uiIcon: "" }];
        if (DocTree.View.validateNodes(nodes)) {
            var countFolder = 0;
            var countFile = 0;
            var countProsecutionBrief = 0;
            var countCourtBrief = 0;
            for (var i = 0; i < nodes.length; i++) {
                var pathNames = DocTree.View.getNodePathNames(nodes[i]);
                if (!Acm.isArrayEmpty(pathNames)) {
                    if (1 < pathNames.length) {
                        if (CaseFile.View.Documents.FOLDER_PROSECUTION_BRIEF == pathNames[1]) {
                            countProsecutionBrief++;
                        } else if (CaseFile.View.Documents.FOLDER_COURT_BRIEF == pathNames[1]) {
                            countCourtBrief++;
                        }
                    }
                }

                if (DocTree.View.isFolderNode(nodes[i])) {
                    countFolder++;
                } else if (DocTree.View.isFileNode(nodes[i])) {
                    countFile++;
                }
            }

            if (0 < countProsecutionBrief && 0 < countCourtBrief) {        //cannot mix under both folders
                ;
            } else if (0 < countFile && 0 >= countFolder) {              //file only menu
                menu = [{title: $.t("doctree:menu.title-email"),  cmd: "email", uiIcon: "ui-icon-mail-closed" }
                    ,{title: $.t("doctree:menu.title-print"),     cmd: "print", uiIcon: "ui-icon-print" }
                    ,{title: $.t("doctree:menu.title-separator") }
                    ,{title: $.t("doctree:menu.title-cut"),       cmd: "cut", uiIcon: "ui-icon-scissors" }
                    ,{title: $.t("doctree:menu.title-copy"),      cmd: "copy", uiIcon: "ui-icon-copy" }
                    ,{title: $.t("doctree:menu.title-delete"),    cmd: "remove", uiIcon: "ui-icon-trash" }
                ];
            } else if (0 >= countFile || 0 < countFolder) {       //folder only menu
                menu = [{title: $.t("doctree:menu.title-cut"),    cmd: "cut", uiIcon: "ui-icon-scissors" }
                    ,{title: $.t("doctree:menu.title-copy"),      cmd: "copy", uiIcon: "ui-icon-copy" }
                    ,{title: $.t("doctree:menu.title-delete"),    cmd: "remove", uiIcon: "ui-icon-trash" }
                ];
            } else if (0 < countFile || 0 < countFolder) {        //mix file and folder menu
                menu = [{title: $.t("doctree:menu.title-cut"),    cmd: "cut", uiIcon: "ui-icon-scissors" }
                    ,{title: $.t("doctree:menu.title-copy"),      cmd: "copy", uiIcon: "ui-icon-copy" }
                    ,{title: $.t("doctree:menu.title-delete"),    cmd: "remove", uiIcon: "ui-icon-trash" }
                ];
            }
        }
        return menu;
    };
    DocTree.View.Menu.getContextMenu = function(node) {
        var menu = [{title: $.t("doctree:menu.title-no-op"), cmd: "noop", uiIcon: "" }];
        if (node) {
            var isProsecutionBrief = false;
            var isCourtBrief = false;
            var pathNames = DocTree.View.getNodePathNames(node);
            if (!Acm.isArrayEmpty(pathNames)) {
                if (1 < pathNames.length) {
                    if (CaseFile.View.Documents.FOLDER_PROSECUTION_BRIEF == pathNames[1]) {
                        isProsecutionBrief = true;
                    } else if (CaseFile.View.Documents.FOLDER_COURT_BRIEF == pathNames[1]) {
                        isCourtBrief = true;
                    }
                }
            }

            if (DocTree.View.isTopNode(node)) {
                menu = [{title: $.t("doctree:menu.title-new-folder"), cmd: "newFolder", uiIcon: "ui-icon-plus" }
                    //,{title: $.t("doctree:menu.title-new-file"),      children: DocTree.View.Menu.docSubMenu}
                    //,{title: $.t("doctree:menu.title-separator") }
                    //,{title: $.t("doctree:menu.title-paste"),         cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                ];
            } else if (DocTree.View.isFolderNode(node)) {
                if (isProsecutionBrief) {
                    menu = [{title: $.t("doctree:menu.title-new-folder"), cmd: "newFolder", uiIcon: "ui-icon-plus" }
                        ,{title: $.t("doctree:menu.title-new-file"),      children: DocTree.View.Menu.docSubMenu}
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-cut"),           cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: $.t("doctree:menu.title-copy"),          cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: $.t("doctree:menu.title-paste"),         cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-rename"),        cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: $.t("doctree:menu.title-delete"),        cmd: "remove", uiIcon: "ui-icon-trash" }
                    ];

                } else if (isCourtBrief) {
                    menu = [{title: $.t("doctree:menu.title-new-folder"), cmd: "newFolder", uiIcon: "ui-icon-plus" }
                        //,{title: $.t("doctree:menu.title-new-file"),      children: DocTree.View.Menu.docSubMenu}
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-cut"),           cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: $.t("doctree:menu.title-copy"),          cmd: "copy", uiIcon: "ui-icon-copy" }
                        //,{title: $.t("doctree:menu.title-paste"),         cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-rename"),        cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: $.t("doctree:menu.title-delete"),        cmd: "remove", uiIcon: "ui-icon-trash" }
                    ];
                }
            } else if (DocTree.View.isFileNode(node)) {
                if (isProsecutionBrief) {
                    menu = [{title: $.t("doctree:menu.title-open"),       cmd: "open", uiIcon: "ui-icon-folder-open" }
                        ,{title: $.t("doctree:menu.title-edit"),          cmd: "edit", uiIcon: "ui-icon-pencil" }
                        ,{title: $.t("doctree:menu.title-email"),         cmd: "email", uiIcon: "ui-icon-mail-closed" }
                        ,{title: $.t("doctree:menu.title-print"),         cmd: "print", uiIcon: "ui-icon-print" }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-cut"),           cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: $.t("doctree:menu.title-copy"),          cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: $.t("doctree:menu.title-paste"),         cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-rename"),        cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: $.t("doctree:menu.title-delete"),        cmd: "remove", uiIcon: "ui-icon-trash" }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-download"),      cmd: "download", uiIcon: "ui-icon-arrowthickstop-1-s" }
                        ,{title: $.t("doctree:menu.title-replace"),       cmd: "replace", uiIcon: "" }
                    ];

                } else if (isCourtBrief) {
                    menu = [{title: $.t("doctree:menu.title-open"),       cmd: "open", uiIcon: "ui-icon-folder-open" }
                        ,{title: $.t("doctree:menu.title-edit"),          cmd: "edit", uiIcon: "ui-icon-pencil" }
                        ,{title: $.t("doctree:menu.title-email"),         cmd: "email", uiIcon: "ui-icon-mail-closed" }
                        ,{title: $.t("doctree:menu.title-print"),         cmd: "print", uiIcon: "ui-icon-print" }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-cut"),           cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: $.t("doctree:menu.title-copy"),          cmd: "copy", uiIcon: "ui-icon-copy" }
                        //,{title: $.t("doctree:menu.title-paste"),         cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-rename"),        cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: $.t("doctree:menu.title-delete"),        cmd: "remove", uiIcon: "ui-icon-trash" }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-download"),      cmd: "download", uiIcon: "ui-icon-arrowthickstop-1-s" }
                        //,{title: $.t("doctree:menu.title-replace"),       cmd: "replace", uiIcon: "" }
                    ];
                }
            }
        }
        return menu;
    };


    CaseFile.Model.Config.requestOrig = Acm.copyObjectFunction(CaseFile.Model.Config, "request", "requestOrig");
    CaseFile.Model.Config.request = function() {
        CaseFile.Model.Config.requestOrig();

        App.Model.Config.requestConfig(ThisApp.CONFIG_NAME_ACM_FORMS).done(function(data) {
            var cfg = App.Model.Config.getConfig(ThisApp.CONFIG_NAME_ACM_FORMS);
            if (Acm.isNotEmpty(cfg)) {
                var myCfg = App.Model.Config.getMyConfig();
                myCfg.courtLocations  = {};
                Acm.goodValue(cfg["ebrief.court.locations"], "").split(",").forEach(function(x){
                    var arr = x.split("=");
                    arr[1] && (myCfg.courtLocations[arr[0]] = arr[1]);
                });
                myCfg.organisations  = {};
                Acm.goodValue(cfg["ebrief.organizations"], "").split(",").forEach(function(x){
                    var arr = x.split("=");
                    arr[1] && (myCfg.organisations[arr[0]] = arr[1]);
                });



            }
        });
        App.Model.Config.requestConfig(CaseFile.Model.Config.CONFIG_NAME_CASE_FILE).done(function(data) {
            var cfg = App.Model.Config.getConfig(CaseFile.Model.Config.CONFIG_NAME_CASE_FILE);
            if (Acm.isNotEmpty(cfg)) {
                var myCfg = App.Model.Config.getMyConfig();
//                myCfg.caseTypes  = Acm.goodValue(cfg["casefile.case-types"], "").split(",");
//                myCfg.treeFilter = Acm.parseJson(cfg["search.tree.filter"], "[]");
                myCfg.folderStructure   = Acm.parseJson(cfg["casefile.folder.structure"], "[]");
                var z = 1;
            }
        });
    };

};


