'use strict';

angular.module('subscriptions').controller('SubscriptionsController', ['$scope', 'ConfigService', 'Authentication',
    function ($scope, ConfigService, Authentication) {
        $scope.config = ConfigService.getModule({moduleId: 'subscriptions'});
        $scope.$on('req-component-config', onConfigRequest);

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo;
                return userInfo;
            }
        );

        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId})
                $scope.$broadcast('component-config', componentId, componentConfig, $scope.user);
            });
        }
    }
]);