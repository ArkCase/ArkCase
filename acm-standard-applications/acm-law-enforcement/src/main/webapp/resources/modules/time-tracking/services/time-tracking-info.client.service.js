'use strict';

/**
 * @ngdoc service
 * @name service:TimeTracking.InfoService
 *
 * @description
 *
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/time-tracking/services/time-tracking-info.client.service.js modules/time-tracking/services/time-tracking-info.client.service.js}

 * TimeTracking.InfoService provides functions for Timesheet database data
 */
angular.module('services').factory('TimeTracking.InfoService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', function($resource, $translate, Store, Util) {
    var Service = $resource('api/v1/service/timesheet', {}, {

        /**
         * @ngdoc method
         * @name get
         * @methodOf service:TimeTracking.InfoService
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
            url: 'api/v1/service/timesheet/:id',
            cache: false,
            isArray: false
        },

        /**
         * @ngdoc method
         * @name save
         * @methodOf service:TimeTracking.InfoService
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
            url: 'api/v1/service/timesheet/:submissionName',
            cache: false
        }
    });

    Service.CacheNames = {
        TIMESHEET_INFO: "TimesheetInfo"
    };

    /**
     * @ngdoc method
     * @name resetTimesheetInfo
     * @methodOf services:TimeTracking.InfoService
     *
     * @description
     * Reset TimeTracking info
     *
     * @returns None
     */
    Service.resetTimesheetInfo = function() {
        var cacheInfo = new Store.CacheFifo(Service.CacheNames.TIMESHEET_INFO);
        cacheInfo.reset();
    };

    /**
     * @ngdoc method
     * @name getTimesheetInfo
     * @methodOf service:TimeTracking.InfoService
     *
     * @description
     * Query timesheet data
     *
     * @param {Number} id  Timesheet ID
     *
     * @returns {Object} Promise
     */
    Service.getTimesheetInfo = function(id) {
        var cacheTimesheetInfo = new Store.CacheFifo(Service.CacheNames.TIMESHEET_INFO);
        var timesheetInfo = cacheTimesheetInfo.get(id);
        return Util.serviceCall({
            service: Service.get,
            param: {
                id: id
            },
            result: timesheetInfo,
            onSuccess: function(data) {
                if (Service.validateTimesheet(data)) {
                    cacheTimesheetInfo.put(id, data);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name getTimesheetParentObjectsTypeId
     * @methodOf service:TimeTracking.InfoService
     *
     * @description
     * Query Objects(complaint/case) related to timesheet data
     *
     * @param {Number} id  Timesheet ID
     *
     * @returns {Object} Promise
     */
    Service.getTimesheetParentObjectsTypeId = function(id) {
        return Util.serviceCall({
            service: Service.get,
            param: {
                id: id
            },
            onSuccess: function(timesheetInfo) {
                if (Service.validateTimesheet(timesheetInfo)) {
                    return _.map(_.uniq(timesheetInfo.times, function(data) {
                        return data.type + data.objectId;
                    }), function(data) {
                        return {
                            type: data.type,
                            objectId: data.objectId
                        };
                    });
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name saveTimesheetInfo
     * @methodOf service:TimeTracking.InfoService
     *
     * @description
     * Save timesheet data
     *
     * @param {Object} timesheetInfo  Timesheet data
     *
     * @returns {Object} Promise
     */
    Service.saveTimesheetInfo = function(timesheetInfo, submissionName) {
        if (!Service.validateTimesheet(timesheetInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        //we need to make one of the fields is changed in order to be sure that update will be executed
        //if we change modified won't make any differences since is updated before update to database
        //but update will be trigger
        timesheetInfo.modified = null;
        return Util.serviceCall({
            service: Service.save,
            data: timesheetInfo,
            param: {
                submissionName: submissionName
            },
            onSuccess: function(data) {
                if (Service.validateTimesheet(data)) {
                    var timesheetInfo = data;
                    var cacheTimesheetInfo = new Store.CacheFifo(Service.CacheNames.TIMESHEET_INFO);
                    cacheTimesheetInfo.put(timesheetInfo.id, timesheetInfo);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name saveNewTimesheetInfo
     * @methodOf service:TimeTracking.InfoService
     *
     * @description
     * Save new timesheet data
     *
     * @param {Object} timesheetInfo  Timesheet data
     *
     * @returns {Object} Promise
     */
    Service.saveNewTimesheetInfo = function(timesheetInfo, submissionName) {
        if (!Service.validateNewTimesheet(timesheetInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        //we need to make one of the fields is changed in order to be sure that update will be executed
        //if we change modified won't make any differences since is updated before update to database
        //but update will be trigger
        timesheetInfo.modified = null;
        return Util.serviceCall({
            service: Service.save,
            data: timesheetInfo,
            param: {
                submissionName: submissionName
            },
            onSuccess: function(data) {
                if (Service.validateTimesheet(data)) {
                    var timesheetInfo = data;
                    var cacheTimesheetInfo = new Store.CacheFifo(Service.CacheNames.TIMESHEET_INFO);
                    cacheTimesheetInfo.put(timesheetInfo.id, timesheetInfo);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateTimesheet
     * @methodOf service:TimeTracking.InfoService
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

    /**
     * @ngdoc method
     * @name validateNewTimesheet
     * @methodOf service:TimeTracking.InfoService
     *
     * @description
     * Validate new timesheet
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateNewTimesheet = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (data.id) {
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
        return true;
    };

    return Service;
} ]);