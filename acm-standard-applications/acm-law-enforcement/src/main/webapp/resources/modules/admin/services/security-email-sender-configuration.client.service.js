'use strict';
/**
 * @ngdoc service
 * @name admin.service:Admin.EmailSenderConfigurationService
 *
 * @description
 * Contains REST calls for Admin Email Sender Configuration
 *
 * The EmailSenderConfigurationService provides $http services for Email Sender Configuration.
 */
angular.module('admin').factory('Admin.EmailSenderConfigurationService', ['$http',
    function($http) {
        return {
            /**
             * @ngdoc method
             * @name saveEmailSenderConfiguration
             * @methodOf admin.service:Admin.EmailSenderConfigurationService
             *
             * @description
             * Performs saving of the email sender configuration.
             *
             * @param {Object} emailConfig - the configuration that should be saved
             *
             * @returns {Object} http promise
             */
            saveEmailSenderConfiguration: function(emailConfig) {
                return $http({
                    method: 'PUT',
                    url: 'api/latest/plugin/admin/email/configuration',
                    data: emailConfig
                });
            },
            /**
             * @ngdoc method
             * @name getEmailSenderConfiguration
             * @methodOf admin.service:Admin.EmailSenderConfigurationService
             *
             * @description
             * Gets the current email sender configuration.
             *
             *
             * @returns {Object} http promise
             */
            getEmailSenderConfiguration: function() {
                return $http({
                    method: 'GET',
                    url: 'api/latest/plugin/admin/email/configuration'
                });
            },
            /**
             * @ngdoc method
             * @name validateEmailSenderConfiguration
             * @methodOf admin.service:Admin.EmailSenderConfigurationService
             *
             * @description
             * Check if the provided email sender configuration is valid.
             *
             * @param {String}  emailConfiguration - the email sender configuration that should be validated
             *
             * @returns {Object} http promise
             */
            validateEmailSenderConfiguration: function(emailConfiguration) {
                return $http({
                    method: 'GET',
                    url: 'api/latest/plugin/admin/email/configuration/' + emailConfiguration
                });
            }
        };
    }
]);
