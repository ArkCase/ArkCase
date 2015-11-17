'use strict';

/**
 * @ngdoc service
 * @name services:Object.ListService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/objects/object-list.client.service.js services/objects/object-list.client.service.js}

 * Object.ListService includes REST calls related to object list in SOLR
 */
angular.module('services').factory('Object.ListService', ['$resource', 'UtilService',
    function ($resource, Util) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _queryObjects
             * @methodOf services:Object.ListService
             *
             * @description
             * Query list of objects from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.objectType Object type. 'CASE_FILE', 'COMPLAINT', 'TASK', etc.
             * @param {Number} params.start  Zero based index of result starts from
             * @param {Number} params.n max Number of list to return
             * @param {String} params.sort  Sort value. Allowed choice is based on backend specification
             * @param {String} params.filters  Filter value. Allowed choice is based on backend specification
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _queryObjects: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/search/:objectType?start=:start&n=:n&sort=:sort&filters=:filters',
                cache: false,
                isArray: false
            }

        });


        /**
         * @ngdoc method
         * @name validateSolrData
         * @methodOf services:Object.ListService
         *
         * @description
         * Validate data of query from SOLR
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateSolrData = function (data) {
            if (!data) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader) || Util.isEmpty(data.response)) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader.status)) {
                return false;
            }
//            if (0 != responseHeader.status) {
//                return false;
//            }
            if (Util.isEmpty(data.responseHeader.params)) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader.params.q)) {
                return false;
            }

            if (Util.isEmpty(data.response.numFound) || Util.isEmpty(data.response.start)) {
                return false;
            }
            if (!Util.isArray(data.response.docs)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
