'use strict';

angular.module('welcome').controller('WelcomeController', [ '$state', '$window', '$translate', 'UtilService', 'Acm.LoginService', 'Acm.AppService', 'Config.LocaleService', function($state, $window, $translate, Util, AcmLoginService, AcmAppService, LocaleService) {

    var redirectState = angular.fromJson(sessionStorage.redirectState);
    var redirectURL = angular.copy(sessionStorage.redirectURL);
    sessionStorage.clear();

    AcmLoginService.setLogin(true);
    AcmLoginService.setLastIdle();

    LocaleService.getLatestSettings();

    if (redirectURL) {
        // redirect to hash passed in the URL of the login page
        sessionStorage.removeItem("redirectUrl");
        $window.location.href = AcmAppService.getAppUrl('home.html' + redirectURL);
    } else if (redirectState) {
        // redirect to the last remembered state
        //because of redirect bug where we are stuck in goodbye state
        //here is the fix
        var index = redirectState.hash.indexOf('goodbye');
        sessionStorage.removeItem("redirectState");
        if (index >= 0) {
            $state.go("dashboard");
        } else {
            $window.location.href = AcmAppService.getAppUrl('home.html' + redirectState.hash);
        }
    } else {
        $state.go("dashboard");
    }

} ]);
