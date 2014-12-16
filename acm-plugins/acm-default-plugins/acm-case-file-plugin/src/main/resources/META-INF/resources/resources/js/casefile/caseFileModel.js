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
        if (CaseFile.Model.CorrespondenceTemplates.create)        {CaseFile.Model.CorrespondenceTemplates.create();}

    }
    ,onInitialized: function() {
        var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
        if (0 < treeInfo.caseFileId) { //single caseFile
            CaseFile.Model.setCaseFileId(treeInfo.caseFileId);
            CaseFile.Service.Detail.retrieveCaseFile(treeInfo.caseFileId);
            CaseFile.Service.Tasks.retrieveTask();

        } else {
            CaseFile.Service.List.retrieveCaseFileList(treeInfo);
            CaseFile.Service.Tasks.retrieveTask();
        }

        if (CaseFile.Model.Lookup.onInitialized)     {CaseFile.Model.Lookup.onInitialized();}
        if (CaseFile.Model.Tree.onInitialized)       {CaseFile.Model.Tree.onInitialized();}
        if (CaseFile.Model.List.onInitialized)       {CaseFile.Model.List.onInitialized();}
        if (CaseFile.Model.Documents.onInitialized)  {CaseFile.Model.Documents.onInitialized();}
        if (CaseFile.Model.Detail.onInitialized)     {CaseFile.Model.Detail.onInitialized();}
        if (CaseFile.Model.Notes.onInitialized)      {CaseFile.Model.Notes.onInitialized();}
        if (CaseFile.Model.Tasks.onInitialized)      {CaseFile.Model.Tasks.onInitialized();}
        if (CaseFile.Model.References.onInitialized) {CaseFile.Model.References.onInitialized();}
        if (CaseFile.Model.Events.onInitialized)     {CaseFile.Model.Events.onInitialized();}
        if (CaseFile.Model.CorrespondenceTemplates.onInitialized)        {CaseFile.Model.CorrespondenceTemplates.onInitialized();}

    }

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
        }
        ,onInitialized: function() {
        }

        ,onViewPrevPageClicked: function() {
            CaseFile.Model.setCaseFileId(0);

            var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
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

            var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
            if (0 > treeInfo.total) {       //should never get to this condition
                treeInfo.start = 0;
            } else if ((treeInfo.total - treeInfo.n) > treeInfo.start) {
                treeInfo.start += treeInfo.n;
            }
            CaseFile.Service.List.retrieveCaseFileList(treeInfo);
        }
        ,onViewChangedCaseTitle: function(caseFileId, title) {
            var pageId = CaseFile.Model.Tree.Config.getPageId();
            var caseFiles = CaseFile.Model.List.cachePage.get(pageId);
            if (caseFiles) {
                for (var i = 0; i < caseFiles.length; i++) {
                    var c = caseFiles[i];
                    if (c) {
                        var cid = parseInt(Acm.goodValue(c.object_id_s, 0));
                        if (cid == caseFileId) {
                            c.title_t = title;
                            CaseFile.Model.List.cachePage.put(pageId, caseFiles);
                            break;
                        }
                    }
                } //end for i
            }
        }

    }

    ,Tree: {
        create : function() {
            if (CaseFile.Model.Tree.Config.create)    {CaseFile.Model.Tree.Config.create();}
            if (CaseFile.Model.Tree.Key.create)       {CaseFile.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (CaseFile.Model.Tree.Config.onInitialized)    {CaseFile.Model.Tree.Config.onInitialized();}
            if (CaseFile.Model.Tree.Key.onInitialized)       {CaseFile.Model.Tree.Key.onInitialized();}
        }

        ,Config: {
            create: function() {
                this._caseFileTreeInfo = new Acm.Model.SessionData("AcmCaseFileTreeInfo");

                var ti = this.getTreeInfo();
                var tiApp = this.getCaseFileTreeInfo();
                if (tiApp) {
                    ti.initKey = tiApp.initKey;
                    ti.start = tiApp.start;
                    ti.n = tiApp.n;
                    ti.s = tiApp.s;
                    ti.q = tiApp.q;
                    ti.caseFileId = tiApp.caseFileId;
                    this.setCaseFileTreeInfo(null);
                }
                var items = $(document).items();
                var caseFileId = items.properties("caseFileId").itemValue();
                if (Acm.isNotEmpty(caseFileId)) {
                    ti.caseFileId = caseFileId;
                }
            }
            ,onInitialized: function() {
            }

            ,_treeInfo: {
                start           : 0
                ,n              : 50
                ,total          : -1
                ,s              : null
                ,q              : null
                ,initKey        : null
                ,caseFileId    : 0
            }
            ,getTreeInfo: function() {
                return this._treeInfo;
            }
            ,getPageId: function() {
                return this._treeInfo.start;
            }

            ,getCaseFileTreeInfo: function() {
                var data = this._caseFileTreeInfo.get();
                if (Acm.isEmpty(data)) {
                    return null;
                }
                return JSON.parse(data);
            }
            ,setCaseFileTreeInfo: function(treeInfo) {
                var data = (Acm.isEmpty(treeInfo))? null : JSON.stringify(treeInfo);
                this._caseFileTreeInfo.set(data);
            }
        }

        ,Key: {
            create: function() {
            }
            ,onInitialized: function() {
            }


            ,NODE_TYPE_PART_PREV_PAGE:    "prevPage"
            ,NODE_TYPE_PART_NEXT_PAGE:    "nextPage"
            ,NODE_TYPE_PART_PAGE:         "p"
            ,NODE_TYPE_PART_OBJECT:       "c"
            ,NODE_TYPE_PART_CHILD:        "c"

            ,NODE_TYPE_PART_DETAILS:      "d"
            ,NODE_TYPE_PART_PEOPLE:       "p"
            ,NODE_TYPE_PART_DOCUMENTS:    "o"
            ,NODE_TYPE_PART_PARTICIPANTS: "a"
            ,NODE_TYPE_PART_NOTES:        "n"
            ,NODE_TYPE_PART_TASKS:        "t"
            ,NODE_TYPE_PART_REFERENCES:   "r"
            ,NODE_TYPE_PART_HISTORY:      "h"
            ,NODE_TYPE_PART_TEMPLATES:    "tm"


            ,_mapNodeType: [
                 {nodeType: "prevPage" ,icon: "i-arrow-up"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage" ,icon: "i-arrow-down" ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"        ,icon: ""             ,tabIds: ["tabBlank"]}
                ,{nodeType: "pc"       ,icon: "i-folder"     ,tabIds: ["tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory","tabTemplates"]}
                ,{nodeType: "pcd"      ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "pcp"      ,icon: "",tabIds: ["tabPeople"]}
                ,{nodeType: "pco"      ,icon: "",tabIds: ["tabDocs"]}
                //,{nodeType: "pcoc"     ,icon: "",tabIds: ["tabDoc"]}
                ,{nodeType: "pca"      ,icon: "",tabIds: ["tabParticipants"]}
                ,{nodeType: "pcn"      ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "pct"      ,icon: "",tabIds: ["tabTasks"]}
                ,{nodeType: "pcr"      ,icon: "",tabIds: ["tabRefs"]}
                ,{nodeType: "pch"      ,icon: "",tabIds: ["tabHistory"]}
                ,{nodeType: "pctm"      ,icon: "",tabIds: ["tabTemplates"]}
            ]

            ,getTabIdsByKey: function(key) {
                var nodeType = this.getNodeTypeByKey(key);
                //var tabIds = ["tabBlank"];
                var tabIds = [];
                for (var i = 0; i < this._mapNodeType.length; i++) {
                    if (nodeType == this._mapNodeType[i].nodeType) {
                        tabIds = this._mapNodeType[i].tabIds;
                        break;
                    }
                }
                return tabIds;
            }
            ,getIconByKey: function(key) {
                var nodeType = this.getNodeTypeByKey(key);
                var icon = null;
                for (var i = 0; i < this._mapNodeType.length; i++) {
                    if (nodeType == this._mapNodeType[i].nodeType) {
                        icon = this._mapNodeType[i].icon;
                        break;
                    }
                }
                return icon;
            }
            ,getTabIds: function() {
                var tabIds = [];
                for (var i = 0; i < this._mapNodeType.length; i++) {
                    var tabIdsThis = this._mapNodeType[i].tabIds;
                    for (var j = 0; j < tabIdsThis.length; j++) {
                        var tabId = tabIdsThis[j];
                        if (!Acm.isItemInArray(tabId, tabIds)) {
                            tabIds.push(tabId);
                        }
                    }
                }
                return tabIds;
            }
            ,getNodeTypeByKey: function(key) {
                if (Acm.isEmpty(key)) {
                    return null;
                }

                var arr = key.split(".");
                if (1 == arr.length) {
                    if (this.NODE_TYPE_PART_PREV_PAGE == key) {
                        return this.NODE_TYPE_PART_PREV_PAGE;
                    } else if (this.NODE_TYPE_PART_NEXT_PAGE == key) {
                        return this.NODE_TYPE_PART_NEXT_PAGE;
                    } else { //if ($.isNumeric(arr[0])) {
                        return this.NODE_TYPE_PART_PAGE;
                    }
                } else if (2 == arr.length) {
                    return this.NODE_TYPE_PART_PAGE + this.NODE_TYPE_PART_OBJECT;
                } else if (3 == arr.length) {
                    return this.NODE_TYPE_PART_PAGE + this.NODE_TYPE_PART_OBJECT + arr[2];
                } else if (4 == arr.length) {
                    return this.NODE_TYPE_PART_PAGE + this.NODE_TYPE_PART_OBJECT + arr[2] + this.NODE_TYPE_PART_CHILD;
                }
                return null;
            }
            ,getCaseFileIdByKey: function(key) {
                return this._parseKey(key).caseFileId;
            }
            ,getPageIdByKey: function(key) {
                return this._parseKey(key).pageId;
            }
            ,getChildIdByKey: function(key) {
                return this._parseKey(key).childId;
            }
            ,_parseKey: function(key) {
                var parts = {pageId: -1, caseFileId: 0, sub: "", childId: 0};
                if (Acm.isEmpty(key)) {
                    return parts;
                }

                var arr = key.split(".");
                if (1 <= arr.length) {
                    var pageId = parseInt(arr[0]);
                    if (! isNaN(pageId)) {
                        parts.pageId = pageId;
                    }
                }
                if (2 <= arr.length) {
                    var caseFileId = parseInt(arr[1]);
                    if (! isNaN(caseFileId)) {
                        parts.caseFileId = caseFileId;
                    }
                }
                if (3 <= arr.length) {
                    parts.sub = arr[2];
                }
                if (4 <= arr.length) {
                    var childId = parseInt(arr[3]);
                    if (! isNaN(caseFileId)) {
                        parts.childId = childId;
                    }
                }
                return parts;
            }
            ,getCaseFileKey: function(caseFileId) {
                var pageId = CaseFile.Model.Tree.Config.getPageId();
                return pageId + "." + caseFileId;
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
        }
    }
    ,Documents: {
        create : function() {
            this.cacheDocuments = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
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

    ,CorrespondenceTemplates: {
        create: function () {
        }
        , onInitialized: function () {
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

