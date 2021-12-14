'use strict';

angular.module('dashboard.my-tasks').controller(
        'Dashboard.MyTasksController',
        [ '$scope', '$translate', 'config', 'Authentication', 'Dashboard.DashboardService', 'ObjectService', '$state', 'Task.AlertsService', 'UtilService', 'Util.DateService', 'ConfigService', 'params',
                function($scope, $translate, config, Authentication, DashboardService, ObjectService, $state, TaskAlertsService, Util, UtilDateService, ConfigService, params) {
                    var vm = this;
                    vm.config = null;
                    var userInfo = null;
                    //var userGroups = null;
                    var userGroupList = null;

                    var paginationOptions = {
                        pageNumber: 1,
                        pageSize: 5,
                        sortBy: 'dueDate_tdt',
                        sortDir: 'asc'
                    };

                    //Get the user's defined options from the Config.
                    if (config.paginationPageSize) {
                        paginationOptions.pageSize = parseInt(config.paginationPageSize);
                    } else {
                        //defaults the dropdown value on edit UI to the default pagination options
                        config.paginationPageSize = "" + paginationOptions.pageSize + "";
                    }

                    var rowTmpl = '<div ng-class="{\'overdue\':row.entity.isOverdue, \'deadline\':row.entity.isDeadline}"><div ng-repeat="(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name" class="ui-grid-cell" ng-class="{ \'ui-grid-row-header-cell\': col.isRowHeader }" ui-grid-cell></div></div>';

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

                    if (!Util.isEmpty(params.description)) {
                        $scope.$parent.model.description = " - " + params.description;
                    } else {
                        $scope.$parent.model.description = "";
                    }

                    ConfigService.getComponentConfig("dashboard", "myTasks").then(function(config) {
                        vm.config = config;
                        vm.gridOptions.columnDefs = config.columnDefs;
                        vm.gridOptions.enableFiltering = config.enableFiltering;
                        vm.gridOptions.paginationPageSizes = config.paginationPageSizes;
                        vm.gridOptions.paginationPageSize = paginationOptions.pageSize;

                        Authentication.queryUserInfo().then(function(responseUserInfo) {
                            userInfo = responseUserInfo;
                            // userGroups = responseUserInfo.authorities;
                            // userGroupList = responseUserInfo.authorities[0];
                            // _.forEach(userGroups, function (group) {
                            // 	userGroupList = userGroupList + " OR " + group;
                            // });
                            // userGroupList = "(" + userGroupList + ")";

                            var userGroups = _.filter(responseUserInfo.authorities, function(userGroup) {
                                return _.startsWith(userGroup, 'ROLE') == false;
                            });

                            userGroupList = userGroups.join("\" OR \"");
                            userGroupList = "(\"" + userGroupList + "\")";
                            userGroupList = encodeURIComponent(userGroupList);
                            getPage();
                            return userInfo;
                        });
                        return config;
                    });

                    function getPage() {
                        DashboardService.queryMyTasks({
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
                                }

                                //calculate to show alert icons if task is in overdue or deadline is approaching
                                value.isOverdue = TaskAlertsService.calculateOverdue(value.dueDate_tdt);
                                value.isDeadline = TaskAlertsService.calculateDeadline(value.dueDate_tdt);

                                vm.gridOptions.data.push(value);
                            });
                        });
                    }

                    vm.onClickCaseComplaintId = function(objectType, objectId) {
                        ObjectService.showObject(objectType, objectId);
                    };
                } ]);
