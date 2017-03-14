'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.CalendarManagementService
 *
 * @description
 * Contains REST calls for Admin Calendar Management
 *
 * The Admin.CalendarManagementService provides $http services for Calendar Management.
 */
angular.module('admin').factory('Admin.CalendarManagementService', ['$http',
    function($http) {
        return {
            /**
             * @ngdoc method
             * @name applyCalendarConfiguration
             * @methodOf admin.service:Admin.CalendarManagementService
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
                    url: 'api/v1/plugin/outlook/calendar/admin',
                    data: calendarConfig
                });
            }
        };
    }
]);