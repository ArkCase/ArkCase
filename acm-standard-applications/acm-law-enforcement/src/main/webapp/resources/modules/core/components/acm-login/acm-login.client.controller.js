'use strict';

var AcmLoginController = ["$scope", "$document", "$state", "$translate"
    , "UtilService", "ConfigService", "LookupService", "Util.TimerService", "Acm.LoginService"
    , function($scope, $document, $state, $translate
        , Util, ConfigService, LookupService, UtilTimerService, AcmLoginService
    ) {
        var ctrl = this;
return;
        var cnt = 0;
        ConfigService.getComponentConfig("core", "acmLogin").then(function (config) {
            ctrl.idleLimit = 600000;
            ctrl.idlePull = 20000;
            ctrl.idleConfirm = 15000;
            var z = 1;
            return config;
        });

        LookupService.getUsers().then(function (users) {
            ctrl.users = users;
        });

        AcmLoginService.setLogin(true);

        ctrl.waitConfirm = false;
        UtilTimerService.useTimer("AutoLogout", 4000, function() { //20000 - every 20 seconds
            var isLogin = AcmLoginService.isLogin();
            if (!isLogin) {
                ctrl.logout();
                return false;
            }

            if (!ctrl.waitConfirm) {
                var idleLimit = ctrl.idleLimit;
                var sinceIdle = AcmLoginService.getSinceIdle();

                //this.limit
                if (5 < cnt++) {
                    ctrl.onIdleDetected();
                }
                console.log("AutoLogout! " + cnt);
            }

            return true;
        });

        ctrl.onIdleDetected = function () {
            UtilTimerService.useTimer("AboutToLogout", 15000, function() {
                ctrl.logout();
                return false;
            });

            ctrl.waitConfirm = true;
            bootbox.confirm($translate.instant("core.comp.acmLogin.confirmLogout"), function(result) {
                if (result) {
                    ctrl.logout();
                } else {
                    AcmLoginService.setLastIdle();
                }
                UtilTimerService.removeListener("AutoLogout");
                ctrl.waitConfirm = false;
            });
        };

        ctrl.logout = function() {
            $state.go("goodbye");
        };

        $document.on("mousemove", function(e){ctrl.onUserActivity(e);});
        $document.on("keypress", function(e){ctrl.onUserActivity(e);});

        ctrl.onUserActivity = function () {
            var a15 = AcmLoginService.isLogin();
            var a16 = AcmLoginService.getLastIdle();
            AcmLoginService.setLastIdle();
            var a17 = AcmLoginService.getSinceIdle();
            console.log('onUserActivity:' + (++cnt));
        };

}];


