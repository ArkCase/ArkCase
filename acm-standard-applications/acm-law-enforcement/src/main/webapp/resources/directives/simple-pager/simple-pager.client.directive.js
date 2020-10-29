/**
 * Created by dragan.simonovski on 2/17/2016.
 */
'use strict';

/**
 * @ngdoc directive
 * @name global.directive:simple-pager
 * @restrict E
 *
 * @description
 *
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/simple-pager/simple-pager.client.directive.js directives/simple-pager/simple-pager.client.directive.js}
 *
 * The "simple-pager" directive adds paging functionality like ui-grid pager
 *
 * @param {Object} pagerData object containing pageSizes array, pageSize and totalItems
 * @param {Function} reloadPage, sends command to parrent control that page or pageSize is changed to reload the page
 *
 * @example
 <example>
 <file name="index.html">
 <simple-pager pager-data="pagerData" reload-page="reloadPage"/>
 </file>
 <file name="app.js">
 angular.module('ngAppDemo', []).controller('ngAppDemoController', function($scope, $log) {
 $scope.pagerData = {
 pageSizes: [10, 20, 30, 40, 50],
 pageSize: 50,
 totalItems: 200
 };
 $scope.reloadPage = function (currentPage, pageSize) {

 var promise = callServiceToGetData(currentPage, pageSize);

 promise.then(function (payload) {
 var tempData = payload.data;
 $scope.pagerData.totalItems = payload.data.length;
 });
 }
 });
 </file>
 </example>
 */
angular.module('directives').directive('simplePager', function() {
    return {
        restrict: 'E', //match only element name
        scope: {
            pagerData: '=', //= : two way binding so that the data can be monitored for changes
            reloadPage: '='
        },

        link: function(scope) { //dom operations
            scope.$watchCollection('pagerData.totalItems', function(totalItems, oldValue) {
                if (totalItems && totalItems != oldValue) {
                    recalculatePagerNumbers();
                }
            });

            scope.pageChanged = function(currentPage) {
                if (currentPage) {
                    scope.pagerData.currentPage = currentPage;
                } else {
                    scope.pagerData.currentPage = 1;
                }
                scope.reloadPage(scope.pagerData.currentPage, scope.pagerData.pageSize);
                recalculatePagerNumbers();
            };

            function recalculatePagerNumbers() {
                var max = scope.pagerData.currentPage * scope.pagerData.pageSize;
                scope.currentItems = scope.pagerData.totalItems < max ? scope.pagerData.totalItems : max;
                var num = parseInt(scope.pagerData.totalItems / scope.pagerData.pageSize);
                scope.totalPages = scope.pagerData.totalItems % scope.pagerData.pageSize > 0 ? num + 1 : num;
            }
        },
        templateUrl: 'directives/simple-pager/simple-pager.client.view.html'
    };
});