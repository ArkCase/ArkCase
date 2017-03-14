'use strict';

angular.module('admin').controller('Admin.CalendarManagementConfigController', ['$scope', 'Admin.CalendarManagementService', 'MessageService',
    function($scope, CalendarManagementService, MessageService) {

        $scope.calendarConfigDataModel = {
            calendarType: 'SYSTEM_BASED',
            systemEmail: '',
            password: ''
        };

        $scope.applyChanges = function() {
            CalendarManagementService.applyCalendarConfiguration($scope.calendarConfigDataModel)
                .then(function(res) {
                    MessageService.succsessAction();
                }, function(err) {
                    MessageService.errorAction();
                });
        };
    }
]);