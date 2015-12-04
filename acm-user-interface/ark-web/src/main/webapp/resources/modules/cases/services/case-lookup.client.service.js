'use strict';

/**
 * @ngdoc service
 * @name services:Case.LookupService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cases/services/case-lookup.client.service.js modules/cases/services/case-lookup.client.service.js}
 *
 * Case.LookupService provides functions for Case lookup data
 */
angular.module('services').factory('Case.LookupService', ['$resource', '$translate', 'StoreService', 'UtilService', 'Object.ModelService',
    function ($resource, $translate, Store, Util, ObjectModelService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * ngdoc method
             * name _getCaseTypes
             * methodOf services:Case.LookupService
             *
             * @description
             * Query list of case types
             *
             * @returns {Object} Object returned by $resource
             */
            _getCaseTypes: {
                url: 'proxy/arkcase/api/latest/plugin/casefile/caseTypes'
                , cache: true
                , isArray: true
            }
            /**
             * ngdoc method
             * name _getApprovers
             * methodOf services:Case.LookupService
             *
             * @description
             * Query list of case approvers
             *
             * @returns {Object} Object returned by $resource
             */
            , _getApprovers: {
                url: 'proxy/arkcase/api/latest/service/functionalaccess/users/acm-case-approve/:group/:assignee'
                , cache: true
                , isArray: true
            }
        });

        Service.SessionCacheNames = {
            CASE_TYPES: "AcmCaseTypes"
        };
        Service.CacheNames = {
            CASE_APPROVERS: "CaseApprovers"
        };

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
            var cacheCaseTypes = new Store.SessionData(Service.SessionCacheNames.CASE_TYPES);
            var caseTypes = cacheCaseTypes.get();
            return Util.serviceCall({
                service: Service._getCaseTypes
                , result: caseTypes
                , onSuccess: function (data) {
                    if (Service.validateCaseTypes(data)) {
                        cacheCaseTypes.set(data);
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

        /**
         * @ngdoc method
         * @name getApprovers
         * @methodOf services:Case.LookupService
         *
         * @description
         * Query list of case types
         *
         * @param {String} group  Group ID
         * @param {String} assignee  Assignee
         *
         * @returns {Object} Promise
         */
        Service.getApprovers = function (group, assignee) {
            var cacheApprovers = new Store.CacheFifo(Service.CacheNames.CASE_APPROVERS);
            var cacheKey = group + "." + assignee;
            var approvers = cacheApprovers.get(cacheKey);
            var param = {};
            if (!Util.isEmpty(group)) {
                param.group = group;
            }
            if (!Util.isEmpty(assignee)) {
                param.assignee = assignee;
            }

            return Util.serviceCall({
                service: Service._getApprovers
                , param: param
                , result: approvers
                , onSuccess: function (data) {
                    if (Service.validateApprovers(data)) {
                        approvers = data;
                        cacheApprovers.put(cacheKey, approvers);
                        return approvers;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateApprovers
         * @methodOf services:Case.LookupService
         *
         * @description
         * Validate case approver data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateApprovers = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
