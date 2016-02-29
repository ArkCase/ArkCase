'use strict';

/**
 * @ngdoc service
 * @name services:Object.ListService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/object/object-list.client.service.js services/object/object-list.client.service.js}

 * Object.ListService includes REST calls related to object list in SOLR
 */
angular.module('services').factory('Object.ListService', ['$resource', 'UtilService', 'SearchService'
    , function ($resource, Util, SearchService) {
        var Service = $resource('api/latest/plugin', {}, {
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
             * @param {String} params.query  Search term for tree entry to match
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _queryObjects: {
                method: 'GET',
                url: 'api/latest/plugin/search/:objectType?start=:start&n=:n&s=:sort&filters=:filters&searchQuery=:query',
                cache: false,
                isArray: false
            }

            /**
             * @ngdoc method
             * @name _queryUserObjects
             * @methodOf services:Object.ListService
             *
             * @description
             * Get list of all costsheets from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {String} params.dataType  Object data type. Currently it supports 'timesheet' and 'costsheet'
             * @param {String} params.userId  String that contains userId for logged user. List of costsheets are generated depending on this userId
             * @param {Number} params.start  Zero based index of result starts from
             * @param {Number} params.n max Number of list to return
             * @param {String} params.sort  Sort value. Allowed choice is based on backend specification
             * @param {String} params.query  Search term for tree entry to match
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _queryUserObjects: {
                method: 'GET',
                url: 'api/v1/service/:dataType/user/:userId?start=:start&n=:n&s=:sort&searchQuery=:query',
                cache: false,
                isArray: false
            }
        });


        /**
         * @ngdoc method
         * @name validateObjects
         * @methodOf services:Object.ListService
         *
         * @description
         * Validate list of objects as SOLR query result
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateObjects = function (data) {
            if (!SearchService.validateSolrData(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
