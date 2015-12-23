'use strict';

angular.module('document-details').controller('Document.ParticipantsController', ['$scope', '$stateParams', '$q', '$modal'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.ParticipantService', 'Authentication', 'MessageService', '$translate',
    function ($scope, $stateParams, $q, $modal, Util, ConfigService, HelperUiGridService, ObjectService, ObjectParticipantService, Authentication, messageService, $translate) {

        $scope.participantType = {};
        $scope.chosenUser = null;
        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        var promiseConfig = ConfigService.getComponentConfig("document-details", "participants").then(function (config) {
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.addEditButton(config.columnDefs, "grid.appScope.editRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
        });

        $scope.retrieveGridData = function () {
            if (Util.goodPositive($stateParams.id)) {
                var promiseQueryParticipants = ObjectParticipantService.retrieveParticipants(ObjectService.ObjectTypes.FILE, $stateParams.id);
                $q.all([promiseQueryParticipants, promiseUsers, promiseConfig]).then(function (data) {
                    var participants = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = participants;
                    $scope.gridOptions.totalItems = participants.length;
                });
            }
        };

        $scope.addNew = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/document-details/views/components/document-user-search.client.view.html',
                controller: 'Document.UserSearchController',
                size: 'lg',
                resolve: {
                    $scopeParticipant: function () {
                        return $scope;
                    }
                }
            });

            modalInstance.result.then(function (chosenUser) {
                if (chosenUser) {
                    $scope.chosenUser = chosenUser.name;
                    ObjectParticipantService.addNewParticipant($scope.chosenUser, $scope.participantType, ObjectService.ObjectTypes.FILE, $stateParams.id).then(
                        function (participantAdded) {
                            $scope.retrieveGridData();
                        }
                    );
                }
            }, function () {
                // Cancel button was clicked.
            });
        };

        $scope.deleteRow = function (rowEntity) {
            ObjectParticipantService.removeParticipant(rowEntity.participantLdapId, rowEntity.participantType, ObjectService.ObjectTypes.FILE, $stateParams.id).then(function () {
                gridHelper.deleteRow(rowEntity);
                messageService.info($translate.instant('documentDetails.comp.participants.message.delete.success'));
            }, function () {
                messageService.error($translate.instant('documentDetails.comp.participants.message.delete.error'));
            });
        };

        $scope.editRow = function (rowEntity) {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/document-details/views/components/participants-role-modal.client.view.html',
                controller: 'Document.ParticipantRoleController',
                size: 'lg',
                resolve: {
                    $scopeRole: function () {
                        return $scope;
                    }

                }
            });

            modalInstance.result.then(function (participantRole) {
                ObjectParticipantService.changeParticipantRole(rowEntity.id, participantRole).then(
                    function () {
                        $scope.retrieveGridData();
                    }
                );
            }, function () {
                // Cancel button was clicked.
            });

        };

    }
]);