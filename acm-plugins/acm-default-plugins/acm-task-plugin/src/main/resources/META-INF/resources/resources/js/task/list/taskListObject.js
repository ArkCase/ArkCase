/**
 * TaskList.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskList.Object = {
    create : function() {

        this.$noTaskFoundMeassge = $("#noTaskFoundMeassge");
        this.showObject(this.$noTaskFoundMeassge, false);
        this.$taskDetailView	 = $("#taskDetailView");

        this.$ulTasks           = $("#ulTasks");
        this.$asideTasks        = this.$ulTasks.closest("aside");

        var items = $(document).items();
        var taskId = items.properties("taskId").itemValue();
        if (Acm.isNotEmpty(taskId)) {
            Task.setTaskId(taskId);
            this.showAsideTasks(false);
            TaskList.setSingleObject(true);
        } else {
            TaskList.setSingleObject(false);
        }

        this.$btnComplete       = $("button[data-title='Complete']");
        this.$btnSignConfirm    = $("#signatureConfirmBtn");
        this.$btnReject         = $("button[data-title='Reject']");
        this.$btnComplete.click(function(e) {TaskList.Event.onClickBtnComplete(e);});
        this.$btnSignConfirm.click(function(e) {TaskList.Event.onClickBtnSignConfirm(e);});
        this.$btnReject.click(function(e) {TaskList.Event.onClickBtnReject(e);});

        this.$lnkTitle          = $("#taskTitle");
        this.$lnkTitle.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                TaskList.Event.onSaveTitle(newValue);
            }
        });

        this.$lnkTaskSubject = $("#taskSubject");
        this.$lnkTaskSubject.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                TaskList.Event.onSaveTitle(newValue);
            }
        });
        this.$h4TitleHeader     = this.$lnkTitle.parent();

        this.$lnkParentNumber = $("#parentNumber");

        this.$perCompleted		= $("#percentageCompleted");
        this.$perCompleted.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                TaskList.Event.onSavePerComplete(newValue);
            }
        });
        this.$lnkStartDate      = $("#startDate");
        this.$lnkStartDate.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,format: 'mm/dd/yyyy'
            ,viewformat: 'mm/dd/yyyy'
            ,datepicker: {
                weekStart: 1
            }
            ,success: function(response, newValue) {
                TaskList.Event.onSaveStartDate(newValue);
            }
        });

        this.$lnkDueDate        = $("#dueDate");
        this.$lnkDueDate.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,format: 'mm/dd/yyyy'
            ,viewformat: 'mm/dd/yyyy'
            ,datepicker: {
                weekStart: 1
            }
            ,success: function(response, newValue) {
                TaskList.Event.onSaveDueDate(newValue);
            }
        });

        this.$lnkIncidentDate        = $("#incident");
        this.$lnkIncidentDate.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,format: 'mm/dd/yyyy'
            ,viewformat: 'mm/dd/yyyy'
            ,datepicker: {
                weekStart: 1
            }
            ,success: function(response, newValue) {
                TaskList.Event.onSaveIncidentDate(newValue);
            }
        });

        this.$lnkPriority       = $("#priority");
        this.$lnkPriority.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                TaskList.Event.onSavePriority(newValue);
            }
        });

        this.$lnkPriority       = $("#priority");
        this.$lnkPriority.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                TaskList.Event.onSavePriority(newValue);
            }
        });

        this.$lnkOwner          = $("#taskOwner");
        this.$lnkOwner.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                TaskList.Event.onSaveOwner(newValue);
            }
        });

        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");

        this.$lnkStatus         = $("#status");
        this.$lnkStatus.editable('disable');


        this.$divDetails        = $(".taskDetails");
        this.$btnEditDetails    = $("#tabDetail button:eq(0)");
        this.$btnSaveDetails    = $("#tabDetail button:eq(1)");
        this.$btnEditDetails.on("click", function(e) {TaskList.Event.onClickBtnEditDetails(e);});
        this.$btnSaveDetails.on("click", function(e) {TaskList.Event.onClickBtnSaveDetails(e);});


        this.$lnkOwner          = $("#taskOwner");
        this.$lnkOwner.editable({placement: 'bottom'
            ,emptytext: "Unknown"
            ,success: function(response, newValue) {
                TaskList.Event.onSaveOwner(newValue);
            }
        });

        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");

        this.$lnkStatus         = $("#status");
        this.$lnkStatus.editable('disable');


        this.$listSignature     = $("#signatureList");

        // forms
        this.$formSignature     = $("#signatureConfirmForm");

        // modals
        this.$modalSignConfirm  = $("#signatureModal");

        //fancy tree
        this.$tree = $("#tree");
        this._useFancyTree(this.$tree);

        this.$divNotes = $("#divNotes");
        TaskList.JTable.createJTableNotes(this.$divNotes);

        this.$divDocuments = $("#divDocuments");
        TaskList.JTable.createJTableDocuments(this.$divDocuments);

        this.$divHistory = $("#divHistory");
        TaskList.JTable.createJTableEvents(this.$divHistory);

    }

    //  Use this to build the Admin tree structure
    //------------------ Tree  ------------------
    //

    ,_treeInfo: {
        start           : 0
        ,n              : 50
        ,total          : -1
        ,s              : null
        ,q              : null
        ,initKey        : null
        ,taskId    : 0
    }
    ,getTreeInfo: function() {
        return this._treeInfo;
    }
    ,getTaskIdByKey: function(key) {
        return this._parseKey(key).taskId;
    }
    ,getPageIdByKey: function(key) {
        return this._parseKey(key).pageId;
    }
    ,getChildIdByKey: function(key) {
        return this._parseKey(key).childId;
    }
    ,_parseKey: function(key) {
        var parts = {pageId: -1, taskId: 0, sub: "", childId: 0};
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
            var taskId = parseInt(arr[1]);
            if (! isNaN(taskId)) {
                parts.taskId = taskId;
            }
        }
        if (3 <= arr.length) {
            parts.sub = arr[2];
        }
        if (4 <= arr.length) {
            var childId = parseInt(arr[3]);
            if (! isNaN(taskId)) {
                parts.childId = childId;
            }
        }
        return parts;
    }

    ,getNodeTypeByKey: function(key) {
        if (Acm.isEmpty(key)) {
            return null;
        }
        var arr = key.split(".");
        if (1 == arr.length) {
            if ("prevPage" == key) {
                return "prevPage";
            } else if ("nextPage" == key) {
                return "nextPage";
            } else { //if ($.isNumeric(arr[0])) {
                return "p";
            }
        } else if (2 == arr.length) {
            return "task";
        } else if (3 == arr.length) {
            return "task" + arr[2];
        }
        return null;
    }
    ,_mapNodeTab: {
        task     : ["tabDetails",
                    "tabDocuments",
                    "tabNotes",
                    "tabHistory"],
        taskdetails  : ["tabDetails"],
        taskdocuments: ["tabDocuments"],
        tasknotes    : ["tabNotes"],
        taskhistory  : ["tabHistory"]
    }
    ,_getTabIdsByKey: function(key) {
        var nodeType = this.getNodeTypeByKey(key);
        var tabIds = ["tabBlank"];
        for (var key in this._mapNodeTab) {
            if (nodeType == key) {
                tabIds = this._mapNodeTab[key];
                break;
            }
        }
        return tabIds;
    }
    ,showTab: function(key) {
        var tabIds = ["tabBlank"
            ,"tabDetails"
            ,"tabDocuments"
            ,"tabNotes"
            ,"tabHistory"
        ];
        var tabIdsToShow = this._getTabIdsByKey(key);
        for (var i = 0; i < tabIds.length; i++) {
            var show = this._foundItemInArray(tabIds[i], tabIdsToShow);
            Acm.Object.show($("#" + tabIds[i]), show);
        }
    }
    ,_foundItemInArray: function(item, arr) {
        for (var i = 0; i < arr.length; i++) {
            if (item == arr[i]) {
                return true;
            }
        }
        return false;
    }

    ,refreshTaskTreeNode: function(task) {
        if (!task) {
            task = TaskList.getTask();
        }
        if (task && task.taskId) {
            var node = this.$tree.fancytree("getTree").getNodeByKey(this._getTaskKey(task.taskId));
            if (node) {
                node.setTitle(Acm.goodValue(task.title));
            }
        }
    }
    ,_getTaskKey: function(taskId) {
        var treeInfo = TaskList.Object.getTreeInfo();
        var start = treeInfo.start;
        var pageId = start.toString();
        return pageId + "." + taskId;
    }
    ,refreshTree: function(key) {
        this.tree.reload().done(function(){
            if (Acm.isNotEmpty(key)) {
                TaskList.Object.tree.activateKey(key);
            }
        });
    }
    ,activeTreeNode: function(key) {
        this.tree.activateKey(key);
    }
    ,expandAllTreeNode: function(key) {
        this.tree.activateKey(key);
    }
    ,_useFancyTree: function($s) {

        $s.fancytree({
            activate: function(event, data){
                var node = data.node;
                TaskList.Event.onActivateTreeNode(node);
            }
            ,renderNode: function(event, data) {
                // Optionally tweak data.node.span
                var node = data.node;
                var key = node.key;
                var acmIcon = null; //node.data.acmIcon;
                var nodeType = TaskList.Object.getNodeTypeByKey(key);
                if ("prevPage" == nodeType) {
                    acmIcon = "<i class='i i-arrow-up'></i>";
                } else if ("nextPage" == nodeType) {
                    acmIcon = "<i class='i i-arrow-down'></i>";
                }
                if (acmIcon) {
                    var span = node.span;
                    var $spanIcon = $(span.children[1]);
                    $spanIcon.removeClass("fancytree-icon");
                    $spanIcon.html(acmIcon);
                }
            }
            ,source: function() {
                return TaskList.Object.treeSource();
            } //end source

        }); //end fancytree

        this.tree = this.$tree.fancytree("getTree");

        $s.contextmenu({
            //delegate: "span.fancytree-title",
            delegate: ".fancytree-title",
            beforeOpen: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                TaskList.Object.$tree.contextmenu("replaceMenu", TaskList.Object._getMenu(node));
                node.setActive();
            },
            select: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                alert("select " + ui.cmd + " on " + node);
            }
        });
    }
    ,treeSource: function() {
        var builder = AcmEx.FancyTreeBuilder.reset();
        var treeInfo = TaskList.Object.getTreeInfo();
        var acmIcon = null;

        var start = treeInfo.start;
        var tasks = TaskList.getTaskList();
        if (null == tasks || 0 >= tasks.length) {
            return builder.getTree();
        }

        if (0 < treeInfo.start) {
            builder.addLeaf({
                key: "prevPage",
                title: treeInfo.start + " records above...",
                tooltip: "Review previous records",
                expanded: false,
                folder: false,
                acmIcon: "<i class='i i-arrow-up'></i>"
            });
        }

        //populate task data
        var pageId = start.toString();
        for (var i = 0; i < tasks.length; i++) {
            var task = tasks[i];
            var taskId = parseInt(task.object_id_s);
            var taskBranchTitle;
            if(task.title_t != null && task.priority_s != null){
                taskBranchTitle = task.title_t + "," + task.priority_s;
            }
            else if((task.title_t != null || task.title_t != "") && (task.priority_s == null || task.priority_s == "")){
                taskBranchTitle = task.title_t;
            }
            else{
                taskBranchTitle = "No title";
            }
            builder.addBranch({key: pageId + "." + taskId                      //level 1: /Task
                , title: taskBranchTitle,
                tooltip: task.name,
                expanded: false
            })

                .addLeaf({key: pageId + "." + taskId + ".details"                   //level 2: /Task/Details
                    , title: "Details"
                })
                .addLeaf({key: pageId + "." + taskId + ".documents"                   //level 2: /Task/Documents
                    , title: "Documents"
                })
                .addLeaf({key: pageId + "." + taskId + ".notes"                   //level 2: /Task/Notes
                    , title: "Notes"
                })
                .addLeafLast({key: pageId + "." + taskId + ".history"                   //level 2: /Task/History
                    , title: "History"
                })
        }

        if ((0 > treeInfo.total)                                    //unknown size
            || (treeInfo.total - treeInfo.n > treeInfo.start)) {   //no more page left
            var title = (0 > treeInfo.total)? "More records..."
                : (treeInfo.total - treeInfo.start - treeInfo.n) + " more records...";
            builder.addLeafLast({key: "nextPage"
                ,title: title
                ,tooltip: "Load more records"
                ,expanded: false
                ,folder: false
                ,acmIcon: "<i class='i i-arrow-down'></i>"
            });
        }
        return builder.getTree();
     //end of tasks
    }
    ,_getMenu: function(node) {
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

    //----------------- end of tree -----------------

    ,showAsideTasks: function(show) {
        Acm.Object.show(this.$asideTasks, show);
    }

    ,showObject : function(obj, show) {
        Acm.Object.show(obj, show);
    }

    ,setSignatureList: function(val) {
        this.$listSignature.empty();
        this.$listSignature.append(val);
    }
    ,getSignatureForm: function() {
        return this.$formSignature;
    }
    ,hideSignatureModal: function() {
        this.$modalSignConfirm.modal('hide');
    }
    ,getHtmlUlTasks: function() {
        return Acm.Object.getHtml(this.$ulTasks);
    }
    ,setHtmlUlTasks: function(val) {
        return Acm.Object.setHtml(this.$ulTasks, val);
    }
    ,registerClickListItemEvents: function() {
        this.$ulTasks.find("a.text-ellipsis").click(function(e) {TaskList.Event.onClickLnkListItem(this);});
        this.$ulTasks.find("a.thumb-sm").click(function(e) {TaskList.Event.onClickLnkListItemImage(this);});
    }
    ,getHiddenTaskId: function(e) {
        var $hidden = $(e).siblings("input[type='hidden']");
        return $hidden.val();
    }
    ,setValueLnkTitle: function(txt) {
        this.$lnkTitle.editable("setValue", txt);
    }

    ,setValueLnkTaskSubject: function(txt) {
        this.$lnkTaskSubject.editable("setValue", txt);
    }

    ,setValueLnkPerCompleted : function(txt) {
        if ( txt ) {
            this.$perCompleted.editable("setValue", txt)
        }
        else {
            this.$perCompleted.editable("setValue", "0")
        }
    }
    ,setValueLnkStartDate : function(date) {
        if ( date ) {
            this.$lnkStartDate.editable("setValue", date, true);
        }
        else {
            this.$lnkStartDate.editable("setValue", "Unknown", true);
        }
    }
    ,setValueLnkDueDate: function(date) {
        if ( date ) {
            this.$lnkDueDate.editable("setValue", date, true);
        }
        else {
            this.$lnkDueDate.editable("setValue", "Unknown", true);
        }
    }
    ,setNumericValueLnkPriority: function(txt) {
        var priorityValue;
        if(txt == "Low"){
            priorityValue = 25;
        }
        else if(txt == "Medium"){
            priorityValue = 50;
        }
        else if (txt == "High"){
            priorityValue = 75;
        }
        else {
            priorityValue = 90;
        }
        this.$lnkPriority.editable("setValue", priorityValue);
    }
    ,setValueLnkPriority: function(txt) {
        this.$lnkPriority.editable("setValue", txt);
    }
    ,setValueLnkAssigned: function(txt) {
        this.$lnkAssigned.editable("setValue", txt);
    }
    ,setValueLnkParentNumber: function(txt) {
        Acm.Object.setText(this.$lnkParentNumber, txt);
    }
    ,initPriority: function(data){
        var choices = []; //[{value: "", text: "Choose Priority"}];
        $.each(data,function(idx,val){
            var opt= {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        this.$lnkPriority.editable({placement: 'bottom', value:"",emptytext: "Unknown",

            source: choices
        })
    }
    ,initAssignee: function(data) {
        var choices = []; //[{value: "", text: "Choose Assignee"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val.userId;
            opt.text = val.fullName;
            choices.push(opt);
        });

        this.$lnkAssigned.editable({placement: 'bottom', value: "",emptytext: "Unknown",
            source: choices
        });
    }
    ,initComplaintType: function(data) {
        var choices = []; //[{value: "", text: "Choose Type"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        this.$lnkComplaintType.editable({placement: 'bottom', value: "",emptytext: "Unknown",
            source: choices
        });
    }

    ,setValueAssignedStatus : function(status) {
        if ( status ) {
            this.$lnkStatus.editable("setValue", "Assigned");
        }
        else {
            this.$lnkStatus.editable("setValue", "Unassigned");
        }

    }

    ,setValueTaskOwner : function(owner) {
        if ( owner ) {
            this.$lnkOwner.editable("setValue", owner, false);
        }
        else {
            this.$lnkOwner.editable("setValue", "Unknown", false);
        }

    }
    ,setValueDetails : function(details) {
        if ( details ) {
            Acm.Object.setHtml(this.$divDetails, details);
        }
        else {
            Acm.Object.setHtml(this.$divDetails, "");
        }
    }
    ,updateDetail: function(t) {
        this.setValueLnkParentNumber(t.attachedToObjectName);
        this.setValueLnkTaskSubject(t.title + "(" + t.attachedToObjectName + ")");
        this.setValueLnkTitle(t.title);
        this.setValueLnkPerCompleted(t.percentComplete);
        this.setValueLnkStartDate(Acm.getDateFromDatetime(t.taskStartDate));
        this.setValueLnkDueDate(Acm.getDateFromDatetime(t.dueDate));
        this.setValueLnkPriority(t.priority);
        this.setValueLnkAssigned(t.assignee);
        this.setValueTaskOwner(t.owner);
        this.setValueAssignedStatus(t.assignee);
        this.setValueDetails(t.details);
    }

    ,editDivDetails: function() {
        AcmEx.Object.editSummerNote(this.$divDetails);
    }
    ,saveDivDetails: function() {
        return AcmEx.Object.saveSummerNote(this.$divDetails);
    }

//============= Old Stuff ==========================
    ,setValueEdtTitle: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtTitle, val);
    }
    ,setValueEdtPriority: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtPriority, val);
    }
    ,setValueEdtDueDate: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtDueDate, val);
    }
    ,setValueEdtAssignee: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtAssignee, val);
    }
    ,setValueEdtTaskId: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtTaskId, val);
    }
    ,setValueEdtBusinessProcessName: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtBusinessProcessName, val);
    }
//    ,setValueEdtAttachedToObjectType: function(val) {
//        return Acm.Object.setPlaceHolderInput(this.$edtAttachedToObjectType, val);
//    }
//    ,setValueEdtAttachedToObjectId: function(val) {
//        return Acm.Object.setPlaceHolderInput(this.$edtAttachedToObjectId, val);
//    }
    ,setTextNodeScanAttachedToObjectType: function(val) {
        Acm.Object.setTextNodeText(this.scanAttachedToObjectType, val);
    }
    ,setTextNodeScanAttachedToObjectId: function(val) {
        Acm.Object.setTextNodeText(this.scanAttachedToObjectId, val);
    }
    ,setHrefLnkAttachedToObject: function(val) {
        this.lnkAttachedToObject.attr("href", val);
    }
//    ,setCheckedChkAdhocTask: function(val) {
//        Acm.Object.setChecked(this.$chkAdhocTask, val);
//    }
    ,setValueEdtAdhocTask: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtAdhocTask, val);
    }
    ,showDivExtra: function(show) {
        Acm.Object.show(this.$divExtra, show);
    }
//=======================================
};




