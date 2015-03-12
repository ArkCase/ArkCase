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
        if (Timesheet.View.People.create)               {Timesheet.View.People.create();}
        if (Timesheet.View.TimeSummary.create)          {Timesheet.View.TimeSummary.create();}
    }
    ,onInitialized: function() {
        if (Timesheet.View.MicroData.onInitialized)      {Timesheet.View.MicroData.onInitialized();}
        if (Timesheet.View.Navigator.onInitialized)      {Timesheet.View.Navigator.onInitialized();}
        if (Timesheet.View.Action.onInitialized)         {Timesheet.View.Action.onInitialized();}
        if (Timesheet.View.Detail.onInitialized)         {Timesheet.View.Detail.onInitialized();}
        if (Timesheet.View.People.onInitialized)         {Timesheet.View.People.onInitialized();}
        if (Timesheet.View.TimeSummary.onInitialized)    {Timesheet.View.TimeSummary.onInitialized();}
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
                Acm.Dialog.openWindow(newTimesheetFormUrl, "", 860, 700, function() {});
            }
        }
        ,onClickBtnEditTimesheetForm:function(event,ctrl){
            var formUrls = Timesheet.View.MicroData.formUrls;
            if(Acm.isNotEmpty(formUrls) && Acm.isNotEmpty(formUrls.editTimesheetFormUrl)){
                var editTimesheetFormUrl = Timesheet.View.MicroData.formUrls.editTimesheetFormUrl;
                editTimesheetFormUrl = editTimesheetFormUrl.replace("_data=(", "_data=(period:'" + Acm.getCurrentDay() + "',");
                editTimesheetFormUrl = editTimesheetFormUrl.replace("embed", "popupform");
                Acm.Dialog.openWindow(editTimesheetFormUrl, "", 860, 700, function() {});
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
        }
        ,onInitialized: function() {

        }
        ,DIRTY_EDITING_DETAIL: "Editing Timesheet detail"
        ,onClickBtnEditDetail: function(event, ctrl) {
            App.Object.Dirty.declare(Timesheet.View.Detail.DIRTY_EDITING_DETAIL);
            Timesheet.View.Detail.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = Timesheet.View.Detail.saveDivDetail();
            App.Object.Dirty.clear(Timesheet.View.Detail.DIRTY_EDITING_DETAIL);
        }
        ,editDivDetail: function() {
            AcmEx.Object.SummerNote.edit(this.$divDetail);
        }
        ,saveDivDetail: function() {
            return AcmEx.Object.SummerNote.save(this.$divDetail);
        }

    }

    ,People: {
        create: function () {
            this.$divPeople = $("#divPeople");
            this.createJTablePerson(this.$divPeople);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT, this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT, this.onViewSelectedObject);
        }
        , onInitialized: function () {
        }

        , onModelRetrievedObject: function (objData) {
            AcmEx.Object.JTable.load(Timesheet.View.People.$divPeople);
        }
        , onViewSelectedObject: function (objType, objId) {
            AcmEx.Object.JTable.load(Timesheet.View.People.$divPeople);
        }

        , _makeJtData: function (people) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Timesheet.Model.People.validatePeople(people)) {
                for (var i = 0; i < people.length; i++) {
                    var Record = {};
                    Record.id = Acm.goodValue(people[i].id);
                    Record.role = Acm.goodValue(people[i].role);
                    Record.username = Acm.goodValue(people[i].username);
                    Record.fullName = Acm.__FixMe__getUserFullName(people[i].username);
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = people.length;
            }
            return jtData;
        }
        , createJTablePerson: function ($jt) {
            var sortMap = {};
            AcmEx.Object.JTable.usePaging($jt
                , {
                    title: 'Person'
                    , multiselect: false
                    , selecting: false
                    , selectingCheckboxes: false
                    , paging: true
                    , sorting: true
                    , pageSize: 10 //Set page size (default: 10)
                    , actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                    }

                    , fields: {
                        id: {
                            title: 'ID'
                            , key: true
                            , list: true
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

        , onModelRetrievedObject: function (objData) {
            AcmEx.Object.JTable.load(Timesheet.View.TimeSummary.$divTimeSummary);
        }
        , onViewSelectedObject: function (objType, objId) {
            AcmEx.Object.JTable.load(Timesheet.View.TimeSummary.$divTimeSummary);
        }
        , _makeJtData: function (timeRecords) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Timesheet.Model.Hours.validateTimeRecords(timeRecords)) {
                for(var i = 0; i < timeRecords.length; i++){
                    var Record = {};
                    Record.id = Acm.goodValue(timeRecords[i].id);
                    Record.parentId = Acm.goodValue(timeRecords[i].parentId);
                    Record.hours = Acm.goodValue(timeRecords[i].hours);
                    Record.chargedDate = Acm.getDateTimeFromDatetime(timeRecords[i].chargedDate);
                    Record.modifiedDate = Acm.getDateTimeFromDatetime(timeRecords[i].modified);
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = timeRecords.length;
            }
            return jtData;
        }
        , createJTableTimeSummary: function ($jt) {
            var sortMap = {};
            AcmEx.Object.JTable.usePaging($jt
                , {
                    title: 'Hours Summary'
                    , multiselect: false
                    , selecting: false
                    , selectingCheckboxes: false
                    , paging: true
                    , sorting: true
                    , pageSize: 10 //Set page size (default: 10)
                    , actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            return AcmEx.Object.JTable.getEmptyRecords();
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

