'use strict';

/**
 * @ngdoc service
 * @name service:CostTracking.InfoService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/cost-tracking/services/cost-tracking-info.client.service.js modules/cost-tracking/services/cost-tracking-info.client.service.js}

 * CostTracking.InfoService provides functions for Costsheet database data
 */
angular.module('services').factory('CostTracking.InfoService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', function($resource, $translate, Store, Util) {
    var Service = $resource('api/v1/service/costsheet', {}, {

        /**
         * @ngdoc method
         * @name get
         * @methodOf service:CostTracking.InfoService
         *
         * @description
         * Query costsheet data by given id
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Costsheet ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        get: {
            method: 'GET',
            url: 'api/v1/service/costsheet/:id',
            cache: false,
            isArray: false
        },

        /**
         * @ngdoc method
         * @name save
         * @methodOf service:CostTracking.InfoService
         *
         * @description
         * Save costsheet data
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Costsheet ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        save: {
            method: 'POST',
            url: 'api/v1/service/costsheet/:submissionName',
            cache: false
        }
    });

    Service.CacheNames = {
        COSTSHEET_INFO: "CostsheetInfo"
    };

    /**
     * @ngdoc method
     * @name resetCaseInfo
     * @methodOf services:CostTracking.InfoService
     *
     * @description
     * Reset CostTracking info
     *
     * @returns None
     */
    Service.resetCostsheetInfo = function() {
        var cacheInfo = new Store.CacheFifo(Service.CacheNames.COSTSHEET_INFO);
        cacheInfo.reset();
    };

    /**
     * @ngdoc method
     * @name getCostsheetInfo
     * @methodOf service:CostTracking.InfoService
     *
     * @description
     * Query costsheet data
     *
     * @param {Number} id  Costsheet ID
     *
     * @returns {Object} Promise
     */
    Service.getCostsheetInfo = function(id) {
        var cacheCostsheetInfo = new Store.CacheFifo(Service.CacheNames.COSTSHEET_INFO);
        var costsheetInfo = cacheCostsheetInfo.get(id);
        return Util.serviceCall({
            service: Service.get,
            param: {
                id: id
            },
            result: costsheetInfo,
            onSuccess: function(data) {
                if (Service.validateCostsheet(data)) {
                    cacheCostsheetInfo.put(id, data);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name saveCostsheetInfo
     * @methodOf service:CostTracking.InfoService
     *
     * @description
     * Save costsheet data
     *
     * @param {Object} costsheetInfo  Costsheet data
     *
     * @returns {Object} Promise
     */
    Service.saveCostsheetInfo = function(costsheetInfo, submissionName) {
        if (!Service.validateCostsheet(costsheetInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        return Util.serviceCall({
            service: Service.save,
            param: {
                submissionName: submissionName
            },
            data: costsheetInfo,
            onSuccess: function(data) {
                if (Service.validateCostsheet(data)) {
                    var costsheetInfo = data;
                    var cacheCostsheetInfo = new Store.CacheFifo(Service.CacheNames.COSTSHEET_INFO);
                    cacheCostsheetInfo.put(costsheetInfo.id, costsheetInfo);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name saveCostsheetInfo
     * @methodOf service:CostTracking.InfoService
     *
     * @description
     * Save costsheet data
     *
     * @param {Object} costsheetInfo  Costsheet data
     *
     * @returns {Object} Promise
     */
    Service.saveNewCostsheetInfo = function(costsheetInfo, submissionName) {
        if (!Service.validateNewCostsheet(costsheetInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        return Util.serviceCall({
            service: Service.save,
            param: {
                submissionName: submissionName
            },
            data: costsheetInfo,
            onSuccess: function(data) {
                if (Service.validateCostsheet(data)) {
                    var costsheetInfo = data;
                    var cacheCostsheetInfo = new Store.CacheFifo(Service.CacheNames.COSTSHEET_INFO);
                    cacheCostsheetInfo.put(costsheetInfo.id, costsheetInfo);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateCostsheet
     * @methodOf service:CostTracking.InfoService
     *
     * @description
     * Validate costsheet
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateCostsheet = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (Util.isEmpty(data.id)) {
            return false;
        }
        if (Util.isEmpty(data.user)) {
            return false;
        }
        if (Util.isEmpty(data.user.userId)) {
            return false;
        }
        if (Util.isEmpty(data.parentId)) {
            return false;
        }
        if (Util.isEmpty(data.parentType)) {
            return false;
        }
        if (Util.isEmpty(data.parentNumber)) {
            return false;
        }
        if (Util.isEmpty(data.costs)) {
            return false;
        }
        if (Util.isEmpty(data.status)) {
            return false;
        }
        if (Util.isEmpty(data.creator)) {
            return false;
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name validateCostsheet
     * @methodOf service:CostTracking.InfoService
     *
     * @description
     * Validate costsheet
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateNewCostsheet = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (data.id) {
            return false;
        }
        if (Util.isEmpty(data.user)) {
            return false;
        }
        if (Util.isEmpty(data.user.userId)) {
            return false;
        }
        if (Util.isEmpty(data.parentId)) {
            return false;
        }
        if (Util.isEmpty(data.parentType)) {
            return false;
        }
        if (Util.isEmpty(data.parentNumber)) {
            return false;
        }
        if (Util.isEmpty(data.costs)) {
            return false;
        }
        if (Util.isEmpty(data.status)) {
            return false;
        }
        return true;
    };

    return Service;
} ]);