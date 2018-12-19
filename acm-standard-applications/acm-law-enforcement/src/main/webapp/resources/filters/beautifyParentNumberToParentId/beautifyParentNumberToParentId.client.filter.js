'use strict';

angular.module('filters').filter('beautifyParentNumberToParentId', function() {
    return function(myString) {
        return parseInt(myString.substring(myString.lastIndexOf("_") + 1));
    };
});