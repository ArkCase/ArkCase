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

    var getPortalUsers = function (start, maxRows, sortBy, sortDir) {
        return $http({
            method: 'GET',
            url: 'api/latest/service/portalgateway/admin/portals/users',
            params: {
                start: start,
                n: maxRows,
                sortBy: sortBy,
                sortDir: sortDir
            }
        });
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

    return {
        getPortals: getPortals,
        savePortal: savePortal,
        updatePortal: updatePortal,
        deletePortal: deletePortal,
        getPortalUsers: getPortalUsers,
        revertPortal: revertPortal
    };

} ]);