'use strict';

/**
 * @ngdoc service
 * @name cost-tracking.service:CostTracking.InfoService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cost-tracking/services/cost-tracking-info.client.service.js modules/cost-tracking/services/cost-tracking-info.client.service.js}

 * CostTracking.InfoService provides functions for Costsheet database data
 */
angular.module('services').factory('CostTracking.InfoService', ['$resource', '$translate', 'UtilService',
    function ($resource, $translate, Util) {
        var Service = $resource('proxy/arkcase/api/v1/service/costsheet', {}, {

            /**
             * @ngdoc method
             * @name get
             * @methodOf cost-tracking.service:CostTracking.InfoService
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
                url: 'proxy/arkcase/api/v1/service/costsheet/:id',
                cache: false,
                isArray: false
            },

            /**
             * @ngdoc method
             * @name save
             * @methodOf cost-tracking.service:CostTracking.InfoService
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
                url: 'proxy/arkcase/api/v1/service/costsheet',
                cache: false
            }
        });

        /**
         * @ngdoc method
         * @name getCostTrackingInfo
         * @methodOf cost-tracking.service:CostTracking.InfoService
         *
         * @description
         * Query costsheet data
         *
         * @param {Number} id  Costsheet ID
         *
         * @returns {Object} Promise
         */
        Service.getCostTrackingInfo =  function (id) {
            return Util.serviceCall({
                service: Service.get
                , param: {id: id}
                , onSuccess: function (data) {
                    if (Service.validateCostsheet(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name saveCostsheetInfo
         * @methodOf cost-tracking.service:CostTracking.InfoService
         *
         * @description
         * Save costsheet data
         *
         * @param {Object} costsheetInfo  Costsheet data
         *
         * @returns {Object} Promise
         */
        Service.saveCostsheetInfo = function (costsheetInfo) {
            if (!Service.validateCostsheet(costsheetInfo)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            }
            return Util.serviceCall({
                service: Service.save
                , data: costsheetInfo
                , onSuccess: function (data) {
                    if (Service.validateCostsheet(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateCostsheet
         * @methodOf cost-tracking.service:CostTracking.InfoService
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

        return Service;
    }
]);