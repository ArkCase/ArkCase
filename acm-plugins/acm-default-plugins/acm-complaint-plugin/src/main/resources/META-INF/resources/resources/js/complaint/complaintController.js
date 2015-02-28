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

    ,MODEL_FOUND_ASSIGNEES                 : "complaint-model-found-assignees"
    ,modelFoundAssignees: function(assignees) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_ASSIGNEES, assignees);
    }
    ,MODEL_FOUND_SUBJECT_TYPES             : "complaint-model-found-complaint-types"
    ,modelFoundComplaintTypes: function(complaintTypes) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_SUBJECT_TYPES, complaintTypes);
    }
    ,MODEL_FOUND_PRIORITIES                : "complaint-model-found-priorities"
    ,modelFoundPriorities: function(priorities) {
        Acm.Dispatcher.fireEvent(this.MODEL_FOUND_PRIORITIES, priorities);
    }

    ,MODEL_RETRIEVED_COMPLAINT             : "complaint-model-retrieved-detail"             //param: complaint
    ,MODEL_SAVED_COMPLAINT                 : "complaint-model-saved-complaint"              //param: complaint
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


    ,VIEW_ADDED_PERSON_ASSOCIATION         : "complaint-view-added-person-association"      //param: complaintFileId, personAssociation
    ,VIEW_UPDATED_PERSON_ASSOCIATION       : "complaint-view-updated-person-association"    //param: complaintFileId, personAssociation
    ,VIEW_DELETED_PERSON_ASSOCIATION       : "complaint-view-deleted-person-association"    //param: complaintFileId, personAssociationId
    ,VIEW_ADDED_ADDRESS                    : "complaint-view-added-address"                 //param: complaintFileId, personAssociationId, address
    ,VIEW_UPDATED_ADDRESS                  : "complaint-view-updated-address"               //param: complaintFileId, personAssociationId, address
    ,VIEW_DELETED_ADDRESS                  : "complaint-view-deleted-address"               //param: complaintFileId, personAssociationId, addressId
    ,VIEW_ADDED_CONTACT_METHOD             : "complaint-view-added-contact-method"          //param: complaintFileId, personAssociationId, contactMethod
    ,VIEW_UPDATED_CONTACT_METHOD           : "complaint-view-updated-contact-method"        //param: complaintFileId, personAssociationId, contactMethod
    ,VIEW_DELETED_CONTACT_METHOD           : "complaint-view-deleted-contact-method"        //param: complaintFileId, personAssociationId, contactMethodId
    ,VIEW_ADDED_SECURITY_TAG               : "complaint-view-added-security-tag"            //param: complaintFileId, personAssociationId, securityTag
    ,VIEW_UPDATED_SECURITY_TAG             : "complaint-view-updated-security-tag"          //param: complaintFileId, personAssociationId, securityTag
    ,VIEW_DELETED_SECURITY_TAG             : "complaint-view-deleted-security-tag"          //param: complaintFileId, personAssociationId, securityTagId
    ,VIEW_ADDED_PERSON_ALIAS               : "complaint-view-added-person-alias"            //param: complaintFileId, personAssociationId, personAlias
    ,VIEW_UPDATED_PERSON_ALIAS             : "complaint-view-updated-person-alias"          //param: complaintFileId, personAssociationId, personAlias
    ,VIEW_DELETED_PERSON_ALIAS             : "complaint-view-deleted-person-alias"          //param: complaintFileId, personAssociationId, personAliasId
    ,VIEW_ADDED_ORGANIZATION               : "complaint-view-added-organization"            //param: complaintFileId, personAssociationId, organization
    ,VIEW_UPDATED_ORGANIZATION             : "complaint-view-updated-organization"          //param: complaintFileId, personAssociationId, organization
    ,VIEW_DELETED_ORGANIZATION             : "complaint-view-deleted-organization"          //param: complaintFileId, personAssociationId, organizationId

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


    ,modelRetrievedComplaint: function(complaint) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_COMPLAINT, complaint);
    }
    ,modelSavedComplaint : function(complaint) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_COMPLAINT, complaint);
    }
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

