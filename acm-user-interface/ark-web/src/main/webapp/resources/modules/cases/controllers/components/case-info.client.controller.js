'use strict';

angular.module('cases').controller('CaseInfoController', ['$scope', '$stateParams', 'ConfigService', 'CasesService',
    function($scope, $stateParams, ConfigService, CasesService) {
        $scope.$on('component-config', applyConfig);
        $scope.$on('case-selected', onSelectedCase);
        $scope.$emit('req-component-config', 'info');
        $scope.components = null;
        $scope.config = null;

        $scope.case = null;
        $scope.caseInfo = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'info') {
                $scope.config = config;
            }
        }

        function onSelectedCase(e, selectedCase){
            if (!$scope.case || $scope.case.object_id_s != selectedCase.object_id_s) {
                $scope.case = selectedCase;
                $scope.caseInfo = CasesService.get({id: selectedCase.object_id_s});
            }
        };

        ConfigService.getModule({moduleId: 'cases'}, function(moduleConfig){
            $scope.components = moduleConfig.components;
        });
    }
]);