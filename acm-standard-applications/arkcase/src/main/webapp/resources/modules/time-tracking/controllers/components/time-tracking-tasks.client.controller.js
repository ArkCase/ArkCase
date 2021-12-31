'use strict';

angular.module('time-tracking').controller(
        'TimeTracking.TasksController',
        [ '$scope', '$stateParams', 'UtilService', 'ConfigService', 'Helper.UiGridService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', 'LookupService', 'Task.AlertsService', 'Object.TaskService', 'ObjectService', 'ModalDialogService', 'Task.WorkflowService', '$timeout', 'Authentication',
                function($scope, $stateParams, Util, ConfigService, HelperUiGridService, TimeTrackingInfoService, HelperObjectBrowserService, LookupService, TaskAlertsService, ObjectTaskService, ObjectService, ModalDialogService, TaskWorkflowService, $timeout, Authentication) {

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "time-tracking",
                        componentId: "tasks",
                        retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo,
                        validateObjectInfo: TimeTrackingInfoService.validateTimesheet,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        },
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        }
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });
                    var promiseUsers = gridHelper.getUsers();

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        //first the filter is set, and after that everything else,
                        //so that the data loads with the new filter applied
                        gridHelper.setUserNameFilterToConfig(promiseUsers).then(function(updatedConfig) {
                            $scope.config = updatedConfig;
                            if ($scope.gridApi != undefined)
                                $scope.gridApi.core.refresh();
                            gridHelper.setColumnDefs(updatedConfig);
                            gridHelper.setBasicOptions(updatedConfig);
                            gridHelper.disableGridScrolling(updatedConfig);
                            gridHelper.setExternalPaging(updatedConfig, retrieveGridData);
                            gridHelper.addButton(updatedConfig, "delete", null, null, "isDeleteDisabled");
                        });

                        //default (init) sorting of tasks grid
                        $scope.sort = {
                            by: "create_date_tdt",
                            dir: "desc"
                        };
                        
                        componentHelper.doneConfig(config);
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        retrieveGridData();

                    };
                    var retrieveGridData = function() {
                        var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
                        if (Util.goodPositive(currentObjectId, false)) {
                            ObjectTaskService.resetChildTasks(ObjectService.ObjectTypes.TIMESHEET, currentObjectId);
                            ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.TIMESHEET, currentObjectId, Util.goodValue($scope.start, 0), Util.goodValue($scope.pageSize, 10), Util.goodValue($scope.sort.by), Util.goodValue($scope.sort.dir)).then(function(data) {
                                var tasks = data.response.docs;
                                angular.forEach(tasks, function(task) {
                                    //calculate to show alert icons if task is in overdue or deadline is approaching if the status of the task is in different state than CLOSED.
                                    task.isOverdue = TaskAlertsService.calculateOverdue(new Date(task.dueDate_tdt)) && !(task.status_lcs === "CLOSED");
                                    task.isDeadline = TaskAlertsService.calculateDeadline(new Date(task.dueDate_tdt)) && !(task.status_lcs === "CLOSED");
                                });
                                $scope.gridOptions = $scope.gridOptions || {};
                                $scope.gridOptions.data = tasks;
                                $scope.gridOptions.totalItems = data.response.numFound;

                                return data;
                            });
                        }
                    };

                    $scope.addNew = function() {
                        var modalMetadata = {
                            moduleName: "tasks",
                            templateUrl: "modules/tasks/views/components/task-new-task.client.view.html",
                            controllerName: "Tasks.NewTaskController",
                            params: {
                                parentType: ObjectService.ObjectTypes.TIMESHEET,
                                parentObject: $scope.objectInfo.timesheetNumber,
                                parentId: $scope.objectInfo.id,
                                parentTitle: $scope.objectInfo.title,
                                taskType: 'ACM_TASK'
                            }
                        };
                        ModalDialogService.showModal(modalMetadata).then(function (value) {
                            $timeout(function() {
                                retrieveGridData();
                                //3 seconds delay so solr can index the new task
                            }, 3000);
                        });
                    };

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.userInfo = userInfo;
                        $scope.userId = userInfo.userId;
                        return userInfo;
                    });

                    $scope.isDeleteDisabled = function(rowEntity) {
                        return ((Util.isEmpty(rowEntity.assignee_id_lcs) || (rowEntity.assignee_id_lcs !== $scope.userId)) || (rowEntity.status_lcs === "CLOSED") || (!rowEntity.adhocTask_b));
                    };
                    $scope.deleteRow = function(rowEntity) {
                        var timesheetInfo = Util.omitNg($scope.objectInfo);
                        if (TimeTrackingInfoService.validateTimesheet(timesheetInfo)) {
                            TaskWorkflowService.deleteTask(rowEntity.object_id_s).then(function(timesheetInfo) {
                                gridHelper.deleteRow(rowEntity);
                                $scope.$emit("report-object-updated", timesheetInfo);
                                return timesheetInfo;
                            });
                        }
                    };
                    $scope.onClickObjLink = function(event, rowEntity) {
                        event.preventDefault();
                        var targetType = (Util.goodMapValue(rowEntity, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                        var targetId = Util.goodMapValue(rowEntity, "object_id_s");
                        gridHelper.showObject(targetType, targetId);
                    };
                } ]);
