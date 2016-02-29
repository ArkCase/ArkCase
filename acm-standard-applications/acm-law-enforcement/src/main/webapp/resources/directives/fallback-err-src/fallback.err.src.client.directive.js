'use strict';
/**
 * @ngdoc directive
 * @name global.directive:fallbackErrSrc
 *
 * @description
 *
 * Directive to watch for an error loading an image and to replace the src.
 *
 * @example
    <example>
        <img ng-src="{{imgSrc}}" fallback-err-src="http://google.com/favicon.ico"/>
    </example>
 */
angular.module('directives').directive('fallbackErrSrc', function () {
    return {
        link: function (scope, element, attrs) {
            element.bind('error', function () {
                if (attrs.src != attrs.fallbackErrSrc) {
                    attrs.$set('src', attrs.fallbackErrSrc);
                }
            });
        }
    }
});
