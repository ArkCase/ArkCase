'use strict';

angular.module('tasks').controller('Tasks.ActionsController', ['$scope', '$state', '$modal', 'UtilService', 'Authentication', 'Task.InfoService', 'Task.WorkflowService',
    function ($scope, $state, $modal, Util, Authentication, TaskInfoService, TaskWorkflowService) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        Authentication.queryUserInfoNew().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        $scope.$on('task-updated', function (e, data) {
            $scope.taskInfo = data;

            $scope.showBtnESignature = false;
            $scope.showBtnDelete = false;
            $scope.showBtnComplete = false;
            $scope.showBtnReject = false;
            $scope.showBtnOutcomes = false;
            if (!Util.isEmpty($scope.taskInfo.assignee)) {
                if (Util.compare($scope.userId, $scope.taskInfo.assignee)) {
                    if ($scope.taskInfo.adhocTask) {
                        if (!Util.goodValue($scope.taskInfo.completed, false)) {
                            $scope.showBtnESignature = true;
                            $scope.showBtnDelete = true;
                            $scope.showBtnComplete = true;
                        }

                        if (!Util.isEmpty($scope.taskInfo.owner) && !Util.isEmpty($scope.taskInfo.assignee)) {
                            if (($scope.taskInfo.owner != $scope.taskInfo.assignee)) {
                                $scope.showBtnESignature = true;
                                $scope.showBtnReject = true;
                            }
                        }

                    } else {
                        if (!Util.goodValue($scope.taskInfo.completed, false)) {
                            $scope.showBtnESignature = true;
                            $scope.showBtnOutcomes = true;
                        }
                    }
                }
            }
        });

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
            console.log('subscribe');
        };
        $scope.delete = function () {
            var taskInfo = Util.omitNg($scope.taskInfo);
            if (TaskInfoService.validateTaskInfo(taskInfo)) {
                TaskWorkflowService.deleteTask(taskInfo).then(
                    function (taskInfo) {
                        $scope.$emit("report-task-updated", taskInfo);
                        return taskInfo;
                    }
                );
            }
        };
        $scope.complete = function () {
            var taskInfo = Util.omitNg($scope.taskInfo);
            if (TaskInfoService.validateTaskInfo(taskInfo)) {
                TaskWorkflowService.completeTask(taskInfo).then(
                    function (taskInfo) {
                        $scope.$emit("report-task-updated", taskInfo);
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
                        $scope.$emit("report-task-updated", taskInfo);
                        return taskInfo;
                    }
                );
            }
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