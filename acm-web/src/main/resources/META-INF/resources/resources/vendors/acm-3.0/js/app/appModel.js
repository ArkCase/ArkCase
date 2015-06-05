/**
 * App.Model
 *
 * @author jwu
 */
App.Model = {
    prepare : function() {
        if (App.Model.Login.prepare)           {App.Model.Login.prepare();}
        if (App.Model.I18n.prepare)            {App.Model.I18n.prepare();}
        if (App.Model.Config.prepare)          {App.Model.Config.prepare();}
    }
    ,create : function() {
        if (App.Model.Login.create)            {App.Model.Login.create();}
        if (App.Model.I18n.create)             {App.Model.I18n.create();}
        if (App.Model.Config.create)           {App.Model.Config.create();}
    }
    ,onInitialized: function() {
        if (App.Model.Login.onInitialized)     {App.Model.Login.onInitialized();}
        if (App.Model.I18n.onInitialized)      {App.Model.I18n.onInitialized();}
        if (App.Model.Config.onInitialized)    {App.Model.Config.onInitialized();}
    }

    ,Storage: {
        SESSION_DATA_PROFILE               : "AcmProfile"
        ,SESSION_DATA_QUICK_SEARCH_TERM     : "AcmQuickSearchTerm"
        ,SESSION_DATA_ASN_LIST              : "AcmAsnList"
        ,SESSION_DATA_ASN_DATA              : "AcmAsnData"

        ,LOCAL_DATA_LOGIN_STATUS            : "AcmLoginStatus"
        ,LOCAL_DATA_LAST_IDLE               : "AcmLastIdle"
        ,LOCAL_DATA_ERROR_COUNT             : "AcmErrorCount"

        ,LOCAL_DATA_I18N                    : "AcmI18n"
        ,SESSION_DATA_I18N_TRACKER          : "AcmI18nTracker"

        ,LOCAL_DATA_CONFIG                  : "AcmConfig"
        ,SESSION_DATA_CONFIG_TRACKER        : "AcmConfigTracker"



        ,reset: function() {
            sessionStorage.setItem(this.SESSION_DATA_PROFILE, null);
            sessionStorage.setItem(this.SESSION_DATA_QUICK_SEARCH_TERM, null);
            sessionStorage.setItem(this.SESSION_DATA_ASN_LIST, null);
            sessionStorage.setItem(this.SESSION_DATA_ASN_DATA, null);

            sessionStorage.setItem(this.SESSION_DATA_I18N_TRACKER, null);
            sessionStorage.setItem(this.SESSION_DATA_CONFIG_TRACKER, null);

            var contextPath = App.getContextPath();
            localStorage.setItem(this.LOCAL_DATA_LOGIN_STATUS + contextPath, null);
            localStorage.setItem(this.LOCAL_DATA_LAST_IDLE + contextPath, new Date().getTime());
            localStorage.setItem(this.LOCAL_DATA_ERROR_COUNT + contextPath, null);
        }
    }

    ,Login: {
        prepare : function() {
            var contextPath = App.getContextPath();
            this.loginStatus = new Acm.Model.LocalData(App.Model.Storage.LOCAL_DATA_LOGIN_STATUS + contextPath);
            this.lastIdle = new Acm.Model.LocalData(App.Model.Storage.LOCAL_DATA_LAST_IDLE + contextPath);
            this.errorCount = new Acm.Model.LocalData(App.Model.Storage.LOCAL_DATA_ERROR_COUNT + contextPath);
        }
        ,create : function() {
        }
        ,onInitialized: function() {
            var isLogin = App.Model.Login.isLogin();
            if (isLogin) {
                var myCfg = App.Model.Config.getMyConfig();
                var autoLogoutIdleLimit  = myCfg.settings.autoLogoutIdleLimit;
                var autoLogoutErrorLimit = myCfg.settings.autoLogoutErrorLimit;
                Acm.Timer.useTimer("AutoLogout"
                    ,20  //every twenty seconds
                    ,function() {
                        var isLogin = App.Model.Login.isLogin();
                        var sinceIdle = App.Model.Login.getSinceIdle();
                        var errorCount = App.Model.Login.getErrorCount();
                        if (!isLogin || (autoLogoutIdleLimit < sinceIdle) || (autoLogoutErrorLimit < errorCount)) {
                            App.Controller.Login.modelDetectedIdle();
                            return false;

                        } else {
                            return true;
                        }
                    }
                );
            }
        }
        ,isLogin: function() {
            return Acm.goodValue(this.loginStatus.get(), false);
        }
        ,setLoginStatus: function(val) {
            this.loginStatus.set(val);
        }

        ,getLastIdle: function() {
            return this.lastIdle.get();
        }
        ,setLastIdle: function(val) {
            var seconds = Acm.goodValue(val, new Date().getTime());
            this.lastIdle.set(seconds);
        }
        ,getSinceIdle: function() {
            var last = this.getLastIdle();
            var now = new Date().getTime();
            return now - last;
        }

        ,getErrorCount: function() {
            var count = 0;
            if (Acm.isNotEmpty(this.errorCount)) {
                count = this.errorCount.get();
            }
            return count;
        }
        ,setErrorCount: function(val) {
            if (Acm.isNotEmpty(this.errorCount)) {
                this.errorCount.set(val);
            }
        }
    }

    ,I18n: {
        prepare : function() {
            var contextPath = App.getContextPath();
            this.i18nData = new Acm.Model.LocalData(App.Model.Storage.LOCAL_DATA_I18N + contextPath);
            this.i18nTracker = new Acm.Model.SessionData(App.Model.Storage.SESSION_DATA_I18N_TRACKER);
        }
        ,create: function() {}
        ,onInitialized: function() {}

        ,init: function(context) {
            var promiseI18n = $.Deferred();
            var promiseLng = $.Deferred();
            var that = App.Model.I18n;
            that.setLastError(null);

            //
            // Get language setting
            //
            var lng = that.getLng();

            if (Acm.isNotEmpty(context.loginPage)) {
                var resLogin = null;
                if (Acm.isNotEmpty(lng)) {
                    resLogin = that.getResource(lng, "login");
                }

                if (Acm.isNotEmpty(resLogin)) {
                    promiseLng.resolve(lng);
                } else {
                    promiseI18n.resolve();
                }

            } else {
                if (that.isCurrentLng()) {
                    promiseLng.resolve(lng);

                } else {
                    App.Service.I18n.retrieveSettings()
                        .done(function() {
                            var lng = that.getLng();
                            promiseLng.resolve(lng);
                        })
                        .fail(function() {
                            promiseLng.reject();
                        });
                }
            }

            //
            // Get resource
            //
            promiseLng.then(function(lan) {
                    //
                    // help out login page by retrieving and caching a copy for future use
                    //
                    if (!context.loginPage) {
                        if (!that.isCurrentResource(lng, "login")) {
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

                    var err = null;
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
                                var res = that.getResource(lng, ns);
                                if (context.loginPage) {
                                    if (Acm.isNotEmpty(res)) {
                                        loadComplete(null, res);
                                    } else {
                                        err = "Resource error - " + lng + "." + ns;
                                        loadComplete(err, null);
                                    }

                                } else {
                                    if (that.isCurrentResource(lng, ns)) {
                                        loadComplete(null, res);

                                    } else {
                                        App.Service.I18n.retrieveResource(lng, ns)
                                            .done(function(data) {
                                                var res = that.getResource(lng, ns);
                                                that.setLastError(err);
                                                loadComplete(null, res);
                                            })
                                            .fail(function(data) {
                                                err = "Resource error - " + lng + "." + ns;
                                                that.setLastError(err);
                                                loadComplete(err, null);
                                            })
                                        ;
                                    }
                                }
                            }
                        }
                        ,function() {
                            $('*[data-i18n]').i18n();
                            $(document).trigger('i18n-ready');
//                        if (Acm.isEmpty(that.getLastError())) {
//                            promiseI18n.resolve();
//                        } else {
//                            promiseI18n.reject();
//                        }
                            promiseI18n.resolve();
                        });
                }
                ,function() {
                    promiseI18n.reject();
                });

            return promiseI18n;
        }

        ,_error: null
        ,getLastError: function() {
            return this._error;
        }
        ,setLastError: function(error) {
            this._error = error;
        }

        ,getLng: function() {
           return this.getValue("lng");
        }
        ,setLng: function(lng) {
            this.setValue("lng", lng);
        }
        ,isCurrentLng: function() {
            return this.isCurrent("lng");
        }
        ,setCurrentLng: function(v) {
            this.setCurrent("lng", v);
        }
        ,getResource: function(lng, ns) {
            var key = lng + "." + ns;
            return this.getValue(key);
        }
        ,setResource: function(lng, ns, res) {
            var key = lng + "." + ns;
            this.setValue(key, res);
        }
        ,isCurrentResource: function(lng, ns) {
            var key = lng + "." + ns;
            return this.isCurrent(key);
        }
        ,setCurrentResource: function(lng, ns, v) {
            var key = lng + "." + ns;
            this.setCurrent(key, v);
        }
        ,getValue: function(k) {
            var v = "";
            var data = this.i18nData.get();
            if (Acm.isNotEmpty(data)) {
                v = Acm.goodValue(data[k]);
            }
            return v;
        }
        ,setValue: function(k, v) {
            var data = this.i18nData.get();
            if (Acm.isEmpty(data)) {
                data = {};
            }
            data[k] = v;
            this.i18nData.set(data);
        }
        ,isCurrent: function(k) {
            var v = false;
            var flags = this.i18nTracker.get();
            if (Acm.isNotEmpty(flags)) {
                v = Acm.goodValue(flags[k], false);
            }
            return v;
        }
        ,setCurrent: function(k, v) {
            var flags = this.i18nTracker.get();
            if (Acm.isEmpty(flags)) {
                flags = {};
            }
            flags[k] = v;
            this.i18nTracker.set(flags);
        }
        ,validateSettings: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.defaultLang)) {
                return false;
            }
            return true;
        }
        ,validateResource: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            return true;
        }
    }

    ,Config: {
        prepare : function() {
            var contextPath = App.getContextPath();
            this.configMap = new Acm.Model.LocalData(App.Model.Storage.LOCAL_DATA_CONFIG + contextPath);
            this.configTracker = new Acm.Model.SessionData(App.Model.Storage.SESSION_DATA_CONFIG_TRACKER);
        }
        ,create: function() {}
        ,onInitialized: function() {}

        ,CONFIG_NAME_APP : "app"

        ,request: function() {
            App.Model.Config.requestConfig(App.Model.Config.CONFIG_NAME_APP).done(function(data) {
                var cfg = App.Model.Config.getConfig(App.Model.Config.CONFIG_NAME_APP);
                if (Acm.isNotEmpty(cfg)) {
                    var myCfg = App.Model.Config.getMyConfig();
                    var settings = cfg.settings;
                    if (Acm.isNotEmpty(settings)) {
                        myCfg.settings = {};
                        myCfg.settings.autoLogoutIdleLimit = Acm.goodValue(settings.autoLogoutIdleLimit, 1200000);  //1200000 == 20x60x1000ms = 20min
                        myCfg.settings.autoLogoutErrorLimit = Acm.goodValue(settings.autoLogoutErrorLimit, 6);
                        myCfg.settings.issueCollectorFlag = Acm.goodValue(settings.issueCollectorFlag, false);
                    }
                }
                var z = 1;
            });
        }

        ,_myConfig: null
        ,getMyConfig: function() {
            if (!this._myConfig) {
                this._myConfig = {};
            }
            return this._myConfig;
        }

        ,_requests: []
        ,requestConfig: function(name) {
            var req = $.when();
            if (!App.Model.Config.isCurrent(name)) {
                req = App.Service.Config.retrieveConfig(name);
                this._requests.push(req);
            }
            return req;
        }
        ,resolveConfig: function() {
            return Acm.Promise.resolvePromises(this._requests);
//            var resolver = $.Deferred();
//            $.when.apply(this, this._requests).then(function(data) {
//                    resolver.resolve();
//                }, function(e) {
//                    resolver.reject();
//                }
//            );
//            return resolver;
        }
        ,getConfig: function(k) {
            var v = "";
            var data = this.configMap.get();
            if (Acm.isNotEmpty(data)) {
                v = Acm.goodValue(data[k]);
            }
            return v;
        }
        ,setConfig: function(k, v) {
            var data = this.configMap.get();
            if (Acm.isEmpty(data)) {
                data = {};
            }
            data[k] = v;
            this.configMap.set(data);
        }
        ,isCurrent: function(k) {
            var v = false;
            var flags = this.configTracker.get();
            if (Acm.isNotEmpty(flags)) {
                v = Acm.goodValue(flags[k], false);
            }
            return v;
        }
        ,setCurrent: function(k, v) {
            var flags = this.configTracker.get();
            if (Acm.isEmpty(flags)) {
                flags = {};
            }
            flags[k] = v;
            this.configTracker.set(flags);
        }
        ,validateConfig: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            return true;
        }
    }
};




