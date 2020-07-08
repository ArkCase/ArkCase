'use strict';

angular.module('queues').factory('Queues.PrintService', [ '$resource', function($resource) {
    return $resource('api/v1/plugin/pitney-bowes-folders', {}, {
        getFolderInfo: {
            method: 'GET',
            cache: false,
            url: 'api/v1/plugin/pitney-bowes-folders'
        }
    });
} ]);
