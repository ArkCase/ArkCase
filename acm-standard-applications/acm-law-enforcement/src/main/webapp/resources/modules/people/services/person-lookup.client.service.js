'use strict';

/**
 * @ngdoc service
 * @name services:Person.LookupService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/people/services/person-lookup.client.service.js modules/people/services/person-lookup.client.service.js}
 *
 * Person.LookupService provides functions for Person database data
 */
angular.module('services').factory('Person.LookupService', ['$resource', '$translate', 'Acm.StoreService', 'UtilService', 'Object.LookupService',
    function ($resource, $translate, Store, Util, ObjectLookupService) {
        var Service = $resource('api/latest/plugin', {}, {
        });

        Service.SessionCacheNames = {
            PERSON_TYPES: "AcmPersonTypes"
        };

        /**
         * @ngdoc method
         * @name getPersonTypes
         * @methodOf services:People.LookupService
         *
         * @description
         * Query list of Person types
         *
         * @returns {Object} Promise
         */
        Service.getPersonTypes = function () {
            return ObjectLookupService.getPersonTypes();
        };

        /**
         * @ngdoc method
         * @name validatePersonTypes
         * @methodOf services:Person.LookupService
         *
         * @description
         * Validate person type data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validatePersonTypes = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
