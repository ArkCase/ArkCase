'use strict';

var AcmIdleLogoutController = ["$scope", "$filter", "$document"
    , "UtilService", "Acm.StoreService", "Util.TimerService", "Acm.LoginStatService", "UserActivityService"
    , function($scope, $filter, $document
        , Util, Store, Timer, AcmLoginStatService, UserActivityService
    ) {
        var ctrl = this;

        var cnt = 0;
        var a1 = ctrl.idleLimit;
        var a2 = $filter;
        var a3 = Util;
        var a4 = Timer;

        $document.on("mousemove", function(e){onUserActivity(e);});
        $document.on("keypress", function(e){onUserActivity(e);});


        var a5 = AcmLoginStatService.isLogin();
        var a6 = AcmLoginStatService.getLastIdle();
        AcmLoginStatService.setLastIdle();
        var a7 = AcmLoginStatService.getSinceIdle();

        function onUserActivity () {
            var a15 = AcmLoginStatService.isLogin();
            var a16 = AcmLoginStatService.getLastIdle();
            AcmLoginStatService.setLastIdle();
            var a17 = AcmLoginStatService.getSinceIdle();
            console.log('onUserActivity:' + (++cnt));
        }

        Timer.useTimer("IdleLogout", 4000, function() {
            var isLogin = AcmLoginStatService.isLogin();
            if (!isLogin) {
                $state.go("goodbye");
                return false;
            }

            var idleLimit = ctrl.idleLimit;
            var sinceIdle = AcmLoginStatService.getSinceIdle();

            //this.limit
            if (5 < cnt++) {
                return false;
            }
            console.log("AcmIdleLogoutController::acmIdleLogout controller! " + cnt);
            return true;
        });

        return;

        // Be sure that service is stopped after module destroyed
        $scope.$on('$destroy', function () {
            UserActivityService.stop();
        });

        // Set user activity timeout to 10 seconds
        UserActivityService.start(10000, userActivityTimeout);


        function userActivityTimeout () {
            console.log('User activity timeout expired');
        }


}];

//function AcmIdleLogoutController2 () {
//    //var ctrl = this;
//
//    console.log("AcmIdleLogoutController::acmIdleLogout conroller! " + this.limit);
//}

