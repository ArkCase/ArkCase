'use strict';

angular.module('administration').controller('ModuleInfoController', ['$scope', '$state', '$stateParams', 'ConfigService',
    function ($scope, $state, $stateParams, ConfigService) {
        $scope.module = null;
        $scope.$on('module-selected', moduleSelected);
        $scope.$on('req-save-module', saveModule);


        function saveModule() {
            ConfigService.updateModule({
                moduleId: $scope.module.id
            }, $scope.module, function(){
                // Indicate that module was saved
            });
        }

        function moduleSelected(e, newModule) {
            $scope.module = ConfigService.getModule({moduleId: newModule.id}, function(module){
                $scope.$broadcast('show-components', module.components);
            });
        }
    }
]);