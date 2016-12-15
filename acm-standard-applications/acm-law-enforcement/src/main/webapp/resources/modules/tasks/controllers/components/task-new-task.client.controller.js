'use strict';

angular.module('tasks').controller('Tasks.NewTaskController', ['$scope', '$state', '$stateParams', '$sce', '$q', '$modal'
    , 'ConfigService', 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'Task.NewTaskService'
    , 'Authentication', 'Util.DateService', 'Dialog.BootboxService', 'ObjectService'
    , function ($scope, $state, $stateParams, $sce, $q, $modal, ConfigService, Util, TicketService, LookupService
        , FrevvoFormService, TaskNewTaskService, Authentication, UtilDateService, DialogService, ObjectService) {

        $scope.config = null;
        $scope.userSearchConfig = null;
        $scope.isAssocType = false;

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
