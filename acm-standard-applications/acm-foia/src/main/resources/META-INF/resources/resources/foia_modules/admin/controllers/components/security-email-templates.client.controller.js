'use strict';

angular.module('admin').controller('Admin.SecurityEmailTemplatesController',
        [ '$scope', '$translate', '$modal', 'Admin.EmailTemplatesService', 'Helper.UiGridService', 'MessageService', 'Dialog.BootboxService', 'UtilService', function($scope, $translate, $modal, emailTemplatesService, HelperUiGridService, MessageService, DialogService, Util) {


            emailTemplatesService.getEmailReceiverConfiguration().then(function(result) {
                $scope.originalEmailReceiverConfiguration = result.data;
                $scope.emailReceiverConfiguration = {
                    enableBurstingAttachments: result.data["email.enableBurstingAttachments"],
                    enableRequest: result.data["email.create.case.enabled"],
                    user_case: result.data["email.CASE_FILE.user"].replace('%40', '@'),
                    pass_case: result.data["email.CASE_FILE.password"],
                    user_task: result.data["email.TASK.user"].replace('%40', '@'),
                    pass_task: result.data["email.TASK.password"]
                }
            });

            $scope.save = function() {
                var newEmailReceiverConfiguration = {
                    "email.create.case.enabled": $scope.emailReceiverConfiguration.enableRequest,
                    "email.CASE_FILE.user": $scope.emailReceiverConfiguration.user_case.replace('@', '%40'),
                    "email.CASE_FILE.password": $scope.emailReceiverConfiguration.pass_case,
                    "email.enableBurstingAttachments": $scope.originalEmailReceiverConfiguration["email.enableBurstingAttachments"],
                    "email.TASK.user": $scope.emailReceiverConfiguration.user_task.replace('@', '%40'),
                    "email.TASK.password": $scope.emailReceiverConfiguration.pass_task,
                };
                emailTemplatesService.saveEmailReceiverConfiguration(newEmailReceiverConfiguration).then(function(value) {
                    MessageService.succsessAction();
                }, function(err) {
                    MessageService.errorAction();
                });
            };

            $scope.saveBurstingConfiguration = function () {
                var emailReceiverConfiguration = {
                    "email.create.case.enabled": $scope.originalEmailReceiverConfiguration["email.create.case.enabled"],
                    "email.CASE_FILE.user": $scope.originalEmailReceiverConfiguration["email.CASE_FILE.user"],
                    "email.CASE_FILE.password": $scope.originalEmailReceiverConfiguration["email.CASE_FILE.password"],
                    "email.enableBurstingAttachments": $scope.emailReceiverConfiguration.enableBurstingAttachments,
                    "email.TASK.user": $scope.emailReceiverConfiguration.user_task.replace('@', '%40'),
                    "email.TASK.password": $scope.emailReceiverConfiguration.pass_task
                };
                emailTemplatesService.saveEmailReceiverConfiguration(emailReceiverConfiguration).then(function() {
                    MessageService.succsessAction();
                }, function() {
                    MessageService.errorAction();
                });
            };

        } ]);
