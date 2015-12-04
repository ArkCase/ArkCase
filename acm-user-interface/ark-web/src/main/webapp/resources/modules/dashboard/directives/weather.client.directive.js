'use strict'
/**
 * @ngdoc directive
 * @name directive:weatherIcon
 * @restrict EA
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/dashboard/directives/weather.client.directive.js modules/dashboard/directives/weather.client.directive.js}
 *
 * The weatherIcon directive renders simple weather icon insisde weather widget
 *
 * @param {expression} id - The Icon Id returned from the weather service and is used for finding appropriate image
 * that will be rendered inside the weather widget
 *
 * @example
 <example>
 <file name="index.html">
 <weather-icon id="{{expression}}">
 </weather-icon>
 </file>
 <file name="app.js">
 angular.module('ngAppDemo', []).controller('ngAppDemoController', function($scope, $log) {
                $scope.id = "01d";
            });
 </file>
 </example>
 */

angular.module("dashboard.weather").directive('weatherIcon', function () {
    return {
        restrict: 'EA',
        replace: true,
        scope: {
            imageId: '@id'
        },
        controller: function ($scope) {
            $scope.imgurl = function () {
                var baseUrl = 'assets/img/dashboard/widgets/weather/';
                return baseUrl + $scope.imageId + ".png";
            };
        },
        template: '<div class="pull-left" "><img ng-src="{{ imgurl() }}"></div>'
    };
})