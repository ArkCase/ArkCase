'use strict';

angular.module('cases').controller(
        'Cases.ChangeStatusController',
        [ '$scope', '$http', '$stateParams', '$translate', '$modalInstance', 'Complaint.InfoService', '$state', 'Object.LookupService', 'MessageService', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Case.InfoService', 'Object.ParticipantService','Admin.FormWorkflowsLinkService',
                function($scope, $http, $stateParams, $translate, $modalInstance, ComplaintInfoService, $state, ObjectLookupService, MessageService, Util, $modal, ConfigService, ObjectService, modalParams, CaseInfoService, ObjectParticipantService, AdminFormWorkflowsLinkService) {

                    $scope.modalParams = modalParams;
                    $scope.approverName = "";
                    $scope.groupName = "";
                    //Functions
                    $scope.statusChanged = statusChanged;
                    $scope.save = save;
                    $scope.cancelModal = cancelModal;
                    //Objects
                    $scope.showCaseCloseStatus = false;
                    $scope.showApprover= modalParams.showApprover;
                    $scope.changeCaseStatus = {
                        caseId: modalParams.info.caseId,
                        status: "",
                        caseResolution: "",
                        objectType: "CHANGE_CASE_STATUS",
                        changeDate: new Date(),
                        participants: [],
                        created: null,
                        creator: null,
                        modified: null,
                        modifier: null,
                        description: "",
                        changeCaseStatusFlow: $scope.showApprover == 'true'
                    };

                    var participantTypeApprover = 'approver';
                    var participantTypeOwningGroup = "owning group";

                    ConfigService.getModuleConfig("cases").then(function(moduleConfig) {
                        $scope.futureTaskConfig = _.find(moduleConfig.components, {
                            id: "newFutureTask"
                        });
                    });

                    ConfigService.getComponentConfig("cases", "participants").then(function(componentConfig) {
                        $scope.participantsConfig = componentConfig;
                    });

                    ObjectLookupService.getLookupByLookupName("changeCaseStatuses").then(function(caseStatuses) {
                        $scope.statuses = caseStatuses;
                    });

                    function statusChanged() {
                        $scope.showCaseCloseStatus = $scope.changeCaseStatus.status === "CLOSED";
                    }

                    // ---------------------------            approver         --------------------------------------
                    $scope.userOrGroupSearch = function() {
                        var params = {};
                        params.header = $translate.instant("cases.comp.change.status.approver.pickerModal.header");
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
                                    addParticipantInChangeCase(participantTypeApprover, selectedUser.object_id_s);

                                    if (selectedGroup) {
                                        $scope.groupName = selectedGroup.name;
                                        addParticipantInChangeCase(participantTypeOwningGroup, selectedGroup.object_id_s);
                                    }

                                    return;
                                } else if (selectedObjectType === 'GROUP') { // Selected group
                                    var selectedUser = selection.detailSelectedItems;
                                    var selectedGroup = selection.masterSelectedItem;

                                    $scope.groupName = selectedGroup.name;
                                    addParticipantInChangeCase(participantTypeOwningGroup, selectedGroup.object_id_s);

                                    if (selectedUser) {
                                        $scope.approverName = selectedUser.name;
                                        addParticipantInChangeCase(participantTypeApprover, selectedUser.object_id_s);
                                    }

                                    return;
                                }
                            }

                        }, function() {
                            // Cancel button was clicked.
                            return [];
                        });

                    };

                    function addParticipantInChangeCase(participantType, participantLdapId){
                        var newParticipant = {};
                        newParticipant.className = $scope.participantsConfig.className;
                        newParticipant.participantType = participantType;
                        newParticipant.participantLdapId = participantLdapId;

                        if (ObjectParticipantService.validateParticipants([newParticipant], true)) {
                            var participantExists = false;
                            _.forEach($scope.changeCaseStatus.participants, function (participant) {
                                if(participant.participantType == participantType){
                                    participantExists = true;
                                    participant.participantLdapId = newParticipant.participantLdapId;
                                    participant.replaceChildrenParticipant = true;
                                    return false;
                                }
                            });
                            if(!participantExists){
                                $scope.changeCaseStatus.participants.push(newParticipant);
                            }
                        }
                    }

                    function save() {
                        $scope.loading = true;
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                        CaseInfoService.changeCaseFileState('change_case_status', $scope.changeCaseStatus).then(function(data) {
                            MessageService.info(data.info);
                            if($scope.changeCaseStatus.changeCaseStatusFlow){
                                $scope.changeCaseStatus.status = 'IN APPROVAL';
                            }
                            $modalInstance.close($scope.changeCaseStatus);
                        });

                    }

                    function cancelModal() {
                        $modalInstance.dismiss();
                    }

                } ]);
