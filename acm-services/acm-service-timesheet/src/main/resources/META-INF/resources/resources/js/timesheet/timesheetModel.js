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
        if (Timesheet.Model.Person.create)          {Timesheet.Model.Person.create();}
        if (Timesheet.Model.TimeSummary.create)     {Timesheet.Model.TimeSummary.create();}
    }
    ,onInitialized: function() {
        if (Timesheet.Service.onInitialized)              {Timesheet.Service.onInitialized();}
        if (Timesheet.Model.MicroData.onInitialized)      {Timesheet.Model.MicroData.onInitialized();}
        if (Timesheet.Model.Tree.onInitialized)           {Timesheet.Model.Tree.onInitialized();}
        if (Timesheet.Model.Action.onInitialized)         {Timesheet.Model.Action.onInitialized();}
        if (Timesheet.Model.Detail.onInitialized)         {Timesheet.Model.Detail.onInitialized();}
        if (Timesheet.Model.Person.onInitialized)         {Timesheet.Model.Person.onInitialized();}
        if (Timesheet.Model.TimeSummary.onInitialized)    {Timesheet.Model.TimeSummary.onInitialized();}
    }

    ,interface: {
        apiListObjects: function() {
            return "/api/v1/service/timesheet/user/" + App.getUserName();
        }
        ,apiRetrieveObject: function(nodeType, objId) {
            return "/api/v1/service/timesheet/" + objId;
        }
        ,apiSaveObject: function(nodeType, objId) {
            return "/api/v1/service/timesheet";
        }
        ,nodeId: function(objSolr) {
            return objSolr.object_id_s;
        }
        ,nodeType: function(objSolr) {
            return Timesheet.Model.DOC_TYPE_TIMESHEET;
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
            solr.object_type_s = Timesheet.Model.DOC_TYPE_TIMESHEET;
            solr.name = "Timesheet" + " " + Acm.getDateFromDatetime(objData.startDate) + " - " + Acm.getDateFromDatetime(objData.endDate)
            return solr;
        }
        ,validateObjData: function(data) {
            return Timesheet.Model.Detail.validateTimesheet(data);
        }
        ,nodeTypeMap: function() {
            return Timesheet.Model.Tree.Key.nodeTypeMap;
        }
    }

    ,DOC_TYPE_TIMESHEET  : "TIMESHEET"

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
            ,NODE_TYPE_PART_PERSON          : "person"
            ,NODE_TYPE_PART_HOURS_SUMMARY   : "timeSummary"



            ,nodeTypeMap: [
                {nodeType: "prevPage"            ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"           ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"                  ,icon: ""                 ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/TIMESHEET"        ,icon: "i i-alarm icon"
                    ,tabIds: ["tabDetail"
                        ,"tabPerson"
                        ,"tabTimeSummary"
                    ]}
                ,{nodeType: "p/TIMESHEET/detail"            ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/TIMESHEET/person"            ,icon: "",tabIds: ["tabPerson"]}
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
            Acm.Dispatcher.addEventListener(Timesheet.Controller.VIEW_SAVED_DETAIL          ,this.onViewSavedDetail);
            Acm.Dispatcher.addEventListener(Timesheet.Controller.VIEW_CLOSED_ADD_TIMESHEET_WINDOW       ,this.onViewClosedAddTimesheetWindow);
            Acm.Dispatcher.addEventListener(Timesheet.Controller.VIEW_CLOSED_EDIT_TIMESHEET_WINDOW     ,this.onViewClosedEditTimesheetWindow);
        }
        ,onInitialized: function() {
        }
        ,retrieveObjectList: function(){
            ObjNav.Service.List.retrieveObjectList(ObjNav.Model.Tree.Config.getTreeInfo());
        }
        ,onViewClosedAddTimesheetWindow: function(){
            setTimeout(Timesheet.Model.Detail.retrieveObjectList,1000);
        }
        ,onViewClosedEditTimesheetWindow: function(timesheet){
            ObjNav.Service.Detail.retrieveObject(Timesheet.Model.DOC_TYPE_TIMESHEET, timesheet.id);
        }
        ,onViewSavedDetail: function(timesheet, details){
            Timesheet.Service.Detail.saveDetail(timesheet,details);
        }
        ,getCacheTimesheet: function(timesheetId) {
            if (0 >= timesheetId) {
                return null;
            }
            return ObjNav.Model.Detail.getCacheObject(Timesheet.Model.DOC_TYPE_TIMESHEET, timesheetId);
        }
        ,putCacheTimesheet: function(timesheetId, timesheet) {
            ObjNav.Model.Detail.putCacheObject(Timesheet.Model.DOC_TYPE_TIMESHEET, timesheetId, timesheet);
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
            return true;
        }
    }

    ,TimeSummary:{
        create : function() {
        }
        ,onInitialized: function() {
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
        ,validateTimeRecords:function(data){
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

