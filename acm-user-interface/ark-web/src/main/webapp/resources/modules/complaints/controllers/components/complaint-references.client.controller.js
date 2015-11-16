'use strict';

angular.module('complaints').controller('Complaints.ReferencesController', ['$scope', '$window', 'UtilService', 'ValidationService', 'HelperService', 'LookupService',
    function ($scope, $window, Util, Validator, Helper, LookupService) {
        var z = 1;
        return;
        $scope.$emit('req-component-config', 'references');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("references" == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
            }
        });

        $scope.$on('complaint-retrieved', function (e, data) {
            $scope.complaintInfo = data;
            $scope.gridOptions.data = $scope.complaintInfo.references;
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.complaintInfo.references.length);
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            Helper.Grid.showObject($scope, targetType, targetId);
        };

    }
]);