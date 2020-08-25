'use strict';

angular.module('tasks').controller(
    'Tasks.PeopleController',
    [ '$scope', '$q', '$stateParams', '$translate', '$modal', 'UtilService', 'Object.TaskService', 'Task.InfoService', 'Authentication', 'Object.LookupService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Person.InfoService', 'Task.PeopleService',
        function($scope, $q, $stateParams, $translate, $modal, Util, ObjectTaskService, TaskInfoService, Authentication, ObjectLookupService, HelperUiGridService, HelperObjectBrowserService, PersonInfoService, TaskPeopleService) {

            var personTypeCreator = 'Creator';

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "tasks",
                componentId: "people",
                retrieveObjectInfo: TaskInfoService.getTaskInfo,
                validateObjectInfo: TaskInfoService.validateTaskInfo,
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

                gridHelper.setUserNameFilterToConfig(promiseUsers, config).then(function(updatedConfig) {
                    $scope.config = updatedConfig;
                    if ($scope.gridApi != undefined)
                        $scope.gridApi.core.refresh();
                    gridHelper.addButton(updatedConfig, "delete", null, null, "isDeleteDisabled");
                    gridHelper.setColumnDefs(updatedConfig);
                    gridHelper.setBasicOptions(updatedConfig);
                    gridHelper.disableGridScrolling(updatedConfig);
                });
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                TaskPeopleService.findPeople(objectInfo.taskId).then(function (data) {
                    $scope.gridOptions.data = data;
                    $scope.gridOptions.totalItems = data.length;
                })
            };


            $scope.deleteRow = function(rowEntity) {
                var id = Util.goodMapValue(rowEntity, "id", 0);
                _.remove($scope.objectInfo.personAssociations, function(item) {
                    return item === rowEntity;
                });
                if (rowEntity.id) {
                    saveObjectInfoAndRefresh();
                }
            };

            $scope.editRow = function(rowEntity) {
                pickPerson(rowEntity);
            };

            function saveObjectInfoAndRefresh() {
                var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                if (TaskInfoService.validateCaseInfo($scope.objectInfo)) {
                    var objectInfo = Util.omitNg($scope.objectInfo);
                    promiseSaveInfo = TaskInfoService.saveTaskInfo(objectInfo);
                    promiseSaveInfo.then(function(objectInfo) {
                        $scope.$emit("report-object-updated", objectInfo);
                        return objectInfo;
                    }, function(error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    });
                }
                return promiseSaveInfo;
            }

            $scope.isDeleteDisabled = function(rowEntity) {
                return rowEntity.personType == personTypeCreator;
            };
        } ]);
