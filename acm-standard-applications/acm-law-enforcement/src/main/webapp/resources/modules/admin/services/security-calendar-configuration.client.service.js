'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.CalendarConfigurationService
 *
 * @description
 * Contains REST calls for Admin Calendar Configuration
 *
 * The Admin.CalendarManagementService provides $http services for Calendar Configuration.
 */
angular.module('admin').factory('Admin.CalendarConfigurationService', ['$http',
    function($http) {
        return {
            /**
             * @ngdoc method
             * @name saveCalendarConfiguration
             * @methodOf admin.service:Admin.CalendarConfigurationService
             *
             * @description
             * Performs saving of the calendar configuration.
             *
             * @param {Object} calendarConfig - the configuration that should be saved
             *
             * @returns {Object} http promise
             */
            saveCalendarConfiguration: function(calendarConfig) {
                return $http({
                    method: 'PUT',
                    url: 'api/v1/service/calendar/configuration',
                    data: calendarConfig
                });
            },
            /**
             * @ngdoc method
             * @name getCurrentCalendarConfiguration
             * @methodOf admin.service:Admin.CalendarConfigurationService
             *
             * @description
             * Gets the current calendar configurations.
             *
             *
             * @returns {Object} http promise
             */
            getCurrentCalendarConfiguration: function() {
                return $http({
                    method: 'GET',
                    url: 'api/v1/service/calendar/configuration'
                });
            },
            /**
             * @ngdoc method
             * @name validateCalendarConfigurationSystemEmail
             * @methodOf admin.service:Admin.CalendarConfigurationService
             *
             * @description
             * Check if the provided system email for the calendar configuration is valid.
             *
             * @param {String} systemEmail - the email that should be validated
             *
             * @returns {Object} http promise
             */
            validateCalendarConfigurationSystemEmail: function(systemEmail) {
                return $http({
                    method: 'GET',
                    url: 'api/v1/service/calendar/configuration/' + systemEmail
                });
            }
        };
    }
]);