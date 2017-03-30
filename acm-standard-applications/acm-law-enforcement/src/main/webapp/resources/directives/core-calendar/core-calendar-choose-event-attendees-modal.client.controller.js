'use strict';

angular.module('directives').controller('Directives.CoreCalendarChooseEventAttendeesController', ['$scope', '$modalInstance', '$config',
    function($scope, $modalInstance, $config) {
        $scope.config = $config;

        $scope.onSelectAttendee = function(selectedItems, lastSelectedItems, isSelected) {
            $scope.attendees = selectedItems;
        };

        $scope.addAttendees = function() {
            $modalInstance.close('attendeesAdded');
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);