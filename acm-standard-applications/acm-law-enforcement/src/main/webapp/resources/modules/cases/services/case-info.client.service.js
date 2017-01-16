'use strict';

/**
 * @ngdoc service
 * @name services:Case.InfoService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/cases/services/case-info.client.service.js modules/cases/services/case-info.client.service.js}
 *
 * Case.InfoService provides functions for Case database data
 */
angular.module('services').factory('Case.InfoService', ['$resource', '$translate', 'Acm.StoreService', 'UtilService', 'Object.InfoService',
    function ($resource, $translate, Store, Util, ObjectInfoService) {
        var Service = $resource('api/latest/plugin', {}, {});

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            CASE_INFO: "CaseInfo"
        };

        /**
         * @ngdoc method
         * @name resetCaseInfo
         * @methodOf services:Case.InfoService
         *
         * @description
         * Reset case info
         *
         * @returns None
         */
        Service.resetCaseInfo = function () {
            var cacheCaseInfo = new Store.CacheFifo(Service.CacheNames.CASE_INFO);
            cacheCaseInfo.reset();
        };

        /**
         * @ngdoc method
         * @name updateCaseInfo
         * @methodOf services:Case.InfoService
         *
         * @description
         * Update case data in local cache. No REST call to backend.
         *
         * @param {Object} caseInfo  Case data
         *
         * @returns {Object} Promise
         */
        Service.updateCaseInfo = function (caseInfo) {
            if (Service.validateCaseInfo(caseInfo)) {
                var cacheCaseInfo = new Store.CacheFifo(Service.CacheNames.CASE_INFO);
                cacheCaseInfo.put(caseInfo.id, caseInfo);
            }
        };

        /**
         * @ngdoc method
         * @name getCaseInfo
         * @methodOf services:Case.InfoService
         *
         * @description
         * Query case data
         *
         * @param {Number} id  Case ID
         *
         * @returns {Object} Promise
         */
        Service.getCaseInfo = function (id) {
            var cacheCaseInfo = new Store.CacheFifo(Service.CacheNames.CASE_INFO);
            var caseInfo = cacheCaseInfo.get(id);
            return Util.serviceCall({
                service: ObjectInfoService.get
                , param: {type: "casefile", id: id}
                , result: caseInfo
                , onSuccess: function (data) {
                    if (Service.validateCaseInfo(data)) {
                        cacheCaseInfo.put(id, data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name saveCaseInfo
         * @methodOf services:Case.InfoService
         *
         * @description
         * Save case data
         *
         * @param {Object} caseInfo  Case data
         *
         * @returns {Object} Promise
         */
        Service.saveCaseInfo = function (caseInfo) {
            if (!Service.validateCaseInfo(caseInfo)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            }
            //we need to make one of the fields is changed in order to be sure that update will be executed
            //if we change modified won't make any differences since is updated before update to database
            //but update will be trigger
            caseInfo.modified = null;
            return Util.serviceCall({
                service: ObjectInfoService.save
                , param: {type: "casefile"}
                , data: caseInfo
                , onSuccess: function (data) {
                    if (Service.validateCaseInfo(data)) {
                        var caseInfo = data;
                        var cacheCaseInfo = new Store.CacheFifo(Service.CacheNames.CASE_INFO);
                        cacheCaseInfo.put(caseInfo.id, caseInfo);
                        return caseInfo;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateCaseInfo
         * @methodOf services:Case.InfoService
         *
         * @description
         * Validate case data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateCaseInfo = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (0 >= Util.goodValue(data.id, 0)) {
                return false;
            }
            if (Util.isEmpty(data.caseNumber)) {
                return false;
            }
            if (!Util.isArray(data.childObjects)) {
                return false;
            }
            if (!Util.isArray(data.milestones)) {
                return false;
            }
            if (!Util.isArray(data.participants)) {
                return false;
            }
            if (!Util.isArray(data.personAssociations)) {
                return false;
            }
            if (!Util.isArray(data.references)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
