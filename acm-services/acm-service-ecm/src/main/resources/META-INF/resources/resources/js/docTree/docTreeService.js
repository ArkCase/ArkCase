/**
 * DocTree.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
DocTree.Service = {
    create : function(args) {
    }
    ,onInitialized: function() {
    }

    ,API_JSON: "/resources/ajax-sub2.json"
    ,API_RETRIEVE_DOCUMENTS_        : "/api/latest/plugin/search/COMPLAINT"
    ,retrieveDocumentsDeferred: function(objType, objId, folderId, callbackSuccess) {
        var url = App.getContextPath() + DocTree.Service.API_RETRIEVE_DOCUMENTS_;
        return Acm.Service.deferredGet(function(data) {

                //save model

//                var rc = [{title: "11",children: [{title: "t11"}, {title: "t12"}]}
//                    ,{title: "22",children: [{title: "t21"}, {title: "t22"}]}];
                var rc = callbackSuccess(data);
                return rc;
            }
            ,url
        );

    }

    ,testService: function(node, parentId, folder) {
        setTimeout(function(){
            var folder = {id: 123, some: "some", value: "value"};
            DocTree.Controller.modelAddedFolder(node, parentId, folder);
        }, 1000);
    }
    ,testService2: function(node, parentId, folder) {
        setTimeout(function(){
            var folder = {id: 123, some: "some", value: "value"};
            DocTree.Controller.modelAddedDocument(node, parentId, folder);
        }, 1000);
    }

    ,Notes: {
        create: function() {
        }
        ,onInitialized: function() {
        }
        ,API_SAVE_NOTE               : "/api/latest/plugin/note"
        ,API_DELETE_NOTE_            : "/api/latest/plugin/note/"
        ,API_LIST_NOTES_             : "/api/latest/plugin/note/"

        ,retrieveNoteListDeferred : function(caseFileId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + DocTree.Service.Notes.API_LIST_NOTES_ + DocTree.Model.DOC_TYPE_CASE_FILE + "/";
                    url += caseFileId;
                    return url;
                }
                ,function(data) {
                    var jtData = null
                    if (DocTree.Model.Notes.validateNotes(data)) {
                        var noteList = data;


                        DocTree.Model.Notes.cacheNoteList.put(caseFileId, noteList);
                        jtData = callbackSuccess(noteList);
                    }
                    return jtData;
                }
            );
        }


        ,saveNote : function(data, handler) {
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        if (handler) {
                            handler(response);
                        } else {
                            DocTree.Controller.modelSavedNote(response);
                        }

                    } else {
                        if (DocTree.Model.Notes.validateNote(response)) {
                            var note = response;
                            var caseFileId = DocTree.Model.getDocTreeId();
                            if (caseFileId == note.parentId) {
                                var noteList = DocTree.Model.Notes.cacheNoteList.get(caseFileId);
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
                                    DocTree.Controller.modelSavedNote(note);
                                }
                            }
                        }
                    }
                }
                ,App.getContextPath() + this.API_SAVE_NOTE
                ,JSON.stringify(data)
            )
        }
        ,addNote: function(note) {
            if (DocTree.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        DocTree.Controller.modelAddedNote(data);
                    }
                );
            }
        }
        ,updateNote: function(note) {
            if (DocTree.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        DocTree.Controller.modelUpdatedNote(data);
                    }
                );
            }
        }

        ,_validateDeletedNote: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedNoteId)) {
                return false;
            }
            return true;
        }
        ,deleteNote : function(noteId) {
            var url = App.getContextPath() + this.API_DELETE_NOTE_ + noteId;

            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        DocTree.Controller.modelDeletedNote(response);

                    } else {
                        if (DocTree.Service.Notes._validateDeletedNote(response)) {
                            var caseFileId = DocTree.Model.getDocTreeId();
                            if (response.deletedNoteId == noteId) {
                                var noteList = DocTree.Model.Notes.cacheNoteList.get(caseFileId);
                                for (var i = 0; i < noteList.length; i++) {
                                    if (noteId == noteList[i].id) {
                                        noteList.splice(i, 1);
                                        DocTree.Controller.modelDeletedNote(Acm.Service.responseWrapper(response, noteId));
                                        return;
                                    }
                                } //end for
                            }
                        }
                    } //end else
                }
                ,url
            )
        }
    }


};

