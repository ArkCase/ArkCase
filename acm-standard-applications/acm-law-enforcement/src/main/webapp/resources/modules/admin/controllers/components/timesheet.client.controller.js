'use strict';

angular.module('admin').controller('Admin.TimesheetController',
        [ '$scope', '$modal', 'Helper.UiGridService', 'Admin.TimesheetConfigurationService', 'MessageService', 'UtilService', 'Object.LookupService', function($scope, $modal, HelperUiGridService, TimesheetConfigurationService, MessageService, Util, ObjectLookupService) {

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            $scope.config.$promise.then(function(config) {
                var config = angular.copy(_.find(config.components, {
                    id: 'timesheetConfiguration'
                }));

                $scope.config = config;

                gridHelper.addButton(config, "edit");
                gridHelper.addButton(config, "delete");
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.disableGridScrolling(config);

                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    noUnselect: false,
                    columnDefs: $scope.config.columnDefs,
                    totalItems: 0,
                    data: []
                };
            });

            $scope.timesheetConfig = {
                chargeRoleItems: []
            };

            $scope.chargeRoleDropdownOptions = [];

            var reloadGrid = function() {
                $scope.gridOptions.data = $scope.timesheetConfig.chargeRoleItems;
            };

            ObjectLookupService.getLookupByLookupName("timesheetChargeRoles").then(function(chargeRoles) {
                $scope.chargeRoleDropdownOptions = chargeRoles;
            });

            TimesheetConfigurationService.getConfig().then(function(response) {
                if (!Util.isEmpty(response.data)) {
                    $scope.timesheetConfig = response.data;
                    reloadGrid();
                }
            });

            TimesheetConfigurationService.getProperties().then(function(response) {
                if (!Util.isEmpty(response.data)) {
                    $scope.timesheetProperties = response.data;
                }
            });

            var saveConfig = function() {
                TimesheetConfigurationService.saveConfig($scope.timesheetConfig).then(function (response) {
                    MessageService.succsessAction();
                }, function (reason) {
                    MessageService.errorAction();
                });
            };

            $scope.saveTimesheetProperties = function() {
                TimesheetConfigurationService.saveProperties($scope.timesheetProperties).then(function (response) {
                    MessageService.succsessAction();
                }, function (reason) {
                    MessageService.errorAction();
                });
            };

            function showModal(chargeRoleItem, isEdit, chargeRoleDropdownOptions) {
                var params = {};
                params.chargeRoleItem = chargeRoleItem;
                params.isEdit = isEdit;
                params.chargeRoleDropdownOptions = chargeRoleDropdownOptions;

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/timesheet.config.modal.client.view.html',
                    controller: 'Admin.TimesheetModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });
                return modalInstance.result;
            }

            $scope.addNew = function() {
                var chargeRoleItem = {
                    chargeRole: '',
                    rate: 0,
                    active: 'INACTIVE'
                };

                showModal(chargeRoleItem, false, $scope.chargeRoleDropdownOptions).then(function(data) {
                    var itemExist = false;
                    var chargeRoleElement;
                    for (var i = 0; i < $scope.timesheetConfig.chargeRoleItems.length; i++) {
                        chargeRoleElement = $scope.timesheetConfig.chargeRoleItems[i];
                        if (chargeRoleElement.chargeRole === data.chargeRoleItem.chargeRole && chargeRoleElement.rate === data.chargeRoleItem.rate && chargeRoleElement.active === data.chargeRoleItem.active) {
                            itemExist = true;
                        } else if (chargeRoleElement.chargeRole === data.chargeRoleItem.chargeRole && chargeRoleElement.active === 'ACTIVE' && data.chargeRoleItem.active === 'ACTIVE') {
                            chargeRoleElement.active = 'INACTIVE'
                        }
                    }

                    if (itemExist == false) {
                        $scope.timesheetConfig.chargeRoleItems.push(data.chargeRoleItem);
                        reloadGrid();
                        saveConfig();
                    } else {
                        MessageService.errorAction();
                    }
                });
            };

            $scope.editRow = function(rowEntity) {
                showModal(rowEntity, true, $scope.chargeRoleDropdownOptions).then(function(data) {
                    var itemExist = false;
                    var chargeRoleElement;
                    for (var i = 0; i < $scope.timesheetConfig.chargeRoleItems.length; i++) {
                        chargeRoleElement = $scope.timesheetConfig.chargeRoleItems[i];
                        if (chargeRoleElement.chargeRole === data.chargeRoleItem.chargeRole && chargeRoleElement.rate === data.chargeRoleItem.rate && chargeRoleElement.active === data.chargeRoleItem.active) {
                            itemExist = true;
                        } else if (chargeRoleElement.chargeRole === data.chargeRoleItem.chargeRole && chargeRoleElement.active === 'ACTIVE' && data.chargeRoleItem.active === 'ACTIVE') {
                            chargeRoleElement.active = 'INACTIVE'
                        }
                    }

                    if (itemExist == false) {
                        rowEntity.chargeRole = data.chargeRoleItem.chargeRole;
                        rowEntity.rate = data.chargeRoleItem.rate;
                        rowEntity.active = data.chargeRoleItem.active;

                        reloadGrid();
                        saveConfig();
                    } else {
                        MessageService.errorAction();
                    }
                });
            };

            $scope.deleteRow = function(rowEntity) {
                _.remove($scope.gridOptions.data, function(item) {
                    return item === rowEntity;
                });
                reloadGrid();
                saveConfig();
            };

        } ]);