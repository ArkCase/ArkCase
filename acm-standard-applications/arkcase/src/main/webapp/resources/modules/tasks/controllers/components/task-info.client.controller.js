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
            var defaultDateTimeUTCFormat = $translate.instant("common.defaultDateTimeUTCFormat");
            var defaultDateTimePickerFormat = $translate.instant("common.defaultDateTimePickerFormat");

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

            $scope.startDate = {
                startDateInfo: null,
            };

            $scope.datepickerDueDateOptions = {
                dueDateInfo: null
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                // unbind both watchers when user switch between tasks. When we call $watch() method,
                // angularJS returns an unbind function that will kill the $watch() listener when its called.
                dueDateWatch();
                startDateWatch();
                $scope.objectInfo = objectInfo;
                $scope.dateInfo = $scope.dateInfo || {};
                if(!Util.isEmpty($scope.objectInfo.dueDate)){
                    $scope.dateInfo.dueDate = moment.utc($scope.objectInfo.dueDate).local().format(defaultDateTimeUTCFormat);
                    $scope.datepickerDueDateOptions.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                    $scope.datepickerDueDateOptions.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                }
                else {
                    $scope.dateInfo.dueDate = null;
                    $scope.datepickerDueDateOptions.dueDateInfo = moment.utc(new Date()).local();
                    $scope.datepickerDueDateOptions.dueDateInfoUIPicker = moment(new Date()).format(defaultDateTimePickerFormat);
                }
                if(!Util.isEmpty($scope.objectInfo.taskStartDate)) {
                    $scope.dateInfo.taskStartDate = moment.utc($scope.objectInfo.taskStartDate).local().format(defaultDateTimeUTCFormat);
                    $scope.startDate.startDateInfo = moment($scope.objectInfo.taskStartDate).local();
                    $scope.startDate.startDateInfoUIPicker = moment($scope.objectInfo.taskStartDate).format(defaultDateTimePickerFormat);
                }
                else {
                    $scope.dateInfo.taskStartDate = null;
                    $scope.startDate.startDateInfo = moment(new Date()).local();
                    $scope.startDate.startDateInfoUIPicker = moment(new Date()).format(defaultDateTimePickerFormat);
                }
                $scope.dateInfo.isOverdue = TaskAlertsService.calculateOverdue(new Date($scope.dateInfo.dueDate)) && $scope.objectInfo.status !== "CLOSED";
                $scope.dateInfo.isDeadline = TaskAlertsService.calculateDeadline(new Date($scope.dateInfo.dueDate)) && $scope.objectInfo.status !== "CLOSED";
                $scope.assignee = ObjectModelService.getAssignee($scope.objectInfo);
                $scope.taskStartDateBeforeChange = $scope.dateInfo.taskStartDate;
                $scope.dueDateBeforeChange = $scope.dateInfo.dueDate;



                var today = new Date();
                if (moment($scope.dateInfo.taskStartDate).isAfter(today)) {
                    $scope.minDate = moment(today);
                } else {
                    $scope.minDate = moment.utc(new Date($scope.dateInfo.taskStartDate)).local();
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
                    startDateWatch();
                }
            };

            $scope.updateStartDate = function(data, oldDate) {
                if (!Util.isEmpty(data)) {
                    if (UtilDateService.compareDatesForUpdate(data, $scope.objectInfo.taskStartDate)) {
                        var startDate = new Date(data);
                        $scope.objectInfo.taskStartDate = moment.utc(UtilDateService.dateToIso(startDate)).format();
                        $scope.startDate.startDateInfo = moment($scope.objectInfo.taskStartDate).local();
                        $scope.startDate.startDateInfoUIPicker = moment($scope.objectInfo.taskStartDate).format(defaultDateTimePickerFormat);
                        // unbind start date watcher before task save so that when user switch to different task
                        // watcher won't be fired before landing on that different task
                        startDateWatch();
                        $scope.validateStartDueDate();
                    }
                } else {
                    if (!oldDate) {
                        $scope.objectInfo.taskStartDate = $scope.taskStartDateBeforeChange;
                        $scope.startDate.startDateInfo = moment($scope.objectInfo.taskStartDate).local();
                        $scope.startDate.startDateInfoUIPicker = moment($scope.objectInfo.taskStartDate).format(defaultDateTimePickerFormat);
                        // unbind start date watcher before task save so that when user switch to different task
                        // watcher won't be fired before landing on that different task
                        startDateWatch();
                    }
                }
            };

            $scope.updateDueDate = function(data, oldDate) {
                if (!Util.isEmpty(data)) {
                    if (UtilDateService.compareDatesForUpdate(data, $scope.objectInfo.dueDate)) {
                        var dueDate = new Date(data);
                        var startDate = new Date($scope.dateInfo.taskStartDate);
                        if(dueDate < startDate){
                            $scope.dateInfo.dueDate = $scope.dueDateBeforeChange;
                            DialogService.alert($translate.instant('tasks.comp.info.alertMessage' ) + $filter("date")(startDate, $translate.instant('common.defaultDateTimeUIFormat')));
                        }else {
                            $scope.objectInfo.dueDate = moment.utc(dueDate).format();
                            $scope.datepickerDueDateOptions.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                            $scope.datepickerDueDateOptions.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                            $scope.dateInfo.dueDate = $scope.datepickerDueDateOptions.dueDateInfo;
                            // unbind due date watcher before task save so that when user switch to different task
                            // watcher won't be fired before landing on that different task
                            dueDateWatch();
                            $scope.saveTask();
                        }
                    }
                } else {
                    if (!oldDate) {
                        $scope.objectInfo.dueDate = $scope.dueDateBeforeChange;
                        $scope.datepickerDueDateOptions.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                        $scope.datepickerDueDateOptions.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                        $scope.dateInfo.dueDate = $scope.datepickerDueDateOptions.dueDateInfo;
                        // unbind due date watcher before task save so that when user switch to different task
                        // watcher won't be fired before landing on that different task
                        dueDateWatch();
                        $scope.saveTask();
                    }
                }
            };
            $scope.updateOwningGroup = function() {
                ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
            };

            // store function references returned by $watch statement in variables
            var dueDateWatch = $scope.$watch('datepickerDueDateOptions.dueDateInfo', dueDateChangeFn, true);
            var startDateWatch = $scope.$watch('startDate.startDateInfo', startDateChangeFn, true);

            // update due date and save task
            var dueDateChangeFn = function (newValue, oldValue) {
                if (newValue && !moment(newValue).isSame(moment(oldValue)) && $scope.datepickerDueDateOptions.isOpen) {
                    $scope.updateDueDate(newValue, oldValue);
                }
            }

            // update start date and save task
            var startDateChangeFn = function (newValue, oldValue) {
                if (newValue && !moment(newValue).isSame(moment(oldValue)) && $scope.startDate.isOpen) {
                    $scope.updateStartDate(newValue, oldValue);
                }
            }

            // register watchers when user open date picker
            $scope.registerWatchers = function (dateType) {
                if (dateType === 'dueDate') {
                    dueDateWatch = $scope.$watch('datepickerDueDateOptions.dueDateInfo', dueDateChangeFn, true);
                } else {
                    startDateWatch = $scope.$watch('startDate.startDateInfo', startDateChangeFn, true);
                }
            }

        } ]);
