'use strict';

/**
 * @deprecated case, complaint and task are not using this service anymore because of adding angular cache they must have own cache.
 * 
 * @ngdoc service
 * @name services:Object.InfoService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/services/object/object-info.client.service.js services/object/object-info.client.service.js}

 * Object.InfoService includes group of REST calls to retrieve and save object info; Objects can be Case, Complaint, Task, etc.
 */
angular.module('services').factory('Object.InfoService', [ '$resource', 'UtilService', 'CacheFactory', function($resource, Util, CacheFactory) {

    var objectCache = CacheFactory('objectCache', {
        maxAge: 1 * 60 * 1000, // Items added to this cache expire after 1 minute
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive', // Items will be deleted from this cache when they expire
        capacity: 1
    });

    var Service = $resource('api/latest/plugin', {}, {
        /**
         * @ngdoc method
         * @name get
         * @methodOf services:Object.InfoService
         *
         * @description
         * Query object data from database.
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.type  Type in REST path. Can be 'casefile', 'complaint', 'task', etc.
         * @param {Number} params.id  Object ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        get: {
            method: 'GET',
            //url: 'api/latest/plugin/casefile/byId/:id',
            url: 'api/latest/plugin/:type/byId/:id',
            cache: objectCache,
            isArray: false
        },

        /**
         * @ngdoc method
         * @name getCostOrTimeSheet
         * @methodOf services:Object.InfoService
         *
         * @description
         * Query object data from database.
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.type  Type in REST path. Can be 'costsheet' or 'timesheet'
         * @param {Number} params.id  Object ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */

        getCostOrTimeSheet: {
            method: 'GET',
            url: 'api/v1/service/:type/:id',
            cache: objectCache,
            isArray: false
        }

        /**
         * @ngdoc method
         * @name save
         * @methodOf services:Object.InfoService
         *
         * @description
         * Save object data to database.
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.type  Type in REST path. Can be 'casefile', 'complaint', 'task', etc.
         * @param {Object} data Object data
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        ,
        save: {
            method: 'POST',
            url: 'api/latest/plugin/:type',
            cache: false
        }

    });

    /**
     * @ngdoc method
     * @name getOriginator
     * @methodOf services:Object.InfoService
     *
     * @description
     * Detract originator from objectInfo
     *
     * @param {Object} params Map of input parameter.
     * @param {Number} params.type  Type in REST path. Can be 'casefile', 'complaint', 'task', etc.
     * @param {Object} data Object data
     * @param {Function} onSuccess (Optional)Callback function of success query.
     * @param {Function} onError (Optional) Callback function when fail.
     *
     * @returns {Object} Object returned by $resource
     */
    Service.getOriginator = function(objectInfo) {
        var pa = _.find(Util.goodMapValue(objectInfo, "personAssociations", []), {
            personType: "Originator"
        });
        return Util.goodMapValue(pa, "person", null);
    };

    /**
     * @ngdoc method
     * @name getObjectInfo
     * @methodOf services:Object.InfoService
     *
     * @description
     * Query association data
     *
     * @param {Number} type  Type in REST path. Can be 'casefile', 'complaint', 'task', etc.
     * @param {Number} id  Object ID
     *
     * @returns {Object} Object returned by $resource
     */
    Service.getObjectInfo = function(type, id) {
        return Util.serviceCall({
            service: Service.get,
            param: {
                type: type,
                id: id
            },
            onSuccess: function(data) {
                return data;
            }
        });
    };

    /**
     * @ngdoc method
     * @name getCostOrTimeSheetObjectInfo
     * @methodOf services:Object.InfoService
     *
     * @description
     * Query association data
     *
     * @param {Number} type  Type in REST path. Can be 'costsheet' or 'timesheet'
     * @param {Number} id  Object ID
     *
     * @returns {Object} Object returned by $resource
     */

    Service.getCostOrTimeSheetObjectInfo = function(type, id) {
        return Util.serviceCall({
            service: Service.getCostOrTimeSheet,
            param: {
                type: type,
                id: id
            },
            onSuccess: function (data) {
                return data;
            }
        });
    };;

    return Service;
} ]);