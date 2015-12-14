'use strict';

angular.module('cases').controller('CasesController', ['$scope', '$stateParams', '$state', '$translate'
    , 'UtilService', 'ConfigService', 'Case.InfoService', 'ObjectService', 'Helper.ObjectTreeService'
    , function ($scope, $stateParams, $state, $translate
        , Util, ConfigService, CaseInfoService, ObjectService, HelperObjectTreeService) {

        var promiseGetModuleConfig = ConfigService.getModuleConfig("cases").then(function (config) {
            $scope.config = config;
            $scope.componentLinks = HelperObjectTreeService.createComponentLinks(config, ObjectService.ObjectTypes.CASE_FILE);
            $scope.activeLinkId = "main";
            return config;
        });
        $scope.$on('req-component-config', function (e, componentId) {
            promiseGetModuleConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
                return config;
            });
        });
        $scope.$on('report-case-updated', function (e, caseInfo) {
            CaseInfoService.updateCaseInfo(caseInfo);
            $scope.$broadcast('case-updated', caseInfo);
        });

        $scope.$on('req-select-case', function (e, selectedCase) {
            var components = Util.goodArray(selectedCase.components);
            $scope.activeLinkId = (1 == components.length) ? components[0] : "main";
        });

        $scope.getActive = function (linkId) {
            return ($scope.activeLinkId == linkId) ? "active" : ""
        };

        $scope.onClickComponentLink = function (linkId) {
            $scope.activeLinkId = linkId;
            $state.go('cases.' + linkId, {
                id: $stateParams.id
            });
        };


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