'use strict';

angular.module('subscriptions').controller('Subscription.ResultsController', ['$scope', 'Subscription', 'SubscriptionService',
    function ($scope, ResultService, SubscriptionService) {
        $scope.$emit('req-component-config', 'results');

        $scope.start = '';
        $scope.pageSize = '';
        $scope.sort = {by: '', dir: 'asc'};
        $scope.filters = [];   //for future work
        $scope.queryString = '';
        $scope.config = null;
        $scope.gridOptions = {};
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'results') {
                $scope.config = config;
                $scope.start = config.searchParams.start;
                $scope.pageSize = config.searchParams.n;
                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    noUnselect: false,
                    paginationPageSizes: config.paginationPageSizes,
                    paginationPageSize: config.paginationPageSize,
                    useExternalPagination: true,
                    useExternalSorting: true,
                    //comment out filtering until service side supports it
                    ////enableFiltering: config.enableFiltering,
                    //enableFiltering: true,
                    useExternalFiltering: true,
                    columnDefs: config.columnDefs,
                    onRegisterApi: function (gridApi) {
                        $scope.gridApi = gridApi;


                        $scope.gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                            if (0 >= sortColumns.length) {
                                $scope.sort.by = null;
                                $scope.sort.dir = null;
                            } else {
                                $scope.sort.by = sortColumns[0].field;
                                $scope.sort.dir = sortColumns[0].sort.direction;
                            }
                            $scope.sortData();
                        });
                        $scope.gridApi.core.on.filterChanged($scope, function () {
                            var grid = this.grid;
                            $scope.filters = [];
                            for (var i = 0; i < grid.columns.length; i++) {
                                if (!_.isEmpty(grid.columns[i].filters[0].term)) {
                                    var filter = {};
                                    filter.by = grid.columns[i].field;
                                    filter.with = grid.columns[i].filters[0].term;
                                    $scope.filters.push(filter);
                                }
                            }
                            //$scope.updatePageData();
                        });
                        $scope.gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                            $scope.start = (newPage - 1) * pageSize;   //newPage is 1-based index
                            $scope.pageSize = pageSize;
                            if (!$scope.sort.dir) {
                                $scope.updatePageData();
                            }
                            else {
                                $scope.sortData();
                            }
                        });
                    }
                };

                $scope.pageSize = config.paginationPageSize;
            }
        }
        $scope.$watch(
                function () {
                    return ResultService.data;
                },
                function () {
                    if (ResultService.data) {
                        $scope.gridOptions.data = ResultService.data.response.docs;
                        $scope.gridOptions.totalItems = ResultService.data.response.numFound;
                        $scope.queryString = ResultService.queryString;
                    }
                }
        );
        $scope.sortData = function () {
            //if no facets selected
            if (!ResultService.filterParams)
            {
                SearchService.queryFacetedSearch({
                    input: $scope.queryString,
                    start: $scope.start,
                    n: $scope.pageSize,
                    s: $scope.sort.by + '%20' + $scope.sort.dir
                },
                function (data) {
                    ResultService.passData(data, $scope.queryString + '*', '');

                });
            }
            //if there is facets selected
            else {
                SubscriptionService.queryFilteredSearch({
                    input: $scope.queryString,
                    start: $scope.start,
                    n: $scope.pageSize,
                    filters: ResultService.filterParams,
                    s: $scope.sort.by + '%20' + $scope.sort.dir
                },
                function (data) {
                    ResultService.passData(data, $scope.queryString + '*', ResultService.filterParams);

                });
            }
        };

        $scope.updatePageData = function () {
            //if no facets selected
            if (!ResultService.filterParams)
            {
                SubscriptionService.queryFacetedSearch({
                    input: $scope.queryString,
                    start: $scope.start,
                    n: $scope.pageSize
                },
                function (data) {
                    ResultService.passData(data, $scope.queryString + '*', '');

                });
            }
            //if there is facets selected
            else {
                SubscriptionService.queryFilteredSearch({
                    input: $scope.queryString,
                    start: $scope.start,
                    n: $scope.pageSize,
                    filters: ResultService.filterParams
                },
                function (data) {
                    ResultService.passData(data, $scope.queryString + '*', ResultService.filterParams);

                });
            }
        };
    }
]);