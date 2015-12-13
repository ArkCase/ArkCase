'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'Case.InfoService'
    , function ($scope, $stateParams, Util, ConfigService, HelperUiGridService, CaseInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        //$scope.$emit('req-component-config', 'references');
        //$scope.$on('component-config', function (e, componentId, config) {
        //    if ("references" == componentId) {
        //        gridHelper.setColumnDefs(config);
        //        gridHelper.setBasicOptions(config);
        //    }
        //});
        ConfigService.getComponentConfig("cases", "references").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            return config;
        });

        //$scope.$on('case-updated', function (e, data) {
        //    if (CaseInfoService.validateCaseInfo(data)) {
        //        $scope.caseInfo = data;
        //        $scope.gridOptions = $scope.gridOptions || {};
        //        $scope.gridOptions.data = $scope.caseInfo.references;
        //        gridHelper.hidePagingControlsIfAllDataShown($scope.caseInfo.references.length);
        //    }
        //});
        CaseInfoService.getCaseInfo($stateParams.id).then(function (caseInfo) {
            $scope.caseInfo = caseInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.caseInfo.references;
            gridHelper.hidePagingControlsIfAllDataShown($scope.caseInfo.references.length);
            return caseInfo;
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

	}
]);