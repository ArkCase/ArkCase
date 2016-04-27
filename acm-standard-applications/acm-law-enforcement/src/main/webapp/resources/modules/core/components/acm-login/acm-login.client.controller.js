'use strict';

var AcmLoginController = ["$q", "$scope", "$document", "$state", "$translate", "$translatePartialLoader"
    , "UtilService", "ConfigService", "LookupService", "Util.TimerService", "Authentication", "Acm.LoginService"
    , function($q, $scope, $document, $state, $translate, $translatePartialLoader
        , Util, ConfigService, LookupService, UtilTimerService, Authentication, AcmLoginService
    ) {
        var ctrl = this;

        //$translatePartialLoader.addPart('common');
        $translatePartialLoader.addPart('core');
        $translate.refresh();

        var promiseConfig = ConfigService.getComponentConfig("core", "acmLogin").then(function (config) {
            ctrl.idleLimit = Util.goodValue(config.idleLimit, 600000);     //600000 - every 10 minutes
            ctrl.idlePull = Util.goodValue(config.idlePull, 20000);         //20000 - every 20 seconds
            ctrl.idleConfirm = Util.goodValue(config.idleConfirm, 15000);   //15000 - every 15 seconds

            //ctrl.idlePull = 4000; for testing

            return config;
        });

        LookupService.getUsers().then(function (users) {
            ctrl.users = users;
        });
        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userFullName = userInfo.fullName;
                $scope.userId = userInfo.userId;
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

                if (!ctrl.waitConfirm) {
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
                AcmLoginService.logout();
                return false;
            });

            ctrl.waitConfirm = true;
            bootbox.confirm($translate.instant("core.comp.acmLogin.confirmLogout"), function(result) {
                UtilTimerService.removeListener("AboutToLogout");
                if (result) {
                    UtilTimerService.removeListener("AutoLogout");
                    AcmLoginService.logout();
                } else {
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


