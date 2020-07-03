'use strict';

angular.module('common').controller(
    'Common.ShowDuplicates',
    ['$scope', '$modal', '$translate','$modalInstance', 'ConfigService', 'UtilService',
        function ($scope, $modal, $translate, $modalInstance, ConfigService, Util) {

            ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                $scope.commonConfig = moduleConfig;
                return moduleConfig;
            });
            $scope.modalInstance = $modalInstance;
            $scope.header = $translate.instant("common.duplicates.header");
            $scope.config = Util.goodMapValue($scope.commonConfig, "showDuplicates");
            $scope.data = data;


            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                multiSelect: true,
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

            $scope.gridOptions.data = params.data;

            $scope.onClickOk = function () {
                $modalInstance.close($scope.selectedItem);
            };

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

        }]);