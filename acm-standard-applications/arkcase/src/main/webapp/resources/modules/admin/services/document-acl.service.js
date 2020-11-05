'use strict';

angular.module('admin').factory('Admin.DocumentACLService', [ '$http', function($http) {

    var _getProperties = function() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/admin/getDataAccessControlProperties'
        });
    };

    return {
        getProperties: _getProperties,
    };

} ]);
