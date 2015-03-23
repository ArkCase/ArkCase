/**
 * Costsheet.Model
 *
 * @author md
 */
Costsheet.Model = {
    create : function() {
        if (Costsheet.Service.create)               {Costsheet.Service.create();}
        if (Costsheet.Model.MicroData.create)       {Costsheet.Model.MicroData.create();}
        if (Costsheet.Model.Tree.create)            {Costsheet.Model.Tree.create();}
        if (Costsheet.Model.Action.create)          {Costsheet.Model.Action.create();}
        if (Costsheet.Model.Detail.create)          {Costsheet.Model.Detail.create();}
        if (Costsheet.Model.ParentDetail.create)    {Costsheet.Model.ParentDetail.create();}
        if (Costsheet.Model.Person.create)          {Costsheet.Model.Person.create();}
        if (Costsheet.Model.CostSummary.create)     {Costsheet.Model.CostSummary.create();}
    }
    ,onInitialized: function() {
        if (Costsheet.Service.onInitialized)              {Costsheet.Service.onInitialized();}
        if (Costsheet.Model.MicroData.onInitialized)      {Costsheet.Model.MicroData.onInitialized();}
        if (Costsheet.Model.Tree.onInitialized)           {Costsheet.Model.Tree.onInitialized();}
        if (Costsheet.Model.Action.onInitialized)         {Costsheet.Model.Action.onInitialized();}
        if (Costsheet.Model.Detail.onInitialized)         {Costsheet.Model.Detail.onInitialized();}
        if (Costsheet.Model.ParentDetail.onInitialized)   {Costsheet.Model.ParentDetail.onInitialized();}
        if (Costsheet.Model.Person.onInitialized)         {Costsheet.Model.Person.onInitialized();}
        if (Costsheet.Model.CostSummary.onInitialized)    {Costsheet.Model.CostSummary.onInitialized();}
    }

    ,interface: {
        apiListObjects: function() {
            return "/api/v1/service/costsheet/user/" + App.getUserName();
        }
        ,apiRetrieveObject: function(nodeType, objId) {
            return "/api/v1/service/costsheet/" + objId;
        }
        ,apiSaveObject: function(nodeType, objId) {
            return "/api/v1/service/costsheet";
        }
        ,nodeId: function(objSolr) {
            return objSolr.object_id_s;
        }
        ,nodeType: function(objSolr) {
            return Costsheet.Model.DOC_TYPE_COSTSHEET;
        }
        ,nodeTitle: function(objSolr) {
            return Acm.goodValue(objSolr.name);
        }
        ,nodeToolTip: function(objSolr) {
            return Acm.goodValue(objSolr.name);
        }
        ,objToSolr: function(objData) {
            var solr = {};
            solr.create_tdt = objData.created;
            solr.author_s = objData.creator;
            solr.object_id_s = objData.id;
            solr.object_type_s = Costsheet.Model.DOC_TYPE_COSTSHEET;
            return solr;
        }
        ,validateObjData: function(data) {
            return Costsheet.Model.Detail.validateCostsheet(data);
        }
        ,nodeTypeMap: function() {
            return Costsheet.Model.Tree.Key.nodeTypeMap;
        }
    }

    ,DOC_TYPE_COSTSHEET  : "COSTSHEET"
    ,DOC_TYPE_COMPLAINT  : "COMPLAINT"
    ,DOC_TYPE_CASE_FILE  : "CASE_FILE"

    ,getActiveCostsheetId : function() {
        return ObjNav.Model.getObjectId();
    }
    ,getActiveCostsheet: function(objType, objId) {
        return ObjNav.Model.Detail.getCacheObject(objType, objId);
    }


    ,Tree: {
        create: function() {
            if (Costsheet.Model.Tree.Key.create)        {Costsheet.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (Costsheet.Model.Tree.Key.onInitialized)        {Costsheet.Model.Tree.Key.onInitialized();}
        }

        ,Key: {
            create: function() {
            }
            ,onInitialized: function() {
            }


            ,NODE_TYPE_PART_DETAIL          : "detail"
            ,NODE_TYPE_PART_PERSON          : "person"
            ,NODE_TYPE_PART_EXPENSES        : "costSummary"



            ,nodeTypeMap: [
                {nodeType: "prevPage"            ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"           ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"                  ,icon: ""                 ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/COSTSHEET"        ,icon: "fa fa-money icon"
                    ,tabIds: ["tabDetail"
                        ,"tabPerson"
                        ,"tabCostSummary"
                    ]}
                ,{nodeType: "p/COSTSHEET/detail"            ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/COSTSHEET/person"            ,icon: "",tabIds: ["tabPerson"]}
                ,{nodeType: "p/COSTSHEET/costSummary"       ,icon: "",tabIds: ["tabCostSummary"]}
            ]
        }
    }

    ,MicroData:{
        create : function() {

        }
        ,onInitialized: function() {

        }

    }

    ,Action:{
        create : function() {

        }
        ,onInitialized: function() {

        }

    }

    ,ParentDetail: {
        create : function() {
            this.cacheParentObject = new Acm.Model.CacheFifo();

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(costsheet) {
            Costsheet.Model.ParentDetail.retrieveParentObject(costsheet);
        }

        ,retrieveParentObject: function(costsheet) {
            if (Costsheet.Model.Detail.validateCostsheet(costsheet)) {
                var parentId = costsheet.parentId;
                var parentType = costsheet.parentType;
                var parentObjData = Costsheet.Model.ParentDetail.cacheParentObject.get(parentId + "." + parentType);
                if (!Costsheet.Model.ParentDetail.validateUnifiedData(parentObjData)) {
                    if (Costsheet.Model.DOC_TYPE_COMPLAINT == parentType) {
                        Costsheet.Service.ParentDetail.retrieveComplaint(parentId,parentType);
                    } else if (Costsheet.Model.DOC_TYPE_CASE_FILE == parentType) {
                        Costsheet.Service.ParentDetail.retrieveCaseFile(parentId,parentType);
                    }
                }
            }
        }

        ,makeUnifiedData:function(parentObj, objType){
            var unifiedData = null;
            if(Costsheet.Model.DOC_TYPE_COMPLAINT == objType){
                if (Costsheet.Model.ParentDetail.validateComplaint(parentObj)) {
                    unifiedData = {};
                    unifiedData.id = Acm.goodValue(parentObj.complaintId);
                    unifiedData.objectType = Acm.goodValue(objType);
                    unifiedData.title = Acm.goodValue(parentObj.complaintTitle);
                    unifiedData.incidentDate = Acm.goodValue(parentObj.created);
                    unifiedData.priority =  Acm.goodValue(parentObj.priority);
                    unifiedData.assignee = Acm.goodValue(parentObj.creator);
                    unifiedData.status = Acm.goodValue(parentObj.status);
                    unifiedData.subjectType = Acm.goodValue(parentObj.complaintType);
                    unifiedData.number = Acm.goodValue(parentObj.complaintNumber);
                }
            }
            else if(Costsheet.Model.DOC_TYPE_CASE_FILE == objType){
                if (Costsheet.Model.ParentDetail.validateCaseFile(parentObj)) {
                    unifiedData = {};
                    unifiedData.id = Acm.goodValue(parentObj.id);
                    unifiedData.objectType = Acm.goodValue(objType);
                    unifiedData.title = Acm.goodValue(parentObj.title);
                    unifiedData.incidentDate = Acm.goodValue(parentObj.created);
                    unifiedData.priority =  Acm.goodValue(parentObj.priority);
                    unifiedData.assignee = Acm.goodValue(parentObj.creator);
                    unifiedData.status = Acm.goodValue(parentObj.status);
                    unifiedData.subjectType = Acm.goodValue(parentObj.caseType);
                    unifiedData.number = Acm.goodValue(parentObj.caseNumber);
                }
            }
            return unifiedData;
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
        ,validateUnifiedData: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.objectType)) {
                return false;
            }
            if (Acm.isEmpty(data.title)) {
                return false;
            }
            if (Acm.isEmpty(data.incidentDate)) {
                return false;
            }
            /*if (Acm.isEmpty(data.priority)) {
                return false;
            }*/
            if (Acm.isEmpty(data.assignee)) {
                return false;
            }
            if (Acm.isEmpty(data.status)) {
                return false;
            }
            /*if (Acm.isEmpty(data.subjectType)) {
                return false;
            }*/
            if (Acm.isEmpty(data.number)) {
                return false;
            }
            return true;
        }
    }

    ,Detail:{
        create : function() {
            Acm.Dispatcher.addEventListener(Costsheet.Controller.VIEW_SAVED_DETAIL          ,this.onViewSavedDetail);
            Acm.Dispatcher.addEventListener(Costsheet.Controller.VIEW_CLOSED_ADD_COSTSHEET_WINDOW       ,this.onViewAddedCostsheet);
            Acm.Dispatcher.addEventListener(Costsheet.Controller.VIEW_CLOSED_EDIT_COSTSHEET_WINDOW     ,this.onViewEdittedCostsheet);
        }
        ,onInitialized: function() {
        }
        ,retrieveObjectList: function(){
            ObjNav.Service.List.retrieveObjectList(ObjNav.Model.Tree.Config.getTreeInfo());
        }
        ,onViewAddedCostsheet: function(costsheet){
            setTimeout(Costsheet.Model.Detail.retrieveObjectList,1000);
        }
        ,onViewEdittedCostsheet: function(costsheet){
            ObjNav.Service.Detail.retrieveObject(Costsheet.Model.DOC_TYPE_COSTSHEET, costsheet.id);
        }
        ,onViewSavedDetail: function(costsheet, details){
            Costsheet.Service.Detail.saveDetail(costsheet,details);
        }
        ,getCacheCostsheet: function(costsheetId) {
            if (0 >= costsheetId) {
                return null;
            }
            return ObjNav.Model.Detail.getCacheObject(Costsheet.Model.DOC_TYPE_COSTSHEET, costsheetId);
        }
        ,putCacheCostsheet: function(costsheetId, costsheet) {
            ObjNav.Model.Detail.putCacheObject(Costsheet.Model.DOC_TYPE_COSTSHEET, costsheetId, costsheet);
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
    }

    ,CostSummary:{
        create : function() {
        }
        ,onInitialized: function() {
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
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.date)) {
                return false;
            }
            return true;
        }
        ,validateCostRecords:function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            return true;
        }
    }

    ,Person:{
        create : function() {

        }
        ,onInitialized: function() {

        }
        ,validatePerson:function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.userId)) {
                return false;
            }
            if (Acm.isEmpty(data.fullName)) {
                return false;
            }
            if (Acm.isEmpty(data.firstName)) {
                return false;
            }
            if (Acm.isEmpty(data.lastName)) {
                return false;
            }
            if (Acm.isEmpty(data.userState)) {
                return false;
            }
            return true;
        }
    }

};

