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
            /**
             * ngdoc method
             * name _getOrganizationTypes
             * methodOf services:Organization.LookupService
             *
             * @description
             * Query list of organization types
             *
             * @returns {Object} Object returned by $resource
             */
            _getOrganizationTypes: {
                url: 'api/latest/plugin/organization/types'
                , cache: true
                , isArray: true
            }
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
            var cacheOrganizationTypes = new Store.SessionData(Service.SessionCacheNames.ORGANIZATION_TYPES);
            var organizationTypes = cacheOrganizationTypes.get();
            return Util.serviceCall({
                service: Service._getOrganizationTypes
                , result: organizationTypes
                , onSuccess: function (data) {
                    if (Service.validateOrganizationTypes(data)) {
                        cacheOrganizationTypes.set(data);
                        return data;
                    }
                }
            });
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
