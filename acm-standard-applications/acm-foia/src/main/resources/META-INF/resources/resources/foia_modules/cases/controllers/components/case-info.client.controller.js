'use strict';

angular.module('cases').controller(
    'Cases.InfoController',
    [ '$scope', '$stateParams', '$translate', '$timeout', 'UtilService', 'Util.DateService', 'ConfigService', 'Object.LookupService', 'Case.LookupService', 'Case.InfoService', 'Object.ModelService', 'Helper.ObjectBrowserService', 'DueDate.Service', 'Admin.HolidayService',
        'MessageService', '$modal', 'LookupService', 'Admin.FoiaConfigService',
        function($scope, $stateParams, $translate, $timeout, Util, UtilDateService, ConfigService, ObjectLookupService, CaseLookupService, CaseInfoService, ObjectModelService, HelperObjectBrowserService, DueDateService, AdminHolidayService, MessageService, $modal, LookupService, AdminFoiaConfigService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "cases",
                componentId: "info",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo,
                onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            AdminFoiaConfigService.getFoiaConfig().then(function(response){
                $scope.isNotificationGroupEnabled = response.data.notificationGroupsEnabled;
            },function(err){
                MessageService.errorAction();
            });

            LookupService.getUserFullNames().then(function (userFullNames) {
                $scope.userFullNames = userFullNames;
                return userFullNames;
            });
            ObjectLookupService.getLookupByLookupName("priorities").then(function (priorities) {
                $scope.priorities = priorities;
                return priorities;
            });

            ObjectLookupService.getGroups().then(function (groups) {
                var options = [];
                _.each(groups, function (group) {
                    options.push({
                        value: group.name,
                        text: group.name
                    });
                });
                $scope.owningGroups = options;
                return groups;
            });

            CaseLookupService.getCaseTypes().then(function (caseTypes) {
                var options = [];
                _.forEach(caseTypes, function (item) {
                    options.push({
                        value: item,
                        text: item
                    });
                });
                $scope.caseTypes = options;
                return caseTypes;
            });
            ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
                var config = _.find(moduleConfig.components, {
                    id: "requests"
                });

                $scope.categories = config.categories;
            });
            ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                $scope.userOrGroupSearchConfig = _.find(moduleConfig.components, {
                    id: "userOrGroupSearch"
                });
            });
            $scope.foiaConfig = {};



            var onObjectInfoRetrieved = function (data) {
                AdminHolidayService.getHolidays().then(function (response) {
                    $scope.holidays = response.data.holidays;
                    $scope.includeWeekends = response.data.includeWeekends;

                    $scope.calculateOverdueObj = {};
                    $scope.owningGroup = ObjectModelService.getGroup(data);
                    $scope.assignee = ObjectModelService.getAssignee(data);
                    $scope.objectInfo.dueDate = UtilDateService.dateToIso(UtilDateService.isoToDate($scope.objectInfo.dueDate));
                    $scope.objectInfo.receivedDate = UtilDateService.dateTimeToIso(UtilDateService.isoToDate($scope.objectInfo.receivedDate));
                    if (!$scope.includeWeekends) {
                        $scope.daysLeft = DueDateService.daysLeft($scope.holidays, $scope.objectInfo.dueDate);
                        $scope.calculateOverdueObj = DueDateService.calculateOverdueDays(new Date($scope.objectInfo.dueDate), $scope.daysLeft, $scope.holidays);
                    }
                    else {
                        $scope.daysLeft = DueDateService.daysLeftWithWeekends($scope.holidays, $scope.objectInfo.dueDate);
                        $scope.calculateOverdueObj = DueDateService.calculateOverdueDaysWithWeekends(new Date($scope.objectInfo.dueDate), $scope.daysLeft, $scope.holidays);
                    }
                    CaseLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(function (approvers) {
                        var options = [];
                        _.each(approvers, function (approver) {
                            options.push({
                                id: approver.userId,
                                name: approver.fullName
                            });
                        });
                        $scope.assignees = options;
                        return approvers;
                    });
                    $scope.today = new Date();
                    $scope.receivedDateMinYear = $scope.today.getFullYear();
                    $scope.receivedDateMaxYear = moment($scope.today).add(1, 'years').toDate().getFullYear();
                });

                $scope.notificationGroup = null;
                ObjectLookupService.getLookupByLookupName("notificationGroups").then(function (notificationGroups) {
                    $scope.notificationGroups = notificationGroups;
                    if ($scope.objectInfo.hasOwnProperty('notificationGroup')) {
                        var notification = _.find($scope.notificationGroups, {
                            key: $scope.objectInfo.notificationGroup
                        });
                        if (typeof notification !== "undefined") {
                            $scope.notificationGroup = notification.value;
                        }
                    }
                });
                AdminFoiaConfigService.getFoiaConfig().then(function (response) {
                    $scope.foiaConfig = response.data;
                    $scope.foiaConfig.receivedDateEnabled = response.data.receivedDateEnabled;
                    if ($scope.foiaConfig.receivedDateEnabled || $scope.objectInfo.status !='In Review'){
                        $scope.receivedDateDisabledLink = true;
                    }else {
                        $scope.receivedDateDisabledLink = false;
                    }
                });

            };
            $scope.userOrGroupSearch = function() {
                var assigneUserName = _.find($scope.userFullNames, function (user)
                {
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
                        $params: function () {
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
                            }else {
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
                                $scope.saveCase();
                            }else {
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

            $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;

            $scope.picker = {
                opened: false
            };
            $scope.onPickerClick = function() {
                $timeout(function() {
                    $scope.picker.opened = true;
                });
            };

            /**
             * Persists the updated casefile metadata to the ArkCase database
             */
            function saveCase() {
                var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));

                if (!Util.isEmpty($scope.objectInfo.releasedDate)) {
                    $scope.objectInfo.releasedDate = UtilDateService.dateToIsoDateTime($scope.objectInfo.releasedDate);
                }
                $scope.objectInfo.recordSearchDateFrom = UtilDateService.dateToIsoDateTime($scope.objectInfo.recordSearchDateFrom);
                $scope.objectInfo.recordSearchDateTo = UtilDateService.dateToIsoDateTime($scope.objectInfo.recordSearchDateTo);

                if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                    var objectInfo = Util.omitNg($scope.objectInfo);
                    promiseSaveInfo = CaseInfoService.saveFoiaRequestInfo(objectInfo);
                    promiseSaveInfo.then(function(caseInfo) {
                        $scope.$emit("report-object-updated", caseInfo);
                        return caseInfo;
                    }, function(error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    });
                }
                return promiseSaveInfo;
            }

            // Updates the ArkCase database when the user changes a case attribute
            // in a case top bar menu item and clicks the save check button
            $scope.saveCase = function() {
                saveCase();
            };

            $scope.updateOwningGroup = function() {
                ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
            };

            $scope.updateAssignee = function() {
                ObjectModelService.setAssignee($scope.objectInfo, $scope.assignee);
            };

            $scope.$on('dueDate-changed', dueDateChanged);

            function dueDateChanged(e, newDueDate) {
                $scope.objectInfo.dueDate = UtilDateService.dateToIso(UtilDateService.isoToDate(newDueDate));
                if(!$scope.includeWeekends) {
                    $scope.daysLeft = DueDateService.daysLeft($scope.holidays, $scope.objectInfo.dueDate);
                }
                else {
                    $scope.daysLeft = DueDateService.daysLeftWithWeekends($scope.holidays, $scope.objectInfo.dueDate);
                }
            }

            $scope.updateNotificationGroup = function() {
                var notification = _.find($scope.notificationGroups, {
                    key: $scope.objectInfo.notificationGroup
                });
                if(typeof notification !== "undefined") {
                    $scope.notificationGroup = notification.value;
                }
                saveCase();
            };
            $scope.setReceivedDate = function(data){
                if (!Util.isEmpty(data)) {
                    var receivedDate = new Date(data);
                    $scope.objectInfo.receivedDate = UtilDateService.dateTimeToIso(UtilDateService.isoToDate(receivedDate));
                    $scope.saveCase();
                } else {
                }
            }

        } ]);
