'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', 'UtilService', 'HelperService'
    , function ($scope, Util, Helper) {

		$scope.$emit('req-component-config', 'references');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("references" == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
            }
        });

        $scope.$on('case-updated', function (e, data) {
            $scope.caseInfo = data;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.caseInfo.references;
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.caseInfo.references.length);
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            Helper.Grid.showObject($scope, targetType, targetId);
        };

	}
]);