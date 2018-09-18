'use strict';

/**
 * @ngdoc service
 * @name tasks.service:Task.InfoService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/tasks/services/task-info.client.service.js modules/tasks/services/task-info.client.service.js}
 *
 * Task.InfoService provides functions for Task database data
 */
angular.module('tasks').factory('Task.InfoService', ['$resource', '$translate', 'UtilService', 'CacheFactory', 'ObjectService', function ($resource, $translate, Util, CacheFactory, ObjectService) {
    var taskCache = CacheFactory(ObjectService.ObjectTypes.TASK, {
        maxAge: 1 * 60 * 1000, // Items added to this cache expire after 1 minute
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive', // Items will be deleted from this cache when they expire
        capacity: 1
    });
    var taskGetUrl = 'api/latest/plugin/task/byId/';

    var Service = $resource('api/latest/plugin', {}, {
        /**
         * ngdoc method
         * name get
         * methodOf tasks.service:Task.InfoService
         *
         * @description
         * Query task data
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Task ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        get: {
            method: 'GET',
            url: taskGetUrl + ':id',
            cache: taskCache,
            isArray: false
        },
        /**
         * @ngdoc method
         * @name save
         * @methodOf tasks.service:Task.InfoService
         *
         * @description
         * Save task data
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Task ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        save: {
            method: 'POST',
            url: 'api/latest/plugin/task/save/:id',
            cache: false
        }
    });

    /**
     * @ngdoc method
     * @name resetTaskInfo
     * @methodOf services:Task.InfoService
     *
     * @description
     * Reset Task info
     *
     * @returns None
     */
    Service.resetTaskInfo = function (taskInfo) {

        if (!Util.isObjectEmpty(taskInfo) && !Util.isEmpty(taskInfo.taskId)) {
            taskCache.remove(taskGetUrl + taskInfo.taskId);
        }
    };

    /**
     * @ngdoc method
     * @name resetTaskCacheById
     * @methodOf tasks.service:Task.InfoService
     *
     * @description
     * Reset cached info for a certain task.
     *
     * @param taskId id of task to clear cache for
     */
    Service.resetTaskCacheById = function (taskId) {
        if (taskId) {
            taskCache.remove(taskGetUrl + taskId);
        }
    };

    /**
     * @ngdoc method
     * @name updateTaskInfo
     * @methodOf tasks.service:Task.InfoService
     *
     * @description
     * Update task data in local cache. No REST call to backend.
     *
     * @param {Object} taskInfo  Task data
     *
     * @returns {Object} Promise
     */
    Service.updateTaskInfo = function (taskInfo) {
        //TODO remove this method
    };

    /**
     * @ngdoc method
     * @name getTaskInfo
     * @methodOf tasks.service:Task.InfoService
     *
     * @description
     * Query task data
     *
     * @param {Number} id  Task ID
     *
     * @returns {Object} Promise
     */
    Service.getTaskInfo = function (id) {
        return Util.serviceCall({
            service: Service.get,
            param: {
                id: id
            },
            onSuccess: function (data) {
                if (Service.validateTaskInfo(data)) {
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name saveTaskInfo
     * @methodOf tasks.service:Task.InfoService
     *
     * @description
     * Save task data
     *
     * @param {Object} taskInfo  Task data
     *
     * @returns {Object} Promise
     */
    Service.saveTaskInfo = function (taskInfo) {
        if (!Service.validateTaskInfo(taskInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        return Util.serviceCall({
            service: Service.save,
            param: {
                id: taskInfo.taskId
            },
            data: taskInfo,
            onSuccess: function (data) {
                if (Service.validateTaskInfo(data)) {
                    var taskInfo = data;
                    taskCache.put(taskGetUrl + taskInfo.taskId, data);
                    return taskInfo;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateTaskInfo
     * @methodOf tasks.service:Task.InfoService
     *
     * @description
     * Validate task data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateTaskInfo = function (data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (0 >= Util.goodValue(data.taskId, 0)) {
            return false;
        }
        return true;
    };

    return Service;
}]);
