'use strict';

angular.module('request-info').controller(
        'RequestInfo.ApprovalRoutingController',
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
                'LookupService',
                'Object.LookupService',
                'Case.InfoService',
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
                function($scope, $stateParams, $q, $translate, $modal, Util, UtilDateService, ConfigService, ObjectService, LookupService, ObjectLookupService, CaseInfoService, HelperUiGridService, HelperObjectBrowserService, Authentication, TaskWorkflowService, PermissionsService, UserInfoService,
                        ObjectTaskService, TaskInfoService, ObjectModelService, MessageService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "request-info",
                        componentId: "approvalrouting",
                        retrieveObjectInfo: CaseInfoService.getCaseInfo,
                        validateObjectInfo: CaseInfoService.validateCaseInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var promiseUser = Authentication.queryUserInfo();

                    $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;

                    var onObjectInfoRetrieved = function(objectInfo) {
                        var currentObjectId = Util.goodMapValue(objectInfo, "id");
                        if (Util.goodPositive(currentObjectId, false)) {
                            //we can change this code with making backend service to return the task and make only one call to server
                            ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.CASE_FILE, currentObjectId, 0, 100, '', '').then(function(data) {
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
                                        $scope.owningGroup = ObjectModelService.getGroup($scope.objectInfo);

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

                    $scope.$bus.subscribe('CHILD_OBJECT_OUTCOME_CLICKED', function(name) {
                        var taskInfo = Util.omitNg($scope.objectInfo);
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
