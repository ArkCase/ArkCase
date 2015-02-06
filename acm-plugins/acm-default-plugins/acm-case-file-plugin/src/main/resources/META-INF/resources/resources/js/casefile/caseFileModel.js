/**
 * CaseFile.Model
 *
 * @author jwu
 */
CaseFile.Model = CaseFile.Model || {
    create : function() {
        if (CaseFile.Model.Lookup.create)        {CaseFile.Model.Lookup.create();}
        if (CaseFile.Model.Tree.create)          {CaseFile.Model.Tree.create();}
        if (CaseFile.Model.List.create)          {CaseFile.Model.List.create();}
        if (CaseFile.Model.Documents.create)     {CaseFile.Model.Documents.create();}
        if (CaseFile.Model.Detail.create)        {CaseFile.Model.Detail.create();}
        if (CaseFile.Model.Notes.create)         {CaseFile.Model.Notes.create();}
        if (CaseFile.Model.Tasks.create)         {CaseFile.Model.Tasks.create();}
        if (CaseFile.Model.References.create)    {CaseFile.Model.References.create();}
        if (CaseFile.Model.Events.create)        {CaseFile.Model.Events.create();}
        if (CaseFile.Model.Correspondence.create) {CaseFile.Model.Correspondence.create();}

        if ("undefined" != typeof Topbar) {
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_SET_ASN_DATA, this.onTopbarViewSetAsnData);
        }

    }
    ,onInitialized: function() {
        AcmEx.Model.Tree.Key.setNodeTypeMap(CaseFile.Model.Tree.Key.nodeTypeMap);
        AcmEx.Model.Tree.Config.setName("/plugin/casefile");
        var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
        if (AcmEx.Object.TreeModifier.defaultFilter) {
            treeInfo.filter = AcmEx.Object.TreeModifier.defaultFilter;
        }
        if (AcmEx.Object.TreeModifier.defaultSort) {
            treeInfo.sort = AcmEx.Object.TreeModifier.defaultSort;
        }

        CaseFile.Model.retrieveData(treeInfo);

        if (CaseFile.Model.Lookup.onInitialized)     {CaseFile.Model.Lookup.onInitialized();}
        if (CaseFile.Model.Tree.onInitialized)       {CaseFile.Model.Tree.onInitialized();}
        if (CaseFile.Model.List.onInitialized)       {CaseFile.Model.List.onInitialized();}
        if (CaseFile.Model.Documents.onInitialized)  {CaseFile.Model.Documents.onInitialized();}
        if (CaseFile.Model.Detail.onInitialized)     {CaseFile.Model.Detail.onInitialized();}
        if (CaseFile.Model.Notes.onInitialized)      {CaseFile.Model.Notes.onInitialized();}
        if (CaseFile.Model.Tasks.onInitialized)      {CaseFile.Model.Tasks.onInitialized();}
        if (CaseFile.Model.References.onInitialized) {CaseFile.Model.References.onInitialized();}
        if (CaseFile.Model.Events.onInitialized)     {CaseFile.Model.Events.onInitialized();}
        if (CaseFile.Model.Correspondence.onInitialized) {CaseFile.Model.Correspondence.onInitialized();}

    }

    ,onTopbarViewSetAsnData: function(asnData) {
        if (AcmEx.Model.Tree.Config.validateTreeInfo(asnData)) {
            if ("/plugin/casefile" == asnData.name) {
                var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
                var sameResultSet = AcmEx.Model.Tree.Config.sameResultSet(asnData);
                AcmEx.Model.Tree.Config.readTreeInfo();

                if (!sameResultSet) {
                    CaseFile.Model.retrieveData(treeInfo);
                }
                return true;
            }
        }
        return false;
    }

    ,retrieveData: function(treeInfo) {
        if (0 < treeInfo.objId) { //single caseFile
            CaseFile.Model.setCaseFileId(treeInfo.objId);
            CaseFile.Service.Detail.retrieveCaseFile(treeInfo.objId);

        } else {
            CaseFile.Service.List.retrieveCaseFileList(treeInfo);
        }
    }

    ,DOCUMENT_TARGET_TYPE_FILE: "FILE"
    ,DOCUMENT_CATEGORY_CORRESPONDENCE: "CORRESPONDENCE"

    ,_objectType: "CASE_FILE"
    ,getObjectType: function() {
        return this._objectType;
    }

    ,_caseFileId: 0
    ,getCaseFileId : function() {
        return this._caseFileId;
    }
    ,setCaseFileId : function(id) {
        this._caseFileId = id;
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

            ,NODE_TYPE_PART_OBJECT       : "c"
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
                {nodeType: "prevPage"    ,icon: "i-arrow-up"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"   ,icon: "i-arrow-down" ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"          ,icon: ""             ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/c"        ,icon: "i-folder"     ,tabIds: ["tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory","tabTemplates"]}
                ,{nodeType: "p/c/d"      ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/c/p"      ,icon: "",tabIds: ["tabPeople"]}
                ,{nodeType: "p/c/o"      ,icon: "",tabIds: ["tabDocs"]}
                //,{nodeType: "p/c/o/c"     ,icon: "",tabIds: ["tabDoc"]}
                ,{nodeType: "p/c/a"      ,icon: "",tabIds: ["tabParticipants"]}
                ,{nodeType: "p/c/n"      ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "p/c/t"      ,icon: "",tabIds: ["tabTasks"]}
                ,{nodeType: "p/c/r"      ,icon: "",tabIds: ["tabRefs"]}
                ,{nodeType: "p/c/h"      ,icon: "",tabIds: ["tabHistory"]}
                ,{nodeType: "p/c/tm"     ,icon: "",tabIds: ["tabTemplates"]}
            ]

            ,getKeyByObj: function(objId) {
                var pageId = AcmEx.Model.Tree.Config.getPageId();
                return this.getKeyByObjWithPage(pageId, objId);
            }
            ,getKeyByObjWithPage: function(pageId, objId) {
                var subKey = this.NODE_TYPE_PART_OBJECT
                        + AcmEx.Model.Tree.Key.TYPE_ID_SEPARATOR
                        + objId
                        ;
                return this.getKeyBySubWithPage(pageId, subKey);
            }
            ,getKeyBySubWithPage: function(pageId, subKey) {
                return AcmEx.Model.Tree.Key.NODE_TYPE_PART_PAGE
                    + AcmEx.Model.Tree.Key.TYPE_ID_SEPARATOR
                    + pageId
                    + AcmEx.Model.Tree.Key.KEY_SEPARATOR
                    + subKey
                    ;
            }
        }
    }

    ,Detail: {
        create : function() {
            this.cacheCaseFile = new Acm.Model.CacheFifo(3);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_SELECTED_CASE_FILE          ,this.onViewSelectedCaseFile);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CASE_FILE           , this.onViewChangedCaseFile);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CASE_TITLE          , this.onViewChangedCaseTitle);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_INCIDENT_DATE       , this.onViewChangedIncidentDate);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_ASSIGNEE            , this.onViewChangedAssignee);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_SUBJECT_TYPE        , this.onViewChangedSubjectType);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_PRIORITY            , this.onViewChangedPriority);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_DUE_DATE            , this.onViewChangedDueDate);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_DETAIL              , this.onViewChangedDetail);
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
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLICKED_RESTRICT_CHECKBOX        , this.onViewClickedRestrictCheckbox);

        }
        ,onInitialized: function() {
        }

        ,onViewSelectedCaseFile: function(caseFileId) {
            CaseFile.Model.setCaseFileId(caseFileId);
            var caseFile = CaseFile.Model.Detail.cacheCaseFile.get(caseFileId);
            if (!caseFile) {
                CaseFile.Service.Detail.retrieveCaseFile(caseFileId);
            }
        }
        ,onViewChangedCaseFile: function(caseFileId) {
        	CaseFile.Service.Detail.retrieveCaseFile(caseFileId);
        }
        ,onViewChangedCaseTitle: function(caseFileId, title) {
            CaseFile.Service.Detail.saveCaseTitle(caseFileId, title);
        }

        ,onViewChangedIncidentDate: function(caseFileId, incidentDate) {
            CaseFile.Service.Detail.saveIncidentDate(caseFileId, incidentDate);
        }
        ,onViewChangedAssignee: function(caseFileId, assignee) {
            CaseFile.Service.Detail.saveAssignee(caseFileId, assignee);
        }
        ,onViewChangedSubjectType: function(caseFileId, caseType) {
            CaseFile.Service.Detail.saveSubjectType(caseFileId, caseType);
        }
        ,onViewChangedPriority: function(caseFileId, priority) {
            CaseFile.Service.Detail.savePriority(caseFileId, priority);
        }
        ,onViewChangedDueDate: function(caseFileId, dueDate) {
            CaseFile.Service.Detail.saveDueDate(caseFileId, dueDate);
        }
        ,onViewChangedDetail: function(caseFileId, details) {
            CaseFile.Service.Detail.saveDetail(caseFileId, details);
        }
        ,onViewChangedChildObject: function(caseFileId, childObject) {
            CaseFile.Service.Detail.saveChildObject(caseFileId, childObject);
        }
        ,onViewAddedParticipant: function(caseFileId, participant) {
            CaseFile.Service.Detail.addParticipant(caseFileId, participant);
        }
        ,onViewUpdatedParticipant: function(caseFileId, participant) {
            CaseFile.Service.Detail.updateParticipant(caseFileId, participant);
        }
        ,onViewDeletedParticipant: function(caseFileId, participantId) {
            CaseFile.Service.Detail.deleteParticipant(caseFileId, participantId);
        }
        ,onViewAddedPersonAssociation: function(caseFileId, personAssociation) {
            var pa = CaseFile.Model.Detail.newPersonAssociation();
            pa.parentType = CaseFile.Model.getObjectType();
            pa.parentId = caseFileId;
            pa.personType = personAssociation.personType;
            //pa.personDescription = personAssociation.personDescription;
            pa.person.title = personAssociation.person.title;
            pa.person.givenName = personAssociation.person.givenName;
            pa.person.familyName = personAssociation.person.familyName;
            CaseFile.Service.Detail.addPersonAssociation(caseFileId, pa);
        }
        ,onViewUpdatedPersonAssociation: function(caseFileId, personAssociation) {
            CaseFile.Service.Detail.updatePersonAssociation(caseFileId, personAssociation);
        }
        ,onViewDeletedPersonAssociation: function(caseFileId, personAssociationId) {
            CaseFile.Service.Detail.deletePersonAssociation(caseFileId, personAssociationId);
        }
        ,onViewAddedAddress: function(caseFileId, personAssociationId, address) {
            CaseFile.Service.Detail.addAddress(caseFileId, personAssociationId, address);
        }
        ,onViewUpdatedAddress: function(caseFileId, personAssociationId, address) {
            CaseFile.Service.Detail.updateAddress(caseFileId, personAssociationId, address);
        }
        ,onViewDeletedAddress: function(caseFileId, personAssociationId, addressId) {
            CaseFile.Service.Detail.deleteAddress(caseFileId, personAssociationId, addressId);
        }
        ,onViewAddedContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            CaseFile.Service.Detail.addContactMethod(caseFileId, personAssociationId, contactMethod);
        }
        ,onViewUpdatedContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            CaseFile.Service.Detail.updateContactMethod(caseFileId, personAssociationId, contactMethod);
        }
        ,onViewDeletedContactMethod: function(caseFileId, personAssociationId, contactMethodId) {
            CaseFile.Service.Detail.deleteContactMethod(caseFileId, personAssociationId, contactMethodId);
        }
        ,onViewAddedSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            CaseFile.Service.Detail.addSecurityTag(caseFileId, personAssociationId, securityTag);
        }
        ,onViewUpdatedSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            CaseFile.Service.Detail.updateSecurityTag(caseFileId, personAssociationId, securityTag);
        }
        ,onViewDeletedSecurityTag: function(caseFileId, personAssociationId, securityTagId) {
            CaseFile.Service.Detail.deleteSecurityTag(caseFileId, personAssociationId, securityTagId);
        }
        ,onViewAddedPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            CaseFile.Service.Detail.addPersonAlias(caseFileId, personAssociationId, personAlias);
        }
        ,onViewUpdatedPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            CaseFile.Service.Detail.updatePersonAlias(caseFileId, personAssociationId, personAlias);
        }
        ,onViewDeletedPersonAlias: function(caseFileId, personAssociationId, personAliasId) {
            CaseFile.Service.Detail.deletePersonAlias(caseFileId, personAssociationId, personAliasId);
        }
        ,onViewAddedOrganization: function(caseFileId, personAssociationId, organization) {
            CaseFile.Service.Detail.addOrganization(caseFileId, personAssociationId, organization);
        }
        ,onViewUpdatedOrganization: function(caseFileId, personAssociationId, organization) {
            CaseFile.Service.Detail.updateOrganization(caseFileId, personAssociationId, organization);
        }
        ,onViewDeletedOrganization: function(caseFileId, personAssociationId, organizationId) {
            CaseFile.Service.Detail.deleteOrganization(caseFileId, personAssociationId, organizationId);
        }
        ,onViewClickedRestrictCheckbox: function(caseFileId, restriction) {
            CaseFile.Service.Detail.updateCaseRestriction(caseFileId, restriction);
        }

        ,getCaseFile: function(caseFileId) {
            if (0 >= caseFileId) {
                return null;
            }
            return this.cacheCaseFile.get(caseFileId);
        }
//        ,getCaseFileCurrent: function() {
//            return this.getCaseFile(this._caseFileId);
//        }

        ,getAssignee: function(caseFile) {
            var assignee = null;
            if (caseFile) {
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

        ,validateData: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id) || Acm.isEmpty(data.caseNumber)) {
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

    ,List: {
        create : function() {
            this.cachePage = new Acm.Model.CacheFifo(2);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLICKED_PREV_PAGE      ,this.onViewPrevPageClicked);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLICKED_NEXT_PAGE      ,this.onViewNextPageClicked);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_CASE_TITLE     ,this.onViewChangedCaseTitle);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_TREE_FILTER    ,this.onViewChangedTreeFilter);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CHANGED_TREE_SORT      ,this.onViewChangedTreeSort);
        }
        ,onInitialized: function() {
        }

        ,onViewPrevPageClicked: function() {
            CaseFile.Model.setCaseFileId(0);

            var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
            if (0 < treeInfo.start) {
                treeInfo.start -= treeInfo.n;
                if (0 > treeInfo.start) {
                    treeInfo.start = 0;
                }
            }
            CaseFile.Service.List.retrieveCaseFileList(treeInfo);
        }
        ,onViewNextPageClicked: function() {
            CaseFile.Model.setCaseFileId(0);

            var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
            if (0 > treeInfo.total) {       //should never get to this condition
                treeInfo.start = 0;
            } else if ((treeInfo.total - treeInfo.n) > treeInfo.start) {
                treeInfo.start += treeInfo.n;
            }
            CaseFile.Service.List.retrieveCaseFileList(treeInfo);
        }
        ,onViewChangedCaseTitle: function(caseFileId, title) {
            var pageId = AcmEx.Model.Tree.Config.getPageId();
            var caseFiles = CaseFile.Model.List.cachePage.get(pageId);
            if (caseFiles) {
                for (var i = 0; i < caseFiles.length; i++) {
                    var c = caseFiles[i];
                    if (c) {
                        var cid = parseInt(Acm.goodValue(c.object_id_s, 0));
                        if (cid == caseFileId) {
                            c.title_parseable = title;
                            CaseFile.Model.List.cachePage.put(pageId, caseFiles);
                            break;
                        }
                    }
                } //end for i
            }
        }
        ,onViewChangedTreeFilter: function(filter) {
            var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
            if (!Acm.compare(treeInfo.filter, filter)) {
                CaseFile.Model.setCaseFileId(0);
                treeInfo.start = 0;
                treeInfo.filter = filter;
                CaseFile.Service.List.retrieveCaseFileList(treeInfo);
            }
        }
        ,onViewChangedTreeSort: function(sort) {
            var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
            if (!Acm.compare(treeInfo.sort, sort)) {
                CaseFile.Model.setCaseFileId(0);
                treeInfo.start = 0;
                treeInfo.sort = sort;
                CaseFile.Service.List.retrieveCaseFileList(treeInfo);
            }
        }

    }


    ,Notes: {
        create : function() {
            this.cacheNoteList = new Acm.Model.CacheFifo(4);

            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_ADDED_NOTE     , this.onViewAddedNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_UPDATED_NOTE   , this.onViewUpdatedNote);
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_DELETED_NOTE   , this.onViewDeletedNote);
        }
        ,onInitialized: function() {
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
    }

    ,Tasks: {
        create : function() {
            this.cacheTaskSolr = new Acm.Model.CacheFifo(4);
            this.cacheTasks = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
            CaseFile.Service.Tasks.retrieveTask();
        }
    }
    ,Documents: {
        create : function() {
            this.cacheDocuments = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
        }
    }
    ,Correspondence: {
        create : function() {
            Acm.Dispatcher.addEventListener(CaseFile.Controller.VIEW_CLICKED_ADD_CORRESPONDENCE, this.onViewClickedAddCorrespondence);
        }
        ,onInitialized: function() {
        }


        ,onViewClickedAddCorrespondence: function(caseFileId, templateName) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                CaseFile.Service.Correspondence.createCorrespondence(caseFile, templateName);
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
            this._assignees    = new Acm.Model.SessionData("AcmCaseAssignees");
            this._subjectTypes = new Acm.Model.SessionData("AcmCaseTypes");
            this._priorities   = new Acm.Model.SessionData("AcmCasePriorities");
        }
        ,onInitialized: function() {
            var assignees = CaseFile.Model.Lookup.getAssignees();
            if (Acm.isEmpty(assignees)) {
                CaseFile.Service.Lookup.retrieveAssignees();
            } else {
                CaseFile.Controller.modelFoundAssignees(assignees);
            }

            var subjectTypes = CaseFile.Model.Lookup.getSubjectTypes();
            if (Acm.isEmpty(subjectTypes)) {
                CaseFile.Service.Lookup.retrieveSubjectTypes();
            } else {
                CaseFile.Controller.modelFoundSubjectTypes(subjectTypes);
            }

            var priorities = CaseFile.Model.Lookup.getPriorities();
            if (Acm.isEmpty(priorities)) {
                CaseFile.Service.Lookup.retrievePriorities();
            } else {
                CaseFile.Controller.modelFoundPriorities(priorities);
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
    }

};

