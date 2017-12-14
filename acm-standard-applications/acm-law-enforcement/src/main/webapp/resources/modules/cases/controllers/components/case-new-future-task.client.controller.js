'use strict';

angular.module('cases').controller('Cases.NewFutureTaskController', ['$scope', '$modal', 'ConfigService', 'UtilService',
    function ($scope, $modal, ConfigService, Util) {


        $scope.futureTaskConfig = null;
        $scope.userSearchConfig = null;

        ConfigService.getModuleConfig("cases").then(function (moduleConfig) {

            $scope.futureTaskConfig = _.find(moduleConfig.components, {id: "newFutureTask"});
            $scope.userSearchConfig = _.find(moduleConfig.components, {id: "userSearch"});

            return moduleConfig;
        });

        $scope.addNewFutureTask = function(){
            var returnUserGroup = {
                pickedUserId: $scope.pickedUserId,
                pickedUserName: $scope.pickedUserName,
                pickedUserGroup: $scope.pickedUserGroup,
                futureTaskTitle: $scope.futureTaskTitle,
                futureTaskDetails: $scope.futureTaskDetails
            };
            $scope.onModalClose(returnUserGroup)
        };

        $scope.cancelModal = function(){
            $scope.onModalDismiss();
        }

        $scope.userOrGroupSearch = function () {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/cases/views/components/case-user-search.client.view.html',
                controller: 'Tasks.UserSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.futureTaskConfig.userOrGroupSearch.userOrGroupFacetFilter;
                    },
                    $extraFilter: function () {
                        return $scope.futureTaskConfig.userOrGroupSearch.userOrGroupFacetExtraFilter;
                    },
                    $config: function () {
                        return $scope.userSearchConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenUserOrGroup) {
                if (chosenUserOrGroup) {
                    $scope.pickedUserId = chosenUserOrGroup.masterSelectedItem.object_id_s;
                    $scope.pickedUserName = chosenUserOrGroup.masterSelectedItem.name;
                    if(!Util.isEmpty(chosenUserOrGroup.detailSelectedItems)){
                        $scope.pickedUserGroup = chosenUserOrGroup.detailSelectedItems.object_id_s;
                    }
                    return;
                }

            }, function () {
                // Cancel button was clicked.
                return [];
            });
        };
    }
]);