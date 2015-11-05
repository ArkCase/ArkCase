'use strict';

angular.module('core').controller('HeaderController', ['$scope', 'Authentication', 'Menus', '$state',
    function ($scope, Authentication, Menus, $state) {
        $scope.$emit('req-component-config', 'header');
        $scope.authentication = Authentication;
        $scope.isCollapsed = false;
        $scope.menu = Menus.getMenu('topbar');
        
        $scope.config=null;
        $scope.start='';
        $scope.count='';
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'header') {
                $scope.config = config;
                $scope.start=config.searchParams.start;
                $scope.count=config.searchParams.n;
            }
        }

        $scope.toggleCollapsibleMenu = function () {
            $scope.isCollapsed = !$scope.isCollapsed;
        };

        // Collapsing the menu after navigation
        $scope.$on('$stateChangeSuccess', function () {
            $scope.isCollapsed = false;
        });
        $scope.search = function () {
            $state.go('search');
            callSearchService();
        };
        $scope.keyDown = function (event) {
            if (event.keyCode == 13) {
                $state.go('search');
                callSearchService();
            }
        };
        function callSearchService() {
            /*SearchService.queryFacetedSearch({
                input: $scope.inputQuery + '*',
                start: 0,
                n: 10},
            function (data) {
                ResultService.passData(data, $scope.inputQuery + '*');
            });*/
        }
    }
]);