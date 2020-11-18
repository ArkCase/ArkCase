'use strict';

angular.module('admin').factory('Admin.PortalConfigurationService', [ '$http', function($http) {

    var getPortalConfig = function () {
        return $http({
            method: 'GET',
            url: 'api/latest/service/portalgateway/admin/portals/config'
        });
    };

    var savePortalConfig = function (portal) {
        return $http({
            method: 'PUT',
            url: 'api/latest/service/portalgateway/admin/portals/config',
            data: portal,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    var getAuthenticatedMode = function () {
        return $http({
            method: 'GET',
            url: 'api/latest/service/portalgateway/admin/portals/authenticatedMode'
        });
    };

    var saveAuthenticatedMode = function (portal) {
        return $http({
            method: 'PUT',
            url: 'api/latest/service/portalgateway/admin/portals/authenticatedMode',
            data: portal,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    return {
        getAuthenticatedMode: getAuthenticatedMode,
        saveAuthenticatedMode: saveAuthenticatedMode,
        getPortalConfig: getPortalConfig,
        savePortalConfig: savePortalConfig
    };

} ]);