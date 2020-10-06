'use strict';

angular.module('admin').controller('Admin.SecurityEmailTemplatesController',
        [ '$scope', '$translate', '$modal', 'Admin.EmailTemplatesService', 'Helper.UiGridService', 'MessageService', 'Dialog.BootboxService', 'UtilService', function($scope, $translate, $modal, emailTemplatesService, HelperUiGridService, MessageService, DialogService, Util) {
            $scope.emailReceiverConfiguration = {};

    emailTemplatesService.getEmailReceiverConfiguration().then(function(result) {
        $scope.emailReceiverConfiguration = {
            enableCase: result.data["email.create.case.enabled"],
            user_case: result.data["email.CASE_FILE.user"].replace('%40', '@'),
            pass_case: result.data["email.CASE_FILE.password"],
            enableComplaint: result.data["email.create.complaint.enabled"],
            user_complaint: result.data["email.COMPLAINT.user"].replace('%40', '@'),
            pass_complaint: result.data["email.COMPLAINT.password"],
            enableBurstingAttachments: result.data["email.enableBurstingAttachments"]
        }
    });

    $scope.save = function() {
        var newEmailReceiverConfiguration = {
            "email.create.case.enabled": $scope.emailReceiverConfiguration.enableCase,
            "email.CASE_FILE.user": $scope.emailReceiverConfiguration.user_case.replace('@', '%40'),
            "email.CASE_FILE.password": $scope.emailReceiverConfiguration.pass_case,
            "email.create.complaint.enabled": $scope.emailReceiverConfiguration.enableComplaint,
            "email.COMPLAINT.user": $scope.emailReceiverConfiguration.user_complaint.replace('@', '%40'),
            "email.COMPLAINT.password": $scope.emailReceiverConfiguration.pass_complaint,
            "email.enableBurstingAttachments": $scope.emailReceiverConfiguration.enableBurstingAttachments
        };
        emailTemplatesService.saveEmailReceiverConfiguration(newEmailReceiverConfiguration).then(function(value) {
            MessageService.succsessAction();
        }, function(err) {
            MessageService.errorAction();
        });
    };
} ]);