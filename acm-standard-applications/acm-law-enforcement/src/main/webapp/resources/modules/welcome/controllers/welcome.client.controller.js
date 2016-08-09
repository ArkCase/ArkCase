'use strict';

angular.module('welcome').controller(
    'WelcomeController', [
        '$state',
        '$window',
        'Acm.LoginService',
        'Acm.AppService',
        function($state, $window, AcmLoginService, AcmAppService) {
            AcmLoginService.setLogin(true);
            AcmLoginService.setLastIdle();


            if (sessionStorage.redirectState) {
                // redirect to the last remembered state
                var redirectState = angular
                    .fromJson(sessionStorage.redirectState);
                sessionStorage.removeItem("redirectState");
                $state.go(redirectState.hash.split('/')[1]);
            //} else if (localStorage.redirectURL) {
            } else if (sessionStorage.redirectURL) {
                // redirect to hash passed in the URL of the login page
                //localStorage.removeItem("redirectUrl");
                sessionStorage.removeItem("redirectUrl");
                $window.location.href = AcmAppService
                    //.getAppUrl('home.html' + localStorage.redirectURL);
                    .getAppUrl('home.html' + sessionStorage.redirectURL);
            } else {
                $state.go("dashboard");
            }
        }
    ]);
