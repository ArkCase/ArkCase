'use strict';

/**
 * @ngdoc service
 * @name services:Request.ZylabMatterService
 *
 * @description
 * Contains REST calls for Matter creation from requests
 *
 * The Request.ZylabMatterService provides $http services for Zylab Matter creation from requests.
 */
angular.module('services').factory('Request.ZylabMatterService', ['$http', function ($http) {
    return {

        /**
         * @ngdoc method
         * @name createMatter
         * @methodOf case.service:Request.ZylabMatterService
         *
         * @description
         * Performs creation of a new ZyLAB Matter from request.
         *
         * @param {Object} requestId
         *
         * @returns {Object} http promise
         */
        createMatter: function (requestId) {
            return $http({
                method: 'POST',
                url: 'api/latest/plugin/casefile/' + requestId + '/createZylabMatter'
            });
        }
    };

}]);