'use strict';

angular.module('tasks').controller(
    'Tasks.ActionsController',
    ['$rootScope', '$scope', '$state', '$stateParams', '$modal', '$translate', 'UtilService', 'ConfigService', 'Authentication', 'Object.SignatureService', 'ObjectService', 'Task.InfoService', 'Task.WorkflowService', 'Object.SubscriptionService', 'Helper.ObjectBrowserService',
        'MessageService', 'Ecm.EmailService', function ($rootScope, $scope, $state, $stateParams, $modal, $translate, Util, ConfigService, Authentication, ObjectSignatureService, ObjectService, TaskInfoService, TaskWorkflowService, ObjectSubscriptionService, HelperObjectBrowserService, MessageService, EcmEmailService) {
        new HelperObjectBrowserService.Component({
            scope: $scope,
            stateParams: $stateParams,
            moduleId: "tasks",
            componentId: "actions",
            retrieveObjectInfo: TaskInfoService.getTaskInfo,
            validateObjectInfo: TaskInfoService.validateTaskInfo,
            onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var promiseQueryUser = Authentication.queryUserInfo();

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            // For adhock tasks we don't have business process who can be shown with graphic,
            // so don't show the button
            $scope.showBtnDiagram = !$scope.objectInfo.adhocTask && $scope.objectInfo.businessProcessId != null;
            $scope.showBtnSignature = false;
            $scope.showBtnDelete = false;
            $scope.showBtnComplete = false;
            $scope.showBtnReject = false;
            $scope.showBtnOutcomes = false;
            $scope.showBtnApprove = false;
            $scope.isClicked = false;

            promiseQueryUser.then(function (userInfo) {
                $scope.userId = userInfo.userId;
                $scope.userInfo = userInfo;

                //we should wait for userId before we compare it with assignee
                if (!Util.isEmpty($scope.objectInfo.assignee)) {
                    if (Util.compare($scope.userId, $scope.objectInfo.assignee)) {
                        if ($scope.objectInfo.adhocTask || Util.isArrayEmpty($scope.objectInfo.availableOutcomes)) {
                            if ($scope.objectInfo.businessProcessName == 'acmDocumentSingleTaskWorkflow' && !Util.goodValue($scope.objectInfo.completed, false)) {
                                $scope.showBtnApprove = true;
                            }
                            if (!Util.goodValue($scope.objectInfo.completed, false) && $scope.objectInfo.businessProcessName != 'acmDocumentSingleTaskWorkflow') {
                                $scope.showBtnSignature = true;
                                $scope.showBtnDelete = true;
                                $scope.showBtnComplete = true;
                            }

                            if (!Util.isEmpty($scope.objectInfo.owner) && !Util.isEmpty($scope.objectInfo.assignee)) {
                                if (($scope.objectInfo.owner != $scope.objectInfo.assignee) && $scope.objectInfo.businessProcessName != 'acmDocumentSingleTaskWorkflow'
                                    && $scope.objectInfo.type != 'web-portal-withdrawal' && $scope.objectInfo.type != 'web-portal-inquiry') {
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

        $scope.diagram = function () {
            var modalInstance = $modal.open({
                templateUrl: "modules/tasks/views/components/task-diagram-modal.client.view.html",
                controller: 'Tasks.DiagramModalController',
                windowClass: 'modal-width-80',
                backdrop: 'static',
                resolve: {
                    taskId: function () {
                        return $scope.objectInfo.taskId;
                    },
                    showLoader: function () {
                        return true;
                    },
                    showError: function () {
                        return false;
                    }
                }
            });
            modalInstance.result.then(function (result) {
                if (result) {
                    // Do nothing
                }
            });
        };

        $scope.sign = function () {
            var modalInstance = $modal.open({
                templateUrl: "modules/tasks/views/components/task-signature.dialog.html",
                controller: 'Tasks.SignatureDialogController',
                backdrop: 'static'
            });
            modalInstance.result.then(function (result) {
                if (result) {
                    console.log("sign task here");
                    ObjectSignatureService.confirmSignature(ObjectService.ObjectTypes.TASK, $scope.objectInfo.taskId, result.pass).then(function (result) {
                        MessageService.succsessAction();
                        $rootScope.$broadcast('task-signed');
                    }, function (error) {
                        if (!Util.isEmpty(error.data.message)) {
                            MessageService.error(error.data.message);
                        } else {
                            MessageService.errorAction();
                        }
                    });
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

        $scope.deleteTaskAction = function () {
            var taskInfo = Util.omitNg($scope.objectInfo);
            if (TaskInfoService.validateTaskInfo(taskInfo)) {
                TaskWorkflowService.deleteTask(taskInfo.taskId).then(function (taskInfo) {
                    $scope.$emit("report-object-updated", taskInfo);
                    return taskInfo;
                });
            }
        };
        $scope.complete = function () {
            //var taskInfo = Util.omitNg($scope.objectInfo);
            //if (TaskInfoService.validateTaskInfo(taskInfo)) {
            if (Util.goodMapValue($scope.objectInfo, "taskId", false)) {
                TaskWorkflowService.completeTask($scope.objectInfo.taskId).then(function (taskInfo) {
                    $scope.$emit("report-object-updated", taskInfo);
                    return taskInfo;
                });
            }
        };
        $scope.reject = function () {
            var modalInstance = $modal.open({
                templateUrl: "modules/tasks/views/components/task-reject.dialog.html",
                controller: 'Tasks.SignatureDialogController',
                backdrop: 'static',
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
            $scope.isClicked = true;

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
                TaskWorkflowService.completeTaskWithOutcome(taskInfo, name).then(function (taskInfo) {
                    $scope.$emit("report-object-updated", taskInfo);
                    if (taskInfo.pendingStatus != null && taskInfo.pendingStatus == "DELETED") {
                        // wait solr to index the change, and update the tree i.e. remove task from tree
                        setTimeout(function () {
                            $scope.$emit("report-tree-updated", taskInfo);
                            $scope.isClicked = false;
                        }, 4000);
                    }
                    return taskInfo;
                }, function (error) {
                    $scope.showErrorDialog(error);
                    $scope.isClicked = false;
                });
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
                TaskWorkflowService.claimTask($scope.objectInfo.taskId).then(function (taskInfo) {
                    TaskInfoService.resetTaskCacheById(taskInfo.taskId);
                    $scope.$emit("report-object-updated", taskInfo);
                    return TaskInfoService.getTaskInfo(taskInfo.taskId);
                });
            }
        };

        $scope.showUnclaimBtn = function () {
            if ($scope.objectInfo && Util.goodMapValue($scope.objectInfo, "assignee", false)) {
                if (Util.goodMapValue($scope.userInfo, "authorities", false) && !Util.isArrayEmpty($scope.userInfo.authorities)) {
                    if (_.includes($scope.userInfo.authorities, "ROLE_ADMINISTRATOR")) {
                        //Admin users can u3n-claim any group task.
                        return true;
                    } else if (!Util.isArrayEmpty(_.intersection($scope.objectInfo.candidateGroups, $scope.userInfo.authorities))) {
                        return true;
                    }
                }
            }
            return false;
        };

        $scope.unclaimTask = function () {
            if (Util.goodMapValue($scope.objectInfo, "taskId", false)) {
                TaskWorkflowService.unclaimTask($scope.objectInfo.taskId).then(function (taskInfo) {
                    TaskInfoService.resetTaskCacheById(taskInfo.taskId);
                    $scope.$emit("report-object-updated", taskInfo);
                    return TaskInfoService.getTaskInfo(taskInfo.taskId);
                }, function (error) {
                    return error;
                });
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

        $scope.sendEmail = function () {
            var params = {
                objectId: $scope.objectInfo.taskId,
                objectType: ObjectService.ObjectTypes.TASK,
                emailSubject: 'Task ' + $scope.objectInfo.taskId
            };
            var modalInstance = $modal.open({
                templateUrl: 'modules/common/views/send-email-modal.client.view.html',
                controller: 'Common.SendEmailModalController',
                animation: true,
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    params: function() {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function(res) {
                var emailData = {};
                emailData.subject = res.subject;
                emailData.body = res.body;
                emailData.footer = '\n\n' + res.footer;
                emailData.emailAddresses = res.recipients;
                emailData.ccEmailAddresses = res.ccRecipients;
                emailData.bccEmailAddresses = res.bccRecipients;
                emailData.objectId = $scope.objectInfo.taskId;
                emailData.objectType = ObjectService.ObjectTypes.TASK;
                emailData.modelReferenceName = res.template;

                if(emailData.modelReferenceName != 'plainEmail') {
                    EcmEmailService.sendManualEmail(emailData);
                } else {
                    EcmEmailService.sendPlainEmail(emailData, ObjectService.ObjectTypes.TASK);
                }

            });
        };
    }]);
angular.module('tasks').controller('Tasks.ActionsErrorDialogController', ['$scope', '$modalInstance', 'errorMessage', function ($scope, $modalInstance, errorMessage) {
    $scope.errorMessage = errorMessage;
    $scope.onClickOk = function () {
        $modalInstance.dismiss('cancel');
    };
}]);
angular.module('tasks').controller('Tasks.RejectDialogController', ['$scope', '$modalInstance', 'aValue', function ($scope, $modalInstance, aValue) {
    $scope.valuePassed = aValue;
    $scope.onClickCancel = function () {
        $modalInstance.close(false);
    };
    $scope.onClickOk = function () {
        $modalInstance.close(true);
    };
}]);
angular.module('tasks').controller('Tasks.SignatureDialogController', ['$scope', '$modalInstance', function ($scope, $modalInstance) {
    $scope.onClickCancel = function () {
        $modalInstance.dismiss('Cancel');
    };
    $scope.onClickOk = function () {
        $modalInstance.close({
            pass: $scope.password
        });
    };
}]);
