/**
 * Complaint.Controller
 *
 * @author jwu
 */
Complaint.Controller = Complaint.Controller || {
    create : function() {
    }
    ,onInitialized: function() {
    }
//    ,Documents: {
//        create : function() {
//        }
//        ,onInitialized: function() {
//        }
//
//        ,VIEW_CHANGED_TREE: "document-view-changed-tree"
//        ,viewChangedTree: function() {
//            Acm.Dispatcher.fireEvent(Complaint.Controller.Documents.VIEW_CHANGED_TREE);
//        }
//    }

    ,MODEL_FOUND_ASSIGNEES                 : "complaint-model-found-assignees"
    ,modelFoundAssignees: function(assignees) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_ASSIGNEES, assignees);
    }
    ,VIEW_CHANGED_ASSIGNEE                 : "complaint-view-changed-assignee"
    ,viewChangedAssignee: function(complaintId, assignee) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_ASSIGNEE, complaintId, assignee);
    }
    ,VIEW_CHANGED_GROUP                 : "complaint-view-changed-group"
    ,viewChangedGroup: function(complaintId, group) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_GROUP, complaintId, group);
    }
    ,MODEL_SAVED_ASSIGNEE                  : "complaint-model-saved-assignee"
    ,modelSavedAssignee : function(complaintId, assignee) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_ASSIGNEE, complaintId, assignee);
    }
    ,MODEL_SAVED_GROUP                  : "complaint-model-saved-group"
    ,modelSavedGroup : function(complaintId, group) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_GROUP, complaintId, group);
    }
    ,MODEL_FOUND_COMPLAINT_TYPES             : "complaint-model-found-complaint-types"
    ,modelFoundComplaintTypes: function(complaintTypes) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_COMPLAINT_TYPES, complaintTypes);
    }
    ,VIEW_CHANGED_COMPLAINT_TYPE             : "complaint-view-changed-complaint-type"
    ,viewChangedComplaintType: function(complaintId, complaintType) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_COMPLAINT_TYPE, complaintId, complaintType);
    }
    ,MODEL_SAVED_COMPLAINT_TYPE              : "complaint-model-saved-complaint-type"
    ,modelSavedComplaintType : function(complaintId, complaintType) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_COMPLAINT_TYPE, complaintId, complaintType);
    }
    ,MODEL_FOUND_PRIORITIES                : "complaint-model-found-priorities"
    ,modelFoundPriorities: function(priorities) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_PRIORITIES, priorities);
    }
    ,VIEW_CHANGED_PRIORITY                 : "complaint-view-changed-priority"
    ,viewChangedPriority: function(complaintId, priority) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_PRIORITY, complaintId, priority);
    }
    ,MODEL_SAVED_PRIORITY                  : "complaint-model-saved-priority"
    ,modelSavedPriority : function(complaintId, priority) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_PRIORITY, complaintId, priority);
    }
    ,VIEW_CHANGED_COMPLAINT_TITLE               : "complaint-view-changed-complaint-title"
    ,viewChangedComplaintTitle: function(complaintId, title) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_COMPLAINT_TITLE, complaintId, title);
    }
    ,MODEL_SAVED_COMPLAINT_TITLE                : "complaint-model-saved-complaint-title"
    ,modelSavedComplaintTitle : function(complaintId, title) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_COMPLAINT_TITLE, complaintId, title);
    }
    ,VIEW_CHANGED_INCIDENT_DATE            : "complaint-view-changed-incident-date"
    ,viewChangedIncidentDate: function(complaintId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_INCIDENT_DATE, complaintId, incidentDate);
    }
    ,MODEL_SAVED_INCIDENT_DATE             : "complaint-model-saved-incident-date"
    ,modelSavedIncidentDate : function(complaintId, incidentDate) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_INCIDENT_DATE, complaintId, incidentDate);
    }
    ,VIEW_CHANGED_DETAIL                   : "complaint-view-changed-detail"
    ,viewChangedDetail: function(complaintId, details) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_DETAIL, complaintId, details);
    }
    ,MODEL_SAVED_DETAIL                    : "complaint-model-saved-detail"
    ,modelSavedDetail : function(complaintId, details) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_DETAIL, complaintId, details);
    }

    ,VIEW_CHANGED_RESTRICTION        : "complaint-view-changed-restriction"
    ,viewChangedRestriction: function(complaintId, restriction) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_RESTRICTION, complaintId, restriction);
    }
    ,MODEL_SAVED_RESTRICTION               : "complaint-model-saved-restriction"
    ,modelSavedRestriction : function(complaintId, restriction) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_RESTRICTION, complaintId, restriction);
    }

    ,MODEL_RETRIEVED_TIMESHEETS               : "complaint-model-retrieved-timesheets"
    ,modelRetrievedTimesheets : function(timesheets) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_TIMESHEETS, timesheets);
    }

    ,MODEL_RETRIEVED_COSTSHEETS               : "complaint-model-retrieved-costsheets"
    ,modelRetrievedCostsheets : function(costsheets) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_COSTSHEETS, costsheets);
    }    
    
    ,MODEL_RETRIEVED_GROUPS                 : "complaint-model-retrieved-groups"
    ,modelRetrievedGroups: function(groups) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_GROUPS, groups);
    }
    
    ,MODEL_RETRIEVED_USERS                 : "complaint-model-retrieved-users"
    ,modelRetrievedUsers: function(users) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_USERS, users);
    }
//to retire
//    ,VIEW_CLOSED_ADD_DOCUMENT_WINDOW                 : "complaint-view-closed-add-document-window"
//	,viewClosedAddDocumentWindow: function(complaintId) {
//        Acm.Dispatcher.fireEvent(this.VIEW_CLOSED_ADD_DOCUMENT_WINDOW, complaintId);
//    }

    //----------------------------------------------------------------------------------


    ,MODEL_ADDED_PERSON_ASSOCIATION        : "complaint-model-added-person-association"     //param: complaintId, personAssociation
    ,MODEL_UPDATED_PERSON_ASSOCIATION      : "complaint-model-updated-person-association"   //param: complaintId, personAssociation
    ,MODEL_DELETED_PERSON_ASSOCIATION      : "complaint-model-deleted-person-association"   //param: complaintId, personAssociationId
    ,MODEL_ADDED_ADDRESS                   : "complaint-model-added-address"                //param: complaintId, personAssociationId, address
    ,MODEL_UPDATED_ADDRESS                 : "complaint-model-updated-address"              //param: complaintId, personAssociationId, address
    ,MODEL_DELETED_ADDRESS                 : "complaint-model-deleted-address"              //param: complaintId, personAssociationId, addressId
    ,MODEL_ADDED_CONTACT_METHOD            : "complaint-model-added-contact-method"         //param: complaintId, personAssociationId, contactMethod
    ,MODEL_UPDATED_CONTACT_METHOD          : "complaint-model-updated-contact-method"       //param: complaintId, personAssociationId, contactMethod
    ,MODEL_DELETED_CONTACT_METHOD          : "complaint-model-deleted-contact-method"       //param: complaintId, personAssociationId, contactMethodId
    ,MODEL_ADDED_SECURITY_TAG              : "complaint-model-added-security-tag"           //param: complaintId, personAssociationId, securityTag
    ,MODEL_UPDATED_SECURITY_TAG            : "complaint-model-updated-security-tag"         //param: complaintId, personAssociationId, securityTag
    ,MODEL_DELETED_SECURITY_TAG            : "complaint-model-deleted-security-tag"         //param: complaintId, personAssociationId, securityTagId
    ,MODEL_ADDED_PERSON_ALIAS              : "complaint-model-added-person-alias"           //param: complaintId, personAssociationId, personAlias
    ,MODEL_UPDATED_PERSON_ALIAS            : "complaint-model-updated-person-alias"         //param: complaintId, personAssociationId, personAlias
    ,MODEL_DELETED_PERSON_ALIAS            : "complaint-model-deleted-person-alias"         //param: complaintId, personAssociationId, personAliasId
    ,MODEL_ADDED_ORGANIZATION              : "complaint-model-added-organization"           //param: complaintId, personAssociationId, organization
    ,MODEL_UPDATED_ORGANIZATION            : "complaint-model-updated-organization"         //param: complaintId, personAssociationId, organization
    ,MODEL_DELETED_ORGANIZATION            : "complaint-model-deleted-organization"         //param: complaintId, personAssociationId, organizationId

    ,MODEL_UPLOADED_DOCUMENTS              : "complaint-model-uploaded-documents"           //param: documents

    ,MODEL_SAVED_NOTE                      : "complaint-model-saved-note"                   //param : note
    ,MODEL_ADDED_NOTE                      : "complaint-model-added-note"                   //param : note
    ,MODEL_UPDATED_NOTE                    : "complaint-model-updated-note"                 //param : note
    ,MODEL_DELETED_NOTE                    : "complaint-model-deleted-note"                 //param : deletedNote

    ,MODEL_ADDED_DOCUMENT                  : "complaint-model-added-document"               //param: complaintId

    ,MODEL_RETRIEVED_TASKS                 : "complaint-model-task-retrieved"               //param: taskId

    ,MODEL_ADDED_PARTICIPANT               : "complaint-model-added-participant"            //param: complaintId, participant
    ,MODEL_UPDATED_PARTICIPANT             : "complaint-model-updated-participant"          //param: complaintId, participant
    ,MODEL_DELETED_PARTICIPANT             : "complaint-model-deleted-participant"          //param: complaintId, participantId

    ,MODEL_ADDED_LOCATION                  : "complaint-model-added-location"               //param: complaintId, location
    ,MODEL_UPDATED_LOCATION                : "complaint-model-updated-location"             //param: complaintId, location
    ,MODEL_DELETED_LOCATION                : "complaint-model-deleted-location"             //param: complaintId, location

    ,VIEW_ADDED_PERSON_ASSOCIATION         : "complaint-view-added-person-association"
    ,VIEW_UPDATED_PERSON_ASSOCIATION       : "complaint-view-updated-person-association"    //param: complaintId, personAssociation
    ,VIEW_DELETED_PERSON_ASSOCIATION       : "complaint-view-deleted-person-association"    //param: complaintId, personAssociationId
    ,VIEW_ADDED_ADDRESS                    : "complaint-view-added-address"                 //param: complaintId, personAssociationId, address
    ,VIEW_UPDATED_ADDRESS                  : "complaint-view-updated-address"               //param: complaintId, personAssociationId, address
    ,VIEW_DELETED_ADDRESS                  : "complaint-view-deleted-address"               //param: complaintId, personAssociationId, addressId
    ,VIEW_ADDED_CONTACT_METHOD             : "complaint-view-added-contact-method"          //param: complaintId, personAssociationId, contactMethod
    ,VIEW_UPDATED_CONTACT_METHOD           : "complaint-view-updated-contact-method"        //param: complaintId, personAssociationId, contactMethod
    ,VIEW_DELETED_CONTACT_METHOD           : "complaint-view-deleted-contact-method"        //param: complaintId, personAssociationId, contactMethodId
    ,VIEW_ADDED_SECURITY_TAG               : "complaint-view-added-security-tag"            //param: complaintId, personAssociationId, securityTag
    ,VIEW_UPDATED_SECURITY_TAG             : "complaint-view-updated-security-tag"          //param: complaintId, personAssociationId, securityTag
    ,VIEW_DELETED_SECURITY_TAG             : "complaint-view-deleted-security-tag"          //param: complaintId, personAssociationId, securityTagId
    ,VIEW_ADDED_PERSON_ALIAS               : "complaint-view-added-person-alias"            //param: complaintId, personAssociationId, personAlias
    ,VIEW_UPDATED_PERSON_ALIAS             : "complaint-view-updated-person-alias"          //param: complaintId, personAssociationId, personAlias
    ,VIEW_DELETED_PERSON_ALIAS             : "complaint-view-deleted-person-alias"          //param: complaintId, personAssociationId, personAliasId
    ,VIEW_ADDED_ORGANIZATION               : "complaint-view-added-organization"            //param: complaintId, personAssociationId, organization
    ,VIEW_UPDATED_ORGANIZATION             : "complaint-view-updated-organization"          //param: complaintId, personAssociationId, organization
    ,VIEW_DELETED_ORGANIZATION             : "complaint-view-deleted-organization"          //param: complaintId, personAssociationId, organizationId

    ,VIEW_ADDED_NOTE                       : "complaint-view-added-note"                    //param : note
    ,VIEW_UPDATED_NOTE                     : "complaint-view-updated-note"                  //param : note
    ,VIEW_DELETED_NOTE                     : "complaint-view-deleted-note"                  //param : deletedNoteId

    ,VIEW_ADDED_DOCUMENT                   : "complaint-view-added-document"                //param: complaintId

    ,VIEW_ADDED_PARTICIPANT                : "complaint-view-added-participant"             //param: complaintId, participant
    ,VIEW_UPDATED_PARTICIPANT              : "complaint-view-updated-participant"           //param: complaintId, participant
    ,VIEW_DELETED_PARTICIPANT              : "complaint-view-deleted-participant"           //param: complaintId, participantId

    ,VIEW_ADDED_LOCATION                   : "complaint-view-added-location"                //param: complaintId, location
    ,VIEW_UPDATED_LOCATION                 : "complaint-view-updated-location"              //param: complaintId, location
    ,VIEW_DELETED_LOCATION                 : "complaint-view-deleted-location"              //param: complaintId, location



    ,modelAddedPersonAssociation : function(complaintId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_PERSON_ASSOCIATION, complaintId, personAssociation);
    }
    ,modelUpdatedPersonAssociation : function(complaintId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_PERSON_ASSOCIATION, complaintId, personAssociation);
    }
    ,modelDeletedPersonAssociation : function(complaintId, personAssociationId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_PERSON_ASSOCIATION, complaintId, personAssociationId);
    }
    ,modelAddedAddress : function(complaintId, personAssociationId, address) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_ADDRESS, complaintId, personAssociationId, address);
    }
    ,modelUpdatedAddress : function(complaintId, personAssociationId, address) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ADDRESS, complaintId, personAssociationId, address);
    }
    ,modelDeletedAddress : function(complaintId, personAssociationId, addressId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_ADDRESS, complaintId, personAssociationId, addressId);
    }
    ,modelAddedContactMethod : function(complaintId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_CONTACT_METHOD, complaintId, personAssociationId, contactMethod);
    }
    ,modelUpdatedContactMethod : function(complaintId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_CONTACT_METHOD, complaintId, personAssociationId, contactMethod);
    }
    ,modelDeletedContactMethod : function(complaintId, personAssociationId, contactMethodId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_CONTACT_METHOD, complaintId, personAssociationId, contactMethodId);
    }
    ,modelAddedSecurityTag : function(complaintId, personAssociationId, securityTag) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_SECURITY_TAG, complaintId, personAssociationId, securityTag);
    }
    ,modelUpdatedSecurityTag : function(complaintId, personAssociationId, securityTag) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_SECURITY_TAG, complaintId, personAssociationId, securityTag);
    }
    ,modelDeletedSecurityTag : function(complaintId, personAssociationId, securityTagId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_SECURITY_TAG, complaintId, personAssociationId, securityTagId);
    }
    ,modelAddedPersonAlias : function(complaintId, personAssociationId, personAlias) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_PERSON_ALIAS, complaintId, personAssociationId, personAlias);
    }
    ,modelUpdatedPersonAlias : function(complaintId, personAssociationId, personAlias) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_PERSON_ALIAS, complaintId, personAssociationId, personAlias);
    }
    ,modelDeletedPersonAlias : function(complaintId, personAssociationId, personAliasId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_PERSON_ALIAS, complaintId, personAssociationId, personAliasId);
    }
    ,modelAddedOrganization : function(complaintId, personAssociationId, organization) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_ORGANIZATION, complaintId, personAssociationId, organization);
    }
    ,modelUpdatedOrganization : function(complaintId, personAssociationId, organization) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ORGANIZATION, complaintId, personAssociationId, organization);
    }
    ,modelDeletedOrganization : function(complaintId, personAssociationId, organizationId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_ORGANIZATION, complaintId, personAssociationId, organizationId);
    }

    ,modelUploadedDocuments: function(documents) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPLOADED_DOCUMENTS, documents);
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

    ,modelAddedDocument: function(complaintId) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_DOCUMENT, complaintId);
    }

    ,modelRetrievedTasks: function(tasks) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_TASKS, tasks);
    }
    ,modelAddedParticipant : function(complaint, participant) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_PARTICIPANT, complaint, participant);
    }
    ,modelUpdatedParticipant : function(complaint, participant) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_PARTICIPANT, complaint, participant);
    }
    ,modelDeletedParticipant : function(complaint, participantId) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_PARTICIPANT, complaint, participantId);
    }
    ,modelAddedLocation : function(complaint) {
        Acm.Dispatcher.fireEvent(this.MODEL_ADDED_LOCATION, complaint);
    }
    ,modelUpdatedLocation : function(complaint) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_LOCATION, complaint);
    }
    ,modelDeletedLocation : function(complaint) {
        Acm.Dispatcher.fireEvent(this.MODEL_DELETED_LOCATION, complaint);
    }
    ,viewAddedPersonAssociation : function(complaintId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_PERSON_ASSOCIATION, complaintId, personAssociation);
    }
    ,viewUpdatedPersonAssociation : function(complaintId, personAssociation) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_PERSON_ASSOCIATION, complaintId, personAssociation);
    }
    ,viewDeletedPersonAssociation : function(complaintId, personAssociationId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_PERSON_ASSOCIATION, complaintId, personAssociationId);
    }
    ,viewAddedAddress : function(complaintId, personAssociationId, address) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_ADDRESS, complaintId, personAssociationId, address);
    }
    ,viewUpdatedAddress : function(complaintId, personAssociationId, address) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_ADDRESS, complaintId, personAssociationId, address);
    }
    ,viewDeletedAddress : function(complaintId, personAssociationId, addressId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_ADDRESS, complaintId, personAssociationId, addressId);
    }
    ,viewAddedContactMethod : function(complaintId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_CONTACT_METHOD, complaintId, personAssociationId, contactMethod);
    }
    ,viewUpdatedContactMethod : function(complaintId, personAssociationId, contactMethod) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_CONTACT_METHOD, complaintId, personAssociationId, contactMethod);
    }
    ,viewDeletedContactMethod : function(complaintId, personAssociationId, contactMethodId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_CONTACT_METHOD, complaintId, personAssociationId, contactMethodId);
    }
    ,viewAddedSecurityTag : function(complaintId, personAssociationId, securityTag) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_SECURITY_TAG, complaintId, personAssociationId, securityTag);
    }
    ,viewUpdatedSecurityTag : function(complaintId, personAssociationId, securityTag) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_SECURITY_TAG, complaintId, personAssociationId, securityTag);
    }
    ,viewDeletedSecurityTag : function(complaintId, personAssociationId, securityTagId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_SECURITY_TAG, complaintId, personAssociationId, securityTagId);
    }
    ,viewAddedPersonAlias : function(complaintId, personAssociationId, personAlias) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_PERSON_ALIAS, complaintId, personAssociationId, personAlias);
    }
    ,viewUpdatedPersonAlias : function(complaintId, personAssociationId, personAlias) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_PERSON_ALIAS, complaintId, personAssociationId, personAlias);
    }
    ,viewDeletedPersonAlias : function(complaintId, personAssociationId, personAliasId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_PERSON_ALIAS, complaintId, personAssociationId, personAliasId);
    }
    ,viewAddedOrganization : function(complaintId, personAssociationId, organization) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_ORGANIZATION, complaintId, personAssociationId, organization);
    }
    ,viewUpdatedOrganization : function(complaintId, personAssociationId, organization) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_ORGANIZATION, complaintId, personAssociationId, organization);
    }
    ,viewDeletedOrganization : function(complaintId, personAssociationId, organizationId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_ORGANIZATION, complaintId, personAssociationId, organizationId);
    }

    ,viewAddedNote: function(note){
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_NOTE, note);
    }

    ,viewUpdatedNote: function(note){
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_NOTE, note);
    }

    ,viewDeletedNote: function(note){
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_NOTE, note);
    }
    ,viewAddedDocument: function(complaintId) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_DOCUMENT, complaintId);
    }
    ,viewAddedParticipant: function(complaint, participant) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_PARTICIPANT, complaint, participant);
    }
    ,viewUpdatedParticipant: function(complaint, participant) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_PARTICIPANT, complaint, participant);
    }
    ,viewDeletedParticipant: function(complaint, participantId) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_PARTICIPANT, complaint, participantId);
    }

    ,viewAddedLocation: function(complaint) {
        Acm.Dispatcher.fireEvent(this.VIEW_ADDED_LOCATION, complaint);
    }
    ,viewUpdatedLocation: function(complaint) {
        Acm.Dispatcher.fireEvent(this.VIEW_UPDATED_LOCATION, complaint);
    }
    ,viewDeletedLocation: function(complaint) {
        Acm.Dispatcher.fireEvent(this.VIEW_DELETED_LOCATION, complaint);
    }
};

