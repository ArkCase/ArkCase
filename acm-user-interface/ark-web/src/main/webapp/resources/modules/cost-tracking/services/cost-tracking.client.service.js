'use strict';

angular.module('services').factory('CostTrackingService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/v1/service/costsheet', {}, {

            listObjects: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/service/costsheet/user/:userId?start=:start&n=:n&sort=:sort',
                cache: false,
                isArray: false
            },

            get: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/service/costsheet/:id',
                cache: false,
                isArray: false
            },

            save: {
                method: 'POST',
                url: 'proxy/arkcase/api/v1/service/costsheet',
                cache: false
            }
        });
    }
]);