'use strict';
/**
 * @ngdoc controller
 * @name dashboard.my-cases.controller:Dashboard.MyCasesController
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/dashboard/controllers/components/my-cases.client.controller.js modules/dashboard/controllers/components/my-cases.client.controller.js}
 *
 * Loads cases in the "My Cases" widget.
 */
angular.module('dashboard.my-cases')
    .controller('Dashboard.MyCasesController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, Authentication, DashboardService) {
            var vm = this;
            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'myCases');
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
            vm.openViewer = function (rowData) {
                if (rowData && rowData.entity.object_id_s) {
                    window.open(window.location.href.split('!')[0] + '!/cases/' + rowData.entity.object_id_s + '/main', '_self');
                }
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
                if (componentId == 'myCases') {
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
                DashboardService.queryMyCases({
                        userId: userInfo.userId,
                        userGroupList: userGroupList,
                        sortBy: paginationOptions.sortBy,
                        sortDir: paginationOptions.sortDir,
                        startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
                        pageSize: paginationOptions.pageSize
                    },
                    function (data) {
                        vm.gridOptions.data = data.response.docs;
                        vm.gridOptions.totalItems = data.response.numFound;
                    }
                );
            }
        }
    ]);