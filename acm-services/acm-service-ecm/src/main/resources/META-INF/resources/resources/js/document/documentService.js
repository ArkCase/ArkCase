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
    }
    ,onInitialized: function() {
        if (AcmDocument.Service.Lookup.onInitialized) {AcmDocument.Service.Lookup.onInitialized();}
        if (AcmDocument.Service.Detail.onInitialized) {AcmDocument.Service.Detail.onInitialized();}
        if (AcmDocument.Service.Documents.onInitialized) {AcmDocument.Service.Documents.onInitialized();}
        if (AcmDocument.Service.Notes.onInitialized) {AcmDocument.Service.Notes.onInitialized();}
    }

    ,Lookup: {
        create: function() {
        }
        ,onInitialized: function() {
        }
    }

    ,Detail: {
        create: function() {
        }
        ,onInitialized: function() {
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
                                //var caseFileId = AcmDocument.Model.getDocumentId();
                                /*var prevAttachmentsList = AcmDocument.Model.Documents.cacheDocuments.get(caseFileId);
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
                                AcmDocument.Model.Documents.cacheDocuments.put(caseFileId, prevAttachmentsList);*/
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

        , retrieveNoteListDeferred: function (caseFileId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                , function () {
                    var url;
                    url = App.getContextPath() + AcmDocument.Service.Notes.API_LIST_NOTES_ + AcmDocument.Model.DOC_TYPE_CASE_FILE + "/";
                    url += caseFileId;
                    return url;
                }
                , function (data) {
                    var jtData = null
                    if (AcmDocument.Model.Notes.validateNotes(data)) {
                        var noteList = data;


                        AcmDocument.Model.Notes.cacheNoteList.put(caseFileId, noteList);
                        jtData = callbackSuccess(noteList);
                    }
                    return jtData;
                }
            );
        }


        , saveNote: function (data, handler) {
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
                            var note = response;
                            var caseFileId = AcmDocument.Model.getDocumentId();
                            if (caseFileId == note.parentId) {
                                var noteList = AcmDocument.Model.Notes.cacheNoteList.get(caseFileId);
                                var found = -1;
                                for (var i = 0; i < noteList.length; i++) {
                                    if (note.id == noteList[i].id) {
                                        found = i;
                                        break;
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
                , App.getContextPath() + this.API_SAVE_NOTE
                , JSON.stringify(data)
            )
        }
        , addNote: function (note) {
            if (AcmDocument.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    , function (data) {
                        AcmDocument.Controller.modelAddedNote(data);
                    }
                );
            }
        }
        , updateNote: function (note) {
            if (AcmDocument.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    , function (data) {
                        AcmDocument.Controller.modelUpdatedNote(data);
                    }
                );
            }
        }

        , _validateDeletedNote: function (data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedNoteId)) {
                return false;
            }
            return true;
        }
        , deleteNote: function (noteId) {
            var url = App.getContextPath() + this.API_DELETE_NOTE_ + noteId;

            Acm.Service.asyncDelete(
                function (response) {
                    if (response.hasError) {
                        AcmDocument.Controller.modelDeletedNote(response);

                    } else {
                        if (AcmDocument.Service.Notes._validateDeletedNote(response)) {
                            var caseFileId = AcmDocument.Model.getDocumentId();
                            if (response.deletedNoteId == noteId) {
                                var noteList = AcmDocument.Model.Notes.cacheNoteList.get(caseFileId);
                                for (var i = 0; i < noteList.length; i++) {
                                    if (noteId == noteList[i].id) {
                                        noteList.splice(i, 1);
                                        AcmDocument.Controller.modelDeletedNote(Acm.Service.responseWrapper(response, noteId));
                                        return;
                                    }
                                } //end for
                            }
                        }
                    } //end else
                }
                , url
            )
        }
    }
};

