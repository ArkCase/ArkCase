'use strict';

/**
 * @ngdoc service
 * @name services:Organization.LookupService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/organizations/services/organization-lookup.client.service.js modules/organizations/services/organization-lookup.client.service.js}
 *
 * Organization.LookupService provides functions for Organization database data
 */
angular.module('services').factory('Organization.LookupService', ['$resource', '$translate', 'Acm.StoreService', 'UtilService', 'Object.LookupService',
    function ($resource, $translate, Store, Util, ObjectLookupService) {
        var Service = $resource('api/latest/plugin', {}, {
        });

        Service.SessionCacheNames = {
            ORGANIZATION_TYPES: "AcmOrganizationTypes"
        };

        /**
         * @ngdoc method
         * @name getOrganizationTypes
         * @methodOf services:Organization.LookupService
         *
         * @description
         * Query list of organization types
         *
         * @returns {Object} Promise
         */
        Service.getOrganizationTypes = function () {
            return ObjectLookupService.getOrganizationTypes();
        };

        /**
         * @ngdoc method
         * @name validateOrganizationTypes
         * @methodOf services:Organization.LookupService
         *
         * @description
         * Validate Organization type data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateOrganizationTypes = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
