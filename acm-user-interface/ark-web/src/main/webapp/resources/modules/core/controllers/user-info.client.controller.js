'use strict';

angular.module('core').controller('UserInfoController', ['$scope','Authentication','Menus',
    function($scope, Authentication, Menus) {
        $scope.menu = Menus.getMenu('usermenu');
    }
]);