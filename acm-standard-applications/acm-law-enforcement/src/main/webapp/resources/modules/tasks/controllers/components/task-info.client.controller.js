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
                'Dialog.BootboxService',
                function($scope, $stateParams, $translate, $filter, $modal, Util, UtilDateService, ConfigService, LookupService, ObjectLookupService, TaskInfoService, ObjectModelService, HelperObjectBrowserService, TaskAlertsService, ObjectService, HelperUiGridService, ObjectParticipantService,
                        CaseInfoService, ComplaintInfoService, SearchService, SearchQueryBuilder, DialogService) {

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
                        if(!Util.isEmpty($scope.objectInfo.dueDate)){
                            $scope.dateInfo.dueDate = moment.utc($scope.objectInfo.dueDate).format('MM/DD/YYYY HH:mm');
                            $scope.dueDateInfo = $scope.dateInfo.dueDate;
                        }
                        else {
                            $scope.dateInfo.dueDate = null;
                            $scope.dueDateInfo = new Date();
                            $scope.dueDateInfo = moment($scope.dueDateInfo).format('MM/DD/YYYY HH:mm');
                        }
                        if(!Util.isEmpty($scope.objectInfo.taskStartDate)){
                            $scope.dateInfo.taskStartDate = moment.utc($scope.objectInfo.taskStartDate).format('MM/DD/YYYY HH:mm');
                            $scope.startDateInfo = $scope.dateInfo.taskStartDate;
                        }
                        else {
                            $scope.dateInfo.taskStartDate = null;
                            $scope.startDateInfo = new Date();
                            $scope.startDateInfo = moment($scope.startDateInfo).format('MM/DD/YYYY HH:mm');
                        }

                        $scope.dateInfo.isOverdue = TaskAlertsService.calculateOverdue(new Date($scope.dateInfo.dueDate));
                        $scope.dateInfo.isDeadline = TaskAlertsService.calculateDeadline(new Date($scope.dateInfo.dueDate));
                        $scope.assignee = ObjectModelService.getAssignee($scope.objectInfo);
                        $scope.taskStartDateBeforeChange = $scope.dateInfo.taskStartDate;
                        $scope.dueDateBeforeChange = $scope.dateInfo.dueDate;
                        
                        var utcDate = moment.utc(UtilDateService.dateToIso(new Date($scope.dateInfo.taskStartDate))).format();
                        $scope.maxYear = moment(utcDate).add(1, 'years').toDate().getFullYear();

                        var today = new Date();
                        if (moment($scope.dateInfo.taskStartDate).isAfter(today)) {
                            $scope.minYear = today.getFullYear();
                        } else {
                            $scope.minYear = new Date($scope.dateInfo.taskStartDate).getFullYear();
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

                    $scope.defaultDatePickerFormat = UtilDateService.defaultDateTimeFormat;
                    $scope.taskStartDateBeforeChange = null;
                    $scope.dueDateBeforeChange = null;

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

                    $scope.validateStartDueDate = function() {
                        if (moment($scope.dateInfo.taskStartDate).isAfter($scope.dateInfo.dueDate)) {
                            $scope.dateInfo.dueDate = $scope.dateInfo.taskStartDate;
                            $scope.updateDueDate($scope.dateInfo.dueDate);
                        } else {
                            $scope.saveTask();
                        }
                    };

                    $scope.updateStartDate = function(data) {
                        if (!Util.isEmpty(data)) {
                            var startDate = new Date(data);
                            $scope.objectInfo.taskStartDate = moment.utc(UtilDateService.dateToIso(startDate)).format();
                            $scope.validateStartDueDate();
                        } else {
                            $scope.objectInfo.taskStartDate = $scope.taskStartDateBeforeChange;
                        }
                    };

                    $scope.updateDueDate = function(data) {
                        if (!Util.isEmpty(data)) {
                            var dueDate = new Date(data);
                            var startDate = new Date($scope.dateInfo.taskStartDate);
                            if(dueDate < startDate){
                                $scope.dateInfo.dueDate = $scope.dueDateBeforeChange;
                                DialogService.alert($translate.instant('tasks.comp.info.alertMessage' ) + $filter("date")(startDate, $translate.instant('common.defaultDateTimeUIFormat')));
                            }else {
                                $scope.objectInfo.dueDate = moment.utc(dueDate).format();
                                $scope.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local().format('MM/DD/YYYY HH:mm');
                                $scope.dateInfo.dueDate = $scope.dueDateInfo;
                                $scope.saveTask();
                            }
                        } else {
                            $scope.objectInfo.dueDate = $scope.dueDateBeforeChange;
                            $scope.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local().format('MM/DD/YYYY HH:mm');
                            $scope.dateInfo.dueDate = $scope.dueDateInfo;
                            $scope.saveTask();
                        }
                    };
                    $scope.updateOwningGroup = function() {
                        ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
                    };

                } ]);