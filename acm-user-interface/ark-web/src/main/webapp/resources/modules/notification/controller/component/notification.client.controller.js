angular.module('notifications').controller('NotificationController', ['$scope',
        function ($scope) {
            $scope.$emit('req-component-config', 'notifications');
            $scope.$on('component-config', applyConfig)
            function applyConfig(e, componentId, config) {
                if (componentId == 'notifications') {
                    $scope.config = config;
                    $scope.filter = config.filter;
                }
            }
        }
    ]
);


