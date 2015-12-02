'use strict';

angular.module('cost-tracking').controller('CostTracking.InfoController', ['$scope', 'Object.InfoService', 'ObjectService',
    function ($scope, ObjectInfoService, ObjectService) {
        $scope.$emit('req-component-config', 'info');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("info" == componentId) {
                $scope.config = config;
            }
        });

        $scope.parentInfo = {};
        $scope.costsheetSolr = null;
        $scope.costsheetInfo = null;
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