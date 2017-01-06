'use strict';

angular.module('tasks').controller('Tasks.InfoController', ['$scope', '$stateParams', '$translate', '$timeout'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'LookupService', 'Object.LookupService', 'Task.InfoService', 'Object.ModelService'
    , 'Helper.ObjectBrowserService', 'MessageService', 'Task.AlertsService', 'ObjectService', 'Helper.UiGridService', '$modal'
    , 'Object.ParticipantService', '$q', 'Case.InfoService', 'Complaint.InfoService'
    , function ($scope, $stateParams, $translate, $timeout
        , Util, UtilDateService, ConfigService, LookupService, ObjectLookupService, TaskInfoService, ObjectModelService
        , HelperObjectBrowserService, MessageService, TaskAlertsService, ObjectService, HelperUiGridService, $modal, ObjectParticipantService, $q
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
        })

        $scope.participantsInit = {
            moduleId: 'tasks',
            componentId: 'participants',
            retrieveObjectInfo: TaskInfoService.getTaskInfo,
            validateObjectInfo: TaskInfoService.validateTaskInfo,
            saveObjectInfo: TaskInfoService.saveTaskInfo,
            objectType: ObjectService.ObjectTypes.TASK,
            participantsTitle: $translate.instant("tasks.comp.participants.title")
        }

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

        ObjectLookupService.getParticipantTypes().then(
            function (participantTypes) {
                $scope.participantTypes = participantTypes;
                return participantTypes;
            }
        );

        $scope.openAssigneePickerModal = function () {
            var participant = {
                        id: '',
                        participantType: $scope.participantTypes[0].type,
                        participantLdapId: '',
                        participantTypes: $scope.participantTypes,
                        config: $scope.config
                    };
            showModal(participant, false);
        };

        var showModal = function (participant, isEdit) {
            var modalScope = $scope.$new();
            modalScope.participant = participant || {};
            modalScope.isEdit = isEdit || false;
            modalScope.selectedType = ""; 

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
                if (ObjectParticipantService.validateType(data.participant, data.selectedType)) {
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

        function saveTask() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (TaskInfoService.validateTaskInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = TaskInfoService.saveTaskInfo(objectInfo);
                promiseSaveInfo.then(
                    function (taskInfo) {
                        $scope.$emit("report-object-updated", taskInfo);
                        return taskInfo;
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    }
                );
            }
            return promiseSaveInfo;
        }

    }
]);