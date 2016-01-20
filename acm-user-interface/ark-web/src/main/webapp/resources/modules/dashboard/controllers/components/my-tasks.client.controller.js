'use strict';

angular.module('dashboard.my-tasks', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('myTasks', {
                title: 'My Tasks',
                description: 'Displays my tasks',
                controller: 'Dashboard.MyTasksController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/my-tasks.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/my-tasks-edit.client.view.html'
                }
            }
        );
    })
    .controller('Dashboard.MyTasksController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService', 'Helper.UiGridService', 'UtilService', 'ObjectService',
        function ($scope, $translate, Authentication, DashboardService, HelperUiGridService, Util, ObjectService) {

            var gridHelper = new HelperUiGridService.Grid({scope: $scope});

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'myTasks');
            $scope.config = null;
            //var userInfo = null;

            $scope.onClickObjLink = function (event, rowEntity) {
                event.preventDefault();
                var targetType = ObjectService.ObjectTypes.TASK;
                var targetId = Util.goodMapValue(rowEntity, "taskId");
                gridHelper.showObject(targetType, targetId);
            };

            $scope.gridOptions = {
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
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.columnDefs;
                    $scope.gridOptions.enableFiltering = config.enableFiltering;
                    $scope.gridOptions.paginationPageSizes = config.paginationPageSizes;
                    $scope.gridOptions.paginationPageSize = config.paginationPageSize;

                    Authentication.queryUserInfo().then(function (userInfo) {
                        DashboardService.queryMyTasks({userId: userInfo.userId},
                            function (data) {
                                $scope.gridOptions.data = data;
                            }
                        );
                        return userInfo;
                    });
                    //Authentication.queryUserInfo(function (responseUserInfo) {
                    //    userInfo = responseUserInfo;
                    //
                    //    DashboardService.queryMyTasks({userId: userInfo.userId},
                    //        function (data) {
                    //            $scope.gridOptions.data = data;
                    //        }
                    //    );
                    //
                    //});
                }
            }
        }
    ]);