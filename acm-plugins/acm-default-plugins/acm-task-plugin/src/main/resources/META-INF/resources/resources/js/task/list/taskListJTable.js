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
            ,messages: {
                addNewRecord: 'Add Document'
            }
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var task = TaskList.getTask();
                    if (task && task.childObjects) {
                        for (var i = 0; i < task.childObjects.length; i++) {
                            var childObject = task.childObjects[i];
                            var record = {};
                            record.id = Acm.goodValue(childObject.targetId, 0);
                            record.title = Acm.goodValue(childObject.targetName);
                            record.created = Acm.getDateFromDatetime(childObject.created);
                            record.creator = Acm.goodValue(childObject.creator);
                            record.status = Acm.goodValue(childObject.status);
                            rc.Records.push(record);
                        }
                    }
                    return rc;
                }
                ,createAction: function(postData, jtParams) {
                    //custom web form creation takes over; this action should never be called
                    var rc = {"Result": "OK", "Record": {id:0, title:"", type:"", created:"", author:"", status:""}};
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    //id,created,creator is readonly
                    //rc.Record.id = record.id;
                    //rc.Record.created = record.created;
                    //rc.Record.creator = record.creator;
                    rc.Record.title = record.title;
                    rc.Record.status = record.status;
                    return rc;
                }
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
            ,recordUpdated : function (event, data) {
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
            }
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
                    /*var task = TaskList.getTask();
                     if (task && task.childObjects) {
                     for (var i = 0; i < task.childObjects.length; i++) {
                     var childObject = task.childObjects[i];
                     var record = {};
                     record.id = Acm.goodValue(childObject.targetId, 0);
                     record.title = Acm.goodValue(childObject.targetName);
                     record.created = Acm.getDateFromDatetime(childObject.created);
                     record.creator = Acm.goodValue(childObject.creator);
                     record.status = Acm.goodValue(childObject.status);
                     rc.Records.push(record);
                     }
                     }*/
                    return rc;
                }
                ,createAction: function(postData, jtParams) {
                    //custom web form creation takes over; this action should never be called
                    var rc = {"Result": "OK", "Record": {id:0, title:"", type:"", created:"", author:""}};
                    return rc;
                }
                ,deleteAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                    };
                }
                /*,updateAction: function(postData, jtParams) {
                    *//*var record = Acm.urlToJson(postData);
                     var rc = AcmEx.Object.jTableGetEmptyRecord();
                     //id,created,creator is readonly
                     //rc.Record.id = record.id;
                     //rc.Record.created = record.created;
                     //rc.Record.creator = record.creator;
                     rc.Record.title = record.title;
                     rc.Record.status = record.status;*//*
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
                        var a = "<a href='" + App.getContextPath() + Complaint.Service.API_DOWNLOAD_DOCUMENT
                            + ((0 >= commData.record.id)? "#" : commData.record.id)
                            + "'>" + commData.record.title + "</a>";
                        return $(a);
                    }
                }
                ,type: {
                    title: 'Type'
                    ,width: '10%'
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
            }
            /*
            ,recordUpdated : function (event, data) {
                 var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                 var record = data.record;
                 var c = Complaint.getComplaint();
                 if (c) {
                 if (c.childObjeccts) {
                 if (0 < c.childObjects.length && whichRow < c.childObjects.length) {
                 var childObject = c.childObjects[whichRow];
                 //id,created,creator is readonly
                 //childObject.Record.id = record.id;
                 //childObject.Record.created = record.created;
                 //childObject.Record.creator = record.creator;
                 childObject.Record.title = record.title;
                 childObject.Record.status = record.status;

                 Complaint.Service.saveComplaint(c);
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
                        rc.Record.parentId = Acm.goodValue(record.parentId);
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
                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        /*var c = CaseFile.getCaseFile();
                        if(c){
                            var jtData=null;
                            jtData = AcmEx.Object.jTableGetEmptyRecords();
                            var caseEvents = CaseFile.cacheCaseEvents.get(c.id);
                            if (caseEvents) {
                                jtData = CaseFile.JTable._makeEventRecords(caseEvents);
                                jtData.TotalRecordCount = caseEvents.length;
                                return jtData;
                            }
                            var caseId = c.id;
                            return AcmEx.Object.jTableDefaultPagingListAction(postData, jtParams, sortMap
                                , function () {
                                    var url;
                                    url = App.getContextPath() + CaseFile.Service.API_EVENTS_CASE_+ caseId;
                                    return url;
                                }
                                , function (caseEvents) {
                                    var err = "Error";
                                    if (caseEvents) {
                                        jtData = CaseFile.JTable._makeEventRecords(caseEvents);
                                        CaseFile.cacheCaseEvents.put(caseId, caseEvents);
                                        jtData.TotalRecordCount = caseEvents.length;
                                    }
                                    else {
                                        if (Acm.isNotEmpty(caseEvents.error)) {
                                            err = caseEvents.error.msg + "(" + caseEvents.error.code + ")";
                                        }
                                    }
                                    return {jtData: jtData, jtError: err};
                                }
                            );
                        }
                        else{*/
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            return rc;
                        //}
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




