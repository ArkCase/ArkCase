/**
 * Profile.View
 *
 * @author jwu
 */
Profile.View = {
    create : function() {
        if (Profile.View.Info.create) {Profile.View.Info.create();}
        if (Profile.View.Subscriptions.create) {Profile.View.Subscriptions.create();}
    }
    ,initialize: function() {
        if (Profile.View.Info.initialize) {Profile.View.Info.initialize();}
        if (Profile.View.Subscriptions.initialize) {Profile.View.Subscriptions.initialize();}
    }

    ,Info: {
        create: function() {
            this.$lnkLocation = $("#location");
            this.$lnkimaccount = $("#imaccount");
            this.$lnkOfficephone = $("#officephone");
            this.$lnkMobilephone = $("#mobilephone");
            this.$lnkCompany = $("#company");
            this.$lnkStreet = $("#street");
            this.$lnkAddress2 = $("#address2");
            this.$lnkCity = $("#city");
            this.$lnkState = $("#state");
            this.$lnkZip = $("#zip");
            this.$lnkMainphone = $("#mainphone");
            this.$lnkFax = $("#fax");
            this.$lnkWebsite = $("#website");

//            AcmEx.Object.XEditable.useEditable(this.$lnkLocation, {success: function(response, newValue) {
//                Profile.Controller.Info.onViewChangedLocation(newValue);
//            }});
            var isReadOnly = Profile.Model.Info.isReadOnly();
            if (!isReadOnly) {
                AcmEx.Object.XEditable.useEditable(this.$lnkLocation, {success: function(response, newValue) {
                    Profile.Controller.Info.onViewChangedLocation(newValue);
                }});
            }
        }
        ,initialize: function() {
        }

        ,ctrlProfileInfoChanged: function(info) {
            this.setValueLnkLocation("profileInfo.location");
        }
        ,ctrlProfileInfoSaved: function() {
            //remove class "editable-unsave"
            alert("view notified; todo: remove editable-unsave");
        }

        ,setValueLnkLocation: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkLocation, txt);
        }

    }

    ,Subscriptions: {
        create: function() {
            this.$divSubscriptions = $("#divSubscriptions");
            this.useJTable(this.$divSubscriptions);
        }
        ,initialize: function() {
        }
        ,useJTable: function($s) {
            $s.jtable({
                title: 'Subscriptions'
                ,paging: false
                ,actions: {
                    listAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var sub = {};
                        rc.Records.push({
                            id: 123
                            ,type: "type1"
                            ,title: "title1"
                            ,date: "m1/dd/yyyy"
                        });
                        rc.Records.push({
                            id: 124
                            ,type: "type2"
                            ,title: "title2"
                            ,date: "m2/dd/yyyy"
                        });
                        return rc;
                    }
                    ,deleteAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }
                ,fields: {
                    id: {
                        title: 'ID'
                        ,key: true
                        ,create: false
                        ,edit: false
                        ,list: true
                    }
                    ,type: {
                        title: 'Type'
                        ,width: '20%'
//                        ,create: false
//                        ,edit: false
                    }
                    ,title: {
                        title: 'Title'
                        ,width: '30%'
//                        ,create: false
//                        ,edit: false
                    }
                    ,date: {
                        title: 'Date'
                        ,width: '30%'
//                        ,create: false
//                        ,edit: false
                    }
                }
            });
            $s.jtable('load');
        }
        ,refreshJTable: function() {
            AcmEx.Object.jTableLoad(this.$divSubscriptions);
        }
    }

};

