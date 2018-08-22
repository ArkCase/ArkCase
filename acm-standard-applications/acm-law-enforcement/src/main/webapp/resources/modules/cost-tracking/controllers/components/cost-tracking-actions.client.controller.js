'use strict';

angular.module('cost-tracking').controller(
        'CostTracking.ActionsController',
        [ '$scope', '$state', '$stateParams', 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService', '$modal', 'FormsType.Service',
                function($scope, $state, $stateParams, Util, ConfigService, CostTrackingInfoService, HelperObjectBrowserService, $modal, FormsTypeService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cost-tracking",
                        componentId: "actions",
                        retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo,
                        validateObjectInfo: CostTrackingInfoService.validateCostsheet,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.editCostsheetParams = {
                            id: objectInfo.id
                        };
                    };

                    FormsTypeService.isAngularFormType().then(function(isAngularFormType) {
                        $scope.isAngularFormType = isAngularFormType;
                    });

                    FormsTypeService.isFrevvoFormType().then(function(isFrevvoFormType) {
                        $scope.isFrevvoFormType = isFrevvoFormType;
                    });

                    $scope.refresh = function() {
                        $scope.$emit('report-object-refreshed', $stateParams.id);
                    };

                    $scope.isVisible = function() {
                        return !Util.isEmpty($scope.objectInfo) && $scope.objectInfo.status === 'DRAFT';
                    };

                    $scope.newCostsheet = function() {
                        var params = {
                            isEdit: false
                        };
                        showModal(params);
                    };

                    $scope.editCostsheet = function() {
                        $scope.editCaseParams = {
                            isEdit: true,
                            costsheet: $scope.objectInfo
                        };
                        showModal($scope.editCaseParams, true);
                    };

                    function showModal(params) {
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/cost-tracking/views/components/cost-tracking-new-costsheet-modal.client.view.html',
                            controller: 'CostTracking.NewCostsheetController',
                            size: 'lg',
                            resolve: {
                                modalParams: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            //Do nothing
                        });
                    }
                    ;
                } ]);