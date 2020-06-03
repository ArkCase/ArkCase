'use strict';

angular.module('consultations').controller('Consultations.NewFutureTaskController', [ '$scope', '$modal', 'ConfigService', 'UtilService', function($scope, $modal, ConfigService, Util) {

    $scope.futureTaskConfig = null;
    $scope.userSearchConfig = null;

    ConfigService.getModuleConfig("consultations").then(function(moduleConfig) {

        $scope.futureTaskConfig = _.find(moduleConfig.components, {
            id: "newFutureTask"
        });
        $scope.userSearchConfig = _.find(moduleConfig.components, {
            id: "userSearch"
        });

        return moduleConfig;
    });

    $scope.addNewFutureTask = function() {
        var returnUserGroup = {
            pickedUserId: $scope.pickedUserId,
            pickedUserName: $scope.pickedUserName,
            pickedGroupId: $scope.pickedGroupId,
            pickedGroupName: $scope.pickedUserName,
            futureTaskTitle: $scope.futureTaskTitle,
            futureTaskDetails: $scope.futureTaskDetails
        };
        $scope.onModalClose(returnUserGroup)
    };

    $scope.cancelModal = function() {
        $scope.onModalDismiss();
    }

    $scope.userOrGroupSearch = function() {
        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'modules/consultations/views/components/consultation-user-search.client.view.html',
            controller: 'Consultations.UserSearchController',
            size: 'lg',
            backdrop: 'static',
            resolve: {
                $filter: function() {
                    return $scope.futureTaskConfig.userOrGroupSearch.userOrGroupFacetFilter;
                },
                $extraFilter: function() {
                    return $scope.futureTaskConfig.userOrGroupSearch.userOrGroupFacetExtraFilter;
                },
                $config: function() {
                    return $scope.userSearchConfig;
                }
            }
        });

        modalInstance.result.then(function(chosenUserOrGroup) {
            if (chosenUserOrGroup) {
                var selectedObjectType = chosenUserOrGroup.masterSelectedItem.object_type_s;
                if (selectedObjectType === 'USER') { //Selected User
                    var selectedUser = chosenUserOrGroup.masterSelectedItem;
                    var selectedGroup = chosenUserOrGroup.detailSelectedItems;
                    $scope.pickedUserId = selectedUser.object_id_s;
                    $scope.pickedUserName = selectedUser.name;
                    if (selectedGroup) {
                        $scope.pickedGroupId = selectedGroup.object_id_s;
                        $scope.pickedGroupName = selectedGroup.name;
                    }
                } else if (selectedObjectType === 'GROUP') { //Selected Group
                    var selectedUser = chosenUserOrGroup.detailSelectedItems;
                    var selectedGroup = chosenUserOrGroup.masterSelectedItem;
                    if (selectedUser) {
                        $scope.pickedUserId = selectedUser.object_id_s;
                        $scope.pickedUserName = selectedUser.name;
                    }
                    $scope.pickedGroupId = selectedGroup.object_id_s;
                    $scope.pickedGroupName = selectedGroup.name;
                }

                return;
            }

        }, function() {
            // Cancel button was clicked.
            return [];
        });
    };
} ]);