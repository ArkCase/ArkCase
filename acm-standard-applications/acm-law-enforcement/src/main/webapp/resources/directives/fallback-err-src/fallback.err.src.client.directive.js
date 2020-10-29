'use strict';

/**
 * @ngdoc directive
 * @name global.directive:fallbackErrSrc
 * @restrict A
 *
 * @description
 *
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/fallback-err-src/fallback.err.src.client.directive.js directives/fallback-err-src/fallback.err.src.client.directive.js}
 *
 * Directive to watch for an error loading an image and to replace the src.
 *
 * @param {string} fallback-err-src URL of the image that is displayed in case of default image loading error.
 *
 * @example
 <example>
 <file name="index.html">
 <img ng-src="{{imgSrc}}" fallback-err-src="http://google.com/favicon.ico"/>
 </file>
 </example>
 */
angular.module('directives').directive('fallbackErrSrc', function() {
    return {
        link: function(scope, element, attrs) {
            element.bind('error', function() {
                if (attrs.src != attrs.fallbackErrSrc) {
                    attrs.$set('src', attrs.fallbackErrSrc);
                }
            });
        }
    }
});
