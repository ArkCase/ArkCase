'use strict';

angular.module('tasks').controller('Tasks.ActionsController', ['$scope', '$state', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'Authentication'
    , 'Task.InfoService', 'Task.WorkflowService', 'Object.SubscriptionService', 'ObjectService'
    , function ($scope, $state, $stateParams, $modal
        , Util, ConfigService, Authentication
        , TaskInfoService, TaskWorkflowService, ObjectSubscriptionService, ObjectService) {

        ConfigService.getComponentConfig("tasks", "actions").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        $scope.$on('object-updated', function (e, data) {
            updateData(data);
        });

        $scope.$on('object-refreshed', function (e, data) {
            updateData(data);
        });

        var updateData = function (data) {
            if (!TaskInfoService.validateTaskInfo(data)) {
                return;
            }
            $scope.taskInfo = data;

            $scope.showBtnSignature = false;
            $scope.showBtnDelete = false;
            $scope.showBtnComplete = false;
            $scope.showBtnReject = false;
            $scope.showBtnOutcomes = false;
            if (!Util.isEmpty($scope.taskInfo.assignee)) {
                if (Util.compare($scope.userId, $scope.taskInfo.assignee)) {
                    if ($scope.taskInfo.adhocTask) {
                        if (!Util.goodValue($scope.taskInfo.completed, false)) {
                            $scope.showBtnSignature = true;
                            $scope.showBtnDelete = true;
                            $scope.showBtnComplete = true;
                        }

                        if (!Util.isEmpty($scope.taskInfo.owner) && !Util.isEmpty($scope.taskInfo.assignee)) {
                            if (($scope.taskInfo.owner != $scope.taskInfo.assignee)) {
                                $scope.showBtnSignature = true;
                                $scope.showBtnReject = true;
                            }
                        }

                    } else {
                        if (!Util.goodValue($scope.taskInfo.completed, false)) {
                            $scope.showBtnSignature = true;
                            $scope.showBtnOutcomes = true;
                        }
                    }
                }
            }
        };


        //
        //jwu: no need to show/hide subscribe button any more?
        //
        //    promiseQueryUser.then(function (userInfo) {
        //        $scope.userId = userInfo.userId;
        //        ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.TASK, $scope.taskInfo.taskId).then(function (subscriptions) {
        //            var found = _.find(subscriptions, {
        //                userId: userInfo.userId,
        //                subscriptionObjectType: ObjectService.ObjectTypes.TASK,
        //                objectId: $scope.taskInfo.taskId
        //            });
        //            $scope.showBtnSubscribe = Util.isEmpty(found);
        //            $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
        //        });
        //        return userInfo;
        //    });
        //});


        //$scope.availableOutcomes0 = [{name: "APPROVE", description: "Approve Document", fields: ["value", "message"]}
        //    , {name: "SEND_FOR_REWORK", description: "Send for Rework", fields: ["reworkInstructions"]}
        //];


        $scope.sign = function () {
            var modalInstance = $modal.open({
                templateUrl: "modules/tasks/views/components/task-signature.dialog.html",
                controller: 'Tasks.SignatureDialogController',
                resolve: {
                    aValue: function () {
                        return "some value";
                    }
                }
            });
            modalInstance.result.then(function (result) {
                if (result) {
                    console.log("sign task here");
                }
            });
        };
        $scope.subscribe = function () {
            ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.TASK, $scope.taskInfo.taskId).then(function (data) {
                $scope.showBtnSubscribe = false;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };

        //
        //jwu: no need to have unsubscribe ?
        //
        //$scope.unsubscribe = function () {
        //    ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.TASK, $scope.taskInfo.taskId).then(function (data) {
        //        $scope.showBtnSubscribe = true;
        //        $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
        //        return data;
        //    });
        //};

        $scope.delete = function () {
            var taskInfo = Util.omitNg($scope.taskInfo);
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
            //var taskInfo = Util.omitNg($scope.taskInfo);
            //if (TaskInfoService.validateTaskInfo(taskInfo)) {
            if (Util.goodMapValue($scope.taskInfo, "taskId", false)) {
                TaskWorkflowService.completeTask($scope.taskInfo.taskId).then(
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
            var taskInfo = Util.omitNg($scope.taskInfo);
            if (TaskInfoService.validateTaskInfo(taskInfo)) {
                TaskWorkflowService.completeTaskWithOutcome(taskInfo, name).then(
                    function (taskInfo) {
                        $scope.$emit("report-object-updated", taskInfo);
                        return taskInfo;
                    }
                );
            }
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

    }
]);


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
angular.module('tasks').controller('Tasks.SignatureDialogController', ['$scope', '$modalInstance', 'aValue',
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