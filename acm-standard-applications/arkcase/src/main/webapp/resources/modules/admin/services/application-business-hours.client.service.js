'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.BusinessHoursService

 * The Admin.BusinessHoursService provides service to get and manage application business hours settings.
 */
angular.module('admin').factory('Admin.BusinessHoursService', ['$http', function ($http) {
    return {
        PROPERTIES: {
            BUSINESS_DAY_HOURS_FLAG: 'businessHours.businessDayHoursEnabled',
            END_OF_BUSINESS_DAY_TIME: 'businessHours.endOfBusinessDayTime',
            START_OF_BUSINESS_DAY_TIME: 'businessHours.startOfBusinessDayTime',
        },

        /**
         * @ngdoc method
         * @name saveBusinessHoursConfig
         * @methodOf admin.service:Admin.BusinessHoursService
         *
         * @description
         * Set property of application business hours settings
         *
         * @param {Object} data of updated application business hours settings value.
         *
         * @returns {Object} updated application business hours settings value
         */
        saveBusinessHoursConfig: function (data) {
            return $http({
                method: 'PUT',
                url: 'api/latest/plugin/admin/businessHours',
                data: data,
                headers: {
                    "Content-Type": "application/json"
                }
            });
        },

        /**
         * @ngdoc method
         * @name getBusinessHoursConfig
         * @methodOf admin.service:Admin.BusinessHoursService
         *
         * @description
         * Get properties of application business hours settings
         *
         * @returns {Object} application business hours settings
         */
        getBusinessHoursConfig: function () {
            return $http({
                method: "GET",
                url: "api/latest/plugin/admin/businessHours"
            })
        }
    }
}]);
