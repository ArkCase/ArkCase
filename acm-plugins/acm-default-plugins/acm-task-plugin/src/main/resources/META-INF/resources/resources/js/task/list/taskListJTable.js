/**
 * Created by manoj.dhungana on 10/31/2014.
 */


TaskList.JTable = {
    create : function() {
    }
    //
    //----------------- Documents ------------------------------
    //

    ,createJTableDocuments: function($s) {
        $s.jtable({
            title: 'Documents Under Review'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var task = TaskList.getTask();
                    if (task && task.documentUnderReview != null) {
                        var documentUnderReview = task.documentUnderReview;
                        var record = {};
                        record.id = Acm.goodValue(documentUnderReview.fileId, 0);
                        record.title = Acm.goodValue(documentUnderReview.fileName);
                        record.created = Acm.getDateFromDatetime(documentUnderReview.created);
                        record.creator = Acm.goodValue(documentUnderReview.creator);
                        record.status = Acm.goodValue(documentUnderReview.parentObjects[0].status);
                        rc.Records.push(record);
                    }
                    return rc;
                }
               /* ,createAction: function(postData, jtParams) {
                    //custom web form creation takes over; this action should never be called
                    var rc = {"Result": "OK", "Record": {id:0, title:"", type:"", created:"", author:"", status:""}};
                    return rc;
                }*/
                /*,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    //id,created,creator is readonly
                    //rc.Record.id = record.id;
                    //rc.Record.created = record.created;
                    //rc.Record.creator = record.creator;
                    rc.Record.title = record.title;
                    rc.Record.status = record.status;
                    return rc;
                }*/
            }
            ,toolbar: {
                items: [{
                    //icon: 'jtable-edit-command-button',
                    text: 'Edit Close Complaint Request',
                    click: function () {
                        TaskList.Event.onEditCloseComplaint();
                    }
                }]
            }
            ,fields: {
                id: {
                    title: 'ID'
                    ,key: true
                    ,list: true
                    ,create: false
                    ,edit: false
                }
                ,title: {
                    title: 'Title'
                    ,width: '10%'
                    ,display: function (commData) {
                        var a = "<a href='" + App.getContextPath() + TaskList.Service.API_DOWNLOAD_DOCUMENT
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
                ,author: {
                    title: 'Author'
                    ,width: '15%'
                    ,edit: false
                }
                ,status: {
                    title: 'Status'
                    ,width: '30%'
                }
            }
            /*,recordUpdated : function (event, data) {
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var record = data.record;
                var task = TaskList.getTask();
                if (task) {
                    if (task.childObjeccts) {
                        if (0 < task.childObjects.length && whichRow < task.childObjects.length) {
                            var childObject = task.childObjects[whichRow];
                            //id,created,creator is readonly
                            //childObject.Record.id = record.id;
                            //childObject.Record.created = record.created;
                            //childObject.Record.creator = record.creator;
                            childObject.Record.title = record.title;
                            childObject.Record.status = record.status;

                            TaskList.Service.listTaskSaveDetail(task.taskId,task);
                        }
                    }
                }
            }*/
        });

        $s.jtable('load');
    }
    //----------------- end of Documents ----------------------


    ,createJTableAttachments: function($s) {
        $s.jtable({
            title: 'Attachments'
            ,paging: false
            ,messages: {
                addNewRecord: 'Add Attachment'
            }
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var task = TaskList.getTask();
                    if (task && task.documentUnderReview != null) {
                        var documentUnderReview = task.documentUnderReview;
                        var record = {};
                        record.id = Acm.goodValue(documentUnderReview.fileId, 0);
                        record.title = Acm.goodValue(documentUnderReview.fileName);
                        record.created = Acm.getDateFromDatetime(documentUnderReview.created);
                        record.creator = Acm.goodValue(documentUnderReview.creator);
                        record.status = Acm.goodValue(documentUnderReview.parentObjects[0].status);
                        rc.Records.push(record);
                    }
                    return rc;
                }
                ,createAction: function(postData, jtParams) {
                    //custom web form creation takes over; this action should never be called
                    var rc = {"Result": "OK", "Record": {id:0, title:"", type:"", created:"", author:"", status:""}};
                    return rc;
                }
                /*,updateAction: function(postData, jtParams) {
                 var record = Acm.urlToJson(postData);
                 var rc = AcmEx.Object.jTableGetEmptyRecord();
                 //id,created,creator is readonly
                 //rc.Record.id = record.id;
                 //rc.Record.created = record.created;
                 //rc.Record.creator = record.creator;
                 rc.Record.title = record.title;
                 rc.Record.status = record.status;
                 return rc;
                 }*/
            }
            ,fields: {
                id: {
                    title: 'ID'
                    ,key: true
                    ,list: true
                    ,create: false
                    ,edit: false
                }
                ,title: {
                    title: 'Title'
                    ,width: '10%'
                    ,display: function (commData) {
                        var a = "<a href='" + App.getContextPath() + TaskList.Service.API_DOWNLOAD_DOCUMENT
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
                ,author: {
                    title: 'Author'
                    ,width: '15%'
                    ,edit: false
                }
                ,status: {
                    title: 'Status'
                    ,width: '30%'
                }
            }
            /*,recordUpdated : function (event, data) {
             var whichRow = data.row.prevAll("tr").length;  //count prev siblings
             var record = data.record;
             var task = TaskList.getTask();
             if (task) {
             if (task.childObjeccts) {
             if (0 < task.childObjects.length && whichRow < task.childObjects.length) {
             var childObject = task.childObjects[whichRow];
             //id,created,creator is readonly
             //childObject.Record.id = record.id;
             //childObject.Record.created = record.created;
             //childObject.Record.creator = record.creator;
             childObject.Record.title = record.title;
             childObject.Record.status = record.status;

             TaskList.Service.listTaskSaveDetail(task.taskId,task);
             }
             }
             }
             }*/
        });

        $s.jtable('load');
    }
    //----------------- end of Attachments ----------------------



    ,createJTableWorkflowOverview: function($s) {
        $s.jtable({
            title: 'Workflow Overview'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    
                    //var task = TaskList.getTask();
                    var taskWorkflowHistory = TaskList.getWorkflowHistory();
                    if (taskWorkflowHistory){
                        var workflowHistory = taskWorkflowHistory;
                        for (var i = 0; i < workflowHistory.length; i++){
                            var record = {};
                            record.id = Acm.goodValue(workflowHistory[i].id);;
                            record.participant = Acm.goodValue(workflowHistory[i].participant);
                            record.role = Acm.goodValue(workflowHistory[i].role);
                            record.status = Acm.goodValue(workflowHistory[i].status);
                            record.dateTime = Acm.getDateTimeFromDatetime(workflowHistory[i].startDate);
                            rc.Records.push(record);
                        }
                    }
                    /*if (task && task.workflowHistory){
                    	var workflowHistory = task.workflowHistory;
                    	for (var i = 0; i < workflowHistory.length; i++){
                    		var record = {};
                            record.id = Acm.goodValue(workflowHistory[i].id);;
                            record.participant = Acm.goodValue(workflowHistory[i].participant);
                            record.role = Acm.goodValue(workflowHistory[i].role);
                            record.status = Acm.goodValue(workflowHistory[i].status);
                            record.dateTime = Acm.getDateTimeFromDatetime(workflowHistory[i].startDate);
                            rc.Records.push(record);
                    	}
                    }*/
                    
                    return rc;
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
                ,dateTime : {
                    title: 'Date/Time'
                    ,width: '20%'
                }
            }
        });

        $s.jtable('load');
    }
    //----------------- end of Attachments ----------------------


    //
    //----------------- Notes ------------------------------

    ,createJTableNotes: function($s) {
        $s.jtable({
            title: 'Notes'
            ,paging: false
            ,messages: {
                addNewRecord: 'Add Note'
            }
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var task = TaskList.getTask();
                    if(task)
                    {
                        var notes = TaskList.cacheNoteList.get(task.taskId);
                        if (notes) {
                            for (var i = 0; i < notes.length; i++) {
                                var noteRecord = notes[i];
                                var record = {};
                                record.id = Acm.goodValue(noteRecord.id);
                                record.note = noteRecord.note;
                                record.created = Acm.getDateFromDatetime(noteRecord.created);
                                record.creator = noteRecord.creator;
                                record.parentId = Acm.goodValue(noteRecord.parentId);
                                record.parentType = noteRecord.parentType;
                                rc.Records.push(record);
                            }
                        }
                    }
                    return rc;
                }
                ,createAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var task = TaskList.getTask();
                    if (task) {
                        if(task.businessProcessId !=null){
                            rc.Record.parentId = Acm.goodValue(task.businessProcessId);
                            rc.Record.parentType = App.OBJTYPE_BUSINESS_PROCESS;
                        }
                        else{
                            rc.Record.parentId = Acm.goodValue(task.taskId);
                            rc.Record.parentType = App.OBJTYPE_TASK;
                        }

                        rc.Record.note = record.note;
                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                        rc.Record.creator = App.getUserName();   //record.creator;
                    }
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var task = TaskList.getTask();
                    if (task) {
                        rc.Record.parentId = record.parentId;
                        rc.Record.parentType = record.parentType;
                        rc.Record.note = record.note;
                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                        rc.Record.creator = App.getUserName();   //record.creator;
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
            }
            ,recordAdded : function (event, data) {
                var record = data.record;
                var task = TaskList.getTask();
                if (task) {
                    var noteToSave = {};
                    noteToSave.note = record.note;
                    noteToSave.created = Acm.getCurrentDayInternal(); //record.created;
                    noteToSave.creator = record.creator;   //record.creator;
                    noteToSave.parentId = Acm.goodValue(record.parentId);
                    noteToSave.parentType = record.parentType;
                    TaskList.Service.saveNote(noteToSave);
                }
            }
            ,recordUpdated: function(event,data){
                var whichRow = data.row.prevAll("tr").length;
                var record = data.record;
                var task = TaskList.getTask();
                if(task){
                    var id;
                    if(task.businessProcessId != null){
                        id = task.businessProcessId;
                    }
                    else{
                        id = task.taskId;
                    }
                    var notes = TaskList.cacheNoteList.get(id);
                    if (notes) {
                        if(notes[whichRow]){
                            var noteToSave;
                            noteToSave = notes[whichRow];
                            noteToSave.note = record.note;
                            TaskList.Service.saveNote(noteToSave);
                        }
                    }
                }
            }
            ,recordDeleted : function (event, data) {
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var task = TaskList.getTask();
                if (task) {
                    var id;
                    if(task.businessProcessId != null){
                        id = task.businessProcessId;
                    }
                    else{
                        id = task.taskId;
                    }
                    var notes = TaskList.cacheNoteList.get(id);
                    if (notes) {
                        var noteToDelete = notes[whichRow];
                        var noteId = noteToDelete.id;
                        TaskList.Service.deleteNoteById(noteId);
                    }
                }
            }

        });

        $s.jtable('load');
    }
    //----------------- end of Notes ----------------------


    //
    //----------------- Event Log ------------------------------

    ,createJTableEvents: function($s) {
        var sortMap = {};
        AcmEx.Object.jTableCreatePaging($s
            , {
                title: 'Event Log'
                ,sorting: "true"
                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        var taskId = TaskList.getTaskId();
                        var taskEvents = TaskList.cacheTaskEvents.get(taskId);
                        //var err = "";
                        var jtData = AcmEx.Object.jTableGetEmptyRecords();
                        if (taskEvents && taskEvents.resultPage) {
                            var resultPage = taskEvents.resultPage;
                            for (var i = 0; i < resultPage.length; i++) {
                                var Record = {};
                                Record.eventType = resultPage[i].eventType;
                                Record.eventDate = Acm.getDateFromDatetime(resultPage[i].eventDate);
                                Record.userId = resultPage[i].userId;
                                jtData.Records.push(Record);
                            }
                            jtData.TotalRecordCount = taskEvents.totalCount;
                            //return {jtData: jtData, jtError: err};
                            return jtData;
                        }
                        return AcmEx.Object.jTableDefaultPagingListAction(postData, jtParams, sortMap
                            , function () {
                                var taskId = TaskList.getTaskId();
                                var url;
                                url = App.getContextPath() + TaskList.Service.API_TASK_EVENTS + taskId;
                                return url;
                            }
                            , function (data) {
                                var jtData = null;
                                var err = "Error";
                                jtData = AcmEx.Object.jTableGetEmptyRecords();
                                if (data) {
                                    var resultPage = data.resultPage;
                                    for (var i = 0; i < resultPage.length; i++) {
                                        var Record = {};
                                        /*if(!(resultPage[i].eventType == ('searchResult') || resultPage[i].eventType == ('result') ||
                                            resultPage[i].eventType == ('findById'))){*/
                                            Record.eventType = resultPage[i].eventType;
                                            Record.eventDate = Acm.getDateFromDatetime(resultPage[i].eventDate);
                                            Record.userId = resultPage[i].userId;
                                            jtData.Records.push(Record);
                                        //}
                                    }
                                    TaskList.cacheTaskEvents.put(taskId, taskEvents);
                                    //jtData.TotalRecordCount = data.totalCount;
                                    jtData.TotalRecordCount = jtData.Records.length;
                                }
                                else {
                                    if (Acm.isNotEmpty(data.hasError)) {
                                        err = data.errorMsg;
                                    }
                                }
                                return {jtData: jtData, jtError: err};
                            }
                        );

                    }
                }
                , fields: {
                    id: {
                        title: 'ID', key: true, list: false, create: false, edit: false
                    }, eventType: {
                        title: 'Event Name', width: '10%'
                    }, eventDate: {
                        title: 'Date', width: '12%'
                    }, userId: {
                        title: 'User', width: '10%'
                    }
                }//end field
            }

            //end arg
            //,sortMap
        );
    }
    //----------------- end of history ----------------------



};




