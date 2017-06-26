'use strict';

angular.module('filters').filter('arrayToString', function () {
    return function (myArray) {
        return myArray.join(', ');
    };
});