'use strict';
angular.module('admin').controller('Admin.SecurityEmailSenderConfigurationController', ['$scope', 'Admin.EmailSenderConfigurationService', 'MessageService',
    function($scope, EmailSenderConfigurationService, MessageService) {
        $scope.emailSenderConfigDataModel = {};

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
                value: 'ssl',
                label: 'admin.security.emailConfiguration.emailConfigForm.encryptionOptions.ssl'
            },
            {
                value: 'tls',
                label: 'admin.security.emailConfiguration.emailConfigForm.encryptionOptions.tls'
            }
            ];
        
        EmailSenderConfigurationService.getEmailSenderConfiguration().then(function(res) {
            $scope.emailSenderConfigDataModel = res.data;
        });
        
        /*Perform validation of the email*/
        $scope.validateEmail = function(emailSenderConfiguration) {
            EmailSenderConfigurationService.validateEmailSenderConfiguration(emailSenderConfiguration).then(function(res) {
                //TO DO
                MessageService.succsessAction();
                //$scope.validEmailsByObjectType[configurableObjectType.id] = 'VALID';
            }, function(err) {
                //TO DO
                MessageService.errorAction();
                //$scope.validEmailsByObjectType[configurableObjectType.id] = 'NOT_VALID';
            });
        };

        /*Remove success/error validation message when email input is changed*/
        $scope.systemEmailInputChanged = function(configurableObjectType) {
            if($scope.validEmailsByObjectType[configurableObjectType.id]) {
                $scope.validEmailsByObjectType[configurableObjectType.id] = null;
            }
        };
        
        $scope.save = function() {
            EmailSenderConfigurationService.saveEmailSenderConfiguration($scope.emailSenderConfigDataModel)
                .then(function(res) {
                    MessageService.succsessAction();
                }, function(err) {
                    if(err.status === 400) {
                        // TO DO
                        // Email Validation error
                        MessageService.errorAction();
                    } else {
                        // TO DO
                        // server error
                        MessageService.errorAction();
                    }
                });
        };
    }
]);