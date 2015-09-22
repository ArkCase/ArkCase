'use strict';

angular.module('cases').controller('Cases.InfoController', ['$scope', '$stateParams', 'ConfigService', 'CasesService', 'LookupService', 'UtilService', 'ValidationService',
    function($scope, $stateParams, ConfigService, CasesService, LookupService, Util, Validator) {
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


        $scope.selectedCase = null;
        $scope.caseInfo = null;
        $scope.$on('case-selected', function onSelectedCase(e, selectedCase) {
            //if (!$scope.case || $scope.case.object_id_s != selectedCase.object_id_s) {
            $scope.selectedCase = selectedCase;
            //$scope.caseInfo = CasesService.get({id: selectedCase.object_id_s});
            //}
        });
        $scope.$on('case-retrieved', function(e, data){
            $scope.caseInfo = Util.goodValue(data, {});
        });


        $scope.updateTitle = function() {
            //alert("update case title:" + $scope.caseInfo.title);

            var caseInfo = Util.omitNg($scope.caseInfo);

            CasesService.save({}, caseInfo
                ,function(successData) {
                    var z = 1;
                }
                ,function(errorData) {
                    var z = 1;
                }
            );

        };

        $scope.priorities = [];
        LookupService.getPriorities({}, function(data) {
            $scope.priorities = [];
            _.forEach(data, function (item) {
                $scope.priorities.push({value: item, text: item});
            });
        });

    }
]);