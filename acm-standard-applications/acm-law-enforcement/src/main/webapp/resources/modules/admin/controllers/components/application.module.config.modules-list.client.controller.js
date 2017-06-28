'use strict';

angular.module('admin').controller('Admin.ModulesListController', ['$scope', '$state', '$stateParams', 'ConfigService',
    function ($scope, $state, $stateParams, ConfigService) {
        var modules = [];

        ConfigService.queryModules().$promise.then(function (data){
            modules = _.filter(data, {configurable: true});
            $scope.modules = modules;
        });

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