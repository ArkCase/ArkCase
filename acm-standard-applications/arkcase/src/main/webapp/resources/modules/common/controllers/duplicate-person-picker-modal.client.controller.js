'use strict';

angular.module('common').controller(
    'Common.DuplicatePersonPickerController',
    ['$scope', '$modal', '$modalInstance', 'params',
        function ($scope, $modal, $modalInstance, params) {
            $scope.modalInstance = $modalInstance;
            $scope.config = params.config;
            $scope.config.data = params.people;
            $scope.isDefault = false;
            $scope.selectedItem = null;
            $scope.isRedirect = params.isRedirect;

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: $scope.config.columnDefs,
                onRegisterApi: function (gridApi) {
                    //set gridApi on scope
                    $scope.gridApi = gridApi;
                    gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                        $scope.selectedItem = row.entity;
                    });
                }
            };
            $scope.gridOptions.data = params.people;

            $scope.onClickOk = function () {
                $modalInstance.close($scope.selectedItem);
            };

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
        }]);
