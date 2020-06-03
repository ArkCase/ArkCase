'use strict';

angular.module('consultations').controller(
    'Consultations.CostController',
    [ '$scope', '$stateParams', '$translate', '$state', 'UtilService', 'ObjectService', 'ConfigService', 'Object.CostService', 'Consultation.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', "Config.LocaleService", '$modal', 'FormsType.Service',
        function($scope, $stateParams, $translate, $state, Util, ObjectService, ConfigService, ObjectCostService, ConsultationInfoService, HelperUiGridService, HelperObjectBrowserService, LocaleService, $modal, FormsTypeService) {

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "consultations",
                componentId: "cost",
                retrieveObjectInfo: ConsultationInfoService.getCaseInfo,
                validateObjectInfo: ConsultationInfoService.validateCaseInfo,
                onConfigRetrieved: function(componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });


            $scope.isEditDisabled = function(rowEntity) {
                return rowEntity.status !== "DRAFT";
            };

            var onConfigRetrieved = function(config) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.disableGridScrolling(config);
                gridHelper.addButton(config, "edit", null, null, "isEditDisabled");

                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if ("name" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].cellTemplate = "<a data-ui-sref=\"cost-tracking.main({id: row.entity.id})\">{{row.entity.acm$_formName}}</a>";
                    } else if ("tally" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].field = "acm$_costs";
                    }
                }
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                var currentObjectId = Util.goodMapValue(objectInfo, "id");
                if (Util.goodPositive(currentObjectId, false)) {
                    $scope.newCostsheetParamsFromObject = {
                        objectId: objectInfo.id,
                        type: ObjectService.ObjectTypes.CONSULTATION,
                        objectNumber: objectInfo.consultationNumber
                    };
                    ObjectCostService.queryCostsheets(ObjectService.ObjectTypes.CONSULTATION, currentObjectId).then(function(costsheets) {
                        for (var i = 0; i < costsheets.length; i++) {
                            costsheets[i].acm$_formName = costsheets[i].user.fullName + " - " + costsheets[i].parentNumber;
                            costsheets[i].acm$_costs = _.reduce(Util.goodArray(costsheets[i].costs), function(total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = costsheets;
                        $scope.gridOptions.totalItems = Util.goodValue(costsheets.length, 0);

                        return costsheets;
                    });
                }
            };

            $scope.newCostsheet = function() {
                var params = {
                    isEdit: false,
                    parentType: $scope.newCostsheetParamsFromObject.type,
                    parentNumber: $scope.newCostsheetParamsFromObject.objectNumber,
                    parentId: $scope.newCostsheetParamsFromObject.objectId
                };
                showModal(params);
            };

            function showModal(params) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/cost-tracking/views/components/cost-tracking-new-costsheet-modal.client.view.html',
                    controller: 'CostTracking.NewCostsheetController',
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        modalParams: function() {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function(data) {
                    var addedCostsheet = data;
                    addedCostsheet.acm$_formName = addedCostsheet.user.fullName + " - " + addedCostsheet.parentNumber;
                    addedCostsheet.acm$_costs = _.reduce(Util.goodArray(addedCostsheet.costs), function(total, n) {
                        return total + Util.goodValue(n.value, 0);
                    }, 0);

                    var foundCostsheetIndex = -1;
                    for(var i=0; i< $scope.gridOptions.data.length; i++){
                        if($scope.gridOptions.data[i].id === addedCostsheet.id && $scope.gridOptions.data[i].parentId === addedCostsheet.parentId) {
                            foundCostsheetIndex = i;
                            break;
                        }
                    }
                    if(foundCostsheetIndex === -1){
                        $scope.gridOptions.data.push(addedCostsheet);
                    }
                    else {
                        $scope.gridOptions.data[foundCostsheetIndex] = addedCostsheet;
                    }
                });
            }

            $scope.editRow = function(rowEntity) {
                $scope.editCaseParams = {
                    isEdit: true,
                    costsheet: rowEntity
                };
                showModal($scope.editCaseParams);
            };

            $scope.currencySymbol = LocaleService.getCurrencySymbol();
            $scope.$bus.subscribe('$translateChangeSuccess', function(data) {
                $scope.currencySymbol = LocaleService.getCurrencySymbol(data.lang);
            });
        } ]);