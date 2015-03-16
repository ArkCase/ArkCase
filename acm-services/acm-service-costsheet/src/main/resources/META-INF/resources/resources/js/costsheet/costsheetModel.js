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
        if (Costsheet.Model.People.create)          {Costsheet.Model.People.create();}
        if (Costsheet.Model.CostSummary.create)     {Costsheet.Model.CostSummary.create();}
    }
    ,onInitialized: function() {
        if (Costsheet.Service.onInitialized)              {Costsheet.Service.onInitialized();}
        if (Costsheet.Model.MicroData.onInitialized)      {Costsheet.Model.MicroData.onInitialized();}
        if (Costsheet.Model.Tree.onInitialized)           {Costsheet.Model.Tree.onInitialized();}
        if (Costsheet.Model.Action.onInitialized)         {Costsheet.Model.Action.onInitialized();}
        if (Costsheet.Model.Detail.onInitialized)         {Costsheet.Model.Detail.onInitialized();}
        if (Costsheet.Model.People.onInitialized)         {Costsheet.Model.People.onInitialized();}
        if (Costsheet.Model.CostSummary.onInitialized)    {Costsheet.Model.CostSummary.onInitialized();}
    }

    //use data from case_file for now because
    //all data in interface is required by objNav
    //to make the tree
    //replace this after costsheet data is available
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
        }
        ,nodeType: function(objSolr) {
            return Costsheet.Model.DOC_TYPE_COSTSHEET;
        }
        ,nodeTitle: function(objSolr) {
            var nodeTitle = "Costsheet " + Acm.getDateFromDatetime(objSolr.create_tdt);
            return nodeTitle;
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
            solr.object_type_s = Costsheet.Model.DOC_TYPE_CASE_FILE;
            solr.owner_s = objData.creator;
            solr.status_s = objData.status;
            solr.title_parseable = objData.title;
            return solr;
        }
        ,validateObjData: function(data) {
            //put costsheet validation here
            //once data is available
            return data;
        }
        ,nodeTypeMap: function() {
            return Costsheet.Model.Tree.Key.nodeTypeMap;
        }
    }

    ,DOC_TYPE_COSTSHEET  : "COSTSHEET"
    ,DOC_TYPE_CASE_FILE  : "CASE_FILE"


    ,getCostsheetId : function() {
        return ObjNav.Model.getObjectId();
    }
    ,getCostsheet: function() {
        var objId = ObjNav.Model.getObjectId();
        return ObjNav.Model.Detail.getCacheObject(Costsheet.Model.DOC_TYPE_COSTSHEET, objId);
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
            ,NODE_TYPE_PART_PERSON          : "people"
            ,NODE_TYPE_PART_EXPENSES        : "costSummary"



            ,nodeTypeMap: [
                {nodeType: "prevPage"            ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"           ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"                  ,icon: ""                 ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/COSTSHEET"        ,icon: "fa fa-money icon"
                    ,tabIds: ["tabDetail"
                        ,"tabPeople"
                        ,"tabCostSummary"
                    ]}
                ,{nodeType: "p/COSTSHEET/detail"            ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/COSTSHEET/people"            ,icon: "",tabIds: ["tabPeople"]}
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

    ,Detail:{
        create : function() {
        }
        ,onInitialized: function() {
        }
    }

    ,CostSummary:{
        create : function() {
        }
        ,onInitialized: function() {
        }
        ,validateCostRecords:function(data){
            //put validations here
            return true;
        }
    }

    ,People:{
        create : function() {

        }
        ,onInitialized: function() {

        }
        ,validatePeople:function(data){
            //put validations here
            return true;
        }
    }

};

