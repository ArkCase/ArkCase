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

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            CaseInfoService.getCaseInfo(currentObjectId).then(function (caseInfo) {
                updateData(caseInfo);
                return caseInfo;
            });
        }

        $scope.$on('object-refreshed', function (e, caseInfo) {
            updateData(caseInfo);
        });

        var updateData = function (caseInfo) {
            $scope.caseInfo = caseInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.caseInfo.references;
            //gridHelper.hidePagingControlsIfAllDataShown($scope.caseInfo.references.length);
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);