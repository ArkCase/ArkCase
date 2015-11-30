'use strict';

angular.module('tasks').controller('Tasks.MainController', ['$scope', 'StoreService', 'UtilService', 'HelperService', 'ConfigService',
    function ($scope, Store, Util, Helper, ConfigService) {
        $scope.$emit('req-component-config', 'main');
        $scope.$on('component-config', function applyConfig(e, componentId, config) {
            if (componentId == 'main') {
                $scope.config = config;
            }
        });


        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            return moduleConfig;
        });
        //ConfigService.getModule({moduleId: 'tasks'}, function (moduleConfig) {
        //    $scope.components = moduleConfig.components;
        //});


        $scope.$on('task-updated', function (e, data) {
            $scope.taskInfo = data;
        });


        $scope.$on('task-selected', function onSelectedTask(e, selectedTask) {
            //var componentsStore = new Store.Variable("TaskComponentsStore");
            //componentsStore.set(selectedTask.components);
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