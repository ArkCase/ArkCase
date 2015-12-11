'use strict';

/**
 * @ngdoc service
 * @name services:Object.CostService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/object/object-cost.client.service.js services/object/object-cost.client.service.js}

 * Object.CostService includes functions for object relate to cost.
 */
angular.module('services').factory('Object.CostService', ['$resource', '$q', 'StoreService', 'UtilService', 'Object.ListService', 'Authentication'
    , function ($resource, $q, Store, Util, ObjectListService, Authentication) {
        var Service = $resource('proxy/arkcase/api/latest/service', {}, {
            /**
             * @ngdoc method
             * @name _queryCostsheets
             * @methodOf services:Object.CostService
             *
             * @description
             * Query cost sheets for an object.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _queryCostsheets: {
                method: 'GET',
                //url: 'proxy/arkcase/api/latest/service/costsheet/objectId/:objectId/objectType/:objectType?start=:start&n=:n&s=:sort',
                url: 'proxy/arkcase/api/latest/service/costsheet/objectId/:objectId/objectType/:objectType',
                cache: false,
                isArray: true
            }
        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            COST_SHEETS: "CostSheets"
        };

        /**
         * @ngdoc method
         * @name queryCostsheets
         * @methodOf services:Object.CostService
         *
         * @description
         * Query cost sheets for an object.
         *
         * @param {String} objectType  Object type
         * @param {Number} objectId  Object ID
         *
         * @returns {Object} Promise
         */
        Service.queryCostsheets = function (objectType, objectId) {
            var cacheCostsheets = new Store.CacheFifo(Service.CacheNames.COST_SHEETS);
            var cacheKey = objectType + "." + objectId;
            var costsheets = cacheCostsheets.get(cacheKey);

            return Util.serviceCall({
                service: Service._queryCostsheets
                , param: {
                    objectType: objectType
                    , objectId: objectId
                }
                , result: costsheets
                , onSuccess: function (data) {
                    if (Service.validateCostsheets(data)) {
                        costsheets = data;
                        cacheCostsheets.put(cacheKey, costsheets);
                        return costsheets;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateCostsheets
         * @methodOf services:Object.CostService
         *
         * @description
         * Validate cost sheets data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateCostsheets = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
