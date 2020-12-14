'use strict';

/**
 * @ngdoc service
 * @name tasks.service:Task.ListService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/tasks/services/task-info.client.service.js modules/tasks/services/task-info.client.service.js}
 *
 * Task.ListService provides functions for Task database data
 */
angular.module('tasks').factory('Task.ListService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', 'ObjectService', 'Object.ListService', function($resource, $translate, Store, Util, ObjectService, ObjectListService) {
    var Service = $resource('api/latest/plugin', {}, {});

    Service.SessionCacheNames = {};
    Service.CacheNames = {
        TASK_LIST: "TaskList"
    };

    /**
     * @ngdoc method
     * @name resetTasksTreeData
     * @methodOf services:Task.ListService
     *
     * @description
     * Reset tree to initial state, including empty tree data
     *
     * @returns None
     */
    Service.resetTasksTreeData = function() {
        var cacheTaskList = new Store.CacheFifo(Service.CacheNames.TASK_LIST);
        cacheTaskList.reset();
    };

    /**
     * @ngdoc method
     * @name updateTasksTreeData
     * @methodOf services:Task.ListService
     *
     * @description
     * Update a node data in tree.
     *
     * @param {Number} start  Zero based index of result starts from
     * @param {Number} n max Number of list to return
     * @param {String} sort  Sort value. Allowed choice is based on backend specification
     * @param {String} filters  Filter value. Allowed choice is based on backend specification
     * @param {String} query  Search term for tree entry to match
     * @param {Object} nodeData  Node data
     *
     * @returns {Object} Promise
     */
    Service.updateTasksTreeData = function(start, n, sort, filters, query, nodeData) {
        ObjectListService.updateObjectTreeData(Service.CacheNames.TASK_LIST, start, n, sort, filters, query, nodeData);
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
     * @param {String} query  Search term for tree entry to match
     *
     * @returns {Object} Promise
     */
    Service.queryTasksTreeData = function(start, n, sort, filters, query, nodeMaker) {
        var param = {};
        param.objectType = "TASK";
        param.start = Util.goodValue(start, 0);
        param.n = Util.goodValue(n, 32);
        param.sort = Util.goodValue(sort);
        param.filters = Util.goodValue(filters);
        param.query = Util.goodValue(query);

        var cacheTaskList = new Store.CacheFifo(Service.CacheNames.TASK_LIST);
        var cacheKey = param.start + "." + param.n + "." + param.sort + "." + param.filters + "." + param.query;
        var treeData = cacheTaskList.get(cacheKey);

        return Util.serviceCall({
            service: ObjectListService._queryObjects,
            param: param,
            result: treeData,
            onSuccess: function(data) {
                if (Service.validateTaskList(data)) {
                    treeData = {
                        docs: [],
                        total: data.response.numFound
                    };
                    var docs = data.response.docs;
                    _.forEach(docs, function(doc) {
                        var nodeType = (Util.goodValue(doc.adhocTask_b, false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;

                        //jwu: for testing
                        //if (doc.object_id_s == 9601) {
                        //    nodeType = ObjectService.ObjectTypes.ADHOC_TASK;
                        //}

                        var node;
                        if (nodeMaker) {
                            node = nodeMaker(doc);
                        } else {
                            node = {
                                nodeId: Util.goodValue(doc.object_id_s, 0),
                                nodeType: nodeType,
                                nodeTitle: Util.goodValue(doc.title_parseable),
                                nodeToolTip: Util.goodValue(doc.title_parseable)
                            };
                        }
                        treeData.docs.push(node);
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
    Service.validateTaskList = function(data) {
        if (!ObjectListService.validateObjects(data)) {
            return false;
        }

        return true;
    };

    return Service;
} ]);
