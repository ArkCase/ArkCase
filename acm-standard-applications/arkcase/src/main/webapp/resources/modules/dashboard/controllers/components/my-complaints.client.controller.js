'use strict';
angular.module('dashboard.my-complaints').controller('Dashboard.MyComplaintsController', [ '$scope', '$translate', 'Authentication', 'Dashboard.DashboardService', 'Task.AlertsService', 'Util.DateService', 'ConfigService', 'params', 'UtilService', function($scope, $translate, Authentication, DashboardService, TaskAlertsService, UtilDateService, ConfigService, params, Util) {
    var vm = this;
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

    var rowTmpl = '<div ng-class="{\'overdue\':row.entity.isOverdue, \'deadline\':row.entity.isDeadline}"><div ng-repeat="(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name" class="ui-grid-cell" ng-class="{ \'ui-grid-row-header-cell\': col.isRowHeader }" ui-grid-cell></div></div>';

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

    if (!Util.isEmpty(params.description)) {
        $scope.$parent.model.description = " - " + params.description;
    } else {
        $scope.$parent.model.description = "";
    }

    ConfigService.getComponentConfig("dashboard", "myComplaints").then(function(config) {
        vm.config = config;
        vm.gridOptions.columnDefs = vm.config.columnDefs;
        vm.gridOptions.enableFiltering = vm.config.enableFiltering;
        vm.gridOptions.paginationPageSizes = vm.config.paginationPageSizes;
        vm.gridOptions.paginationPageSize = vm.config.paginationPageSize;
        paginationOptions.pageSize = vm.config.paginationPageSize;

        Authentication.queryUserInfo().then(function(responseUserInfo) {
            userInfo = responseUserInfo;
            var userGroups = _.filter(responseUserInfo.authorities, function(userGroup) {
                return _.startsWith(userGroup, 'ROLE') == false;
            });

            userGroupList = userGroups.join("\" OR \"");
            userGroupList = "(\"" + userGroupList + "\")";
            userGroupList = encodeURIComponent(userGroupList);
            getPage();
            return userInfo;
        });
    });

    function getPage() {
        DashboardService.queryMyComplaints({
            userId: userInfo.userId,
            userGroupList: userGroupList,
            sortBy: paginationOptions.sortBy,
            sortDir: paginationOptions.sortDir,
            startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
            pageSize: paginationOptions.pageSize
        }, function(data) {
            vm.gridOptions.data = [];
            vm.gridOptions.totalItems = data.response.numFound;

            _.forEach(data.response.docs, function(value) {
                value.status_lcs = value.status_lcs.toUpperCase();

                if (Util.goodValue(value.dueDate_tdt)) {
                    value.dueDate_tdt = UtilDateService.isoToLocalDateTime(value.dueDate_tdt);

                    //calculate to show alert icons if complaints is in overdue or deadline is approaching
                    value.isOverdue = TaskAlertsService.calculateOverdue(value.dueDate_tdt);
                    value.isDeadline = TaskAlertsService.calculateDeadline(value.dueDate_tdt);
                }

                vm.gridOptions.data.push(value);
            });
        });
    }
} ]);
