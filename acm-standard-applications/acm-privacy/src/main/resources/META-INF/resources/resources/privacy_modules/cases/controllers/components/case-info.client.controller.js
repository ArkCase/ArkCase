'use strict';

angular.module('cases').controller(
    'Cases.InfoController',
    ['$scope', '$stateParams', '$state', '$translate', '$timeout', 'UtilService', 'Util.DateService', 'ConfigService', 'Object.LookupService', 'Case.LookupService', 'Case.InfoService', 'Object.ModelService', 'Helper.ObjectBrowserService', 'DueDate.Service', 'Admin.HolidayService',
        'MessageService', '$modal', 'LookupService', 'Admin.PrivacyConfigService', 'Admin.ObjectTitleConfigurationService', 'ObjectService', '$filter', 'Dialog.BootboxService',
        function ($scope, $stateParams, $state, $translate, $timeout, Util, UtilDateService, ConfigService, ObjectLookupService, CaseLookupService, CaseInfoService, ObjectModelService, HelperObjectBrowserService, DueDateService, AdminHolidayService, MessageService, $modal, LookupService, AdminPrivacyConfigService, AdminObjectTitleConfigurationService, ObjectService, $filter, DialogService) {

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

            AdminPrivacyConfigService.getPrivacyConfig().then(function (response) {
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

            ObjectLookupService.getLookupByLookupName("requestTypes").then(function (requestTypes) {
                $scope.requestTypes = requestTypes;
                return requestTypes;
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
            $scope.privacyConfig = {};

            $scope.updateDueDate = function (data) {
                if (!Util.isEmpty(data)) {
                    var correctedDueDate = new Date(data);
                    var startDate = new Date($scope.objectInfo.created);
                    if (correctedDueDate < startDate) {
                        $scope.dateInfo.dueDate = $scope.dueDateBeforeChange;
                        DialogService.alert($translate.instant("cases.comp.info.alertMessage ") + $filter("date")(startDate, $translate.instant('common.defaultDateTimeUIFormat')));
                    } else {
                        $scope.objectInfo.dueDate = moment.utc(correctedDueDate).format("YYYY-MM-DDTHH:mm:ss.sss");
                        $scope.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local().format('MM/DD/YYYY');
                        $scope.dateInfo.dueDate = $scope.dueDateInfo;
                        $scope.saveCase();
                    }
                } else {
                    $scope.objectInfo.dueDate = moment.utc($scope.dueDateBeforeChange).format("YYYY-MM-DDTHH:mm:ss.sss");
                    ;
                    $scope.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local().format('MM/DD/YYYY');
                    $scope.dateInfo.dueDate = $scope.dueDateInfo;
                    $scope.saveCase();
                }
            };

            var onObjectInfoRetrieved = function (data) {
                AdminHolidayService.getHolidays().then(function (response) {
                    $scope.holidays = response.data.holidays;
                    $scope.includeWeekends = response.data.includeWeekends;

                    $scope.dateInfo = $scope.dateInfo || {};
                    if (!Util.isEmpty($scope.objectInfo.dueDate)) {
                        $scope.dateInfo.dueDate = moment.utc($scope.objectInfo.dueDate).local().format('MM/DD/YYYY');
                        $scope.dueDateInfo = $scope.dateInfo.dueDate;
                    } else {
                        $scope.dateInfo.dueDate = null;
                        $scope.dueDateInfo = new Date();
                        $scope.dueDateInfo = moment($scope.dueDateInfo).format('MM/DD/YYYY');
                    }
                    $scope.dueDateBeforeChange = $scope.dateInfo.dueDate;

                    $scope.calculateDaysObj = {};
                    $scope.owningGroup = ObjectModelService.getGroup(data);
                    $scope.assignee = ObjectModelService.getAssignee(data);

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

                    var utcDate = moment.utc(UtilDateService.dateToIso(new Date(data.created))).format();
                    $scope.maxYear = moment(utcDate).add(1, 'years').toDate().getFullYear();
                    $scope.minYear = new Date(data.created).getFullYear();
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

                $scope.componentAgency = null;
                ObjectLookupService.getLookupByLookupName("componentsAgencies").then(function (componentsAgencies) {
                    $scope.componentsAgencies = componentsAgencies;
                    if ($scope.objectInfo.hasOwnProperty('componentAgency')) {
                        var notification = _.find($scope.componentsAgencies, {
                            key: $scope.objectInfo.componentAgency
                        });
                        if (typeof notification !== "undefined") {
                            $scope.componentAgency = notification.value;
                        }
                    }
                });

                $scope.isAmendmentAdded = data.amendmentFlag;

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
                            }else {
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
                if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                    var objectInfo = Util.omitNg($scope.objectInfo);
                    promiseSaveInfo = CaseInfoService.saveSubjectAccessRequestInfo(objectInfo);
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

            $scope.updateNotificationGroup = function() {
                var notification = _.find($scope.notificationGroups, {
                    key: $scope.objectInfo.notificationGroup
                });
                if(typeof notification !== "undefined") {
                    $scope.notificationGroup = notification.value;
                }
                saveCase();
            };

            $scope.updateComponentAgency = function () {
                var notification = _.find($scope.componentsAgencies, {
                    key: $scope.objectInfo.componentAgency
                });
                if (typeof notification !== "undefined") {
                    $scope.componentAgency = notification.value;
                }
                saveCase();
            };

            $scope.setExternalIdentifier = function (data) {
                if (!Util.isEmpty(data)) {
                    $scope.objectInfo.externalIdentifier = data;
                    $scope.saveCase();
                }
            }


        } ]);