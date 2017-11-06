'use strict';

angular.module('tasks').controller('Tasks.InfoController', ['$scope', '$stateParams', '$translate', '$filter', '$modal'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'LookupService', 'Object.LookupService', 'Task.InfoService'
    , 'Object.ModelService', 'Helper.ObjectBrowserService', 'Task.AlertsService', 'ObjectService', 'Helper.UiGridService'
    , 'Object.ParticipantService', 'Case.InfoService', 'Complaint.InfoService', 'SearchService', 'Search.QueryBuilderService'
    , function ($scope, $stateParams, $translate, $filter, $modal
        , Util, UtilDateService, ConfigService, LookupService, ObjectLookupService, TaskInfoService
        , ObjectModelService, HelperObjectBrowserService, TaskAlertsService, ObjectService, HelperUiGridService
        , ObjectParticipantService, CaseInfoService, ComplaintInfoService, SearchService, SearchQueryBuilder
    ) {

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

        ConfigService.getComponentConfig("tasks", "info").then(function (componentConfig) {
            $scope.config = componentConfig;
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

        ObjectLookupService.getGroups().then(
            function (groups) {
                var options = [];
                _.each(groups, function (group) {
                    options.push({value: group.name, text: group.name});
                });
                $scope.owningGroups = options;
                return groups;
            }
        );

        ObjectLookupService.getLookupByLookupName("priorities").then(function (priorities) {
            $scope.priorities = priorities;
            return priorities;
        });

        $scope.openAssigneePickerModal = function () {
            var participant = {
                id: '',
                participantLdapId: '',
                config: $scope.config
            };
            showAssigneeModal(participant, false);
        };

        var showAssigneeModal = function (participant, isEdit) {
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

            modalInstance.result.then(function (chosenNode) {
                $scope.participant = {};

                if (chosenNode.participant.participantLdapId != '' && chosenNode.participant.participantLdapId != null) {
                    $scope.participant.participantLdapId = chosenNode.participant.participantLdapId;
                    $scope.participant.object_type_s = chosenNode.participant.object_type_s;

                    if ($scope.participant.object_type_s === 'USER') { //Selected a user
                        if ($scope.participant.participantLdapId) {
                            $scope.objectInfo.candidateGroups = [];
                            $scope.assignee = chosenNode.participant.participantLdapId;
                            $scope.updateAssignee($scope.assignee);
                        }
                    } else if ($scope.participant.object_type_s === 'GROUP') { //Selected a group
                        if ($scope.participant.participantLdapId) {
                            $scope.objectInfo.candidateGroups = [$scope.participant.participantLdapId];
                            $scope.owningGroup = chosenNode.participant.selectedAssigneeName;
                            $scope.assignee = null;

                            var assigneeParticipantType = 'assignee';
                            // Iterating through the array to find the participant with the ParticipantType eqaul assignee
                            // then setiing the participantLdapId to empty string
                            _.each($scope.objectInfo.participants, function(participant) {
                                if(participant.participantType == assigneeParticipantType){
                                    participant.participantLdapId = '';
                                }
                            });

                            // Seting the owningGroup in the objectInfo before the save
                            ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
                            $scope.updateAssignee($scope.assignee);
                        }
                    }
                }

            }, function(error) {
            });
        };

        $scope.openGroupPickerModal = function () {
            var participant = {
                id: '',
                participantLdapId: '',
                config: $scope.config
            };
            showGroupModal(participant, false);
        };

        var showGroupModal = function (participant, isEdit) {
            var modalScope = $scope.$new();
            modalScope.participant = participant || {};

            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: "modules/tasks/views/components/task-group-picker-modal.client.view.html",
                controller: "Tasks.GroupPickerController",
                size: 'md',
                backdrop: 'static',
                resolve: {
                    owningGroup: function () {
                        return $scope.owningGroup;
                    }
                }
            });

            modalInstance.result.then(function (chosenGroup) {
                $scope.participant = {};

                if (chosenGroup.participant.participantLdapId != '' && chosenGroup.participant.participantLdapId != null) {
                    $scope.participant.participantLdapId = chosenGroup.participant.participantLdapId;
                    $scope.participant.object_type_s = chosenGroup.participant.object_type_s;

                    var currentAssignee = $scope.assignee;
                    var chosenOwningGroup = chosenGroup.participant.participantLdapId;
                    $scope.iscurrentAssigneeInOwningGroup = false;
                    var size = 20;
                    var start = 0;
                    var searchQuery = '*';
                    var filter = 'fq=fq="object_type_s": USER' + '&fq="groups_id_ss": ' + '"' + chosenOwningGroup + '"';

                    // Creating a query to get all the users that belong to a particular Owning Group
                    // this query is need for the search logic below
                    var query = SearchQueryBuilder.buildSafeFqFacetedSearchQuery(searchQuery, filter, size, start);
                    if (query) {
                        SearchService.queryFilteredSearch({
                                query: query
                            },
                            function (data) {
                                var returnedUsers = data.response.docs;
                                // Going through th collection of returnedUsers to see if there is a match with the current assignee
                                // if there is a match that means the current assignee is within that owning group hence no
                                // changes to the current assignee is needed
                                _.each(returnedUsers, function (returnedUser) {
                                    if (currentAssignee === returnedUser.object_id_s) {
                                        $scope.iscurrentAssigneeInOwningGroup = true;
                                    }
                                });
                                if ($scope.participant.participantLdapId && $scope.iscurrentAssigneeInOwningGroup) {
                                    $scope.owningGroup = chosenGroup.participant.selectedAssigneeName;
                                    $scope.objectInfo.candidateGroups = [$scope.participant.participantLdapId];

                                    $scope.updateOwningGroup();
                                } else {
                                    $scope.owningGroup = chosenGroup.participant.selectedAssigneeName;
                                    $scope.objectInfo.candidateGroups = [$scope.participant.participantLdapId];
                                    $scope.assignee = null;

                                    var assigneeParticipantType = 'assignee';
                                    // Iterating through the array to find the participant with the ParticipantType eqaul assignee
                                    // then setiing the participantLdapId to empty string
                                    _.each($scope.objectInfo.participants, function(participant) {
                                        if(participant.participantType == assigneeParticipantType){
                                            participant.participantLdapId = '';
                                        }
                                    });

                                    // Seting the owningGroup in the objectInfo before the save
                                    ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
                                    $scope.updateAssignee($scope.assignee);
                                }
                            });
                    }
                }
            }, function(error) {
            });
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.dateInfo = $scope.dateInfo || {};
            $scope.dateInfo.dueDate = $scope.objectInfo.dueDate;
            $scope.dateInfo.taskStartDate = $scope.objectInfo.taskStartDate;
            $scope.dateInfo.isOverdue = TaskAlertsService.calculateOverdue($scope.dateInfo.dueDate);
            $scope.dateInfo.isDeadline = TaskAlertsService.calculateDeadline($scope.dateInfo.dueDate);
            $scope.assignee = ObjectModelService.getAssignee($scope.objectInfo);
            
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
        $scope.picker = {opened: false};
        $scope.onPickerClick = function () {
            $scope.picker.opened = true;
        };

        $scope.validatePercentComplete = function (value) {
            if (value < 0 || value == null || value > 100) {
                return "Invalid value";
            }
        };


        $scope.saveTask = function () {
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
                    //updadateCaseOrComplaintInfo(taskInfo);
                    onObjectInfoRetrieved(taskInfo);
                });
            }
            return promiseSaveInfo;
        };
        $scope.updateAssignee = function (assignee) {
            //var taskInfo = Util.omitNg($scope.objectInfo);
            //TasksModelsService.setAssignee($scope.objectInfo, $scope.assignee);
            $scope.objectInfo.assignee = assignee;
            $scope.saveTask();
        };
        $scope.updateStartDate = function () {
            var correctedDate = UtilDateService.convertToCurrentTime($scope.dateInfo.taskStartDate);
            $scope.objectInfo.taskStartDate = moment.utc(UtilDateService.dateToIso(correctedDate)).format();
            $scope.saveTask();
        };
        $scope.updateDueDate = function () {
            var correctedDueDate = UtilDateService.convertToCurrentTime($scope.dateInfo.dueDate);
            $scope.objectInfo.dueDate = moment.utc(UtilDateService.dateToIso(correctedDueDate)).format();
            $scope.saveTask();
        };
        $scope.updateOwningGroup = function () {
            ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
            $scope.saveTask();
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

    }
]);