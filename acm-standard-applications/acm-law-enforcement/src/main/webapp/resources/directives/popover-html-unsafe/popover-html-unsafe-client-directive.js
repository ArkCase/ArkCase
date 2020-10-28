'use strict';

/**
 * @ngdoc directive
 * @name global.directive:popover-html-unsafe
 * @restrict E
 *
 * @description
 *
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/popover-html-unsafe/popover-html-unsafe.client.directive.js directives/popover-html-unsafe/popover-html-unsafe.client.directive.js}
 *
 * The "popover-html-unsafe" directive, add popover functionality with unsafe html
 *
 *
 * @example
 <example>
 <file name="index.html">
 <button type="button" class="btn btn-primary" popover-placement="right" popover-html-unsafe="{{unsafeHtml}}">
 unsafe html popver
 </button>
 </file>
 <file name="app.js">
 var app = angular.module('myApp', ['ui.bootstrap']);
 app.controller('testCtrl', ['$scope',
 function( $scope ) {
 $dom = '<div class="table-responsive"><table class="table"><tr class="danger"><th>th1</th><th>th2</th><tr><tr><td>td1</td><td>td2</td><tr></table></div>';
 $scope.unsafeHtml = $dom;
 }]);
 </file>
 </example>
 */
angular
        .module('directives')
        .directive(
                'popoverHtmlUnsafePopup',
                function() {
                    return {
                        restrict: 'EA',
                        replace: true,
                        scope: {
                            title: '@',
                            content: '@',
                            placement: '@',
                            animation: '&',
                            isOpen: '&'
                        },
                        template: '<div class="popover {{placement}}" ng-class="{ in: isOpen(), fade: animation() }"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title" bind-html-unsafe="title" ng-show="title"></h3><div class="popover-content" bind-html-unsafe="content"></div></div></div>'
                    };
                }).directive('popoverHtmlUnsafe', [ '$tooltip', function($tooltip) {
            return $tooltip('popoverHtmlUnsafe', 'popover', 'click');
        } ]);
