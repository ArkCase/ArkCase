'use strict';

angular.module('tasks').controller('Tasks.MainController', ['$scope', '$stateParams', 'StoreService'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.NoteService', 'Object.AuditService', 'Object.SignatureService', 'Task.InfoService', 'Task.HistoryService'
    , function ($scope, $stateParams, Store
        , Util, ConfigService, ObjectService, ObjectNoteService, ObjectAuditService, ObjectSignatureService, TaskInfoService, TaskHistoryService) {

        $scope.$emit('main-component-started');

        var promiseConfig = ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            return moduleConfig;
        });


        $scope.shallInclude = function (component) {
            if (component.enabled) {
                var componentsStore = new Store.Variable("TaskComponentsStore");
                var componentsToShow = Util.goodValue(componentsStore.get(), []);
                for (var i = 0; i < componentsToShow.length; i++) {
                    if (componentsToShow[i] == component.id) {
                        return true;
                    }
                }
            }
            return false;
        };

    }
])
;