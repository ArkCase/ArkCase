'use strict';

angular.module('core').controller('PageController', ['$scope',
    function ($scope) {
        $scope.isCollapsed = false;

        $scope.$on('isCollapsed', function(e,isCollapsed){
            $scope.isCollapsed = isCollapsed;
        })
    }
]);