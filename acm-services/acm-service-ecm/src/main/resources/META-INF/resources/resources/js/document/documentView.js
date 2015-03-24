/**
 * AcmDocument.View
 *
 * @author jwu
 */
AcmDocument.View = AcmDocument.View || {
    create : function() {
        if (AcmDocument.View.MicroData.create)              {AcmDocument.View.MicroData.create();}
        if (AcmDocument.View.Content.create)                {AcmDocument.View.Content.create();}
        if (AcmDocument.View.Action.create)                 {AcmDocument.View.Action.create();}
        if (AcmDocument.View.Detail.create)                 {AcmDocument.View.Detail.create();}
        if (AcmDocument.View.DocViewer.create)              {AcmDocument.View.DocViewer.create();}
        if (AcmDocument.View.Notes.create)                  {AcmDocument.View.Notes.create();}
        if (AcmDocument.View.Participants.create)           {AcmDocument.View.Participants.create();}
        if (AcmDocument.View.EventHistory.create)           {AcmDocument.View.EventHistory.create();}
        if (AcmDocument.View.VersionHistory.create)         {AcmDocument.View.VersionHistory.create();}
        if (AcmDocument.View.Tags.create)                   {AcmDocument.View.Tags.create();}
    }
    ,onInitialized: function() {
        if (AcmDocument.View.MicroData.onInitialized)               {AcmDocument.View.MicroData.onInitialized();}
        if (AcmDocument.View.Content.onInitialized)                 {AcmDocument.View.Content.onInitialized();}
        if (AcmDocument.View.Action.onInitialized)                  {AcmDocument.View.Action.onInitialized();}
        if (AcmDocument.View.Detail.onInitialized)                  {AcmDocument.View.Detail.onInitialized();}
        if (AcmDocument.View.DocViewer.onInitialized)               {AcmDocument.View.DocViewer.onInitialized();}
        if (AcmDocument.View.Notes.onInitialized)                   {AcmDocument.View.Notes.onInitialized();}
        if (AcmDocument.View.Participants.onInitialized)            {AcmDocument.View.Participants.onInitialized();}
        if (AcmDocument.View.EventHistory.onInitialized)            {AcmDocument.View.EventHistory.onInitialized();}
        if (AcmDocument.View.VersionHistory.onInitialized)          {AcmDocument.View.VersionHistory.onInitialized();}
        if (AcmDocument.View.Tags.onInitialized)                    {AcmDocument.View.Tags.onInitialized();}
    }

    ,getActiveDocumentId: function() {
        return ObjNav.View.Navigator.getActiveObjId();
    }
    ,getActiveDocument: function() {
        var objId = ObjNav.View.Navigator.getActiveObjId();
        var document = null;
        if (Acm.isNotEmpty(objId)) {
            document = ObjNav.Model.Detail.getCacheObject(AcmDocument.Model.DOC_TYPE_DOCUMENT, objId);
        }
        return document;
    }

    ,MicroData: {
        create : function() {
        }
        ,onInitialized: function() {
        }
    }


    ,Content: {
        create : function() {
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT_ERROR    ,this.onModelRetrievedObjectError);
        }
        ,onInitialized: function() {
        }
        ,onModelRetrievedObjectError: function(error) {
            Acm.Dialog.error(Acm.goodValue(error.errorMsg, "Error occurred while retrieving data"));
        }
    }

    ,Action: {
        create: function() {
            this.$btnReplaceFile    	    = $("#btnReplaceFile");
            this.$btnReplaceFile.on("click", function(e) {AcmDocument.View.Action.onClickBtnReplaceFile(e, this);});

            this.$btnCopyFile    	        = $("#btnCopyFile");
            this.$btnCopyFile.on("click", function(e) {AcmDocument.View.Action.onClickBtnCopyFile(e, this);});

            this.$btnMoveFile    	        = $("#btnMoveFile");
            this.$btnMoveFile.on("click", function(e) {AcmDocument.View.Action.onClickBtnMoveFile(e, this);});

            this.$btnDeleteFile    	        = $("#btnDeleteFile");
            this.$btnDeleteFile.on("click", function(e) {AcmDocument.View.Action.onClickBtnDeleteFile(e, this);});

            this.$modalReplaceFile          = $("#modalReplaceFile");
            this.$modalCopyFile             = $("#modalCopyFile");
            this.$modalMoveFile             = $("#modalMoveFile");
            this.$modalDeleteFile           = $("#modalDeleteFile");
        }
        ,onInitialized: function() {
        }
        ,onClickBtnReplaceFile: function(event,ctrl) {
            AcmDocument.View.Action.$modalReplaceFile.modal("show");
        }
        ,onClickBtnCopyFile: function(event,ctrl) {
            AcmDocument.View.Action.$modalCopyFile.modal("show");
        }
        ,onClickBtnMoveFile: function(event,ctrl) {
            AcmDocument.View.Action.$modalMoveFile.modal("show");
            //Acm.Dialog.modal(AcmDocument.View.Action.$modalMoveFile, onClickBtnPrimary);
        }
        ,onClickBtnDeleteFile: function(event,ctrl) {
            AcmDocument.View.Action.$modalDeleteFile.modal("show");
        }
    }

    ,Detail: {
        create: function() {
            this.$lnkDocTitle       = $("#docTitle");
            this.$lnkOwner          = $("#owner");
            this.$lnkCreateDate     = $("#createDate");
            this.$lnkAssignee       = $("#assignee");
            this.$lnkSubjectType    = $("#type");
            this.$lnkStatus         = $("#status");

            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT_ERROR     ,this.onModelRetrievedObjectError);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }
        ,onModelRetrievedObject: function(objData) {
            AcmDocument.View.Detail.populateDocument(objData);
        }
        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            AcmDocument.View.Detail.populateDocument(objData);
        }
        ,populateDocument: function(document) {
            if (AcmDocument.Model.Detail.validateDocument(document)) {
                this.setTextLnkDocTitle(Acm.goodValue(document.title));
            }
        }
        ,populateParentDetails: function(parentObject) {
            if (AcmDocument.Model.Detail.validateParentObject(parentObject)) {
                this.setTextLnkCreateDate(Acm.getDateFromDatetime(parentObject.created));
                this.setTextLnkSubjectType(Acm.goodValue(parentObject.type));
                this.setTextLnkOwner(Acm.goodValue(parentObject.owner));
                this.setTextLnkStatus(Acm.goodValue(parentObject.status));
                this.setTextLnkAssignee(Acm.__FixMe__getUserFullName(parentObject.assignee));
            }
        }
        ,setTextLnkDocTitle: function(txt) {
            Acm.Object.setText(this.$lnkDocTitle, txt);
        }
        ,setTextLnkCreateDate: function(txt) {
            Acm.Object.setText(this.$lnkCreateDate, txt);
        }
        ,setTextLnkAssignee: function(txt) {
            Acm.Object.setText(this.$lnkAssignee, txt);
        }
        ,setTextLnkSubjectType: function(txt) {
            Acm.Object.setText(this.$lnkSubjectType, txt);
        }
        ,setTextLnkOwner: function(txt) {
            Acm.Object.setText(this.$lnkOwner, txt);
        }
        ,setTextLnkStatus: function(txt) {
            Acm.Object.setText(this.$lnkStatus, txt);
        }
    }

    ,DocViewer: {
        create: function() {
            this.$divDocViewer    = $("#divDocViewer");
            this.createJTableDocViewer(this.$divDocViewer);

            /*Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
             Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);*/
        }
        ,onInitialized: function() {
        }
        /*,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(AcmDocument.View.DocViewer.$divDocViewer);
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(AcmDocument.View.DocViewer.$divDocViewer);
        }*/
        ,createJTableDocViewer: function($s) {
            AcmEx.Object.JTable.useBasic($s, {
                title: 'Document Viewer'
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.JTable.getEmptyRecords();
                        return rc;
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
                }
            });
        }
    }

    ,Participants: {
        create: function() {
            this.$tabParticipants = $("#tabParticipants");

            //dummy
            var participants = [{"fullname" : "ArkCase" , "id" : "1", "role" : "Author"},
                                {"fullname" : "ArkCase1" , "id" : "2", "role" : "Author1"},
                                {"fullname" : "ArkCase2" , "id" : "3", "role" : "Author2"}];

            this.buildParticipantsTable(participants);

            this.$btnRemoveParticipant = $(".removeParticipant");
            this.$btnRemoveParticipant.on("click", function(e) {AcmDocument.View.Participants.onClickBtnRemoveParticipant(e, this);});

            this.$btnChangeRole = $(".changeParticipantRole");
            this.$btnChangeRole.on("click", function(e) {AcmDocument.View.Participants.onClickBtnChangeRole(e, this);});

            this.$participant =  $(".participantFullName");
            /*Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);*/
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(AcmDocument.View.Participants.buildParticipantsTable());
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(AcmDocument.View.Participants.buildParticipantsTable());
        }

        ,setHtmlTabParticipants: function(val) {
            AcmDocument.View.Participants.$tabParticipants.append(val);
        }
        ,onClickBtnRemoveParticipant: function(event,ctrl) {
            event.preventDefault();
            //find participant name and id
            var participantId = $(event.target).closest('div').next().attr('id');
            var participantName = $(event.target).closest('div').next().children('div').prop('textContent');
            var participantRole = $(event.target).closest('div').next().children('small').prop('textContent');

            alert("participantId: " + participantId + " participantName: " + participantName + " participantRole: " + participantRole );
        }
        ,onClickBtnChangeRole: function(event,ctrl) {
            var participantId = $(event.target).closest('div').next().attr('id');
            var participantName = $(event.target).closest('div').next().children('div').prop('textContent');
            var participantRole = $(event.target).closest('div').next().children('small').prop('textContent');

            alert("participantRole: " + participantRole );
        }

        ,buildParticipantsTable: function(participants) {
            var html = "";
            for (var i = 0; i < participants.length; i++) {

                html += "<li class='list-group-item'>"
                        + "<div class='media'>"
                            + "<span class='pull-left thumb-sm'><img src='resources/images/a1.png' class='img-circle'></span>"
                            + "<div class='btn-group pull-right'>"
                                + "<button type='button' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i> </button>"
                                + "<ul class='dropdown-menu'>"
                                    + "<li><a href='#' class='removeParticipant'>Remove</a></li>"
                                    + "<li><a href='#' class='changeParticipantRole'>Change Role</a></li>"
                                + "</ul>"
                            + "</div>"
                            + "<div class='media-body' id='" + Acm.goodValue(participants[i].id) + "'>"
                            + "<div><a href='#'>" + Acm.goodValue(participants[i].fullname) + "</a></div>"
                            + "<small class='text-muted'>" + Acm.goodValue(participants[i].role) + "</small> </div>"
                        + "</div>"
                        +"</li>";

                +"<td><button type='button' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i></button>"
                +"<ul class='dropdown-menu'>"
                +"<li><a href='#' class='makeActiveVersion'>Make Active</a></li>"
                +"</ul></td>"
            }
            this.setHtmlTabParticipants(html);
        }
    }

    ,Notes: {
        create: function() {
            this.$divNotes          = $("#divNotes");
            this.createJTableNotes(this.$divNotes);

            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_ADDED_NOTE        ,this.onModelAddedNote);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_UPDATED_NOTE      ,this.onModelUpdatedNote);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_DELETED_NOTE      ,this.onModelDeletedNote);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(AcmDocument.View.Notes.$divNotes);
        }
        ,onModelAddedNote: function(note) {
            if (note.hasError) {
                Acm.Dialog.info(note.errorMsg);
            } else {
                AcmEx.Object.JTable.load(AcmDocument.View.Notes.$divNotes);
            }
        }
        ,onModelUpdatedNote: function(note) {
            if (note.hasError) {
                Acm.Dialog.info(note.errorMsg);
            } else {
                AcmEx.Object.JTable.load(AcmDocument.View.Notes.$divNotes);
            }
        }
        ,onModelDeletedNote: function(noteId) {
            if (noteId.hasError) {
                Acm.Dialog.info(noteId.errorMsg);
            } else {
                AcmEx.Object.JTable.load(AcmDocument.View.Notes.$divNotes);
            }
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(AcmDocument.View.Notes.$divNotes);
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
                    ,sorting: true
                    ,pageSize: 10 //Set page size (default: 10)
                    ,selecting: true
                    ,messages: {
                        addNewRecord: 'Add Note'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var jtData = AcmEx.Object.JTable.getEmptyRecords();
                            return jtData;
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            var documentId = AcmDocument.View.getActiveDocumentId();
                            var document = AcmDocument.View.getActiveDocument();
                            if (document) {
                                rc.Record.parentId = Acm.goodValue(documentId, 0);
                                rc.Record.note = record.note;
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            var documentId = AcmDocument.View.getActiveDocumentId();
                            var document = AcmDocument.View.getActiveDocument();
                            if (document) {
                                rc.Record.parentId = Acm.goodValue(caseFileId, 0);
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
                        var documentId = AcmDocument.View.getActiveDocumentId();
                        if (0 < documentId) {
                            var noteToSave = {};
                            //noteToSave.id = record.id;
                            noteToSave.id = 0;
                            noteToSave.note = record.note;
                            noteToSave.created = Acm.getCurrentDayInternal();
                            noteToSave.creator = record.creator;
                            noteToSave.parentId = documentId;
                            //noteToSave.parentType = AcmDocument.Model.DOC_TYPE_DOCUMENT;
                            //AcmDocument.Controller.viewAddedNote(noteToSave);
                        }
                    }
                    ,recordUpdated: function(event,data){
                        var whichRow = data.row.prevAll("tr").length;
                        var record = data.record;
                        var documentId = AcmDocument.View.getActiveDocumentId();
                        if (0 < documentId) {
                            var notes = AcmDocument.Model.Notes.cacheNoteList.get(documentId);
                            if (notes) {
                                if(notes[whichRow]){
                                    notes[whichRow].note = record.note;
                                    //AcmDocument.Controller.viewUpdatedNote(notes[whichRow]);
                                }
                            }
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var documentId = AcmDocument.View.getActiveDocumentId();
                        if (0 < documentId) {
                            var notes = AcmDocument.Model.Notes.cacheNoteList.get(documentId);
                            if (notes) {
                                if(notes[whichRow]){
                                    //AcmDocument.Controller.viewDeletedNote(notes[whichRow].id);
                                }
                            }
                        }
                    }
                } //end arg
                ,sortMap
            );
        }
    }

    ,Tags: {
        create: function() {
            this.$modalNewTag      = $("#modalNewTag")
            this.$tabTags = $("#tabTags")

            //dummy
            var tags = [{"name" : "Case" , "id" : "123", "user" : "ann-acm"},
                        {"name" : "Complaint" , "id" : "1234", "user" : "ann-acm"},
                        {"name" : "Other" , "id" : "1235", "user" : "ann-acm"}];

            this.buildTagsTable(tags);

            this.$btnRemoveTag = $(".removeTag");
            this.$btnRemoveTag.on("click", function(e) {AcmDocument.View.Tags.onClickBtnRemoveTag(e, this);});

            this.$btnNewTag    	    = $("#btnNewTag");
            this.$btnNewTag.on("click", function(e) {AcmDocument.View.Tags.onClickBtnNewTag(e, this);});

            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(AcmDocument.View.Tags.buildTagsTable());
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(AcmDocument.View.Tags.buildTagsTable());
        }
        ,onClickBtnNewTag: function(event,ctrl) {
            AcmDocument.View.Tags.$modalNewTag.modal("show");
        }
        ,onClickBtnRemoveTag: function(event,ctrl) {
            event.preventDefault();
            //find tag and id
            var id = $(event.target).closest('td').siblings(':first-child').attr('id');
            var tag = $(event.target).closest('td').siblings(':first-child').text();
            alert("id: " + id + " " + "tag: " + tag);
        }
        ,setHtmlTabTags: function(val) {
            AcmDocument.View.Tags.$tabTags.append(val);
        }
        ,buildTagsTable: function(tags) {
            var html = "";
            for (var i = 0; i < Acm.goodValue(tags.length); i++) {
                html+= "<tr>"
                +"<td id='" + Acm.goodValue(tags[i].id) + "'>" + Acm.goodValue(tags[i].name) + "</td>"
                +"<td><button type='button' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i></button>"
                +"<ul class='dropdown-menu'>"
                +"<li><a href='#' class='removeTag'>Remove</a></li>"
                +"</ul></td>"
                +"</tr>"
            }
            this.setHtmlTabTags(html);
        }
    }

    ,VersionHistory: {
        create: function() {
            this.$tabVersionHistory = $("#tabVersionHistory")
            //dummy
            var versionHistoryList = [{"name" : "V1" , "id": "11", "date" : "03/13/2015", "user" : "ann-acm"},
                                        {"name" : "V2" ,"id": "12", "date" : "03/17/2015", "user" : "ann-acm"},
                                        {"name" : "V3" ,"id": "13", "date" : "03/19/2015", "user" : "ann-acm"}];

            this.buildVersionHistoryTable(versionHistoryList);

            this.$btnMakeActiveVersion    	    = $(".makeActiveVersion");
            this.$btnMakeActiveVersion.on("click", function(e) {AcmDocument.View.VersionHistory.onClickBtnMakeActiveVersion(e, this);});

            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(AcmDocument.View.VersionHistory.buildVersionHistoryTable());
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(AcmDocument.View.VersionHistory.buildVersionHistoryTable());
        }

        ,onClickBtnMakeActiveVersion:function(event,ctrl){
            event.preventDefault();
            //find version name and id
            var id = $(event.target).closest('td').siblings(':first-child').attr('id');
            var versionName = $(event.target).closest('td').siblings(':first-child').text();
            alert("id: " + id + " " + "version name: " + versionName);
        }

        ,setHtmlTabVersionHistory: function(val) {
            //$(val).appendTo(this.$tabVersionHistory);
            AcmDocument.View.VersionHistory.$tabVersionHistory.append(val);
        }
        ,buildVersionHistoryTable: function(versionHistoryList) {
            var html = "";
            for (var i = 0; i < versionHistoryList.length; i++) {
                html+= "<tr>"
                            +"<td id='" + Acm.goodValue(versionHistoryList[i].id) + "'>" + Acm.goodValue(versionHistoryList[i].name) + "</td>"
                            +"<td>" + Acm.goodValue(versionHistoryList[i].date) + "</td>"
                            +"<td>" + Acm.goodValue(versionHistoryList[i].user) +  "</td>"
                            +"<td><button type='button' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i></button>"
                            +"<ul class='dropdown-menu'>"
                                +"<li><a href='#' class='makeActiveVersion'>Make Active</a></li>"
                            +"</ul></td>"
                        +"</tr>"
            }
            this.setHtmlTabVersionHistory(html);
        }
    }

    ,EventHistory: {
        create: function() {
            this.$tabEventHistory = $("#tabEventHistory");

            //dummy
            var eventHistoryList = [{"event" : "Assigned" , "date" : "03/17/2015", "user" : "ann-acm"},
                {"event" : "Unassigned" , "date" : "03/17/2015", "user" : "ann-acm"},
                {"event" : "Approved" , "date" : "03/17/2015", "user" : "ann-acm"}];

            this.buildEventHistoryTable(eventHistoryList);

            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
            //Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT      ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(AcmDocument.View.EventHistory.buildEventHistoryTable());
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(AcmDocument.View.EventHistory.buildEventHistoryTable());
        }
        ,setHtmlTabEventHistory: function(val) {
            AcmDocument.View.EventHistory.$tabEventHistory.append(val);
        }
        ,buildEventHistoryTable: function(eventHistoryList) {
            var html = "";
            for (var i = 0; i < eventHistoryList.length; i++) {

                html+= "<tr>"
                            +"<td>" + eventHistoryList[i].event + "</td>"
                            +"<td>" + eventHistoryList[i].date + "</td>"
                            +"<td>" + eventHistoryList[i].user +  "</td>"
                        +"</tr>"
            }
            this.setHtmlTabEventHistory(html);
        }
    }
};

