'use strict';

angular.module('services').factory('LookupService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/latest/plugin', {}, {

            getPriorites: {
                method: 'GET',
                cache: true,
                isArray: true,
                url: 'proxy/arkcase/api/latest/plugin/complaint/priorities'
            }

        });
    }
]);