'use strict';

angular.module('tasks').controller('Tasks.ActionsController', ['$scope', '$state', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'Authentication'
    , 'Task.InfoService', 'Task.WorkflowService', 'Object.SubscriptionService', 'Object.SignatureService', 'ObjectService'
    , 'Helper.ObjectBrowserService', '$translate'
    , function ($scope, $state, $stateParams, $modal
        , Util, ConfigService, Authentication
        , TaskInfoService, TaskWorkflowService, ObjectSubscriptionService, ObjectSignatureService, ObjectService
        , HelperObjectBrowserService, $translate) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "tasks"
            , componentId: "actions"
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var promiseQueryUser = Authentication.queryUserInfo();

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            $scope.showBtnSignature = false;
            $scope.showBtnDelete = false;
            $scope.showBtnComplete = false;
            $scope.showBtnReject = false;
            $scope.showBtnOutcomes = false;

            promiseQueryUser.then(function (userInfo) {
                $scope.userId = userInfo.userId;
                $scope.userInfo = userInfo;

                //we should wait for userId before we compare it with assignee
                if (!Util.isEmpty($scope.objectInfo.assignee)) {
                    if (Util.compare($scope.userId, $scope.objectInfo.assignee)) {
                        if ($scope.objectInfo.adhocTask) {
                            if (!Util.goodValue($scope.objectInfo.completed, false)) {
                                $scope.showBtnSignature = true;
                                $scope.showBtnDelete = true;
                                $scope.showBtnComplete = true;
                            }

                            if (!Util.isEmpty($scope.objectInfo.owner) && !Util.isEmpty($scope.objectInfo.assignee)) {
                                if (($scope.objectInfo.owner != $scope.objectInfo.assignee)) {
                                    $scope.showBtnSignature = true;
                                    $scope.showBtnReject = true;
                                }
                            }

                        } else {
                            if (!Util.goodValue($scope.objectInfo.completed, false)) {
                                $scope.showBtnSignature = true;
                                $scope.showBtnOutcomes = true;
                            }
                        }
                    }
                }

                ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.TASK, $scope.objectInfo.taskId).then(function (subscriptions) {
                    var found = _.find(subscriptions, {
                        userId: userInfo.userId,
                        subscriptionObjectType: ObjectService.ObjectTypes.TASK,
                        objectId: $scope.objectInfo.taskId
                    });
                    $scope.showBtnSubscribe = Util.isEmpty(found);
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                });
                return userInfo;
            });
        };


        //$scope.availableOutcomes0 = [{name: "APPROVE", description: "Approve Document", fields: ["value", "message"]}
        //    , {name: "SEND_FOR_REWORK", description: "Send for Rework", fields: ["reworkInstructions"]}
        //];


        $scope.sign = function () {
            var modalInstance = $modal.open({
                templateUrl: "modules/tasks/views/components/task-signature.dialog.html",
                controller: 'Tasks.SignatureDialogController'
            });
            modalInstance.result.then(function (result) {
                if (result) {
                    console.log("sign task here");
                    ObjectSignatureService.confirmSignature(ObjectService.ObjectTypes.TASK, $scope.objectInfo.taskId, result.pass);
                }
            });
        };
        $scope.subscribe = function () {
            ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.TASK, $scope.objectInfo.taskId).then(function (data) {
                $scope.showBtnSubscribe = false;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };

        $scope.unsubscribe = function () {
            ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.TASK, $scope.objectInfo.taskId).then(function (data) {
                $scope.showBtnSubscribe = true;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };

        $scope.delete = function () {
            var taskInfo = Util.omitNg($scope.objectInfo);
            if (TaskInfoService.validateTaskInfo(taskInfo)) {
                TaskWorkflowService.deleteTask(taskInfo.taskId).then(
                    function (taskInfo) {
                        $scope.$emit("report-object-updated", taskInfo);
                        return taskInfo;
                    }
                );
            }
        };
        $scope.complete = function () {
            //var taskInfo = Util.omitNg($scope.objectInfo);
            //if (TaskInfoService.validateTaskInfo(taskInfo)) {
            if (Util.goodMapValue($scope.objectInfo, "taskId", false)) {
                TaskWorkflowService.completeTask($scope.objectInfo.taskId).then(
                    function (taskInfo) {
                        $scope.$emit("report-object-updated", taskInfo);
                        return taskInfo;
                    }
                );
            }
        };
        $scope.reject = function () {
            var modalInstance = $modal.open({
                templateUrl: "modules/tasks/views/components/task-reject.dialog.html",
                controller: 'Tasks.SignatureDialogController',
                resolve: {
                    aValue: function () {
                        return "some value";
                    }
                }
            });
            modalInstance.result.then(function (result) {
                if (result) {
                    console.log("reject task here");
                }
            });
        };
        $scope.onClickOutcome = function (name) {
            var taskInfo = Util.omitNg($scope.objectInfo);
            if (TaskInfoService.validateTaskInfo(taskInfo)) {
                TaskWorkflowService.completeTaskWithOutcome(taskInfo, name).then(
                    function (taskInfo) {
                        $scope.$emit("report-object-updated", taskInfo);
                        if (taskInfo.pendingStatus != null && taskInfo.pendingStatus == "DELETED") {
                            // wait solr to index the change, and update the tree i.e. remove task from tree
                            setTimeout(function () {
                                $scope.$emit("report-tree-updated", taskInfo);
                            }, 4000);
                        }
                        return taskInfo;
                    },
                    function (error) {
                        $scope.showErrorDialog(error);
                    }
                );
            }
        };

        $scope.showClaimBtn = function () {
            var isClosedStatus = Util.compare(Util.goodMapValue($scope.objectInfo, 'status'), "CLOSED") || Util.compare(Util.goodMapValue($scope.objectInfo, 'status'), "TERMINATED");
            if ($scope.objectInfo && !Util.goodMapValue($scope.objectInfo, "assignee", false)) {
                if (Util.goodMapValue($scope.objectInfo, "candidateGroups", false) && !Util.isArrayEmpty($scope.objectInfo.candidateGroups)) {
                    if (Util.goodMapValue($scope.userInfo, "authorities", false) && !Util.isArrayEmpty($scope.userInfo.authorities)) {
                        if (_.includes($scope.userInfo.authorities, "ROLE_ADMINISTRATOR")) {
                            //Admin users can claim any group task.
                            return !isClosedStatus;
                        }
                        if (!Util.isArrayEmpty(_.intersection($scope.objectInfo.candidateGroups, $scope.userInfo.authorities))) {
                            return !isClosedStatus;
                        }
                    }
                }
            }
            return false;
        };

        $scope.claimTask = function () {
            if (Util.goodMapValue($scope.objectInfo, "taskId", false)) {
                TaskWorkflowService.claimTask($scope.objectInfo.taskId).then(
                    function (taskInfo) {
                        TaskInfoService.resetTaskCacheById(taskInfo.taskId);
                        return TaskInfoService.getTaskInfo(taskInfo.taskId);
                    }
                ).then(function (taskInfoUpdated) {
                    saveTask(taskInfoUpdated).then(function (taskInfoUpdated) {
                        $scope.refresh();
                        return taskInfoUpdated;
                    }, function (error) {
                        //Ignore a failed save here as the claim will take care of modifying Activiti Task.
                        //Error is caused by a participant data integrity issue that occurs every so often.
                        $scope.$emit("report-object-updated", taskInfoUpdated);
                        return taskInfoUpdated;
                    });
                });
            }
        };

        $scope.showUnclaimBtn = function () {
            if ($scope.objectInfo && Util.goodMapValue($scope.objectInfo, "assignee", false)) {
                if (Util.goodMapValue($scope.userInfo, "authorities", false) && !Util.isArrayEmpty($scope.userInfo.authorities)) {
                    if (_.includes($scope.userInfo.authorities, "ROLE_ADMINISTRATOR")) {
                        //Admin users can u3n-claim any group task.
                        return true;
                    }
                    else if (!Util.isArrayEmpty(_.intersection($scope.objectInfo.candidateGroups, $scope.userInfo.authorities))) {
                        return true;
                    }
                }
            }
            return false;
        };

        $scope.unclaimTask = function () {
            if (Util.goodMapValue($scope.objectInfo, "taskId", false)) {
                TaskWorkflowService.unclaimTask($scope.objectInfo.taskId).then(
                    function (taskInfo) {
                        $scope.$emit("report-object-updated", taskInfo);
                        return taskInfo;
                    }
                    , function (error) {
                        return error;
                    }
                );
            }
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        var saveTask = function (taskInfoUpdated) {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (TaskInfoService.validateTaskInfo($scope.objectInfo)) {
                var objectInfo = taskInfoUpdated == null ? Util.omitNg($scope.objectInfo) : Util.omitNg(taskInfoUpdated);
                TaskInfoService.saveTaskInfo(objectInfo).then(function (taskInfo) {
                    $scope.$emit("report-object-updated", taskInfo);
                    return taskInfo;
                });
            }
            return promiseSaveInfo;
        };

        $scope.showErrorDialog = function (error) {
            $modal.open({
                animation: true,
                templateUrl: 'modules/tasks/views/components/task-actions-error-dialog.client.view.html',
                controller: 'Tasks.ActionsErrorDialogController',
                backdrop: 'static',
                resolve: {
                    errorMessage: function () {
                        return error;
                    }
                }
            });
        }
    }
]);
angular.module('tasks').controller('Tasks.ActionsErrorDialogController', ['$scope', '$modalInstance', 'errorMessage',
        function ($scope, $modalInstance, errorMessage) {
            $scope.errorMessage = errorMessage;
            $scope.onClickOk = function () {
                $modalInstance.dismiss('cancel');
            };
        }
    ]
);
angular.module('tasks').controller('Tasks.RejectDialogController', ['$scope', '$modalInstance', 'aValue',
        function ($scope, $modalInstance, aValue) {
            $scope.valuePassed = aValue;
            $scope.onClickCancel = function () {
                $modalInstance.close(false);
            };
            $scope.onClickOk = function () {
                $modalInstance.close(true);
            };
        }
    ]
);
angular.module('tasks').controller('Tasks.SignatureDialogController', ['$scope', '$modalInstance',
        function ($scope, $modalInstance) {
            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close({pass: $scope.password});
            };
        }
    ]
);