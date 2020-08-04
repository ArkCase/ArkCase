'use strict';

/**
 * @ngdoc service
 * @name request-info.service:RequestInfo.RequestsService
 *
 * @description
 *
 * {@link https://github.com/Armedia/bactes360/blob/develop/bactes-user-interface/src/main/resources/META-INF/resources/resources/modules/request-info/services/requests.client.service.js request-info/services/requests.client.service.js}
 *
 * The RequestsService provides functionality for work with requests.
 */
angular.module('request-info').factory('RequestInfo.RequestsService', [ '$resource', function($resource) {
    return $resource('', {}, {

        /**
         * @ngdoc method
         * @name getRequestInfo
         * @methodOf request-info.service:RequestInfo.RequestsService
         *
         * @param {Object} requestInfo Information about requested request
         * @param {String} requestInfo.requestId Request's identifier
         *
         * @description
         * Retrieves information about request by RequestId
         *
         * @returns {HttpPromise} Future info about request
         */
        getRequestInfo: {
            method: 'GET',
            cache: false,
            url: 'api/v1/plugin/casefile/byId/:requestId'
        },

        /**
         * @ngdoc method
         * @name lockRequest
         * @methodOf request-info.service:RequestInfo.RequestsService
         *
         * @description
         * Perform request lock
         *
         * @param {Object} requestInfo Information about unlocked request
         * @param {String} requestInfo.requestId Request identifier
         * @returns {HttpPromise} Future information about object lock
         */
        lockRequest: {
            method: 'PUT',
            params: {
                requestId: '@requestId'
            },
            url: 'api/v1/plugin/CASE_FILE/:requestId/lock',
            data: ''
        },

        /**
         * @ngdoc method
         * @name releaseRequestLock
         * @methodOf request-info.service:RequestInfo.RequestsService
         *
         * @description
         * Release object lock
         *
         * @param {Object} requestInfo Information about unlocked request
         * @param {String} requestInfo.requestId Request identifier
         * @returns {HttpPromise} Future information about object lock release
         */
        releaseRequestLock: {
            method: 'DELETE',
            url: 'api/v1/plugin/CASE_FILE/:requestId/lock',
            responseType: 'text',
            cache: false,
            transformResponse: function(data, headers) {
                return null;
            }
        },

        getNextAvailableRequestInQueue: {
            method: 'GET',
            params: {
                createdDate: '@createdDate',
            },
            url: 'api/v1/plugin/request/nextAvailableRequestInQueue/:queueId',
            cache: false
        }
    });
} ]);