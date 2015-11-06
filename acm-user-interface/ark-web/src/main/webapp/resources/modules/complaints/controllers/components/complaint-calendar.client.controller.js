'use strict';

angular.module('complaints').controller('Complaints.CalendarController', ['$scope',
    function ($scope) {
        var z = 1;
        return;
        $scope.$emit('req-component-config', 'calendar');
        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'calendar') {
                $scope.config = config;
            }
        });
    }
]);