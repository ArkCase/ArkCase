'use strict';

angular.module('cost-tracking').controller('CostTracking.ActionsController', ['$scope', '$state', 'CostTracking.InfoService',
    function ($scope, $state, CostTrackingInfoService) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('costsheet-updated', function (e, data) {
            if (CostTrackingInfoService.validateCostsheet(data)) {
                $scope.costsheetInfo = data;
            }
        });

        $scope.createNew = function () {
            $state.go("frevvo-new-costsheet", {
                name: "new-costsheet"
            });
            //$state.go('newCostsheet');
        };

        $scope.edit = function (costsheetInfo) {
            $state.go("frevvo-edit-costsheet", {
                name: "edit-costsheet",
                arg: {
                    id: costsheetInfo.id
                }
            });
            //$state.go('editCostsheet', { id : $scope.costsheetInfo.id});
        };

    }
]);