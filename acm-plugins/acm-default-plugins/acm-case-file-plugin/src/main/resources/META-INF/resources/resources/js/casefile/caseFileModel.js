/**
 * CaseFile.Model
 *
 * @author jwu
 */
CaseFile.Model = {
    create : function() {
        this.cachePage = new Acm.Model.CacheFifo(2);
        this.cacheCaseFile = new Acm.Model.CacheFifo(3);
        //this.cacheCaseEvents = new Acm.Model.CacheFifo(3);

        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_PREV_PAGE_CLICKED      ,this.onPrevPageClicked);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_NEXT_PAGE_CLICKED      ,this.onNextPageClicked);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_FILE_SELECTED     ,this.onCaseFileSelected);

        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CASE_TITLE_CHANGED     ,this.onCaseTitleChanged);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_INCIDENT_DATE_CHANGED  ,this.onIncidentDateChanged);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_ASSIGNEE_CHANGED       ,this.onAssigneeChanged);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_SUBJECT_TYPE_CHANGED   ,this.onSubjectTypeChanged);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_PRIORITY_CHANGED       ,this.onPriorityChanged);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_DUE_DATE_CHANGED       ,this.onDueDateChanged);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_DETAIL_CHANGED         ,this.onDetailChanged);
        Acm.Dispatcher.addEventListener(CaseFile.Controller.VE_CHILD_OBJECT_CHANGED   ,this.onChildObjectChanged);

        if (CaseFile.Model.Lookup.create)  {CaseFile.Model.Lookup.create();}
        if (CaseFile.Model.Tree.create)    {CaseFile.Model.Tree.create();}
        if (CaseFile.Model.Notes.create)   {CaseFile.Model.Notes.create();}
        if (CaseFile.Model.Tasks.create)   {CaseFile.Model.Tasks.create();}
    }
    ,initialize: function() {
        var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
        if (0 < treeInfo.caseFileId) { //single caseFile
            CaseFile.Model.setCaseFileId(treeInfo.caseFileId);
            CaseFile.Service.Detail.retrieveCaseFile(treeInfo.caseFileId);
        } else {
            CaseFile.Service.List.retrieveCaseFileList(treeInfo);
        }

        if (CaseFile.Model.Lookup.initialize)  {CaseFile.Model.Lookup.initialize();}
        if (CaseFile.Model.Tree.initialize)    {CaseFile.Model.Tree.initialize();}
        if (CaseFile.Model.Notes.initialize)   {CaseFile.Model.Notes.initialize();}
        if (CaseFile.Model.Tasks.initialize)   {CaseFile.Model.Tasks.initialize();}
    }

    ,onPrevPageClicked: function() {
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
    ,onNextPageClicked: function() {
        CaseFile.Model.setCaseFileId(0);

        var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
        if (0 > treeInfo.total) {       //should never get to this condition
            treeInfo.start = 0;
        } else if ((treeInfo.total - treeInfo.n) > treeInfo.start) {
            treeInfo.start += treeInfo.n;
        }
        CaseFile.Service.List.retrieveCaseFileList(treeInfo);
    }
    ,onCaseFileSelected: function(caseFileId) {
        CaseFile.Model.setCaseFileId(caseFileId);
        var caseFile = CaseFile.Model.cacheCaseFile.get(caseFileId);
        if (!caseFile) {
            CaseFile.Service.Detail.retrieveCaseFile(caseFileId);
        }
    }
    ,onCaseTitleChanged: function(caseFileId, title) {
        CaseFile.Service.Detail.saveCaseTitle(caseFileId, title);

        var pageId = CaseFile.Model.Tree.Config.getPageId();
        var caseFiles = CaseFile.Model.cachePage.get(pageId);
        if (caseFiles) {
            for (var i = 0; i < caseFiles.length; i++) {
                var c = caseFiles[i];
                if (c) {
                    var cid = parseInt(Acm.goodValue(c.object_id_s, 0));
                    if (cid == caseFileId) {
                        c.title_t = title;
                        CaseFile.Model.cachePage.put(pageId, caseFiles);
                        break;
                    }
                }
            } //end for i
        }
    }
    ,onIncidentDateChanged: function(caseFileId, incidentDate) {
        CaseFile.Service.Detail.saveIncidentDate(caseFileId, incidentDate);
    }
    ,onAssigneeChanged: function(caseFileId, assignee) {
        CaseFile.Service.Detail.saveAssignee(caseFileId, assignee);
    }
    ,onSubjectTypeChanged: function(caseFileId, caseType) {
        CaseFile.Service.Detail.saveSubjectType(caseFileId, caseType);
    }
    ,onPriorityChanged: function(caseFileId, priority) {
        CaseFile.Service.Detail.savePriority(caseFileId, priority);
    }
    ,onDueDateChanged: function(caseFileId, dueDate) {
        CaseFile.Service.Detail.saveDueDate(caseFileId, dueDate);
    }
    ,onDetailChanged: function(caseFileId, details) {
        CaseFile.Service.Detail.saveDetail(caseFileId, details);
    }
    ,onChildObjectChanged: function(caseFileId, idx, childObject) {
        CaseFile.Service.Detail.saveChildObject(caseFileId, idx, childObject);
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
    ,getCaseFile: function(caseFileId) {
        if (0 >= caseFileId) {
            return null;
        }
        return this.cacheCaseFile.get(caseFileId);
    }
    ,getCaseFileCurrent: function() {
        return this.getCaseFile(this._caseFileId);
    }

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


            participant = {};
            participant.participantType = "assignee";
            participant.participantLdapId = assignee;
            caseFile.participants.push(participant);
        }
    }

    ,Notes: {
        create : function() {
            this.cacheNoteList = new Acm.Model.CacheFifo(4);
        }
        ,initialize: function() {
        }
    }

    ,Tasks: {
        create : function() {
            this.cacheTaskList = new Acm.Model.CacheFifo(4);
        }
        ,initialize: function() {
        }
    }

    ,Tree: {
        create : function() {
            if (CaseFile.Model.Tree.Config.create)    {CaseFile.Model.Tree.Config.create();}
            if (CaseFile.Model.Tree.Key.create)       {CaseFile.Model.Tree.Key.create();}
        }
        ,initialize: function() {
            if (CaseFile.Model.Tree.Config.initialize)    {CaseFile.Model.Tree.Config.initialize();}
            if (CaseFile.Model.Tree.Key.initialize)       {CaseFile.Model.Tree.Key.initialize();}
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
            ,initialize: function() {
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
            ,initialize: function() {
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
            ,NODE_TYPE_PART_TASKS:   "r"
            ,NODE_TYPE_PART_HISTORY:      "h"

            ,_mapNodeType: [
                 {nodeType: "prevPage" ,icon: "i-arrow-up"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage" ,icon: "i-arrow-down" ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"        ,icon: ""             ,tabIds: ["tabBlank"]}
                ,{nodeType: "pc"       ,icon: "i-folder"     ,tabIds: ["tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory"]}
                ,{nodeType: "pcd"      ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "pcp"      ,icon: "",tabIds: ["tabPeople"]}
                ,{nodeType: "pco"      ,icon: "",tabIds: ["tabDocs"]}
                //,{nodeType: "pcoc"     ,icon: "",tabIds: ["tabDoc"]}
                ,{nodeType: "pca"      ,icon: "",tabIds: ["tabParticipants"]}
                ,{nodeType: "pcn"      ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "pct"      ,icon: "",tabIds: ["tabTasks"]}
                ,{nodeType: "pcr"      ,icon: "",tabIds: ["tabRefs"]}
                ,{nodeType: "pch"      ,icon: "",tabIds: ["tabHistory"]}
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

    ,Lookup: {
        create: function() {
            this._assignees    = new Acm.Model.SessionData("AcmCaseAssignees");
            this._subjectTypes = new Acm.Model.SessionData("AcmCaseTypes");
            this._priorities   = new Acm.Model.SessionData("AcmCasePriorities");
        }
        ,initialize: function() {
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
        
        ,PERSON_SUBTABLE_TITLE_DEVICES:       "Communication Devices"
        ,PERSON_SUBTABLE_TITLE_ORGANIZATIONS: "Organizations"
        ,PERSON_SUBTABLE_TITLE_LOCATIONS:     "Locations"
        ,PERSON_SUBTABLE_TITLE_ALIASES:       "Aliases"

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


        ,_personTypes : ['Complaintant','Subject','Witness','Wrongdoer','Other', 'Initiator']
        ,getPersonTypes : function() {
            return this._personTypes;
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

