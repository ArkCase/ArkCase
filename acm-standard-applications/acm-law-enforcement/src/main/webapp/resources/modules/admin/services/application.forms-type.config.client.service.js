'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.ApplicationFormsTypeConfigService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/application.forms-type.config.client.service.js }
 *
 * The Admin.ApplicationFormsTypeConfigService provides service to get and manage forms type settings.
 */
angular.module('admin').factory('Admin.ApplicationFormsTypeConfigService', [ '$http', function($http) {
    return {
        PROPERTIES: {
            FORMS_TYPE: 'formsType'
        },

        /**
         * @ngdoc method
         * @name getSettings
         *
         * @description
         * Performs retrieving all application settings
         *
         *
         * @returns {Object} Application settings structure
         */
        getSettings: function() {
            return $http({
                method: 'GET',
                url: 'api/latest/service/forms/type'
            });
        },

        /**
         * @ngdoc method
         * @name getProperty
         *
         * @description
         * Get property of application settings
         *
         * @param {Object} propertyName Name of application property.
         *
         * @returns {String} Application settings value
         */
        getProperty: function(propertyName) {
            return $http({
                method: 'GET',
                url: 'api/latest/service/forms/type/' + propertyName
            });
        },

        /**
         * @ngdoc method
         * @name setProperty
         *
         * @description
         * Set property of application settings
         *
         * @param {Object} params Map of input parameter.
         *
         * @returns {String} updated Application settings value
         */
        setProperty: function(propertyName, propertyValue) {
            var data = {};
            data[propertyName] = propertyValue;

            return $http({
                method: 'PUT',
                url: 'api/latest/service/forms/type',
                data: data
            });
        }
    }
} ]);
