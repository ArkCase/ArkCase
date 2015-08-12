'use strict';

angular.module('services').factory('SchemasService', ['$resource',
    function($resource) {
        return $resource('api/config/',{
        },{
            getSchema: {
                method: 'GET',
                cache: false,
                url: 'modules_config/schemas/{schemaId}.json',
                isArray: false
            }
        });
    }
]);