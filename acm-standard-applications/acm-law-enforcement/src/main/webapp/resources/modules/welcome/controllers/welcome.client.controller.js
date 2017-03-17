'use strict';

angular.module('welcome').controller(
    'WelcomeController', ['$state', '$window', '$translate'
        , 'UtilService', 'Acm.LoginService', 'Acm.AppService'
        , function ($state, $window, $translate
            , Util, AcmLoginService, AcmAppService) {

            AcmLoginService.setLogin(true);
            AcmLoginService.setLastIdle();


            if (sessionStorage.redirectState) {
                // redirect to the last remembered state
                var redirectState = angular.fromJson(sessionStorage.redirectState);

                //because of redirect bug where we are stuck in goodbye state
                //here is the fix
                var index = redirectState.hash.indexOf('goodbye');
                if (index >= 0) {
                    sessionStorage.removeItem("redirectState");
                    $state.go("dashboard");
                } else {
                    sessionStorage.removeItem("redirectState");
                    $state.go(redirectState.hash.split('/')[1]);
                }
            } else if (sessionStorage.redirectURL) {
                // redirect to hash passed in the URL of the login page
                sessionStorage.removeItem("redirectUrl");
                $window.location.href = AcmAppService.getAppUrl('home.html' + sessionStorage.redirectURL);
            } else {
                $state.go("dashboard");
            }

        }
    ]);
