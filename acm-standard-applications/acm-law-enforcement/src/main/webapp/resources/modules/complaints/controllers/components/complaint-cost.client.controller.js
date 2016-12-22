'use strict';

angular.module('complaints').controller('Complaints.CostController', ['$scope', '$stateParams', '$translate', '$state'
    , 'UtilService', 'ObjectService', 'ConfigService', 'Object.CostService', 'Complaint.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate, $state
        , Util, ObjectService, ConfigService, ObjectCostService, ComplaintInfoService
        , HelperUiGridService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "cost"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.addButton(config, "edit");

            for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                if ("name" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].cellTemplate = "<a data-ui-sref=\"cost-tracking.main({id: row.entity.id})\">{{row.entity.acm$_formName}}</a>";
                } else if ("tally" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].field = "acm$_costs";
                }
            }
        };

        if (Util.goodPositive(componentHelper.currentObjectId, false)) {
        	$scope.newCostsheetParamsFromObject = {
        		objectId: componentHelper.currentObjectId,
                type: ObjectService.ObjectTypes.COMPLAINT
            }
        	ObjectCostService.queryCostsheets(ObjectService.ObjectTypes.COMPLAINT, componentHelper.currentObjectId).then(
                function (costsheets) {
                    componentHelper.promiseConfig.then(function (config) {
                        for (var i = 0; i < costsheets.length; i++) {
                            costsheets[i].acm$_formName = $translate.instant("complaints.comp.cost.formNamePrefix") + " " + Util.goodValue(costsheets[i].parentNumber);
                            costsheets[i].acm$_costs = _.reduce(Util.goodArray(costsheets[i].costs), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = costsheets;
                        $scope.gridOptions.totalItems = Util.goodValue(costsheets.length, 0);
                        return config;
                    });
                    return costsheets;
                }
            );
        }
        
        $scope.editRow = function(rowEntity){
        	$state.go('frevvo.edit-costsheet',{id: rowEntity.id});
        }
    }
]);