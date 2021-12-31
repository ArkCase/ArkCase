'use strict';

angular.module('complaints').controller(
        'Complaints.ApprovalRoutingController',
        [
                '$scope',
                '$stateParams',
                '$q',
                '$translate',
                '$modal',
                'UtilService',
                'Util.DateService',
                'ConfigService',
                'ObjectService',
                'Complaint.InfoService',
                'Helper.UiGridService',
                'Helper.ObjectBrowserService',
                'Authentication',
                'Task.WorkflowService',
                'PermissionsService',
                'Profile.UserInfoService',
                'Object.TaskService',
                'Task.InfoService',
                'Object.ModelService',
                'MessageService',
                function($scope, $stateParams, $q, $translate, $modal, Util, UtilDateService, ConfigService, ObjectService, ComplaintInfoService, HelperUiGridService, HelperObjectBrowserService, Authentication, TaskWorkflowService, PermissionsService, UserInfoService, ObjectTaskService,
                        TaskInfoService, ObjectModelService, MessageService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "complaints",
                        componentId: "approvalRouting",
                        retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var promiseUser = Authentication.queryUserInfo();

                    $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;

                    $scope.isTask = function(objectInfo) {
                        return !Util.isEmpty(objectInfo) && objectInfo.hasOwnProperty('taskId') && objectInfo.hasOwnProperty('adhocTask');
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.owningGroup = '';
                        var currentObjectId = Util.goodMapValue(objectInfo, "complaintId");
                        if (!$scope.isTask(objectInfo)) {
                            $scope.objectInfo = {
                                'id': currentObjectId
                            };
                            $scope.dateInfo = null;
                        }
                        if (Util.goodPositive(currentObjectId, false)) {
                            //we can change this code with making backend service to return the task and make only one call to server
                            ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.COMPLAINT, currentObjectId, 0, 100, '', '').then(function(data) {
                                var tasks = data.response.docs;
                                var objectId = _.result(_.find(tasks, function(task) {
                                    return task.status_lcs === 'ACTIVE' && task.business_process_name_lcs === 'ArkCase Buckslip Process';
                                }), 'object_id_s');
                                if (!Util.isEmpty(objectId)) {
                                    TaskInfoService.getTaskInfo(objectId).then(function(taskInfo) {
                                        $scope.$bus.publish('buckslip-task-object-updated', taskInfo);

                                        $scope.objectInfo = taskInfo;
                                        $scope.dateInfo = $scope.dateInfo || {};
                                        $scope.dateInfo.dueDate = UtilDateService.isoToLocalDateTime($scope.objectInfo.dueDate);
                                        $scope.dateInfo.taskStartDate = UtilDateService.isoToLocalDateTime($scope.objectInfo.taskStartDate);
                                        $scope.assignee = ObjectModelService.getAssignee($scope.objectInfo);

                                        if (!Util.isEmpty(ObjectModelService.getGroup($scope.objectInfo))) {
                                            $scope.owningGroup = ObjectModelService.getGroup($scope.objectInfo);
                                        } else if (Util.goodMapValue($scope.objectInfo, "candidateGroups[0]", false)) {
                                            $scope.owningGroup = $scope.objectInfo.candidateGroups[0];
                                        }

                                        //we should wait for userId before we compare it with assignee
                                        promiseUser.then(function(data) {
                                            $scope.userId = data.userId;

                                            if (!Util.isEmpty($scope.objectInfo.assignee)) {
                                                if (Util.compare($scope.userId, $scope.objectInfo.assignee)) {

                                                    if (!Util.goodValue($scope.objectInfo.completed, false)) {
                                                        $scope.$bus.publish('CHILD_OBJECT_OUTCOMES_FOUND', $scope.objectInfo.availableOutcomes);
                                                    }
                                                }
                                            }
                                        });
                                    });
                                }
                            });
                        }
                    };

                    $scope.$bus.subscribe('buckslip-process-refresh', function(objectInfo) {
                        onObjectInfoRetrieved(objectInfo);
                    });

                    $scope.$bus.subscribe('buckslip-task-object-updated-subscribe-created', function(created) {
                        if ($scope.objectInfo && created) {
                            $scope.$bus.publish('buckslip-task-object-updated', $scope.objectInfo);
                        }
                    });

                    $scope.$bus.subscribe('object.changed/' + $stateParams.type + '/' + $stateParams.id, function(data) {
                        $scope.$emit('report-object-refreshed', $stateParams.id);
                    });

                    $scope.$bus.subscribe('CHILD_OBJECT_OUTCOME_CLICKED', function(name) {
                        var taskInfo = Util.omitNg($scope.objectInfo);

                        //QUICK FIX TO BE REMOVED AFTER THE DEMO
                        //REMOVES THE MILLISECONDS AFTER THE DOT(.) AND ADD "Z" for UTC
                        //DATETIME BEFORE: "2017-10-04T22:00:00.000+0000" DATETIME AFTER: "2017-10-04T22Z"

                        if (!Util.isEmpty(taskInfo.buckslipFutureApprovers) && taskInfo.buckslipFutureApprovers.length > 0) {
                            for (var i = 0; i < taskInfo.buckslipFutureApprovers.length; i++) {
                                taskInfo.buckslipFutureApprovers[i].created = taskInfo.buckslipFutureApprovers[i].created.split('.')[0] + "Z";
                                taskInfo.buckslipFutureApprovers[i].modified = taskInfo.buckslipFutureApprovers[i].modified.split('.')[0] + "Z";

                                if (!Util.isEmpty(taskInfo.buckslipFutureApprovers[i].deleted)) {
                                    taskInfo.buckslipFutureApprovers[i].deleted = taskInfo.buckslipFutureApprovers[i].deleted.split('.')[0] + "Z";
                                }
                            }
                        }

                        if (TaskInfoService.validateTaskInfo(taskInfo)) {
                            TaskWorkflowService.completeTaskWithOutcome(taskInfo, name).then(function(taskInfo) {
                                $scope.$emit('report-object-refreshed', $stateParams.id);
                                MessageService.succsessAction();
                                return taskInfo;
                            }, function(error) {
                                MessageService.errorAction();
                                return error;
                            });
                        }
                    });

                    $scope.onClickTitle = function() {
                        ObjectService.showObject(ObjectService.ObjectTypes.TASK, $scope.objectInfo.taskId);
                    };
                }

        ]);