'use strict';

angular.module('core').controller('HeaderController', ['$scope', 'Authentication', 'Menus', '$state', 'ServCommService'
    , function ($scope, Authentication, Menus, $state, ServCommService) {
        $scope.$emit('req-component-config', 'header');
        $scope.authentication = Authentication;
        $scope.isCollapsed = false;
        $scope.menu = Menus.getMenu('topbar');

        $scope.config = null;
        $scope.start = '';
        $scope.count = '';
        $scope.inputQuery = '';
        $scope.data = {};
        $scope.data.inputQuery = '';
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'header') {
                $scope.config = config;
                $scope.start = config.searchParams.start;
                $scope.count = config.searchParams.n;
            }
        };

        ServCommService.handleRequest();


        $scope.toggleCollapsibleMenu = function () {
            $scope.isCollapsed = !$scope.isCollapsed;
        };

        // Collapsing the menu after navigation
        $scope.$on('$stateChangeSuccess', function () {
            $scope.isCollapsed = false;
        });

        $scope.search = function () {
            $state.go('quick-search', {
                query: $scope.data.inputQuery
            });
        };

        $scope.keyDown = function (event) {
            if (event.keyCode == 13) {
                $state.go('quick-search', {
                    query: $scope.data.inputQuery
                });
            }
        };
    }
]);