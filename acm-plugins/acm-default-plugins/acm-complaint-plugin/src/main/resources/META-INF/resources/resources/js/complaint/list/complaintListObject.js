/**
 * ComplaintList.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
ComplaintList.Object = {
    initialize : function() {
        this.$ulComplaints      = $("#ulComplaints");
        this.$asideComplaints   = this.$ulComplaints.closest("aside");
        this.$ulTabs            = $("#ulTabs");

        var items = $(document).items();
        var complaintId = items.properties("complaintId").itemValue();
        if (Acm.isNotEmpty(complaintId)) {
            Complaint.setComplaintId(complaintId);
            this.showAsideComplaints(false);
            ComplaintList.setSingleObject(true);
        } else {
            ComplaintList.setSingleObject(false);
        }
        this.setInitId(items.properties("initId").itemValue());
        this.setInitTab(items.properties("initTab").itemValue());


        this.$lnkTitle          = $("#caseTitle");
        this.$h4TitleHeader     = $("#caseTitle").parent();

        this.$lnkIncident       = $("#incident");
        this.$lnkPriority       = $("#priority");
        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");
        this.$lnkStatus         = $("#status");

        this.$divDetails        = $(".complaintDetails");
        this.$secIncident       = $("#secIncident");
        this.$tableIncident     = $("#secIncident>div>table");

        this.$divInitiator      = $("#divInitiator");
        this._createJTableInitiator(this.$divInitiator);

        this.$secDocDocuments   = $("#secDocDocuments");
        this.$tableDocDocuments = $("#secDocDocuments>div>table");
        this.$lnkNewDoc         = $("#secDocDocuments>div>span");
        //this.$upploadList       = $('#secDocDocuments ul');
        this.$upploadList       = $('#upload ul');
        this._useFileUpload(this.$secDocDocuments, this.$tableDocDocuments, this.$upploadList, this.$lnkNewDoc);

        this.$tableTasks        = $("div#tasks>div>div>section>div>table");
        this.$lnkNewTasks       = $("div#tasks>div>div>section>div>span");
        this.$lnkNewTasks.click(function(e){ComplaintList.Event.onClickLnkNewTasks(e);});


        this.$tableRefDocuments = $("#secRefDocuments>div>table");


        //$.fn.editable.defaults.url = '/post';
        this.$lnkTitle.editable({placement: 'right'});
        this.$lnkIncident.editable({placement: 'bottom'
            ,format: 'yyyy-mm-dd'
            ,viewformat: 'yyyy/mm/dd'
            ,datepicker: {
                weekStart: 1
            }
        });

//test area
//        $("#sex").editable({placement: 'right'
//            , value: "F"
//            ,source: [
//                //{value: "", text: 'no select'},
//                {value: "M", text: 'Male'}
//                ,{value: "F", text: 'Female'}
//            ]
//            ,url: ""
//            ,success: function(response, newValue) {
//                console.log("editable, sex=" + newValue); //update backbone model
//            }
//
//        });
//        $("#sex").editable("setValue", "");


        this.$tree = $("#tree");
        this.$tree.fancytree({

            source: [{
                title: "2014-03-12321",
                tooltip: "Sample Compalint Title",
                expanded: "fancytree-expanded",
                children: [{
                    title: "Incident",
                    folder: true,
                    children: [{
                        title: "Initiator "
                    }, {
                        title: "People",
                        folder: true,
                        children: [{title: "Person 1"}, {title: "Person 2"}]
                    }]
                }, {
                    title: "Attachments",
                    folder: true,
                    children: [{title: "Pending", folder:true}, {title: "Approved", folder:true}, {title: "Rejected", folder:true} ]
                }, {
                    title: "Tasks",
                    folder: true,
                    children: [{title: "Unassigned", folder:true}, {title: "Assigned", folder:true}, {title: "Completed", folder:true} ]
                }, {
                    title: "References",
                    folder: true,
                    children: [{title: "Complaints", folder:true}, {title: "Cases", folder:true}, {title: "Tasks", folder:true}, {title: "Documents", folder:true} ]
                }, {
                    title: "Participants",
                    folder: true,
                    children: [{title: "Approvers", folder:true}, {title: "Collaborators", folder:true}, {title: "Watchers", folder:true} ]
                }]
            }]
        });


    }

    ,_initId: ""
    ,getInitId: function() {
        return this._initId;
    }
    ,setInitId: function(id) {
        this._initId = id;
    }

    ,_initTab: ""
    ,getInitTab: function() {
        return this._initTab;
    }
    ,setInitTab: function(tab) {
        this._initTab = tab;
    }

    ,showAsideComplaints: function(show) {
        Acm.Object.show(this.$asideComplaints, show);
    }
    ,hiliteSelectedItem: function() {
        var cur = Complaint.getComplaintId();
        this.$ulComplaints.find("li").each(function(index) {
            var cid = $(this).find("input[type='hidden']").val();
            if (cid == cur) {
                $(this).addClass("active");

                //todo: scroll selected item to view
                //$('#yourUL').scrollTop($('#yourUL li:nth-child(14)').position().top);
                //$('#yourUL').scrollTop($('#yourUL').top + $('#yourUL li:nth-child(14)').position().top);
                //this.$ulComplaints.scrollTop($(this).position().top);
            } else {
                $(this).removeClass("active");
            }
        });
    }


    ,initAssignee: function(data) {
        var choices = []; //[{value: "", text: "Choose Assignee"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val.userId;
            opt.text = val.fullName;
            choices.push(opt);
        });

        this.$lnkAssigned.editable({placement: 'bottom', value: "",
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

        this.$lnkComplaintType.editable({placement: 'bottom', value: "",
            source: choices
        });
    }
    ,initPriority: function(data) {
        var choices = []; //[{value: "", text: "Choose Priority"}];
        $.each(data, function(idx, val) {
            var opt = {};
            opt.value = val;
            opt.text = val;
            choices.push(opt);
        });

        this.$lnkPriority.editable({placement: 'bottom', value: "",
            source: choices
        });
    }
    ,getHtmlUlComplaints: function() {
        return Acm.Object.getHtml(this.$ulComplaints);
    }
    ,setHtmlUlComplaints: function(val) {
        return Acm.Object.setHtml(this.$ulComplaints, val);
    }
    ,registerClickListItemEvents: function() {
        this.$ulComplaints.find("a.thumb-sm").click(function(e) {ComplaintList.Event.onClickLnkListItemImage(this);});
        this.$ulComplaints.find("a.text-ellipsis").click(function(e) {ComplaintList.Event.onClickLnkListItem(this);});
    }
    ,getHiddenComplaintId: function(e) {
        var $hidden = $(e).siblings("input[type='hidden']");
        return $hidden.val();
    }
    ,updateDetail: function(c) {
        this.setValueLnkTitle(c.complaintTitle);
        this.setTextH4TitleHeader(" (" + c.complaintNumber + ")");
        this.setValueLnkIncident(Acm.getDateFromDatetime(c.created));
        this.setValueLnkPriority(c.priority);
        this.setValueLnkAssigned(c.assignee);
        this.setValueLnkComplaintType(c.complaintType);
        this.setTextLnkStatus(c.status);

        this.setHtmlDetails(c.details);

        this.$divInitiator.jtable('load');

        //ComplaintList.Page.buildTableIncident(c);

        this.removeUploadFileArea();
        ComplaintList.Page.buildTableDocDocuments(c);
        //ComplaintList.Page.buildTableRefDocuments(c);
    }
    ,setValueLnkTitle: function(txt) {
        this.$lnkTitle.editable("setValue", txt);
    }
    ,setTextH4TitleHeader: function(txt) {
        Acm.Object.setTextNodeText(this.$h4TitleHeader, txt, 1);
    }

    ,setValueLnkIncident: function(txt) {
        Acm.Object.setText(this.$lnkIncident, txt);
        //this.$lnkIncident.editable("setValue", txt);
    }
    ,setValueLnkPriority: function(txt) {
        this.$lnkPriority.editable("setValue", txt);
    }
    ,setValueLnkAssigned: function(txt) {
        this.$lnkAssigned.editable("setValue", txt);
    }
    ,setValueLnkComplaintType: function(txt) {
        this.$lnkComplaintType.editable("setValue", txt);
    }
    ,setTextLnkStatus: function(txt) {
        Acm.Object.setText(this.$lnkStatus, txt);
    }

    ,clickTab: function(tab) {
        var lnk = this.$ulTabs.find("a[href='#" + tab + "']");
        lnk.click();
    }

    ,setHtmlDetails: function(html) {
        Acm.Object.setHtml(this.$divDetails, html);
    }


    ,resetTableDocDocuments: function() {
        this.$tableDocDocuments.find("tbody > tr").remove();
    }
    ,addRowTableDocDocuments: function(row) {
        this.$tableDocDocuments.find("tbody:last").append(row);
    }
    ,resetTableTasks: function() {
        this.$tableTasks.find("tbody > tr").remove();
    }
    ,addRowTableTasks: function(row) {
        this.$tableTasks.find("tbody:last").append(row);
    }
    ,registerChangeSelTasksEvents: function() {
        this.$tableTasks.find("select").change(function(e) {ComplaintList.Event.onChangeSelTasks(this);});
    }
    ,resetTableRefDocuments: function() {
        this.$tableRefDocuments.find("tbody > tr").remove();
    }
    ,addRowTableRefDocuments: function(row) {
        this.$tableRefDocuments.find("tbody:last").append(row);
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
                            ComplaintList.Object._toggleInitiatorDevices($s, $a);
                            e.preventDefault();
                        });
                        $b.click(function (e) {
                            ComplaintList.Object._toggleInitiatorOrganizations($s, $b);
                            e.preventDefault();
                        });
                        $c.click(function (e) {
                            ComplaintList.Object._toggleInitiatorLocations($s, $c);
                            e.preventDefault();
                        });
                        $d.click(function (e) {
                            ComplaintList.Object._toggleInitiatorAliases($s, $d);
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


    ,removeUploadFileArea: function() {
        this.$upploadList.find("li").remove();
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
                url: App.getContextPath() + ComplaintList.Service.API_UPLOAD_COMPLAINT_FILE
                ,dropZone: $drop

                ,done: function (e, data) {
//                    var a1 = data.result
//                    var a2 = data.textStatus;
//                    var a3 = data.jqXHR;

                    if ("success" == data.textStatus) {
                        ComplaintList.Object.removeUploadFileArea();
                        ComplaintList.Event.doClickLnkListItem();
                    }
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
};




