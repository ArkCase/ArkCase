/**
 * Task.Controller
 *
 * @author jwu
 */
Task.Controller = Task.Controller || {
    create : function() {
    }
    ,onInitialized: function() {
    }

//    ,VIEW_SELECTED_TASK                             : "task-view-selected-task"                                 //param : task

    ,VIEW_ADDED_NOTE                                : "task-view-added-note"                                    //param : note
    ,VIEW_UPDATED_NOTE                              : "task-view-updated-note"                                  //param : note
    ,VIEW_DELETED_NOTE                              : "task-view-deleted-note"                                  //param : deletedNoteId

    ,VIEW_CHANGED_DETAIL                            : "task-view-changed-detail"                                //param : taskDetail

    ,MODEL_SAVED_DETAIL                             : "task-model-changed-detail"                               //param : taskDetail

    //,MODEL_RETRIEVED_TASK                           : "task-model-retrieved-task"                               //param : task
    //,MODEL_SAVED_TASK                               : "task-model-saved-task"                                   //param : task

    ,MODEL_SAVED_NOTE                               : "task-model-saved-note"                                   //param : note
    ,MODEL_ADDED_NOTE                               : "task-model-added-note"                                   //param : note
    ,MODEL_UPDATED_NOTE                             : "task-model-updated-note"                                 //param : note
    ,MODEL_DELETED_NOTE                             : "task-model-deleted-note"                                 //param : deletedNote

    ,MODEL_RETRIEVED_WORKFLOW_OVERVIEW              : "task-model-retrieved-workflow-overview"                  //param : workflowOverview

    ,MODEL_UPLOADED_ATTACHMENTS                     : "task-model-uploaded-attachments"                         //param : attachments

    ,MODEL_RETRIEVED_REJECT_COMMENTS                : "task-model-retrieved-reject-comments"                    //param : rejectComments


    ,viewAddedNote: function(note){
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_NOTE, note);
    }

    ,viewUpdatedNote: function(note){
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_NOTE, note);
    }

    ,viewDeletedNote: function(note){
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_NOTE, note);
    }

    ,viewChangedDetail: function(nodeType, nodeId, details) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_DETAIL, nodeType, nodeId, details);
    }

    ,modelSavedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_NOTE, note);
    }

    ,modelAddedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_NOTE, note);
    }

    ,modelUpdatedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_NOTE, note);
    }

    ,modelDeletedNote: function(deletedNote) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_NOTE, deletedNote);
    }

    ,modelRetrievedWorkflowOverview: function(workflowOverview) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_WORKFLOW_OVERVIEW, workflowOverview);
    }

    ,modelUploadedAttachments: function(attachments) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPLOADED_ATTACHMENTS, attachments);
    }

    ,modelSavedDetail: function(taskId, details) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DETAIL, taskId, details);
    }

//    ,modelSavedTask: function(task) {
//        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_TASK, task);
//    }

    ,modelRetrievedRejectComments: function(rejectComments) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_REJECT_COMMENTS, rejectComments);
    }

};

