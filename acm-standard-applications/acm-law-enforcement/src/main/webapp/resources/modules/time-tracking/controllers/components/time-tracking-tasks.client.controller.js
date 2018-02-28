'use strict';

angular.module('time-tracking').controller(
        'TimeTracking.TasksController',
        [
                '$scope',
                '$stateParams',
                'UtilService',
                'ConfigService',
                'Helper.UiGridService',
                'TimeTracking.InfoService',
                'Helper.ObjectBrowserService',
                'LookupService',
                'Task.AlertsService',
                'Object.TaskService',
                'ObjectService',
                function($scope, $stateParams, Util, ConfigService, HelperUiGridService, TimeTrackingInfoService,
                        HelperObjectBrowserService, LookupService, TaskAlertsService, ObjectTaskService, ObjectService) {

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope : $scope,
                        stateParams : $stateParams,
                        moduleId : "time-tracking",
                        componentId : "tasks",
                        retrieveObjectInfo : TimeTrackingInfoService.getTimesheetInfo,
                        validateObjectInfo : TimeTrackingInfoService.validateTimesheet,
                        onObjectInfoRetrieved : function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        },
                        onConfigRetrieved : function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        }
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope : $scope
                    });
                    var promiseUsers = gridHelper.getUsers();

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                        gridHelper.setExternalPaging(config, retrieveGridData);
                        gridHelper.setUserNameFilter(promiseUsers);
                        gridHelper.addButton(config, "delete", null, null, "isDeleteDisabled");

                        componentHelper.doneConfig(config);
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        retrieveGridData();

                    };
                    var retrieveGridData = function() {
                        var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
                        if (Util.goodPositive(currentObjectId, false)) {
                            ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.TIMESHEET, currentObjectId,
                                    Util.goodValue($scope.start, 0), Util.goodValue($scope.pageSize, 10), Util.goodValue($scope.sort.by),
                                    Util.goodValue($scope.sort.dir)).then(function(data) {
                                var tasks = data.response.docs;
                                angular.forEach(tasks, function(task) {
                                    //calculate to show alert icons if task is in overdue or deadline is approaching
                                    task.isOverdue = TaskAlertsService.calculateOverdue(new Date(task.due_tdt));
                                    task.isDeadline = TaskAlertsService.calculateDeadline(new Date(task.due_tdt));
                                });
                                $scope.gridOptions = $scope.gridOptions || {};
                                $scope.gridOptions.data = tasks;
                                $scope.gridOptions.totalItems = data.response.numFound;

                                return data;
                            });
                        }
                    };

                    $scope.isDeleteDisabled = function(rowEntity) {
                        return !rowEntity.adhocTask_b;
                    };
                    $scope.onClickObjLink = function(event, rowEntity) {
                        event.preventDefault();
                        var targetType = (Util.goodMapValue(rowEntity, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK
                                : ObjectService.ObjectTypes.TASK;
                        var targetId = Util.goodMapValue(rowEntity, "object_id_s");
                        gridHelper.showObject(targetType, targetId);
                    };
                } ]);