'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', 'UtilService', 'Helper.UiGridService', 'Case.InfoService'
    , function ($scope, Util, HelperUiGridService, CaseInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

		$scope.$emit('req-component-config', 'references');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("references" == componentId) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
            }
        });

        $scope.$on('case-updated', function (e, data) {
            if (CaseInfoService.validateCaseInfo(data)) {
                $scope.caseInfo = data;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.caseInfo.references;
                gridHelper.hidePagingControlsIfAllDataShown($scope.caseInfo.references.length);
            }
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

	}
]);