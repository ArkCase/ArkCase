/**
 * Document.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
AcmDocument.Service = {
    create : function() {
        if (AcmDocument.Service.Lookup.create) {AcmDocument.Service.Lookup.create();}
        if (AcmDocument.Service.Detail.create) {AcmDocument.Service.Detail.create();}
        if (AcmDocument.Service.Documents.create) {AcmDocument.Service.Documents.create();}
        if (AcmDocument.Service.Notes.create) {AcmDocument.Service.Notes.create();}
        if (AcmDocument.Service.AssociatedTags.create) {AcmDocument.Service.AssociatedTags.create();}
        if (AcmDocument.Service.Participants.create) {AcmDocument.Service.Participants.create();}
        if (AcmDocument.Service.VersionHistory.create) {AcmDocument.Service.VersionHistory.create();}
    }
    ,onInitialized: function() {
        if (AcmDocument.Service.Lookup.onInitialized) {AcmDocument.Service.Lookup.onInitialized();}
        if (AcmDocument.Service.Detail.onInitialized) {AcmDocument.Service.Detail.onInitialized();}
        if (AcmDocument.Service.Documents.onInitialized) {AcmDocument.Service.Documents.onInitialized();}
        if (AcmDocument.Service.Notes.onInitialized) {AcmDocument.Service.Notes.onInitialized();}
        if (AcmDocument.Service.AssociatedTags.onInitialized) {AcmDocument.Service.AssociatedTags.onInitialized();}
        if (AcmDocument.Service.Participants.onInitialized) {AcmDocument.Service.Participants.onInitialized();}
        if (AcmDocument.Service.VersionHistory.onInitialized) {AcmDocument.Service.VersionHistory.onInitialized();}
    }

    ,Lookup: {
        create: function() {
        }
        ,onInitialized: function() {
        }
    }

    ,Participants:{
        create: function(){

        }
        ,onInitialized: function(){

        }

        ,API_PARTICIPANTS_COMMON            : "/api/v1/service/participant/"

        ,retrieveParticipants : function(documentId) {
            var url = App.getContextPath() + this.API_PARTICIPANTS_COMMON + AcmDocument.Model.DOC_TYPE_DOCUMENT;
            url += "/" + documentId;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelRetrievedParticipants(response);

                    } else {
                        if (AcmDocument.Model.Participants.validateParticipants(response)) {
                            var participants = response;
                            AcmDocument.Model.Participants.cacheParticipants.put(documentId, participants);
                            AcmDocument.Controller.modelRetrievedParticipants(participants);
                        }
                    }
                }
                ,url
            )
        }

        ,addNewParticipant : function(userId, participantType, documentId) {
            var url = App.getContextPath() + this.API_PARTICIPANTS_COMMON + userId;
            url += "/" + participantType;
            url += "/" + AcmDocument.Model.DOC_TYPE_DOCUMENT;
            url += "/" + documentId;

            Acm.Service.asyncPut(
                function (response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelAddedNewParticipant(response);
                    } else {
                        if (AcmDocument.Model.Participants.validateParticipant(response)) {
                            var participant = response;
                            var participants = AcmDocument.Model.Participants.cacheParticipants.get(documentId);
                            if(AcmDocument.Model.Participants.validateParticipants(participants)){
                                participants.push(participant);
                                AcmDocument.Controller.modelAddedNewParticipant(participants);
                            }
                        }
                    } //end else
                }
                , url
            )
        }

        ,removeParticipant : function(participantId, userId, participantType,documentId) {
            var url = App.getContextPath() + this.API_PARTICIPANTS_COMMON + userId;
            url += "/" + participantType;
            url += "/" + AcmDocument.Model.DOC_TYPE_DOCUMENT;
            url += "/" + documentId;

            Acm.Service.asyncDelete(
                function (response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelRemovedParticipant(response);
                    } else {
                        if (AcmDocument.Model.Participants.validateRemovedParticipant(response)) {
                            if (response.deletedParticipantId == participantId) {
                                var participants = AcmDocument.Model.Participants.cacheParticipants.get(documentId);
                                if(AcmDocument.Model.Participants.validateParticipants(participants)){
                                    for (var i = 0; i < participants.length; i++) {
                                        if(AcmDocument.Model.Participants.validateParticipant(participants[i])){
                                            if (participantId == participants[i].id) {
                                                participants.splice(i, 1);
                                                AcmDocument.Controller.modelRemovedParticipant(participants);
                                            }
                                        }
                                    } //end for
                                }
                            }
                        }
                    } //end else
                }
                , url
            )
        }

        ,changeParticipantRole : function(participantType, participantId, documentId) {
            var url = App.getContextPath() + this.API_PARTICIPANTS_COMMON + participantId;
            url += "/" + participantType;
            Acm.Service.asyncDelete(
                function (response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelChangedParticipantRole(response);
                    } else {
                        if (AcmDocument.Model.Participants.validateParticipant(response)) {
                            if (response.id == participantId) {
                                var participants = AcmDocument.Model.Participants.cacheParticipants.get(documentId);
                                if(AcmDocument.Model.Participants.validateParticipants(participants)){
                                    for (var i = 0; i < participants.length; i++) {
                                        if(AcmDocument.Model.Participants.validateParticipant(participants[i])){
                                            if (participantId == participants[i].id) {
                                                participants[i] = response;
                                                AcmDocument.Controller.modelChangedParticipantRole(participants);
                                            }
                                        }
                                    } //end for
                                }
                            }
                        }
                    } //end else
                }
                , url
            )
        }
    }
    ,AssociatedTags: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_ASSOCIATED_TAGS_COMMON           : "/api/latest/service/tag"

        ,API_RETRIEVE_ASSOCIATED_TAGS         : "/api/latest/service/tag/"
        ,API_REMOVE_ASSOCIATED_TAGS           : "/api/latest/service/tag/"

        ,retrieveAllTags : function(documentId) {
            var url = App.getContextPath() + this.API_ASSOCIATED_TAGS_COMMON;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelRetrievedAllTags(response);

                    } else {
                        if (AcmDocument.Model.AssociatedTags.validateTags(response)) {
                            var allTags = response;
                            AcmDocument.Model.AssociatedTags.cacheAllTags.put(documentId, allTags);
                            AcmDocument.Controller.modelRetrievedAllTags(allTags);
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveAssociatedTags : function(documentId) {
            var url = App.getContextPath() + this.API_RETRIEVE_ASSOCIATED_TAGS + documentId;
            url += "/" + AcmDocument.Model.DOC_TYPE_DOCUMENT;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelRetrievedAssociatedTags(response);

                    } else {
                        if (AcmDocument.Model.AssociatedTags.validateTags(response)) {
                            var associatedTags = response;
                            AcmDocument.Model.AssociatedTags.cacheAssociatedTags.put(documentId, associatedTags);
                            AcmDocument.Controller.modelRetrievedAssociatedTags(associatedTags);
                        }
                    }
                }
                ,url
            )
        }

        ,createNewTag : function(tagName,tagDesc,tagText,documentId) {
            var url = App.getContextPath() + this.API_ASSOCIATED_TAGS_COMMON;
            url +=  "?name=" + tagName;
            url +=  "&desc=" + tagDesc;
            url +=  "&text=" + tagText;

            Acm.Service.asyncPut(
                function (response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelCreatedNewTag(response);
                    } else {
                        if (AcmDocument.Model.AssociatedTags.validateTag(response)) {
                            var newTag = response;
                            var allTags = AcmDocument.Model.AssociatedTags.cacheAllTags.get(documentId);
                            if (AcmDocument.Model.AssociatedTags.validateTags(allTags)) {
                                allTags.push(newTag);
                            }
                            AcmDocument.Controller.modelCreatedNewTag(newTag);
                        }
                    } //end else
                }
                , url
            )
        }

        ,associateNewTag : function(documentId, tagId) {
            var url = App.getContextPath() + this.API_ASSOCIATED_TAGS_COMMON + "/" + documentId;
            url += "/" + AcmDocument.Model.DOC_TYPE_DOCUMENT;
            url += "/" + tagId;

            Acm.Service.asyncPut(
                function (response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelAssociatedNewTag(response);
                    } else {
                        if (AcmDocument.Model.AssociatedTags.validateNewTagAssociation(response)) {
                            var tagId = response.tagId;
                            var allTags = AcmDocument.Model.AssociatedTags.cacheAllTags.get(documentId);
                            var associatedTags = AcmDocument.Model.AssociatedTags.cacheAssociatedTags.get(documentId);
                            if (AcmDocument.Model.AssociatedTags.validateTags(allTags) && AcmDocument.Model.AssociatedTags.validateTags(associatedTags)) {
                                for(var i = 0; i < allTags.length; i++){
                                    if(tagId == allTags[i].id){
                                        associatedTags.push(allTags[i]);
                                        AcmDocument.Controller.modelAssociatedNewTag(associatedTags);
                                    }
                                }
                            }
                        }
                    } //end else
                }
                , url
            )
        }

        ,removeAssociatedTag : function(documentId,tagId) {
            var url = App.getContextPath() + this.API_REMOVE_ASSOCIATED_TAGS + documentId;
            url += "/" + AcmDocument.Model.DOC_TYPE_DOCUMENT;
            url += "/" + tagId;

            Acm.Service.asyncDelete(
                function (response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelRemovedAssociatedTag(response);

                    } else {
                        if (AcmDocument.Model.AssociatedTags.validateRemovedAssociatedTag(response)) {
                            if (response.tagId == tagId) {
                                var associatedTags = AcmDocument.Model.AssociatedTags.cacheAssociatedTags.get(documentId);
                                if(AcmDocument.Model.AssociatedTags.validateTags(associatedTags)){
                                    for (var i = 0; i < associatedTags.length; i++) {
                                        if(AcmDocument.Model.AssociatedTags.validateTag(associatedTags[i])){
                                            if (tagId == associatedTags[i].id) {
                                                associatedTags.splice(i, 1);
                                                AcmDocument.Controller.modelRemovedAssociatedTag(associatedTags);
                                            }
                                        }
                                    } //end for
                                }

                            }
                        }
                    } //end else
                }
                , url
            )
        }
    }

    ,Detail: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_RETRIEVE_DOCUMENT_DETAILS: "/api/v1/service/ecm/file/"

        ,retrieveDocumentDetail : function(documentId) {
            var url = App.getContextPath() + this.API_RETRIEVE_DOCUMENT_DETAILS;
            url += documentId;

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelRetrievedDocumentDetail(response);

                    } else {
                        if (AcmDocument.Model.Detail.validateDocumentDetail(response)) {
                            var documentDetail = response;
                            AcmDocument.Model.Detail.cacheDocumentDetail.put(documentId, documentDetail);
                            AcmDocument.Controller.modelRetrievedDocumentDetail(documentDetail);
                        }
                    }
                }
                ,url
            )
        }

    }

    ,Documents: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_DOWNLOAD_DOCUMENT_      : "/api/latest/plugin/ecm/download/byId/"
        ,API_UPLOAD_DOCUMENT: "/api/latest/plugin/casefile/file"

        ,_validateUploadInfo: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            if (0 >= data.length) {
                return false;
            }
            return true;
        }
        ,uploadDocument: function(formData) {
            var url = App.getContextPath() + this.API_UPLOAD_DOCUMENT;
            Acm.Service.ajax({
                url: url
                ,data: formData
                ,processData: false
                ,contentType: false
                ,type: 'POST'
                ,success: function(response){
                    if (response.hasError) {
                        AcmDocument.Controller.modelAddedDocument(response);
                    } else {
                        if (AcmDocument.Service.Documents._validateUploadInfo(response)) {
                            if(response!= null){
                                var uploadInfo = response;
                                //var documentId = AcmDocument.Model.getDocumentId();
                                /*var prevAttachmentsList = AcmDocument.Model.Documents.cacheDocuments.get(documentId);
                                for(var i = 0; i < response.files.length; i++){
                                    var attachment = {};
                                    attachment.id = response.files[i].id;
                                    attachment.name = response.files[i].name;
                                    attachment.status = response.files[i].status;
                                    attachment.creator = response.files[i].creator;
                                    attachment.created = response.files[i].created;
                                    attachment.targetSubtype = response.files[i].uploadFileType;
                                    attachment.targetType = AcmDocument.Model.DOCUMENT_TARGET_TYPE_FILE;
                                    prevAttachmentsList.push(attachment);
                                    //attachment.category = response.files[i].category;
                                }
                                AcmDocument.Model.Documents.cacheDocuments.put(documentId, prevAttachmentsList);*/
                                AcmDocument.Controller.modelAddedDocument(uploadInfo);
                            }
                        }
                    }
                }
            });
        }

    }

    ,Notes: {
        create: function () {
        }
        , onInitialized: function () {
        }
        , API_SAVE_NOTE: "/api/latest/plugin/note"
        , API_DELETE_NOTE_: "/api/latest/plugin/note/"
        , API_LIST_NOTES_: "/api/latest/plugin/note/"

        , retrieveNoteListDeferred: function (documentId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                , function () {
                    var url;
                    url = App.getContextPath() + AcmDocument.Service.Notes.API_LIST_NOTES_ + AcmDocument.Model.DOC_TYPE_DOCUMENT_SM + "/";
                    url += documentId;
                    return url;
                }
                , function (data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (AcmDocument.Model.Notes.validateNotes(data)) {
                        var noteList = data;
                        AcmDocument.Model.Notes.cacheNoteList.put(documentId, noteList);
                        jtData = callbackSuccess(noteList);
                    }
                    return jtData;
                }
            );
        }


        , saveNote: function (data,objectId,handler) {
            Acm.Service.asyncPost(
                function (response) {
                    if (response.hasError) {
                        if (handler) {
                            handler(response);
                        } else {
                            AcmDocument.Controller.modelSavedNote(response);
                        }

                    } else {
                        if (AcmDocument.Model.Notes.validateNote(response)) {
                            var documentId = objectId;
                            var note = response;
                            if (documentId == note.parentId) {
                                var noteList = AcmDocument.Model.Notes.cacheNoteList.get(documentId);
                                if(AcmDocument.Model.Notes.validateNotes(noteList)){
                                    var found = -1;
                                    for (var i = 0; i < noteList.length; i++) {
                                        if(AcmDocument.Model.Notes.validateNote(noteList[i])){
                                            if (note.id == noteList[i].id) {
                                                found = i;
                                                break;
                                            }
                                        }
                                    }
                                    if (0 > found) {                //add new note
                                        noteList.push(note);
                                    } else {                        // update existing note
                                        noteList[found] = note;
                                    }

                                    if (handler) {
                                        handler(note);
                                    } else {
                                        AcmDocument.Controller.modelSavedNote(note);
                                    }
                                }
                            }
                        }
                    }
                }
                , App.getContextPath() + this.API_SAVE_NOTE
                , JSON.stringify(data)
            )
        }
        , addNote: function (note,documentId) {
            if (AcmDocument.Model.Notes.validateNote(note)) {
                this.saveNote(note,documentId
                    , function (data) {
                        AcmDocument.Controller.modelAddedNote(data,documentId);
                    }
                );
            }
        }
        , updateNote: function (note,documentId) {
            if (AcmDocument.Model.Notes.validateNote(note)) {
                this.saveNote(note,documentId
                    , function (data) {
                        AcmDocument.Controller.modelUpdatedNote(data);
                    }
                );
            }
        }


        , deleteNote: function (noteId,documentId) {
            var url = App.getContextPath() + this.API_DELETE_NOTE_ + noteId;

            Acm.Service.asyncDelete(
                function (response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelDeletedNote(response);

                    } else {
                        if (AcmDocument.Model.Notes.validateDeletedNote(response)) {
                            if (response.deletedNoteId == noteId) {
                                var noteList = AcmDocument.Model.Notes.cacheNoteList.get(documentId);
                                if(AcmDocument.Model.Notes.validateNotes(noteList)){
                                    for (var i = 0; i < noteList.length; i++) {
                                        if(AcmDocument.Model.Notes.validateNote(noteList[i])){
                                            if (noteId == noteList[i].id) {
                                                noteList.splice(i, 1);
                                                AcmDocument.Controller.modelDeletedNote(Acm.Service.responseWrapper(response, noteId));
                                                return;
                                            }
                                        }
                                    } //end for
                                }

                            }
                        }
                    } //end else
                }
                , url
            )
        }
    }

    ,VersionHistory: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_CHANGE_ACTIVE_FILE_VERSION: "/api/v1/service/ecm/file/"

        ,changeActiveFileVersion: function(documentId, versionTag){
            var url = App.getContextPath() + this.API_CHANGE_ACTIVE_FILE_VERSION;
            url+= documentId + "?versionTag=" + versionTag;

            Acm.Service.asyncPost(
                function (response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelChangedActiveFileVersion(documentId,response);
                    } else {
                        if (AcmDocument.Model.VersionHistory.validateDocumentDetail(response)) {
                            var documentDetail = response;
                            AcmDocument.Controller.modelChangedActiveFileVersion(documentId,documentDetail);
                        }
                    } //end else
                }
                , url
            )
        }
    }
};

