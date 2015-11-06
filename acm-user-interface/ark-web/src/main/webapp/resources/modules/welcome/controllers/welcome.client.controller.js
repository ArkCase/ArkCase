'use strict';

angular.module('welcome').controller('WelcomeController', ['$scope', '$state', 'StoreService', 'HelperService', 'ConfigService',
    function ($scope, $state, Store, Helper, ConfigService) {
        $scope.config = ConfigService.getModule({moduleId: 'welcome'});
        $scope.$on('req-component-config', function (e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId})
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        });


        _.each(Helper.SessionCacheNames, function (name) {
            var cache = new Store.SessionData(name);
            cache.set(null);
        });

        $state.go("dashboard");
    }
]);