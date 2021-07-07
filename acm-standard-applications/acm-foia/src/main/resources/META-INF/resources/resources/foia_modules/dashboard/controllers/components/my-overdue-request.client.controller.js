'use strict';

angular.module('dashboard.my-overdue-requests').controller('Dashboard.MyOverdueRequestsController',
        [ '$scope', '$translate', 'Authentication', 'Dashboard.DashboardService', '$state', 'ConfigService', 'DueDate.Service', 'Admin.HolidayService', function($scope, $translate, Authentication, DashboardService, $state, ConfigService, DueDateService, AdminHolidayService) {

            var vm = this;
            vm.config = null;
            var userInfo = null;
            var requestQueue = "Release OR Hold";

            var paginationOptions = {
                pageNumber: 1,
                pageSize: 5,
                sortBy: 'id',
                sortDir: 'desc'
            };

            var rowTmpl = '<div class="overdue"><div ng-repeat="(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name" class="ui-grid-cell" ng-class="{ \'ui-grid-row-header-cell\': col.isRowHeader }" ui-grid-cell></div></div>';

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
                rowTemplate: rowTmpl,
                onRegisterApi: function(gridApi) {
                    vm.gridApi = gridApi;

                    gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
                        if (sortColumns.length == 0) {
                            paginationOptions.sort = null;
                        } else {
                            paginationOptions.sortBy = sortColumns[0].name;
                            paginationOptions.sortDir = sortColumns[0].sort.direction;
                        }
                        getPage();
                    });
                    gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
                        paginationOptions.pageNumber = newPage;
                        paginationOptions.pageSize = pageSize;
                        getPage();
                    });
                }
            };

            ConfigService.getComponentConfig("dashboard", "myOverdueRequests").then(function(config) {

                vm.config = config;
                vm.gridOptions.columnDefs = config.columnDefs;
                vm.gridOptions.enableFiltering = config.enableFiltering;
                vm.gridOptions.paginationPageSizes = config.paginationPageSizes;
                vm.gridOptions.paginationPageSize = config.paginationPageSize;
                paginationOptions.pageSize = config.paginationPageSize;
                
                Authentication.queryUserInfo().then(function (responseUserInfo) { 
                    userInfo = responseUserInfo;
                    getPage();
                    return userInfo;
                });
                

            });

            function getPage() {
                DashboardService.queryMyOverdueRequests({
                    userId: userInfo.userId,
                    queue: requestQueue,
                    sortBy: paginationOptions.sortBy,
                    sortDir: paginationOptions.sortDir,
                    startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
                    pageSize: paginationOptions.pageSize
                }, function(data) {
                    var requests = data.response.docs.map(function(request) {
                        AdminHolidayService.getHolidays().then(function(response) {
                            vm.holidays = response.data.holidays;
                            vm.includeWeekends = response.data.includeWeekends;
                            if (!vm.includeWeekends) {
                                vm.daysLeft = DueDateService.daysLeft(vm.holidays, request.dueDate_tdt);
                                vm.OverdueDays = DueDateService.calculateOverdueDays(new Date(request.dueDate_tdt), vm.daysLeft, vm.holidays);
                            } else {
                                vm.daysLeft = DueDateService.daysLeftWithWeekends(vm.holidays, request.dueDate_tdt);
                                vm.OverdueDays = DueDateService.calculateOverdueDaysWithWeekends(new Date(request.dueDate_tdt), vm.daysLeft, vm.holidays);
                            }
                            request.numDays = vm.OverdueDays.countOverdueDays;
                        });
                        return request;
                    });
                    vm.gridOptions.data = requests;
                    vm.gridOptions.totalItems = data.response.numFound;
                });
            }

        } ]);