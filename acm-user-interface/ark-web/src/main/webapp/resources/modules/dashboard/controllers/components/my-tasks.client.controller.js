'use strict';

angular.module('dashboard.my-tasks')
    .controller('Dashboard.MyTasksController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService', 'Helper.UiGridService', 'UtilService', 'ObjectService',
        function ($scope, $translate, Authentication, DashboardService, HelperUiGridService, Util, ObjectService) {

            var vm = this;
            var gridHelper = new HelperUiGridService.Grid({scope: $scope});

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'myTasks');
            vm.config = null;


            $scope.onClickObjLink = function (event, rowEntity) {
                event.preventDefault();
                var targetType = ObjectService.ObjectTypes.TASK;
                var targetId = Util.goodMapValue(rowEntity, "taskId");
                gridHelper.showObject(targetType, targetId);
            };

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