'use strict';

angular.module('request-info').controller(
        'RequestInfo.TasksController',
    ['$scope', '$state', '$stateParams', '$q', '$translate', '$timeout', 'UtilService', 'ConfigService', 'ObjectService', 'Object.TaskService', 'Task.WorkflowService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Case.InfoService', 'ModalDialogService',
        function ($scope, $state, $stateParams, $q, $translate, $timeout, Util, ConfigService, ObjectService, ObjectTaskService, TaskWorkflowService, HelperUiGridService, HelperObjectBrowserService, CaseInfoService, ModalDialogService) {

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "request-info",
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

                    $scope.childDocumentSearch = {
                        parentType: ObjectService.ObjectTypes.CASE_FILE,
                        childTypes:["TIMESHEET", "COSTSHEET"],
                        startRow: Util.goodValue($scope.start, 0),
                        maxRows: Util.goodValue($scope.pageSize, 10)
                    };

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });
                    var promiseUsers = gridHelper.getUsers();

                    var onConfigRetrieved = function(config) {
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                        gridHelper.setExternalPaging(config, retrieveGridData);
                        gridHelper.setUserNameFilterToConfig(promiseUsers);

                        //default (init) sorting of tasks grid
                        $scope.sort.by = "create_date_tdt";
                        $scope.sort.dir = "desc";

                        componentHelper.doneConfig(config);

                        return false;
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        retrieveGridData();
                    };

                    var retrieveGridData = function() {
                        var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
                        $scope.childDocumentSearch.parentId = currentObjectId;
                        if (Util.goodPositive(currentObjectId, false)) {
                            CaseInfoService.queryCaseTasks($scope.objectInfo.id, $scope.childDocumentSearch, Util.goodMapValue($scope.sort, "by"), Util.goodMapValue($scope.sort, "dir")).then(function(data) {
                                var tasks = data.response.docs;
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
                                parentType: ObjectService.ObjectTypes.CASE_FILE,
                                parentObject: $scope.objectInfo.caseNumber,
                                parentId: $scope.objectInfo.id,
                                parentTitle: $scope.objectInfo.title,
                                returnState: $state.current,
                                taskType: 'ACM_TASK'
                            }
                        };
                        ModalDialogService.showModal(modalMetadata).then(function() {
                            $timeout(function() {
                                retrieveGridData();
                                //4 seconds delay so solr can index the new task
                            }, 4000);                            
                        });
                    };

                    $scope.onClickObjLink = function(event, rowEntity) {
                        event.preventDefault();
                        var targetType = (Util.goodMapValue(rowEntity, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                        //var targetType = Util.goodMapValue(rowEntity, "object_type_s");
                        var targetId = Util.goodMapValue(rowEntity, "object_id_s");
                        gridHelper.showObject(targetType, targetId, true);
                    };
                } ]);