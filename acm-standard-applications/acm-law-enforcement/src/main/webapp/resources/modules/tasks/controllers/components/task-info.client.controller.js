'use strict';

angular.module('tasks').controller('Tasks.InfoController', ['$scope', '$stateParams', '$translate', '$timeout'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'LookupService', 'Object.LookupService', 'Task.InfoService', 'Object.ModelService'
    , 'Helper.ObjectBrowserService', 'Task.AlertsService', 'ObjectService', 'Helper.UiGridService', '$modal'
    , 'Object.ParticipantService', '$q', 'Case.InfoService', 'Complaint.InfoService'
    , function ($scope, $stateParams, $translate, $timeout
        , Util, UtilDateService, ConfigService, LookupService, ObjectLookupService, TaskInfoService, ObjectModelService
        , HelperObjectBrowserService, TaskAlertsService, ObjectService, HelperUiGridService, $modal, ObjectParticipantService, $q
        , CaseInfoService, ComplaintInfoService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "tasks"
            , componentId: "info"
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();
        var promiseConfig = ConfigService.getModuleConfig("tasks");

        $q.all([promiseConfig]).then(function (data) {
            var foundComponent = data[0].components.filter(function(component) { return component.title === 'Info'; });
            $scope.config = foundComponent[0];
        });

        LookupService.getUsers().then(
            function (users) {
                var options = [];
                _.each(users, function (user) {
                    options.push({object_id_s: user.object_id_s, name: user.name});
                });
                $scope.assignableUsers = options;
                return users;
            }
        );

        ObjectLookupService.getPriorities().then(
            function (priorities) {
                var options = [];
                _.each(priorities, function (priority) {
                    options.push({value: priority, text: priority});
                });
                $scope.priorities = options;
                return priorities;
            }
        );

        $scope.openAssigneePickerModal = function () {
            var participant = {
                        id: '',
                        participantLdapId: '',
                        config: $scope.config
                    };
            showModal(participant, false);
        };

        var showModal = function (participant, isEdit) {
            var modalScope = $scope.$new();
            modalScope.participant = participant || {};

            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: "modules/tasks/views/components/task-assignee-picker-modal.client.view.html",
                controller: "Tasks.AssigneePickerController",
                size: 'md',
                backdrop: 'static',
                resolve: {
                    owningGroup: function () {
                        return $scope.owningGroup;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                $scope.participant = {};
                if (data.participant.participantLdapId != '' && data.participant.participantLdapId != null) {
                    $scope.participant.participantLdapId = data.participant.participantLdapId;
                    $scope.assignee = data.participant.participantLdapId;
                    $scope.updateAssignee($scope.assignee);
                }
            }, function(error) {
            });
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.dateInfo = $scope.dateInfo || {};
            $scope.dateInfo.dueDate = UtilDateService.isoToDate($scope.objectInfo.dueDate);
            $scope.dateInfo.taskStartDate = UtilDateService.isoToDate($scope.objectInfo.taskStartDate);
            $scope.dateInfo.isOverdue = TaskAlertsService.calculateOverdue($scope.dateInfo.dueDate);
            $scope.dateInfo.isDeadline = TaskAlertsService.calculateDeadline($scope.dateInfo.dueDate);
            $scope.assignee = ObjectModelService.getAssignee($scope.objectInfo);

            if (ObjectService.ObjectTypes.CASE_FILE == $scope.objectInfo.parentObjectType) {
                CaseInfoService.getCaseInfo($scope.objectInfo.parentObjectId).then(
                    function (caseInfo) {
                        $scope.owningGroup = ObjectModelService.getGroup(caseInfo);
                    }
                );
            } else if (ObjectService.ObjectTypes.COMPLAINT == $scope.objectInfo.parentObjectType) {
                ComplaintInfoService.getComplaintInfo($scope.objectInfo.parentObjectId).then(
                    function (complaintInfo) {
                        $scope.owningGroup = ObjectModelService.getGroup(complaintInfo);
                    }
                );
            }
        };

        $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;
        $scope.picker = {opened: false};
        $scope.onPickerClick = function () {
            $scope.picker.opened = true;
        };

        $scope.validatePercentComplete = function (value) {
            if (value < 0 || value > 100) {
                return "Invalid value";
            }
        };

        $scope.saveTask = function () {
            saveTask();
        };
        $scope.updateAssignee = function (assignee) {
            //var taskInfo = Util.omitNg($scope.objectInfo);
            //TasksModelsService.setAssignee($scope.objectInfo, $scope.assignee);
            $scope.objectInfo.assignee = assignee;
            saveTask();
        };
        $scope.updateStartDate = function (taskStartDate) {
            $scope.objectInfo.taskStartDate = UtilDateService.dateToIso($scope.dateInfo.taskStartDate);
            saveTask();
        };
        $scope.updateDueDate = function (dueDate) {
            $scope.objectInfo.dueDate = UtilDateService.dateToIso($scope.dateInfo.dueDate);
            saveTask();
        };

        //user group picker
        $scope.showAssigneePicker = function () {
            var cfg = {};
            cfg.topLevelGroupTypes = ["LDAP_GROUP"];

            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/dialog/user-group-picker.client.view.html',
                controller: 'Tasks.UserGroupPickerDialogController',
                size: 'lg',
                resolve: {
                    cfg: function () {
                        return cfg;
                    },
                    parentType: function () {
                        return $scope.objectInfo.attachedToObjectType;
                    },
                    showGroupAndUserPicker: function () {
                        return true;
                    }
                }
            });

            modalInstance.result.then(function (chosenNode) {
                if (chosenNode) {
                    if (Util.goodValue(chosenNode[0])) { //Selected a user
                        //Change Assignee
                        var userId = Util.goodMapValue(chosenNode[0], 'object_id_s');
                        if (userId) {
                            $scope.objectInfo.candidateGroups = [];
                            $scope.updateAssignee(userId);
                        }
                    } else if (Util.goodValue(chosenNode[1])) { //Selected a group
                        var group = Util.goodMapValue(chosenNode[1], 'object_id_s');
                        if (group) {
                            $scope.objectInfo.candidateGroups = [group];

                            //Clear participants as it causes concurrent modification errors when
                            //there is no assignee, but a participant of type assignee is present
                            $scope.objectInfo.participants = null;
                            $scope.updateAssignee(null);
                        }
                    }
                }

            }, function () {
                // Cancel button was clicked.
                return [];
            });
        };

        function saveTask() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (TaskInfoService.validateTaskInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = TaskInfoService.saveTaskInfo(objectInfo);
                promiseSaveInfo.then(
                    function (taskInfo) {
                        $scope.$emit("report-object-updated", taskInfo);
                        TaskInfoService.resetTaskCacheById(taskInfo.taskId);
                        return TaskInfoService.getTaskInfo(taskInfo.taskId);
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    }
                ).then(function (taskInfo) {
                    onObjectInfoRetrieved(taskInfo);
                });
            }
            return promiseSaveInfo;
        }

    }
]);