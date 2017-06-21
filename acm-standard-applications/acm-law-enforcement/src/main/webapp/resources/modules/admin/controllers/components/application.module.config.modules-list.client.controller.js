'use strict';

angular.module('admin').controller('Admin.ModulesListController', ['$scope', '$state', '$stateParams', 'ConfigService',
    function ($scope, $state, $stateParams, ConfigService) {
        var modules = [];
        //If we want to prevent a module to be displayed on Module Configuration page, we should explicitly place it in the blacklist array
        var blacklist = ['Common settings', 'Core', 'Document details', 'Frevvo', 'Goodbye', 'Welcome'];

        ConfigService.queryModules().$promise.then(function (data){
            _.forEach(data, function (module){
                modules.push(module.title);
            });
            $scope.modules = _.difference(modules, blacklist);
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