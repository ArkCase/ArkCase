'use strict';

/**
 * @ngdoc service
 * @name services:Object.InfoService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/object/object-info.client.service.js services/object/object-info.client.service.js}

 * Object.InfoService includes group of REST calls to retrieve and save object info; Objects can be Case, Complaint, Task, etc.
 */
angular.module('services').factory('Object.InfoService', ['$resource', 'UtilService',
    function ($resource, Util) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
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
                //url: 'proxy/arkcase/api/latest/plugin/casefile/byId/:id',
                url: 'proxy/arkcase/api/latest/plugin/:type/byId/:id',
                cache: false,
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
            , save: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/:type',
                cache: false
            }

        });


        return Service;
    }
]);
