/**
 * Timesheet.Model
 *
 * @author md
 */
Timesheet.Model = {    
    create : function() {
        if (Timesheet.Service.create)               {Timesheet.Service.create();}
        if (Timesheet.Model.MicroData.create)       {Timesheet.Model.MicroData.create();}
        if (Timesheet.Model.Tree.create)            {Timesheet.Model.Tree.create();}
        if (Timesheet.Model.Action.create)          {Timesheet.Model.Action.create();}
        if (Timesheet.Model.Detail.create)          {Timesheet.Model.Detail.create();}
        if (Timesheet.Model.People.create)          {Timesheet.Model.People.create();}
        if (Timesheet.Model.TimeSummary.create)     {Timesheet.Model.TimeSummary.create();}
    }
    ,onInitialized: function() {
        if (Timesheet.Service.onInitialized)              {Timesheet.Service.onInitialized();}
        if (Timesheet.Model.MicroData.onInitialized)      {Timesheet.Model.MicroData.onInitialized();}
        if (Timesheet.Model.Tree.onInitialized)           {Timesheet.Model.Tree.onInitialized();}
        if (Timesheet.Model.Action.onInitialized)         {Timesheet.Model.Action.onInitialized();}
        if (Timesheet.Model.Detail.onInitialized)         {Timesheet.Model.Detail.onInitialized();}
        if (Timesheet.Model.People.onInitialized)         {Timesheet.Model.People.onInitialized();}
        if (Timesheet.Model.TimeSummary.onInitialized)    {Timesheet.Model.TimeSummary.onInitialized();}
    }

    //use data from case_file for now because
    //all data in interface is required by objNav
    //to make the tree
    //replace this after timesheet data is available
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
            return Timesheet.Model.DOC_TYPE_TIMESHEET;
        }
        ,nodeTitle: function(objSolr) {
            var nodeTitle = "Timesheet " + Acm.getDateFromDatetime(objSolr.create_tdt);
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
            solr.object_type_s = Timesheet.Model.DOC_TYPE_CASE_FILE;
            solr.owner_s = objData.creator;
            solr.status_s = objData.status;
            solr.title_parseable = objData.title;
            return solr;
        }
        ,validateObjData: function(data) {
            //put timesheet validation here
            //once data is available
            return data;
        }
        ,nodeTypeMap: function() {
            return Timesheet.Model.Tree.Key.nodeTypeMap;
        }
    }

    ,DOC_TYPE_TIMESHEET  : "TIMESHEET"
    ,DOC_TYPE_CASE_FILE  : "CASE_FILE"


    ,getTimesheetId : function() {
        return ObjNav.Model.getObjectId();
    }
    ,getTimesheet: function() {
        var objId = ObjNav.Model.getObjectId();
        return ObjNav.Model.Detail.getCacheObject(Timesheet.Model.DOC_TYPE_TIMESHEET, objId);
    }


    ,Tree: {
        create: function() {
            if (Timesheet.Model.Tree.Key.create)        {Timesheet.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (Timesheet.Model.Tree.Key.onInitialized)        {Timesheet.Model.Tree.Key.onInitialized();}
        }

        ,Key: {
            create: function() {
            }
            ,onInitialized: function() {
            }


            ,NODE_TYPE_PART_DETAIL          : "detail"
            ,NODE_TYPE_PART_PERSON          : "people"
            ,NODE_TYPE_PART_HOURS_SUMMARY   : "timeSummary"



            ,nodeTypeMap: [
                {nodeType: "prevPage"            ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"           ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"                  ,icon: ""                 ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/TIMESHEET"        ,icon: "i i-alarm icon"
                    ,tabIds: ["tabDetail"
                        ,"tabPeople"
                        ,"tabTimeSummary"
                    ]}
                ,{nodeType: "p/TIMESHEET/detail"            ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/TIMESHEET/people"            ,icon: "",tabIds: ["tabPeople"]}
                ,{nodeType: "p/TIMESHEET/timeSummary"       ,icon: "",tabIds: ["tabTimeSummary"]}
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

    ,TimeSummary:{
        create : function() {
        }
        ,onInitialized: function() {
        }
        ,validateTimeRecords:function(data){
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

