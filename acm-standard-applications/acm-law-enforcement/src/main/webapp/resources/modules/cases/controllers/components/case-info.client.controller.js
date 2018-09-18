'use strict';

angular.module('cases').controller(
        'Cases.InfoController',
        [
                '$scope',
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
                function($scope, $stateParams, $translate, $modal, Util, UtilDateService, ConfigService, ObjectLookupService, CaseLookupService, CaseInfoService, ObjectModelService, MessageService, ObjectService, ObjectParticipantService, SearchService, SearchQueryBuilder,
                        HelperObjectBrowserService, HelperUiGridService) {

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
                    ConfigService.getModuleConfig("cases").then(function(moduleConfig) {
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
                            return user.name === $scope.assignee
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
                                    if (selectedUser) {
                                        $scope.assignee = selectedUser.object_id_s;
                                        $scope.updateAssignee();
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
                        $scope.owningGroup = ObjectModelService.getGroup(data);
                        $scope.assignee = ObjectModelService.getAssignee(data);

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
                    $scope.updateDueDate = function() {
                        var correctedDueDate = UtilDateService.convertToCurrentTime($scope.dateInfo.dueDate);
                        $scope.objectInfo.dueDate = moment.utc(UtilDateService.dateToIso(correctedDueDate)).format();
                        $scope.saveCase();
                    };

                } ]);
