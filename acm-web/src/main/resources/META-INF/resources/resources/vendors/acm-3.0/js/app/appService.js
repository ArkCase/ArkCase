/**
 * App.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
App.Service = {
    create : function() {
        if (App.Service.I18n.create)             {App.Service.I18n.create();}
        if (App.Service.Config.create)           {App.Service.Config.create();}
    }

    ,I18n: {
        create: function() {
        }

        ,API_GET_SETTINGS:  "/api/latest/plugin/admin/labelconfiguration/settings"
        ,API_GET_RESOURCE:  "/api/latest/plugin/admin/labelconfiguration/resource" //?lang=' + lng + "&ns=" + ns;

        ,retrieveSettings: function() {
            return Acm.Service.call({type: "GET"
                ,url: App.getContextPath() + this.API_GET_SETTINGS
                ,callback: function(response) {
                    if (response.hasError) {
                        ;

                    } else {
                        if (App.Model.I18n.validateSettings(response)) {
                            var settings = response;
                            App.Model.I18n.setLng(settings.defaultLang);
                            App.Model.I18n.setCurrentLng(true);
                            return true;
                        }
                    }
                } //end callback
            })
        }
        ,retrieveResource: function(lng, ns) {
            return Acm.Service.call({type: "GET"
                ,url: App.getContextPath() + this.API_GET_RESOURCE + "?lang=" + lng + "&ns=" + ns
                ,callback: function(response) {
                    if (response.hasError) {
                        ;

                    } else {
                        if (App.Model.I18n.validateResource(response)) {
                            var res = response;
                            App.Model.I18n.setResource(lng, ns, res);
                            App.Model.I18n.setCurrentResource(lng, ns, true);
                            return true;
                        }
                    }
                } //end callback
            })
        }

    }

    ,Config: {
        create: function() {
        }

        ,API_GET_CONFIG:  "/api/latest/service/config/" //{name}

        ,retrieveConfig: function(name) {
            return Acm.Service.call({type: "GET"
                ,url: App.getContextPath() + this.API_GET_CONFIG + name
                ,callback: function(response) {
                    if (response.hasError) {
                        ;

                    } else {
                        if (App.Model.Config.validateConfig(response)) {
                            var cfg = response;
                            App.Model.Config.setConfig(name, cfg);
                            App.Model.Config.setCurrent(name, true);
                            return true;
                        }
                    }
                } //end callback
            })
        }

    }

//    ,API_GET_APPROVERS             : "/api/latest/service/functionalaccess/users/acm-complaint-approve"
//    ,API_GET_COMPLAINT_TYPES       : "/api/latest/plugin/complaint/types"
//    ,API_GET_PRIORITIES            : "/api/latest/plugin/complaint/priorities"
//
//
//    ,getApprovers : function() {
//        Acm.Ajax.asyncGet(App.getContextPath() + this.API_GET_APPROVERS
//            ,App.Callback.EVENT_APPROVERS_RETRIEVED
//        );
//    }
//    ,getComplaintTypes : function() {
//        Acm.Ajax.asyncGet(App.getContextPath() + this.API_GET_COMPLAINT_TYPES
//            ,App.Callback.EVENT_COMPLAINT_TYPES_RETRIEVED
//        );
//    }
//    ,getPriorities : function() {
//        Acm.Ajax.asyncGet(App.getContextPath() + this.API_GET_PRIORITIES
//            ,App.Callback.EVENT_PRIORIES_RETRIEVED
//        );
//    }
};

