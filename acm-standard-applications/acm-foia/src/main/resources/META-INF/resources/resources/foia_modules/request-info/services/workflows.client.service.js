'use strict';

angular.module('request-info').factory('RequestInfo.WorkflowsService', [ '$http', function($http) {

    return {
        accept: function(options) {
            return $http({
                url: 'api/v1/plugin/request/workflow/accept/' + options.requestId,
                method: 'POST',
                data: ''
            });
        },

        reject: function(options) {
            return $http({
                url: 'api/v1/plugin/request/workflow/reject/' + options.requestId,
                method: 'POST',
                data: ''
            });
        },

        pendingResolutionResults: function(options) {
            return $http({
                url: 'api/v1/plugin/request/workflow/pendingResolutionResults/' + options.requestId + '/' + options.result,
                method: 'POST',
                data: ''
            });
        },

        save: function(requestInfo) {
            return $http.post('api/v1/plugin/casefile', requestInfo);
        },

        getSubscribers: function(options) {
            return $http({
                url: 'api/v1/service/subscription/' + options.userId + '/CASE_FILE/' + options.requestId,
                method: 'GET',
                data: ''
            })
        },

        subscribe: function(options) {
            return $http({
                url: 'api/v1/service/subscription/' + options.userId + '/CASE_FILE/' + options.requestId,
                method: 'PUT',
                data: ''
            })
        },

        unsubscribe: function(options) {
            return $http({
                url: 'api/v1/service/subscription/' + options.userId + '/CASE_FILE/' + options.requestId,
                method: 'DELETE',
                data: ''
            })
        }
    }
} ]);