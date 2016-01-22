'use strict';

angular.module('dashboard.my-tasks')
    .controller('Dashboard.MyTasksController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, Authentication, DashboardService) {

            var vm = this;

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'myTasks');

            vm.config = null;

            vm.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableSelectAll: false,
                enableRowHeaderSelection: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: []
            };

            function applyConfig(e, componentId, config) {
                if (componentId == 'myTasks') {
                    vm.config = config;
                    vm.gridOptions.columnDefs = config.columnDefs;
                    vm.gridOptions.enableFiltering = config.enableFiltering;
                    vm.gridOptions.paginationPageSizes = config.paginationPageSizes;
                    vm.gridOptions.paginationPageSize = config.paginationPageSize;

                    Authentication.queryUserInfo().then(function (userInfo) {
                        DashboardService.queryMyTasks({userId: userInfo.userId},
                            function (data) {
                                vm.gridOptions.data = data;
                            }
                        );
                        return userInfo;
                    });
                }
            }
        }
    ]);