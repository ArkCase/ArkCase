'use strict';


angular.module('services').factory('FOIAConfiguration.Service', [ '$http', 'UtilService', function($http, Util) {

    var _isDashboardBannerEnabled = function() {
            var url = 'api/latest/service/foia/configuration/dashboardBannerConfiguration';
            return $http({
                method: 'GET',
                url: url
            });
        };

    return {
        isDashboardBannerEnabled: _isDashboardBannerEnabled
    };

}]);
