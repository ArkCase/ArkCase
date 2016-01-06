'use strict';

angular.module('preference').controller('Preference.ModulesListController', ['$scope', '$state', '$stateParams', 'ConfigService', 'Preference.PreferenceService', 'dashboard',
    function ($scope, $state, $stateParams, ConfigService, PreferenceService, dashboard) {

        $scope.filterModules = function (allModules) {
            //var allModules = ConfigService.queryModules();
            //Modules with widgets enabled: Currently [Complaints, Cases, Tasks, Cost Tracking, Time Tracking]
            var modulesWithWidgets = ['Complaints', 'Cases', 'Tasks', 'Cost Tracking', 'Time Tracking'];

            var modules = []; //empty array to store kept variables
            //Filter modules against the modulesWithWidgets array

            for (var j = 0; j < modulesWithWidgets.length; j++) {
                for (var i = 0; i < allModules.length; i++) {
                    if (allModules[i].title === modulesWithWidgets[j]) {
                        modules.push(allModules[i]);
                        break;
                    }
                }
            }

            $scope.modules = modules;
            return modules;
        }

        ConfigService.queryModules().$promise.then($scope.filterModules);

        $scope.selectModule = selectModule;

        function selectModule(newActive) {
            var prevActive = _.find($scope.modules, {
                active: true
            });
            if (prevActive) {
                prevActive.active = false;
            }
            newActive.active = true;
            $scope.$emit('req-module-selected', newActive);
        }
    }
]);
