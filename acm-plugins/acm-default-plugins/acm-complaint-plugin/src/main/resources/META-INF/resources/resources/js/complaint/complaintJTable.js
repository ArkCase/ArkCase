/**
 * Complaint.JTable
 *
 * JTable
 *
 * @author jwu
 */
Complaint.JTable = {
    initialize : function() {
    }


    //
    //------------------ Initiator ------------------
    //
    ,_toggleSubJTable: function($t, $row, fnOpen, fnClose, title) {
        var $childRow = $t.jtable('getChildRow', $row.closest('tr'));
        var curTitle = $childRow.find("div.jtable-title-text").text();

        var toClose;
        if ($t.jtable('isChildRowOpen', $row.closest('tr'))) {
            toClose = (curTitle === title);
        } else {
            toClose = false;
        }

        if (toClose) {
            fnClose($t, $row);
        } else {
            fnOpen($t, $row);
        }
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
    ,_createJTable4SubTable: function($s, arg) {
        //return;
        var argNew = {fields:{}};
        argNew.fields.subTables = {
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
                    Complaint.JTable._toggleInitiatorDevices($s, $a);
                    e.preventDefault();
                });
                $b.click(function (e) {
                    Complaint.JTable._toggleInitiatorOrganizations($s, $b);
                    e.preventDefault();
                });
                $c.click(function (e) {
                    Complaint.JTable._toggleInitiatorLocations($s, $c);
                    e.preventDefault();
                });
                $d.click(function (e) {
                    Complaint.JTable._toggleInitiatorAliases($s, $d);
                    e.preventDefault();
                });
                return $a.add($b).add($c).add($d);
            }
        }
        for (var key in arg) {
            if ("fields" == key) {
                for (var FieldKey in arg.fields) {
                    argNew.fields[FieldKey] = arg.fields[FieldKey];
                }
            } else {
                argNew[key] = arg[key];
            }
        }
        $s.jtable(argNew);
        $s.jtable('load');
    }


//    ,refreshJTableInitiator: function() {
//        AcmEx.Object.jTableLoad(this.$divInitiator);
//    }
    ,_getEmptyOriginator: function() {
        return {id: null
            ,personType: "Complaint"
            ,personDescription: ""
            ,person: {id: null
                ,company: ""
                ,addresses: []
                ,contactMethods: []
                ,securityTags: []
                ,personAliases: []
            }
        };
    }
    ,createJTableInitiator: function($s) {
        this._createJTable4SubTable($s, {
            title: 'Initiator'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = {"Result": "OK"
                        ,"Records": [{id:0, title:"", givenName:"", familyName:"", type:"", description:""}]
                        ,TotalRecordCount: 1
                    };
                    var c = Complaint.getComplaint();
                    if (c && c.originator) {
                        rc.Records[0].id = c.originator.id;
                        rc.Records[0].type = Acm.goodValue(c.originator.personType);
                        rc.Records[0].description = Acm.goodValue(c.originator.personDescription);

                        var person = c.originator.person;
                        if (person) {
                            rc.Records[0].title = Acm.goodValue(person.title);
                            rc.Records[0].givenName = Acm.goodValue(person.givenName);
                            rc.Records[0].familyName = Acm.goodValue(person.familyName);
                        }
                    }
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var c = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    if (c) {
                        var originatorId = 0;
                        if (c.originator && c.originator.id) {
                            originatorId = c.originator.id;
                        }
                        rc.Record.id = originatorId;    // (record.id) is empty, do not assign;
                        rc.Record.title = record.title;
                        rc.Record.givenName = record.givenName;
                        rc.Record.familyName = record.familyName;
                        rc.Record.type = record.type;
                        rc.Record.description = record.description;
                    }
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
            ,recordUpdated: function(event, data){
                var record = data.record;
                var c = Complaint.getComplaint();
                if (c) {
                    if (!c.originator) {
                        c.originator = Complaint.JTable._getEmptyOriginator();
                    }

                    c.originator.personType = record.type;
                    c.originator.personDescription = record.description;
                    var person = c.originator.person;
                    if (person) {
                        person.title = record.title;
                        person.givenName = record.givenName;
                        person.familyName = record.familyName;

                        Complaint.Service.saveComplaint(c);
                    }
                } //end if (c)
            }
            ,formCreated: function (event, data) {
                //to be used for typeahead in future
                var a = data;
                var z = 1;
            }
        });
    }

    ,_closeInitiatorDevices: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorDevices: function($t, $row) {
        $t.jtable('openChildTable', $row.closest('tr')
            ,{
                title: Complaint.PERSON_SUBTABLE_TITLE_DEVICES
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var c = Complaint.getComplaint();
                        if (c && c.originator && c.originator.person && c.originator.person.contactMethods) {
                            var contactMethods = c.originator.person.contactMethods;
                            var cnt = contactMethods.length;
                            for (i = 0; i < cnt; i++) {
                                rc.Records.push({personId: c.originator.id
                                    //,id: contactMethods[i].id
                                    ,type: contactMethods[i].type
                                    ,value: contactMethods[i].value
                                    ,created: Acm.goodValue(contactMethods[i].created)
                                    ,creator: Acm.goodValue(contactMethods[i].creator)
                                });
                            }
                        }
                        return rc;
//                        return {
//                            "Result": "OK"
//                            ,"Records": [
//                                { "personId":  1, "type": "Phone", "value": "703-123-5678", "created": "01-02-03", "creator": "123 do re mi" }
//                                ,{ "personId": 2, "type": "Email", "value": "doe@gmail.com", "created": "14-05-15", "creator": "xyz abc" }
//                            ]
//                            ,"TotalRecordCount": 2
//                        };

                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var c = Complaint.getComplaint();
                        if (c & c.originator) {
                            rc.Record.personId = c.originator.id;
                            //rc.Record.id = parseInt(record.id);
                            rc.Record.type = record.type;
                            rc.Record.value = record.value;
                            rc.Record.created = Acm.getCurrentDay(); //record.created;
                            rc.Record.creator = App.getUserName();   //record.creator;
                        }
                        return rc;
//                        return {
//                            "Result": "OK"
//                            ,"Record":
//                            { "personId": 3, "id": "c", "type": "Phone", "value": "703-123-9999", "created": "01-02-03", "creator": "test" }
//                        };
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var c = Complaint.getComplaint();
                        if (c && c.originator) {
                            rc.Record.personId = c.originator.id;
                            //rc.Record.id = parseInt(record.id);           //no such field in postData, ignored
                            rc.Record.type = record.type;
                            rc.Record.value = record.value;
                            rc.Record.created = record.created;
                            rc.Record.creator = record.creator;
                        }
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
//                    ,id: {
//                        key: false
//                        ,type: 'hidden'
//                        ,edit: false
//                        ,defaultValue: 0
//                    }
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
                    if (c && c.originator && c.originator.person && c.originator.person.contactMethods) {
                        var contactMethods = c.originator.person.contactMethods;

                        var contactMethod = {};
                        //contactMethod.id = parseInt(record.id);
                        contactMethod.type = record.type;
                        contactMethod.value = record.value;
                        //contactMethod.created = record.created;   //created,creator is readonly
                        //contactMethod.creator = record.creator;
                        contactMethods.push(contactMethod);

                        Complaint.Service.saveComplaint(c);
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var c = Complaint.getComplaint();
                    if (c && c.originator && c.originator.person && c.originator.person.contactMethods) {
                        var contactMethods = c.originator.person.contactMethods;

                        var contactMethod = contactMethods[whichRow];
                        contactMethod.type = record.type;
                        contactMethod.value = record.value;
                        //contactMethod.created = record.created;   //created,creator is readonly
                        //contactMethod.creator = record.creator;

                        Complaint.Service.saveComplaint(c);
                    }
                }
                ,recordDeleted : function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var c = Complaint.getComplaint();
                    if (c && c.originator && c.originator.person && c.originator.person.contactMethods) {
                        var contactMethods = c.originator.person.contactMethods;
                        contactMethods.splice(whichRow, 1);
                        Complaint.Service.saveComplaint(c);
                    }
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
        $t.jtable('openChildTable', $row.closest('tr')
            ,{
                title: Complaint.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
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
        $t.jtable('openChildTable', $row.closest('tr')
            ,{
                title: Complaint.PERSON_SUBTABLE_TITLE_LOCATIONS
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var c = Complaint.getComplaint();
                        if (c && c.originator && c.originator.person && c.originator.person.addresses) {
                            var addresses = c.originator.person.addresses;
                            var cnt = addresses.length;
                            for (i = 0; i < cnt; i++) {
                                rc.Records.push({personId: c.originator.id
                                    //,id: addresses[i].id
                                    ,type: addresses[i].type
                                    ,streetAddress: addresses[i].streetAddress
                                    ,city: addresses[i].city
                                    ,state: addresses[i].state
                                    ,zip: addresses[i].zip
                                    ,created: Acm.goodValue(addresses[i].created)
                                    ,creator: Acm.goodValue(addresses[i].creator)
                                });
                            }
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var c = Complaint.getComplaint();
                        if (c & c.originator) {
                            rc.Record.personId = c.originator.id;
                            rc.Record.type = record.type;
                            rc.Record.streetAddress = record.streetAddress;
                            rc.Record.city = record.city;
                            rc.Record.state = record.state;
                            rc.Record.zip = record.zip;
                            rc.Record.created = Acm.getCurrentDay(); //record.created;
                            rc.Record.creator = App.getUserName();   //record.creator;
                        }
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var c = Complaint.getComplaint();
                        if (c && c.originator) {
                            rc.Record.personId = c.originator.id;
                            rc.Record.type = record.type;
                            rc.Record.streetAddress = record.streetAddress;
                            rc.Record.city = record.city;
                            rc.Record.state = record.state;
                            rc.Record.zip = record.zip;
                            rc.Record.created = record.created;
                            rc.Record.creator = record.creator;
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
                    personId: {
                        type: 'hidden'
                        ,defaultValue: 1 //commData.record.StudentId
                    }
                    ,type: {
                        title: 'Type'
                        ,width: '8%'
                        ,options: Complaint.getLocationTypes()
                    }
                    ,streetAddress: {
                        title: 'Address'
                        ,width: '20%'
                    }
                    ,city: {
                        title: 'City'
                        ,width: '10%'
                    }
                    ,state: {
                        title: 'State'
                        ,width: '8%'
                    }
                    ,zip: {
                        title: 'Zip'
                        ,width: '8%'
                    }
                    ,country: {
                        title: 'Country'
                        ,width: '8%'
                    }
                    ,created: {
                        title: 'Date Added'
                        ,width: '15%'
                        //,type: 'date'
                        //,displayFormat: 'yy-mm-dd'
                        ,create: false
                        ,edit: false
                    }
                    ,creator: {
                        title: 'Added By'
                        ,width: '15%'
                    }
                }
                ,recordAdded : function (event, data) {
                    var record = data.record;
                    var c = Complaint.getComplaint();
                    if (c && c.originator && c.originator.person && c.originator.person.addresses) {
                        var addresses = c.originator.person.addresses;
                        var address = {};
                        address.type = record.type;
                        address.streetAddress = record.streetAddress;
                        address.city = record.city;
                        address.state = record.state;
                        address.zip = record.zip;
                        addresses.push(address);

                        Complaint.Service.saveComplaint(c);
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var c = Complaint.getComplaint();
                    if (c && c.originator && c.originator.person && c.originator.person.addresses) {
                        var addresses = c.originator.person.addresses;

                        var address = addresses[whichRow];
                        address.type = record.type;
                        address.streetAddress = record.streetAddress;
                        address.city = record.city;
                        address.state = record.state;
                        address.zip = record.zip;
                        //address.created = record.created;   //created,creator is readonly
                        //address.creator = record.creator;

                        Complaint.Service.saveComplaint(c);
                    }
                }
                ,recordDeleted : function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var c = Complaint.getComplaint();
                    if (c && c.originator && c.originator.person && c.originator.person.addresses) {
                        var addresses = c.originator.person.addresses;
                        addresses.splice(whichRow, 1);
                        Complaint.Service.saveComplaint(c);
                    }
                }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closeInitiatorAliases: function($jt, $row) {
        $jt.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openInitiatorAliases: function($jt, $row) {
        $jt.jtable('openChildTable', $row.closest('tr')
            ,{
                title: Complaint.PERSON_SUBTABLE_TITLE_ALIASES
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var c = Complaint.getComplaint();
                        if (c && c.originator && c.originator.person && c.originator.person.personAliases) {
                            var personAliases = c.originator.person.personAliases;
                            var cnt = personAliases.length;
                            for (i = 0; i < cnt; i++) {
                                rc.Records.push({personId: c.originator.id
                                    //,id: personAliases[i].id
                                    ,type: personAliases[i].type
                                    ,value: personAliases[i].value
                                    ,created: Acm.goodValue(personAliases[i].created)
                                    ,creator: Acm.goodValue(personAliases[i].creator)
                                });
                            }
                        }
                        return rc;
                    }
                    ,createAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var c = Complaint.getComplaint();
                        if (c & c.originator) {
                            rc.Record.personId = c.originator.id;
                            //rc.Record.id = parseInt(record.id);
                            rc.Record.type = record.type;
                            rc.Record.value = record.value;
                            rc.Record.created = Acm.getCurrentDay(); //record.created;
                            rc.Record.creator = App.getUserName();   //record.creator;
                        }
                        return rc;
                    }
                    ,updateAction: function(postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var c = Complaint.getComplaint();
                        if (c && c.originator) {
                            rc.Record.personId = c.originator.id;
                            //rc.Record.id = parseInt(record.id);           //no such field in postData, ignored
                            rc.Record.type = record.type;
                            rc.Record.value = record.value;
                            rc.Record.created = record.created;
                            rc.Record.creator = record.creator;
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
                    personId: {
                        type: 'hidden'
                        ,defaultValue: 1 //commData.record.StudentId
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
                    ,created: {
                        title: 'Date Added'
                        ,width: '20%'
                        //,type: 'date'
                        //,displayFormat: 'yy-mm-dd'
                        ,create: false
                        ,edit: false
                    }
                    ,creator: {
                        title: 'Added By'
                        ,width: '30%'
                    }
                }
                ,recordAdded : function (event, data) {
                    var record = data.record;
                    var c = Complaint.getComplaint();
                    if (c && c.originator && c.originator.person && c.originator.person.personAliases) {
                        var personAliases = c.originator.person.personAliases;

                        var personAliase = {};
                        //personAliase.id = parseInt(record.id);
                        personAliase.type = record.type;
                        personAliase.value = record.value;
                        //personAliase.created = record.created;   //created,creator is readonly
                        //personAliase.creator = record.creator;
                        personAliases.push(personAliase);

                        Complaint.Service.saveComplaint(c);
                    }
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var c = Complaint.getComplaint();
                    if (c && c.originator && c.originator.person && c.originator.person.personAliases) {
                        var personAliases = c.originator.person.personAliases;

                        var personAliase = personAliases[whichRow];
                        personAliase.type = record.type;
                        personAliase.value = record.value;
                        //contactMethod.created = record.created;   //created,creator is readonly
                        //contactMethod.creator = record.creator;

                        Complaint.Service.saveComplaint(c);
                    }
                }
                ,recordDeleted : function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var c = Complaint.getComplaint();
                    if (c && c.originator && c.originator.person && c.originator.person.personAliases) {
                        var personAliases = c.originator.person.personAliases;
                        personAliases.splice(whichRow, 1);
                        Complaint.Service.saveComplaint(c);
                    }
                }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    //----------------- end of Initiator -----------------------


    //
    //------------------ People ------------------
    //
//    ,refreshJTablePeople: function() {
//        AcmEx.Object.jTableLoad(this.$divPeople);
//    }
    ,createJTablePeople: function($s) {
        this._createJTable4SubTable($s, {
            title: 'People'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    //var rc = {"Result": "OK", "Records": [{id:0, title:"", giventName:"", familyName:"", type:"", description:""}]};

                    var rc = {"Result": "OK", "Records": [{id:0, title:"", givenName:"", familyName:"", type:"", description:""}]};
                   // var rc = {"Result": "OK", "Records": []};
                    var c = Complaint.getComplaint();
                    if (c) {
                        if (Acm.isNotEmpty(c.originator)) {
                            rc.Records[0].id = c.originator.id;
                            rc.Records[0].title = c.originator.title;
                            rc.Records[0].givenName = c.originator.givenName;
                            rc.Records[0].familyName = c.originator.familyName;
                            rc.Records[0].type = c.originator.type;
                            rc.Records[0].description = c.originator.description;
                        }
                    }
                    return rc;
                }
                ,createAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var c = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.id = c.originator.id;
                    rc.Record.title = record.title;
                    rc.Record.givenName = record.givenName;
                    rc.Record.familyName = record.familyName;
                    rc.Record.type = record.type;
                    rc.Record.description = record.description;
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var c = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.id = c.originator.id;    // (record.id) is empty, do not assign;
                    rc.Record.title = record.title;
                    rc.Record.givenName = record.givenName;
                    rc.Record.familyName = record.familyName;
                    rc.Record.type = record.type;
                    rc.Record.description = record.description;
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
                /*var record = data.record;
                var c = Complaint.getComplaint();
                c.originator.title = record.title;
                c.originator.givenName = record.givenName;
                c.originator.familyName = record.familyName;
                c.originator.type = record.type;
                c.originator.description = record.description;*/
                $s.jtable('load');
            }
            ,recordUpdated: function(event, data){
               // var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var record = data.record;
                var c = Complaint.getComplaint();
                c.originator.title = record.title;
                c.originator.givenName = record.givenName;
                c.originator.familyName = record.familyName;
                c.originator.type = record.type;
                c.originator.description = record.description;
                //$s.jtable('load');
            }
        });
    }

    ,_closePeopleDevices: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPeopleDevices: function($t, $row) {
        $t.jtable('openChildTable'
            ,$row.closest('tr')
            ,{
                title: Complaint.PERSON_SUBTABLE_TITLE_DEVICES
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var c = Complaint.getComplaint();
                        var contactMethods = c.originator.contactMethods;
                        var cnt = contactMethods.length;

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
                    var c = Complaint.getComplaint();
                    var addresses = c.originator.addresses;
                    var cnt = addresses.length;
                    var rc = {"Result": "OK", "Records": []};
                    for(i = 0; i<cnt; i++){
                        rc.Records.push({personId: c.originator.id
                            ,id: addresses[i].id
                            ,type: addresses[i].type
                            ,streetAddress: addresses[i].streetAddress
                            ,city: addresses[i].city
                            ,state: addresses[i].state
                            ,zip: addresses[i].zip
                            ,creator: addresses[i].creator
                            ,created: addresses[i].created
                        });
                    }
                    return rc;
                    }
                  /*  return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "Home", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "Office", "address": "999 Fairfax Blvd #201, Fairfax, VA 22030", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };*/

                ,createAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var c = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.personId = c.originator.id;
                    rc.Record.type = record.type;
                    rc.Record.streetAddress = record.streetAddress;
                    rc.Record.city = record.city;
                    rc.Record.state = record.state;
                    rc.Record.zip = record.zip;
                    rc.Record.created = Acm.getCurrentDay(); //record.created;
                    rc.Record.creator = App.getUserName();   //record.creator;
                    return rc;
                    /*return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Home", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "test" }
                    };*/
                }
                ,updateAction: function(postData, jtParams) {

                    var record = Acm.urlToJson(postData);
                    var c = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.personId = c.originator.id;
                    rc.Record.type = record.type;
                    rc.Record.streetAddress = record.streetAddress;
                    rc.Record.city = record.city;
                    rc.Record.state = record.state;
                    rc.Record.zip = record.zip;
                    rc.Record.created = record.created;
                    rc.Record.creator = record.creator;
                    return rc;
                    /*return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Hotel", "address": "123 Main St", "city": "Vienna", "state": "VA", "zip": "22000", "country": "US", "createDate": "01-02-03", "createBy": "test" }
                    };*/
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
                ,streetAddress: {
                    title: 'Address'
                    ,width: '20%'
                }
                ,city: {
                    title: 'City'
                    ,width: '10%'
                }
                ,state: {
                    title: 'State'
                    ,width: '8%'
                }
                ,zip: {
                    title: 'Zip'
                    ,width: '8%'
                }
                ,country: {
                    title: 'Country'
                    ,width: '8%'
                }
                ,created: {
                    title: 'Date Added'
                    ,width: '15%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,creator: {
                    title: 'Added By'
                    ,width: '15%'
                }
            }
            ,recordAdded : function (event, data) {
                var record = data.record;
                var c = Complaint.getComplaint();
                var locations = c.originator.addresses;
                var location = {};
                location.type = record.type;
                location.streetAddress = record.streetAddress;
                location.city = record.city;
                location.state = record.state;
                location.zip = record.zip;
                locations.push(location);
            }
            ,recordUpdated : function (event, data) {
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var record = data.record;
                var c = Complaint.getComplaint();
                var locations = c.originator.addresses;
                var location = locations[whichRow];
                location.type = record.type;
                location.streetAddress = record.streetAddress;
                location.city = record.city;
                location.state = record.state;
                location.zip = record.zip;
            }
            ,recordDeleted : function (event, data) {
                var r = data.row;
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var c = Complaint.getComplaint();
                var locations = c.originator.addresses;
                locations.splice(whichRow, 1);
            }
        }
        ,function (data) { //opened handler
            data.childTable.jtable('load');
        });
    }
    ,_closePeopleAliases: function($jt, $row) {
        $jt.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPeopleAliases: function($jt, $row) {
        $jt.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: Complaint.PERSON_SUBTABLE_TITLE_ALIASES
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    var c = Complaint.getComplaint();
                    var personAliases = c.originator.personAliases;
                    var cnt = personAliases.length;

                    var rc = {"Result": "OK", "Records": []};
                    for(i = 0; i<cnt; i++){
                        rc.Records.push({personId: c.originator.id
                            ,id: personAliases[i].id
                            ,type: personAliases[i].type
                            ,value: personAliases[i].value
                            ,created: personAliases[i].created
                            ,creator: personAliases[i].creator
                        });
                    }
                    return rc;


                 /*   return {
                        "Result": "OK"
                        ,"Records": [
                            { "personId":  1, "id": "a", "type": "Nick Name", "value": "JJ", "createDate": "01-02-03", "createBy": "123 do re mi" }
                            ,{ "personId": 2, "id": "b", "type": "Some Name", "value": "Ice Man", "createDate": "14-05-15", "createBy": "xyz abc" }
                        ]
                        //,"TotalRecordCount": 2
                    };*/
                }
                ,createAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var c = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.personId = c.originator.id;
                    rc.Record.type = record.type;
                    rc.Record.value = record.value;
                    rc.Record.created = Acm.getCurrentDay(); //record.created;
                    rc.Record.creator = App.getUserName();   //record.creator;
                    return rc;

                   /* return{
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Nick Name", "value": "Ice Man", "createDate": "01-02-03", "createBy": "test" }
                    };*/
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var c = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.personId = c.originator.id;
                    rc.Record.type = record.type;
                    rc.Record.value = record.value;
                    rc.Record.created = record.created;
                    rc.Record.creator = record.creator;
                    return rc;

                    /*return {
                        "Result": "OK"
                        ,"Record":
                        { "personId": 3, "id": "c", "type": "Nick Name", "value": "Big Man", "createDate": "01-02-03", "createBy": "test" }
                    };*/
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
                ,created: {
                    title: 'Date Added'
                    ,width: '20%'
                    //,type: 'date'
                    //,displayFormat: 'yy-mm-dd'
                    ,create: false
                    ,edit: false
                }
                ,creator: {
                    title: 'Added By'
                    ,width: '30%'
                }
            }
            ,recordAdded : function (event, data) {
                var record = data.record;
                var c = Complaint.getComplaint();
                var personAliases = c.originator.personAliases;
                var personAlias = {};
                personAlias.type = record.type;
                personAlias.value = record.value;
            }
            ,recordUpdated : function (event, data) {
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var record = data.record;
                var c = Complaint.getComplaint();
                var personAliases = c.originator.personAliases;
                var personAlias = personAliases[whichRow];
                personAlias.type = record.type;
                personAlias.value = record.value;
            }
            ,recordDeleted : function (event, data) {
                var r = data.row;
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var c = Complaint.getComplaint();
                var personAliases = c.originator.addresses;
                personAliases.splice(whichRow, 1);
            }
        }
        ,function (data) { //opened handler
            data.childTable.jtable('load');
        });
    }

    //----------------- end of People -----------------------


    //
    //----------------- Documents ------------------------------
    //
//    ,refreshJTableDocuments: function() {
//        AcmEx.Object.jTableLoad(this.$divDocuments);
//    }
    ,createJTableDocuments: function($s) {
        $s.jtable({
            title: 'Documents'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var c = Complaint.getComplaint();
                    if (c && c.childObjects) {
                        for (var i = 0; i < c.childObjects; i++) {
                            var childObject = c.childObjects[i];
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
//for test
//                    return {
//                        "Result": "OK"
//                        ,"Records": [
//                            {"id": 11, "title": "Nick Name", "created": "01-02-03", "creator": "123 do re mi", "status": "JJ"}
//                            ,{"id": 12, "title": "Some Name", "created": "14-05-15", "creator": "xyz abc", "status": "Ice Man"}
//                        ]
//                        ,"TotalRecordCount": 2
//                    };
                }
                ,createAction: function(postData, jtParams) {
                    //custom web form creation takes over; this action should never be called
                    var rc = {"Result": "OK", "Record": {id:0, title:"", created:"", creator:"", status:""}};
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
                    ,list: false
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
                ,created: {
                    title: 'Created'
                    ,width: '15%'
                    ,edit: false
                }
                ,creator: {
                    title: 'Creator'
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
            }
        });

        $s.jtable('load');
    }
    //----------------- end of Documents ----------------------


    //
    //------------------ Tasks ------------------
    //
    ,refreshJTableTasks: function() {
        AcmEx.Object.jTableLoad(this.$divTasks);
    }

    ,createJTableTasks: function($jt) {
        var sortMap = {};
        sortMap["title"] = "title_t";


        AcmEx.Object.jTableCreatePaging($jt
            ,{
                title: 'Tasks'
                ,selecting: true
                ,multiselect: false
                ,selectingCheckboxes: false

                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        return AcmEx.Object.jTableDefaultPagingListAction(postData, jtParams, sortMap
                            ,function() {
                                var url;
                                url =  App.getContextPath() + Complaint.Service.API_RETRIEVE_TASKS;
                                url += Complaint.getComplaintId();
                                return url;
                            }
                            ,function(data) {
                                var rc = {jtData: null, jtError: "Invalid task data"};
                                if (data && data.response && data.responseHeader && Acm.isNotEmpty(data.responseHeader.status)) {
                                    var responseHeader = data.responseHeader;
                                    if (0 == responseHeader.status) {
                                        //response.start should match to jtParams.jtStartIndex
                                        //response.docs.length should be <= jtParams.jtPageSize

                                        rc.jtData = AcmEx.Object.jTableGetEmptyRecords();
                                        var response = data.response;
                                        for (var i = 0; i < response.docs.length; i++) {
                                            var Record = {};
                                            Record.id = response.docs[i].object_id_s;
                                            Record.title = Acm.goodValue(response.docs[i].name); //title_t ?
                                            Record.created = Acm.getDateFromDatetime(response.docs[i].create_dt);
                                            Record.priority = "[priority]";
                                            Record.dueDate = "[due]";
                                            Record.status = Acm.goodValue(response.docs[i].status_s);
                                            Record.assignee = "[assignee]";
                                            rc.jtData.Records.push(Record);

                                        }
                                        rc.jtData.TotalRecordCount = Acm.goodValue(response.numFound, 0);
                                        rc.jtError = null;

                                    } else {
                                        if (Acm.isNotEmpty(data.error)) {
                                            rc.jtError = data.error.msg + "(" + data.error.code + ")";
                                        }
                                    }
                                }

                                return rc;
                            }
                        );
                    }

                    ,createAction: function(postData, jtParams) {
                        return AcmEx.Object.jTableGetEmptyRecord();
                    }
                }

                ,fields: {
                    id: {
                        title: 'ID'
                        ,key: true
                        ,list: true
                        ,create: false
                        ,edit: false
                        ,sorting: false
                    }
                    ,title: {
                        title: 'Title'
                        ,width: '30%'
                    }
                    ,created: {
                        title: 'Created'
                        ,width: '15%'
                        ,sorting: false
                    }
                    ,priority: {
                        title: 'Priority'
                        ,width: '10%'
                        ,sorting: false
                    }
                    ,dueDate: {
                        title: 'Due'
                        ,width: '15%'
                        ,sorting: false
                    }
                    ,status: {
                        title: 'status'
                        ,width: '10%'
                        ,sorting: false
                    }
                    ,description: {
                        title: 'Action'
                        ,width: '10%'
                        ,sorting: false
                        ,edit: false
                        ,create: false
                        ,display: function (commData) {
                            var $a = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-phone'></i></a>");
                            var $b = $("<a href='#' class='inline animated btn btn-default btn-xs' data-toggle='class:show'><i class='fa fa-book'></i></a>");

                            $a.click(function (e) {
                                Complaint.Event.onClickBtnTaskAssign(e);
                                e.preventDefault();
                            });
                            $b.click(function (e) {
                                Complaint.Event.onClickBtnTaskUnassign(e);
                                e.preventDefault();
                            });
                            return $a.add($b);
                        }
                    }
                } //end field
            } //end arg
            ,sortMap
        );
    }
    //---------------- end of Tasks --------------------------


};




