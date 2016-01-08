'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Case.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, CaseInfoService, HelperUiGridService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        ConfigService.getComponentConfig("cases", "references").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            return config;
        });

        //$scope.$on('object-updated', function (e, data) {
        //    if (CaseInfoService.validateCaseInfo(data)) {
        //        $scope.caseInfo = data;
        //        $scope.gridOptions = $scope.gridOptions || {};
        //        $scope.gridOptions.data = $scope.caseInfo.references;
        //        gridHelper.hidePagingControlsIfAllDataShown($scope.caseInfo.references.length);
        //    }
        //});
        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            CaseInfoService.getCaseInfo(currentObjectId).then(function (caseInfo) {
                $scope.caseInfo = caseInfo;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.caseInfo.references;
                //gridHelper.hidePagingControlsIfAllDataShown($scope.caseInfo.references.length);
                return caseInfo;
            });
        }

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);