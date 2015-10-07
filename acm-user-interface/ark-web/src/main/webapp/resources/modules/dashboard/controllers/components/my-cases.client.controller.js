'use strict';

angular.module('dashboard.my-cases', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('myCases', {
                title: 'My Cases',
                description: 'Displays my cases',
                controller: 'Dashboard.MyCasesController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/my-cases.client.view.html'
            });
    })
    .controller('Dashboard.MyCasesController', ['$scope', 'Authentication', 'Dashboard.DashboardService',
        function ($scope, Authentication, DashboardService) {
            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'myCases');
            $scope.config = null;
            var userInfo = null;

            var paginationOptions = {
                pageNumber: 1,
                pageSize: 25,
                sortBy: 'id',
                sortDir: 'desc'
            };

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableSelectAll: false,
                enableRowHeaderSelection: false,
                useExternalPagination: true,
                useExternalSorting: true,
                multiSelect: false,
                noUnselect: false,
                columnDefs: [],
                onRegisterApi: function (gridApi) {
                    $scope.gridApi = gridApi;

                    gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                        if (sortColumns.length == 0) {
                            paginationOptions.sort = null;
                        } else {
                            paginationOptions.sortBy = sortColumns[0].name;
                            paginationOptions.sortDir = sortColumns[0].sort.direction;
                        }
                        getPage();
                    });
                    gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                        paginationOptions.pageNumber = newPage;
                        paginationOptions.pageSize = pageSize;
                        getPage();
                    });
                }
            };


            function applyConfig(e, componentId, config) {
                if (componentId == 'myCases') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.columnDefs;
                    $scope.gridOptions.enableFiltering = config.enableFiltering;
                    $scope.gridOptions.paginationPageSizes = config.paginationPageSizes;
                    $scope.gridOptions.paginationPageSize = config.paginationPageSize;
                    paginationOptions.pageSize = config.paginationPageSize;

                    Authentication.queryUserInfo(function (responseUserInfo) {
                        userInfo = responseUserInfo;
                        getPage();
                    });
                }
            }


            function getPage() {
                DashboardService.queryMyCases({
                        userId: userInfo.userId,
                        sortBy: paginationOptions.sortBy,
                        sortDir: paginationOptions.sortDir,
                        startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
                        pageSize: paginationOptions.pageSize
                    },
                    function (data) {
                        $scope.gridOptions.data = data.response.docs;
                        $scope.gridOptions.totalItems = data.response.numFound;
                    }
                );
            }
        }
    ]);