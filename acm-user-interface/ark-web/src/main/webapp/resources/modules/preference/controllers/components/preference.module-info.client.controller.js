'use strict';

angular.module('preference').controller('Preference.ModuleInfoController', ['$scope', '$state', '$stateParams', 'ConfigService', 'dashboard',
    function ($scope, $state, $stateParams, ConfigService, dashboard) {
        $scope.module = null;
        $scope.$on('module-selected', moduleSelected);
        $scope.$on('req-save-module', saveModule);


        function saveModule() {
            //ConfigService.updateModule({
            //    moduleId: $scope.module.id
            //}, $scope.module, function(){
            //    // Indicate that module was saved
            //});
            console.log("module saved placeholder");
        }

        function moduleSelected(e, newModule) {
            $scope.module = ConfigService.getModule({moduleId: newModule.id}, function (module) {
                $scope.$broadcast('show-widgets', dashboard.widgets);
            });
        }
    }
]);
