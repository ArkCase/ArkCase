'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.ApplicationSettingsService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/admin/services/labels.config.client.service.js modules/admin/services/application-settings.client.service.js }
 *
 * The Admin.ApplicationSettingsService provides service to get and manage application settings.
 */
angular.module('admin').factory('Admin.ApplicationSettingsService', [ '$http', function($http) {
    return {
        PROPERTIES: {
            DISPLAY_USERNAME: 'application.properties.displayUserName',
            IDLE_LIMIT: 'application.properties.idleLimit',
            DEFAULT_TIMEZONE: 'application.properties.defaultTimezone',
            IDLE_PULL: 'application.properties.idlePull',
            IDLE_CONFIRM: 'application.properties.idleConfirm',
            HISTORY_DAYS: 'application.properties.historyDays',
            ORGANIZATION_ADDRESS1: 'application.properties.organizationAddress1',
            ORGANIZATION_ADDRESS2: 'application.properties.organizationAddress2',
            ORGANIZATION_CITY: 'application.properties.organizationCity',
            ORGANIZATION_FAX: 'application.properties.organizationFax',
            ORGANIZATION_NAME: 'application.properties.organizationName',
            ORGANIZATION_PHONE: 'application.properties.organizationPhone',
            ORGANIZATION_STATE: 'application.properties.organizationState',
            ORGANIZATION_ZIP: 'application.properties.organizationZip',
            DASHBOARD_BANNER: 'application.properties.dashboardBannerEnabled'

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
