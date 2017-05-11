'use strict';
angular.module('admin').controller('Admin.SecurityEmailSenderConfigurationController', ['$scope', 'Admin.EmailSenderConfigurationService', 'MessageService',
    function($scope, EmailSenderConfigurationService, MessageService) {
        $scope.emailSenderConfigDataModel = {};
        $scope.isSmtpValid = null;

        $scope.serverTypeSelectOptions = [
            {
                value: 'smtp',
                label: 'admin.security.emailConfiguration.emailConfigForm.serverTypeOptions.smtp'
            },
            {
                value: 'outlook',
                label: 'admin.security.emailConfiguration.emailConfigForm.serverTypeOptions.outlook'
            }
            ];
        
        $scope.encryptionSelectOptions = [
            {
                value: 'off',
                label: 'admin.security.emailConfiguration.emailConfigForm.encryptionOptions.off'
            },
            {
                value: 'ssl-tls',
                label: 'admin.security.emailConfiguration.emailConfigForm.encryptionOptions.ssl-tls'
            },
            {
                value: 'starttls',
                label: 'admin.security.emailConfiguration.emailConfigForm.encryptionOptions.starttls'
            }
            ];
        
        EmailSenderConfigurationService.getEmailSenderConfiguration().then(function(res) {
            $scope.emailSenderConfigDataModel = res.data;
        });
        
        $scope.changeEncryption = function(encryptionType) {
            switch (encryptionType) {
            case 'off':
                $scope.emailSenderConfigDataModel.port = 25;
                break;
            case 'ssl-tls':
                $scope.emailSenderConfigDataModel.port = 465;
                break;
            case 'starttls':
                $scope.emailSenderConfigDataModel.port = 587;
            }
        }
        
        $scope.validateSmtpConfiguration = function(smtpConfiguration) {
            EmailSenderConfigurationService.validateSmtpConfiguration(smtpConfiguration).then(function(res) {
                if (res.data) {
                    $scope.isSmtpValid = true;
                } else {
                    $scope.isSmtpValid = false;
                }
            }, function(err) {
                MessageService.errorAction();
            });
        };
        
        $scope.save = function() {
            EmailSenderConfigurationService.saveEmailSenderConfiguration($scope.emailSenderConfigDataModel)
                .then(function(res) {
                    MessageService.succsessAction();
                    $scope.isSmtpValid = null;
                }, function(err) {
                    if(err.status === 400) {
                        MessageService.errorAction();
                    } else {
                        MessageService.errorAction();
                    }
                });
        };
    }
]);