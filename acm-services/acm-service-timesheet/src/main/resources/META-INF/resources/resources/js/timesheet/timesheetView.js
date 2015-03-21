/**
 * Timesheet.View
 *
 * @author md
 */
Timesheet.View = {
    create : function() {
        if (Timesheet.View.MicroData.create)            {Timesheet.View.MicroData.create();}
        if (Timesheet.View.Navigator.create)            {Timesheet.View.Navigator.create();}
        if (Timesheet.View.Action.create)               {Timesheet.View.Action.create();}
        if (Timesheet.View.Detail.create)               {Timesheet.View.Detail.create();}
        if (Timesheet.View.Person.create)               {Timesheet.View.Person.create();}
        if (Timesheet.View.TimeSummary.create)          {Timesheet.View.TimeSummary.create();}
    }
    ,onInitialized: function() {
        if (Timesheet.View.MicroData.onInitialized)      {Timesheet.View.MicroData.onInitialized();}
        if (Timesheet.View.Navigator.onInitialized)      {Timesheet.View.Navigator.onInitialized();}
        if (Timesheet.View.Action.onInitialized)         {Timesheet.View.Action.onInitialized();}
        if (Timesheet.View.Detail.onInitialized)         {Timesheet.View.Detail.onInitialized();}
        if (Timesheet.View.Person.onInitialized)         {Timesheet.View.Person.onInitialized();}
        if (Timesheet.View.TimeSummary.onInitialized)    {Timesheet.View.TimeSummary.onInitialized();}
    }

    ,getActiveTimesheetId: function() {
        return ObjNav.View.Navigator.getActiveObjId();
    }
    ,getActiveTimesheet: function() {
        var objId = ObjNav.View.Navigator.getActiveObjId();
        var timesheet = null;
        if (Acm.isNotEmpty(objId)) {
            timesheet = ObjNav.Model.Detail.getCacheObject(Timesheet.Model.DOC_TYPE_TIMESHEET, objId);
        }
        return timesheet;
    }

    ,MicroData:{
        create : function() {
            this.formUrls = {};
            this.formUrls.newTimesheetFormUrl          = Acm.Object.MicroData.get("newTimesheetFormUrl");
            // edit form has same url as new form
            this.formUrls.editTimesheetFormUrl         = Acm.Object.MicroData.get("newTimesheetFormUrl");

        }
        ,onInitialized: function() {

        }

    }

    ,Navigator:{
        create: function() {
            this.$ulFilter = $("#ulFilter");
            this.$ulSort   = $("#ulSort");
            this.$tree     = $("#tree");
        }
        ,onInitialized: function() {
        }        

        ,getTreeArgs: function() {
            return {
                lazyLoad: function(event, data) {
                    Timesheet.View.Navigator.lazyLoad(event, data);
                }
                ,getContextMenu: function(node) {
                    Timesheet.View.Navigator.getContextMenu(node);
                }
            };
        }
        ,lazyLoad: function(event, data) {
            var key = data.node.key;
            var nodeType = ObjNav.Model.Tree.Key.getNodeTypeByKey(key);
            switch (nodeType) {
                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, Timesheet.Model.DOC_TYPE_TIMESHEET]):
                    data.result = AcmEx.FancyTreeBuilder
                        .reset()
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Timesheet.Model.Tree.Key.NODE_TYPE_PART_DETAIL
                            ,title: "Detail"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Timesheet.Model.Tree.Key.NODE_TYPE_PART_PERSON
                            ,title: "Person"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Timesheet.Model.Tree.Key.NODE_TYPE_PART_HOURS_SUMMARY
                            ,title: "Hours Summary"
                        })
                        .getTree();

                    break;

                default:
                    data.result = [];
                    break;
            }
        }

        ,getContextMenu: function(node) {
            var key = node.key;
            var menu = [
                {title: "Menu:" + key, cmd: "cut", uiIcon: "ui-icon-scissors"},
                {title: "Copy", cmd: "copy", uiIcon: "ui-icon-copy"},
                {title: "Paste", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: false },
                {title: "----"},
                {title: "Edit", cmd: "edit", uiIcon: "ui-icon-pencil", disabled: true },
                {title: "Delete", cmd: "delete", uiIcon: "ui-icon-trash", disabled: true },
                {title: "More", children: [
                    {title: "Sub 1", cmd: "sub1"},
                    {title: "Sub 2", cmd: "sub1"}
                ]}
            ];
            return menu;
        }

    }

    ,Action:{
        create : function() {
            this.$btnNewTimesheetForm     = $("#btnNewTimesheetForm");
            this.$btnNewTimesheetForm.on("click", function(e) {Timesheet.View.Action.onClickBtnNewTimesheetForm(e, this);});

            this.$btnEditTimesheetForm     = $("#btnEditTimesheetForm");
            this.$btnEditTimesheetForm.on("click", function(e) {Timesheet.View.Action.onClickBtnEditTimesheetForm(e, this);});
        }
        ,onInitialized: function() {
        }
        ,onClickBtnNewTimesheetForm:function(event,ctrl){
            var formUrls = Timesheet.View.MicroData.formUrls;
            if(Acm.isNotEmpty(formUrls) && Acm.isNotEmpty(formUrls.newTimesheetFormUrl)){
                var newTimesheetFormUrl = Timesheet.View.MicroData.formUrls.newTimesheetFormUrl;
                newTimesheetFormUrl = newTimesheetFormUrl.replace("embed", "popupform");
                Acm.Dialog.openWindow(newTimesheetFormUrl, "", 860, 700, function() {
                    Timesheet.Controller.viewAddedTimesheet(Timesheet.View.getActiveTimesheet());
                    Timesheet.Controller.viewEdittedTimesheet(Timesheet.View.getActiveTimesheet());
                });
            }
        }
        ,onClickBtnEditTimesheetForm:function(event,ctrl){
            var formUrls = Timesheet.View.MicroData.formUrls;
            if(Acm.isNotEmpty(formUrls) && Acm.isNotEmpty(formUrls.editTimesheetFormUrl)){
                var editTimesheetFormUrl = Timesheet.View.MicroData.formUrls.editTimesheetFormUrl;
                editTimesheetFormUrl = editTimesheetFormUrl.replace("_data=(", "_data=(period:'" + Acm.getCurrentDay() + "',");
                editTimesheetFormUrl = editTimesheetFormUrl.replace("embed", "popupform");
                Acm.Dialog.openWindow(editTimesheetFormUrl, "", 860, 700, function() {
                    Timesheet.Controller.viewEdittedTimesheet(Timesheet.View.getActiveTimesheet());
                });
            }
        }
    }

    ,Detail:{
        create : function() {
            this.$divDetail       = $(".divDetail");
            this.$btnEditDetail   = $("#tabDetail button:eq(0)");
            this.$btnSaveDetail   = $("#tabDetail button:eq(1)");
            this.$btnEditDetail.on("click", function(e) {Timesheet.View.Detail.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {Timesheet.View.Detail.onClickBtnSaveDetail(e, this);});

            this.$timesheetName           = $("#timesheetName");
            this.$timesheetModifiedDate   = $("#timesheetModifiedDate");


            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Timesheet.Controller.MODEL_SAVED_DETAIL            ,this.onModelSavedDetail);
        }
        ,onInitialized: function() {

        }
        ,onModelRetrievedObject: function(timesheet) {
            if(Timesheet.Model.Detail.validateTimesheet(timesheet)){
                if(Acm.isNotEmpty(timesheet.details)){
                    Timesheet.View.Detail.setHtmlDivDetail(timesheet.details);
                }
                if(Acm.isNotEmpty(timesheet.startDate) && Acm.isNotEmpty(timesheet.endDate)){
                    var timesheetName = Timesheet.Model.DOC_TYPE_TIMESHEET + " " + Acm.getDateFromDatetime(timesheet.startDate) + " - " +  Acm.getDateFromDatetime(timesheet.endDate)
                    Timesheet.View.Detail.setTextTimesheetName(timesheetName);
                }
                if(Acm.isNotEmpty(timesheet.modified)){
                    Timesheet.View.Detail.setTextTimesheetModifiedDate("Last Modified " + Acm.getDateFromDatetime(timesheet.modified));
                }
            }
        }
        ,onViewSelectedObject: function(objType, objId) {
            var timesheet = Timesheet.View.getActiveTimesheet();
            if(Timesheet.Model.Detail.validateTimesheet(timesheet)){
                Timesheet.View.Detail.populateDetail(timesheet);
            }
        }
        ,onModelSavedDetail: function(timesheet, details) {
            if (details.hasError) {
                Timesheet.View.Detail.setHtmlDivDetail("(Error)");
            }
        }
        ,populateDetail: function(timesheet){
            Timesheet.View.Detail.resetDetail();
            if(Acm.isNotEmpty(timesheet.details)){
                Timesheet.View.Detail.setHtmlDivDetail(timesheet.details);
            }
        }
        ,resetDetail: function(timesheet) {
            Timesheet.View.Detail.setHtmlDivDetail("");
        }

        ,DIRTY_EDITING_DETAIL: "Editing Timesheet detail"
        ,onClickBtnEditDetail: function(event, ctrl) {
            App.Object.Dirty.declare(Timesheet.View.Detail.DIRTY_EDITING_DETAIL);
            Timesheet.View.Detail.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = Timesheet.View.Detail.saveDivDetail();
            if(Acm.isNotEmpty(htmlDetail)){
                Timesheet.Controller.viewSavedDetail(Timesheet.View.getActiveTimesheet(), htmlDetail);
                App.Object.Dirty.clear(Timesheet.View.Detail.DIRTY_EDITING_DETAIL);
            }
        }
        ,getHtmlDivDetail: function() {
            return AcmEx.Object.SummerNote.get(this.$divDetail);
        }
        ,setHtmlDivDetail: function(html) {
            AcmEx.Object.SummerNote.set(this.$divDetail, html);
        }
        ,editDivDetail: function() {
            AcmEx.Object.SummerNote.edit(this.$divDetail);
        }
        ,saveDivDetail: function() {
            return AcmEx.Object.SummerNote.save(this.$divDetail);
        }
        ,setTextTimesheetName: function(txt) {
            Acm.Object.setText(this.$timesheetName, txt);
        }
        ,setTextTimesheetModifiedDate: function(txt) {
            Acm.Object.setText(this.$timesheetModifiedDate, txt);
        }

    }

    ,Person: {
        create: function () {
            this.$divPerson = $("#divPerson");
            this.createJTablePerson(this.$divPerson);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT            , this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT              , this.onViewSelectedObject);
            /*Acm.Dispatcher.addEventListener(Timesheet.Controller.VIEW_ADDED_TIMESHEET           ,this.onModelAddedTimesheet);
            Acm.Dispatcher.addEventListener(Timesheet.Controller.MODEL_EDITTED_TIMESHEET         ,this.onModelAddedTimesheet);*/
        }
        , onInitialized: function () {
        }

        , onModelRetrievedObject: function (timesheet) {
            AcmEx.Object.JTable.load(Timesheet.View.Person.$divPerson);
        }
        , onViewSelectedObject: function (objType, objId) {
            AcmEx.Object.JTable.load(Timesheet.View.Person.$divPerson);
        }

        , _makeJtData: function (person) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Timesheet.Model.Person.validatePerson(person)) {
                var Record = {};
                Record.role = Acm.goodValue(person.role);
                Record.username = Acm.goodValue(person.userId);
                Record.fullName = Acm.goodValue(person.fullName);
                jtData.Records.push(Record);
            }
            return jtData;
        }
        , createJTablePerson: function ($jt) {
            var sortMap = {};
            AcmEx.Object.JTable.useBasic($jt
                , {
                    title: 'Person'
                    , sorting: true
                    , actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            var timesheetId = parseInt(Timesheet.Model.getTimesheetId());
                            if (0 >= timesheetId) {
                                return rc;
                            }
                            else{
                                var timesheet = Timesheet.Model.getTimesheet();
                                if(Timesheet.Model.Detail.validateTimesheet(timesheet)){
                                    var person = timesheet.user;
                                    rc = Timesheet.View.Person._makeJtData(person);
                                }
                                return rc;
                            }
                        }
                    }
                    , fields: {
                        id: {
                            title: 'ID'
                            , key: true
                            , list: false
                            , create: false
                            , edit: false
                            , sorting: true
                            , width: '5%'
                        }
                        , fullName: {
                            title: 'Full Name'
                            , width: '10%'
                            , sorting: true
                        }
                        , username: {
                            title: 'Username'
                            , width: '10%'
                            , sorting: true
                        }
                        , role: {
                            title: 'Role'
                            , width: '10%'
                            , sorting: true
                            ,list: false
                        }
                    } //end field
                } //end arg
                , sortMap
            );
        }
    }

    ,TimeSummary: {
        create: function () {
            this.$divTimeSummary = $("#divTimeSummary");
            this.createJTableTimeSummary(this.$divTimeSummary);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT, this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT, this.onViewSelectedObject);
        }
        , onInitialized: function () {
        }
        , onModelRetrievedObject: function (timesheet) {
            AcmEx.Object.JTable.load(Timesheet.View.TimeSummary.$divTimeSummary);
        }
        , onViewSelectedObject: function (objType, objId) {
            AcmEx.Object.JTable.load(Timesheet.View.TimeSummary.$divTimeSummary);
        }
        , _makeJtData: function (timeRecords) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Timesheet.Model.TimeSummary.validateTimeRecords(timeRecords)) {
                for(var i = 0; i < timeRecords.length; i++){
                    if (Timesheet.Model.TimeSummary.validateTimeRecord(timeRecords[i])) {
                        var Record = {};
                        Record.id = Acm.goodValue(timeRecords[i].id);
                        Record.parentId = Acm.goodValue(timeRecords[i].objectId);
                        Record.code = Acm.goodValue(timeRecords[i].code);
                        Record.parentType = Acm.goodValue(timeRecords[i].type);
                        Record.hours = Acm.goodValue(timeRecords[i].value);
                        Record.chargedDate = Acm.getDateFromDatetime(timeRecords[i].date);
                        Record.modifiedDate = Acm.getDateFromDatetime(timeRecords[i].modified);
                        jtData.Records.push(Record);
                    }
                }
                //jtData.TotalRecordCount = timeRecords.length;
            }
            return jtData;
        }
        , createJTableTimeSummary: function ($jt) {
            var sortMap = {};
            AcmEx.Object.JTable.useBasic($jt
                , {
                    title: 'Hours Summary'
                    , sorting: true
                    , actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            var timesheetId = Timesheet.View.getActiveTimesheetId();
                            if (0 >= timesheetId) {
                                return rc;
                            }
                            else{
                                var timesheet = Timesheet.View.getActiveTimesheet();
                                if (Timesheet.Model.Detail.validateTimesheet(timesheet)) {
                                    var timeRecords = timesheet.times;
                                    rc = Timesheet.View.TimeSummary._makeJtData(timeRecords);
                                }
                                return rc;
                            }
                        }
                    }

                    , fields: {
                        id: {
                            title: 'ID'
                            , key: true
                            , list: false
                            , create: false
                            , edit: false
                            , sorting: true
                            , width: '5%'
                        }
                        , parentId: {
                            title: 'Parent ID'
                            , width: '10%'
                            , sorting: true
                            ,display: function(data) {
                                var url = App.buildObjectUrl(Acm.goodValue(data.record.parentType), Acm.goodValue(data.record.parentId), "#");
                                var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.code) + "</a>");
                                return $lnk;
                            }
                        }
                        ,parentType: {
                            title: 'Parent Type'
                            , width: '10%'
                            , sorting: true
                            , list : true
                        }
                        ,code: {
                            title: 'Code'
                            , width: '10%'
                            , sorting: true
                            , list : false
                        }
                        , hours: {
                            title: 'Total Hours'
                            , width: '10%'
                            , sorting: true
                        }
                        , chargedDate: {
                            title: 'Date Charged'
                            , width: '10%'
                            , sorting: true
                        }
                        , modifiedDate: {
                            title: 'Modified Date'
                            , width: '10%'
                            , sorting: true
                        }
                    } //end field
                } //end arg
                , sortMap
            );
        }
    }
};

