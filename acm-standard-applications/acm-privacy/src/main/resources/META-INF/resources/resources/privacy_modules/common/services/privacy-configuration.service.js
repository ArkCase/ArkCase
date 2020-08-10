'use strict';


angular.module('services').factory('PrivacyConfiguration.Service', ['$http', 'UtilService', function ($http, Util) {

    var _isDashboardBannerEnabled = function() {
        var url = 'api/latest/service/privacy/configuration/dashboardBannerConfiguration';
            return $http({
                method: 'GET',
                url: url
            });
        };

    return {
        isDashboardBannerEnabled: _isDashboardBannerEnabled
    };

}]);
