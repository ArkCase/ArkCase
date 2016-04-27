'use strict';

angular.module('goodbye').controller('GoodbyeController', ['$window'
    , 'UtilService', 'Acm.LoginService', 'LookupService', 'Acm.AppService'
    , function ($window, Util, AcmLoginService, LookupService, AcmAppService
    ) {
        // Retrieves the app properties from app-config.xml file
        var appConfig = LookupService.getConfig('app').then(function (data) {
            // clear redirectURL and redirectState
            localStorage.removeItem('redirectURL');
            sessionStorage.removeItem('redirectState');
            AcmLoginService.resetCaches();
            AcmLoginService.setLogin(false);

            // redirect to logout page
            $window.location.href = AcmAppService.getAppUrl(Util.goodMapValue(data, "logoutUrl", "/logout"));
        });

    }
]);