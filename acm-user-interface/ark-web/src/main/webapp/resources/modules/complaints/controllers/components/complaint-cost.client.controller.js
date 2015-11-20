'use strict';

angular.module('complaints').controller('Complaints.CostController', ['$scope', '$stateParams', '$q', '$window', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'Complaint.InfoService',
    function ($scope, $stateParams, $q, $window, $translate, Store, Util, Validator, Helper, LookupService, ComplaintInfoService) {
        var z = 1;
        $scope.gridOptions = {};
        return;
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


        $scope.$on('complaint-updated', function (e, data) {
            if (Validator.validateComplaint(data)) {
                $scope.complaintInfo = data;

                var cacheComplaintCost = new Store.CacheFifo(Helper.CacheNames.CASE_COST_SHEETS);
                var cacheKey = Helper.ObjectTypes.CASE_FILE + "." + $scope.complaintInfo.id;
                var costsheets = cacheComplaintCost.get(cacheKey);
                Util.serviceCall({
                    service: ComplaintsService.queryCostsheets
                    , param: {
                        objectType: Helper.ObjectTypes.COMPLAINT,
                        objectId: $scope.complaintInfo.id
                    }
                    , result: costsheets
                    , onSuccess: function (data) {
                        if (Validator.validateCostsheets(data)) {
                            costsheets = data;
                            for (var i = 0; i < costsheets.length; i++) {
                                costsheets[i].acm$_formName = $translate.instant("complaints.comp.cost.formNamePrefix") + " " + Util.goodValue(costsheets[i].parentNumber);
                                costsheets[i].acm$_costs = _.reduce(Util.goodArray(costsheets[i].costs), function (total, n) {
                                    return total + Util.goodValue(n.value, 0);
                                }, 0);
                            }
                            cacheComplaintCost.put(cacheKey, costsheets);
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
            Helper.Grid.showObject($scope, Helper.ObjectTypes.COSTSHEET, Util.goodMapValue(rowEntity, "id", 0));
        };
    }
]);