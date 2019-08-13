'use strict';

angular.module('admin').factory('Admin.ResetConfigurationService', [ '$http', function($http) {

    return ({
        resetConfiguration: resetConfiguration
    });

    function resetConfiguration() {
        return $http({
            method: 'DELETE',
            url: 'api/latest/plugin/admin/configuration/reset'
        });
    }
} ]);