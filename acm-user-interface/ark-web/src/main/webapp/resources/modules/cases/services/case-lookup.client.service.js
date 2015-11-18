'use strict';

/**
 * @ngdoc service
 * @name services:Case.LookupService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cases/services/case-lookup.client.service.js modules/cases/services/case-lookup.client.service.js}
 *
 * Case.LookupService provides functions for Case database data
 */
angular.module('services').factory('Case.LookupService', ['$resource', '$translate', 'StoreService', 'UtilService', 'Object.LookupService',
    function ($resource, $translate, Store, Util, ObjectLookupService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * ngdoc method
             * name _getCaseTypes
             * methodOf services:Case.LookupService
             *
             * @description
             * Query case data
             *
             * @returns {Object} Object returned by $resource
             */
            _getCaseTypes: {
                url: 'proxy/arkcase/api/latest/plugin/casefile/caseTypes'
                , cache: true
                , isArray: true
            }
        });

        Service.SessionCacheNames = {
            CASE_TYPES: "AcmCaseTypes"
        };
        Service.CacheNames = {};

        /**
         * @ngdoc method
         * @name getCaseTypes
         * @methodOf services:Case.LookupService
         *
         * @description
         * Query list of case types
         *
         * @returns {Object} Promise
         */
        Service.getCaseTypes = function () {
            var cacheCaseTypes = new Store.CacheFifo(Service.CacheNames.CASE_TYPES);
            var caseTypes = cacheCaseTypes.get();
            return Util.serviceCall({
                service: Service._getCaseTypes
                , result: caseTypes
                , onSuccess: function (data) {
                    if (Service.validateCaseTypes(data)) {
                        cacheCaseTypes.put(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateCaseTypes
         * @methodOf services:Case.LookupService
         *
         * @description
         * Validate case type data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateCaseTypes = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
