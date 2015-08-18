'use strict';

angular.module('administration').controller('ModulesListController', ['$scope', '$state', '$stateParams', 'ConfigService',
    function ($scope, $state, $stateParams, ConfigService) {
        $scope.modules = ConfigService.queryModules();
        $scope.selectModule = selectModule;

        function selectModule(newActive){
            var prevActive = _.find($scope.modules, {active: true});
            if (prevActive) {
                prevActive.active = false;
            }
            newActive.active = true;
            $scope.$emit('req-module-selected', newActive);
        }
    }
]);