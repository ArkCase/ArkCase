/**
 * App serves as namespace for Application
 *
 * @author jwu
 */
var App = App || {
    create : function() {
        if (App.Controller.create)         App.Controller.create();
        if (App.Model.create)              App.Model.create();
        if (App.View.create)               App.View.create();

        //this.create_old();
    }
    ,onInitialized: function() {
        if (App.Controller.onInitialized)  App.Controller.onInitialized();
        if (App.Model.onInitialized)       App.Model.onInitialized();
        if (App.View.onInitialized)        App.View.onInitialized();
    }


    ,getPageContext: function() {
        var context = {};
        context.path = App.getContextPath();
        context.resourceNamespace = Acm.Object.MicroData.get("resourceNamespace");

        var dataLabelSettings = new Acm.Model.LocalData(Application.LOCAL_DATA_LABEL_SETTINGS);
        var labelSettings = Acm.Object.MicroData.getJson("labelSettings");
        if (Acm.isEmpty(labelSettings)) {
            var localLabelSettings = dataLabelSettings.get();
            if (Acm.isNotEmpty(localLabelSettings)) {
                context.labelSettings = localLabelSettings;
                return context;
            } else {
                labelSettings = {"defaultLang": "en"};
            }
        }
        context.labelSettings = labelSettings;
        dataLabelSettings.set(labelSettings);
        return context;
    }

    ,getContextPath: function() {
        return Acm.Object.MicroData.get("contextPath");
    }
    ,getUserName: function() {
        return Acm.Object.MicroData.get("userName");
    }

    ,buildObjectUrl : function(objectType, objectId, defaultUrl) {
        var url = null;
        var ot = App.View.MicroData.findObjectType(objectType);
        if (ot && Acm.isNotEmpty(ot.url)) {
            url = App.getContextPath() + ot.url + objectId + Acm.goodValue(ot.urlEnd);
        }
        if (null == url && undefined != defaultUrl) {
            url = defaultUrl;
        }
        return url;
    }

    ,gotoPage: function(url) {
        window.location.href = App.getContextPath() + url;
    }

    ,I18n: {
        init: function(context, onDone) {
            App.Model.I18n.init();

            var lng = App.Model.I18n.getLng();

            if (Acm.isNotEmpty(context.loginPage)) {
                var resLogin = null;
                if (Acm.isNotEmpty(lng)) {
                    resLogin = App.Model.I18n.getResource(lng, "login");
                }

                if (Acm.isEmpty(resLogin)) {
                    onDone(context);
                    //patch up login page with default labels
                } else {
                    App.I18n._doInit(context, onDone, lng, true);
                }

            } else {
                if (App.Model.I18n.isCurrentLng()) {
                    App.I18n._doInit(context, onDone, lng, false);

                } else {
                    App.Service.I18n.retrieveSettings()
                        .done(function() {
                            var lng = App.Model.I18n.getLng();
                            App.I18n._doInit(context, onDone, lng, false);
                            var z = 1;
                        })
                        .fail(function() {
                            var z = 2;
                        })
                    ;
                }
            }

        }

        ,_doInit: function(context, onDone, lng, loginPage) {
            //
            // help out login page by retrieving and caching a copy for future use
            //
            if (!loginPage) {
                if (!App.Model.I18n.isCurrentResource(lng, "login")) {
                    App.Service.I18n.retrieveResource(lng, "login");
                }
            }

            var names = context.resourceNamespace;      // namespaces are divided by "," symbol from detailData
            var namespaces = ['common'];
            if (names) {
                names = names.split(',');
                for (var i = 0; i < names.length; i++) {
                    namespaces.push($.trim(names[i]));
                }
            }

            i18n.init({
                useLocalStorage: false,
                localStorageExpirationTime: 86400000, // 1 week
                load: 'current', // Prevent loading of 'en' locale
                fallbackLng: false,
                lng: lng,
                ns:{
                    namespaces: namespaces
                }
                ,lowerCaseLng: true
                ,customLoad: function(lng, ns, options, loadComplete) {
                    var res = App.Model.I18n.getResource(lng, ns);
                    if (loginPage) {
                        if (Acm.isNotEmpty(res)) {
                            loadComplete(null, res);
                        } else {
                            loadComplete("Resource error - " + lng + "." + ns, null);
                        }

                    } else {
                        if (App.Model.I18n.isCurrentResource(lng, ns)) {
                            loadComplete(null, res);

                        } else {
                            App.Service.I18n.retrieveResource(lng, ns)
                                .done(function(data) {
                                    var res = App.Model.I18n.getResource(lng, ns);
                                    loadComplete(null, res);
                                })
                                .fail(function(data) {
                                    loadComplete("Resource error - " + lng + "." + ns, null);
                                })
                            ;
                        }
                    }
                }
            }, function() {
                $('*[data-i18n]').i18n();
                onDone(context);
                $(document).trigger('i18n-ready');
            });
        }
    }
//retired
//    ,create_old : function() {
//        App.Object.create();
//        App.Event.create();
//        App.Service.create();
//        App.Callback.create();
//
//        Acm.deferred(App.Event.onPostInit);
//    }
//
//    ,OBJTYPE_CASE:        "CASE_FILE"
//    ,OBJTYPE_COMPLAINT:   "COMPLAINT"
//    ,OBJTYPE_TASK:        "TASK"
//    ,OBJTYPE_DOCUMENT:    "DOCUMENT"
//    ,OBJTYPE_PEOPLE:    "PEOPLE"
//    ,OBJTYPE_PERSON:    "PERSON"
//    ,OBJTYPE_BUSINESS_PROCESS: "BUSINESS_PROCESS"
//    ,OBJTYPE_TIMESHEET:        "TIMESHEET"
//    ,OBJTYPE_COSTSHEET:        "COSTSHEET"
//
//
//    //fix me: make it plugin independent
//    ,getComplaintTreeInfo: function() {
//        var data = sessionStorage.getItem("AcmComplaintTreeInfo");
//        if (Acm.isEmpty(data)) {
//            return null;
//        }
//        return JSON.parse(data);
//    }
//    ,setComplaintTreeInfo: function(treeInfo) {
//        var data = (Acm.isEmpty(treeInfo))? null : JSON.stringify(treeInfo);
//        sessionStorage.setItem("AcmComplaintTreeInfo", data);
//    }
//
//
//    ,getContextPath_old: function() {
//        return App.Object.getContextPath();
//    }
//    ,getUserName_old: function() {
//        return App.Object.getUserName();
//    }
//    ,buildObjectUrl_old : function(objectType, objectId)
//    {
//        var url = App.getContextPath();
//        if (App.OBJTYPE_CASE == objectType) {
//            url += "/plugin/casefile/" + objectId;
//        } else if (App.OBJTYPE_COMPLAINT == objectType) {
//            url += "/plugin/complaint/" + objectId;
//        } else if (App.OBJTYPE_TASK == objectType) {
//            url += "/plugin/task/" + objectId;
//        } else if (App.OBJTYPE_DOCUMENT == objectType) {
//            url += "/plugin/document/" + objectId;
//        } else if (App.OBJTYPE_PEOPLE == objectType) {
//            url += "/plugin/people/" + objectId;
//        }else if (App.OBJTYPE_PERSON == objectType) {
//            url += "/plugin/person/" + objectId;
//        }
//
//        return url;
//    }


};


