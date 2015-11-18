'use strict';

/**
 * @ngdoc service
 * @name cost-tracking.service:CostTrackingService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cost-tracking/services/cost-tracking.client.service.js modules/cost-tracking/services/cost-tracking.client.service.js}

 * CostTrackingService includes group of REST calls related to CostTracking module. Functions are implemented using $resource.
 */
angular.module('services').factory('CostTrackingService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/v1/service/costsheet', {}, {

            /**
             * @ngdoc method
             * @name listObjects
             * @methodOf cost-tracking.service:CostTrackingService
             *
             * @description
             * Get list of all costsheets from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {String} params.userId  String that contains userId for logged user. List of costsheets are generated depending on this userId
             * @param {Number} params.start  Zero based index of result starts from
             * @param {Number} params.n max Number of list to return
             * @param {String} params.sort  Sort value. Allowed choice is based on backend specification
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            listObjects: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/service/costsheet/user/:userId?start=:start&n=:n&sort=:sort',
                cache: false,
                isArray: false
            },

            /**
             * @ngdoc method
             * @name get
             * @methodOf cost-tracking.service:CostTrackingService
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
             * @methodOf cost-tracking.service:CostTrackingService
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
    }
]);