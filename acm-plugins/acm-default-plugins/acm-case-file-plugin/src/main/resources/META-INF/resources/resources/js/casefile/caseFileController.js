/**
 * CaseFile.Controller
 *
 * @author jwu
 */
CaseFile.Controller = CaseFile.Controller || {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_FOUND_ASSIGNEES                 : "case-model-found-assignees"              //param: assignees
    ,MODEL_FOUND_SUBJECT_TYPES             : "case-model-found-subject-types"          //param: subjectTypes
    ,MODEL_FOUND_PRIORITIES                : "case-model-found-priorities"             //param: priorities

    ,MODEL_RETRIEVED_CASE_FILE_LIST        : "case-model-retrieved-case-file-list"     //param: key
    ,MODEL_RETRIEVED_CASE_FILE             : "case-model-retrieved-detail"             //param: caseFile
    ,MODEL_SAVED_CASE_FILE                 : "case-model-saved-detail"                 //param: caseFile

    ,MODEL_SAVED_CASE_TITLE                : "case-model-saved-case-title"             //param: caseFileId, caseTitle
    ,MODEL_SAVED_INCIDENT_DATE             : "case-model-saved-incident-date"          //param: caseFileId, incidentDate
    ,MODEL_SAVED_ASSIGNEE                  : "case-model-saved-assignee"               //param: caseFileId, assignee
    ,MODEL_SAVED_SUBJECT_TYPE              : "case-model-saved-subject-type"           //param: caseFileId, caseType
    ,MODEL_SAVED_PRIORITY                  : "case-model-saved-priority"               //param: caseFileId, priority
    ,MODEL_SAVED_DUE_DATE                  : "case-model-saved-due-date"               //param: caseFileId, dueDate
    ,MODEL_SAVED_DETAIL                    : "case-model-saved-detail"                 //param: caseFileId, details
    ,MODEL_SAVED_CHILD_OBJECT              : "case-model-saved-childObject"            //param: caseFileId, childObject
    ,MODEL_ADDED_PARTICIPANT               : "case-model-added-participant"            //param: caseFileId, participant
    ,MODEL_UPDATED_PARTICIPANT             : "case-model-updated-participant"          //param: caseFileId, participant
    ,MODEL_DELETED_PARTICIPANT             : "case-model-deleted-participant"          //param: caseFileId, participantId
    ,MODEL_ADDED_PERSON_ASSOCIATION        : "case-model-added-person-association"     //param: caseFileId, personAssociation
    ,MODEL_UPDATED_PERSON_ASSOCIATION      : "case-model-updated-person-association"   //param: caseFileId, personAssociation
    ,MODEL_DELETED_PERSON_ASSOCIATION      : "case-model-deleted-person-association"   //param: caseFileId, personAssociationId
    ,MODEL_ADDED_ADDRESS                   : "case-model-added-address"                //param: caseFileId, personAssociationId, address
    ,MODEL_UPDATED_ADDRESS                 : "case-model-updated-address"              //param: caseFileId, personAssociationId, address
    ,MODEL_DELETED_ADDRESS                 : "case-model-deleted-address"              //param: caseFileId, personAssociationId, addressId
    ,MODEL_ADDED_CONTACT_METHOD            : "case-model-added-contact-method"         //param: caseFileId, personAssociationId, contactMethod
    ,MODEL_UPDATED_CONTACT_METHOD          : "case-model-updated-contact-method"       //param: caseFileId, personAssociationId, contactMethod
    ,MODEL_DELETED_CONTACT_METHOD          : "case-model-deleted-contact-method"       //param: caseFileId, personAssociationId, contactMethodId
    ,MODEL_ADDED_SECURITY_TAG              : "case-model-added-security-tag"           //param: caseFileId, personAssociationId, securityTag
    ,MODEL_UPDATED_SECURITY_TAG            : "case-model-updated-security-tag"         //param: caseFileId, personAssociationId, securityTag
    ,MODEL_DELETED_SECURITY_TAG            : "case-model-deleted-security-tag"         //param: caseFileId, personAssociationId, securityTagId
    ,MODEL_ADDED_PERSON_ALIAS              : "case-model-added-person-alias"           //param: caseFileId, personAssociationId, personAlias
    ,MODEL_UPDATED_PERSON_ALIAS            : "case-model-updated-person-alias"         //param: caseFileId, personAssociationId, personAlias
    ,MODEL_DELETED_PERSON_ALIAS            : "case-model-deleted-person-alias"         //param: caseFileId, personAssociationId, personAliasId
    ,MODEL_ADDED_ORGANIZATION              : "case-model-added-organization"           //param: caseFileId, personAssociationId, organization
    ,MODEL_UPDATED_ORGANIZATION            : "case-model-updated-organization"         //param: caseFileId, personAssociationId, organization
    ,MODEL_DELETED_ORGANIZATION            : "case-model-deleted-organization"         //param: caseFileId, personAssociationId, organizationId

    ,MODEL_SAVED_NOTE                      : "case-model-saved-note"                   //param: note
    ,MODEL_ADDED_NOTE                      : "case-model-added-note"                   //param: note
    ,MODEL_UPDATED_NOTE                    : "case-model-updated-note"                 //param: note
    ,MODEL_DELETED_NOTE                    : "case-model-deleted-note"                 //param: noteId

    ,MODEL_ADDED_DOCUMENT                  : "case-model-added-document"               //param: caseFileId
    ,VIEW_CLICKED_PREV_PAGE                : "case-view-clicked-prev-page"             //param: none
    ,VIEW_CLICKED_NEXT_PAGE 	           : "case-view-clicked-next-page"             //param: none
    ,VIEW_SELECTED_CASE_FILE 		       : "case-view-selected-case"                 //param: caseFileId
    ,VIEW_SELECTED_TREE_NODE 		       : "case-view-selected-tree-node"            //param: node key

    ,VIEW_CHANGED_CASE_FILE               : "case-view-changed-case-file"              //param: caseFileId
    ,VIEW_CHANGED_CASE_TITLE               : "case-view-changed-case-title"            //param: caseFileId, title
    ,VIEW_CHANGED_INCIDENT_DATE            : "case-view-changed-incident-date"         //param: caseFileId, incidentDate
    ,VIEW_CHANGED_ASSIGNEE                 : "case-view-changed-assignee"              //param: caseFileId, assignee
    ,VIEW_CHANGED_SUBJECT_TYPE             : "case-view-changed-subject-type"          //param: caseFileId, caseType
    ,VIEW_CHANGED_PRIORITY                 : "case-view-changed-priority"              //param: caseFileId, priority
    ,VIEW_CHANGED_DUE_DATE                 : "case-view-changed-due-date"              //param: caseFileId, dueDate
    ,VIEW_CHANGED_DETAIL                   : "case-viewl-changed-detai"                //param: caseFileId, details

    ,VIEW_CLOSED_CASE_FILE                 : "case-view-closed-case"                   //param: caseFileId
    ,VIEW_ADDED_DOCUMENT                   : "case-view-added-document"                //param: caseFileId
    ,VIEW_CHANGED_CHILD_OBJECT             : "case-view-changed-child-object"          //param: caseFileId, childObject
    ,VIEW_ADDED_PARTICIPANT                : "case-view-added-participant"             //param: caseFileId, participant
    ,VIEW_UPDATED_PARTICIPANT              : "case-view-updated-participant"           //param: caseFileId, participant
    ,VIEW_DELETED_PARTICIPANT              : "case-view-deleted-participant"           //param: caseFileId, participantId
    ,VIEW_ADDED_PERSON_ASSOCIATION         : "case-view-added-person-association"      //param: caseFileId, personAssociation
    ,VIEW_UPDATED_PERSON_ASSOCIATION       : "case-view-updated-person-association"    //param: caseFileId, personAssociation
    ,VIEW_DELETED_PERSON_ASSOCIATION       : "case-view-deleted-person-association"    //param: caseFileId, personAssociationId
    ,VIEW_ADDED_ADDRESS                    : "case-view-added-address"                 //param: caseFileId, personAssociationId, address
    ,VIEW_UPDATED_ADDRESS                  : "case-view-updated-address"               //param: caseFileId, personAssociationId, address
    ,VIEW_DELETED_ADDRESS                  : "case-view-deleted-address"               //param: caseFileId, personAssociationId, addressId
    ,VIEW_ADDED_CONTACT_METHOD             : "case-view-added-contact-method"          //param: caseFileId, personAssociationId, contactMethod
    ,VIEW_UPDATED_CONTACT_METHOD           : "case-view-updated-contact-method"        //param: caseFileId, personAssociationId, contactMethod
    ,VIEW_DELETED_CONTACT_METHOD           : "case-view-deleted-contact-method"        //param: caseFileId, personAssociationId, contactMethodId
    ,VIEW_ADDED_SECURITY_TAG               : "case-view-added-security-tag"            //param: caseFileId, personAssociationId, securityTag
    ,VIEW_UPDATED_SECURITY_TAG             : "case-view-updated-security-tag"          //param: caseFileId, personAssociationId, securityTag
    ,VIEW_DELETED_SECURITY_TAG             : "case-view-deleted-security-tag"          //param: caseFileId, personAssociationId, securityTagId
    ,VIEW_ADDED_PERSON_ALIAS               : "case-view-added-person-alias"            //param: caseFileId, personAssociationId, personAlias
    ,VIEW_UPDATED_PERSON_ALIAS             : "case-view-updated-person-alias"          //param: caseFileId, personAssociationId, personAlias
    ,VIEW_DELETED_PERSON_ALIAS             : "case-view-deleted-person-alias"          //param: caseFileId, personAssociationId, personAliasId
    ,VIEW_ADDED_ORGANIZATION               : "case-view-added-organization"            //param: caseFileId, personAssociationId, organization
    ,VIEW_UPDATED_ORGANIZATION             : "case-view-updated-organization"          //param: caseFileId, personAssociationId, organization
    ,VIEW_DELETED_ORGANIZATION             : "case-view-deleted-organization"          //param: caseFileId, personAssociationId, organizationId

    ,VIEW_ADDED_NOTE                       : "case-view-added-note"                    //param: note
    ,VIEW_UPDATED_NOTE                     : "case-view-updated-note"                  //param: note
    ,VIEW_DELETED_NOTE                     : "case-view-deleted-note"                  //param: noteId


    ,modelFoundAssignees: function(assignees) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_ASSIGNEES, assignees);
    }
    ,modelFoundSubjectTypes: function(subjectTypes) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_SUBJECT_TYPES, subjectTypes);
    }
    ,modelFoundPriorities: function(priorities) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_PRIORITIES, priorities);
    }
    ,modelRetrievedCaseFileList: function(key) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_CASE_FILE_LIST, key);
    }
    ,modelRetrievedCaseFile: function(caseFile) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_CASE_FILE, caseFile);
    }
    ,modelSavedCaseFile : function(caseFile) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_CASE_FILE, caseFile);
    }
    ,modelSavedCaseTitle : function(caseFileId, title) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_CASE_TITLE, caseFileId, title);
    }
    ,modelSavedIncidentDate : function(caseFileId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_INCIDENT_DATE, caseFileId, incidentDate);
    }
    ,modelSavedAssignee : function(caseFileId, assignee) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_ASSIGNEE, caseFileId, assignee);
    }
    ,modelSavedSubjectType : function(caseFileId, caseType) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_SUBJECT_TYPE, caseFileId, caseType);
    }
    ,modelSavedPriority : function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_PRIORITY, caseFileId, priority);
    }
    ,modelSavedDueDate : function(caseFileId, dueDate) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DUE_DATE, caseFileId, dueDate);
    }
    ,modelSavedDetail : function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DETAIL, caseFileId, details);
    }
    ,modelSavedChildObject : function(caseFileId, childObject) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_CHILD_OBJECT, caseFileId, childObject);
    }
    ,modelAddedParticipant : function(caseFileId, participant) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_PARTICIPANT, caseFileId, participant);
    }
    ,modelUpdatedParticipant : function(caseFileId, participant) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_PARTICIPANT, caseFileId, participant);
    }
    ,modelDeletedParticipant : function(caseFileId, participantId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_PARTICIPANT, caseFileId, participantId);
    }
    ,modelAddedPersonAssociation : function(caseFileId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_PERSON_ASSOCIATION, caseFileId, personAssociation);
    }
    ,modelUpdatedPersonAssociation : function(caseFileId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_PERSON_ASSOCIATION, caseFileId, personAssociation);
    }
    ,modelDeletedPersonAssociation : function(caseFileId, personAssociationId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_PERSON_ASSOCIATION, caseFileId, personAssociationId);
    }
    ,modelAddedAddress : function(caseFileId, personAssociationId, address) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_ADDRESS, caseFileId, personAssociationId, address);
    }
    ,modelUpdatedAddress : function(caseFileId, personAssociationId, address) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ADDRESS, caseFileId, personAssociationId, address);
    }
    ,modelDeletedAddress : function(caseFileId, personAssociationId, addressId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_ADDRESS, caseFileId, personAssociationId, addressId);
    }
    ,modelAddedContactMethod : function(caseFileId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_CONTACT_METHOD, caseFileId, personAssociationId, contactMethod);
    }
    ,modelUpdatedContactMethod : function(caseFileId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_CONTACT_METHOD, caseFileId, personAssociationId, contactMethod);
    }
    ,modelDeletedContactMethod : function(caseFileId, personAssociationId, contactMethodId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_CONTACT_METHOD, caseFileId, personAssociationId, contactMethodId);
    }
    ,modelAddedSecurityTag : function(caseFileId, personAssociationId, securityTag) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_SECURITY_TAG, caseFileId, personAssociationId, securityTag);
    }
    ,modelUpdatedSecurityTag : function(caseFileId, personAssociationId, securityTag) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_SECURITY_TAG, caseFileId, personAssociationId, securityTag);
    }
    ,modelDeletedSecurityTag : function(caseFileId, personAssociationId, securityTagId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_SECURITY_TAG, caseFileId, personAssociationId, securityTagId);
    }
    ,modelAddedPersonAlias : function(caseFileId, personAssociationId, personAlias) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_PERSON_ALIAS, caseFileId, personAssociationId, personAlias);
    }
    ,modelUpdatedPersonAlias : function(caseFileId, personAssociationId, personAlias) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_PERSON_ALIAS, caseFileId, personAssociationId, personAlias);
    }
    ,modelDeletedPersonAlias : function(caseFileId, personAssociationId, personAliasId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_PERSON_ALIAS, caseFileId, personAssociationId, personAliasId);
    }
    ,modelAddedOrganization : function(caseFileId, personAssociationId, organization) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_ORGANIZATION, caseFileId, personAssociationId, organization);
    }
    ,modelUpdatedOrganization : function(caseFileId, personAssociationId, organization) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ORGANIZATION, caseFileId, personAssociationId, organization);
    }
    ,modelDeletedOrganization : function(caseFileId, personAssociationId, organizationId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_ORGANIZATION, caseFileId, personAssociationId, organizationId);
    }
    ,modelAddedDocument: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_DOCUMENT, caseFileId);
    }
    ,modelSavedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_NOTE, note);
    }
    ,modelAddedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_NOTE, note);
    }
    ,modelUpdatedNote : function(note) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_NOTE, note);
    }
    ,modelDeletedNote : function(noteId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_NOTE, noteId);
    }
    ,viewClickedPrevPage: function() {
        Acm.Dispatcher.fireEvent(this.VIEW_CLICKED_PREV_PAGE);
    }
    ,viewClickedNextPage: function() {
        Acm.Dispatcher.fireEvent(this.VIEW_CLICKED_NEXT_PAGE);
    }
    ,viewSelectedCaseFile: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_SELECTED_CASE_FILE, caseFileId);
    }
    ,viewSelectedTreeNode: function(nodeKey) {
        Acm.Dispatcher.fireEvent(this.VIEW_SELECTED_TREE_NODE, nodeKey);
    }
    ,viewChangedCaseFile: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_CASE_FILE, caseFileId);
    }
    ,viewChangedCaseTitle: function(caseFileId, title) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_CASE_TITLE, caseFileId, title);
    }
    ,viewChangedIncidentDate: function(caseFileId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_INCIDENT_DATE, caseFileId, incidentDate);
    }
    ,viewChangedAssignee: function(caseFileId, assignee) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_ASSIGNEE, caseFileId, assignee);
    }
    ,viewChangedSubjectType: function(caseFileId, caseType) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_SUBJECT_TYPE, caseFileId, caseType);
    }
    ,viewChangedPriority: function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_PRIORITY, caseFileId, priority);
    }
    ,viewChangedDueDate: function(caseFileId, dueDate) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_DUE_DATE, caseFileId, dueDate);
    }
    ,viewChangedDetail: function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_DETAIL, caseFileId, details);
    }
    ,viewClosedCaseFile: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_CLOSED_CASE_FILE, caseFileId);
    }
    ,viewAddedDocument: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_DOCUMENT, caseFileId);
    }
    ,viewChangedChildObject: function(caseFileId, childObject) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_CHILD_OBJECT, caseFileId, childObject);
    }
    ,viewAddedParticipant: function(caseFileId, participant) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_PARTICIPANT, caseFileId, participant);
    }
    ,viewUpdatedParticipant: function(caseFileId, participant) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_PARTICIPANT, caseFileId, participant);
    }
    ,viewDeletedParticipant: function(caseFileId, participantId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_PARTICIPANT, caseFileId, participantId);
    }
    ,viewAddedPersonAssociation : function(caseFileId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_PERSON_ASSOCIATION, caseFileId, personAssociation);
    }
    ,viewUpdatedPersonAssociation : function(caseFileId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_PERSON_ASSOCIATION, caseFileId, personAssociation);
    }
    ,viewDeletedPersonAssociation : function(caseFileId, personAssociationId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_PERSON_ASSOCIATION, caseFileId, personAssociationId);
    }
    ,viewAddedAddress : function(caseFileId, personAssociationId, address) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_ADDRESS, caseFileId, personAssociationId, address);
    }
    ,viewUpdatedAddress : function(caseFileId, personAssociationId, address) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_ADDRESS, caseFileId, personAssociationId, address);
    }
    ,viewDeletedAddress : function(caseFileId, personAssociationId, addressId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_ADDRESS, caseFileId, personAssociationId, addressId);
    }
    ,viewAddedContactMethod : function(caseFileId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_CONTACT_METHOD, caseFileId, personAssociationId, contactMethod);
    }
    ,viewUpdatedContactMethod : function(caseFileId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_CONTACT_METHOD, caseFileId, personAssociationId, contactMethod);
    }
    ,viewDeletedContactMethod : function(caseFileId, personAssociationId, contactMethodId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_CONTACT_METHOD, caseFileId, personAssociationId, contactMethodId);
    }
    ,viewAddedSecurityTag : function(caseFileId, personAssociationId, securityTag) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_SECURITY_TAG, caseFileId, personAssociationId, securityTag);
    }
    ,viewUpdatedSecurityTag : function(caseFileId, personAssociationId, securityTag) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_SECURITY_TAG, caseFileId, personAssociationId, securityTag);
    }
    ,viewDeletedSecurityTag : function(caseFileId, personAssociationId, securityTagId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_SECURITY_TAG, caseFileId, personAssociationId, securityTagId);
    }
    ,viewAddedPersonAlias : function(caseFileId, personAssociationId, personAlias) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_PERSON_ALIAS, caseFileId, personAssociationId, personAlias);
    }
    ,viewUpdatedPersonAlias : function(caseFileId, personAssociationId, personAlias) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_PERSON_ALIAS, caseFileId, personAssociationId, personAlias);
    }
    ,viewDeletedPersonAlias : function(caseFileId, personAssociationId, personAliasId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_PERSON_ALIAS, caseFileId, personAssociationId, personAliasId);
    }
    ,viewAddedOrganization : function(caseFileId, personAssociationId, organization) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_ORGANIZATION, caseFileId, personAssociationId, organization);
    }
    ,viewUpdatedOrganization : function(caseFileId, personAssociationId, organization) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_ORGANIZATION, caseFileId, personAssociationId, organization);
    }
    ,viewDeletedOrganization : function(caseFileId, personAssociationId, organizationId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_ORGANIZATION, caseFileId, personAssociationId, organizationId);
    }

    ,viewAddedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_NOTE, note);
    }
    ,viewUpdatedNote: function(note) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_NOTE, note);
    }
    ,viewDeletedNote: function(noteId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_NOTE, noteId);
    }


};

