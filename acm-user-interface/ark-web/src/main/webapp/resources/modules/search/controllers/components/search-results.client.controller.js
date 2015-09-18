'use strict';

angular.module('search').controller('Search.ResultsController', ['$scope', 'resultService', 'SearchService',
    function ($scope, resultService, SearchService) {
        $scope.$emit('req-component-config', 'results');

        $scope.start = 0;
        $scope.pageSize = 10;
        $scope.sort = {by: "", dir: "asc"};
        $scope.filters = [];   //for future work
        $scope.queryString = '';
        $scope.config = null;
        $scope.gridOptions = {};
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'results') {
                $scope.config = config;
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
                    //useExternalFiltering: true,

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
                            $scope.updatePageData();
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
                            $scope.updatePageData();
                        });

                    }
                };

                $scope.pageSize = config.paginationPageSize;
            }
        }
        $scope.$on('queryComplete', function () {
            $scope.gridOptions.data = resultService.data.response.docs;
            $scope.gridOptions.totalItems = resultService.data.response.numFound;
            $scope.queryString = resultService.queryString;
        });

        $scope.updatePageData = function () {
            var sort = "";
            if ($scope.sort) {
                if (!_.isEmpty($scope.sort.by) && !_.isEmpty($scope.sort.dir)) {
                    sort = $scope.sort.by + "%20" + $scope.sort.dir;
                }
            }
            SearchService.queryFacetedSearch({
                input: $scope.queryString,
                start: $scope.start,
                n: $scope.pageSize,
                sort:sort
            },
            function (data) {
                resultService.passData(data,$scope.queryString+'*');
                
            });
        };

//        $scope.updatePageData = function () {
//
//            var sort = "";
//            if ($scope.sort) {
//                if (!_.isEmpty($scope.sort.by) && !_.isEmpty($scope.sort.dir)) {
//                    sort = $scope.sort.by + "%20" + $scope.sort.dir;
//                }
//            }
//
//            var searchFacets = SearchService.queryFacetedSearch({
//                },
//                function (data) {
//                    console.log(data);
////                    $scope.gridOptions.data = data.response.docs;
////                    $scope.gridOptions.totalItems = data.response.numFound;
//                });
//
//        };
    }
]);