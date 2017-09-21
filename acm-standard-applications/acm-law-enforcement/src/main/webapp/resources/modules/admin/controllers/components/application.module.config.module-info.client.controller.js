'use strict';

angular.module('admin').controller('Admin.ModuleInfoController', ['$scope', '$state', '$stateParams', '$translate', 'ConfigService',
    function ($scope, $state, $stateParams, $translate, ConfigService) {
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
                _.forEach(module.components, function(component){
                    $translate.instant(component.title);
                })
                $translate.refresh();
                $scope.$broadcast('show-components', module.components);
            });
        }
    }
]);