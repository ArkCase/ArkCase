'use strict';

angular.module('preference').controller('Preference.Controller', ['$scope', 'ConfigService', '$modal',
    function ($scope, ConfigService, $modal) {
        $scope.config = ConfigService.getModule({moduleId: 'preference'});
        $scope.$on('req-component-config', onConfigRequest);

        $scope.$on('req-module-selected', moduleSelected);

        function moduleSelected(e, module) {
          $scope.$broadcast('module-selected', module);
        }

        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
    }
]);
