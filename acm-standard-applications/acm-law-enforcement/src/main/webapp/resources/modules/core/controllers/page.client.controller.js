'use strict';

angular.module('core').controller('PageController', ['$scope',
    function ($scope) {
        $scope.isLeftMenuCollapsed = false;

        $scope.$on('isLeftMenuCollapsed', function(e,isLeftMenuCollapsed){
            $scope.isLeftMenuCollapsed = isLeftMenuCollapsed;
        })
    }
]);
