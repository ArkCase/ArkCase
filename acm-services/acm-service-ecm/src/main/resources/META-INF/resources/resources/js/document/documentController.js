/**
 * AcmDocument.Controller
 *
 * @author jwu
 */
AcmDocument.Controller = AcmDocument.Controller || {
    create : function() {
    }
    ,onInitialized: function() {
    }

    //Lookup

    ,MODEL_FOUND_ASSIGNEES                 : "document-model-found-assignees"
    ,modelFoundAssignees: function(assignees) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_ASSIGNEES, assignees);                //param: assignees
    }
    ,MODEL_FOUND_SUBJECT_TYPES             : "document-model-found-subject-types"
    ,modelFoundSubjectTypes: function(subjectTypes) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_SUBJECT_TYPES, subjectTypes);         //param: subjectTypes
    }
    ,MODEL_FOUND_PRIORITIES                : "document-model-found-priorities"
    ,modelFoundPriorities: function(priorities) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_PRIORITIES, priorities);              //param: priorities
    }

    //ECM File

    ,VIEW_REPLACED_FILE                    : "document-model-replaced-file"                //param: ..
    ,viewReplacedFile: function(fileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_REPLACED_FILE, fileId);
    }
    ,VIEW_COPIED_FILE                      : "document-model-copied-file"                  //param: ..
    ,viewCopiedFile: function(fileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_COPIED_FILE, fileId);
    }
    ,VIEW_DELETED_FILE                     : "document-model-deleted-file"                 //param: ..
    ,viewDeletedFile: function(fileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_FILE, fileId);
    }
    ,VIEW_MOVED_FILE                       : "document-model-moved-file"                   //param: ..
    ,viewMovedFile: function(fileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_MOVED_FILE, fileId);
    }

    ,MODEL_REPLACED_FILE                   : "document-model-replaced-file"                //param: ..
    ,modelReplacedFile: function(fileId) {
        Acm.Dispatcher.fireEvent(this.MODEL_REPLACED_FILE, fileId);
    }
    ,MODEL_COPIED_FILE                     : "document-model-copied-file"                  //param: ..
    ,modelCopiedFile: function(fileId) {
        Acm.Dispatcher.fireEvent(this.MODEL_REPLACED_FILE, fileId);
    }
    ,MODEL_MOVED_FILE                      : "document-model-moved-file"                   //param: ..
    ,modelMovedFile: function(fileId) {
        Acm.Dispatcher.fireEvent(this.MODEL_MOVED_FILE, fileId);
    }
    ,MODEL_DELETED_FILE                    : "document-model-deleted-file"                 //param: ..
    ,modelDeletedFile: function(fileId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_FILE, fileId);
    }

    //Notes

    ,VIEW_ADDED_NOTE                       : "document-view-added-note"                    //param: note
    ,viewAddedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_NOTE, note);
    }
    ,VIEW_UPDATED_NOTE                     : "document-view-updated-note"                  //param: note
    ,viewUpdatedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_NOTE, note);
    }
    ,VIEW_DELETED_NOTE                     : "document-view-deleted-note"                  //param: noteId
    ,viewDeletedNote: function(noteId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_NOTE, noteId);
    }

    ,MODEL_SAVED_NOTE                      : "document-model-saved-note"                   //param: note
    ,modelSavedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_NOTE, note);
    }
    ,MODEL_ADDED_NOTE                      : "document-model-added-note"                   //param: note
    ,modelAddedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_NOTE, note);
    }
    ,MODEL_UPDATED_NOTE                    : "document-model-updated-note"                 //param: note
    ,modelUpdatedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_NOTE, note);
    }
    ,MODEL_DELETED_NOTE                    : "document-model-deleted-note"                 //param: noteId
    ,modelDeletedNote : function(noteId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_NOTE, noteId);
    }
};

