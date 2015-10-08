'use strict';

angular.module('directives').directive('panelView', ['$q',
    function ($q) {
        return {
            restrict: 'E',
            transclude: true,
            scope: {
                header: '@',
                collapsible: '@',
                collapsed: '@'
            },

            link: function (scope, element, attrs) {
                if (!attrs.collapsible) {
                    attrs.collapsible = 'true';
                }

                if (attrs.collapsed == 'true') {
                    scope.isCollapsed = true;
                }

                scope.onCollapseIconClick = function($event) {
                    $event.preventDefault();
                    scope.isCollapsed = !scope.isCollapsed
                };
            },

            templateUrl: 'directives/panel-view/panel-view.client.view.html'
        };
    }
]);