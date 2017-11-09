'use strict';

angular.module('admin').controller('Admin.SecurityCalendarConfigurationController', ['$scope', 'Admin.CalendarConfigurationService', 'MessageService', 'ConfigService',
    function ($scope, CalendarConfigurationService, MessageService, ConfigService) {

        $scope.isLoading = false;
        $scope.calendarConfigDataModel = {
            configurationsByType: {}
        };
        $scope.configurableObjectTypes = [];
        $scope.passwordRequirementByObjectType = {};
        $scope.validEmailsByObjectType = {};
        $scope.purgeSelectOptions = [
            {
                value: 'RETAIN_INDEFINITELY',
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

        var processCalendarConfigData = function (calendarAdminConfig) {
            _.forEach($scope.configurableObjectTypes, function (configurableObjectType) {
                // Check if there is Admin Calendar Configuration available for each object type defined in the component config
                if (calendarAdminConfig.data.configurationsByType[configurableObjectType.id]) {
                    $scope.calendarConfigDataModel.configurationsByType[configurableObjectType.id] = calendarAdminConfig.data.configurationsByType[configurableObjectType.id];
                    if (calendarAdminConfig.data.configurationsByType[configurableObjectType.id].systemEmail){
                        $scope.calendarConfigDataModel.configurationsByType[configurableObjectType.id].password = "******";
                    }
                }
                /*Check if password required by object type*/
                if (!calendarAdminConfig.data.configurationsByType[configurableObjectType.id].systemEmail) {
                    $scope.passwordRequirementByObjectType[configurableObjectType.id] = true;
                }
            });

        };

        /*Get component config and get current admin calendar configuration for each object type defined in the component config*/
        var getConfigurationData = function () {
            $scope.isLoading = true;
            ConfigService.getComponentConfig('admin', 'securityCalendarConfig').then(function (componentConfigRes) {
                CalendarConfigurationService.getCurrentCalendarConfiguration().then(function (calendarAdminConfigRes) {
                    $scope.isLoading = false;
                    $scope.configurableObjectTypes = componentConfigRes.configurableObjectTypes;
                    processCalendarConfigData(calendarAdminConfigRes);
                }, function (err) {
                    $scope.isLoading = false;
                    MessageService.errorAction();
                });
            }, function (err) {
                $scope.isLoading = false;
                MessageService.errorAction();
            });
        };

        /*Perform validation of the email*/
        $scope.validateEmail = function (systemEmail, password, configurableObjectType) {
            $scope.isLoading = true;
            var emailCredentials = {
                email: systemEmail,
                password: password
            };

            CalendarConfigurationService.validateCalendarConfigurationSystemEmail(emailCredentials).then(function (res) {
                $scope.isLoading = false;
                MessageService.succsessAction();
                if (res.data && res.data == "true") {
                    $scope.validEmailsByObjectType[configurableObjectType.id] = 'VALID';
                } else {
                    $scope.validEmailsByObjectType[configurableObjectType.id] = 'NOT_VALID';
                }
            }, function (err) {
                $scope.isLoading = false;
                MessageService.errorAction();
                $scope.validEmailsByObjectType[configurableObjectType.id] = 'NOT_VALID';
            });
        };

        $scope.systemEmailInputChanged = function (configurableObjectType) {
            /*Remove success/error validation message when email input is changed*/
            if ($scope.validEmailsByObjectType[configurableObjectType.id]) {
                $scope.validEmailsByObjectType[configurableObjectType.id] = null;
            }
            $scope.passwordRequirementByObjectType[configurableObjectType.id] = true;
        };

        $scope.saveChanges = function () {
            $scope.isLoading = true;
            CalendarConfigurationService.saveCalendarConfiguration($scope.calendarConfigDataModel)
                .then(function (res) {
                    $scope.isLoading = false;
                    // remove all success/error validation messages
                    $scope.validEmailsByObjectType = {};
                    MessageService.succsessAction();
                }, function (err) {
                    $scope.isLoading = false;
                    MessageService.errorAction();
                });
        };

        getConfigurationData();
    }
]);