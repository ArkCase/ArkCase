'use strict';

angular.module('complaints').controller('Complaints.CostController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConstantService', 'HelperService', 'Object.CostService'
    , function ($scope, $stateParams, $translate, Util, Constant, Helper, ObjectCostService) {

        var promiseConfig = Helper.requestComponentConfig($scope, "cost", function (config) {
            Helper.Grid.setColumnDefs($scope, config);
            Helper.Grid.setBasicOptions($scope, config);

            for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                if ("name" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].cellTemplate = "<a href='#' ng-click='grid.appScope.onClickObjLink($event, row.entity)'>{{row.entity.acm$_formName}}</a>";
                } else if ("tally" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].field = "acm$_costs";
                }
            }
        });

        if ($stateParams.id) {
            ObjectCostService.queryCostsheets(Constant.ObjectTypes.COMPLAINT, $stateParams.id).then(
                function (costsheets) {
                    promiseConfig.then(function (config) {
                        for (var i = 0; i < costsheets.length; i++) {
                            costsheets[i].acm$_formName = $translate.instant("components.comp.cost.formNamePrefix") + " " + Util.goodValue(costsheets[i].parentNumber);
                            costsheets[i].acm$_costs = _.reduce(Util.goodArray(costsheets[i].costs), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = costsheets;
                        $scope.gridOptions.totalItems = Util.goodValue(costsheets.length, 0);
                        Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                        return config;
                    });
                    return costsheets;
                }
            );
        }

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            Helper.Grid.showObject($scope, Constant.ObjectTypes.COSTSHEET, Util.goodMapValue(rowEntity, "id", 0));
        };
    }
]);