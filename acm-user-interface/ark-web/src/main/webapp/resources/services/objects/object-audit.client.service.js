'use strict';

/**
 * @ngdoc service
 * @name services:Object.AuditService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/objects/object-audit.client.service.js services/objects/object-audit.client.service.js}

 * Object.AuditService includes group of audit related REST calls.
 */
angular.module('services').factory('Object.AuditService', ['$resource', 'StoreService', 'UtilService',
    function ($resource, Store, Util) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _queryAudit
             * @methodOf services:Object.AuditService
             *
             * @description
             * Query audit history for an object.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             * @param {Number} params.start Zero based start number of record
             * @param {Number} params.n Max Number of list to return
             * @param {String} params.sort  Sort value, with format 'sortBy sortDir', sortDir can be 'asc' or 'desc'
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _queryAudit: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/audit/:objectType/:objectId?start=:start&n=:n&s=:sort',
                cache: false
            }
        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            AUDIT_DATA: "AuditData"
        };

        /**
         * @ngdoc method
         * @name queryAudit
         * @methodOf services:Object.AuditService
         *
         * @description
         * Query audit history for an object.
         *
         * @param {String} params.objectType  Object type
         * @param {Number} params.objectId  Object ID
         * @param {Number} params.start Zero based start number of record
         * @param {Number} params.n Max Number of list to return
         * @param {String} params.sort  Sort value, with format 'sortBy sortDir', sortDir can be 'asc' or 'desc'
         *
         * @returns {Object} Promise
         */
        Service.queryAudit = function (objectType, objectId, start, n, sortBy, sortDir) {
            var cacheCaseAuditData = new Store.CacheFifo(Service.CacheNames.AUDIT_DATA);
            var cacheKey = objectType + "." + objectId + "." + start + "." + n + "." + sortBy + "." + sortDir;
            var auditData = cacheCaseAuditData.get(cacheKey);

            var sort = "";
            if (!Util.isEmpty(sortBy)) {
                sort = sortBy + " " + Util.goodValue(sortDir, "asc");
            }
            //implement filtering here when service side supports it
            //var filter = "";
            //filters = [{by: "eventDate", with: "term"}];

            return Util.serviceCall({
                service: Service._queryAudit
                , param: {
                    objectType: objectType
                    , objectId: objectId
                    , start: start
                    , n: n
                    , sort: sort
                }
                , result: auditData
                , onSuccess: function (data) {
                    if (Service.validateAuditData(data)) {
                        auditData = data;
                        cacheCaseAuditData.put(cacheKey, auditData);
                        return auditData;

                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateAuditData
         * @methodOf services:Object.AuditService
         *
         * @description
         * Validate audit data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateAuditData = function (data) {
            if (!Util.isArray(data.resultPage)) {
                return false;
            }
            for (var i = 0; i < data.resultPage.length; i++) {
                if (!this.validateEvent(data.resultPage[i])) {
                    return false;
                }
            }
            if (Util.isEmpty(data.totalCount)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateEvent
         * @methodOf services:Object.AuditService
         *
         * @description
         * Validate event data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateEvent = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.eventDate)) {
                return false;
            }
            //if (Util.isEmpty(data.eventType)) {
            //    return false;
            //}
            if (Util.isEmpty(data.objectId)) {
                return false;
            }
            if (Util.isEmpty(data.objectType)) {
                return false;
            }
            if (Util.isEmpty(data.userId)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
