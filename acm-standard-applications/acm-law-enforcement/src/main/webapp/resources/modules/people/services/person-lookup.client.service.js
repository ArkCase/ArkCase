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
            /**
             * ngdoc method
             * name _getPersonTypes
             * methodOf services:Person.LookupService
             *
             * @description
             * Query list of person types
             *
             * @returns {Object} Object returned by $resource
             */
            _getPersonTypes: {
                url: 'api/latest/plugin/person/types'
                , cache: true
                , isArray: true
            }
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
            var cachePersonTypes = new Store.SessionData(Service.SessionCacheNames.PERSON_TYPES);
            var personTypes = cachePersonTypes.get();
            return Util.serviceCall({
                service: Service._getPersonTypes
                , result: personTypes
                , onSuccess: function (data) {
                    if (Service.validatePersonTypes(data)) {
                        cachePersonTypes.set(data);
                        return data;
                    }
                }
            });
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
