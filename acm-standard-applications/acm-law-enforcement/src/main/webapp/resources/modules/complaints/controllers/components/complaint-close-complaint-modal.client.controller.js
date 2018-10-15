'use strict';

angular.module('complaints').controller(
        'Complaints.CloseComplaintController',
        [ '$scope', '$http', '$stateParams', '$translate', '$modalInstance', 'Complaint.InfoService', '$state', 'Object.LookupService', 'MessageService', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Case.InfoService',
                function($scope, $http, $stateParams, $translate, $modalInstance, ComplaintInfoService, $state, ObjectLookupService, MessageService, Util, $modal, ConfigService, ObjectService, modalParams, CaseInfoService) {

                    $scope.modalParams = modalParams;
                    //Functions
                    $scope.dispositionTypeChanged = dispositionTypeChanged;
                    $scope.addApprover = addApprover;
                    $scope.addNewApprover = addNewApprover;
                    $scope.removeApprover = removeApprover;
                    $scope.searchCase = searchCase;
                    $scope.save = save;
                    $scope.cancelModal = cancelModal;
                    //Objects
                    $scope.complaintInfo = {};
                    $scope.closeComplaintRequest = {
                        complaintId: modalParams.info.complaintId,
                        disposition: {
                            closeDate: new Date(),
                            dispositionType: "",
                            referExternalOrganizationName: null,
                            referExternalDate: new Date(),
                            referExternalContactPersonName: null,
                            referExternalContactMethod: null,
                            existingCaseNumber: null,
                            existingCaseTitle: null,
                            existingCaseDate: null,
                            existingCasePriority: null,
                            created: null,
                            creator: null,
                            modified: null,
                            modifier: null,
                            className: ""
                        },
                        status: "IN APPROVAL",
                        objectType: "CLOSE_COMPLAINT_REQUEST",
                        participants: [ {} ],
                        created: null,
                        creator: null,
                        modified: null,
                        modifier: null,
                        description: ""
                    };
                    $scope.complaintDispositions = [];
                    $scope.contactTypes = [];
                    $scope.loading = false;
                    $scope.showExistingCase = false;
                    $scope.showReferExternal = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.futureTaskConfig = null;
                    $scope.isDescriptionRequired = false;

                    ConfigService.getModuleConfig("complaints").then(function(moduleConfig) {
                        $scope.futureTaskConfig = _.find(moduleConfig.components, {
                            id: "newFutureTask"
                        });
                        $scope.complaintInfo = moduleConfig;
                        $scope.closeComplaintRequest.disposition.className = moduleConfig.closeComplaintClassNames.disposition.className;
                    });

                    ConfigService.getComponentConfig("complaints", "participants").then(function(componentConfig) {
                        $scope.config = componentConfig;
                    });

                    ObjectLookupService.getDispositionTypes().then(function(dispositionTypes) {
                        _.forEach(dispositionTypes, function(item) {
                            var dispositionType = {
                                "key": item.key,
                                "value": $translate.instant(item.value)
                            };
                            $scope.complaintDispositions.push(dispositionType);
                        })
                    });

                    ObjectLookupService.getContactMethodTypes().then(function(contactTypes) {
                        _.forEach(contactTypes, function(item) {
                            var dispositionType = {
                                "key": item.key,
                                "value": $translate.instant(item.value)
                            };
                            $scope.contactTypes.push(dispositionType);
                        });
                    });

                    function dispositionTypeChanged() {
                        $scope.isDescriptionRequired = $scope.closeComplaintRequest.disposition.dispositionType == 'no_action'
                        if ($scope.closeComplaintRequest.disposition.dispositionType == 'add_existing_case') {
                            $scope.existingCase = {};
                            $scope.showReferExternal = false;
                            $scope.showExistingCase = true;
                        } else if ($scope.closeComplaintRequest.disposition.dispositionType == 'refer_external') {
                            $scope.referExternal = {
                                "date": new Date()
                            };

                            $scope.closeComplaintRequest.disposition.referExternalContactMethod = {
                                created: null,
                                creator: null,
                                modified: null,
                                modifier: null,
                                status: null,
                                type: null,
                                subType: null,
                                types: null,
                                value: null,
                                description: null,
                                className: $scope.complaintInfo.closeComplaintClassNames.disposition.referExternalMethod.className,
                                objectType: "CONTACT_METHOD"
                            };

                            $scope.showReferExternal = true;
                            $scope.showExistingCase = false;
                        } else {
                            $scope.showReferExternal = false;
                            $scope.showExistingCase = false;
                        }
                    }

                    function addNewApprover() {
                        $scope.addApprover(-1);
                    }

                    function removeApprover(approver) {
                        _.remove($scope.closeComplaintRequest.participants, function(object) {
                            return object === approver;
                        });
                    }

                    function addApprover(index) {
                        var params = {};

                        params.header = $translate.instant("complaints.comp.closeComplaint.approver.pickerModal.header");
                        params.filter = $scope.futureTaskConfig.userSearch.userFacetFilter;
                        params.extraFilter = $scope.futureTaskConfig.userSearch.userFacetExtraFilter;
                        params.config = Util.goodMapValue($scope.config, "dialogUserPicker");
                        params.modalInstance = $modalInstance;

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/complaints/views/components/complaint-approver-picker-search-modal.client.view.html',
                            controller: 'Complaints.ApproverPickerController',
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
                                    participantFullName: data.name,
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
                                    $scope.closeComplaintRequest.participants[index] = approver;
                                } else {
                                    $scope.closeComplaintRequest.participants.push(approver);
                                }
                            }
                        }, function() {
                            return {};
                        });
                    }

                    function searchCase() {
                        CaseInfoService.getCaseInfoByNumber($scope.closeComplaintRequest.disposition.existingCaseNumber).then(function(caseInfo) {
                            $scope.objectId = caseInfo.id;
                            $scope.existingCase.caseNumber = caseInfo.caseNumber;
                            $scope.closeComplaintRequest.disposition.existingCaseNumber = caseInfo.caseNumber;
                            $scope.closeComplaintRequest.disposition.existingCaseTitle = caseInfo.title;
                            $scope.closeComplaintRequest.disposition.existingCaseCreated = caseInfo.created;
                            $scope.closeComplaintRequest.disposition.existingCasePriority = caseInfo.priority;
                        });
                    }

                    function save() {
                        $scope.loading = true;
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";

                        ComplaintInfoService.closeComplaint('create', $scope.closeComplaintRequest).then(function(data) {
                            MessageService.info(data.info);
                            $modalInstance.dismiss();
                        });
                    }

                    function cancelModal() {
                        $modalInstance.dismiss();
                    }

                } ]);
