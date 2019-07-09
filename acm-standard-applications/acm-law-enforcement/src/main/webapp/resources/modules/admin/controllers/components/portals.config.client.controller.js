'use strict';

angular.module('admin').controller(
        'Admin.PortalsController',
        [ '$scope', '$modal', 'Helper.UiGridService', 'Admin.TimesheetConfigurationService', 'MessageService', 'UtilService', 'Object.LookupService', 'Admin.PortalConfigurationService', '$translate',
                function($scope, $modal, HelperUiGridService, TimesheetConfigurationService, MessageService, Util, ObjectLookupService, AdminPortalConfigurationService, $translate) {

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });
                    $scope.config.$promise.then(function(config) {
                        var config = angular.copy(_.find(config.components, {
                            id: 'portalsConfiguration'
                        }));

                        $scope.config = config;

                        gridHelper.addButton(config, "edit");
                        gridHelper.addButton(config, "delete");
                        gridHelper.addButton(config, "copy");
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
                            paginationPageSizes: $scope.config.paginationPageSizes,
                            paginationPageSize: $scope.config.paginationPageSize,
                            data: []
                        };
                    });

                    $scope.portal = {};
                    var reloadGrid = function(portals) {
                        $scope.gridOptions.data = portals;
                    };

                    var getAndRefresh = function() {
                        AdminPortalConfigurationService.getPortals().then(function(response) {
                            if (!Util.isEmpty(response.data)) {
                                reloadGrid(response.data);
                            }
                        });
                    };
                    getAndRefresh();

                    function showModal(portal) {
                        var params = {};
                        params.portal = portal;

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/admin/views/components/portal.config.modal.client.view.html',
                            controller: 'Admin.PortalModalController',
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
                        var portal = {
                            portalUrl: '',
                            portalDescription: '',
                            fullName: '',
                            userId: '',
                            groupName: ''
                        };

                        showModal(portal).then(function(data) {

                            if (data.portal != undefined) {
                                portal = data.portal;
                                AdminPortalConfigurationService.savePortal(portal).then(function(response) {
                                    MessageService.info($translate.instant('admin.portals.portalsConfiguration.message.add'));
                                    getAndRefresh();
                                }, function(response) {
                                    MessageService.errorAction(error(response));
                                });
                            }
                        });
                    };

                    $scope.editRow = function(rowEntity) {
                        var portal = {
                            portalId: rowEntity.portalId,
                            portalUrl: rowEntity.portalUrl,
                            portalDescription: rowEntity.portalDescription,
                            fullName: rowEntity.fullName,
                            userId: rowEntity.userId,
                            groupName: rowEntity.groupName
                        };
                        showModal(portal).then(function(data) {

                            if (data.portal != undefined) {
                                portal = data.portal;
                                portal.portalId = rowEntity.portalId;
                                AdminPortalConfigurationService.updatePortal(portal).then(function(response) {

                                    MessageService.info($translate.instant('admin.portals.portalsConfiguration.message.edit'));
                                    getAndRefresh();
                                }, function(response) {
                                    MessageService.errorAction();

                                });

                            }
                        });
                    };
                    $scope.deleteRow = function(rowEntity) {
                        AdminPortalConfigurationService.deletePortal(rowEntity.portalId).then(function(response) {
                            MessageService.info($translate.instant('admin.portals.portalsConfiguration.message.delete'));
                            getAndRefresh();
                        }, function(response) {
                            MessageService.errorAction(response);
                        });
                    };
                    $scope.copyRow = function(rowEntity) {

                        var range = document.createRange();
                        range.selectNode(document.getElementById(rowEntity.portalId));
                        window.getSelection().addRange(range);
                        document.execCommand("copy");
                        alert("The portal ID is copied");
                        window.getSelection().removeAllRanges();

                    }


                } ]);
