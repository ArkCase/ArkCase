/**
 * TaskList.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskList.Object = {
    create : function() {

        var ti = this.getTreeInfo();
        /*var tiApp = App.getComplaintTreeInfo();
        if (tiApp) {
            ti.initKey = tiApp.initKey;
            ti.start = tiApp.start;
            ti.n = tiApp.n;
            ti.s = tiApp.s;
            ti.q = tiApp.q;
            ti.complaintId = tiApp.complaintId;
            App.setComplaintTreeInfo(null);
        }*/

        var items = $(document).items();
        var taskId = items.properties("taskId").itemValue();
        if (Acm.isNotEmpty(taskId)) {
            ti.taskId = taskId;
        }
        /*var token = items.properties("token").itemValue();
        this.setToken(token);*/

        this.$noTaskFoundMeassge = $("#noTaskFoundMeassge");
        this.showObject(this.$noTaskFoundMeassge, false);


        this.$btnSignConfirm    = $("#signatureConfirmBtn");
        this.$btnSignature = $("#btnSignature");
        this.$btnReject         = $("button[data-title='Reject']");
        this.$btnSignConfirm.click(function(e) {TaskList.Event.onClickBtnSignConfirm(e);});
        this.$btnReject.click(function(e) {TaskList.Event.onClickBtnReject(e);});

        //parent object information
        this.$lnkParentObjTitle          = $("#parentObjTitle");
        this.$lnkParentNumber = $("#parentObjNumber");
        this.$lnkParentObjIncidentDate = $("#parentObjIncidentDate");
        this.$lnkParentObjPriority       = $("#parentObjPriority");
        this.$lnkParentObjAssigned       = $("#parentObjAssigned");
        this.$lnkParentObjSubjectType  = $("#parentObjSubjectType");
        this.$lnkParentObjStatus         = $("#parentObjStatus");

        // end of parent object information


        //workflow approval buttons
        this.$btnApproveTask = $("#btnApprove");
        this.$btnApproveTask.click(function(e) {TaskList.Event.onClickBtnTaskOutcomeApprove(e);});

        this.$btnSendForRework = $("#btnSendForRework");
        this.$btnSendForRework.click(function(e) {TaskList.Event.onClickBtnTaskOutcomeRework(e);});

        this.$btnResubmit = $("#btnResubmit");
        this.$btnResubmit.click(function(e) {TaskList.Event.onClickBtnTaskOutcomeResubmit(e);});

        this.$btnCancelRequest = $("#btnCancelRequest");
        this.$btnCancelRequest.click(function(e) {TaskList.Event.onClickBtnTaskOutcomeCancelRequest(e);});

        this.$bthAssignTask = $("#btnAssign");
        this.$btnReassignTask = $("#btnReassign");
        this.$btnUnassignTask = $("#btnUnassign");
        this.$btnCompleteTask = $("#btnComplete");
        this.$btnCompleteTask.click(function(e) {TaskList.Event.onClickBtnAdHocTaskComplete(e);});
        
        // Reject Task
        this.$dlgRejectTask = $('#reject');
        this.$btnRejectTask = $("#btnReject");
        this.$btnRejectTask.click(function(e) {TaskList.Event.onClickBtnRejectTask(e);});
        this.$btnSubmitRejectTask = this.$dlgRejectTask.find("button[name=submitRejectTask]");
        this.$btnSearchRejectTask = this.$dlgRejectTask.find("button[name=searchUsersRejectTask]");
        this.$btnSearchRejectTask.click(function(e) {TaskList.Event.onClickSearchRejectTask(e);});
        this.$inputSearchRejectTask = this.$dlgRejectTask.find("input[name=searchKeywordRejectTask]");
        this.$inputSearchRejectTask.keyup(function(e) {TaskList.Event.onKeyUpSearchRejectTask(e);});
        this.$txtCommentRejectTask = this.$dlgRejectTask.find("textarea[id=commentRejectTask]");
        this.$txtCommentRejectTask.change(function(e) {TaskList.Event.onChangeCommentRejectTask(e);});
        this.initDlgRejectTask();
        this.$dlgRejectTaskSortableColumns = this.$dlgRejectTask.find('thead th.th-sortable');
        this.$dlgRejectTaskSortableColumns.each(function(index) {
        	$(this).click(function(e) {TaskList.Event.onClickDlgRejectTaskSortableColumn(e);});
        });

        this.$btnRejectTask = $("#btnReject");
        this.$btnDeleteTask = $("#btnDelete");
        this.$btnDeleteTask.click(function(e) {TaskList.Event.onClickBtnAdHocTaskDelete(e);});

        this.hideAllWorkflowButtons();


        this.$lnkStatus = $("#status");
        this.$lnkStatus.editable('disable');

        this.$lnkTaskSubject = $("#taskSubject");

        this.$lnkTaskSubject.editable({placement: 'bottom'
            ,emptytext: "N/A"
            ,color: "black"

        ,success: function(response, newValue) {
                TaskList.Event.onSaveTitle(newValue);
            }
        });


        this.$perCompleted		= $("#percentageCompleted");
        this.$perCompleted.editable({placement: 'bottom'
            ,emptytext: "N/A"
            ,success: function(response, newValue) {
                TaskList.Event.onSavePerComplete(newValue);
            }
        });
        this.$lnkStartDate      = $("#startDate");
        this.$lnkStartDate.editable({placement: 'bottom'
            ,emptytext: "N/A"
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
            ,emptytext: "N/A"
            ,format: 'mm/dd/yyyy'
            ,viewformat: 'mm/dd/yyyy'
            ,datepicker: {
                weekStart: 1
            }
            ,success: function(response, newValue) {
                TaskList.Event.onSaveDueDate(newValue);
            }
        });

        this.$lnkPriority       = $("#priority");

      /*  this.$lnkOwner          = $("#taskOwner");
        this.$lnkOwner.editable({placement: 'bottom'
            ,emptytext: "N/A"
            ,success: function(response, newValue) {
                TaskList.Event.onSaveOwner(newValue);
            }
        });*/

        this.$divDetails        = $(".taskDetails");
        this.$btnEditDetails    = $("#tabDetails button:eq(0)");
        this.$btnSaveDetails    = $("#tabDetails button:eq(1)");
        this.$btnEditDetails.on("click", function(e) {TaskList.Event.onClickBtnEditDetails(e);});
        this.$btnSaveDetails.on("click", function(e) {TaskList.Event.onClickBtnSaveDetails(e);});


        this.$lnkOwner          = $("#taskOwner");
        this.$lnkOwner.editable({placement: 'bottom'
            ,emptytext: "N/A"
            ,success: function(response, newValue) {
                TaskList.Event.onSaveOwner(newValue);
            }
        });

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

        /*TaskList.Page.createEditCloseComplaintReqButton();
        this.$spanEditCloseComplaintReqBtn = $("#spanEditCloseComplaintReqBtn");
        this.$spanEditCloseComplaintReqBtn  = this.$divDocuments.find(".jtable-toolbar-item-add-record");
        this.$spanEditCloseComplaintReqBtn.unbind("click").on("click", function(e){TaskList.Event.onEditCloseComplaint(e, this);});*/

        this.$divHistory = $("#divHistory");
        TaskList.JTable.createJTableEvents(this.$divHistory);

        this.$divWorkflowOverview = $("#divWorkflowOverview");
        TaskList.JTable.createJTableWorkflowOverview(this.$divWorkflowOverview);

        this.$divReworkInstructions = $(".taskReworkInstructions");
        this.$btnEditReworkInstructions    = $("#tabReworkInstructions button:eq(0)");
        this.$btnSaveReworkInstructions    = $("#tabReworkInstructions button:eq(1)");
        this.$btnEditReworkInstructions.on("click", function(e) {TaskList.Event.onClickBtnEditReworkInstructions(e);});
        this.$btnSaveReworkInstructions.on("click", function(e) {TaskList.Event.onClickBtnSaveReworkInstructions(e);});
        
        this.$divRejectComments = $("#divRejectComments");
        TaskList.JTable.createJTableRejectComments(this.$divRejectComments);

        this.$divAttachments = $("#divAttachments");
        TaskList.JTable.createJTableAttachments(this.$divAttachments);

        //attachments

        this.$btnNewAttachment = $("#newAttachment");
        this.$btnNewAttachment.on("change", function(e) {TaskList.Event.onChangeFileInput(e, this);});

        this.$formAttachment = $("#formFiles");
        this.$formAttachment.submit(function(e) {TaskList.Event.onAddNewAttachment(e, this);});


        //frevvo edit close complaint
        this.$lnkEditComplaintClose = $(".editCloseComplaint");
        
        //frevvo change case status
        this.$lnkChangeCaseStatus = $(".changeCaseStatus");

        var formUrls = new Object();
        formUrls["roi"] = $('#roiFormUrl').val();
        formUrls["close_complaint"] = $('#closeComplaintFormUrl').val();
        formUrls["edit_close_complaint"] = $('#editCloseComplaintFormUrl').val();
        formUrls["change_case_status"] = $('#changeCaseStatusFormUrl').val();
        this.setFormUrls(formUrls);

    }

    //frevvo edit close complaint


    ,_formUrls: null
    ,getFormUrls: function() {
        return this._formUrls;
    }
    ,setFormUrls: function(formUrls) {
        this._formUrls = formUrls;
    }
    
    ,_popupWindow: null
    ,getPopUpWindow: function() {
    	return this._popupWindow;
    }
    ,setPopUpWindow: function(popupWindow) {
    	this._popupWindow = popupWindow;
    }
    
    // Reject Task
    ,setDlgRejectTaskStart: function(start) {
    	this._dlgRejectTaskStart = start;
    }
    ,getDlgRejectTaskStart: function() {
    	return this._dlgRejectTaskStart;
    }
    ,setDlgRejectTaskN: function(n) {
    	this._dlgRejectTaskN = n;
    }
    ,getDlgRejectTaskN: function() {
    	return this._dlgRejectTaskN;
    }
    ,setDlgRejectTaskSortDirection: function(sortDirection) {
    	this._dlgRejectTaskSortDirection = sortDirection;
    }
    ,getDlgRejectTaskSortDirection: function() {
    	return this._dlgRejectTaskSortDirection;
    }
    ,setDlgRejectTaskPage: function(page) {
    	this._dlgRejectTaskPage = page;
    }
    ,getDlgRejectTaskPage: function() {
    	return this._dlgRejectTaskPage;
    }
    ,setDlgRejectTaskPages: function(pages) {
    	this._dlgRejectTaskPages = pages;
    }
    ,getDlgRejectTaskPages: function() {
    	return this._dlgRejectTaskPages;
    }
    ,setDlgRejectTaskSelected: function(selected) {
    	this._dlgRejectTaskSelected = selected;
    }
    ,getDlgRejectTaskSelected: function() {
    	return this._dlgRejectTaskSelected;
    }
    ,setDlgRejectTaskSearchKeyword: function(keyword) {
    	this._dlgRejectTaskSearchKeyword = keyword;
    }
    ,getDlgRejectTaskSearchKeyword: function() {
    	return this._dlgRejectTaskSearchKeyword;
    }
    ,setDlgRejectTaskComment: function(comment) {
    	this._dlgRejectTaskComment = comment;
    }
    ,getDlgRejectTaskComment: function() {
    	return this._dlgRejectTaskComment;
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
    ,_parseKey: function(key) {
        var parts = {pageId: -1, taskId: 0, sub: ""};
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
            var taskId;
            if(arr[1].indexOf("adHoc") != -1){
                var adHocTaskId = arr[1];
                taskId = adHocTaskId.replace("adHoc", "");
                taskId = parseInt(taskId);
            }
            else{
                taskId = parseInt(arr[1]);
            }
            if (! isNaN(taskId)) {
                parts.taskId = taskId;
            }
        }
        if (3 <= arr.length) {
            parts.sub = arr[2];
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
            if(arr[1].indexOf("adHoc") != -1){
                return "taskAdHoc";
            }
            else{
                return "task";
            }
        } else if (3 == arr.length) {
            return "task" + arr[2];
        }
         else if (4 == arr.length) {
            return "taskAdHoc";
        }
        else if (5 == arr.length) {
            return "task" + arr[4];
        }

        return null;
    }
    ,_mapNodeTab: {
        task     : ["tabDetails",
                    "tabDocuments",
                    "tabNotes",
                    "tabHistory",
                    "tabReworkInstructions",
                    "tabWorkflowOverview",
                    "tabAttachments"],

        taskAdHoc     : ["tabDetails",
                        "tabNotes",
                        "tabHistory",
                        "tabRejectComments",
                        "tabWorkflowOverview",
                        "tabAttachments",
                        ],

        taskDetails  : ["tabDetails"],
        taskDocuments: ["tabDocuments"],
        taskNotes    : ["tabNotes"],
        taskHistory  : ["tabHistory"],
        taskReworkInstructions : ["tabReworkInstructions"],
        taskRejectComments : ["tabRejectComments"],
        taskWorkflowOverview : ["tabWorkflowOverview"],
        taskAttachments: ["tabAttachments"]
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
            ,"tabWorkflowOverview"
            ,"tabReworkInstructions"
            ,"tabRejectComments"
            ,"tabAttachments"
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
            var key;
            if(task.adhocTask == true){
                key = "adHoc" + task.taskId;
            }
            else{key = task.taskId;}
            var node = this.$tree.fancytree("getTree").getNodeByKey(this._getTaskKey(key));
            //var node = this.$tree.fancytree("getActiveNode");
            if (node) {
                var nodeTitle = Acm.goodValue(Acm.getDateFromDatetime(task.dueDate) + "," + task.priority + "," + task.title);
                node.setTitle(nodeTitle);
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
        var tasks = TaskList.cachePage.get(start);
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
            var taskSolr = tasks[i];
            var taskBranchID = taskSolr.object_id_s;

            //check if task is associated with an object or not
            var adHoc;
            if(taskSolr.adhocTask_b == true){
                adHoc = true;
            }
            else{
                adHoc = false;
            }
            //&& task.due_dt != null
            //
            var taskBranchTitle;
            if(taskSolr.name != null && taskSolr.priority_s != null && taskSolr.due_dt != null){
                taskBranchTitle = Acm.getDateFromDatetime(taskSolr.due_dt) + "," + taskSolr.priority_s +","+ taskSolr.name;
            }
            else if(taskSolr.name != null && taskSolr.priority_s != null){
                taskBranchTitle = taskSolr.priority_s +","+ taskSolr.name;
            }
            else if(taskSolr.name != null){
                taskBranchTitle = taskSolr.name;
            }
            else{
                taskBranchTitle = "No title";
            }
            if(adHoc == false){
                builder.addBranch({key: pageId + "." + taskBranchID                      //level 1: /Task
                    , title: taskBranchTitle,
                    tooltip: taskSolr.name,
                    expanded: false
                })

                    .addLeaf({key: pageId + "." + taskBranchID + ".Details"                   //level 2: /Task/Details
                        , title: "Details"
                    })
                    .addLeaf({key: pageId + "." + taskBranchID + ".ReworkInstructions"                   //level 2: /Task/Rework Instructions
                        , title: "Rework Instructions"
                    })
                    .addLeaf({key: pageId + "." + taskBranchID + ".Documents"                   //level 2: /Task/Documents
                        , title: "Documents Under Review"
                    })
                    .addLeaf({key: pageId + "." + taskBranchID + ".Attachments"                   //level 2: /Task/Attachments
                        , title: "Attachments"
                    })
                    .addLeaf({key: pageId + "." + taskBranchID + ".Notes"                   //level 2: /Task/Notes
                        , title: "Notes"
                    })
                    .addLeaf({key: pageId + "." + taskBranchID + ".WorkflowOverview"                   //level 2: /Task/Workflow Overview
                        , title: "Workflow Overview"
                    })
                    .addLeafLast({key: pageId + "." + taskBranchID + ".History"                   //level 2: /Task/History
                        , title: "History"
                    })

            }
            else{

                builder.addBranch({key: pageId + "." + "adHoc"+taskBranchID                      //level 1: /Task
                    , title: taskBranchTitle,
                    tooltip: taskSolr.name,
                    expanded: false
                })

                    .addLeaf({key: pageId + "." + taskBranchID + ".Details"                   //level 2: /Task/Details
                        , title: "Details"
                    })
                    .addLeaf({key: pageId + "." + taskBranchID + ".RejectComments"                   //level 2: /Task/Reject Comments
                        , title: "Reject Comments"
                    })
                    .addLeaf({key: pageId + "." + taskBranchID + ".Attachments"                   //level 2: /Task/Attachments
                        , title: "Attachments"
                    })
                    .addLeaf({key: pageId + "." + taskBranchID + ".Notes"                   //level 2: /Task/Notes
                        , title: "Notes"
                    })
                    .addLeaf({key: pageId + "." + taskBranchID + ".WorkflowOverview"                   //level 2: /Task/Workflow Overview
                        , title: "Workflow Overview"
                    })
                    .addLeafLast({key: pageId + "." + taskBranchID + ".History"                   //level 2: /Task/History
                        , title: "History"
                    })
            }
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
    ,setValueTaskOwner : function(owner) {
        if ( owner ) {
            this.$lnkOwner.editable("setValue", owner, false);
        }
        else {
            this.$lnkOwner.editable("setValue", "N/A", false);
        }

    }
    ,setValueLnkPriority: function(txt) {
        if (txt) {
            this.$lnkPriority.editable("setValue", txt);
        }
        else {
            this.$lnkPriority.editable("setValue", "N/A");
        }
    }
    ,setValueLnkStartDate : function(date) {
        if ( date ) {
            this.$lnkStartDate.editable("setValue", date, true);
        }
        else {
            this.$lnkStartDate.editable("setValue", "N/A", true);
        }
    }
    ,setValueLnkDueDate: function(date) {
        if ( date ) {
            this.$lnkDueDate.editable("setValue", date, true);
        }
        else {
            this.$lnkDueDate.editable("setValue", "N/A", true);
        }
    }
    ,setValueAssignedStatus : function(status) {
        if ( status ) {
            this.$lnkStatus.editable("setValue", status);
        }
        else {
            this.$lnkStatus.editable("setValue", "N/A");
        }
    }

    ,setValueDetails : function(details) {
        if ( details ) {
            Acm.Object.setHtml(this.$divDetails, details);
        }
        else {
            Acm.Object.setHtml(this.$divDetails, null);
        }
    }
    ,setValueReworkInstructions : function(reworkInstructions) {
        if ( reworkInstructions ) {
            Acm.Object.setHtml(this.$divReworkInstructions, reworkInstructions);
        }
        else {
            Acm.Object.setHtml(this.$divReworkInstructions, null);
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



    //parent object information setters
    ,setLnkParentObjTitle: function(txt) {
        if (txt) {
            Acm.Object.setText(this.$lnkParentObjTitle, txt);
        }
        else {
            Acm.Object.setText(this.$lnkParentObjTitle, "N/A");
        }
    }
    ,setValueLnkParentObjNumber: function(txt) {
        if (txt) {
            Acm.Object.setText(this.$lnkParentNumber, txt);
        }
        else {
            Acm.Object.setText(this.$lnkParentNumber, "N/A");
        }
    }
    ,setValueLnkParentObjIncidentDate: function(date) {
        if ( date ) {
            Acm.Object.setText(this.$lnkParentObjIncidentDate, date);
        }
        else {
            Acm.Object.setText(this.$lnkParentObjIncidentDate, "N/A");
        }
    }
    ,setLnkParentObjPriority: function(txt) {
        if (txt) {
            Acm.Object.setText(this.$lnkParentObjPriority, txt);
        }
        else {
            Acm.Object.setText(this.$lnkParentObjPriority, "N/A");
        }
    }
    ,setLnkParentObjAssigned: function(txt) {
        if (txt) {
            Acm.Object.setText(this.$lnkParentObjAssigned, txt);
        }
        else {
            Acm.Object.setText(this.$lnkParentObjAssigned, "N/A");
        }
    }
    ,setLnkParentObjSubjectType: function(txt) {
        if(txt){
            Acm.Object.setText(this.$lnkParentObjSubjectType, txt);
        }
        else{
            Acm.Object.setText(this.$lnkParentObjSubjectType, "N/A");
        }
    }
    ,setLnkParentObjStatus: function(txt) {
        if(txt){
            Acm.Object.setText(this.$lnkParentObjStatus, txt);
        }
        else{
            Acm.Object.setText(this.$lnkParentObjStatus, "N/A");
        }
    }

    ,initPriority: function(data) {
        var choices = []; //[{value: "", text: "Choose Priority"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        this.$lnkPriority.editable({placement: 'bottom'
            ,emptytext: "N/A"
            ,value: ""
            ,source: choices
            ,success: function(response, newValue) {
                TaskList.Event.onSavePriority(newValue);
            }
        });
    }
    ,updateDetail: function(task) {
    	if (task && task.attachedToObjectType && task.attachedToObjectType.toLowerCase() == "complaint"){
    		this.$lnkEditComplaintClose.show();
    		this.$lnkChangeCaseStatus.hide();
    	}else if(task && task.attachedToObjectType && task.attachedToObjectType.toLowerCase() == "case_file"){
    		this.$lnkEditComplaintClose.hide();
    		this.$lnkChangeCaseStatus.show();
    	}
        if(task.adhocTask){
            this.hideAllWorkflowButtons();
            if(task.completed != true){
                this.$btnCompleteTask.show();
                this.$btnDeleteTask.show();
            }
            
            if (task.owner != task.assignee) {
            	this.$btnRejectTask.show();
            }
            
            this.setTaskDetails(task);

            this.refreshJTableAttachments();
            this.refreshJTableNotes();
            this.refreshJTableWorkflowOverview();
            this.refreshJTableRejectComments();
            this.refreshJTableHistory();
        }
        else{
            this.hideAllWorkflowButtons();
            if(task.completed != true){
                if(task.availableOutcomes != null){
                    for(var i = 0; i < task.availableOutcomes.length; i++){
                        var availableOutcomes = task.availableOutcomes;
                        switch (availableOutcomes[i].name){
                            case "APPROVE":
                                this.$btnApproveTask.show();
                                break;

                            case "SEND_FOR_REWORK":
                                this.$btnSendForRework.show();
                                break;

                            case "RESUBMIT":
                                this.$btnResubmit.show();
                                break;

                            case "CANCEL_DOCUMENT":
                                this.$btnCancelRequest.show();
                                break;

                            default:
                                break;
                        }
                    }
                }
            }
            TaskList.Object.refreshTaskTreeNode(task);
            this.setTaskDetails(task);
            this.setValueReworkInstructions(task.reworkInstructions);

            this.refreshJTableAttachments();
            this.refreshJTableNotes();
            this.refreshJTableWorkflowOverview();
            this.refreshJTableHistory();
            this.refreshJTableDocuments();
            this.refreshJTableHistory();
        }

    }

    ,updateParentObjDetail: function(parentObj) {
        this.setLnkParentObjTitle(parentObj.title);
        this.setValueLnkParentObjIncidentDate(Acm.getDateFromDatetime(parentObj.incidentDate));
        this.setLnkParentObjPriority(parentObj.priority);
        this.setLnkParentObjAssigned(parentObj.assignee);
        this.setLnkParentObjStatus(parentObj.status);
        this.setLnkParentObjSubjectType(parentObj.subjectType);
        this.setValueLnkParentObjNumber(parentObj.number);
    }

    ,editDivDetails: function() {
        AcmEx.Object.editSummerNote(this.$divDetails);
    }
    ,saveDivDetails: function() {
        return AcmEx.Object.saveSummerNote(this.$divDetails);
    }
    ,editDivReworkInstructions: function() {
        AcmEx.Object.editSummerNote(this.$divReworkInstructions);
    }
    ,saveDivReworkInstructions: function() {
        return AcmEx.Object.saveSummerNote(this.$divReworkInstructions);
    }
    ,refreshJTableNotes: function(){
        AcmEx.Object.jTableLoad(this.$divNotes);

    }
    ,refreshJTableAttachments: function(){
        AcmEx.Object.jTableLoad(this.$divAttachments);

    }
    ,refreshJTableDetails: function(){
        AcmEx.Object.jTableLoad(this.$divDetails);

    }
    ,refreshJTableDocuments: function(){
        AcmEx.Object.jTableLoad(this.$divDocuments);

    }
    ,refreshJTableHistory: function(){
        AcmEx.Object.jTableLoad(this.$divHistory);

    }
    ,refreshJTableInstructions: function(){
        AcmEx.Object.jTableLoad(this.$divReworkInstructions);

    }
    ,refreshJTableRejectComments: function(){
        AcmEx.Object.jTableLoad(this.$divRejectComments);

    }
    ,refreshJTableWorkflowOverview: function(){
        AcmEx.Object.jTableLoad(this.$divWorkflowOverview);

    }

    ,hideAllWorkflowButtons: function(){
        this.$btnApproveTask.hide();
        this.$btnSendForRework.hide();
        this.$btnResubmit.hide();
        this.$btnCancelRequest.hide();
        this.$btnReassignTask.hide();
        this.$btnRejectTask.hide();
        this.$btnUnassignTask.hide();
        this.$btnCompleteTask.hide();
        this.$btnRejectTask.hide();
        this.$btnDeleteTask.hide();
        this.$bthAssignTask.hide();
        this.$btnSignature.hide();
    }
    ,setTaskDetails : function(task){
        this.setValueLnkTaskSubject(task.title);
        this.setValueLnkPerCompleted(task.percentComplete);
        this.setValueLnkStartDate(Acm.getDateFromDatetime(task.taskStartDate));
        this.setValueLnkDueDate(Acm.getDateFromDatetime(task.dueDate));
        this.setValueLnkPriority(task.priority);
        this.setValueTaskOwner(task.owner);
        this.setValueAssignedStatus(task.status);
        this.setValueDetails(task.details);
    }
    
    // Reject Task
    ,initDlgRejectTask: function() {    	
    	TaskList.Page.cleanDlgRejectTaskOwner(this.$dlgRejectTask);
    	TaskList.Page.cleanDlgRejectTaskUsers(this.$dlgRejectTask);
    	
    	this.$inputSearchRejectTask.val('');
    	this.$txtCommentRejectTask.val('');
    	
    	this.setDlgRejectTaskStart(TaskList.DLG_REJECT_TASK_START);
    	this.setDlgRejectTaskN(TaskList.DLG_REJECT_TASK_N);
    	this.setDlgRejectTaskSortDirection(TaskList.DLG_REJECT_TASK_SORT_DIRECTION);
    	this.setDlgRejectTaskPage(0);
    	this.setDlgRejectTaskPages(0);
    	this.setDlgRejectTaskSelected(null);
    	this.setDlgRejectTaskSearchKeyword('');
    	this.setDlgRejectTaskComment('');
    	this.$btnSubmitRejectTask.addClass('disabled');
    }
    ,showDlgRejectTask: function(onClickBtnPrimary) {    	
        Acm.Dialog.bootstrapModal(this.$dlgRejectTask, onClickBtnPrimary);
    }
    ,refreshDlgRejectTaskUsers: function(data) {
    	var tbodyOwner = this.$dlgRejectTask.find('table#ownerTableRejectTask tbody');
    	var tbodyUsers = this.$dlgRejectTask.find('table#usersTableRejectTask tbody');
    	
    	TaskList.Page.cleanDlgRejectTaskOwner(this.$dlgRejectTask);
    	TaskList.Page.cleanDlgRejectTaskUsers(this.$dlgRejectTask);
    	
    	this._refreshDlgRejectTaskOwner(tbodyOwner, data);
    	this._refreshDlgRejectTaskUsers(tbodyUsers, data);
    	this._refreshDlgRejectTaskPaging(data);
    	
    	if (this.getDlgRejectTaskSelected() == null) {
    		this.$btnSubmitRejectTask.addClass('disabled');
    	} else {
    		this.$btnSubmitRejectTask.removeClass('disabled');
    	}
    }
    ,_refreshDlgRejectTaskOwner: function(tbody, data) {
    	if (data && data.response && data.response.owner) {
    		data = data.response.owner;
    	}else {
    		data = null;
    	}
    	if (tbody && data && data.response && data.response.docs && data.response.docs.length > 0) {  
    		TaskList.Page.buildDlgRejectTaskOwner(tbody, data.response.docs);
    	}
    }
    ,_refreshDlgRejectTaskUsers: function(tbody, data) {   
    	if (tbody && data && data.response && data.response.docs && data.response.docs.length > 0) {    		   		
    		TaskList.Page.buildDlgRejectTaskUsers(tbody, data.response.docs);
    	}
    }
    ,_refreshDlgRejectTaskPaging: function(data) {
    	var total = 0;
    	var from = 0;
    	var to = 0;
    	var page = 0;
    	var pages = 0;
    	
    	if (data && data.response && data.response.numFound != -1) {
    		total = data.response.numFound;
    	}
    	
    	if (data && data.response && data.response.start != -1 && total > 0) {
    		from = data.response.start + 1;
    	}
    	
    	if (data && data.response && data.response.start != -1 && data.response.docs) {
    		to = data.response.start + data.response.docs.length;
    	}
    	
    	if (data.response.start != -1) {
    		page = Math.floor(data.response.start/this.getDlgRejectTaskN()) + 1;
    		this.setDlgRejectTaskPage(page);
    	}
    	
    	if (total > 0) {
    		pages = Math.ceil(total/this.getDlgRejectTaskN());
    		this.setDlgRejectTaskPages(pages);
    	}
    	
    	// Build Muted Text
    	var $textMuted = this.$dlgRejectTask.find('footer.panel-footer small.text-muted');
    	TaskList.Page.buildDlgRejectTaskMutedText($textMuted, from, to, total);
    	
    	// Build Pagintion
    	$ulPagination = this.$dlgRejectTask.find('footer.panel-footer ul.pagination');
    	TaskList.Page.buildDlgRejectTaskPagination($ulPagination, page, pages);
    	
    	this.setDlgRejectTaskStart(data.response.start);
    }
};




