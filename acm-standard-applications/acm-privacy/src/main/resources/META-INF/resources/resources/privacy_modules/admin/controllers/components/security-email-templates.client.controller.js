'use strict';

angular.module('admin').controller('Admin.SecurityEmailTemplatesController',
        [ '$scope', '$translate', '$modal', 'Admin.EmailTemplatesService', 'Helper.UiGridService', 'MessageService', 'Dialog.BootboxService', 'UtilService', function($scope, $translate, $modal, emailTemplatesService, HelperUiGridService, MessageService, DialogService, Util) {


            emailTemplatesService.getEmailReceiverConfiguration().then(function(result) {
                $scope.emailReceiverConfiguration = {
                    enableBurstingAttachments: result.data["email.enableBurstingAttachments"]
                };
            });
            
            $scope.saveBurstingConfiguration = function () {
                var emailReceiverConfiguration = {
                    "email.enableBurstingAttachments": $scope.emailReceiverConfiguration.enableBurstingAttachments
                };
                emailTemplatesService.saveEmailReceiverConfiguration(emailReceiverConfiguration).then(function() {
                    MessageService.succsessAction();
                }, function() {
                    MessageService.errorAction();
                });
            };

        } ]);