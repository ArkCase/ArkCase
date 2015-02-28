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
        if (Task.View.Detail.create)                                {Task.View.Detail.create();}
        if (Task.View.Notes.create)                                 {Task.View.Notes.create();}
        if (Task.View.History.create)                               {Task.View.History.create();}
        if (Task.View.WorkflowOverview.create)                      {Task.View.WorkflowOverview.create();}
        if (Task.View.Attachments.create)                           {Task.View.Attachments.create();}
        if (Task.View.Detail.create)                                {Task.View.Detail.create();}
        if (Task.View.DocumentUnderReview.create)                   {Task.View.DocumentUnderReview.create();}
        if (Task.View.RejectComments.create)                        {Task.View.RejectComments.create();}

    }
    ,onInitialized: function() {
        if (Task.View.MicroData.onInitialized)                      {Task.View.MicroData.onInitialized();}
        if (Task.View.Navigator.onInitialized)                      {Task.View.Navigator.onInitialized();}
        if (Task.View.Content.onInitialized)                        {Task.View.Content.onInitialized();}
        if (Task.View.Detail.onInitialized)                         {Task.View.Detail.onInitialized();}
        if (Task.View.Notes.onInitialized)                          {Task.View.Notes.onInitialized();}
        if (Task.View.History.onInitialized)                        {Task.View.History.onInitialized();}
        if (Task.View.WorkflowOverview.onInitialized)               {Task.View.WorkflowOverview.onInitialized();}
        if (Task.View.Attachments.onInitialized)                    {Task.View.Attachments.onInitialized();}
        if (Task.View.Detail.onInitialized)                         {Task.View.Detail.onInitialized();}
        if (Task.View.DocumentUnderReview.onInitialized)            {Task.View.DocumentUnderReview.onInitialized();}
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
        }
        ,onInitialized: function() {
        }

    }

    ,Navigator: {
        create: function() {
            this.$ulFilter = $("#ulFilter");
            this.$ulSort   = $("#ulSort");
            this.$tree     = $("#tree");

            //Acm.Dispatcher.addEventListener(Task.Controller.VIEW_CHANGED_TASK_TITLE       , this.onViewChangedTaskTitle);
        }
        ,onInitialized: function() {
        }

        ,onViewChangedTaskTitle: function(nodeType, nodeId, title) {
            Task.View.Navigator.updateTitle(nodeType, nodeId, title);
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

        ,updateTitle: function(nodeType, nodeId, caseTitle) {
            var task = Task.Model.findTask(nodeType, nodeId);
            if (task) {
                var nodeTitle = Task.Model.interface(task);
                var key = ObjNav.Model.Tree.Key.getKeyByObj(nodeType, nodeId);
                ObjNav.View.Navigator.setTitle(key, nodeTitle);
            }
        }
        ,lazyLoad: function(event, data) {
//            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
//            var pageId = treeInfo.start;

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

    ,Detail: {
        create : function() {
            this.$divDetail       = $(".divDetail");
            this.$btnEditDetail   = $("#tabDetails button:eq(0)");
            this.$btnSaveDetail   = $("#tabDetails button:eq(1)");
            this.$btnEditDetail.on("click", function(e) {Task.View.Detail.onClickBtnEditDetail(e, this);});
            this.$btnSaveDetail.on("click", function(e) {Task.View.Detail.onClickBtnSaveDetail(e, this);});

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT          ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT            ,this.onViewSelectedObject);
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_SAVED_DETAIL                ,this.onModelSavedDetail);
        }
        ,onInitialized: function() {
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

        ,onModelRetrievedObject: function(objData) {
            if (Task.Model.interface.validateObjData(objData)) {
                Task.View.Detail.setHtmlDivDetail(Acm.goodValue(objData.details));
            }
        }
        ,onViewSelectedObject: function(objType, objId) {
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            if (Task.Model.interface.validateObjData(objData)) {
                Task.View.Detail.setHtmlDivDetail(Acm.goodValue(objData.details));
            }
        }
        ,onModelSavedDetail: function(details){
            if (details.hasError) {
                Task.View.Detail.setHtmlDivDetail("(Error)");
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
                            var history = Task.Model.History.cacheHistory.get(taskId);
                            if (Task.Model.History.validateHistory(history)) {
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

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT   ,this.onModelRetrievedObject);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT     ,this.onViewSelectedObject);

        }
        , onInitialized: function () {
        }
        ,onViewSelectedObject: function(objType, objId) {
            AcmEx.Object.JTable.load(Task.View.DocumentUnderReview.$divDocuments);
        }
        ,onModelRetrievedObject: function(objData) {
            AcmEx.Object.JTable.load(Task.View.DocumentUnderReview.$divDocuments);
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
                jtData.TotalRecordCount = documentsUnderReview.length;
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
                            TaskList.Event.onEditCloseComplaint();
                        }
                    },
                        {
                            //icon: 'jtable-edit-command-button',
                            cssClass: 'changeCaseStatus',
                            text: 'Change Case Status',
                            click: function () {
                                TaskList.Event.onChangeCaseStatus();
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

