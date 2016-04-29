'use strict';

var AcmLoginController = ["$q", "$scope", "$document", "$state", "$translate"
    , "UtilService", "ConfigService", "Util.TimerService", "Authentication", "Acm.LoginService"
    , function($q, $scope, $document, $state, $translate
        , Util, ConfigService, UtilTimerService, Authentication, AcmLoginService
    ) {
        var ctrl = this;

        var promiseConfig = ConfigService.getComponentConfig("core", "acmLogin").then(function (config) {
            ctrl.idleLimit = Util.goodValue(config.idleLimit, 600000);     //600000 - every 10 minutes
            ctrl.idlePull = Util.goodValue(config.idlePull, 5000);         //5000 - every 20 seconds
            ctrl.idleConfirm = Util.goodValue(config.idleConfirm, 15000);   //15000 - every 15 seconds
            return config;
        });

        Authentication.queryUserInfo().then(
            function (userInfo) {
                AcmLoginService.setUserId(Util.goodMapValue(userInfo, "userId"));
                return userInfo;
            }
        );

        var promiseSetLogin = AcmLoginService.getSetLoginPromise();
        $q.all([promiseConfig, promiseSetLogin]).then(function(data){
            ctrl.waitConfirm = false;
            UtilTimerService.useTimer("AutoLogout", ctrl.idlePull, function() {
                var isLogin = AcmLoginService.isLogin();
                if (!isLogin) {
                    AcmLoginService.logout();
                    return false;
                }
                if (ctrl.waitConfirm) {
                    if (AcmLoginService.isConfirmCanceled()) {
                        bootbox.hideAll();
                        ctrl.waitConfirm = false;
                        return;
                    }
                } else { //if (!ctrl.waitConfirm) {
                    var sinceIdle = AcmLoginService.getSinceIdle();
                    if (ctrl.idleLimit < sinceIdle) {
                        ctrl.onIdleDetected();
                    }
                }

                return true;
            });

        });


        ctrl.onIdleDetected = function () {
            UtilTimerService.useTimer("AboutToLogout", ctrl.idleConfirm, function() {
                if (ctrl.waitConfirm) {
                    if (AcmLoginService.isConfirmCanceled()) {
                        bootbox.hideAll();
                        ctrl.waitConfirm = false;
                        return;
                    }
                }

                AcmLoginService.logout();
                return false;
            });

            ctrl.waitConfirm = true;
            AcmLoginService.setConfirmCanceled(false);
            bootbox.confirm($translate.instant("common.comp.acmLogin.confirmLogout"), function(result) {
                UtilTimerService.removeListener("AboutToLogout");
                if (result) {
                    UtilTimerService.removeListener("AutoLogout");
                    AcmLoginService.logout();
                } else {
                    AcmLoginService.setConfirmCanceled(true);
                    AcmLoginService.setLastIdle();
                }
                ctrl.waitConfirm = false;
            });
        };


        $document.on("mousemove", function(e){ctrl.onUserActivity(e);});
        $document.on("keypress", function(e){ctrl.onUserActivity(e);});

        ctrl.onUserActivity = function () {
            AcmLoginService.setLastIdle();
        };

}];


