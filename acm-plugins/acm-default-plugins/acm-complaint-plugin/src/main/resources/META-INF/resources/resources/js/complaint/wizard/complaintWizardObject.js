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
        this.$btnSubmit              = $("button[data-title='Submit']");
        this.$edtFname               = $("#fname");
        this.$edtLname               = $("#lname");
        this.$edtCompany             = $("#company");
        this.$selPersonTitle         = $("#personTitle");
        this.$divComplaintDetails    = $('.complaintDetails');
        this.$selIntiatorFlags       = $(".choose-intitiatorFlags");   //"#intiatorFlags"
        this.$selComplaintFlags      = $(".choose-complaintFlags");    //"#intiatorFlags"
        this.$selApprovers           = $(".choose-approvers");         //"#approvers"
        this.$selCollab              = $(".choose-collab");            //""
        this.$selNotifications       = $(".choose-notifications");     //"#notifications"
        this.$edtIncidentDate        = $("#incidentDate");
        this.$selComplaintType       = $("#complaintType");

        this.$divInitiator           = $("#divInitiator");
        this.$divPeople              = $("#divPeople");
        this.$divDevices             = $("#divDevices");

        this.$uppload                = $('#upload');
        this.$upploadDrop            = $('#drop');
        this.$upploadList            = $('#upload ul');
        this.$upploadClick           = $('#drop a');

        this.$btnSave.click(function(e) {ComplaintWizard.Event.onClickBtnSave(e);});
        this.$btnSubmit.click(function(e) {ComplaintWizard.Event.onClickBtnSubmit(e);});

        this.$divComplaintDetails.summernote({
            height: 300
        });

        this.$selIntiatorFlags.chosen();
        this.$selComplaintFlags.chosen();
        this.$selApprovers.chosen();
        this.$selCollab.chosen();
        this.$selNotifications.chosen();

        this._createJTableInitiator(this.$divInitiator);
        this._createJTablePeople(this.$divPeople);
        this._createJTableDevices(this.$divDevices);
        this._useFileUpload(this.$uppload, this.$upploadDrop, this.$upploadList, this.$upploadClick);
    }


    ,setEnableBtnSave: function(enable) {
        Acm.Object.setEnable(this.$btnSave, enable);
    }
    ,getValueEdtFname: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtFname);
    }
//    ,setValueEdtFname: function(val) {
//        return Acm.Object.setPlaceHolderInput(this.$edtFname, val);
//    }
    ,getValueEdtLname: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtLname);
    }
    ,getValueEdtCompany: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtCompany);
    }
    ,getValueSelPersonTitle: function() {
        return Acm.Object.getSelectValue(this.$selPersonTitle);
    }
    ,getHtmlDivComplaintDetails: function() {
        return Acm.Object.getSummernote(this.$divComplaintDetails);
    }
    ,getValueEdtIncidentDate: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtIncidentDate);
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

    ,getComplaintData : function() {
        var data = {};

        data.originator = {};
        if (Acm.isNotEmpty(Complaint.getComplaintId())) {
            data.complaintId = Complaint.getComplaintId();
        }
        data.originator.givenName = this.getValueEdtFname();
        data.originator.familyName = this.getValueEdtLname();
        data.originator.company = this.getValueEdtCompany();

        //$selComplaintType
        data.originator.title = this.getValueSelPersonTitle();

//        ContactMethod contactMethods
//        PostalAddress addresses
//        organizations
//        aliases

        //data.details
        var a1 = this.getHtmlDivComplaintDetails();

        //incidentDate
        var d1 = this.getValueEdtIncidentDate();

        //"securityTags": ["Anonymous", "Confidential", "Top Secret"]
        var b6 = this.getSelectValuesSelApprovers();



        var z = 1;

//        term.docType = ComplaintWizard.Object._getValueSelDocType();
//        term.subjectLastName = ComplaintWizard.Object._getValueEdtLastName();
//        term.subjectSSN = ComplaintWizard.Object._getValueEdtSsn();
//        term.eqipRequestNumber = ComplaintWizard.Object._getValueEdtEQipRequest();
//        term.soi = ComplaintWizard.Object._getValueEdtSoi();
//        term.son = ComplaintWizard.Object._getValueEdtSon();
//        term.assignee = ComplaintWizard.Object._getValueSelAssignee();
//
//        term.supervisorReviewFlag = ComplaintWizard.Object._isCheckedChkSupervisorReview();
//        term.contractOversightReviewFlag = ComplaintWizard.Object._isCheckedChkContractOversight();
//
//        term.queues = [{},{},{}];
//        term.queues[0].name = Unassigned.queueProcessing.name;
//        term.queues[0].checked = ComplaintWizard.Object.isCheckedChkProcessing();
//        term.queues[1].name = Unassigned.queueQa.name;
//        term.queues[1].checked = ComplaintWizard.Object.isCheckedChkQa();
//        term.queues[2].name = Unassigned.queueMailback.name;
//        term.queues[2].checked = ComplaintWizard.Object.isCheckedChkMailback();

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
                //redirect : to complaintList
                //redirectParamName:
                //
//check if complaintId not created, create it first
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



                url: Acm.getContextPath() + ComplaintWizard.Service.API_UPLOAD_COMPLAINT_FILE,

                formData: function(form) {
                    var fd = [{}];
                    fd[0].name = "complaintId";
                    fd[0].value = Complaint.getComplaintId();
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
                    //,options: Acm.getContextPath() + '/api/latest/plugin/complaint/types'
                    ,options: Complaint.getPersonTypes()
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
                    var c = Complaint.getComplaint();
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
                    var c = Complaint.getComplaint();
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
                    //,options: Acm.getContextPath() + '/api/latest/plugin/complaint/types'
                    ,options: Complaint.getPersonTypes()
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
//        var checked = ComplaintWizard.Object.isCheckedChkQa();
//        ComplaintWizard.Object.showLnkNextItemQa(checked);
//        checked = ComplaintWizard.Object.isCheckedChkMailback();
//        ComplaintWizard.Object.showLnkNextItemMb(checked);
    }
    ,showLnkNextItemQa: function(show) {
        Acm.Object.showParent(this.$lnkNextItemQa, show);
    }
    ,showLnkNextItemMb: function(show) {
        Acm.Object.showParent(this.$lnkNextItemMb, show);
    }
	,putUnassignedDoc: function(unassignedTaskId, unassignedDoc) {
        var selected = false;
        var prevUnassignedDoc = ComplaintWizard.Object.getUnassignedDoc(unassignedTaskId);
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
        var unassignedDocs = ComplaintWizard.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = ComplaintWizard.Object.getUnassignedDoc(unassignedTaskId);
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
        var unassignedDocs = ComplaintWizard.Object.getUnassignedDocs();
        for (unassignedTaskId in unassignedDocs) {
            var unassignedDoc = ComplaintWizard.Object.getUnassignedDoc(unassignedTaskId);
            if (unassignedDoc) {
                if (unassignedDoc.selected) {
                    taskIds.push(unassignedTaskId);
                }
            }
        }
        return taskIds;
    }
	,setCheckChkSelectOne : function(checked) {
		jQuery("input[type=checkbox]" + ComplaintWizard.Object.clsChkSelectOne).each(function(i, item) {
			item.checked = checked;
            ComplaintWizard.Object.selectUnassignedDoc(item);
		});
	}
	,regClickChkSelectOne: function() {
		jQuery("input[type=checkbox]" + ComplaintWizard.Object.clsChkSelectOne).click(function() {
            ComplaintWizard.Callbacks.onClickChkSelectOne(this);
        });
	}
	,getTaskIdClicked: function(checkBox) {
		return jQuery(checkBox).next().val();
	}
    ,selectUnassignedDoc : function(checkBox) {
        var unassignedTaskId = ComplaintWizard.Object.getTaskIdClicked(checkBox);
        var unassignedDoc  = ComplaintWizard.Object.getUnassignedDoc(unassignedTaskId);
        if (unassignedDoc) {
            unassignedDoc.selected = checkBox.checked;
        }
    }
	,getContextPath: function() {
        return ComplaintWizard.contextPath;
	}
    ,getTicket: function() {
        return ComplaintWizard.ticket;
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
        ComplaintWizard.Object.setValueTxtAssignComment("");
        ComplaintWizard.Object.showInvalidAssign(false);
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
        jQuery(ComplaintWizard.Object.idBtnWidDeleteCancel).click();
    }
    ,clearFrmWidDelete: function() {
        ComplaintWizard.Object.setValueTxtDeleteComment("");
        ComplaintWizard.Object.showInvalidDelete(false);
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
        ComplaintWizard.Object.setValueTxtFingerPrintName("");
        ComplaintWizard.Object.showInvalidFingerPrint(false);
    }

    ,showResultList: function() {
    	ComplaintWizard.Object._showDivResultList(true);
    	ComplaintWizard.Object._showDivResultGrid(false);
    	ComplaintWizard.Object._showLnkViewList(false);
    	ComplaintWizard.Object._showLnkViewGrid(true);
    }
    ,showResultGrid: function() {
    	ComplaintWizard.Object._showDivResultList(false);
    	ComplaintWizard.Object._showDivResultGrid(true);
    	ComplaintWizard.Object._showLnkViewList(true);
    	ComplaintWizard.Object._showLnkViewGrid(false);
    }
	,getSearchTerm : function() {
		var term = {};

		term.docType = ComplaintWizard.Object._getValueSelDocType();
		term.subjectLastName = ComplaintWizard.Object._getValueEdtLastName();
		term.subjectSSN = ComplaintWizard.Object._getValueEdtSsn();
		term.eqipRequestNumber = ComplaintWizard.Object._getValueEdtEQipRequest();
		term.soi = ComplaintWizard.Object._getValueEdtSoi();
		term.son = ComplaintWizard.Object._getValueEdtSon();
		term.assignee = ComplaintWizard.Object._getValueSelAssignee();

		term.supervisorReviewFlag = ComplaintWizard.Object._isCheckedChkSupervisorReview();
		term.contractOversightReviewFlag = ComplaintWizard.Object._isCheckedChkContractOversight();

		term.queues = [{},{},{}];
		term.queues[0].name = Unassigned.queueProcessing.name;
		term.queues[0].checked = ComplaintWizard.Object.isCheckedChkProcessing();
		term.queues[1].name = Unassigned.queueQa.name;
		term.queues[1].checked = ComplaintWizard.Object.isCheckedChkQa();
		term.queues[2].name = Unassigned.queueMailback.name;
		term.queues[2].checked = ComplaintWizard.Object.isCheckedChkMailback();

		return term;
	}
    ,clearSearchTerm : function() {
        ComplaintWizard.Object._setValueSelDocType("placeholder");
        ComplaintWizard.Object._setValueEdtLastName("");
        ComplaintWizard.Object._setValueEdtSsn("");
        ComplaintWizard.Object._setValueEdtEQipRequest("");
        ComplaintWizard.Object._setValueEdtSoi("");
        ComplaintWizard.Object._setValueEdtSon("");
        ComplaintWizard.Object._setValueSelAssignee("placeholder");

        ComplaintWizard.Object._setCheckedChkSupervisorReview(false);
        ComplaintWizard.Object._setCheckedChkContractOversight(false);

        ComplaintWizard.Object._setCheckedChkProcessing(false);
        ComplaintWizard.Object._setCheckedChkQa(true);
        ComplaintWizard.Object._setCheckedChkMailback(false);
    }

    ,getSelectedQueueNames : function () {
    	var queueNames = "";
    	if (ComplaintWizard.Object.isCheckedChkProcessing()) {
    		queueNames += "," + Unassigned.queueProcessing.name;
    	}
    	if (ComplaintWizard.Object.isCheckedChkQa()) {
    		queueNames += "," + Unassigned.queueQa.name;
    	}
    	if (ComplaintWizard.Object.isCheckedChkMailback()) {
    		queueNames += "," + Unassigned.queueMailback.name;
    	}

    	if (Acm.Common.isNotEmpty(queueNames)) {
    		queueNames = queueNames.substring(1, queueNames.length); //discard extra leading ','
    	}
    	return queueNames;
    }
//    ,getSelectedQueue : function () {
//        if (ComplaintWizard.Object.isCheckedChkProcessing()) {
//            return Unassigned.queueProcessing;
//        } else if (ComplaintWizard.Object.isCheckedChkQa()) {
//            return Unassigned.queueQa;
//        } else if (ComplaintWizard.Object.isCheckedChkMailback()) {
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




