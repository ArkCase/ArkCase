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

    ,VIEW_ADDED_NOTE                       : "document-view-added-note"                    //param: note,documentId
    ,viewAddedNote: function(note,documentId) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_NOTE, note,documentId);
    }
    ,VIEW_UPDATED_NOTE                     : "document-view-updated-note"                  //param: note,documentId
    ,viewUpdatedNote: function(note,documentId) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_NOTE, note,documentId);
    }
    ,VIEW_DELETED_NOTE                     : "document-view-deleted-note"                  //param: noteId,documentId
    ,viewDeletedNote: function(noteId,documentId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_NOTE, noteId,documentId);
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

    //Tags

    ,VIEW_REMOVED_ASSOCIATED_TAG            : "document-view-removed-associated-tag"         //param: documentId,associatedTagId
    ,viewRemovedAssociatedTag : function(documentId, associatedTagId){
        Acm.Dispatcher.fireEvent(this.VIEW_REMOVED_ASSOCIATED_TAG, documentId, associatedTagId);
    }
    ,MODEL_REMOVED_ASSOCIATED_TAGS          : "document-model-removed-associated-tags"      //param: associatedTags
    ,modelRemovedAssociatedTag : function(associatedTags){
        Acm.Dispatcher.fireEvent(this.MODEL_REMOVED_ASSOCIATED_TAGS, associatedTags);
    }
    ,MODEL_RETRIEVED_ASSOCIATED_TAGS        : "document-model-retrieved-associated-tags"    //param: associatedTags
    ,modelRetrievedAssociatedTags : function(associatedTags){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_ASSOCIATED_TAGS, associatedTags);
    }
    ,MODEL_RETRIEVED_ALL_TAGS                   : "document-model-retrieved-all-tags"    //param: allTags
    ,modelRetrievedAllTags : function(allTags){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_ALL_TAGS, allTags);
    }
    ,VIEW_ASSOCIATED_NEW_TAG                    : "document-view-associated-new-tag"    //param: tagId
    ,viewAssociatedNewTag : function(tagId){
        Acm.Dispatcher.fireEvent(this.VIEW_ASSOCIATED_NEW_TAG, tagId);
    }
    ,MODEL_ASSOCIATED_NEW_TAG                   : "document-model-associated-new-tag"    //param: associatedTags
    ,modelAssociatedNewTag : function(associatedTags){
        Acm.Dispatcher.fireEvent(this.MODEL_ASSOCIATED_NEW_TAG, associatedTags);
    }
    ,VIEW_CREATED_NEW_TAG                       : "document-view-created-new-tag"       //param: newTagName, newTagDesc, newTagText
    ,viewCreatedNewTag : function(newTagName, newTagDesc, newTagText){
        Acm.Dispatcher.fireEvent(this.VIEW_CREATED_NEW_TAG, newTagName, newTagDesc, newTagText);
    }
    ,MODEL_CREATED_NEW_TAG                      : "document-model-created-new-tag"      //param: newTag
    ,modelCreatedNewTag : function(newTag){
        Acm.Dispatcher.fireEvent(this.MODEL_CREATED_NEW_TAG, newTag);
    }


    //Participants

    ,VIEW_REMOVED_PARTICIPANT              : "document-view-removed-participant"            //param: participantId, userId, participantType,documentId
    ,viewRemovedParticipant : function(participantId, userId, participantType,documentId){
        Acm.Dispatcher.fireEvent(this.VIEW_REMOVED_PARTICIPANT, participantId, userId, participantType,documentId);
    }
    ,MODEL_REMOVED_PARTICIPANT             : "document-model-removed-participant"           //param: participants
    ,modelRemovedParticipant : function(participant){
        Acm.Dispatcher.fireEvent(this.MODEL_REMOVED_PARTICIPANT, participant);
    }
    ,MODEL_RETRIEVED_PARTICIPANTS           : "document-model-retrieved-participants"         //param: participants
    ,modelRetrievedParticipants : function(participants){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PARTICIPANTS, participants);
    }
    ,VIEW_CHANGED_PARTICIPANT_ROLE          : "document-view-changed-participant-role"          //param: participantType,participantId
    ,viewChangedParticipantRole : function(participantType, participantId, documentId){
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_PARTICIPANT_ROLE, participantType, participantId, documentId);
    }
    ,MODEL_CHANGED_PARTICIPANT_ROLE          : "document-model-changed-participant-role"          //param: participants
    ,modelChangedParticipantRole : function(participants){
        Acm.Dispatcher.fireEvent(this.MODEL_CHANGED_PARTICIPANT_ROLE, participants);
    }
    ,VIEW_ADDED_NEW_PARTICIPANT              : "document-view-added-new-participant"             //param: userId, participantType, documentId
    ,viewAddedNewParticipant : function(userId, participantType, documentId){
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_NEW_PARTICIPANT, userId, participantType, documentId);
    }
    ,MODEL_ADDED_NEW_PARTICIPANT              : "document-model-added-new-participant"            //param: userId, participantType, documentId
    ,modelAddedNewParticipant : function(userId, participantType, documentId){
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_NEW_PARTICIPANT, userId, participantType, documentId);
    }

    //Document Detail
    ,MODEL_RETRIEVED_DOCUMENT_DETAIL            : "document-model-retrieved-document-detail"      //param: documentDetail
    ,modelRetrievedDocumentDetail: function(documentDetail){
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_DOCUMENT_DETAIL, documentDetail);
    }
};

