'use strict';

angular.module('common').controller(
    'Common.ShowDuplicates',
    ['$scope','$modal', '$modalInstance', 'params',
        function ($scope, $modal, $modalInstance, params) {
            $scope.modalInstance = $modalInstance;
            $scope.header = params.header;
            $scope.config = params.config;

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: false,
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

            $scope.onClickOk = function() {
                $modalInstance.close($scope.selectedItem);
            };

            $scope.onClickCancel = function() {
                $modalInstance.dismiss('Cancel');
            };

        }]);