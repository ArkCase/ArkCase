'use strict';

/**
 * @ngdoc service
 * @name services:Object.InfoService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-info.client.service.js services/object/object-info.client.service.js}

 * Object.InfoService includes group of REST calls to retrieve and save object info; Objects can be Case, Complaint, Task, etc.
 */
angular.module('services').factory('Object.InfoService', [ '$resource', 'UtilService', function($resource, Util) {
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
        get : {
            method : 'GET',
            //url: 'api/latest/plugin/casefile/byId/:id',
            url : 'api/latest/plugin/:type/byId/:id',
            cache : false,
            isArray : false
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
        save : {
            method : 'POST',
            url : 'api/latest/plugin/:type',
            cache : false
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
            personType : "Originator"
        });
        return Util.goodMapValue(pa, "person", null);
    };

    return Service;
} ]);
