'use strict';

/**
 * @ngdoc service
 * @name time-tracking.service:TimeTracking.InfoService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/time-tracking/services/time-tracking-info.client.service.js modules/time-tracking/services/time-tracking-info.client.service.js}

 * TimeTracking.InfoService provides functions for Timesheet database data
 */
angular.module('services').factory('TimeTracking.InfoService', ['$resource', '$translate', 'UtilService',
    function ($resource, $translate, Util) {
        var Service = $resource('proxy/arkcase/api/v1/service/timesheet', {}, {

            /**
             * @ngdoc method
             * @name get
             * @methodOf time-tracking.service:TimeTracking.InfoService
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
             * @methodOf time-tracking.service:TimeTracking.InfoService
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

        /**
         * @ngdoc method
         * @name getTimeTrackingInfo
         * @methodOf time-tracking.service:TimeTracking.InfoService
         *
         * @description
         * Query timesheet data
         *
         * @param {Number} id  Timesheet ID
         *
         * @returns {Object} Promise
         */
        Service.getTimeTrackingInfo =  function (id) {
            return Util.serviceCall({
                service: Service.get
                , param: {id: id}
                , onSuccess: function (data) {
                    if (Service.validateTimesheet(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name saveTimesheetInfo
         * @methodOf time-tracking.service:TimeTracking.InfoService
         *
         * @description
         * Save timesheet data
         *
         * @param {Object} timesheetInfo  Timesheet data
         *
         * @returns {Object} Promise
         */
        Service.saveTimesheetInfo = function (timesheetInfo) {
            if (!Service.validateTimesheet(timesheetInfo)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            }
            return Util.serviceCall({
                service: Service.save
                , data: timesheetInfo
                , onSuccess: function (data) {
                    if (Service.validateTimesheet(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateTimesheet
         * @methodOf time-tracking.service:TimeTracking.InfoService
         *
         * @description
         * Validate timesheet
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateTimesheet = function(data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.user)) {
                return false;
            }
            if (Util.isEmpty(data.user.userId)) {
                return false;
            }
            if (Util.isEmpty(data.startDate)) {
                return false;
            }
            if (Util.isEmpty(data.endDate)) {
                return false;
            }
            if (!Util.isArray(data.times)) {
                return false;
            }
            if (Util.isEmpty(data.status)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);