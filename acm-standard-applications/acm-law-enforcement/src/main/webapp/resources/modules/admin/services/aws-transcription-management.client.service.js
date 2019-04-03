'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.AWSTranscriptionManagementService
 *
 * @description
 * Contains REST calls for Admin AWS provider Configuration
 *
 * The Admin.AWSTranscriptionManagementService provides $http services for AWS provider Configuration.
 */
angular.module('admin').factory('Admin.AWSTranscriptionManagementService', [ '$http', function($http) {
    return {
        /**
         * @ngdoc method
         * @name getAWSTranscribeConfiguration
         * @methodOf admin.service:Admin.AWSTranscriptionManagementService
         *
         * @description
         * Gets the aws provider configuration.
         *
         *
         * @returns {Object} http promise
         */
        getAWSTranscribeConfiguration: function() {
            return $http({
                method: 'GET',
                url: 'api/v1/plugin/admin/transcribe/aws/configuration'
            });
        },
        /**
         * @ngdoc method
         * @name saveAWSTranscribeConfiguration
         * @methodOf admin.service:Admin.AWSTranscriptionManagementService
         *
         * @description
         * Performs saving of the aws provider configuration.
         *
         * @param {Object} AWSConfig - the configuration that should be saved
         *
         * @returns {Object} http promise
         */

        saveAWSTranscribeConfiguration: function(AWSConfig) {
            return $http({
                method: 'POST',
                url: 'api/v1/plugin/admin/transcribe/aws/configuration',
                data: AWSConfig
            })

        }
    };

} ]);