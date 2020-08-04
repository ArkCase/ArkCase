'use strict';

angular.module('common').controller(
    'Common.ShowDuplicates',
    ['$scope', '$modal', '$translate', '$modalInstance', 'ConfigService', 'UtilService', 'params',
        function ($scope, $modal, $translate, $modalInstance, ConfigService, Util, params) {

            $scope.modalInstance = $modalInstance;
            $scope.gridOptions = {};
            $scope.header = $translate.instant("common.duplicates.header");
            $scope.data = params.data;
            $scope.moduleConfig = ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                $scope.config = Util.goodMapValue(moduleConfig, "showDuplicates");
                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    multiSelect: true,
                    noUnselect: false,
                    columnDefs: $scope.config.columnDefs,
                    data: $scope.data,
                    onRegisterApi: function (gridApi) {
                        //set gridApi on scope
                        $scope.gridApi = gridApi;
                        gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                            $scope.selectedItem = row.entity;
                        });
                    }
                };
            });


            $scope.onClickOk = function () {
                $modalInstance.close($scope.selectedItem);
            };

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

        }]);