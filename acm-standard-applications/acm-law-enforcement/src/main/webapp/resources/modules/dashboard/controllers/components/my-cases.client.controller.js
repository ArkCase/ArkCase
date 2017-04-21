'use strict';
angular.module('dashboard.my-complaints')
    .controller('Dashboard.MyComplaintsController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, Authentication, DashboardService) {
            var vm = this;
            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'myComplaints');
            vm.config = null;
            var userInfo = null;
            var paginationOptions = {
                pageNumber: 1,
                pageSize: 5,
                sortBy: 'id',
                sortDir: 'desc'
            };
            vm.gridOptions = {
                appScopeProvider: vm,
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
                    vm.gridApi = gridApi;
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
                if (componentId == 'myComplaints') {
                    vm.config = config;
                    vm.gridOptions.columnDefs = vm.config.columnDefs;
                    vm.gridOptions.enableFiltering = vm.config.enableFiltering;
                    vm.gridOptions.paginationPageSizes = vm.config.paginationPageSizes;
                    vm.gridOptions.paginationPageSize = vm.config.paginationPageSize;
                    paginationOptions.pageSize = vm.config.paginationPageSize;
                    Authentication.queryUserInfo().then(function (responseUserInfo) {
                        userInfo = responseUserInfo;
                        getPage();
                        return userInfo;
                    });
                }
            }

            function getPage() {
                DashboardService.queryMyComplaints({
                        userId: userInfo.userId,
                        groupId: userInfo.groupId,
                        sortBy: paginationOptions.sortBy,
                        sortDir: paginationOptions.sortDir,
                        startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
                        pageSize: paginationOptions.pageSize
                    },
                    function (data) {
                        vm.gridOptions.data = data.response.docs;
                        vm.gridOptions.totalItems = data.response.numFound;
                    }
                );
            }
        }
    ]);