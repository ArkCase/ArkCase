'use strict';

angular.module('cases').controller(
        'Cases.TasksController',
        [ '$scope', '$state', '$stateParams', '$q', '$translate', 'UtilService', 'ConfigService', 'ObjectService', 'Object.TaskService', 'Task.WorkflowService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Case.InfoService', 'Task.AlertsService', 'ModalDialogService', 'PermissionsService', '$timeout', 'Authentication',
                function($scope, $state, $stateParams, $q, $translate, Util, ConfigService, ObjectService, ObjectTaskService, TaskWorkflowService, HelperUiGridService, HelperObjectBrowserService, CaseInfoService, TaskAlertsService, ModalDialogService, PermissionsService, $timeout, Authentication) {

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cases",
                        componentId: "tasks",
                        retrieveObjectInfo: CaseInfoService.getCaseInfo,
                        validateObjectInfo: CaseInfoService.validateCaseInfo,
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

                    $scope.childDocumentSearch = {
                        parentType: ObjectService.ObjectTypes.CASE_FILE,
                        childTypes:["TIMESHEET", "COSTSHEET"],
                        startRow: Util.goodValue($scope.start, 0),
                        maxRows: Util.goodValue($scope.pageSize, 10)
                    };

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

                        componentHelper.doneConfig(config);

                        return false;
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        retrieveGridData();
                        PermissionsService.getActionPermission('addTasksToCase', objectInfo, {
                            objectType: ObjectService.ObjectTypes.CASE_FILE
                        }).then(function(result) {
                            $scope.isReadOnly = !result;
                        });
                    };

                    var retrieveGridData = function() {
                        var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
                        $scope.childDocumentSearch.parentId = currentObjectId;
                        $scope.childDocumentSearch.startRow = Util.goodValue($scope.start, 0);
                        $scope.childDocumentSearch.maxRows = Util.goodValue($scope.pageSize, 10);
                        if (Util.goodPositive(currentObjectId, false)) {
                            
                            ObjectTaskService.resetChildTasks(ObjectService.ObjectTypes.CASE_FILE, currentObjectId);
                            
                            CaseInfoService.queryCaseTasks($scope.objectInfo.id, $scope.childDocumentSearch, Util.goodMapValue($scope.sort, "by"), Util.goodMapValue($scope.sort, "dir")).then(function(data) {
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
                        var modalParams = {};
                        modalParams.parentType = ObjectService.ObjectTypes.CASE_FILE;
                        modalParams.parentObject = $scope.objectInfo.caseNumber;
                        modalParams.parentId = $scope.objectInfo.id;
                        modalParams.parentTitle = $scope.objectInfo.title;
                        modalParams.taskType = 'ACM_TASK';

                        var modalMetadata = {
                            moduleName: "tasks",
                            templateUrl: "modules/tasks/views/components/task-new-task.client.view.html",
                            controllerName: "Tasks.NewTaskController",
                            params: modalParams
                        };
                        ModalDialogService.showModal(modalMetadata).then(function (value) {
                            $timeout(function() {
                                retrieveGridData();
                                //3 seconds delay so solr can index the new task
                            }, 3000);
                        });

                    };

                    $scope.deleteRow = function(rowEntity) {
                        var caseInfo = Util.omitNg($scope.objectInfo);
                        if (CaseInfoService.validateCaseInfo(caseInfo)) {
                            TaskWorkflowService.deleteTask(rowEntity.object_id_s).then(function(caseInfo) {
                                gridHelper.deleteRow(rowEntity);
                                $scope.$emit("report-object-updated", caseInfo);
                                return caseInfo;
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

                    $scope.onClickObjLink = function(event, rowEntity) {
                        event.preventDefault();
                        var targetType = (Util.goodMapValue(rowEntity, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                        //var targetType = Util.goodMapValue(rowEntity, "object_type_s");
                        var targetId = Util.goodMapValue(rowEntity, "object_id_s");
                        gridHelper.showObject(targetType, targetId);
                    };

                    //$scope.$on("object-refreshed", function (e, objectId) {
                    //    ObjectTaskService.resetChildTasks(ObjectService.ObjectTypes.CASE_FILE, $scope.objectInfo.id);
                    //});
                } ]);
