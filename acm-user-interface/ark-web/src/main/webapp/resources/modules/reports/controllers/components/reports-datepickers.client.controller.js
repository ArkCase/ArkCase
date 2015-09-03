'use strict';

angular.module('cases').controller('ReportsDatepickersController', ['$scope',
    function ($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'datepickers');
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'datepickers') {
                $scope.config = config;
            }
        }

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;

        $scope.open = function ($event, datepicker) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened[datepicker] = true;
        };

    }
]);