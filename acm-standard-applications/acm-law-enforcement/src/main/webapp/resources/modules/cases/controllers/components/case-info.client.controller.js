'use strict';

angular.module('cases').controller(
        'Cases.InfoController',
        [
                '$scope',
                '$state',
                '$stateParams',
                '$translate',
                '$modal',
                'UtilService',
                'Util.DateService',
                'ConfigService',
                'Object.LookupService',
                'Case.LookupService',
                'Case.InfoService',
                'Object.ModelService',
                'MessageService',
                'ObjectService',
                'Object.ParticipantService',
                'SearchService',
                'Search.QueryBuilderService',
                'Helper.ObjectBrowserService',
                'Helper.UiGridService',
                'Dialog.BootboxService',
                '$filter',
                'Cases.SuggestedCases',
                function($scope, $state, $stateParams, $translate, $modal, Util, UtilDateService, ConfigService, ObjectLookupService, CaseLookupService, CaseInfoService, ObjectModelService, MessageService, ObjectService, ObjectParticipantService, SearchService, SearchQueryBuilder,
                        HelperObjectBrowserService, HelperUiGridService, DialogService, $filter, SuggestedCasesService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cases",
                        componentId: "info",
                        retrieveObjectInfo: CaseInfoService.getCaseInfo,
                        validateObjectInfo: CaseInfoService.validateCaseInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    var promiseUsers = gridHelper.getUsers();

                    ConfigService.getComponentConfig("cases", "participants").then(function(componentConfig) {
                        $scope.config = componentConfig;
                    });
                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.userOrGroupSearchConfig = _.find(moduleConfig.components, {
                            id: "userOrGroupSearch"
                        });
                    });

                    ObjectLookupService.getGroups().then(function(groups) {
                        var options = [];
                        _.each(groups, function(group) {
                            options.push({
                                value: group.name,
                                text: group.name
                            });
                        });
                        $scope.owningGroups = options;
                        return groups;
                    });

                    ObjectLookupService.getLookupByLookupName("priorities").then(function(priorities) {
                        $scope.priorities = priorities;
                        return priorities;
                    });

                    ObjectLookupService.getLookupByLookupName("caseFileTypes").then(function(caseTypes) {
                        $scope.caseTypes = caseTypes;
                        return caseTypes;
                    });

                    ObjectLookupService.getLookupByLookupName("changeCaseStatuses").then(function(caseStatuses) {
                        $scope.caseStatuses = caseStatuses;
                        return caseStatuses;
                    });

                    $scope.userOrGroupSearch = function() {
                        var assigneUserName = _.find($scope.userFullNames, function(user) {
                            return user.id === $scope.assignee
                        });
                        var params = {
                            owningGroup: $scope.owningGroup,
                            assignee: assigneUserName
                        };
                        var modalInstance = $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'modules/common/views/user-group-picker-modal.client.view.html',
                            controller: 'Common.UserGroupPickerController',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                $filter: function() {
                                    return $scope.userOrGroupSearchConfig.userOrGroupSearchFilters.userOrGroupFacetFilter;
                                },
                                $extraFilter: function() {
                                    return $scope.userOrGroupSearchConfig.userOrGroupSearchFilters.userOrGroupFacetExtraFilter;
                                },
                                $config: function() {
                                    return $scope.userOrGroupSearchConfig;
                                },
                                $params: function() {
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

                                    $scope.assignee = selectedUser.object_id_s;
                                    $scope.updateAssignee();

                                    //set for AFDP-6831 to inheritance in the Folder/file participants
                                    var len = $scope.objectInfo.participants.length;
                                    for (var i = 0; i < len; i++) {
                                        if($scope.objectInfo.participants[i].participantType =='assignee' || $scope.objectInfo.participants[i].participantType =='owning group'){
                                            $scope.objectInfo.participants[i].replaceChildrenParticipant = true;
                                        }
                                    }
                                    if (selectedGroup) {
                                        $scope.owningGroup = selectedGroup.object_id_s;
                                        $scope.updateOwningGroup();
                                        $scope.saveCase();

                                    } else {
                                        $scope.saveCase();
                                    }

                                    return;
                                } else if (selectedObjectType === 'GROUP') { // Selected group
                                    var selectedUser = selection.detailSelectedItems;
                                    var selectedGroup = selection.masterSelectedItem;
                                    $scope.owningGroup = selectedGroup.object_id_s;
                                    $scope.updateOwningGroup();

                                    //set for AFDP-6831 to inheritance in the Folder/file participants
                                    var len = $scope.objectInfo.participants.length;
                                    for (var i = 0; i < len; i++) {
                                        if($scope.objectInfo.participants[i].participantType =='owning group'|| $scope.objectInfo.participants[i].participantType =='assignee') {
                                            $scope.objectInfo.participants[i].replaceChildrenParticipant = true;
                                        }
                                    }
                                    if (selectedUser) {
                                        $scope.assignee = selectedUser.object_id_s;
                                        $scope.updateAssignee();
                                        $scope.saveCase();
                                    } else {
                                        $scope.saveCase();
                                    }

                                    return;
                                }
                            }

                        }, function() {
                            // Cancel button was clicked.
                            return [];
                        });

                    };

                    var onObjectInfoRetrieved = function(data) {
                        $scope.dateInfo = $scope.dateInfo || {};
                        $scope.dateInfo.dueDate = $scope.objectInfo.dueDate;
                        $scope.dueDateBeforeChange = $scope.dateInfo.dueDate;
                        $scope.owningGroup = ObjectModelService.getGroup(data);
                        $scope.assignee = ObjectModelService.getAssignee(data);

                        var utcDate = moment.utc(UtilDateService.dateToIso(new Date(data.created))).format();
                        $scope.maxYear = moment(utcDate).add(1, 'years').toDate().getFullYear();
                        $scope.minYear = new Date(data.created).getFullYear();

                        CaseLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(function(approvers) {
                            var options = [];
                            _.each(approvers, function(approver) {
                                options.push({
                                    id: approver.userId,
                                    name: approver.fullName
                                });
                            });
                            $scope.assignees = options;
                            return approvers;
                        });

                        SuggestedCasesService.getSuggestedCases($scope.objectInfo.title, $scope.objectInfo.id).then(function (value) {
                            $scope.hasSuggestedCases = value.data.length > 0 ? true : false;
                            $scope.numberOfSuggestedCases = value.data.length;
                        });
                    };

                    // Updates the ArkCase database when the user changes a case attribute
                    // in a case top bar menu item and clicks the save check button
                    $scope.saveCase = function() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = CaseInfoService.saveCaseInfo(objectInfo);
                            promiseSaveInfo.then(function(caseInfo) {
                                $scope.$emit("report-object-updated", caseInfo);
                                return caseInfo;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    };
                    $scope.updateOwningGroup = function() {
                        ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
                    };
                    $scope.updateAssignee = function() {
                        ObjectModelService.setAssignee($scope.objectInfo, $scope.assignee);
                    };
                    $scope.updateDueDate = function(data) {
                        if (!Util.isEmpty(data)) {
                            var correctedDueDate = new Date(data);
                            var startDate = new Date($scope.objectInfo.create_date_tdt);
                            if(correctedDueDate < startDate){
                                $scope.dateInfo.dueDate = $scope.dueDateBeforeChange;
                                DialogService.alert($translate.instant("cases.comp.info.alertMessage ") + $filter("date")(startDate, $translate.instant('common.defaultDateTimeUIFormat')));
                            }else {
                                $scope.objectInfo.dueDate = moment.utc(UtilDateService.dateToIso(correctedDueDate)).format();
                                $scope.saveCase();
                            }
                        }else {
                            $scope.objectInfo.dueDate = $scope.dueDateBeforeChange;
                            $scope.saveCase();
                        }
                    };
                    
                    $scope.suggestedCases = function () {
                        $state.go('cases.suggestedCases',{
                            id: $scope.objectInfo.id
                        });
                    };

                } ]);
