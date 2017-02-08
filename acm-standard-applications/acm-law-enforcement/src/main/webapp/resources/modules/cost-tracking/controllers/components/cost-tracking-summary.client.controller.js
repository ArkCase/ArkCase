'use strict';

angular.module('cost-tracking').controller('CostTracking.SummaryController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, HelperUiGridService, CostTrackingInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cost-tracking"
            , componentId: "summary"
            , retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo
            , validateObjectInfo: CostTrackingInfoService.validateCostsheet
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        ConfigService.getModuleConfig("common").then(function (moduleConfig) {
            var customization = Util.goodMapValue(moduleConfig, "customization", {});
            if (customization) {
                $scope.customization = customization;
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            var parentNumber = {parentNumber: $scope.objectInfo.parentNumber};
            var parentType = {parentType: $scope.objectInfo.parentType};
            var parentId = {parentId: $scope.objectInfo.parentId};

            var costs = angular.copy($scope.objectInfo.costs);
            costs = costs.map(function (obj) {
                return angular.extend(obj, parentNumber, parentType, parentId);
            });
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = costs;
        };

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "parentType");
            var targetId = Util.goodMapValue(rowEntity, "parentId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);