'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.FileUploaderConfigurationService
 *
 * @description
 * Contains REST calls for Admin File Uploader Configuration
 *
 * The Admin.FileUploaderConfigurationService provides $http services for File Upload Configuration.
 */
angular.module('admin').factory('Admin.FileUploaderConfigurationService', [ '$http', function($http) {
    return {
        /**
         * @ngdoc method
         * @name getFileUploaderConfiguration
         * @methodOf admin.service:Admin.FileUploaderConfigurationService
         *
         * @description
         * Gets the current file upload configurations.
         *
         *
         * @returns {Object} http promise
         */
        getFileUploaderConfiguration: function() {
            return $http({
                method: 'GET',
                url: 'api/v1/service/ecm/upload/configure'
            });
        },

        /**
         * @ngdoc method
         * @name saveFileUploaderConfiguration
         * @methodOf admin.service:Admin.FileUploaderConfigurationService
         *
         * @description
         * Performs saving of the file upload configuration.
         *
         * @param {Object} fileUploaderConfig - the configuration that should be saved
         *
         * @returns {Object} http promise
         */
        saveFileUploaderConfiguration: function(fileUploaderConfig) {
            return $http({
                method: 'PUT',
                url: 'api/v1/service/ecm/upload/configure',
                data: fileUploaderConfig
            });
        }
    }
} ]);
