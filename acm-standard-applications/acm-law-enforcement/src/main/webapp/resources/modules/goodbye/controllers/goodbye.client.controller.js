'use strict';

angular.module('goodbye').controller('GoodbyeController', ['$window'
    , 'Acm.StoreService', 'UtilService', 'Acm.LoginService', 'LookupService', 'Acm.AppService'
    , function ($window, Store, Util, AcmLoginService, LookupService, AcmAppService) {
        // Retrieves the app properties from app-config.xml file
        var appConfig = LookupService.getConfig('app').then(function (data) {
            var logoutUrl = AcmAppService.getAppUrl(Util.goodMapValue(data, "logoutUrl", "/logout"));

            localStorage.removeItem('redirectURL');
            sessionStorage.removeItem('redirectState');
            sessionStorage.removeItem('warningAccepted');

            AcmLoginService.setLogin(false);
            Store.Registry.clearSessionCache();

            $window.location.href = logoutUrl;
        });

    }
]);