'use strict';

angular.module('admin').controller('Admin.RecycleBin',
        [ '$scope', '$modal', 'Helper.UiGridService', 'Admin.RecycleBinService', 'MessageService', 'UtilService', 'Dialog.BootboxService', '$translate', 'Util.DateService', 'ObjectService', '$filter', function($scope, $modal, HelperUiGridService, AdminRecycleBinService, MessageService, Util, DialogService, $translate, UtilDateService, ObjectService, $filter) {

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            var paginationOptions = {
                pageNumber: 1,
                pageSize: 20,
                sortBy: 'create_date_tdt',
                sortDir: 'desc'
            };

            $scope.selectedRows = [];
            $scope.config.$promise.then(function(config) {
                var config = angular.copy(_.find(config.components, {
                    id: 'recycleBin'
                }));

                $scope.config = config;

                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.disableGridScrolling(config);

                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    useExternalPagination: true,
                    useExternalSorting: true,
                    multiSelect: true,
                    noUnselect: false,
                    columnDefs: $scope.config.columnDefs,
                    paginationPageSizes: $scope.config.paginationPageSizes,
                    paginationPageSize: $scope.config.paginationPageSize,
                    totalItems: 0,
                    data: [],
                    onRegisterApi: function(gridApi) {
                        $scope.gridApi = gridApi;
                        gridApi.selection.on.rowSelectionChanged($scope, function(row) {
                            $scope.selectedRows = gridApi.selection.getSelectedRows();
                        });

                        gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                            if (sortColumns.length == 0) {
                                paginationOptions.sort = null;
                            } else {
                                paginationOptions.sortBy = generateColumnNameBySolr(sortColumns[0].name);
                                paginationOptions.sortDir = sortColumns[0].sort.direction;
                            }
                            sortColumns === [];
                            getPage(paginationOptions);
                        });

                        gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
                            paginationOptions.pageNumber = newPage;
                            paginationOptions.pageSize = pageSize;
                            getPage(paginationOptions);
                        });
                    }
                };
            });

            function getPage(paginationOptions) {
                AdminRecycleBinService.findRecycleBinItems({
                    sortBy: paginationOptions.sortBy,
                    sortDir: paginationOptions.sortDir,
                    start: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
                    maxRows: paginationOptions.pageSize
                }).then(function (response) {
                    if (!Util.isEmpty(response.data)) {
                        $scope.gridData = response.data.recycleBinItems;
                        $scope.gridOptions.totalItems = response.data.numRecycleBinItems;
                        reloadGrid($scope.gridData);
                    }
                });
            }

            function generateColumnNameBySolr(columnName){
                if (columnName === 'fileSizeBytes') {
                    return 'object_item_size_l';
                }
                else if (columnName === 'fileActiveVersionNameExtension') {
                    return 'item_type_s';
                }
                else if (columnName === 'fileName') {
                    return 'object_name_s';
                }
                else if (columnName === 'dateModified') {
                    return 'modified_date_tdt';
                }
                else if (columnName === 'containerObjectTitle') {
                    return 'object_container_object_title_s';
                }
            }

            var reloadGrid = function (data) {
                $scope.gridOptions.data = data;
            };

            $scope.loadPage = function () {
                getPage(paginationOptions);
            };
            $scope.loadPage();


            $scope.contextmenuOptions = function(row) {
                if ($scope.selectedRows.length == 0) {
                    $scope.selectedRows.push(row.entity);
                }
                var contextMenuData = [];
                var restore = $translate.instant("contextMenu.options.restore");
                var deleteOption = $translate.instant("contextMenu.options.delete");

                contextMenuData.push([restore, function() {
                    AdminRecycleBinService.restoreItemsFromRecycleBin($scope.selectedRows).then(function(response) {
                        $scope.gridData = _.filter($scope.gridData, function (object) {
                            return response.config.data.indexOf(object) < 0;
                        });
                        reloadGrid($scope.gridData);
                    }, function () {
                            MessageService.error($translate.instant("admin.application.recycleBin.error"));
                    });
                    $scope.selectedRows = [];
                }]);

                contextMenuData.push([deleteOption, function() {
                    AdminRecycleBinService.removeItemsFromRecycleBin($scope.selectedRows).then(function(response) {
                        $scope.gridData = _.filter($scope.gridData, function (object) {
                            return response.config.data.indexOf(object) < 0;
                        });
                        reloadGrid($scope.gridData);
                    }, function() {
                            MessageService.error($translate.instant("admin.application.recycleBin.error"))
                    });
                    $scope.selectedRows = [];
                }]);
                return contextMenuData;
            };

            $scope.onClickObjLink = function(event, rowEntity) {
                event.preventDefault();
                var targetType = Util.goodMapValue(rowEntity, "containerObjectType");
                var targetId = Util.goodMapValue(rowEntity, "containerObjectTitle").split("_")[1];
                gridHelper.showObject(targetType, targetId);
            };

        }
]);