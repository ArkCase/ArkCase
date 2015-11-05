'use strict';

angular.module('cases').controller('Cases.TasksController', ['$scope', '$stateParams', '$q', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, Store, Util, Validator, Helper, LookupService, CasesService) {
		$scope.$emit('req-component-config', 'tasks');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("tasks" == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
                Helper.Grid.setExternalPaging($scope, config, $scope.retrieveGridData);
                Helper.Grid.setUserNameFilter($scope, promiseUsers);

                promiseMyTasks.promise.then(function (data) {
                    for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                        if ("taskId" == $scope.config.columnDefs[i].name) {
                            $scope.gridOptions.columnDefs[i].cellTemplate = "<a href='#' ng-click='grid.appScope.showUrl($event, row.entity)'>{{row.entity.object_id_s}}</a>";
                        } else if (Util.Constant.LOOKUP_TASK_OUTCOMES == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].cellTemplate = '<span ng-hide="row.entity.acm$_taskActionDone"><select'
                                + ' ng-options="option.value for option in row.entity.acm$_taskOutcomes track by option.id"'
                                + ' ng-model="row.entity.acm$_taskOutcome">'
                                + ' </select>'
                                + ' <span ng-hide="\'noop\'==row.entity.acm$_taskOutcome.id"><i class="fa fa-gear fa-lg" ng-click="grid.appScope.action(row.entity)"></i></span></span>';
                        }
                    }
                });

                $scope.retrieveGridData();
            }
        });


        var promiseUsers = Helper.Grid.getUsers($scope);

        var promiseMyTasks = $q.defer();
        Helper.getUserInfo().then(function (data) {
            var userId = data.userId;

            var cacheMyTasks = new Store.CacheFifo(Helper.CacheNames.MY_TASKS);
            var cacheKey = $scope.currentId;
            var myTasks = cacheMyTasks.get(cacheKey);
            Util.serviceCall({
                service: CasesService.queryMyTasks
                , param: {user: userId}
                , result: myTasks
                , onSuccess: function (data) {
                    if (Validator.validateMyTasks(data)) {
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
            }).then(
                function (myTasks) {
                    $scope.myTasks = myTasks;
                    promiseMyTasks.resolve(myTasks);
                    return myTasks;
                }
                , function (error) {
                    promiseMyTasks.reject(error);
                    return error;
                }
            );

        });


        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            CasesService.queryTasks(Helper.Grid.withPagingParams($scope, {
                id: $scope.currentId
            }), function (data) {
                if (Validator.validateSolrData(data)) {
                    $q.all([promiseUsers, promiseMyTasks]).then(function () {
                        var tasks = data.response.docs;
                        $scope.gridOptions.data = tasks;
                        $scope.gridOptions.totalItems = data.response.numFound;
                        Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);

                        for (var i = 0; i < tasks.length; i++) {
                            var task = tasks[i];
                            task.acm$_taskOutcomes = [{id: "noop", value: "(Select One)"}];
                            task.acm$_taskOutcome = {id: "noop", value: "(Select One)"};
                            task.acm$_taskActionDone = true;

                            var found = _.find($scope.myTasks, {taskId: tasks[i].id});
                            if (found) {
                                if (!found.completed && found.adhocTask) {
                                    task.acm$_taskOutcomes.push({id: "complete", value: "Complete"});
                                    task.acm$_taskOutcomes.push({id: "delete", value: "Delete"});
                                    task.acm$_taskActionDone = false;

                                } else if (!found.completed && !found.adhocTask && !Util.isArrayEmpty(found.availableOutcomes)) {
                                    var availableOutcomes = Util.goodArray(found.availableOutcomes);
                                    for (var j = 0; j < availableOutcomes.length; j++) {
                                        var outcome = {
                                            id: Util.goodValue(availableOutcomes[j].description),
                                            value: Util.goodValue(availableOutcomes[j].description)
                                        };
                                        task.acm$_taskOutcomes.push(outcome);
                                    }
                                    task.acm$_taskActionDone = (1 >= availableOutcomes.length); //1 for '(Select One)'
                                }
                            }
                        }
                    }); //end $q
                }
            })
        };

        $scope.addNew = function () {
            alert("TODO: Launch task wizard");
        };

        var completeTask = function (rowEntity) {
            return Util.serviceCall({
                service: CasesService.completeTask
                , param: {taskId: rowEntity.id}
                , data: {}
            }).then(
                function (successData) {
                    rowEntity.acm$_taskActionDone = true;
                    rowEntity.status_s = "COMPLETE";
                    return successData;
                }
            );

            //CasesService.completeTask({taskId: rowEntity.id}, {}
            //    , function (successData) {
            //        rowEntity.acm$_taskActionDone = true;
            //        rowEntity.status_s = "COMPLETE";
            //    }
            //    , function (errorData) {
            //    }
            //);
        };
        var deleteTask = function (rowEntity) {
            return Util.serviceCall({
                service: CasesService.deleteTask
                , param: {taskId: rowEntity.id}
                , data: {}
            }).then(
                function (successData) {
                    rowEntity.acm$_taskActionDone = true;
                    var tasks = Util.goodArray($scope.gridOptions.data);
                    for (var i = 0; i < tasks.length; i++) {
                        if (tasks[i].id == rowEntity.id) {
                            tasks.splice(i, 1);
                            break;
                        }
                    }
                    return successData;
                }
            );
            //CasesService.deleteTask({taskId: rowEntity.id}, {}
            //    , function (successData) {
            //        rowEntity.acm$_taskActionDone = true;
            //        var tasks = Util.goodArray($scope.gridOptions.data);
            //        for (var i = 0; i < tasks.length; i++) {
            //            if (tasks[i].id == rowEntity.id) {
            //                tasks.splice(i, 1);
            //                break;
            //            }
            //        }
            //    }
            //    , function (errorData) {
            //    }
            //);
        };
        var completeTaskWithOutcome = function (rowEntity) {
            var task = Util.omitNg(rowEntity);
            return Util.serviceCall({
                service: CasesService.completeTaskWithOutcome
                , data: task
            }).then(
                function (successData) {
                    rowEntity.acm$_taskActionDone = true;
                    rowEntity.status_s = "COMPLETE";
                    return successData;
                }
            );
            //CasesService.completeTaskWithOutcome({}, task
            //    , function (successData) {
            //        rowEntity.acm$_taskActionDone = true;
            //        rowEntity.status_s = "COMPLETE";
            //    }
            //    , function (errorData) {
            //    }
            //);
        };
        $scope.action = function (rowEntity) {
            console.log("act, rowEntity.id=" + rowEntity.id + ", action=" + rowEntity.acm$_taskOutcome.id);
            if ("complete" == rowEntity.acm$_taskOutcome.id) {
                completeTask(rowEntity);
            } else if ("delete" == rowEntity.acm$_taskOutcome.id) {
                deleteTask(rowEntity);
            } else {
                completeTaskWithOutcome(rowEntity);
            }
        }
        $scope.showUrl = function (event, rowEntity) {
            event.preventDefault();
            Helper.Grid.showObject($scope, Util.Constant.OBJTYPE_TASK, Util.goodMapValue(rowEntity, "object_id_s", 0));
        };

	}
]);