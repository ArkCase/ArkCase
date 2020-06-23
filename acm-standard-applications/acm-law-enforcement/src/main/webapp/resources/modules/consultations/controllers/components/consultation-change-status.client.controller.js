'use strict';

angular.module('consultations').controller(
    'Consultations.ChangeStatusController',
    [ '$scope', '$http', '$stateParams', '$translate', '$modalInstance', '$state', 'Object.LookupService', 'MessageService', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Consultation.InfoService', 'Object.ParticipantService',
        function($scope, $http, $stateParams, $translate, $modalInstance, $state, ObjectLookupService, MessageService, Util, $modal, ConfigService, ObjectService, modalParams, ConsultationInfoService, ObjectParticipantService) {

            $scope.modalParams = modalParams;
            $scope.currentStatus = modalParams.info.status;
            $scope.approverName = "";
            $scope.groupName = "";
            //Functions
            $scope.statusChanged = statusChanged;
            $scope.save = save;
            $scope.cancelModal = cancelModal;
            //Objects
            $scope.showConsultationCloseStatus = false;
            $scope.showApprover= modalParams.showApprover;
            $scope.changeConsultationStatus = {
                consultationId: modalParams.info.consultationId,
                status: "",
                consultationResolution: "",
                objectType: "CHANGE_CONSULTATION_STATUS",
                changeDate: new Date(),
                participants: [],
                created: null,
                creator: null,
                modified: null,
                modifier: null,
                description: "",
                changeConsultationStatusFlow: $scope.showApprover == 'true'
            };

            var participantTypeApprover = 'approver';
            var participantTypeOwningGroup = "owning group";

            ConfigService.getModuleConfig("consultations").then(function(moduleConfig) {
                $scope.futureTaskConfig = _.find(moduleConfig.components, {
                    id: "newFutureTask"
                });
            });

            ConfigService.getComponentConfig("consultations", "participants").then(function(componentConfig) {
                $scope.participantsConfig = componentConfig;
            });

            ObjectLookupService.getLookupByLookupName("changeConsultationStatuses").then(function(consultationStatuses) {
                $scope.statuses = consultationStatuses;
            });

            function statusChanged() {
                $scope.showConsultationCloseStatus = $scope.changeConsultationStatus.status === "CLOSED";
            }

            // ---------------------------            approver         --------------------------------------
            $scope.userOrGroupSearch = function() {
                var params = {};
                params.header = $translate.instant("consultations.comp.change.status.approver.pickerModal.header");
                params.filter = "fq=\"object_type_s\":(GROUP OR USER)&fq=\"status_lcs\":(ACTIVE OR VALID)";
                params.extraFilter = "&fq=\"name\": ";
                params.config = Util.goodMapValue($scope.participantsConfig, "dialogUserPicker");
                params.secondGrid = 'true';

                var modalInstance = $modal.open({
                    templateUrl: "directives/core-participants/participants-user-group-search.client.view.html",
                    controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                        $scope.modalInstance = $modalInstance;
                        $scope.header = params.header;
                        $scope.filter = params.filter;
                        $scope.config = params.config;
                        $scope.secondGrid = params.secondGrid;
                        $scope.extraFilter = params.extraFilter;
                    } ],
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function(selection) {
                    if (selection) {
                        var selectedObjectType = selection.masterSelectedItem.object_type_s;
                        if (selectedObjectType === 'USER') { // Selected user
                            var selectedUser = selection.masterSelectedItem;
                            var selectedGroup = selection.detailSelectedItems;

                            $scope.approverName = selectedUser.name;
                            addParticipantInChangeConsultation(participantTypeApprover, selectedUser.object_id_s);

                            if (selectedGroup) {
                                $scope.groupName = selectedGroup.name;
                                addParticipantInChangeConsultation(participantTypeOwningGroup, selectedGroup.object_id_s);
                            }

                            return;
                        } else if (selectedObjectType === 'GROUP') { // Selected group
                            var selectedUser = selection.detailSelectedItems;
                            var selectedGroup = selection.masterSelectedItem;

                            $scope.groupName = selectedGroup.name;
                            addParticipantInChangeConsultation(participantTypeOwningGroup, selectedGroup.object_id_s);

                            if (selectedUser) {
                                $scope.approverName = selectedUser.name;
                                addParticipantInChangeConsultation(participantTypeApprover, selectedUser.object_id_s);
                            }

                            return;
                        }
                    }

                }, function() {
                    // Cancel button was clicked.
                    return [];
                });

            };

            function addParticipantInChangeConsultation(participantType, participantLdapId){
                var newParticipant = {};
                newParticipant.className = $scope.participantsConfig.className;
                newParticipant.participantType = participantType;
                newParticipant.participantLdapId = participantLdapId;

                if (ObjectParticipantService.validateParticipants([newParticipant], true)) {
                    var participantExists = false;
                    _.forEach($scope.changeConsultationStatus.participants, function (participant) {
                        if(participant.participantType == participantType){
                            participantExists = true;
                            participant.participantLdapId = newParticipant.participantLdapId;
                            participant.replaceChildrenParticipant = true;
                            return false;
                        }
                    });
                    if(!participantExists){
                        $scope.changeConsultationStatus.participants.push(newParticipant);
                    }
                }
            }

            function save() {
                $scope.loading = true;
                $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                ConsultationInfoService.changeConsultationState('change_consultation_status', $scope.changeConsultationStatus).then(function (data) {
                    MessageService.info(data.info);
                    if($scope.changeConsultationStatus.changeConsultationStatusFlow){
                        $scope.changeConsultationStatus.status = 'IN APPROVAL';
                    }
                    $modalInstance.close($scope.changeConsultationStatus);
                });

            }

            function cancelModal() {
                $modalInstance.dismiss();
            }

        } ]);
