'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$window', 'UtilService', 'ValidationService', 'LookupService',
    function ($scope, $window, Util, Validator, LookupService) {
		$scope.$emit('req-component-config', 'references');


        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'references') {
                Util.AcmGrid.setColumnDefs($scope, config);
                Util.AcmGrid.setBasicOptions($scope, config);
            }
        });


        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = Util.goodValue(data, {references: []});
                $scope.gridOptions.data = $scope.caseInfo.references;
                Util.AcmGrid.hidePagingControlsIfAllDataShown($scope, $scope.caseInfo.references.length);
            }
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValueArr(rowEntity, "targetType");
            var targetId = Util.goodMapValueArr(rowEntity, "targetId");
            Util.AcmGrid.showObject($scope, targetType, targetId);
        };

	}
]);