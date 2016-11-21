'use strict';

/**
 * @ngdoc service
 * @name services:Object.TaskService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-task.client.service.js services/object/object-task.client.service.js}

 * Object.TaskService includes functions for object relate to task.
 */
angular.module('services').factory('Object.TaskService', ['$resource', '$q', 'Acm.StoreService', 'UtilService', 'SearchService', 'Authentication'
    , function ($resource, $q, Store, Util, SearchService, Authentication) {
        var Service = $resource('api/latest/plugin', {}, {
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
                url: 'api/latest/plugin/search/children?parentType=:parentType&childType=TASK&parentId=:parentId&start=:start&n=:n&s=:sort',
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
                url: 'api/latest/plugin/task/forUser/:user',
                cache: false,
                isArray: true
            }
            /**
             * @ngdoc method
             * @name _queryMyTasksByParentType
             * @methodOf services:Object.TaskService
             *
             * @description
             * Query task for a user.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.user  User ID
             * @param {String} params.parentType  Parent Object Type
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _queryMyTasksByParentType: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=assignee_id_lcs\\::user+AND+object_type_s\\:TASK+AND+parent_type_s\\::parentType',
                cache: false,
                isArray: false
            }
        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            CHILD_TASK_DATA: "ChildTaskData"
            , My_TASKS: "MyTasks"
        };

        /**
         * @ngdoc method
         * @name resetChildTasks
         * @methodOf services:Object.TaskService
         *
         * @description
         * Reset cache of child tasks
         * If no arguments are given, reset caceh to empty
         * If Only parent type and parent ID is given, reset entries for the given parent object only
         * If all arguments are present, reset cache entry to null for the page
         *
         * @param {String} parentType  (Optional) Parent type
         * @param {Number} parentId  (Optional) Parent ID.
         * @param {String} start  (Optional) Start page
         * @param {String} n  (Optional) Number of task in the page
         * @param {String} sortBy  (Optional) Sort field
         * @param {String} sortDir  (Optional) Sort direction
         *
         */
        Service.resetChildTasks = function (parentType, parentId, start, n, sortBy, sortDir) {
            var cacheChildTaskData = new Store.CacheFifo(Service.CacheNames.CHILD_TASK_DATA);
            if (!Util.isEmpty(start)) {
                var cacheKey = parentType + "." + parentId + "." + start + "." + n + "." + sortBy + "." + sortDir;
                cacheChildTaskData.put(cacheKey, null);

            } else if (!Util.isEmpty(parentId)) {
                var keys = cacheChildTaskData.keys();
                var keyBegin = parentType + "." + parentId + ".";
                var found = _.filter(keys, function(key) {
                    return (key && key.startsWith(keyBegin));
                });
                _.each(found, function(key) {
                    cacheChildTaskData.put(key, null);
                });

            } else {
                cacheChildTaskData.reset();
            }
        };

        /**
         * @ngdoc method
         * @name queryChildTasks
         * @methodOf services:Object.TaskService
         *
         * @description
         * Query child tasks for an object.
         *
         * @param {String} parentType  Object type
         * @param {Number} parentId  Object ID
         * @param {Number} start Zero based start number of record
         * @param {Number} n Max Number of list to return
         * @param {String} sortBy  (Optional)Sort property
         * @param {String} sortDir  (Optional)Sort direction. Value can be 'asc' or 'desc'
         *
         * @returns {Object} Promise
         */
        Service.queryChildTasks = function (parentType, parentId, start, n, sortBy, sortDir) {
            var taskData;
            
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
            if (!SearchService.validateSolrData(data)) {
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
                service: Service._queryMyTasks
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
        
        Service.queryMyTasksByParentType = function (userId, parentType) {
            var myTasks;

            return Util.serviceCall({
                service: Service._queryMyTasksByParentType
                , param: {
                    user: userId
                    , parentType: parentType
                }
                , result: myTasks
                , onSuccess: function (data) {
                    if (Service.validateMyTasksByParentType(data)) {
                        myTasks = _.map(data, _.partialRight(_.pick, "taskId", "adhocTask", "completed", "status", "availableOutcomes"));
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

        Service.validateMyTasksByParentType = function (data) {
            if (!SearchService.validateSolrData(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name queryCurrentUserTasks
         * @methodOf services:Object.TaskService
         *
         * @description
         * Query list of tasks for current login user.
         *
         * @returns {Object} Promise
         */
        Service.queryCurrentUserTasks = function () {
            var dfd = $q.defer();
            Authentication.queryUserInfo().then(
                function (userInfo) {
                    Service.queryMyTasks(userInfo.userId).then(
                        function (myTasks) {
                            dfd.resolve(myTasks);
                            return myTasks;
                        }
                        , function (error) {
                            dfd.reject(error);
                            return error;
                        }
                    );
                    return userInfo;
                }
                , function (error) {
                    dfd.reject(error);
                    return error;
                }
            );
            return dfd.promise;
        };
        
        /**
         * @ngdoc method
         * @name queryMyTasksByParentType
         * @methodOf services:Object.TaskService
         *
         * @description
         * Query list of tasks from Solr for current login user and parent type.
         *
         * @returns {Object} Promise
         */
        Service.queryCurrentUserTasksByParentType = function (parentType) {
            var dfd = $q.defer();
            Authentication.queryUserInfo().then(
                function (userInfo) {
                    Service.queryMyTasksByParentType(userInfo.userId, parentType).then(
                        function (myTasks) {
                            dfd.resolve(myTasks);
                            return myTasks;
                        }
                        , function (error) {
                            dfd.reject(error);
                            return error;
                        }
                    );
                    return userInfo;
                }
                , function (error) {
                    dfd.reject(error);
                    return error;
                }
            );
            return dfd.promise;
        };

        return Service;
    }
]);
