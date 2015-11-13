'use strict';

/**
 * @ngdoc service
 * @name services.service:CallTasksService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/call-tasks.client.service.js services/resource/call-tasks.client.service.js}

 * CallTasksService contains wrapper functions of TasksService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('CallTasksService', ['$resource', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'TasksService', 'ConstantService',
    function ($resource, $translate, Store, Util, Validator, TasksService, Constant) {
        var ServiceCall = {
            SessionCacheNames: {
            }
            , CacheNames: {
                TASK_LIST: "TaskList"
                , TASK_INFO: "TaskInfo"
                , TASK_HISTORY: "TaskHistory"
            }

            /**
             * @ngdoc method
             * @name queryTasksTreeData
             * @methodOf services.service:CallTasksService
             *
             * @description
             * Query list of tasks from SOLR, pack result for Object Tree.
             *
             * @param {Number} start  Zero based index of result starts from
             * @param {Number} n max Number of list to return
             * @param {String} sort  Sort value. Allowed choice is based on backend specification
             * @param {String} filters  Filter value. Allowed choice is based on backend specification
             *
             * @returns {Object} Promise
             */
            , queryTasksTreeData: function (start, n, sort, filters) {
                var cacheTaskList = new Store.CacheFifo(this.CacheNames.TASK_LIST);
                var cacheKey = start + "." + n + "." + sort + "." + filters;
                var treeData = cacheTaskList.get(cacheKey);

                var param = {};
                param.start = start;
                param.n = n;
                param.sort = sort;
                param.filters = filters;
                return Util.serviceCall({
                    service: TasksService.queryTasks
                    , param: param
                    , result: treeData
                    , onSuccess: function (data) {
                        if (Validator.validateSolrData(data)) {
                            treeData = {docs: [], total: data.response.numFound};
                            var docs = data.response.docs;
                            _.forEach(docs, function (doc) {
                                var nodeType = (Util.goodValue(doc.adhocTask_b, false)) ? Constant.ObjectTypes.ADHOC_TASK : Constant.ObjectTypes.TASK;

                                //jwu: for testing
                                if (doc.object_id_s == 9601) {
                                    nodeType = Constant.ObjectTypes.ADHOC_TASK;
                                }

                                treeData.docs.push({
                                    nodeId: Util.goodValue(doc.object_id_s, 0)
                                    , nodeType: nodeType
                                    , nodeTitle: Util.goodValue(doc.title_parseable)
                                    , nodeToolTip: Util.goodValue(doc.title_parseable)
                                });
                            });
                            cacheTaskList.put(cacheKey, treeData);
                            return treeData;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name getTaskInfo
             * @methodOf services.service:CallTasksService
             *
             * @description
             * Query task data
             *
             * @param {Number} id  Task ID
             *
             * @returns {Object} Promise
             */
            , getTaskInfo: function (id) {
                var cacheTaskInfo = new Store.CacheFifo(this.CacheNames.TASK_INFO);
                var taskInfo = cacheTaskInfo.get(id);
                return Util.serviceCall({
                    service: TasksService.get
                    , param: {id: id}
                    , result: taskInfo
                    , onSuccess: function (data) {
                        if (ServiceCall.validateTaskInfo(data)) {
                            cacheTaskInfo.put(id, data);
                            return data;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name saveTaskInfo
             * @methodOf services.service:CallTasksService
             *
             * @description
             * Save task data
             *
             * @param {Object} taskInfo  Task data
             *
             * @returns {Object} Promise
             */
            , saveTaskInfo: function (taskInfo) {
                if (!ServiceCall.validateTaskInfo(taskInfo)) {
                    return Util.errorPromise($translate.instant("common.service.error.invalidData"));
                }
                return Util.serviceCall({
                    service: TasksService.save
                    , param: {id: taskInfo.taskId}
                    , data: taskInfo
                    , onSuccess: function (data) {
                        if (ServiceCall.validateTaskInfo(data)) {
                            return data;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validateTaskInfo
             * @methodOf services.service:CallTasksService
             *
             * @description
             * Validate task data
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateTaskInfo: function (data) {
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
            }

            /**
             * @ngdoc method
             * @name queryTaskHistory
             * @methodOf services.service:CallTasksService
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
            , queryTaskHistory: function (taskInfo) {
                var cacheTaskHistory = new Store.CacheFifo(this.CacheNames.TASK_HISTORY);
                var taskHistory = cacheTaskHistory.get(taskInfo.taskId);
                var adhoc = Util.isEmpty(taskInfo.businessProcessId);
                var queryId = (adhoc) ? taskInfo.taskId : taskInfo.businessProcessId;
                return Util.serviceCall({
                    service: TasksService.queryTaskHistory
                    , param: {
                        queryId: queryId
                        , adhoc: adhoc
                    }
                    , result: taskHistory
                    , onSuccess: function (data) {
                        if (ServiceCall.validateTaskHistory(data)) {
                            cacheTaskHistory.put(taskInfo.taskId, data);
                            return data;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validateTaskHistory
             * @methodOf services.service:CallTasksService
             *
             * @description
             * Validate task history
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateTaskHistory: function (data) {
                if (!Util.isArray(data)) {
                    return false;
                }
                return true;
            }

        };

        return ServiceCall;
    }
]);
