'use strict';

angular.module('admin').factory('Admin.ApplicationVersionService', [ '$http', function($http) {

    return ({
        getApplicationVersion: getApplicationVersion
    });

    function getApplicationVersion() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/admin/application/version'
        });
    }
} ]);