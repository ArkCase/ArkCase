'use strict';

angular.module('cases').controller('Cases.NotesController', ['$scope',
    function($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'notes');
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'notes') {
                $scope.config = config;
            }
        }
    }
]);