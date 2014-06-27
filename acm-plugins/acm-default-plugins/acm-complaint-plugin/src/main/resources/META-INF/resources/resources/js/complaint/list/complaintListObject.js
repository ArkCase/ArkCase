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

        var items = $(document).items();
        var complaintId = items.properties("complaintId").itemValue();
        if (Acm.isNotEmpty(complaintId)) {
            Complaint.setComplaintId(complaintId);
            this.showAsideComplaints(false);
            ComplaintList.setSingleObject(true);
        } else {
            ComplaintList.setSingleObject(false);
        }

        this.$lnkTitle          = $("#caseTitle");
        this.$h4ComplaintNumber = $("#caseTitle").parent();

        this.$lnkIncident       = $("#incident");
        this.$lnkPriority       = $("#priority");
        this.$lnkAssigned       = $("#assigned");
        this.$lnkComplaintType  = $("#type");
        this.$lnkStatus         = $("#status");

        this.$divDetails        = $(".complaintDetails");
        this.$secIncident       = $("#secIncident");
        this.$tableIncident     = $("#secIncident>div>table");

        this.$divInitiator           = $("#divInitiator");
        this._createJTableInitiator(this.$divInitiator);

        this.$tableDocDocuments     = $("#secDocDocuments>div>table");

        this.$tableRefDocuments     = $("#secRefDocuments>div>table");


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
            } else {
                $(this).removeClass("active");
            }
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
        this.setTextLnkTitle(c.complaintTitle);
        this.setTextH4ComplaintNumber(" (" + c.complaintNumber + ")");
        this.setTextLnkIncident(Acm.getDateFromDatetime(c.created));
        this.setTextLnkPriority(c.priority);
        this.setTextLnkAssigned(c.assignee);
        this.setTextLnkComplaintType(c.complaintType);
        this.setTextLnkStatus(c.status);

        this.setHtmlDetails(c.details);

        this.$divInitiator.jtable('load');

        //ComplaintList.Page.buildTableIncident(c);
        ComplaintList.Page.buildTableDocDocuments(c);
        //ComplaintList.Page.buildTableRefDocuments(c);
    }
    ,setTextLnkTitle: function(txt) {
        Acm.Object.setText(this.$lnkTitle, txt);
    }
    ,setTextH4ComplaintNumber: function(txt) {
        Acm.Object.setTextNodeText(this.$h4ComplaintNumber, txt, 1);
    }

    ,setTextLnkIncident: function(txt) {
        Acm.Object.setText(this.$lnkIncident, txt);
    }
    ,setTextLnkPriority: function(txt) {
        Acm.Object.setText(this.$lnkPriority, txt);
    }
    ,setTextLnkAssigned: function(txt) {
        Acm.Object.setText(this.$lnkAssigned, txt);
    }
    ,setTextLnkComplaintType: function(txt) {
        Acm.Object.setText(this.$lnkComplaintType, txt);
    }
    ,setTextLnkStatus: function(txt) {
        Acm.Object.setText(this.$lnkStatus, txt);
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
                    //,options: Acm.getContextPath() + '/api/latest/plugin/complaint/types'
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
                        rc.Record.creator = Acm.getUserName();   //record.creator;
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

};




