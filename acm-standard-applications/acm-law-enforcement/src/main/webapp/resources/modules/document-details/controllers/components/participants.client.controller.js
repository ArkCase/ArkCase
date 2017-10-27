'use strict';
angular.module('document-details').controller('Document.ParticipantsController', ['$scope', '$stateParams', '$q', '$modal'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.ParticipantService', 'Authentication', 'MessageService', '$translate',
    'Object.LookupService', 'Object.ModelService', 'EcmService',
    function ($scope, $stateParams, $q, $modal, Util, ConfigService, HelperUiGridService, ObjectService, ObjectParticipantService, Authentication, MessageService, $translate
        , ObjectLookupService, ObjectModelService, EcmService) {

        $scope.participantType = {};
        $scope.chosenUser = null;
        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        $scope.$on('document-data', function (event, ecmFile) {
	        $scope.promiseTypes = ObjectLookupService.getParticipantTypes(ecmFile.container.containerObjectType).then(
	            function (participantTypes) {
	                $scope.participantTypes = participantTypes;
	                return participantTypes;
	            }
	        );
        });

        var promiseConfig = ConfigService.getComponentConfig("document-details", "participants").then(function (config) {
            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
        });

        $scope.retrieveGridData = function () {
            if (Util.goodPositive($stateParams.id)) {
                var promiseFileInfo = EcmService.getFile({fileId: $stateParams.id}).$promise;
                $q.all([promiseFileInfo, promiseUsers]).then(function (data) {
                    $scope.fileInfo = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = $scope.fileInfo.participants;
                    $scope.gridOptions.totalItems = Util.goodValue($scope.fileInfo.participants.length, 0);
                });
            }
        };

        $scope.addNew = function () {
            $scope.participant = {};
            var item = {
                id: '',
                participantType: '',
                participantLdapId: '',
                participantTypes: $scope.participantTypes,
                config: $scope.config
            };
            showModal(item, false);
        };

        $scope.deleteRow = function (rowEntity) {
            var typeOwningGroup = "owning group";
            var typeAssignee = "assignee";

            if (rowEntity.participantType == typeOwningGroup) {
                MessageService.error($translate.instant("common.directive.coreParticipants.message.error.owninggroupDelete"));
            }
            else if (rowEntity.participantType == typeAssignee) {
                MessageService.error($translate.instant("common.directive.coreParticipants.message.error.assigneeDelete"));
            }
            else {
                gridHelper.deleteRow(rowEntity);
                var id = Util.goodMapValue(rowEntity, "id", 0);
                if (0 < id) {    //do not need to call service when deleting a new row
                    saveInfoAndRefresh();
                }
            }
        };

        $scope.editRow = function (rowEntity) {
            $scope.participant = rowEntity;
            var participantDataPromise = ObjectParticipantService.findParticipantById(rowEntity.participantLdapId);
            participantDataPromise.then(function (participantData) {
                if (!Util.isArrayEmpty(participantData)) {
                    var item = {
                        id: rowEntity.id,
                        participantType: rowEntity.participantType,
                        participantLdapId: rowEntity.participantLdapId,
                        participantTypes: $scope.participantTypes,
                        selectedType: participantData[0].object_type_s ? participantData[0].object_type_s : "",
                        config: $scope.config
                    };
                    showModal(item, true);
                }
            })

        };

        var showModal = function (participant, isEdit) {

            var modalScope = $scope.$new();
            modalScope.participant = participant || {};
            modalScope.isEdit = isEdit || false;
            modalScope.selectedType = participant.selectedType ? participant.selectedType : "";

            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: "directives/core-participants/core-participants-modal.client.view.html",
                controller: "Directives.CoreParticipantsModalController",
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return {};
                    }
                }
            });

            modalInstance.result.then(function (data) {
                    if (ObjectParticipantService.validateType(data.participant, data.selectedType)) {
                        $scope.participant.id = data.participant.id;
                        $scope.participant.participantLdapId = data.participant.participantLdapId;
                        $scope.participant.participantType = data.participant.participantType;

                        var assignee = ObjectModelService.getParticipantByType($scope.fileInfo, "assignee");
                        var typeNoAccess = 'No Access';
                        if ($scope.config.typeNoAccess) {
                            typeNoAccess = $scope.config.typeNoAccess;
                        }

                        if (data.isEdit) {
                            var participant = _.find($scope.fileInfo.participants, function (pa) {
                                return Util.compare(pa.id, data.participant.id);
                            });
                            participant.participantLdapId = data.participant.participantLdapId;
                            participant.id = data.participant.id;

                            if (data.participant.participantType == typeNoAccess && assignee == data.participant.participantLdapId) {
                                MessageService.error($translate.instant("common.directive.coreParticipants.message.error.noAccessCombo"));
                            }
                            else {
                                participant.participantType = data.participant.participantType;
                            }
                        }
                        else {
                            var participant = {};
                            participant.participantLdapId = data.participant.participantLdapId;

                            if (data.participant.participantType == typeNoAccess && assignee == data.participant.participantLdapId) {
                                MessageService.error($translate.instant("common.directive.coreParticipants.message.error.noAccessCombo"));
                            }
                            else {
                                participant.participantType = data.participant.participantType;
                                participant.className = $scope.config.className;
                                $scope.fileInfo.participants.push(participant);
                            }
                        }

                    }
                    if (ObjectParticipantService.validateParticipants($scope.fileInfo.participants)) {
                        saveInfoAndRefresh();
                    }
                    else {
                        $scope.retrieveGridData();
                    }
                }
            );
        };

        var saveInfoAndRefresh = function () {

            Util.serviceCall({
                service: EcmService.updateFile
                , param: {fileId: $stateParams.id}
                ,
                data: JSOG.encode($scope.fileInfo)
            }).then(function (objectSaved) {
                    $scope.retrieveGridData();
                    return objectSaved;
                },
                function (error) {
                    $scope.retrieveGridData();
                    return error;
                });
        };

    }
]);