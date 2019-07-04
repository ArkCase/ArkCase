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
angular.module('services').factory('Case.InfoService', [ '$resource', '$translate', 'UtilService', 'CacheFactory', 'ObjectService', '$http', function($resource, $translate, Util, CacheFactory, ObjectService, $http) {

    var caseCache = CacheFactory(ObjectService.ObjectTypes.CASE_FILE, {
        maxAge: 1 * 60 * 1000, // Items added to this cache expire after 1 minute
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive', // Items will be deleted from this cache when they expire
        capacity: 1
    });
    var caseGetUrl = 'api/latest/plugin/casefile/byId/';

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
     * @name saveCaseInfoNewCase
     * @methodOf services:Case.InfoService
     *
     * @description
     * Save case data
     *
     * @param {Object} caseInfo  Case data
     *
     * @returns {Object} Promise
     */
    Service.saveCaseInfoNewCase = function(caseInfo) {
        if (!Service.validateCaseInfoNewCasefile(caseInfo)) {
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
     * @name validateCaseInfoNewCasefile
     * @methodOf services:Case.InfoService
     *
     * @description
     * Validate case data when creating new casefile
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateCaseInfoNewCasefile = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (data.id) {
            return false;
        }
        return true;
    };


    /**
     * @ngdoc method
     * @name saveFoiaRequestInfo
     * @methodOf services:Case.InfoService
     *
     * @description
     * Save FoiaRequest info
     *
     * @param {Object} caseInfo  Case data
     *
     * @returns {Object} Promise
     */
    Service.saveFoiaRequestInfo = function(caseInfo) {
        if (!Service.validateCaseInfo(caseInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        caseInfo.modified = null;
        if(caseInfo.dueDate != null) {
            caseInfo.dueDate = moment(caseInfo.dueDate.replace(/(\d{4})\-(\d{2})\-(\d{2}).*/, '$1/$2/$3'));    
        }
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/foiarequest',
            data: JSOG.encode(caseInfo),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function(result) {
            if (Service.validateCaseInfo(result.data)) {
                var caseInfo = result.data;
                if (caseInfo.id) {
                    caseCache.put(caseGetUrl + caseInfo.id, caseInfo);
                }
                return caseInfo;
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