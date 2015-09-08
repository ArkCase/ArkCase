'use strict';

angular.module('cases').controller('Cases.InfoController', ['$scope', '$stateParams', 'ConfigService', 'CasesService', 'LookupService',
    function($scope, $stateParams, ConfigService, CasesService, LookupService) {
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

        $scope.priorities = [];
        LookupService.getPriorities({}, function(data) {
            //$scope.priorities = data;

            $scope.priorities = [];
            _.forEach(data, function (item) {
                $scope.priorities.push({value: item, text: item});
            });
        });

        //$scope.myselect = "var1";
        //$scope.myoptions = ['var1', 'var2', 'var3'];

    }
]);