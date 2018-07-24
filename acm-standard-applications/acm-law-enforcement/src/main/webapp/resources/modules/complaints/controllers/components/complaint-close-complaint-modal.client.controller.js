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
                    $scope.complaint = {
                        "information": {
                            "id": modalParams.info.complaintId,
                            "number": modalParams.info.complaintNumber,
                            "date": new Date(),
                            "option": "",
                            "resolveOptions": null
                        },
                        "referExternal": null,
                        "existingCase": {
                            "caseNumber": null,
                            "caseTitle": null,
                            "caseCreationDate": null,//Tuka datata od selectiraniot case
                            "casePriority": null
                        },
                        "approvers": [ {} ],
                        "description": ""
                    };
                    $scope.complaintDispositions = [];
                    $scope.contactTypes = [];
                    $scope.loading = false;
                    $scope.showExistingCase = false;
                    $scope.showReferExternal = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.userSearchConfig = null;
                    $scope.futureTaskConfig = null;

                    ConfigService.getModuleConfig("complaints").then(function(moduleConfig) {
                        $scope.futureTaskConfig = _.find(moduleConfig.components, {
                            id: "newFutureTask"
                        });
                        $scope.userSearchConfig = _.find(moduleConfig.components, {
                            id: "userSearch"
                        });
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
                        //TUKA VIDI DRUG NACIN! PRERABOTI
                        if ($scope.complaint.information.option == 'Add to Existing Case') {
                            $scope.existingCase = {};
                            $scope.showReferExternal = false;
                            $scope.showExistingCase = true;
                        } else if ($scope.complaint.information.option == 'Refer External') {
                            $scope.referExternal = {
                                "date": new Date()
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
                        _.remove($scope.complaint.approvers, function(object) {
                            return object === approver;
                        });
                    }

                    function addApprover(index) {
                        var params = {};

                        params.header = $translate.instant("complaints.comp.approver.pickerModal.header");
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
                                    "approverName": data.name,
                                    "id": null, //proveri id dali treba da e null??????????????
                                    "value": data.object_id_s
                                };
                                if (index > -1) {
                                    $scope.complaint.approvers[index] = approver;
                                } else {
                                    $scope.complaint.approvers.push(approver);
                                }
                            }
                        }, function() {
                            return {};
                        });
                    }

                    function searchCase(caseNumber) {
                        CaseInfoService.getCaseInfoByNumber(caseNumber).then(function(caseInfo) {
                            $scope.objectId = caseInfo.id;
                            $scope.complaint.existingCase.caseNumber = caseInfo.caseNumber;
                            $scope.complaint.existingCase.caseTitle = caseInfo.title;
                            $scope.complaint.existingCase.caseCreationDate = caseInfo.created; //Tuka so date vidi!!!
                            $scope.complaint.existingCase.casePriority = caseInfo.priority;
                        });
                    }

                    function save() {
                        $scope.loading = true;
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                        $http.post('https://acm-arkcase/arkcase/api/latest/plugin/complaint/close?mode=123', $scope.complaint).then(function(data) {
                            console.log(data);
                        });
                    }

                    function cancelModal() {
                        $modalInstance.dismiss();
                    }

                } ]);
