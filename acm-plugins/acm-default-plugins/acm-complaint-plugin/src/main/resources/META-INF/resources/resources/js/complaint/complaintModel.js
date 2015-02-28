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
        if (Complaint.Model.Detail.create)                {Complaint.Model.Detail.create();}
        if (Complaint.Model.Documents.create)             {Complaint.Model.Documents.create();}
        if (Complaint.Model.Notes.create)                 {Complaint.Model.Notes.create();}
        if (Complaint.Model.References.create)            {Complaint.Model.References.create();}
        if (Complaint.Model.Tasks.create)                 {Complaint.Model.Tasks.create();}
        if (Complaint.Model.Location.create)              {Complaint.Model.Location.create();}
    }
    ,onInitialized: function() {
        if (Complaint.Service.onInitialized)              {Complaint.Service.onInitialized();}
        if (Complaint.Model.Lookup.onInitialized)         {Complaint.Model.Lookup.onInitialized();}
        if (Complaint.Model.Tree.onInitialized)           {Complaint.Model.Tree.onInitialized();}
        if (Complaint.Model.Detail.onInitialized)         {Complaint.Model.Detail.onInitialized();}
        if (Complaint.Model.Documents.onInitialized)      {Complaint.Model.Documents.onInitialized();}
        if (Complaint.Model.Notes.onInitialized)          {Complaint.Model.Notes.onInitialized();}
        if (Complaint.Model.References.onInitialized)     {Complaint.Model.References.onInitialized();}
        if (Complaint.Model.Tasks.onInitialized)          {Complaint.Model.Tasks.onInitialized();}
        if (Complaint.Model.Location.onInitialized)       {Complaint.Model.Tasks.onInitialized();}
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
        ,nodeId: function(obj) {
            return obj.object_id_s;
            //return parseInt(obj.object_id_s);
        }
        ,nodeType: function(obj) {
            return Complaint.Model.DOC_TYPE_COMPLAINT;
        }
        ,nodeTitle: function(obj) {
            return Acm.goodValue(obj.title_parseable);;
        }
        ,nodeToolTip: function(obj) {
            return Acm.goodValue(obj.name);
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

            ,NODE_TYPE_PART_DETAILS      : "d"
            ,NODE_TYPE_PART_LOCATION     : "p"
            ,NODE_TYPE_PART_INITIATOR    : "i"
            ,NODE_TYPE_PART_PEOPLE       : "p"
            ,NODE_TYPE_PART_DOCUMENTS    : "o"
            ,NODE_TYPE_PART_TASKS        : "t"
            ,NODE_TYPE_PART_NOTES        : "n"
            ,NODE_TYPE_PART_PARTICIPANTS : "a"
            ,NODE_TYPE_PART_REFERENCES   : "r"
            ,NODE_TYPE_PART_HISTORY      : "h"


            ,nodeTypeMap: [
                {nodeType: "prevPage"    ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"   ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"          ,icon: ""                 ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/COMPLAINT"        ,icon: "i i-notice" ,tabIds:
                    ["tabCloseComplaintButton"
                    ,"tabDetail"
                    ,"tabLocation"
                    ,"tabInitiator"
                    ,"tabPeople"
                    ,"tabNotes"
                    ,"tabDocuments"
                    ,"tabTasks"
                    ,"tabRefComplaints"
                    ,"tabRefCases"
                    ,"tabRefTasks"
                    ,"tabRefDocuments"
                    ,"tabApprovers"
                    ,"tabCollaborators"
                    ,"tabWatchers"
                    ,"tabParticipants"
                    ,"tabHistory"
                    ]}
                ,{nodeType: "p/COMPLAINT/d"      ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/COMPLAINT/l"      ,icon: "",tabIds: ["tabLocation"]}
                ,{nodeType: "p/COMPLAINT/i"      ,icon: "",tabIds: ["tabInitiator"]}
                ,{nodeType: "p/COMPLAINT/p"      ,icon: "",tabIds: ["tabPeople"]}
                ,{nodeType: "p/COMPLAINT/o"      ,icon: "",tabIds: ["tabDocuments"]}
                ,{nodeType: "p/COMPLAINT/t"      ,icon: "",tabIds: ["tabTasks"]}
                ,{nodeType: "p/COMPLAINT/n"      ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "p/COMPLAINT/a"      ,icon: "",tabIds: ["tabParticipants"]}
                ,{nodeType: "p/COMPLAINT/r"      ,icon: "",tabIds: ["tabRefs"]}
                ,{nodeType: "p/COMPLAINT/h"      ,icon: "",tabIds: ["tabHistory"]}
            ]
        }
    }

    ,Detail: {
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
            Complaint.Service.Detail.addPersonAssociation(complaintId, pa);
        }
        ,onViewUpdatedPersonAssociation: function(complaintId, personAssociation) {
            Complaint.Service.Detail.updatePersonAssociation(complaintId, personAssociation);
        }
        ,onViewDeletedPersonAssociation: function(complaintId, personAssociationId) {
            Complaint.Service.Detail.deletePersonAssociation(complaintId, personAssociationId);
        }
        ,onViewAddedAddress: function(complaintId, personAssociationId, address) {
            Complaint.Service.Detail.addAddress(complaintId, personAssociationId, address);
        }
        ,onViewUpdatedAddress: function(complaintId, personAssociationId, address) {
            Complaint.Service.Detail.updateAddress(complaintId, personAssociationId, address);
        }
        ,onViewDeletedAddress: function(complaintId, personAssociationId, addressId) {
            Complaint.Service.Detail.deleteAddress(complaintId, personAssociationId, addressId);
        }
        ,onViewAddedContactMethod: function(complaintId, personAssociationId, contactMethod) {
            Complaint.Service.Detail.addContactMethod(complaintId, personAssociationId, contactMethod);
        }
        ,onViewUpdatedContactMethod: function(complaintId, personAssociationId, contactMethod) {
            Complaint.Service.Detail.updateContactMethod(complaintId, personAssociationId, contactMethod);
        }
        ,onViewDeletedContactMethod: function(complaintId, personAssociationId, contactMethodId) {
            Complaint.Service.Detail.deleteContactMethod(complaintId, personAssociationId, contactMethodId);
        }
        ,onViewAddedSecurityTag: function(complaintId, personAssociationId, securityTag) {
            Complaint.Service.Detail.addSecurityTag(complaintId, personAssociationId, securityTag);
        }
        ,onViewUpdatedSecurityTag: function(complaintId, personAssociationId, securityTag) {
            Complaint.Service.Detail.updateSecurityTag(complaintId, personAssociationId, securityTag);
        }
        ,onViewDeletedSecurityTag: function(complaintId, personAssociationId, securityTagId) {
            Complaint.Service.Detail.deleteSecurityTag(complaintId, personAssociationId, securityTagId);
        }
        ,onViewAddedPersonAlias: function(complaintId, personAssociationId, personAlias) {
            Complaint.Service.Detail.addPersonAlias(complaintId, personAssociationId, personAlias);
        }
        ,onViewUpdatedPersonAlias: function(complaintId, personAssociationId, personAlias) {
            Complaint.Service.Detail.updatePersonAlias(complaintId, personAssociationId, personAlias);
        }
        ,onViewDeletedPersonAlias: function(complaintId, personAssociationId, personAliasId) {
            Complaint.Service.Detail.deletePersonAlias(complaintId, personAssociationId, personAliasId);
        }
        ,onViewAddedOrganization: function(complaintId, personAssociationId, organization) {
            Complaint.Service.Detail.addOrganization(complaintId, personAssociationId, organization);
        }
        ,onViewUpdatedOrganization: function(complaintId, personAssociationId, organization) {
            Complaint.Service.Detail.updateOrganization(complaintId, personAssociationId, organization);
        }
        ,onViewDeletedOrganization: function(complaintId, personAssociationId, organizationId) {
            Complaint.Service.Detail.deleteOrganization(complaintId, personAssociationId, organizationId);
        }
        ,onViewAddedParticipant: function(complaintId, participant) {
            Complaint.Service.Detail.addParticipant(complaintId, participant);
        }
        ,onViewUpdatedParticipant: function(complaintId, participant) {
            Complaint.Service.Detail.updateParticipant(complaintId, participant);
        }
        ,onViewDeletedParticipant: function(complaintId, participantId) {
            Complaint.Service.Detail.deleteParticipant(complaintId, participantId);
        }
        ,onViewAddedLocation: function(complaint) {
            Complaint.Service.Detail.addLocation(complaint);
        }
        ,onViewUpdatedLocation: function(complaint) {
            Complaint.Service.Detail.updateLocation(complaint);
        }
        ,onViewDeletedLocation: function(complaint) {
            Complaint.Service.Detail.deleteLocation(complaint);
        }

        ,getComplaint: function(caseFileId) {
            if (0 >= caseFileId) {
                return null;
            }

            return ObjNav.Model.Detail.getCacheObject(Complaint.Model.DOC_TYPE_COMPLAINT, caseFileId);
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
            return true;
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


        //,options: App.getContextPath() + '/api/latest/plugin/complaint/types'
        ,_personTypes : ['Complaintant','Subject','Witness','Wrongdoer','Other', 'Initiator']
        ,getPersonTypes : function() {
            return this._personTypes;
        }

        ,_personTitles : ['Mr','mr', 'Mrs','mrs', 'Ms','ms', 'Miss','miss']
        ,getPersonTitles : function() {
            return this._personTitles;
        }

        ,_contactMethodTypes : ['Home phone', 'Office phone', 'Cell phone', 'Pager',
            'Email','Instant messenger', 'Social media','Website','Blog']
        ,getContactMethodTypes : function() {
            return this._contactMethodTypes;
        }

        ,_securityTagTypes : ['Home phone', 'Office phone', 'Cell phone', 'Pager',
            'Email','Instant messenger', 'Social media','Website','Blog']
        ,getSecurityTagTypes : function() {
            return this._securityTagTypes;
        }

        ,_organizationTypes : ['Non-profit','Government','Corporation']
        ,getOrganizationTypes : function() {
            return this._organizationTypes;
        }

        ,_addressTypes : ['Business' , 'Home']
        ,getAddressTypes : function() {
            return this._addressTypes;
        }

        ,_aliasTypes : ['FKA' , 'Married']
        ,getAliasTypes : function() {
            return this._aliasTypes;
        }

        ,getComplaintTypes: function() {
            return ["SSBI", "Type1", "Type2", "Type3", "Type4"];
            //return ["Type1", "Type2", "Type3", "Type4"];
        }
        ,getCloseDispositions: function() {
            return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
            //return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
        }
    }

};

