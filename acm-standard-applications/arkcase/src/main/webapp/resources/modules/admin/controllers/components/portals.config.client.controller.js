'use strict';

angular.module('admin').controller(
        'Admin.PortalsController',
        [ '$scope', 'Helper.UiGridService', 'Admin.TimesheetConfigurationService', 'MessageService', 'UtilService', 'Object.LookupService',
            'Admin.PortalConfigurationService', '$translate', 'ConfigService', '$modal',
            function($scope, HelperUiGridService, TimesheetConfigurationService, MessageService, Util, ObjectLookupService,
                     AdminPortalConfigurationService, $translate, ConfigService, $modal) {

                    $scope.portalConfigDataModel = {};

                    var getPortalConfig = function () {
                        AdminPortalConfigurationService.getPortalConfig().then(function (response) {
                            if (!Util.isEmpty(response.data)) {
                                $scope.portalConfigDataModel = response.data;
                            }
                        });
                    };

                    getPortalConfig();

                    ConfigService.getModuleConfig("admin").then(function(moduleConfig) {
                        $scope.configUser = _.find(moduleConfig.components, {
                            id: "userSearch"
                        });
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
                        AdminPortalConfigurationService.savePortalConfig($scope.portalConfigDataModel).then(function () {
                            MessageService.succsessAction();
                        }, function () {
                            MessageService.errorAction();
                        });
                    };

                }]);
