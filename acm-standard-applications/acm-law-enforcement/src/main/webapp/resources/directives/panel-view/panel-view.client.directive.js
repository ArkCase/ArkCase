'use strict';

/**
 * @ngdoc directive
 * @name global.directive:panelView
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/panel-view/panel-view.client.directive.js directives/panel-view/panel-view.client.directive.js}
 *
 * The panelView directive allows you create bootstrap panel with header and body areas.
 *
 * @param {string} header Panel's header content.
 * @param {boolean} collapsible Defines is panel collapsible or not.
 * @param {boolean} collapsed Defines default panel state for collapsible panel.
 *
 * @example
 <example>
 <file name="index.html">
 <panel-view header="Panel header text" collapsible="true" "collapsed="false">
 <h3>This is panel body</h3>
 </panel-view>
 </file>
 </example>
 */
angular.module('directives').directive('panelView', [ '$q', function($q) {
    return {
        restrict : 'E',
        transclude : true,
        scope : {
            header : '@',
            collapsible : '@',
            collapsed : '@'
        },

        link : function(scope, element, attrs) {
            if (!attrs.collapsible) {
                attrs.collapsible = 'true';
            }

            if (attrs.collapsed == 'true') {
                scope.isCollapsed = true;
            }

            attrs.$observe('collapsed', function(collapsedVal) {
                if (collapsedVal == 'true') {
                    scope.isCollapsed = true;
                } else {
                    scope.isCollapsed = false;
                }
            });

            scope.onCollapseIconClick = function($event) {
                $event.preventDefault();
                scope.isCollapsed = !scope.isCollapsed;

                //Allows the controller of where the directive is used to control if it's collapsed as well as keeping the parent's scope updated
                scope.$parent.collapsed = scope.isCollapsed + "";
            };
        },

        templateUrl : 'directives/panel-view/panel-view.client.view.html'
    };
} ]);