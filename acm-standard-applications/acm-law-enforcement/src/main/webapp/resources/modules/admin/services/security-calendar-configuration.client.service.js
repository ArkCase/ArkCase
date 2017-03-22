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
             * @name applyCalendarConfiguration
             * @methodOf admin.service:Admin.CalendarConfigurationService
             *
             * @description
             * Performs applying of the calendar configuration.
             *
             * @param {Object} calendarConfig - the configuration that should be applied
             *
             * @returns {Object} http promise
             */
            applyCalendarConfiguration: function(calendarConfig) {
                return $http({
                    method: 'PUT',
                    url: 'api/v1/service/calendar/configuration',
                    data: calendarConfig
                });
            },
            /**
             * @ngdoc method
             * @name getCalendarConfiguration
             * @methodOf admin.service:Admin.CalendarConfigurationService
             *
             * @description
             * Gets the current calendar configurations.
             *
             *
             * @returns {Object} http promise - the current configuration of the calendar
             */
            getCalendarConfiguration: function() {
                return $http({
                    method: 'GET',
                    url: 'api/v1/service/calendar/configuration'
                });
            },
            /**
             * @ngdoc method
             * @name validateEmail
             * @methodOf admin.service:Admin.CalendarConfigurationService
             *
             * @description
             * Check if the provided system email for the calendar configuration is valid.
             *
             * @param {String} systemEmail - the email that should be validated
             *
             * @returns {Object} http promise
             */
            validateEmail: function(systemEmail) {

            }
        };
    }
]);