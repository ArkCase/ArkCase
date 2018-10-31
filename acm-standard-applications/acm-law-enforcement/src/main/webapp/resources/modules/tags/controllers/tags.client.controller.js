'use strict';

angular.module('tags').controller('TagsController', [ '$scope', 'ConfigService', '$stateParams', function($scope, ConfigService, $stateParams) {
    $scope.config = ConfigService.getModule({
        moduleId: 'tags'
    });

    $scope.searchQuery = $stateParams['query'] ? $stateParams['query'] : '';

    $scope.$on('req-component-config', onConfigRequest);

    function onConfigRequest(e, componentId) {
        $scope.config.$promise.then(function(config) {
            var componentConfig = _.find(config.components, {
                id: componentId
            })
            $scope.$broadcast('component-config', componentId, componentConfig);
        });
    }
} ]);