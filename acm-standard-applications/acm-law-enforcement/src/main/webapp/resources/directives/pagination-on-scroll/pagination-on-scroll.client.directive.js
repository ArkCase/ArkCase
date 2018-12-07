'use strict';
/**
 * @ngdoc directive
 * @name global.directive:paginationOnScroll
 * @restrict A
 *
 * @description
 *
 *{@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/pagination-on-scroll/pagination-on-scroll.client.directive.js directives/pagination-on-scroll/pagination-on-scroll.client.directive.js}
 *
 * The "paginationOnScroll" should be used for infinite pagination while user is scrolling
 *
 * @param {object} loadMore - An object(functions) that should be called when the user scroll and to retrieve more info
 * @param {object} resetScrollPx - An object(Boolean) that should reset the scroll when the user select new object.
 *                                 If there is one scrolling panel there is no need of this variable
 *
 * @example
 <example>
 <div pagination-on-scroll load-more="exampleFunction()"></div>
 </example>
 **/

angular.module('directives').directive('paginationOnScroll', [ 'UtilService', function(Util) {
    return {
        restrict: 'A',
        scope: {
            loadMore: "&",
            resetScrollPx: "=?"
        },
        link: function(scope, elem, attr) {
            elem.bind("scroll", function() {
                if (Util.isEmpty(scope.resetScrollPx) || scope.resetScrollPx) {
                    var scrolledpx = 0;
                    scope.resetScrollPx = false;
                } else {
                    var scrolledpx = elem.context.clientHeight + elem.context.scrollTop;
                }

                if (!(elem.context.clientHeight === elem.context.scrollHeight) && scrolledpx === elem.context.scrollHeight) {
                    scope.loadMore();
                }
            });
        }
    }
} ]);