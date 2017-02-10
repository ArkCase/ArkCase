'use strict';

angular.module('goodbye').controller('GoodbyeController', ['$window'
    , 'Acm.StoreService', 'UtilService', 'Acm.LoginService', 'LookupService', 'Acm.AppService', 'WebSocketsListener'
    , function ($window, Store, Util, AcmLoginService, LookupService, AcmAppService, WebSocketService) {
        // Retrieves the app properties from app-config.xml file
        var appConfig = LookupService.getConfig('app').then(function (data) {
            var logoutUrl = AcmAppService.getAppUrl(Util.goodMapValue(data, "logoutUrl", "/logout"));

            AcmLoginService.setLogin(false);
            sessionStorage.removeItem('redirectURL');
            sessionStorage.removeItem('redirectState');
            Store.Registry.clearSessionCache();
            Store.Registry.clearLocalCache();

            try {
                // disconnect websocket
                WebSocketService.disconnect();
            } catch (exc) {

            }
            $window.location.href = logoutUrl;

        });

    }
]);