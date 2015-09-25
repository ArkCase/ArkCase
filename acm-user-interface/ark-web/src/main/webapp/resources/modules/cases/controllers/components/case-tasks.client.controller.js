'use strict';

angular.module('cases').controller('Cases.TasksController', ['$scope', '$stateParams', '$q', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, Util, Validator, LookupService, CasesService) {
		$scope.$emit('req-component-config', 'tasks');

        $scope.currentId = $stateParams.id;
        $scope.start = 0;
        $scope.pageSize = 10;
        $scope.sort = {by: "", dir: "asc"};
        $scope.filters = [];


        var promiseUsers = Util.servicePromise({
            service: LookupService.getUsers
            , callback: function (data) {
                $scope.userFullNames = [];
                var arr = Util.goodArray(data);
                for (var i = 0; i < arr.length; i++) {
                    var obj = Util.goodJsonObj(arr[i]);
                    if (obj) {
                        var user = {};
                        user.id = Util.goodValue(obj.object_id_s);
                        user.name = Util.goodValue(obj.name);
                        $scope.userFullNames.push(user);
                    }
                }
                return $scope.userFullNames;
            }
        });

        var promiseMyTasks = Util.servicePromise({
            service: CasesService.queryMyTasks
            , param: {user: "ann-acm"}
            , callback: function (data) {
                var arr = Util.goodArray(data);
                $scope.myTasks = _.map(data, _.partialRight(_.pick, "taskId", "adhocTask", "completed", "status", "availableOutcomes"));
                //
                //lodash equivalent to the following:
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
                return $scope.myTasks;
            }
        });


        $scope.config = null;
        $scope.gridOptions = {};
        $scope.$on('component-config', applyConfig);
		function applyConfig(e, componentId, config) {
			if (componentId == 'tasks') {
				$scope.config = config;
				$scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: false,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    noUnselect: false,

                    paginationPageSizes: config.paginationPageSizes,
                    paginationPageSize: config.paginationPageSize,
                    useExternalPagination: true,
                    useExternalSorting: true,

                    //comment out filtering until service side supports it
                    ////enableFiltering: config.enableFiltering,
                    //enableFiltering: true,
                    //useExternalFiltering: true,

					columnDefs: config.columnDefs,
					onRegisterApi: function(gridApi) {
						$scope.gridApi = gridApi;
                        $scope.gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                            if (0 >= sortColumns.length) {
                                $scope.sort.by = null;
                                $scope.sort.dir = null;
                            } else {
                                $scope.sort.by = sortColumns[0].field;
                                $scope.sort.dir = sortColumns[0].sort.direction;
                            }
                            $scope.updatePageData();
                        });
                        $scope.gridApi.core.on.filterChanged($scope, function () {
                            var grid = this.grid;
                            $scope.filters = [];
                            for (var i = 0; i < grid.columns.length; i++) {
                                if (!_.isEmpty(grid.columns[i].filters[0].term)) {
                                    var filter = {};
                                    filter.by = grid.columns[i].field;
                                    filter.with = grid.columns[i].filters[0].term;
                                    $scope.filters.push(filter);
                                }
                            }
                            $scope.updatePageData();
                        });
                        $scope.gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                            $scope.start = (newPage - 1) * pageSize;   //newPage is 1-based index
                            $scope.pageSize = pageSize;
                            $scope.updatePageData();
                        });
					}
				};

                $q.all([promiseUsers, promiseMyTasks]).then(function (data) {
                    for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                        if ("userFullNames" == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: grid.appScope.userFullNames:'id':'name'";

                        } else if ("taskOutcomes" == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].cellTemplate = '<span ng-hide="row.entity.acm$_taskActionDone"><select'
                                + ' ng-options="option.value for option in row.entity.acm$_taskOutcomes track by option.id"'
                                + ' ng-model="row.entity.acm$_taskOutcome">'
                                + ' </select>'
                                + ' <span ng-hide="\'noop\'==row.entity.acm$_taskOutcome.id"><i class="fa fa-gear fa-lg" ng-click="grid.appScope.action(row.entity)"></i></span></span>';
                        }
                    }
                });

                $scope.pageSize = config.paginationPageSize;
                $scope.updatePageData();
			}
		}


        $scope.updatePageData = function () {
            var sort = "";
            if ($scope.sort) {
                if (!_.isEmpty($scope.sort.by) && !_.isEmpty($scope.sort.dir)) {
                    sort = $scope.sort.by + "%20" + $scope.sort.dir;
                }
            }
            //implement filtering here when service side supports it
            //var filter = "";
            ////$scope.filters = [{by: "eventDate", with: "term"}];

            CasesService.queryTasks({
                id: $scope.currentId,
                startWith: $scope.start,
                count: $scope.pageSize,
                sort: sort
            }, function (data) {
                if (Validator.validateSolrData(data)) {
                    $q.all([promiseUsers, promiseMyTasks]).then(function () {
                        var tasks = data.response.docs;
                        $scope.gridOptions.data = tasks;
                        $scope.gridOptions.totalItems = data.response.numFound;

                        for (var i = 0; i < tasks.length; i++) {
                            var task = tasks[i];
                            task.acm$_taskOutcomes = [{id: "noop", value: "(Select One)"}];
                            task.acm$_taskOutcome = {id: "noop", value: "(Select One)"};
                            task.acm$_taskActionDone = true;

                            var found = _.where($scope.myTasks, {taskId: tasks[i].id});
                            if (0 < found.length) {
                                var myTask = found[0];
                                if (!myTask.completed && myTask.adhocTask) {
                                    task.acm$_taskOutcomes.push({id: "complete", value: "Complete"});
                                    task.acm$_taskOutcomes.push({id: "delete", value: "Delete"});
                                    task.acm$_taskActionDone = false;

                                } else if (!myTask.completed && !myTask.adhocTask && !Util.isArrayEmpty(myTask.availableOutcomes)) {
                                    var availableOutcomes = Util.goodArray(myTask.availableOutcomes);
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
            return CasesService.completeTask({taskId: rowEntity.id}, {}
                , function (successData) {
                    rowEntity.acm$_taskActionDone = true;
                    rowEntity.status_s = "COMPLETE";
                }
                , function (errorData) {
                }
            );
        };
        var deleteTask = function (rowEntity) {
            return CasesService.deleteTask({taskId: rowEntity.id}, {}
                , function (successData) {
                    rowEntity.acm$_taskActionDone = true;
                    var tasks = Util.goodArray($scope.gridOptions.data);
                    for (var i = 0; i < tasks.length; i++) {
                        if (tasks[i].id == rowEntity.id) {
                            tasks.splice(i, 1);
                            break;
                        }
                    }
                }
                , function (errorData) {
                }
            );
        };
        var completeTaskWithOutcome = function (rowEntity) {
            var task = Util.omitNg(rowEntity);
            return CasesService.completeTaskWithOutcome({}, task
                , function (successData) {
                    rowEntity.acm$_taskActionDone = true;
                    rowEntity.status_s = "COMPLETE";
                }
                , function (errorData) {
                }
            );
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

	}
]);