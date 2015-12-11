'use strict';

angular.module('cases').controller('CasesController', ['$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Case.InfoService',
    function ($scope, $stateParams, $translate, Util, ConfigService, CaseInfoService) {
        var promiseGetModuleConfig = ConfigService.getModuleConfig("cases").then(function (config) {
            $scope.config = config;
            return config;
        });
        $scope.$on('req-component-config', function (e, componentId) {
            promiseGetModuleConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        });
        $scope.$on('report-case-updated', function (e, caseInfo) {
            CaseInfoService.updateCaseInfo(caseInfo);
            $scope.$broadcast('case-updated', caseInfo);
        });



        $scope.progressMsg = $translate.instant("cases.progressNoCase");
        $scope.$on('req-select-case', function (e, selectedCase) {
            $scope.$broadcast('case-selected', selectedCase);

            var id = Util.goodMapValue(selectedCase, "nodeId", null);
            loadCase(id);
        });


        var loadCase = function (id) {
            if (Util.goodPositive(id)) {
                if ($scope.caseInfo && $scope.caseInfo.id != id) {
                    $scope.caseInfo = null;
                }
                $scope.progressMsg = $translate.instant("cases.progressLoading") + " " + id + "...";

                CaseInfoService.getCaseInfo(id).then(
                    function (caseInfo) {
                        $scope.progressMsg = null;
                        $scope.caseInfo = caseInfo;
                        $scope.$broadcast('case-updated', caseInfo);
                        return caseInfo;
                    }
                    , function (error) {
                        $scope.caseInfo = null;
                        $scope.progressMsg = $translate.instant("cases.progressError") + " " + id;
                        return error;
                    }
                );
            }
        };

        loadCase($stateParams.id);
	}
]);