/**
 * Task.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Task.Service = {
    create : function() {
    }
    ,onInitialized: function() {
    }


    ,Detail: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,saveDetail: function(nodeType, taskId, details) {
            var task = Task.Model.findTask(nodeType, taskId);
            if (Task.Model.interface.validateObjData(task)) {
                task.details = details;
                ObjNav.Service.Detail.saveObject(nodeType, taskId
                    ,task
                    ,function(data) {
                        Task.Controller.modelSavedDetail(taskId, Acm.Service.responseWrapper(data, data.details));
                    }
                );
            }
        }
    }

    ,Notes: {
        create: function() {
        }
        ,onInitialized: function() {
        }
        ,API_SAVE_NOTE               : "/api/latest/plugin/note"
        ,API_DELETE_NOTE_            : "/api/latest/plugin/note/"
        ,API_LIST_NOTES_             : "/api/latest/plugin/note/"

        ,retrieveNoteListDeferred : function(taskId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + Task.Service.Notes.API_LIST_NOTES_ + Task.Model.DOC_TYPE_TASK + "/";
                    url += taskId;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (Task.Model.Notes.validateNotes(data)) {
                        var noteList = data;
                        Task.Model.Notes.cacheNoteList.put(taskId, noteList);
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
                            Task.Controller.modelSavedNote(response);
                        }

                    } else {
                        if (Task.Model.Notes.validateNote(response)) {
                            var note = response;
                            var taskId = ObjNav.Model.getObjectId();

                            if (taskId == note.parentId) {
                                var noteList = Task.Model.Notes.cacheNoteList.get(taskId);
                                if(Acm.isNotEmpty(noteList)){
                                    var found = -1;
                                    for (var i = 0; i < noteList.length; i++) {
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
                                    Task.Controller.modelSavedNote(note);
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
            if (Task.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        Task.Controller.modelAddedNote(data);
                    }
                );
            }
        }
        ,updateNote: function(note) {
            if (Task.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        Task.Controller.modelUpdatedNote(data);
                    }
                );
            }
        }

        ,deleteNote : function(noteId) {
            var url = App.getContextPath() + this.API_DELETE_NOTE_ + noteId;

            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelDeletedNote(response);

                    } else {
                        if (Task.Model.Notes.validateDeletedNote(response)) {
                            var taskId = ObjNav.Model.getObjectId();
                            var deletedNote = response;
                            if (deletedNote.deletedNoteId == noteId) {
                                var notes = Task.Model.Notes.cacheNoteList.get(taskId);
                                if (Task.Model.Notes.validateNotes(notes)) {
                                    for (var i = 0; i < notes.length; i++) {
                                        if (noteId == notes[i].id) {
                                            notes.splice(i, 1);
                                            Task.Controller.modelDeletedNote(Acm.Service.responseWrapper(deletedNote, noteId));
                                            return;
                                        }
                                    } //end for
                                }
                            }
                        }
                    } //end else
                }
                ,url
            )
        }
    }

    ,History: {
        create: function() {
        }
        ,onInitialized: function() {
        }
        ,API_TASK_HISTORY : "/api/latest/plugin/task/events/"

        ,retrieveHistoryDeferred : function(taskId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + Task.Service.History.API_TASK_HISTORY;
                    url += taskId;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (Task.Model.History.validateHistory(data)) {
                        var history = data;
                        Task.Model.History.cacheHistory.put(taskId, history);
                        jtData = callbackSuccess(history);
                    }
                    return jtData;
                }
            );
        }
    }

    ,WorkflowOverview: {
        create: function() {
        }
        ,onInitialized: function() {
        }
        ,API_RETRIEVE_WORKFLOW_HISTORY       : "/api/latest/plugin/task/history/"

        ,retrieveWorkflowOverview : function(queryId,taskId,adHoc_b) {
            var url = App.getContextPath() + this.API_RETRIEVE_WORKFLOW_HISTORY;
            url += queryId;
            url += "/";
            url += adHoc_b;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelRetrievedWorkflowOverview(response);
                    } else {
                        if (Task.Model.WorkflowOverview.validateWorkflowOverview(response)) {
                            var workflowOverview = response;
                            Task.Model.WorkflowOverview.cacheWorkflowOverview.put(taskId, workflowOverview);
                            Task.Controller.modelRetrievedWorkflowOverview(workflowOverview);
                        }
                    }
                }
                ,url
            )
        }
    }

    ,Attachments: {
        create: function(){
        }
        ,onInitialized: function(){
        }

        ,API_UPLOAD_FILE            : "/api/latest/plugin/task/file"
        ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"

        ,uploadAttachments: function(formData) {
            var url = App.getContextPath() + this.API_UPLOAD_FILE;
            Acm.Service.ajax({
                url: url
                ,data: formData
                ,processData: false
                ,contentType: false
                ,type: 'POST'
                ,success: function(response){
                    if (response.hasError) {
                        Task.Controller.modelUploadedAttachments(response);
                    } else {
                        if(Task.Model.Attachments.validateUploadedAttachments(response)){
                            var task = Task.Model.getObject();
                            if(Task.Model.Attachments.validateExistingAttachments(task)){
                                for(var i = 0; i < response.length; i++){
                                    var attachment = {};
                                    attachment.targetId = response[i].files[0].id;
                                    attachment.targetName = response[i].files[0].name;
                                    attachment.status = response[i].files[0].status;
                                    attachment.creator = response[i].files[0].creator;
                                    attachment.created = response[i].files[0].created;
                                    task.childObjects.push(attachment);
                                }
                            }
                        }
                        Task.Controller.modelUploadedAttachments(task);
                    }
                }
            });
        }
    }

    ,DocumentUnderReview: {
        create: function(){
        }
        ,onInitialized: function(){
        }

        ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"
    }

    ,RejectComments: {
        create: function(){
        }
        ,onInitialized: function(){
        }

        ,API_RETRIEVE_REJECT_COMMENTS      : "/api/latest/plugin/note/"

        ,retrieveRejectComments : function(rejectNoteType, parentId, parentType) {
            var url = App.getContextPath() + this.API_RETRIEVE_REJECT_COMMENTS;
            url += parentType;
            url += "/" + parentId;
            url += "?type=" + rejectNoteType
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelRetrievedRejectComments(response);
                    } else {
                        if (Task.Model.RejectComments.validateRejectComments(response)) {
                            var rejectComments = response;
                            var taskId = ObjNav.Model.getObjectId();
                            Task.Model.RejectComments.cacheRejectComments.put(taskId, rejectComments);
                            Task.Controller.modelRetrievedRejectComments(rejectComments);
                        }
                    }
                }
                ,url
            )
        }
    }

};

