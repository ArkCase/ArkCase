'use strict';

angular.module('tasks').controller('Tasks.UserSearchController', ['$scope', '$modalInstance', '$clientInfoScope', '$filter',
    function ($scope, $modalInstance, $clientInfoScope, $filter) {
        $scope.filter = $filter;

        $clientInfoScope.$on('component-config', applyConfig);
        $clientInfoScope.$emit('req-component-config', 'userSearch');

        $scope.config = null;
        $scope.modalInstance = $modalInstance;
        function applyConfig(e, componentId, config) {
            if (componentId == 'task.comp.userSearch') {
                $scope.config = config;
            }
        }
    }
]);