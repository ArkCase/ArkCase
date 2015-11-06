'use strict';
/**
 * @ngdoc controller
 * @name dashboard.my-cases.controller:Dashboard.MyCasesController
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/dashboard/controllers/components/my-cases.client.controller.js modules/dashboard/controllers/components/my-cases.client.controller.js}
 *
 * Loads cases in the "My Cases" widget.
 */
angular.module('dashboard.my-cases', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('myCases', {
                title: 'My Cases',
                description: 'Displays my cases',
                controller: 'Dashboard.MyCasesController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/my-cases.client.view.html'
            });
    })
    .controller('Dashboard.MyCasesController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, Authentication, DashboardService) {

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'myCases');
            $scope.config = null;
            var userInfo = null;

            var paginationOptions = {
                pageNumber: 1,
                pageSize: 25,
                sortBy: 'id',
                sortDir: 'desc'
            };

            /**
             * @ngdoc method
             * @name openViewer
             * @methodOf dashboard.my-cases.controller:Dashboard.MyCasesController
             *
             * @param {Object} data from the current row of the ui-grid (including the file id)
             *
             * @description
             * This method opens the selected file in the snowbound viewer
             */
            $scope.openViewer = function (rowData) {
                if (rowData && rowData.entity.object_id_s) {
                    window.open(window.location.href.split('!')[0] + '!/cases/' + rowData.entity.object_id_s + '/main');
                }
            };

            $scope.gridOptions = {
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
                    $scope.gridApi = gridApi;

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
                if (componentId == 'myCases') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.columnDefs;
                    $scope.gridOptions.enableFiltering = config.enableFiltering;
                    $scope.gridOptions.paginationPageSizes = config.paginationPageSizes;
                    $scope.gridOptions.paginationPageSize = config.paginationPageSize;
                    paginationOptions.pageSize = config.paginationPageSize;

                    Authentication.queryUserInfo(function (responseUserInfo) {
                        userInfo = responseUserInfo;
                        getPage();
                    });
                }
            }


            function getPage() {
                DashboardService.queryMyCases({
                        userId: userInfo.userId,
                        sortBy: paginationOptions.sortBy,
                        sortDir: paginationOptions.sortDir,
                        startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
                        pageSize: paginationOptions.pageSize
                    },
                    function (data) {
                        $scope.gridOptions.data = data.response.docs;
                        $scope.gridOptions.totalItems = data.response.numFound;
                    }
                );
            }
        }
    ]);