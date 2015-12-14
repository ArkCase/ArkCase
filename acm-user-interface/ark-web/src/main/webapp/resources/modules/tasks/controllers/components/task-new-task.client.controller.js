'use strict';

angular.module('tasks').controller('Tasks.NewTaskController', ['$scope', '$stateParams', '$sce', '$q', 'ConfigService'
    , 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'Task.NewTaskService', 'Authentication'
    , function ($scope, $stateParams, $sce, $q, ConfigService, Util, TicketService, LookupService, FrevvoFormService, TaskNewTaskService, Authentication) {

        $scope.config = null;

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );


        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.config = _.find(moduleConfig.components, {id: "newTask"});

            $scope.config.data.assignee = $scope.userId;

            if (!Util.isEmpty($stateParams.parentObject) && !Util.isEmpty($stateParams.parentType)) {
                $scope.config.data.attachedToObjectName = $stateParams.parentObject;
                $scope.config.data.attachedToObjectType = $stateParams.parentType;
            }
            return moduleConfig;
        });

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;
        $scope.saved = false;

        $scope.saveNewTask = function () {
            $scope.saved = true;
            TaskNewTaskService.saveAdHocTask($scope.config.data);
        };

        $scope.userSearch = function () {
            $event.preventDefault();
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/task-user-search.client.view.html',
                controller: 'Tasks.UserSearchController',
                size: 'lg',
                resolve: {
                    $userSearchScope: function () {
                        return $scope;
                    },
                    $filter: function () {
                        return $scope.config.userSearch.userFacetFilter;
                    }
                }
            });

            modalInstance.result.then(function (chosenUser) {
                if (chosenUser) {
                    console.log("A user was chosen for this task");
                }
            }, function () {
                // Cancel button was clicked.
            });
        };
    }
]);