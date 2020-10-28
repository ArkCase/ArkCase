'use strict';

/**
 * @ngdoc directive
 * @name global.directive:adjustTableHeight
 * @restrict A
 *
 * @description
 *
 * {@link /ACM3/blob/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/table-view/table-height.directive.js directives/table-view/table-height.directive.js}
 *
 * The adjust-table-height directive makes ui-grid 100% height. Also it solves ui-grid scroll problem (https://github.com/angular-ui/ui-grid/issues/1926).
 *
 * @param {number} adjust-table-height Number of grid's rows
 *
 * @example
 <example>
 <file name="index.html">
 <div ui-grid="gridOptions"
 adjust-table-height="gridOptions.data.length"
 ui-grid-auto-resize>
 </div>
 </file>
 </example>
 */
angular.module('directives').directive('adjustTableHeight', function() {
    return {
        restrict: 'A',
        link: function($scope, element, attrs) {

            // This hack allows to scroll page that contains grid.
            // Solution is described at https://github.com/angular-ui/ui-grid/issues/1926
            $scope.$watch('isDisplayed', function(newValue, oldValue) {
                var viewport = element.find('.ui-grid-render-container');
                [ 'touchstart', 'touchmove', 'touchend', 'keydown', 'wheel', 'mousewheel', 'DomMouseScroll', 'MozMousePixelScroll' ].forEach(function(eventName) {
                    viewport.unbind(eventName);
                });
            });

            $scope.$watch(attrs.adjustTableHeight, function(value, oldValue) {
                if (value && value > 0) {
                    element.attr('style', 'height: ' + getTableHeight(value) + 'px');
                } else {
                    element.attr('style', 'height: ' + getTableHeight(0) + 'px');
                }
            }, true);

            // function that returns table height
            var getTableHeight = function getTableHeight(newRows) {

                // default values for row height, header height and number of rows in table
                var rowHeight = 30;
                var headerHeight = 107;
                var rows = 10;

                // if data of grid more then default rows then recalculate number of rows
                if (newRows && newRows > rows) {
                    rows = newRows;
                }

                // return entire table height
                return rows * rowHeight + headerHeight;
            };
        }
    };
});