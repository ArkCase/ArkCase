'use strict';

angular.module('tasks').controller('Tasks.NewTaskController', ['$scope', '$state', '$sce', '$q', '$modal'
    , 'ConfigService', 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'Task.NewTaskService'
    , 'Authentication', 'Util.DateService', 'Dialog.BootboxService', 'ObjectService', 'Object.LookupService', 'Admin.FunctionalAccessControlService'
    , 'modalParams'
    , function ($scope, $state, $sce, $q, $modal, ConfigService, Util, TicketService, LookupService
        , FrevvoFormService, TaskNewTaskService, Authentication, UtilDateService, DialogService, ObjectService, ObjectLookupService
        , AdminFunctionalAccessControlService, modalParams) {

        $scope.modalParams = modalParams;
        $scope.config = null;
        $scope.userSearchConfig = null;
        $scope.objectSearchConfig = null;
        $scope.isAssocType = false;
        $scope.loading = false;

        $scope.groupTask = false;
        $scope.chosenGroup = '';

        $scope.options = {
            focus: true,
            dialogsInBody: true
            //,height: 120
        };

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userInfo = userInfo;
                $scope.userFullName = userInfo.fullName;
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        ObjectLookupService.getGroups().then(
            function (groups) {
                var options = [];
                _.each(groups, function (group) {
                    options.push({value: group.name, text: group.name});
                });
                $scope.assignableGroups = options;
                return groups;
            }
        );

        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.config = _.find(moduleConfig.components, {id: "newTask"});

            $scope.userSearchConfig = _.find(moduleConfig.components, {id: "userSearch"});
            $scope.objectSearchConfig = _.find(moduleConfig.components, {id: "objectSearch"});

            $scope.userName = $scope.userFullName;
            $scope.config.data.assignee = $scope.userId;
            $scope.config.data.taskStartDate = new Date();
            $scope.config.data.priority = $scope.config.priority[1].id;
            $scope.config.data.percentComplete = 0;


            if (!Util.isEmpty($scope.modalParams.parentObject) && !Util.isEmpty($scope.modalParams.parentType)) {
                $scope.config.data.attachedToObjectName = $scope.modalParams.parentObject;
                $scope.config.data.attachedToObjectType = $scope.modalParams.parentType;
                if (!Util.isEmpty($scope.modalParams.parentTitle)) {
                    $scope.config.data.parentObjectTitle = $scope.modalParams.parentTitle;
                }
            }
            return moduleConfig;
        });

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;
        $scope.saved = false;

        $scope.saveNewTask = function () {
            $scope.saved = true;
            $scope.loading = true;
            var taskData = angular.copy($scope.config.data);
            taskData.dueDate = moment.utc(UtilDateService.dateToIso($scope.config.data.dueDate));
            if ($scope.config.data.attachedToObjectName === "") {
                $scope.config.data.attachedToObjectName = "";
            }
            TaskNewTaskService.saveAdHocTask($scope.config.data).then(function (data) {
                $scope.saved = false;
                $scope.loading = false;
                if ($scope.modalParams.returnState != null && $scope.modalParams.returnState != ":returnState") {
                    $state.go($scope.modalParams.returnState, {type: $scope.modalParams.parentType, id: $scope.modalParams.parentId});
                } else {
                    ObjectService.showObject(ObjectService.ObjectTypes.ADHOC_TASK, data.taskId);
                }
                $scope.onModalClose();
            }, function (err) {
                $scope.saved = false;
                $scope.loading = false;
                if (!Util.isEmpty(err)) {
                    var statusCode = Util.goodMapValue(err, "status");
                    var message = Util.goodMapValue(err, "data.message");

                    if (statusCode == 400) {
                        DialogService.alert(message);
                    }
                }
            });
        };

        $scope.updateAssocParentType = function () {
            $scope.isAssocType = $scope.config.data.attachedToObjectType !== '';

        };

        $scope.inputClear = function(){
            $scope.config.data.attachedToObjectName = null;
        }

        //groupChange function
        $scope.groupChange = function () {
            $scope.config.data.candidateGroups = [$scope.chosenGroup];
        };

        $scope.groupTaskToggle = function () {
            //Clear relevant information
            $scope.config.data.candidateGroups = [];
            $scope.chosenGroup = "";
            $scope.config.data.assignee = null;
            $scope.userName = "";
        };

        $scope.userOrGroupSearch = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/task-user-search.client.view.html',
                controller: 'Tasks.UserSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.config.userOrGroupSearch.userOrGroupFacetFilter;
                    },
                    $extraFilter: function () {
                        return $scope.config.userOrGroupSearch.userOrGroupFacetExtraFilter;
                    },
                    $config: function () {
                        return $scope.userSearchConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenUserOrGroup) {
                if (chosenUserOrGroup) {
                    if (chosenUserOrGroup.object_type_s === 'USER') {  // Selected a user
                        $scope.config.data.assignee = chosenUserOrGroup.object_id_s;
                        $scope.userOrGroupName = chosenUserOrGroup.name;
                        $scope.pickOwningGroup(chosenUserOrGroup.object_id_s, chosenUserOrGroup.name);

                        return;
                    } else if (chosenUserOrGroup.object_type_s === 'GROUP') {
                        $scope.config.data.assignee = null;
                        $scope.config.data.candidateGroups = [chosenUserOrGroup.object_id_s];
                        $scope.userOrGroupName = chosenUserOrGroup.name;

                        return;
                    }
                }

            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

        $scope.pickOwningGroup = function (assigneeLdapId, asigneeName) {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/task-group-search.client.view.html',
                controller: 'Tasks.GroupSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.config.groupSearch.groupFacetFilter + assigneeLdapId +$scope.config.groupSearch.groupFacetExtraFilter;
                    },
                    $searchValue: function () {
                        return asigneeName;
                    },
                    $config: function () {
                        return $scope.userSearchConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenUserOrGroup) {
                $scope.config.data.candidateGroups = [chosenUserOrGroup.object_id_s];
                $scope.testId = chosenUserOrGroup.object_id_s;

                return;
            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

        $scope.objectSearch = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/task-object-search.client.view.html',
                controller: 'Tasks.ObjectSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.config.objectSearch.objectFacetFilter + $scope.config.data.attachedToObjectType;
                    },
                    $config: function () {
                        return $scope.objectSearchConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenObject) {
                if (chosenObject) {
                    $scope.config.data.attachedToObjectName = chosenObject.name;

                    return;
                }

            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

        $scope.cancelModal = function() {
            $scope.onModalDismiss();
        };
    }
]);
