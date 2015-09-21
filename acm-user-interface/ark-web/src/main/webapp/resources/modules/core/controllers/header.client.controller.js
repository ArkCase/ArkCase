'use strict';

angular.module('core').controller('HeaderController', ['$scope', 'Authentication', 'Menus', '$state', 'ResultService', 'SearchService',
    function ($scope, Authentication, Menus, $state, ResultService, SearchService) {
        $scope.authentication = Authentication;
        $scope.isCollapsed = false;
        $scope.menu = Menus.getMenu('topbar');

        $scope.toggleCollapsibleMenu = function () {
            $scope.isCollapsed = !$scope.isCollapsed;
        };

        // Collapsing the menu after navigation
        $scope.$on('$stateChangeSuccess', function () {
            $scope.isCollapsed = false;
        });
        $scope.search = function () {
            $state.go('search');
            serviceCall();
        };
        $scope.keyDown = function (event) {
            if (event.keyCode == 13) {
                $state.go('search');
                serviceCall();
            }
        };
        function serviceCall() {
            SearchService.queryFacetedSearch({
                input: $scope.inputQuery + '*',
                start: 0,
                n: 10},
            function (data) {
                ResultService.passData(data, $scope.inputQuery + '*');
            });
        }
    }
]);