'use strict';

angular.module('tasks').controller(
        'Tasks.InfoController',
        [
                '$scope',
                '$stateParams',
                '$translate',
                '$filter',
                '$modal',
                'UtilService',
                'Util.DateService',
                'ConfigService',
                'LookupService',
                'Object.LookupService',
                'Task.InfoService',
                'Object.ModelService',
                'Helper.ObjectBrowserService',
                'Task.AlertsService',
                'ObjectService',
                'Helper.UiGridService',
                'Object.ParticipantService',
                'Case.InfoService',
                'Complaint.InfoService',
                'SearchService',
                'Search.QueryBuilderService',
                function($scope, $stateParams, $translate, $filter, $modal, Util, UtilDateService, ConfigService, LookupService, ObjectLookupService, TaskInfoService, ObjectModelService, HelperObjectBrowserService, TaskAlertsService, ObjectService, HelperUiGridService, ObjectParticipantService,
                        CaseInfoService, ComplaintInfoService, SearchService, SearchQueryBuilder) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "tasks",
                        componentId: "info",
                        retrieveObjectInfo: TaskInfoService.getTaskInfo,
                        validateObjectInfo: TaskInfoService.validateTaskInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });
                    var promiseUsers = gridHelper.getUsers();

                    ConfigService.getComponentConfig("tasks", "info").then(function(componentConfig) {
                        $scope.config = componentConfig;
                    });

                    LookupService.getUsers().then(function(users) {
                        var options = [];
                        _.each(users, function(user) {
                            options.push({
                                object_id_s: user.object_id_s,
                                name: user.name
                            });
                        });
                        $scope.assignableUsers = options;
                        return users;
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

                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.userOrGroupSearchConfig = _.find(moduleConfig.components, {
                            id: "userOrGroupSearch"
                        });
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

                                    $scope.objectInfo.assignee = selectedUser.object_id_s;
                                    $scope.updateAssignee();
                                    if (selectedGroup) {
                                        $scope.objectInfo.candidateGroups = [ selectedGroup.object_id_s ];
                                        $scope.owningGroup = selectedGroup.object_id_s;
                                        $scope.updateOwningGroup();
                                        $scope.saveTask();

                                    } else {
                                        $scope.saveTask();
                                    }
                                    return;
                                } else if (selectedObjectType === 'GROUP') { // Selected group
                                    var selectedUser = selection.detailSelectedItems;
                                    var selectedGroup = selection.masterSelectedItem;

                                    $scope.objectInfo.candidateGroups = [ selectedGroup.object_id_s ];
                                    $scope.owningGroup = selectedGroup.object_id_s;
                                    $scope.updateOwningGroup();
                                    if (selectedUser) {
                                        $scope.objectInfo.assignee = selectedUser.object_id_s;
                                        $scope.updateAssignee();
                                        $scope.saveTask();
                                    } else {
                                        $scope.saveTask();
                                    }
                                    return;
                                }
                            }

                        }, function() {
                            // Cancel button was clicked.
                            return [];
                        });

                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.dateInfo = $scope.dateInfo || {};
                        $scope.dateInfo.dueDate = $scope.objectInfo.dueDate;
                        $scope.dateInfo.taskStartDate = $scope.objectInfo.taskStartDate;
                        $scope.dateInfo.isOverdue = TaskAlertsService.calculateOverdue($scope.dateInfo.dueDate);
                        $scope.dateInfo.isDeadline = TaskAlertsService.calculateDeadline($scope.dateInfo.dueDate);
                        $scope.assignee = ObjectModelService.getAssignee($scope.objectInfo);
                        $scope.taskStartDateBeforeChange = $scope.dateInfo.taskStartDate;
                        $scope.dueDateBeforeChange = $scope.dateInfo.dueDate;

                        var today = new Date();
                        if (moment($scope.dateInfo.taskStartDate).isAfter(today)) {
                            $scope.minStartDate = new Date();
                        } else {
                            $scope.minStartDate = $scope.dateInfo.taskStartDate;
                        }

                        var owningGroupParticipantType = 'owning group';
                        $scope.owningGroup = 'Unknown';

                        if (!Util.isEmpty(ObjectModelService.getGroup(objectInfo))) {
                            // If the owning group gets updated, check the participants array for the current Owning group
                            $scope.owningGroup = ObjectModelService.getGroup(objectInfo);
                        }
                        // If when creating a new Task a Group Task is created check the candidateGroups array for the Owning Group
                        else if (Util.goodMapValue($scope.objectInfo, "candidateGroups[0]", false)) {
                            $scope.owningGroup = $scope.objectInfo.candidateGroups[0];
                        }
                    };

                    $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;
                    $scope.picker = {
                        opened: false
                    };

                    $scope.taskStartDateBeforeChange = null;
                    $scope.dueDateBeforeChange = null;

                    $scope.onPickerClick = function(data, form) {
                        if (!Util.isEmpty(data)) {
                            $scope.picker.opened = true;
                            form.$setError(name, "");
                        } else {
                            form.$setError(name, "Format: M/d/yy");
                        }
                    };

                    $scope.validatePercentComplete = function(value) {
                        var pctCompleteValue = Util.goodValue(value, -1); // -1 instead of 0
                        if (pctCompleteValue < 0 || pctCompleteValue > 100) {
                            return "Invalid value";
                        }
                    };
                    $scope.saveTask = function(isChangeGroup) {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (TaskInfoService.validateTaskInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = TaskInfoService.saveTaskInfo(objectInfo);
                            promiseSaveInfo.then(function(taskInfo) {
                                $scope.$emit("report-object-updated", taskInfo);
                                TaskInfoService.resetTaskCacheById(taskInfo.taskId);
                                return TaskInfoService.getTaskInfo(taskInfo.taskId);

                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            }).then(function(taskInfo) {
                                //updadateCaseOrComplaintInfo(taskInfo);
                                onObjectInfoRetrieved(taskInfo);
                            });
                        }
                        return promiseSaveInfo;
                    };
                    $scope.updateAssignee = function() {
                        ObjectModelService.setAssignee($scope.objectInfo, $scope.objectInfo.assignee);

                    };

                    $scope.convertToCorrectCurrentTime = function(from, to) {
                        var correctedDate = null;
                        if (!Util.isEmpty(to)) {
                            correctedDate = UtilDateService.convertToCurrentTime(to);
                        } else {
                            correctedDate = UtilDateService.convertToCurrentTime(from);
                        }
                        return moment.utc(UtilDateService.dateToIso(correctedDate)).format();
                    };

                    $scope.onChange = function(data, form) {
                        if (Util.isEmpty(data)) {
                            $scope.picker.opened = false;
                            form.$setError(name, "Format: M/d/yy");
                        } else {
                            form.$setError(name, "");
                        }
                    };

                    $scope.validateStartDueDate = function() {
                        if (moment($scope.dateInfo.taskStartDate).isAfter($scope.dateInfo.dueDate)) {
                            $scope.dateInfo.dueDate = $scope.dateInfo.taskStartDate;
                            $scope.updateDueDate();
                        } else {
                            $scope.saveTask();
                        }
                    };

                    $scope.updateStartDate = function() {
                        if (!Util.isEmpty($scope.dateInfo.taskStartDate)) {
                            $scope.objectInfo.taskStartDate = $scope.convertToCorrectCurrentTime($scope.dateInfo.taskStartDate);
                            $scope.validateStartDueDate();
                        } else {
                            var tempDate = new Date($scope.taskStartDateBeforeChange);
                            $scope.dateInfo.taskStartDate = $scope.taskStartDateBeforeChange;
                            $scope.objectInfo.taskStartDate = $scope.convertToCorrectCurrentTime($scope.dateInfo.taskStartDate, tempDate);
                            $scope.validateStartDueDate();
                        }
                    };

                    $scope.updateDueDate = function() {
                        if (!Util.isEmpty($scope.dateInfo.dueDate)) {
                            var correctedDueDate = UtilDateService.convertToCurrentTime($scope.dateInfo.dueDate);
                            $scope.objectInfo.dueDate = moment.utc(UtilDateService.dateToIso(correctedDueDate)).format();
                            $scope.saveTask();
                        } else {
                            var tempDate = new Date($scope.dueDateBeforeChange);
                            $scope.dateInfo.dueDate = $scope.dueDateBeforeChange;
                            var correctedDueDate = UtilDateService.convertToCurrentTime(tempDate);
                            $scope.objectInfo.dueDate = moment.utc(UtilDateService.dateToIso(correctedDueDate)).format();
                            $scope.saveTask();
                        }
                    };
                    $scope.updateOwningGroup = function() {
                        ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
                    };

                } ]);