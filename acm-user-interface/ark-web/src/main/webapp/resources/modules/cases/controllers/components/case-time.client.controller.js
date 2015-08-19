'use strict';

angular.module('cases').controller('CaseTimeController', ['$scope',
    function($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'time');
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'time') {
                $scope.config = config;
            }
        }
    }
]);