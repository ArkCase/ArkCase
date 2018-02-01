'use strict'

angular.module("dashboard.weather").filter('temp', function($filter) {
    return function(input, precision, units) {
        if (!precision) {
            precision = 1;
        }
        var numberFilter = $filter('number');

        var tempChars = '\u00B0C';

        if (units === "imperial") {
            tempChars = '\u00B0F';
        }
        return numberFilter(input, precision) + tempChars;
    };
})
