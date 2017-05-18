'use strict';

angular.module('directives').controller('Directives.CoreCalendarDeleteEventController', ['$scope', '$modalInstance', 'params', 'Object.CalendarService', 'MessageService',
    function($scope, $modalInstance, params, CalendarService, MessageService) {
        $scope.eventDataModel = params.eventDataModel;
        $scope.objectId = params.objectId;
        $scope.objectType = params.objectType;
        $scope.deleteEventModel = {
            deleteRecurring: false
        };

        $scope.deleteEvent = function() {
            CalendarService.deleteEvent($scope.objectType, $scope.objectId, $scope.eventDataModel.eventId, $scope.deleteEventModel.deleteRecurring).then(function(res) {
                MessageService.succsessAction();
                $modalInstance.close('DELETE_EVENT');
            }, function(err) {
                MessageService.errorAction();
            });
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);