'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$window', 'UtilService', 'ValidationService', 'HelperService', 'LookupService',
    function ($scope, $window, Util, Validator, Helper, LookupService) {
		$scope.$emit('req-component-config', 'references');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("references" == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
            }
        });

        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = Util.goodValue(data, {references: []});
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.caseInfo.references;
                Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.caseInfo.references.length);
            }
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            Helper.Grid.showObject($scope, targetType, targetId);
        };

	}
]);