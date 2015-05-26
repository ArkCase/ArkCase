/**
 * CaseFileSplit.Controller
 *
 * @author jwu
 */
CaseFileSplit.Controller = CaseFileSplit.Controller || {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_RETRIEVED_PARENT_CASE_FILE      : "casefile-split-model-retrieved-parent-case-file"
    ,modelRetrievedParentCaseFile: function(parentCaseFile){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PARENT_CASE_FILE, parentCaseFile);
    }
    ,MODEL_SAVED_SPLITTED_CASE_FILE        : "casefile-split-model-saved-splitted-case-file"
    ,modelSavedSplittedCaseFile: function(splittedCaseFile){
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_SPLITTED_CASE_FILE, splittedCaseFile);
    }
    ,VIEW_CHANGED_DETAIL                   : "casefile-split-view-changed-detail"
    ,viewChangedDetail: function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_DETAIL, caseFileId, details);
    }
    ,MODEL_SAVED_DETAIL                    : "casefile-split-model-saved-detail"
    ,modelSavedDetail : function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DETAIL, caseFileId, details);
    }

    ,MODEL_RETRIEVED_GROUPS                 : "casefile-split-model-retrieved-groups"
    ,modelRetrievedGroups: function(groups) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_GROUPS, groups);
    }
    
    ,MODEL_RETRIEVED_USERS                 : "casefile-split-model-retrieved-users"
    ,modelRetrievedUsers: function(users) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_USERS, users);
    }
    ,MODEL_FOUND_ASSIGNEES                 : "casefile-split-model-found-assignees"
    ,modelFoundAssignees: function(assignees) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_ASSIGNEES, assignees);
    }
    ,VIEW_DELETED_PARTICIPANT              : "casefile-split-view-deleted-participant"           //param: caseFileId, participantId
    ,viewDeletedParticipant: function(caseFileId, participantId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_PARTICIPANT, caseFileId, participantId);
    }
    ,MODEL_DELETED_PARTICIPANT             : "casefile-split-model-deleted-participant"          //param: caseFileId, participantId
    ,modelDeletedParticipant : function(caseFileId, participantId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_PARTICIPANT, caseFileId, participantId);
    }

    ,VIEW_DELETED_PERSON_ASSOCIATION       : "casefile-split-view-deleted-person-association"    //param: caseFileId, personAssociationId
    ,viewDeletedPersonAssociation : function(caseFileId, personAssociationId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_PERSON_ASSOCIATION, caseFileId, personAssociationId);
    }
    ,MODEL_DELETED_PERSON_ASSOCIATION      : "casefile-split-model-deleted-person-association"   //param: caseFileId, personAssociationId
    ,modelDeletedPersonAssociation : function(caseFileId, personAssociationId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_PERSON_ASSOCIATION, caseFileId, personAssociationId);
    }

    ,VIEW_DELETED_NOTE                     : "casefile-split-view-deleted-note"                  //param: noteId
    ,viewDeletedNote: function(noteId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_NOTE, noteId);
    }
    ,MODEL_DELETED_NOTE                    : "casefile-split-model-deleted-note"                 //param: noteId
    ,modelDeletedNote : function(noteId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_NOTE, noteId);
    }

    ,VIEW_CHANGED_CASE_FILE               : "casefile-split-view-changed-case-file"
    ,viewChangedCaseFile: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_CASE_FILE, caseFileId);
    }
};

