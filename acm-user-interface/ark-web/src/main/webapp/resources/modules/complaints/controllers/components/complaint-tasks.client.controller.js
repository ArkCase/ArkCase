'use strict';

angular.module('complaints').controller('Complaints.TasksController', ['$scope', '$state', '$stateParams', '$q', '$translate'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.TaskService', 'Task.WorkflowService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $q, $translate
        , Util, ConfigService, ObjectService, ObjectTaskService, TaskWorkflowService
        , HelperUiGridService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();
        var promiseMyTasks = ObjectTaskService.queryCurrentUserTasks();

        ConfigService.getComponentConfig("complaints", "tasks").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, $scope.retrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);

            promiseMyTasks.then(function (data) {
                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if ("taskId" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].cellTemplate = "<a href='#' ng-click='grid.appScope.showUrl($event, row.entity)'>{{row.entity.object_id_s}}</a>";
                    } else if (HelperUiGridService.Lookups.TASK_OUTCOMES == $scope.config.columnDefs[i].lookup) {
                        $scope.gridOptions.columnDefs[i].cellTemplate = '<span ng-hide="row.entity.acm$_taskActionDone"><select'
                            + ' ng-options="option.value for option in row.entity.acm$_taskOutcomes track by option.id"'
                            + ' ng-model="row.entity.acm$_taskOutcome">'
                            + ' </select>'
                            + ' <span ng-hide="\'noop\'==row.entity.acm$_taskOutcome.id"><i class="fa fa-gear fa-lg" ng-click="grid.appScope.action(row.entity)"></i></span></span>';
                    }
                }
            });

            $scope.retrieveGridData();
            return config;
        });

        $scope.retrieveGridData = function () {
            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.CASE_FILE
                    , currentObjectId
                    , Util.goodValue($scope.start, 0)
                    , Util.goodValue($scope.pageSize, 10)
                    , Util.goodValue($scope.sort.by)
                    , Util.goodValue($scope.sort.dir)
                ).then(
                    function (data) {
                        $q.all([promiseUsers, promiseMyTasks]).then(function () {
                            var tasks = data.response.docs;
                            $scope.gridOptions = $scope.gridOptions || {};
                            $scope.gridOptions.data = tasks;
                            $scope.gridOptions.totalItems = data.response.numFound;
                            //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);

                            for (var i = 0; i < tasks.length; i++) {
                                var task = tasks[i];
                                task.acm$_taskOutcomes = [{
                                    id: "noop",
                                    value: $translate.instant("common.select.option.none")
                                }];
                                task.acm$_taskOutcome = {
                                    id: "noop",
                                    value: $translate.instant("common.select.option.none")
                                };
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

                        return data;
                    }
                ); //end then
            }
        };

        $scope.addNew = function () {
            $state.go("newTaskFromParentObject", {
                parentType: ObjectService.ObjectTypes.COMPLAINT,
                parentObject: $scope.complaintInfo.complaintNumber
            });

        };

        var completeTask = function (rowEntity) {
            TaskWorkflowService.completeTask(rowEntity.id).then(
                function (taskInfo) {
                    rowEntity.acm$_taskActionDone = true;
                    rowEntity.status_s = TaskWorkflowService.WorkflowStatus.COMPLETE;
                    return taskInfo;
                }
            );
        };
        var deleteTask = function (rowEntity) {
            TaskWorkflowService.deleteTask(rowEntity.id).then(
                function (taskInfo) {
                    rowEntity.acm$_taskActionDone = true;
                    var tasks = Util.goodArray($scope.gridOptions.data);
                    for (var i = 0; i < tasks.length; i++) {
                        if (tasks[i].id == rowEntity.id) {
                            tasks.splice(i, 1);
                            break;
                        }
                    }
                    return taskInfo;
                }
            );
        };
        var completeTaskWithOutcome = function (rowEntity) {
            var task = Util.omitNg(rowEntity);       //todo: need to convert taskSolr to taskInfo
            var outcome = "fixme";
            TaskWorkflowService.completeTaskWithOutcome(task, outcome).then(
                function (successData) {
                    rowEntity.acm$_taskActionDone = true;
                    rowEntity.status_s = TaskWorkflowService.WorkflowStatus.COMPLETE;
                    return successData;
                }
            );
        };
        $scope.action = function (rowEntity) {
            //console.log("act, rowEntity.id=" + rowEntity.id + ", action=" + rowEntity.acm$_taskOutcome.id);
            if ("complete" == rowEntity.acm$_taskOutcome.id) {
                completeTask(rowEntity);
            } else if ("delete" == rowEntity.acm$_taskOutcome.id) {
                deleteTask(rowEntity);
            } else {
                completeTaskWithOutcome(rowEntity);
            }
        };
        $scope.showUrl = function (event, rowEntity) {
            event.preventDefault();
            gridHelper.showObject(ObjectService.ObjectTypes.TASK, Util.goodMapValue(rowEntity, "object_id_s", 0));
        };
        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            var targetType = Util.goodMapValue(rowEntity, "object_type_s");
            var targetId = Util.goodMapValue(rowEntity, "object_id_s");
            gridHelper.showObject(targetType, targetId);
        };
    }
]);