'use strict';

/**
 * @ngdoc service
 * @name tasks.service:Task.HistoryService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/tasks/services/task-history.client.service.js modules/tasks/services/task-history.client.service.js}
 *
 * Task.HistoryService provides functions for Task history
 */
angular.module('tasks').factory('Task.HistoryService', ['$resource', '$translate', 'StoreService', 'UtilService',
    function ($resource, $translate, Store, Util) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _queryTaskHistory
             * @methodOf tasks.service:Task.HistoryService
             *
             * @description
             * Query list of task history.
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.queryId  Task ID for none ADHOC task; business process ID for ADHOC task
             * @param {Boolean} params.adhoc True if ADHOC task
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            _queryTaskHistory: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/task/history/:queryId/:adhoc',
                cache: false,
                isArray: true
            }

        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            TASK_HISTORY: "TaskHistory"
        };


        /**
         * @ngdoc method
         * @name queryTaskHistory
         * @methodOf tasks.service:Task.HistoryService
         *
         * @param {Object} taskInfo Task data
         *
         * @description
         * Query task history
         *
         * @param {Number} id  Task ID
         *
         * @returns {Object} Promise
         */
        Service.queryTaskHistory = function (taskInfo) {
            var cacheTaskHistory = new Store.CacheFifo(Service.CacheNames.TASK_HISTORY);
            var taskHistory = cacheTaskHistory.get(taskInfo.taskId);
            var adhoc = Util.isEmpty(taskInfo.businessProcessId);
            var queryId = (adhoc) ? taskInfo.taskId : taskInfo.businessProcessId;
            return Util.serviceCall({
                service: Service._queryTaskHistory
                , param: {
                    queryId: queryId
                    , adhoc: adhoc
                }
                , result: taskHistory
                , onSuccess: function (data) {
                    if (Service.validateTaskHistory(data)) {
                        cacheTaskHistory.put(taskInfo.taskId, data);
                        return data;
                    }
                }
            });
        }

        /**
         * @ngdoc method
         * @name validateTaskHistory
         * @methodOf tasks.service:Task.HistoryService
         *
         * @description
         * Validate task history
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateTaskHistory = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        }


        return Service;
    }
]);
