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
angular.module('admin').factory('Admin.ApplicationSettingsService', ['$http',
    function ($http) {
        return {
            PROPERTIES : {
                DISPLAY_USERNAME: 'displayUserName',
                IDLE_LIMIT: 'idleLimit',
                IDLE_PULL: 'idlePull',
                IDLE_CONFIRM: 'idleConfirm',
                HISTORY_DAYS: 'historyDays'
            },


            /**
             * @ngdoc method
             * @name getSettings
             * @methodOf admin.service:Admin.ApplicationSettingsService
             *
             * @description
             * Performs retrieving all application settings
             *
             *
             * @returns {Object} Application seting structure
             */
            getSettings: function () {
                return $http({
                    method: 'GET',
                    url: 'api/latest/plugin/admin/app-properties'
                });
            },

            /**
             * @ngdoc method
             * @name getProperty
             * @methodOf admin.service:Admin.ApplicationSettingsService
             *
             * @description
             * Get property of application settings
             *
             * @param {Object} propertyName Name of application property.
             *
             * @returns {String} Application settings value
             */
            getProperty: function (propertyName) {
                return $http({
                    method: 'GET',
                    url: 'api/latest/plugin/admin/app-properties/' + propertyName
                });
            },

            /**
             * @ngdoc method
             * @name setProperty
             * @methodOf admin.service:Admin.ApplicationSettingsService
             *
             * @description
             * Set property of application settings
             *
             * @param {Object} params Map of input parameter.
             *
             * @returns {String} updated Application settings value
             */
            setProperty: function (propertyName, propertyValue) {
                var data = {};
                data[propertyName] = propertyValue;

                return $http({
                    method: 'PUT',
                    url: 'api/latest/plugin/admin/app-properties',
                    data: data
                });
            }
        }
    }
]);
