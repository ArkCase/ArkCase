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
                multiSelect: false,
                noUnselect: false,
                columnDefs: []
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