'use strict';

angular.module('services').factory('AdministrationService', ['$resource',
    function($resource) {
        return $resource('api/administration',{
        },{
            queryModules: {
                method: 'GET',
                cache: false,
                url: 'api/administration/modules',
                isArray: true
            },

            getModule: {
                method: 'GET',
                cache: false,
                url: 'api/administration/modules/:moduleId',
                isArray: false
            },

            updateModule: {
                method: 'PUT',
                url: 'api/administration/modules/:moduleId',
                isArray: false
            },

            getSchema: {
                method: 'GET',
                cache: true,
                url: 'api/administration/schemas/:schemaId',
                isArray: false
            }
        });
    }
]);