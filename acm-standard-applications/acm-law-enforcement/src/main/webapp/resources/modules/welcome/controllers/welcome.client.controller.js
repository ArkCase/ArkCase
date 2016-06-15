'use strict';

angular.module('welcome').controller(
    'WelcomeController',
    [
        '$state',
        '$window',
        'Acm.LoginService',
        'Acm.AppService',
        function ($state, $window, AcmLoginService, AcmAppService) {
            AcmLoginService.resetCaches();
            AcmLoginService.setLogin(true);
            AcmLoginService.setLastIdle();

            // TODO: check if this is needed. It is used in
            // 'user-info.client.controller.js'. Doesn't seem like it is
            // necessary...
            // In the 'goodbye.client.controller.js' the
            // AcmLoginService.setLogin(false) will do nothing because
            // the page will already be redirected to logout page.
            //
            // jwu: setLogin() flag is needed for auto logout.
            // setLogin(false) in goodbye is now fixed to call before
            // redirection.

            if (sessionStorage.redirectState) {
                // redirect to the last remembered state
                var redirectState = angular
                    .fromJson(sessionStorage.redirectState);
                sessionStorage.removeItem("redirectState");
                $state.go(redirectState.hash.split('/')[1]);
            } else if (localStorage.redirectURL) {
                // redirect to hash passed in the URL of the login page
                localStorage.removeItem("redirectUrl");
                $window.location.href = AcmAppService
                    .getAppUrl('home.html'
                        + localStorage.redirectURL);
            } else {
                $state.go("dashboard");
            }
        }]);