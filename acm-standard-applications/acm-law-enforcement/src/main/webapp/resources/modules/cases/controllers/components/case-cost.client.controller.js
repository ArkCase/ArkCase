'use strict';

angular.module('cases').controller('Cases.CostController', ['$scope', '$stateParams', '$translate', '$state'
    , 'UtilService', 'ObjectService', 'ConfigService', 'Object.CostService', 'Case.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate, $state
        , Util, ObjectService, ConfigService, ObjectCostService, CaseInfoService
        , HelperUiGridService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "cost"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.addButton(config,"edit");

            for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                if ("name" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].cellTemplate = "<a data-ui-sref=\"cost-tracking.main({id: row.entity.id})\">{{row.entity.acm$_formName}}</a>";
                } else if ("tally" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].field = "acm$_costs";
                }
            }
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            var currentObjectId = Util.goodMapValue(objectInfo, "id");
            if (Util.goodPositive(currentObjectId, false)) {
            	$scope.newCostsheetParamsFromObject = {
            		objectId: objectInfo.id,
                    type: ObjectService.ObjectTypes.CASE_FILE
                }
                ObjectCostService.queryCostsheets(ObjectService.ObjectTypes.CASE_FILE, currentObjectId).then(
                    function (costsheets) {
                        for (var i = 0; i < costsheets.length; i++) {
                            costsheets[i].acm$_formName = $translate.instant("cases.comp.cost.formNamePrefix") + " " + Util.goodValue(costsheets[i].parentNumber);
                            costsheets[i].acm$_costs = _.reduce(Util.goodArray(costsheets[i].costs), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = costsheets;
                        $scope.gridOptions.totalItems = Util.goodValue(costsheets.length, 0);
                        
                        return costsheets;
                    }
                );
            }
        };
        $scope.editRow = function(rowEntity){
        	$state.go('frevvo.edit-costsheet',{id: rowEntity.id});
        }
    }
]);