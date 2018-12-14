'use strict';

/**
 * @ngdoc controller
 * @name dashboard.my-cases.controller:Dashboard.UserWebsitesController
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/dashboard/controllers/components/audit-report-widget.client.controller.js modules/dashboard/controllers/components/audit-report-widget.client.controller.js}
 *
 * Loads sites in the "User Websites" widget.
 */
angular.module('dashboard.websites-widget', [ 'adf.provider' ]).config(function(ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget('userWebsites', {
        title: 'dashboard.widgets.userWebsites.title',
        description: 'dashboard.widgets.userWebsites.description',
        controller: 'Dashboard.UserWebsitesController',
        controllerAs: 'userWebsites',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/websites-widget.client.view.html',
        edit: {
            templateUrl: 'modules/dashboard/views/components/websites-widget-edit.client.view.html'
        }
    });
}).controller('Dashboard.UserWebsitesController',
        [ '$scope', '$window', '$modal', '$translate', 'config', 'Authentication', 'Dashboard.DashboardService', 'UtilService', 'Helper.UiGridService', 'ConfigService', function($scope, $window, $modal, $translate, config, Authentication, DashboardService, Util, HelperUiGridService, ConfigService) {

            var vm = this;
            vm.config = null;
            var userInfo = null;

            vm.gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            var paginationOptions = {
                pageNumber: 1,
                pageSize: 5
            };

            //Get the user's defined options from the Config.
            if (config.paginationPageSize) {
                paginationOptions.pageSize = parseInt(config.paginationPageSize);
            } else {
                //defaults the dropdown value on edit UI to the default pagination options
                config.paginationPageSize = "" + paginationOptions.pageSize + "";
            }

            vm.gridOptions = {
                rowHeight: 'auto',
                enableColumnResizing: true,
                enableRowSelection: true,
                enableSelectAll: false,
                enableRowHeaderSelection: false,
                useExternalPagination: false,
                useExternalSorting: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: [],
                onRegisterApi: function(gridApi) {
                    vm.gridApi = gridApi;
                }
            };

            ConfigService.getComponentConfig("dashboard", "userWebsites").then(function(config) {
                vm.config = config;
                vm.gridOptions.columnDefs = config.columnDefs;
                vm.gridOptions.enableFiltering = config.enableFiltering;
                vm.gridOptions.paginationPageSizes = config.paginationPageSizes;
                vm.gridOptions.paginationPageSize = paginationOptions.pageSize;
                vm.gridOptions.enableSorting = false;
                vm.gridOptions.enableColumnMenus = false;
                if (!_.find(config.columnDefs, 'name', 'act')) {
                    vm.gridHelper.addButton(config, "edit");
                    vm.gridHelper.addButton(config, "delete");
                }

                Authentication.queryUserInfo().then(function(responseUserInfo) {
                    userInfo = responseUserInfo;
                    loadSites(userInfo.userId);
                    return userInfo;
                });
            });

            vm.addNew = function() {
                showModal({}, false);
            };

            $scope.editRow = function(rowEntity) {
                var website = angular.copy(rowEntity);
                showModal(website, true);
            };

            $scope.deleteRow = function(rowEntity) {
                vm.gridHelper.deleteRow(rowEntity);
                for (var i = 0; i < vm.sitesList.length; i++) {
                    if (vm.sitesList[i].key == rowEntity.key) {
                        vm.sitesList.splice(i, 1);
                        break;
                    }
                }
                vm.gridOptions.totalItems--;
                vm.gridOptions.data = angular.copy(vm.sitesList);
                vm.saveData();
            };

            vm.saveData = function() {
                _.forEach(vm.sitesList, function(site) {
                    if (site.$$hashKey) {
                        delete site.$$hashKey;
                    }
                });
                vm.sitesInfo.json = JSON.stringify({
                    'sites': vm.sitesList
                });
                DashboardService.saveUserWebsites(vm.sitesInfo, function(data) {
                    if (data && data.id) {
                        vm.sitesInfo = data;
                        var jsonObj = JSON.parse(data.json);
                        vm.sitesList = jsonObj.sites;
                        vm.gridOptions.totalItems = vm.sitesList.length;
                        vm.gridOptions.data = angular.copy(vm.sitesList);
                    }
                });
            };

            function loadSites(userId) {
                DashboardService.queryUserWebsites({
                    userId: userId
                }, function(data) {
                    if (data && data[0]) {
                        vm.sitesInfo = data[0];
                        var jsonObj = JSON.parse(data[0].json);
                        vm.sitesList = jsonObj.sites;
                        vm.gridOptions.totalItems = vm.sitesList.length;
                        vm.gridOptions.data = angular.copy(vm.sitesList);
                    } else {
                        // This user does not have the dashboard data yet, create a new object
                        vm.sitesInfo = {
                            user: userId
                        };
                        vm.sitesList = [];
                        vm.gridOptions.totalItems = vm.sitesList.length;
                    }
                });
            }

            function showModal(website, isEdit) {
                var websiteDialog = $modal.open({
                    templateUrl: 'modules/dashboard/views/dialogs/websites-modal.client.view.html',
                    controller: 'Dashboard.WebsiteModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        websiteInfo: function() {
                            return website;
                        },
                        isEdit: function() {
                            return isEdit;
                        }
                    }
                });
                websiteDialog.result.then(function(data) {
                    if (data && data.name && data.url) {
                        if (isEdit) {
                            for (var i = 0; i < vm.sitesList.length; i++) {
                                if (vm.sitesList[i].key == data.key) {
                                    vm.sitesList[i].name = data.name;
                                    vm.sitesList[i].url = data.url;
                                    vm.sitesList[i].key = data.name + "_" + Math.random();
                                    break;
                                }
                            }
                        } else {
                            vm.sitesList.push({
                                name: data.name,
                                url: data.url,
                                key: data.name + "_" + Math.random()
                            });
                            vm.gridOptions.data = angular.copy(vm.sitesList);
                            vm.gridOptions.totalItems++;
                        }
                        vm.saveData();
                    }
                });
            }

            $scope.onClickObjLink = function(event, rowEntity) {
                event.preventDefault();
                $window.open('//' + rowEntity.url);
            }
        } ]);