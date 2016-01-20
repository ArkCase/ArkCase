'use strict';

angular.module('cost-tracking').controller('CostTracking.InfoController', ['$scope', 'UtilService'
    , 'ConfigService', 'Object.InfoService', 'ObjectService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, Util
        , ConfigService, ObjectInfoService, ObjectService, CostTrackingInfoService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("cost-tracking", "info").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.$on('object-updated', function (e, data) {
            updateData(data);
        });

        $scope.$on('object-refreshed', function (e, costsheetInfo) {
            updateData(costsheetInfo);
        });

        $scope.parentInfo = {};
        var updateData = function (data) {
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
        };

        $scope.onClickTitle = function () {
            ObjectService.gotoUrl($scope.costsheetInfo.parentType, $scope.costsheetInfo.parentId);
        }
    }
]);