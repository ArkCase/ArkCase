'use strict';

angular.module('welcome').controller(
    'WelcomeController', ['$state', '$window', '$translate'
        , 'Acm.LoginService', 'Acm.AppService', 'Config.LocaleService'
        , function ($state, $window, $translate
            , AcmLoginService, AcmAppService, LocaleService) {

            AcmLoginService.setLogin(true);
            AcmLoginService.setLastIdle();

            LocaleService.getLatestSettings();


            var redirectState = sessionStorage.redirectState;
            var redirectURL = sessionStorage.redirectURL;
            sessionStorage.removeItem("redirectState");
            sessionStorage.removeItem("redirectURL");
            //TODO: check whats up with 'passwordWarningAccepted'; don't see its usage
            //sessionStorage.removeItem('passwordWarningAccepted');

            if (redirectState) {
                // redirect to the last remembered state
                var redirectState = angular.fromJson(redirectState);

                //because of redirect bug where we are stuck in goodbye state
                //here is the fix
                var index = redirectState.hash.indexOf('goodbye');
                if (index >= 0) {
                    $state.go("dashboard");
                } else {
                    $state.go(redirectState.hash.split('/')[1]);
                }
            } else if (redirectURL) {
                // redirect to hash passed in the URL of the login page
                $window.location.href = AcmAppService.getAppUrl('home.html' + redirectURL);
            } else {
                $state.go("dashboard");
            }

        }
    ]);
