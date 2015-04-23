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
        if (Profile.View.OutlookPassword.create)  {Profile.View.OutlookPassword.create();}
    }
    ,onInitialized: function() {
        if (Profile.View.Picture.onInitialized)      {Profile.View.Picture.onInitialized();}
        if (Profile.View.Info.onInitialized)         {Profile.View.Info.onInitialized();}
        if (Profile.View.Subscription.onInitialized) {Profile.View.Subscription.onInitialized();}
        if (Profile.View.OutlookPassword.onInitialized)     {Profile.View.OutlookPassword.onInitialized();}
    }

    ,Picture: {
        create: function() {
            this.$imgPicLoading    = $("#picLoading");
            //this.$imgPicture       = $("#picture");
            this.$lnkChangePicture = $("#lnkChangePicture");
            this.$formPicture      = $("#formPicture");
            this.$fileInput        = $("#file");

            this.$lnkChangePicture.on("click", function(e) {Profile.View.Picture.onClickLnkChangePicture(e, this);});
            this.$fileInput.on("change", function(e) {Profile.View.Picture.onChangeFileInput(e, this);});
            this.$formPicture.submit(function(e) {Profile.View.Picture.onSubmitFormPicture(e, this);});


            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_RETRIEVED_PROFILE_INFO  ,this.onModelRetrievedProfileInfo);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_UPLOADED_PICTURE        ,this.onModelUploadedPicture);
        }
        ,onInitialized: function() {
        }

        ,onClickLnkChangePicture: function(event, ctrl) {
            Profile.View.Picture.$fileInput.click();
        }
        ,onChangeFileInput: function(event, ctrl) {
            Profile.View.Picture.$formPicture.submit();
        }
        ,onSubmitFormPicture: function(event, ctrl) {
            event.preventDefault();

            var _this = Profile.View.Picture;
            var fd = new FormData();
            fd.append("parentObjectId", Profile.Model.Info.getProfileInfo().userOrgId);
            fd.append("parentObjectType", Profile.Model.DOC_TYPE_USER_PROFILE);
            fd.append("fileType", "user_profile");
            fd.append("file", _this.$fileInput[0].files[0]);
            Profile.Service.Picture.uploadImage(fd);
            Profile.View.Picture.showImgPicLoading(true);
        }


        ,onModelRetrievedProfileInfo: function(profileInfo) {
            if (Profile.Model.Info.isReadOnly()) {
                //disable chnage pic link
            } else {
                //enable chnage pic link
            }
        }
        ,onModelUploadedPicture: function(uploadInfo) {
            if (uploadInfo.hasError) {
                alert("View: onPictureUploaded, hasError, errorMsg:" + uploadInfo.errorMsg);
            }
            Profile.View.Picture.showImgPicLoading(false);
        }

        ,showImgPicLoading: function(show) {
            Acm.Object.show(this.$imgPicLoading, show);
        }
    }


    ,Info: {
        create: function() {
            this.$imgPicture     = $("#picture");
            this.$h4FullName     = $("#fullName");
            this.$h4Email        = $("#email");
            this.$lnkTitle        = $("#title");

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
                AcmEx.Object.XEditable.useEditable(this.$lnkTitle, {success: function(response, newValue) {
                    Profile.Controller.viewChangedTitle(newValue);
                }});
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


            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_RETRIEVED_PROFILE_INFO  ,this.onModelRetrievedProfileInfo);
            //Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_PROFILE_INFO      ,this.onModelSavedProfileInfo);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_TITLE             ,this.onModelSavedTitle);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_LOCATION          ,this.onModelSavedLocation);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_IM_ACCOUNT        ,this.onModelSavedImAccount);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_IM_SYSTEM         ,this.onModelSavedImSystem);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_OFFICE_PHONE      ,this.onModelSavedOfficePhone);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_MOBILE_PHONE      ,this.onModelSavedMobilePhone);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_COMPANY           ,this.onModelSavedCompany);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_STREET            ,this.onModelSavedStreet);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_ADDRESS2          ,this.onModelSavedAddress2);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_CITY              ,this.onModelSavedCity);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_STATE             ,this.onModelSavedState);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_ZIP               ,this.onModelSavedZip);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_MAIN_PHONE        ,this.onModelSavedMainPhone);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_FAX               ,this.onModelSavedFax);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_WEBSITE           ,this.onModelSavedWebsite);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_ECM_FILE_ID       ,this.onModelSavedEcmFileId);

        }
        ,onInitialized: function() {
        }

        ,populateProfileInfo: function(profileInfo) {
            this.displayPicture(Acm.goodValue(profileInfo.ecmFileId, -1));

            this.setTextH4FullName     (Acm.goodValue(profileInfo.fullName));
            this.setTextH4Email        (Acm.goodValue(profileInfo.email));

            this.displayGroups(profileInfo.groups);

            if (Profile.Model.Info.isReadOnly()) {
                this.setTextH4Title        (Acm.goodValue(profileInfo.title));
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
                this.setTextLnkTitle       (Acm.goodValue(profileInfo.title));
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
        ,displayPicture: function(ecmFileId) {
            var pictureUrl = (0 < ecmFileId)? Profile.Service.Info.getPictureUrl(ecmFileId)
                : this.getDefaultImgPicture();

            this.setSrcImgPicture(pictureUrl);
        }
        ,displayGroups: function(groups) {
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
        ,setTextH4Title: function(txt) {
            Acm.Object.setText(this.$lnkTitle, txt);
        }
        ,setTextLnkTitle: function(txt) {
            AcmEx.Object.XEditable.setValue(this.$lnkTitle, txt);
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

        ,onModelRetrievedProfileInfo: function(profileInfo) {
            if (profileInfo.hasError) {
                alert("View: onProfileInfoRetrieved, hasError, errorMsg:" + profileInfo.errorMsg);
            } else {
                Profile.View.Info.populateProfileInfo(profileInfo);
            }
        }
//        ,onModelSavedProfileInfo: function(profileInfo) {
//            if (profileInfo.hasError) {
//                alert("View: onProfileInfoSaved, hasError");
//                //update the field to as error
//            } else {
//                alert("View: onProfileInfoSaved");
//            }
//
//        }
        ,onModelSavedTitle: function(title) {
            if (title.hasError) {
                Profile.View.Info.setTextLnkTitle("(Error)");
            }
        }
        ,onModelSavedLocation: function(location) {
            if (location.hasError) {
                //alert("View: onLocationSaved, hasError, errorMsg:" + location.errorMsg);
                Profile.View.Info.setTextLnkLocation("(Error)");
            }
        }
        ,onModelSavedImAccount: function(imAccount) {
            if (imAccount.hasError) {
                Profile.View.Info.setTextLnkImAccount("(Error)");
            }
        }
        ,onModelSavedImSystem: function(imSystem) {
            if (imSystem.hasError) {
                Profile.View.Info.setTextLnkImSystem("(Error)");
            }
        }
        ,onModelSavedOfficePhone: function(officePhoneNumber) {
            if (officePhoneNumber.hasError) {
                Profile.View.Info.setTextLnkOfficePhone("(Error)");
            }
        }
        ,onModelSavedMobilePhone: function(mobilePhoneNumber) {
            if (mobilePhoneNumber.hasError) {
                Profile.View.Info.setTextLnkMobilePhone("(Error)");
            }
        }
        ,onModelSavedCompany: function(companyName) {
            if (companyName.hasError) {
                Profile.View.Info.setTextLnkCompany("(Error)");
            }
        }
        ,onModelSavedStreet: function(firstAddress) {
            if (firstAddress.hasError) {
                Profile.View.Info.setTextLnkStreet("(Error)");
            }
        }
        ,onModelSavedAddress2: function(secondAddress) {
            if (secondAddress.hasError) {
                Profile.View.Info.setTextLnkAddress2("(Error)");
            }
        }
        ,onModelSavedCity: function(city) {
            if (city.hasError) {
                Profile.View.Info.setTextLnkCity("(Error)");
            }
        }
        ,onModelSavedState: function(state) {
            if (state.hasError) {
                Profile.View.Info.setTextLnkState("(Error)");
            }
        }
        ,onModelSavedZip: function(zip) {
            if (zip.hasError) {
                Profile.View.Info.setTextLnkZip("(Error)");
            }
        }
        ,onModelSavedMainPhone: function(mainOfficePhone) {
            if (mainOfficePhone.hasError) {
                Profile.View.Info.setTextLnkMainPhone("(Error)");
            }
        }
        ,onModelSavedFax: function(fax) {
            if (fax.hasError) {
                Profile.View.Info.setTextLnkFax("(Error)");
            }
        }
        ,onModelSavedWebsite: function(website) {
            if (website.hasError) {
                Profile.View.Info.setTextLnkWebsite("(Error)");
            }
        }
        ,onModelSavedEcmFileId: function(ecmFileId) {
            if (ecmFileId.hasError) {
                alert("Save FildId: " + ecmFileId.errorMsg);
            } else {
                Profile.View.Info.displayPicture(ecmFileId)
            }
        }
    }

    ,Subscription: {
        create: function() {
            this.$divSubscriptions = $("#divSubscriptions");
            this.useJTable(this.$divSubscriptions);

            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_RETRIEVED_SUBSCRIPTIONS , this.onModelRetrievedSubscriptions);
            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_DELETED_SUBSCRIPTION , this.onModelDeletedSubscription);

        }
        ,onInitialized: function() {
        }
        ,onModelRetrievedSubscriptions: function(subsriptions) {
            if (subsriptions.hasError) {
                Acm.Dialog.info(subsriptions.errorMsg);
            }
            else{
                Profile.View.Subscription.refreshJTableSubscription();
            }
        }
        ,onModelDeletedSubscription: function(subsription) {
            if (subsription.hasError) {
                Acm.Dialog.info(subsription.errorMsg);
            }
            else{
                Profile.View.Subscription.refreshJTableSubscription();
            }
        }
        ,_makeJtData: function(subscriptions) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (subscriptions) {
                for (var i = 0; i < subscriptions.length; i++) {
                    var Record = {};
                    Record.id       = Acm.goodValue(subscriptions[i].id);
                    Record.parentId    = Acm.goodValue(subscriptions[i].parentId);
                    Record.parentTitle    = Acm.goodValue(subscriptions[i].parentTitle);
                    Record.parentType     = Acm.goodValue(subscriptions[i].parentType);
                    Record.parentName     = Acm.goodValue(subscriptions[i].parentName);
                    Record.created  = Acm.goodValue(subscriptions[i].created);
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = subscriptions.length;
            }
            return jtData;
        }
        ,useJTable: function($s) {
            AcmEx.Object.JTable.usePaging($s
            ,{
                messages: {
                    areYouSure: 'Are you sure?',
                    noDataAvailable: 'No subscriptions available!',
                    cancel: 'Cancel',
                    deleteText: 'Unsubscribe'
                }
                ,title: 'Subscriptions'
                ,paging: true
                ,selecting: true //Enable selecting
                ,multiselect: true //Allow multiple selecting
                ,selectingCheckboxes: true //Show checkboxes on first column                ,pageSize: 10 //Set page size (default: 10)
                ,toolbar: {
                    items: [{
                        text: 'Unsubscribe Selected',
                        click: function () {
                            var $selectedRows = Profile.View.Subscription.$divSubscriptions.jtable('selectedRows');
                            Profile.View.Subscription.$divSubscriptions.jtable('deleteRows', $selectedRows);
                        }
                    }]
                }
                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        var subscriptions = Profile.Model.Subscription.cacheSubscription.get(App.getUserName());
                        if (subscriptions) {
                            return Profile.View.Subscription._makeJtData(subscriptions);

                        } else {
                            return Profile.Service.Subscription.retrieveSubscriptionDeferred(postData
                                ,jtParams
                                ,sortMap
                                ,function(data) {
                                    var subscriptions = data;
                                    return Profile.View.Subscription._makeJtData(subscriptions);
                                }
                                ,function(error) {
                                }
                            );
                        }  //end else
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
                        ,list: false
                    }
                    ,parentId: {
                        title: 'Object ID'
                        ,create: false
                        ,edit: false
                        ,list: false
                    }
                    ,parentTitle: {
                        title: 'Title'
                        ,width: '40%'
                        ,display: function(data) {
                            var url = App.buildObjectUrl(Acm.goodValue(data.record.parentType), Acm.goodValue(data.record.parentId), "#");
                            var $lnk = $("<a href='" + url + "'>" + Acm.goodValue(data.record.parentTitle) + "</a>");
                            return $lnk;
                        }
                    }
                    ,parentType: {
                        title: 'Type'
                        ,width: '20%'
                    }
                    ,parentName: {
                        title: 'Object Name'
                        ,create: false
                        ,edit: false
                        ,list: false
                    }
                    ,created: {
                        title: 'Created'
                        ,width: '10%'
                    }
                }
                ,deleteConfirmation: function(data) {
                    data.deleteConfirmMessage = 'Unsubscribe from "' + data.record.parentTitle + ' (' + data.record.parentName +')" ?';
                }
                ,recordDeleted: function (event, data) {
                    var record = data.record;
                    var parentType = Acm.goodValue(record.parentType);
                    var parentId = Acm.goodValue(record.parentId);
                    var userId = Acm.goodValue(App.getUserName());
                    if (Acm.isNotEmpty(parentId) && 0 < parentId && Acm.isNotEmpty(userId) && Acm.compare(userId, App.getUserName())) {
                        Profile.Controller.viewDeletedSubscription(parentId,parentType,userId);
                    }
                }
            });
            $s.jtable('load');
        }
        ,refreshJTableSubscription: function() {
            AcmEx.Object.jTableLoad(this.$divSubscriptions);
        }
    }


    ,OutlookPassword: {
        create: function () {
            this.$modalChangeOutlookPassword = $("#changePassword").on("hidden.bs.modal", function(e) {
                Profile.View.OutlookPassword.clearPasswordFields();
            });
            this.$btnChangePassword          = this.$modalChangeOutlookPassword.find('button.btn-primary');
            this.$btnChangePassword.on("click", function(e){Profile.View.OutlookPassword.onClickBtnChangePassword(e, this);});

            this.$newPassword                = $("#newpass");
            this.$newPasswordCheck           = $("#newpassagain");
            //this.$currentPassword            = $("#currentpassword");

            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_OUTLOOK_PASSWORD, this.onModelSavedOutlookPassword);
        }
        , onInitialized: function () {
        }

        ,onModelSavedOutlookPassword: function(savedOutlookPassword){
            if(savedOutlookPassword.hasError){
                App.View.MessageBoard.show("Error changing outlook password.", savedOutlookPassword.errorMsg);
            }
            else{
                App.View.MessageBoard.show("Outlook password successfully changed.")
            }
        }

        ,onClickBtnChangePassword: function(event,ctrl){
            var newPassword = Acm.goodValue(Profile.View.OutlookPassword.getValueNewPassword());
            var newPasswordCheck = Acm.goodValue(Profile.View.OutlookPassword.getValueNewPasswordCheck());
            if (Acm.isEmpty(newPassword)) {
                Acm.Dialog.info("New password cannot be empty.");
                Profile.View.OutlookPassword.$modalChangeOutlookPassword.modal("show");

            } else if (Acm.isEmpty(newPasswordCheck)) {
                Acm.Dialog.info("Please re-enter your password.");
                Profile.View.OutlookPassword.$modalChangeOutlookPassword.modal("show");

            } else if (!Acm.compare(newPassword,newPasswordCheck)) {
                Acm.Dialog.info("Passwords do not match. Please try again.");
                Profile.View.OutlookPassword.$modalChangeOutlookPassword.modal("show");
            }
            else{
                var outlookPasswordToSave = {};
                outlookPasswordToSave.outlookPassword = newPassword;
                Profile.Controller.viewChangedOutlookPassword(outlookPasswordToSave);
                Profile.View.OutlookPassword.$modalChangeOutlookPassword.modal("hide");
            }
        }

        ,clearPasswordFields: function(){
            Profile.View.OutlookPassword.$newPassword.val('');
            Profile.View.OutlookPassword.$newPasswordCheck.val('');
            //Profile.View.OutlookPassword.$currentPassword.val('');
        }
        ,getValueNewPassword: function(){
            return Acm.goodValue(Profile.View.OutlookPassword.$newPassword.val());
        }
        ,getValueNewPasswordCheck: function(){
            return Acm.goodValue(Profile.View.OutlookPassword.$newPasswordCheck.val());
        }
        /*,getValueCurrentPassword: function(){
            return Acm.goodValue(Profile.View.OutlookPassword.$currentPassword.val());
        }*/
    }
};

