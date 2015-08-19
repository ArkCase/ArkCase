'use strict';

angular.module('services').factory('PermissionsService', ['$resource',
    function($resource) {
        return $resource('modules_config/permissions/modules.json',{
        },{
            queryPermissions: {
                method: 'GET',
                cache: true,
                url: 'modules_config/permissions/modules.json',
                isArray: false
            }
        });
    }
]);