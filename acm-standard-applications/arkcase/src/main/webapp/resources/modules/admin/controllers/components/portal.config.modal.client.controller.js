'use strict';

angular.module('admin').controller('Admin.PortalModalController', [ '$scope', '$modalInstance', 'params', 'Util.DateService', '$filter', '$modal', 'ConfigService', function($scope, $modalInstance, params, UtilDateService, $filter, $modal, ConfigService) {

    $scope.portal = {
        portalUrl: '',
        portalDescription: '',
        portalUserName: '',
        portalUserID: '',
        groupName: ''
    };

    $scope.portal = params.portal;
    $scope.portal.portalId = params.portal.portalId;
    $scope.portal.portalUrl = params.portal.portalUrl;
    $scope.portal.groupName = params.portal.groupName;
    $scope.portal.portalDescription = params.portal.portalDescription;

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
                $scope.portal.fullName = selectedUser.name;
                $scope.portal.userId = selectedUser.object_id_s;
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
                $scope.portal.groupName = selectedGroup.name;
                $scope.portal.groupId = selectedGroup.object_id_s;
            }
        }, function() {
            // Cancel button was clicked.
            return [];
        });
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {
        $modalInstance.close({
            portal: $scope.portal
        });
    };

} ]);