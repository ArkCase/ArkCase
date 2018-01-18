'use strict'
/**
 * @ngdoc directive
 * @name directive:weatherIcon
 * @restrict EA
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/dashboard/directives/weather.client.directive.js modules/dashboard/directives/weather.client.directive.js}
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

angular.module("dashboard.weather").directive('weatherIcon', weatherIcon);

function weatherIcon() {

    var directive = {
        restrict : 'EA',
        replace : true,
        controller : WeatherIconController,
        controllerAs : 'vm',
        bindToController : true,
        scope : {
            id : '@id'
        },
        template : '<div class="pull-left" "><img ng-src="{{ vm.imgurl() }}"></div>'
    };
    return directive;
}

function WeatherIconController() {
    var vm = this;
    vm.imgurl = function() {
        var baseUrl = 'assets/img/dashboard/widgets/weather/';
        return baseUrl + vm.id + ".png";
    };
}