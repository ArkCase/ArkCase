'use strict';

angular.module('cases').controller('Cases.NewFutureTaskController', ['$scope', '$modal', 'ConfigService',
    function ($scope, $modal, ConfigService) {


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
                templateUrl: 'modules/tasks/views/components/task-user-search.client.view.html',
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
                    if (chosenUserOrGroup.object_type_s === 'USER') {  // Selected a user
                        $scope.pickedUserId = chosenUserOrGroup.object_id_s;
                        $scope.pickedUserName = chosenUserOrGroup.name;
                        $scope.pickOwningGroup(chosenUserOrGroup.object_id_s, chosenUserOrGroup.name);

                        return;
                    }
                    else if (chosenUserOrGroup.object_type_s === 'GROUP') {
                        $scope.pickedUserGroup = chosenUserOrGroup.object_id_s;
                        return;
                    }
                }

            }, function () {
                // Cancel button was clicked.
                return [];
            });
        };

        $scope.pickOwningGroup = function (assigneeLdapId, asigneeName) {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/tasks/views/components/task-group-search.client.view.html',
                controller: 'Tasks.GroupSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.futureTaskConfig.groupSearch.groupFacetFilter + assigneeLdapId +$scope.futureTaskConfig.groupSearch.groupFacetExtraFilter;
                    },
                    $searchValue: function () {
                        return asigneeName;
                    },
                    $config: function () {
                        return $scope.userSearchConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenUserOrGroup) {
                $scope.pickedUserGroup = chosenUserOrGroup.object_id_s;

                return;
            }, function () {
                // Cancel button was clicked.
                return [];
            });


        };
    }
]);
