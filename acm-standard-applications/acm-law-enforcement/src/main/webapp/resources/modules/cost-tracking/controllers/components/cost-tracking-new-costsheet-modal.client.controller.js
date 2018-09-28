'use strict';

angular.module('cost-tracking').controller(
        'CostTracking.NewCostsheetController',
        [ '$scope', '$stateParams', '$translate', '$modalInstance', 'CostTracking.InfoService', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Person.InfoService', 'Object.ModelService', 'Object.ParticipantService',
                'Profile.UserInfoService',
                function($scope, $stateParams, $translate, $modalInstance, CostTrackingInfoService, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, ObjectService, modalParams, PersonInfoService, ObjectModelService, ObjectParticipantService, UserInfoService) {

                    $scope.modalParams = modalParams;
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.isEdit = $scope.modalParams.isEdit;
                    $scope.sumAmount = 0;
                    $scope.disableCostType = false;

                    ConfigService.getModuleConfig("cost-tracking").then(function(moduleConfig) {
                        $scope.config = moduleConfig;

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
                                costs: [ {} ],
                                participants: [ {} ]
                            };

                            if(!Util.isEmpty($scope.modalParams.parentType) && !Util.isEmpty($scope.modalParams.parentNumber) && !Util.isEmpty($scope.modalParams.parentId)) {
                                $scope.costsheet.parentId = $scope.modalParams.parentId;
                                $scope.costsheet.parentType = $scope.modalParams.parentType;
                                $scope.costsheet.parentNumber = $scope.modalParams.parentNumber;
                                $scope.costsheet.title =  "Costsheet" + " " + $scope.modalParams.parentNumber;
                                $scope.isTypeSelected = true;
                                $scope.disableCostType = true;
                            }
                        }

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
                        $scope.objectInfo = $scope.modalParams.costsheet;
                        var tmpCostsheet = $scope.modalParams.costsheet;
                        $scope.isApproverAdded = !Util.isArrayEmpty($scope.objectInfo.participants) && $scope.objectInfo.participants[0].hasOwnProperty("id") ? true : false;
                        $scope.updateBalance($scope.objectInfo.costs);

                        if (tmpCostsheet.participants != undefined) {
                            if (!Util.isArrayEmpty(tmpCostsheet.participants)) {
                                _.forEach(tmpCostsheet.participants, function(participant) {
                                    UserInfoService.getUserInfoById(participant.participantLdapId).then(function(userInfo) {
                                        participant.participantFullName = userInfo.fullName;
                                    });
                                });
                            } else {
                                tmpCostsheet.participants = [ {} ];
                            }
                        }

                        if (tmpCostsheet.costs != undefined) {
                            _.forEach(tmpCostsheet.costs, function(cost) {
                                cost.date = new Date(cost.date);
                            });
                        }

                        $scope.costsheet = {
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
                    });

                    UserInfoService.getUserInfo().then(function(infoData) {
                        $scope.costsheet.user = infoData;
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
                                $scope.costsheet.title = "Costsheet" + " " + selected.name;
                            }
                        });

                    };

                    //-----------------------------------   costs    -------------------------------------------
                    $scope.addCost = function() {
                        $timeout(function() {
                            if (!_.isEmpty($scope.costsheet.costs[0])) {
                                $scope.costsheet.costs.push({});
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

                    // ---------------------------            approver         --------------------------------------
                    var participantType = 'approver';

                    $scope.addApprover = function() {
                        $timeout(function() {
                            $scope.searchApprover(-1);
                        }, 0);
                    };

                    $scope.removeApprover = function(approver) {
                        $timeout(function() {
                            _.remove($scope.costsheet.participants, function(object) {
                                return object === approver;
                            });
                            $scope.isApproverAdded = !Util.isArrayEmpty($scope.costsheet.participants);
                        }, 0);
                    };

                    $scope.searchApprover = function(index) {
                        var participant = index > -1 ? $scope.costsheet.participants[index] : {};

                        var params = {};

                        params.header = $translate.instant("costTracking.comp.newCostsheet.userSearch.title");
                        params.filter = 'fq="object_type_s": USER &fq="status_lcs": VALID';
                        params.config = $scope.userSearchConfig;

                        var modalInstance = $modal.open({
                            templateUrl: "directives/core-participants/core-participants-picker-modal.client.view.html",
                            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                $scope.modalInstance = $modalInstance;
                                $scope.header = params.header;
                                $scope.filter = params.filter;
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
                        modalInstance.result.then(function(selection) {
                            if (selection) {
                                participant.className = $scope.participantsConfig.className;
                                participant.participantType = participantType;
                                participant.participantLdapId = selection.object_id_s;
                                participant.participantFullName = selection.name;
                                if (ObjectParticipantService.validateParticipants([ participant ], true) && !_.includes($scope.costsheet.participants, participant)) {
                                    $scope.costsheet.participants.push(participant);
                                }
                                if ($scope.isEdit) {
                                    $scope.isApproverAdded = !Util.isArrayEmpty($scope.objectInfo.participants);
                                } else {
                                    $scope.isApproverAdded = !Util.isArrayEmpty($scope.costsheet.participants);
                                }
                            }
                        });
                    };

                    //-----------------------------------------------------------------------------------------------

                    $scope.save = function(submissionName) {

                        if (!$scope.isEdit) {
                            $scope.loading = true;
                            $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                            CostTrackingInfoService.saveNewCostsheetInfo(clearNotFilledElements(_.cloneDeep($scope.costsheet)), submissionName).then(function(objectInfo) {
                                var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.COSTSHEET);
                                var costsheetUpdatedMessage = $translate.instant('{{objectType}} {{costsheetTitle}} was created.', {
                                    objectType: objectTypeString,
                                    costsheetTitle: objectInfo.title
                                });
                                MessageService.info(costsheetUpdatedMessage);
                                ObjectService.showObject(ObjectService.ObjectTypes.COSTSHEET, objectInfo.id);
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
                                promiseSaveInfo = CostTrackingInfoService.saveCostsheetInfo(clearNotFilledElements(_.cloneDeep(objectInfo)), submissionName);
                                promiseSaveInfo.then(function(costsheetInfo) {
                                    $scope.$emit("report-object-updated", costsheetInfo);
                                    var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.COSTSHEET);
                                    var costsheetUpdatedMessage = $translate.instant('{{objectType}} {{costsheetTitle}} was updated.', {
                                        objectType: objectTypeString,
                                        costsheetTitle: objectInfo.title
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
                        return objectInfo;
                    }

                    function clearNotFilledElements(costsheet) {

                        if (Util.isEmpty(costsheet.details)) {
                            costsheet.details = null;
                        }

                        _.remove(costsheet.participants, function(participant) {
                            if (!participant.participantFullName) {
                                return true;
                            } else {
                                //remove temporary values
                                delete participant['participantFullName'];
                                return false;
                            }
                        });

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