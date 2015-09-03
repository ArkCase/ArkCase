'use strict';

angular.module('cases').controller('Cases.CorrespondenceController', ['$scope',
    function($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'correspondence');
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'correspondence') {
                $scope.config = config;
            }
        }
    }
]);