'use strict';

angular.module('admin').controller('Admin.SecurityCalendarConfigurationController', ['$scope', 'Admin.CalendarConfigurationService', 'MessageService', 'ConfigService',
    function($scope, CalendarConfigurationService, MessageService, ConfigService) {

        $scope.calendarConfigDataModel = {};
        $scope.configurableObjectTypes = [];
        $scope.purgeSelectOptions = [
            {
                value: 'RETAIN_INDEFINETELY',
                label: 'admin.security.calendarConfiguration.calendarConfigForm.purge.placeholder'
            },
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

        ConfigService.getComponentConfig('admin', 'securityCalendarConfig').then(function(res) {
            $scope.configurableObjectTypes = res.configurableObjectTypes;
            buildCalendarConfigDataModel();
        });

        var buildCalendarConfigDataModel = function() {
            CalendarConfigurationService.getCalendarConfiguration().then(function(res) {
                $scope.calendarConfigDataModel = res.data;
            });
        };

        $scope.validateEmail = function(systemEmail) {
            CalendarConfigurationService.validateEmail(systemEmail).then(function(res) {
                if(res.valid) {
                    //TO DO
                } else {
                    //TO DO
                }
            });
        };

        $scope.applyChanges = function() {
            CalendarConfigurationService.applyCalendarConfiguration($scope.calendarConfigDataModel)
                .then(function(res) {
                    MessageService.succsessAction();
                }, function(err) {
                    MessageService.errorAction();
                });
        };
    }
]);