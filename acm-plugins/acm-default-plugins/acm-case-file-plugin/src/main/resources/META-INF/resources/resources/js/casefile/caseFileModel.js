/**
 * CaseFile.Model
 *
 * @author jwu
 */
CaseFile.Model = CaseFile.Model || {
    create : function() {
        if (CaseFile.Model.Lookup.create)         {CaseFile.Model.Lookup.create();}
        if (CaseFile.Model.Action.create)         {CaseFile.Model.Action.create();}
        if (CaseFile.Model.Tree.create)           {CaseFile.Model.Tree.create();}
        if (CaseFile.Model.Documents.create)      {CaseFile.Model.Documents.create();}
        if (CaseFile.Model.Detail.create)         {CaseFile.Model.Detail.create();}
        if (CaseFile.Model.People.create)         {CaseFile.Model.People.create();}
        if (CaseFile.Model.Notes.create)          {CaseFile.Model.Notes.create();}
        if (CaseFile.Model.Tasks.create)          {CaseFile.Model.Tasks.create();}
        if (CaseFile.Model.References.create)     {CaseFile.Model.References.create();}
        if (CaseFile.Model.History.create)        {CaseFile.Model.History.create();}
        if (CaseFile.Model.Correspondence.create) {CaseFile.Model.Correspondence.create();}
        if (CaseFile.Model.Time.create)           {CaseFile.Model.Time.create();}
        if (CaseFile.Model.Cost.create)           {CaseFile.Model.Cost.create();}
        if (CaseFile.Model.Participants.create)   {CaseFile.Model.Participants.create();}

        if (CaseFile.Service.create)              {CaseFile.Service.create();}

        if ("undefined" != typeof Topbar) {
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_SET_ASN_DATA, this.onTopbarViewSetAsnData);
        }
    }
    ,onInitialized: function() {
        if (CaseFile.Model.Lookup.onInitialized)         {CaseFile.Model.Lookup.onInitialized();}
        if (CaseFile.Model.Action.onInitialized)         {CaseFile.Model.Action.onInitialized();}
        if (CaseFile.Model.Tree.onInitialized)           {CaseFile.Model.Tree.onInitialized();}
        if (CaseFile.Model.Documents.onInitialized)      {CaseFile.Model.Documents.onInitialized();}
        if (CaseFile.Model.Detail.onInitialized)         {CaseFile.Model.Detail.onInitialized();}
        if (CaseFile.Model.Notes.onInitialized)          {CaseFile.Model.Notes.onInitialized();}
        if (CaseFile.Model.Tasks.onInitialized)          {CaseFile.Model.Tasks.onInitialized();}
        if (CaseFile.Model.References.onInitialized)     {CaseFile.Model.References.onInitialized();}
        if (CaseFile.Model.History.onInitialized)        {CaseFile.Model.History.onInitialized();}
        if (CaseFile.Model.Correspondence.onInitialized) {CaseFile.Model.Correspondence.onInitialized();}
        if (CaseFile.Model.Time.onInitialized)           {CaseFile.Model.Time.onInitialized();}
        if (CaseFile.Model.Cost.onInitialized)           {CaseFile.Model.Cost.onInitialized();}
        if (CaseFile.Model.Participants.onInitialized)   {CaseFile.Model.Participants.onInitialized();}

        if (CaseFile.Service.onInitialized)              {CaseFile.Service.onInitialized();}
    }

    ,interfaceNavObj: {
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
            return CaseFile.Model.DOC_TYPE_CASE_FILE;
        }
        ,nodeTypeSupported: function(nodeType) {
            return (CaseFile.Model.DOC_TYPE_CASE_FILE == nodeType);
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
            solr.object_type_s = CaseFile.Model.DOC_TYPE_CASE_FILE;
            solr.owner_s = objData.creator;
            solr.status_s = objData.status;
            solr.title_parseable = objData.title;
            return solr;
        }
        ,validateObjData: function(data) {
            return CaseFile.Model.Detail.validateCaseFile(data);
        }
//        ,nodeTypeMap: function() {
//            return CaseFile.Model.Tree.Key.nodeTypeMap;
//        }
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
    ,DOC_CATEGORY_CORRESPONDENCE_SM : "Correspondence"
    ,DOC_CATEGORY_DOCUMENT_SM : "Document"
    ,DOC_TYPE_TIMESHEET  : "TIMESHEET"
    ,DOC_TYPE_COSTSHEET  : "COSTSHEET"
    ,DOC_TYPE_FILE_SM       : "file"
    ,DOC_CATEGORY_FILE_SM   : "Document"


    ,getCaseFileId : function() {
        return ObjNav.Model.getObjectId();
    }
    ,getCaseFile: function() {
        var objId = ObjNav.Model.getObjectId();
        return ObjNav.Model.Detail.getCacheObject(CaseFile.Model.DOC_TYPE_CASE_FILE, objId);
    }

    ,Config: {
        CONFIG_NAME_CASE_FILE : "caseFile"
        ,request: function() {
            App.Model.Config.requestConfig(CaseFile.Model.Config.CONFIG_NAME_CASE_FILE).done(function(data) {
                var cfg = App.Model.Config.getConfig(CaseFile.Model.Config.CONFIG_NAME_CASE_FILE);
                if (Acm.isNotEmpty(cfg)) {
                    var myCfg = App.Model.Config.getMyConfig();
                    myCfg.caseTypes  = Acm.goodValue(cfg["casefile.case-types"], "").split(",");
                    myCfg.treeFilter = Acm.parseJson(cfg["search.tree.filter"], "[]");
                    myCfg.treeSort   = Acm.parseJson(cfg["search.tree.sort"], "[]");
                }
            });
        }
    }

    ,Tree: {
        create: function() {
            if (CaseFile.Model.Tree.Key.create)        {CaseFile.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (CaseFile.Model.Tree.Key.onInitialized)        {CaseFile.Model.Tree.Key.onInitialized();}
        }

        ,Key: {
            create: function() {
            }
            ,onInitialized: function() {
            }

            ,NODE_TYPE_PART_DETAILS      : "det"
            ,NODE_TYPE_PART_PEOPLE       : "ppl"
            ,NODE_TYPE_PART_DOCUMENTS    : "doc"
            ,NODE_TYPE_PART_PARTICIPANTS : "par"
            ,NODE_TYPE_PART_NOTES        : "note"
            ,NODE_TYPE_PART_TASKS        : "task"
            ,NODE_TYPE_PART_REFERENCES   : "ref"
            ,NODE_TYPE_PART_HISTORY      : "his"
            ,NODE_TYPE_PART_TEMPLATES    : "tpl"
            ,NODE_TYPE_PART_TIME         : "time"
            ,NODE_TYPE_PART_COST         : "cost"
            ,NODE_TYPE_PART_CALENDAR     : "calendar"

//            ,nodeTypeMap: [
//                {nodeType: "prevPage"      ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
//                ,{nodeType: "nextPage"     ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
//                ,{nodeType: "p"            ,icon: ""                 ,tabIds: ["tabBlank"]}
//                ,{nodeType: "p/CASE_FILE"  ,icon: "i i-folder"       ,tabIds: ["tabTitle"
//                    ,"tabDetail"
//                    ,"tabPeople"
//                    ,"tabDocs"
//                    ,"tabParticipants"
//                    ,"tabNotes"
//                    ,"tabTasks"
//                    ,"tabRefs"
//                    ,"tabHistory"
//                    ,"tabCorrespondence"
//                    ,"tabOutlookCalendar"
//                    ,"tabTime"
//                    ,"tabCost"
//                ]}
//                ,{nodeType: "p/CASE_FILE/det"       ,icon: "", res: "casefile:navigation.leaf-title.details"        ,tabIds: ["tabDetail"]}
//                ,{nodeType: "p/CASE_FILE/ppl"       ,icon: "", res: "casefile:navigation.leaf-title.people"         ,tabIds: ["tabPeople"]}
//                ,{nodeType: "p/CASE_FILE/doc"       ,icon: "", res: "casefile:navigation.leaf-title.documents"      ,tabIds: ["tabDocs"]}
//                //,{nodeType: "p/CASE_FILE/doc/c"     ,icon: "",tabIds: ["tabDoc"]}
//                ,{nodeType: "p/CASE_FILE/par"       ,icon: "", res: "casefile:navigation.leaf-title.participants"   ,tabIds: ["tabParticipants"]}
//                ,{nodeType: "p/CASE_FILE/note"      ,icon: "", res: "casefile:navigation.leaf-title.notes"          ,tabIds: ["tabNotes"]}
//                ,{nodeType: "p/CASE_FILE/task"      ,icon: "", res: "casefile:navigation.leaf-title.tasks"          ,tabIds: ["tabTasks"]}
//                ,{nodeType: "p/CASE_FILE/ref"       ,icon: "", res: "casefile:navigation.leaf-title.references"     ,tabIds: ["tabRefs"]}
//                ,{nodeType: "p/CASE_FILE/his"       ,icon: "", res: "casefile:navigation.leaf-title.history"        ,tabIds: ["tabHistory"]}
//                ,{nodeType: "p/CASE_FILE/tpl"       ,icon: "", res: "casefile:navigation.leaf-title.correspondence" ,tabIds: ["tabCorrespondence"]}
//                ,{nodeType: "p/CASE_FILE/calendar"  ,icon: "", res: "casefile:navigation.leaf-title.calendar"       ,tabIds: ["tabOutlookCalendar"]}
//                ,{nodeType: "p/CASE_FILE/time"      ,icon: "", res: "casefile:navigation.leaf-title.time"           ,tabIds: ["tabTime"]}
//                ,{nodeType: "p/CASE_FILE/cost"      ,icon: "", res: "casefile:navigation.leaf-title.cost"           ,tabIds: ["tabCost"]}
//
//            ]

//            ,findNodeTypeInfo: function(nodeType) {
//                var info = null;
//                for (var i = 0; i < this.nodeTypeMap.length; i++) {
//                    if (this.nodeTypeMap[i].nodeType == nodeType) {
//                        info = this.nodeTypeMap[i];
//                        break;
//                    }
//                }
//                return info;
//            }
        }
    }

    ,Action: {
        create: function(){
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_MERGED_CASE_FILES           , this.onViewMergedCaseFiles);
        }
        ,onInitialized: function(){

        }
        ,onViewMergedCaseFiles: function(sourceCaseFileId, targetCaseFileId){
            if(Acm.isNotEmpty(sourceCaseFileId) && Acm.isNotEmpty(targetCaseFileId)){
                CaseFile.Service.Action.mergeCaseFiles(sourceCaseFileId, targetCaseFileId);
            }
        }
    }

    ,Detail: {
        create : function() {
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CASE_FILE           , this.onViewChangedCaseFile);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CASE_TITLE          , this.onViewChangedCaseTitle);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_INCIDENT_DATE       , this.onViewChangedIncidentDate);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_ASSIGNEE            , this.onViewChangedAssignee);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_GROUP            	 , this.onViewChangedGroup);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_SUBJECT_TYPE        , this.onViewChangedSubjectType);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_PRIORITY            , this.onViewChangedPriority);
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_DUE_DATE            , this.onViewChangedDueDate);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_DETAIL              , this.onViewChangedDetail);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLICKED_RESTRICT_CHECKBOX   , this.onViewClickedRestrictCheckbox);
        }
        ,onInitialized: function() {
        }

        ,onViewChangedCaseFile: function(caseFileId) {
            ObjNav.Service.retrieveObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId);
        }
//        ,onViewChangedCaseTitle: function(caseFileId, title) {
//            CaseFile.Service.Detail.saveCaseTitle(caseFileId, title);
//        }
//        ,onViewChangedIncidentDate: function(caseFileId, incidentDate) {
//            CaseFile.Service.Detail.saveIncidentDate(caseFileId, incidentDate);
//        }
        ,onViewChangedAssignee: function(caseFileId, assignee) {
            CaseFile.Service.Detail.saveAssignee(caseFileId, assignee);
        }
        ,onViewChangedGroup: function(caseFileId, group) {
            CaseFile.Service.Detail.saveGroup(caseFileId, group);
        }
        ,onViewChangedSubjectType: function(caseFileId, caseType) {
            CaseFile.Service.Detail.saveSubjectType(caseFileId, caseType);
        }
        ,onViewChangedPriority: function(caseFileId, priority) {
            CaseFile.Service.Detail.savePriority(caseFileId, priority);
        }
//        ,onViewChangedDueDate: function(caseFileId, dueDate) {
//            CaseFile.Service.Detail.saveDueDate(caseFileId, dueDate);
//        }
        ,onViewChangedDetail: function(caseFileId, details) {
            CaseFile.Service.Detail.saveDetail(caseFileId, details);
        }
        ,onViewClickedRestrictCheckbox: function(caseFileId, restriction) {
            CaseFile.Service.Detail.updateCaseRestriction(caseFileId, restriction);
        }


        ,getAssignee: function(caseFile) {
            var assignee = null;
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
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
        
        ,getGroup: function(caseFile) {
            var group = null;
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                if (Acm.isArray(caseFile.participants)) {
                    for (var i = 0; i < caseFile.participants.length; i++) {
                        var participant =  caseFile.participants[i];
                        if ("owning group" == participant.participantType) {
                            group = participant.participantLdapId;
                            break;
                        }
                    }
                }
            }
            return group;
        }
        ,setGroup: function(caseFile, group) {
            if (caseFile) {
                if (!Acm.isArray(caseFile.participants)) {
                    caseFile.participants = [];
                }

                for (var i = 0; i < caseFile.participants.length; i++) {
                    if ("owning group" == caseFile.participants[i].participantType) {
                        caseFile.participants[i].participantLdapId = group;
                        return;
                    }
                }


                var participant = {};
                participant.participantType = "owning group";
                participant.participantLdapId = group;
                caseFile.participants.push(participant);
            }
        }
        
        ,getCacheCaseFile: function(caseFileId) {
            if (0 >= caseFileId) {
                return null;
            }
            return ObjNav.Model.Detail.getCacheObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId);
        }
        ,putCacheCaseFile: function(caseFileId, caseFile) {
            ObjNav.Model.Detail.putCacheObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId, caseFile);
        }
        ,validateCaseFile: function(data) {
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
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CHILD_OBJECT        , this.onViewChangedChildObject);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_PARTICIPANT           , this.onViewAddedParticipant);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_PARTICIPANT         , this.onViewUpdatedParticipant);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_PARTICIPANT         , this.onViewDeletedParticipant);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_PERSON_ASSOCIATION    , this.onViewAddedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_PERSON_ASSOCIATION  , this.onViewUpdatedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_PERSON_ASSOCIATION  , this.onViewDeletedPersonAssociation);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_ADDRESS               , this.onViewAddedAddress);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_ADDRESS             , this.onViewUpdatedAddress);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_ADDRESS             , this.onViewDeletedAddress);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_CONTACT_METHOD        , this.onViewAddedContactMethod);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_CONTACT_METHOD      , this.onViewUpdatedContactMethod);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_CONTACT_METHOD      , this.onViewDeletedContactMethod);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_SECURITY_TAG          , this.onViewAddedSecurityTag);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_SECURITY_TAG        , this.onViewUpdatedSecurityTag);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_SECURITY_TAG        , this.onViewDeletedSecurityTag);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_PERSON_ALIAS          , this.onViewAddedPersonAlias);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_PERSON_ALIAS        , this.onViewUpdatedPersonAlias);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_PERSON_ALIAS        , this.onViewDeletedPersonAlias);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_ORGANIZATION          , this.onViewAddedOrganization);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_ORGANIZATION        , this.onViewUpdatedOrganization);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_ORGANIZATION        , this.onViewDeletedOrganization);

        }
        ,onInitialized: function() {
        }

        ,onViewChangedChildObject: function(caseFileId, childObject) {
            CaseFile.Service.People.saveChildObject(caseFileId, childObject);
        }
        ,onViewAddedParticipant: function(caseFileId, participant) {
            CaseFile.Service.People.addParticipant(caseFileId, participant);
        }
        ,onViewUpdatedParticipant: function(caseFileId, participant) {
            CaseFile.Service.People.updateParticipant(caseFileId, participant);
        }
        ,onViewDeletedParticipant: function(caseFileId, participantId) {
            CaseFile.Service.People.deleteParticipant(caseFileId, participantId);
        }
        ,onViewAddedPersonAssociation: function(caseFileId, personAssociation) {
            var pa = CaseFile.Model.People.newPersonAssociation();
            pa.parentType = CaseFile.Model.DOC_TYPE_CASE_FILE;
            pa.parentId = caseFileId;
            pa.personType = personAssociation.personType;
            //pa.personDescription = personAssociation.personDescription;
            pa.person.title = personAssociation.person.title;
            pa.person.givenName = personAssociation.person.givenName;
            pa.person.familyName = personAssociation.person.familyName;
            CaseFile.Service.People.addPersonAssociation(caseFileId, pa);
        }
        ,onViewUpdatedPersonAssociation: function(caseFileId, personAssociation) {
            CaseFile.Service.People.updatePersonAssociation(caseFileId, personAssociation);
        }
        ,onViewDeletedPersonAssociation: function(caseFileId, personAssociationId) {
            CaseFile.Service.People.deletePersonAssociation(caseFileId, personAssociationId);
        }
        ,onViewAddedAddress: function(caseFileId, personAssociationId, address) {
            CaseFile.Service.People.addAddress(caseFileId, personAssociationId, address);
        }
        ,onViewUpdatedAddress: function(caseFileId, personAssociationId, address) {
            CaseFile.Service.People.updateAddress(caseFileId, personAssociationId, address);
        }
        ,onViewDeletedAddress: function(caseFileId, personAssociationId, addressId) {
            CaseFile.Service.People.deleteAddress(caseFileId, personAssociationId, addressId);
        }
        ,onViewAddedContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            CaseFile.Service.People.addContactMethod(caseFileId, personAssociationId, contactMethod);
        }
        ,onViewUpdatedContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            CaseFile.Service.People.updateContactMethod(caseFileId, personAssociationId, contactMethod);
        }
        ,onViewDeletedContactMethod: function(caseFileId, personAssociationId, contactMethodId) {
            CaseFile.Service.People.deleteContactMethod(caseFileId, personAssociationId, contactMethodId);
        }
        ,onViewAddedSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            CaseFile.Service.People.addSecurityTag(caseFileId, personAssociationId, securityTag);
        }
        ,onViewUpdatedSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            CaseFile.Service.People.updateSecurityTag(caseFileId, personAssociationId, securityTag);
        }
        ,onViewDeletedSecurityTag: function(caseFileId, personAssociationId, securityTagId) {
            CaseFile.Service.People.deleteSecurityTag(caseFileId, personAssociationId, securityTagId);
        }
        ,onViewAddedPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            CaseFile.Service.People.addPersonAlias(caseFileId, personAssociationId, personAlias);
        }
        ,onViewUpdatedPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            CaseFile.Service.People.updatePersonAlias(caseFileId, personAssociationId, personAlias);
        }
        ,onViewDeletedPersonAlias: function(caseFileId, personAssociationId, personAliasId) {
            CaseFile.Service.People.deletePersonAlias(caseFileId, personAssociationId, personAliasId);
        }
        ,onViewAddedOrganization: function(caseFileId, personAssociationId, organization) {
            CaseFile.Service.People.addOrganization(caseFileId, personAssociationId, organization);
        }
        ,onViewUpdatedOrganization: function(caseFileId, personAssociationId, organization) {
            CaseFile.Service.People.updateOrganization(caseFileId, personAssociationId, organization);
        }
        ,onViewDeletedOrganization: function(caseFileId, personAssociationId, organizationId) {
            CaseFile.Service.People.deleteOrganization(caseFileId, personAssociationId, organizationId);
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

        ,validatePersonAssociations: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validatePersonAssociation(data[i])) {
                    return false;
                }
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

    ,Notes: {
        create : function() {
            this.cacheNoteList = new Acm.Model.CacheFifo({maxSize: 16});

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_NOTE     , this.onViewAddedNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_NOTE   , this.onViewUpdatedNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_NOTE   , this.onViewDeletedNote);
        }
        ,onInitialized: function() {
        }

        ,API_LIST_NOTES              : "/api/latest/plugin/note/"
        ,API_SAVE_NOTE               : "/api/latest/plugin/note"
        ,API_DELETE_NOTE_            : "/api/latest/plugin/note/"


//keep this for future service paging support
//        ,noteListAction : function(caseFileId, postData, jtParams, sortMap, dataMaker, cacheKey) {
//            var noteListData = CaseFile.Model.Notes.cacheNoteList.get(cacheKey);
//            if (noteListData) {
//                return Acm.Promise.donePromise(dataMaker(noteListData)).promise();
//            }
//
//            var url =  CaseFile.Model.Notes.API_LIST_NOTES + CaseFile.Model.DOC_TYPE_CASE_FILE + "/";
//            url += caseFileId;
//
//            return AcmEx.Model.JTable.serviceListAction(url, postData, jtParams, sortMap
//                ,function(data) {
//                    if (CaseFile.Model.Notes.validateNotes(data)) {
//                        var noteList = data;
//
//                        var noteListData = {};
//                        noteListData.total = noteList.length;  //fix me: need service returns total count
//                        noteListData.list = noteList;
//                        CaseFile.Model.Notes.cacheNoteList.put(cacheKey, noteListData);
//                        return dataMaker(noteListData);
//                    }
//                }
//            );
//        }
        ,getNoteList : function(caseFileId, cacheKey) {
            var noteListData = CaseFile.Model.Notes.cacheNoteList.get(cacheKey);
            if (noteListData) {
                return Acm.Promise.donePromise(noteListData).promise();
            }

            var url =  CaseFile.Model.Notes.API_LIST_NOTES + CaseFile.Model.DOC_TYPE_CASE_FILE + "/";
            url += caseFileId;

            return Acm.Service.call({type: "GET"
                ,url: url
                ,callback: function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Model.Notes.validateNotes(response)) {
                            var noteList = response;

                            var noteListData = {};
                            noteListData.total = noteList.length;  //fix me: need service returns total count
                            noteListData.list = noteList;
                            CaseFile.Model.Notes.cacheNoteList.put(cacheKey, noteListData);
                            return noteListData;
                        }
                    }
                }
            });
        }
        ,saveNote : function(data, cacheKey) {
            return Acm.Service.call({type: "POST"
                ,url: this.API_SAVE_NOTE
                ,data: JSON.stringify(data)
                ,callback: function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Model.Notes.validateNote(response)) {
                            var note = response;
                            var caseFileId = CaseFile.Model.getCaseFileId();
                            if (caseFileId == note.parentId) {
                                var noteListData = CaseFile.Model.Notes.cacheNoteList.get(cacheKey);
                                var noteList = noteListData.list;
                                var found = Acm.findIndexInArray(noteList, "id", note.id);
                                if (0 > found) {                //add new note
                                    noteList.push(note);
                                    noteListData.total++;
                                } else {                        // update existing note
                                    noteList[found] = note;
                                }
                                CaseFile.Model.Notes.cacheNoteList.put(cacheKey, noteListData);
                                return note;
                            }
                        }//end validate
                    }
                }
            });
        }
        ,deleteNote : function(noteId, cacheKey) {
            return Acm.Service.call({type: "DELETE"
                ,url: this.API_DELETE_NOTE_ + noteId
                ,callback: function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Model.Notes.validateDeletedNote(response)) {
                            var caseFileId = CaseFile.Model.getCaseFileId();
                            if (response.deletedNoteId == noteId) {
                                var noteListData = CaseFile.Model.Notes.cacheNoteList.get(cacheKey);
                                var noteList = noteListData.list;
                                for (var i = 0; i < noteList.length; i++) {
                                    if (noteId == noteList[i].id) {
                                        noteList.splice(i, 1);
                                        noteListData.total--;
                                        CaseFile.Model.Notes.cacheNoteList.put(cacheKey, noteListData);
                                        //CaseFile.Controller.modelDeletedNote(Acm.Service.responseWrapper(response, noteId));
                                        return noteId;
                                    }
                                }
                            }
                        }//end validate
                    }
                }
            });
        }

        ,onViewAddedNote: function(note) {
            CaseFile.Service.Notes.addNote(note);
        }
        ,onViewUpdatedNote: function(note) {
            CaseFile.Service.Notes.updateNote(note);
        }
        ,onViewDeletedNote: function(noteId) {
            CaseFile.Service.Notes.deleteNote(noteId);
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

    ,Tasks: {
        create : function() {
            this.cacheTaskSolr = new Acm.Model.CacheFifo({maxSize: 16});
            this.cacheMyTasks = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
            CaseFile.Model.Tasks.retrieveMyTasks();
        }

        ,API_RETRIEVE_TASKS_SOLR         : "/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId="
        ,API_RETRIEVE_MY_TASKS           : "/api/latest/plugin/task/forUser/"
        ,API_COMPLETE_TASK               : "/api/latest/plugin/task/completeTask/"
        ,API_COMPLETE_TASK_WITH_OUTCOME  : "/api/latest/plugin/task/completeTask"
        ,API_DELETE_TASK                 : "/api/latest/plugin/task/deleteTask/"

        ,taskListAction : function(caseFileId, postData, jtParams, sortMap, dataMaker, cacheKey) {
            var taskListData = CaseFile.Model.Tasks.cacheTaskSolr.get(cacheKey);
            if (taskListData) {
                return Acm.Promise.donePromise(dataMaker(taskListData)).promise();
            }

            var url =  CaseFile.Model.Tasks.API_RETRIEVE_TASKS_SOLR;
            url += caseFileId;

            return AcmEx.Model.JTable.serviceListAction(url, postData, jtParams, sortMap
                ,function(data) {
                    if (Acm.Validator.validateSolrData(data)) {
                        var responseHeader = data.responseHeader;
                        if (0 == responseHeader.status) {
                            //response.start should match to jtParams.jtStartIndex
                            //response.docs.length should be <= jtParams.jtPageSize

                            var response = data.response;
                            var taskListData = {};
                            taskListData.total = Acm.goodValue(response.numFound, 0);

                            var taskList = [];
                            for (var i = 0; i < response.docs.length; i++) {
                                var doc = response.docs[i];
                                var task = {};
                                task.id = doc.object_id_s;
                                task.title = Acm.goodValue(doc.name); //title_parseable ?? //title_t ?
                                task.created = (Acm.getDateFromDatetime(doc.create_tdt,$.t("common:date.short")));
                                task.priority = Acm.goodValue(doc.priority_s);
                                task.dueDate = (Acm.getDateFromDatetime(doc.due_tdt,$.t("common:date.short")));
                                task.status = Acm.goodValue(doc.status_s);
                                task.assignee = Acm.goodValue(doc.assignee_s);
                                taskList.push(task);
                            }
                            taskListData.list = taskList;
                            CaseFile.Model.Tasks.cacheTaskSolr.put(cacheKey, taskListData);

                            return dataMaker(taskListData);
                        }
                    }
                }
            );
        }

        ,retrieveMyTasks : function() {
            var url = this.API_RETRIEVE_MY_TASKS + App.getUserName();
            return Acm.Service.call({type: "GET"
                ,url: url
                ,callback: function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedTasks(response);

                    } else {
                        //if (CaseFile.Model.Detail.validateCaseFile(response)) {
                            var myTasks = response;
                            CaseFile.Model.Tasks.cacheMyTasks.put(App.getUserName(), myTasks);
                            CaseFile.Controller.modelRetrievedMyTasks(myTasks);
                            return myTasks;
                        //}
                    }
                }
            });
        }
        ,completeTask : function(taskId, cacheKey) {
            var url = this.API_COMPLETE_TASK + taskId;
            return Acm.Service.call({type: "POST"
                ,url: url
                ,data: "{}"
                ,callback: function(response) {
                    if (!response.hasError) {
                        //if (CaseFile.Model.Detail.validateCaseFile(response)) {
                        var task = response;
                        //var caseFileId = CaseFile.Model.getCaseFileId();
                        var myTasks = CaseFile.Model.Tasks.cacheMyTasks.get(App.getUserName());
                        var taskList = CaseFile.Model.Tasks.cacheTaskSolr.get(cacheKey);
                        for(var i = 0; i < myTasks.length; i++){
                            if(task.taskId ==  myTasks[i].taskId){
                                myTasks[i] = task;
                            }
                        }
                        for(var i = 0; i < taskList.length; i++){
                            if(task.taskId ==  taskList[i].id){
                                taskList[i].status = 'COMPLETE';
                            }
                        }
                        CaseFile.Model.Tasks.cacheMyTasks.put(App.getUserName(),myTasks);
                        CaseFile.Model.Tasks.cacheTaskSolr.put(cacheKey,taskList);

                        // CaseFile.Model.Tasks.cacheTaskSolr.reset();
                        CaseFile.Controller.modelCompletedTask(task);
                        return task;
                        //}
                    }
                }
            });
        }
        ,deleteTask : function(taskId,cacheKey) {
            var url = this.API_DELETE_TASK + taskId;
            return Acm.Service.call({type: "POST"
                ,url: url
                ,data: "{}"
                ,callback: function(response) {
                    if (!response.hasError) {
                        var task = response;
                        var myTasks = CaseFile.Model.Tasks.cacheMyTasks.get(App.getUserName());
                        var taskList = CaseFile.Model.Tasks.cacheTaskSolr.get(cacheKey);
                        for(var i = 0; i < myTasks.length; i++){
                            if(task.taskId ==  myTasks[i].taskId){
                                myTasks.splice(i,1);
                            }
                        }
                        for(var i = 0; i < taskList.length; i++){
                            if(task.taskId ==  taskList[i].id){
                                taskList.splice(i,1);
                            }
                        }
                        CaseFile.Model.Tasks.cacheMyTasks.put(App.getUserName(),myTasks);
                        CaseFile.Model.Tasks.cacheTaskSolr.put(cacheKey,taskList);
                        CaseFile.Controller.modelDeletedTask(task);
                        return task;
                    }
                }
            });
        }
        ,completeTaskWithOutcome : function(task, cacheKey) {
            var url = this.API_COMPLETE_TASK_WITH_OUTCOME;
            return Acm.Service.call({type: "POST"
                ,url: url
                ,data: JSON.stringify(task)
                ,callback: function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelCompletedTask(response);

                    } else {
                        //if (CaseFile.Model.Detail.validateCaseFile(response)) {
                        var task = response;
                        var caseFileId = CaseFile.Model.getCaseFileId();
                        var myTasks = CaseFile.Model.Tasks.cacheMyTasks.get(App.getUserName());
                        var taskList = CaseFile.Model.Tasks.cacheTaskSolr.get(cacheKey);
                        for(var i = 0; i < myTasks.length; i++){
                            if(task.taskId ==  myTasks[i].taskId){
                                myTasks[i] = task;
                            }
                        }
                        for(var i = 0; i < taskList.length; i++){
                            if(task.taskId ==  taskList[i].id){
                                taskList[i].status = 'COMPLETE';
                            }
                        }
                        CaseFile.Model.Tasks.cacheMyTasks.put(App.getUserName(),myTasks);
                        CaseFile.Model.Tasks.cacheTaskSolr.put(cacheKey,taskList);
                        //CaseFile.Controller.modelCompletedTask(task);
                        return task;
                        //}
                    }
                }
            });
        }

    }

    ,Documents: {
        create : function() {
            this.cachePlainForms = new Acm.Model.CacheFifo({maxSize: 1});
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_LODGED_DOCUMENTS      , this.onViewLodgedDocuments);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_REJECTED_DOCUMENTS    , this.onViewRejectedDocuments);
        }
        ,onInitialized: function() {
        	CaseFile.Service.Documents.retrievePlainForms();
        }

        ,validatePlainForms: function(plainForms) {
            if (Acm.isEmpty(plainForms)) {
                return false;
            }
            if(!Acm.isArray(plainForms)){
                return false;
            }
            return true;
        }

        ,getPlainForms: function() {
        	return CaseFile.Model.Documents.cachePlainForms.get("forms.plainforms");
        }

        ,setPlainForms: function(plainForms) {
        	 CaseFile.Model.Documents.cachePlainForms.put("forms.plainforms", plainForms);
        }

        ,onViewLodgedDocuments: function(caseFileId, docIds) {
                if ( Acm.isEmpty(caseFileId) )
                {
                    return;
                }

                if ( !Acm.isArray(docIds) ) {
                    return;
                }

                CaseFile.Service.Documents.auditDocuments(caseFileId, docIds, "file.lodged");

        }

        ,onViewRejectedDocuments: function(caseFileId, docIds) {
                if ( Acm.isEmpty(caseFileId) )
                {
                    return;
                }

                if ( !Acm.isArray(docIds) ) {
                    return;
                }

                CaseFile.Service.Documents.auditDocuments(caseFileId, docIds, "file.rejected");

        }
    }

    ,Correspondence: {
        create : function() {
            this.cacheCorrespondences = new Acm.Model.CacheFifo({maxSize: 16});
            //Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLICKED_ADD_CORRESPONDENCE, this.onViewClickedAddCorrespondence);
        }
        ,onInitialized: function() {
        }
//        ,onViewClickedAddCorrespondence: function(caseFileId, templateName) {
//            //var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
//            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
//            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
//                CaseFile.Service.Correspondence.createCorrespondence(caseFile, templateName);
//            }
//        }
        ,API_RETRIEVE_CORRESPONDENCE      : "/api/latest/service/ecm/bycategory/"
        ,API_CREATE_CORRESPONDENCE        : "/api/latest/service/correspondence"

        ,correspondenceListAction : function(caseFileId, postData, jtParams, sortMap, dataMaker, cacheKey) {
            var correspondencesData = CaseFile.Model.Correspondence.cacheCorrespondences.get(cacheKey);
            if (CaseFile.Model.Correspondence.validateCorrespondences(correspondencesData)) {
                return Acm.Promise.donePromise(dataMaker(correspondencesData)).promise();
            }

            var url =  this.API_RETRIEVE_CORRESPONDENCE + CaseFile.Model.DOC_TYPE_CASE_FILE;
            url += "/" + caseFileId + "?category=" + CaseFile.Model.DOC_CATEGORY_CORRESPONDENCE_SM;

            return AcmEx.Model.JTable.serviceListAction(url, postData, jtParams, sortMap
                ,function(data) {
                    if (CaseFile.Model.Correspondence.validateCorrespondences(data)) {
                        var correspondences = data;
                        CaseFile.Model.Correspondence.cacheCorrespondences.put(cacheKey, correspondences);
                        return dataMaker(correspondences);
                    }
                }
            );
        }
        ,createCorrespondence : function(caseFile, templateName, cacheKey) {
            var url = this.API_CREATE_CORRESPONDENCE
                + "?templateName=" + templateName
                + "&parentObjectType=" + CaseFile.Model.DOC_TYPE_CASE_FILE
                + "&parentObjectId=" + caseFile.id
                + "&targetCmisFolderId=" + caseFile.container.folder.cmisFolderId;

            return Acm.Service.call({type: "POST"
                ,url: url
                ,callback: function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Model.Correspondence.validateNewCorrespondence(response)) {
                            var newCorrespondence = response;

                            //add to the top of the cache
                            var correspondenceData = CaseFile.Model.Correspondence.cacheCorrespondences.get(cacheKey);
                            if(CaseFile.Model.Correspondence.validateCorrespondences(correspondenceData)) {
                                var correspondences = correspondenceData.children;
                                var correspondence = {};
                                correspondence.objectId = Acm.goodValue(newCorrespondence.fileId);
                                correspondence.name = Acm.goodValue(newCorrespondence.fileName);
                                correspondence.creator = Acm.goodValue(newCorrespondence.creator);
                                correspondence.created = Acm.goodValue(newCorrespondence.created);
                                correspondence.objectType = CaseFile.Model.DOC_TYPE_FILE_SM;
                                correspondence.category = CaseFile.Model.DOC_CATEGORY_CORRESPONDENCE_SM;
                                correspondenceData.children.unshift(correspondence);
                                correspondenceData.totalChildren++;
                                return newCorrespondence;
                            }
                            //CaseFile.Controller.modelCreatedCorrespondence(response);
                        }
                    }
                }
            });
        }

        ,validateCorrespondences:function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.containerObjectId)) {
                return false;
            }
            if (Acm.isEmpty(data.folderId)) {
                return false;
            }
            if (Acm.isEmpty(data.children)) {
                return false;
            }
            if (Acm.isEmpty(data.totalChildren)) {
                return false;
            }
            if (Acm.isNotArray(data.children)) {
                return false;
            }
            for (var i = 0; i < data.children.length; i++) {
                if (!this.validateCorrespondence(data.children[i])) {
                    return false;
                }
            }
            if (!Acm.compare(data.category,CaseFile.Model.DOC_CATEGORY_CORRESPONDENCE_SM)) {
                return false;
            }
            return true;
        }

        ,validateCorrespondence: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.objectId)) {
                return false;
            }
            if (Acm.isEmpty(data.name)) {
                return false;
            }
            if (Acm.isEmpty(data.created)) {
                return false;
            }
            if (Acm.isEmpty(data.created)) {
                return false;
            }
            if (!Acm.compare(data.objectType,CaseFile.Model.DOC_TYPE_FILE_SM)) {
                return false;
            }
            if (!Acm.compare(data.category,CaseFile.Model.DOC_CATEGORY_CORRESPONDENCE_SM)) {
                return false;
            }
            return true;
        }

        ,validateNewCorrespondence: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.category)) {
                return false;
            }
            if (!Acm.compare(data.category, CaseFile.Model.DOC_CATEGORY_CORRESPONDENCE_SM)) {
                return false;
            }
            if (Acm.isEmpty(data.created)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.fileId)) {
                return false;
            }
            if (Acm.isEmpty(data.fileName)) {
                return false;
            }
            if (Acm.isEmpty(data.fileType)) {
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
    }

    ,History: {
        create : function() {
            this.cacheHistory = new Acm.Model.CacheFifo({maxSize: 16});
        }
        ,onInitialized: function() {
        }

        ,API_CASE_FILE_HISTORY : "/api/latest/plugin/audit"

        ,historyListAction : function(caseFileId, postData, jtParams, sortMap, dataMaker, cacheKey) {
            var history = CaseFile.Model.History.cacheHistory.get(cacheKey);
            if (CaseFile.Model.History.validateHistory(history)) {
                return Acm.Promise.donePromise(CaseFile.View.History._makeJtData(history)).promise();
            }

            var url;
            url =  CaseFile.Model.History.API_CASE_FILE_HISTORY;
            url += '/' + CaseFile.Model.DOC_TYPE_CASE_FILE + '/';
            url += caseFileId;

            return AcmEx.Model.JTable.serviceListAction(url, postData, jtParams, sortMap
                ,function(data) {
                    if (CaseFile.Model.History.validateHistory(data)) {
                        var history = data;
                        CaseFile.Model.Tasks.cacheTaskSolr.put(cacheKey, history);
                        return dataMaker(history);
                    }
                }
            );
        }

        ,validateHistory: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data.resultPage)) {
                return false;
            }
            for (var i = 0; i < data.resultPage.length; i++) {
                if (!this.validateEvent(data.resultPage[i])) {
                    return false;
                }
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


    ,Lookup: {
        create: function() {
            this._assignees    =  new Acm.Model.CacheFifo();
            this._subjectTypes = new Acm.Model.SessionData(ThisApp.SESSION_DATA_CASE_FILE_TYPES);
            this._priorities   = new Acm.Model.SessionData(ThisApp.SESSION_DATA_CASE_FILE_PRIORITIES);
            this._groups    =  new Acm.Model.CacheFifo();
            this._users    = new Acm.Model.SessionData(ThisApp.SESSION_DATA_CASE_FILE_USERS);
            
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT          ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        	// Do not do getAssignees() here. It should call after the object is loaded.
        	// This will help in the first loading to be retrieved correct users
        	// The place for calling this now is onModelRetrievedObject(...) in this file
            /*var assignees = CaseFile.Model.Lookup.getAssignees();
            if (Acm.isEmpty(assignees)) {
                CaseFile.Service.Lookup.retrieveAssignees();
            } else {
                CaseFile.Controller.modelFoundAssignees(assignees);
            }*/

            var subjectTypes = CaseFile.Model.Lookup.getSubjectTypes();
            if (Acm.isEmpty(subjectTypes)) {
                CaseFile.Service.Lookup.retrieveSubjectTypes();
            } else {
                CaseFile.Controller.modelFoundSubjectTypes(subjectTypes);
            }

            var personAssociationTypes = CaseFile.Model.Lookup.getPersonTypes();
            if (Acm.isArrayEmpty(personAssociationTypes)) {
                CaseFile.Service.Lookup.retrievePersonAssocitaionTypes();
            } else {
                CaseFile.Controller.modelFoundPersonAssociationTypes(personAssociationTypes);
            }

            var priorities = CaseFile.Model.Lookup.getPriorities();
            if (Acm.isEmpty(priorities)) {
                CaseFile.Service.Lookup.retrievePriorities();
            } else {
                CaseFile.Controller.modelFoundPriorities(priorities);
            }
            
            // Do not do getGroups() here. It should call after the object is loaded.
        	// This will help in the first loading to be retrieved correct groups
        	// The place for calling this now is onModelRetrievedObject(...) in this file
            /*var groups = CaseFile.Model.Lookup.getGroups();
            if (Acm.isEmpty(groups)) {
                CaseFile.Service.Lookup.retrieveGroups();
            } else {
                CaseFile.Controller.modelRetrievedGroups(groups);
            }*/
            
            var users = CaseFile.Model.Lookup.getUsers();
            if (Acm.isEmpty(users)) {
                CaseFile.Service.Lookup.retrieveUsers();
            } else {
                CaseFile.Controller.modelRetrievedUsers(users);
            }
        }

        ,getAssignees: function(caseFileId) {
            return this._assignees.get(caseFileId);
        }
        ,setAssignees: function(caseFileId, assignees) {
            this._assignees.put(caseFileId, assignees);
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
        ,getGroups: function(caseFileId) {
            return this._groups.get(caseFileId);
        }
        ,setGroups: function(caseFileId, groups) {
            this._groups.put(caseFileId, groups);
        }
        ,getUsers: function() {
            return this._users.get();
        }
        ,setUsers: function(users) {
            this._users.set(users);
        }


        //,options: App.getContextPath() + '/api/latest/plugin/complaint/types'
        ,_personTypes : [] //['Complaintant','Subject','Witness','Wrongdoer','Other', 'Initiator', 'Primary Victim', 'Victim', 'Defendant', 'Investigating Officer', 'Police Witness']
        ,getPersonTypes : function() {
            return CaseFile.Model.Lookup._personTypes;
        }
        ,setPersonTypes : function(personTypes) {
            this._personTypes = personTypes;
        }
        ,_personTitles : ['Mr','mr', 'Mrs','mrs', 'Ms','ms', 'Miss','miss']
        ,getPersonTitles : function() {
            return this._personTitles;
        }

        ,_contactMethodTypes : ['Home phone', 'Work phone', 'Mobile', 'Email','Facebook']

        /*,_contactMethodTypes : ['Home phone', 'Office phone', 'Cell phone', 'Pager',
            'Email','Instant messenger', 'Social media','Website','Blog'] */
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
//    ,getCaseFileTypes: function() {
//        var data = sessionStorage.getItem("AcmCaseFileTypes");
//        var item = ("null" === data)? null : JSON.parse(data);
//        return item;
//    }
//    ,setCaseFileTypes: function(data) {
//        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
//        sessionStorage.setItem("AcmCaseFileTypes", item);
//    }

        ,getCloseDispositions: function() {
            return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
            //return ["Close Deposition1", "Close Deposition2", "Close Deposition3", "Close Deposition4"];
        }
        
        ,_participantTypes : {'assignee': 'Assignee', 'co-owner': 'Co-Owner', 'supervisor': 'Supervisor', 'owning group': 'Owning Group', 'approver': 'Approver', 'collaborator': 'Collaborator', 'follower': 'Follower', 'reader': 'Reader', 'No Access': 'No Access'}
        ,getParticipantTypes : function() {
            return this._participantTypes;
        }
        
        ,onModelRetrievedObject: function(objData) {
        	CaseFile.Model.Lookup.refreshAssigneesAndGroups();
        }
        
        ,onViewSelectedObject: function(objType, objId) {
        	CaseFile.Model.Lookup.refreshAssigneesAndGroups();
        }
        
        ,refreshAssigneesAndGroups: function() {
        	var caseFileId = CaseFile.View.getActiveCaseFileId();
        	
        	var assignees = CaseFile.Model.Lookup.getAssignees(caseFileId);
            if (Acm.isEmpty(assignees)) {
                CaseFile.Service.Lookup.retrieveAssignees();
            } else {
                CaseFile.Controller.modelFoundAssignees(assignees);
            }
            
            var groups = CaseFile.Model.Lookup.getGroups(caseFileId);
            if (Acm.isEmpty(groups)) {
                CaseFile.Service.Lookup.retrieveGroups();
            } else {
                CaseFile.Controller.modelRetrievedGroups(groups);
            }
        }
    }

    ,Time: {
        create : function() {
            this.cacheTimesheets = new Acm.Model.CacheFifo();

            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
        }
        ,onInitialized: function() {
        }

//        ,onModelRetrievedObject: function(objData) {
//            CaseFile.Service.Time.retrieveTimesheets(CaseFile.Model.getCaseFileId());
//        }

        , API_RETRIEVE_TIMESHEETS: "/api/v1/service/timesheet/"

        ,getTimesheets : function(caseFileId) {
            var timesheets = CaseFile.Model.Time.cacheTimesheets.get(caseFileId);
            if (timesheets) {
                return Acm.Promise.donePromise(timesheets).promise();
            }

            var url = this.API_RETRIEVE_TIMESHEETS;
            url += "objectId/" + caseFileId + "/";
            url += "objectType/" + CaseFile.Model.DOC_TYPE_CASE_FILE;
            return Acm.Service.call({TYPE: "GET"
                ,url: url
                ,callback: function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Model.Time.validateTimesheets(response)) {
                            var timesheets = response;
                            CaseFile.Model.Time.cacheTimesheets.put(caseFileId, timesheets);
                            //CaseFile.Controller.modelRetrievedTimesheets(timesheets);
                            return timesheets;
                        }
                    }
                }
            });
        }

        ,validateTimesheet: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.user)) {
                return false;
            }
            if (Acm.isEmpty(data.user.userId)) {
                return false;
            }
            if (Acm.isEmpty(data.startDate)) {
                return false;
            }
            if (Acm.isEmpty(data.endDate)) {
                return false;
            }
            if (!Acm.isArray(data.times)) {
                return false;
            }
            if (Acm.isEmpty(data.status)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.modified)) {
                return false;
            }

            return true;
        }
        ,validateTimesheets: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateTimesheet(data[i])) {
                    return false;
                }
            }
            return true;
        }
        ,validateTimeRecord: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.code)) {
                return false;
            }
            if (Acm.isEmpty(data.type)) {
                return false;
            }
            if (Acm.isEmpty(data.objectId)) {
                return false;
            }
            if (Acm.isEmpty(data.value)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.modified)) {
                return false;
            }
            if (Acm.isEmpty(data.date)) {
                return false;
            }
            return true;
        }
    }

    ,Participants: {
        create : function() {
        }
        ,onInitialized: function() {
        }
    }

    ,Cost: {
        create : function() {
            this.cacheCostsheets = new Acm.Model.CacheFifo();

            //Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
        }
        ,onInitialized: function() {
        }

//        ,onModelRetrievedObject: function(objData) {
//            CaseFile.Service.Cost.retrieveCostsheets(CaseFile.Model.getCaseFileId());
//        }

        , API_RETRIEVE_COSTSHEETS: "/api/v1/service/costsheet/"

        ,retrieveCostsheets : function(caseFileId) {
            var costsheets = CaseFile.Model.Time.cacheTimesheets.get(caseFileId);
            if (costsheets) {
                return Acm.Promise.donePromise(costsheets).donePromise();
            }

            var url = this.API_RETRIEVE_COSTSHEETS;
            url += "objectId/" + caseFileId + "/";
            url += "objectType/" + CaseFile.Model.DOC_TYPE_CASE_FILE;
            return Acm.Service.call({type: "GET"
                ,url: url
                ,callback: function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Model.Cost.validateCostsheets(response)) {
                            var costsheets = response;
                            CaseFile.Model.Cost.cacheCostsheets.put(caseFileId, costsheets);
                            //CaseFile.Controller.modelRetrievedCostsheets(costsheets);
                            return costsheets;
                        }
                    }
                }
            });
        }

        ,validateCostsheet: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.user)) {
                return false;
            }
            if (Acm.isEmpty(data.user.userId)) {
                return false;
            }
            if (Acm.isEmpty(data.parentId)) {
                return false;
            }
            if (Acm.isEmpty(data.parentType)) {
                return false;
            }
            if (Acm.isEmpty(data.parentNumber)) {
                return false;
            }
            if (Acm.isEmpty(data.costs)) {
                return false;
            }
            if (Acm.isEmpty(data.status)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            return true;
        }
        ,validateCostsheets: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateCostsheet(data[i])) {
                    return false;
                }
            }
            return true;
        }
        ,validateCostRecord: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.title)) {
                return false;
            }
            if (Acm.isEmpty(data.description)) {
                return false;
            }
            if (Acm.isEmpty(data.value)) {
                return false;
            }
            if (Acm.isEmpty(data.date)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.modified)) {
                return false;
            }
            return true;
        }
    }

};

