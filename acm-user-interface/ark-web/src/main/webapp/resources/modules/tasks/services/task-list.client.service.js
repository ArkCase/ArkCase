'use strict';

/**
 * @ngdoc service
 * @name tasks.service:Task.ListService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/tasks/services/task-info.client.service.js modules/tasks/services/task-info.client.service.js}
 *
 * Task.ListService provides functions for Task database data
 */
angular.module('tasks').factory('Task.ListService', ['$resource', '$translate', 'StoreService', 'UtilService', 'ConstantService', 'Object.ListService',
    function ($resource, $translate, Store, Util, Constant, ObjectListService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {});

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            TASK_LIST: "TaskList"
        };

        /**
         * @ngdoc method
         * @name queryTasksTreeData
         * @methodOf tasks.service:Task.ListService
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
        Service.queryTasksTreeData = function (start, n, sort, filters) {
            var cacheTaskList = new Store.CacheFifo(Service.CacheNames.TASK_LIST);
            var cacheKey = start + "." + n + "." + sort + "." + filters;
            var treeData = cacheTaskList.get(cacheKey);

            var param = {};
            param.objectType = "TASK";
            param.start = start;
            param.n = n;
            param.sort = sort;
            param.filters = filters;
            return Util.serviceCall({
                service: ObjectListService._queryObjects
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Service.validateTaskList(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            var nodeType = (Util.goodValue(doc.adhocTask_b, false)) ? Constant.ObjectTypes.ADHOC_TASK : Constant.ObjectTypes.TASK;

                            //jwu: for testing
                            //if (doc.object_id_s == 9601) {
                            //    nodeType = Constant.ObjectTypes.ADHOC_TASK;
                            //}

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
        };


        /**
         * @ngdoc method
         * @name validateTaskList
         * @methodOf tasks.service:Task.ListService
         *
         * @description
         * Validate task list data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateTaskList = function (data) {
            if (!ObjectListService.validateSolrData(data)) {
                return false;
            }

            return true;
        };

        return Service;
    }
]);
