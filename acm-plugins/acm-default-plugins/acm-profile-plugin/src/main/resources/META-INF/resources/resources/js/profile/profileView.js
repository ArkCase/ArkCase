/**
 * Profile.View
 *
 * @author jwu
 */
Profile.View = {
    create : function() {
        if (Profile.View.Picture.create)          {Profile.View.Picture.create();}
        if (Profile.View.Info.create)             {Profile.View.Info.create();}
        if (Profile.View.Subscription.create)     {Profile.View.Subscription.create();}
    }
    ,initialize: function() {
        if (Profile.View.Picture.initialize)      {Profile.View.Picture.initialize();}
        if (Profile.View.Info.initialize)         {Profile.View.Info.initialize();}
        if (Profile.View.Subscription.initialize) {Profile.View.Subscription.initialize();}
    }

    ,Picture: {
        create: function() {
            //this.$imgPicture       = $("#picture");
            this.$lnkChangePicture = $("#lnkChangePicture");
            this.$formPicture      = $("#formPicture");
            this.$fileInput        = $("#file");

            this.$lnkChangePicture.on("click", function(e) {Profile.View.Picture.onClickLnkChnagePicture(e, this);});
            this.$fileInput.on("change", function(e) {Profile.View.Picture.onChangeFileInput(e, this);});
            this.$formPicture.submit(function(e) {Profile.View.Picture.onSubmitFormPicture(e, this);});


            Acm.Dispatcher.addEventListener(Profile.Controller.ME_PROFILE_INFO_RETRIEVED  ,this.onProfileInfoRetrieved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_PICTURE_UPLOADED        ,this.onPictureUploaded);
        }
        ,initialize: function() {
        }

        ,onClickLnkChnagePicture: function(event, ctrl) {
            Profile.View.Picture.$fileInput.click();
        }
        ,onChangeFileInput: function(event, ctrl) {
            Profile.View.Picture.$formPicture.submit();
        }
        ,onSubmitFormPicture: function(event, ctrl) {
            event.preventDefault();

            var _this = Profile.View.Picture;
            var fd = new FormData();
            fd.append("userId", App.getUserName());
            fd.append("file", _this.$fileInput[0].files[0]);
            Profile.Service.Picture.uploadImage(fd);
        }


        ,onProfileInfoRetrieved: function(profileInfo) {
            if (Profile.Model.Info.isReadOnly()) {
                //disable chnage pic link
            } else {
                //enable chnage pic link
            }
        }
        ,onPictureUploaded: function(uploadInfo) {
            if (uploadInfo.hasError) {
                alert("View: onPictureUploaded, hasError, errorMsg:" + uploadInfo.errorMsg);
            }
        }
    }


    ,Info: {
        create: function() {
            this.$imgPicture     = $("#picture");
            this.$h4FullName     = $("#fullName");
            this.$h4Email        = $("#email");

            this.$divGroups      = $("#groups");

            this.$lnkImAccount   = $("#imaccount");
            this.$lnkImSystem    = $("#imsystem");
            this.$h4Im           = this.$lnkImAccount.parent();

            this.$lnkLocation    = $("#location");
            this.$h4Location     = this.$lnkLocation.parent();
            this.$lnkOfficePhone = $("#officephone");
            this.$h4OfficePhone  = this.$lnkOfficePhone.parent();
            this.$lnkMobilePhone = $("#mobilephone");
            this.$h4MobilePhone  = this.$lnkMobilePhone.parent();
            this.$lnkCompany     = $("#company");
            this.$h4Company      = this.$lnkCompany.parent();
            this.$lnkStreet      = $("#street");
            this.$h4Street       = this.$lnkStreet.parent();
            this.$lnkAddress2    = $("#address2");
            this.$h4Address2     = this.$lnkAddress2.parent();
            this.$lnkCity        = $("#city");
            this.$h4City         = this.$lnkCity.parent();
            this.$lnkState       = $("#state");
            this.$h4State        = this.$lnkState.parent();
            this.$lnkZip         = $("#zip");
            this.$h4Zip          = this.$lnkZip.parent();
            this.$lnkMainPhone   = $("#mainphone");
            this.$h4MainPhone    = this.$lnkMainPhone.parent();
            this.$lnkFax         = $("#fax");
            this.$h4Fax          = this.$lnkFax.parent();
            this.$lnkWebsite     = $("#website");
            this.$h4Website      = this.$lnkWebsite.parent();

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
                AcmEx.Object.XEditable.useEditable(this.$lnkOfficePhone, {success: function(response, newValue) {
                    Profile.Controller.viewChangedOfficePhone(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkMobilePhone, {success: function(response, newValue) {
                    Profile.Controller.viewChangedMobilePhone(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkCompany, {success: function(response, newValue) {
                    Profile.Controller.viewChangedCompany(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkStreet, {success: function(response, newValue) {
                    Profile.Controller.viewChangedStreet(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkAddress2, {success: function(response, newValue) {
                    Profile.Controller.viewChangedAddress2(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkCity, {success: function(response, newValue) {
                    Profile.Controller.viewChangedCity(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkState, {success: function(response, newValue) {
                    Profile.Controller.viewChangedState(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkZip, {success: function(response, newValue) {
                    Profile.Controller.viewChangedZip(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkMainPhone, {success: function(response, newValue) {
                    Profile.Controller.viewChangedMainPhone(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkFax, {success: function(response, newValue) {
                    Profile.Controller.viewChangedFax(newValue);
                }});
                AcmEx.Object.XEditable.useEditable(this.$lnkWebsite, {success: function(response, newValue) {
                    Profile.Controller.viewChangedWebsite(newValue);
                }});
            }


            Acm.Dispatcher.addEventListener(Profile.Controller.ME_PROFILE_INFO_RETRIEVED  ,this.onProfileInfoRetrieved);
            //Acm.Dispatcher.addEventListener(Profile.Controller.ME_PROFILE_INFO_SAVED      ,this.onProfileInfoSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_LOCATION_SAVED          ,this.onLocationSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_IM_ACCOUNT_SAVED        ,this.onImAccountSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_IM_SYSTEM_SAVED         ,this.onImSystemSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_OFFICE_PHONE_SAVED      ,this.onOfficePhoneSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_MOBILE_PHONE_SAVED      ,this.onMobilePhoneSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_COMPANY_SAVED           ,this.onCompanySaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_STREET_SAVED            ,this.onStreetSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_ADDRESS2_SAVED          ,this.onAddress2Saved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_CITY_SAVED              ,this.onCitySaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_STATE_SAVED             ,this.onStateSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_ZIP_SAVED               ,this.onZipSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_MAIN_PHONE_SAVED        ,this.onMainPhoneSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_FAX_SAVED               ,this.onFaxSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_WEBSITE_SAVED           ,this.onWebsiteSaved);
            Acm.Dispatcher.addEventListener(Profile.Controller.ME_ECM_FILE_ID_SAVED       ,this.onEcmFileIdSaved);

        }
        ,initialize: function() {
        }

        ,populateProfileInfo: function(profileInfo) {
            this._displayPicture(Acm.goodValue(profileInfo.ecmFileId, -1));

            this.setTextH4FullName     (Acm.goodValue(profileInfo.fullName));
            this.setTextH4Email        (Acm.goodValue(profileInfo.email));

            this._displayGroups(profileInfo.groups);

            if (Profile.Model.Info.isReadOnly()) {
                this.setTextH4Location     (Acm.goodValue(profileInfo.location));
                this.setTextH4Im           (Acm.goodValue(profileInfo.imAccount) + " (" + Acm.goodValue(profileInfo.imAccount) + ")");
                this.setTextH4OfficePhone  (Acm.goodValue(profileInfo.officePhoneNumber));
                this.setTextH4MobilePhone  (Acm.goodValue(profileInfo.mobilePhoneNumber));
                this.setTextH4Company      (Acm.goodValue(profileInfo.companyName));
                this.setTextH4Street       (Acm.goodValue(profileInfo.firstAddress));
                this.setTextH4Address2     (Acm.goodValue(profileInfo.secondAddress));
                this.setTextH4City         (Acm.goodValue(profileInfo.city));
                this.setTextH4State        (Acm.goodValue(profileInfo.state));
                this.setTextH4Zip          (Acm.goodValue(profileInfo.zip));
                this.setTextH4MainPhone    (Acm.goodValue(profileInfo.mainOfficePhone));
                this.setTextH4Fax          (Acm.goodValue(profileInfo.fax));
                this.setTextH4Website      (Acm.goodValue(profileInfo.website));
            } else {
                this.setTextLnkLocation    (Acm.goodValue(profileInfo.location));
                this.setTextLnkImAccount   (Acm.goodValue(profileInfo.imAccount));
                this.setTextLnkImSystem    (Acm.goodValue(profileInfo.imSystem));
                this.setTextLnkOfficePhone (Acm.goodValue(profileInfo.officePhoneNumber));
                this.setTextLnkMobilePhone (Acm.goodValue(profileInfo.mobilePhoneNumber));
                this.setTextLnkCompany     (Acm.goodValue(profileInfo.companyName));
                this.setTextLnkStreet      (Acm.goodValue(profileInfo.firstAddress));
                this.setTextLnkAddress2    (Acm.goodValue(profileInfo.secondAddress));
                this.setTextLnkCity        (Acm.goodValue(profileInfo.city));
                this.setTextLnkState       (Acm.goodValue(profileInfo.state));
                this.setTextLnkZip         (Acm.goodValue(profileInfo.zip));
                this.setTextLnkMainPhone   (Acm.goodValue(profileInfo.mainOfficePhone));
                this.setTextLnkFax         (Acm.goodValue(profileInfo.fax));
                this.setTextLnkWebsite     (Acm.goodValue(profileInfo.website));
            }

        }
        ,_displayPicture: function(ecmFileId) {
            var pictureUrl = (0 < ecmFileId)? CaseFile.Service.Info.getPictureUrl(ecmFileId)
                : this.getDefaultImgPicture();
            //var pictureUrl = (Acm.isEmpty(profileInfo.pictureUrl)) ? this.getDefaultImgPicture() : profileInfo.pictureUrl;
            this.setSrcImgPicture(pictureUrl);
        }
        ,_displayGroups: function(groups) {
            if (Acm.isArray(groups)) {
                var html = "";
                for (var i = 0; i < groups.length; i++) {
                    html += "<span class='btn-rounded btn-sm btn-info'>" + groups[i] + "</span>";
                }
                this.setHtmlGroups(html);
            }
        }

        ,setSrcImgPicture: function(src) {
            this.$imgPicture.attr("src", src);
        }
        ,getDefaultImgPicture: function() {
            return this.$imgPicture.attr("default");
        }
        ,setHtmlGroups: function(html) {
            Acm.Object.setHtml(this.$divGroups, html);
        }
        ,setTextH4FullName: function(txt) {
            Acm.Object.setText(this.$h4FullName, txt);
        }
        ,setTextH4Email: function(txt) {
            Acm.Object.setText(this.$h4Email, txt);
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
        ,setTextLnkOfficePhone: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkOfficePhone, txt);
        }
        ,setTextH4OfficePhone: function(txt) {
            Acm.Object.setText(this.$h4OfficePhone, txt);
        }
        ,setTextLnkMobilePhone: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkMobilePhone, txt);
        }
        ,setTextH4MobilePhone: function(txt) {
            Acm.Object.setText(this.$h4MobilePhone, txt);
        }
        ,setTextLnkCompany: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkCompany, txt);
        }
        ,setTextH4Company: function(txt) {
            Acm.Object.setText(this.$h4Company, txt);
        }
        ,setTextLnkStreet: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkStreet, txt);
        }
        ,setTextH4Street: function(txt) {
            Acm.Object.setText(this.$h4Street, txt);
        }
        ,setTextLnkAddress2: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkAddress2, txt);
        }
        ,setTextH4Address2: function(txt) {
            Acm.Object.setText(this.$h4Address2, txt);
        }
        ,setTextLnkCity: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkCity, txt);
        }
        ,setTextH4City: function(txt) {
            Acm.Object.setText(this.$h4City, txt);
        }
        ,setTextLnkState: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkState, txt);
        }
        ,setTextH4State: function(txt) {
            Acm.Object.setText(this.$h4State, txt);
        }
        ,setTextLnkZip: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkZip, txt);
        }
        ,setTextH4Zip: function(txt) {
            Acm.Object.setText(this.$h4Zip, txt);
        }
        ,setTextLnkMainPhone: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkMainPhone, txt);
        }
        ,setTextH4MainPhone: function(txt) {
            Acm.Object.setText(this.$h4MainPhone, txt);
        }
        ,setTextLnkFax: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkFax, txt);
        }
        ,setTextH4Fax: function(txt) {
            Acm.Object.setText(this.$h4Fax, txt);
        }
        ,setTextLnkWebsite: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkWebsite, txt);
        }
        ,setTextH4Website: function(txt) {
            Acm.Object.setText(this.$h4Website, txt);
        }

        ,onProfileInfoRetrieved: function(profileInfo) {
            if (profileInfo.hasError) {
                alert("View: onProfileInfoRetrieved, hasError, errorMsg:" + profileInfo.errorMsg);
            } else {
                Profile.View.Info.populateProfileInfo(profileInfo);
            }
        }
//        ,onProfileInfoSaved: function(profileInfo) {
//            if (profileInfo.hasError) {
//                alert("View: onProfileInfoSaved, hasError");
//                //update the field to as error
//            } else {
//                alert("View: onProfileInfoSaved");
//            }
//
//        }
        ,onLocationSaved: function(location) {
            if (location.hasError) {
                //alert("View: onLocationSaved, hasError, errorMsg:" + location.errorMsg);
                Profile.View.Info.setTextLnkLocation("(Error)");
            }
        }
        ,onImAccountSaved: function(imAccount) {
            if (imAccount.hasError) {
                Profile.View.Info.setTextLnkImAccount("(Error)");
            }
        }
        ,onImSystemSaved: function(imSystem) {
            if (imSystem.hasError) {
                Profile.View.Info.setTextLnkImSystem("(Error)");
            }
        }
        ,onOfficePhoneSaved: function(officePhoneNumber) {
            if (officePhoneNumber.hasError) {
                Profile.View.Info.setTextLnkOfficePhone("(Error)");
            }
        }
        ,onMobilePhoneSaved: function(mobilePhoneNumber) {
            if (mobilePhoneNumber.hasError) {
                Profile.View.Info.setTextLnkMobilePhone("(Error)");
            }
        }
        ,onCompanySaved: function(companyName) {
            if (companyName.hasError) {
                Profile.View.Info.setTextLnkCompany("(Error)");
            }
        }
        ,onStreetSaved: function(firstAddress) {
            if (firstAddress.hasError) {
                Profile.View.Info.setTextLnkStreet("(Error)");
            }
        }
        ,onAddress2Saved: function(secondAddress) {
            if (secondAddress.hasError) {
                Profile.View.Info.setTextLnkAddress2("(Error)");
            }
        }
        ,onCitySaved: function(city) {
            if (city.hasError) {
                Profile.View.Info.setTextLnkCity("(Error)");
            }
        }
        ,onStateSaved: function(state) {
            if (state.hasError) {
                Profile.View.Info.setTextLnkState("(Error)");
            }
        }
        ,onZipSaved: function(zip) {
            if (zip.hasError) {
                Profile.View.Info.setTextLnkZip("(Error)");
            }
        }
        ,onMainPhoneSaved: function(mainOfficePhone) {
            if (mainOfficePhone.hasError) {
                Profile.View.Info.setTextLnkMainPhone("(Error)");
            }
        }
        ,onFaxSaved: function(fax) {
            if (fax.hasError) {
                Profile.View.Info.setTextLnkFax("(Error)");
            }
        }
        ,onWebsiteSaved: function(website) {
            if (website.hasError) {
                Profile.View.Info.setTextLnkWebsite("(Error)");
            }
        }
        ,onEcmFileIdSaved: function(ecmFileId) {
            if (ecmFileId.hasError) {
                //report error
            } else {
                Profile.View.Info._displayPicture(ecmFileId)
            }
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

