'use strict';

/**
 * @ngdoc service
 * @name services:Object.TaskService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/objects/object-task.client.service.js services/objects/object-task.client.service.js}

 * Object.TaskService includes functions for object relate to task.
 */
angular.module('services').factory('Object.TaskService', ['$resource', 'StoreService', 'UtilService', 'Object.ListService',
    function ($resource, Store, Util, ObjectListService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _queryChildTasks
             * @methodOf services:Object.TaskService
             *
             * @description
             * Query child tasks for an object.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.parentType  Parent object type
             * @param {Number} params.parentId  Parent object ID
             * @param {Number} params.start Zero based start number of record
             * @param {Number} params.n Max Number of list to return
             * @param {String} params.sort  Sort value, with format 'sortBy sortDir', sortDir can be 'asc' or 'desc'
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _queryChildTasks: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/search/children?parentType=:parentType&childType=TASK&parentId=:parentId&start=:start&n=:n&s=:sort',
                cache: false
            }
            /**
             * @ngdoc method
             * @name _queryChildTasks
             * @methodOf services:Object.TaskService
             *
             * @description
             * Query task for a user.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.user  User ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _queryMyTasks: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/task/forUser/:user',
                cache: false,
                isArray: true
            }
        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            CHILD_TASK_DATA: "ChildTaskData"
            , My_TASKS: "MyTasks"
        };

        /**
         * @ngdoc method
         * @name queryChildTasks
         * @methodOf services:Object.TaskService
         *
         * @description
         * Query child tasks for an object.
         *
         * @param {Object} params Map of input parameter
         * @param {String} params.objectType  Object type
         * @param {Number} params.parentId  Object ID
         * @param {Number} params.start Zero based start number of record
         * @param {Number} params.n Max Number of list to return
         * @param {String} params.sort  Sort value, with format 'sortBy sortDir', sortDir can be 'asc' or 'desc'
         *
         * @returns {Object} Promise
         */
        Service.queryChildTasks = function (parentType, parentId, start, n, sortBy, sortDir) {
            var cacheChildTaskData = new Store.CacheFifo(Service.CacheNames.CHILD_TASK_DATA);
            var cacheKey = parentType + "." + parentId + "." + start + "." + n + "." + sortBy + "." + sortDir;
            var taskData = cacheChildTaskData.get(cacheKey);

            var sort = "";
            if (!Util.isEmpty(sortBy)) {
                sort = sortBy + " " + Util.goodValue(sortDir, "asc");
            }

            return Util.serviceCall({
                service: Service._queryChildTasks
                , param: {
                    parentType: parentType
                    , parentId: parentId
                    , start: start
                    , n: n
                    , sort: sort
                }
                , result: taskData
                , onSuccess: function (data) {
                    if (Service.validateChildTaskData(data)) {
                        taskData = data;
                        cacheChildTaskData.put(cacheKey, taskData);
                        return taskData;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateChildTaskData
         * @methodOf services:Object.TaskService
         *
         * @description
         * Validate task data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateChildTaskData = function (data) {
            if (!ObjectListService.validateSolrData(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name queryMyTasks
         * @methodOf services:Object.TaskService
         *
         * @description
         * Query list of tasks for a user.
         *
         * @param {String} userId  User ID
         *
         * @returns {Object} Promise
         */
        Service.queryMyTasks = function (userId) {
            var cacheMyTasks = new Store.CacheFifo(Service.CacheNames.My_TASKS);
            var cacheKey = userId;
            var myTasks = cacheMyTasks.get(cacheKey);

            return Util.serviceCall({
                service: Service._queryChildTasks
                , param: {
                    user: userId
                }
                , result: myTasks
                , onSuccess: function (data) {
                    if (Service.validateMyTasks(data)) {
                        myTasks = _.map(data, _.partialRight(_.pick, "taskId", "adhocTask", "completed", "status", "availableOutcomes"));
                        //
                        //Above lodash functions equivalent to the following:
                        //
                        //$scope.myTasks = [];
                        //for (var i = 0; i < arr.length; i++) {
                        //    var task = {};
                        //    task.taskId = Util.goodValue(arr[i].taskId);
                        //    task.adhocTask = Util.goodValue(arr[i].adhocTask);
                        //    task.completed = Util.goodValue(arr[i].completed);
                        //    task.status = Util.goodValue(arr[i].status);
                        //    task.availableOutcomes = Util.goodArray(arr[i].availableOutcomes);
                        //    $scope.myTasks.push(task);
                        //}

                        cacheMyTasks.put(cacheKey, myTasks);
                        return myTasks;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateMyTasks
         * @methodOf services:Object.TaskService
         *
         * @description
         * Validate task array for a user
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateMyTasks = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
