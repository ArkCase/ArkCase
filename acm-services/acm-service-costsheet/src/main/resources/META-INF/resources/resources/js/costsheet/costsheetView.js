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
        if (Costsheet.View.ParentDetail.create)         {Costsheet.View.ParentDetail.create();}
        if (Costsheet.View.Person.create)               {Costsheet.View.Person.create();}
        if (Costsheet.View.CostSummary.create)          {Costsheet.View.CostSummary.create();}
    }
    ,onInitialized: function() {
        if (Costsheet.View.MicroData.onInitialized)      {Costsheet.View.MicroData.onInitialized();}
        if (Costsheet.View.Navigator.onInitialized)      {Costsheet.View.Navigator.onInitialized();}
        if (Costsheet.View.Action.onInitialized)         {Costsheet.View.Action.onInitialized();}
        if (Costsheet.View.Detail.onInitialized)         {Costsheet.View.Detail.onInitialized();}
        if (Costsheet.View.ParentDetail.onInitialized)   {Costsheet.View.ParentDetail.onInitialized();}
        if (Costsheet.View.Person.onInitialized)         {Costsheet.View.Person.onInitialized();}
        if (Costsheet.View.CostSummary.onInitialized)    {Costsheet.View.CostSummary.onInitialized();}
    }

    ,getActiveCostsheetId: function() {
        return ObjNav.View.Navigator.getActiveObjId();
    }
    ,getActiveCostsheet: function() {
        var objId = ObjNav.View.Navigator.getActiveObjId();
        var costsheet = null;
        if (Acm.isNotEmpty(objId)) {
            costsheet = ObjNav.Model.Detail.getCacheObject(Costsheet.Model.DOC_TYPE_COSTSHEET, objId);
        }
        return costsheet;
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
                Acm.Dialog.openWindow(newCostsheetFormUrl, "", 860, 700, function() {
                    Costsheet.Controller.viewClosedAddCostsheetWindow();
                    if(Costsheet.Model.Detail.validateCostsheet(Costsheet.View.getActiveCostsheet())) {
                        Costsheet.Controller.viewClosedEditCostsheetWindow(Costsheet.View.getActiveCostsheet());
                    }
                });
            }
        }
        ,onClickBtnEditCostsheetForm:function(event,ctrl){
            var formUrls = Costsheet.View.MicroData.formUrls;
            if(Acm.isNotEmpty(formUrls) && Acm.isNotEmpty(formUrls.editCostsheetFormUrl)){
                var editCostsheetFormUrl = Costsheet.View.MicroData.formUrls.editCostsheetFormUrl;
                if(Costsheet.Model.Detail.validateCostsheet(Costsheet.View.getActiveCostsheet())){
                    var objectId = Acm.goodValue(Costsheet.View.getActiveCostsheet().parentId);
                    var objectType = Acm.goodValue(Costsheet.View.getActiveCostsheet().parentType);
                    editCostsheetFormUrl = editCostsheetFormUrl.replace("_data=(", "_data=(objectId:'" + objectId + "',type:'" + objectType + "',");
                    editCostsheetFormUrl = editCostsheetFormUrl.replace("embed", "popupform");
                    Acm.Dialog.openWindow(editCostsheetFormUrl, "", 860, 700, function() {
                        Costsheet.Controller.viewClosedEditCostsheetWindow(Costsheet.View.getActiveCostsheet());
                    });
                }
            }
        }
    }
    ,ParentDetail: {
        create : function() {
            this.$divParentDetail          = $("#divParentDetail");

            this.$lnkParentObjTitle          = $("#parentObjTitle");
            this.$lnkParentObjNumber         = $("#parentObjNumber");
            this.$lnkParentObjIncidentDate   = $("#parentObjIncidentDate");
            this.$lnkParentObjPriority       = $("#parentObjPriority");
            this.$lnkParentObjAssigned       = $("#parentObjAssignee");
            this.$lnkParentObjSubjectType    = $("#parentObjSubjectType");
            this.$lnkParentObjStatus         = $("#parentObjStatus");

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Costsheet.Controller.MODEL_RETRIEVED_PARENT_OBJECT  ,this.onModelRetrievedParentObject);

        }
        ,onInitialized: function() {
        }
        ,onViewSelectedObject: function(objType,objId) {
            var costsheet = Costsheet.View.getActiveCostsheet();
            if(Costsheet.Model.Detail.validateCostsheet(costsheet)) {
                var objId = costsheet.parentId;
                var objType = costsheet.parentType;
                var parentObjData = Costsheet.Model.ParentDetail.cacheParentObject.get(objId + "." + objType);
                if (Costsheet.Model.ParentDetail.validateUnifiedData(parentObjData)) {
                    Costsheet.View.ParentDetail.updateParentDetail(parentObjData);
                }
            }
        }
        ,onModelRetrievedObject: function(costsheet) {
            if(Costsheet.Model.Detail.validateCostsheet(costsheet)){
                var objId = costsheet.parentId;
                var objType = costsheet.parentType;
                var parentObjData = Costsheet.Model.ParentDetail.cacheParentObject.get(objId+"."+objType);
                if(Costsheet.Model.ParentDetail.validateUnifiedData(parentObjData)){
                    Costsheet.View.ParentDetail.updateParentDetail(parentObjData);
                }
            }
        }
        ,onModelRetrievedParentObject: function(parentObjData) {
            Costsheet.View.ParentDetail.updateParentDetail(parentObjData);
        }

        ,updateParentDetail: function(parentObjData) {
            if (Costsheet.Model.ParentDetail.validateUnifiedData(parentObjData)) {
                this.setTextParentObjTitle(parentObjData.title);
                this.setTextLnkParentObjIncidentDate(Acm.getDateFromDatetime(parentObjData.incidentDate));
                this.setTextLnkParentObjPriority(parentObjData.priority);
                this.setTextLnkParentObjAssigned(Acm.__FixMe__getUserFullName(parentObjData.assignee));
                this.setTextLnkParentObjStatus(parentObjData.status);
                this.setTextLnkParentObjSubjectType(parentObjData.subjectType);
                this.setTextLnkParentObjNumber(parentObjData.number);
                this.setParentObjLink(parentObjData.id, parentObjData.objectType);
                this.showDivParentDetail(true);
            }
        }
        ,setParentObjLink: function(parentId, parentType) {
            if (Acm.isNotEmpty(parentId) && Acm.isNotEmpty(parentType)) {
                var url = App.buildObjectUrl(Acm.goodValue(parentType), Acm.goodValue(parentId), "#");
                this.$lnkParentObjTitle.prop("href", url);
                this.$lnkParentObjNumber.prop("href", url);
            }
        }
        
        ,showDivParentDetail: function(show) {
            Acm.Object.show(this.$divParentDetail, show);
        }
        ,setTextParentObjTitle: function(txt) {
            Acm.Object.setText(this.$lnkParentObjTitle, txt);
        }
        ,setTextLnkParentObjIncidentDate: function(txt) {
            Acm.Object.setText(this.$lnkParentObjIncidentDate, txt);
        }
        ,setTextLnkParentObjPriority: function(txt) {
            Acm.Object.setText(this.$lnkParentObjPriority, txt);
        }
        ,setTextLnkParentObjAssigned: function(txt) {
            Acm.Object.setText(this.$lnkParentObjAssigned, txt);
        }
        ,setTextLnkParentObjStatus: function(txt) {
            Acm.Object.setText(this.$lnkParentObjStatus, txt);
        }
        ,setTextLnkParentObjNumber: function(txt) {
            Acm.Object.setText(this.$lnkParentObjNumber, txt);
        }
        ,setTextLnkParentObjSubjectType: function(txt) {
            Acm.Object.setText(this.$lnkParentObjSubjectType, txt);
        }
    }

    ,Detail:{
        create : function() {
            this.$divDetail       = $(".divDetail");
            this.$btnEditDetail   = $("#tabDetail button:eq(0)");
            this.$btnSaveDetail   = $("#tabDetail button:eq(1)");
            this.$btnEditDetail.on("click", function(e) {Costsheet.View.Detail.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {Costsheet.View.Detail.onClickBtnSaveDetail(e, this);});

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT           ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT             ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Costsheet.Controller.MODEL_SAVED_DETAIL            ,this.onModelSavedDetail);
        }
        ,onInitialized: function() {

        }
        ,onViewSelectedObject: function(objType,objId) {
            Costsheet.View.Detail.resetDetail();
            var costsheet = Costsheet.View.getActiveCostsheet();
            if(Costsheet.Model.Detail.validateCostsheet(costsheet)){
                Costsheet.View.Detail.populateDetail(costsheet);
            }
        }
        ,onModelRetrievedObject: function(costsheet) {
            Costsheet.View.Detail.resetDetail();
            if(Costsheet.Model.Detail.validateCostsheet(costsheet)){
                Costsheet.View.Detail.populateDetail(costsheet);
            }
        }
        ,onModelSavedDetail: function(costsheet, details) {
            if (details.hasError) {
                Costsheet.View.Detail.setHtmlDivDetail("(Error)");
            }
        }
        ,populateDetail: function(costsheet){
            if(Acm.isNotEmpty(costsheet.details)){
                Costsheet.View.Detail.setHtmlDivDetail(costsheet.details);
            }
        }
        ,resetDetail: function(costsheet) {
            Costsheet.View.Detail.setHtmlDivDetail("");
        }

        ,DIRTY_EDITING_DETAIL: "Editing Costsheet detail"
        ,onClickBtnEditDetail: function(event, ctrl) {
            App.Object.Dirty.declare(Costsheet.View.Detail.DIRTY_EDITING_DETAIL);
            Costsheet.View.Detail.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = Costsheet.View.Detail.saveDivDetail();
            if(Acm.isNotEmpty(htmlDetail)){
                Costsheet.Controller.viewSavedDetail(Costsheet.View.getActiveCostsheet(), htmlDetail);
                App.Object.Dirty.clear(Costsheet.View.Detail.DIRTY_EDITING_DETAIL);
            }
        }
        ,editDivDetail: function() {
            AcmEx.Object.SummerNote.edit(this.$divDetail);
        }
        ,saveDivDetail: function() {
            return AcmEx.Object.SummerNote.save(this.$divDetail);
        }
        ,getHtmlDivDetail: function() {
            return AcmEx.Object.SummerNote.get(this.$divDetail);
        }
        ,setHtmlDivDetail: function(html) {
            AcmEx.Object.SummerNote.set(this.$divDetail, html);
        }

    }

    ,Person: {
        create: function () {
            this.$divPerson = $("#divPerson");
            this.createJTablePerson(this.$divPerson);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT            , this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT              , this.onViewSelectedObject);
        }
        , onInitialized: function () {
        }

        , onModelRetrievedObject: function (costsheet) {
            AcmEx.Object.JTable.load(Costsheet.View.Person.$divPerson);
        }
        , onViewSelectedObject: function (objType, objId) {
            AcmEx.Object.JTable.load(Costsheet.View.Person.$divPerson);
        }

        , _makeJtData: function (person) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Costsheet.Model.Person.validatePerson(person)) {
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
                            var costsheetId = parseInt(Costsheet.View.getActiveCostsheetId());
                            if (0 >= costsheetId) {
                                return rc;
                            }
                            else{
                                var costsheet = Costsheet.View.getActiveCostsheet();
                                if(Costsheet.Model.Detail.validateCostsheet(costsheet)){
                                    var person = costsheet.user;
                                    rc = Costsheet.View.Person._makeJtData(person);
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
            if (Costsheet.Model.CostSummary.validateCostRecords(costRecords)) {
                for(var i = 0; i < costRecords.length; i++){
                    if (Costsheet.Model.CostSummary.validateCostRecord(costRecords[i])) {
                        var Record = {};
                        Record.id = Acm.goodValue(costRecords[i].id);
                        Record.parentId = Acm.goodValue(Costsheet.View.getActiveCostsheet().parentId);
                        Record.parentNumber = Acm.goodValue(Costsheet.View.getActiveCostsheet().parentNumber);
                        Record.parentType = Acm.goodValue(Costsheet.View.getActiveCostsheet().parentType);
                        Record.cost = Acm.goodValue(costRecords[i].value);
                        Record.title = Acm.goodValue(costRecords[i].title);
                        Record.description = Acm.goodValue(costRecords[i].description);
                        jtData.Records.push(Record);
                    }
                }
                //jtData.TotalRecordCount = costRecords.length;
            }
            return jtData;
        }
        , createJTableCostSummary: function ($jt) {
            var sortMap = {};
            AcmEx.Object.JTable.useBasic($jt
                , {
                    title: 'Hours Summary'
                    , sorting: true
                    , actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.JTable.getEmptyRecords();
                            var costsheetId = Costsheet.View.getActiveCostsheetId();
                            if (0 >= costsheetId) {
                                return rc;
                            }
                            else{
                                var costsheet = Costsheet.View.getActiveCostsheet();
                                if (Costsheet.Model.Detail.validateCostsheet(costsheet)) {
                                    var costRecords = costsheet.costs;
                                    rc = Costsheet.View.CostSummary._makeJtData(costRecords);
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
                                var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.parentNumber) + "</a>");
                                return $lnk;
                            }
                        }
                        ,parentType: {
                            title: 'Parent Type'
                            , width: '10%'
                            , sorting: true
                            , list : true
                        }
                        ,parentNumber: {
                            title: 'Parent Number'
                            , width: '10%'
                            , sorting: true
                            , list : false
                        }
                        , cost: {
                            title: 'Total Cost'
                            , width: '10%'
                            , sorting: true
                        }
                        , title: {
                            title: 'Title'
                            , width: '10%'
                            , sorting: true
                        }
                        , description: {
                            title: 'Description'
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

