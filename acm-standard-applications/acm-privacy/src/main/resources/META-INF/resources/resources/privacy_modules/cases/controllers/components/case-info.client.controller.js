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

            var defaultDateTimeUTCFormat = $translate.instant("common.defaultDateTimeUTCFormat");
            var defaultDateTimePickerFormat = $translate.instant("common.defaultDateTimePickerFormat");

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

            $scope.updateDueDate = function (data, oldDate) {
                if (!Util.isEmpty(data)) {
                    if (UtilDateService.compareDatesForUpdate(data, $scope.objectInfo.dueDate)) {
                        var correctedDueDate = new Date(data);
                        var startDate = new Date($scope.objectInfo.receivedDate);
                        if (correctedDueDate < startDate) {
                            $scope.dateInfo.dueDate = $scope.dueDateBeforeChange;
                            DialogService.alert($translate.instant("cases.comp.info.alertMessage ") + $filter("date")(startDate, $translate.instant('common.defaultDateTimeUIFormat')));
                        } else {
                            $scope.objectInfo.dueDate = moment.utc(correctedDueDate).format(defaultDateTimeUTCFormat);
                            $scope.dueDate.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                            $scope.dueDate.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                            $scope.dateInfo.dueDate = $scope.dueDate.dueDateInfoUIPicker;
                            // unbind due date watcher before case save so that when user switch to different case
                            // watcher won't be fired before landing on that different case
                            dueDateWatch();
                            $scope.saveCase();
                        }
                    }
                } else {
                    if (!oldDate) {
                        $scope.objectInfo.dueDate = $scope.dueDateBeforeChange;
                        $scope.dueDate.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                        $scope.dueDate.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                        $scope.dateInfo.dueDate = $scope.dueDate.dueDateInfoUIPicker;
                        // unbind due date watcher before case save so that when user switch to different case
                        // watcher won't be fired before landing on that different case
                        dueDateWatch();
                        $scope.saveCase();
                    }
                }
            };

            $scope.dueDate = {
                dueDateInfo: null
            };

            var onObjectInfoRetrieved = function (data) {
                // unbind watcher when user switch between tasks. When we call $watch() method,
                // angularJS returns an unbind function that will kill the $watch() listener when its called.
                dueDateWatch();
                AdminHolidayService.getHolidays().then(function (response) {
                    $scope.holidays = response.data.holidays;
                    $scope.includeWeekends = response.data.includeWeekends;

                    $scope.dateInfo = $scope.dateInfo || {};
                    if (!Util.isEmpty($scope.objectInfo.dueDate)) {
                        $scope.dateInfo.dueDate = moment.utc($scope.objectInfo.dueDate).local().format(defaultDateTimeUTCFormat);
                        $scope.dueDate.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                        $scope.dueDate.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                    } else {
                        $scope.dateInfo.dueDate = null;
                        $scope.dueDate.dueDateInfoUIPicker = moment(new Date).format(defaultDateTimePickerFormat);
                        $scope.dueDate.dueDateInfo = moment.utc(new Date()).local();
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


                    $scope.minDate = moment.utc(new Date(data.receivedDate)).local();
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

            // store function reference returned by $watch statement in variable
            var dueDateWatch = $scope.$watch('dueDate.dueDateInfo', dueDateChangeFn, true);

            // update due date and save task
            var dueDateChangeFn = function (newValue, oldValue) {
                if (newValue && !moment(newValue).isSame(moment(oldValue)) && $scope.dueDate.isOpen) {
                    $scope.updateDueDate(newValue);
                }
            }

            // register watcher when user open date picker
            $scope.registerWatcher = function () {
                dueDateWatch = $scope.$watch('dueDate.dueDateInfo', dueDateChangeFn, true);
            }

        } ]);
