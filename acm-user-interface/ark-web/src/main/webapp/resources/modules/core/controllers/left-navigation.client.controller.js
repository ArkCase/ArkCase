'use strict';

angular.module('core').controller('LeftNavigationController', ['$scope', '$location', '$state', 'Authentication', 'Menus',
    function($scope, $location, $state, Authentication, Menus) {
        var baseUrl = $location.absUrl();
        var idx = baseUrl.indexOf('#');
        if (idx > -1 ) {
            baseUrl = baseUrl.substring(0, idx);
        }


        $scope.baseUrl = baseUrl;
        $scope.$state = $state;
        $scope.authentication = Authentication;
        $scope.isCollapsed = false;
        $scope.menu = Menus.getMenu('leftnav');
    }
]);