'use strict';

angular.module('admin').factory('Admin.PortalConfigurationService', [ '$http', function($http) {

    var getPortals = function () {
        return $http({
            method: 'GET',
            url: 'api/latest/service/portalgateway/admin/portals'
        });
    };

    var savePortal = function (portal) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/portalgateway/admin/portals',
            data: portal,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    var updatePortal = function (portal) {
        return $http({
            method: 'PUT',
            url: 'api/latest/service/portalgateway/admin/portals',
            data: portal,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };
    var deletePortal = function (portalId) {
        return $http({
            method: 'DELETE',
            url: 'api/latest/service/portalgateway/admin/portals/' + portalId
        })
    };

    var revertPortal = function (portal) {
        return $http({
            method: 'PUT',
            url: 'api/latest/service/portalgateway/admin/portals/revert',
            data: portal,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    var getAuthenticatedMode = function () {
        return $http({
            method: 'GET',
            url: 'api/latest/service/portalgateway/admin/portals/config'
        });
    };

    var saveAuthenticatedMode = function (portal) {
        return $http({
            method: 'PUT',
            url: 'api/latest/service/portalgateway/admin/portals/config',
            data: portal,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    return {
        getPortals: getPortals,
        savePortal: savePortal,
        updatePortal: updatePortal,
        deletePortal: deletePortal,
        revertPortal: revertPortal,
        getAuthenticatedMode: getAuthenticatedMode,
        saveAuthenticatedMode: saveAuthenticatedMode
    };

} ]);