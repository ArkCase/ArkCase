'use strict';

angular.module('cases').controller('Cases.CostController', ['$scope', '$stateParams', '$q', '$window', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, $window, $translate, Store, Util, Validator, Helper, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'cost');
        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'cost') {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);

                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if ("name" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].cellTemplate = "<a href='#' ng-click='grid.appScope.onClickObjLink($event, row.entity)'>{{row.entity.acm$_formName}}</a>";
                    } else if ("tally" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].field = "acm$_costs";
                    }
                }
            }
        });


        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;

                var cacheCaseCost = new Store.CacheFifo(Helper.CacheNames.CASE_COST_SHEETS);
                var cacheKey = Util.Constant.OBJTYPE_CASE_FILE + "." + $scope.caseInfo.id;
                var costsheets = cacheCaseCost.get(cacheKey);
                Util.serviceCall({
                    service: CasesService.queryCostsheets
                    , param: {
                        objectType: Util.Constant.OBJTYPE_CASE_FILE,
                        objectId: $scope.caseInfo.id
                    }
                    , result: costsheets
                    , onSuccess: function (data) {
                        if (Validator.validateCostsheets(data)) {
                            costsheets = data;
                            for (var i = 0; i < costsheets.length; i++) {
                                costsheets[i].acm$_formName = $translate.instant("cases.comp.cost.formNamePrefix") + " " + Util.goodValue(costsheets[i].parentNumber);
                                costsheets[i].acm$_costs = _.reduce(Util.goodArray(costsheets[i].costs), function (total, n) {
                                    return total + Util.goodValue(n.value, 0);
                                }, 0);
                            }
                            cacheCaseCost.put(cacheKey, costsheets);
                            return costsheets;
                        }
                    }
                }).then(
                    function (costsheets) {
                        $scope.gridOptions.data = costsheets;
                        $scope.gridOptions.totalItems = Util.goodValue(costsheets.length, 0);
                        Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                        return costsheets;
                    }
                );
            } //end validate
        });


        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            Helper.Grid.showObject($scope, Util.Constant.OBJTYPE_COSTSHEET, Util.goodMapValue(rowEntity, "id", 0));
        };
    }
]);