'use strict';

angular.module('cost-tracking').controller('CostTracking.InfoController', ['$scope'
    , 'ConfigService', 'Object.InfoService', 'ObjectService'
    , function ($scope, ConfigService, ObjectInfoService, ObjectService) {

        ConfigService.getComponentConfig("cost-tracking", "info").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.parentInfo = {};
        //$scope.costsheetSolr = null;
        //$scope.costsheetInfo = null;
        $scope.$on('costsheet-selected', function onSelectedCase(e, selectedTimesheet) {
            $scope.costsheetSolr = selectedTimesheet;
        });

        $scope.$on('costsheet-updated', function (e, data) {
            $scope.costsheetInfo = data;

            if ($scope.costsheetInfo.parentType == ObjectService.ObjectTypes.CASE_FILE) {
                ObjectInfoService.get({
                        type: "casefile",
                        id: $scope.costsheetInfo.parentId
                    },
                    function (data) {
                        $scope.parentInfo.title = data.title;
                        $scope.parentInfo.incidentDate = moment(data.created).format($scope.config.parentDateFormat);
                        $scope.parentInfo.priortiy = data.priority;
                        $scope.parentInfo.type = data.caseType;
                        $scope.parentInfo.status = data.status;
                    });
            } else if ($scope.costsheetInfo.parentType == ObjectService.ObjectTypes.COMPLAINT) {
                ObjectInfoService.get({
                        type: "complaint",
                        id: $scope.costsheetInfo.parentId
                    },
                    function (data) {
                        $scope.parentInfo.title = data.complaintTitle;
                        $scope.parentInfo.incidentDate = moment(data.incidentDate).format($scope.config.parentDateFormat);
                        $scope.parentInfo.priortiy = data.priority;
                        $scope.parentInfo.type = data.complaintType;
                        $scope.parentInfo.status = data.status;
                    });
            }
        });

        $scope.onClickTitle = function () {
            ObjectService.gotoUrl($scope.costsheetInfo.parentType, $scope.costsheetInfo.parentId);
        }
    }
]);