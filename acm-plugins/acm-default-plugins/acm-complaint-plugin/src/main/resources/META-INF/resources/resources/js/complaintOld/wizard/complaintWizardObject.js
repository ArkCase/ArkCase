/**
 * ComplaintWizard.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
ComplaintWizard.Object = {
    initialize : function() {
        this.$btnSave                = $("button[data-title='Save']");
        this.$btnSave.click(function(e) {ComplaintWizard.Event.onClickBtnSave(e);});

        this.$btnSubmit              = $("button[data-title='Submit']");
        this.$btnSubmit.click(function(e) {ComplaintWizard.Event.onClickBtnSubmit(e);});

        this.$h3Title = $("section>div>h3");

        this.$selIntiatorFlags       = $(".choose-intitiatorFlags");   //"#intiatorFlags"
        this.$selIntiatorFlags.chosen();

        this.$selComplaintFlags      = $(".choose-complaintFlags");    //"#intiatorFlags"
        this.$selComplaintFlags.chosen();

        this.$selApprovers           = $(".choose-approvers");         //"#approvers"
        //this.$selApprovers.chosen();

        this.$selCollab              = $(".choose-collab");            //""
        this.$selCollab.chosen();

        this.$selNotifications       = $(".choose-notifications");     //"#notifications"
        this.$selNotifications.chosen();

        this.$edtIncidentDate        = $("#incidentDate");
        this.setValueEdtIncidentDate(Acm.getCurrentDay());

        this.$edtComplaintTitle      = $("#edtComplaintTitle");
        this.$selComplaintType       = $("select[name='complaintType']");
        this.$selPriority            = $("select[name='priority']");

        this.$divInitiator           = $("#divInitiator");
        this._createJTableInitiator(this.$divInitiator);

        this.$divPeople              = $("#divPeople");
        this._createJTablePeople(this.$divPeople);

        this.$divDevices             = $("#divDevices");
        this._createJTableDevices(this.$divDevices);

        this.$uppload                = $('#upload');
        this.$upploadDrop            = $('#drop');
        this.$upploadList            = $('#upload ul');
        this.$upploadClick           = $('#drop a');
        this._useFileUpload(this.$uppload, this.$upploadDrop, this.$upploadList, this.$upploadClick);

        this.$divComplaintDetails    = $('.complaintDetails');
        this.$divComplaintDetails.summernote({
            height: 300
        });
    }

    ,getTextH3Title: function() {
        return Acm.Object.getText(this.$h3Title);
    }
    ,setTextH3Title: function(title) {
        Acm.Object.setText(this.$h3Title, title);
    }
    ,initApprovers: function(data) {
        $.each(data, function(idx, val) {
            Acm.Object.appendSelect(ComplaintWizard.Object.$selApprovers, val.userId, val.fullName);
        });
        this.$selApprovers.chosen();
    }
    ,initComplaintTypes: function(data) {
        $.each(data, function(idx, val) {
            Acm.Object.appendSelect(ComplaintWizard.Object.$selComplaintType, val, val);
        });
    }
    ,initPriorities: function(data) {
        $.each(data, function(idx, val) {
            Acm.Object.appendSelect(ComplaintWizard.Object.$selPriority, val, val);
        });
    }

    ,setEnableBtnSave: function(enable) {
        Acm.Object.setEnable(this.$btnSave, enable);
    }
    ,getHtmlDivComplaintDetails: function() {
        return Acm.Object.getSummernote(this.$divComplaintDetails);
    }
    ,getValueEdtIncidentDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtIncidentDate);
    }
    ,setValueEdtIncidentDate: function(val) {
        return Acm.Object.setPlaceHolderInput(this.$edtIncidentDate, val);
    }
    ,getValueEdtComplaintTitle: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtComplaintTitle);
    }
    ,getSelectValuesSelIntiatorFlags: function() {
        return Acm.Object.getSelectValues(this.$selIntiatorFlags);
    }
    ,getSelectValuesSelComplaintFlags: function() {
        return Acm.Object.getSelectValues(this.$selComplaintFlags);
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
        return Acm.Object.getSelectValueIgnoreFirst(this.$selPriority);
    }
    ,getSelectValueSelComplaintType: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selComplaintType);
    }

    ,setComplaintData : function(data) {
        var c = Complaint.getComplaint();
        //c.complaintTitle = data.complaintTitle;
        //c.priority = data.priority;
        //c.complaintType = data.complaintType;
        //c.details = data.details;
        //c.incidentDate = data.incidentDate;
        //c.approvers = data.approvers;

        c.complaintId = data.complaintId;
        c.complaintNumber = data.complaintNumber;
        c.created = data.created;
        c.creator = data.creator;
        c.modified = data.modified;
        c.modifier = data.modifier;
        c.status = data.status;

        c.originator.id = data.originator.id;
        //c.originator.title = data.originator.title;
        //c.originator.givenName = data.originator.givenName;
        //c.originator.familyName = data.originator.familyName;
        c.originator.company = data.originator.company;
        c.originator.created = data.originator.created;
        c.originator.creator = data.originator.creator;
        c.originator.modified = data.originator.modified;
        c.originator.modifier = data.originator.modifier;
        c.originator.status = data.originator.status;
        c.originator.securityTags = data.originator.securityTags;   //[]

        c.originator.addresses = data.originator.addresses;
        c.originator.contactMethods = data.originator.contactMethods; //=devices[]

//        c.originator.aliases[] = data.originator.aliases;
//        c.originator.organizations[] = data.originator.organizations;
//        c.originator.ecmFolderId[] = data.originator.ecmFolderId;
//        c.originator.ecmFolderPath[] = data.originator.ecmFolderPath;

//        organizations
//        aliases

        c.childObjects = data.childObjects
        c.ecmFolderId = data.ecmFolderId
        c.ecmFolderPath = data.ecmFolderPath

    }

    //convert date format from "dd-MM-yyyy" to "yyyy-MM-dd")
//    ,_dateFmtDmy2Ymd: function(dmy) {
//        if (Acm.isEmpty(dmy)) {
//            return null;
//        }
//
//        var arr = dmy.split('-');
//        if (3 != arr.length) {
//            return null;
//        }
//
//        var ymd = arr[2] + "-" + arr[1] + "-" + arr[0];
//        return ymd;
//    }
    ,getComplaintData : function() {
        var c = Complaint.getComplaint();

        var data = {};
        data.complaintTitle = this.getValueEdtComplaintTitle();
        data.priority = this.getSelectValueSelPriority();
        data.complaintType = this.getSelectValueSelComplaintType();
        data.details = this.getHtmlDivComplaintDetails();
        data.incidentDate = this.getValueEdtIncidentDate();

        data.complaintId = c.complaintId;
        data.complaintNumber = c.complaintNumber;
        data.created = c.created;
        data.creator = c.creator;
        data.modified = c.modified;
        data.modifier = c.modifier;
        data.status = c.status;

        data.originator = {};
        data.originator.id = c.originator.id;
        data.originator.title = c.originator.title;
        data.originator.givenName = c.originator.givenName;
        data.originator.familyName = c.originator.familyName;

        data.originator.company = c.originator.company;
        data.originator.created = c.originator.created;
        data.originator.creator = c.originator.creator;
        data.originator.modified = c.originator.modified;
        data.originator.modifier = c.originator.modifier;
        data.originator.status = c.originator.status;

//        data.originator.addresses = c.originator.addresses;
        data.originator.contactMethods = c.originator.contactMethods;

//        organizations
//        aliases

        data.originator.securityTags = c.originator.securityTags;   //[], "securityTags": ["Anonymous", "Confidential", "Top Secret"]
        data.approvers = this.getSelectValuesSelApprovers();

        return data;
    }


    ,_jqXHR : undefined
    ,_useFileUpload: function($upload, $drop, $ul, $click) {
        $(function(){
            $click.click(function(){
                // Simulate a click on the file input button
                // to show the file browser dialog
                $(this).parent().find('input').click();
            });

            // Initialize the jQuery File Upload plugin
            _jqXHR = $upload.fileupload({
                url: App.getContextPath() + ComplaintWizard.Service.API_UPLOAD_COMPLAINT_FILE
                ,dropZone: $drop

                ,done: function (e, data) {
                    var a1 = data.result
                    var a2 = data.textStatus;
                    var a3 = data.jqXHR;
                    var z = 1;
                    //alert("done");
                }

                ,formData: function(form) {
                    var fd = [{}];
                    fd[0].name = "complaintId";
                    fd[0].value = Complaint.getComplaintId();
                    return fd;
                }

                // This function is called when a file is added to the queue;
                // either via the browse button, or via drag/drop:
                ,add: function (e, data) {

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
                            _jqXHR.abort();
                        }

                        tpl.fadeOut(function(){
                            tpl.remove();
                        });

                    });

                    // Automatically upload the file once it is added to the queue
                    _jqXHR = data.submit();
                }

                ,progress: function(e, data){
                    // Calculate the completion percentage of the upload
                    var progress = parseInt(data.loaded / data.total * 100, 10);

                    // Update the hidden input field and trigger a change
                    // so that the jQuery knob plugin knows to update the dial
                    data.context.find('input').val(progress).change();

                    if(progress == 100){
                        data.context.removeClass('working');
                    }
                }

                ,fail:function(e, data){
                    // Something has gone wrong!
                    data.context.addClass('error');
                }


//To Explore:
                //redirect : to complaintList
                //redirectParamName:
                //autoUpload: false
                //sequentialUploads: true
//
//check if complaintId not created, create it first
//                ,submit: function (e, data) {
//                    var input = $('#input');
//                    data.formData = {example: input.val()};
//                    if (!data.formData.example) {
//                        data.context.find('button').prop('disabled', false);
//                        input.focus();
//                        return false;
//                    }
//                }
//                ,always: function (e, data) {
//                    // data.result
//                    // data.textStatus;
//                    // data.jqXHR;
//                }

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
                    var c = Complaint.getComplaint();
                    if (Acm.isEmpty(c.originator)) {
                        c = Complaint.constructComplaint();
                    }
                    var rc = {"Result": "OK", "Records": [{}]};
                    rc.Records[0].id = c.originator.id;
                    rc.Records[0].title = c.originator.title;
                    rc.Records[0].givenName = c.originator.givenName;
                    rc.Records[0].familyName = c.originator.familyName;
                    rc.Records[0].type = "";
                    rc.Records[0].description = "";
                    //rc.Records[0].description = "first init line\r\nsecond init line";
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
                    var c = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.id = c.originator.id;    // (record.id) is empty, do not assign;
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
                            ComplaintWizard.Object._toggleInitiatorDevices($s, $a);
                            e.preventDefault();
                        });
                        $b.click(function (e) {
                            ComplaintWizard.Object._toggleInitiatorOrganizations($s, $b);
                            e.preventDefault();
                        });
                        $c.click(function (e) {
                            ComplaintWizard.Object._toggleInitiatorLocations($s, $c);
                            e.preventDefault();
                        });
                        $d.click(function (e) {
                            ComplaintWizard.Object._toggleInitiatorAliases($s, $d);
                            e.preventDefault();
                        });
                        return $a.add($b).add($c).add($d);
                    }
                }


                ,title: {
                    title: 'Title'
                    ,width: '10%'
                    ,options: Complaint.getPersonTitles()
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
                    //,options: App.getContextPath() + '/api/latest/plugin/complaint/types'
                    ,options: Complaint.getPersonTypes()
                }
                ,description: {
                    title: 'Description'
                    ,type: 'textarea'
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
        this._toggleSubJTable($t, $row, this._openInitiatorDevices, this._closeInitiatorDevices, Complaint.PERSON_SUBTABLE_TITLE_DEVICES);
    }
    ,_toggleInitiatorOrganizations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorOrganizations, this._closeInitiatorOrganizations, Complaint.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
    }
    ,_toggleInitiatorLocations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorLocations, this._closeInitiatorLocations, Complaint.PERSON_SUBTABLE_TITLE_LOCATIONS);
    }
    ,_toggleInitiatorAliases: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openInitiatorAliases, this._closeInitiatorAliases, Complaint.PERSON_SUBTABLE_TITLE_ALIASES);
    }
    ,_closeInitiatorDevices: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorDevices: function($t, $row) {
        $t.jtable('openChildTable'
            ,$row.closest('tr')
            ,{
                title: Complaint.PERSON_SUBTABLE_TITLE_DEVICES
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var c = Complaint.getComplaint();
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
                        var c = Complaint.getComplaint();
                        var rc = {"Result": "OK", "Record": {}};
                        rc.Record.personId = c.originator.id;
                        rc.Record.id = parseInt(record.id);
                        rc.Record.type = record.type;
                        rc.Record.value = record.value;
                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                        rc.Record.creator = App.getUserName();   //record.creator;
                        return rc;
//                        return {
//                            "Result": "OK"
//                            ,"Record":
//                            { "personId": 3, "id": "c", "type": "Phone", "value": "703-123-9999", "created": "01-02-03", "creator": "test" }
//                        };
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var c = Complaint.getComplaint();
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
                        ,options: Complaint.getDeviceTypes()
                    }
                    ,value: {
                        title: 'Value'
                        ,width: '30%'
                    }
                    ,created: {
                        title: 'Date Added'
                        ,width: '20%'
                        ,create: false
                        ,edit: false
                        //,type: 'date'
                        //,displayFormat: 'yy-mm-dd'
                    }
                    ,creator: {
                        title: 'Added By'
                        ,width: '30%'
                        ,create: false
                        ,edit: false
                    }
                }
                ,recordAdded : function (event, data) {
                    var record = data.record;
                    var c = Complaint.getComplaint();
                    var contactMethods = c.originator.contactMethods;
                    var contactMethod = {};
                    contactMethod.id = parseInt(record.id);
                    contactMethod.type = record.type;
                    contactMethod.value = record.value;
                    //contactMethod.created = record.created;   //created,creator is readonly
                    //contactMethod.creator = record.creator;
                    contactMethods.push(contactMethod);
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var c = Complaint.getComplaint();
                    var contactMethods = c.originator.contactMethods;
                    var contactMethod = contactMethods[whichRow];
                    contactMethod.type = record.type;
                    contactMethod.value = record.value;
                    //contactMethod.created = record.created;   //created,creator is readonly
                    //contactMethod.creator = record.creator;
                }
                ,recordDeleted : function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var c = Complaint.getComplaint();
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
                title: Complaint.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
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
                    ,options: Complaint.getOrganizationTypes()
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
                title: Complaint.PERSON_SUBTABLE_TITLE_LOCATIONS
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
                    ,options: Complaint.getLocationTypes()
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
                title: Complaint.PERSON_SUBTABLE_TITLE_ALIASES
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
                    ,options: Complaint.getAliasTypes()
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
        this._toggleSubJTable($t, $row, this._openPeopleDevices, this._closePeopleDevices, Complaint.PERSON_SUBTABLE_TITLE_DEVICES);
    }
    ,_togglePeopleOrganizations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPeopleOrganizations, this._closePeopleOrganizations, Complaint.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
    }
    ,_togglePeopleLocations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPeopleLocations, this._closePeopleLocations, Complaint.PERSON_SUBTABLE_TITLE_LOCATIONS);
    }
    ,_togglePeopleAliases: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPeopleAliases, this._closePeopleAliases, Complaint.PERSON_SUBTABLE_TITLE_ALIASES);
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
                            ComplaintWizard.Object._togglePeopleDevices($s, $a);
                            e.preventDefault();
                        });
                        $b.click(function (e) {
                            ComplaintWizard.Object._togglePeopleOrganizations($s, $b);
                            e.preventDefault();
                        });
                        $c.click(function (e) {
                            ComplaintWizard.Object._togglePeopleLocations($s, $c);
                            e.preventDefault();
                        });
                        $d.click(function (e) {
                            ComplaintWizard.Object._togglePeopleAliases($s, $d);
                            e.preventDefault();
                        });
                        return $a.add($b).add($c).add($d);
                    }
                }


                ,title: {
                    title: 'Title'
                    ,width: '10%'
                    ,options: Complaint.getPersonTitles()
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
                    //,options: App.getContextPath() + '/api/latest/plugin/complaint/types'
                    ,options: Complaint.getPersonTypes()
                }
                ,description: {
                    title: 'Description'
                    ,type: 'textarea'
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
                title: Complaint.PERSON_SUBTABLE_TITLE_DEVICES
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
                    ,options: Complaint.getDeviceTypes()
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
                title: Complaint.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
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
                    ,options: Complaint.getOrganizationTypes()
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
                title: Complaint.PERSON_SUBTABLE_TITLE_LOCATIONS
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
                    ,options: Complaint.getLocationTypes()
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
                title: Complaint.PERSON_SUBTABLE_TITLE_ALIASES
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
                    ,options: Complaint.getAliasTypes()
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
            title: Complaint.PERSON_SUBTABLE_TITLE_DEVICES
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
                    ,options: Complaint.getDeviceTypes()
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


};




