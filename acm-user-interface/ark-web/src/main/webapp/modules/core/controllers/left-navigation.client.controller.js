'use strict';

angular.module('core').controller('LeftNavigationController', ['$scope', '$state', 'Authentication', 'Menus',
    function($scope, $state, Authentication, Menus) {
        $scope.$state = $state;
        $scope.authentication = Authentication;
        $scope.isCollapsed = false;
        $scope.menu = Menus.getMenu('leftnav');
    }
]);