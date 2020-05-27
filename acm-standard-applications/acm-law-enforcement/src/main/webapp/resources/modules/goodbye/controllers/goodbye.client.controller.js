'use strict';

angular.module('goodbye').controller('GoodbyeController', [ '$window', 'Acm.StoreService', 'UtilService', 'Acm.LoginService', 'ApplicationConfigService', 'Acm.AppService', 'WebSocketsListener', function($window, Store, Util, AcmLoginService, ApplicationConfigService, AcmAppService, WebSocketService) {
    // Retrieves the property from arkcase.yaml file
        ApplicationConfigService.getProperty(ApplicationConfigService.PROPERTIES.LOGOUTURL).then(function (result) {
                var logoutUrl = AcmAppService.getAppUrl(result);

        AcmLoginService.setLogin(false);
        sessionStorage.clear();
        Store.Registry.clearSessionCache();
        Store.Registry.clearLocalCache();

        try {
            // disconnect websocket
            WebSocketService.disconnect();
        } catch (exc) {

        }
        $window.location.href = logoutUrl;

    });

} ]);