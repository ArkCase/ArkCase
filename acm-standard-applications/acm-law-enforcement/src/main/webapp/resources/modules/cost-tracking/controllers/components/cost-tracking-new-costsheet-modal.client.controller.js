'use strict';

angular.module('cost-tracking').controller(
        'CostTracking.NewCostsheetController',
        [ '$scope', '$stateParams', '$translate', '$modalInstance', 'CostTracking.InfoService', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Person.InfoService', 'Object.ModelService', 'Object.ParticipantService',
                'Profile.UserInfoService',
                function($scope, $stateParams, $translate, $modalInstance, CostTrackingInfoService, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, ObjectService, modalParams, PersonInfoService, ObjectModelService, ObjectParticipantService, UserInfoService) {

                    $scope.modalParams = modalParams;
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.selectedFiles = [];
                    $scope.userSearchConfig = null;
                    $scope.isEdit = $scope.modalParams.isEdit;
                    $scope.isTypeSelected = false;

                    ConfigService.getModuleConfig("cost-tracking").then(function(moduleConfig) {
                        $scope.config = moduleConfig;

                        $scope.newCostObjectPicker = _.find(moduleConfig.components, {
                            id: "newCostObjectPicker"
                        });

                        return moduleConfig;
                    });

                    if ($scope.isEdit) {

                        $scope.objectInfo = $scope.modalParams.costsheet;
                        var tmpCostsheet = $scope.modalParams.costsheet;

                        $scope.costsheet = {
                            caseType: $scope.objectInfo.caseType,
                            title: $scope.objectInfo.title,
                            details: $scope.objectInfo.details,
                            initiator: $scope.modalParams.initiator,
                            personAssociations: tmpCostsheet.personAssociations,
                            participants: tmpCostsheet.participants
                        };

                    } else {

                        //new costsheet with predefined values
                        $scope.costsheet = {
                            className: 'com.armedia.acm.services.costsheet.model.AcmCostsheet',
                            status: 'DRAFT',
                            parentId: '',
                            parentType: '',
                            parentNumber: '',
                            details: '',
                            costs: [ {} ]
                        };
                    }
                    var initiatorType = 'Initiator';

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
                        params.header = $translate.instant("Search object");

                        if ($scope.costsheet.parentType == ObjectService.ObjectTypes.CASE_FILE) {
                            params.filter = 'fq="object_type_s": CASE_FILE';
                        } else if ($scope.costsheet.parentType == ObjectService.ObjectTypes.COMPLAINT) {
                            params.filter = 'fq="object_type_s": COMPLAINT';
                        }

                        // var filter = '"Object Type": CASE_FILE' + '&fq="!status_lcs":"CLOSED"';
                        // var f = 'object_type_s:CASE_FILE&fq=creator_lcs=?&fq=-status_s:COMPLETE&fq=-status_s:DELETE&fq=-status_s:CLOSED';
                        // params.extraFilter = '&fq="name": ';
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
                            $scope.buildAndPushCostToCosts(-1);
                        }, 0);
                    };

                    $scope.removeCost = function(cost) {
                        $timeout(function() {
                            _.remove($scope.costsheet.costs, function(object) {
                                return object === cost;
                            });
                        }, 0);
                    };

                    $scope.buildAndPushCostToCosts = function(index) {
                        if (!_.isEmpty($scope.costsheet.costs[0])) {
                            $scope.costsheet.costs.push({});
                        }
                        // var cost = index > -1 ? $scope.costsheet.costs[index] : {};
                        // $scope.costsheet.costs.push(cost);
                    };

                    // ---------------------------            initiator         --------------------------------------
                    var newPersonAssociation = function() {
                        return {
                            id: null,
                            personType: "",
                            parentId: $scope.casefile.id,
                            parentType: ObjectService.ObjectTypes.CASE_FILE,
                            parentTitle: $scope.casefile.caseNumber,
                            personDescription: "",
                            notes: "",
                            person: null,
                            className: "com.armedia.acm.plugins.person.model.PersonAssociation"
                        };
                    };

                    $scope.addPersonInitiator = function() {
                        pickPerson(null);
                    };

                    function pickPerson(association) {

                        var params = {};
                        params.types = $scope.personTypesInitiator;
                        params.type = initiatorType;
                        params.typeEnabled = false;
                        association = new newPersonAssociation();

                        var modalInstance = $modal.open({
                            scope: $scope,
                            animation: true,
                            templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                            controller: 'Common.AddPersonModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (data.isNew) {
                                PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function(response) {
                                    data.person = response.data;
                                    $scope.casefile.initiator = data.person.givenName + " " + data.person.familyName;
                                    updatePersonAssociationData(association, data.person, data);
                                });
                            } else {
                                PersonInfoService.getPersonInfo(data.personId).then(function(person) {
                                    $scope.casefile.initiator = person.givenName + " " + person.familyName;
                                    updatePersonAssociationData(association, person, data);
                                });
                            }
                        });
                    }

                    function updatePersonAssociationData(association, person, data) {
                        association.person = person;
                        association.personType = data.type;
                        association.personDescription = data.description;
                        association.parentTitle = $scope.casefile.caseNumber;
                        if (!association.id) {
                            $scope.casefile.originator = association;
                        }
                    }

                    //-----------------------------------------------------------------------------------------------

                    $scope.save = function() {

                        if (!$scope.isEdit) {
                            $scope.loading = true;
                            $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                            CostTrackingInfoService.saveCostsheetInfoNewCostsheet(clearNotFilledElements(_.cloneDeep($scope.costsheet))).then(function(objectInfo) {
                                var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.COSTSHEET);
                                var costsheetUpdatedMessage = $translate.instant('{{objectType}} {{costsheetTitle}} was created.', {
                                    objectType: objectTypeString,
                                    costsheetTitle: objectInfo.title
                                });
                                MessageService.info(costsheetUpdatedMessage);
                                ObjectService.showObject(ObjectService.ObjectTypes.COSTSHEET, objectInfo.id);
                                $modalInstance.dismiss();
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
                                promiseSaveInfo = CostTrackingInfoService.saveCostsheetInfo(objectInfo);
                                promiseSaveInfo.then(function(costsheetInfo) {
                                    $scope.$emit("report-object-updated", costsheetInfo);
                                    var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.COSTSHEET);
                                    var costsheetUpdatedMessage = $translate.instant('{{objectType}} {{costsheetTitle}} was updated.', {
                                        objectType: objectTypeString,
                                        costsheetTitle: objectInfo.title
                                    });
                                    MessageService.info(costsheetUpdatedMessage);
                                    $modalInstance.dismiss();
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
                        if (objectInfo.title != $scope.costsheet.title) {
                            objectInfo.title = $scope.costsheet.title
                        }
                        if (objectInfo.caseType != $scope.costsheet.caseType) {
                            objectInfo.caseType = $scope.costsheet.caseType
                        }
                        if ($scope.costsheet.originator != undefined && objectInfo.originator.person != $scope.costsheet.originator.person) {
                            objectInfo.originator.person = $scope.costsheet.originator.person
                        }
                        if (objectInfo.details != $scope.costsheet.details) {
                            objectInfo.details = $scope.costsheet.details
                        }
                        return objectInfo;
                    }

                    function clearNotFilledElements(costsheet) {

                        if (Util.isEmpty(costsheet.details)) {
                            costsheet.details = null;
                        }

                        return costsheet;
                    }

                    $scope.cancelModal = function() {
                        $modalInstance.dismiss();
                    };

                } ]);