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
angular.module('tasks').factory('Task.InfoService', ['$resource', '$translate', 'Acm.StoreService', 'UtilService', 'Object.InfoService'
    , function ($resource, $translate, Store, Util, ObjectInfoService) {
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
            //get: {
            //    method: 'GET',
            //    url: 'api/latest/plugin/task/byId/:id',
            //    cache: false,
            //    isArray: false
            //}

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

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            TASK_INFO: "TaskInfo"
        };

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
        Service.resetTaskInfo = function () {
            var cacheInfo = new Store.CacheFifo(Service.CacheNames.TASK_INFO);
            cacheInfo.reset();
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
            if (Util.goodValue(taskId) && Util.goodPositive(taskId)) {
                var cacheInfo = new Store.CacheFifo(Service.CacheNames.TASK_INFO);
                cacheInfo.put(taskId, null);
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
            if (Service.validateTaskInfo(taskInfo)) {
                var cacheTaskInfo = new Store.CacheFifo(Service.CacheNames.TASK_INFO);
                cacheTaskInfo.put(taskInfo.taskId, taskInfo);
            }
        }

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
            var cacheTaskInfo = new Store.CacheFifo(Service.CacheNames.TASK_INFO);
            var taskInfo = cacheTaskInfo.get(id);
            return Util.serviceCall({
                service: ObjectInfoService.get
                , param: {type: "task", id: id}
                , result: taskInfo
                , onSuccess: function (data) {
                    if (Service.validateTaskInfo(data)) {
                        cacheTaskInfo.put(id, data);
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
                service: Service.save
                , param: {id: taskInfo.taskId}
                , data: taskInfo
                , onSuccess: function (data) {
                    if (Service.validateTaskInfo(data)) {
                        var taskInfo = data;
                        var cacheTaskInfo = new Store.CacheFifo(Service.CacheNames.TASK_INFO);
                        cacheTaskInfo.put(taskInfo.taskId, taskInfo);
                        return taskInfo;
                    }
                }
            });
            //return Util.serviceCall({
            //    service: ObjectInfoService.save
            //    , param: {type: "TASK"}
            //    , data: taskInfo
            //    , onSuccess: function (data) {
            //        if (Service.validateTaskInfo(data)) {
            //            var taskInfo = data;
            //            var cacheTaskInfo = new Store.CacheFifo(Service.CacheNames.TASK_INFO);
            //            cacheTaskInfo.put(taskInfo.taskId, taskInfo);
            //            return taskInfo;
            //        }
            //    }
            //});
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
//            if (Util.isEmpty(data.id) || Util.isEmpty(data.caseNumber)) {
//             return false;
//             }
//             if (!Util.isArray(data.childObjects)) {
//             return false;
//             }
//             if (!Util.isArray(data.participants)) {
//             return false;
//             }
//             if (!Util.isArray(data.personAssociations)) {
//             return false;
//             }
            return true;
        };

        return Service;
    }
]);
