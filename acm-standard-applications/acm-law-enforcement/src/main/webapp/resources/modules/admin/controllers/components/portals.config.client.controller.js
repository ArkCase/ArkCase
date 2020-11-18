'use strict';

angular.module('admin').controller(
        'Admin.PortalsController',
        [ '$scope', 'Helper.UiGridService', 'Admin.TimesheetConfigurationService', 'MessageService', 'UtilService', 'Object.LookupService', 'Admin.PortalConfigurationService', '$translate', 'ConfigService', '$modal',
            function($scope, HelperUiGridService, TimesheetConfigurationService, MessageService, Util, ObjectLookupService, AdminPortalConfigurationService, $translate, ConfigService, $modal) {

                    $scope.portalConfigDataModel = {};
                    $scope.portalAuthenticatedMode = {};

                    var getAuthenticatedMode = function () {
                        AdminPortalConfigurationService.getAuthenticatedMode().then(function (response) {
                            if (!Util.isEmpty(response.data)) {
                                $scope.portalAuthenticatedMode["portal.authenticatedMode"] = response.data["portal.authenticatedMode"];
                            }
                        });
                    };
                    getAuthenticatedMode();

                    var getPortalConfig = function () {
                        AdminPortalConfigurationService.getPortalConfig().then(function (response) {
                            if (!Util.isEmpty(response.data)) {
                                $scope.portalConfigDataModel["portal.url"] = response.data["portal.url"];
                                $scope.portalConfigDataModel["portal.groupName"] = response.data["portal.groupName"];
                                $scope.portalConfigDataModel["portal.description"] = response.data["portal.description"];
                                $scope.portalConfigDataModel["portal.id"] = response.data["portal.id"];
                                $scope.portalConfigDataModel["portal.fullName"] = response.data["portal.fullName"];
                                $scope.portalConfigDataModel["portal.userId"] = response.data["portal.userId"];
                            }
                        });
                    };
                    getPortalConfig();

                    ConfigService.getModuleConfig("admin").then(function(moduleConfig) {
                        $scope.configUser = _.find(moduleConfig.components, {
                            id: "userSearch"
                        });

                    });
                    ConfigService.getModuleConfig("admin").then(function(moduleConfig) {
                        $scope.configGroup = _.find(moduleConfig.components, {
                            id: "groupSearch"
                        });
                    });

                    $scope.userSearch = function() {
                        var modalInstance = $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'modules/admin/views/components/portal-user-search.client.view.html',
                            controller: 'Admin.PortalUserSearchController',
                            size: 'lg',
                            resolve: {
                                $filter: function() {
                                    return $scope.configUser.userSearch.userFacetFilter;
                                },
                                $extraFilter: function() {
                                    return $scope.configUser.userSearch.userFacetExtraFilter;
                                },
                                $config: function() {
                                    return $scope.configUser;
                                }
                            }
                        });

                        modalInstance.result.then(function(selectedUser) {
                            if (selectedUser) {
                                $scope.portalConfigDataModel["portal.fullName"] = selectedUser.name;
                                $scope.portalConfigDataModel["portal.userId"] = selectedUser.object_id_s;
                            }
                        }, function() {
                            // Cancel button was clicked.
                            return [];
                        });
                    };

                    $scope.groupSearch = function() {
                        var modalInstance = $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'modules/admin/views/components/portal-group-search.client.view.html',
                            controller: 'Admin.PortalGroupSearchController',
                            size: 'lg',
                            resolve: {
                                $filter: function() {
                                    return $scope.configGroup.groupSearch.groupFacetFilter;
                                },
                                $extraFilter: function() {
                                    return $scope.configGroup.groupSearch.groupFacetExtraFilter;
                                },
                                $config: function() {
                                    return $scope.configGroup;
                                }
                            }
                        });

                        modalInstance.result.then(function(selectedGroup) {
                            if (selectedGroup) {
                                $scope.portalConfigDataModel["portal.groupName"] = selectedGroup.name;
                            }
                        }, function() {
                            // Cancel button was clicked.
                            return [];
                        });
                    };

                    $scope.savePortalConfig = function () {
                        AdminPortalConfigurationService.saveAuthenticatedMode($scope.portalAuthenticatedMode).then(function () {
                            MessageService.succsessAction();
                        }, function () {
                            MessageService.errorAction();
                        });
                    };

                    $scope.onClickOk = function () {
                        AdminPortalConfigurationService.savePortalConfig($scope.portalConfigDataModel).then(function () {
                            MessageService.succsessAction();
                        }, function () {
                            MessageService.errorAction();
                        });
                    };

                } ]);
