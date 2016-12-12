'use strict';

angular.module('cases').controller('Cases.TasksController', ['$scope', '$state', '$stateParams', '$q', '$translate'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.TaskService', 'Task.WorkflowService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Case.InfoService'
    , function ($scope, $state, $stateParams, $q, $translate
        , Util, ConfigService, ObjectService, ObjectTaskService, TaskWorkflowService
        , HelperUiGridService, HelperObjectBrowserService, CaseInfoService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "tasks"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, retrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);

            componentHelper.doneConfig(config);

            return false;
        };


        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            retrieveGridData();
        };

        //jwu: for testing, remove meeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
        //$scope.isReadOnly = function (objectInfo) {
        //    return true;
        //};

        var retrieveGridData = function () {
            var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
            if (Util.goodPositive(currentObjectId, false)) {
                ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.CASE_FILE
                    , currentObjectId
                    , Util.goodValue($scope.start, 0)
                    , Util.goodValue($scope.pageSize, 10)
                    , Util.goodValue($scope.sort.by)
                    , Util.goodValue($scope.sort.dir)
                ).then(function (data) {
                    var tasks = data.response.docs;
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = tasks;
                    $scope.gridOptions.totalItems = data.response.numFound;

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

                        if (task.status_s === "ACTIVE" && task.adhocTask_b) {
                            task.acm$_taskOutcomes.push({id: "complete", value: "Complete"});
                            task.acm$_taskOutcomes.push({id: "delete", value: "Delete"});
                            task.acm$_taskActionDone = false;
                        }
                        else if (task.status_s === "ACTIVE" && !task.adhocTask_b && !Util.isArrayEmpty(task.outcome_value_ss)) {
                            var availableOutcomes = Util.goodArray(task.outcome_value_ss);
                            if (availableOutcomes !== undefined && availableOutcomes.length > 0) {
                                for (var j = 0; j < availableOutcomes.length; j++) {
                                    var outcome = {
                                        id: Util.goodValue(availableOutcomes[j]),
                                        value: Util.goodValue(availableOutcomes[j])
                                    };
                                    task.acm$_taskOutcomes.push(outcome);
                                }
                            }
                            task.acm$_taskActionDone = (1 >= availableOutcomes.length); //1 for '(Select One)'
                        }
                    }
                    return data;
                });
            }
        };

        $scope.addNew = function () {
            $state.go("newTaskFromParentObject", {
                parentType: ObjectService.ObjectTypes.CASE_FILE,
                parentObject: $scope.objectInfo.caseNumber,
                parentTitle: $scope.objectInfo.title
            });
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            var targetType = (Util.goodMapValue(rowEntity, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
            //var targetType = Util.goodMapValue(rowEntity, "object_type_s");
            var targetId = Util.goodMapValue(rowEntity, "object_id_s");
            gridHelper.showObject(targetType, targetId);
        };

        //$scope.$on("object-refreshed", function (e, objectId) {
        //    ObjectTaskService.resetChildTasks(ObjectService.ObjectTypes.CASE_FILE, $scope.objectInfo.id);
        //});
    }
]);