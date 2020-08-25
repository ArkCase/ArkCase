'use strict';

angular.module('services').factory('QueuesService', [ '$resource', 'UtilService', function($resource, UtilService) {
    var Service = $resource('api/latest/service', {}, {
        _queryNextPossibleQueues: {
            method: 'GET',
            isArray: false,
            url: 'api/latest/plugin/casefile/nextPossibleQueues/:requestId',
            cache: false
        },

        _enqueueNextQueue: {
            method: 'GET',
            isArray: false,
            url: 'api/latest/plugin/casefile/enqueue/:requestId?nextQueue=:queueName&nextQueueAction=:queueAction',
            cache: false
        }
    });

    Service.queryNextPossibleQueues = function(requestId) {
        return UtilService.serviceCall({
            service: Service._queryNextPossibleQueues,
            param: {
                requestId: requestId
            },
            onSuccess: function(data) {
                return data;
            }
        });
    };

    Service.nextQueue = function(requestId, queueName, queueAction) {
        return UtilService.serviceCall({
            service: Service._enqueueNextQueue,
            param: {
                requestId: requestId,
                queueName: queueName,
                queueAction: queueAction
            },
            onSuccess: function(data) {
                return data;
            }
        });
    };

    return Service;
} ]);
