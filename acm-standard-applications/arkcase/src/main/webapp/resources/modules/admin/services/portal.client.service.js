'use strict';

angular.module('admin').factory('Admin.PortalConfigurationService', [ '$http', function($http) {

    var getPortalConfig = function () {
        return $http({
            method: 'GET',
            url: 'api/latest/service/portalgateway/admin/portal/config'
        });
    };

    var savePortalConfig = function (portal) {
        return $http({
            method: 'PUT',
            url: 'api/latest/service/portalgateway/admin/portal/config',
            data: portal,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    return {
        getPortalConfig: getPortalConfig,
        savePortalConfig: savePortalConfig
    };

} ]);