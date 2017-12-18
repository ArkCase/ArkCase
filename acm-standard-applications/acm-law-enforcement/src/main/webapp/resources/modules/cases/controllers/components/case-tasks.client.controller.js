'use strict';

angular.module('cases').controller('Cases.TasksController', ['$scope', '$state', '$stateParams', '$q', '$translate'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.TaskService', 'Task.WorkflowService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Case.InfoService', 'Task.AlertsService', 'ModalDialogService'
    , function ($scope, $state, $stateParams, $q, $translate
        , Util, ConfigService, ObjectService, ObjectTaskService, TaskWorkflowService
        , HelperUiGridService, HelperObjectBrowserService, CaseInfoService, TaskAlertsService, ModalDialogService) {

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
            gridHelper.addButton(config, "delete");

            componentHelper.doneConfig(config);

            return false;
        };


        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            retrieveGridData();
        };

        var retrieveGridData = function () {
            var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
            var exceptDeleteOnly = true;
            if (Util.goodPositive(currentObjectId, false)) {
                ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.CASE_FILE
                    , currentObjectId
                    , Util.goodValue($scope.start, 0)
                    , exceptDeleteOnly
                    , Util.goodValue($scope.pageSize, 10)
                    , Util.goodValue($scope.sort.by)
                    , Util.goodValue($scope.sort.dir)
                ).then(function (data) {
                    var tasks = data.response.docs;
                    angular.forEach(tasks,function (task) {
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

        $scope.addNew = function () {
            var modalMetadata = {
                moduleName: "tasks",
                templateUrl: "modules/tasks/views/components/task-new-task.client.view.html",
                controllerName: "Tasks.NewTaskController",
                params: {
                    parentType: ObjectService.ObjectTypes.CASE_FILE,
                    parentObject: $scope.objectInfo.caseNumber,
                    parentId: $scope.objectInfo.id,
                    parentTitle: $scope.objectInfo.title,
                    taskType: 'ACM_TASK'
                }
            };
            ModalDialogService.showModal(modalMetadata);
        };

        $scope.deleteRow = function (rowEntity) {
            var caseInfo = Util.omitNg($scope.objectInfo);
            if (CaseInfoService.validateCaseInfo(caseInfo))
            {
                TaskWorkflowService.deleteTask(rowEntity.object_id_s).then(
                    function (caseInfo) {
                        $scope.$emit("report-object-updated", caseInfo);
                        return caseInfo;
                    }
                );
            }
            gridHelper.deleteRow(rowEntity);
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