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

    ,MODEL_FOUND_ASSIGNEES                 : "case-model-found-assignees"
    ,modelFoundAssignees: function(assignees) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_ASSIGNEES, assignees);
    }
    ,VIEW_CHANGED_ASSIGNEE                 : "case-view-changed-assignee"
    ,viewChangedAssignee: function(caseFileId, assignee) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_ASSIGNEE, caseFileId, assignee);
    }
    ,MODEL_SAVED_ASSIGNEE                  : "case-model-saved-assignee"
    ,modelSavedAssignee : function(caseFileId, assignee) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_ASSIGNEE, caseFileId, assignee);
    }
    ,MODEL_FOUND_SUBJECT_TYPES             : "case-model-found-subject-types"
    ,modelFoundSubjectTypes: function(subjectTypes) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_SUBJECT_TYPES, subjectTypes);
    }
    ,VIEW_CHANGED_SUBJECT_TYPE             : "case-view-changed-subject-type"
    ,viewChangedSubjectType: function(caseFileId, caseType) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_SUBJECT_TYPE, caseFileId, caseType);
    }
    ,MODEL_SAVED_SUBJECT_TYPE              : "case-model-saved-subject-type"
    ,modelSavedSubjectType : function(caseFileId, caseType) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_SUBJECT_TYPE, caseFileId, caseType);
    }
    ,MODEL_FOUND_PRIORITIES                : "case-model-found-priorities"
    ,modelFoundPriorities: function(priorities) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_PRIORITIES, priorities);
    }
    ,VIEW_CHANGED_PRIORITY                 : "case-view-changed-priority"
    ,viewChangedPriority: function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_PRIORITY, caseFileId, priority);
    }
    ,MODEL_SAVED_PRIORITY                  : "case-model-saved-priority"
    ,modelSavedPriority : function(caseFileId, priority) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_PRIORITY, caseFileId, priority);
    }
    ,VIEW_CHANGED_CASE_TITLE               : "case-view-changed-case-title"
    ,viewChangedCaseTitle: function(caseFileId, title) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_CASE_TITLE, caseFileId, title);
    }
    ,MODEL_SAVED_CASE_TITLE                : "case-model-saved-case-title"
    ,modelSavedCaseTitle : function(caseFileId, title) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_CASE_TITLE, caseFileId, title);
    }
    ,VIEW_CHANGED_INCIDENT_DATE            : "case-view-changed-incident-date"
    ,viewChangedIncidentDate: function(caseFileId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_INCIDENT_DATE, caseFileId, incidentDate);
    }
    ,MODEL_SAVED_INCIDENT_DATE             : "case-model-saved-incident-date"
    ,modelSavedIncidentDate : function(caseFileId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_INCIDENT_DATE, caseFileId, incidentDate);
    }
    ,VIEW_CHANGED_DUE_DATE                 : "case-view-changed-due-date"
    ,viewChangedDueDate: function(caseFileId, dueDate) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_DUE_DATE, caseFileId, dueDate);
    }
    ,MODEL_SAVED_DUE_DATE                  : "case-model-saved-due-date"
    ,modelSavedDueDate : function(caseFileId, dueDate) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DUE_DATE, caseFileId, dueDate);
    }
    ,VIEW_CHANGED_DETAIL                   : "case-view-changed-detail"
    ,viewChangedDetail: function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_DETAIL, caseFileId, details);
    }
    ,MODEL_SAVED_DETAIL                    : "case-model-saved-detail"
    ,modelSavedDetail : function(caseFileId, details) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DETAIL, caseFileId, details);
    }

    ,VIEW_CLICKED_RESTRICT_CHECKBOX        : "case-view-clicked-restrict-checkbox"
    ,viewClickedRestrictCheckbox: function(caseFileId, restriction) {
        Acm.Dispatcher.fireEvent(this.VIEW_CLICKED_RESTRICT_CHECKBOX, caseFileId, restriction);
    }
    ,MODEL_SAVED_RESTRICTION               : "case-model-saved-restriction"
    ,modelSavedRestriction : function(caseFileId, restriction) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_RESTRICTION, caseFileId, restriction);
    }

    ,MODEL_RETRIEVED_TIMESHEETS               : "case-model-retrieved-timesheets"
    ,modelRetrievedTimesheets : function(timesheets) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_TIMESHEETS, timesheets);
    }

    ,MODEL_RETRIEVED_COSTSHEETS               : "case-model-retrieved-costsheets"
    ,modelRetrievedCostsheets : function(costsheets) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_COSTSHEETS, costsheets);
    }


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
    ,MODEL_CREATED_CORRESPONDENCE          : "case-model-created-correspondence"       //param: caseFileId

    ,MODEL_RETRIEVED_TASKS                 : "case-model-task-retrieved"               //param: taskId
    ,MODEL_COMPLETED_TASK                 : "case-model-task-completed"               //param: task


    ,VIEW_CHANGED_CASE_FILE               : "case-view-changed-case-file"
    ,viewChangedCaseFile: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_CASE_FILE, caseFileId);
    }

    ,VIEW_CHANGED_TREE_FILTER              : "case-view-changed-tree-filter"           //param: filter
    ,VIEW_CHANGED_TREE_SORT                : "case-view-changed-tree-sort"             //param: sort
    ,VIEW_CLOSED_CASE_FILE                 : "case-view-closed-case"                   //param: caseFileId
    ,VIEW_ADDED_DOCUMENT                   : "case-view-added-document"                //param: caseFileId
    ,VIEW_CLICKED_ADD_CORRESPONDENCE       : "case-model-clicked-add-correspondence"   //param: caseFileId, templateName
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
    ,modelCreatedCorrespondence: function(correspondence) {
        Acm.Dispatcher.fireEvent(this.MODEL_CREATED_CORRESPONDENCE, correspondence);
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
    ,modelRetrievedTasks: function(tasks) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_TASKS, tasks);
    }
    ,modelCompletedTask: function(task) {
        Acm.Dispatcher.fireEvent(this.MODEL_COMPLETED_TASK, task);
    }
    ,viewChangedTreeFilter: function(filter) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_TREE_FILTER, filter);
    }
    ,viewChangedTreeSort: function(sort) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_TREE_SORT, sort);
    }
    ,viewClosedCaseFile: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_CLOSED_CASE_FILE, caseFileId);
    }
    ,viewAddedDocument: function(caseFileId) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_DOCUMENT, caseFileId);
    }
    ,viewClickedAddCorrespondence: function(caseFileId, templateName) {
        Acm.Dispatcher.fireEvent(this.VIEW_CLICKED_ADD_CORRESPONDENCE, caseFileId, templateName);
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

