'use strict';

angular.module('cases').controller('Cases.CostController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'ConfigService', 'Object.CostService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate
        , Util, ObjectService, ConfigService, ObjectCostService, HelperUiGridService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var promiseConfig = ConfigService.getComponentConfig("cases", "cost").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);

            for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                if ("name" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].cellTemplate = "<a data-ui-sref=\"cost-tracking.main({id: row.entity.id})\">{{row.entity.acm$_formName}}</a>";
                } else if ("tally" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].field = "acm$_costs";
                }
            }
            return config;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            ObjectCostService.queryCostsheets(ObjectService.ObjectTypes.CASE_FILE, currentObjectId).then(
                function (costsheets) {
                    promiseConfig.then(function (config) {
                        for (var i = 0; i < costsheets.length; i++) {
                            costsheets[i].acm$_formName = $translate.instant("cases.comp.cost.formNamePrefix") + " " + Util.goodValue(costsheets[i].parentNumber);
                            costsheets[i].acm$_costs = _.reduce(Util.goodArray(costsheets[i].costs), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = costsheets;
                        $scope.gridOptions.totalItems = Util.goodValue(costsheets.length, 0);
                        //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                        return config;
                    });
                    return costsheets;
                }
            );
        }
    }
]);