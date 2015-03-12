/**
 * Document.Model
 *
 * @author jwu
 */
Document.Model = Document.Model || {
    create : function() {
        if (Document.Model.Lookup.create)         {Document.Model.Lookup.create();}
        if (Document.Model.Tree.create)           {Document.Model.Tree.create();}
        if (Document.Model.Documents.create)      {Document.Model.Documents.create();}
        if (Document.Model.Detail.create)         {Document.Model.Detail.create();}
        if (Document.Model.People.create)         {Document.Model.People.create();}
        if (Document.Model.Notes.create)          {Document.Model.Notes.create();}
        if (Document.Model.Tasks.create)          {Document.Model.Tasks.create();}
        if (Document.Model.References.create)     {Document.Model.References.create();}
        if (Document.Model.Events.create)         {Document.Model.Events.create();}
        if (Document.Model.Correspondence.create) {Document.Model.Correspondence.create();}

        if (Document.Service.create)              {Document.Service.create();}

        if ("undefined" != typeof Topbar) {
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_SET_ASN_DATA, this.onTopbarViewSetAsnData);
        }
    }
    ,onInitialized: function() {
        if (Document.Model.Lookup.onInitialized)         {Document.Model.Lookup.onInitialized();}
        if (Document.Model.Tree.onInitialized)           {Document.Model.Tree.onInitialized();}
        if (Document.Model.Documents.onInitialized)      {Document.Model.Documents.onInitialized();}
        if (Document.Model.Detail.onInitialized)         {Document.Model.Detail.onInitialized();}
        if (Document.Model.Notes.onInitialized)          {Document.Model.Notes.onInitialized();}
        if (Document.Model.Tasks.onInitialized)          {Document.Model.Tasks.onInitialized();}
        if (Document.Model.References.onInitialized)     {Document.Model.References.onInitialized();}
        if (Document.Model.Events.onInitialized)         {Document.Model.Events.onInitialized();}
        if (Document.Model.Correspondence.onInitialized) {Document.Model.Correspondence.onInitialized();}

        if (Document.Service.onInitialized)              {Document.Service.onInitialized();}
    }

    ,interface: {
        apiListObjects: function() {
            return "/api/latest/plugin/search/CASE_FILE";
        }
        ,apiRetrieveObject: function(nodeType, objId) {
            return "/api/latest/plugin/casefile/byId/" + objId;
        }
        ,apiSaveObject: function(nodeType, objId) {
            return "/api/latest/plugin/casefile/";
        }
        ,nodeId: function(objSolr) {
            return objSolr.object_id_s;
            //return parseInt(objSolr.object_id_s);
        }
        ,nodeType: function(objSolr) {
            return Document.Model.DOC_TYPE_CASE_FILE;
        }
        ,nodeTitle: function(objSolr) {
            return Acm.goodValue(objSolr.title_parseable) + " (" + Acm.goodValue(objSolr.name) + ")";
        }
        ,nodeToolTip: function(objSolr) {
            return Acm.goodValue(objSolr.title_parseable);
        }
        ,objToSolr: function(objData) {
            var solr = {};
            solr.author = objData.creator;
            solr.author_s = objData.creator;
            solr.create_tdt = objData.created;
            solr.last_modified_tdt = objData.modified;
            solr.modifier_s = objData.modifier;
            solr.name = objData.caseNumber;
            solr.object_id_s = objData.id;
            solr.object_type_s = Document.Model.DOC_TYPE_CASE_FILE;
            solr.owner_s = objData.creator;
            solr.status_s = objData.status;
            solr.title_parseable = objData.title;
            return solr;
        }
        ,validateObjData: function(data) {
            return Document.Model.Detail.validateDocument(data);
        }
        ,nodeTypeMap: function() {
            return Document.Model.Tree.Key.nodeTypeMap;
        }
    }

    ,onTopbarViewSetAsnData: function(asnData) {
        if (ObjNav.Model.Tree.Config.validateTreeInfo(asnData)) {
            if ("/plugin/casefile" == asnData.name) {
                var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
                var sameResultSet = ObjNav.Model.Tree.Config.sameResultSet(asnData);
                ObjNav.Model.Tree.Config.readTreeInfo();

                if (!sameResultSet) {
                    ObjNav.Model.retrieveData(treeInfo);
                }
                return true;
            }
        }
        return false;
    }

    ,DOC_TYPE_CASE_FILE  : "CASE_FILE"
    ,DOC_TYPE_FILE       : "FILE"
    ,DOC_CATEGORY_CORRESPONDENCE: "CORRESPONDENCE"

    ,getDocumentId : function() {
        return ObjNav.Model.getObjectId();
    }
    ,getDocument: function() {
        var objId = ObjNav.Model.getObjectId();
        return ObjNav.Model.Detail.getCacheObject(Document.Model.DOC_TYPE_CASE_FILE, objId);
    }

    ,Tree: {
        create: function() {
            if (Document.Model.Tree.Key.create)        {Document.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (Document.Model.Tree.Key.onInitialized)        {Document.Model.Tree.Key.onInitialized();}
        }

        ,Key: {
            create: function() {
            }
            ,onInitialized: function() {
            }

            ,NODE_TYPE_PART_DETAILS      : "d"
            ,NODE_TYPE_PART_PEOPLE       : "p"
            ,NODE_TYPE_PART_DOCUMENTS    : "o"
            ,NODE_TYPE_PART_PARTICIPANTS : "a"
            ,NODE_TYPE_PART_NOTES        : "n"
            ,NODE_TYPE_PART_TASKS        : "t"
            ,NODE_TYPE_PART_REFERENCES   : "r"
            ,NODE_TYPE_PART_HISTORY      : "h"
            ,NODE_TYPE_PART_TEMPLATES    : "tm"


            ,nodeTypeMap: [
                {nodeType: "prevPage"    ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"   ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"          ,icon: ""                 ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/CASE_FILE"        ,icon: "i i-folder"
                    ,tabIds: ["tabTitle"
                        ,"tabDetail","tabPeople"
                        ,"tabDocs","tabParticipants"
                        ,"tabNotes","tabTasks"
                        ,"tabRefs","tabHistory"
                        ,"tabTemplates"
                    ]
                }
                ,{nodeType: "p/CASE_FILE/d"      ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/CASE_FILE/p"      ,icon: "",tabIds: ["tabPeople"]}
                ,{nodeType: "p/CASE_FILE/o"      ,icon: "",tabIds: ["tabDocs"]}
                //,{nodeType: "p/CASE_FILE/o/c"     ,icon: "",tabIds: ["tabDoc"]}
                ,{nodeType: "p/CASE_FILE/a"      ,icon: "",tabIds: ["tabParticipants"]}
                ,{nodeType: "p/CASE_FILE/n"      ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "p/CASE_FILE/t"      ,icon: "",tabIds: ["tabTasks"]}
                ,{nodeType: "p/CASE_FILE/r"      ,icon: "",tabIds: ["tabRefs"]}
                ,{nodeType: "p/CASE_FILE/h"      ,icon: "",tabIds: ["tabHistory"]}
                ,{nodeType: "p/CASE_FILE/tm"     ,icon: "",tabIds: ["tabTemplates"]}
            ]
        }
    }

    ,Detail: {
        create : function() {
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_CASE_FILE           , this.onViewChangedDocument);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_CASE_TITLE          , this.onViewChangedCaseTitle);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_INCIDENT_DATE       , this.onViewChangedIncidentDate);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_ASSIGNEE            , this.onViewChangedAssignee);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_SUBJECT_TYPE        , this.onViewChangedSubjectType);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_PRIORITY            , this.onViewChangedPriority);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_DUE_DATE            , this.onViewChangedDueDate);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_DETAIL              , this.onViewChangedDetail);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CLICKED_RESTRICT_CHECKBOX   , this.onViewClickedRestrictCheckbox);
        }
        ,onInitialized: function() {
        }

        ,onViewChangedDocument: function(caseFileId) {
            ObjNav.Service.retrieveObject(Document.Model.DOC_TYPE_CASE_FILE, caseFileId);
        }
        ,onViewChangedCaseTitle: function(caseFileId, title) {
            Document.Service.Detail.saveCaseTitle(caseFileId, title);
        }

        ,onViewChangedIncidentDate: function(caseFileId, incidentDate) {
            Document.Service.Detail.saveIncidentDate(caseFileId, incidentDate);
        }
        ,onViewChangedAssignee: function(caseFileId, assignee) {
            Document.Service.Detail.saveAssignee(caseFileId, assignee);
        }
        ,onViewChangedSubjectType: function(caseFileId, caseType) {
            Document.Service.Detail.saveSubjectType(caseFileId, caseType);
        }
        ,onViewChangedPriority: function(caseFileId, priority) {
            Document.Service.Detail.savePriority(caseFileId, priority);
        }
        ,onViewChangedDueDate: function(caseFileId, dueDate) {
            Document.Service.Detail.saveDueDate(caseFileId, dueDate);
        }
        ,onViewChangedDetail: function(caseFileId, details) {
            Document.Service.Detail.saveDetail(caseFileId, details);
        }
        ,onViewClickedRestrictCheckbox: function(caseFileId, restriction) {
            Document.Service.Detail.updateCaseRestriction(caseFileId, restriction);
        }


        ,getAssignee: function(caseFile) {
            var assignee = null;
            if (Document.Model.Detail.validateDocument(caseFile)) {
                if (Acm.isArray(caseFile.participants)) {
                    for (var i = 0; i < caseFile.participants.length; i++) {
                        var participant =  caseFile.participants[i];
                        if ("assignee" == participant.participantType) {
                            assignee = participant.participantLdapId;
                            break;
                        }
                    }
                }
            }
            return assignee;
        }
        ,setAssignee: function(caseFile, assignee) {
            if (caseFile) {
                if (!Acm.isArray(caseFile.participants)) {
                    caseFile.participants = [];
                }

                for (var i = 0; i < caseFile.participants.length; i++) {
                    if ("assignee" == caseFile.participants[i].participantType) {
                        caseFile.participants[i].participantLdapId = assignee;
                        return;
                    }
                }


                var participant = {};
                participant.participantType = "assignee";
                participant.participantLdapId = assignee;
                caseFile.participants.push(participant);
            }
        }
        ,getCacheDocument: function(caseFileId) {
            if (0 >= caseFileId) {
                return null;
            }
            return ObjNav.Model.Detail.getCacheObject(Document.Model.DOC_TYPE_CASE_FILE, caseFileId);
        }
        ,putCacheDocument: function(caseFileId, caseFile) {
            ObjNav.Model.Detail.putCacheObject(Document.Model.DOC_TYPE_CASE_FILE, caseFileId, caseFile);
        }
        ,validateDocument: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id) || Acm.isEmpty(data.caseNumber)) {
                return false;
            }
            if (!Acm.isArray(data.childObjects)) {
                return false;
            }
            if (!Acm.isArray(data.milestones)) {
                return false;
            }
            if (!Acm.isArray(data.participants)) {
                return false;
            }
            if (!Acm.isArray(data.personAssociations)) {
                return false;
            }
            if (!Acm.isArray(data.references)) {
                return false;
            }
            return true;
        }
    }

    ,People: {
        create : function() {
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CHANGED_CHILD_OBJECT        , this.onViewChangedChildObject);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_ADDED_PARTICIPANT           , this.onViewAddedParticipant);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_UPDATED_PARTICIPANT         , this.onViewUpdatedParticipant);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_DELETED_PARTICIPANT         , this.onViewDeletedParticipant);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_ADDED_PERSON_ASSOCIATION    , this.onViewAddedPersonAssociation);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_UPDATED_PERSON_ASSOCIATION  , this.onViewUpdatedPersonAssociation);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_DELETED_PERSON_ASSOCIATION  , this.onViewDeletedPersonAssociation);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_ADDED_ADDRESS               , this.onViewAddedAddress);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_UPDATED_ADDRESS             , this.onViewUpdatedAddress);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_DELETED_ADDRESS             , this.onViewDeletedAddress);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_ADDED_CONTACT_METHOD        , this.onViewAddedContactMethod);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_UPDATED_CONTACT_METHOD      , this.onViewUpdatedContactMethod);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_DELETED_CONTACT_METHOD      , this.onViewDeletedContactMethod);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_ADDED_SECURITY_TAG          , this.onViewAddedSecurityTag);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_UPDATED_SECURITY_TAG        , this.onViewUpdatedSecurityTag);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_DELETED_SECURITY_TAG        , this.onViewDeletedSecurityTag);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_ADDED_PERSON_ALIAS          , this.onViewAddedPersonAlias);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_UPDATED_PERSON_ALIAS        , this.onViewUpdatedPersonAlias);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_DELETED_PERSON_ALIAS        , this.onViewDeletedPersonAlias);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_ADDED_ORGANIZATION          , this.onViewAddedOrganization);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_UPDATED_ORGANIZATION        , this.onViewUpdatedOrganization);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_DELETED_ORGANIZATION        , this.onViewDeletedOrganization);

        }
        ,onInitialized: function() {
        }

        ,onViewChangedChildObject: function(caseFileId, childObject) {
            Document.Service.People.saveChildObject(caseFileId, childObject);
        }
        ,onViewAddedParticipant: function(caseFileId, participant) {
            Document.Service.People.addParticipant(caseFileId, participant);
        }
        ,onViewUpdatedParticipant: function(caseFileId, participant) {
            Document.Service.People.updateParticipant(caseFileId, participant);
        }
        ,onViewDeletedParticipant: function(caseFileId, participantId) {
            Document.Service.People.deleteParticipant(caseFileId, participantId);
        }
        ,onViewAddedPersonAssociation: function(caseFileId, personAssociation) {
            var pa = Document.Model.People.newPersonAssociation();
            pa.parentType = Document.Model.DOC_TYPE_CASE_FILE;
            pa.parentId = caseFileId;
            pa.personType = personAssociation.personType;
            //pa.personDescription = personAssociation.personDescription;
            pa.person.title = personAssociation.person.title;
            pa.person.givenName = personAssociation.person.givenName;
            pa.person.familyName = personAssociation.person.familyName;
            Document.Service.People.addPersonAssociation(caseFileId, pa);
        }
        ,onViewUpdatedPersonAssociation: function(caseFileId, personAssociation) {
            Document.Service.People.updatePersonAssociation(caseFileId, personAssociation);
        }
        ,onViewDeletedPersonAssociation: function(caseFileId, personAssociationId) {
            Document.Service.People.deletePersonAssociation(caseFileId, personAssociationId);
        }
        ,onViewAddedAddress: function(caseFileId, personAssociationId, address) {
            Document.Service.People.addAddress(caseFileId, personAssociationId, address);
        }
        ,onViewUpdatedAddress: function(caseFileId, personAssociationId, address) {
            Document.Service.People.updateAddress(caseFileId, personAssociationId, address);
        }
        ,onViewDeletedAddress: function(caseFileId, personAssociationId, addressId) {
            Document.Service.People.deleteAddress(caseFileId, personAssociationId, addressId);
        }
        ,onViewAddedContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            Document.Service.People.addContactMethod(caseFileId, personAssociationId, contactMethod);
        }
        ,onViewUpdatedContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            Document.Service.People.updateContactMethod(caseFileId, personAssociationId, contactMethod);
        }
        ,onViewDeletedContactMethod: function(caseFileId, personAssociationId, contactMethodId) {
            Document.Service.People.deleteContactMethod(caseFileId, personAssociationId, contactMethodId);
        }
        ,onViewAddedSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            Document.Service.People.addSecurityTag(caseFileId, personAssociationId, securityTag);
        }
        ,onViewUpdatedSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            Document.Service.People.updateSecurityTag(caseFileId, personAssociationId, securityTag);
        }
        ,onViewDeletedSecurityTag: function(caseFileId, personAssociationId, securityTagId) {
            Document.Service.People.deleteSecurityTag(caseFileId, personAssociationId, securityTagId);
        }
        ,onViewAddedPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            Document.Service.People.addPersonAlias(caseFileId, personAssociationId, personAlias);
        }
        ,onViewUpdatedPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            Document.Service.People.updatePersonAlias(caseFileId, personAssociationId, personAlias);
        }
        ,onViewDeletedPersonAlias: function(caseFileId, personAssociationId, personAliasId) {
            Document.Service.People.deletePersonAlias(caseFileId, personAssociationId, personAliasId);
        }
        ,onViewAddedOrganization: function(caseFileId, personAssociationId, organization) {
            Document.Service.People.addOrganization(caseFileId, personAssociationId, organization);
        }
        ,onViewUpdatedOrganization: function(caseFileId, personAssociationId, organization) {
            Document.Service.People.updateOrganization(caseFileId, personAssociationId, organization);
        }
        ,onViewDeletedOrganization: function(caseFileId, personAssociationId, organizationId) {
            Document.Service.People.deleteOrganization(caseFileId, personAssociationId, organizationId);
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
                    /*,hairColor:""
                     ,eyeColor:""
                     ,heightInInches:null*/
                    ,weightInPounds:null
                    /*,dateOfBirth:null
                     ,dateMarried:null*/
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

    ,Notes: {
        create : function() {
            this.cacheNoteList = new Acm.Model.CacheFifo(4);

            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_ADDED_NOTE     , this.onViewAddedNote);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_UPDATED_NOTE   , this.onViewUpdatedNote);
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_DELETED_NOTE   , this.onViewDeletedNote);
        }
        ,onInitialized: function() {
        }


        ,onViewAddedNote: function(note) {
            Document.Service.Notes.addNote(note);
        }
        ,onViewUpdatedNote: function(note) {
            Document.Service.Notes.updateNote(note);
        }
        ,onViewDeletedNote: function(noteId) {
            Document.Service.Notes.deleteNote(noteId);
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
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.parentId)) {
                return false;
            }
            return true;
        }
    }

    ,Tasks: {
        create : function() {
            this.cacheTaskSolr = new Acm.Model.CacheFifo(4);
            this.cacheTasks = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
            Document.Service.Tasks.retrieveTask();
        }
    }
    ,Documents: {
        create : function() {
            //this.cacheDocuments = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
        }
    }
    ,Correspondence: {
        create : function() {
            Acm.Dispatcher.addEventListener(Document.Controller.VIEW_CLICKED_ADD_CORRESPONDENCE, this.onViewClickedAddCorrespondence);
        }
        ,onInitialized: function() {
        }


        ,onViewClickedAddCorrespondence: function(caseFileId, templateName) {
            //var caseFile = Document.Model.Detail.getDocument(caseFileId);
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                Document.Service.Correspondence.createCorrespondence(caseFile, templateName);
            }
        }
    }

    ,References: {
        create : function() {
            this.cacheReferenceList = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
        }
    }

    ,Events: {
        create : function() {
            this.cacheEventList = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
        }
    }


    ,Lookup: {
        create: function() {
            this._assignees    = new Acm.Model.SessionData(Application.SESSION_DATA_CASE_FILE_ASSIGNEES);
            this._subjectTypes = new Acm.Model.SessionData(Application.SESSION_DATA_CASE_FILE_TYPES);
            this._priorities   = new Acm.Model.SessionData(Application.SESSION_DATA_CASE_FILE_PRIORITIES);
        }
        ,onInitialized: function() {
            var assignees = Document.Model.Lookup.getAssignees();
            if (Acm.isEmpty(assignees)) {
                Document.Service.Lookup.retrieveAssignees();
            } else {
                Document.Controller.modelFoundAssignees(assignees);
            }

            var subjectTypes = Document.Model.Lookup.getSubjectTypes();
            if (Acm.isEmpty(subjectTypes)) {
                Document.Service.Lookup.retrieveSubjectTypes();
            } else {
                Document.Controller.modelFoundSubjectTypes(subjectTypes);
            }

            var priorities = Document.Model.Lookup.getPriorities();
            if (Acm.isEmpty(priorities)) {
                Document.Service.Lookup.retrievePriorities();
            } else {
                Document.Controller.modelFoundPriorities(priorities);
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
        ,getSubjectTypes: function() {
            return this._subjectTypes.get();
        }
        ,setSubjectTypes: function(subjectTypes) {
            this._subjectTypes.set(subjectTypes);
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

        ,getCaseTypes: function() {
            return ["SSBI", "Type1", "Type2", "Type3", "Type4"];
            //return ["Type1", "Type2", "Type3", "Type4"];
        }
//    ,getDocumentTypes: function() {
//        var data = sessionStorage.getItem("AcmDocumentTypes");
//        var item = ("null" === data)? null : JSON.parse(data);
//        return item;
//    }
//    ,setDocumentTypes: function(data) {
//        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
//        sessionStorage.setItem("AcmDocumentTypes", item);
//    }

        ,getCloseDispositions: function() {
            return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
            //return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
        }
    }

};

