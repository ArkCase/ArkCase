'use strict';

angular.module('complaints').controller(
    'Complaints.CloseComplaintController',
    ['$scope', '$http', '$stateParams', '$translate', '$modalInstance', 'Complaint.InfoService', 'Organization.InfoService', 'Person.InfoService', 'Profile.UserInfoService', '$state', 'Object.LookupService', 'MessageService', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Case.InfoService', 'Object.ParticipantService',
        function ($scope, $http, $stateParams, $translate, $modalInstance, ComplaintInfoService, OrganizationInfoService, PersonInfoService, UserInfoService, $state, ObjectLookupService, MessageService, Util, $modal, ConfigService, ObjectService, modalParams, CaseInfoService, ObjectParticipantService) {

            $scope.modalParams = modalParams;
            $scope.approverName = "";
            $scope.groupName = "";
            //Functions
            $scope.dispositionTypeChanged = dispositionTypeChanged;
            $scope.searchCase = searchCase;
            $scope.save = save;
            $scope.cancelModal = cancelModal;
            //Objects
            $scope.complaintInfo = {};
            $scope.closeComplaintRequest = {
                complaintId: modalParams.info.complaintId,
                complaintNumber: modalParams.info.complaintNumber,
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
                participants: [],
                created: null,
                creator: null,
                modified: null,
                modifier: null,
                description: "",
                referExternalPersonId: undefined,
                referExternalOrganizationId: undefined
            };
            $scope.complaintDispositions = [];
            $scope.contactTypes = [];
            $scope.loading = false;
            $scope.showExistingCase = false;
            $scope.showReferExternal = false;
            $scope.loadingIcon = "fa fa-floppy-o";
            $scope.futureTaskConfig = null;
            $scope.complaintPeopleConfig = null;
            $scope.isDescriptionRequired = false;

            var participantTypeApprover = 'approver';
            var participantTypeOwningGroup = "owning group";

            ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
                $scope.complaintInfo = moduleConfig;

                $scope.futureTaskConfig = _.find(moduleConfig.components, {
                    id: "newFutureTask"
                });
                $scope.participantsConfig = _.find(moduleConfig.components, {
                    id: "participants"
                });
                $scope.complaintPeopleConfig = _.find(moduleConfig.components, {
                    id: "people"
                });
                $scope.closeComplaintRequest.disposition.className = moduleConfig.closeComplaintClassNames.disposition.className;
            });

            ObjectLookupService.getDispositionTypes().then(function (dispositionTypes) {
                _.forEach(dispositionTypes, function (item) {
                    var dispositionType = {
                        "key": item.key,
                        "value": $translate.instant(item.value)
                    };
                    $scope.complaintDispositions.push(dispositionType);
                });
                $scope.defaultComplaintDisposition = ObjectLookupService.getPrimaryLookup($scope.complaintDispositions);
                if ($scope.defaultComplaintDisposition && !$scope.closeComplaintRequest.disposition.dispositionType) {
                    $scope.closeComplaintRequest.disposition.dispositionType = $scope.defaultComplaintDisposition.key;
                }

            });

            ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.COMPLAINT).then(function (data) {
                var personTypes = [];
                for (var i = 0; i < data.length; i++) {
                    if (data[i].key === "ExternalReferal") {
                        personTypes.push(data[i]);
                    }
                }
                $scope.personTypeExternalReferal = personTypes;
                return personTypes;
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

            // ---------------------------            approver         --------------------------------------
            $scope.userOrGroupSearch = function () {
                var params = {};
                params.header = $translate.instant("complaints.comp.closeComplaint.approver.pickerModal.header");
                params.filter = "fq=\"object_type_s\":(GROUP OR USER)&fq=\"status_lcs\":(ACTIVE OR VALID)";
                params.extraFilter = "&fq=\"name\": ";
                params.config = Util.goodMapValue($scope.participantsConfig, "dialogUserPicker");
                params.secondGrid = 'true';

                var modalInstance = $modal.open({
                    templateUrl: "directives/core-participants/participants-user-group-search.client.view.html",
                    controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                        $scope.modalInstance = $modalInstance;
                        $scope.header = params.header;
                        $scope.filter = params.filter;
                        $scope.config = params.config;
                        $scope.secondGrid = params.secondGrid;
                        $scope.extraFilter = params.extraFilter;
                    }],
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (selection) {
                    if (selection) {
                        var selectedObjectType = selection.masterSelectedItem.object_type_s;
                        if (selectedObjectType === 'USER') { // Selected user
                            var selectedUser = selection.masterSelectedItem;
                            var selectedGroup = selection.detailSelectedItems;

                            $scope.approverName = selectedUser.name;
                            addParticipantInCloseComplaint(participantTypeApprover, selectedUser.object_id_s);

                            if (selectedGroup) {
                                $scope.groupName = selectedGroup.name;
                                addParticipantInCloseComplaint(participantTypeOwningGroup, selectedGroup.object_id_s);
                            }

                            return;
                        } else if (selectedObjectType === 'GROUP') { // Selected group
                            var selectedUser = selection.detailSelectedItems;
                            var selectedGroup = selection.masterSelectedItem;

                            $scope.groupName = selectedGroup.name;
                            addParticipantInCloseComplaint(participantTypeOwningGroup, selectedGroup.object_id_s);

                            if (selectedUser) {
                                $scope.approverName = selectedUser.name;
                                addParticipantInCloseComplaint(participantTypeApprover, selectedUser.object_id_s);
                            }

                            return;
                        }
                    }

                }, function () {
                    // Cancel button was clicked.
                    return [];
                });

            };

            function addParticipantInCloseComplaint(participantType, participantLdapId) {
                var newParticipant = {};
                newParticipant.className = $scope.participantsConfig.className;
                newParticipant.participantType = participantType;
                newParticipant.participantLdapId = participantLdapId;

                if (ObjectParticipantService.validateParticipants([newParticipant], true)) {
                    var participantExists = false;
                    _.forEach($scope.closeComplaintRequest.participants, function (participant) {
                        if (participant.participantType == participantType) {
                            participantExists = true;
                            participant.participantLdapId = newParticipant.participantLdapId;
                            participant.replaceChildrenParticipant = true;
                            return false;
                        }
                    });
                    if (!participantExists) {
                        $scope.closeComplaintRequest.participants.push(newParticipant);
                    }
                }
            }

            // -------------------  people --------------------------------------------------------------------

            var ExternalReferalType = 'External Referal';

            var newPersonAssociation = function () {
                return {
                    id: null,
                    personType: "",
                    parentId: $scope.closeComplaintRequest.complaintId,
                    parentType: ObjectService.ObjectTypes.COMPLAINT,
                    parentTitle: $scope.closeComplaintRequest.complaintNumber,
                    personDescription: "",
                    notes: "",
                    person: null,
                    className: $scope.complaintPeopleConfig.personAssociationClassName
                };
            };
            $scope.addPerson = function () {
                pickPerson(null);
            };

            function pickPerson(association) {

                var params = {};
                params.types = $scope.personTypeExternalReferal;
                params.type = ExternalReferalType;
                params.typeEnabled = true;
                association = new newPersonAssociation();

                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                    controller: 'Common.AddPersonModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    if (data.isNew) {
                        PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function (response) {
                            data.person = response.data;
                            $scope.closeComplaintRequest.disposition.referExternalContactPersonName = data.person.givenName + " " + data.person.familyName;
                            $scope.contactTypes = data.person.contactMethods;
                            $scope.closeComplaintRequest.referExternalPersonId = data.person.id;
                        });
                    } else {
                        PersonInfoService.getPersonInfo(data.personId).then(function (person) {
                            $scope.closeComplaintRequest.disposition.referExternalContactPersonName = person.givenName + " " + person.familyName;
                            $scope.contactTypes = person.contactMethods;
                            $scope.closeComplaintRequest.referExternalPersonId = person.id;
                        });
                    }
                });
            }


            //------------------------------- Organizations --------------------------------------------

            var newOrganizationAssociation = function () {
                return {
                    id: null,
                    associationType: "",
                    parentId: $scope.closeComplaintRequest.complaintId,
                    parentType: ObjectService.ObjectTypes.COMPLAINT,
                    parentTitle: $scope.closeComplaintRequest.complaintNumber,
                    organization: null,
                    className: "com.armedia.acm.plugins.person.model.OrganizationAssociation"
                };
            };

            $scope.addOrganization = function () {
                pickOrganization(null);
            };

            function pickOrganization(association) {
                var params = {};
                params.types = $scope.personTypeExternalReferal;

                if (association) {
                    angular.extend(params, {
                        organizationId: association.organization.organizationId,
                        organizationValue: association.organization.organizationValue,
                        type: association.personTypeExternalReferal,
                        description: association.description
                    });
                } else {
                    association = new newOrganizationAssociation();
                }

                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/common/views/add-organization-modal.client.view.html',
                    controller: 'Common.AddOrganizationModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    if (data.isNew) {
                        $scope.closeComplaintRequest.disposition.referExternalOrganizationName = data.organization.organizationValue;
                        $scope.closeComplaintRequest.referExternalOrganizationId = data.organization.organizationId;
                    } else {
                        OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {

                            $scope.closeComplaintRequest.disposition.referExternalOrganizationName = organization.organizationValue;
                            $scope.closeComplaintRequest.referExternalOrganizationId = organization.organizationId;
                        })
                    }
                });
            }

            function searchCase() {
                CaseInfoService.getCaseInfoByNumber($scope.closeComplaintRequest.disposition.existingCaseNumber).then(function (caseInfo) {
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

                ComplaintInfoService.closeComplaint('create', $scope.closeComplaintRequest).then(function (data) {
                    MessageService.info(data.info);
                    $modalInstance.dismiss();
                });
            }

            function cancelModal() {
                $modalInstance.dismiss();
            }

        }]);
