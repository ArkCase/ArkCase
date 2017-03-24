'use strict';

angular.module('admin').controller('Admin.SecurityCalendarConfigurationController', ['$scope', 'Admin.CalendarConfigurationService', 'MessageService', 'ConfigService',
    function($scope, CalendarConfigurationService, MessageService, ConfigService) {

        $scope.calendarConfigDataModel = {};
        $scope.configurableObjectTypes = [];
        $scope.purgeSelectOptions = [
            {
                value: 'RETAIN_INDEFINETELY',
                label: 'admin.security.calendarConfiguration.calendarConfigForm.purgeOptions.retainIndefinitely'
            },
            {
                value: 'CLOSED',
                label: 'admin.security.calendarConfiguration.calendarConfigForm.purgeOptions.objectClosed'
            },
            {
                value: 'CLOSED_X_DAYS',
                label: 'admin.security.calendarConfiguration.calendarConfigForm.purgeOptions.objectClosedXDays'
            }
        ];

        $scope.validEmailsByObjectType = {};
        $scope.noCalendarIntegrationEnabled = true;

        /*Check if integration is enabled for at least one configurableObjectType to enable save button*/
        var checkIfCalendarIntegrationEnabled = function() {
            for(var i = 0; i <= $scope.configurableObjectTypes.length; i++) {
                if($scope.calendarConfigDataModel.configurationsByType[$scope.configurableObjectTypes[i].id].integrationEnabled) {
                    $scope.noCalendarIntegrationEnabled = false;
                    break;
                } else {
                    $scope.noCalendarIntegrationEnabled = true;
                }
            }
        };

        /*Get component config and current calendar configuration*/
        ConfigService.getComponentConfig('admin', 'securityCalendarConfig').then(function(res) {
            $scope.configurableObjectTypes = res.configurableObjectTypes;
            CalendarConfigurationService.getCalendarConfiguration().then(function(res) {
                $scope.calendarConfigDataModel = res.data;
                checkIfCalendarIntegrationEnabled();
            });
        });

        /*Perform validation of the email*/
        $scope.validateEmail = function(systemEmail, configurableObjectType) {
            CalendarConfigurationService.validateEmail(systemEmail).then(function(res) {
                if(res.valid) {
                    //TO DO
                    MessageService.succsessAction();
                    $scope.validEmailsByObjectType[configurableObjectType.id] = 'VALID';
                } else {
                    //TO DO
                    MessageService.errorAction();
                    $scope.validEmailsByObjectType[configurableObjectType.id] = 'NOT_VALID';
                }
            });
        };

        $scope.integrationEnabledChanged = function() {
            checkIfCalendarIntegrationEnabled();
        };

        /*Remove success/error validation message when email input is changed*/
        $scope.systemEmailInputChanged = function(configurableObjectType) {
            if($scope.validEmailsByObjectType[configurableObjectType.id]) {
                $scope.validEmailsByObjectType[configurableObjectType.id] = null;
            }
        };

        $scope.applyChanges = function() {
            CalendarConfigurationService.applyCalendarConfiguration($scope.calendarConfigDataModel)
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