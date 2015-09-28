'use strict';

angular.module('cases').controller('Cases.CostController', ['$scope', '$stateParams', '$q', '$window', '$translate', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, $window, $translate, Util, Validator, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'cost');


        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'cost') {
                $scope.config = config;

                Util.uiGrid.typicalOptions(config, $scope);
                $scope.gridOptions.columnDefs = config.columnDefs;

                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if ("name" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].cellTemplate = "<a href='#' ng-click='grid.appScope.showUrl($event, row.entity)'>{{row.entity.acm$_formName}}</a>";
                    } else if ("tally" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].field = "acm$_costs";
                    }
                }
            }
        }


        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;

                CasesService.queryCostsheets({
                    objectType: "CASE_FILE",
                    objectId: $scope.caseInfo.id
                }, function (data) {
                    if (Validator.validateCostsheets(data)) {
                        var costsheets = data;
                        for (var i = 0; i < costsheets.length; i++) {
                            costsheets[i].acm$_formName = $translate.instant("cases.comp.cost.formNamePrefix") + " " + Util.goodValue(costsheets[i].parentNumber);
                            costsheets[i].acm$_costs = _.reduce(Util.goodArray(costsheets[i].costs), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions.data = costsheets;
                        $scope.gridOptions.totalItems = Util.goodValue(costsheets.length, 0);
                    }

                });
            } //end validate
        });


        $scope.showUrl = function (event, rowEntity) {
            event.preventDefault();
            Util.uiGrid.showObject("COSTSHEET", Util.goodMapValue([rowEntity, "id"], 0), $scope);
        };
    }
]);