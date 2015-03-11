/**
 * Costsheet.View
 *
 * @author md
 */
Costsheet.View = {
    create : function() {
        if (Costsheet.View.MicroData.create)            {Costsheet.View.MicroData.create();}
        if (Costsheet.View.Navigator.create)            {Costsheet.View.Navigator.create();}
        if (Costsheet.View.Action.create)               {Costsheet.View.Action.create();}
        if (Costsheet.View.Detail.create)               {Costsheet.View.Detail.create();}
        if (Costsheet.View.People.create)               {Costsheet.View.People.create();}
        if (Costsheet.View.CostSummary.create)          {Costsheet.View.CostSummary.create();}
    }
    ,onInitialized: function() {
        if (Costsheet.View.MicroData.onInitialized)      {Costsheet.View.MicroData.onInitialized();}
        if (Costsheet.View.Navigator.onInitialized)      {Costsheet.View.Navigator.onInitialized();}
        if (Costsheet.View.Action.onInitialized)         {Costsheet.View.Action.onInitialized();}
        if (Costsheet.View.Detail.onInitialized)         {Costsheet.View.Detail.onInitialized();}
        if (Costsheet.View.People.onInitialized)         {Costsheet.View.People.onInitialized();}
        if (Costsheet.View.CostSummary.onInitialized)    {Costsheet.View.CostSummary.onInitialized();}
    }

    ,MicroData:{
        create : function() {
            this.formUrls = {};
            this.formUrls.newCostsheetFormUrl          = Acm.Object.MicroData.get("newCostsheetFormUrl");
            // edit form has same url as new form
            this.formUrls.editCostsheetFormUrl         = Acm.Object.MicroData.get("newCostsheetFormUrl");

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
                    Costsheet.View.Navigator.lazyLoad(event, data);
                }
                ,getContextMenu: function(node) {
                    Costsheet.View.Navigator.getContextMenu(node);
                }
            };
        }
        ,lazyLoad: function(event, data) {
            var key = data.node.key;
            var nodeType = ObjNav.Model.Tree.Key.getNodeTypeByKey(key);
            switch (nodeType) {
                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, Costsheet.Model.DOC_TYPE_COSTSHEET]):
                    data.result = AcmEx.FancyTreeBuilder
                        .reset()
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Costsheet.Model.Tree.Key.NODE_TYPE_PART_DETAIL
                            ,title: "Detail"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Costsheet.Model.Tree.Key.NODE_TYPE_PART_PERSON
                            ,title: "Person"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Costsheet.Model.Tree.Key.NODE_TYPE_PART_EXPENSES
                            ,title: "Expenses"
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
            this.$btnNewCostsheetForm     = $("#btnNewCostsheetForm");
            this.$btnNewCostsheetForm.on("click", function(e) {Costsheet.View.Action.onClickBtnNewCostsheetForm(e, this);});

            this.$btnEditCostsheetForm     = $("#btnEditCostsheetForm");
            this.$btnEditCostsheetForm.on("click", function(e) {Costsheet.View.Action.onClickBtnEditCostsheetForm(e, this);});
        }
        ,onInitialized: function() {
        }
        ,onClickBtnNewCostsheetForm:function(event,ctrl){
            var formUrls = Costsheet.View.MicroData.formUrls;
            if(Acm.isNotEmpty(formUrls) && Acm.isNotEmpty(formUrls.newCostsheetFormUrl)){
                var newCostsheetFormUrl = Costsheet.View.MicroData.formUrls.newCostsheetFormUrl;
                newCostsheetFormUrl = newCostsheetFormUrl.replace("embed", "popupform");
                Acm.Dialog.openWindow(newCostsheetFormUrl, "", 860, 700, function() {});
            }
        }
        ,onClickBtnEditCostsheetForm:function(event,ctrl){
            var formUrls = Costsheet.View.MicroData.formUrls;
            if(Acm.isNotEmpty(formUrls) && Acm.isNotEmpty(formUrls.editCostsheetFormUrl)){
                var editCostsheetFormUrl = Costsheet.View.MicroData.formUrls.editCostsheetFormUrl;
                //temporary ID, need to change later
                var objectId = Costsheet.Model.getCostsheetId();
                editCostsheetFormUrl = editCostsheetFormUrl.replace("_data=(", "_data=(objectId:'" + objectId + "',");
                editCostsheetFormUrl = editCostsheetFormUrl.replace("embed", "popupform");
                Acm.Dialog.openWindow(editCostsheetFormUrl, "", 860, 700, function() {});
            }
        }
    }

    ,Detail:{
        create : function() {
            this.$divDetail       = $(".divDetail");
            this.$btnEditDetail   = $("#tabDetail button:eq(0)");
            this.$btnSaveDetail   = $("#tabDetail button:eq(1)");
            this.$btnEditDetail.on("click", function(e) {Costsheet.View.Detail.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {Costsheet.View.Detail.onClickBtnSaveDetail(e, this);});
        }
        ,onInitialized: function() {

        }
        ,DIRTY_EDITING_DETAIL: "Editing Costsheet detail"
        ,onClickBtnEditDetail: function(event, ctrl) {
            App.Object.Dirty.declare(Costsheet.View.Detail.DIRTY_EDITING_DETAIL);
            Costsheet.View.Detail.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = Costsheet.View.Detail.saveDivDetail();
            App.Object.Dirty.clear(Costsheet.View.Detail.DIRTY_EDITING_DETAIL);
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
            AcmEx.Object.JTable.load(Costsheet.View.People.$divPeople);
        }
        , onViewSelectedObject: function (objType, objId) {
            AcmEx.Object.JTable.load(Costsheet.View.People.$divPeople);
        }

        , _makeJtData: function (people) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Costsheet.Model.People.validatePeople(people)) {
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

    ,CostSummary: {
        create: function () {
            this.$divCostSummary = $("#divCostSummary");
            this.createJTableCostSummary(this.$divCostSummary);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT, this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT, this.onViewSelectedObject);
        }
        , onInitialized: function () {
        }

        , onModelRetrievedObject: function (objData) {
            AcmEx.Object.JTable.load(Costsheet.View.CostSummary.$divCostSummary);
        }
        , onViewSelectedObject: function (objType, objId) {
            AcmEx.Object.JTable.load(Costsheet.View.CostSummary.$divCostSummary);
        }
        , _makeJtData: function (costRecords) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Costsheet.Model.Hours.validateCostRecords(costRecords)) {
                for(var i = 0; i < costRecords.length; i++){
                    var Record = {};
                    Record.id = Acm.goodValue(costRecords[i].id);
                    Record.parentId = Acm.goodValue(costRecords[i].parentId);
                    Record.costs = Acm.goodValue(costRecords[i].costs);
                    Record.description = Acm.goodValue(costRecords[i].description);
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = costRecords.length;
            }
            return jtData;
        }
        , createJTableCostSummary: function ($jt) {
            var sortMap = {};
            AcmEx.Object.JTable.usePaging($jt
                , {
                    title: 'Expenses'
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
                        , description: {
                            title: 'Description'
                            , width: '10%'
                            , sorting: true
                        }
                        , costs: {
                            title: 'Total Cost'
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

