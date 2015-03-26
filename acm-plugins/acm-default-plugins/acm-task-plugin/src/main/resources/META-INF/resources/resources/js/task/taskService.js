/**
 * Task.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Task.Service = {
    create : function() {
        if (Task.Service.Lookup.create)              {Task.Service.Lookup.create();}
        if (Task.Service.Action.create)              {Task.Service.Action.create();}
        if (Task.Service.ParentDetail.create)        {Task.Service.ParentDetail.create();}
        if (Task.Service.Detail.create)              {Task.Service.Detail.create();}
        if (Task.Service.Notes.create)               {Task.Service.Notes.create();}
        if (Task.Service.History.create)             {Task.Service.History.create();}
        if (Task.Service.WorkflowOverview.create)    {Task.Service.WorkflowOverview.create();}
        if (Task.Service.Attachments.create)         {Task.Service.Attachments.create();}
        if (Task.Service.DocumentUnderReview.create) {Task.Service.DocumentUnderReview.create();}
        if (Task.Service.RejectComments.create)      {Task.Service.RejectComments.create();}
    }
    ,onInitialized: function() {
        if (Task.Service.Lookup.onInitialized)              {Task.Service.Lookup.onInitialized();}
        if (Task.Service.Action.onInitialized)              {Task.Service.Action.onInitialized();}
        if (Task.Service.ParentDetail.onInitialized)        {Task.Service.ParentDetail.onInitialized();}
        if (Task.Service.Detail.onInitialized)              {Task.Service.Detail.onInitialized();}
        if (Task.Service.Notes.onInitialized)               {Task.Service.Notes.onInitialized();}
        if (Task.Service.History.onInitialized)             {Task.Service.History.onInitialized();}
        if (Task.Service.WorkflowOverview.onInitialized)    {Task.Service.WorkflowOverview.onInitialized();}
        if (Task.Service.Attachments.onInitialized)         {Task.Service.Attachments.onInitialized();}
        if (Task.Service.DocumentUnderReview.onInitialized) {Task.Service.DocumentUnderReview.onInitialized();}
        if (Task.Service.RejectComments.onInitialized)      {Task.Service.RejectComments.onInitialized();}
    }

    ,Lookup: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        //,API_GET_ASSIGNEES             : "/api/latest/users/withPrivilege/acm-complaint-approve"

        ,API_GET_ASSIGNEES             : "/api/latest/plugin/search/USER"

        ,API_GET_PRIORITIES            : "/api/latest/plugin/complaint/priorities"


        ,retrieveAssignees : function() {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelRetrievedAssignees(response);

                    } else {
                        if (Acm.Validator.validateSolrData(response)) {
                            if (Task.Model.Lookup.validateAssignees(response.response.docs)) {
                                var assignees = response.response.docs;
                                Task.Model.Lookup.setAssignees(assignees);
                                Task.Controller.modelRetrievedAssignees(assignees);
                            }
                        }
                    }
                }
                ,App.getContextPath() + Task.Service.Lookup.API_GET_ASSIGNEES
            )
        }

        ,retrievePriorities : function() {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelRetrievedPriorities(response);

                    } else {
                        if (Task.Model.Lookup.validatePriorities(response)) {
                            var priorities = response;
                            Task.Model.Lookup.setPriorities(priorities);
                            Task.Controller.modelRetrievedPriorities(priorities);
                        }
                    }
                }
                ,App.getContextPath() + Task.Service.Lookup.API_GET_PRIORITIES
            )
        }
    }


    ,Action: {
        create: function() {
        }
        ,onInitialized: function() {
        }
    }

    ,ParentDetail: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_RETRIEVE_COMPLAINT_        : "/api/latest/plugin/complaint/byId/"
        ,API_RETRIEVE_CASE_FILE_        : "/api/latest/plugin/casefile/byId/"

        ,retrieveComplaint : function(objId) {
            var url = App.getContextPath() + this.API_RETRIEVE_COMPLAINT_ + objId;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelRetrievedParentObjectError(response);

                    } else {
                        if (Task.Model.ParentDetail.validateUnifiedData(response)) {
                            var complaint = response;
                            var unifiedData = Task.Model.ParentDetail.makeUnifiedData(complaint, Task.Model.DOC_TYPE_COMPLAINT);
                            if (unifiedData) {
                                Task.Model.ParentDetail.cacheParentObject.put(objId, unifiedData);
                                Task.Controller.modelRetrievedParentObject(unifiedData);
                            }
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveCaseFile : function(objId) {
            var url = App.getContextPath() + this.API_RETRIEVE_CASE_FILE_ + objId;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelRetrievedParentObjectError(response);

                    } else {
                        if (Task.Model.ParentDetail.validateUnifiedData(response)) {
                            var caseFile = response;
                            var unifiedData = Task.Model.ParentDetail.makeUnifiedData(caseFile, Task.Model.DOC_TYPE_CASE_FILE);
                            if (unifiedData) {
                                Task.Model.ParentDetail.cacheParentObject.put(objId, unifiedData);
                                Task.Controller.modelRetrievedParentObject(unifiedData);
                            }
                        }
                    }
                }
                ,url
            )
        }

//        ,apiRetrieveParentObject: function(objType, objId) {
//            if(Task.Model.DOC_TYPE_COMPLAINT == objType){
//                return "/api/latest/plugin/complaint/byId/" + objId;
//            } else if(Task.Model.DOC_TYPE_CASE_FILE == objType){
//                return "/api/latest/plugin/casefile/byId/" + objId;
//            }
//        }
//
//        ,retrieveParentObject : function(objType, objId) {
//            var url = App.getContextPath() + Task.Service.ParentDetail.apiRetrieveParentObject(objType, objId);
//            Acm.Service.asyncGet(
//                function(response) {
//                    if (response.hasError) {
//                        Task.Controller.modelRetrievedParentObjectError(response);
//
//                    } else {
//                        if (Task.Model.ParentDetail.validateUnifiedData(response)) {
//                            var parentObj = response;
//                            var topBarParentObjData = Task.Model.ParentDetail.makeUnifiedParentData(parentObj,objType);
//                            if (topBarParentObjData) {
//                                Task.Model.ParentDetail.cacheParentObject.put(objId, topBarParentObjData);
//                                Task.Controller.modelRetrievedParentObject(topBarParentObjData);
//                            }
//                        }
//                    }
//                }
//                ,url
//            )
//        }
    }

    ,Detail: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_COMPLETE_TASK         : "/api/latest/plugin/task/completeTask"
        ,API_DELETE_TASK           : "/api/latest/plugin/task/deleteTask/"
        ,API_RETRIEVE_USERS        : "/api/latest/plugin/search/usersSearch"

        ,saveDetail: function(nodeType, taskId, details) {
            var task = Task.Model.findTask(nodeType, taskId);
            if (Task.Model.interface.validateObjData(task)) {
                task.details = details;
                ObjNav.Service.Detail.saveObject(nodeType, taskId
                    ,task
                    ,function(data) {
                        Task.Controller.modelSavedDetail(nodeType, taskId, Acm.Service.responseWrapper(data, data.details));
                    }
                );
            }
        }
        ,saveReworkDetails: function(nodeType, taskId, reworkDetails) {
            var task = Task.Model.findTask(nodeType, taskId);
            if (Task.Model.interface.validateObjData(task)) {
                task.reworkInstructions = reworkDetails;
                ObjNav.Service.Detail.saveObject(nodeType, taskId
                    ,task
                    ,function(data) {
                        Task.Controller.modelSavedReworkDetails(nodeType, taskId, Acm.Service.responseWrapper(data, data.reworkInstructions));
                    }
                );
            }
        }
        ,saveTitle: function(nodeType, taskId, title) {
            var task = Task.Model.findTask(nodeType, taskId);
            if (Task.Model.interface.validateObjData(task)) {
                task.title = title;
                ObjNav.Service.Detail.saveObject(nodeType, taskId
                    ,task
                    ,function(data) {
                        Task.Controller.modelSavedTitle(nodeType, taskId, Acm.Service.responseWrapper(data, data.title));
                    }
                );
            }
        }
        ,saveStartDate: function(nodeType, taskId, startDate) {
            var task = Task.Model.findTask(nodeType, taskId);
            if (Task.Model.interface.validateObjData(task)) {
                task.incidentDate = startDate;
                ObjNav.Service.Detail.saveObject(nodeType, taskId
                    ,task
                    ,function(data) {
                        Task.Controller.modelSavedStartDate(nodeType, taskId, Acm.Service.responseWrapper(data, data.startDate));
                    }
                );
            }
        }
        ,saveAssignee: function(nodeType, taskId, assignee) {
            var task = Task.Model.findTask(nodeType, taskId);
            if (Task.Model.interface.validateObjData(task)) {
                task.assignee = assignee
                ObjNav.Service.Detail.saveObject(nodeType, taskId
                    ,task
                    ,function(data) {
                        Task.Controller.modelSavedAssignee(nodeType, taskId, Acm.Service.responseWrapper(data, data.assignee));
                    }
                );
            }
        }
        ,savePercentCompleted: function(nodeType, taskId, percent) {
            var task = Task.Model.findTask(nodeType, taskId);
            if (Task.Model.interface.validateObjData(task)) {
                task.percentComplete = percent;
                ObjNav.Service.Detail.saveObject(nodeType, taskId
                    ,task
                    ,function(data) {
                        Task.Controller.modelSavedPercentCompleted(nodeType, taskId, Acm.Service.responseWrapper(data, data.percentComplete));
                    }
                );
            }
        }
        ,savePriority: function(nodeType, taskId, priority) {
            var task = Task.Model.findTask(nodeType, taskId);
            if (Task.Model.interface.validateObjData(task)) {
                task.priority = priority;
                ObjNav.Service.Detail.saveObject(nodeType, taskId
                    ,task
                    ,function(data) {
                        Task.Controller.modelSavedPriority(nodeType, taskId, Acm.Service.responseWrapper(data, data.priority));
                    }
                );
            }
        }
        ,saveDueDate: function(nodeType, taskId, dueDate) {
            var task = Task.Model.findTask(nodeType, taskId);
            if (Task.Model.interface.validateObjData(task)) {
                task.dueDate = dueDate;
                ObjNav.Service.Detail.saveObject(nodeType, taskId
                    ,task
                    ,function(data) {
                        Task.Controller.modelSavedDueDate(nodeType, taskId, Acm.Service.responseWrapper(data, data.dueDate));
                    }
                );
            }
        }
        ,completeTask : function(task) {
            var data;
            var url = App.getContextPath() + this.API_COMPLETE_TASK;
            if (Task.Model.interface.validateObjData(task)) {
                data = task;

            }
            else{
                var taskId = ObjNav.Model.getObjectId();
                if(Acm.isNotEmpty(taskId) && taskId > 0){
                    url = App.getContextPath() + this.API_COMPLETE_TASK + "/" + taskId;
                }
                data = {};
            }
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelCompletedTask(response);
                    } else {
                        if (Task.Model.interface.validateObjData(response)) {
                            Task.Controller.modelCompletedTask(response);
                        }
                    }
                }
                ,url
                ,JSON.stringify(data)
            )
        }
        ,deleteTask : function(taskId) {
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelDeletedTask(response);
                    } else {
                        if (Task.Model.interface.validateObjData(response)) {
                            Task.Controller.modelDeletedTask(response);
                        }
                    }
                }
                ,App.getContextPath() + this.API_DELETE_TASK + taskId
                ,"{}"
            )
        }

        ,retrieveUsers : function(start, n, sortDirection, searchKeyword, exclude) {
            var params = {'start': start, 'n': n, 'sortDirection': sortDirection, 'searchKeyword': searchKeyword, 'exclude': exclude}
            var query = $.param(params);

            var url = App.getContextPath() + Task.Service.Detail.API_RETRIEVE_USERS + '?' + query;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Task.Controller.modelRetrievedUsers(response);

                    } else {
                        //if (Task.Model.interface.validateParentObjData(response)) {
                        Task.Controller.modelRetrievedUsers(response);
                        //}
                    }
                }
                ,url
            )
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
                        Task.Model.History.cacheHistory.put(taskId + "." +jtParams.jtStartIndex, history);
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

        ,API_UPLOAD_FILE            : "/api/latest/service/ecm/upload"
        ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"
        ,API_RETRIEVE_DOCUMENTS_      : "/api/latest/service/ecm/folder/"

        ,retrieveDocumentsDeferred : function(taskId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + Task.Service.Attachments.API_RETRIEVE_DOCUMENTS_ + Task.Model.DOC_TYPE_TASK;
                    url += "/" + taskId;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (Task.Model.Attachments.validateDocuments(data)) {
                        var documents = data;
                        Task.Model.Attachments.cacheAttachments.put(taskId + "." +jtParams.jtStartIndex, documents);
                        jtData = callbackSuccess(documents);
                    }
                    return jtData;
                }
            );
        }

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
                        // the upload returns an array of documents, for when the user uploads multiple files.
                        if (Task.Model.Attachments.validateNewDocument(response)) {
                            //add to the top of the cache
                            var taskId = Task.View.getActiveTask().taskId;
                            var documentsCache = Task.Model.Attachments.cacheAttachments.get(taskId + "." + 0);
                            if(Task.Model.Attachments.validateDocuments(documentsCache)) {
                                var documents = documentsCache.children;
                                for ( var a = 0; a < response.length; a++ )
                                {
                                    var f = response[a];
                                    var doc = {};
                                    doc.objectId = Acm.goodValue(f.fileId);
                                    doc.name = Acm.goodValue(f.fileName);
                                    doc.creator = Acm.goodValue(f.creator);
                                    doc.created = Acm.goodValue(f.created);
                                    doc.objectType = Task.Model.DOC_TYPE_FILE_SM;
                                    doc.category = Task.Model.DOC_CATEGORY_FILE_SM;
                                    documentsCache.children.unshift(doc);
                                    documents.totalChildren++;
                                }
                            }
                            Task.Controller.modelUploadedAttachments(response);
                        }
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

