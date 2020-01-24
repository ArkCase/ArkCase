'use strict';
angular.module('admin').controller('Admin.SecurityEmailSenderConfigurationController', [ '$scope', 'Admin.EmailSenderConfigurationService', 'MessageService', '$translate', function($scope, EmailSenderConfigurationService, MessageService, $translate) {
    $scope.emailSenderConfigDataModel = {};
    $scope.isSmtpValid = null;

    $scope.serverTypeSelectOptions = [ {
        value: 'smtp',
        label: 'admin.security.emailConfiguration.emailConfigForm.serverTypeOptions.smtp'
    }, {
        value: 'outlook',
        label: 'admin.security.emailConfiguration.emailConfigForm.serverTypeOptions.outlook'
    } ];

    $scope.encryptionSelectOptions = [ {
        value: 'off',
        label: 'admin.security.emailConfiguration.emailConfigForm.encryptionOptions.off'
    }, {
        value: 'ssl-tls',
        label: 'admin.security.emailConfiguration.emailConfigForm.encryptionOptions.ssl-tls'
    }, {
        value: 'starttls',
        label: 'admin.security.emailConfiguration.emailConfigForm.encryptionOptions.starttls'
    } ];

    EmailSenderConfigurationService.getEmailSenderConfiguration().then(function(res) {
        $scope.emailSenderConfigDataModel = res.data;
    });

    $scope.changeEncryption = function(encryptionType) {
        switch (encryptionType) {
        case 'off':
            $scope.emailSenderConfigDataModel['email.sender.port'] = 25;
            break;
        case 'ssl-tls':
            $scope.emailSenderConfigDataModel['email.sender.port'] = 465;
            break;
        case 'starttls':
            $scope.emailSenderConfigDataModel['email.sender.port'] = 587;
        }
    };

    $scope.validateSmtpConfiguration = function(smtpConfiguration) {
        EmailSenderConfigurationService.validateSmtpConfiguration(smtpConfiguration).then(function(res) {
            if (res.data && $scope.emailSenderConfigDataModel['email.sender.host']) {
                $scope.isSmtpValid = true;
            } else {
                $scope.isSmtpValid = false;
            }
        }, function(err) {
            $scope.isSmtpValid = false;
            MessageService.error(err.data);
        });
    };

    $scope.save = function() {
        EmailSenderConfigurationService.saveEmailSenderConfiguration($scope.emailSenderConfigDataModel).then(function(res) {
            MessageService.succsessAction();
            $scope.isSmtpValid = null;
        }, function(err) {
            MessageService.errorAction();
        });
    };
} ]);