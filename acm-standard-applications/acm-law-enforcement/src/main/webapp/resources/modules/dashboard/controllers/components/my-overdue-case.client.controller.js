'use strict';

angular.module('dashboard.my-overdue-case').controller('Dashboard.MyOverdueCasesController', [ '$scope', '$translate', 'Authentication', 'Dashboard.DashboardService', '$state', 'ConfigService', function($scope, $translate, Authentication, DashboardService, $state, ConfigService) {

    var vm = this;
    vm.config = null;

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

    ConfigService.getComponentConfig("dashboard", "myOverdueCase").then(function(config) {

        vm.config = config;
        vm.gridOptions.columnDefs = config.columnDefs;
        vm.gridOptions.enableFiltering = config.enableFiltering;
        vm.gridOptions.paginationPageSizes = config.paginationPageSizes;
        vm.gridOptions.paginationPageSize = config.paginationPageSize;
        paginationOptions.pageSize = config.paginationPageSize;
        getPage();

        return config;
    });

    function getPage() {
        DashboardService.queryMyOverdueCases({
            sortBy: paginationOptions.sortBy,
            sortDir: paginationOptions.sortDir,
            startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
            pageSize: paginationOptions.pageSize
        }, function(data) {
            var requests = data.response.docs.map(function(request) {
                request.numDays = overdueDays(request.dueDate_tdt);
                return request;
            });
            vm.gridOptions.data = requests;
            vm.gridOptions.totalItems = data.response.numFound;
        });
    }

    function overdueDays(overdue) {
        var momentObject = moment(overdue);
        var today = moment();
        var days = 0;
        while (today >= momentObject) {
            momentObject.add(1, 'days');
            days += 1;
        }
        return days;
    }
    vm.onTopicClick = function(row) {
        $state.go('request-info', {
            id: row.entity.request_id_lcs
        }, true);
    }
} ]);