'use strict';

/**
 * @ngdoc service
 * @name services:Case.InfoService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/cases/services/case-info.client.service.js modules/cases/services/case-info.client.service.js}
 *
 * Case.InfoService provides functions for Case database data
 */
angular.module('services').factory('Case.InfoService', [ '$resource', '$translate', 'UtilService', 'CacheFactory', 'ObjectService', function($resource, $translate, Util, CacheFactory, ObjectService) {

    var caseCache = CacheFactory(ObjectService.ObjectTypes.CASE_FILE, {
        maxAge: 1 * 60 * 1000, // Items added to this cache expire after 1 minute
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive', // Items will be deleted from this cache when they expire
        capacity: 1
    });
    var caseGetUrl = 'api/latest/plugin/casefile/byId/';
    var complaintGetByNumberUrl = 'api/latest/plugin/casefile/bynumber';

    var Service = $resource('api/latest/plugin', {}, {
        /**
         * @ngdoc method
         * @name save
         * @methodOf services:Case.InfoService
         *
         * @description
         * Save case data
         *
         * @param {Object} params Map of input parameter.
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Case returned by $resource
         */
        save: {
            method: 'POST',
            url: 'api/latest/plugin/casefile'
        },

        /**
         * @ngdoc method
         * @name get
         * @methodOf services:Case.InfoService
         *
         * @description
         * Query case data from database.
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Object ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        get: {
            method: 'GET',
            url: caseGetUrl + ':id',
            cache: caseCache,
            isArray: false
        },

        /**
         * @ngdoc method
         * @name get
         * @methodOf services:Case.InfoService
         *
         * @description
         * Query case data from database.
         *
         * @param {String} caseNumber  caseNumber
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        getByNumber: {
            method: 'GET',
            url: complaintGetByNumberUrl,
            cache: false,
            isArray: false
        }
    });

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
    Service.resetCaseInfo = function(caseInfo) {
        if (caseInfo && caseInfo.id) {
            caseCache.remove(caseGetUrl + caseInfo.id);
        }
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
    Service.updateCaseInfo = function(caseInfo) {
        //TODO remove this method
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
    Service.getCaseInfo = function(id) {
        return Util.serviceCall({
            service: Service.get,
            param: {
                id: id
            },
            onSuccess: function(data) {
                if (Service.validateCaseInfo(data)) {
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name getCaseInfoByNumber
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Query complaint data by number
     *
     * @param {String} caseNumber caseNumber
     *
     * @returns {Object} Promise
     */
    Service.getCaseInfoByNumber = function(caseNumber) {
        return Util.serviceCall({
            service: Service.getByNumber,
            param: {
                caseNumber: caseNumber
            },
            onSuccess: function(data) {
                if (Service.validateCaseInfo(data)) {
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
    Service.saveCaseInfo = function(caseInfo) {
        if (!Service.validateCaseInfo(caseInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        //we need to make one of the fields is changed in order to be sure that update will be executed
        //if we change modified won't make any differences since is updated before update to database
        //but update will be trigger
        caseInfo.modified = null;
        return Util.serviceCall({
            service: Service.save,
            data: JSOG.encode(caseInfo),
            onSuccess: function(data) {
                if (Service.validateCaseInfo(data)) {
                    var caseInfo = data;
                    if (caseInfo.id) {
                        caseCache.put(caseGetUrl + caseInfo.id, caseInfo);
                    }
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
    Service.validateCaseInfo = function(data) {
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
} ]);