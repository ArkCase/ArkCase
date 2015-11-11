'use strict';

//
//jwu: need ngdoc in controller ???
//

///**
// * @ngdoc controller
// * @name cases.controller:CasesController
// *
// * @description
// * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cases/controllers/cases.client.controller.js modules/cases/controllers/cases.client.controller.js}
// *
// * The Cases module main controller
// */
angular.module('cases').controller('CasesController', ['$scope', '$stateParams', '$translate', 'UtilService', 'CallConfigService', 'CallCasesService',
    function ($scope, $stateParams, $translate, Util, CallConfigService, CallCasesService) {
        var promiseGetModuleConfig = CallConfigService.getModuleConfig("cases").then(function (config) {
            $scope.config = config;
            return config;
        });
        $scope.$on('req-component-config', function (e, componentId) {
            promiseGetModuleConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        });



        $scope.progressMsg = $translate.instant("cases.progressNoCase");
        $scope.$on('req-select-case', function (e, selectedCase) {
            $scope.$broadcast('case-selected', selectedCase);

            var id = Util.goodMapValue(selectedCase, "nodeId", null);
            loadCase(id);
        });


        var loadCase = function (id) {
            if (id) {
                if ($scope.caseInfo && $scope.caseInfo.id != id) {
                    $scope.caseInfo = null;
                }
                $scope.progressMsg = $translate.instant("cases.progressLoading") + " " + id + "...";

                CallCasesService.getCaseInfo(id).then(
                    function (caseInfo) {
                        $scope.progressMsg = null;
                        $scope.caseInfo = caseInfo;
                        $scope.$broadcast('case-retrieved', caseInfo);
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