/**
 * App.Model
 *
 * @author jwu
 */
App.Model = {
    create : function() {
        if (App.Model.Login.create)            {App.Model.Login.create();}
    }
    ,onInitialized: function() {
        if (App.Model.Login.onInitialized)     {App.Model.Login.onInitialized();}
    }

    ,Login: {
        create : function() {
            this.loginStatus = new Acm.Model.LocalData(Application.LOCAL_DATA_LOGIN_STATUS);
            this.lastIdle = new Acm.Model.LocalData(Application.LOCAL_DATA_LAST_IDLE);
            this.errorCount = new Acm.Model.LocalData(Application.LOCAL_DATA_ERROR_COUNT);

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
            return this.errorCount.get();
        }
        ,setErrorCount: function(val) {
            this.errorCount.set(val);
        }
    }

};




