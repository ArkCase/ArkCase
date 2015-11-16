'use strict';

angular.module('tasks').controller('Tasks.MainController', ['$scope', 'StoreService', 'UtilService', 'HelperService', 'ConfigService',
    function ($scope, Store, Util, Helper, ConfigService) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'main');
        $scope.components = null;
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'main') {
                $scope.config = config;
            }
        }

        ConfigService.getModule({moduleId: 'tasks'}, function (moduleConfig) {
            $scope.components = moduleConfig.components;
        });


        $scope.$on('task-retrieved', function (e, data) {
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