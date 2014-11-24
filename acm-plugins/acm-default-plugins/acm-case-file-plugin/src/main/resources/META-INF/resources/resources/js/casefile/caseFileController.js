/**
 * CaseFile.Controller
 *
 * @author jwu
 */
CaseFile.Controller = {
    create : function() {
    }
    ,initialize: function() {
    }

    ,ME_ASSIGNEES_FOUND                 : "case-model-assignees"                    //param: assignees
    ,ME_SUBJECT_TYPES_FOUND             : "case-model-subject-types"                //param: subjectTypes
    ,ME_PRIORITIES_FOUND                : "case-model-priorities"                   //param: priorities

    ,ME_CASE_FILE_LIST_RETRIEVED        : "case-model-case-file-list-retrieved"     //param: key
    ,ME_CASE_FILE_RETRIEVED             : "case-model-detail-retrieved"             //param: caseFile
    ,ME_CASE_FILE_SAVED                 : "case-model-detail-saved"                 //param: caseFile

    ,ME_CASE_TITLE_SAVED                : "case-model-case-title-saved"             //param: caseFileId, caseTitle
    ,ME_INCIDENT_DATE_SAVED             : "case-model-incident-date-saved"          //param: caseFileId, incidentDate
    ,ME_ASSIGNEE_SAVED                  : "case-model-assignee-saved"               //param: caseFileId, assignee
    ,ME_SUBJECT_TYPE_SAVED              : "case-model-subject-type-saved"           //param: caseFileId, caseType
    ,ME_PRIORITY_SAVED                  : "case-model-priority-saved"               //param: caseFileId, priority
    ,ME_DUE_DATE_SAVED                  : "case-model-due-date-saved"               //param: caseFileId, dueDate
    ,ME_DETAIL_SAVED                    : "case-model-detail-saved"                 //param: caseFileId, details
    ,ME_CHILD_OBJECT_SAVED              : "case-model-childObject-saved"            //param: caseFileId, childObject
    ,ME_PARTICIPANT_ADDED               : "case-model-participant-added"            //param: caseFileId, participant
    ,ME_PARTICIPANT_UPDATED             : "case-model-participant-updated"          //param: caseFileId, participant
    ,ME_PARTICIPANT_DELETED             : "case-model-participant-deleted"          //param: caseFileId, participantId
    ,ME_PERSON_ASSOCIATION_ADDED        : "case-model-person-association-added"     //param: caseFileId, personAssociation
    ,ME_PERSON_ASSOCIATION_UPDATED      : "case-model-person-association-updated"   //param: caseFileId, personAssociation
    ,ME_PERSON_ASSOCIATION_DELETED      : "case-model-person-association-deleted"   //param: caseFileId, personAssociationId
    ,ME_CONTACT_METHOD_ADDED        : "case-model-contact-method-added"     //param: caseFileId, personAssociationId, contactMethod
    ,ME_CONTACT_METHOD_UPDATED      : "case-model-contact-method-updated"   //param: caseFileId, personAssociationId, contactMethod
    ,ME_CONTACT_METHOD_DELETED      : "case-model-contact-method-deleted"   //param: caseFileId, personAssociationId, contactMethodId

    ,ME_NOTE_SAVED                      : "case-model-note-saved"                   //param: note
    ,ME_NOTE_ADDED                      : "case-model-note-added"                   //param: note
    ,ME_NOTE_UPDATED                    : "case-model-note-updated"                 //param: note
    ,ME_NOTE_DELETED                    : "case-model-note-deleted"                 //param: noteId

    ,VE_PREV_PAGE_CLICKED               : "case-view-prev-page-clicked"             //param: none
    ,VE_NEXT_PAGE_CLICKED		        : "case-view-next-page-clicked"             //param: none
    ,VE_CASE_FILE_SELECTED		        : "case-view-case-selected"                 //param: caseFileId
    ,VE_TREE_NODE_SELECTED		        : "case-view-tree-node-selected"            //param: node key

    ,VE_CASE_TITLE_CHANGED              : "case-view-case-title-changed"            //param: caseFileId, title
    ,VE_INCIDENT_DATE_CHANGED           : "case-view-incident-date-changed"         //param: caseFileId, incidentDate
    ,VE_ASSIGNEE_CHANGED                : "case-view-assignee-changed"              //param: caseFileId, assignee
    ,VE_SUBJECT_TYPE_CHANGED            : "case-view-subject-type-changed"          //param: caseFileId, caseType
    ,VE_PRIORITY_CHANGED                : "case-view-priority-changed"              //param: caseFileId, priority
    ,VE_DUE_DATE_CHANGED                : "case-view-due-date-changed"              //param: caseFileId, dueDate
    ,VE_DETAIL_CHANGED                  : "case-view-detail-changed"                //param: caseFileId, details

    ,VE_CASE_FILE_CLOSED                : "case-view-case-closed"                   //param: caseFileId
    ,VE_DOCUMENT_ADDED                  : "case-view-document-added"                //param: caseFileId
    ,VE_CHILD_OBJECT_CHANGED            : "case-view-child-object-changed"          //param: caseFileId, childObject
    ,VE_PARTICIPANT_ADDED               : "case-view-participant-added"             //param: caseFileId, participant
    ,VE_PARTICIPANT_UPDATED             : "case-view-participant-updated"           //param: caseFileId, participant
    ,VE_PARTICIPANT_DELETED             : "case-view-participant-deleted"           //param: caseFileId, participantId
    ,VE_PERSON_ASSOCIATION_ADDED        : "case-view-person-association-added"      //param: caseFileId, personAssociation
    ,VE_PERSON_ASSOCIATION_UPDATED      : "case-view-person-association-updated"    //param: caseFileId, personAssociation
    ,VE_PERSON_ASSOCIATION_DELETED      : "case-view-person-association-deleted"    //param: caseFileId, personAssociationId
    ,VE_CONTACT_METHOD_ADDED        : "case-view-contact-method-added"      //param: caseFileId, personAssociationId, contactMethod
    ,VE_CONTACT_METHOD_UPDATED      : "case-view-contact-method-updated"    //param: caseFileId, personAssociationId, contactMethod
    ,VE_CONTACT_METHOD_DELETED      : "case-view-contact-method-deleted"    //param: caseFileId, personAssociationId, contactMethodId

    ,VE_NOTE_ADDED                      : "case-view-note-added"                    //param: note
    ,VE_NOTE_UPDATED                    : "case-view-note-updated"                  //param: note
    ,VE_NOTE_DELETED                    : "case-view-note-deleted"                  //param: noteId


    ,modelFoundAssignees: function(assignees) {
        Acm.Dispatcher.fireEvent(this.ME_ASSIGNEES_FOUND, assignees);
    }
    ,modelFoundSubjectTypes: function(subjectTypes) {
        Acm.Dispatcher.fireEvent(this.ME_SUBJECT_TYPES_FOUND, subjectTypes);
    }
    ,modelFoundPriorities: function(priorities) {
        Acm.Dispatcher.fireEvent(this.ME_PRIORITIES_FOUND, priorities);
    }
    ,modelRetrievedCaseFileList: function(key) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_FILE_LIST_RETRIEVED, key);
    }
    ,modelRetrievedCaseFile: function(caseFile) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_FILE_RETRIEVED, caseFile);
    }
    ,modelSavedCaseFile : function(caseFile) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_FILE_SAVED, caseFile);
    }
    ,modelSavedCaseTitle : function(caseFileId, title) {
        Acm.Dispatcher.fireEvent(this.ME_CASE_TITLE_SAVED, caseFileId, title);
    }
    ,modelSavedIncidentDate : function(caseFileId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.ME_INCIDENT_DATE_SAVED, caseFileId, incidentDate);
    }
    ,modelSavedAssignee : function(caseFileId, assignee) {
        Acm.Dispatcher.fireEvent(this.ME_ASSIGNEE_SAVED, caseFileId, assignee);
    }
    ,modelSavedSubjectType : function(caseFileId, caseType) {
        Acm.Dispatcher.fireEvent(this.ME_SUBJECT_TYPE_SAVED, caseFileId, caseType);
    }
    ,modelSavedPriority : function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.ME_PRIORITY_SAVED, caseFileId, priority);
    }
    ,modelSavedDueDate : function(caseFileId, dueDate) {
        Acm.Dispatcher.fireEvent(this.ME_DUE_DATE_SAVED, caseFileId, dueDate);
    }
    ,modelSavedDetail : function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.ME_DETAIL_SAVED, caseFileId, details);
    }
    ,modelSavedChildObject : function(caseFileId, childObject) {
        Acm.Dispatcher.fireEvent(this.ME_CHILD_OBJECT_SAVED, caseFileId, childObject);
    }
    ,modelAddedParticipant : function(caseFileId, participant) {
        Acm.Dispatcher.fireEvent(this.ME_PARTICIPANT_ADDED, caseFileId, participant);
    }
    ,modelUpdatedParticipant : function(caseFileId, participant) {
        Acm.Dispatcher.fireEvent(this.ME_PARTICIPANT_UPDATED, caseFileId, participant);
    }
    ,modelDeletedParticipant : function(caseFileId, participantId) {
        Acm.Dispatcher.fireEvent(this.ME_PARTICIPANT_DELETED, caseFileId, participantId);
    }
    ,modelAddedPersonAssociation : function(caseFileId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.ME_PERSON_ASSOCIATION_ADDED, caseFileId, personAssociation);
    }
    ,modelUpdatedPersonAssociation : function(caseFileId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.ME_PERSON_ASSOCIATION_UPDATED, caseFileId, personAssociation);
    }
    ,modelDeletedPersonAssociation : function(caseFileId, personAssociationId) {
        Acm.Dispatcher.fireEvent(this.ME_PERSON_ASSOCIATION_DELETED, caseFileId, personAssociationId);
    }
    ,modelAddedContactMethod : function(caseFileId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.ME_CONTACT_METHOD_ADDED, caseFileId, personAssociationId, contactMethod);
    }
    ,modelUpdatedContactMethod : function(caseFileId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.ME_CONTACT_METHOD_UPDATED, caseFileId, personAssociationId, contactMethod);
    }
    ,modelDeletedContactMethod : function(caseFileId, personAssociationId, contactMethodId) {
        Acm.Dispatcher.fireEvent(this.ME_CONTACT_METHOD_DELETED, caseFileId, personAssociationId, contactMethodId);
    }

    ,modelSavedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.ME_NOTE_SAVED, note);
    }
    ,modelAddedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.ME_NOTE_ADDED, note);
    }
    ,modelUpdatedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.ME_NOTE_UPDATED, note);
    }
    ,modelDeletedNote : function(noteId) {
        Acm.Dispatcher.fireEvent(this.ME_NOTE_DELETED, noteId);
    }

    ,viewClickedPrevPage: function() {
        Acm.Dispatcher.fireEvent(this.VE_PREV_PAGE_CLICKED);
    }
    ,viewClickedNextPage: function() {
        Acm.Dispatcher.fireEvent(this.VE_NEXT_PAGE_CLICKED);
    }
    ,viewSelectedCaseFile: function(caseFileId) {
        //CaseFile.Model.setCaseFileId(caseFileId);
        Acm.Dispatcher.fireEvent(this.VE_CASE_FILE_SELECTED, caseFileId);
    }
    ,viewSelectedTreeNode: function(nodeKey) {
        Acm.Dispatcher.fireEvent(this.VE_TREE_NODE_SELECTED, nodeKey);
    }
    ,viewChangedCaseTitle: function(caseFileId, title) {
        Acm.Dispatcher.fireEvent(this.VE_CASE_TITLE_CHANGED, caseFileId, title);
    }
    ,viewChangedIncidentDate: function(caseFileId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.VE_INCIDENT_DATE_CHANGED, caseFileId, incidentDate);
    }
    ,viewChangedAssignee: function(caseFileId, assignee) {
        Acm.Dispatcher.fireEvent(this.VE_ASSIGNEE_CHANGED, caseFileId, assignee);
    }
    ,viewChangedSubjectType: function(caseFileId, caseType) {
        Acm.Dispatcher.fireEvent(this.VE_SUBJECT_TYPE_CHANGED, caseFileId, caseType);
    }
    ,viewChangedPriority: function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.VE_PRIORITY_CHANGED, caseFileId, priority);
    }
    ,viewChangedDueDate: function(caseFileId, dueDate) {
        Acm.Dispatcher.fireEvent(this.VE_DUE_DATE_CHANGED, caseFileId, dueDate);
    }
    ,viewChangedDetail: function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.VE_DETAIL_CHANGED, caseFileId, details);
    }
    ,viewClosedCaseFile: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VE_CASE_FILE_CLOSED, caseFileId);
    }
    ,viewAddedDocument: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VE_DOCUMENT_ADDED, caseFileId);
    }
    ,viewChangedChildObject: function(caseFileId, childObject) {
        Acm.Dispatcher.fireEvent(this.VE_CHILD_OBJECT_CHANGED, caseFileId, childObject);
    }
    ,viewAddedParticipant: function(caseFileId, participant) {
        Acm.Dispatcher.fireEvent(this.VE_PARTICIPANT_ADDED, caseFileId, participant);
    }
    ,viewUpdatedParticipant: function(caseFileId, participant) {
        Acm.Dispatcher.fireEvent(this.VE_PARTICIPANT_UPDATED, caseFileId, participant);
    }
    ,viewDeletedParticipant: function(caseFileId, participantId) {
        Acm.Dispatcher.fireEvent(this.VE_PARTICIPANT_DELETED, caseFileId, participantId);
    }
    ,viewAddedPersonAssociation : function(caseFileId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.VE_PERSON_ASSOCIATION_ADDED, caseFileId, personAssociation);
    }
    ,viewUpdatedPersonAssociation : function(caseFileId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.VE_PERSON_ASSOCIATION_UPDATED, caseFileId, personAssociation);
    }
    ,viewDeletedPersonAssociation : function(caseFileId, personAssociationId) {
        Acm.Dispatcher.fireEvent(this.VE_PERSON_ASSOCIATION_DELETED, caseFileId, personAssociationId);
    }
    ,viewAddedContactMethod : function(caseFileId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.VE_CONTACT_METHOD_ADDED, caseFileId, personAssociationId, contactMethod);
    }
    ,viewUpdatedContactMethod : function(caseFileId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.VE_CONTACT_METHOD_UPDATED, caseFileId, personAssociationId, contactMethod);
    }
    ,viewDeletedContactMethod : function(caseFileId, personAssociationId, contactMethodId) {
        Acm.Dispatcher.fireEvent(this.VE_CONTACT_METHOD_DELETED, caseFileId, personAssociationId, contactMethodId);
    }

    ,viewAddedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.VE_NOTE_ADDED, note);
    }
    ,viewUpdatedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.VE_NOTE_UPDATED, note);
    }
    ,viewDeletedNote: function(noteId) {
        Acm.Dispatcher.fireEvent(this.VE_NOTE_DELETED, noteId);
    }


};

