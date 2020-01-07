'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.ApplicationSettingsService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/labels.config.client.service.js modules/admin/services/application-settings.client.service.js }
 *
 * The Admin.ApplicationSettingsService provides service to get and manage application settings.
 */
angular.module('admin').factory('Admin.ApplicationSettingsService', [ '$http', function($http) {
    return {
        PROPERTIES: {
            DISPLAY_USERNAME: 'application.properties.displayUserName',
            IDLE_LIMIT: 'application.properties.idleLimit',
            IDLE_PULL: 'application.properties.idlePull',
            IDLE_CONFIRM: 'application.properties.idleConfirm',
            HISTORY_DAYS: 'application.properties.historyDays'
        },

        /**
         * @ngdoc method
         * @name setPropertyConfiguration
         * @methodOf admin.service:Admin.ApplicationSettingsService
         *
         * @description
         * Set property of application settings
         *
         * @param {Object} Object of input parameter.
         *
         * @returns {Object} updated Application settings value
         */
        saveApplicationPropertyConfig: function (data) {
            return $http({
                method: 'PUT',
                url: 'api/latest/plugin/admin/app-properties',
                data: data,
                headers: {
                    "Content-Type": "application/json"
                }
            });
        },

        /**
         * @ngdoc method
         * @name getApplicationPropertiesConfig
         * @methodOf admin.service:Admin.ApplicationSettingsService
         *
         * @description
         * Get properties of application settings
         *
         * @returns {Object} updated Application settings value
         */
        getApplicationPropertiesConfig: function () {
            return $http({
                method: "GET",
                url: "api/latest/plugin/admin/app-properties"
            })
        }
    }
} ]);
