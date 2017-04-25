'use strict';

/**
 * @ngdoc directive
 * @name global.directive:progressIndicator
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/progress-indicator/progress-indicator.directive.js directives/progress-indicator/progress-indicator.directive.js}
 *
 * The "Progress-Indicator" directive shows progress on longer action for which the current progress is fetched by a generic event
 *
 * @example
 <example>
 <file name="index.html">
 <progress-indicator></progress-indicator>
 </file>
 </example>
 */

angular.module('directives').directive('progressIndicator', ['$timeout'
    , function ($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'directives/progress-indicator/progress-indicator.html',
            link: function (scope) {

                scope.showProgress = false;
                var eventName = "live_progress";
                scope.$bus.subscribe(eventName, function (data) {

                    scope.$apply(function () {
                        scope.showProgress = true;
                        scope.currentProgress = data.current;
                        scope.percentageStyle = {
                            width: scope.currentProgress + '%'
                        };
                        if (scope.currentProgress >= 100) {
                            $timeout(function () {
                                scope.showProgress = false;
                            }, 3000);
                        }
                    });
                });
            }
        };
    }
]);

