'use strict';

angular.module('complaints').controller(
        'Complaints.TasksController',
        [ '$scope', '$state', '$stateParams', '$q', '$translate', 'UtilService', 'ConfigService', 'ObjectService', 'Object.TaskService', 'Task.WorkflowService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Complaint.InfoService', 'Task.AlertsService', 'ModalDialogService',
                function($scope, $state, $stateParams, $q, $translate, Util, ConfigService, ObjectService, ObjectTaskService, TaskWorkflowService, HelperUiGridService, HelperObjectBrowserService, ComplaintInfoService, TaskAlertsService, ModalDialogService) {

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "complaints",
                        componentId: "tasks",
                        retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
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
                            gridHelper.setExternalPaging(updatedConfig, $scope.retrieveGridData);
                        });
                        componentHelper.doneConfig(config);
                        return false;
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        retrieveGridData();
                    };

                    var retrieveGridData = function() {
                        if (Util.goodPositive(componentHelper.currentObjectId, false)) {
                            ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.COMPLAINT, componentHelper.currentObjectId,
                                    Util.goodValue($scope.start, 0), Util.goodValue($scope.pageSize, 10),
                                    Util.goodMapValue($scope.sort, "by"), Util.goodMapValue($scope.sort, "dir")).then(function(data) {
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
                            }); //end then
                        }
                    };

                    $scope.addNew = function() {
                        var modalMetadata = {
                            moduleName: "tasks",
                            templateUrl: "modules/tasks/views/components/task-new-task.client.view.html",
                            controllerName: "Tasks.NewTaskController",
                            params: {
                                parentType: ObjectService.ObjectTypes.COMPLAINT,
                                parentObject: $scope.objectInfo.complaintNumber,
                                parentId: $scope.objectInfo.complaintId,
                                parentTitle: $scope.objectInfo.title,
                                taskType: 'ACM_TASK'
                            }
                        };
                        ModalDialogService.showModal(modalMetadata);
                    };

                    $scope.deleteRow = function(rowEntity) {
                        var complaintInfo = Util.omitNg($scope.objectInfo);
                        if (ComplaintInfoService.validateComplaintInfo(complaintInfo)) {
                            TaskWorkflowService.deleteTask(rowEntity.object_id_s).then(function(complaintInfo) {
                                gridHelper.deleteRow(rowEntity);
                                $scope.$emit("report-object-updated", complaintInfo);
                                return complaintInfo;
                            });
                        }
                    };

                    $scope.onClickObjLink = function(event, rowEntity) {
                        event.preventDefault();

                        var targetType = (Util.goodMapValue(rowEntity, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                        var targetId = Util.goodMapValue(rowEntity, "object_id_s");
                        gridHelper.showObject(targetType, targetId);
                    };

                    $scope.isDeleteDisabled = function(rowEntity) {
                        return ((Util.isEmpty(rowEntity.task_owner_s) || (rowEntity.task_owner_s !== rowEntity.author_s)) || (rowEntity.status_s === "CLOSED"));
                    };

                } ]);
