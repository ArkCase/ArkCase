'use strict';

angular.module('tasks').controller('Tasks.NewTaskController', ['$scope', '$stateParams', '$sce', '$q', '$modal', 'ConfigService'
    , 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'Task.NewTaskService', 'Authentication', 'Util.DateService'
    , function ($scope, $stateParams, $sce, $q, $modal, ConfigService, Util, TicketService
        , LookupService, FrevvoFormService, TaskNewTaskService, Authentication, UtilDateService) {

        $scope.config = null;
        $scope.userSearchConfig = null;

        $scope.options = {
            focus: true,
            dialogsInBody: true
            //,height: 120
        };

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userFullName = userInfo.fullName;
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.config = _.find(moduleConfig.components, {id: "newTask"});

            $scope.userSearchConfig = _.find(moduleConfig.components, {id: "userSearch"});

            $scope.userName = $scope.userFullName;
            $scope.config.data.assignee = $scope.userId;
            $scope.config.data.taskStartDate = new Date();
            var defaultPriority = $scope.config.priority[1].id;
            $scope.config.data.priority = defaultPriority;
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
            TaskNewTaskService.saveAdHocTask($scope.config.data);
        };

        $scope.userSearch = function () {

            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/task-user-search.client.view.html',
                controller: 'Tasks.UserSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.config.userSearch.userFacetFilter;
                    },
                    $config: function () {
                        return $scope.userSearchConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenUser) {
                if (chosenUser) {
                    $scope.config.data.assignee = chosenUser.object_id_s;
                    $scope.userName = chosenUser.name;

                    return;
                }

            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };
    }
]);
