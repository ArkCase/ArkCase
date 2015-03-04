/**
 * Complaint.Model
 *
 * @author jwu
 */
Complaint.Model = Complaint.Model || {
    create : function() {
        if (Complaint.Service.create)                     {Complaint.Service.create();}
        if (Complaint.Model.Lookup.create)                {Complaint.Model.Lookup.create();}
        if (Complaint.Model.Tree.create)                  {Complaint.Model.Tree.create();}
//        if (Complaint.Model.Object.create)                {Complaint.Model.Object.create();}
//        if (Complaint.Model.TopPanel.create)              {Complaint.Model.TopPanel.create();}
//        if (Complaint.Model.Action.create)                {Complaint.Model.Action.create();}
        if (Complaint.Model.Detail.create)                {Complaint.Model.Detail.create();}
        if (Complaint.Model.People.create)                {Complaint.Model.People.create();}
        if (Complaint.Model.Documents.create)             {Complaint.Model.Documents.create();}
        if (Complaint.Model.Notes.create)                 {Complaint.Model.Notes.create();}
        if (Complaint.Model.References.create)            {Complaint.Model.References.create();}
        if (Complaint.Model.Tasks.create)                 {Complaint.Model.Tasks.create();}
        if (Complaint.Model.Location.create)              {Complaint.Model.Location.create();}
        if (Complaint.Model.History.create)               {Complaint.Model.History.create();}
    }
    ,onInitialized: function() {
        if (Complaint.Service.onInitialized)              {Complaint.Service.onInitialized();}
        if (Complaint.Model.Lookup.onInitialized)         {Complaint.Model.Lookup.onInitialized();}
        if (Complaint.Model.Tree.onInitialized)           {Complaint.Model.Tree.onInitialized();}
//        if (Complaint.Model.Object.onInitialized)         {Complaint.Model.Object.onInitialized();}
//        if (Complaint.Model.TopPanel.onInitialized)       {Complaint.Model.TopPanel.onInitialized();}
//        if (Complaint.Model.Action.onInitialized)         {Complaint.Model.Action.onInitialized();}
        if (Complaint.Model.Detail.onInitialized)         {Complaint.Model.Detail.onInitialized();}
        if (Complaint.Model.People.onInitialized)         {Complaint.Model.People.onInitialized();}
        if (Complaint.Model.Documents.onInitialized)      {Complaint.Model.Documents.onInitialized();}
        if (Complaint.Model.Notes.onInitialized)          {Complaint.Model.Notes.onInitialized();}
        if (Complaint.Model.References.onInitialized)     {Complaint.Model.References.onInitialized();}
        if (Complaint.Model.Tasks.onInitialized)          {Complaint.Model.Tasks.onInitialized();}
        if (Complaint.Model.Location.onInitialized)       {Complaint.Model.Location.onInitialized();}
        if (Complaint.Model.History.onInitialized)        {Complaint.Model.History.onInitialized();}
    }

    ,interface: {
        apiListObjects: function() {
            return "/api/latest/plugin/search/COMPLAINT";
        }
        ,apiRetrieveObject: function(nodeType, objId) {
            return "/api/latest/plugin/complaint/byId/" + objId;
        }
        ,apiSaveObject: function(nodeType, objId) {
            return "/api/latest/plugin/complaint/";
        }
        ,nodeId: function(objSolr) {
            return objSolr.object_id_s;
            //return parseInt(objSolr.object_id_s);
        }
        ,nodeType: function(objSolr) {
            return Complaint.Model.DOC_TYPE_COMPLAINT;
        }
        ,nodeTitle: function(objSolr) {
            return Acm.goodValue(objSolr.title_parseable);
        }
        ,nodeToolTip: function(objSolr) {
            return Acm.goodValue(objSolr.name);
        }
        ,objToSolr: function(objData) {
            var solr = {};
            solr.author = objData.creator;
            solr.author_s = objData.creator;
            solr.create_tdt = objData.created;
            solr.last_modified_tdt = objData.modified;
            solr.modifier_s = objData.modifier;
            solr.name = objData.complaintNumber;
            solr.object_id_s = objData.complaintId;
            solr.object_type_s = Complaint.Model.DOC_TYPE_COMPLAINT;
            solr.owner_s = objData.creator;
            solr.status_s = objData.status;
            solr.title_parseable = objData.complaintTitle;
            return solr;
        }
        ,validateObjData: function(data) {
            return Complaint.Model.Detail.validateComplaint(data);
        }
        ,nodeTypeMap: function() {
            return Complaint.Model.Tree.Key.nodeTypeMap;
        }
    }

    ,DOC_TYPE_COMPLAINT  : "COMPLAINT"
    ,DOC_TYPE_FILE       : "FILE"

    ,getComplaintId : function() {
        return ObjNav.Model.getObjectId();
    }
    ,getComplaint: function() {
        var objId = ObjNav.Model.getObjectId();
        return ObjNav.Model.Detail.getCacheObject(Complaint.Model.DOC_TYPE_COMPLAINT, objId);
    }


    ,Tree: {
        create: function() {
            if (Complaint.Model.Tree.Key.create)        {Complaint.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (Complaint.Model.Tree.Key.onInitialized)        {Complaint.Model.Tree.Key.onInitialized();}
        }

        ,Key: {
            create: function() {
            }
            ,onInitialized: function() {
            }

            ,NODE_TYPE_PART_DETAILS      : "det"
            ,NODE_TYPE_PART_LOCATION     : "loc"
            //,NODE_TYPE_PART_INITIATOR    : "i"
            ,NODE_TYPE_PART_PEOPLE       : "ppl"
            ,NODE_TYPE_PART_DOCUMENTS    : "doc"
            ,NODE_TYPE_PART_TASKS        : "task"
            ,NODE_TYPE_PART_NOTES        : "note"
            ,NODE_TYPE_PART_PARTICIPANTS : "part"
            ,NODE_TYPE_PART_REFERENCES   : "ref"
            ,NODE_TYPE_PART_HISTORY      : "his"


            ,nodeTypeMap: [
                {nodeType: "prevPage"    ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"   ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"          ,icon: ""                 ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/COMPLAINT"        ,icon: "i i-notice"
                    ,tabIds: ["tabAction"
                        ,"tabDetail"
                        ,"tabLocation"
                        ,"tabInitiator"
                        ,"tabPeople"
                        ,"tabNotes"
                        ,"tabDocuments"
                        ,"tabTasks"
                        ,"tabRefs"
//                    ,"tabRefComplaints"
//                    ,"tabRefCases"
//                    ,"tabRefTasks"
//                    ,"tabRefDocuments"
//                    ,"tabApprovers"
//                    ,"tabCollaborators"
//                    ,"tabWatchers"
                        ,"tabParticipants"
                        ,"tabHistory"
                    ]}
                ,{nodeType: "p/COMPLAINT/det"      ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/COMPLAINT/loc"      ,icon: "",tabIds: ["tabLocation"]}
                //,{nodeType: "p/COMPLAINT/i"      ,icon: "",tabIds: ["tabInitiator"]}
                ,{nodeType: "p/COMPLAINT/ppl"      ,icon: "",tabIds: ["tabPeople"]}
                ,{nodeType: "p/COMPLAINT/doc"      ,icon: "",tabIds: ["tabDocuments"]}
                ,{nodeType: "p/COMPLAINT/task"     ,icon: "",tabIds: ["tabTasks"]}
                ,{nodeType: "p/COMPLAINT/note"     ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "p/COMPLAINT/part"     ,icon: "",tabIds: ["tabParticipants"]}
                ,{nodeType: "p/COMPLAINT/ref"      ,icon: "",tabIds: ["tabRefs"]}
                ,{nodeType: "p/COMPLAINT/his"      ,icon: "",tabIds: ["tabHistory"]}
            ]
        }
    }


//    ,Object: {
//        create : function() {
//        }
//
//        ,onInitialized: function() {
//        }
//        ,getCache: function(complaintId) {
//            return ObjNav.Model.Detail.getCacheObject(Complaint.Model.DOC_TYPE_COMPLAINT, complaintId);
//        }
//        ,putCache: function(complaintId, complaint) {
//            ObjNav.Model.Detail.putCacheObject(Complaint.Model.DOC_TYPE_COMPLAINT, complaintId, complaint);
//        }
//
//    }
    ,Detail: {
        create : function() {
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_CHANGED_COMPLAINT_TITLE     , this.onViewChangedComplaintTitle);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_CHANGED_INCIDENT_DATE       , this.onViewChangedIncidentDate);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_CHANGED_ASSIGNEE            , this.onViewChangedAssignee);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_CHANGED_COMPLAINT_TYPE      , this.onViewChangedComplaintType);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_CHANGED_PRIORITY            , this.onViewChangedPriority);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_CHANGED_DETAIL              , this.onViewChangedDetail);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_CHANGED_RESTRICTION         , this.onViewChangedRestriction);
        }

        ,onInitialized: function() {
        }

        ,onViewChangedComplaint: function(complaintId) {
            ObjNav.Service.retrieveObject(Complaint.Model.DOC_TYPE_COMPLAINT, complaintId);
        }
        ,onViewChangedComplaintTitle: function(complaintId, title) {
            Complaint.Service.Detail.saveComplaintTitle(complaintId, title);
        }

        ,onViewChangedIncidentDate: function(complaintId, incidentDate) {
            Complaint.Service.Detail.saveIncidentDate(complaintId, incidentDate);
        }
        ,onViewChangedAssignee: function(complaintId, assignee) {
            Complaint.Service.Detail.saveAssignee(complaintId, assignee);
        }
        ,onViewChangedComplaintType: function(complaintId, caseType) {
            Complaint.Service.Detail.saveComplaintType(complaintId, caseType);
        }
        ,onViewChangedPriority: function(complaintId, priority) {
            Complaint.Service.Detail.savePriority(complaintId, priority);
        }
        ,onViewChangedDetail: function(complaintId, details) {
            Complaint.Service.Detail.saveDetail(complaintId, details);
        }
        ,onViewChangedRestriction: function(complaintId, restriction) {
            Complaint.Service.Detail.updateComplaintRestriction(complaintId, restriction);
        }

        ,getAssignee: function(complaint) {
            var assignee = null;
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                if (Acm.isArray(complaint.participants)) {
                    for (var i = 0; i < complaint.participants.length; i++) {
                        var participant =  complaint.participants[i];
                        if ("assignee" == participant.participantType) {
                            assignee = participant.participantLdapId;
                            break;
                        }
                    }
                }
            }
            return assignee;
        }
        ,setAssignee: function(complaint, assignee) {
            if (complaint) {
                if (!Acm.isArray(complaint.participants)) {
                    complaint.participants = [];
                }

                for (var i = 0; i < complaint.participants.length; i++) {
                    if ("assignee" == complaint.participants[i].participantType) {
                        complaint.participants[i].participantLdapId = assignee;
                        return;
                    }
                }

                var participant = {};
                participant.participantType = "assignee";
                participant.participantLdapId = assignee;
                complaint.participants.push(participant);
            }
        }

        ,getCacheComplaint: function(complaintId) {
            if (0 >= complaintId) {
                return null;
            }
            return ObjNav.Model.Detail.getCacheObject(Complaint.Model.DOC_TYPE_COMPLAINT, complaintId);
        }
        ,putCacheComplaint: function(complaintId, complaint) {
            ObjNav.Model.Detail.putCacheObject(Complaint.Model.DOC_TYPE_COMPLAINT, complaintId, complaint);
        }

        ,validateComplaint: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.complaintId) || Acm.isEmpty(data.complaintNumber)) {
                return false;
            }
            if (!Acm.isArray(data.childObjects)) {
                return false;
            }
            if (!Acm.isArray(data.participants)) {
                return false;
            }
            if (!Acm.isArray(data.personAssociations)) {
                return false;
            }
            if (Acm.isEmpty(data.originator)) {
                return false;
            }

            return true;
        }

    }

//if (Complaint.Model.Object.create)                {Complaint.Model.Object.create();}
//if (Complaint.Model.TopPanel.create)              {Complaint.Model.TopPanel.create();}
//if (Complaint.Model.Action.create)                {Complaint.Model.Action.create();}
//if (Complaint.Model.Detail.create)                {Complaint.Model.Detail.create();}
//if (Complaint.Model.People.create)                {Complaint.Model.People.create();}

    ,People: {
        create : function() {
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_ADDED_PERSON_ASSOCIATION    , this.onViewAddedPersonAssociation);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_UPDATED_PERSON_ASSOCIATION  , this.onViewUpdatedPersonAssociation);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_DELETED_PERSON_ASSOCIATION  , this.onViewDeletedPersonAssociation);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_ADDED_ADDRESS               , this.onViewAddedAddress);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_UPDATED_ADDRESS             , this.onViewUpdatedAddress);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_DELETED_ADDRESS             , this.onViewDeletedAddress);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_ADDED_CONTACT_METHOD        , this.onViewAddedContactMethod);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_UPDATED_CONTACT_METHOD      , this.onViewUpdatedContactMethod);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_DELETED_CONTACT_METHOD      , this.onViewDeletedContactMethod);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_ADDED_SECURITY_TAG          , this.onViewAddedSecurityTag);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_UPDATED_SECURITY_TAG        , this.onViewUpdatedSecurityTag);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_DELETED_SECURITY_TAG        , this.onViewDeletedSecurityTag);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_ADDED_PERSON_ALIAS          , this.onViewAddedPersonAlias);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_UPDATED_PERSON_ALIAS        , this.onViewUpdatedPersonAlias);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_DELETED_PERSON_ALIAS        , this.onViewDeletedPersonAlias);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_ADDED_ORGANIZATION          , this.onViewAddedOrganization);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_UPDATED_ORGANIZATION        , this.onViewUpdatedOrganization);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_DELETED_ORGANIZATION        , this.onViewDeletedOrganization);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_ADDED_PARTICIPANT           , this.onViewAddedParticipant);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_UPDATED_PARTICIPANT         , this.onViewUpdatedParticipant);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_DELETED_PARTICIPANT         , this.onViewDeletedParticipant);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_ADDED_LOCATION              , this.onViewAddedLocation);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_UPDATED_LOCATION            , this.onViewUpdatedLocation);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_DELETED_LOCATION            , this.onViewDeletedLocation);
        }
        ,onInitialized: function() {
        }

        ,onViewAddedPersonAssociation: function(complaintId, personAssociation) {
            var pa = Complaint.Model.Detail.newPersonAssociation();
            pa.parentType = Complaint.Model.getObjectType();
            pa.parentId = complaintId;
            pa.personType = personAssociation.personType;
            //pa.personDescription = personAssociation.personDescription;
            pa.person.title = personAssociation.person.title;
            pa.person.givenName = personAssociation.person.givenName;
            pa.person.familyName = personAssociation.person.familyName;
            Complaint.Service.People.addPersonAssociation(complaintId, pa);
        }
        ,onViewUpdatedPersonAssociation: function(complaintId, personAssociation) {
            Complaint.Service.People.updatePersonAssociation(complaintId, personAssociation);
        }
        ,onViewDeletedPersonAssociation: function(complaintId, personAssociationId) {
            Complaint.Service.People.deletePersonAssociation(complaintId, personAssociationId);
        }
        ,onViewAddedAddress: function(complaintId, personAssociationId, address) {
            Complaint.Service.People.addAddress(complaintId, personAssociationId, address);
        }
        ,onViewUpdatedAddress: function(complaintId, personAssociationId, address) {
            Complaint.Service.People.updateAddress(complaintId, personAssociationId, address);
        }
        ,onViewDeletedAddress: function(complaintId, personAssociationId, addressId) {
            Complaint.Service.People.deleteAddress(complaintId, personAssociationId, addressId);
        }
        ,onViewAddedContactMethod: function(complaintId, personAssociationId, contactMethod) {
            Complaint.Service.People.addContactMethod(complaintId, personAssociationId, contactMethod);
        }
        ,onViewUpdatedContactMethod: function(complaintId, personAssociationId, contactMethod) {
            Complaint.Service.People.updateContactMethod(complaintId, personAssociationId, contactMethod);
        }
        ,onViewDeletedContactMethod: function(complaintId, personAssociationId, contactMethodId) {
            Complaint.Service.People.deleteContactMethod(complaintId, personAssociationId, contactMethodId);
        }
        ,onViewAddedSecurityTag: function(complaintId, personAssociationId, securityTag) {
            Complaint.Service.People.addSecurityTag(complaintId, personAssociationId, securityTag);
        }
        ,onViewUpdatedSecurityTag: function(complaintId, personAssociationId, securityTag) {
            Complaint.Service.People.updateSecurityTag(complaintId, personAssociationId, securityTag);
        }
        ,onViewDeletedSecurityTag: function(complaintId, personAssociationId, securityTagId) {
            Complaint.Service.People.deleteSecurityTag(complaintId, personAssociationId, securityTagId);
        }
        ,onViewAddedPersonAlias: function(complaintId, personAssociationId, personAlias) {
            Complaint.Service.People.addPersonAlias(complaintId, personAssociationId, personAlias);
        }
        ,onViewUpdatedPersonAlias: function(complaintId, personAssociationId, personAlias) {
            Complaint.Service.People.updatePersonAlias(complaintId, personAssociationId, personAlias);
        }
        ,onViewDeletedPersonAlias: function(complaintId, personAssociationId, personAliasId) {
            Complaint.Service.People.deletePersonAlias(complaintId, personAssociationId, personAliasId);
        }
        ,onViewAddedOrganization: function(complaintId, personAssociationId, organization) {
            Complaint.Service.People.addOrganization(complaintId, personAssociationId, organization);
        }
        ,onViewUpdatedOrganization: function(complaintId, personAssociationId, organization) {
            Complaint.Service.People.updateOrganization(complaintId, personAssociationId, organization);
        }
        ,onViewDeletedOrganization: function(complaintId, personAssociationId, organizationId) {
            Complaint.Service.People.deleteOrganization(complaintId, personAssociationId, organizationId);
        }
        ,onViewAddedParticipant: function(complaintId, participant) {
            Complaint.Service.People.addParticipant(complaintId, participant);
        }
        ,onViewUpdatedParticipant: function(complaintId, participant) {
            Complaint.Service.People.updateParticipant(complaintId, participant);
        }
        ,onViewDeletedParticipant: function(complaintId, participantId) {
            Complaint.Service.People.deleteParticipant(complaintId, participantId);
        }
        ,onViewAddedLocation: function(complaint) {
            Complaint.Service.People.addLocation(complaint);
        }
        ,onViewUpdatedLocation: function(complaint) {
            Complaint.Service.People.updateLocation(complaint);
        }
        ,onViewDeletedLocation: function(complaint) {
            Complaint.Service.People.deleteLocation(complaint);
        }

        ,newPersonAssociation: function() {
            return {
                id: null
                ,personType: ""
                ,parentId:null
                ,parentType:""
                ,personDescription: ""
                ,notes:""
                ,person:{
                    id: null
                    ,title: ""
                    ,givenName: ""
                    ,familyName: ""
                    ,company: ""
//                    ,hairColor:""
//                     ,eyeColor:""
//                     ,heightInInches:null
                    ,weightInPounds:null
//                    ,dateOfBirth:null
//                     ,dateMarried:null
                    ,addresses: []
                    ,contactMethods: []
                    ,securityTags: []
                    ,personAliases: []
                    ,organizations: []
                }
            };
        }
        ,findPersonAssociation: function(personAssociationId, personAssociations) {
            var personAssociation = null;
            for (var i = 0; i < personAssociations.length; i++) {
                if (personAssociationId == personAssociations[i].id) {
                    personAssociation = personAssociations[i];
                    break;
                }
            }
            return personAssociation;
        }

        ,validatePersonAssociation: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.person)) {
                return false;
            }
            if (!Acm.isArray(data.person.contactMethods)) {
                return false;
            }
            if (!Acm.isArray(data.person.addresses)) {
                return false;
            }
            if (!Acm.isArray(data.person.securityTags)) {
                return false;
            }
            if (!Acm.isArray(data.person.personAliases)) {
                return false;
            }
            if (!Acm.isArray(data.person.organizations)) {
                return false;
            }
            return true;
        }

    }

    ,Documents: {
        create : function() {
            this.cacheDocuments = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
        }
        ,validateUploadedDocuments: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            return true;
        }
        ,validateExistingDocuments: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.childObjects)) {
                return false;
            }
            if (Acm.isNotArray(data.childObjects)) {
                return false;
            }
            return true;
        }
        ,validateDocumentRecord: function(data) {
            if (Acm.isEmpty(data.targetId)) {
                return false;
            }
            if (Acm.isEmpty(data.targetName)) {
                return false;
            }
            if (Acm.isEmpty(data.created)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.status)) {
                return false;
            }
            return true;
        }
    }

    ,Notes: {
        create : function() {
            this.cacheNoteList = new Acm.Model.CacheFifo();

            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_ADDED_NOTE     , this.onViewAddedNote);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_UPDATED_NOTE   , this.onViewUpdatedNote);
            Acm.Dispatcher.addEventListener(Complaint.Controller.VIEW_DELETED_NOTE   , this.onViewDeletedNote);
        }
        ,onInitialized: function() {
        }

        ,onViewAddedNote: function(note) {
            Complaint.Service.Notes.addNote(note);
        }
        ,onViewUpdatedNote: function(note) {
            Complaint.Service.Notes.updateNote(note);
        }
        ,onViewDeletedNote: function(noteId) {
            Complaint.Service.Notes.deleteNote(noteId);
        }

        ,validateNotes: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }

        ,validateNote: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
//            if (Acm.isEmpty(data.id)) {
//             return false;
//             }
            if (Acm.isEmpty(data.parentId)) {
                return false;
            }
            if (Acm.isEmpty(data.note)) {
                return false;
            }
            return true;
        }

        ,validateDeletedNote: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedNoteId)) {
                return false;
            }
            return true;
        }
    }

    ,History: {
        create : function() {
            this.cacheHistory = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
        }
        ,validateHistory: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.resultPage)) {
                return false;
            }
            if (Acm.isNotArray(data.resultPage)) {
                return false;
            }
            if (Acm.isEmpty(data.totalCount)) {
                return false;
            }
            return true;
        }
        ,validateEvent: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.eventDate)) {
                return false;
            }
            if (Acm.isEmpty(data.eventType)) {
                return false;
            }
            if (Acm.isEmpty(data.objectId)) {
                return false;
            }
            if (Acm.isEmpty(data.objectType)) {
                return false;
            }
            if (Acm.isEmpty(data.userId)) {
                return false;
            }
            return true;
        }
    }

    ,References: {
        create : function() {
            this.cacheReferenceList = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
        }
        ,validateExistingDocuments: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.childObjects)) {
                return false;
            }
            if (Acm.isNotArray(data.childObjects)) {
                return false;
            }
            return true;
        }
        ,validateReferenceRecord: function(data) {
            if (Acm.isEmpty(data.associationType)) {
                return false;
            }
            if("REFERENCE" != data.associationType){
                return false;
            }
            if (Acm.isEmpty(data.targetId)) {
                return false;
            }
            if (Acm.isEmpty(data.targetName)) {
                return false;
            }
            if (Acm.isEmpty(data.created)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.status)) {
                return false;
            }
            return true;
        }
    }

    ,Tasks: {
        create : function() {
            this.cacheTaskSolr = new Acm.Model.CacheFifo();
            this.cacheTasks = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
        }
        ,validateTask: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            /*if (Acm.isEmpty(data.id) || Acm.isEmpty(data.caseNumber)) {
             return false;
             }
             if (!Acm.isArray(data.childObjects)) {
             return false;
             }
             if (!Acm.isArray(data.participants)) {
             return false;
             }
             if (!Acm.isArray(data.personAssociations)) {
             return false;
             }*/
            return true;
        }
    }

    ,Participants: {
        create : function() {

        }
        ,onInitialized: function() {
        }
        ,validateParticipants: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            return true;
        }
        ,validateParticipantRecord: function(data){
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.objectType)) {
                return false;
            }
            if (Acm.isEmpty(data.objectId)) {
                return false;
            }
            if (Acm.isEmpty(data.participantType)) {
                return false;
            }
            if (Acm.isEmpty(data.participantLdapId)) {
                return false;
            }
            return true;
        }
    }

    ,Location: {
        create : function() {

        }
        ,onInitialized: function() {
        }
        ,validateLocation: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            /*if (Acm.isEmpty(data.address)) {
             return false;
             }
             if (Acm.isEmpty(data.type)) {
             return false;
             }
             if (Acm.isEmpty(data.city)) {
             return false;
             }
             if (Acm.isEmpty(data.zip)) {
             return false;
             }
             if (Acm.isEmpty(data.zip)) {
             return false;
             }*/
            return true;
        }
    }

    ,Lookup: {
        create: function() {
            this._assignees      = new Acm.Model.SessionData(Application.SESSION_DATA_COMPLAINT_ASSIGNEES);
            this._complaintTypes = new Acm.Model.SessionData(Application.SESSION_DATA_COMPLAINT_TYPES);
            this._priorities     = new Acm.Model.SessionData(Application.SESSION_DATA_COMPLAINT_PRIORITIES);
        }
        ,onInitialized: function() {
            var assignees = Complaint.Model.Lookup.getAssignees();
            if (Acm.isEmpty(assignees)) {
                Complaint.Service.Lookup.retrieveAssignees();
            } else {
                Complaint.Controller.modelFoundAssignees(assignees);
            }

            var complaintTypes = Complaint.Model.Lookup.getComplaintTypes();
            if (Acm.isEmpty(complaintTypes)) {
                Complaint.Service.Lookup.retrieveComplaintTypes();
            } else {
                Complaint.Controller.modelFoundComplaintTypes(complaintTypes);
            }

            var priorities = Complaint.Model.Lookup.getPriorities();
            if (Acm.isEmpty(priorities)) {
                Complaint.Service.Lookup.retrievePriorities();
            } else {
                Complaint.Controller.modelFoundPriorities(priorities);
            }
        }

        ,PERSON_SUBTABLE_TITLE_CONTACT_METHODS:   "Communication Devices"
        ,PERSON_SUBTABLE_TITLE_ORGANIZATIONS:     "Organizations"
        ,PERSON_SUBTABLE_TITLE_ADDRESSES:         "Locations"
        ,PERSON_SUBTABLE_TITLE_ALIASES:           "Aliases"
        ,PERSON_SUBTABLE_TITLE_SECURITY_TAGS:     "Security Tags"

        ,getAssignees: function() {
            return this._assignees.get();
        }
        ,setAssignees: function(assignees) {
            this._assignees.set(assignees);
        }
        ,getComplaintTypes: function() {
            return this._complaintTypes.get();
        }
        ,setComplaintTypes: function(complaintTypes) {
            this._complaintTypes.set(complaintTypes);
        }
        ,getPriorities: function() {
            return this._priorities.get();
        }
        ,setPriorities: function(priorities) {
            this._priorities.set(priorities);
        }

        ,_personTypes : ['Initiator', 'Complaintant','Subject','Witness','Wrongdoer','Other']
        ,getPersonTypes : function() {
            return this._personTypes;
        }
        ,_personTypesModifiable : ['Complaintant','Subject','Witness','Wrongdoer','Other']
        ,getPersonTypesModifiable : function() {
            return this._personTypesModifiable;
        }

        ,_personTitles : ['Mr', 'Mrs', 'Ms', 'Miss']
        ,getPersonTitles : function() {
            return this._personTitles;
        }

        ,_deviceTypes : ['Home phone', 'Office phone', 'Cell phone', 'Pager',
            'Email','Instant messenger', 'Social media','Website','Blog']
        ,getDeviceTypes : function() {
            return this._deviceTypes;
        }

        ,_organizationTypes : ['Non-profit','Government','Corporation']
        ,getOrganizationTypes : function() {
            return this._organizationTypes;
        }

        ,_locationTypes : ['Business' , 'Home']
        ,getLocationTypes : function() {
            return this._locationTypes;
        }

        ,_aliasTypes : ['FKA' , 'Married']
        ,getAliasTypes : function() {
            return this._aliasTypes;
        }

    }

};

