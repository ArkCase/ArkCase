'use strict';

angular.module('cost-tracking').controller('CostTracking.InfoController', ['$scope', 'UtilService'
    , 'ConfigService', 'Object.InfoService', 'ObjectService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, Util
        , ConfigService, ObjectInfoService, ObjectService, CostTrackingInfoService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("cost-tracking", "info").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.parentInfo = {};
        //$scope.costsheetSolr = null;
        //$scope.costsheetInfo = null;
        //$scope.$on('costsheet-selected', function onSelectedCase(e, selectedTimesheet) {
        //    $scope.costsheetSolr = selectedTimesheet;
        //});

        $scope.$on('object-updated', function (e, data) {
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
        //var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        //if (Util.goodPositive(currentObjectId, false)) {
        //    CostTrackingInfoService.getCostsheetInfo(currentObjectId).then(function (costsheetInfo) {
        //        $scope.costsheetInfo = costsheetInfo;
        //        if ($scope.costsheetInfo.parentType == ObjectService.ObjectTypes.CASE_FILE) {
        //            ObjectInfoService.get({
        //                    type: "casefile",
        //                    id: $scope.costsheetInfo.parentId
        //                },
        //                function (data) {
        //                    $scope.parentInfo.title = data.title;
        //                    $scope.parentInfo.incidentDate = moment(data.created).format($scope.config.parentDateFormat);
        //                    $scope.parentInfo.priortiy = data.priority;
        //                    $scope.parentInfo.type = data.caseType;
        //                    $scope.parentInfo.status = data.status;
        //                });
        //        } else if ($scope.costsheetInfo.parentType == ObjectService.ObjectTypes.COMPLAINT) {
        //            ObjectInfoService.get({
        //                    type: "complaint",
        //                    id: $scope.costsheetInfo.parentId
        //                },
        //                function (data) {
        //                    $scope.parentInfo.title = data.complaintTitle;
        //                    $scope.parentInfo.incidentDate = moment(data.incidentDate).format($scope.config.parentDateFormat);
        //                    $scope.parentInfo.priortiy = data.priority;
        //                    $scope.parentInfo.type = data.complaintType;
        //                    $scope.parentInfo.status = data.status;
        //                });
        //        }
        //        return costsheetInfo;
        //    });
        //}

        $scope.onClickTitle = function () {
            ObjectService.gotoUrl($scope.costsheetInfo.parentType, $scope.costsheetInfo.parentId);
        }
    }
]);