'use strict';

angular.module('tasks').controller('Tasks.NewTaskController', ['$scope', '$state', '$stateParams', '$sce', '$q', '$modal'
    , 'ConfigService', 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'Task.NewTaskService'
    , 'Authentication', 'Util.DateService', 'Dialog.BootboxService', 'ObjectService', 'Object.LookupService', 'Admin.FunctionalAccessControlService'
    , function ($scope, $state, $stateParams, $sce, $q, $modal, ConfigService, Util, TicketService, LookupService
        , FrevvoFormService, TaskNewTaskService, Authentication, UtilDateService, DialogService, ObjectService, ObjectLookupService 
        , AdminFunctionalAccessControlService) {

        $scope.config = null;
        $scope.userSearchConfig = null;
        $scope.objectSearchConfig = null;
        $scope.isAssocType = false;

        $scope.groupTask = false;
        $scope.chosenGroup = "";

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


            if (!Util.isEmpty($stateParams.parentObject) && !Util.isEmpty($stateParams.parentType)) {
                $scope.config.data.attachedToObjectName = $stateParams.parentObject;
                $scope.config.data.attachedToObjectType = $stateParams.parentType;
                if (!Util.isEmpty($stateParams.parentTitle)) {
                    $scope.config.data.parentObjectTitle = $stateParams.parentTitle;
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
            $scope.config.data.dueDate = UtilDateService.dateToIso($scope.config.data.dueDate);
            TaskNewTaskService.saveAdHocTask($scope.config.data).then(function (data) {
                $scope.saved = false;
                if ($stateParams.returnState != null && $stateParams.returnState != ":returnState") {
                    $state.go($stateParams.returnState, {type: $stateParams.parentType, id: $stateParams.parentId});
                } else {
                    ObjectService.showObject(ObjectService.ObjectTypes.ADHOC_TASK, data.taskId);
                }
            }, function (err) {
                $scope.saved = false;
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

        }
        
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
    }
]);
