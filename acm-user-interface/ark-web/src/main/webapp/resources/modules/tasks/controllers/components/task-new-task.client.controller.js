'use strict';

angular.module('tasks').controller('Tasks.NewTaskController', ['$scope', '$stateParams', '$sce', '$q', 'ConfigService'
    , 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'Task.NewTaskService', 'Authentication'
    , function ($scope, $stateParams, $sce, $q, ConfigService, Util, TicketService, LookupService, FrevvoFormService, TaskNewTaskService, Authentication){

        $scope.config = null;

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.config = _.find(moduleConfig.components, {id: "newTask"});

            if (!Util.isEmpty($stateParams.parentObject) && !Util.isEmpty($stateParams.parentType)){
                $scope.config.data.attachedToObjectName = $stateParams.parentObject;
                $scope.config.data.attachedToObjectType = $stateParams.parentType;
                $scope.config.data.assignee = $scope.userId;
            }
            return moduleConfig;
        });

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;

        $scope.saveNewTask = TaskNewTaskService.saveAdHocTask($scope.config.data);
    }
]);