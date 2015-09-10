'use strict';

angular.module('reports').controller('Reports.SelectionController', ['$scope',
    function ($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'reportselection');
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'reportselection') {
                $scope.config = config;
            }
        }
    }
]);