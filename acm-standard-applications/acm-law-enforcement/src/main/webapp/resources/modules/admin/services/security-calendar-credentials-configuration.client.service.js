'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.CalendarCredentialsConfigurationService
 *
 * @description
 * Contains REST calls for Admin Calendar Credentials Configuration
 *
 * The Admin.CalendarCredentialsService provides $http services for Calendar Credentials Configuration.
 */
angular.module('admin').factory('Admin.CalendarCredentialsConfigurationService', ['$http',
    function($http) {
        return {
            /**
             * @ngdoc method
             * @name findInvalidOutlookFolderCreators
             * @methodOf admin.service:Admin.CalendarCredentialsConfigurationService
             *
             * @description
             * Gets a list of outlook folder creators which have invalid credentials.
             *
             *
             * @returns {Object} http promise
             */
            findInvalidOutlookFolderCreators: function() {
                return $http({
                    method: 'GET',
                    url: 'api/latest/service/calendar/exchange/configure/credentials/invalid'
                });
            },

            /**
             * @ngdoc method
             * @name saveOutlookFolderCreator
             * @methodOf admin.service:Admin.CalendarCredentialsConfigurationService
             *
             * @description
             * Performs saving of the outlook folder creator.
             *
             * @param {Object} creator - the creator that should be saved
             *
             * @returns {Object} http promise
             */
            saveOutlookFolderCreator: function(creator) {
                return $http({
                    method: 'PUT',
                    url: 'api/latest/service/calendar/exchange/configure',
                    data: creator
                });
            }
        };
    }
]);