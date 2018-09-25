'use strict';

angular.module('cases').controller(
        'Cases.ChangeStatusController',
        [ '$scope', '$http', '$stateParams', '$translate', '$modalInstance', 'Complaint.InfoService', '$state', 'Object.LookupService', 'MessageService', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Case.InfoService',
                function($scope, $http, $stateParams, $translate, $modalInstance, ComplaintInfoService, $state, ObjectLookupService, MessageService, Util, $modal, ConfigService, ObjectService, modalParams, CaseInfoService) {

                    $scope.modalParams = modalParams;
                    //Functions
                    $scope.statusChanged = statusChanged;
                    $scope.addApprover = addApprover;
                    $scope.addNewApprover = addNewApprover;
                    $scope.removeApprover = removeApprover;
                    $scope.save = save;
                    $scope.cancelModal = cancelModal;
                    //Objects
                    $scope.showCaseCloseStatus = false;
                    $scope.changeCaseStatus = {
                        caseId: modalParams.info.caseId,
                        status: "",
                        caseResolution: "",
                        objectType: "CHANGE_CASE_STATUS",
                        changeDate: new Date(),
                        participants: [ {} ],
                        created: null,
                        creator: null,
                        modified: null,
                        modifier: null,
                        description: ""
                    };

                    ConfigService.getModuleConfig("cases").then(function(moduleConfig) {
                        $scope.futureTaskConfig = _.find(moduleConfig.components, {
                            id: "newFutureTask"
                        });
                    });

                    ConfigService.getComponentConfig("cases", "participants").then(function(componentConfig) {
                        $scope.config = componentConfig;
                    });

                    ObjectLookupService.getLookupByLookupName("changeCaseStatuses").then(function(caseStatuses) {
                        $scope.statuses = caseStatuses;
                    });

                    function statusChanged() {
                        $scope.showCaseCloseStatus = $scope.changeCaseStatus.status === "CLOSED";
                    }

                    function addNewApprover() {
                        $scope.addApprover(-1);
                    }

                    function removeApprover(approver) {
                        _.remove($scope.changeCaseStatus.participants, function(object) {
                            return object === approver;
                        });
                    }

                    function addApprover(index) {
                        var params = {};

                        params.header = $translate.instant("cases.comp.change.status.approver.pickerModal.header");
                        params.filter = $scope.futureTaskConfig.userSearch.userFacetFilter;
                        params.extraFilter = $scope.futureTaskConfig.userSearch.userFacetExtraFilter;
                        params.config = Util.goodMapValue($scope.config, "dialogUserPicker");
                        params.modalInstance = $modalInstance;

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/cases/views/components/case-approver-picker-search-modal.client.view.html',
                            controller: 'Cases.ApproverPickerController',
                            size: 'lg',
                            resolve: {
                                modalParams: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (data) {
                                var approver = {
                                    className: $scope.config.className,
                                    objectType: null,
                                    objectId: null,
                                    participantType: "approver",
                                    participantLdapId: data.email_lcs,
                                    created: null,
                                    creator: null,
                                    modified: null,
                                    modifier: null,
                                    privileges: [],
                                    replaceChildrenParticipant: false,
                                    isEditableUser: true,
                                    isEditableType: true,
                                    isDeletable: true
                                };
                                if (index > -1) {
                                    $scope.changeCaseStatus.participants[index] = approver;
                                } else {
                                    $scope.changeCaseStatus.participants.push(approver);
                                }
                            }
                        }, function() {
                            return {};
                        });
                    }

                    function save() {
                        $scope.loading = true;
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                        CaseInfoService.changeCaseFileState('change_case_status', $scope.changeCaseStatus).then(function(data) {
                            MessageService.info(data.info);
                            $modalInstance.dismiss();
                        });

                    }

                    function cancelModal() {
                        $modalInstance.dismiss();
                    }

                } ]);
