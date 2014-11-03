/**
 * Profile.View
 *
 * @author jwu
 */
Profile.View = {
    create : function() {
        if (Profile.View.Info.create) {Profile.View.Info.create();}
        if (Profile.View.Subscription.create) {Profile.View.Subscription.create();}
    }
    ,initialize: function() {
        if (Profile.View.Info.initialize) {Profile.View.Info.initialize();}
        if (Profile.View.Subscription.initialize) {Profile.View.Subscription.initialize();}
    }

    ,Info: {
        create: function() {
            this.$lnkLocation = $("#location");
            this.$h4Location = this.$lnkLocation.parent();

            this.$lnkImAccount = $("#imaccount");
            this.$lnkImSystem = $("#imsystem");
            this.$h4Im = this.$lnkImAccount.parent();

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

            if (!Profile.Model.Info.isReadOnly()) {
                AcmEx.Object.XEditable.useEditable(this.$lnkLocation, {success: function(response, newValue) {
                    Profile.Controller.viewChangedLocation(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkImAccount, {success: function(response, newValue) {
                    Profile.Controller.viewChangedImAccount(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkImSystem, {success: function(response, newValue) {
                    Profile.Controller.viewChangedImSystem(newValue);
                }});
            }


            Acm.Dispatcher.addEventListener(Profile.Controller.ME_PROFILE_INFO_RETRIEVED, this.onProfileInfoRetrieved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_PROFILE_INFO_SAVED,     this.onProfileInfoSaved);
        }
        ,initialize: function() {
        }


        ,onProfileInfoRetrieved: function(profileInfo) {
            if (profileInfo.hasError) {
                alert("View: onProfileInfoRetrieved, hasError");
            } else {
                Profile.View.Info.populateProfileInfo(profileInfo);
            }
        }
        ,onProfileInfoSaved: function(profileInfo) {
            if (profileInfo.hasError) {
                alert("View: onProfileInfoSaved, hasError");
                //update the field to as error
            } else {
                alert("View: onProfileInfoSaved");
            }

        }

        ,populateProfileInfo: function(profileInfo) {
            if (Profile.Model.Info.isReadOnly()) {
                this.setTextH4Location(Acm.goodValue(profileInfo.location));
                this.setTextH4Im(Acm.goodValue(profileInfo.imAccount) + " (" + Acm.goodValue(profileInfo.imAccount) + ")");
            } else {
                this.setTextLnkLocation(Acm.goodValue(profileInfo.location));
                this.setTextLnkImAccount(Acm.goodValue(profileInfo.imAccount));
                this.setTextLnkImSystem(Acm.goodValue(profileInfo.imSystem));
            }

        }
        ,setTextLnkLocation: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkLocation, txt);
        }
        ,setTextH4Location: function(txt) {
            Acm.Object.setText(this.$h4Location, txt);
        }
        ,setTextLnkImAccount: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkImAccount, txt);
        }
        ,setTextLnkImSystem: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkImSystem, txt);
        }
        ,setTextH4Im: function(txt) {
            Acm.Object.setText(this.$h4Im, txt);
        }


    }

    ,Subscription: {
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

