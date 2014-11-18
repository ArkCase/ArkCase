
/**
 * Complaint.JTable
 *
 * JTable
 *
 * @author jwu
 */
Complaint.JTable = {
    create : function() {
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

    ,_createJTable4SubTablePeople: function($s, arg) {
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
                    Complaint.JTable._togglePeopleDevices($s, $a);
                    e.preventDefault();
                });
                $b.click(function (e) {
                    Complaint.JTable._togglePeopleOrganizations($s, $b);
                    e.preventDefault();
                });
                $c.click(function (e) {
                    Complaint.JTable._togglePeopleLocations($s, $c);
                    e.preventDefault();
                });
                $d.click(function (e) {
                    Complaint.JTable._togglePeopleAliases($s, $d);
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

    ,_updatePersonAssociation: function(complaint){
        if(complaint.originator){
            var originator = complaint.originator;
            if (complaint.personAssociations )
            {
                for ( var i = 0; i < complaint.personAssociations.length; i++)
                {
                    var currentPerson = complaint.personAssociations[i];
                    if ( currentPerson.id == originator.id )
                    {
                        complaint.personAssociations[i] = originator;
                    }
                }
            }
        }
    }
    ,createJTableInitiator: function($s) {
        this._createJTable4SubTable($s, {

            title: 'Initiator'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var complaint = Complaint.getComplaint();
                    if(complaint){
                        if(complaint.originator && complaint.originator.id && complaint.originator.person)
                        {
                            var originator = complaint.originator;
                            var originatorPerson = originator.person;
                                rc.Records.push({
                                    personId: originatorPerson.id
                                    ,title: originatorPerson.title
                                    ,givenName: originatorPerson.givenName
                                    ,familyName: originatorPerson.familyName
                                    ,personType: originator.personType
                                    ,personDescription: originator.personDescription
                                });
                            }
                    }
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var complaint = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    if (complaint) {
                        if(complaint.originator && complaint.originator.person){
                            rc.Record.title = record.title;
                            rc.Record.givenName = record.givenName;
                            rc.Record.familyName = record.familyName;
                            rc.Record.personType = record.personType;
                            rc.Record.personDescription = record.personDescription;
                        }
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
                ,personType: {
                    title: 'Type'
                    ,edit: false
                }
                ,personDescription: {
                    title: 'Description'
                    ,type: 'textarea'
                    ,width: '30%'
                }
            }
            ,recordUpdated: function(event, data){
                var record = data.record;
                var complaint = Complaint.getComplaint();
                if (complaint.originator && complaint.originator.person) {
                    var originator = complaint.originator;
                    var originatorPerson = complaint.originator.person;
                    var originatorPersonId = 0;
                    if (originatorPerson) {
                        originatorPersonId = originatorPerson.id;
                    }

                    originator.personType = record.personType;
                    originator.personDescription = record.personDescription;
                    originatorPerson.title = record.title;
                    originatorPerson.givenName = record.givenName;
                    originatorPerson.familyName = record.familyName;

                    //copy over originator to personAssoc in order to update the complaint
                    //this is the way it works at the moment
                    Complaint.JTable._updatePersonAssociation(complaint);

                    Complaint.Service.saveComplaint(complaint);
                }
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
            , {
                title: Complaint.PERSON_SUBTABLE_TITLE_DEVICES
                ,sorting: true
                ,messages: {
                    addNewRecord: 'Add Device'
                }
                ,actions: {
                    listAction: function (postData, jtParams) {
                        var index = $row.closest('tr');
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                if (originatorPerson.contactMethods) {
                                    var contactMethods = originatorPerson.contactMethods;
                                    var cnt = contactMethods.length;
                                    for (var i = 0; i < cnt; i++) {
                                        rc.Records.push({
                                            personId: originatorPerson.id,
                                            type: contactMethods[i].type,
                                            value: Acm.goodValue(contactMethods[i].value),
                                            created: Acm.getDateFromDatetime(contactMethods[i].created),
                                            creator: contactMethods[i].creator
                                        });
                                    }
                                }
                            }
                        }
                        return rc;
                    }, createAction: function (postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                rc.Record.personId = originatorPerson.id;
                                rc.Record.type = record.type;
                                rc.Record.value = Acm.goodValue(record.value);
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                        }
                        return rc;
                    }, updateAction: function (postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                rc.Record.type = record.type;
                                rc.Record.value = Acm.goodValue(record.value);
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                        }
                        return rc;
                    }, deleteAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }, fields: {
                    personId: {
                        key: false, create: false, edit: false, list: false
                    }, type: {
                        title: 'Type', width: '15%', options: Complaint.getDeviceTypes()
                    }, value: {
                        title: 'Value', width: '30%'
                    }, created: {
                        title: 'Date Added', width: '20%', create: false, edit: false
                    }, creator: {
                        title: 'Added By', width: '30%', create: false, edit: false
                    }
                }, recordAdded: function (event, data) {
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.contactMethods) {
                                var contactMethods = originatorPerson.contactMethods;
                                var contactMethod = {};
                                contactMethod.type = record.type;
                                contactMethod.value = Acm.goodValue(record.value);
                                contactMethods.push(contactMethod);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
                    }
                }, recordUpdated: function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.contactMethods) {
                                var contactMethods = originatorPerson.contactMethods;
                                var contactMethod = contactMethods[whichRow];
                                contactMethod.type = record.type;
                                contactMethod.value = Acm.goodValue(record.value);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
                    }
                }, recordDeleted: function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.contactMethods) {
                                var contactMethods = originatorPerson.contactMethods;
                                contactMethods.splice(whichRow, 1);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
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
            , {
                title: Complaint.PERSON_SUBTABLE_TITLE_ORGANIZATIONS
                ,sorting: true
                ,messages: {
                    addNewRecord: 'Add Organization'
                }
                ,actions: {
                    listAction: function (postData, jtParams) {
                        var index = $row.closest('tr');
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                if (originatorPerson.organizations) {
                                    var organizations = originatorPerson.organizations;
                                    var cnt = organizations.length;
                                    for (var i = 0; i < cnt; i++) {
                                        rc.Records.push({
                                            personId: originatorPerson.id,
                                            type: organizations[i].organizationType,
                                            value: Acm.goodValue(organizations[i].organizationValue),
                                            created: Acm.getDateFromDatetime(organizations[i].created),
                                            creator: organizations[i].creator
                                        });
                                    }
                                }
                            }
                        }
                        return rc;
                    }, createAction: function (postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                rc.Record.personId = originatorPerson.id;
                                rc.Record.type = record.type;
                                rc.Record.value = Acm.goodValue(record.value);
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                        }
                        return rc;
                    }, updateAction: function (postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                rc.Record.type = record.type;
                                rc.Record.value = Acm.goodValue(record.value);
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                        }
                        return rc;
                    }, deleteAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }, fields: {
                    personId: {
                        type: 'hidden', defaultValue: 1 //commData.record.StudentId
                    }, id: {
                        key: true, create: false, edit: false, list: false
                    }, type: {
                        title: 'Type', width: '15%'
                        , options: Complaint.getOrganizationTypes()
                    }, value: {
                        title: 'Value', width: '30%'
                    }, created: {
                        title: 'Date Added', width: '20%', create: false, edit: false
                    }, creator: {
                        title: 'Added By', width: '30%',create: false,edit: false
                    }
                }, recordAdded: function (event, data) {
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.organizations) {
                                var organizations = originatorPerson.organizations;
                                var organization = {};
                                organization.organizationType = record.type;
                                organization.organizationValue = Acm.goodValue(record.value);
                                organizations.push(organization);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
                    }
                }, recordUpdated: function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.organizations) {
                                var organizations = originatorPerson.organizations;
                                var organization = organizations[whichRow];
                                organization.organizationType = record.type;
                                organization.organizationValue = Acm.goodValue(record.value);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
                    }
                }, recordDeleted: function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.organizations) {
                                var organizations = originatorPerson.organizations;
                                organizations.splice(whichRow, 1);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
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
            , {
                title: Complaint.PERSON_SUBTABLE_TITLE_LOCATIONS
                ,sorting: true
                ,messages: {
                    addNewRecord: 'Add Location'
                }
                ,actions: {
                    listAction: function (postData, jtParams) {
                        var index = $row.closest('tr');
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                if (originatorPerson.addresses) {
                                    var addresses = originatorPerson.addresses;
                                    var cnt = addresses.length;
                                    for (var i = 0; i < cnt; i++) {
                                        rc.Records.push({
                                            personId: originatorPerson.id,
                                            type: addresses[i].type,
                                            streetAddress: addresses[i].streetAddress,
                                            city: addresses[i].city,
                                            state: addresses[i].state,
                                            zip: Acm.goodValue(addresses[i].zip),
                                            country: addresses[i].country,
                                            created: Acm.getDateFromDatetime(addresses[i].created),
                                            creator: addresses[i].creator
                                        });
                                    }
                                }
                            }
                        }
                        return rc;
                    }, createAction: function (postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                rc.Record.personId = originatorPerson.id;
                                rc.Record.type = record.type;
                                rc.Record.streetAddress = record.streetAddress;
                                rc.Record.city = record.city;
                                rc.Record.state = record.state;
                                rc.Record.country = record.country;
                                rc.Record.zip = Acm.goodValue(record.zip);
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                        }
                        return rc;
                    }, updateAction: function (postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                rc.Record.personId = originatorPerson.id;
                                rc.Record.type = record.type;
                                rc.Record.streetAddress = record.streetAddress;
                                rc.Record.city = record.city;
                                rc.Record.state = record.state;
                                rc.Record.zip = Acm.goodValue(record.zip);
                                rc.Record.country = record.country;
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                        }
                        return rc;
                    }, deleteAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }, fields: {
                    personId: {
                        type: 'hidden', defaultValue: 1 //commData.record.StudentId
                    }, type: {
                        title: 'Type', width: '8%', options: Complaint.getLocationTypes()
                    }, streetAddress: {
                        title: 'Address', width: '20%'
                    }, city: {
                        title: 'City', width: '10%'
                    }, state: {
                        title: 'State', width: '8%'
                    }, zip: {
                        title: 'Zip', width: '8%'
                    }, country: {
                        title: 'Country', width: '8%'
                    }, created: {
                        title: 'Date Added', width: '15%', create: false, edit: false
                    }, creator: {
                        title: 'Added By', width: '15%',create: false, edit: false
                    }
                }, recordAdded: function (event, data) {
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.addresses) {
                                var addresses = originatorPerson.addresses;
                                var address = {};
                                address.type = record.type;
                                address.streetAddress = record.streetAddress;
                                address.city = record.city;
                                address.state = record.state;
                                address.country = record.country;
                                address.zip = record.zip;
                                addresses.push(address);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
                    }
                }, recordUpdated: function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            if (complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                if (originatorPerson.addresses) {
                                    var addresses = originatorPerson.addresses;
                                    var address = addresses[whichRow];
                                    address.type = record.type;
                                    address.streetAddress = record.streetAddress;
                                    address.city = record.city;
                                    address.country = record.country;
                                    address.state = record.state;
                                    address.zip = record.zip;

                                    //copy over originator to personAssoc in order to update the complaint
                                    //this is the way it works at the moment
                                    Complaint.JTable._updatePersonAssociation(complaint);

                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
                }, recordDeleted: function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.addresses) {
                                var addresses = originatorPerson.addresses;
                                addresses.splice(whichRow, 1);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
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
            , {
                title: Complaint.PERSON_SUBTABLE_TITLE_ALIASES
                ,sorting: true
                ,messages: {
                    addNewRecord: 'Add Alias'
                }
                ,actions: {
                    listAction: function (postData, jtParams) {
                        var index = $row.closest('tr');
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                if (originatorPerson.personAliases) {
                                    var personAliases = originatorPerson.personAliases;
                                    var cnt = personAliases.length;
                                    for (var i = 0; i < cnt; i++) {
                                        rc.Records.push({
                                            personId: originatorPerson.id,
                                            type: personAliases[i].aliasType,
                                            value: Acm.goodValue(personAliases[i].aliasValue),
                                            created: Acm.getDateFromDatetime(personAliases[i].created),
                                            creator: personAliases[i].creator
                                        });
                                    }
                                }
                            }
                        }
                        return rc;
                    }, createAction: function (postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                rc.Record.personId = originatorPerson.id;
                                rc.Record.type = record.type;
                                rc.Record.value = Acm.goodValue(record.value);
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                        }
                        return rc;
                    }, updateAction: function (postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.originator && complaint.originator.person) {
                                var originatorPerson = complaint.originator.person;
                                rc.Record.personId = originatorPerson.id;
                                rc.Record.type = record.type;
                                rc.Record.value = Acm.goodValue(record.value);
                                rc.Record.created = Acm.getCurrentDay(); //record.created;
                                rc.Record.creator = App.getUserName();   //record.creator;
                            }
                        }
                        return rc;
                    }, deleteAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }, fields: {
                    aliasId: {
                        type: 'hidden', defaultValue: 1 //commData.record.StudentId
                    }, type: {
                        title: 'Type', width: '15%', options: Complaint.getAliasTypes()
                    }, value: {
                        title: 'Value', width: '30%'
                    }, created: {
                        title: 'Date Added', width: '20%', create: false, edit: false
                    }, creator: {
                        title: 'Added By', width: '30%',create: false, edit: false
                    }
                }, recordAdded: function (event, data) {
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.personAliases) {
                                var personAliases = originatorPerson.personAliases;
                                var personAlias = {};
                                personAlias.aliasType = record.type;
                                personAlias.aliasValue = Acm.goodValue(record.value);
                                personAliases.push(personAlias);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
                    }
                }, recordUpdated: function (event, data) {
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    var rc = {"Result": "OK", "Record": {}};
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.personAliases) {
                                if (originatorPerson.personAliases) {
                                    var personAliases = originatorPerson.personAliases;
                                    var personAlias = personAliases[whichRow];
                                    personAlias.aliasType = record.type;
                                    personAlias.aliasValue = Acm.goodValue(record.value);

                                    //copy over originator to personAssoc in order to update the complaint
                                    //this is the way it works at the moment
                                    Complaint.JTable._updatePersonAssociation(complaint);

                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
                }, recordDeleted: function (event, data) {
                    var r = data.row;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.originator && complaint.originator.person) {
                            var originatorPerson = complaint.originator.person;
                            if (originatorPerson.personAliases) {
                                var personAliases = originatorPerson.personAliases;
                                personAliases.splice(whichRow, 1);

                                //copy over originator to personAssoc in order to update the complaint
                                //this is the way it works at the moment
                                Complaint.JTable._updatePersonAssociation(complaint);

                                Complaint.Service.saveComplaint(complaint);
                            }
                        }
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

    ,_updateOriginatorRecord: function (personAssociationRecord, complaint)
    {
        //if an originator record has been modified/added, copy it to the complaint.originator
        //this is the way it works at the moment
        if(personAssociationRecord){
            if(personAssociationRecord.personType == 'Initiator'){
                if(complaint){
                    var currentPerson = personAssociationRecord;
                    complaint.originator = currentPerson;
                    Complaint.Object.refreshJTableInitiator();
                }
            }
        }
    }
    ,_getNewPersonAssociationRecord: function() {
        return {
            id: null
            ,personType: ""
            ,parentId:null
            ,parentType:""
            ,personDescription: ""
            ,notes:""
            ,person:{
                id: null
                ,title: ""
                ,givenName: ""
                ,familyName: ""
                ,company: ""
                /*,hairColor:""
                ,eyeColor:""
                ,heightInInches:null*/
                ,weightInPounds:null
                /*,dateOfBirth:null
                ,dateMarried:null*/
                ,addresses: []
                ,contactMethods: []
                ,securityTags: []
                ,personAliases: []
                ,organizations: []
            }
        };
    }
    ,_findPersonAssoc: function(personId,personAssociations) {
        var personAssoc;
        for (var i = 0; i < personAssociations.length; i++) {
            if (personId == personAssociations[i].person.id) {
                personAssoc = personAssociations[i];
                break;
            }
        }
        return personAssoc;
    }

    ,createJTablePeople: function($s) {
        this._createJTable4SubTablePeople($s, {
            title: 'People'
            ,paging: false
            ,messages: {
                addNewRecord: 'Add Person'
            }
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var complaint = Complaint.getComplaint();
                    if(complaint && complaint.originator){
                        if(complaint.personAssociations){
                            var personAssociations = complaint.personAssociations;
                            var cnt = personAssociations.length;
                            for (var i = 0; i < cnt; i++) {
                                if(personAssociations[i].id != complaint.originator.id){
                                    var person = personAssociations[i].person;
                                    rc.Records.push({
                                        personId: person.id
                                        ,title: person.title
                                        ,givenName: person.givenName
                                        ,familyName: person.familyName
                                        ,personType: personAssociations[i].personType
                                        ,personDescription: personAssociations[i].personDescription
                                    });
                                }
                            }
                        }
                    }
                    return rc;
                }
                ,createAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations){
                            rc.Record.title = record.title;
                            rc.Record.givenName = record.givenName;
                            rc.Record.familyName = record.familyName;
                            rc.Record.personType = record.personType;
                            rc.Record.personDescription = record.personDescription;
                        }
                    }
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            rc.Record.title = record.title;
                            rc.Record.givenName = record.givenName;
                            rc.Record.familyName = record.familyName;
                            rc.Record.personType = record.personType;
                            rc.Record.personDescription = record.personDescription;
                        }
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
                ,personType: {
                    title: 'Type'
                    //,options: App.getContextPath() + '/api/latest/plugin/complaint/types'
                    ,options: Complaint.getPersonTypesModifiable()
                }
                ,personDescription: {
                    title: 'Description'
                    ,type: 'textarea'
                    ,width: '30%'
                }
            }
            ,recordAdded: function(event, data){
                var record = data.record;
                var complaint = Complaint.getComplaint();
                if (complaint) {
                    if (complaint.personAssociations) {
                        var newPersonAssociationRecord = Complaint.JTable._getNewPersonAssociationRecord();
                        newPersonAssociationRecord.parentType = App.OBJTYPE_COMPLAINT;
                        newPersonAssociationRecord.parentId = complaint.complaintId;
                        newPersonAssociationRecord.personType = record.personType;
                        newPersonAssociationRecord.personDescription = record.personDescription;
                        newPersonAssociationRecord.person.title = record.title;
                        newPersonAssociationRecord.person.givenName = record.givenName;
                        newPersonAssociationRecord.person.familyName = record.familyName;
                        Complaint.Service.savePersonAssociation(newPersonAssociationRecord);
                    }
                }
             }

            ,recordUpdated: function(event, data){
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var record = data.record;
                var complaint = Complaint.getComplaint();
                if (complaint) {
                    if (complaint.personAssociations) {
                        var personAssociations = complaint.personAssociations;
                        if(personAssociations[whichRow].person){
                            var person = personAssociations[whichRow].person;
                            personAssociations[whichRow].personType = record.personType;
                            personAssociations[whichRow].personDescription = record.personDescription;
                            person.title = record.title;
                            person.givenName = record.givenName;
                            person.familyName = record.familyName;
                            Complaint.Service.saveComplaint(complaint);
                        }
                    }
                }
            }
            ,recordDeleted: function(event,data) {
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var record = data.record;
                var complaint = Complaint.getComplaint();
                if (complaint) {
                    if (complaint.personAssociations) {
                        var personAssociations = complaint.personAssociations;
                        if (personAssociations[whichRow]) {
                            var personAssocId = personAssociations[whichRow].id;
                            Complaint.Service.deletePersonAssociationById(personAssocId);
                        }
                    }
                }
            }
        });
    }

    ,_closePeopleDevices: function($t, $row) {
        $t.jtable('closeChildTable', $row.closest('tr'));
    }
    ,_openPeopleDevices: function($t, $row) {
        $t.jtable('openChildTable'
            ,$row.closest('tr')
            , {
                title: Complaint.PERSON_SUBTABLE_TITLE_DEVICES, sorting: true, messages: {
                    addNewRecord: 'Add Device'
                }, actions: {
                    listAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;

                            //var rowId = $row.closest('tr')[0].rowIndex;
                            //var rowId = $row.closest('tr').index();
                            //var rowIndex = rowId - 1;
                            //var rowIndex = 0;
                            //var rowIndex = $row
//                            .closest('tr') // closest tr parent element
//                            .prevAll() // all sibling elements in front of it
//                            .length; // find their count
                            var complaint = Complaint.getComplaint();
                            if (complaint) {
                                if (complaint.personAssociations) {
                                    var personAssociations = complaint.personAssociations;
                                    var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        if (person.contactMethods) {
                                            var contactMethods = person.contactMethods;
                                            var cnt = contactMethods.length;
                                            for (var i = 0; i < cnt; i++) {
                                                rc.Records.push({
                                                    personId: person.id,
                                                    type: contactMethods[i].type,
                                                    value: Acm.goodValue(contactMethods[i].value),
                                                    created: Acm.getDateFromDatetime(contactMethods[i].created),
                                                    creator: contactMethods[i].creator
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return rc;
                    }, createAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var record = Acm.urlToJson(postData);
                            var complaint = Complaint.getComplaint();
                            if (complaint) {
                                if (complaint.personAssociations) {
                                    var personAssociations = complaint.personAssociations;
                                    var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        rc.Record.personId = person.id;
                                        rc.Record.type = record.type;
                                        rc.Record.value = Acm.goodValue(record.value);
                                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                                        rc.Record.creator = App.getUserName();   //record.creator;
                                    }
                                }
                            }
                        }
                        return rc;
                    }, updateAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecord();
                        var recordParent = $row.closest('tr').data('record');
                        if (recordParent && recordParent.personId) {
                            var personId = recordParent.personId;
                            var record = Acm.urlToJson(postData);
                            var complaint = Complaint.getComplaint();
                            if (complaint) {
                                if (complaint.personAssociations) {
                                    var personAssociations = complaint.personAssociations;
                                    var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                    if (currentPersonAssoc.person) {
                                        var person = currentPersonAssoc.person;
                                        rc.Record.personId = person.id;
                                        rc.Record.type = record.type;
                                        rc.Record.value = Acm.goodValue(record.value);
                                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                                        rc.Record.creator = App.getUserName();   //record.creator;
                                    }
                                }
                            }
                        }
                        return rc;
                    }, deleteAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }, fields: {
                    personId: {
                        key: false, create: false, edit: false, list: false
                    }, id: {
                        key: false, type: 'hidden', edit: false, defaultValue: 0
                    }, type: {
                        title: 'Type', width: '15%', options: Complaint.getDeviceTypes()
                    }, value: {
                        title: 'Value', width: '30%'
                    }, created: {
                        title: 'Date Added', width: '20%', create: false, edit: false
                        //,type: 'date'
                        //,displayFormat: 'yy-mm-dd'
                    }, creator: {
                        title: 'Added By', width: '30%', create: false, edit: false
                    }
                }, recordAdded: function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = data.record;
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.contactMethods) {
                                        var contactMethods = person.contactMethods;
                                        var contactMethod = {};
                                        contactMethod.type = record.type;
                                        contactMethod.value = Acm.goodValue(record.value);
                                        contactMethods.push(contactMethod);
                                        Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }, recordUpdated: function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var record = data.record;
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.contactMethods) {
                                        var contactMethods = person.contactMethods;
                                        var contactMethod = contactMethods[whichRow];
                                        contactMethod.type = record.type;
                                        contactMethod.value = Acm.goodValue(record.value);
                                        Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
                    }
                }, recordDeleted: function (event, data) {
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.contactMethods) {
                                        var contactMethods = person.contactMethods;
                                        contactMethods.splice(whichRow, 1);
                                        Complaint.Service.saveComplaint(complaint);
                                    }
                                }
                            }
                        }
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
                ,sorting: true
                ,messages: {
                    addNewRecord: 'Add Organization'
                }
                ,actions: {
                listAction: function (postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.organizations) {
                                        var organizations = person.organizations;
                                        var cnt = organizations.length;
                                        for (var i = 0; i < cnt; i++) {
                                            rc.Records.push({
                                                personId: person.id,
                                                type: organizations[i].organizationType,
                                                value: Acm.goodValue(organizations[i].organizationValue),
                                                created: Acm.getDateFromDatetime(organizations[i].created),
                                                creator: organizations[i].creator
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return rc;
                }
                , createAction: function (postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = Acm.urlToJson(postData);
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    rc.Record.personId = person.id;
                                    rc.Record.type = record.type;
                                    rc.Record.value = Acm.goodValue(record.value);
                                    rc.Record.created = Acm.getCurrentDay(); //record.created;
                                    rc.Record.creator = App.getUserName();   //record.creator;
                                }
                            }
                        }
                    }
                    return rc;
                }
                , updateAction: function (postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = Acm.urlToJson(postData);
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    rc.Record.personId = person.id;
                                    rc.Record.type = record.type;
                                    rc.Record.value = Acm.goodValue(record.value);
                                    rc.Record.created = Acm.getCurrentDay(); //record.created;
                                    rc.Record.creator = App.getUserName();   //record.creator;
                                }
                            }
                        }
                    }
                    return rc;
                }
                , deleteAction: function (postData, jtParams) {
                    return {
                        "Result": "OK"
                    };
                }
            }
            , fields: {
                personId: {
                    type: 'hidden',
                    defaultValue: 1 //commData.record.StudentId
                }
                , id: {
                    key: true,
                    create: false,
                    edit: false,
                    list: false
                }
                , type: {
                    title: 'Type',
                    width: '15%',
                    options: Complaint.getOrganizationTypes()
                }
                , value: {
                    title: 'Value',
                    width: '30%'
                }
                , created: {
                    title: 'Date Added',
                    width: '20%',
                    create: false,
                    edit: false
                }
                , creator: {
                    title: 'Added By',
                    width: '30%',
                    create: false,
                    edit: false
                }
            }
            , recordAdded: function (event, data) {
                var recordParent = $row.closest('tr').data('record');
                if (recordParent && recordParent.personId) {
                    var personId = recordParent.personId;
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            var personAssociations = complaint.personAssociations;
                            var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                            if (currentPersonAssoc.person) {
                                var person = currentPersonAssoc.person;
                                if (person.organizations) {
                                    var organizations = person.organizations;
                                    var organization = {};
                                    organization.organizationType = record.type;
                                    organization.organizationValue = Acm.goodValue(record.value);
                                    organizations.push(organization);
                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
                }
            }
            , recordUpdated: function (event, data) {
                var recordParent = $row.closest('tr').data('record');
                if (recordParent && recordParent.personId) {
                    var personId = recordParent.personId;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            var personAssociations = complaint.personAssociations;
                            var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                            if (currentPersonAssoc.person) {
                                var person = currentPersonAssoc.person;
                                if (person.organizations) {
                                    var organizations = person.organizations;
                                    var organization = organizations[whichRow];
                                    organization.organizationType = record.type;
                                    organization.organizationValue = Acm.goodValue(record.value);
                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
                }
            }
            , recordDeleted: function (event, data) {
                var recordParent = $row.closest('tr').data('record');
                if (recordParent && recordParent.personId) {
                    var personId = recordParent.personId;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            var personAssociations = complaint.personAssociations;
                            var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                            if (currentPersonAssoc.person) {
                                var person = currentPersonAssoc.person;
                                if (person.organizations) {
                                    var organizations = person.organizations;
                                    organizations.splice(whichRow, 1);
                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
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
                ,sorting: true
                ,messages: {
                    addNewRecord: 'Add Location'
                }
                ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.addresses) {
                                        var addresses = person.addresses;
                                        var cnt = addresses.length;
                                        if(cnt > 0) {
                                            for (var i = 0; i < cnt; i++) {
                                                rc.Records.push({
                                                    personId: person.id,
                                                    type: addresses[i].type,
                                                    streetAddress: addresses[i].streetAddress,
                                                    city: addresses[i].city,
                                                    state: addresses[i].state,
                                                    zip: Acm.goodValue(addresses[i].zip),
                                                    created: Acm.getDateFromDatetime(addresses[i].created),
                                                    creator: addresses[i].creator
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return rc;
                }
                ,createAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = Acm.urlToJson(postData);
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    rc.Record.personId = person.id;
                                    rc.Record.type = record.type;
                                    rc.Record.streetAddress = record.streetAddress;
                                    rc.Record.city = record.city;
                                    rc.Record.state = record.state;
                                    rc.Record.zip = record.zip;
                                    rc.Record.created = Acm.getCurrentDay(); //record.created;
                                    rc.Record.creator = App.getUserName();   //record.creator;
                                }
                            }
                        }
                    }
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = Acm.urlToJson(postData);
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    rc.Record.personId = person.id;
                                    rc.Record.type = record.type;
                                    rc.Record.streetAddress = record.streetAddress;
                                    rc.Record.city = record.city;
                                    rc.Record.state = record.state;
                                    rc.Record.zip = record.zip;
                                    rc.Record.created = record.created;
                                    rc.Record.creator = record.creator;
                                }
                            }
                        }
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
                    ,create: false
                    ,edit: false
                }
                ,creator: {
                    title: 'Added By'
                    ,width: '15%'
                    ,create: false
                    ,edit: false
                }
            }
            ,recordAdded : function (event, data) {
                var recordParent = $row.closest('tr').data('record');
                if (recordParent && recordParent.personId) {
                    var personId = recordParent.personId;
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            var personAssociations = complaint.personAssociations;
                            var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                            if (currentPersonAssoc.person) {
                                var person = currentPersonAssoc.person;
                                if (person.addresses) {
                                    var addresses = person.addresses;
                                    var address = {};
                                    address.type = record.type;
                                    address.streetAddress = record.streetAddress;
                                    address.city = record.city;
                                    address.state = record.state;
                                    address.zip = record.zip;
                                    addresses.push(address);
                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
                }
            }
            ,recordUpdated : function (event, data) {
                var recordParent = $row.closest('tr').data('record');
                if (recordParent && recordParent.personId) {
                    var personId = recordParent.personId;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            var personAssociations = complaint.personAssociations;
                            var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                            if (currentPersonAssoc.person) {
                                var person = currentPersonAssoc.person;
                                if (person.addresses) {
                                    var addresses = person.addresses;
                                    var address = addresses[whichRow];
                                    address.type = record.type;
                                    address.streetAddress = record.streetAddress;
                                    address.city = record.city;
                                    address.state = record.state;
                                    address.zip = record.zip;
                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
                }
            }
            ,recordDeleted : function (event, data) {
                var recordParent = $row.closest('tr').data('record');
                if (recordParent && recordParent.personId) {
                    var personId = recordParent.personId;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            var personAssociations = complaint.personAssociations;
                            var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                            if (currentPersonAssoc.person) {
                                var person = currentPersonAssoc.person;
                                if (person.addresses) {
                                    var addresses = person.addresses;
                                    addresses.splice(whichRow, 1);
                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
                }
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
                ,sorting: true
                ,messages: {
                    addNewRecord: 'Add Alias'
                }
                ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    if (person.personAliases) {
                                        var personAliases = person.personAliases;
                                        var cnt = personAliases.length;
                                        if(cnt > 0)
                                        {
                                            for (var i = 0; i < cnt; i++) {
                                                rc.Records.push({
                                                    personId: person.id,
                                                    type: personAliases[i].aliasType,
                                                    value: Acm.goodValue(personAliases[i].aliasValue),
                                                    created: Acm.getDateFromDatetime(personAliases[i].created),
                                                    creator: personAliases[i].creator
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return rc;
                }
                ,createAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = Acm.urlToJson(postData);
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            //var assocId = complaint.originator.id;
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    rc.Record.personId = person.id;
                                    rc.Record.type = record.type;
                                    rc.Record.value = Acm.goodValue(record.value);
                                    rc.Record.created = Acm.getCurrentDay(); //record.created;
                                    rc.Record.creator = App.getUserName();   //record.creator;
                                }
                            }
                        }
                    }
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var recordParent = $row.closest('tr').data('record');
                    if (recordParent && recordParent.personId) {
                        var personId = recordParent.personId;
                        var record = Acm.urlToJson(postData);
                        var complaint = Complaint.getComplaint();
                        if (complaint) {
                            if (complaint.personAssociations) {
                                var personAssociations = complaint.personAssociations;
                                var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                                if (currentPersonAssoc.person) {
                                    var person = currentPersonAssoc.person;
                                    rc.Record.personId = person.id;
                                    rc.Record.type = record.type;
                                    rc.Record.value = Acm.goodValue(record.value);
                                    rc.Record.created = Acm.getCurrentDay(); //record.created;
                                    rc.Record.creator = App.getUserName();   //record.creator;
                                }
                            }
                        }
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
                    ,create: false
                    ,edit: false
                }
                ,creator: {
                    title: 'Added By'
                    ,width: '30%'
                    ,create: false
                    ,edit: false
                }
            }
            ,recordAdded : function (event, data) {
                var recordParent = $row.closest('tr').data('record');
                if (recordParent && recordParent.personId) {
                    var personId = recordParent.personId;
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            var personAssociations = complaint.personAssociations;
                            var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                            if (currentPersonAssoc.person) {
                                var person = currentPersonAssoc.person;
                                if (person.personAliases) {
                                    var personAliases = person.personAliases;
                                    var personAlias = {};
                                    personAlias.aliasType = record.type;
                                    personAlias.aliasValue = Acm.goodValue(record.value);
                                    personAliases.push(personAlias);
                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
                }
            }
            ,recordUpdated : function (event, data) {
                var recordParent = $row.closest('tr').data('record');
                if (recordParent && recordParent.personId) {
                    var personId = recordParent.personId;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var record = data.record;
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            var personAssociations = complaint.personAssociations;
                            var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                            if (currentPersonAssoc.person) {
                                var person = currentPersonAssoc.person;
                                if (person.personAliases) {
                                    var personAliases = person.personAliases;
                                    var personAlias = personAliases[whichRow];
                                    personAlias.aliasType = record.type;
                                    personAlias.aliasValue = Acm.goodValue(record.value);
                                    Complaint.Service.saveComplaint(complaint);
                                }
                            }
                        }
                    }
                }
            }
            ,recordDeleted : function (event, data) {
                var recordParent = $row.closest('tr').data('record');
                if (recordParent && recordParent.personId) {
                    var personId = recordParent.personId;
                    var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        if (complaint.personAssociations) {
                            var personAssociations = complaint.personAssociations;
                            var currentPersonAssoc = Complaint.JTable._findPersonAssoc(personId, personAssociations);
                            if (currentPersonAssoc.person) {
                                var person = currentPersonAssoc.person;
                                if (person.personAliases) {
                                    var personAliases = person.personAliases;
                                    personAliases.splice(whichRow, 1);
                                    Complaint.Service.saveComplaint(complaint);

                                }
                            }
                        }
                    }
                }
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
            ,messages: {
                addNewRecord: 'Add Document'
            }
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var c = Complaint.getComplaint();
                    if (c && c.childObjects) {
                        for (var i = 0; i < c.childObjects.length; i++) {
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
                ,messages: {
                    addNewRecord: 'Add Task'
                }
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
                                            Record.priority = response.docs[i].priority_s;
                                            Record.dueDate = Acm.getDateFromDatetime(response.docs[i].due_dt);
                                            Record.status = Acm.goodValue(response.docs[i].status_s);
                                            Record.assignee = response.docs[i].assignee_s;
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
                        ,width: '5%'
                    }
                    ,title: {
                        title: 'Title'
                        ,width: '30%'
                        ,sorting: false
                        ,display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + '/plugin/task/' +
                                + ((0 >= commData.record.id)? "#" : commData.record.id)
                                + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }
                    ,created: {
                        title: 'Created'
                        ,width: '10%'
                        ,sorting: false
                    }
                    ,priority: {
                        title: 'Priority'
                        ,width: '10%'
                        ,sorting: false
                    }
                    ,dueDate: {
                        title: 'Due'
                        ,width: '10%'
                        ,sorting: true
                    }
                    ,assignee: {
                        title: 'Assignee'
                        ,width: '10%'
                        ,sorting: true
                    }
                    ,status: {
                        title: 'Status'
                        ,width: '10%'
                        ,sorting: false
                    }
                    ,description: {
                        title: 'Action'
                        ,width: '5%'
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

    
    //
    //----------------- Location ------------------------------
    //
    ,createJTableLocation: function($s) {
        $s.jtable({
            title: 'Location'
            ,paging: false
            ,actions: {
                listAction: function(postData, jtParams) {
                    var rc = AcmEx.Object.jTableGetEmptyRecords();
                    var c = Complaint.getComplaint();
                    if (c && c.location) {
                    	var record = {};
                        record.id = Acm.goodValue(c.location.id, 0);
                        record.address = Acm.goodValue(c.location.streetAddress);
                        record.city = Acm.goodValue(c.location.city);
                        record.state = Acm.goodValue(c.location.state);
                        record.zip = Acm.goodValue(c.location.zip);
                        rc.Records.push(record);
                    }
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    rc.Record.address = record.address;
                    rc.Record.city = record.city;
                    rc.Record.state = record.state;
                    rc.Record.zip = record.zip;
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
                ,address: {
                    title: 'Address'
                    ,width: '20%'
                }
                ,city: {
                    title: 'City'
                    ,width: '20%'
                }
                ,state: {
                    title: 'State'
                    ,width: '20%'
                }
                ,zip: {
                    title: 'Zip'
                    ,width: '10%'
                }
            }
            ,recordUpdated : function (event, data) {
                var record = data.record;
                var complaint = Complaint.getComplaint();
                if (complaint) {
                    complaint.location.streetAddress = record.address;
                    complaint.location.city = record.city;
                    complaint.location.state = record.state;
                    complaint.location.zip = record.zip;

                    Complaint.Service.saveComplaint(complaint);
                }
            }
        });

        $s.jtable('load');
    }
    //----------------- end of Location ----------------------

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
                    var complaint = Complaint.getComplaint();
                    if(complaint)
                    {
                        var notes = Complaint.cacheNoteList.get(complaint.complaintId);
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
                ,formCreated: function(event, data)
                {

                }
                ,createAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var complaint = Complaint.getComplaint();
                    if (complaint) {
                        rc.Record.parentId = Acm.goodValue(complaint.complaintId);
                        rc.Record.parentType = App.OBJTYPE_COMPLAINT;
                        rc.Record.note = record.note;
                        rc.Record.created = Acm.getCurrentDay(); //record.created;
                        rc.Record.creator = App.getUserName();   //record.creator;
                    }
                    return rc;
                }
                ,updateAction: function(postData, jtParams) {
                    var record = Acm.urlToJson(postData);
                    var rc = AcmEx.Object.jTableGetEmptyRecord();
                    var complaint = Complaint.getComplaint();
                    if(complaint){
                        rc.Record.parentId = Acm.goodValue(complaint.complaintId);
                        rc.Record.parentType = App.OBJTYPE_COMPLAINT;
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
                var complaint = Complaint.getComplaint();
                if (complaint) {
                    var noteToSave = {};
                    //noteToSave.id = record.id;
                    noteToSave.note = record.note;
                    noteToSave.created = Acm.getCurrentDayInternal(); //record.created;
                    noteToSave.creator = record.creator;   //record.creator;
                    noteToSave.parentId = complaint.complaintId;
                    noteToSave.parentType = App.OBJTYPE_COMPLAINT;
                    Complaint.Service.saveNote(noteToSave);
                }
            }
            ,recordUpdated: function(event,data){
                var whichRow = data.row.prevAll("tr").length;
                var record = data.record;
                var complaint = Complaint.getComplaint();
                if(complaint){
                    var notes = Complaint.cacheNoteList.get(Complaint.getComplaintId());
                    if (notes) {
                        if(notes[whichRow]){
                            var noteToSave;
                            noteToSave = notes[whichRow];
                            noteToSave.note = record.note;
                            Complaint.Service.saveNote(noteToSave);
                        }
                    }
                }
            }
            ,recordDeleted : function (event, data) {
                var whichRow = data.row.prevAll("tr").length;  //count prev siblings
                var complaint = Complaint.getComplaint();
                if (complaint) {
                    var notes = Complaint.cacheNoteList.get(Complaint.getComplaintId());
                    if (notes) {
                        var noteToDelete = notes[whichRow];
                        var noteId = noteToDelete.id;
                        Complaint.Service.deleteNoteById(noteId);
                    }
                }
            }

            });

        $s.jtable('load');
    }
    //----------------- end of Notes ----------------------


};




