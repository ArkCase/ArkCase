'use strict';

angular.module('tasks').controller('Tasks.ActionsController', ['$scope', '$state', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'Authentication'
    , 'Task.InfoService', 'Task.WorkflowService', 'Object.SubscriptionService', 'ObjectService'
    , 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $modal
        , Util, ConfigService, Authentication
        , TaskInfoService, TaskWorkflowService, ObjectSubscriptionService, ObjectService
        , HelperObjectBrowserService) {

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


        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            var promiseQueryUser = Authentication.queryUserInfo();

            promiseQueryUser.then(function (userInfo) {

                $scope.showBtnSignature = false;
                $scope.showBtnDelete = false;
                $scope.showBtnComplete = false;
                $scope.showBtnReject = false;
                $scope.showBtnOutcomes = false;
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


                $scope.userId = userInfo.userId;
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