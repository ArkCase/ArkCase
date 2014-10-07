/**
 * CaseFile.JTable
 *
 * JTable
 *
 * @author jwu
 */
CaseFile.JTable = {
    initialize : function() {
    }


//    ,createJTablePerson: function($s) {
//
//    }


    ,createJTableRois: function($s) {
        $s.jtable({
            title: 'ROI'
            ,paging: false
            ,sorting: false
            ,actions: {
                listAction: function(postData,jtParams){
                    var rc = {"Result": "OK", "Records": [{id:0, itemNumber:"1", documentType:"ABC", scheduled:"08/09/1967", cancelled:"08/09/2014", completed:"...", reopened:"YES", contactState:"VA", creator:"ann-acm"}]};
                    return rc;

                }
                ,createAction: function(postData,jtParams){
                    //var record = Acm.urlToJson(postData);
                    var rc = {"Result": "OK", "Records": [{id:0, itemNumber:"1", documentType:"ABC", scheduled:"08/09/1967", cancelled:"08/09/2014", completed:"...", reopened:"YES", contactState:"VA", creator:"ann-acm"}]};
                    return rc;
                }
            }
            ,fields: {
                id:{
                    title:'ID'
                    ,key: true
                    ,list: false
                    ,create: false
                    ,edit: false
                }
                ,itemNumber: {
                    title: 'Item #'
                    ,width: '10%'
                }
                ,documentType: {
                    title: 'Type'
                    ,width: '10%'
                }
                ,scheduled: {
                    title: 'Scheduled'
                    ,width: '10%'
                }
                ,cancelled: {
                    title: 'Cancelled'
                    ,width: '10%'
                }
                ,reopened: {
                    title: 'Reopened'
                    ,width: '10%'
                }
                ,contactState: {
                    title: 'State'
                    ,width: '10%'
                }
                ,creator: {
                    title: 'Assignee'
                    ,width: '10%'
                }
            }
            ,recordAdded: function(event, data){
                $s.jtable('load');
            }

        });
        $s.jtable('load');
    }


    //
    //------------------ Person ------------------
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
    ,_createJTable4SubTable: function($s, arg) {
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
                    CaseFile.JTable._togglePersonDevices($s, $a);
                    e.preventDefault();
                });
                $b.click(function (e) {
                    CaseFile.JTable._togglePersonOrganizations($s, $b);
                    e.preventDefault();
                });
                $c.click(function (e) {
                    CaseFile.JTable._togglePersonLocations($s, $c);
                    e.preventDefault();
                });
                $d.click(function (e) {
                    CaseFile.JTable._togglePersonAliases($s, $d);
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

    ,_togglePersonDevices: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPersonDevices, this._closePersonDevices, CaseFile.PERSON_SUBTABLE_TITLE_DEVICES);
    }
    ,_togglePersonOrganizations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPersonOrganizations, this._closePersonOrganizations, CaseFile.PERSON_SUBTABLE_TITLE_ORGANIZATIONS);
    }
    ,_togglePersonLocations: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPersonLocations, this._closePersonLocations, CaseFile.PERSON_SUBTABLE_TITLE_LOCATIONS);
    }
    ,_togglePersonAliases: function($t, $row) {
        this._toggleSubJTable($t, $row, this._openPersonAliases, this._closePersonAliases, CaseFile.PERSON_SUBTABLE_TITLE_ALIASES);
    }


//    ,refreshJTablePerson: function() {
//        AcmEx.Object.jTableLoad(this.$divPerson);
//    }
    ,deferredGet: function(url, responseHandler, postData) {
        return $.Deferred(function ($dfd) {
            var arg = {
                url: url
                ,type: 'GET'
                ,dataType: 'json'
                ,success: function (data) {
                    if (data) {
                        var jtResponse = responseHandler(data);
                    }

                    if (jtResponse.jtData) {
                        $dfd.resolve(jtResponse.jtData);
                    } else {
                        $dfd.reject();
                        Acm.Dialog.error(jtResponse.jtError);
                    }
                }
                ,error: function () {
                    $dfd.reject();
                }
            };
            if (postData) {
                arg.data = postData;
            }
            $.ajax(arg);
        });
    }

    ,_closePersonDevices: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPersonDevices: function($t, $row) {
        $t.jtable('openChildTable'
            ,$row.closest('tr')
            ,{
                title: CaseFile.PERSON_SUBTABLE_TITLE_DEVICES
                ,sorting: true
                ,actions: {
                    listAction: function(postData, jtParams) {
                        var c = CaseFile.getCaseFile();
                        var subject = CaseFile.cacheSubject.get(c.subject.id);
                        var contactMethods = subject.contactMethods;
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
                        var c = CaseFile.getCaseFile();
                        var subject = CaseFile.cacheSubject.get(c.subject.id);
                        var rc = {"Result": "OK", "Record": {}};
                        rc.Record.personId = subject.id;
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
                        var c = CaseFile.getCaseFile();
                        var subject = CaseFile.cacheSubject.get(c.subject.id);
                        var rc = {"Result": "OK", "Record": {}};
                        rc.Record.personId = subject.id;
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
                        ,options: CaseFile.getDeviceTypes()
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
                    var c = CaseFile.getCaseFile();
                    var subject = CaseFile.cacheSubject.get(c.subject.id);
                    var contactMethods = subject.contactMethods;
                    var contactMethod = {};
                    contactMethod.id = parseInt(record.id);
                    contactMethod.type = record.type;
                    contactMethod.value = record.value;
                    //contactMethod.created = record.created;   //created,creator is readonly
                    //contactMethod.creator = record.creator;
                    contactMethods.push(contactMethod);

                    //todo: call serice to update subject
                }
                ,recordUpdated : function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var c = CaseFile.getCaseFile();
                    var subject = CaseFile.cacheSubject.get(c.subject.id);
                    var contactMethods = subject.contactMethods;
                    var contactMethod = contactMethods[whichRow];
                    contactMethod.type = record.type;
                    contactMethod.value = record.value;
                    //contactMethod.created = record.created;   //created,creator is readonly
                    //contactMethod.creator = record.creator;

                    //todo: call service to update subject
                }
                ,recordDeleted : function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var c = CaseFile.getCaseFile();
                    var subject = CaseFile.cacheSubject.get(c.subject.id);
                    var contactMethods = subject.contactMethods;
                    contactMethods.splice(whichRow, 1);

                    //todo: call service to update subject
                }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closePersonOrganizations: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPersonOrganizations: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: CaseFile.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
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
                    ,options: CaseFile.getOrganizationTypes()
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
    ,_closePersonLocations: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPersonLocations: function($t, $row) {
        $t.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: CaseFile.PERSON_SUBTABLE_TITLE_LOCATIONS
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    var c = CaseFile.getCaseFile();
                    var subject = CaseFile.cacheSubject.get(c.subject.id);
                    var addresses = subject.addresses;
                    var cnt = addresses.length;
                    var rc = {"Result": "OK", "Records": []};
                    for(i = 0; i<cnt; i++){
                        rc.Records.push({personId: subject.id
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
                    var c = CaseFile.getCaseFile();
                    var subject = CaseFile.cacheSubject.get(c.subject.id);
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.personId = subject.id;
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
                    var c = CaseFile.getCaseFile();
                    var subject = CaseFile.cacheSubject.get(c.subject.id);
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.personId = subject.id;
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
                    ,options: CaseFile.getLocationTypes()
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
                var c = CaseFile.getCaseFile();
                var subject = CaseFile.cacheSubject.get(c.subject.id);
                var locations = subject.addresses;
                var location = {};
                location.type = record.type;
                location.streetAddress = record.streetAddress;
                location.city = record.city;
                location.state = record.state;
                location.zip = record.zip;
                locations.push(location);

                //call service to update subject
            }
                ,recordUpdated : function (event, data) {
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var record = data.record;
                var c = CaseFile.getCaseFile();
                var subject = CaseFile.cacheSubject.get(c.subject.id);
                var locations = subject.addresses;
                var location = locations[whichRow];
                location.type = record.type;
                location.streetAddress = record.streetAddress;
                location.city = record.city;
                location.state = record.state;
                location.zip = record.zip;

                //call service to update subject
            }
                ,recordDeleted : function (event, data) {
                var r = data.row;
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var c = CaseFile.getCaseFile();
                var subject = CaseFile.cacheSubject.get(c.subject.id);
                var locations = subject.addresses;
                locations.splice(whichRow, 1);

                //todo: call service to update subject
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    ,_closePersonAliases: function($jt, $row) {
        $jt.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPersonAliases: function($jt, $row) {
        $jt.jtable('openChildTable',
            $row.closest('tr'),
            {
                title: CaseFile.PERSON_SUBTABLE_TITLE_ALIASES
                //,paging: true
                //,pageSize: 10
                ,sorting: true
                ,actions: {
                listAction: function(postData, jtParams) {
                    var c = CaseFile.getCaseFile();
                    var subject = CaseFile.cacheSubject.get(c.subject.id);
                    var personAliases = subject.personAliases;
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
                    var c = CaseFile.getCaseFile();
                    var subject = CaseFile.cacheSubject.get(c.subject.id);
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.personId = subject.id;
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
                    var c = CaseFile.getCaseFile();
                    var subject = CaseFile.cacheSubject.get(c.subject.id);
                    var rc = {"Result": "OK", "Record": {}};
                    rc.Record.personId = subject.id;
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
                    ,options: CaseFile.getAliasTypes()
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
                var c = CaseFile.getCaseFile();
                var subject = CaseFile.cacheSubject.get(c.subject.id);
                var personAliases = subject.personAliases;
                var personAlias = {};
                personAlias.type = record.type;
                personAlias.value = record.value;
            }
                ,recordUpdated : function (event, data) {
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var record = data.record;
                var c = CaseFile.getCaseFile();
                var subject = CaseFile.cacheSubject.get(c.subject.id);
                var personAliases = subject.personAliases;
                var personAlias = personAliases[whichRow];
                personAlias.type = record.type;
                personAlias.value = record.value;
            }
                ,recordDeleted : function (event, data) {
                var r = data.row;
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var c = CaseFile.getCaseFile();
                var subject = CaseFile.cacheSubject.get(c.subject.id);
                var personAliases = subject.addresses;
                personAliases.splice(whichRow, 1);
            }
            }
            ,function (data) { //opened handler
                data.childTable.jtable('load');
            });
    }
    //----------------- end of Person -----------------------
};




