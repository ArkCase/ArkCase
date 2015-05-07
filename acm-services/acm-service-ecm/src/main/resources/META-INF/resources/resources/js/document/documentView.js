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
        if (AcmDocument.View.AssociatedTags.create)                   {AcmDocument.View.AssociatedTags.create();}
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
        if (AcmDocument.View.AssociatedTags.onInitialized)                    {AcmDocument.View.AssociatedTags.onInitialized();}
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
            this.documentId   = Acm.Object.MicroData.get("objId");
            this.participantTypes   = Acm.Object.MicroData.getJson("participantTypes");
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
            this.$lnkActiveVersion  = $("#activeVersion");

            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_RETRIEVED_DOCUMENT_DETAIL           ,this.onModelRetrievedDocumentDetail);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_CHANGED_ACTIVE_FILE_VERSION           ,this.onModelChangedActiveFileVersion);

        }
        ,onInitialized: function() {
        }

        ,onModelChangedActiveFileVersion: function(documentId, documentDetail) {
            if (documentDetail.hasError) {
                //App.View.MessageBoard.show(documentDetail.errorMsg);
            }
            else if(AcmDocument.Model.Detail.validateDocumentDetail(documentDetail)) {
                AcmDocument.Model.Detail.cacheDocumentDetail.put(documentId, documentDetail);
                AcmDocument.View.Detail.setTextLnkActiveVersion("(v" + Acm.goodValue(documentDetail.activeVersionTag)+ ")");
            }
        }
        ,onModelRetrievedDocumentDetail: function(documentDetail) {
            if(documentDetail.hasError){
                App.View.MessageBoard.show(documentDetail.errorMsg);
            }
            else if (AcmDocument.Model.Detail.validateDocumentDetail(documentDetail)) {
                AcmDocument.View.Detail.setTextLnkDocTitle(Acm.goodValue(documentDetail.fileName));
                AcmDocument.View.Detail.setTextLnkActiveVersion("(v" + Acm.goodValue(documentDetail.activeVersionTag)+ ")");
                AcmDocument.View.Detail.setTextLnkCreateDate(Acm.getDateFromDatetime(documentDetail.created));
                AcmDocument.View.Detail.setTextLnkType(Acm.goodValue(documentDetail.fileType));
                AcmDocument.View.Detail.setTextLnkOwner(Acm.__FixMe__getUserFullName(documentDetail.creator));
                AcmDocument.View.Detail.setTextLnkStatus(Acm.goodValue(documentDetail.status));
                AcmDocument.View.Detail.setTextLnkAssignee(Acm.__FixMe__getUserFullName(documentDetail.creator));
            }
        }
        ,setTextLnkDocTitle: function(txt) {
            Acm.Object.setText(this.$lnkDocTitle, txt);
        }
        ,setTextLnkActiveVersion: function(txt) {
            Acm.Object.setText(this.$lnkActiveVersion, txt);
        }
        ,setTextLnkCreateDate: function(txt) {
            Acm.Object.setText(this.$lnkCreateDate, txt);
        }
        ,setTextLnkAssignee: function(txt) {
            Acm.Object.setText(this.$lnkAssignee, txt);
        }
        ,setTextLnkType: function(txt) {
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
            //this.createJTableDocViewer(this.$divDocViewer);

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
            this.$tabParticipants                       = $("#tabParticipants");
            this.$tabParticipants.on("click", ".removeParticipant", function(e) {AcmDocument.View.Participants.onClickBtnRemoveParticipant(e, this);});
            this.$tabParticipants.on("click", ".changeParticipantRole", function(e) {AcmDocument.View.Participants.onClickBtnChangeRole(e, this);});
            this.$modalParticipantChangeRole            = $("#modalParticipantChangeRole");
            this.$btnChangeParticipantRole              = this.$modalParticipantChangeRole.find('button.btn-primary');
            this.$btnChangeParticipantRole.on("click", function(e) {AcmDocument.View.Participants.onClickModalBtnChangeRole(e, this);})
            this.$selParticipantRole                    = $("#participantRoles");

            this.$labParticipants                       = $("#labParticipants");

            this.$btnNewParticipant                     = $("#newParticipant");
            this.$btnNewParticipant.on("click", function(e) {AcmDocument.View.Participants.onClickBtnNewParticipant(e, this);});

            this.$dlgObjectPicker                       = $("#dlgObjectPicker");
            this.$selParticipantType                    = $("#participantType");

            this.$btnAddParticipant                     = this.$dlgObjectPicker.find('button.btn-primary');
            this.$btnAddParticipant.unbind("click").on("click", function(e) {AcmDocument.View.Participants.onClickBtnAddParticipant(e, this);})

            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_RETRIEVED_PARTICIPANTS    ,this.onModelRetrievedParticipants);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_REMOVED_PARTICIPANT       ,this.onModelRemovedParticipant);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_CHANGED_PARTICIPANT_ROLE  ,this.onModelChangedParticipantRole);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_ADDED_NEW_PARTICIPANT      ,this.onModelAddedNewParticipant);
        }
        ,onInitialized: function() {
        }
        ,_participantId:0
        ,getParticipantId: function() {
            return this._participantId;
        }
        ,setParticipantId: function(participantId) {
            this._participantId = participantId;
        }
        ,onModelAddedNewParticipant: function(participants) {
            if(participants.hasError){
                App.View.MessageBoard.show(participants.errorMsg);
            }
            else{
                AcmDocument.View.Participants.buildParticipantsTable(participants);
            }
        }
        ,onModelRetrievedParticipants: function(participants) {
            if(participants.hasError){
                App.View.MessageBoard.show(participants.errorMsg);
            }
            else{
                AcmDocument.View.Participants.buildParticipantsTable(participants);
            }
        }
        ,onModelRemovedParticipant: function(participants) {
            if(participants.hasError){
                App.View.MessageBoard.show(participants.errorMsg);
            }
            else{
                AcmDocument.View.Participants.buildParticipantsTable(participants);
            }
        }
        ,onModelChangedParticipantRole: function(participants) {
            if(participants.hasError){
                App.View.MessageBoard.show(participants.errorMsg);
            }
            else{
                AcmDocument.View.Participants.buildParticipantsTable(participants);
            }
        }
        ,pickParticipant: function() {
            SearchBase.showSearchDialog({name: "New Participant"
                ,title: "Add New Participant"
                ,prompt: "Enter to search for user.."
                ,btnGoText: "Search Now!"
                ,btnOkText: "Select"
                ,btnCancelText: "Cancel"
                ,filters: [{key: "Object Type", values: ["USER"]}]
                ,$dlgObjectPicker : AcmDocument.View.Participants.$dlgObjectPicker
            });
        }

        ,onClickBtnNewParticipant: function() {
            AcmDocument.View.Participants.$selParticipantType.empty();
            var participantTypes = AcmDocument.View.MicroData.participantTypes;
            if(Acm.isNotEmpty(participantTypes)){
                for(var i=0;i< participantTypes.length;i++){
                    var participantType = AcmDocument.View.MicroData.participantTypes[i];
                    Acm.Object.appendSelect(AcmDocument.View.Participants.$selParticipantType, participantType.toLowerCase(), participantType);
                };
            }
            AcmDocument.View.Participants.pickParticipant();
            return;
        }

        ,onClickBtnAddParticipant : function(event, ctrl) {
            SearchBase.View.Results.getSelectedRows().each(function () {
                var record = $(this).data('record');
                if(Acm.isNotEmpty(record) && Acm.isNotEmpty(record.id)){
                    var userId = Acm.goodValue(record.id);
                    var participantType = Acm.goodValue(AcmDocument.View.Participants.getSelectValueParticipantType());
                    if(Acm.isEmpty(userId) || Acm.isEmpty(participantType)){
                        Acm.Dialog.info("Please select both participant and participant type.")
                    }
                    else{
                        var documentId = Acm.goodValue(AcmDocument.View.MicroData.documentId);
                        AcmDocument.Controller.viewAddedNewParticipant(Acm.goodValue(record.id),Acm.goodValue(participantType),documentId)
                        AcmDocument.View.Participants.$dlgObjectPicker.modal("hide");
                    }
                }
            });
        }

        ,onClickBtnRemoveParticipant: function(event,ctrl) {
            event.preventDefault();
            //find participant name and id
            var documentId = Acm.goodValue(AcmDocument.View.MicroData.documentId);
            var participantId = $(event.target).closest('div').next().attr('id');
            var userId = $(event.target).closest('div').next().children('div').attr('data-user-id');
            var participantType = $(event.target).closest('div').next().children('small').prop('textContent').toLowerCase();
            AcmDocument.Controller.viewRemovedParticipant(participantId, userId, participantType, documentId);
        }
        ,onClickBtnChangeRole: function(event,ctrl) {
            AcmDocument.View.Participants.buildChangeRoleDialog();
            AcmDocument.View.Participants.$modalParticipantChangeRole.modal('show');
            var participantId = $(event.target).closest('div').next().attr('id');
            AcmDocument.View.Participants.setParticipantId(participantId);
            //var participantName = $(event.target).closest('div').next().children('div').prop('textContent');
            //var participantType = $(event.target).closest('div').next().children('small').prop('textContent');
            //AcmDocument.Controller.viewChangedParticipantRole(participantType, participantId, documentId);
        }
        ,onClickModalBtnChangeRole: function(event,ctrl){
            var selctedParticipantRole = AcmDocument.View.Participants.getSelectValueParticipantRole();
            if(Acm.isNotEmpty(selctedParticipantRole)){
                var documentId = Acm.goodValue(AcmDocument.View.MicroData.documentId);
                var participantId = AcmDocument.View.Participants.getParticipantId();
                AcmDocument.Controller.viewChangedParticipantRole(selctedParticipantRole, participantId, documentId);
                AcmDocument.View.Participants.$modalParticipantChangeRole.modal('hide');
            }
        }

        ,buildChangeRoleDialog: function(){
            AcmDocument.View.Participants.$selParticipantRole.empty();
            var participantRoles = AcmDocument.View.MicroData.participantTypes;
            if(Acm.isNotEmpty(participantRoles)){
                for(var i=0;i< participantRoles.length;i++){
                    var participantRole = AcmDocument.View.MicroData.participantTypes[i];
                    Acm.Object.appendSelect(AcmDocument.View.Participants.$selParticipantRole, participantRole.toLowerCase(), participantRole);
                };
            }
        }
        ,setTextLabParticipants: function(totalParticipants){
            Acm.Object.setText(AcmDocument.View.Participants.$labParticipants, totalParticipants);
        }
        ,getSelectValueParticipantType: function() {
            return Acm.Object.getSelectValue(AcmDocument.View.Participants.$selParticipantType);
        }
        ,getSelectValueParticipantRole: function() {
            return Acm.Object.getSelectValue(AcmDocument.View.Participants.$selParticipantRole);
        }
        ,setHtmlTabParticipants: function(val) {
            AcmDocument.View.Participants.$tabParticipants.append(val);
        }
        ,clearHtmlTabParticipants: function(){
            AcmDocument.View.Participants.$tabParticipants.find("li").remove();
        }

        ,buildParticipantsTable: function(participants) {
            if(AcmDocument.Model.Participants.validateParticipants(participants)) {
                AcmDocument.View.Participants.clearHtmlTabParticipants();
                var html = "";
                for (var i = 0; i < participants.length; i++) {
                    if(AcmDocument.Model.Participants.validateParticipant(participants[i])){
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
                        + "<div data-user-id='" + Acm.goodValue(participants[i].participantLdapId) + "'><a href='#'>" + Acm.goodValue(Acm.__FixMe__getUserFullName(participants[i].participantLdapId)) + "</a></div>"
                        + "<small class='text-muted'>" + Acm.goodValue(participants[i].participantType.charAt(0).toUpperCase() + participants[i].participantType.slice(1)) + "</small> </div>"
                        + "</div>"
                        +"</li>";
                    }
                }
                AcmDocument.View.Participants.setTextLabParticipants(participants.length);
                AcmDocument.View.Participants.setHtmlTabParticipants(html);
            }

        }
    }

    ,Notes: {
        create: function() {
            this.$divNotes          = $("#divNotes");
            this.createJTableNotes(this.$divNotes);

            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_ADDED_NOTE        ,this.onModelAddedNote);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_UPDATED_NOTE      ,this.onModelUpdatedNote);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_DELETED_NOTE      ,this.onModelDeletedNote);
        }
        ,onInitialized: function() {
        }
        ,onModelAddedNote: function(note) {
            if (note.hasError) {
                App.View.MessageBoard.show(note.errorMsg);
            } else {
                AcmEx.Object.JTable.load(AcmDocument.View.Notes.$divNotes);
            }
        }
        ,onModelUpdatedNote: function(note) {
            if (note.hasError) {
                App.View.MessageBoard.show(note.errorMsg);
            } else {
                AcmEx.Object.JTable.load(AcmDocument.View.Notes.$divNotes);
            }
        }
        ,onModelDeletedNote: function(note) {
            if (note.hasError) {
                App.View.MessageBoard.show(note.errorMsg);
            } else {
                AcmEx.Object.JTable.load(AcmDocument.View.Notes.$divNotes);
            }
        }
        ,_makeJtData: function(noteList) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (AcmDocument.Model.Notes.validateNotes(noteList)) {
                for (var i = 0; i < noteList.length; i++) {
                    if(AcmDocument.Model.Notes.validateNote(noteList[i])){
                        var Record = {};
                        Record.id         = Acm.goodValue(noteList[i].id, 0);
                        Record.note       = Acm.goodValue(noteList[i].note);
                        Record.created    = Acm.getDateFromDatetime(noteList[i].created);
                        Record.creator    = Acm.__FixMe__getUserFullName(Acm.goodValue(noteList[i].creator));
                        jtData.Records.push(Record);
                    }
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
                    ,messages: {
                        addNewRecord: 'Add Note'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            //var documentId = AcmDocument.View.getActiveDocumentId();
                            var documentId = AcmDocument.View.MicroData.documentId;
                            if (0 >= documentId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var noteList = AcmDocument.Model.Notes.cacheNoteList.get(documentId);
                            if (noteList) {
                                return AcmDocument.View.Notes._makeJtData(noteList);

                            } else {
                                return AcmDocument.Service.Notes.retrieveNoteListDeferred(documentId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        var noteList = data;
                                        return AcmDocument.View.Notes._makeJtData(noteList);
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            //var documentId = AcmDocument.View.getActiveDocumentId();
                            var documentId = AcmDocument.View.MicroData.documentId;
                            //var document = AcmDocument.View.getActiveDocument();
                            //if (document) {
                                rc.Record.parentId = Acm.goodValue(documentId, 0);
                                rc.Record.note = Acm.goodValue(record.note);
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = Acm.__FixMe__getUserFullName(App.getUserName());    //record.creator;
                            //}
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            //var documentId = AcmDocument.View.getActiveDocumentId();
                            var documentId = AcmDocument.View.MicroData.documentId;
                            //var document = AcmDocument.View.getActiveDocument();
                            //if (document) {
                                rc.Record.parentId = Acm.goodValue(documentId, 0);
                                rc.Record.note = Acm.goodValue(record.note);
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = Acm.__FixMe__getUserFullName(App.getUserName());   //record.creator;
                            //}
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
                            ,display: function (data) {
                                return "<p id='acm-docDetailLongNote' title='" + data.record.note + "'>" + data.record.note + "</p>";
                            }
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
                        //var documentId = AcmDocument.View.getActiveDocumentId();
                        //var documentId = AcmDocument.View.MicroData.documentId;
                        var documentId = Acm.goodValue(record.parentId);
                        if (0 < documentId) {
                            var noteToSave = {};
                            noteToSave.note = Acm.goodValue(record.note);
                            noteToSave.created = Acm.getCurrentDayInternal();
                            noteToSave.creator = Acm.goodValue(record.creator);
                            noteToSave.parentId = Acm.goodValue(record.parentId);
                            noteToSave.parentType = AcmDocument.Model.DOC_TYPE_DOCUMENT;
                            AcmDocument.Controller.viewAddedNote(noteToSave,documentId);
                        }
                    }
                    ,recordUpdated: function(event,data){
                        var whichRow = data.row.prevAll("tr").length;
                        var record = data.record;
                        //var documentId = AcmDocument.View.getActiveDocumentId();
                        //var documentId = AcmDocument.View.MicroData.documentId;
                        var documentId = data.record.parentId;
                        if (0 < documentId) {
                            var notes = AcmDocument.Model.Notes.cacheNoteList.get(documentId);
                            if (Acm.isNotEmpty(notes)) {
                                if(Acm.isNotEmpty(notes[whichRow])){
                                    notes[whichRow].note = Acm.goodValue(record.note);
                                    AcmDocument.Controller.viewUpdatedNote(notes[whichRow],documentId);
                                }
                            }
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        //var documentId = AcmDocument.View.getActiveDocumentId();
                        var documentId = AcmDocument.View.MicroData.documentId;
                        if (0 < documentId) {
                            var notes = AcmDocument.Model.Notes.cacheNoteList.get(documentId);
                            if (Acm.isNotEmpty(notes)) {
                                if(Acm.isNotEmpty(notes[whichRow]) && Acm.isNotEmpty(notes[whichRow].id)){
                                    AcmDocument.Controller.viewDeletedNote(notes[whichRow].id,documentId);
                                }
                            }
                        }
                    }
                } //end arg
                ,sortMap
            );
        }
    }

    ,AssociatedTags: {
        create: function() {
            this.$modalNewAssociatedTag         = $("#modalNewTag")
            this.$modalNewAssociatedTag.on("hidden.bs.modal", function(e) {
                AcmDocument.View.AssociatedTags.clearNewTagModalContents();
            });
            this.$tabAssociatedTags             = $("#tabTags");
            this.$tabAssociatedTags.unbind("click").on("click", "a", function(e) {AcmDocument.View.AssociatedTags.onClickBtnRemoveAssociatedTag(e, this);});

            this.$labTags                       = $("#labTags");
            this.$btnNewAssociatedTag    	    = $("#btnNewTag");
            this.$btnNewAssociatedTag.on("click", function(e) {AcmDocument.View.AssociatedTags.onClickBtnNewAssociatedTag(e, this);});

            this.$btnAddNewAssociatedTag        = this.$modalNewAssociatedTag.find('button.btn-primary');
            this.$btnAddNewAssociatedTag.unbind("click").on("click", function(e) {AcmDocument.View.AssociatedTags.onClickBtnAddNewAssociatedTag(e, this);})

            this.$newTagForm                       = $("#newTagForm");
            this.$edtNewTagName                    = $("#newTagName");
            this.$edtNewTagDesc                    = $("#newTagDesc");
            this.$edtNewTagText                    = $("#newTagText");

            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_RETRIEVED_ASSOCIATED_TAGS    ,this.onModelRetrievedAssociatedTags);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_REMOVED_ASSOCIATED_TAGS      ,this.onModelRemovedAssociatedTags);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_ASSOCIATED_NEW_TAG           , this.onModelAssociatedNewTag);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_RETRIEVED_ALL_TAGS           , this.onModelRetrievedAllTags);
        }
        ,onInitialized: function() {
        }
        ,onModelRemovedAssociatedTags: function(associatedTags) {
            if(associatedTags.hasError){
                App.View.MessageBoard.show(associatedTags.errorMsg);
            }
            else{
                AcmDocument.View.AssociatedTags.buildAssociatedTagsTable(associatedTags);
            }
        }
        ,onModelRetrievedAssociatedTags: function(associatedTags) {
            if(associatedTags.hasError){
                App.View.MessageBoard.show(associatedTags.errorMsg);
            }
            else{
                AcmDocument.View.AssociatedTags.buildAssociatedTagsTable(associatedTags);
            }
        }
        ,onModelRetrievedAllTags: function(allTags) {
            if(allTags.hasError){
                App.View.MessageBoard.show(allTags.errorMsg);
            }
        }
        ,onModelAssociatedNewTag: function(associatedTags) {
            if(associatedTags.hasError){
                App.View.MessageBoard.show(associatedTags.errorMsg);
            }
            else{
                AcmDocument.View.AssociatedTags.buildAssociatedTagsTable(associatedTags);
            }
        }
        ,onClickBtnNewAssociatedTag: function(event,ctrl) {
            var documentId = Acm.goodValue(AcmDocument.View.MicroData.documentId);
            //if any previous radio buttons
            //with name tags remove them
            $("input:radio[name=tags]").parent().remove();
            var html = "";
            var allTags = AcmDocument.Model.AssociatedTags.cacheAllTags.get(documentId)
            for(var i = 0; i<allTags.length; i++){
                if(AcmDocument.Model.AssociatedTags.validateTag(allTags[i])){
                    html+= "<label for='tags'>" + "<input type='radio' name='tags' value='" + allTags[i].id + "'/>"
                    html+= allTags[i].tagName + "</label>" + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp";
                }
            }
            AcmDocument.View.AssociatedTags.$newTagForm.append(html);
            AcmDocument.View.AssociatedTags.$modalNewAssociatedTag.modal("show");
        }
        ,onClickBtnAddNewAssociatedTag: function(event,ctrl){
            var tagId = $( "input:radio[name=tags]:checked").val();
            if(Acm.isNotEmpty(tagId)){
                AcmDocument.Controller.viewAssociatedNewTag(tagId);
                AcmDocument.View.AssociatedTags.$modalNewAssociatedTag.modal("hide");
                return;
            }
            else{
                AcmDocument.View.AssociatedTags.checkCeateNewTagForm();
            }
        }
        ,onClickBtnRemoveAssociatedTag: function(event,ctrl) {
            event.preventDefault();
            //find associatedTag and tagId
            var documentId = Acm.goodValue(AcmDocument.View.MicroData.documentId);
            var tagId = $(event.target).closest('td').siblings(':first-child').attr('tagId');
            var associatedTag = $(event.target).closest('td').siblings(':first-child').text();
            AcmDocument.Controller.viewRemovedAssociatedTag(documentId, tagId);
        }
        ,checkCeateNewTagForm: function(){
            var newTagName = AcmDocument.View.AssociatedTags.getTextNewTagName();
            var newTagDesc = AcmDocument.View.AssociatedTags.getTextNewTagDesc();
            var newTagText = AcmDocument.View.AssociatedTags.getTextNewTagText();
            if(Acm.isEmpty(newTagName) || Acm.isEmpty(newTagDesc) || Acm.isEmpty(newTagText)){
                Acm.Dialog.info("Invalid input. Please check the entries and try again.")
            }
            else{
                AcmDocument.Controller.viewCreatedNewTag(newTagName, newTagDesc, newTagText);
                AcmDocument.View.AssociatedTags.$modalNewAssociatedTag.modal("hide");
            }
        }
        ,clearHtmlTabAssociatedTags: function(val) {
            AcmDocument.View.AssociatedTags.$tabAssociatedTags.find("td").remove();
        }
        ,setHtmlTabAssociatedTags: function(val) {
            AcmDocument.View.AssociatedTags.$tabAssociatedTags.append(val);
        }
        ,setTextLabTags: function(totalTags){
            Acm.Object.setText(AcmDocument.View.AssociatedTags.$labTags, totalTags);
        }
        ,getTextNewTagName: function(){
            return Acm.goodValue(AcmDocument.View.AssociatedTags.$edtNewTagName.val());
        }
        ,getTextNewTagDesc: function(){
            return Acm.goodValue(AcmDocument.View.AssociatedTags.$edtNewTagDesc.val());
        }
        ,getTextNewTagText: function(){
            return Acm.goodValue(AcmDocument.View.AssociatedTags.$edtNewTagText.val());
        }
        ,clearNewTagModalContents: function(){
            AcmDocument.View.AssociatedTags.$edtNewTagName.val('');
            AcmDocument.View.AssociatedTags.$edtNewTagDesc.val('');
            AcmDocument.View.AssociatedTags.$edtNewTagText.val('');
        }
        ,buildAssociatedTagsTable: function(associatedTags) {
            if(AcmDocument.Model.AssociatedTags.validateTags(associatedTags)){
                AcmDocument.View.AssociatedTags.clearHtmlTabAssociatedTags();
                var html = "";
                    for (var i = 0; i < Acm.goodValue(associatedTags.length); i++) {
                        if(AcmDocument.Model.AssociatedTags.validateTag(associatedTags[i])) {
                            html+= "<tr>"
                            +"<td tagId='" + Acm.goodValue(associatedTags[i].id) + "'>" + Acm.goodValue(associatedTags[i].tagName) + "</td>"
                            +"<td><button type='button' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i></button>"
                            +"<ul class='dropdown-menu'>"
                            +"<li><a href='#' class='removeTag'>Remove</a></li>"
                            +"</ul></td>"
                            +"</tr>"
                    }
                }
                AcmDocument.View.AssociatedTags.setTextLabTags(associatedTags.length);
                AcmDocument.View.AssociatedTags.setHtmlTabAssociatedTags(html);
            }
        }
    }

    ,VersionHistory: {
        create: function() {
            this.$tabVersionHistory = $("#tabVersionHistory")

            this.$tabVersionHistory.on("click", ".makeActiveVersion", function(e) {AcmDocument.View.VersionHistory.onClickBtnMakeActiveVersion(e, this);});

            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_RETRIEVED_DOCUMENT_DETAIL           ,this.onModelRetrievedDocumentDetail);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_CHANGED_ACTIVE_FILE_VERSION           ,this.onModelChangedActiveVersion);

        }
        ,onInitialized: function() {
        }

        ,onModelChangedActiveVersion:function(documentId, documentDetail){
            if(AcmDocument.Model.VersionHistory.validateDocumentDetail(documentDetail)){
                App.View.MessageBoard.show("Active file version changed successfully.");
                AcmDocument.View.VersionHistory.buildVersionHistoryTable(documentDetail.versions);
            }
            else if (documentDetail.hasError) {
                App.View.MessageBoard.show("Error setting active file version", documentDetail.errorMsg);
            }

        }
        ,onModelRetrievedDocumentDetail:function(documentDetail){
            if(AcmDocument.Model.VersionHistory.validateDocumentDetail(documentDetail)){
                AcmDocument.View.VersionHistory.buildVersionHistoryTable(documentDetail.versions);
            }
        }
        ,onClickBtnMakeActiveVersion:function(event,ctrl){
            event.preventDefault();
            //find version name and id
            var id = $(event.target).closest('td').siblings(':first-child').attr('id');
            var versionTag = $(event.target).closest('td').siblings(':first-child').text();
            var documentId = AcmDocument.View.MicroData.documentId;
            AcmDocument.Controller.viewChangedActiveFileVersion(documentId,versionTag);
        }
        ,clearHtmlTabVersionHistory: function(val) {
                AcmDocument.View.VersionHistory.$tabVersionHistory.find("td").remove();
        }
        ,setHtmlTabVersionHistory: function(val) {
            //$(val).appendTo(this.$tabVersionHistory);
            AcmDocument.View.VersionHistory.$tabVersionHistory.append(val);
        }
        ,buildVersionHistoryTable: function(versionHistoryList) {
            if(AcmDocument.Model.VersionHistory.validateVersionHistoryList(versionHistoryList)){
                AcmDocument.View.VersionHistory.clearHtmlTabVersionHistory();
                var html = "";
                for (var i = 0; i < versionHistoryList.length; i++) {
                    if(AcmDocument.Model.VersionHistory.validateVersionHistory(versionHistoryList[i])){
                        html+= "<tr>"
                            +"<td id='" + Acm.goodValue(versionHistoryList[i].id) + "'>" + Acm.goodValue(versionHistoryList[i].versionTag) + "</td>"
                            +"<td>" + Acm.getDateFromDatetime(versionHistoryList[i].created) + "</td>"
                            +"<td>" + Acm.goodValue(versionHistoryList[i].creator) +  "</td>"
                            +"<td>"
                            +"<div class='btn-group pull-right'>"

                            +"<button type='button' class='dropdown-toggle' data-toggle='dropdown'> <i class='fa fa-cog'></i></button>"
                            +"<ul class='dropdown-menu'>"
                            +"<li><a href='#' class='makeActiveVersion'>Make Active</a></li>"
                            +"</ul></div></td>"
                            +"</tr>"
                    }
                }
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

