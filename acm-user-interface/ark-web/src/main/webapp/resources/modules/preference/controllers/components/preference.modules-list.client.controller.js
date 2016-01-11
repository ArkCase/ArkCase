'use strict';

angular.module('preference').controller('Preference.ModulesListController', ['$scope', '$state', '$stateParams', 'ConfigService', 'dashboard', 'UtilService',
    function ($scope, $state, $stateParams, ConfigService, dashboard, Util) {

        $scope.filterModules = function (allModules) {
            var modules = []; //empty array to store kept variables

            for (var i = 0; i < allModules.length; i++) {
                ConfigService.getModuleConfig(allModules[i].id).then(function (config) {
                    if (Util.goodValue(config.hasOverviewWidgets, false)) {
                        //Need to get full module object, already in allModules
                        for (var j = 0; j < allModules.length; j++) {
                            if (allModules[j].id === config.id) {
                                modules.push(allModules[j]);
                                break;
                            }
                        }
                    }
                });
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
