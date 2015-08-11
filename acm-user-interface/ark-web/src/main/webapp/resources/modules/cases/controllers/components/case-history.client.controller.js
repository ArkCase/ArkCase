'use strict';

angular.module('cases').controller('CaseHistoryController', ['$scope', '$stateParams', 'CasesService',
    function ($scope, $stateParams, CasesService) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'history');
        $scope.config = null;
        $scope.gridOptions = {};

        function applyConfig(e, componentId, config) {
            if (componentId == 'history') {
                $scope.config = config;
                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    enableFiltering: config.enableFiltering,
                    multiSelect: false,
                    noUnselect: false,
                    columnDefs: config.columnDefs,
                    onRegisterApi: function (gridApi) {
                        $scope.gridApi = gridApi;
                    }
                };

                var id = $stateParams.id;
                CasesService.queryAudit({
                    id: id,
                    startWith: 0,
                    count: 10
                }, function (data) {
                    $scope.gridOptions.data = data.resultPage;
                    $scope.gridOptions.totalItems = data.totalCount;
                })
            }
        }
    }
]);