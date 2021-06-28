'use strict';

angular.module('people').controller(
    'People.TasksController',
    [ '$scope', '$state', '$stateParams', '$q', '$translate', 'UtilService', 'ConfigService', 'ObjectService', 'Person.InfoService', 'Task.WorkflowService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Task.InfoService', 'Task.AlertsService', 'ModalDialogService', '$timeout', 'Authentication', 'Person.TaskService',
        function($scope, $state, $stateParams, $q, $translate, Util, ConfigService, ObjectService, PersonInfoService, TaskWorkflowService, HelperUiGridService, HelperObjectBrowserService, TaskInfoService, TaskAlertsService, ModalDialogService, $timeout, Authentication, PersonTaskService) {

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "people",
                componentId: "tasks",
                retrieveObjectInfo: PersonInfoService.getPersonInfo,
                validateObjectInfo: PersonInfoService.validatePersonInfo,
                onConfigRetrieved: function(componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
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

                    gridHelper.addButton(updatedConfig, "delete", null, null, "isDeleteDisabled");
                    gridHelper.setColumnDefs(updatedConfig);
                    gridHelper.setBasicOptions(updatedConfig);
                    gridHelper.disableGridScrolling(updatedConfig);
                    gridHelper.setExternalPaging(updatedConfig, retrieveGridData);
                });
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                retrieveGridData();
            };

            var retrieveGridData = function() {
                PersonTaskService.findTasks($scope.objectInfo.id).then(function (data) {
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = data;
                    $scope.gridOptions.totalItems = data.length;
                })
            };

            $scope.deleteRow = function(rowEntity) {
                var personInfo = Util.omitNg($scope.objectInfo);
                if (PersonInfoService.validatePersonInfo(personInfo)) {
                    TaskWorkflowService.deleteTask(rowEntity.object_id_s).then(function(personInfo) {
                        gridHelper.deleteRow(rowEntity);
                        $scope.$emit("report-object-updated", personInfo);
                        return taskInfo;
                    });
                }
            };

            Authentication.queryUserInfo().then(function(userInfo) {
                $scope.userInfo = userInfo;
                $scope.userId = userInfo.userId;
                return userInfo;
            });

            $scope.isDeleteDisabled = function(rowEntity) {
                return ((Util.isEmpty(rowEntity.assignee_id_lcs) || (rowEntity.assignee_id_lcs !== $scope.userId)) || (rowEntity.status_lcs === "CLOSED") || (!rowEntity.adhocTask_b));
            };

        } ]);
