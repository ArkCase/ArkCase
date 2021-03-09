'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.ZylabIntegrationService
 *
 * @description
 * Contains REST calls for Zylab Integration configuration
 *
 * The Admin.ZylabIntegrationService provides $http services for Zylab Integration Configuration.
 */
angular.module('admin').factory('Admin.ZylabIntegrationService', ['$http', function ($http) {
    return {

        /**
         * @ngdoc method
         * @name getConfiguration
         * @methodOf admin.service:Admin.ZylabIntegrationService
         *
         * @description
         * Gets the ZyLAB Integration configuration.
         *
         *
         * @returns {Object} http promise
         */
        getConfiguration: function () {
            return $http({
                method: 'GET',
                url: 'api/latest/plugin/admin/zylab/configuration'
            });
        },

        /**
         * @ngdoc method
         * @name saveConfiguration
         * @methodOf admin.service:Admin.ZylabIntegrationService
         *
         * @description
         * Performs saving of the ZyLAB Integration configuration.
         *
         * @param {Object} data - the configuration that should be saved
         *
         * @returns {Object} http promise
         */
        saveConfiguration: function (data) {
            return $http({
                method: 'PUT',
                url: 'api/latest/plugin/admin/zylab/configuration',
                data: data,
                headers: {
                    "Content-Type": "application/json"
                }
            });
        }
    };

}]);