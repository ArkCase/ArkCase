'use strict';

angular.module('services').factory('PermissionsService', ['$resource',
    function($resource) {
        return $resource('api/permissions',{
        },{
            queryPermissions: {
                method: 'GET',
                cache: true,
                url: 'api/permissions',
                isArray: false
            }
        });
    }
]);