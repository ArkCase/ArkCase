'use strict';

angular.module('cost-tracking').controller(
        'CostTracking.NewCostsheetController',
        [ '$scope', '$stateParams', '$translate', '$modalInstance', 'CostTracking.InfoService', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Person.InfoService', 'Object.ModelService', 'Object.ParticipantService',
                'Profile.UserInfoService', 'Admin.CostsheetConfigurationService',
                function($scope, $stateParams, $translate, $modalInstance, CostTrackingInfoService, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, ObjectService, modalParams, PersonInfoService, ObjectModelService, ObjectParticipantService, UserInfoService, CostsheetConfigurationService) {

                    $scope.modalParams = modalParams;
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.isEdit = $scope.modalParams.isEdit;
                    $scope.sumAmount = 0;
                    $scope.disableCostType = false;
                    var participantTypeApprover = 'approver';
                    var participantTypeOwningGroup = "owning group";

                    $scope.costsheetProperties = {
                        "cost.plugin.useApprovalWorkflow": "true"
                    };

                    ConfigService.getModuleConfig("cost-tracking").then(function(moduleConfig) {
                        $scope.config = moduleConfig;

                        UserInfoService.getUserInfo().then(function(infoData) {

                            if (!$scope.isEdit) {
                                //new costsheet with predefined values
                                $scope.isTypeSelected = false;
                                $scope.isApproverAdded = false;
                                $scope.costsheet = {
                                    className: $scope.config.className,
                                    status: 'DRAFT',
                                    parentId: '',
                                    parentType: '',
                                    parentNumber: '',
                                    details: '',
                                    costs: [ {
                                        date: new Date()
                                    } ],
                                    participants: []
                                };
                                $scope.costsheet.user = infoData;

                                $scope.approverName = "";
                                $scope.groupName = "";


                                if(!Util.isEmpty($scope.modalParams.parentType) && !Util.isEmpty($scope.modalParams.parentNumber) && !Util.isEmpty($scope.modalParams.parentId)) {
                                    $scope.costsheet.parentId = $scope.modalParams.parentId;
                                    $scope.costsheet.parentType = $scope.modalParams.parentType;
                                    $scope.costsheet.parentNumber = $scope.modalParams.parentNumber;
                                    $scope.costsheet.title =  $scope.costsheet.user.fullName + " - " + $scope.modalParams.parentNumber;
                                    $scope.isTypeSelected = true;
                                    $scope.disableCostType = true;
                                }
                            }
                        });


                        $scope.newCostObjectPicker = _.find(moduleConfig.components, {
                            id: "newCostObjectPicker"
                        });

                        $scope.userSearchConfig = _.find(moduleConfig.components, {
                            id: "userSearch"
                        });

                        $scope.participantsConfig = _.find(moduleConfig.components, {
                            id: "person"
                        });

                        return moduleConfig;
                    });

                    CostsheetConfigurationService.getProperties().then(function(response) {
                        if (!Util.isEmpty(response.data)) {
                            $scope.costsheetProperties = response.data;
                        }
                    });


                    $scope.updateBalance = function(costs) {
                        $scope.sumAmount = 0;
                        _.forEach(costs, function(cost) {
                            $scope.sumAmount += cost.value;
                            if (Util.isEmpty(cost.value))
                                $scope.sumAmount = 0;
                        });
                    };

                    if ($scope.isEdit) {
                        $scope.isTypeSelected = true;
                        $scope.objectInfo = angular.copy($scope.modalParams.costsheet);
                        var tmpCostsheet = angular.copy($scope.modalParams.costsheet);
                        updateIsApproverAdded($scope.objectInfo.participants);
                        $scope.updateBalance($scope.objectInfo.costs);

                        if (tmpCostsheet.participants != undefined) {
                            if (!Util.isArrayEmpty(tmpCostsheet.participants)) {
                                _.forEach(tmpCostsheet.participants, function(participant) {
                                    if(participant.participantType == participantTypeApprover){
                                        UserInfoService.getUserInfoById(participant.participantLdapId).then(function(userInfo) {
                                            $scope.approverName = userInfo.fullName;
                                        });

                                    }
                                    if(participant.participantType == participantTypeOwningGroup){
                                        $scope.groupName = participant.participantLdapId;
                                    }
                                });
                            }
                        }

                        if (tmpCostsheet.costs != undefined) {
                            _.forEach(tmpCostsheet.costs, function(cost) {
                                cost.date = new Date(cost.date);
                            });
                        }

                        $scope.costsheet = {
                            id: $scope.objectInfo.id,
                            user: $scope.objectInfo.user,
                            status: $scope.objectInfo.status,
                            parentId: $scope.objectInfo.parentId,
                            parentType: $scope.objectInfo.parentType,
                            parentNumber: $scope.objectInfo.parentNumber,
                            details: $scope.objectInfo.details,
                            costs: tmpCostsheet.costs,
                            participants: tmpCostsheet.participants
                        };
                    }

                    ObjectLookupService.getCostsheetTypes().then(function(costsheetTypes) {
                        $scope.costsheetTypes = costsheetTypes;
                    });

                    ObjectLookupService.getCostsheetTitles().then(function(costsheetTitles) {
                        $scope.costsheetTitles = costsheetTitles;
                    });
                    ObjectLookupService.getCostsheetStatuses().then(function(costsheetStatuses) {
                        $scope.costsheetStatuses = costsheetStatuses;

                            for(var i = $scope.costsheetStatuses.length - 1; i >= 0; i--) {
                                if($scope.costsheetStatuses[i].key !== "DRAFT" && $scope.costsheetStatuses[i].key !== "FINAL") {
                                    $scope.costsheetStatuses.splice(i, 1);
                                }
                            }
                    });

                    $scope.updateIsTypeSelected = function() {
                        if ($scope.costsheet.parentType == undefined) {
                            $scope.isTypeSelected = false;
                        } else {
                            $scope.isTypeSelected = true;
                        }
                        $scope.costsheet.parentNumber = "";
                    };

                    $scope.pickObject = function() {

                        var params = {};
                        params.header = $translate.instant("costTracking.comp.newCostsheet.objectPicker.title");

                        if ($scope.costsheet.parentType == ObjectService.ObjectTypes.CASE_FILE) {
                            params.filter = 'fq="object_type_s": CASE_FILE';
                        } else if ($scope.costsheet.parentType == ObjectService.ObjectTypes.COMPLAINT) {
                            params.filter = 'fq="object_type_s": COMPLAINT';
                        }

                        params.config = $scope.newCostObjectPicker;

                        var modalInstance = $modal.open({
                            templateUrl: "modules/cost-tracking/views/components/cost-tracking-object-picker-search-modal.client.view.html",
                            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                $scope.modalInstance = $modalInstance;
                                $scope.header = params.header;
                                $scope.filter = params.filter;
                                $scope.extraFilter = params.extraFilter;
                                $scope.config = params.config;
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
                        modalInstance.result.then(function(selected) {
                            if (!Util.isEmpty(selected)) {
                                $scope.costsheet.parentNumber = selected.name;
                                $scope.costsheet.parentId = selected.object_id_s;
                                $scope.costsheet.title = $scope.costsheet.user.fullName + " - " + selected.name;
                            }
                        });

                    };

                    //-----------------------------------   costs    -------------------------------------------
                    $scope.addCost = function() {
                        $timeout(function() {
                            if (!_.isEmpty($scope.costsheet.costs[0])) {
                                $scope.costsheet.costs.push({
                                    date: new Date()
                                });
                            }
                        }, 0);
                    };

                    $scope.removeCost = function(cost) {
                        $timeout(function() {
                            _.remove($scope.costsheet.costs, function(object) {
                                return object === cost;
                            });
                            $scope.updateBalance($scope.costsheet.costs);
                        }, 0);
                    };

                    // ---------------------------            approver         -------------------------------------
                    $scope.userOrGroupSearch = function() {
                        var params = {};
                        params.header = $translate.instant("costTracking.comp.newCostsheet.userSearch.title");
                        params.filter = "fq=\"object_type_s\":(GROUP OR USER)&fq=\"status_lcs\":(ACTIVE OR VALID)";
                        params.extraFilter = "&fq=\"name\": ";
                        params.config = $scope.userSearchConfig;
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
                                    addParticipantInCostsheet(participantTypeApprover, selectedUser.object_id_s);

                                    if (selectedGroup) {
                                        $scope.groupName = selectedGroup.name;
                                        addParticipantInCostsheet(participantTypeOwningGroup, selectedGroup.object_id_s);
                                    }

                                    updateIsApproverAdded($scope.costsheet.participants);

                                    return;
                                } else if (selectedObjectType === 'GROUP') { // Selected group
                                    var selectedUser = selection.detailSelectedItems;
                                    var selectedGroup = selection.masterSelectedItem;

                                    $scope.groupName = selectedGroup.name;
                                    addParticipantInCostsheet(participantTypeOwningGroup, selectedGroup.object_id_s);

                                    if (selectedUser) {
                                        $scope.approverName = selectedUser.name;
                                        addParticipantInCostsheet(participantTypeApprover, selectedUser.object_id_s);
                                    }

                                    updateIsApproverAdded($scope.costsheet.participants);

                                    return;
                                }
                            }

                        }, function() {
                            // Cancel button was clicked.
                            return [];
                        });

                    };

                    function addParticipantInCostsheet(participantType, participantLdapId){
                        var newParticipant = {};
                        newParticipant.className = $scope.participantsConfig.className;
                        newParticipant.participantType = participantType;
                        newParticipant.participantLdapId = participantLdapId;

                        if (ObjectParticipantService.validateParticipants([newParticipant], true)) {
                            var participantExists = false;
                            _.forEach($scope.costsheet.participants, function (participant, idx) {
                                if(participant.participantType == participantType){
                                    participantExists = true;
                                    participant.participantLdapId = newParticipant.participantLdapId;
                                    participant.replaceChildrenParticipant = true;
                                    // $scope.costsheet.participants.splice(idx, 1, newParticipant);
                                    return false;
                                }
                            });
                            if(!participantExists){
                                $scope.costsheet.participants.push(newParticipant);
                            }
                        }
                    }

                    function updateIsApproverAdded(participants){
                        var approver = _.find(participants, function (participant) {
                            return participant.participantType == participantTypeApprover;
                        });
                        $scope.isApproverAdded = !Util.isEmpty(approver);
                    }

                    //-----------------------------------------------------------------------------------------------

                    $scope.save = function(submissionName) {

                        if (!$scope.isEdit) {
                            $scope.loading = true;
                            $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                            if($scope.costsheet.status === "FINAL") {
                                submissionName = "SaveFinal";
                            }
                            CostTrackingInfoService.saveNewCostsheetInfo(clearNotFilledElements(_.cloneDeep($scope.costsheet)), submissionName).then(function(objectInfo) {
                                var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.COSTSHEET);
                                var costsheetUpdatedMessage = $translate.instant('{{objectType}} {{costsheetTitle}} was created.', {
                                    objectType: objectTypeString,
                                    costsheetTitle: objectInfo.title
                                });
                                MessageService.info(costsheetUpdatedMessage);
                                $modalInstance.close(objectInfo);
                                $scope.loading = false;
                                $scope.loadingIcon = "fa fa-floppy-o";
                            }, function(error) {
                                $scope.loading = false;
                                $scope.loadingIcon = "fa fa-floppy-o";
                                if (error.data && error.data.message) {
                                    $scope.error = error.data.message;
                                } else {
                                    MessageService.error(error);
                                }
                            });
                        } else {
                            // Updates the ArkCase database when the user changes a costsheet attribute
                            // from the form accessed by clicking 'Edit' and then 'update costsheet button
                            $scope.loading = true;
                            $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                            checkForChanges($scope.objectInfo);
                            if (CostTrackingInfoService.validateCostsheet($scope.objectInfo)) {
                                var objectInfo = Util.omitNg($scope.objectInfo);
                                if($scope.objectInfo.status === "FINAL") {
                                    submissionName = "SaveFinal";
                                }
                                promiseSaveInfo = CostTrackingInfoService.saveCostsheetInfo(objectInfo, submissionName);
                                promiseSaveInfo.then(function(costsheetInfo) {
                                    objectInfo.modified = costsheetInfo.modified;
                                    $scope.$emit("report-object-updated", costsheetInfo);
                                    var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.COSTSHEET);
                                    var objectAction = $translate.instant('common.objectAction.updated');
                                    var costsheetUpdatedMessage = $translate.instant('{{objectType}} {{costsheetTitle}} {{action}}.', {
                                        objectType: objectTypeString,
                                        costsheetTitle: objectInfo.title,
                                        action: objectAction
                                    });
                                    MessageService.info(costsheetUpdatedMessage);
                                    $modalInstance.close(objectInfo);
                                    $scope.loading = false;
                                    $scope.loadingIcon = "fa fa-floppy-o";
                                }, function(error) {
                                    $scope.$emit("report-object-update-failed", error);
                                    $scope.loading = false;
                                    $scope.loadingIcon = "fa fa-floppy-o";
                                    if (error.data && error.data.message) {
                                        $scope.error = error.data.message;
                                    } else {
                                        MessageService.error(error);
                                    }
                                });
                            }
                            return promiseSaveInfo;
                        }
                    };

                    function checkForChanges(objectInfo) {
                        if (objectInfo.parentType != $scope.costsheet.parentType) {
                            objectInfo.parentType = $scope.costsheet.parentType;
                        }
                        if (objectInfo.parentNumber != $scope.costsheet.parentNumber) {
                            objectInfo.parentNumber = $scope.costsheet.parentNumber;
                            objectInfo.parentId = $scope.costsheet.parentId;
                        }
                        if (objectInfo.details != $scope.costsheet.details) {
                            objectInfo.details = $scope.costsheet.details;
                        }
                        if (objectInfo.status != $scope.costsheet.status) {
                            objectInfo.status = $scope.costsheet.status;
                        }

                        objectInfo.costs = $scope.costsheet.costs;


                        var addedApprover =  _.find($scope.costsheet.participants, function (participant) {
                            return participant.participantType == participantTypeApprover;
                        });

                        if(!Util.isEmpty(addedApprover)){
                            var hasApprover = false;
                            _.forEach(objectInfo.participants, function (participant, idx) {
                                if((participant.participantType == participantTypeApprover)){
                                    hasApprover = true;
                                    objectInfo.participants.splice(idx, 1, addedApprover);
                                    return false;
                                }
                            });
                            if(!hasApprover){
                                objectInfo.participants.push(addedApprover);
                            }
                        }

                        var addedOwningGroup =  _.find($scope.costsheet.participants, function (participant) {
                            return participant.participantType == participantTypeOwningGroup;
                        });

                        if(!Util.isEmpty(addedOwningGroup)){
                            var hasOwningGroup = false;
                            _.forEach(objectInfo.participants, function (participant, idx) {
                                if((participant.participantType == participantTypeOwningGroup)){
                                    hasOwningGroup = true;
                                    objectInfo.participants.splice(idx, 1, addedOwningGroup);
                                    return false;
                                }
                            });
                            if(!hasOwningGroup){
                                objectInfo.participants.push(addedOwningGroup);
                            }
                        }

                        return objectInfo;
                    }

                    function clearNotFilledElements(costsheet) {

                        if (Util.isEmpty(costsheet.details)) {
                            costsheet.details = null;
                        }

                        return costsheet;
                    }

                    $scope.sendForApproval = function() {
                        var submissionName = "Submit";
                        $scope.save(submissionName);
                    };

                    $scope.cancelModal = function() {
                        $modalInstance.dismiss();
                    };

                    $scope.refresh = function(id) {
                        $scope.$emit('report-object-refreshed', id);
                    };

                } ]);