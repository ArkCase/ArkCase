'use-strict'

/**
 * @ngdoc service
 * @name time-tracking.service:TimeTrackingService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/time-tracking/services/time-tracking.client.service.js modules/time-tracking/services/time-tracking.client.service.js}

 * CasesService includes group of REST calls related to Cases module. Functions are implemented using $resource.
 */
angular.module('services').factory('TimeTrackingService', ['$resource',
    function ($resource){
        return $resource('proxy/arkcase/api/v1/service/timesheet', {}, {

            /**
             * @ngdoc method
             * @name listObjects
             * @methodOf time-tracking.service:TimeTrackingService
             *
             * @description
             * Get list of all timesheets from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {String} params.userId  String that contains userId for logged user. List of timesheets are generated depending on this userId
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
                url: 'proxy/arkcase/api/v1/service/timesheet/user/:userId?start=:start&n=:n&sort=:sort',
                cache: false,
                isArray: false
            },

            /**
             * @ngdoc method
             * @name get
             * @methodOf time-tracking.service:TimeTrackingService
             *
             * @description
             * Query timesheet data by given id
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Timesheet ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            get: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/service/timesheet/:id',
                cache: false,
                isArray: false
            },

            /**
             * @ngdoc method
             * @name save
             * @methodOf time-tracking.service:TimeTrackingService
             *
             * @description
             * Save timesheet data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Timesheet ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            save: {
                method: 'POST',
                url: 'proxy/arkcase/api/v1/service/timesheet',
                cache: false
            }
        });
    }
]);
