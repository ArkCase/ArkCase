/**
 * App.Model
 *
 * @author jwu
 */
App.Model = {
    create : function() {
        if (App.Model.Login.create)            {App.Model.Login.create();}
        if (App.Model.I18n.create)             {App.Model.I18n.create();}
    }
    ,onInitialized: function() {
        if (App.Model.Login.onInitialized)     {App.Model.Login.onInitialized();}
        if (App.Model.I18n.onInitialized)      {App.Model.I18n.onInitialized();}
    }

    ,Login: {
        create : function() {
            var contextPath = App.getContextPath();
            this.loginStatus = new Acm.Model.LocalData(Application.LOCAL_DATA_LOGIN_STATUS + contextPath);
            this.lastIdle = new Acm.Model.LocalData(Application.LOCAL_DATA_LAST_IDLE + contextPath);
            this.errorCount = new Acm.Model.LocalData(Application.LOCAL_DATA_ERROR_COUNT + contextPath);
        }
        ,onInitialized: function() {
            var isLogin = App.Model.Login.isLogin();
            if (isLogin) {
                Acm.Timer.useTimer("AutoLogout"
                    //,30
                    ,20  //every twenty seconds
                    //,10
                    ,function() {
                        var isLogin = App.Model.Login.isLogin();
                        var sinceIdle = App.Model.Login.getSinceIdle();
                        var errorCount = App.Model.Login.getErrorCount();
                        if (!isLogin || (1200000 < sinceIdle) || (6 < errorCount)) {  //20x60x1000ms=20min
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
        create: function() {}
        ,onInitialized: function() {}

        ,init : function() {
            var contextPath = App.getContextPath();
            this.dataCache = new Acm.Model.LocalData(Application.LOCAL_DATA_I18N + contextPath);
            this.flagCache = new Acm.Model.SessionData(Application.SESSION_DATA_I18N_FLAGS);
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
            var data = this.dataCache.get();
            if (Acm.isNotEmpty(data)) {
                v = Acm.goodValue(data[k]);
            }
            return v;
        }
        ,setValue: function(k, v) {
            var data = this.dataCache.get();
            if (Acm.isEmpty(data)) {
                data = {};
            }
            data[k] = v;
            this.dataCache.set(data);
        }
        ,isCurrent: function(k) {
            var v = false;
            var flags = this.flagCache.get();
            if (Acm.isNotEmpty(flags)) {
                v = Acm.goodValue(flags[k], false);
            }
            return v;
        }
        ,setCurrent: function(k, v) {
            var flags = this.flagCache.get();
            if (Acm.isEmpty(flags)) {
                flags = {};
            }
            flags[k] = v;
            this.flagCache.set(flags);
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
};




