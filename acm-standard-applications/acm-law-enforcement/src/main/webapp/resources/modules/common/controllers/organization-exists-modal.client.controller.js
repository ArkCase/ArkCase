    'use strict';

    angular.module('common').controller(
        'Common.OrganizationExistsModalController',
        ['$scope','$modal', '$modalInstance', 'params',
            function ($scope, $modal, $modalInstance, params) {
                $scope.modalInstance = $modalInstance;
                $scope.header = params.header;
                $scope.config = params.config;
                $scope.config.data = params.organizations;
                $scope.selectedItem = null;
                $scope.isFromNewOrganizationModal = params.isFromNewOrganizationModal? params.isFromNewOrganizationModal : false;

                var selectedItem = null;
                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    noUnselect: false,
                    columnDefs: $scope.config.columnDefs,
                    onRegisterApi: function(gridApi){
                        //set gridApi on scope
                        $scope.gridApi = gridApi;
                        gridApi.selection.on.rowSelectionChanged($scope,function(row){
                            $scope.selectedItem = row.entity;
                        });
                    }
                };
                $scope.gridOptions.data = params.organizations;

                $scope.onClickOk = function() {
                    $modalInstance.close($scope.selectedItem);
                };

                $scope.onClickCancel = function() {
                    $modalInstance.dismiss('Cancel');
                };

            }]);