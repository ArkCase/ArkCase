/**
 * TaskWizard.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
TaskWizard.Object = {
    initialize : function() {
        this.$btnSave                = $("button[data-title='Save']");
        this.$btnSubmit              = $("button[data-title='Submit']");

        this.$divTaskDetails    = $('.taskDetails');
        this.$selIntiatorFlags       = $(".choose-intitiatorFlags");   //"#intiatorFlags"
        this.$selTaskFlags      = $(".choose-taskFlags");    //"#intiatorFlags"
        this.$selApprovers           = $(".choose-approvers");         //"#approvers"
        this.$selCollab              = $(".choose-collab");            //""
        this.$selNotifications       = $(".choose-notifications");     //"#notifications"
        this.$edtIncidentDate        = $("#incidentDate");
        this.$edtTaskTitle      = $("#edtTaskTitle");
        this.$selPriority            = $("select[name='priority']");

        this.$divInitiator           = $("#divInitiator");
        this.$divPeople              = $("#divPeople");
        this.$divDevices             = $("#divDevices");

        this.$uppload                = $('#upload');
        this.$upploadDrop            = $('#drop');
        this.$upploadList            = $('#upload ul');
        this.$upploadClick           = $('#drop a');

        this.$btnSave.click(function(e) {TaskWizard.Event.onClickBtnSave(e);});
        this.$btnSubmit.click(function(e) {TaskWizard.Event.onClickBtnSubmit(e);});

        this.$divTaskDetails.summernote({
            height: 300
        });

        this.$selIntiatorFlags.chosen();
        this.$selTaskFlags.chosen();
        //this.$selApprovers.chosen();
        this.$selCollab.chosen();
        this.$selNotifications.chosen();

        this._createJTableInitiator(this.$divInitiator);
        this._createJTablePeople(this.$divPeople);
        this._createJTableDevices(this.$divDevices);
        this._useFileUpload(this.$uppload, this.$upploadDrop, this.$upploadList, this.$upploadClick);
    }

    ,initApprovers: function(data) {
        $.each(data, function(idx, val) {
            TaskWizard.Object.appendApprovers(val.userId, val.fullName);
        });
        this.$selApprovers.chosen();
    }
    ,appendApprovers: function(key, val) {
        this.$selApprovers.append($("<option></option>")
                .attr("value",key)
                .text(val));
    }
    ,setEnableBtnSave: function(enable) {
        Acm.Object.setEnable(this.$btnSave, enable);
    }
    ,getHtmlDivTaskDetails: function() {
        return Acm.Object.getSummernote(this.$divTaskDetails);
    }
    ,getValueEdtIncidentDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtIncidentDate);
    }
    ,getValueEdtTaskTitle: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtTaskTitle);
    }
    ,getSelectValuesSelIntiatorFlags: function() {
        return Acm.Object.getSelectValues(this.$selIntiatorFlags);
    }
    ,getSelectValuesSelTaskFlags: function() {
        return Acm.Object.getSelectValues(this.$selTaskFlags);
    }
    ,getSelectValuesSelApprovers: function() {
        return Acm.Object.getSelectValues(this.$selApprovers);
    }
    ,getSelectValuesSelCollab: function() {
        return Acm.Object.getSelectValues(this.$selCollab);
    }
    ,getSelectValuesSelNotifications: function() {
        return Acm.Object.getSelectValues(this.$selNotifications);
    }
    ,getSelectValueSelPriority: function() {
        return Acm.Object.getSelectValue(this.$selPriority);
    }

    ,setTaskData : function(data) {
        var c = Task.getTask();
        c.complaintId = data.complaintId;

        c.complaintTitle = data.complaintTitle;
        c.priority = data.priority;
//        c.complaintNumber = data.complaintNumber;
//        c.complaintType = data.complaintType;
//        c.created = data.created;
//        c.creator = data.creator;
//        c.details = data.details;
//        c.incidentDate = data.incidentDate;   //need date convert
//        c.modified = data.modified;
//        c.modifier = data.modifier;
//        c.status = data.status;

        c.originator.id = data.originator.id;
        c.originator.title = data.originator.title;
        c.originator.givenName = data.originator.givenName;
        c.originator.familyName = data.originator.familyName;
//        c.originator.created = data.originator.created;
//        c.originator.creator = data.originator.creator;
//        c.originator.modified = data.originator.modified;
//        c.originator.modifier = data.originator.modifier;
//        c.originator.status = data.originator.status;

//        c.originator.addresses[...] = data.originator.addresses;
//        c.originator.contactMethods[] = data.originator.contactMethods;
//        c.originator.devices[] = data.originator.devices;
//        c.originator.aliases[] = data.originator.aliases;
//        c.originator.ecmFolderId[] = data.originator.ecmFolderId;
//        c.originator.ecmFolderPath[] = data.originator.ecmFolderPath;
    }
    ,getTaskData : function() {
        var data = {};
        var c = Task.getTask();
//        var taskId = Task.getTaskId();
//        //data.complaintId = (Acm.isEmpty(taskId))?  0 : taskId;
//        if (Acm.isNotEmpty(taskId)) {
//            data.complaintId = taskId;
//        }

        data.complaintId = c.complaintId;

        data.originator = {};
        data.originator.id = c.originator.id;
        data.originator.title = c.originator.title;
        data.originator.givenName = c.originator.givenName;
        data.originator.familyName = c.originator.familyName;


        //$selTaskType

//        ContactMethod contactMethods
//        PostalAddress addresses
//        organizations
//        aliases

        data.details = this.getHtmlDivTaskDetails();
        //data.incidentDate = this.getValueEdtIncidentDate();
        //need to parse date "12-02-2013": to forms ("yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd"))

        data.complaintTitle = this.getValueEdtTaskTitle();
        data.priority = this.getSelectValueSelPriority();

        //"securityTags": ["Anonymous", "Confidential", "Top Secret"]
        var b6 = this.getSelectValuesSelApprovers();


        var z = 1;

        return data;
    }


    ,_jqXHR : undefined
    ,_useFileUpload: function($upload, $drop, $ul, $click) {
        $(function(){

            //var ul = $ul;

            $click.click(function(){
                // Simulate a click on the file input button
                // to show the file browser dialog
                $(this).parent().find('input').click();
            });

            // Initialize the jQuery File Upload plugin
            //jwu $('#upload').fileupload({
            _jqXHR = $upload.fileupload({
                //To Explore:
                //redirect : to taskList
                //redirectParamName:
                //
//check if taskId not created, create it first
//                submit: function (e, data) {
//                    var input = $('#input');
//                    data.formData = {example: input.val()};
//                    if (!data.formData.example) {
//                        data.context.find('button').prop('disabled', false);
//                        input.focus();
//                        return false;
//                    }
//                },
                done: function (e, data) {
                    var a1 = data.result
                    var a2 = data.textStatus;
                    var a3 = data.jqXHR;
                    var z = 1;
                    //alert("done");
                },
//                always: function (e, data) {
//                    // data.result
//                    // data.textStatus;
//                    // data.jqXHR;
//                },
                //autoUpload: false
                //sequentialUploads: true



                url: Acm.getContextPath() + TaskWizard.Service.API_UPLOAD_COMPLAINT_FILE,

                formData: function(form) {
                    var fd = [{}];
                    fd[0].name = "taskId";
                    fd[0].value = Task.getTaskId();
                    return fd;
                },

                // This element will accept file drag/drop uploading
                dropZone: $drop,

                // This function is called when a file is added to the queue;
                // either via the browse button, or via drag/drop:
                add: function (e, data) {

                    var tpl = $('<li class="working"><input type="text" value="0" data-width="48" data-height="48"'+
                        ' data-fgColor="#0788a5" data-readOnly="1" data-bgColor="#3e4043" /><p></p><span></span></li>');

                    // Append the file name and file size
                    tpl.find('p').text(data.files[0].name)
                        .append('<i>' + formatFileSize(data.files[0].size) + '</i>');

                    // Add the HTML to the UL element
                    data.context = tpl.appendTo($ul);

                    // Initialize the knob plugin
                    tpl.find('input').knob();

                    // Listen for clicks on the cancel icon
                    tpl.find('span').click(function(){

                        if(tpl.hasClass('working')){
                            //jwu jqXHR.abort();
                            _jqXHR.abort();
                        }

                        tpl.fadeOut(function(){
                            tpl.remove();
                        });

                    });

                    // Automatically upload the file once it is added to the queue
                    //var jqXHR = data.submit();
                    _jqXHR = data.submit();
                },

                progress: function(e, data){

                    // Calculate the completion percentage of the upload
                    var progress = parseInt(data.loaded / data.total * 100, 10);

                    // Update the hidden input field and trigger a change
                    // so that the jQuery knob plugin knows to update the dial
                    data.context.find('input').val(progress).change();

                    if(progress == 100){
                        data.context.removeClass('working');
                    }
                },

                fail:function(e, data){
                    // Something has gone wrong!
                    data.context.addClass('error');
                }

            });


            // Prevent the default action when a file is dropped on the window
            $(document).on('drop dragover', function (e) {
                e.preventDefault();
            });

            // Helper function that formats the file sizes
            function formatFileSize(bytes) {
                if (typeof bytes !== 'number') {
                    return '';
                }

                if (bytes >= 1000000000) {
                    return (bytes / 1000000000).toFixed(2) + ' GB';
                }

                if (bytes >= 1000000) {
                    return (bytes / 1000000).toFixed(2) + ' MB';
                }

                return (bytes / 1000).toFixed(2) + ' KB';
            }

        });
    }

    ,_toggleSubJTable: function($t, $row, fnOpen, fnClose, title) {
        var $childRow = $t.jtable('getChildRow', $row.closest('tr'));
        var curTitle = $childRow.find("div.jtable-title-text").text();

        var toClose;
        if ($t.jtable('isChildRowOpen', $row.closest('tr'))) {
            if (curTitle === title) {
                toClose = true;
            } else {
                toClose = false;
            }
        } else {
            toClose = false;
        }

        if (toClose) {
            fnClose($t, $row);
        } else {
            fnOpen($t, $row);
        }
    }

    //
    // Initiator ------------------
    //
    ,_createJTableInitiator: function($s) {
        $s.jtable({
            title: 'Initiator'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    var c = Task.getTask();
                    if (Acm.isEmpty(c.originator)) {
                        c = Task.constructTask();
                    }
                    var rc = {"Result": "OK", "Records": [{}]};
                    rc.Records[0].id = c.originator.id;
                    rc.Records[0].title = c.originator.title;
                    rc.Records[0].givenName = c.originator.givenName;
                    rc.Records[0].familyName = c.originator.familyName;
                    rc.Records[0].type = "";
                    rc.Records[0].description = "";
                    return rc;
//                    return {
//                        "Result": "OK"
//                        ,"Records": [
//                            { "personId":  1, "title": "Mr.", "firstName": "John", "lastName": "Garcia", "type": "Witness", "description": "123 do re mi" }
//                        ]
//                    };
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var c = Task.getTask();
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.id = c.originator.id = parseInt(record.id);
                    rc.Record.title = c.originator.title = record.title;
                    rc.Record.givenName = c.originator.givenName = record.givenName;
                    rc.Record.familyName = c.originator.familyName = record.familyName;
                    rc.Record.type = record.type;
                    rc.Record.description = record.description;
                    return rc;
//                    return {
//                        "Result": "OK"
//                        ,"Record":
//                        { "id": 3, "title": "Dr.", "givenName": "Joe", "familyName": "Lee", "type": "Witness", "description": "someone" }
//                    };
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
                ,subTables: {
                    title: 'Entities'
                    ,width: '10%'
                    ,sorting: false
                    ,edit: false
                    ,create: false
                    ,openChildAsAccordion: true
                    ,display: function (commData) {
                        var $a = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-phone'></i></a>");
                        var $b = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-book'></i></a>");
                        var $c = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-map-marker'></i></a>");
                        var $d = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-users'></i></a>");

                        $a.click(function (e) {
                            TaskWizard.Object._toggleInitiatorDevices($s, $a);
                            e.preventDefault();
                        });
                        $b.click(function (e) {
                            TaskWizard.Object._toggleInitiatorOrganizations($s, $b);
                            e.preventDefault();
                        });
                        $c.click(function (e) {
                            TaskWizard.Object._toggleInitiatorLocations($s, $c);
                            e.preventDefault();
                        });
                        $d.click(function (e) {
                            TaskWizard.Object._toggleInitiatorAliases($s, $d);
                            e.preventDefault();
                        });
                        return $a.add($b).add($c).add($d);
                    }
                }


                ,title: {
                    title: 'Title'
                    ,width: '10%'
                    ,options: Task.getPersonTitles()
                }
                ,givenName: {
                    title: 'First Name'
                    ,width: '15%'
                }
                ,familyName: {
                    title: 'Last Name'
                    ,width: '15%'
                }
                ,type: {
                    title: 'Type'
                    //,options: Acm.getContextPath() + '/api/latest/plugin/task/types'
                    ,options: Task.getPersonTypes()
                }
                ,description: {
                    title: 'Description'
                    ,width: '30%'
                }
            }
            ,recordAdded: function(event, data){
                $s.jtable('load');
            }
            ,recordUpdated: function(event, data){
                $s.jtable('load');
            }
        });

        $s.jtable('load');
    }
    ,_toggleInitiatorDevices: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorDevices, this._closeInitiatorDevices, Task.PERSON_SUBTABLE_TITLE_DEVICES);
    }
    ,_toggleInitiatorOrganizations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorOrganizations, this._closeInitiatorOrganizations, Task.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
    }
    ,_toggleInitiatorLocations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorLocations, this._closeInitiatorLocations, Task.PERSON_SUBTABLE_TITLE_LOCATIONS);
    }
    ,_toggleInitiatorAliases: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorAliases, this._closeInitiatorAliases, Task.PERSON_SUBTABLE_TITLE_ALIASES);
    }
    ,_closeInitiatorDevices: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorDevices: function($t, $row) {
        $t.jtable('openChildTable'
            ,$row.closest('tr')
            ,{
                title: Task.PERSON_SUBTABLE_TITLE_DEVICES
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var c = Task.getTask();
                        var contactMethods = c.originator.contactMethods;
                        var cnt = contactMethods.length;;

                        var rc = {"Result": "OK", "Records": []};
                        for (i = 0; i < cnt; i++) {
                            rc.Records.push({personId: c.originator.id
                                ,id: contactMethods[i].id
                                ,type: contactMethods[i].type
                                ,value: contactMethods[i].value
                                ,created: contactMethods[i].created
                                ,creator: contactMethods[i].creator
                            });
                        }
                        return rc;
//                        return {
//                            "Result": "OK"
//                            ,"Records": [
//                                { "personId":  1, "id": "a", "type": "Phone", "value": "703-123-5678", "created": "01-02-03", "creator": "123 do re mi" }
//                                ,{ "personId": 2, "id": "b", "type": "Email", "value": "doe@gmail.com", "created": "14-05-15", "creator": "xyz abc" }
//                            ]
//                            //,"TotalRecordCount": 2
//                        };

                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var c = Task.getTask();
                        var rc = {"Result": "OK", "Record": {}};
                        rc.Record.personId = c.originator.id;
                        rc.Record.id = parseInt(record.id);
                        rc.Record.type = record.type;
                        rc.Record.value = record.value;
                        rc.Record.created = record.created;
                        rc.Record.creator = record.creator;
                        return rc;
//                        return {
//                            "Result": "OK"
//                            ,"Record":
//                            { "personId": 3, "id": "c", "type": "Phone", "value": "703-123-9999", "created": "01-02-03", "creator": "test" }
//                        };
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var c = Task.getTask();
                        var rc = {"Result": "OK", "Record": {}};
                        rc.Record.personId = c.originator.id;
                        //rc.Record.id = parseInt(record.id);           //no such field in postData, ignored
                        rc.Record.type = record.type;
                        rc.Record.value = record.value;
                        rc.Record.created = record.created;
                        rc.Record.creator = record.creator;
                        return rc;
//                        return {
//                            "Result": "OK"
//                            ,"Record":
//                            { "personId": 3, "id": "c", "type": "Phone", "value": "703-123-9999", "created": "01-02-03", "creator": "test" }
//                        };

                    }
                    ,deleteAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }
                ,fields: {
                    personId: {
                        key: false
                        ,create: false
                        ,edit: false
                        ,list: false
                    }
                    ,id: {
                        key: false
                        ,type: 'hidden'
                        ,edit: false
                        ,defaultValue: 0
                    }
                    ,type: {
                        title: 'Type'
                        ,width: '15%'
                        ,options: Task.getDeviceTypes()
                    }
                    ,value: {
                        title: 'Value'
                        ,width: '30%'
                    }
                    ,created: {
                        title: 'Date Added'
                        ,width: '20%'
                        //,type: 'date'
                        //,displayFormat: 'yy-mm-dd'
                    }
                    ,creator: {
                        title: 'Added By'
                        ,width: '30%'
                    }
                }
                ,recordAdded : function (event, data) {
                    var record = data.record;
                    var c = Task.getTask();
                    var contactMethods = c.originator.contactMethods;
                    var contactMethod = {};
                    contactMethod.personId = c.originator.id;
                    contactMethod.id = parseInt(record.id);
                    contactMethod.type = record.type;
                    contactMethod.value = record.value;
                    contactMethod.created = record.created;
                    contactMethod.creator = record.creator;
                    contactMethods.push(contactMethod);
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var c = Task.getTask();
                    var contactMethods = c.originator.contactMethods;
                    var contactMethod = contactMethods[whichRow];
                    contactMethod.type = record.type;
                    contactMethod.value = record.value;
                    contactMethod.created = record.created;
                    contactMethod.creator = record.creator;
                }
                ,recordDeleted : function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var c = Task.getTask();
                    var contactMethods = c.originator.contactMethods;
                    contactMethods.splice(whichRow, 1);
                }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closeInitiatorOrganizations: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorOrganizations: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Task.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "com", "value": "ABC, Inc.", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "gov", "value": "IRS", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "com", "value": "ABC, Inc.", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "gov", "value": "IRS", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,deleteAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                    };
                }
            }
                ,fields: {
                personId: {
                    type: 'hidden'
                    ,defaultValue: 1 //commData.record.StudentId
                }
                ,id: {
                    key: true
                    ,create: false
                    ,edit: false
                    ,list: false
                }
                ,type: {
                    title: 'Type'
                    ,width: '15%'
                    ,options: Task.getOrganizationTypes()
                }
                ,value: {
                    title: 'Value'
                    ,width: '30%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '20%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closeInitiatorLocations: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorLocations: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Task.PERSON_SUBTABLE_TITLE_LOCATIONS
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "Home", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "Office", "address": "999 Fairfax Blvd #201, Fairfax, VA 22030", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Home", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Hotel", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,deleteAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                    };
                }
            }
                ,fields: {
                personId: {
                    type: 'hidden'
                    ,defaultValue: 1 //commData.record.StudentId
                }
                ,id: {
                    key: true
                    ,create: false
                    ,edit: false
                    ,list: false
                }
                ,type: {
                    title: 'Type'
                    ,width: '8%'
                    ,options: Task.getLocationTypes()
                }
                ,address: {
                    title: 'Address'
                    ,width: '30%'
                }
                ,city: {
                    title: 'City'
                    ,width: '12%'
                }
                ,state: {
                    title: 'State'
                    ,width: '5%'
                }
                ,zip: {
                    title: 'Zip'
                    ,width: '8%'
                }
                ,country: {
                    title: 'Country'
                    ,width: '8%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '15%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closeInitiatorAliases: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorAliases: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Task.PERSON_SUBTABLE_TITLE_ALIASES
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "Nick Name", "value": "JJ", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "Some Name", "value": "Ice Man", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Nick Name", "value": "Ice Man", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Nick Name", "value": "Big Man", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,deleteAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                    };
                }
            }
                ,fields: {
                personId: {
                    type: 'hidden'
                    ,defaultValue: 1 //commData.record.StudentId
                }
                ,id: {
                    key: true
                    ,create: false
                    ,edit: false
                    ,list: false
                }
                ,type: {
                    title: 'Type'
                    ,width: '15%'
                    ,options: Task.getAliasTypes()
                }
                ,value: {
                    title: 'Value'
                    ,width: '30%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '20%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }

    //
    // People ------------------
    //
    ,_togglePeopleDevices: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPeopleDevices, this._closePeopleDevices, Task.PERSON_SUBTABLE_TITLE_DEVICES);
    }
    ,_togglePeopleOrganizations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPeopleOrganizations, this._closePeopleOrganizations, Task.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
    }
    ,_togglePeopleLocations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPeopleLocations, this._closePeopleLocations, Task.PERSON_SUBTABLE_TITLE_LOCATIONS);
    }
    ,_togglePeopleAliases: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPeopleAliases, this._closePeopleAliases, Task.PERSON_SUBTABLE_TITLE_ALIASES);
    }

    ,_createJTablePeople: function($s) {
        $s.jtable({
            title: 'People'
            ,selecting: true
            ,paging: true
            ,pageSize: 10
            ,sorting: true
            ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "title": "Mr.", "firstName": "John", "lastName": "Garcia", "type": "Witness", "description": "123 do re mi" }
                            ,{ "personId": 2, "title": "Ms.", "firstName": "Jane", "lastName": "Doe", "type": "Subject", "description": "xyz abc" }
                        ]
                        ,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "title": "Dr.", "firstName": "Joe", "lastName": "Lee", "type": "Witness", "description": "someone" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "title": "Dr.", "firstName": "Joe", "lastName": "Lee", "type": "Witness", "description": "someone" }
                    };
                }
                ,deleteAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                    };
                }
            }
            ,fields: {
                personId: {
                    title: 'ID'
                    ,key: true
                    ,list: false
                    ,create: false
                    ,edit: false
                }
                ,subTables: {
                    title: 'Entities'
                    ,width: '10%'
                    ,sorting: false
                    ,edit: false
                    ,create: false
                    ,openChildAsAccordion: true
                    ,display: function (commData) {
                        var $a = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-phone'></i></a>");
                        var $b = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-book'></i></a>");
                        var $c = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-map-marker'></i></a>");
                        var $d = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-users'></i></a>");

                        $a.click(function (e) {
                            TaskWizard.Object._togglePeopleDevices($s, $a);
                            e.preventDefault();
                        });
                        $b.click(function (e) {
                            TaskWizard.Object._togglePeopleOrganizations($s, $b);
                            e.preventDefault();
                        });
                        $c.click(function (e) {
                            TaskWizard.Object._togglePeopleLocations($s, $c);
                            e.preventDefault();
                        });
                        $d.click(function (e) {
                            TaskWizard.Object._togglePeopleAliases($s, $d);
                            e.preventDefault();
                        });
                        return $a.add($b).add($c).add($d);
                    }
                }


                ,title: {
                    title: 'Title'
                    ,width: '10%'
                    ,options: Task.getPersonTitles()
                }
                ,firstName: {
                    title: 'First Name'
                    ,width: '15%'
                }
                ,lastName: {
                    title: 'Last Name'
                    ,width: '15%'
                }
                ,type: {
                    title: 'Type'
                    //,options: Acm.getContextPath() + '/api/latest/plugin/task/types'
                    ,options: Task.getPersonTypes()
                }
                ,description: {
                    title: 'Description'
                    ,width: '30%'
                }
            }
            ,recordAdded: function(event, data){
                $s.jtable('load');
            }
            ,recordUpdated: function(event, data){
                $s.jtable('load');
            }
        });

        $s.jtable('load');
    }
    ,_closePeopleDevices: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPeopleDevices: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Task.PERSON_SUBTABLE_TITLE_DEVICES
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "Phone", "value": "703-123-5678", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "Email", "value": "doe@gmail.com", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Phone", "value": "703-123-9999", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Phone", "value": "703-123-9999", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,deleteAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                    };
                }
            }
                ,fields: {
                personId: {
                    type: 'hidden'
                    ,defaultValue: 1 //commData.record.StudentId
                }
                ,id: {
                    key: true
                    ,create: false
                    ,edit: false
                    ,list: false
                }
                ,type: {
                    title: 'Type'
                    ,width: '15%'
                    ,options: Task.getDeviceTypes()
                }
                ,value: {
                    title: 'Value'
                    ,width: '30%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '20%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closePeopleOrganizations: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPeopleOrganizations: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Task.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                            ,"Records": [
                                { "personId":  1, "id": "a", "type": "com", "value": "ABC, Inc.", "createDate": "01-02-03", "createBy": "123 do re mi" }
                                ,{ "personId": 2, "id": "b", "type": "gov", "value": "IRS", "createDate": "14-05-15", "createBy": "xyz abc" }
                            ]
                            //,"TotalRecordCount": 2
                        };
                    }
                    ,createAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                            ,"Record":
                            { "personId": 3, "id": "c", "type": "com", "value": "ABC, Inc.", "createDate": "01-02-03", "createBy": "test" }
                        };
                    }
                    ,updateAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                            ,"Record":
                            { "personId": 3, "id": "c", "type": "gov", "value": "IRS", "createDate": "01-02-03", "createBy": "test" }
                        };
                    }
                    ,deleteAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }
                ,fields: {
                personId: {
                    type: 'hidden'
                    ,defaultValue: 1 //commData.record.StudentId
                }
                ,id: {
                    key: true
                    ,create: false
                    ,edit: false
                    ,list: false
                }
                ,type: {
                    title: 'Type'
                    ,width: '15%'
                    ,options: Task.getOrganizationTypes()
                }
                ,value: {
                    title: 'Value'
                    ,width: '30%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '20%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closePeopleLocations: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPeopleLocations: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Task.PERSON_SUBTABLE_TITLE_LOCATIONS
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                            ,"Records": [
                                { "personId":  1, "id": "a", "type": "Home", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "123 do re mi" }
                                ,{ "personId": 2, "id": "b", "type": "Office", "address": "999 Fairfax Blvd #201, Fairfax, VA 22030", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "14-05-15", "createBy": "xyz abc" }
                            ]
                            //,"TotalRecordCount": 2
                        };
                    }
                    ,createAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                            ,"Record":
                            { "personId": 3, "id": "c", "type": "Home", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "test" }
                        };
                    }
                    ,updateAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                            ,"Record":
                            { "personId": 3, "id": "c", "type": "Hotel", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "test" }
                        };
                    }
                    ,deleteAction: function(postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }
                ,fields: {
                personId: {
                    type: 'hidden'
                    ,defaultValue: 1 //commData.record.StudentId
                }
                ,id: {
                    key: true
                    ,create: false
                    ,edit: false
                    ,list: false
                }
                ,type: {
                    title: 'Type'
                    ,width: '8%'
                    ,options: Task.getLocationTypes()
                }
                ,address: {
                    title: 'Address'
                    ,width: '30%'
                }
                ,city: {
                    title: 'City'
                    ,width: '12%'
                }
                ,state: {
                    title: 'State'
                    ,width: '5%'
                }
                ,zip: {
                    title: 'Zip'
                    ,width: '8%'
                }
                ,country: {
                    title: 'Country'
                    ,width: '8%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '15%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closePeopleAliases: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPeopleAliases: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Task.PERSON_SUBTABLE_TITLE_ALIASES
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "Nick Name", "value": "JJ", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "Some Name", "value": "Ice Man", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Nick Name", "value": "Ice Man", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Nick Name", "value": "Big Man", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,deleteAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                    };
                }
            }
                ,fields: {
                personId: {
                    type: 'hidden'
                    ,defaultValue: 1 //commData.record.StudentId
                }
                ,id: {
                    key: true
                    ,create: false
                    ,edit: false
                    ,list: false
                }
                ,type: {
                    title: 'Type'
                    ,width: '15%'
                    ,options: Task.getAliasTypes()
                }
                ,value: {
                    title: 'Value'
                    ,width: '30%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '20%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }


    //
    // Assignment-Communication Devices ------------------
    //
    ,_createJTableDevices: function($s) {
        $s.jtable({
            title: Task.PERSON_SUBTABLE_TITLE_DEVICES
            ,selecting: true
            ,paging: true
            ,pageSize: 10
            ,sorting: true
            ,actions: {
                listAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Records": [
                            { "id": "a", "type": "Phone", "value": "703-123-5678", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "id": "b", "type": "Email", "value": "doe@gmail.com", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };
                }
                ,createAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "id": "c", "type": "Phone", "value": "703-123-9999", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,updateAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                        ,"Record":
                        { "id": "c", "type": "Phone", "value": "703-123-9999", "createDate": "01-02-03", "createBy": "test" }
                    };
                }
                ,deleteAction: function(postData, jtParams) {
                    return {
                        "Result": "OK"
                    };
                }
            }
            ,fields: {
                id: {
                    key: true
                    ,create: false
                    ,edit: false
                    ,list: false
                }
                ,type: {
                    title: 'Type'
                    ,width: '15%'
                    ,options: Task.getDeviceTypes()
                }
                ,value: {
                    title: 'Value'
                    ,width: '30%'
                }
                ,createDate: {
                    title: 'Date Added'
                    ,width: '20%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,createBy: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            ,recordAdded: function(event, data){
                $s.jtable('load');
            }
            ,recordUpdated: function(event, data){
                $s.jtable('load');
            }
        });

        $s.jtable('load');
    }

////////////////////////////////////////////////////////////////////////////////////////










    ,usePagination: function(totalItems, itemsPerPage, callback) {
        Acm.Object.usePagination(this.$jPaginate, totalItems, itemsPerPage, callback)
    }
    ,setEnableBtnPrint: function(enable) {
        Acm.Object.setEnable(this.$btnPrint, enable);
    }
    ,setEnableBtnAssign: function(enable) {
        Acm.Object.setEnable(this.$btnAssign, enable);
    }
    ,setEnableBtnDelete: function(enable) {
        Acm.Object.setEnable(this.$btnDelete, enable);
    }
    ,showBtnPrint: function(show) {
        Acm.Object.show(this.$btnPrint, show);
    }
    ,setEnableBtnFingerPrint: function(enable) {
        Acm.Object.setEnable(this.$btnFingerPrint, enable);
    }
    ,showBtnFingerPrint: function(show) {
        Acm.Object.show(this.$btnFingerPrint, show);
    }
    ,showBtnAssign: function(show) {
        Acm.Object.show(this.$btnAssign, show);
    }
    ,showBtnDelete: function(show) {
        Acm.Object.show(this.$btnDelete, show);
    }
    ,showLnkNextItems: function() {
        alert("showLnkNextItem depleted");
//        var checked = TaskWizard.Object.isCheckedChkQa();
//        TaskWizard.Object.showLnkNextItemQa(checked);
//        checked = TaskWizard.Object.isCheckedChkMailback();
//        TaskWizard.Object.showLnkNextItemMb(checked);
    }
    ,showLnkNextItemQa: function(show) {
        Acm.Object.showParent(this.$lnkNextItemQa, show);
    }
    ,showLnkNextItemMb: function(show) {
        Acm.Object.showParent(this.$lnkNextItemMb, show);
    }
	,putUnassignedDoc: function(unassignedTaskId, unassignedDoc) {
        var selected = false;
        var prevUnassignedDoc = TaskWizard.Object.getUnassignedDoc(unassignedTaskId);
        if (prevUnassignedDoc) {
            selected = prevUnassignedDoc.selected;
        }
        unassignedDoc.selected = selected;
		window.unassignedDocs[unassignedTaskId] = unassignedDoc;
	}
	,getUnassignedDoc: function(unassignedTaskId) {
		return window.unassignedDocs[unassignedTaskId];
	}
	,getUnassignedDocs: function() {
		return window.unassignedDocs;
	}
    ,resetUnassignedDocs: function() {
        window.unassignedDocs = {};
    }
    ,getSelectedDocs : function(e) {
        var selectedDocs = {};
        var unassignedDocs = TaskWizard.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = TaskWizard.Object.getUnassignedDoc(unassignedTaskId);
            if (unassignedDoc) {
                if (unassignedDoc.selected) {
                    selectedDocs[unassignedTaskId] = unassignedDoc;
                }
            }
        }
        return selectedDocs;
    }
    ,getSelectedTaskIds : function(e) {
        var taskIds = [];
        var unassignedDocs = TaskWizard.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = TaskWizard.Object.getUnassignedDoc(unassignedTaskId);
            if (unassignedDoc) {
                if (unassignedDoc.selected) {
                    taskIds.push(unassignedTaskId);
                }
            }
        }
        return taskIds;
    }
	,setCheckChkSelectOne : function(checked) {
		jQuery("input[type=checkbox]" + TaskWizard.Object.clsChkSelectOne).each(function(i, item) {
			item.checked = checked;
            TaskWizard.Object.selectUnassignedDoc(item);
		});
	}
	,regClickChkSelectOne: function() {
		jQuery("input[type=checkbox]" + TaskWizard.Object.clsChkSelectOne).click(function() {
            TaskWizard.Callbacks.onClickChkSelectOne(this);
        });
	}
	,getTaskIdClicked: function(checkBox) {
		return jQuery(checkBox).next().val();
	}
    ,selectUnassignedDoc : function(checkBox) {
        var unassignedTaskId = TaskWizard.Object.getTaskIdClicked(checkBox);
        var unassignedDoc  = TaskWizard.Object.getUnassignedDoc(unassignedTaskId);
        if (unassignedDoc) {
            unassignedDoc.selected = checkBox.checked;
        }
    }
	,getContextPath: function() {
        return TaskWizard.contextPath;
	}
    ,getTicket: function() {
        return TaskWizard.ticket;
    }
    ,showLabAssigneeProgress: function(show) {
    	Acm.Object.show(this.$labAssigneeProgress, show);
	}
    ,setTextLabTotalCount: function(value) {
    	Acm.Object.setText(this.$labTotalCount, value);
    }
    ,setHtmlDivResultList : function(html) {
    	Acm.Object.setHtml(this.$divResultList, html);
    }
    ,setHtmlDivResultGrid: function(html) {
    	Acm.Object.setHtml(this.$divResultGrid, html);
    }
    ,emptySelAssignee: function() {
    	Acm.Object.empty(this.$selAssignee);
    }
    ,setHtmlSelAssignee : function(html) {
    	Acm.Object.setHtml(this.$selAssignee, html);
    }
    ,emptySelWidAssignee: function() {
        Acm.Object.empty(this.$selWidAssignee);
    }
    ,setHtmlSelWidAssignee : function(html) {
        Acm.Object.setHtml(this.$selWidAssignee, html);
    }
    ,getValueSelWidAssignee: function() {
        return Acm.Object.getSelectValue(this.$selWidAssignee);
    }
    ,showSelWidAssignee: function(show) {
        return Acm.Object.show(this.$selWidAssignee, show);
    }
    ,getTextDivAssigneeSelf: function() {
        return Acm.Object.getTextNodeText(this.$divAssigneeSelf);
    }
    ,setTextDivAssigneeSelf: function() {
        Acm.Object.setTextNodeText(this.$divAssigneeSelf);
    }
    ,showDivAssigneeSelf: function(show) {
        Acm.Object.show(this.$divAssigneeSelf, show);
    }
    ,isVisibleDivAssigneeSelf: function() {
        return Acm.Object.isVisible(this.$divAssigneeSelf);
    }
    ,getWidAssign: function() {
	    return this.$widAssign;
	}
    ,getValueTxtAssignComment: function() {
        return Acm.Object.getValue(this.$txtAssignComment);
    }
    ,setValueTxtAssignComment: function(value) {
        Acm.Object.setValue(this.$txtAssignComment, value);
    }
    ,showInvalidAssign: function(show) {
        Acm.Object.showParent(this.$spanInvalidAssign, show);
    }
    ,closeFrmWidAssign: function() {
        this.$btnWidAssignCancel.click();
    }
    ,clearFrmWidAssign: function() {
        TaskWizard.Object.setValueTxtAssignComment("");
        TaskWizard.Object.showInvalidAssign(false);
    }
    ,getWidDelete: function() {
    	return jQuery(this.idWidDelete);
	}
    ,getValueTxtDeleteComment: function() {
        return Acm.Object.getValue(jQuery(this.idTxtDeleteComment));
    }
    ,setValueTxtDeleteComment: function(value) {
        Acm.Object.setValue(this.$txtDeleteComment, value);
    }
    ,showInvalidDelete: function(show) {
        Acm.Object.showParent(this.$spanInvalidDelete, show);
    }
    ,closeFrmWidDelete: function() {
        jQuery(TaskWizard.Object.idBtnWidDeleteCancel).click();
    }
    ,clearFrmWidDelete: function() {
        TaskWizard.Object.setValueTxtDeleteComment("");
        TaskWizard.Object.showInvalidDelete(false);
    }
	,getWidFingerPrint: function() {
	    return this.$widFingerPrint;
	}
    ,getValueTxtFingerPrintName: function() {
        return Acm.Object.getValue(this.$txtFingerPrintName);
    }
    ,setValueTxtFingerPrintName: function(value) {
        Acm.Object.setValue(this.$txtFingerPrintName, value);
    }
    ,showInvalidFingerPrint: function(show) {
        Acm.Object.showParent(this.$spanInvalidFingerPrint, show);
    }
    ,closeFrmWidFingerPrint: function() {
        this.$btnWidFingerPrintCancel.click();
    }
    ,clearFrmWidFingerPrint: function() {
        TaskWizard.Object.setValueTxtFingerPrintName("");
        TaskWizard.Object.showInvalidFingerPrint(false);
    }

    ,showResultList: function() {
    	TaskWizard.Object._showDivResultList(true);
    	TaskWizard.Object._showDivResultGrid(false);
    	TaskWizard.Object._showLnkViewList(false);
    	TaskWizard.Object._showLnkViewGrid(true);
    }
    ,showResultGrid: function() {
    	TaskWizard.Object._showDivResultList(false);
    	TaskWizard.Object._showDivResultGrid(true);
    	TaskWizard.Object._showLnkViewList(true);
    	TaskWizard.Object._showLnkViewGrid(false);
    }
	,getSearchTerm : function() {
		var term = {};

		term.docType = TaskWizard.Object._getValueSelDocType();
		term.subjectLastName = TaskWizard.Object._getValueEdtLastName();
		term.subjectSSN = TaskWizard.Object._getValueEdtSsn();
		term.eqipRequestNumber = TaskWizard.Object._getValueEdtEQipRequest();
		term.soi = TaskWizard.Object._getValueEdtSoi();
		term.son = TaskWizard.Object._getValueEdtSon();
		term.assignee = TaskWizard.Object._getValueSelAssignee();

		term.supervisorReviewFlag = TaskWizard.Object._isCheckedChkSupervisorReview();
		term.contractOversightReviewFlag = TaskWizard.Object._isCheckedChkContractOversight();

		term.queues = [{},{},{}];
		term.queues[0].name = Unassigned.queueProcessing.name;
		term.queues[0].checked = TaskWizard.Object.isCheckedChkProcessing();
		term.queues[1].name = Unassigned.queueQa.name;
		term.queues[1].checked = TaskWizard.Object.isCheckedChkQa();
		term.queues[2].name = Unassigned.queueMailback.name;
		term.queues[2].checked = TaskWizard.Object.isCheckedChkMailback();

		return term;
	}
    ,clearSearchTerm : function() {
        TaskWizard.Object._setValueSelDocType("placeholder");
        TaskWizard.Object._setValueEdtLastName("");
        TaskWizard.Object._setValueEdtSsn("");
        TaskWizard.Object._setValueEdtEQipRequest("");
        TaskWizard.Object._setValueEdtSoi("");
        TaskWizard.Object._setValueEdtSon("");
        TaskWizard.Object._setValueSelAssignee("placeholder");

        TaskWizard.Object._setCheckedChkSupervisorReview(false);
        TaskWizard.Object._setCheckedChkContractOversight(false);

        TaskWizard.Object._setCheckedChkProcessing(false);
        TaskWizard.Object._setCheckedChkQa(true);
        TaskWizard.Object._setCheckedChkMailback(false);
    }

    ,getSelectedQueueNames : function () {
    	var queueNames = "";
    	if (TaskWizard.Object.isCheckedChkProcessing()) {
    		queueNames += "," + Unassigned.queueProcessing.name;
    	}
    	if (TaskWizard.Object.isCheckedChkQa()) {
    		queueNames += "," + Unassigned.queueQa.name;
    	}
    	if (TaskWizard.Object.isCheckedChkMailback()) {
    		queueNames += "," + Unassigned.queueMailback.name;
    	}

    	if (Acm.Common.isNotEmpty(queueNames)) {
    		queueNames = queueNames.substring(1, queueNames.length); //discard extra leading ','
    	}
    	return queueNames;
    }
//    ,getSelectedQueue : function () {
//        if (TaskWizard.Object.isCheckedChkProcessing()) {
//            return Unassigned.queueProcessing;
//        } else if (TaskWizard.Object.isCheckedChkQa()) {
//            return Unassigned.queueQa;
//        } else if (TaskWizard.Object.isCheckedChkMailback()) {
//            return Unassigned.queueMailback;
//        } else {
//            return null;
//        }
//    }

    ,_showLnkViewList: function(show) {
        Acm.Object.show(this.$lnkViewList).closest('li', show);
    }
    ,_showLnkViewGrid: function(show) {
        Acm.Object.show(this.$lnkViewGrid).closest('li', show);
    }
    ,_showDivResultList: function(show) {
        Acm.Object.show(this.$divResultList, show);
    }
    ,_showDivResultGrid: function(show) {
        Acm.Object.show(this.$divResultGrid, show);
    }
    ,_getValueSelDocType : function() {
        return Acm.Object.getSelectValue(this.$selDocType);
    }
    ,_setValueSelDocType : function(val) {
        Acm.Object.setSelectValue(this.$selDocType, val);
    }
    ,_getValueEdtLastName: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtLastName);
    }
    ,_setValueEdtLastName: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtLastName, val);
    }
    ,_getValueEdtSsn: function() {
    	var display = Acm.Object.getPlaceHolderInput(this.$edtSsn);
        return Acm.Object.getSsnValue(display);
    }
    ,_setValueEdtSsn: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtSsn, val);
    }
    ,_getValueEdtEQipRequest: function() {
    	return Acm.Object.getPlaceHolderInput(this.$edtEQipRequest);
    }
    ,_setValueEdtEQipRequest: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtEQipRequest, val);
    }
    ,_getValueEdtSoi: function() {
    	return Acm.Object.getPlaceHolderInput(this.$edtSoi);
    }
    ,_setValueEdtSoi: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtSoi, val);
    }
    ,_getValueEdtSon: function() {
    	return Acm.Object.getPlaceHolderInput(this.$edtSon);
    }
    ,_setValueEdtSon: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtSon, val);
    }
    ,_getValueSelAssignee: function() {
    	return Acm.Object.getSelectValue(this.$selAssignee);
    }
    ,_setValueSelAssignee : function(val) {
        Acm.Object.setSelectValue(this.$selAssignee, val);
    }
    ,_isCheckedChkSupervisorReview: function() {
    	return Acm.Object.isChecked(this.$chkSupervisorReview);
    }
    ,_setCheckedChkSupervisorReview: function(val) {
        Acm.Object.setChecked(this.$chkSupervisorReview, val);
    }
    ,_isCheckedChkContractOversight: function() {
    	return Acm.Object.isChecked(this.$chkContractOversight);
    }
    ,_setCheckedChkContractOversight: function(val) {
        Acm.Object.setChecked(this.$chkContractOversight, val);
    }
    ,isCheckedChkProcessing: function() {
    	return Acm.Object.isChecked(this.$chkProcessing);
    }
    ,_setCheckedChkProcessing: function(val) {
        Acm.Object.setChecked(this.$chkProcessing, val);
    }
    ,isCheckedChkQa: function() {
    	return Acm.Object.isChecked(this.$chkQa);
    }
    ,_setCheckedChkQa: function(val) {
        Acm.Object.setChecked(this.$chkQa, val);
    }
    ,isCheckedChkMailback: function() {
    	return Acm.Object.isChecked(this.$chkMailback);
    }
    ,_setCheckedChkMailback: function(val) {
        Acm.Object.setChecked(this.$chkMailback, val);
    }

};




