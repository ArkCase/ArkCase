'use strict';

angular.module('core').controller('LeftNavigationController', ['$scope', '$state', 'Authentication', 'Menus',
    function ($scope, $state, Authentication, Menus) {
        $scope.$state = $state;
        $scope.authentication = Authentication;
        $scope.isLeftMenuCollapsed = false;
        $scope.menu = Menus.getMenu('leftnav');

        $scope.$watch('isLeftMenuCollapsed', function () {
            $scope.$emit('isLeftMenuCollapsed', $scope.isLeftMenuCollapsed);
        });

        $scope.$bus.subscribe('refreshLeftMenu', function (data) {
            $scope.menu = Menus.getMenu('leftnav');
        });
    }
]); 
