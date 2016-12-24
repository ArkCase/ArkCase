'use strict';

/**
 * @ngdoc service
 * @name services:Object.TimeService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-time.client.service.js services/object/object-time.client.service.js}

 * Object.TimeService includes functions for object relate to time.
 */
angular.module('services').factory('Object.TimeService', ['$resource', '$q', 'Acm.StoreService', 'UtilService', 'Object.ListService', 'Authentication'
    , function ($resource, $q, Store, Util, ObjectListService, Authentication) {
        var Service = $resource('api/latest/service', {}, {
            /**
             * @ngdoc method
             * @name _queryTimesheets
             * @methodOf services:Object.TimeService
             *
             * @description
             * Query time sheets for an object.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _queryTimesheets: {
                method: 'GET',
                //url: 'api/latest/service/timesheet/objectId/:objectId/objectType/:objectType?start=:start&n=:n&s=:sort',
                url: 'api/latest/service/timesheet/objectId/:objectId/objectType/:objectType',
                cache: false,
                isArray: true
            }
        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            TIME_SHEETS: "TimeSheets"
        };

        /**
         * @ngdoc method
         * @name queryTimesheets
         * @methodOf services:Object.TimeService
         *
         * @description
         * Query time sheets for an object.
         *
         * @param {String} objectType  Object type
         * @param {Number} objectId  Object ID
         *
         * @returns {Object} Promise
         */
        Service.queryTimesheets = function (objectType, objectId) {
            var cacheTimesheets = new Store.CacheFifo(Service.CacheNames.TIME_SHEETS);
            var cacheKey = objectType + "." + objectId;
            var timesheets = cacheTimesheets.get(cacheKey);

            return Util.serviceCall({
                service: Service._queryTimesheets
                , param: {
                    objectType: objectType
                    , objectId: objectId
                }
                , result: timesheets
                , onSuccess: function (data) {
                    if (Service.validateTimesheets(data)) {
                        timesheets = data;
                        cacheTimesheets.put(cacheKey, timesheets);
                        return timesheets;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateTimesheets
         * @methodOf services:Object.TimeService
         *
         * @description
         * Validate time sheets data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateTimesheets = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
