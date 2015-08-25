'use strict';

angular.module('cases').controller('CaseInfoController', ['$scope', '$stateParams', 'ConfigService', 'CasesService',
    function($scope, $stateParams, ConfigService, CasesService) {
        $scope.$emit('req-component-config', 'info');

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'info') {
                $scope.config = config;
            }
        }

        $scope.components = null;
        ConfigService.getModule({moduleId: 'cases'}, function(moduleConfig){
            $scope.components = moduleConfig.components;
        });


        $scope.kase = null;
        $scope.caseInfo = null;
        $scope.$on('case-selected', function onSelectedCase(e, selectedCase) {
            //if (!$scope.case || $scope.case.object_id_s != selectedCase.object_id_s) {
            $scope.kase = selectedCase;
            //$scope.caseInfo = CasesService.get({id: selectedCase.object_id_s});
            //}
        });
        $scope.$on('case-retrieved', function(e, data){
            if (data) {
                $scope.caseInfo = data;
            }
        });


        $scope.updateTitle = function() {
            alert("update case title:" + $scope.caseInfo.title);
            //return $http.post('/updateTitle', $scope.caseInfo.title);
        };

        $scope.priorities = [
            { value: "High"     ,text: "High" }
            ,{ value: "Medium"  ,text: "Medium" }
            ,{ value: "Low"     ,text: "Low" }
        ];
    }
]);