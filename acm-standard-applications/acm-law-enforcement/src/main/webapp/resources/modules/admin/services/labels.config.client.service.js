'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.LabelsConfigService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/labels.config.client.service.js modules/admin/services/labels.config.client.service.js }
 *
 * The AdAdmin.LabelsConfigService provides Application Labels Config REST calls functionality
 */
angular.module('admin').factory('Admin.LabelsConfigService', ['$resource',
    function ($resource) {
        return $resource('api/latest/plugin/admin/labelmanagement', {}, {

            /**
             * @ngdoc method
             * @name retrieveNamespaces
             * @methodOf admin.service:Admin.LabelsConfigService
             *
             * @description
             * Performs retrieving all namespaces in label configuration for dropdown list
             *
             * @param {Object} params Map of input parameter.
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            retrieveNamespaces: {
                method: "GET",
                url: "api/latest/plugin/admin/labelmanagement/namespaces",
                cache: false,
                isArray: true
            },

            /**
             * @ngdoc method
             * @name retrieveResource
             * @methodOf admin.service:Admin.LabelsConfigService
             *
             * @description
             * Performs retrieving all resources in label configuration for populating grid depending of selected namespace and language
             *
             * @param {Object} params Map of input parameter.
             * @param {String} params.lang String that contains value for selected namespace from dropdown
             * @param {String} params.ns String that contains value for selected language from dropdown
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            retrieveResource: {
                method: "GET",
                url: "api/latest/plugin/admin/labelmanagement/admin-resource?lang=:lang&ns=:ns",
                cache: false,
                isArray: true
            },

            /**
             * @ngdoc method
             * @name retrieveSettings
             * @methodOf admin.service:Admin.LabelsConfigService
             *
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @description
             * Performs retrieving labels settings
             *
             * @returns {Object} Object returned by $resource
             */
            retrieveSettings: {
                method: "GET",
                url: "api/latest/plugin/admin/labelmanagement/settings",
                cache: false,
                isArray: false
            },

            /**
             * @ngdoc method
             * @name updateResource
             * @methodOf admin.service:Admin.LabelsConfigService
             *
             * @description
             * Updating value for selected resource from grid table depending of selected namespace and language
             *
             * @param {Object} params Map of input parameter.
             * @param {String} params.lang String that contains value for selected namespace from dropdown
             * @param {String} params.ns String that contains value for selected language from dropdown
             * @param {String} params.id String that contains value for id for selected record from grid
             * @param {String} params.value String that contains value for value for selected record from grid
             * @param {String} params.description String that contains value for description for selected record from grid
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            updateResource: {
                method: "PUT",
                url: "api/latest/plugin/admin/labelmanagement/admin-resource?lang=:lang&ns=:ns",
                cache: false,
                isArray: true
            },

            /**
             * @ngdoc method
             * @name updateSettings
             * @methodOf admin.service:Admin.LabelsConfigService
             *
             * @description
             * Updating value for default language taken from dropdown list
             *
             * @param {Object} params Map of input parameter.
             * @param {Object} params.settings Object that contains default value for language
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            updateSettings: {
                method: "PUT",
                url: "api/latest/plugin/admin/labelmanagement/settings",
                cache: false
            },

            /**
             * @ngdoc method
             * @name resetResource
             * @methodOf admin.service:Admin.LabelsConfigService
             *
             * @description
             * Reseting values for grid columns to default values
             *
             * @param {Object} params Map of input parameter.
             * @param {Object} params.lng Object that contains value for selected language from dropdown
             * @param {Object} params.ns Object that contains value for selected namespace from dropdown
             * @param {Boolean} params.useBaseLang If true then use base language for absent resources
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            resetResource: {
                method: "POST",
                url: "api/latest/plugin/admin/labelmanagement/admin-resource/reset",
                cache: false
            },

            /**
             * @ngdoc method
             * @name refreshResource
             * @methodOf admin.service:Admin.LabelsConfigService
             *
             * @description
             * Refresh resources without cusotm resources removing
             *
             * @param {Object} params Map of input parameter.
             * @param {Object} params.lng Object that contains value for selected language from dropdown
             * @param {Object} params.ns Object that contains value for selected namespace from dropdown
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            refreshResource: {
                method: "POST",
                url: "api/latest/plugin/admin/labelmanagement/admin-resource/refresh",
                cache: false
            }
        });
    }
]);