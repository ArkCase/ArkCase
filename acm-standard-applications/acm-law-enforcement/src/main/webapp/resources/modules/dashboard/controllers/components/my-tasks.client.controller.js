'use strict';

angular.module('dashboard.my-tasks')
    .controller('Dashboard.MyTasksController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService', 'ObjectService', '$state', 'Task.AlertsService', 'UtilService', 'Util.DateService',
        function ($scope, $translate, Authentication, DashboardService, ObjectService, $state, TaskAlertsService, Util, UtilDateService) {

            var vm = this;

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'myTasks');

            vm.config = null;
            var userInfo = null;
            var userGroups = null;
            var userGroupList = null;

            var paginationOptions = {
                pageNumber: 1,
                pageSize: 5,
                sortBy: 'id',
                sortDir: 'desc'
            };

            vm.gridOptions = {
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
                if (componentId == 'myTasks') {
                    vm.config = config;
                    vm.gridOptions.columnDefs = config.columnDefs;
                    vm.gridOptions.enableFiltering = config.enableFiltering;
                    vm.gridOptions.paginationPageSizes = config.paginationPageSizes;
                    vm.gridOptions.paginationPageSize = config.paginationPageSize;
                    paginationOptions.pageSize = config.paginationPageSize;

                    Authentication.queryUserInfo().then(function (responseUserInfo) {
                        userInfo = responseUserInfo;
                        var userGroups = _.filter(responseUserInfo.authorities, function (userGroup) {
                            return _.startsWith(userGroup, 'ROLE') == false;
                        });

                        userGroupList = userGroups.join(" OR ");
                        userGroupList = "(" + userGroupList + ")";
                        getPage();
                        return userInfo;
                    });
                }
            }

            function getPage() {
                DashboardService.queryMyTasks({
                        userId: userInfo.userId,
                        userGroupList: userGroupList,
                        sortBy: paginationOptions.sortBy,
                        sortDir: paginationOptions.sortDir,
                        startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
                        pageSize: paginationOptions.pageSize
                    },
                    function (data) {
                        vm.gridOptions.data = [];
                        vm.gridOptions.totalItems = data.response.numFound;

                        _.forEach(data.response.docs, function (value) {
                            value.status_lcs = value.status_lcs.toUpperCase();

                            if (Util.goodValue(value.dueDate_tdt)) {
                                value.dueDate_tdt = UtilDateService.isoToDate(value.dueDate_tdt);
                            }

                            //calculate to show alert icons if task is in overdue or deadline is approaching
                            value.isOverdue = TaskAlertsService.calculateOverdue(value.dueDate_tdt);
                            value.isDeadline = TaskAlertsService.calculateDeadline(value.dueDate_tdt);

                            vm.gridOptions.data.push(value);
                        });
                    }
                );
            }

            vm.onClickCaseComplaintId = function (objectType, objectId) {
                ObjectService.gotoUrl(objectType, objectId);
            };
        }
    ]);