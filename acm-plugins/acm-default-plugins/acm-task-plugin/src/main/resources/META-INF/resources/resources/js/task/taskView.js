/**
 * Task.View
 *
 * @author jwu
 */
Task.View = Task.View || {
    create : function() {
        if (Task.View.MicroData.create)                             {Task.View.MicroData.create();}
        if (Task.View.Navigator.create)                             {Task.View.Navigator.create();}
        if (Task.View.Content.create)                               {Task.View.Content.create();}
        if (Task.View.DocumentUnderReview.create)                   {Task.View.DocumentUnderReview.create();}
        if (Task.View.Action.create)                                {Task.View.Action.create();}
        if (Task.View.Detail.create)                                {Task.View.Detail.create();}
        if (Task.View.RejectTask.create)                            {Task.View.RejectTask.create();}
        if (Task.View.ParentDetail.create)                          {Task.View.ParentDetail.create();}
        if (Task.View.Notes.create)                                 {Task.View.Notes.create();}
        if (Task.View.History.create)                               {Task.View.History.create();}
        if (Task.View.WorkflowOverview.create)                      {Task.View.WorkflowOverview.create();}
        if (Task.View.Attachments.create)                           {Task.View.Attachments.create();}
        if (Task.View.RejectComments.create)                        {Task.View.RejectComments.create();}

    }
    ,onInitialized: function() {
        if (Task.View.MicroData.onInitialized)                      {Task.View.MicroData.onInitialized();}
        if (Task.View.Navigator.onInitialized)                      {Task.View.Navigator.onInitialized();}
        if (Task.View.Content.onInitialized)                        {Task.View.Content.onInitialized();}
        if (Task.View.DocumentUnderReview.onInitialized)            {Task.View.DocumentUnderReview.onInitialized();}
        if (Task.View.Action.onInitialized)                         {Task.View.Action.onInitialized();}
        if (Task.View.Detail.onInitialized)                         {Task.View.Detail.onInitialized();}
        if (Task.View.RejectTask.onInitialized)                     {Task.View.RejectTask.onInitialized();}
        if (Task.View.ParentDetail.onInitialized)                   {Task.View.ParentDetail.onInitialized();}
        if (Task.View.Notes.onInitialized)                          {Task.View.Notes.onInitialized();}
        if (Task.View.History.onInitialized)                        {Task.View.History.onInitialized();}
        if (Task.View.WorkflowOverview.onInitialized)               {Task.View.WorkflowOverview.onInitialized();}
        if (Task.View.Attachments.onInitialized)                    {Task.View.Attachments.onInitialized();}
        if (Task.View.RejectComments.onInitialized)                 {Task.View.RejectComments.onInitialized();}
    }

    ,getActiveTask: function() {
        var nodeId = ObjNav.View.Navigator.getActiveObjId();
        var nodeType = ObjNav.View.Navigator.getActiveObjType();
        var task = null;
        if (Acm.isNotEmpty(nodeId)) {
            task = ObjNav.Model.Detail.getCacheObject(nodeType, nodeId);
        }
        return task;
    }

    ,MicroData: {
        create : function() {
            this.treeFilter = Acm.Object.MicroData.getJson("treeFilter");
            this.treeSort   = Acm.Object.MicroData.getJson("treeSort");

            this.formUrls = {};
            //this.formUrls.closeComplaintFormUrl          = Acm.Object.MicroData.get("closeComplaintFormUrl");
            this.formUrls.editCloseComplaintFormUrl      = Acm.Object.MicroData.get("editCloseComplaintFormUrl");
            this.formUrls.roiFormUrl                     = Acm.Object.MicroData.get("roiFormUrl");
            this.formUrls.changeCaseStatusFormUrl        = Acm.Object.MicroData.get("changeCaseStatusFormUrl");
        }
        ,onInitialized: function() {
        }

    }

    ,Navigator: {
        create: function() {
            this.$ulFilter = $("#ulFilter");
            this.$ulSort   = $("#ulSort");
            this.$tree     = $("#tree");

            Acm.Dispatcher.addEventListener(Task.Controller.VIEW_CHANGED_TITLE       , this.onViewChangedTaskTitle);
        }
        ,onInitialized: function() {
        }

        ,onViewChangedTaskTitle: function(nodeType, nodeId, title) {
            var taskSolr = ObjNav.Model.List.getSolrObject(nodeType, nodeId);
            if (ObjNav.Model.List.validateObjSolr(taskSolr)) {
                taskSolr.name = Acm.goodValue(title);
                ObjNav.View.Navigator.updateObjNode(nodeType, nodeId);
            }
        }

        ,getTreeArgs: function() {
            return {
                lazyLoad: function(event, data) {
                    Task.View.Navigator.lazyLoad(event, data);
                }
                ,getContextMenu: function(node) {
                    Task.View.Navigator.getContextMenu(node);
                }
            };
        }
        ,lazyLoad: function(event, data) {
            var key = data.node.key;
            var nodeType = ObjNav.Model.Tree.Key.getNodeTypeByKey(key);
            switch (nodeType) {
                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, Task.Model.DOC_TYPE_TASK]):
                    data.result = AcmEx.FancyTreeBuilder
                        .reset()
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_DETAILS
                            ,title: "Task Details"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_REWORK
                            ,title: "Rework Details"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_DOCUMENTS
                            ,title: "Documents Under Review"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_ATTACHMENTS
                            ,title: "Attachments"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_NOTES
                            ,title: "Notes"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_WORKFLOW
                            ,title: "Workflow Overview"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_HISTORY
                            ,title: "History"
                        })
                        .getTree();

                    break;

                case ObjNav.Model.Tree.Key.makeNodeType([ObjNav.Model.Tree.Key.NODE_TYPE_PART_PAGE, Task.Model.DOC_TYPE_ADHOC_TASK]):
                    data.result = AcmEx.FancyTreeBuilder
                        .reset()
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_DETAILS
                            ,title: "Details"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_REJECT
                            ,title: "Reject Comments"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_ATTACHMENTS
                            ,title: "Attachments"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_NOTES
                            ,title: "Notes"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_WORKFLOW
                            ,title: "Workflow Overview"
                        })
                        .addLeaf({key: key + ObjNav.Model.Tree.Key.KEY_SEPARATOR + Task.Model.Tree.Key.NODE_TYPE_PART_HISTORY
                            ,title: "History"
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


    ,Content: {
        create : function() {
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT_ERROR    ,this.onModelRetrievedObjectError);
        }
        ,onInitialized: function() {
        }
        ,onModelRetrievedObjectError: function(error) {
            Acm.Dialog.error(Acm.goodValue(error.errMsg));
        }
    }

    ,ParentDetail: {
        create : function() {
            this.$divParentDetail          = $("#divParentDetail");

            this.$lnkParentObjTitle          = $("#parentObjTitle");
            this.$lnkParentObjNumber         = $("#parentObjNumber");
            this.$lnkParentObjIncidentDate   = $("#parentObjIncidentDate");
            this.$lnkParentObjPriority       = $("#parentObjPriority");
            this.$lnkParentObjAssigned       = $("#parentObjAssigned");
            this.$lnkParentObjSubjectType    = $("#parentObjSubjectType");
            this.$lnkParentObjStatus         = $("#parentObjStatus");

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT            ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT              ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_RETRIEVED_PARENT_OBJECT       ,this.onModelRetrievedParentObjectData);

        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            var task = Task.Model.findTask(nodeType, nodeId);
            Task.View.ParentDetail.updateParentDetail(task);
        }
        ,onModelRetrievedObject: function(objData) {
            Task.View.ParentDetail.updateParentDetail(objData);
        }
        ,onModelRetrievedParentObjectData: function(parentObjData) {
            var task = Task.View.getActiveTask();
            Task.View.ParentDetail.updateParentDetail(task);
        }

        ,updateParentDetail: function(task) {
            this.showDivParentDetail(false);
            if (Task.Model.Detail.validateTask(task)) {
                if(Acm.isNotEmpty(task.attachedToObjectId) && Acm.isNotEmpty(task.attachedToObjectType)){
                    var parentObjData = Task.Model.ParentDetail.cacheParentObject.get(task.attachedToObjectId);
                    if (Task.Model.ParentDetail.validateUnifiedData(parentObjData)) {
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
                else{
                    this.setTextParentObjTitle("");
                    this.setTextLnkParentObjIncidentDate("");
                    this.setTextLnkParentObjPriority("");
                    this.setTextLnkParentObjAssigned("");
                    this.setTextLnkParentObjStatus("");
                    this.setTextLnkParentObjSubjectType("");
                    this.setTextLnkParentObjNumber("");
                    this.setParentObjLink("");
                }
            }


        }

        ,setParentObjLink: function(objectId, objectType) {
            if (Acm.isNotEmpty(objectId) && Acm.isNotEmpty(objectType)) {
                var url = "#";
                if(Task.Model.DOC_TYPE_COMPLAINT == objectType){
                    url = App.getContextPath() + "/plugin/complaint/" + objectId;
                } else if(Task.Model.DOC_TYPE_CASE_FILE == objectType){
                    url = App.getContextPath() + "/plugin/casefile/" + objectId;
                }
                this.$lnkParentObjTitle.prop("href", url);
                this.$lnkParentObjNumber.prop("href", url);
            }
        }

        //parent obj details setters

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


    ,Action: {
        create: function() {


            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT         ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedObject: function(objData) {
            Task.View.Action.populate(objData);
        }
        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            Task.View.Action.populate(objData);
            SubscriptionOp.Model.checkSubscription(App.getUserName(), objType, objId);
        }

        ,populate: function(task) {
            if (Task.Model.Detail.validateTask(task)) {

            }
        }
    }

    ,Detail: {
        create : function() {
            this.$divDetail       = $(".divDetail");
            this.$btnEditDetail   = $("#tabDetails button:eq(0)");
            this.$btnSaveDetail   = $("#tabDetails button:eq(1)");
            this.$btnEditDetail.on("click", function(e) {Task.View.Detail.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {Task.View.Detail.onClickBtnSaveDetail(e, this);});

            this.$divReworkDetails = $(".taskReworkInstructions");
            this.$btnEditReworkDetails    = $("#tabReworkInstructions button:eq(0)");
            this.$btnSaveReworkDetails    = $("#tabReworkInstructions button:eq(1)");
            this.$btnEditReworkDetails.on("click", function(e) {Task.View.Detail.onClickBtnEditReworkDetails(e,this);});
            this.$btnSaveReworkDetails.on("click", function(e) {Task.View.Detail.onClickBtnSaveReworkDetails(e,this);});

//            //frevvo edit close complaint
//            this.$lnkEditComplaintClose = $(".editCloseComplaint");
//
//            //frevvo change case status
//            this.$lnkChangeCaseStatus = $(".changeCaseStatus");
//
//            var formUrls = new Object();
//            formUrls["roi"] = $('#roiFormUrl').val();
//            formUrls["close_complaint"] = $('#closeComplaintFormUrl').val();
//            formUrls["edit_close_complaint"] = $('#editCloseComplaintFormUrl').val();
//            formUrls["change_case_status"] = $('#changeCaseStatusFormUrl').val();
//            this.setFormUrls(formUrls);

            //task action buttons
            this.$btnCompleteTask = $("#btnComplete");
            this.$btnCompleteTask.on("click", function(e) {Task.View.Detail.onClickBtnCompleteTask(e, this);});
            this.$btnDeleteTask = $("#btnDelete");
            this.$btnDeleteTask.on("click", function(e) {Task.View.Detail.onClickBtnDeleteTask(e, this);});


            //workflow approval buttons
            this.$btnGroup = $("div.btn-group-task");
            this.$btnGroup.on("click", ".businessProcess", function(e) {Task.View.Detail.onClickBtnOutcome(e, this);});


            //task details
            this.$lnkStatus = $("#status");
            this.$lnkStatus.editable('disable');

            this.$lnkOwner          = $("#taskOwner");

            this.$lnkTaskSubject = $("#taskSubject");
            AcmEx.Object.XEditable.useEditable(this.$lnkTaskSubject, {
                success: function(response, newValue) {
                    Task.Controller.viewChangedTitle(ObjNav.View.Navigator.getActiveObjType(),ObjNav.Model.getObjectId(), newValue);
                }
            });

            this.$perCompleted		= $("#percentageCompleted");
            AcmEx.Object.XEditable.useEditable(this.$perCompleted, {
                success: function(response, newValue) {
                    Task.Controller.viewChangedPercentCompleted(ObjNav.View.Navigator.getActiveObjType(),ObjNav.Model.getObjectId(), newValue);
                }
            });

            this.$lnkStartDate      = $("#startDate");
            AcmEx.Object.XEditable.useEditableDate(this.$lnkStartDate, {
                success: function(response, newValue) {
                    Task.Controller.viewChangedStartDate(ObjNav.View.Navigator.getActiveObjType(),ObjNav.Model.getObjectId(), newValue);
                }
            });

            this.$lnkDueDate        = $("#dueDate");
            AcmEx.Object.XEditable.useEditableDate(this.$lnkDueDate, {
                success: function(response, newValue) {
                    Task.Controller.viewChangedDueDate(ObjNav.View.Navigator.getActiveObjType(),ObjNav.Model.getObjectId(), newValue);
                }
            });

            this.$lnkPriority       = $("#priority");

            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_RETRIEVED_ASSIGNEES          ,this.onModelRetrievedAssignees);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_RETRIEVED_PRIORITIES         ,this.onModelRetrievedPriorities);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT          ,this.onModelRetrievedObject);
            //Acm.Dispatcher.addEventListener(Task.Controller.MODEL_RETRIEVED_PARENT_OBJECT     ,this.onModelRetrievedParentObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT            ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_SAVED_DETAIL                ,this.onModelSavedDetail);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_SAVED_REWORK_DETAILS        ,this.onModelSavedReworkDetails);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_SAVED_TITLE                 ,this.onModelSavedTitle);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_SAVED_PERCENT_COMPLETED     ,this.onModelSavedPercentComplete);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_SAVED_ASSIGNEE              ,this.onModelSavedAssignee);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_SAVED_PRIORITY              ,this.onModelSavedPriority);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_SAVED_START_DATE            ,this.onModelSavedStartDate);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_SAVED_DUE_DATE              ,this.onModelSavedDueDate);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_COMPLETED_TASK              ,this.onModelCompletedTask);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_DELETED_NOTE                ,this.onModelDeletedTask);
            //Acm.Dispatcher.addEventListener(Task.Controller.MODEL_RETRIEVED_USERS             ,this.onModelRetrievedUsers);


        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedAssignees: function(assignees) {
            var choices = [];
            $.each(assignees, function(idx, val) {
                var opt = {};
                /*opt.value = val.userId;
                opt.text = val.fullName;*/
                opt.value = val.object_id_s;
                opt.text = val.name;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(Task.View.Detail.$lnkOwner, {
                source: choices
                ,success: function(response, newValue) {
                    Task.Controller.viewChangedAssignee(ObjNav.View.Navigator.getActiveObjType(),ObjNav.Model.getObjectId(), newValue);
                }
            });
        }

        ,onModelRetrievedPriorities: function(priorities) {
            var choices = []; //[{value: "", text: "Choose Priority"}];
            $.each(priorities, function(idx, val) {
                var opt = {};
                opt.value = val;
                opt.text = val;
                choices.push(opt);
            });

            AcmEx.Object.XEditable.useEditable(Task.View.Detail.$lnkPriority, {
                source: choices
                ,success: function(response, newValue) {
                    Task.Controller.viewChangedPriority(ObjNav.View.Navigator.getActiveObjType(),ObjNav.Model.getObjectId(), newValue);
                }
            });
        }

//        ,_formUrls: null
//        ,getFormUrls: function() {
//            return this._formUrls;
//        }
//        ,setFormUrls: function(formUrls) {
//            this._formUrls = formUrls;
//        }

        ,_popupWindow: null
        ,getPopUpWindow: function() {
            return this._popupWindow;
        }
        ,setPopUpWindow: function(popupWindow) {
            this._popupWindow = popupWindow;
        }


        //buttons actions
        ,hideAllWorkflowButtons: function(){
            Task.View.Detail.$btnCompleteTask.hide();
            Task.View.Detail.$btnDeleteTask.hide();
            Task.View.RejectTask.$btnRejectTask.hide();
        }

        ,hideDynamicWorkflowButtons: function(){
            var $businessProcessButtons = $(".businessProcess");
            $businessProcessButtons.remove();
        }

        ,populateButtons: function(task) {
//moved to doc under review
//            if (task && task.attachedToObjectType && task.attachedToObjectType.toLowerCase() == "complaint"){
//                Task.View.Detail.$lnkEditComplaintClose.show();
//                Task.View.Detail.$lnkChangeCaseStatus.hide();
//            }else if(task && task.attachedToObjectType && task.attachedToObjectType.toLowerCase() == "case_file"){
//                Task.View.Detail.$lnkEditComplaintClose.hide();
//                Task.View.Detail.$lnkChangeCaseStatus.show();
//            }
            if(task.adhocTask){
                Task.View.Detail.hideAllWorkflowButtons();
                Task.View.Detail.hideDynamicWorkflowButtons();
                if(task.completed != true){
                    Task.View.Detail.$btnCompleteTask.show();
                    Task.View.Detail.$btnDeleteTask.show();
                }

                if (Acm.isNotEmpty(task.owner) && Acm.isNotEmpty(task.assignee)) {
                    if((task.owner != task.assignee)){
                        Task.View.RejectTask.$btnRejectTask.show();
                    }
                }
            }
            else if(!task.adhocTask){
                Task.View.Detail.hideAllWorkflowButtons();
                Task.View.Detail.hideDynamicWorkflowButtons();
                if(task.completed != true){
                    if(task.availableOutcomes != null){
                        for(var i = 0; i < task.availableOutcomes.length; i++){
                            var availableOutcomes = task.availableOutcomes;
                            var availableOutcomeName = availableOutcomes[i].description;

                            var html =  "<button class='btn btn-default btn-sm businessProcess' id='" + availableOutcomes[i].name +
                                "' data-toggle='modal' title='" +availableOutcomes[i].description +
                                "'>" + availableOutcomes[i].description +"</button>";
                            Task.View.Detail.$btnGroup.append(html).append(" ");
                            Task.View.Detail.$btnFromAvailableOutcomes = $("#" + availableOutcomes[i].name);
                            Task.View.Detail.$btnFromAvailableOutcomes.show();
                        }
                    }
                }
                Task.View.Detail.setHtmlDivReworkDetails(task.reworkInstructions);
            }

        }

        ,onClickBtnOutcome : function(event,ctrl) {
            var clicked = event.target.id;
            if (clicked == "SEND_FOR_REWORK") {
                var reworkInstructions = AcmEx.Object.SummerNote.get(Task.View.Detail.$divReworkInstructions);
                if (reworkInstructions == null || reworkInstructions == "") {
                    Acm.Dialog.info("Must enter rework details");
                }
                else {
                    var task = Task.View.getActiveTask();
                    task.reworkInstructions = reworkInstructions;
                    Task.View.Detail.onClickBtnTaskWithOutcome(clicked);
                }
            }
            else
            {
                Task.View.Detail.onClickBtnTaskWithOutcome(clicked);
            }
            //$('.businessProcess').each(function(){$(this).hide();})
            //alert(clicked);
            //$("#" + clicked).hide();
        }

        ,onClickBtnTaskWithOutcome : function(outcome) {
            var task = Task.View.getActiveTask();
            var availableOutcome = {};
            for(var i = 0; i < task.availableOutcomes.length; i++){
                availableOutcome = task.availableOutcomes[i];
                if(availableOutcome.name == outcome) {
                    task.taskOutcome = availableOutcome;
                    Task.Controller.viewCompletedTask(task)
                }
            }
        }

        ,onClickBtnDeleteTask:function(event,ctrl){
            var taskId = ObjNav.Model.getObjectId();
            if(Acm.isNotEmpty(taskId) && taskId > 0){
                Task.Controller.viewDeletedTask(taskId);
            }
        }

        ,onClickBtnCompleteTask:function(event,ctrl){
            Task.Controller.viewCompletedTask();
        }

        ,onModelCompletedTask: function(task) {
            if (task.hasError) {
                Acm.Dialog.info(task.errorMsg);
            }
            else{
                Task.View.Detail.hideAllWorkflowButtons();
                Task.View.Detail.hideDynamicWorkflowButtons();
            }
        }

        ,onModelDeletedTask: function(task) {
            if (task.hasError) {
                Acm.Dialog.info(task.errorMsg);
            }
            else{
                Task.View.Detail.hideAllWorkflowButtons();
                Task.View.Detail.hideDynamicWorkflowButtons();
            }
        }


        ,onModelSavedTitle: function(nodeType, taskId,title) {
            if (title.hasError) {
                //Acm.Dialog.info(title.errorMsg);

                Task.View.Detail.setTextLnkTaskTitle("(Error)");
            }
        }
        ,onModelSavedStartDate: function(nodeType, taskId,startDate) {
            if (startDate.hasError) {
                //Acm.Dialog.info(startDate.errorMsg);

                Task.View.Detail.setTextLnkStartDate("(Error)");
            }
        }
        ,onModelSavedAssignee: function(nodeType, taskId, assginee) {
            if (assginee.hasError) {
                //Acm.Dialog.info(assginee.errorMsg);

                Task.View.Detail.setTextLnkAssignee("(Error)");
            }
        }
        ,onModelSavedPercentComplete: function(nodeType, taskId, percentComplete) {
            if (percentComplete.hasError) {
                //Acm.Dialog.info(percentComplete.errorMsg);

                Task.View.Detail.setTextLnkPercentComplete("(Error)");
            }
        }
        ,onModelSavedPriority: function(nodeType, taskId,priority) {
            if (priority.hasError) {
                //Acm.Dialog.info(priority.errorMsg);

                Task.View.Detail.setTextLnkPriority("(Error)");
            }
        }
        ,onModelSavedDueDate: function(nodeType, taskId,dueDate) {
            if (dueDate.hasError) {
                //Acm.Dialog.info(dueDate.errorMsg);

                Task.View.Detail.setTextLnkDueDate("(Error)");
            }
        }

        ,onModelRetrievedObject: function(objData) {
            if (Task.Model.Detail.validateTask(objData)) {
                Task.View.Detail.populateTaskDetails(objData);
            }
        }
//        ,onModelRetrievedParentObject: function(parentObjData) {
//            if (Task.Model.interface.validateParentObjData(parentObjData)) {
//                Task.View.Detail.populateParentObjDetail(parentObjData);
//            }
//        }
        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            if (Task.Model.Detail.validateTask(objData)) {
                Task.View.Detail.populateTaskDetails(objData);
            }
        }
        ,onModelSavedDetail: function(nodeType, taskId, details){
            if (details.hasError) {
                Task.View.Detail.setHtmlDivDetail("(Error)");
            }
        }
        ,onModelSavedReworkDetails: function(nodeType, taskId, reworkDetails){
            if (reworkDetails.hasError) {
                Task.View.Detail.setHtmlDivReworkDetails("(Error)");
            }
        }

        ,populateTaskDetails : function(task){
            this.setTextLnkTaskTitle(Acm.goodValue(task.title));
            this.setTextLnkPercentComplete(Acm.goodValue(task.percentComplete, 0));
            this.setTextLnkStartDate(Acm.getDateFromDatetime(task.taskStartDate));
            this.setTextLnkDueDate(Acm.getDateFromDatetime(task.dueDate));
            this.setTextLnkPriority(Acm.goodValue(task.priority));
            //this.setTextLnkTaskOwner(Acm.__FixMe__getUserFullName(task.assignee));
            this.setTextLnkTaskOwner(Acm.goodValue(task.assignee));
            this.setTextLnkStatus(Acm.goodValue(task.status));
            this.setHtmlDivDetail(Acm.goodValue(task.details));
            Task.View.Detail.populateButtons(task);
            //Task.Controller.viewPopulatedTaskDetails(task);
        }

        //task details setters

        ,setTextLnkTaskTitle: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkTaskSubject, txt);
        }
        ,setTextLnkStartDate: function(txt) {
            AcmEx.Object.XEditable.setDate(this.$lnkStartDate, txt);
        }
        ,setTextLnkPercentComplete: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$perCompleted, txt);
        }
        ,setTextLnkAssignee: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkAssignee, txt);
        }
        ,setTextLnkPriority: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkPriority, txt);
        }
        ,setTextLnkDueDate: function(txt) {
            AcmEx.Object.XEditable.setDate(this.$lnkDueDate, txt);
        }
        ,setTextLnkStatus: function(txt) {
            Acm.Object.setText(this.$lnkStatus, txt);
        }
        ,setTextLnkTaskOwner: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkOwner, txt);
        }


        //summernote

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
        ,DIRTY_EDITING_DETAIL: "Editing task detail"
        ,onClickBtnEditDetail: function(event, ctrl) {
            App.Object.Dirty.declare(Task.View.Detail.DIRTY_EDITING_DETAIL);
            Task.View.Detail.editDivDetail();
        }
        ,onClickBtnSaveDetail: function(event, ctrl) {
            var htmlDetail = Task.View.Detail.saveDivDetail();
            Task.Controller.viewChangedDetail(ObjNav.View.Navigator.getActiveObjType(), ObjNav.View.Navigator.getActiveObjId(), htmlDetail);
            App.Object.Dirty.clear(Task.View.Detail.DIRTY_EDITING_DETAIL);
        }

        ,getHtmlDivReworkDetails: function() {
            return AcmEx.Object.SummerNote.get(this.$divReworkDetails);
        }
        ,setHtmlDivReworkDetails: function(html) {
            AcmEx.Object.SummerNote.set(this.$divReworkDetails, html);
        }
        ,editDivReworkDetails: function() {
            AcmEx.Object.SummerNote.edit(this.$divReworkDetails);
        }
        ,saveDivReworkDetails: function() {
            return AcmEx.Object.SummerNote.save(this.$divReworkDetails);
        }
        ,DIRTY_EDITING_REWORK_DETAIL: "Editing rework detail"
        ,onClickBtnEditReworkDetails: function(event, ctrl) {
            App.Object.Dirty.declare(Task.View.Detail.DIRTY_EDITING_REWORK_DETAIL);
            Task.View.Detail.editDivReworkDetails();
        }
        ,onClickBtnSaveReworkDetails: function(event, ctrl) {
            var htmlDetail = Task.View.Detail.saveDivReworkDetails();
            Task.Controller.viewChangedReworkDetails(ObjNav.View.Navigator.getActiveObjType(), ObjNav.View.Navigator.getActiveObjId(), htmlDetail);
            App.Object.Dirty.clear(Task.View.Detail.DIRTY_EDITING_REWORK_DETAIL);
        }
    }

    ,RejectTask: {
        create: function() {
            this.$dlgRejectTask = $('#reject');
            this.$btnRejectTask = $("#btnReject");
            this.$btnRejectTask.on("click",function(e) {Task.View.RejectTask.onClickBtnRejectTask(e,this);});
            this.$btnSubmitRejectTask = this.$dlgRejectTask.find("button[name=submitRejectTask]");
            this.$btnSearchRejectTask = this.$dlgRejectTask.find("button[name=searchUsersRejectTask]");
            this.$btnSearchRejectTask.on("click",function(e) {Task.View.RejectTask.onClickSearchRejectTask(e,this);});
            this.$inputSearchRejectTask = this.$dlgRejectTask.find("input[name=searchKeywordRejectTask]");
            this.$inputSearchRejectTask.keyup(function(e) {Task.View.RejectTask.onKeyUpSearchRejectTask(e,this);});
            this.$txtCommentRejectTask = this.$dlgRejectTask.find("textarea[id=commentRejectTask]");
            this.$txtCommentRejectTask.change(function(e) {Task.View.RejectTask.onChangeCommentRejectTask(e,this);});
            this.initDlgRejectTask();
            this.$dlgRejectTaskSortableColumns = this.$dlgRejectTask.find('thead th.th-sortable');
            this.$dlgRejectTaskSortableColumns.each(function(index) {
                $(this).on("click",function(e) {Task.View.RejectTask.onClickDlgRejectTaskSortableColumn(e,this);});
            });

           /* Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT         ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);*/
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_RETRIEVED_USERS             ,this.onModelRetrievedUsers);


        }
        ,onInitialized: function() {
        }

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
        ,buildDlgRejectTaskOwner: function(element, results) {
            if (element) {
                for (var i = 0; i < results.length; i++) {
                    var result = results[i];
                    var checked = '';

                    var selected = Task.View.RejectTask.getDlgRejectTaskSelected();
                    if (selected == null) {
                        var task = Task.View.getActiveTask();

                        if (Acm.isNotEmpty(task) && task.owner) {
                            selected = task.owner;
                        }
                    }

                    if (selected && result.object_id_s == selected) {
                        checked = 'checked="checked"';
                        Task.View.RejectTask.setDlgRejectTaskSelected(selected);
                    }

                    var tr = '<tr>' +
                        '<td><label class="checkbox m-n"><input type="radio" value="' + result.object_id_s + '" id="returnToUser" name="returnToUser" ' + checked + ' /><i></i></label></td>' +
                        '<td>' + result.first_name_lcs + '</td>' +
                        '<td>' + result.last_name_lcs + '</td>' +
                        '<td>' + result.object_id_s + '</td>' +
                        '<td>' + '' + '</td>' +
                        '</tr>';

                    element.append(tr);
                }

                $('input[name=returnToUser]:radio').change(function(e) {Task.View.RejectTask.onChangeDlgRejectTaskSelected(e,this);});
            }
        }
        ,buildDlgRejectTaskUsers: function(element, results) {
            if (element) {
                for (var i = 0; i < results.length; i++) {
                    var result = results[i];
                    var checked = '';

                    var selected = Task.View.RejectTask.getDlgRejectTaskSelected();

                    if (selected && result.object_id_s == selected) {
                        checked = 'checked="checked"';
                        Task.View.RejectTask.setDlgRejectTaskSelected(selected);
                    }

                    var tr = '<tr>' +
                        '<td><label class="checkbox m-n"><input type="radio" value="' + result.object_id_s + '" id="returnToUser" name="returnToUser" ' + checked + ' /><i></i></label></td>' +
                        '<td>' + result.first_name_lcs + '</td>' +
                        '<td>' + result.last_name_lcs + '</td>' +
                        '<td>' + result.object_id_s + '</td>' +
                        '<td>' + '' + '</td>' +
                        '</tr>';

                    element.append(tr);
                }

                $('input[name=returnToUser]:radio').change(function(e) {Task.View.RejectTask.onChangeDlgRejectTaskSelected(e,this);});
            }
        }
        ,buildDlgRejectTaskMutedText: function(element, from, to, total) {
            if (element) {
                element.empty();
                element.append('Showing ' + from + '-' + to +' of ' + total + ' items');
            }
        }
        ,buildDlgRejectTaskPagination: function(element, page, pages) {
            if (element) {
                element.empty();

                // Left pagination button
                var $leftBtnHtml = $($.parseHTML('<li><a href="#"><i class="fa fa-chevron-left"></i></a></li>'));
                if (page == 1) {
                    $leftBtnHtml.addClass('disabled');
                } else {
                    $leftBtnHtml.on("click",function(e) {Task.View.RejectTask.onClickDlgRejectTaskLeftBtn(e,this);});
                }
                element.append($leftBtnHtml);

                // Page button
                if (page != -1) {
                    for (var i = 0; i < pages; i++) {
                        var $page = $($.parseHTML('<li><a href="#">' + (i+1) + '</a></li>'));
                        var active = '';

                        if (i == (page - 1)) {
                            $page.addClass('active');
                        } else {
                            $page.on("click",function(e) {Task.View.RejectTask.onClickDlgRejectTaskPageBtn(e,this);});
                        }

                        element.append($page);
                    }
                }

                // Right pagination button
                var $rightBtnHtml = $($.parseHTML('<li><a href="#"><i class="fa fa-chevron-right"></i></a></li>'));
                if (page == pages || pages == 0) {
                    $rightBtnHtml.addClass('disabled');
                } else {
                    $rightBtnHtml.on("click",function(e) {Task.View.RejectTask.onClickDlgRejectTaskRightBtn(e,this);});
                }
                element.append($rightBtnHtml);
            }
        }
        ,cleanDlgRejectTaskOwner: function(element) {
            var $tbody = element.find('table#ownerTableRejectTask tbody');

            if ($tbody) {
                $tbody.empty();
            }
        }
        ,cleanDlgRejectTaskUsers: function(element) {
            var $tbody = element.find('table#usersTableRejectTask tbody');
            var $textMuted = element.find('footer.panel-footer small.text-muted');
            var $ulPagination = element.find('footer.panel-footer ul.pagination');

            if ($tbody) {
                $tbody.empty();
            }

            if ($textMuted) {
                $textMuted.empty();
            }

            if ($ulPagination) {
                $ulPagination.empty();
            }
        }
        ,initDlgRejectTask: function() {
            Task.View.RejectTask.cleanDlgRejectTaskOwner(this.$dlgRejectTask);
            Task.View.RejectTask.cleanDlgRejectTaskUsers(this.$dlgRejectTask);

            this.$inputSearchRejectTask.val('');
            this.$txtCommentRejectTask.val('');

            this.setDlgRejectTaskStart(Task.DLG_REJECT_TASK_START);
            this.setDlgRejectTaskN(Task.DLG_REJECT_TASK_N);
            this.setDlgRejectTaskSortDirection(Task.DLG_REJECT_TASK_SORT_DIRECTION);
            this.setDlgRejectTaskPage(0);
            this.setDlgRejectTaskPages(0);
            this.setDlgRejectTaskSelected(null);
            this.setDlgRejectTaskSearchKeyword('');
            this.setDlgRejectTaskComment('');
            this.$btnSubmitRejectTask.addClass('disabled');
        }
        ,showDlgRejectTask: function(onClickBtnPrimary) {
            Acm.Dialog.modal(this.$dlgRejectTask, onClickBtnPrimary);
        }
        ,refreshDlgRejectTaskUsers: function(data) {
            var tbodyOwner = this.$dlgRejectTask.find('table#ownerTableRejectTask tbody');
            var tbodyUsers = this.$dlgRejectTask.find('table#usersTableRejectTask tbody');

            Task.View.RejectTask.cleanDlgRejectTaskOwner(this.$dlgRejectTask);
            Task.View.RejectTask.cleanDlgRejectTaskUsers(this.$dlgRejectTask);

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
            if(Acm.Validator.validateSolrData(data)){
                var users = data.response.docs;
                var task = Task.View.getActiveTask();
                if(Task.Model.Detail.validateTask(task)){
                    for(var i = 0; i < users.length; i++){
                        if (task.owner == users[i].object_id_s){
                            data = users[i];
                        }
                    }
                }
            }
            else {
                data = null;
            }
            /*if (data && data.response && data.response.docs) {
             var task = Task.View.getActiveTask();
             var users = data.response.docs;

             data = data.response.owner;

             && data.response && data.response.docs && data.response.docs.length > 0
             }*/
            if (tbody && data) {
                Task.View.RejectTask.buildDlgRejectTaskOwner(tbody, data);
            }
        }
        ,_refreshDlgRejectTaskUsers: function(tbody, data) {
            if (tbody && data && data.response && data.response.docs && data.response.docs.length > 0) {
                Task.View.RejectTask.buildDlgRejectTaskUsers(tbody, data.response.docs);
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
            Task.View.RejectTask.buildDlgRejectTaskMutedText($textMuted, from, to, total);

            // Build Pagintion
            $ulPagination = this.$dlgRejectTask.find('footer.panel-footer ul.pagination');
            Task.View.RejectTask.buildDlgRejectTaskPagination($ulPagination, page, pages);

            this.setDlgRejectTaskStart(data.response.start);
        }
        // Reject Task Events
        ,onModelRetrievedUsers: function(users){
            Task.View.RejectTask.refreshDlgRejectTaskUsers(users);
        }
        ,_onRetrieveUsers: function() {
            var task = Task.View.getActiveTask();
            var start = Task.View.RejectTask.getDlgRejectTaskStart();
            var n = Task.View.RejectTask.getDlgRejectTaskN();
            var sortDirection = Task.View.RejectTask.getDlgRejectTaskSortDirection();
            var searchKeyword = Task.View.RejectTask.getDlgRejectTaskSearchKeyword();
            var exclude = task.owner;
            //todo
            Task.Controller.viewRetrievedUsers(start, n, sortDirection, searchKeyword, exclude);
        }
        ,onClickBtnRejectTask: function(event,ctrl) {
            Task.View.RejectTask.initDlgRejectTask();
            this._onRetrieveUsers();
            Task.View.RejectTask.showDlgRejectTask(function(event, ctrl) {
                var returnTo = Task.View.RejectTask.getDlgRejectTaskSelected();
                var note = Task.View.RejectTask.getDlgRejectTaskComment();
                if (returnTo != null) {
                    Task.View.RejectTask.onSaveAssignee(returnTo);
                    if (note && note.trim() !== '') {
                        var noteToSave = {};
                        noteToSave.note = note;
                        noteToSave.type = Task.REJECT_COMMENT;
                        noteToSave.parentId = Acm.goodValue(ObjNav.Model.getObjectId());
                        noteToSave.parentType = Acm.goodValue(ObjNav.Model.getObjectType());
                        Task.Controller.viewAddedNote(noteToSave);
                    }
                }
            });
        }
        ,onClickDlgRejectTaskSortableColumn: function(event,ctrl) {
            var sortDirection = Task.View.RejectTask.getDlgRejectTaskSortDirection();

            if (sortDirection && sortDirection == 'ASC') {
                Task.View.RejectTask.setDlgRejectTaskSortDirection('DESC');
            } else {
                Task.View.RejectTask.setDlgRejectTaskSortDirection('ASC');
            }

            this._onRetrieveUsers();
        }
        ,onClickDlgRejectTaskLeftBtn: function(event,ctrl) {
            var start = Task.View.RejectTask.getDlgRejectTaskStart();
            var page = Task.View.RejectTask.getDlgRejectTaskPage();

            if (page > 0) {
                Task.View.RejectTask.setDlgRejectTaskStart(start - Task.View.RejectTask.getDlgRejectTaskN());
                Task.View.RejectTask.setDlgRejectTaskPage(page - 1);
            }

            this._onRetrieveUsers();
        }
        ,onClickDlgRejectTaskRightBtn: function(event,ctrl) {
            var start = Task.View.RejectTask.getDlgRejectTaskStart();
            var page = Task.View.RejectTask.getDlgRejectTaskPage();
            var pages = Task.View.RejectTask.getDlgRejectTaskPages();

            if (page < pages) {
                Task.View.RejectTask.setDlgRejectTaskStart(start + Task.View.RejectTask.getDlgRejectTaskN());
                Task.View.RejectTask.setDlgRejectTaskPage(page + 1);
            }

            this._onRetrieveUsers();
        }
        ,onClickDlgRejectTaskPageBtn: function(event,ctrl) {
            var $page = $(event.target);
            var page = $page.html();

            if (page) {
                Task.View.RejectTask.setDlgRejectTaskStart((page - 1) * Task.View.RejectTask.getDlgRejectTaskN());
                Task.View.RejectTask.setDlgRejectTaskPage(page);
            }

            this._onRetrieveUsers();
        }
        ,onClickSearchRejectTask: function(event,ctrl) {
            var keyword = Task.View.RejectTask.$inputSearchRejectTask.val();
            Task.View.RejectTask.setDlgRejectTaskSearchKeyword(keyword);

            Task.View.RejectTask.setDlgRejectTaskStart(Task.DLG_REJECT_TASK_START);
            Task.View.RejectTask.setDlgRejectTaskN(Task.DLG_REJECT_TASK_N);
            Task.View.RejectTask.setDlgRejectTaskSortDirection(Task.DLG_REJECT_TASK_SORT_DIRECTION);
            Task.View.RejectTask.setDlgRejectTaskPage(0);
            Task.View.RejectTask.setDlgRejectTaskPages(0);

            this._onRetrieveUsers();
        }
        ,onKeyUpSearchRejectTask: function(event,ctrl) {
            if (event.keyCode == 13) {
                var keyword = Task.View.RejectTask.$inputSearchRejectTask.val();
                Task.View.RejectTask.setDlgRejectTaskSearchKeyword(keyword);

                Task.View.RejectTask.setDlgRejectTaskStart(Task.DLG_REJECT_TASK_START);
                Task.View.RejectTask.setDlgRejectTaskN(Task.DLG_REJECT_TASK_N);
                Task.View.RejectTask.setDlgRejectTaskSortDirection(Task.DLG_REJECT_TASK_SORT_DIRECTION);
                Task.View.RejectTask.setDlgRejectTaskPage(0);
                Task.View.RejectTask.setDlgRejectTaskPages(0);

                this._onRetrieveUsers();
            }
        }
        ,onChangeCommentRejectTask: function(event,ctrl) {
            var comment = Task.View.RejectTask.$txtCommentRejectTask.val();
            if (comment && comment.trim() !== '') {
                Task.View.RejectTask.setDlgRejectTaskComment(comment);
            }
        }
        ,onChangeDlgRejectTaskSelected: function(event,ctrl) {
            Task.View.RejectTask.setDlgRejectTaskSelected($(event.target).val());
        }
        // end of Reject ------------------------------------------

}

    ,Notes: {
        create: function() {
            this.$divNotes = $("#divNotes");
            this.createJTableNotes(this.$divNotes);

            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_ADDED_NOTE        ,this.onModelAddedNote);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_UPDATED_NOTE      ,this.onModelUpdatedNote);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_DELETED_NOTE      ,this.onModelDeletedNote);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(nodeType, nodeId) {
            AcmEx.Object.JTable.load(Task.View.Notes.$divNotes);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Task.View.Notes.$divNotes);
        }
        ,onModelAddedNote: function(note) {
            if (note.hasError) {
                Acm.Dialog.info(note.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Task.View.Notes.$divNotes);
            }
        }
        ,onModelUpdatedNote: function(note) {
            if (note.hasError) {
                Acm.Dialog.info(note.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Task.View.Notes.$divNotes);
            }
        }
        ,onModelDeletedNote: function(deletedNote) {
            if (deletedNote.hasError) {
                Acm.Dialog.info(deletedNote.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Task.View.Notes.$divNotes);
            }
        }
        ,_makeJtData: function(notes) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Acm.isNotEmpty(notes)) {
                for (var i = 0; i < notes.length; i++) {
                    if(Task.Model.Notes.validateNote(notes[i])){
                        var Record = {};
                        Record.id         = Acm.goodValue(notes[i].id, 0);
                        Record.note       = Acm.goodValue(notes[i].note);
                        Record.created    = Acm.getDateFromDatetime(notes[i].created);
                        Record.creator    = Acm.__FixMe__getUserFullName(Acm.goodValue(notes[i].creator));
                        //Record.parentId   = Acm.goodValue(noteList[i].parentId);
                        //Record.parentType = Acm.goodValue(noteList[i].parentType);
                        jtData.Records.push(Record);
                    }
                }
                jtData.TotalRecordCount = notes.length;
            }
            return jtData;
        }
        ,createJTableNotes: function($jt) {
            var sortMap = {};
            sortMap["created"] = "created";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'Notes'
                    ,paging: true
                    ,sorting: true
                    ,pageSize: 10 //Set page size (default: 10)
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,messages: {
                        addNewRecord: 'Add Note'
                    }
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var taskId = ObjNav.View.Navigator.getActiveObjId();
                            if (0 >= taskId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var notes = Task.Model.Notes.cacheNoteList.get(taskId);
                            if (Task.Model.Notes.validateNotes(notes)) {
                                return Task.View.Notes._makeJtData(notes);

                            } else {
                                return Task.Service.Notes.retrieveNoteListDeferred(taskId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(notes) {
                                        if(Task.Model.Notes.validateNotes(notes)){
                                            return Task.View.Notes._makeJtData(notes);
                                        }
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }
                        ,createAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.JTable.getEmptyRecord();
                            if (Acm.isNotEmpty(record.note)) {
                                rc.Record.note = record.note;
                            }
                            return rc;
                        }
                        ,updateAction: function(postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = AcmEx.Object.jTableGetEmptyRecord();
                            if (Acm.isNotEmpty(record.note)) {
                                rc.Record.note = record.note;
                            }
                            return rc;
                        }
                        ,deleteAction: function(postData, jtParams) {
                            return {
                                "Result": "OK"
                            };
                        }
                    }

                    ,fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                            ,defaultvalue : 0
                        }
                        ,note: {
                            title: 'Note'
                            ,type: 'textarea'
                            ,width: '50%'
                            ,edit: true
                        }
                        ,created: {
                            title: 'Created'
                            ,width: '15%'
                            ,edit: false
                            ,create: false
                        }
                        ,creator: {
                            title: 'Author'
                            ,width: '15%'
                            ,edit: false
                            ,create: false
                        }
                    } //end field
                    ,recordAdded : function (event, data) {
                        var record = data.record;
                        var taskId = ObjNav.View.Navigator.getActiveObjId();
                        if (0 < taskId) {
                            var noteToSave = {};
                            //noteToSave.id = record.id;
                            //noteToSave.id = 0;
                            noteToSave.note = record.note;
                            noteToSave.created = Acm.getCurrentDayInternal();
                            noteToSave.creator = App.getUserName();
                            //noteToSave.parentId = Task.Model.getObjectId();     set parentId = current taskId ???
                            //noteToSave.parentType = Task.Model.getObjectType();
                            noteToSave.parentId = taskId;
                            noteToSave.parentType = Task.Model.DOC_TYPE_TASK;
                            Task.Controller.viewAddedNote(noteToSave);
                        }
                    }
                    ,recordUpdated: function(event,data){
                        var whichRow = data.row.prevAll("tr").length;
                        var record = data.record;
                        var taskId = ObjNav.View.Navigator.getActiveObjId();
                        if (0 < taskId) {
                            var notes = Task.Model.Notes.cacheNoteList.get(taskId);
                            if (Task.Model.Notes.validateNotes(notes)) {
                                if(Acm.isNotEmpty(notes[whichRow])){
                                    notes[whichRow].note = record.note;
                                    Task.Controller.viewUpdatedNote(notes[whichRow]);
                                }
                            }
                        }
                    }
                    ,recordDeleted : function (event, data) {
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var taskId = ObjNav.View.Navigator.getActiveObjId();
                        if (0 < taskId) {
                            var notes = Task.Model.Notes.cacheNoteList.get(taskId);
                            if (Task.Model.Notes.validateNotes(notes)) {
                                if(notes[whichRow]){
                                    Task.Controller.viewDeletedNote(notes[whichRow].id);
                                }
                            }
                        }
                    }
                } //end arg
                ,sortMap
            );
        }
    }

    ,History: {
        create: function() {
            this.$divHistory          = $("#divHistory");
            this.createJTableHistory(this.$divHistory);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Task.View.History.$divHistory);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Task.View.History.$divHistory);
        }

        ,_makeJtData: function(history) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Acm.isNotEmpty(history.events)) {
                var events = history.events;
                for (var i = 0; i < events.length; i++) {
                    if(Task.Model.History.validateEvent(events[i])){
                        var Record = {};
                        Record.eventType = Acm.goodValue(events[i].eventType);
                        Record.eventDate = Acm.getDateFromDatetime(events[i].eventDate);
                        Record.userId = Acm.__FixMe__getUserFullName(events[i].userId);
                        jtData.Records.push(Record);
                    }
                }
                jtData.TotalRecordCount = history.totalEvents;
            }
            return jtData;
        }
        ,createJTableHistory: function($jt) {
            var sortMap = {};
            sortMap["created"] = "created";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'History'
                    ,paging: true
                    ,sorting: true
                    ,pageSize: 10 //Set page size (default: 10)
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            var taskId = ObjNav.View.Navigator.getActiveObjId();
                            if (0 >= taskId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var historyCache = Task.Model.History.cacheHistory.get(taskId + "." + jtParams.jtStartIndex);
                            if (Task.Model.History.validateHistory(historyCache)) {
                                var history = {};
                                history.events = historyCache.resultPage;
                                history.totalEvents = historyCache.totalCount;
                                return Task.View.History._makeJtData(history);
                            } else {
                                return Task.Service.History.retrieveHistoryDeferred(taskId
                                    ,postData
                                    ,jtParams
                                    ,sortMap
                                    ,function(data) {
                                        if(Task.Model.History.validateHistory(data)){
                                            var history = {};
                                            history.events = data.resultPage;
                                            history.totalEvents = data.totalCount;
                                            return Task.View.History._makeJtData(history);
                                        }
                                        return AcmEx.Object.JTable.getEmptyRecords();
                                    }
                                    ,function(error) {
                                    }
                                );
                            }  //end else
                        }
                    }
                    , fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }, eventType: {
                            title: 'Event Name'
                            ,width: '50%'
                        }, eventDate: {
                            title: 'Date'
                            ,width: '25%'
                        }, userId: {
                            title: 'User'
                            ,width: '25%'
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }

    ,WorkflowOverview: {
        create: function() {
            this.$divWorkflowOverview          = $("#divWorkflowOverview");
            this.createJTableWorkflowOverview(this.$divWorkflowOverview);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_RETRIEVED_WORKFLOW_OVERVIEW    ,this.onModelRetrievedWorkflowOverview);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Task.View.WorkflowOverview.$divWorkflowOverview);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Task.View.WorkflowOverview.$divWorkflowOverview);
        }
        ,onModelRetrievedWorkflowOverview: function(workflowOverview) {
            if (workflowOverview.hasError) {
                Acm.Dialog.info(workflowOverview.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Task.View.WorkflowOverview.$divWorkflowOverview);
            }
        }

        ,_makeJtData: function(workflowOverview) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (Acm.isNotEmpty(workflowOverview)) {
                for (var i = 0; i < workflowOverview.length; i++) {
                    if(Task.Model.WorkflowOverview.validateWorkflowOverviewRecord(workflowOverview[i])){
                        var Record = {};
                        Record.participant = Acm.goodValue(workflowOverview[i].participant);
                        Record.startDateTime = Acm.getDateFromDatetime(workflowOverview[i].startDate);
                        Record.endDateTime = Acm.getDateFromDatetime(workflowOverview[i].endDate);
                        Record.role = Acm.goodValue(workflowOverview[i].role);
                        Record.status = Acm.goodValue(workflowOverview[i].status);
                        jtData.Records.push(Record);
                    }
                }
                jtData.TotalRecordCount = workflowOverview.length;
            }
            return jtData;
        }
        ,createJTableWorkflowOverview: function($jt) {
            var sortMap = {};
            sortMap["created"] = "created";

            AcmEx.Object.JTable.usePaging($jt
                ,{
                    title: 'Workflow Overview'
                    ,paging: true
                    ,sorting: true
                    ,pageSize: 10 //Set page size (default: 10)
                    ,selecting: true
                    ,multiselect: false
                    ,selectingCheckboxes: false
                    ,actions: {
                        listAction: function(postData, jtParams) {
                            var taskId = ObjNav.View.Navigator.getActiveObjId();
                            if (0 >= taskId) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var workflowOverview = Task.Model.WorkflowOverview.cacheWorkflowOverview.get(taskId);
                            if(Task.Model.WorkflowOverview.validateWorkflowOverview(workflowOverview)){
                                return Task.View.WorkflowOverview._makeJtData(workflowOverview);
                            }
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }
                    }
                    ,fields: {
                        id: {
                            title: 'ID'
                            ,key: true
                            ,list: false
                            ,create: false
                            ,edit: false
                        }
                        ,participant: {
                            title: 'Participant'
                            ,width: '15%'
                            ,edit: false
                        }
                        ,role: {
                            title: 'Role'
                            ,width: '15%'
                            ,edit: false
                        }
                        ,status: {
                            title: 'Status'
                            ,width: '30%'
                        }
                        ,startDateTime : {
                            title: 'Date/Time'
                            ,width: '20%'
                        }
                        ,endDateTime : {
                            title: 'Date/Time'
                            ,width: '20%'
                            ,list: false
                        }
                    } //end field
                } //end arg
                ,sortMap
            );
        }
    }

    ,Attachments:{
        create : function() {
            this.$divAttachments = $("#divAttachments");
            this.createJTableAttachments(this.$divAttachments);

            this.$btnAddNewAttachment = $("#addNewAttachments");
            this.$btnAddNewAttachment.on("change", function(e) {Task.View.Attachments.onChangeFileInput(e, this);});

            this.$formNewAttachments = $("#formAttachments");
            this.$formNewAttachments.submit(function(e) {Task.View.Attachments.onSubmitAddNewAttachments(e, this);});

            AcmEx.Object.JTable.clickAddRecordHandler(this.$divAttachments,this.onClickSpanAddNewAttachments);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_UPLOADED_ATTACHMENTS    ,this.onModelUploadedAttachments);

        }
        ,onInitialized: function() {
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Task.View.Attachments.$divAttachments);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Task.View.Attachments.$divAttachments);
        }
        ,onModelUploadedAttachments: function(attachments) {
            if (attachments.hasError) {
                Acm.Dialog.info(attachments.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Task.View.Attachments.$divAttachments);
            }
        }
        ,onClickSpanAddNewAttachments: function(event, ctrl) {
            Task.View.Attachments.$btnAddNewAttachment.click();
        }
        ,onChangeFileInput: function(event, ctrl) {
            Task.View.Attachments.$formNewAttachments.submit();
        }
        ,onSubmitAddNewAttachments: function(event, ctrl) {
            event.preventDefault();
            var count = Task.View.Attachments.$btnAddNewAttachment[0].files.length;
            var fd = new FormData();
            fd.append("taskId", ObjNav.View.Navigator.getActiveObjId());
            for(var i = 0; i < count; i++ ){
                fd.append("files[]", Task.View.Attachments.$btnAddNewAttachment[0].files[i]);
            }
            Task.Service.Attachments.uploadAttachments(fd);
            Task.View.Attachments.$formNewAttachments[0].reset();
        }
        ,_makeJtData: function(attachments){
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if(Acm.isNotEmpty(attachments)){
                for (var i = 0; i < attachments.length; i++) {
                    if(Task.Model.Attachments.validateAttachmentRecord(attachments[i])){
                        var record = {};
                        record.id = Acm.goodValue(attachments[i].targetId, 0);
                        record.title = Acm.goodValue(attachments[i].targetName);
                        record.created = Acm.getDateFromDatetime(attachments[i].created);
                        record.creator = Acm.__FixMe__getUserFullName(Acm.goodValue(attachments[i].creator));
                        record.status = Acm.goodValue(attachments[i].status);
                        jtData.Records.push(record);
                    }
                }
                jtData.TotalRecordCount = attachments.length;
            }
            return jtData;
        }
        ,createJTableAttachments: function($s) {
            $s.jtable({
                title: 'Attachments'
                ,paging: true
                ,pageSize: 10 //Set page size (default: 10)
                ,sorting: true
                ,messages: {
                    addNewRecord: 'Add Attachment'
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var task = Task.View.getActiveTask();
                        if(Task.Model.Attachments.validateExistingAttachments(task)){
                            var attachments = task.childObjects;
                            return Task.View.Attachments._makeJtData(attachments);
                        }
                        return AcmEx.Object.JTable.getEmptyRecords();
                    }
                    , createAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }
                ,fields: {
                    id: {
                        title: 'ID'
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                    }
                    ,title: {
                        title: 'Title'
                        ,width: '30%'
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + Task.Service.Attachments.API_DOWNLOAD_DOCUMENT
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    ,created: {
                        title: 'Created'
                        ,width: '15%'
                        ,edit: false
                    }
                    ,creator: {
                        title: 'Author'
                        ,width: '15%'
                        ,edit: false
                    }
                    ,status: {
                        title: 'Status'
                        ,width: '10%'
                    }
                }
            });

            $s.jtable('load');
        }

    }

    ,DocumentUnderReview: {
        create: function () {
            this.$divDocuments = $("#divDocuments");
            this.createJTableDocuments(this.$divDocuments);

            this.$lnkEditComplaintClose = $(".editCloseComplaint");//frevvo edit close complaint
            this.$lnkChangeCaseStatus = $(".changeCaseStatus");//frevvo change case status

//            var formUrls = new Object();
//            formUrls["roi"] = $('#roiFormUrl').val();
//            formUrls["close_complaint"] = $('#closeComplaintFormUrl').val();
//            formUrls["edit_close_complaint"] = $('#editCloseComplaintFormUrl').val();
//            formUrls["change_case_status"] = $('#changeCaseStatusFormUrl').val();
//            this.setFormUrls(formUrls);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);

        }
        , onInitialized: function () {
        }

        //frevvo edit close complaint
        ,onEditCloseComplaint: function() {
            /*var doc = {
             "fileId" : 4056,
             "status" : "ACTIVE",
             "created" : "2014-11-06T20:15:27.541+0000",
             "creator" : "ecmillar",
             "modified" : "2014-11-06T20:15:27.541+0000",
             "modifier" : "ecmillar",
             "ecmFileId" : "workspace://SpacesStore/88fa9bbd-f1ae-4b94-985a-ace90d3da228",
             "fileName" : "Close_Complaint_06112014151525629.pdf",
             "fileMimeType" : "application/pdf;frevvo-snapshot=true; charset=utf-8",
             "fileType" : "close_complaint",
             "parentObjects" : [{
             "associationId" : 4057,
             "status" : "ACTIVE",
             "parentName" : "20140806_198",
             "parentType" : "COMPLAINT",
             "parentId" : 409,
             "targetName" : "Close_Complaint_06112014151525629.pdf",
             "targetType" : "FILE",
             "targetId" : 4056,
             "created" : "2014-11-06T20:15:27.541+0000",
             "creator" : "ecmillar",
             "modified" : "2014-11-06T20:15:27.541+0000",
             "modifier" : "ecmillar"
             }
             ]
             };*/
            var task = Task.View.getActiveTask();
            if(Task.Model.Detail.validateTask(task)){
                if(Task.Model.DocumentUnderReview.validateDocumentUnderReview(task)) {
                    if (Task.Model.DocumentUnderReview.validateDocumentUnderReviewRecord(task.documentUnderReview)) {
                        var documentUnderReview = Acm.goodValue(task.documentUnderReview);
                        var parentName = Acm.goodValue(documentUnderReview.parentObjects[0].parentName);
                        var parentId = Acm.goodValue(documentUnderReview.parentObjects[0].parentId);
                        var reviewDocumentPdfRenditionId = Acm.goodValue(task.reviewDocumentPdfRenditionId);
                        var reviewDocumentFormXmlId = Acm.goodValue(task.reviewDocumentFormXmlId);
                        var workflowRequestId = Acm.goodValue(task.workflowRequestId);

                        var url = Task.View.MicroData.formUrls.editCloseComplaintFormUrl != null ? Task.View.MicroData.formUrls.editCloseComplaintFormUrl : '';
                        if (Acm.isNotEmpty(url)) {
                            url = url.replace("_data=(", "_data=(complaintId:'" + parentId + "',complaintNumber:'" + parentName +
                            "',mode:'edit',xmlId:" + "'" + reviewDocumentFormXmlId + "'" + ",pdfId:" + "'" + reviewDocumentPdfRenditionId + "'" + ",requestId:" + "'" + workflowRequestId + "'" + ",");
                            //url = url.replace("_data=(", "_data=(complaintId:'" + "409" + "',complaintNumber:'" + "20140806_198" + "',mode:'edit',xmlId:'783',pdfId:'785',requestId:'780',");

                            Acm.Dialog.openWindow(url, "", 860, 700, this.onDone);
                        }
                    }
                }
            }


            //task.documentUnderReview = doc;

        }

        ,onChangeCaseStatus: function() {
            var task = Task.View.getActiveTask();
            if(Task.Model.Detail.validateTask(task)) {
                if (Task.Model.DocumentUnderReview.validateDocumentUnderReview(task)) {
                    if (Task.Model.DocumentUnderReview.validateDocumentUnderReviewRecord(task.documentUnderReview)) {
                        //task.documentUnderReview = doc;
                        var documentUnderReview = Acm.goodValue(task.documentUnderReview);
                        var parentName = Acm.goodValue(documentUnderReview.parentObjects[0].parentName);
                        var parentId = Acm.goodValue(documentUnderReview.parentObjects[0].parentId);
                        var reviewDocumentPdfRenditionId = Acm.goodValue(task.reviewDocumentPdfRenditionId);
                        var reviewDocumentFormXmlId = Acm.goodValue(task.reviewDocumentFormXmlId);
                        var workflowRequestId = Acm.goodValue(task.workflowRequestId);

                        var url = Acm.isNotEmpty(Task.View.MicroData.formUrls.changeCaseStatusFormUrl) ? Task.View.MicroData.formUrls.changeCaseStatusFormUrl : '';
                        if (Acm.isNotEmpty(url)) {
                            url = url.replace("_data=(", "_data=(caseId:'" + parentId + "',caseNumber:'" + parentName +
                            "',mode:'edit',xmlId:" + "'" + reviewDocumentFormXmlId + "'" + ",pdfId:" + "'" + reviewDocumentPdfRenditionId + "'" + ",requestId:" + "'" + workflowRequestId + "'" + ",");

                            Acm.Dialog.openWindow(url, "", 860, 700, this.onDone);
                        }
                    }
                }
            }
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Task.View.DocumentUnderReview.$divDocuments);

            var task = Task.View.getActiveTask();
            Task.View.DocumentUnderReview.updateDocumentUnderReview(task);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Task.View.DocumentUnderReview.$divDocuments);
            Task.View.DocumentUnderReview.updateDocumentUnderReview(objData);
        }
        ,updateDocumentUnderReview: function(task) {
            if (Task.Model.Detail.validateTask(task)) {
                if (Task.Model.DOC_TYPE_COMPLAINT == Acm.goodValue(task.attachedToObjectType)) {
                    Task.View.DocumentUnderReview.$lnkEditComplaintClose.show();
                    Task.View.DocumentUnderReview.$lnkChangeCaseStatus.hide();
                } else if (Task.Model.DOC_TYPE_CASE_FILE == Acm.goodValue(task.attachedToObjectType)) {
                    Task.View.DocumentUnderReview.$lnkEditComplaintClose.hide();
                    Task.View.DocumentUnderReview.$lnkChangeCaseStatus.show();
                }
            }
        }
        ,_makeJtData: function(documentsUnderReview){
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if(Acm.isNotEmpty(documentsUnderReview)){
                if(Task.Model.DocumentUnderReview.validateDocumentUnderReviewRecord(documentsUnderReview)){
                    var record = {};
                    record.id = Acm.goodValue(documentsUnderReview.fileId, 0);
                    record.title = Acm.goodValue(documentsUnderReview.fileName);
                    record.created = Acm.getDateFromDatetime(documentsUnderReview.created);
                    record.creator = Acm.__FixMe__getUserFullName((Acm.goodValue(documentsUnderReview.creator)));
                    record.status = Acm.goodValue(documentsUnderReview.parentObjects[0].status);
                    jtData.Records.push(record);
                }
                //jtData.TotalRecordCount = documentsUnderReview.length;
            }
            return jtData;
        }
        , createJTableDocuments: function ($s) {
            $s.jtable({
                title: 'Documents Under Review'
                , paging: true
                , pageSize: 10 //Set page size (default: 10)
                , sorting: true
                , actions: {
                    listAction: function (postData, jtParams) {
                        var task = Task.View.getActiveTask();
                        if(Task.Model.DocumentUnderReview.validateDocumentUnderReview(task)){
                            var documentUnderReview = task.documentUnderReview;
                            return Task.View.DocumentUnderReview._makeJtData(documentUnderReview);
                        }
                        return AcmEx.Object.JTable.getEmptyRecords();
                    }
                }
                , toolbar: {
                    items: [{
                        //icon: 'jtable-edit-command-button',
                        cssClass: 'editCloseComplaint',
                        text: 'Edit Close Complaint Request',
                        click: function () {
                            Task.View.DocumentUnderReview.onEditCloseComplaint();
                        }
                    },
                        {
                            //icon: 'jtable-edit-command-button',
                            cssClass: 'changeCaseStatus',
                            text: 'Change Case Status',
                            click: function () {
                                Task.View.DocumentUnderReview.onChangeCaseStatus();
                            }
                        }]
                }
                , fields: {
                    id: {
                        title: 'ID'
                        , key: true
                        , list: true
                        , create: false
                        , edit: false
                    }
                    , title: {
                        title: 'Title'
                        , width: '50%'
                        , display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + Task.Service.DocumentUnderReview.API_DOWNLOAD_DOCUMENT
                                + ((0 >= commData.record.id) ? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    , created: {
                        title: 'Created'
                        , width: '15%'
                        , edit: false
                    }
                    , author: {
                        title: 'Author'
                        , width: '15%'
                        , edit: false
                    }
                    , status: {
                        title: 'Status'
                        , width: '20%'
                    }
                }
            });

            $s.jtable('load');
        }
    }

    ,RejectComments: {
        create: function () {
            this.$divRejectComments = $("#divRejectComments");
            this.createJTableRejectComments(this.$divRejectComments);

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT         ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT           ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_RETRIEVED_REJECT_COMMENTS  ,this.onModelRetrievedRejectComments);

        }
        , onInitialized: function () {
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Task.View.RejectComments.$divRejectComments);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Task.View.RejectComments.$divRejectComments);
        }
        ,onModelRetrievedRejectComments: function(rejectComments){
            if (rejectComments.hasError) {
                Acm.Dialog.info(rejectComments.errorMsg);
            } else {
                AcmEx.Object.JTable.load(Task.View.RejectComments.$divRejectComments);
            }
        }
        ,_makeJtData: function(rejectComments){
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if(Acm.isNotEmpty(rejectComments)){
                if(Task.Model.RejectComments.validateRejectComments(rejectComments)) {
                    for (var i = 0; i < rejectComments.length; i++) {
                        if(Task.Model.RejectComments.validateRejectCommentRecord(rejectComments[i])) {
                            var record = {};
                            record.id = Acm.goodValue(rejectComments[i].id);
                            record.comment = rejectComments[i].note;
                            record.created = Acm.getDateFromDatetime(rejectComments[i].created);
                            record.creator = Acm.__FixMe__getUserFullName(rejectComments[i].creator);
                            record.parentId = Acm.goodValue(rejectComments[i].parentId);
                            record.parentType = rejectComments[i].parentType;
                            jtData.Records.push(record);
                        }
                    }
                }
                jtData.TotalRecordCount = rejectComments.length;
            }
            return jtData;
        }
        , createJTableRejectComments: function ($s) {
            $s.jtable({
                title: 'Reject Comments'
                , paging: true
                , sorting: true
                , pageSize: 10 //Set page size (default: 10)
                , actions: {
                    listAction: function (postData, jtParams) {
                        var taskId = ObjNav.View.Navigator.getActiveObjId();
                        var rejectComments = Task.Model.RejectComments.cacheRejectComments.get(taskId);
                        if(Task.Model.RejectComments.validateRejectComments(rejectComments)){
                            return Task.View.RejectComments._makeJtData(rejectComments);
                        }
                        return AcmEx.Object.JTable.getEmptyRecords();

//                        var rc = AcmEx.Object.jTableGetEmptyRecords();
//                        var task = Task.View.getActiveTask();
//                        if (task) {
//                            var rejectComments = Task.Model.RejectComments.cacheRejectComments.get(task.taskId);
//                            if (rejectComments) {
//                                for (var i = 0; i < rejectComments.length; i++) {
//                                    var rejectCommentRecord = rejectComments[i];
//                                    var record = {};
//                                    record.id = Acm.goodValue(rejectCommentRecord.id);
//                                    record.comment = rejectCommentRecord.note;
//                                    record.created = Acm.getDateFromDatetime(rejectCommentRecord.created);
//                                    record.creator = Acm.__FixMe__getUserFullName(rejectCommentRecord.creator);
//                                    record.parentId = Acm.goodValue(rejectCommentRecord.parentId);
//                                    record.parentType = rejectCommentRecord.parentType;
//                                    rc.Records.push(record);
//                                }
//                            }
//                        }
//                        return rc;
                    }
                }
                , fields: {
                    id: {
                        title: 'ID'
                        , key: true
                        , list: false
                        , create: false
                        , edit: false
                        , defaultvalue: 0
                    }
                    , comment: {
                        title: 'Comment'
                        , type: 'textarea'
                        , width: '50%'
                        , edit: false
                    }
                    , created: {
                        title: 'Created'
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                    , creator: {
                        title: 'Author'
                        , width: '15%'
                        , edit: false
                        , create: false
                    }
                }
            });

            $s.jtable('load');
        }
    }
};

